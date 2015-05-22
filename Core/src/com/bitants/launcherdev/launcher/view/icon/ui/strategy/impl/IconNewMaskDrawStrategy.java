package com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 */
public class IconNewMaskDrawStrategy extends DrawStrategy {
	
	private static IconNewMaskDrawStrategy instance;
	
	public static IconNewMaskDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconNewMaskDrawStrategy();
		}
		return instance;
	}
	
	
	private IconNewMaskDrawStrategy() {
	}
	
	
	@Override
	public DrawStragegyFactory.DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawStragegyFactory.DrawPriority.NewMask;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		Bitmap bitmap = LauncherIconSoftReferences.getInstance().getSoftAndGameUpdateIcon();
		canvas.drawBitmap(bitmap, (data.viewWidth+iconRect.width())/2-data.iconResourceData.newMaskPadding, data.iconResourceData.newfunctionIconTop, null);
	}
	
	

}
