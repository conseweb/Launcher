package com.nd.hilauncherdev.launcher.touch;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public interface DragLayerEventHandler {

	/**
	 * 处理DragLayer的dispatchKeyEvent
	 */
	public boolean dispatchKeyEvent(KeyEvent event);

	/**
	 * 处理DragLayer的onInterceptTouchEvent
	 */
	public boolean onInterceptTouchEvent(MotionEvent ev);

	/**
	 * 处理DragLayer的onTouchEvent
	 */
	public boolean onTouchEvent(MotionEvent ev);

	/**
	 * 处理DragLayer的dispatchUnhandledMove
	 */
	public boolean dispatchUnhandledMove(View focused, int direction);

	/**
	 * 是否允许在DragLayer上绘制拖动view
	 */
	public boolean isLayoutDragView();
	
}
