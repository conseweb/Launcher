package com.bitants.launcherdev.launcher.screens;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 填充用的ViewGroup，所有的子view都占有与该ViewGroup一样大小的布局
 */
public class SingleViewGroup extends ViewGroup {

	public SingleViewGroup(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i ++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.GONE)
				continue;
			
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		for (int i = 0; i < count; i ++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.GONE)
				continue;
			
			child.layout(l, t, r, b);
		}
	}

}
