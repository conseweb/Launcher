package com.bitants.common.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.common.launcher.view.icon.ui.LauncherIconData;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;

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
