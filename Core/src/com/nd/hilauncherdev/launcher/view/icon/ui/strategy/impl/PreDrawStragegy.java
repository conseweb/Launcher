package com.nd.hilauncherdev.launcher.view.icon.ui.strategy.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;

import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStrategy;

public class PreDrawStragegy extends DrawStrategy{
	
	private static PreDrawStragegy instance;
	
	public static PreDrawStragegy getInstance(){
		if(instance == null){
			instance = new PreDrawStragegy();
		}
		return instance;
	}
	
	
	private PreDrawStragegy() {
	}
	
	@Override
	public DrawPriority getDrawDrawPriority() {
		// TODO Auto-generated method stub
		return DrawPriority.Prepare;
	}

	@Override
	public void draw(Canvas canvas, LauncherIconViewConfig config, LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode
			, boolean isDefaultTheme) {
		/**
		 * 去除画布锯齿
		 */
		canvas.setDrawFilter(PaintFlagsDrawFilterEx.getInstance(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		float scaleSize = BaseConfig.NO_DATA_FLOAT;
		scaleSize = data.getScale(config);
		if (scaleSize != 1f && scaleSize != BaseConfig.NO_DATA_FLOAT) {
			canvas.scale(scaleSize, scaleSize, data.viewWidth/2, 0);
		}
	}
	
	/**
	 * 避免每次新建对象
	 * @author dingdj
	 * Date:2013-11-4上午11:59:46
	 *
	 */
	public static class PaintFlagsDrawFilterEx extends PaintFlagsDrawFilter{
		
		private static PaintFlagsDrawFilterEx instance;
		
		public static PaintFlagsDrawFilter getInstance(int clearBits, int setBits){
			if(instance == null){
				instance = new PaintFlagsDrawFilterEx(clearBits, setBits);
			}
			return instance;
		}
		/**
		 * @param clearBits
		 * @param setBits
		 */
		private PaintFlagsDrawFilterEx(int clearBits, int setBits) {
			super(clearBits, setBits);
			// TODO Auto-generated constructor stub
		}
		
	}
}
