package com.nd.hilauncherdev.folder;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ZscaleAnimation extends Animation {
	private Rect mSrcRect;
	private Rect mDestRect;
	private int mBoderW;
	private int mTopBoder;
	private float mScaleX;
	private float mScaleY;
	private float moveX;
	private float moveY;
	private int mDirect = 0;
	private float mInterpolatedTime = 0;

	/**
	 * direct 0 打开 1关闭
	 * */
	public ZscaleAnimation(Rect srcRect, Rect destRect, int boderW,
			int topBoder, int bottomBoder, int direct, int offsetY) {
		mSrcRect = srcRect;
		mDestRect = destRect;
		mBoderW = boderW;
		mTopBoder = topBoder;
		mScaleX = (float) mDestRect.width() / (srcRect.width() - mBoderW * 2);
		mScaleY = (float) mDestRect.height()
				/ (srcRect.height() - mTopBoder - bottomBoder);

		moveX = mSrcRect.left + mBoderW - mDestRect.left;
		moveY = mSrcRect.top + mTopBoder - (mDestRect.top + offsetY);
		mDirect = direct;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		Matrix matrix = t.getMatrix();
		matrix.preTranslate(-mBoderW, -mTopBoder);
		// Log.e("zhou", "interpolatedTime="+interpolatedTime);
		if (mDirect == 0) {
			mInterpolatedTime = 1 - interpolatedTime;

		} else {
			mInterpolatedTime = interpolatedTime;
			// mInterpolatedTime=1;
		}
		// Log.e("zhou", "interpolatedTime="+mInterpolatedTime);
		float scaleX = 1 - (1 - mScaleX) * mInterpolatedTime;
		float scaleY = 1 - (1 - mScaleY) * mInterpolatedTime;
		matrix.postScale(scaleX, scaleY);
		matrix.postTranslate(-moveX * mInterpolatedTime, -moveY
				* mInterpolatedTime);
		// matrix.postTranslate(-moveX,-moveY);

		matrix.postTranslate(mBoderW, mTopBoder);
	}
}
