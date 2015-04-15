package com.nd.launcherdev.launcher.touch.outline;

import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * 用来实现拖放VIew时，让View的轮廓渐显和渐聊的动画类
 */
public class InterruptibleInOutAnimator {
	private long mOriginalDuration;
	private float mOriginalFromValue;
	private float mOriginalToValue;
	private ValueAnimator mAnimator;

	private boolean mFirstRun = true;

	private Object mTag = null;
	boolean mIsOut = true;
	private static final int STOPPED = 0;
	private static final int IN = 1;
	private static final int OUT = 2;

	/**
	 * 动画的方法，取值为STOPPED(0)、IN(1)、OUT(2)
	 * */
	private int mDirection = STOPPED;
	private String mLogTag = null;

	void setLogTag(String logTag) {
		mLogTag = logTag;
	}

	public InterruptibleInOutAnimator(long duration, float fromValue, float toValue) {
		mAnimator = ValueAnimator.ofFloat(fromValue, toValue).setDuration(duration);
		mOriginalDuration = duration;
		mOriginalFromValue = fromValue;
		mOriginalToValue = toValue;

		mAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDirection = STOPPED;
			}
		});
	}

	/** 根据输入参数决定执行渐显或者渐隐的动画 */
	private void animate(int direction) {

		mDirection = direction;
		float toValue = (direction == IN) ? mOriginalToValue : mOriginalFromValue;
		float startValue = (direction == IN) ? mOriginalFromValue : mOriginalToValue;
		mAnimator.setDuration(mOriginalDuration);
		mAnimator.setFloatValues(startValue, toValue);
		mAnimator.start();
		mFirstRun = false;
	}

	/**
	 * 动画是否已停止
	 * */
	public boolean isStopped() {
		return mDirection == STOPPED;
	}

	/** 执行渐显动画 */
	public void animateIn() {
		mIsOut = false;
		animate(IN);
	}

	/** 执行渐隐动画 */
	public void animateOut() {
		mIsOut = true;
		animate(OUT);

	}

	/**
	 * 设置Tag，实际上就是轮廓图片
	 * */
	public void setTag(Object tag) {
		mTag = tag;
	}

	/**
	 * 设置cellLayout,即这个动画是在这个CellLayout上执行的
	 * */
	CellLayout mCellLayout;

	public void setCellLayout(CellLayout view) {
		mCellLayout = view;
	}

	/**
	 * 获取cellLayout,即这个动画是在该CellLayout上执行的
	 * */
	public CellLayout getCellLayout() {
		return mCellLayout;
	}

	/**
	 * 获取Tag，实际上就是轮廓图片
	 * */
	public Object getTag() {
		return mTag;
	}

	public ValueAnimator getAnimator() {
		return mAnimator;
	}

	/**
	 * 是否正在执行渐隐动画
	 * */
	public boolean isOut() {
		return mIsOut;
	}
	
	/**直接清除轮廓 不执行渐隐动作*/
	public void clear() {
		CellLayout cellLayout = mCellLayout;
		if (cellLayout != null) {
			setCellLayout(null);
			cellLayout.invalidate();
		}
		mIsOut = true;
	}
}
