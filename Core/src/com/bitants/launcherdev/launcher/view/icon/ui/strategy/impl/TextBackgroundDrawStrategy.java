package com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 带有背景的标题绘画策略
 * @author ZSY
 * @since 2013-3-5
 */
public class TextBackgroundDrawStrategy extends DrawStrategy {
	
	private static TextBackgroundDrawStrategy instance;
	
	public static TextBackgroundDrawStrategy getInstance(){
		if(instance == null){
			instance = new TextBackgroundDrawStrategy();
		}
		return instance;
	}
	
	
	private TextBackgroundDrawStrategy() {
	}
	


	@Override
	public DrawStragegyFactory.DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawStragegyFactory.DrawPriority.TextBackground;
	}



	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		if(!StringUtil.isEmpty(data.getLabel())){
			int textWidth = data.getTextWidth();
			int left = (data.viewWidth - textWidth)/2;
			left = left - data.iconResourceData.textBackgroundPaddingLeft;
			left = left < 0 ? 0 : left;
			int padding = Math.abs(left - (data.viewWidth - textWidth)/2);
			int right = left+padding+textWidth+padding;
			if(right >= data.viewWidth){
				right = data.viewWidth;
			}
			RectF mBackgroundRect =  data.iconResourceData.mBackgroundRect;
			int top = 0;
			if(isLargeIconMode){
				top = data.iconRects.maxRectAndScale.rect.bottom + data.getDrawPadding(config) + 
						data.iconResourceData.textBackgroundPaddingTop;
			}else{
				top = iconRect.bottom + data.getDrawPadding(config) + 
						data.iconResourceData.textBackgroundPaddingTop;
			}
			mBackgroundRect.set(left, top, right, top+data.textHeight+data.iconResourceData.textBackgroundPaddingBottom);
			
			canvas.drawRoundRect(mBackgroundRect, 8.0f, 8.0f, data.textBackgroundPanit);
			data.alphaPaint.clearShadowLayer();
			data.iconPaint.clearShadowLayer();
			data.titlePaint.clearShadowLayer();
		}
	}

}
