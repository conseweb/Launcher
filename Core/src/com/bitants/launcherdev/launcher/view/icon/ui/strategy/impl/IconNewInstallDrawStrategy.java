package com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;

/**
 * 
 * @author Michael
 * Date:2013-8-26下午3:49:03
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