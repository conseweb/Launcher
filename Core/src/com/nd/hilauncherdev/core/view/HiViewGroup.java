package com.nd.hilauncherdev.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 简单继承了ViewGroup, 封装一些操作
 * @author ZSY
 * @since 2013-3-26
 */
public abstract class HiViewGroup extends ViewGroup {
	
	protected boolean mIsChildViewCatchTouchEvent = false;

	public HiViewGroup(Context context) {
		super(context);
	}
	
	public HiViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public HiViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * 设置触摸事件是否被子View捕获掉
	 */
	public void setIsChildCatchTouchEvent(boolean isCatch) {
		mIsChildViewCatchTouchEvent = isCatch;
	}
	
}
