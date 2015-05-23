package com.bitants.common.launcher.broadcast;

import android.content.BroadcastReceiver;
/**
 * 集中管理广播，请将IntentFilter的Action类型定义与此
 */
public abstract class HiBroadcastReceiver extends BroadcastReceiver {
	/**
	 * 提示应用商店检测出的可升级软件数量
	 */
	public static String APP_STORE_HINT_FILTER = "com.wireless.assistant.mobile.market.business.getUpdateBean";
	
	/**
	 * 应用程序提示信息，未接电话，未读短信数量
	 */
	public static String APP_HINT_FILTER = "hilauncherdev.application.hint";
	
	/**
	 * 图标更新广播
	 */
	public static String REFRESH_ICON_ACTION = "com.nd.pandahome.internal.refresh.icon";
    
    /**
     * 刷新文件夹样式广播Action
     */
    public static String ACTION_CHANGE_FOLDER_STYLE = "nd.panda.action.internal.change.folder.style";
	
	/**
	 * 桌面搬家请求广播
	 */
	public static String MOVE_HOME_ACTION = "com.nd.pandahome.internal.move.home";
	
	/**
	 * 桌面应用旧主题请求广播
	 */
	public static String APPLY_OLD_THEME_ACTION = "com.nd.pandahome.internal.apply.oldtheme";
	
	/**
	 * 软件升级广播
	 */
	public static String SOFT_UPGRADE_ACTION = "com.nd.pandahome.internal.soft.upgrade";
	
	/**
	 * 刷新搜索小部件UI广播
	 */
	public static String ACTION_REFRESH_SEARCH_WIDGET_UI = "com.nd.pandahome.internal.refresh.search.widget";
	
	/**
	 * 桌面内部创建快捷方式至桌面的广播
	 */
	public static String ACTION_INTERNAL_INSTALL_SHORTCUT = "com.nd.pandahome.install_shortcut";
	
    /**
     * 刷新动态图标广播Action
     */
    public static String ACTION_REFRESH_DYNAMIC_ICON = "nd.panda.action.internal.refresh.dynamic.icon";
	
    /**
     * 一键换壁纸结束widget动画广播Action
     */
    public static String ACTION_STOP_FLASH_SWAP_WALLPAPER = "nd.panda.action.internal.stopflash.swap.wallpaper";
    
    /**
     * 每日新鲜事hot图标变化广播Action
     */
    public static String ACTION_DAILY_HOT_NEWS_CHANGE = "com.nd.pandahome.dailyhotnews.change";
}
