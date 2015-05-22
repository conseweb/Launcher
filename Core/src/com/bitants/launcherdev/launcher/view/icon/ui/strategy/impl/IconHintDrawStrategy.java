package com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconSoftReferences;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 图标提示绘画策略
 */
public class IconHintDrawStrategy extends DrawStrategy{
	
	private static IconHintDrawStrategy instance;
	
	public static IconHintDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconHintDrawStrategy();
		}
		return instance;
	}
	
	
	private IconHintDrawStrategy() {
	}


	@Override
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.Hint;
	}



	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode
			, boolean isDefaultTheme) {
		if(data.mHint != 0){
			Bitmap mNoticeBg = LauncherIconSoftReferences.getInstance().getDefNoticeBg();
			int iconTop = Math.max(0, iconRect.top-data.iconResourceData.hintPadding);
			int iconLeft = (int) ((data.viewWidth-iconRect.width())/2+iconRect.width()*0.8f-data.iconResourceData.hintPadding);
			iconLeft = Math.min(iconLeft, maskRect.right - mNoticeBg.getWidth());
			canvas.drawBitmap(mNoticeBg, iconLeft, iconTop, null);
			
			String hint = String.valueOf(data.mHint);
			float left = iconLeft + (mNoticeBg.getWidth() - data.iconResourceData.hintColorPaint.measureText(data.mHint + "")) / 2;
			float top = iconTop + mNoticeBg.getHeight() / 2 + data.iconResourceData.hintTextHeight/ 3;
			canvas.drawText(hint, left, top, data.iconResourceData.hintColorPaint);
		}
	}

}
