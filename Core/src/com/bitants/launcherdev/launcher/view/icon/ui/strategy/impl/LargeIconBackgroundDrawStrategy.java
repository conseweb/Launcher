package com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 画大图标
 * 
 */
public class LargeIconBackgroundDrawStrategy extends DrawStrategy {

	
	private static LargeIconBackgroundDrawStrategy instance;
	
	public static LargeIconBackgroundDrawStrategy getInstance(){
		if(instance == null){
			instance = new LargeIconBackgroundDrawStrategy();
		}
		return instance;
	}
	
	
	private LargeIconBackgroundDrawStrategy() {
	}


	@Override
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.LargeIconBackgroud;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode , boolean isDefaultTheme) {

	}

}
