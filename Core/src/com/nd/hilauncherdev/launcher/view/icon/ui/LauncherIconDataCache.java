/**
 * @author Michael
 * Date:2014-3-19下午1:45:11
 *
 */
package com.nd.hilauncherdev.launcher.view.icon.ui;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.ColorUtil;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.config.preference.BaseSettingsPreference;

/**
 * @author Michael
 * Date:2014-3-19下午1:45:11
 *
 */
public class LauncherIconDataCache {

	private static LauncherIconDataCache instance;
	
	public static final int TEXT_BACKGROUND_ALPHA = 150;
	
	public static final float DEFAULT_THEME_MASK_SCALE = 15/12f;
	
	public static LauncherIconDataCache getInstance(){
		if(instance == null){
			instance = new LauncherIconDataCache();
		}
		return instance;
	}
	
	private LauncherIconDataCache(){
	}
	
	private HashMap<String, IconRects> rectCache = new HashMap<String, IconRects>();
	
	private int defaultFontMeasureSize = -1;
	
	private LauncherIconResourceData iconResourceData;
	
	
	public IconRects getIconRects(int width, int height){
		String key = width+"|"+height;
		return rectCache.get(key);
	}
	
	
	public void updateIconRects(){
		Context context = BaseConfig.getApplicationContext();
		iconResourceData = getIconResourceData(context); 
		iconResourceData.defaultPaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		int paintFontMeasureSize = iconResourceData.defaultPaint.getFontMetricsInt(null);
		int textHeight = paintFontMeasureSize < BaseConfig.defaultFontMeasureSize ? BaseConfig.defaultFontMeasureSize : paintFontMeasureSize;
		for (String key : rectCache.keySet()) {
			String[] strs = key.split("\\|");
			if(strs != null && strs.length == 2){
				try{
					int measureW = Integer.parseInt(strs[0]);
					int measureH = Integer.parseInt(strs[1]);
					IconRects iconRects = rectCache.get(key);
					int targetW = iconResourceData.minIconSize;
					int targetH = iconResourceData.minIconSize + iconResourceData.minMargin+
									iconResourceData.minMargin + textHeight;
					iconRects.minRectAndScale = LauncherIconDataCache.calcRectAndScale(measureW, measureH, targetW, targetH, iconResourceData.minIconSize);
					if(iconRects.defaultThemeFrontminRectAndScale == null){
						iconRects.defaultThemeFrontminRectAndScale = new RectAndScale();
					}
					iconRects.defaultThemeFrontminRectAndScale.rect = LauncherIconDataCache.calcDefaultThemeFrontIconMaskRectAndScale(iconRects.minRectAndScale.rect);
					
					
					targetW = iconResourceData.mediumIconSize;
					targetH = iconResourceData.mediumIconSize + iconResourceData.minMargin+
									iconResourceData.minMargin + textHeight;
					iconRects.mediumRectAndScale = LauncherIconDataCache.calcRectAndScale(measureW, measureH, targetW, targetH, iconResourceData.mediumIconSize);
					if(iconRects.defaultThemeFrontmediumRectAndScale == null){
						iconRects.defaultThemeFrontmediumRectAndScale = new RectAndScale();
					}
					iconRects.defaultThemeFrontmediumRectAndScale.rect = LauncherIconDataCache.calcDefaultThemeFrontIconMaskRectAndScale(iconRects.mediumRectAndScale.rect);
					
					
					
					targetW = iconResourceData.maxIconSize;
					targetH = iconResourceData.maxIconSize + iconResourceData.minMargin+
									iconResourceData.minMargin + textHeight;
					iconRects.maxRectAndScale = LauncherIconDataCache.calcRectAndScale(measureW, measureH, targetW, targetH, iconResourceData.maxIconSize);
				    if(iconRects.defaultThemeFrontmaxRectAndScale == null){
				    	iconRects.defaultThemeFrontmaxRectAndScale = new RectAndScale();
				    }
					iconRects.defaultThemeFrontmaxRectAndScale.rect = LauncherIconDataCache.calcDefaultThemeFrontIconMaskRectAndScale(iconRects.maxRectAndScale.rect);
					
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setIconRects(int width, int height, IconRects iconRects){
		String key = width+"|"+height;
		rectCache.put(key, iconRects);
	}
	
	/**
	 * 获取默认的文字高度
	 * @author Michael
	 * Date:2014-3-19下午3:22:39
	 *  @param context
	 *  @return
	 */
	public int getDefaultFontMeasureSize(Context context){
		if(defaultFontMeasureSize == -1){
			Paint paint = new Paint();
			paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.default_launcher_icon_text_size));
			defaultFontMeasureSize = paint.getFontMetricsInt(null);
		}
		return defaultFontMeasureSize;
	}
	
	
	public LauncherIconResourceData getIconResourceData(Context mContext) {
		if(iconResourceData == null){
			iconResourceData = new LauncherIconResourceData(mContext);
		}
		return iconResourceData;
	}


	public static class IconRects{
		public RectAndScale minRectAndScale;
		public RectAndScale mediumRectAndScale;
		public RectAndScale maxRectAndScale;
		//将大图标放大一点 画前蒙板 默认主题使用
		public RectAndScale defaultThemeFrontminRectAndScale;
		public RectAndScale defaultThemeFrontmediumRectAndScale;
		public RectAndScale defaultThemeFrontmaxRectAndScale;
		public RectAndScale fillRectAndScale;
		public RectAndScale fillFitCenterRectAndScale;
		
	}
	
	public static class RectAndScale{
		public float scale = 1.0f;
		public Rect rect;
	}
	
	
	/**
	 * 
	 * @author Michael
	 * Date:2014-3-19下午2:26:11
	 *  @param measureW
	 *  @param measureH
	 *  @param targetW
	 *  @param targetH
	 *  @return
	 */
	public static RectAndScale calcRectAndScale(int measureW, int measureH, int targetW, int targetH, int iconSize){
		Context context = BaseConfig.getApplicationContext();
		RectAndScale rectAndScale = new RectAndScale();
		rectAndScale.rect = new Rect();
		float scaleW = 1.0f;
		float scaleH = 1.0f;
		if(targetW > measureW){
			scaleW = measureW * 1.0f / targetW;
			rectAndScale.rect.left = 0;
		}else{
			rectAndScale.rect.left = (measureW - iconSize) / 2;
		}
		rectAndScale.rect.right = rectAndScale.rect.left + iconSize;
		if(targetH > measureH){
			scaleH = measureH * 1.0f / targetH;
			rectAndScale.rect.top = LauncherIconDataCache.getInstance().getIconResourceData(context).minMargin;
		}else{
			rectAndScale.rect.top = (measureH - targetH) / 2;
		}
		rectAndScale.rect.bottom = rectAndScale.rect.top + iconSize;
		rectAndScale.scale = scaleW < scaleH ? scaleW : scaleH;
		return rectAndScale;
	}
	
	/**
	 * 
	 * @author Michael
	 * Date:2014-3-19下午2:26:11
	 *  @param originalW
	 *  @param originalH
	 *  @param targetW
	 *  @param targetH
	 *  @return
	 */
	public static RectAndScale fillRect(int measureW, int measureH, boolean fitCenter){
		RectAndScale rectAndScale = new RectAndScale();
		rectAndScale.rect = new Rect();
		int right = measureW;
		int bottom = measureH;
		if(fitCenter){
			right = bottom = measureW < measureH ? measureW : measureH;
		}
		rectAndScale.rect.left = 0;
		rectAndScale.rect.top = 0;
		rectAndScale.rect.right = right;
		rectAndScale.rect.bottom = bottom;
		return rectAndScale;
	}
	
	
	
	public class LauncherIconResourceData{
		
		public int  minIconSize, 
		             mediumIconSize, 
		             maxIconSize, 
		             minMargin, 
		             drawPadding ,
		             dockBarDrawPadding,
		             textSize, 
		             textColor, 
		             shadowColor,
		             hintTextHeight,
		             textBackgroundPaddingLeft,
		             textBackgroundPaddingTop,
		             textBackgroundPaddingRight,
		             textBackgroundPaddingBottom,
		             newfunctionIconTop,
		             newInstallIconTop,
		             newMaskPadding,
		             hintPadding;
		
		public Paint hintColorPaint;
		
		public Paint defaultPaint;
		
		public RectF mBackgroundRect = new RectF();
		
		public LauncherIconResourceData(Context mContext){
			minIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
			maxIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.app_background_size);
			mediumIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_bigsize);
			minMargin = mContext.getResources().getDimensionPixelSize(R.dimen.min_padding);
			drawPadding = mContext.getResources().getDimensionPixelSize(R.dimen.text_drawpadding);
			textSize = mContext.getResources().getDimensionPixelSize(R.dimen.text_size);
			
			textColor = BaseSettingsPreference.getInstance().getAppNameColor();
			shadowColor = ColorUtil.antiColorAlpha(255, textColor);
			
			hintColorPaint = new Paint();
			hintColorPaint.setTextSize(mContext.getResources().
					getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize));
			hintColorPaint.setColor(Color.WHITE);
			hintColorPaint.setAntiAlias(true);
			int paintFontMeasureSize = hintColorPaint.getFontMetricsInt(null);
			hintTextHeight =  paintFontMeasureSize < BaseConfig.defaultFontMeasureSize ? BaseConfig.defaultFontMeasureSize : paintFontMeasureSize;
		
			defaultPaint = new Paint();
			
			newfunctionIconTop = ScreenUtil.dip2px(mContext, 20);
			newInstallIconTop = ScreenUtil.dip2px(mContext, 5);
			textBackgroundPaddingTop = ScreenUtil.dip2px(mContext, 1);
			textBackgroundPaddingLeft = ScreenUtil.dip2px(mContext, 3);
			textBackgroundPaddingRight = ScreenUtil.dip2px(mContext, 3);
			textBackgroundPaddingBottom = ScreenUtil.dip2px(mContext, 4);
			dockBarDrawPadding = ScreenUtil.dip2px(mContext, 1);
			newMaskPadding = ScreenUtil.dip2px(mContext, 4);
			hintPadding = ScreenUtil.dip2px(mContext, 4);
		}
	}
	
	/**
	 * 
	 * @author Michael
	 * Date:2014-3-19下午2:26:11
	 *  @param measureW
	 *  @param measureH
	 *  @param targetW
	 *  @param targetH
	 *  @return
	 */
	public static Rect calcSpecilRectAndScale(Rect rect, float scaleSize){
		Rect curRect = new Rect();
		int height = rect.height();
		int realHeight = (int) (height*scaleSize);
		int padding =  (realHeight - height)/2;
		curRect.top = rect.top - padding;
		curRect.left = rect.left - padding;
		curRect.right = rect.right + padding;
		curRect.bottom = rect.bottom + padding;
		return curRect;
	}
	
	/**
	 * 
	 * @author Michael
	 * Date:2014-3-19下午2:26:11
	 *  @param measureW
	 *  @param measureH
	 *  @param targetW
	 *  @param targetH
	 *  @return
	 */
	public static Rect calcDefaultThemeFrontIconMaskRectAndScale(Rect rect){
		Rect curRect = new Rect();
		int width = rect.width();
		int d_width = width*148/120;
		int w_padding = (d_width - width)/2;
		
		
		curRect.left = rect.left - w_padding;
		curRect.right = rect.right + w_padding;
		curRect.top = rect.top;
		
		int height = rect.height();
		int d_height = height*151/120;
		curRect.bottom = curRect.top + d_height;
		return curRect;
	}


}
