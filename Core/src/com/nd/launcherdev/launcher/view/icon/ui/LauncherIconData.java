package com.nd.launcherdev.launcher.view.icon.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.nd.android.pandahome2.R;
import com.nd.launcherdev.kitset.util.ColorUtil;
import com.nd.launcherdev.kitset.util.PaintUtils2;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.nd.launcherdev.launcher.support.BaseIconCache;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconDataCache.IconRects;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconDataCache.LauncherIconResourceData;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconDataCache.RectAndScale;
import com.nd.launcherdev.theme.pref.ThemeSharePref;
import com.nd.launcherdev.kitset.util.PaintUtils2;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.config.BaseConfig;

/**
 * view的相关数据
 * @author Michael
 * @createtime 2013-7-30
 */
public class LauncherIconData {
	
	private static final String TAG = "LauncherIconData";
	protected Context mContext;
	/**
	 * 各种Rect和画布的缩放比例
	 */
	public IconRects iconRects;
	
	/**
	 * 图标
	 */
	protected Bitmap bitmap;
	
	/**
	 * 文字
	 */
	public CharSequence label;
	
	/**
	 * 在资源中的值
	 */
	public LauncherIconResourceData iconResourceData;
	
	public Paint iconPaint;
	public Paint titlePaint;
	public Paint alphaPaint;
	public Paint textBackgroundPanit;
	/**
	 * 判断是否值已填充
	 */
	protected boolean isDataReady;
	
	/**
	 * view的宽度
	 */
	public int viewWidth;
	
	/**
	 * 文字的宽度
	 */
	protected int textWidth;
	
	public int textHeight;
	
	public int mHint;
	
	public boolean isAni;
	
	protected int textBackgroundAlpha;
	
	public float scaleCenterY = 0f;
	public float canvasScale = 1f;
	
	private boolean textSizeFixed = false;

	
	public LauncherIconData(Context mContext){
		this.mContext = mContext;
		iconResourceData = LauncherIconDataCache.getInstance().getIconResourceData(mContext);
		
		iconPaint = new Paint();
		iconPaint.setDither(true);
		iconPaint.setAntiAlias(true);
		
		
		titlePaint = new Paint();
		titlePaint.setDither(true);
		titlePaint.setAntiAlias(true);
		int textColor = BaseSettingsPreference.getInstance().getAppNameColor();
		int shadowColor = ColorUtil.antiColorAlpha(255, textColor);
		titlePaint.setColor(textColor);
		titlePaint.setShadowLayer(1, 1, 1, shadowColor);
		titlePaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		PaintUtils2.assemblyTypeface(titlePaint);
		
		alphaPaint = new Paint();
		alphaPaint.setDither(true);
		alphaPaint.setAntiAlias(true);
		alphaPaint.setColor(textColor);
		alphaPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		alphaPaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		
		textBackgroundPanit = new Paint();
		textBackgroundPanit.setDither(true);
		textBackgroundPanit.setAntiAlias(true);
		textBackgroundPanit.setColor(Color.BLACK);
		textBackgroundPanit.setAlpha(LauncherIconDataCache.TEXT_BACKGROUND_ALPHA);
	}
	
	public LauncherIconData(Context mContext, AttributeSet attrs){
		this.mContext = mContext;
		iconResourceData = LauncherIconDataCache.getInstance().getIconResourceData(mContext);
		
		iconPaint = new Paint();
		iconPaint.setDither(true);
		iconPaint.setAntiAlias(true);
		
		
		titlePaint = new Paint();
		titlePaint.setDither(true);
		titlePaint.setAntiAlias(true);
		int textColor = BaseSettingsPreference.getInstance().getAppNameColor();
		int shadowColor = ColorUtil.antiColorAlpha(255, textColor);
		titlePaint.setColor(textColor);
		titlePaint.setShadowLayer(1, 1, 1, shadowColor);
		titlePaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		PaintUtils2.assemblyTypeface(titlePaint);
		
		alphaPaint = new Paint();
		alphaPaint.setDither(true);
		alphaPaint.setAntiAlias(true);
		alphaPaint.setColor(Color.WHITE);
		alphaPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		alphaPaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		
		textBackgroundPanit = new Paint();
		textBackgroundPanit.setDither(true);
		textBackgroundPanit.setAntiAlias(true);
		textBackgroundPanit.setColor(Color.BLACK);
		
		if(attrs != null){
//			textSizeFixed = true;
			TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LauncherIconView);
			titlePaint.setColor(a.getColor(R.styleable.LauncherIconView_custom_text_color, textColor));
			titlePaint.setShadowLayer(1, 1, 1, a.getColor(R.styleable.LauncherIconView_custom_shadow_color, shadowColor));
			titlePaint.setTextSize(iconResourceData.textSize);
			a.recycle();
		}
	}
	
	
	/**
	 * 计算出所有需要的数据
	 * @author Michael
	 * Date:2014-3-20下午3:25:14
	 *  @param widthSize
	 *  @param heightSize
	 */
	public void updateData(int measureW, int measureH){
		//先从缓存中获取
		iconRects = LauncherIconDataCache.getInstance().getIconRects(measureW, measureH);
		if(iconRects == null){
			iconRects = new IconRects();
			int paintFontMeasureSize = titlePaint.getFontMetricsInt(null);
			textHeight = paintFontMeasureSize < BaseConfig.defaultFontMeasureSize ? BaseConfig.defaultFontMeasureSize : paintFontMeasureSize;
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
			
			
			iconRects.fillRectAndScale = LauncherIconDataCache.fillRect(measureW, measureH, false);
			iconRects.fillFitCenterRectAndScale = LauncherIconDataCache.fillRect(measureW, measureH, true);
			
			//放入缓存
			LauncherIconDataCache.getInstance().setIconRects(measureW, measureH, iconRects);
		}
		viewWidth = measureW;
		isDataReady = true;
		if(!textSizeFixed){
			titlePaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
			int paintFontMeasureSize = titlePaint.getFontMetricsInt(null);
			textHeight = paintFontMeasureSize < BaseConfig.defaultFontMeasureSize ? BaseConfig.defaultFontMeasureSize : paintFontMeasureSize;
		}else{
			titlePaint.setTextSize(iconResourceData.textSize);
			int paintFontMeasureSize = titlePaint.getFontMetricsInt(null);
			textHeight = paintFontMeasureSize;
		}
	}
	
	
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Bitmap icon) {
		this.bitmap = icon;
	}
	
	/**
	 * 设置文字
	 * @author Michael
	 * Date:2014-3-20下午3:46:41
	 *  @param label
	 */
	public void setTitle(CharSequence label){
		this.label = label;
		if (StringUtil.isEmpty(label)){
			textWidth = 0;
			return;
		}
		if(!isDataReady){
			textWidth = 0;
		}
		if(textSizeFixed){
			titlePaint.setTextSize(iconResourceData.textSize);
		}else{
			titlePaint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		}
		
		textWidth = (int) titlePaint.measureText(label.toString());
		int maxTextWidth = viewWidth - ScreenUtil.dip2px(mContext, 7);
		if (textWidth > maxTextWidth) {
			int mid = 0;
			for (int i = 1; i <= label.length(); i++) {
				int len = (int) titlePaint.measureText(label, 0, i);
				if (len > maxTextWidth) {
					mid = i;
					break;
				}
			}
			if (mid != 0) {
				this.label = label.subSequence(0, mid-1);
			} else {
				this.label = label;
			}
			textWidth = (int) titlePaint.measureText(this.label.toString());
		}
	}
	
	
	public IconRects getIconRects() {
		return iconRects;
	}


	public Bitmap getBitmap() {
		return bitmap;
	}


	public CharSequence getLabel() {
		return label;
	}

	public void setLabel(CharSequence label) {
		this.label = label;
	}


	/**
	 * @author Michael
	 * Date:2014-3-20下午5:19:53
	 *  @param b
	 */
	public void setAni(boolean b) {
		this.isAni = b;
	}


	/**
	 * @author Michael
	 * Date:2014-3-20下午5:21:39
	 *  @return
	 */
	public Paint getAlphaPaint() {
		return alphaPaint;
	}


	public int getTextBackgroundAlpha() {
		return textBackgroundAlpha;
	}


	public void setTextBackgroundAlpha(int textBackgroundAlpha) {
		this.textBackgroundAlpha = textBackgroundAlpha;
	}


	public int getTextWidth() {
		return textWidth;
	}
	
	/**
	 * 获取图标的大小
	 * @author Michael
	 * Date:2014-3-24上午8:38:18
	 *  @return
	 */
	public Rect getIconRect(LauncherIconViewConfig config){
		if(isDataReady){
			if(BaseConfig.isOnScene()){
				if(config.isSceneFillContent()){
					if(config.isSceneFillContentFitCenter()){
						return iconRects.fillFitCenterRectAndScale.rect;
					}else{
						return iconRects.fillRectAndScale.rect;
					}
				}
			}
			
			if(config.isLargeIconMode()){
				return iconRects.maxRectAndScale.rect;
			}else{
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return iconRects.mediumRectAndScale.rect;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return iconRects.minRectAndScale.rect;
				return iconRects.minRectAndScale.rect;
			}
		}
		return new Rect();
	}
	
	
	/**
	 * 获取蒙板的rect大小
	 * @author Michael
	 * Date:2014-3-24上午8:38:18
	 *  @return
	 */
	public Rect getMaskRect(LauncherIconViewConfig config) {
		if (isDataReady) {
			if (BaseConfig.isOnScene()) {
				if (config.isSceneFillContent()) {
					if (config.isSceneFillContentFitCenter()) {
						return iconRects.fillFitCenterRectAndScale.rect;
					} else {
						return iconRects.fillRectAndScale.rect;
					}
				}
			}
			
			if (config.isLargeIconMode()) {
				return iconRects.defaultThemeFrontmaxRectAndScale.rect;
			} else {
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return iconRects.defaultThemeFrontmediumRectAndScale.rect;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return iconRects.defaultThemeFrontminRectAndScale.rect;
				return iconRects.defaultThemeFrontminRectAndScale.rect;
			}
		}
		return new Rect();
	}
	
	/**
	 * 获取缩放比例
	 * @author Michael
	 * Date:2014-3-25上午11:09:54
	 *  @param config
	 *  @return
	 */
	public float getScale(LauncherIconViewConfig config){
		if (BaseConfig.isOnScene()) {
			if (config.isSceneFillContent()) {
				if (config.isSceneFillContentFitCenter()) {
					return iconRects.fillFitCenterRectAndScale.scale;
				} else {
					return iconRects.fillRectAndScale.scale;
				}
			}
		}
		if(iconRects != null){
			if (config.isLargeIconMode()) {
				return iconRects.maxRectAndScale.scale;
			} else {
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return iconRects.mediumRectAndScale.scale;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return iconRects.minRectAndScale.scale;
				return iconRects.maxRectAndScale.scale;
			}
		}
		return 1f;
	}
	
	public int getDrawPadding(LauncherIconViewConfig config){
		if(config.isLargeIconMode()){
			return 0;
		}else{
			return iconResourceData.drawPadding;
		}
	}
}
