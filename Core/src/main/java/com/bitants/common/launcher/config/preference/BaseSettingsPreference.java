package com.bitants.common.launcher.config.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.framework.effect.EffectsType;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.launcher.broadcast.HiBroadcastReceiver;
import com.bitants.common.launcher.view.icon.ui.IconSizeManager;
import com.bitants.common.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.common.R;

/**
 * 桌面设置Preference持久类
 */
public class BaseSettingsPreference {
	private static BaseSettingsPreference baseSettings;
	private static SharedPreferences baseSP;

	// **********手势菜单***********/
	public static final int GESTURE_MENU_NONE = 0;
	public static final int GESTURE_MENU_APPLICATION = 1;
	public static final int GESTURE_MENU_LAUNCHER_SHORT_CUT = 2;
	public static final int GESTURE_MENU_SYSTEM_SHORT_CUT = 3;
	public static final int GESTURE_MENU_DEFAULT = 4;
	// **********手势菜单***********/

	// **********桌面功能菜单*************/
	public static final int LAUNCHER_FUNCTION_MENU_SHOW_SEARCH = 0;// 显示搜索
	public static final int LAUNCHER_FUNCTION_MENU_SHOW_DEFAULT_SCREEN = 1;// 显示主屏幕
	public static final int LAUNCHER_FUNCTION_MENU_SHOW_DEFAULT_AND_PREVIEW = 2;// 先主屏幕预览
	public static final int LAUNCHER_FUNCTION_MENU_SHOW_MY_THEME = 3;// 我的主题
	public static final int LAUNCHER_FUNCTION_MENU_SHOW_MENU = 4;// 桌面菜单
	public static final int LAUNCHER_FUNCTION_MENU_SHOW_SHORTCUT_MENU = 5;// 显示快捷菜单
	// **********桌面功能菜单*************/
	public static final int LAUNCHER_FUNCTION_SCENE_DESKTOP = 4;
	
	/**
	 * 含有GT 串的手机桌面默认特效设置为 盒子内
	 */
	public final static String GT = "GT";
	/**
	 * 含有HTC 串的手机桌面默认特效设置为 盒子外
	 */
	public final static String HTC = "HTC";
		
	//=====================以下需要静态变量来控制，保证继承的子类能读到正确值!!!=====================//
	/**
	 * 是否显示第0屏
	 */
	private static boolean isShowZeroView = true;
	/**
	 * 桌面是否循环滚屏
	 */
	private static boolean isRollingCycle = false;
	
	/**
	 * 桌面行列数
	 */
	private static int[] screenCountXY;

	/**
	 * 匣子行列数
	 */
	private static int drawerCountXY;
	
	/**
	 * 是否显示通知栏
	 */
	private static boolean isNotificationBarVisible;

	/**
	 * 桌面是否显示图标标题背景
	 */
	protected static boolean isShowTitleBackaground;

	/**
	 * 蒙板是否开启
	 */
	private static boolean isIconMaskEnabled;

	/**
	 * 当前是否大图标主题
	 */
	private static boolean isLargeIconTheme;

	/**
	 * 应用程序名称颜色
	 */
	private static int appNameColor;

	/**
	 * 应用程序名称大小
	 */
	private static int appNameSize;
	
	/**
	 * 图标大小选择
	 */
	private static int iconSizeType;

	/**
	 * 大图标模式是否开启
	 */
	protected static boolean isLargeIconEnabled;

	/**
	 * 匣子 滑动特效
	 */
	public static int drawerScollEffects;
	
	/**
	 * 文件夹风格
	 */
	private static int folderStyle;
	
	/**
	 * 桌面 滑动特效
	 */
	private static int screenScollEffects;
	/**
	 * 使用当前主题的壁纸来自绘壁纸
	 */
	private static boolean drawWallpaperFromTheme;
	
	/**
     * 支持动态壁纸
     * */
    private static final String KEY_SUPPORT_LIVEWP = "key_support_livewp";
    private static boolean isSupportedLiveWallpaper = false;
    
	//=====================以上需要静态变量来控制，保证继承的子类能读到正确值!!!=====================//
	
	private BaseSettingsPreference() {
		this(BaseConfig.getApplicationContext());
	}
	
	public static synchronized BaseSettingsPreference getInstance() {
		if (baseSettings == null) {
			baseSettings = new BaseSettingsPreference();
		}
		return baseSettings;
	}
	
	protected BaseSettingsPreference(Context mContext) {
		baseSP = mContext.getSharedPreferences(SettingsConstants.SETTINGS_NAME, Context.MODE_PRIVATE);
		
		isShowZeroView = baseSP.getBoolean(SettingsConstants.SETTING_SCREEN_NAVIGATION_VIEW, true);
		isRollingCycle = baseSP.getBoolean(SettingsConstants.SETTING_SCREEN_ROLLING_CYCLE, false);
		
		isNotificationBarVisible = baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_STATUS_BAR_SWITCH, true);
		screenCountXY = new int[2];
		screenCountXY[0] = Integer.parseInt(baseSP.getString(SettingsConstants.SCREEN_COUNT_X, "4"));
		screenCountXY[1] = Integer.parseInt(baseSP.getString(SettingsConstants.SCREEN_COUNT_Y, "4"));
		isIconMaskEnabled = baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_ICON_MASK_SWITCH, true);
		if (ScreenUtil.isLargeScreen()) {
			isShowTitleBackaground = baseSP.getBoolean(SettingsConstants.SETTING_SCREEN_ICON_TITLE_BACKGROUND, false);
		} else {
			isShowTitleBackaground = baseSP.getBoolean(SettingsConstants.SETTING_SCREEN_ICON_TITLE_BACKGROUND, true);
		}
		
		drawerCountXY = Integer.parseInt(baseSP.getString(SettingsConstants.SETTING_DRAWER_COUNTXY, String.valueOf(0)));
		
		isLargeIconTheme = baseSP.getBoolean(SettingsConstants.IS_THEME_LARGE_ICON, false);
		appNameColor = baseSP.getInt(SettingsConstants.SETTING_FONT_APP_COLOR, Color.WHITE);
		appNameSize = baseSP.getInt(SettingsConstants.SETTING_FONT_APP_NAME_SIZE, 
				mContext.getResources().getDimensionPixelSize(R.dimen.text_size));
		
		if(ScreenUtil.isSuperLargeScreenAndLowDensity()){//大屏低分辨率默认用中等大小的图标
			isLargeIconEnabled = baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_LARGE_ICON_SWITCH, false);
			if(isLargeIconEnabled){//升级的情况且原来是选择大图标
				iconSizeType = baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, IconSizeManager.LARGE_ICON_SIZE);
			}else{
				iconSizeType = baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, IconSizeManager.MEDUIM_ICON_SIZE);
			}
		}else if (ScreenUtil.isExLardgeScreen()) {
			isLargeIconEnabled = baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_LARGE_ICON_SWITCH, true);
			if(isLargeIconEnabled){
				iconSizeType = baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, IconSizeManager.LARGE_ICON_SIZE);
			}else{//升级的情况且原来是选择小图标
				iconSizeType = baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, IconSizeManager.SMALL_ICON_SIZE);
			}
		} else {
			isLargeIconEnabled = baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_LARGE_ICON_SWITCH, false);
			if(isLargeIconEnabled){//升级的情况且原来是选择大图标
				iconSizeType = baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, IconSizeManager.LARGE_ICON_SIZE);
			}else{
				iconSizeType = baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, IconSizeManager.SMALL_ICON_SIZE);
			}
		}
		
		folderStyle = Integer.parseInt(baseSP.getString(SettingsConstants.SETTING_PERSONAL_FOLDER_STYLE, String.valueOf(FolderIconTextView.FOLDER_STYLE_FULL_SCREEN)));
		
		/**
		 * 4.0 以上手机默认开启层叠
		 */
		if (Build.VERSION.SDK_INT < 14) {
			screenScollEffects = Integer.parseInt(baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.DEFAULT)));
		} else {
			if (TelephoneUtil.getMachineName().contains(GT)) {
				screenScollEffects = Integer.parseInt(baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.CUBE_INSIDE)));
//			} else if (TelephoneUtil.getMachineName().contains(HTC)) {
//				screenScollEffects = Integer.parseInt(sp.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.CUBE_OUTSIDE)));
			} else {
				screenScollEffects = Integer.parseInt(baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.DEFAULT)));
			}
		}
		
		drawWallpaperFromTheme = baseSP.getBoolean(SettingsConstants.SETTING_DRAW_WALLPAPER_FROM_THEME, false);
		isSupportedLiveWallpaper = baseSP.getBoolean(KEY_SUPPORT_LIVEWP, false);
	}

	public SharedPreferences getBaseSP() {
		return baseSP;
	}

	public boolean isRollingCycle() {
		return isRollingCycle;
	}

	public void setRollingCycle(boolean isCycle) {
		isRollingCycle = isCycle;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_SCREEN_ROLLING_CYCLE, isCycle).commit();
	}

	/**
	 * <br>
	 * Description: 应用列表选项是否锁定 <br>
	 *
	 * @return
	 */
	public boolean isDrawerTabsLock() {
		return baseSP.getBoolean(SettingsConstants.SETTING_DRAWER_TABS_LOCK, false);
	}

	/**
	 * <br>
	 * Description: 设置应用列表选项是否锁定 <br>
	 *
	 * @param isEnabled
	 */
	public void setDrawerTabsLock(boolean isEnabled) {
		baseSP.edit().putBoolean(SettingsConstants.SETTING_DRAWER_TABS_LOCK, isEnabled);
	}
	
	/**
	 * 返回匣子背景是否透明
	 */
	public boolean getDrawerBgTransparent() {
		return baseSP.getBoolean(SettingsConstants.SETTINGS_DRAWER_BG_TRANSPARENT, false);
	}

	/**
	 * 桌面行列数 <br>
	 */
	public int getCountXY() {
		String value = baseSP.getString(SettingsConstants.SETTING_SCREEN_COUNTXY, "0");
		return Integer.parseInt(value);
	}

	/**
	 * 设置桌面行列数
	 * 
	 * @param type
	 */
	public void setCountXY(int type) {
		baseSP.edit().putString(SettingsConstants.SETTING_SCREEN_COUNTXY, String.valueOf(type)).commit();
	}

	/**
	 * 获取匣子默认特效
	 */
	public String getDrawerDefauleEffect() {
		if (Build.VERSION.SDK_INT < 14) {
			return baseSP.getString(SettingsConstants.SETTING_DRAWER_SLIDE_EFFECT, String.valueOf(EffectsType.DEFAULT));
		} else {
			return baseSP.getString(SettingsConstants.SETTING_DRAWER_SLIDE_EFFECT, String.valueOf(EffectsType.CASCADE));
		}
	}

	/**
	 * 获取桌面默认特效
	 * 
	 */
	public String getScreenDefauleEffect() {
		if (Build.VERSION.SDK_INT < 14) {
			return baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.DEFAULT));
		} else {
			if(TelephoneUtil.getMachineName().contains(GT)){
				return baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.CUBE_INSIDE));
			}
			else if(TelephoneUtil.getMachineName().contains(HTC)){
				return baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.CUBE_OUTSIDE));
			}
			else{
				return baseSP.getString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(EffectsType.DEFAULT));
			}
		}
	}

	/**
	 * 获取桌面行列数
	 * 
	 * @return
	 */
	public int[] getScreenCountXY() {
		return screenCountXY;
	}

	/**
	 * 设置桌面行列数
	 * 
	 * @param countXY
	 */
	public void setScreenCountXY(int[] countXY) {
		screenCountXY = countXY;
		SharedPreferences.Editor editor = baseSP.edit();
		editor.putString(SettingsConstants.SCREEN_COUNT_X, String.valueOf(countXY[0]));
		editor.putString(SettingsConstants.SCREEN_COUNT_Y, String.valueOf(countXY[1]));
		editor.commit();
	}
	
	/**
	 * 若未设置，返回0
	 */
	public int getScreenCountX(){
		return Integer.parseInt(baseSP.getString(SettingsConstants.SCREEN_COUNT_X, "0"));
	}
	
	/**
	 * 若未设置，返回0
	 */
	public int getScreenCountY(){
		return Integer.parseInt(baseSP.getString(SettingsConstants.SCREEN_COUNT_Y, "0"));
	}

	public boolean isShowNavigationView() {
		if(BaseConfig.isOnScene())
			return false;
		return isShowZeroView;
//		return sp.getBoolean(SettingsConstants.SETTING_SCREEN_NAVIGATION_VIEW, !ChannelUtil.isChannelPackage());
//		return sp.getBoolean(SettingsConstants.SETTING_SCREEN_NAVIGATION_VIEW, true);
	}

	public void setShowNavigationView(boolean isShow) {
		isShowZeroView = isShow;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_SCREEN_NAVIGATION_VIEW, isShow).commit();
	}

	public boolean isIconMaskEnabled() {
		return isIconMaskEnabled;
	}

	public void setIconMaskEnabled(Context context, boolean isEnabled) {
		isIconMaskEnabled = isEnabled;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_PERSONAL_ICON_MASK_SWITCH, isEnabled).commit();
		Intent intent = new Intent(HiBroadcastReceiver.REFRESH_ICON_ACTION);
		context.sendBroadcast(intent);
	}

	public boolean isLargeIconEnabled() {
		return isLargeIconEnabled;
	}

	public void setLargeIconEnabled(Context context, boolean isEnabled) {
		isLargeIconEnabled = isEnabled;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_PERSONAL_LARGE_ICON_SWITCH, isEnabled).commit();
		Intent intent = new Intent(HiBroadcastReceiver.REFRESH_ICON_ACTION);
		context.sendBroadcast(intent);
	}

	public boolean isPandaLockEnabled() {
		return baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_PANDALOCK_SWITCH, true);
	}

	public boolean isDockVisible() {
		return baseSP.getBoolean(SettingsConstants.SETTING_PERSONAL_DOCK_SWITCH, true);
	}

	public void setDockVisible(boolean isDockVisible) {
		baseSP.edit().putBoolean(SettingsConstants.SETTING_PERSONAL_DOCK_SWITCH, isDockVisible).commit();
	}

	public boolean isNotificationBarVisible() {
		if(BaseConfig.isOnScene()){
			return true;
		}
		return isNotificationBarVisible;
	}

	public void setNotificationBarVisible(boolean isVisible) {
		isNotificationBarVisible = isVisible;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_PERSONAL_STATUS_BAR_SWITCH, isVisible).commit();
	}

	/**
	 * 通讯统计未接电话
	 */
	public boolean isShowCommunicatePhone() {
		return baseSP.getBoolean(SettingsConstants.SETTING_COMMUNICATE_PHONE, true);
	}

	/**
	 * 通讯统计未读短信
	 */
	public boolean isShowCommunicateMms() {
		return baseSP.getBoolean(SettingsConstants.SETTING_COMMUNICATE_MMS, true);
	}

	/**
	 * 图标标题背景
	 */
	public boolean isShowTitleBackaground() {
		return isShowTitleBackaground;
	}

	public void setShowTitleBackground(Context context, boolean isShowed) {
		isShowTitleBackaground = isShowed;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_SCREEN_ICON_TITLE_BACKGROUND, isShowed).commit();
		Intent intent = new Intent(HiBroadcastReceiver.REFRESH_ICON_ACTION);
		context.sendBroadcast(intent);
	}
	

	/**
	 * <br>
	 * Description: 获取当前主题是否大图标模式 <br>
	 *
	 * @return
	 */
	public boolean isLargeIconTheme() {
		return isLargeIconTheme;
	}

	/**
	 * <br>
	 * Description: 设置当前主题是否大图标模式 <br>
	 *
	 * @param isLarge
	 */
	public void setLargeIconTheme(boolean isLarge) {
		isLargeIconTheme = isLarge;
		baseSP.edit().putBoolean(SettingsConstants.IS_THEME_LARGE_ICON, isLarge).commit();
	}

	/**
	 * <br>
	 * Description: 获取应用程序名称颜色 <br>
	 *
	 * @return
	 */
	public int getAppNameColor() {
		if(BaseConfig.isOnScene()){// 设置应用程序名称颜色,情景桌面暂不支持
			return Color.WHITE;
		}
		return appNameColor;
	}

	/**
	 * <br>
	 * Description: 设置应用程序名称颜色 <br>
	 *
	 * @param nameColor
	 */
	public void setAppNameColor(int nameColor) {
		appNameColor = nameColor;
		baseSP.edit().putInt(SettingsConstants.SETTING_FONT_APP_COLOR, nameColor).commit();
	}

	/**
	 * <br>
	 * Description: 获取应用程序名称大小 <br>
	 *
	 * @return
	 */
	public int getAppNameSize() {
		return appNameSize;
	}

	/**
	 * <br>
	 * Description: 设置应用程序名称大小 <br>
	 *
	 * @param nameSize
	 */
	public void setAppNameSize(int nameSize) {
		appNameSize = nameSize;
		baseSP.edit().putInt(SettingsConstants.SETTING_FONT_APP_NAME_SIZE, nameSize).commit();
	}

	/**
	 * 传入字体路径, 设置桌面使用的字体风格
	 * 
	 * @param fontStylePath
	 *            传入字体路径
	 */
	public void setFontStyle(String fontStylePath) {
		baseSP.edit().putString(SettingsConstants.SETTING_FONT_STYLE, fontStylePath).commit();
	}

	/**
	 * 获取桌面当前所使用的字体风格
	 */
	public String getFontStyle() {
		return baseSP.getString(SettingsConstants.SETTING_FONT_STYLE, "");
	}
	
	/**
	 * 传入字体路径, 设置安卓系统使用的字体风格
	 * 
	 * @param fontStylePath
	 *            传入字体路径
	 */
	public void setSystemFontStyle(String fontStylePath) {
		baseSP.edit().putString(SettingsConstants.SETTING_SYSTEM_FONT_STYLE, fontStylePath).commit();
	}
	
	/**
	 * 获取安卓系统当前所使用的字体风格
	 */
	public String getSystemFontStyle() {
		return baseSP.getString(SettingsConstants.SETTING_SYSTEM_FONT_STYLE, "");
	}

	/**
	 * 是否显示dock栏上app的title
	 */
	public boolean isShowDockbarText() {
		return baseSP.getBoolean(SettingsConstants.SETTINGS_DOCKBAR_TEXT_SHOW, true);
	}

	public void setShowDockbarText(boolean show) {
		baseSP.edit().putBoolean(SettingsConstants.SETTINGS_DOCKBAR_TEXT_SHOW, show).commit();
	}
	
	public void setDrawerScrollEffects(int effects) {
		drawerScollEffects = effects;
		baseSP.edit().putString(SettingsConstants.SETTING_DRAWER_SLIDE_EFFECT, String.valueOf(effects)).commit();
	}

	public int getDrawerScrollEffects() {
		return drawerScollEffects;
	}
	
	/**
	 * 获取应用图标名称
	 *  @return
	 */
	public int getAppIconSize(){
		return baseSP.getInt(SettingsConstants.SETTING_APP_ICON_SIZE_KEY, 48);
	}
	
	/**
	 * 设置应用图标名称
	 *  @return
	 */
	public void setAppIconSize(int iconSize){
		baseSP.edit().putInt(SettingsConstants.SETTING_APP_ICON_SIZE_KEY, iconSize).commit();
	}
	
	/**
	 * 获取应用图标大小类型
	 *  @return
	 */
	public int getAppIconType(){
		return iconSizeType;
	}
	
	/**
	 * 设置应用图标大小类型
	 *  @return
	 */
	public void setAppIconType(int type){
		if(type > -1 && type < IconSizeManager.CUSTOM_ICON_SIZE+1){
			iconSizeType = type;
			baseSP.edit().putInt(SettingsConstants.SETTING_APP_ICON_SIZE_TYPE, type).commit();
		}else{
			Log.e("SettingsPreference", "unknown type:"+type);
		}
	}
	
	
	public int getFolderStyle() {
		if(BaseConfig.isOnScene()){
			return FolderIconTextView.FOLDER_STYLE_IPHONE;
		}
		return folderStyle;
	}

	public void setFolderStyle(Context context, int style) {
		folderStyle = style;
		baseSP.edit().putString(SettingsConstants.SETTING_PERSONAL_FOLDER_STYLE, String.valueOf(style)).commit();
		Intent intent = new Intent(HiBroadcastReceiver.ACTION_CHANGE_FOLDER_STYLE);
		context.sendBroadcast(intent);
	}
	
	public int getDrawerCountXY() {
		return drawerCountXY;
	}

	public void setDrawerCountXY(int countXY) {
		drawerCountXY = countXY;
		baseSP.edit().putString(SettingsConstants.SETTING_DRAWER_COUNTXY, String.valueOf(countXY)).commit();
	}
	
	public void setScreenScrollEffects(int effcet) {
		screenScollEffects = effcet;
		baseSP.edit().putString(SettingsConstants.SETTING_SCREEN_EFFECTS, String.valueOf(effcet)).commit();
		EffectsType.setCurrentEffect(screenScollEffects);
	}

	public int getScreenScrollEffects() {
		return screenScollEffects;
	}

	
	/**
	 * 是否使用当前主题的壁纸来自绘壁纸
	 * @return
	 */
	public boolean isDrawWallpaperFromTheme() {
		return drawWallpaperFromTheme;
	}

	/**
	 * 设置是否使用当前主题的壁纸来自绘壁纸
	 * @param isDraw
	 */
	public void setDrawWallpaperFromTheme(boolean isDraw) {
		drawWallpaperFromTheme = isDraw;
		baseSP.edit().putBoolean(SettingsConstants.SETTING_DRAW_WALLPAPER_FROM_THEME, isDraw).commit();
	}
	
	/**
	 * 设置使用异步加载桌面数据
	 */
	public void setAsyncLoadLauncherData(boolean value){
		baseSP.edit().putBoolean(SettingsConstants.SETTING_ASYNC_LOAD_LAUNCHER, value).commit();
	}
	
	/**
	 * 是否使用异步加载桌面数据，默认是True
	 */
	public boolean isAsyncLoadLauncherData(){
		return baseSP.getBoolean(SettingsConstants.SETTING_ASYNC_LOAD_LAUNCHER, true);
	}
	
	/**
	 * 是否支持动态壁纸
	 * @return
	 */
	public boolean isSupportLiveWP() {
		return isSupportedLiveWallpaper;
	}

	/**
	 * 设置是否支持动态壁纸
	 * @param isSupport
	 */
	public void setIsSupportLiveWP(boolean isSupport) {
		isSupportedLiveWallpaper = isSupport;
		baseSP.edit().putBoolean(KEY_SUPPORT_LIVEWP, isSupport).commit();
	}

}
