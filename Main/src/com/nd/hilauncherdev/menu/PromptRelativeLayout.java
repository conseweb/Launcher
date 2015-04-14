package com.nd.hilauncherdev.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 提示标签布局，注意使用的位置
 */
public class PromptRelativeLayout extends RelativeLayout{
	private String keys;

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public PromptRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PromptRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PromptRelativeLayout(Context context) {
		super(context);
	}


}
