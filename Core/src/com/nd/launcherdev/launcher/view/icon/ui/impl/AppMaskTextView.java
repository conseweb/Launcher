package com.nd.launcherdev.launcher.view.icon.ui.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconView;

/**
 * 普通应用程序图标 <br>
 * Author:ryan <br>
 * Date:2012-7-12下午03:19:49
 */
public class AppMaskTextView extends LauncherIconView {
	static final String TAG = "AppMaskTextView";

	public AppMaskTextView(Context context) {
		super(context);
	}
	
	public AppMaskTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppMaskTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 改变数据 并重新设置各个绘画策略所需数据
	 * 
	 * @author Michael
	 * @createtime 2013-8-7
	 * @param w
	 * @param h
	 */
	protected void initValue(int w, int h) {
		//文件夹中的view不需要注册广播
		isNeedRegisterBroadcastReceiver = false;
		config.setDrawTextBackground(false);
		data.updateData(w, h);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (icon == null) {
			return;
		}
		super.drawCanvas(canvas, config, data);
	}

	public void setLazy(boolean isLazy) {
		// do nothing
	}

	

	@Override
	public void updateText() {
	}

	@Override
	public void udpateIconConfig() {
	}

	@Override
	public void updateNewMaskConfig() {
	}

	@Override
	public void updateHintConfig(int hintCount) {
	}

	@Override
	public void updateDraw() {
	}


	@Override
	protected void initParams(AttributeSet attrs) {
		initParamsDefault(attrs);
	}


	@Override
	protected LauncherIconData createIconMaskData(AttributeSet attrs) {
		return new LauncherIconData(getContext(), attrs);
	}

}
