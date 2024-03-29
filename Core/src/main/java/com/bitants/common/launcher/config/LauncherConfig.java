package com.bitants.common.launcher.config;

import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.theme.data.ThemeGlobal;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.broadcast.AntBroadcastReceiver;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.screens.dockbar.DockbarCellLayoutConfig;
import com.bitants.common.theme.data.BaseThemeData;
import com.bitants.common.R;
import com.bitants.common.launcher.model.BaseLauncherSettings;
import com.bitants.common.launcher.model.load.LauncherLoaderHelper;
import com.bitants.common.launcher.screens.ScreenViewGroup;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class LauncherConfig {
	private static final String TAG = "LauncherConfig";
	private static boolean initConfig = false;//是否已经初始化配置
	
	private static boolean initCellConfig = false;//是否已初始化单元格信息
	
	private static LauncherLoaderHelper mLauncherHelper = null;
	
	public static void setLauncherHelper(LauncherLoaderHelper configHelper) {
		mLauncherHelper = configHelper;
	}

	public static LauncherLoaderHelper getLauncherHelper(){
		return mLauncherHelper;
	}
	
	public static boolean isInitConfig() {
		return initConfig;
	}
	
	public static void init(Application mContext, LauncherLoaderHelper launcherLoaderHelper){
		init(mContext);
		BaseConfig.setApplicationContext(mContext.getApplicationContext());
		setLauncherHelper(launcherLoaderHelper);
	}
	
	/**
	 * 读取config.xml基础信息
	 * @param mContext
	 */
	public static void init(Context mContext){
		try{
			if(initConfig)
				return;
			
			if(mContext == null || mContext.getResources() == null)
				return;
			
			
			initConfig = true;
			
			if(mContext.getResources().getBoolean(R.integer.is_91Launcher)){
				return;
			}
			
			//Log.e("LauncherConfig", "InitRes");
			/**
			 * 下列变量从config.xml读取，不可直接赋给其它静态变量使用！
			 */
			BaseLauncherSettings.Favorites.AUTHORITY = mContext.getResources().getString(R.string.pkg_content_uri_authority);
			BaseLauncherSettings.Favorites.AUTHORITY_SUB = mContext.getResources().getString(R.string.pkg_content_uri_authority_sub);
			
			BaseConfig.initDir(mContext.getResources().getString(R.string.pkg_base_dir));
			
			ScreenViewGroup.MAX_SCREEN = mContext.getResources().getInteger(R.integer.workspace_max_screens);
			ScreenViewGroup.DEFAULT_SCREEN_COUNT = mContext.getResources().getInteger(R.integer.workspace_default_screen_count);
			ScreenViewGroup.DEFAULT_SCREEN = mContext.getResources().getInteger(R.integer.workspace_default_screen);
					
			BaseMagicDockbar.DEFAULT_SCREEN_COUNT = mContext.getResources().getInteger(R.integer.dockbar_max_screens);
			BaseMagicDockbar.DEFAULT_SCREEN = mContext.getResources().getInteger(R.integer.dockbar_default_main_screen);
			BaseMagicDockbar.DEFAULT_SCREEN_ITEM_COUNT = mContext.getResources().getInteger(R.integer.dockbar_cell_counts);
			
			BaseLauncher.hasDrawer = mContext.getResources().getBoolean(R.integer.has_drawer);//是否有匣子
			
			BaseThemeData.THEME_COMP_PKG = mContext.getResources().getString(R.string.theme_comp_pkg);
			BaseThemeData.THEME_APP_SELECT_ACTIVITY = mContext.getResources().getString(R.string.theme_app_select_activity);
			BaseThemeData.init();
			
			
			ThemeGlobal.INTENT_CURRENT_THEME_INFO = mContext.getResources().getString(R.string.INTENT_CURRENT_THEME_INFO);
			ThemeGlobal.ACTION_ASK_THEME = mContext.getResources().getString(R.string.ACTION_ASK_THEME);
			ThemeGlobal.INTENT_SPACE_INSTALL_THEME = mContext.getResources().getString(R.string.INTENT_SPACE_INSTALL_THEME);
			ThemeGlobal.LAUNCHER_UI_REFRESH_ACTION = mContext.getResources().getString(R.string.LAUNCHER_UI_REFRESH_ACTION);
			ThemeGlobal.INTENT_THEME_LIST_REFRESH = mContext.getResources().getString(R.string.INTENT_THEME_LIST_REFRESH);
			
			AntBroadcastReceiver.ACTION_CHANGE_FOLDER_STYLE = mContext.getResources().getString(R.string.ACTION_CHANGE_FOLDER_STYLE);
			AntBroadcastReceiver.MOVE_HOME_ACTION = mContext.getResources().getString(R.string.MOVE_HOME_ACTION);
			AntBroadcastReceiver.APPLY_OLD_THEME_ACTION = mContext.getResources().getString(R.string.APPLY_OLD_THEME_ACTION);
			AntBroadcastReceiver.SOFT_UPGRADE_ACTION = mContext.getResources().getString(R.string.SOFT_UPGRADE_ACTION);
			AntBroadcastReceiver.ACTION_REFRESH_SEARCH_WIDGET_UI = mContext.getResources().getString(R.string.ACTION_REFRESH_SEARCH_WIDGET_UI);
			AntBroadcastReceiver.ACTION_INTERNAL_INSTALL_SHORTCUT = mContext.getResources().getString(R.string.ACTION_INTERNAL_INSTALL_SHORTCUT);
			AntBroadcastReceiver.ACTION_REFRESH_DYNAMIC_ICON = mContext.getResources().getString(R.string.ACTION_REFRESH_DYNAMIC_ICON);
			AntBroadcastReceiver.ACTION_STOP_FLASH_SWAP_WALLPAPER = mContext.getResources().getString(R.string.ACTION_STOP_FLASH_SWAP_WALLPAPER);
			AntBroadcastReceiver.ACTION_DAILY_HOT_NEWS_CHANGE = mContext.getResources().getString(R.string.ACTION_DAILY_HOT_NEWS_CHANGE);
			AntBroadcastReceiver.REFRESH_ICON_ACTION = mContext.getResources().getString(R.string.REFRESH_ICON_ACTION);
			AntBroadcastReceiver.APP_HINT_FILTER = mContext.getResources().getString(R.string.APP_HINT_FILTER);
			AntBroadcastReceiver.APP_STORE_HINT_FILTER = mContext.getResources().getString(R.string.APP_STORE_HINT_FILTER);
			
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
		
	}
	
	/**
	 * 初始化打点统计
	 * @param mContext
	 */
	public static void initHiAnalytics(Context mContext){
		if(getLauncherHelper() != null){			
			getLauncherHelper().initHiAnalytics(mContext);
		}
	}
	
	/**
	 * 初始化Workspace和dock的单元格布局信息
	 * @param mContext
	 */
	public static void initCellConfig(Context mContext, boolean isNewInstall){
		if(initCellConfig || mLauncherHelper == null)
			return;
		initCellConfig = true;
		
		try {
			Log.i(TAG, "start init()");
			if(BaseConfig.isOnScene()){//情景桌面初始化
				mLauncherHelper.initForScene(mContext);
			}else{//主桌面初始化
				if(isNewInstall){//新安装用户
					mLauncherHelper.setShowDockbarTitleForNewInstall(mContext);
					setNewInstallCellLayoutXYCount(mContext);
				}else{
					setCellLayoutXYCount(mContext);
				}
				CellLayoutConfig.init(mContext, false);
				DockbarCellLayoutConfig.init(mContext, false);
			}
			
			
			Log.i(TAG, "end init()");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	/**
	 * 用于数据升级，初始化Workspace和dock的单元格布局信息
	 * @param mContext
	 */
	public static void initCellConfigForUpdate(Context mContext){
		setCellLayoutXYCount(mContext);
		CellLayoutConfig.init(mContext, true);
		DockbarCellLayoutConfig.init(mContext, true);
	}
	
	/**
	 * Description: 设置新安装时的默认行列数
	 */
	private static void setNewInstallCellLayoutXYCount(Context mContext){
		int[] countXY = getDefaultCellCountXY(mContext);
		int xyType = getCellCountXYType(countXY);
		BaseSettingsPreference.getInstance().setCountXY(xyType);
		BaseSettingsPreference.getInstance().setScreenCountXY(countXY);
	}

	/**
	 * 新安装用户获取默认行列数配置
	 * @param ctx
	 * @return
	 */
	public static int[] getDefaultCellCountXY(Context ctx) {
		int[] col_row = new int[2];
		col_row[0] = 4;
		col_row[1] = 4;
		if (ScreenUtil.isExLardgeScreen()) {
			col_row[0] = 4;
			col_row[1] = 5;
			// 大分辨率，匣子行列数为5x4
			BaseSettingsPreference.getInstance().setDrawerCountXY(1);
		}
		if(ScreenUtil.isSuperLargeScreenAndLowDensity() || ScreenUtil.isLargeScreenAndSuperLowDensity()){
			// 超大分辨率但屏幕密度低，桌面行列数为5x5
			col_row[0] = 5;
			col_row[1] = 5;
			BaseSettingsPreference.getInstance().setDrawerCountXY(2);
		}
		
		return col_row;
	}
	
	/**
	 * 获取桌面行列数类型
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getCellCountXYType(int[] col_row) {
		int type = 0;
		// 判断行列数类型，4x4为0,5x4为1,5x5为2,其余的为3
		if (col_row != null) {
			if (col_row[0] == 4 && col_row[1] == 4) {
				type = 0;
			} else if (col_row[0] == 4 && col_row[1] == 5) {
				type = 1;
			} else if (col_row[0] == 5 && col_row[1] == 5) {
				type = 2;
			} else {
				type = 3;
			}
		}
		return type;
	}
	/**
	 * Description: 设置桌面已设置的行列数
	 */
	private static void setCellLayoutXYCount(Context mContext){
		int type = BaseSettingsPreference.getInstance().getCountXY();
		int xCount, yCount;
		switch (type) {
		case 0:
			xCount = 4;
			yCount = 4;
			break;
		case 1:
			xCount = 4;
			yCount = 5;
			break;
		case 2:
			xCount = 5;
			yCount = 5;
			break;
		case 3:
			int[] countXY = BaseSettingsPreference.getInstance().getScreenCountXY();
			xCount = countXY[0];
			yCount = countXY[1];
			break;
		default:
			xCount = 4;
			yCount = 4;
			break;
		}
		BaseSettingsPreference.getInstance().setScreenCountXY(new int[]{xCount, yCount});
	}

}
