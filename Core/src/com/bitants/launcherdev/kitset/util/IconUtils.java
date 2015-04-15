package com.bitants.launcherdev.kitset.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import com.bitants.launcherdev.launcher.support.FastBitmapDrawable;
import com.nd.android.pandahome2.R;

/**
 * 生成Launcher下icon图标工具类
 */
public class IconUtils {
	static final String TAG = "IconUtils";

	private static final boolean TEXT_BURN = false;

	private static int sIconWidth = -1;
	private static int sIconHeight = -1;

	private static final Paint sBlurPaint = new Paint();
	private static final Paint sGlowColorPressedPaint = new Paint();
	private static final Paint sGlowColorFocusedPaint = new Paint();
	private static final Paint sDisabledPaint = new Paint();
	private static final Canvas sCanvas = new Canvas();

	static {
		sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
	}

	static Bitmap centerToFit(Bitmap bitmap, int width, int height, Context context) {
		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		if (bitmapWidth < width || bitmapHeight < height) {
			int color = context.getResources().getColor(R.color.window_background);

			Bitmap centered = Bitmap.createBitmap(bitmapWidth < width ? width : bitmapWidth, bitmapHeight < height ? height : bitmapHeight, Bitmap.Config.RGB_565);
			centered.setDensity(bitmap.getDensity());
			Canvas canvas = new Canvas(centered);
			canvas.drawColor(color);
			canvas.drawBitmap(bitmap, (width - bitmapWidth) / 2.0f, (height - bitmapHeight) / 2.0f, null);

			bitmap = centered;
		}

		return bitmap;
	}

	static int sColors[] = { 0xffff0000, 0xff00ff00, 0xff0000ff };
	static int sColorIndex = 0;

	/**
	 * 生成图标Drawable
	 * @param drawable
	 * @param context
	 * @return Drawable
	 */
	public static Drawable createIconDrawable(Drawable drawable, Context context) {
		if (null == drawable)
			return null;
		return new FastBitmapDrawable(BaseBitmapUtils.createIconBitmapThumbnail(drawable, context));
	}

	/**
	 * Returns a bitmap suitable for the all apps view. The bitmap will be a
	 * power of two sized ARGB_8888 bitmap that can be used as a gl texture.
	 */
	public static Bitmap createIconBitmap(Drawable icon, Context context) {
		if (null == icon)
			return null;

		return BaseBitmapUtils.createIconBitmapThumbnail(icon, context);
	}

	static void drawSelectedAllAppsBitmap(Canvas dest, int destWidth, int destHeight, boolean pressed, Bitmap src) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				// We can't have gotten to here without src being initialized,
				// which
				// comes from this file already. So just assert.
				// initStatics(context);
				throw new RuntimeException("Assertion failed: Utilities not initialized");
			}

			dest.drawColor(0, PorterDuff.Mode.CLEAR);

			int[] xy = new int[2];
			Bitmap mask = src.extractAlpha(sBlurPaint, xy);

			float px = (destWidth - src.getWidth()) / 2;
			float py = (destHeight - src.getHeight()) / 2;
			dest.drawBitmap(mask, px + xy[0], py + xy[1], pressed ? sGlowColorPressedPaint : sGlowColorFocusedPaint);

			mask.recycle();
		}
	}

	/**
	 * Returns a Bitmap representing the thumbnail of the specified Bitmap. The
	 * size of the thumbnail is defined by the dimension
	 * android.R.dimen.launcher_application_icon_size.
	 * 
	 * @param bitmap
	 *            The bitmap to get a thumbnail of.
	 * @param context
	 *            The application's context.
	 * 
	 * @return A thumbnail for the specified bitmap or the bitmap itself if the
	 *         thumbnail could not be created.
	 */
	static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}

			if (bitmap.getWidth() == sIconWidth && bitmap.getHeight() == sIconHeight) {
				return bitmap;
			} else {
				return createIconBitmap(new BitmapDrawable(context.getResources(), bitmap), context);
			}
		}
	}

	static Bitmap drawDisabledBitmap(Bitmap bitmap, Context context) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}
			final Bitmap disabled = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(disabled);

			canvas.drawBitmap(bitmap, 0.0f, 0.0f, sDisabledPaint);

			return disabled;
		}
	}

	private static void initStatics(Context context) {
		final Resources resources = context.getResources();
		final DisplayMetrics metrics = resources.getDisplayMetrics();
		final float density = metrics.density;

		sIconWidth = sIconHeight = (int) resources.getDimension(android.R.dimen.app_icon_size);

		sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * density, BlurMaskFilter.Blur.NORMAL));
		MaskFilter filter = null;
		try {
			Class c = Class.forName("android.graphics.TableMaskFilter");
			Method CreateClipTable = c.getDeclaredMethod("CreateClipTable", new Class[] { int.class, int.class });
			filter = (MaskFilter) CreateClipTable.invoke(c, new Object[] {0, 30});
		} catch (Exception e) {
			e.printStackTrace();
		}
		sGlowColorPressedPaint.setColor(0xffffc300);
		sGlowColorPressedPaint.setMaskFilter(filter);
		sGlowColorFocusedPaint.setColor(0xffff8e00);
		sGlowColorFocusedPaint.setMaskFilter(filter);

		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0.2f);
		sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
		sDisabledPaint.setAlpha(0x88);
	}

	static class BubbleText {
		private static final int MAX_LINES = 2;

		private final TextPaint mTextPaint;

		private final RectF mBubbleRect = new RectF();

		private final float mTextWidth;
		private final int mLeading;
		private final int mFirstLineY;
		private final int mLineHeight;

		private final int mBitmapWidth;
		private final int mBitmapHeight;
		private final int mDensity;

		BubbleText(Context context) {
			final Resources resources = context.getResources();

			final DisplayMetrics metrics = resources.getDisplayMetrics();
			final float scale = metrics.density;
			mDensity = metrics.densityDpi;

			final float paddingLeft = 2.0f * scale;
			final float paddingRight = 2.0f * scale;
			final float cellWidth = resources.getDimension(R.dimen.title_texture_width);

			RectF bubbleRect = mBubbleRect;
			bubbleRect.left = 0;
			bubbleRect.top = 0;
			bubbleRect.right = (int) cellWidth;

			mTextWidth = cellWidth - paddingLeft - paddingRight;

			TextPaint textPaint = mTextPaint = new TextPaint();
			textPaint.setTypeface(Typeface.DEFAULT);
			textPaint.setTextSize(13 * scale);
			textPaint.setColor(0xffffffff);
			textPaint.setAntiAlias(true);
			if (TEXT_BURN) {
				textPaint.setShadowLayer(8, 0, 0, 0xff000000);
			}

			float ascent = -textPaint.ascent();
			float descent = textPaint.descent();
			float leading = 0.0f;// (ascent+descent) * 0.1f;
			mLeading = (int) (leading + 0.5f);
			mFirstLineY = (int) (leading + ascent + 0.5f);
			mLineHeight = (int) (leading + ascent + descent + 0.5f);

			mBitmapWidth = (int) (mBubbleRect.width() + 0.5f);
			mBitmapHeight = roundToPow2((int) ((MAX_LINES * mLineHeight) + leading + 0.5f));

			mBubbleRect.offsetTo((mBitmapWidth - mBubbleRect.width()) / 2, 0);
		}

		/** You own the bitmap after this and you must call recycle on it. */
		Bitmap createTextBitmap(String text) {
			Bitmap b = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ALPHA_8);
			b.setDensity(mDensity);
			Canvas c = new Canvas(b);

			StaticLayout layout = new StaticLayout(text, mTextPaint, (int) mTextWidth, Alignment.ALIGN_CENTER, 1, 0, true);
			int lineCount = layout.getLineCount();
			if (lineCount > MAX_LINES) {
				lineCount = MAX_LINES;
			}
			// if (!TEXT_BURN && lineCount > 0) {
			// RectF bubbleRect = mBubbleRect;
			// bubbleRect.bottom = height(lineCount);
			// c.drawRoundRect(bubbleRect, mCornerRadius, mCornerRadius,
			// mRectPaint);
			// }
			for (int i = 0; i < lineCount; i++) {
				// int x = (int)((mBubbleRect.width() - layout.getLineMax(i)) /
				// 2.0f);
				// int y = mFirstLineY + (i * mLineHeight);
				final String lineText = text.substring(layout.getLineStart(i), layout.getLineEnd(i));
				int x = (int) (mBubbleRect.left + ((mBubbleRect.width() - mTextPaint.measureText(lineText)) * 0.5f));
				int y = mFirstLineY + (i * mLineHeight);
				c.drawText(lineText, x, y, mTextPaint);
			}

			return b;
		}

		private int height(int lineCount) {
			return (int) ((lineCount * mLineHeight) + mLeading + mLeading + 0.0f);
		}

		int getBubbleWidth() {
			return (int) (mBubbleRect.width() + 0.5f);
		}

		int getMaxBubbleHeight() {
			return height(MAX_LINES);
		}

		int getBitmapWidth() {
			return mBitmapWidth;
		}

		int getBitmapHeight() {
			return mBitmapHeight;
		}
	}

	/** Only works for positive numbers. */
	static int roundToPow2(int n) {
		int orig = n;
		n >>= 1;
		int mask = 0x8000000;
		while (mask != 0 && (n & mask) == 0) {
			mask >>= 1;
		}
		while (mask != 0) {
			n |= mask;
			mask >>= 1;
		}
		n += 1;
		if (n != orig) {
			n <<= 1;
		}
		return n;
	}
}
