package com.bitants.common.kitset.util;

import java.io.*;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.util.Log;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.support.FastBitmapDrawable;
import com.bitants.common.launcher.view.icon.ui.LauncherIconDataCache;
import com.bitants.common.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.common.theme.data.ThemeGlobal;
import com.bitants.common.launcher.support.BaseIconCache;
import com.bitants.common.theme.parse.EncodeTools;
import com.bitants.common.R;

/**
 * 图片处理相关内容
 */
public class BaseBitmapUtils {

	protected static String TAG = "BitmapUtils";

	protected static int sIconWidth = -1;
	protected static int sIconHeight = -1;

	protected static final Rect sOldBounds = new Rect();
	protected static final Canvas sCanvas = new Canvas();
	private static Rect maxRect;
	private static Rect minRect;
	private static Rect defaultThemeIconMaskRect;

	static {
		sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
	}

	/**
	 * 获取默认应用程序图标 暂时取消默认图标的显示
	 * @param res
	 * @return 目前暂都返回null
	 */
	public static Bitmap getDefaultAppIcon(Resources res) {
		// Bitmap draw = BitmapFactory.decodeResource(res,
		// android.R.drawable.sym_def_app_icon);
		// if (draw == null)
		// draw = BitmapFactory.decodeResource(res,
		// R.drawable.ic_launcher_application);

		// 不显示默认图标
		return null;
	}

	/**
	 * 根据指定大小，缩放Bitmap
	 * @param bmp 被缩放的bitmap
	 * @param newWidth bitmap缩放后的宽度
	 * @param newHeiht bitmap缩放后的高度
	 * @return Bitmap
	 */
	public static Bitmap resizeImage(Bitmap bmp, int newWidth, int newHeiht) {
		if (bmp == null) {
			return null;
		}

		int originWidth = bmp.getWidth();
		int originHeight = bmp.getHeight();
		if (originWidth == newWidth && originHeight == newHeiht)
			return bmp;

		float scaleWidth = ((float) newWidth) / originWidth;
		float scaleHeight = ((float) newHeiht) / originHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBitmap = Bitmap.createBitmap(bmp, 0, 0, originWidth, originHeight, matrix, true);
		return resizeBitmap;
	}

	/**
	 * 根据指定缩放比例，缩放Bitmap
	 * @param bmp 被缩放的bitmap
	 * @param scale 缩放比例
	 * @return Bitmap
	 */
	public static Bitmap resizeImage(Bitmap bmp, float scale) {
		if (bmp == null) {
			return null;
		}

		int originWidth = bmp.getWidth();
		int originHeight = bmp.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bmp, 0, 0, originWidth, originHeight, matrix, true);
	}

	/**
	 * 获取图像的宽高
	 * @param path 图像路径
	 * @return 失败返回{ -1, -1 }
	 */
	public static int[] getImageWH(String path) {
		int[] wh = { -1, -1 };
		if (path == null) {
			return wh;
		}
		File file = new File(path);
		if (file.exists() && !file.isDirectory()) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				InputStream is = new FileInputStream(path);
				BitmapFactory.decodeStream(is, null, options);
				wh[0] = options.outWidth;
				wh[1] = options.outHeight;
			} catch (Throwable e) {
				Log.w(TAG, "getImageWH Throwable.", e);
			}
		}
		return wh;
	}

	/**
	 * 获取图像的宽高
	 * @param is
	 * @return 失败返回{ -1, -1 }
	 */
	public static int[] getImageWH(InputStream is) {
		int[] wh = { -1, -1 };
		if (is == null) {
			return wh;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(is, null, options);
			wh[0] = options.outWidth;
			wh[1] = options.outHeight;
		} catch (Throwable e) {
			Log.w(TAG, "getImageWH Throwable.", e);
		}
		return wh;
	}

	/**
	 * 根据坐标和高宽生成Rect，该Rect会修正小于0的问题，目前用于文件夹及动画绘制
	 * @param x 中心点x坐标
	 * @param y 中心点y坐标
	 * @param width 宽
	 * @param height 高
	 * @return Rect
	 */
	public static Rect caculateRect(float x, float y, float width, float height) {
		int left = Math.round(x - width / 2);
		int top = Math.round(y - height / 2);
		int right = Math.round(left + width);
		int bottom = Math.round(top + height);
		
		int split = 0;
		if(left < 0){
			split = -left;
		}
		if(top < 0 && -top > split){
			split = -top;
		}
		if(split > 0){
			left += split;
			top += split;
			right -= split;
			bottom -= split;
		}
		return new Rect(left, top, right, bottom);
	}

	/**
	 * 生成指定大小的Bitmap
	 * @param bitmapOrg
	 * @param newWidth
	 * @param newHeight
	 * @return Bitmap
	 */
	public static Bitmap toSizeBitmap(Bitmap bitmapOrg, int newWidth, int newHeight) {

		// Bitmap bitmapOrg = BitmapFactory.decodeResource(this.getResources(),
		// srcId);
		if (null == bitmapOrg) {
			return null;
		}

		// 获取这个图片的宽和高
		int w = bitmapOrg.getWidth();
		int h = bitmapOrg.getHeight();

		int x, y = 0;

		int wTemp = newWidth * h / newHeight;
		if (wTemp > w) {
			// 以宽度
			h = newHeight * w / newWidth;
			x = 0;
			y = (bitmapOrg.getHeight() - h) / 2;
		} else {
			w = wTemp;
			y = 0;
			x = (bitmapOrg.getWidth() - wTemp) / 2;
		}

		float scaleWidth, scaleHeight = 0;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		Bitmap resizedBitmap;

		// 将整个头像按比例缩放绘制到屏幕中
		// 计算缩放率，新尺寸除原始尺寸
		scaleWidth = ((float) newWidth) / w;
		scaleHeight = ((float) newHeight) / h;

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		// 创建新的图片
		resizedBitmap = Bitmap.createBitmap(bitmapOrg, x, y, w, h, matrix, true);
		// 此壁纸在S5830回收原图后会有问题 caizp 2012-8-31
		// BitmapUtils.destoryBitmap(bitmapOrg);
		return resizedBitmap;
	}

	/**
	 * 复制图片 
	 * @param bitmapOrg
	 * @return Bitmap
	 */
	public static Bitmap copyBitmap(Bitmap bitmapOrg) {
		if (null == bitmapOrg)
			return null;
		Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.getWidth(), bitmapOrg.getHeight(), true);
		Canvas canvas = new Canvas();
		canvas.setBitmap(resultBitmap);
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmapOrg, 0, 0, paint);
		return resultBitmap;
	}

	/**
	 * 用于生成桌面高亮投影 
	 * @param ctx
	 * @param sourceBmp
	 * @param bmpScale 缩放比例
	 * @return Bitmap
	 */
	public static Bitmap createBlurBitmap(Context ctx, Bitmap sourceBmp, float bmpScale) {
		if (null == sourceBmp)
			return null;

		final float scale = ScreenUtil.getDisplayMetrics(ctx).density;

		// calculate the inner blur
		Canvas srcDstCanvas = new Canvas();
		srcDstCanvas.setBitmap(sourceBmp);
		srcDstCanvas.drawColor(0xFF000000, PorterDuff.Mode.SRC_OUT);
		BlurMaskFilter innerBlurMaskFilter = new BlurMaskFilter(scale * 2.0f, BlurMaskFilter.Blur.NORMAL);
		Paint mBlurPaint = new Paint();
		mBlurPaint.setFilterBitmap(true);
		mBlurPaint.setAntiAlias(true);
		mBlurPaint.setMaskFilter(innerBlurMaskFilter);
		int[] thickInnerBlurOffset = new int[2];
		Bitmap thickInnerBlur = sourceBmp.extractAlpha(mBlurPaint, thickInnerBlurOffset);

		// mask out the inner blur
		Paint mErasePaint = new Paint();
		mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mErasePaint.setFilterBitmap(true);
		mErasePaint.setAntiAlias(true);
		srcDstCanvas.setBitmap(thickInnerBlur);
		srcDstCanvas.drawBitmap(sourceBmp, -thickInnerBlurOffset[0], -thickInnerBlurOffset[1], mErasePaint);
		srcDstCanvas.drawRect(0, 0, -thickInnerBlurOffset[0], thickInnerBlur.getHeight(), mErasePaint);
		srcDstCanvas.drawRect(0, 0, thickInnerBlur.getWidth(), -thickInnerBlurOffset[1], mErasePaint);

		// draw the inner and outer blur
		Paint mHolographicPaint = new Paint();
		mHolographicPaint.setFilterBitmap(true);
		mHolographicPaint.setAntiAlias(true);
		mHolographicPaint.setAlpha(150);
		srcDstCanvas.setBitmap(sourceBmp);
		srcDstCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		final int outlineColor = Color.parseColor("#33b5e5");
		mHolographicPaint.setColor(outlineColor);
		srcDstCanvas.drawBitmap(thickInnerBlur, thickInnerBlurOffset[0], thickInnerBlurOffset[1], mHolographicPaint);
		thickInnerBlur.recycle();

		//
		BlurMaskFilter outerBlurMaskFilter = new BlurMaskFilter(scale * 2.0f, BlurMaskFilter.Blur.OUTER);
		mBlurPaint.setMaskFilter(outerBlurMaskFilter);
		int[] outerBlurOffset = new int[2];
		Bitmap thickOuterBlur = sourceBmp.extractAlpha(mBlurPaint, outerBlurOffset);
		srcDstCanvas.drawBitmap(thickOuterBlur, outerBlurOffset[0], outerBlurOffset[1], mHolographicPaint);
		thickOuterBlur.recycle();

		// draw the bright outline
		mHolographicPaint.setColor(outlineColor);
		BlurMaskFilter sThinOuterBlurMaskFilter = new BlurMaskFilter(scale * 1.0f, BlurMaskFilter.Blur.OUTER);
		mBlurPaint.setMaskFilter(sThinOuterBlurMaskFilter);
		int[] brightOutlineOffset = new int[2];
		Bitmap brightOutline = sourceBmp.extractAlpha(mBlurPaint, brightOutlineOffset);
		srcDstCanvas.drawBitmap(brightOutline, brightOutlineOffset[0], brightOutlineOffset[1], mHolographicPaint);
		brightOutline.recycle();

		Matrix matrix = new Matrix();
		matrix.postScale(bmpScale, bmpScale); // 长和宽放大缩小的比例
		Bitmap bitmap = Bitmap.createBitmap(sourceBmp, 0, 0, sourceBmp.getWidth(), sourceBmp.getHeight(), matrix, true);
		sourceBmp.recycle();
		return bitmap;
	}

	/**
	 * 获取图片文件，指定大小来获取，如果指定的大小超过原图尺寸，则按原图尺寸返回
	 * @param contentUri 图片资源
	 * @param ctx
	 * @param targetWidth
	 * @param targetHeight
	 * @return Bitmap
	 * @throws Exception
	 */
	public static Bitmap getImageFile(Uri contentUri, Context ctx, int targetWidth, int targetHeight) throws Exception {
		Bitmap tmpBmp = null;
		try {
			if (contentUri == null)// 文件不存在
				return null;
			ContentResolver cr = ctx.getContentResolver();
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 先测量图片的尺寸
			if (contentUri.toString().indexOf("content") != -1)
				BitmapFactory.decodeStream(cr.openInputStream(contentUri), null, opts);
			else
				BitmapFactory.decodeFile(contentUri.toString(), opts);

			int imWidth = opts.outWidth; // 图片宽
			int imHeight = opts.outHeight; // 图片高

			int scale = 1;
			if (imWidth > imHeight)
				scale = Math.round((float) imWidth / targetWidth);
			else
				scale = Math.round((float) imHeight / targetHeight);
			scale = scale == 0 ? 1 : scale;

			opts.inJustDecodeBounds = false;
			opts.inSampleSize = scale;
			if (contentUri.toString().indexOf("content") != -1)
				tmpBmp = BitmapFactory.decodeStream(cr.openInputStream(contentUri), null, opts);
			else {
				FileInputStream fis = new FileInputStream(new File(contentUri.toString()));
				tmpBmp = BitmapFactory.decodeStream(fis, null, opts);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return tmpBmp;
	}

	/**
	 * 详见decodeStreamABitmap(Context context, int resId, int insampleSize)
	 * @param context
	 * @param resId
	 * @return Bitmap
	 */
	public static Bitmap decodeStreamABitmap(Context context, int resId) {
		return decodeStreamABitmap(context, resId, 1);
	}

	/**
	 * 先通过BitmapFactory.decodeStream方法，创建出一个bitmap，再将其设为ImageView的 source，
	 * decodeStream最大的秘密在于其直接调用JNI>>nativeDecodeAsset()来完成decode，
	 * 无需再使用java层的createBitmap，从而节省了java层的空间。
	 * 如果在读取时加上图片的Config参数，可以跟有效减少加载的内存，从而跟有效阻止抛out of Memory异常
	 * 另外，decodeStream直接拿的图片来读取字节码了， 不会根据机器的各种分辨率来自动适应
	 * 使用了decodeStream之后，需要在hdpi和mdpi，ldpi中配置相应的图片资源，
	 * 否则在不同分辨率机器上都是同样大小（像素点数量），显示出来的大小就不对了
	 * @param context
	 * @param resId 资源Id
	 * @param insampleSize 缩小比例
	 * @return Bitmap
	 */
	public static Bitmap decodeStreamABitmap(Context context, int resId, int insampleSize) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = insampleSize;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opts);
	}

	/**
	 * 详见decodeStreamABitmap(Context context, int resId, int insampleSize)
	 * @param context
	 * @param resId
	 * @param targetWidth
	 * @param targetHeight
	 * @return Bitmap
	 */
	public static Bitmap decodeStreamABitmap(Context context, int resId, int targetWidth, int targetHeight) {
		InputStream is = context.getResources().openRawResource(resId);
		int[] imageWH = getImageWH(is);
		if (imageWH == null)
			return null;

		int scale = 1;
		if (imageWH[0] > imageWH[1])
			scale = Math.round((float) imageWH[0] / targetWidth);
		else
			scale = Math.round((float) imageWH[1] / targetHeight);
		scale = scale == 0 ? 1 : scale;

		return decodeStreamABitmap(context, resId, scale);
	}

	/**
	 * 将drawable转换为bitmap
	 * @param drawable
	 * @return Bitmap
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (null == drawable || drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
			return null;
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		if(bitmap == null)
			return null;
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 将bitmap转换为byte[]
	 * @param bm
	 * @return byte[]
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		if(null == bm) return null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] result = baos.toByteArray();
			return result;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将drawable转换为byte[]
	 * @param drawable
	 * @return byte[]
	 */
	public static byte[] drawable2Bytes(Drawable drawable) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			if (drawable instanceof BitmapDrawable) {
				((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
			} else if (drawable instanceof FastBitmapDrawable) {
				((FastBitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
			}
			byte[] result = baos.toByteArray();
			return result;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将byte[]转换为bitmap
	 * @param b
	 * @return 失败返回null
	 */
	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b != null && b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

    /**
     * 将byte[]转换为InputStream
     * @param b
     * @return 失败返回null
     */
    public static InputStream bytes2InputStream(byte[] b) {
        if (null != b) {
            return new ByteArrayInputStream(b);
        } else {
            return null;
        }
    }

	/**
	 * 销毁Bitmap，回收资源
	 * @param bmp
	 */
	public static void destoryBitmap(Bitmap bmp) {
		if (null != bmp && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
	}

	/**
	 * 以JPEG格式保存Bitmap为文件
	 * @param bmp
	 * @param filePath 保存的文件路径
	 * @return boolean
	 */
	public static boolean saveBitmap2file(Bitmap bmp, String filePath) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (null == stream)
			return false;
		return bmp.compress(format, quality, stream);
	}

	/**
	 * 保存Bitmap为文件
	 * @param bmp
	 * @param filePath 保存的文件路径
	 * @param format 压缩格式
	 * @return 成功返回true
	 */
	public static boolean saveBitmap2file(Bitmap bmp, String filePath, CompressFormat format) {
		if (bmp == null || bmp.isRecycled())
			return false;

		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null == stream)
			return false;

		return bmp.compress(format, quality, stream);
	}

	/**
	 * 将字节流生成文件
	 * @param inStream
	 * @param filename
	 */
	public static void saveStream2file(InputStream inStream, String filename) {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(filename);
			// 创建一个Buffer字符串
			byte[] buffer = new byte[1024];
			// 每次读取的字符串长度，如果为-1，代表全部读取完毕
			int len = 0;
			// 使用一个输入流从buffer里把数据读取出来
			while ((len = inStream.read(buffer)) != -1) {
				// 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
				outStream.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null)
					inStream.close();
				if (outStream != null)
					outStream.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 以最省内存的方式读取图片资源
	 * @param context
	 * @param resId
	 * @return 16位的图
	 */
	public static Bitmap readBitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取图片资源
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 图片圆角处理
	 * @param bitmap
	 * @param roundPx 值越大 圆角越大
	 * @return Bitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 销毁Drawable，回收系统资源
	 * @param drawable
	 */
	public static void safeDestoryDrawable(Drawable drawable) {
		if (null == drawable)
			return;
		drawable.setCallback(null);
		drawable = null;
	}

	/**
	 * 销毁Drawable和该Drawable带有的Bitmap，回收系统资源
	 * @param drawable
	 */
	public static void destoryDrawable(Drawable drawable) {
		if (null == drawable)
			return;
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable b = (BitmapDrawable) drawable;
			destoryBitmap(b.getBitmap());
		} else if (drawable instanceof FastBitmapDrawable) {
			FastBitmapDrawable f = (FastBitmapDrawable) drawable;
			destoryBitmap(f.getBitmap());
		}
		drawable.setCallback(null);
		drawable = null;
	}

	/**
	 * 将一个新图片合成到旧图片里
	 * @param src 旧图片
	 * @param dst 新图片
	 * @return Bitmap
	 */
	public static Bitmap craeteComposeBitmap(Bitmap src, Bitmap dst) {
		Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);
		cv.drawBitmap(dst, 0, 0, null);
		return newb;
	}
	
	/**
	 * 读取图片InputStream
	 * 
	 * @param ctx
	 * @param tag
	 *            如: com.xxx.xxx|icons/xx.png格式 如: com.xxx.xxx@1235456格式
	 * @return
	 */
	public static InputStream getImageInputStream(Context ctx, String key, String tag) {
		if (StringUtil.isEmpty(tag)) {
			return null;
		}

		int tagAT = tag.indexOf('@');
		int tagOR = tag.indexOf('|');
		if (tagAT < 0 && tagOR < 0) {
			// 读取文件处理
			try {
                if(tag.endsWith(ThemeGlobal.GUARDED_RES)) {//获取加密资源 caizp 2014-7-18
                    return BaseBitmapUtils.bytes2InputStream(EncodeTools.getResource(tag, key));
                }
				return new FileInputStream(tag);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}

		if (tagAT > 0) {
			// 读res
			String[] arr = tag.split("@");
			if (arr.length == 2) {
				String pname = arr[0];
				String id = arr[1];
				int resId = StringUtil.parseInt(id, 0);
				if (resId != 0) {
					try {
						Context ct;
						// 开头为@时
						if (StringUtil.isEmpty(pname)) {
							ct = ctx;
						} else {
							ct = ctx.createPackageContext(pname,
									Context.CONTEXT_INCLUDE_CODE
											| Context.CONTEXT_IGNORE_SECURITY);
						}
						return ct.getResources().openRawResource(resId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}
		}

		if (tagOR > 0) {
			// 读asset
			String[] s = tag.split("\\|");
			AssetManager am = null;
			String path = "";
			if (s.length != 2) {
				return null;
			}
			if (s[0].length() == 0) {
				am = ctx.getAssets();
			} else {
				path = s[1];
				Context packCtx;
				try {
					packCtx = ctx.createPackageContext(s[0],
							Context.CONTEXT_INCLUDE_CODE
									| Context.CONTEXT_IGNORE_SECURITY);
					am = packCtx.getAssets();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				return am.open(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	/**
	 * 修复Bitmap清晰度问题
	 * @param icon
	 * @param context
	 * @return Bitmap
	 */
	public static Bitmap createIconBitmapThumbnail(Drawable icon, Context context) {
		return createIconBitmapThumbnail(icon, context, null);
	}


    /**
     * 修复Bitmap清晰度问题
     * @param icon
     * @param context
     * @return Bitmap
     */
    public static Bitmap createIconBitmapThumbnail(Drawable icon, Context context, Bitmap reusedBitmap) {
        if (null == icon)
            return null;
        synchronized (sCanvas) {
            if (sIconWidth <= 0 || sIconHeight <= 0) {
                final Resources resources = context.getResources();
//				if(ScreenUtil.isExLardgeScreen()){
                sIconWidth = sIconHeight = (int) resources.getDimensionPixelSize(R.dimen.app_background_size);
//				}else{
//					sIconWidth = sIconHeight = (int) resources.getDimensionPixelSize(R.dimen.app_icon_size);
//				}
            }

            boolean isReuse = false && reusedBitmap != null &&
                    reusedBitmap.getHeight() == sIconHeight &&
                    reusedBitmap.getWidth() == sIconWidth &&
                    reusedBitmap.isMutable() &&
                    !reusedBitmap.isRecycled();
            if(isReuse) {
                Log.e(TAG, "reuse bitmap");
            } else {
//                Log.e(TAG, "create new bitmap");
            }

            int width = sIconWidth;
            int height = sIconHeight;

            if (icon instanceof FastBitmapDrawable) {
                icon = new BitmapDrawable(context.getResources(), ((FastBitmapDrawable) icon).getBitmap());
            }

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            } else if (icon instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int iconWidth = icon.getIntrinsicWidth();
            int iconHeight = icon.getIntrinsicHeight();

            if (width < iconWidth || height < iconHeight) {
                final float ratio = (float) iconWidth / iconHeight;

                if (iconWidth > iconHeight) {
                    height = (int) (width / ratio);
                } else if (iconHeight > iconWidth) {
                    width = (int) (height * ratio);
                }

                final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                Bitmap thumb = null;
                if(isReuse) {
                    thumb = reusedBitmap;
                }else {
                    thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                }
                Canvas canvas = sCanvas;
                canvas.setBitmap(thumb);
                if(isReuse){
                    canvas.drawPaint(PaintUtils.getClearPaint());
                }
                sOldBounds.set(icon.getBounds());
                final int x = (sIconWidth - width) / 2;
                final int y = (sIconHeight - height) / 2;
                icon.setBounds(x, y, x + width, y + height);
                icon.draw(canvas);
                icon.setBounds(sOldBounds);
                return thumb;
            } else if (iconWidth < width && iconHeight < height) {
                final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                Bitmap thumb = null;
                if(isReuse) {
                    thumb = reusedBitmap;
                }else {
                    thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                }
                final Canvas canvas = sCanvas;
                canvas.setBitmap(thumb);
                if(isReuse){
                    canvas.drawPaint(PaintUtils.getClearPaint());
                }
                sOldBounds.set(icon.getBounds());
                icon.setBounds(0, 0, width, height);
                icon.draw(canvas);
                icon.setBounds(sOldBounds);
                return thumb;
            } else if (icon instanceof BitmapDrawable) {
                // Log.i(TAG, "icon instanceof BitmapDrawable");
                return ((BitmapDrawable) icon).getBitmap();
            } else {
                final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                Bitmap thumb = null;
                if(isReuse) {
                    thumb = reusedBitmap;
                }else {
                    thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                }
                final Canvas canvas = sCanvas;
                canvas.setBitmap(thumb);
                if(isReuse){
                    canvas.drawPaint(PaintUtils.getClearPaint());
                }
                sOldBounds.set(icon.getBounds());
                icon.setBounds(0, 0, width, height);
                icon.draw(canvas);
                icon.setBounds(sOldBounds);
                return thumb;
            }
        }
    }


    /**
	 * 获取android系统默认应用程序图标
	 * @param res
	 * @return Bitmap
	 */
	public static Bitmap getAlwaysDefaultAppIcon(Resources res) {
		Bitmap draw = BitmapFactory.decodeResource(res, android.R.drawable.sym_def_app_icon);
		if (draw == null)
			draw = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_application);

		return draw;
	}

	/**
	 * 获取android系统默认应用程序图标
	 * @param res
	 * @return Drawable
	 */
	public static Drawable getDefaultAppDrawable(Resources res) {
		Drawable draw = res.getDrawable(android.R.drawable.sym_def_app_icon);
		if (draw == null)
			draw = res.getDrawable(R.drawable.ic_launcher_application);

		return draw;
	}
	
	/**
	 * 修复Bitmap清晰度问题
	 * @param icon
	 * @param context
	 * @return Bitmap
	 */
	public static Bitmap createIconBitmap(Drawable icon, Context context, boolean isLargeIcon) {
		if (null == icon)
			return null;
		synchronized (sCanvas) {
			sIconWidth = -1;
			final Resources resources = context.getResources();
			if(ScreenUtil.isExLardgeScreen() && isLargeIcon){
				sIconWidth = sIconHeight = (int) resources.getDimensionPixelSize(R.dimen.app_background_size);
			}else{
				sIconWidth = sIconHeight = (int) resources.getDimensionPixelSize(R.dimen.app_icon_size);
			}

			int width = sIconWidth;
			int height = sIconHeight;

			if (icon instanceof FastBitmapDrawable) {
				icon = new BitmapDrawable(context.getResources(), ((FastBitmapDrawable) icon).getBitmap());
			}

			if (icon instanceof PaintDrawable) {
				PaintDrawable painter = (PaintDrawable) icon;
				painter.setIntrinsicWidth(width);
				painter.setIntrinsicHeight(height);
			} else if (icon instanceof BitmapDrawable) {
				// Ensure the bitmap has a density.
				BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
					bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				}
			}
			int iconWidth = icon.getIntrinsicWidth();
			int iconHeight = icon.getIntrinsicHeight();

			if (width < iconWidth || height < iconHeight) {
				final float ratio = (float) iconWidth / iconHeight;

				if (iconWidth > iconHeight) {
					height = (int) (width / ratio);
				} else if (iconHeight > iconWidth) {
					width = (int) (height * ratio);
				}

				final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
				final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(thumb);
				sOldBounds.set(icon.getBounds());
				final int x = (sIconWidth - width) / 2;
				final int y = (sIconHeight - height) / 2;
				icon.setBounds(x, y, x + width, y + height);
				icon.draw(canvas);
				icon.setBounds(sOldBounds);
				return thumb;
			} else if (iconWidth < width && iconHeight < height) {
				final Bitmap.Config c = Bitmap.Config.ARGB_8888;
				final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(thumb);
				sOldBounds.set(icon.getBounds());
				icon.setBounds(0, 0, width, height);
				icon.draw(canvas);
				icon.setBounds(sOldBounds);
				return thumb;
			} else if (icon instanceof BitmapDrawable) {
				// Log.i(TAG, "icon instanceof BitmapDrawable");
				return ((BitmapDrawable) icon).getBitmap();
			} else {
				final Bitmap.Config c = Bitmap.Config.ARGB_8888;
				final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(thumb);
				sOldBounds.set(icon.getBounds());
				icon.setBounds(0, 0, width, height);
				icon.draw(canvas);
				icon.setBounds(sOldBounds);
				return thumb;
			}
		}
	}
	
	/**
	 * 生成指定大小的图标
	 * @param icon
	 * @param context
	 * @param appIconSize
	 * @return Bitmap
	 */
	public synchronized static Bitmap createSpecialIconBitmap(Drawable icon, Context context, int appIconSize) {
		sIconWidth = sIconHeight = appIconSize;
		Bitmap newBmp = createIconBitmapThumbnail(icon, context);
		sIconWidth = -1;
		return newBmp;
	}
	
	
	
	/**
	 * 
	 *  @param bitmap
	 *  @param context
	 *  @param useIconMask
	 *  @return
	 */
	public static Bitmap createIconBitmapFor91Icon(Bitmap bitmap, Context context){
        if (null == bitmap)
            return null;
        synchronized (sCanvas) {
            if (maxRect == null) {
                final Resources resources = context.getResources();
                int iconSize = (int) resources.getDimensionPixelSize(R.dimen.app_background_size);
                maxRect = new Rect(0, 0, iconSize, iconSize);
            }
            int iconSize = maxRect.width();

            bitmap = resizeImage(bitmap, maxRect.width(), maxRect.height());

            boolean isReuse = false && bitmap != null &&
                    bitmap.getHeight() == iconSize &&
                    bitmap.getWidth() == iconSize &&
                    bitmap.isMutable() &&
                    !bitmap.isRecycled();

            final Canvas canvas = sCanvas;
            final Bitmap.Config c = Bitmap.Config.ARGB_8888;
            boolean isDefaultThemeWithDefaultMuduleId = BaseIconCache.isDefaultThemeWithDefaultModuleId(context);
            Bitmap thumb = null;
            if(isReuse) {
                thumb = bitmap;
            }else {
                thumb = Bitmap.createBitmap(iconSize, iconSize, c);
            }
            canvas.setBitmap(thumb);
            if(isReuse) {
                canvas.drawPaint(PaintUtils.getClearPaint());
            }

            canvas.drawBitmap(bitmap, null, maxRect, PaintUtils.getIconPaint());

            if (BaseConfig.iconMask != null) {
                if (isDefaultThemeWithDefaultMuduleId) {
                    if (defaultThemeIconMaskRect == null) {
                        defaultThemeIconMaskRect = LauncherIconDataCache.calcSpecilRectAndScale(maxRect, LauncherIconDataCache.DEFAULT_THEME_MASK_SCALE);
                    }
                    canvas.drawBitmap(BaseConfig.iconMask, null, defaultThemeIconMaskRect, PaintUtils.getDestin(255));
                } else {
                    canvas.drawBitmap(BaseConfig.iconMask, null, maxRect, PaintUtils.getDestin(255));
                }
            }
            if (BaseConfig.iconFrontground != null && !isDefaultThemeWithDefaultMuduleId) {
                canvas.drawBitmap(BaseConfig.iconFrontground, null, maxRect, PaintUtils.getIconPaint());
            }
            if (BaseConfig.iconBackground != null) {
                canvas.drawBitmap(BaseConfig.iconBackground, null, maxRect, PaintUtils.getDstover(255));
            }

            return thumb;
        }
    }
	
	/**
	 * 
	 *  @param bitmap
	 *  @param context
	 *  @return
	 */
	public static Bitmap createIconBitmapForApkIcon(Bitmap bitmap, Context context){
		if (null == bitmap)
			return null;
		synchronized (sCanvas) {
			if(maxRect == null){
				final Resources resources = context.getResources();
				int iconSize = (int) resources.getDimensionPixelSize(R.dimen.app_background_size);
				maxRect = new Rect(0, 0, iconSize, iconSize);
			}
			int iconSize = maxRect.width();

            boolean isReuse = false && bitmap != null &&
                    bitmap.getHeight() == iconSize &&
                    bitmap.getWidth() == iconSize &&
                    bitmap.isMutable() &&
                    !bitmap.isRecycled();
            if(isReuse) {
                Log.e(TAG, "reuse apk bitmap");
            } else {
                Log.e(TAG, "create new apk bitmap");
            }

			final Canvas canvas = sCanvas;
			final Bitmap.Config c = Bitmap.Config.ARGB_8888;
            Bitmap thumb = null;
            if(isReuse) {
                thumb = bitmap;
            }else {
                thumb = Bitmap.createBitmap(iconSize, iconSize, c);
            }
			canvas.setBitmap(thumb);
            if(isReuse) {
                canvas.drawPaint(PaintUtils.getClearPaint());
            }
			
			//画大图标背板
			boolean isDefaultThemeWithDefaultMuduleId = BaseIconCache.isDefaultThemeWithDefaultModuleId(context);
			if(isDefaultThemeWithDefaultMuduleId){
				int[] res = MaskUtils.MatchMask(context, bitmap);
				if (res[0] == 1 && res[1] != -1){// 需要画背板
					Bitmap largeIconMask = LauncherIconSoftReferences.getInstance().getLargeIconBackGround(res[1]);
					canvas.drawBitmap(largeIconMask, null, maxRect, PaintUtils.getIconPaint());
					if(minRect == null){
						Resources resources = context.getResources();
						float scale = resources.getDimensionPixelSize(R.dimen.app_icon_size)/(resources.getDimensionPixelSize(R.dimen.app_background_size)+0.0f);
						int minIconSize = (int) (iconSize*scale);
						int padding = (iconSize - minIconSize)/2;
						minRect = new Rect(padding, padding, padding+minIconSize, padding+minIconSize);
					}
                    bitmap = resizeImage(bitmap, minRect.width(), minRect.height());
					canvas.drawBitmap(bitmap, null, minRect, PaintUtils.getIconPaint());
				}else{//不需要画背板 图标需要放大处理
					if (res[3] != 0) {
						int topPadding = res[3];
						int bottomPadding = res[4];
						int centerY = topPadding+ (iconSize - topPadding - bottomPadding) / 2;
						float canvasScale = iconSize/((iconSize - res[3] - res[4]) + 0.0f);
						canvas.save();
						canvas.scale(canvasScale, canvasScale, iconSize/2, centerY);
						canvas.drawBitmap(bitmap, null, maxRect, PaintUtils.getIconPaint());
						canvas.restore();
					}else{
						canvas.drawBitmap(bitmap, null, maxRect, PaintUtils.getIconPaint());
					}
				}
			}else{
				canvas.drawBitmap(bitmap, null, maxRect, PaintUtils.getIconPaint());
			}

			if(BaseConfig.iconMask != null){
				if(isDefaultThemeWithDefaultMuduleId){
					if(defaultThemeIconMaskRect == null){
						defaultThemeIconMaskRect = LauncherIconDataCache.calcSpecilRectAndScale(maxRect, LauncherIconDataCache.DEFAULT_THEME_MASK_SCALE);
					}
					canvas.drawBitmap(BaseConfig.iconMask, null, defaultThemeIconMaskRect, PaintUtils.getDestin(255));
				}else{
					canvas.drawBitmap(BaseConfig.iconMask, null, maxRect, PaintUtils.getDestin(255));
				}
			}
			if(BaseConfig.iconFrontground != null && !isDefaultThemeWithDefaultMuduleId){
				canvas.drawBitmap(BaseConfig.iconFrontground, null, maxRect, PaintUtils.getIconPaint());
			}
			if(BaseConfig.iconBackground != null){
				canvas.drawBitmap(BaseConfig.iconBackground, null, maxRect,  PaintUtils.getDstover(255));
			}
			return thumb;
		}
	}


    /**
     * @param bitmap1
     * @param bitmap2
     * @param context
     * @return
     */
    public static Bitmap composeBitmap(Bitmap bitmap1, Bitmap bitmap2, Context context) {
        if (null == bitmap1 || null == bitmap2)
            return null;
        synchronized (sCanvas) {
            if (maxRect == null) {
                final Resources resources = context.getResources();
                int iconSize = (int) resources.getDimensionPixelSize(R.dimen.app_background_size);
                maxRect = new Rect(0, 0, iconSize, iconSize);
            }
            int iconSize = maxRect.width();

            final Canvas canvas = sCanvas;
            final Bitmap.Config c = Bitmap.Config.ARGB_8888;

            Bitmap thumb = Bitmap.createBitmap(iconSize, iconSize, c);
            canvas.setBitmap(thumb);
            canvas.drawBitmap(bitmap1, null, maxRect, PaintUtils.getIconPaint());
            canvas.drawBitmap(bitmap2, null, maxRect, PaintUtils.getIconPaint());
            return thumb;
        }
    }

}
