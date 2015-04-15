package com.bitants.launcherdev.framework.effect;

import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;

public class WallpaperFilterView extends View {
	private Bitmap bitmap = null;
	Paint paint = new Paint();

	public WallpaperFilterView(Context paramContext) {
		this(paramContext, null);
	}

	public WallpaperFilterView(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public WallpaperFilterView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onDraw(Canvas canvas) {
		
		if (bitmap != null && !bitmap.isRecycled() && !BaseConfig.isOnScene()) {
			int x = 0;
			int y = 0;
			x = (getWidth() - bitmap.getWidth()) / 2;
			y = (getHeight() - bitmap.getHeight()) / 2;
			canvas.drawBitmap(bitmap, x, y, paint);
		}

		return;
	}
	public void setFilter(String path) {

		if (path == null && bitmap != null) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}

		} else {
			bitmap = getBitmap(path, 1);

		}
		invalidate();
	}

	/**
	 * 从SD卡中读出图片，
	 * */
	private Bitmap getBitmap(String path, int isSca) {
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(path);
		int h = ScreenUtil.getCurrentScreenHeight(getContext());
		int w = ScreenUtil.getCurrentScreenWidth(getContext());

		if (bitmap != null && (bitmap.getWidth() != w || bitmap.getHeight() != h)) {
			Matrix matrix = new Matrix();
			float scale = (float) (w) / bitmap.getWidth();
			matrix.preScale(scale, scale);
			Bitmap tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			if (bitmap != tempBitmap) {
				bitmap.recycle();
				return tempBitmap;
			}
		}
		return bitmap;
	}

	public Bitmap getCurrentBitmap() {
		return bitmap;
	}
}