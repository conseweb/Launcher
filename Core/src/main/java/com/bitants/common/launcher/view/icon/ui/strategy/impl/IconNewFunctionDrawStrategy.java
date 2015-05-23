/**
 *
 */
package com.bitants.common.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.common.launcher.view.icon.ui.LauncherIconData;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;


/**
 */
public class IconNewFunctionDrawStrategy extends DrawStrategy {
	
	private static IconNewFunctionDrawStrategy instance;
	
	public static IconNewFunctionDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconNewFunctionDrawStrategy();
		}
		return instance;
	}
	
	
	private IconNewFunctionDrawStrategy() {
	}
	
	@Override
	public DrawPriority getDrawDrawPriority() {
		return DrawPriority.NewFunction;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		Bitmap bitmap = LauncherIconSoftReferences.getInstance().getDrawerNewFunctionFlagIcon();
		canvas.drawBitmap(bitmap, data.viewWidth-bitmap.getWidth(), data.iconResourceData.newfunctionIconTop, null);
	}
}
