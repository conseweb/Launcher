package com.bitants.common.theme.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.bitants.common.theme.data.ThemeGlobal;

/**
 * <br>
 * Description: 主题配置类，基于SharedPreferences <br>
 */
public class ThemeSharePref {
	
	private static ThemeSharePref themeSharePref;

	/**
	 * 主题配置文件名
	 */
	public static final String ThemeSharePrefName = "panda_theme_config";

	/**
	 * Key -- 当前主题Id
	 */
	public static final String KEY_CURRENT_THEME_ID = "current_theme_id";

	/**
	 * 是否提示导入本地已存在的APT主题
	 */
	private static final String SHOULD_REMIND_EXIST_LOCAL_THEME = "should_remind_exist_local_theme";
	
	/**
     * 是否推荐下载91动态壁纸(服务端开关控制)
     */
    private static final String KEY_RECOMMEND_91LIVE_WALLPAPER = "key_recommend_91LIVE_WALLPAPER";
    
    /**
     * 是否在桌面弹窗通知推荐下载91动态壁纸
     */
    private static final String KEY_SHOW_NOTIFI_RECOMMEND_91LIVE_WALLPAPER = "key_show_notifi_recommend_91LIVE_WALLPAPER";

	private SharedPreferences spThemeSharePref;
	
	private ThemeSharePref(Context context) {
		spThemeSharePref = context.getSharedPreferences(ThemeSharePrefName, Context.MODE_PRIVATE | 4);
	}
	
	public static ThemeSharePref getInstance(Context context) {
//		if(null == themeSharePref) {
			themeSharePref = new ThemeSharePref(context);
//		}
		return themeSharePref;
	}
	
	public SharedPreferences getSP() {
		return spThemeSharePref;
	}
	
	/**
	 * <br>
	 * Description: 保存当前主题Id <br>
	 *
	 * @param themeId
	 */
	public void setCurrentThemeId(String themeId) {
		if (themeId == null) {
			return;
		}
		spThemeSharePref.edit().putString(KEY_CURRENT_THEME_ID, themeId).commit();
	}

	/**
	 * <br>
	 * Description: 获取当前主题Id <br>
	 *
	 * @param themeId
	 * @return
	 */
	public String getCurrentThemeId() {
		return spThemeSharePref.getString(KEY_CURRENT_THEME_ID, ThemeGlobal.DEFAULT_THEME_ID);
	}

	/**
	 * 是否默认主题 <br>
	 */
	public boolean isDefaultTheme() {
		String currentThemeId = spThemeSharePref.getString(KEY_CURRENT_THEME_ID, ThemeGlobal.DEFAULT_THEME_ID);
		return ThemeGlobal.DEFAULT_THEME_ID.equals(currentThemeId);
	}

	/**
	 * <br>
	 * Description: 是否提示导入本地已存在的APT主题 <br>
	 *
	 * @return
	 */
	public boolean shouldRemindExistLocalTheme() {
		return spThemeSharePref.getBoolean(SHOULD_REMIND_EXIST_LOCAL_THEME, true);
	}

	/**
	 * <br>
	 * Description: 设置是否提示导入本地已存在的APT主题 <br>
	 *
	 * @param enable
	 */
	public void setShouldRemindExistLocalTheme(boolean enable) {
		spThemeSharePref.edit().putBoolean(SHOULD_REMIND_EXIST_LOCAL_THEME, enable).commit();
	}
	
	/**
     * <br>Description: 是否推荐下载91动态壁纸(服务端开关控制)
     * @return
     */
    public boolean isRecommendLiveWallpaper() {
        return spThemeSharePref.getBoolean(KEY_RECOMMEND_91LIVE_WALLPAPER, false);
    }
    
    /**
     * <br>Description: 设置是否推荐下载91动态壁纸(服务端开关控制)
     * */
    public  void setIsRecommendLiveWallpaper(boolean enable) {
    	spThemeSharePref.edit().putBoolean(KEY_RECOMMEND_91LIVE_WALLPAPER, enable).commit();
    }
    
    /**
     * <br>Description: 是否显示通知消息推荐下载91动态壁纸
     * @return
     */
    public boolean isShowNotifiRecommendLiveWallpaper() {
        return spThemeSharePref.getBoolean(KEY_SHOW_NOTIFI_RECOMMEND_91LIVE_WALLPAPER, false);
    }
    
    /**
     * <br>Description: 设置是否显示通知消息推荐下载91动态壁纸
     * */
    public  void setIsShowNotifiRecommendLiveWallpaper(boolean enable) {
    	spThemeSharePref.edit().putBoolean(KEY_SHOW_NOTIFI_RECOMMEND_91LIVE_WALLPAPER, enable).commit();
    }

}
