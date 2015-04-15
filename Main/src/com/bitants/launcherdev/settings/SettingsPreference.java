package com.bitants.launcherdev.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;
import com.bitants.launcherdev.theme.data.ThemeGlobal;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.SettingsConstants;

/**
 * 桌面设置Preference持久类
 * 
 * @author Anson
 */
public class SettingsPreference extends BaseSettingsPreference {
	
	private static SettingsPreference settings;
	private static SharedPreferences sp;
	
	private boolean isLauncherGestureUp;
    private boolean isLauncherGestureDown;
    private boolean isLauncherGestureDoubleFingerOut;
    private boolean isCardScreen;
    private boolean isNavigation;
    private boolean isDynamicWallpaper;
    private boolean isDynamicWeather;
    private boolean isFolderInnerRecommend;
    private int launcherGestureDoubleClickVal;
    private String particleEffectsThemeId;
    private int screenParticleEffects;
    
	public static synchronized SettingsPreference getInstance() {
		if(sp == null){
			sp = BaseSettingsPreference.getInstance().getBaseSP();
		}
		if (settings == null) {
			settings = new SettingsPreference();
		}
		return (SettingsPreference) settings;
	}
	
	public SharedPreferences getSP() {
		return sp;
	}

	private SettingsPreference() {
		this(BaseConfig.getApplicationContext());
	}
	
	protected SettingsPreference(Context mContext) {
		super(mContext);
		isLauncherGestureUp = sp.getBoolean(SettingsConstantsEx.SETTING_GESTURE_UP, true);
		isLauncherGestureDown = sp.getBoolean(SettingsConstantsEx.SETTING_GESTURE_DOWN, true);
		isLauncherGestureDoubleFingerOut = sp.getBoolean(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_FINGER_OUT, true);
		isCardScreen = sp.getBoolean(SettingsConstantsEx.SETTING_CARD_SCREEN, true);
		isNavigation = sp.getBoolean(SettingsConstantsEx.SETTING_NAVIGATION, true);
		isDynamicWallpaper = sp.getBoolean(SettingsConstantsEx.SETTING_DYNAMIC_WALLPAPER, true);
		isDynamicWeather = sp.getBoolean(SettingsConstantsEx.SETTING_DYNAMIC_WEATHER, true);
		isFolderInnerRecommend = sp.getBoolean(SettingsConstantsEx.SETTING_FOLDER_INNER_RECOMMEND, true);
		launcherGestureDoubleClickVal = sp.getInt(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_CLICK, 
				SettingsConstantsEx.Val.GESTURE_DOUBLE_CLICK_NONE);	
		particleEffectsThemeId = sp.getString(SettingsConstants.SETTING_PARTICLE_EFFECT_THEME_ID, ThemeGlobal.DEFAULT_THEME_ID);
		screenParticleEffects = Integer.parseInt(sp.getString(SettingsConstants.SETTING_PARTICLE_EFFECT, "0"));
	}
	
	/**
	 * 桌面手势上滑
	 */
	public boolean isLauncherGestureUp() {
		return isLauncherGestureUp;
	}
	public void setLauncherGestureUp(boolean state) {
		isLauncherGestureUp = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_GESTURE_UP,state).commit();
	}
	
	/**
	 * 桌面手势下滑
	 */
	public boolean isLauncherGestureDown() {
		return isLauncherGestureDown;
	}
	public void setLauncherGestureDown(boolean state) {
		isLauncherGestureDown = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_GESTURE_DOWN,state).commit();
	}
	
	/**
	 * 桌面手势双指外扩
	 */
	public boolean isLauncherGestureDoubleFingerOut() {
		return isLauncherGestureDoubleFingerOut;
	}
	public void setLauncherGestureDoubleFingerOut(boolean state) {
		isLauncherGestureDoubleFingerOut = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_FINGER_OUT,state).commit();
	}
	
	/**
	 * 卡片屏设置
	 */
	public boolean isCardScreen() {
		return isCardScreen;
	}
	public void setCardScreen(boolean state) {
		isCardScreen = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_CARD_SCREEN,state).commit();
	}
	
	/**
	 * 网址导航设置
	 */
	public boolean isNavigation() {
		return isNavigation;
	}
	public void setNavigation(boolean state) {
		isNavigation = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_NAVIGATION,state).commit();
	}
	
	/**
	 * 动态壁纸效果设置
	 */
	public boolean isDynamicWallpaper() {
		return isDynamicWallpaper;
	}
	public void setDynamicWallpaper(boolean state) {
		isDynamicWallpaper = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_DYNAMIC_WALLPAPER,state).commit();
	}
	
	/**
	 * 壁纸天气动画设置
	 */
	public boolean isDynamicWeather() {
		return isDynamicWeather;
	}
	public void setDynamicWeather(boolean state) {
		isDynamicWeather = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_DYNAMIC_WEATHER,state).commit();
	}
	
	/**
	 * 文件夹内应用推荐设置
	 */
	public boolean isFolderInnerRecommend() {
		return isFolderInnerRecommend;
	}
	public void setFolderInnerRecommend(boolean state) {
		isFolderInnerRecommend = state;
		sp.edit().putBoolean(SettingsConstantsEx.SETTING_FOLDER_INNER_RECOMMEND,state).commit();
	}
	
	/**
	 * 桌面手势双击
	 */
	public int getLauncherGestureDoubleClickVal() {
		return launcherGestureDoubleClickVal;
	}
	public void setLauncherGestureDoubleClickVal(int val) {
		launcherGestureDoubleClickVal = val;
		sp.edit().putInt(SettingsConstantsEx.SETTING_GESTURE_DOUBLE_CLICK, val).commit();
	}
	
	public String getParticleEffectsThemeId() {
		return particleEffectsThemeId;
	}
	public void setParticleEffectsThemeId(String particleEffectsThemeId) {
		this.particleEffectsThemeId = particleEffectsThemeId;
		sp.edit().putString(SettingsConstants.SETTING_PARTICLE_EFFECT_THEME_ID, particleEffectsThemeId).commit();
	}
	
	public int getParticleEffects() {
		return screenParticleEffects;
	}
	public void setParticleEffects(int screenParticleEffects) {
		this.screenParticleEffects = screenParticleEffects;
		sp.edit().putString(SettingsConstants.SETTING_PARTICLE_EFFECT, String.valueOf(screenParticleEffects)).commit();
	}
	
}
