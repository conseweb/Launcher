package com.nd.hilauncherdev.theme.data;

import java.io.File;

import android.os.Environment;
import android.text.TextUtils;

import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.FileUtil;
import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.theme.module.ModuleConstant;


/**
 * <br>Description:主题全局常量
 * <br>Author:caizp
 * <br>Date:2011-6-28下午04:04:26
 */
public class ThemeGlobal {
	
	/**
	 * 默认主题Id
	 */
	public final static String DEFAULT_THEME_ID = "0";
	
	/**
     * 91黄历天气皮肤数据存放目录
     */
    public final static String BASE_DIR_CLOCKWEATHER = Environment.getExternalStorageDirectory()+"/.91Calendar/skin/";
	
	/**
	 * apt主题包配置文件名称
	 */
	public final static String THEME_CONFIG_NAME = "panda_theme.xml";
	
	/**
     * 主题包中天气皮肤包名称
     */
    public final static String THEME_CLOCKWEATHER_SKIN = "weather.nwa";
    
    /**
     * 主题包中91智能锁皮肤包名称
     */
    public final static String THEME_91ZNS_SKIN = "screenlock.zip";
    
    /**
     * 主题包91智能锁皮肤资源目录
     */
    public final static String THEME_91ZNS_PATH = "/screenlock/";
    
    /**
     * 主题包中第三方小插件皮肤包名称
     */
    public final static String THEME_WIDGET_SKIN = "widget.zip";
    
    /**
     * 主题包第三方小插件皮肤资源目录
     */
    public final static String THEME_WIDGET_PATH = "/widget/";
    
    /**
     * 主题推荐应用配置文件
     */
    public final static String THEME_RECOMMEND_APP = "/app/info";
    
    /**
     * 主题推荐应用图标目录
     */
    public final static String THEME_RECOMMEND_APP_ICON = "/app/";
    
    /**
     * APT主题包图片资源目录
     */
    public final static String THEME_APT_DRAWABLE_DIR = "res/drawable/";
    
    /**
     * APT主题包高分辨率图片资源目录
     */
    public final static String THEME_APT_DRAWABLE_XHDPI_DIR = "res/drawable-xhdpi/";
	
	public final static String DEFAULT_VALUE = "&default&";
	
	/**
	 * apt主题包后缀名
	 */
	public final static String SUFFIX_APT_THEME = "apt";
	
	/**
	 * 当前桌面支持的主题加密算法最高版本号
	 */
	public final static int SUPPORT_MAX_GUARDED_VERSION = 1;
	
	/**
	 * 加密资源文件
	 */
	public final static String GUARDED_RES = "collectall.dtc";
	
	/**
	 * PNG图片转化后的后缀名
	 */
	public final static String CONVERTED_SUFFIX_PNG = ".a";
	
	/**
	 * JPG图片转化后的后缀名
	 */
	public final static String CONVERTED_SUFFIX_JPG = ".b";
	
	/**
	 * PNG后缀名
	 */
	public final static String SUFFIX_PNG = ".png";
	
	/**
	 * JPG后缀名
	 */
	public final static String SUFFIX_JPG = ".jpg";
	
	/**
	 * 执行成功
	 */
    public final static int EXEC_SUCCESS = 1;

    /**
	 * 执行失败
	 */
    public final static int EXEC_FAIL = 0;

    /**
	 * 主题不存在
	 */
    public final static int THEME_NO_EXIST = -1;

    /**
	 * 主题已存在
	 */
    public final static int THEME_EXIST = -2;
    
    /**
     * 大图标尺寸
     */
    public final static int LARGE_ICON_SIZE = 90;
	
	/**
	 * AHOME字体程序包Intent
	 */
    public final static String INTENT_AHOME_FONT = "mobi.bbase.ahome.FONT_PROVIDER";

    /**
	 * OPENHOME字体程序包Intent
	 */
    public final static String INTENT_OPENHOME_FONT = "com.betterandroid.fonts.ACTION_REQUEST_FOR_FONTS";
    
    /**
     * OPENHOME图标程序包Intent
     */
    public final static String INTENT_OPENHOME_ICON = "com.betterandroid.launcher2.icons";
    
    /**
     * 保存在apk中assets目录下的apt主题包名称
 	 */
    public final static String APK_THEME_PATH = "theme.db";
    
    /**
     * 保存在assets目录下的主题商城包名
     */
    public final static String THEME_SHOP_PCK_NAME = "com.nd.hilauncherdev.shop3";
    
    /**
     * 保存在apk中assets目录下的apt主题包(检测标识)
     */
    public final static String INTENT_APK_THEME = "com.nd.android.pandadesktop.apk_theme";
    
    /**
	 * 桌面UI刷新广播Action
	 */
	public static String LAUNCHER_UI_REFRESH_ACTION = "nd.pandahome.internal.launcher.refreshUI";
    
    /**
     * apk主题包(检测标识)
     */
    public final static String INTENT_PANDAHOME_THEME = "com.nd.android.pandahome.theme";
    
    /**
     * 主题列表刷新广播
     */
    public static String INTENT_THEME_LIST_REFRESH = "nd.panda.theme.list.refresh";
    
    /**
     * 当前主题信息广播
     */
    public static String INTENT_CURRENT_THEME_INFO = "com.nd.android.pandahome.THEME_INFO";
    
    /**
	 * 询问当前主题广播
	 */
	public static String ACTION_ASK_THEME = "com.nd.android.pandahome.ASK_THEME";
    
    /** 百宝箱请求安装应用主题广播 */
    public static String INTENT_PANDASPACE_INSTALL_THEME = "com.nd.android.pandadesktop2.install_theme";
    
    /**
     * 黄历天气皮肤随主题安装成功广播
     */
//    public final static String WIDGET_THEME_WEATHERCLOCK_IMPORT_ACTION = "nd.pandahome.theme.THEME_91CALENDAR_IMPORT";
   
    /** 主题91天气秀皮肤包导入action*/
//    public final static String WIDGET_THEME_91WEATHERCLOCK_IMPORT_ACTION="com.nd.android.pandahome.THEME_CLOCKWEATHER_IMPORT";
    
    /**
     * 91智能锁皮肤随主题安装成功广播
     */
//    public final static String WIDGET_THEME_91ZNS_IMPORT_ACTION = "nd.pandahome.theme.THEME_91ZNS_IMPORT";
    
    /**
     * 主题切换广播主题ID参数
     */
    public final static String INTENT_THEME_PARAM_THEME_ID = "themeid";
    
    /**
     * <br>Description: 获取主题缩略图位置
     * <br>Author:caizp
     * <br>Date:2014-6-25下午3:34:51
     * @param themeId
     * @param themeType
     * @return
     */
    public static String getThemeThumbPath(String themeId, int themeType) {
    	if(TextUtils.isEmpty(themeId))return null;
    	if (ThemeType.DEFAULT == themeType) {
    		String path = BaseConfig.THEME_DIR + themeId.replace(" ", "_") + "/" + BaseThemeData.THUMBNAIL
    				+ ThemeGlobal.CONVERTED_SUFFIX_JPG;
    		if(new File(path).exists()){
    			return path;
    		}
			path = BaseConfig.THEME_DIR + themeId.replace(" ", "_") + "/" + BaseThemeData.THUMBNAIL + ".jpg";
			if(new File(path).exists()){
    			return path;
    		}
			path = BaseConfig.THEME_DIR + themeId.replace(" ", "_") + "/" + THEME_APT_DRAWABLE_DIR + BaseThemeData.THUMBNAIL + ThemeGlobal.CONVERTED_SUFFIX_JPG;
			if(new File(path).exists()){
    			return path;
    		}
			path = BaseConfig.THEME_DIR + themeId.replace(" ", "_") + "/" + THEME_APT_DRAWABLE_DIR + BaseThemeData.THUMBNAIL + ".jpg";
			if(new File(path).exists()){
    			return path;
    		}
		} else if (ThemeType.PANDAHOME == themeType) {
			return BaseConfig.THEME_THUMB_DIR + themeId + ThemeGlobal.CONVERTED_SUFFIX_JPG;
		}
    	return BaseConfig.THEME_DIR + themeId.replace(" ", "_") + "/" + BaseThemeData.THUMBNAIL
				+ ThemeGlobal.CONVERTED_SUFFIX_JPG;
    }
    
    /**
     * <br>Description: 获取模块包缩略图位置
     * <br>Author:caizp
     * <br>Date:2014年11月13日下午4:58:27
     * @param moduleId
     * @param moduleKey
     * @return
     */
    public static String getModuleThumbPath(String moduleId, String moduleKey) {
    	if(TextUtils.isEmpty(moduleId) || TextUtils.isEmpty(moduleKey)) return "";
    	String previewPath = "";
    	// 91通讯录及91智能锁使用长方形缩略图
		if(ModuleConstant.MODULE_SMS.equals(moduleKey) || ModuleConstant.MODULE_LOCKSCREEN.equals(moduleKey)) {
			previewPath = BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/" + moduleId.replace(" ", "_") + "/" + moduleKey.replace("@", "/") + "/preview.b";
			if(!new File(previewPath).exists()) {
				previewPath = BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/" + moduleId.replace(" ", "_") + "/" + moduleKey.replace("@", "/") + "/preview.jpg";
			}
		} else {// 图标、天气、百度输入法使用正方形缩略图
			previewPath = BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/" + moduleId.replace(" ", "_") + "/" + moduleKey.replace("@", "/") + "/thumbnail.b";
			if(!new File(previewPath).exists()) {
				previewPath = BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/" + moduleId.replace(" ", "_") + "/" + moduleKey.replace("@", "/") + "/thumbnail.jpg";
			}
		}
		return previewPath;
    }
    
    /**
     * <br>Description: 判断SD卡主题资源是否被删除
     * <br>Author:caizp
     * <br>Date:2014年12月29日上午11:15:00
     * @param themeType
     * @param themeId
     * @param aptPath
     * @return
     */
    public static boolean isThemeResCleaned(int themeType, String themeId, String aptPath) {
    	if(DEFAULT_THEME_ID.equals(themeId)) return false;
    	if(!TelephoneUtil.isSdcardExist()) return false;
    	return (ThemeType.DEFAULT == themeType && !FileUtil.isFileExits(BaseConfig.THEME_DIR + aptPath+ ThemeGlobal.THEME_CONFIG_NAME))
		|| (ThemeType.PANDAHOME == themeType && !AndroidPackageUtils.isPkgInstalled(BaseConfig.getApplicationContext(), themeId));
    }
    
    /**
     * <br>Description: 判断SD卡模块资源是否被删除
     * <br>Author:caizp
     * <br>Date:2014年12月29日上午11:24:43
     * @param moduleId
     * @param moduleKey
     * @return
     */
    public static boolean isModuleResCleaned(String moduleId, String moduleKey) {
    	if(DEFAULT_THEME_ID.equals(moduleId)) return false;
    	if(!TelephoneUtil.isSdcardExist()) return false;
    	return (!FileUtil.isFileExits(BaseConfig.MODULE_DIR	+ moduleKey.replace("@", "/") + "/"
				+ moduleId.replace(" ", "_") + "/" + ThemeGlobal.THEME_CONFIG_NAME));
    }
	
}
