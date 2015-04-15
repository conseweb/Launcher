package com.nd.launcherdev.launcher.view.icon.ui;

import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconDataCache.IconRects;
import com.nd.launcherdev.launcher.view.icon.ui.LauncherIconDataCache.RectAndScale;

import android.content.Context;
import android.graphics.Rect;
import com.nd.launcherdev.launcher.config.BaseConfig;

public class DockbarCellData extends LauncherIconData {
	
	
	public DockbarCellData(Context mContext) {
		super(mContext);
	}
	
	public void updateData(int measureW, int measureH, boolean isShowText){
		if(isShowText){
			super.updateData(measureW, measureH);
			return;
		}
		
		iconRects = new LauncherIconDataCache.IconRects();
		textHeight = 0;
		int targetW = iconResourceData.minIconSize;
		int targetH = iconResourceData.minIconSize + iconResourceData.minMargin+
						iconResourceData.minMargin + textHeight;
		iconRects.minRectAndScale = LauncherIconDataCache.calcRectAndScale(measureW, measureH, targetW, targetH, iconResourceData.minIconSize);
		if(iconRects.defaultThemeFrontminRectAndScale == null){
			iconRects.defaultThemeFrontminRectAndScale = new LauncherIconDataCache.RectAndScale();
		}
		iconRects.defaultThemeFrontminRectAndScale.rect = LauncherIconDataCache.calcDefaultThemeFrontIconMaskRectAndScale(iconRects.minRectAndScale.rect);
		
		
		targetW = iconResourceData.mediumIconSize;
		targetH = iconResourceData.mediumIconSize + iconResourceData.minMargin+
						iconResourceData.minMargin + textHeight;
		iconRects.mediumRectAndScale = LauncherIconDataCache.calcRectAndScale(measureW, measureH, targetW, targetH, iconResourceData.mediumIconSize);
		if(iconRects.defaultThemeFrontmediumRectAndScale == null){
			iconRects.defaultThemeFrontmediumRectAndScale = new LauncherIconDataCache.RectAndScale();
		}
		iconRects.defaultThemeFrontmediumRectAndScale.rect = LauncherIconDataCache.calcDefaultThemeFrontIconMaskRectAndScale(iconRects.mediumRectAndScale.rect);
		
		targetW = iconResourceData.maxIconSize;
		targetH = iconResourceData.maxIconSize + iconResourceData.minMargin+
						iconResourceData.minMargin + textHeight;
		iconRects.maxRectAndScale = LauncherIconDataCache.calcRectAndScale(measureW, measureH, targetW, targetH, iconResourceData.maxIconSize);
		if(iconRects.defaultThemeFrontmaxRectAndScale == null){
		    	iconRects.defaultThemeFrontmaxRectAndScale = new LauncherIconDataCache.RectAndScale();
		    }
		iconRects.defaultThemeFrontmaxRectAndScale.rect = LauncherIconDataCache.calcDefaultThemeFrontIconMaskRectAndScale(iconRects.maxRectAndScale.rect);
		
		
		iconRects.fillRectAndScale = LauncherIconDataCache.fillRect(measureW, measureH, false);
		iconRects.fillFitCenterRectAndScale = LauncherIconDataCache.fillRect(measureW, measureH, true);
		viewWidth = measureW;
		isDataReady = true;
	}
	

	/**
	 * 获取图标的大小
	 * @author Michael
	 * Date:2014-3-24上午8:38:18
	 *  @return
	 */
	@Override
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
				return iconRects.mediumRectAndScale.rect;
			}else{
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return iconRects.mediumRectAndScale.rect;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return iconRects.minRectAndScale.rect;
				return iconRects.mediumRectAndScale.rect;
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
	@Override
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
				return iconRects.defaultThemeFrontmediumRectAndScale.rect;
			} else {
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return iconRects.defaultThemeFrontmediumRectAndScale.rect;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return iconRects.defaultThemeFrontminRectAndScale.rect;
				return iconRects.defaultThemeFrontmediumRectAndScale.rect;
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
	@Override
	public float getScale(LauncherIconViewConfig config){
		if(config == null || iconRects == null){
			return 1f;
		}
		try{
			if (BaseConfig.isOnScene()) {
				if (config.isSceneFillContent()) {
					if (config.isSceneFillContentFitCenter()) {
						return iconRects.fillFitCenterRectAndScale.scale;
					} else {
						return iconRects.fillRectAndScale.scale;
					}
				}
			}
			if (config.isLargeIconMode()) {
				return iconRects.mediumRectAndScale.scale;
			} else {
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return iconRects.mediumRectAndScale.scale;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return iconRects.minRectAndScale.scale;
				return iconRects.minRectAndScale.scale;
			}
		}catch(Exception e){
			e.printStackTrace();
			return 1f;
		}
		
	}
	
	public int getDrawPadding(LauncherIconViewConfig config){
		return 0;
	}

}
