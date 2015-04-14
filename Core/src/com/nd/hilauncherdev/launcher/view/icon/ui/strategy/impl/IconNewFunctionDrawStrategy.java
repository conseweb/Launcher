/**
 * @author Michael
 * Date:2013-9-9下午4:20:19
 *
 */
package com.nd.hilauncherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStrategy;


/**
 * @author Michael
 * Date:2013-9-9下午4:20:19
 */
public class IconNewFunctionDrawStrategy extends DrawStrategy{
	
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
