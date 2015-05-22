package com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

import com.bitants.launcherdev.kitset.util.PaintUtils;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.launcherdev.kitset.util.PaintUtils;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.launcherdev.kitset.util.PaintUtils;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 桌面图标绘画策略
 */
public class IconDrawStrategy extends DrawStrategy {

	
	private static IconDrawStrategy instance;
	
	public static IconDrawStrategy getInstance(){
		if(instance == null){
			instance = new IconDrawStrategy();
		}
		return instance;
	}
	
	
	private IconDrawStrategy() {
	}
	
	@Override
	public DrawStragegyFactory.DrawPriority getDrawDrawPriority() {
		return DrawStragegyFactory.DrawPriority.Icon;
	}


	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config,
			LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme) {
		Bitmap mIcon = data.getBitmap();
		if(mIcon != null){
			drawBitmapWithColorFilter(config, canvas, mIcon, null, iconRect, data.iconPaint);
			if(isDefaultTheme && BaseConfig.iconFrontground != null && config.isDrawFrontIconMask() && !config.isCustomIcon() && isLargeIconMode){
				drawBitmapWithColorFilter(config, canvas, BaseConfig.iconFrontground, null, maskRect, data.iconPaint);
			}
		}
	}
	
	private void drawBitmapWithColorFilter(LauncherIconViewConfig config, Canvas canvas, Bitmap bitmap, Rect src, Rect dst, Paint paint){
		if(!config.isDrawNotMergeFoler()){					
			canvas.drawBitmap(bitmap, src, dst, paint);
		}else{
			ColorFilter cf = paint.getColorFilter();
			int alpha = paint.getAlpha();
			paint.setColorFilter(PaintUtils.getNotMergeFolderPaintFilter());
			paint.setAlpha(PaintUtils.getNotMergeFolderPaintAlpha());
			canvas.drawBitmap(bitmap, src, dst,paint);
			paint.setColorFilter(cf);
			paint.setAlpha(alpha);
		}
		
	}
}
