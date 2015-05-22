package com.bitants.launcherdev.launcher.view.icon.ui;

import android.content.Context;

import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;

/**
 * 桌面图标大小管理类
 *
 */
public class IconSizeManager {
	
	//SETTING_APP_ICON_SIZE_KEY
	/**
	 * 小图标
	 */
	public static final int SMALL_ICON_SIZE = 0;
	/**
	 * 中图标
	 */
	public static final int MEDUIM_ICON_SIZE = 1;
	/**
	 * 大图标
	 */
	public static final int LARGE_ICON_SIZE = 2;
	
	/**
	 * 自定义图标
	 */
	public static final int CUSTOM_ICON_SIZE = 3;
	
	/**
	 * 获取图标的值
	 *  @return
	 */
	public static int getIconSizeBySp(Context context){
		int type = BaseSettingsPreference.getInstance().getAppIconType();
		switch (type) {
		case CUSTOM_ICON_SIZE:
			return BaseSettingsPreference.getInstance().getAppIconSize();
		case SMALL_ICON_SIZE:	
			return context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		case MEDUIM_ICON_SIZE:
			return context.getResources().getDimensionPixelSize(R.dimen.app_icon_bigsize);
		case LARGE_ICON_SIZE://选择了大图标模式
			if(BaseConfig.isLargeIconMode()){//是大图标主题
				return context.getResources().getDimensionPixelSize(R.dimen.app_background_size);
			}else{//非大图标主题
				return context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
			}
		default:
			return BaseSettingsPreference.getInstance().getAppIconSize();
		}
	}
	
	/**
	 * 获取dock栏图标值
	 * @param context
	 * @return
	 */
	public static int getDockbarIconSizeBySp(Context context){
		int type = BaseSettingsPreference.getInstance().getAppIconType();
		switch (type) {
		case CUSTOM_ICON_SIZE:
			return BaseSettingsPreference.getInstance().getAppIconSize();
		case SMALL_ICON_SIZE:	
			return context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		case MEDUIM_ICON_SIZE:
			return context.getResources().getDimensionPixelSize(R.dimen.app_icon_bigsize);
		case LARGE_ICON_SIZE:
			return context.getResources().getDimensionPixelSize(R.dimen.app_icon_bigsize);
		default:
			return BaseSettingsPreference.getInstance().getAppIconSize();
		}
	}
	
	/**
	 * 设置IconSize
	 *  @param type
	 */
    public static void setIconSizeType(Context context, int type){
    	switch (type) {
		case SMALL_ICON_SIZE:
			BaseSettingsPreference.getInstance().setAppIconSize(
					context.getResources().getDimensionPixelSize(R.dimen.app_icon_size));
			break;
		case MEDUIM_ICON_SIZE:	
			BaseSettingsPreference.getInstance().setAppIconSize(
					context.getResources().getDimensionPixelSize(R.dimen.app_icon_bigsize));
			break;
		case LARGE_ICON_SIZE:
			BaseSettingsPreference.getInstance().setAppIconSize(
					context.getResources().getDimensionPixelSize(R.dimen.app_background_size));
			break;
		default:
			break;
		}
    	BaseSettingsPreference.getInstance().setAppIconType(type);
    }
}
