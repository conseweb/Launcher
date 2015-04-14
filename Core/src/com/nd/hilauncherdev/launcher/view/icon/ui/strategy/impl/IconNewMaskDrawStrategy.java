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
 * @author dingdj
 * @createtime 2013-7-31
 */
public class IconNewMaskDrawStrategy extends DrawStrategy{
	
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
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.NewMask;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		Bitmap bitmap = LauncherIconSoftReferences.getInstance().getSoftAndGameUpdateIcon();
		canvas.drawBitmap(bitmap, (data.viewWidth+iconRect.width())/2-data.iconResourceData.newMaskPadding, data.iconResourceData.newfunctionIconTop, null);
	}
	
	

}
