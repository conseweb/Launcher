package com.bitants.common.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.common.kitset.util.PaintUtils;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.ui.LauncherIconData;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;

public class IconBackgroundDrawStragegy extends DrawStrategy{
	
	private static IconBackgroundDrawStragegy instance;
	
	public static IconBackgroundDrawStragegy getInstance(){
		if(instance == null){
			instance = new IconBackgroundDrawStragegy();
		}
		return instance;
	}
	
	
	private IconBackgroundDrawStragegy() {
	}
	@Override
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.IconBackGround;
	}

	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		if(BaseConfig.iconBackground != null){
			canvas.drawBitmap(BaseConfig.iconBackground, null, maskRect, BaseConfig.iconMask == null ? PaintUtils.getStaticAlphaPaint(255) : PaintUtils.getDstover(255));
		}
	}

}
