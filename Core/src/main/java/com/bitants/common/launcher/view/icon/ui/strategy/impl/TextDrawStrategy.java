package com.bitants.common.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.view.icon.ui.LauncherIconData;
import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 标题绘画策略
 */
public class TextDrawStrategy extends DrawStrategy {
	
	private static TextDrawStrategy instance;
	
	public static TextDrawStrategy getInstance(){
		if(instance == null){
			instance = new TextDrawStrategy();
		}
		return instance;
	}
	
	
	private TextDrawStrategy() {
	}


	@Override
	public DrawStragegyFactory.DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawStragegyFactory.DrawPriority.Text;
	}



	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		if(!StringUtil.isEmpty(data.label)){
			int textWidth = data.getTextWidth();
			int left = (data.viewWidth - textWidth)/2;
			Paint paint = data.isAni? data.alphaPaint : data.titlePaint;
			int y = 0;
			if(isLargeIconMode){
				y = data.iconRects.maxRectAndScale.rect.bottom + data.textHeight + data.getDrawPadding(config);
			}else{
				y = iconRect.bottom + data.textHeight + data.getDrawPadding(config);
			}
			canvas.drawText(data.label.toString(), left, y, paint);
			
		}
	}
	
}
