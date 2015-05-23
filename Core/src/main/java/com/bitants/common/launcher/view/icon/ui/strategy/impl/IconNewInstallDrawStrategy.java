package com.bitants.common.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.common.launcher.view.icon.ui.LauncherIconData;
import com.bitants.common.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 
 *
 */
public class IconNewInstallDrawStrategy extends DrawStrategy{
	
	private static IconNewInstallDrawStrategy instance;
	
	public static IconNewInstallDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconNewInstallDrawStrategy();
		}
		return instance;
	}
	
	
	private IconNewInstallDrawStrategy() {
	}

	@Override
	public DrawPriority getDrawDrawPriority() {
		return DrawPriority.NewInstall;
	}

	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		Bitmap bitmap = LauncherIconSoftReferences.getInstance().getDrawerNewInstallFlagIcon();
		canvas.drawBitmap(bitmap, data.viewWidth-bitmap.getWidth(), data.iconResourceData.newInstallIconTop, null);
	}


}
