package com.bitants.launcherdev.datamodel;

import android.os.Environment;
import com.bitants.common.launcher.config.BaseConfig;

public class CommonGlobal extends BaseConfig {
	/** OLD_DIR */
	public final static String OLD_BASE_DIR = Environment.getExternalStorageDirectory() + "/PandaHome2";
	
	/** BASE_DIR */
	public final static String BASE_91_DIR = Environment.getExternalStorageDirectory() + "/91 WireLess";
	
	/**
	 * 异常备份数据目录
	 */
	public final static String EXCEPTION_BACKUP_DIR = BaseConfig.getBaseDir() + "/ExceptionBackup/";
	
	/**
	 * 分享保存目录
	 */
	public final static String SHARE_DIR = BaseConfig.getBaseDir() + "/Share/";

	/**
	 * 下载目录
	 */
	public final static String DOWNLOAD_DIR = BaseConfig.getBaseDir() + "/Downloads/";
	
	public final static String PLUGIN_DIR = BaseConfig.getBaseDir() + "/myphone/plugin/";
	
	public final static String WIDGET_PLUGIN_DIR = BaseConfig.getBaseDir() + "/myphone/widgets/";
	
	/**
	 * 桌面包名
	 */
	public final static String CONSTSTR_PANDAHOME_PACKAGENAME = "com.dianxinos.dxhome";
	/**
	 * 软件的下载地址
	 */
	public static final String SOFT_DOWNLOAD_URL = "http://pandahome.sj.91.com/soft.ashx/SoftUrl?mt=4&redirect=1&fwv=%s&packagename=%s";

	public static final String INTENT_USER_STAT = "pandahome.intent.userStat";

	/**
	 * 关闭百变锁屏LockActivity Action
	 */
	public static final String COM_ND_ANDROID_PANDALOCK_CLOSE_LOCK_ACTIVITY = "com.nd.android.pandahome.close_lock_activity";
	
	/**
	 * 开启百变锁屏LockActivity Action
	 */
	public static final String COM_ND_ANDROID_PANDALOCK_OPEN_LOCK_ACTIVITY = "com.nd.android.pandahome.open_lock_activity";
	
	/** 一键装机、玩机的缓存目录 */
	public static final String MARKET_DIR = BaseConfig.getBaseDir() + "/market/";
	
	/** 图标缓存目录 */
	public static final String ICON_CACHE_DIR = MARKET_DIR + "icon/";
	
	/** 下载软件的目录 */
	public static final String PACKAGE_DOWNLOAD_DIR=MARKET_DIR + "downloads/";
	
	/** 默认情景id */
	public static final String DEFAULT_SCENE_ID = "-1";
}
