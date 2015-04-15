package com.bitants.launcherdev.framework.view.draggersliding;

import android.view.View;

import com.bitants.launcherdev.launcher.info.ApplicationInfo;

/**
 * 拖曳选择项
 */
public class DraggerChooseItem {
	
	private View view;
	
	private ApplicationInfo info;

	public DraggerChooseItem(View view, ApplicationInfo info) {
		this.view = view;
		this.info = info;
	}

	public View getView() {
		return view;
	}
	
	public void setView(View view) {
		this.view = view;
	}

	public ApplicationInfo getInfo() {
		return info;
	}

}
