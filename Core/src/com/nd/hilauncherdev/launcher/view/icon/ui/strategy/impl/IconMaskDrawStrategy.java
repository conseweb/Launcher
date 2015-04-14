package com.nd.hilauncherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.nd.hilauncherdev.kitset.util.PaintUtils;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStrategy;

public class IconMaskDrawStrategy extends DrawStrategy{
	
	private static IconMaskDrawStrategy instance;
	
	public static IconMaskDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconMaskDrawStrategy();
		}
		return instance;
	}
	
	
	private IconMaskDrawStrategy() {
	}
	


	@Override
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.IconMask;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode
			, boolean isDefaultTheme) {
		
	}
	
}
