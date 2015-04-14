package com.nd.hilauncherdev.launcher.touch;

import android.view.MotionEvent;

/**
 * 支持 多指手势判断
 * @author DingXiaohui
 * @date 2013-7-29
 * @time 上午11:19:26
 * @Description 
 *
 */
public interface MultiGestureDispatcher {
	/* 双指上划 */
	public void onMultiUp(MotionEvent event);
	/* 双指下滑 */
	public void onMultiDown(MotionEvent event);
	
}
