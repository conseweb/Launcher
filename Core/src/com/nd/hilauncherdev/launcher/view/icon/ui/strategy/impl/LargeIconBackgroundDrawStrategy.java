package com.nd.hilauncherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 画大图标
 * 
 * @author Michael
 * @createtime 2013-7-31
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
