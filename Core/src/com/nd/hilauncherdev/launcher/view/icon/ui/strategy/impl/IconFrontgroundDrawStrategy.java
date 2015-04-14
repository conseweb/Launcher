package com.nd.hilauncherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.nd.hilauncherdev.theme.pref.ThemeSharePref;

public class IconFrontgroundDrawStrategy extends DrawStrategy{

	
	private static IconFrontgroundDrawStrategy instance;
	
	public static IconFrontgroundDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconFrontgroundDrawStrategy();
		}
		return instance;
	}
	
	
	private IconFrontgroundDrawStrategy() {
	}
	
	@Override
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.IconFrontground;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode
			, boolean isDefaultTheme) {
		
	}
}
