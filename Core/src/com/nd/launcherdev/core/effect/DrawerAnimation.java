package com.nd.launcherdev.core.effect;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class DrawerAnimation extends Animation {

	public static final int ACTION_SHOW = 0;
	public static final int ACTION_HIDE = 1;

	public static final int ANIM_TYPE_DEFAULT = 0;
	public static final int ANIM_TYPE_RANDOM = 1;	
	public static final int ANIM_TYPE_SCALE = 2;
	public static final int ANIM_TYPE_SCALE_EX = 3;
	public static final int ANIM_TYPE_WIND_MILL = 4;
	public static final int ANIM_TYPE_TV = 5;
	public static final int ANIM_TYPE_DOOR = 6;

	public Camera mCamera = null;
	public int mWidth = 0;
	public int mHeight = 0;
	public int mAnimationType = 0;
	public int mActionType = 0;
    public static int lastAnimationType = ANIM_TYPE_DEFAULT;

	public DrawerAnimation(int animationType, int actionType) {
		mWidth = 0;
		mHeight = 0;
		mAnimationType = animationType == ANIM_TYPE_RANDOM ? ((int) (Math.random() * 6)) + 2 : animationType;
		if (mAnimationType > 6) {
			mAnimationType = 0;
		}		
		mActionType = actionType;
	}

	public int getAnimationType() {
		return mAnimationType;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		mCamera = new Camera();
		super.initialize(width, height, parentWidth, parentHeight);
		mWidth = width;
		mHeight = height;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		switch (mAnimationType) {
		case ANIM_TYPE_WIND_MILL:
			if (mActionType == ACTION_SHOW) {
				windMillAnimationAppear(interpolatedTime, t);
			} else if (mActionType == ACTION_HIDE) {
				windMillAnimationDisappear(interpolatedTime, t);
			}
			break;
		case ANIM_TYPE_SCALE:
			if (mActionType == ACTION_SHOW) {
				scaleAnimationAppear(interpolatedTime, t);
			} else if (mActionType == ACTION_HIDE) {
				scaleAnimationDisappear(interpolatedTime, t);
			}
			break;
		case ANIM_TYPE_DOOR:
			if (mActionType == ACTION_SHOW) {
				doorAnimationAppear(interpolatedTime, t);
			} else if (mActionType == ACTION_HIDE) {
				doorAnimationDisappear(interpolatedTime, t);
			}
			break;
		case ANIM_TYPE_TV:
			if (mActionType == ACTION_SHOW) {
				tvAnimationAppear(interpolatedTime, t);
			} else if (mActionType == ACTION_HIDE) {
				tvAnimationDisappear(interpolatedTime, t);
			}
			break;
		case ANIM_TYPE_SCALE_EX:
			if (mActionType == ACTION_SHOW) {
				scaleAnimationAppearEx(interpolatedTime, t);
			} else if (mActionType == ACTION_HIDE) {
				scaleAnimationDisappearEx(interpolatedTime, t);
			}
			break;
		default:
			if (mActionType == ACTION_SHOW) {
				alphaAnimationAppear(interpolatedTime, t);
			} else if (mActionType == ACTION_HIDE) {
				alphaAnimationDisappear(interpolatedTime, t);
			}
			break;
		}
	}

	// 出现时的风车效果
	private void windMillAnimationAppear(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		float Scale = (float) (0.3 + 0.7 * interpolatedTime); // 缩放比，0.3~1
		int deg = (int) (270 * (1 - interpolatedTime)); // 角度 ，270~0
		float alpha = (float) (0.7 * interpolatedTime + 0.3); // 透明度,0.3~1
		// alpha=1;
		mCamera.save();
		mCamera.rotateZ(deg);
		mCamera.getMatrix(tMatrix);
		mCamera.restore();

		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);

		tMatrix.postScale(Scale, Scale);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
		t.setAlpha(alpha);
	}

	// 消失时的风车效果
	private void windMillAnimationDisappear(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		float Scale = (float) (1 - 0.7 * interpolatedTime); // 缩放比，1~0.3
		int deg = (int) (270 * interpolatedTime); // 角度 ，0~270
		float alpha = 1 - 1 * interpolatedTime; // 透明度,1~0
		mCamera.save();
		mCamera.rotateZ(deg);
		mCamera.getMatrix(tMatrix);
		mCamera.restore();

		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postScale(Scale, Scale);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
		t.setAlpha(alpha);
	}

	// 出现时的缩放效果
	private void scaleAnimationAppear(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		float Scale = 2 - (1 * interpolatedTime); // 缩放比 2~1
		float alpha = 1 * interpolatedTime; // 透明度1~0

		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postScale(Scale, Scale);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
		t.setAlpha(alpha);
	}

	// 消失时的缩放效果
	private void scaleAnimationDisappear(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		float Scale = 1 + (1 * interpolatedTime); // 缩放比 1~2
		float alpha = 1 - 1 * interpolatedTime; // 透明度1~0

		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postScale(Scale, Scale);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
		t.setAlpha(alpha);
	}

	// 出现时的开门效果
	private void doorAnimationAppear(float interpolatedTime, Transformation t) {
		double SinValue = 0;
		Matrix tMatrix = t.getMatrix();
		int deg = (int) (60 * (1 - interpolatedTime)); // 角度 ，60~0

		SinValue = Math.sin(deg * Math.PI / 180);
		float z = (float) (mWidth * SinValue / 2); // 旋转后，Z发生变生，所以要计算Z值，用于修正
		mCamera.save();
		mCamera.translate(0f, 0f, z);
		mCamera.rotateY(deg);
		mCamera.getMatrix(tMatrix);
		mCamera.restore();
		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);

	}

	// 消失时的开门效果
	private void doorAnimationDisappear(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		int deg = (int) (90 * interpolatedTime); // 角度 ，0~90

		mCamera.save();
		mCamera.rotateY(-deg); // 顺时针转，加负号
		mCamera.getMatrix(tMatrix);
		mCamera.restore();

		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
	}

	// 出现时的电视机效果
	private void tvAnimationAppear(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		if (interpolatedTime < 0.2) {
			tMatrix.postScale(3.75f * interpolatedTime + 0.75f, 0.05f * interpolatedTime);
		} else {
			tMatrix.postScale(-0.625f * interpolatedTime + 1.625f, 1.2375f * interpolatedTime - 0.2375f);
		}
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);

	}

	// 消失时的电视机效果
	private void tvAnimationDisappear(float interpolatedTime, Transformation t) {

		Matrix tMatrix = t.getMatrix();
		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);

		if (interpolatedTime < 0.8) {
			tMatrix.postScale(1 + 0.625f * interpolatedTime, 1 - interpolatedTime / 0.8f + 0.01f);
		} else {
			tMatrix.postScale(5f * (1.1f - interpolatedTime), (1f - interpolatedTime) * 0.01f);
		}
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);

	}

	// 出现时的缩放效果
	private void scaleAnimationAppearEx(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		float Scale = (float) (0.8 + (0.2 * interpolatedTime)); // 缩放比 2~1
		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postScale(Scale, Scale);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
		float alpha = 1 * interpolatedTime; // 透明度0~1
		t.setAlpha(alpha);
	}

	// 消失时的缩放效果
	private void scaleAnimationDisappearEx(float interpolatedTime, Transformation t) {
		Matrix tMatrix = t.getMatrix();
		float Scale = (float) (1 - (0.2 * interpolatedTime)); // 缩放比 1~2
		tMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
		tMatrix.postScale(Scale, Scale);
		tMatrix.postTranslate(mWidth / 2, mHeight / 2);
		float alpha = 1 - 1 * interpolatedTime; // 透明度1~0
		t.setAlpha(alpha);

	}

	// 出现时的默认淡出效果
	private void alphaAnimationAppear(float interpolatedTime, Transformation t) {
		t.setAlpha(interpolatedTime);
	}

	// 消失时的默认淡出效果
	private void alphaAnimationDisappear(float interpolatedTime, Transformation t) {
		t.setAlpha(1 - interpolatedTime);
	}
}
