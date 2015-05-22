package com.bitants.launcherdev.launcher.touch;

import android.view.MotionEvent;

/**
 * 支持 多指手势判断
 * @Description
 *
 */
public interface MultiGestureDispatcher {
	/* 双指上划 */
	public void onMultiUp(MotionEvent event);
	/* 双指下滑 */
	public void onMultiDown(MotionEvent event);
	
}
