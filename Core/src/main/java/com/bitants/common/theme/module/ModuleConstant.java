package com.bitants.common.theme.module;

import java.io.File;

import com.bitants.common.launcher.config.BaseConfig;

/**
 * <br>Description: 主题模块常量
 */
public class ModuleConstant {
	
	public final static String INTENT_MODULE_LIST_REFRESH = "nd.panda.module.list.refresh";
	
	//-------------------------- 主题模块分类 begin---------------------------------//
	/**
	 * 图标模块分类
	 */
	public final static String MODULE_CATEGORY_ICONS = "icons";
	/**
	 * 天气模块分类
	 */
	public final static String MODULE_CATEGORY_WEATHER = "weather";
	/**
	 * 锁屏模块分类
	 */
	public final static String MODULE_CATEGORY_LOCK = "lock";
	/**
	 * 通讯录模块分类
	 */
	public final static String MODULE_CATEGORY_CONTACT = "contact";
	/**
	 * 输入法模块分类
	 */
	public final static String MODULE_CATEGORY_INPUT_METHOD = "input_method";
	//-------------------------- 主题模块分类 end---------------------------------//
	
	//-------------------------- 主题可单独拆分模块KEY begin------------------------//
	/**
	 * 图标模块key(目录)
	 */
	public final static String MODULE_ICONS = "icons";
	/**
	 * 壁纸模块key(目录)
	 */
	public final static String MODULE_WALLPAPER = "wallpaper";
	/**
	 * 天气皮肤模块key(目录)
	 */
	public final static String MODULE_WEATHER = "weather";
	/**
	 * 锁屏皮肤模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_LOCKSCREEN = "widget@lockscreen";
	/**
	 * 91通讯录皮肤模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_SMS = "widget@sms";
	/**
	 * 百度输入法皮肤模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_BAIDU_INPUT = "widget@baidu_input";
	/**
	 * 讯飞输入法皮肤模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_IFLYTEK_INPUT = "widget@com.iflytek.inputmethod";
	/**
	 * 触宝拨号皮肤模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_COOTEK_DIALER = "widget@com.cootek.smartdialer";
	
	//-------------------------- 主题可单独拆分模块KEY end------------------------//
	
	
	
	//-------------------------- 主题其他模块KEY begin------------------------//
	/**
	 * 桌面UI模块key(目录)
	 */
	public final static String MODULE_HOME = "home";
	/**
	 * 桌面UI模块key(目录)
	 */
	public final static String MODULE_INNER_WIDGET = "inner_widget";
	/**
	 * 桌面自带插件--模拟时钟模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_INNER_WIDGET_ANALOG_CLOCK = "inner_widget@analog_clock";
	/**
	 * 桌面自带插件--搜索模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_INNER_WIDGET_SEARCH = "inner_widget@search";
	/**
	 * 桌面自带插件--淘宝模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_INNER_WIDGET_TAOBAO = "inner_widget@taobao";
	/**
	 * 桌面自带插件--省电模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_INNER_WIDGET_POWER = "inner_widget@power";
	/**
	 * 桌面自带插件--一键清理模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_INNER_WIDGET_CLEANER = "inner_widget@cleaner";
	/**
	 * 桌面自带插件--一键换壁纸模块key(目录，多级目录以@分隔)
	 */
	public final static String MODULE_INNER_WIDGET_WALLPAPER = "inner_widget@wallpaper";
	
	//-------------------------- 主题其他模块KEY end------------------------//
	
	
	
	//-------------------------- 模块对应插件或应用包名 begin------------------------//
	/**
	 * 天气模块对应插件包名(天气插件SDK内嵌桌面中，故包名为桌面包名)
	 */
	public final static String MODULE_WEATHER_PKG = BaseConfig.getApplicationContext().getPackageName();
	/**
	 * 锁屏模块对应应用包名
	 */
	public final static String MODULE_LOCKSCREEN_PKG="cn.com.nd.s";
	/**
	 * 91通讯录对应应用包名
	 */
	public final static String MODULE_SMS_PKG="com.nd.desktopcontacts";
	/**
	 * 百度输入法对应应用包名
	 */
	public final static String MODULE_BAIDU_INPUT_PKG="com.baidu.input";
	/**
	 * 讯飞输入法对应应用包名
	 */
	public final static String MODULE_IFLYTEK_INPUT_PKG="com.iflytek.inputmethod";
	/**
	 * 触宝拨号对应应用包名
	 */
	public final static String MODULE_COOTEK_DIALER_PKG="com.cootek.smartdialer";
	//-------------------------- 模块对应插件或应用包名 end------------------------//
	
	/**
	 * 单独模块KEY、对应包名、所属模块分类
	 */
	public static String[][] MODULE_KEY_ARRAY = { 
		{MODULE_ICONS, "", MODULE_CATEGORY_ICONS}, {MODULE_WALLPAPER, "", MODULE_CATEGORY_ICONS},
		{MODULE_WEATHER, MODULE_WEATHER_PKG, MODULE_CATEGORY_WEATHER}, 
		{MODULE_LOCKSCREEN, MODULE_LOCKSCREEN_PKG, MODULE_CATEGORY_LOCK}, 
		{MODULE_SMS, MODULE_SMS_PKG, MODULE_CATEGORY_CONTACT}, 
		{MODULE_BAIDU_INPUT, MODULE_BAIDU_INPUT_PKG, MODULE_CATEGORY_INPUT_METHOD},
		{MODULE_IFLYTEK_INPUT, MODULE_IFLYTEK_INPUT_PKG, MODULE_CATEGORY_INPUT_METHOD},
		{MODULE_COOTEK_DIALER, MODULE_COOTEK_DIALER_PKG, MODULE_CATEGORY_CONTACT} 
	};
	
	/**
	 * <br>Description: 根据模块key获取模块分类
	 * @param moduleKey
	 * @return
	 */
	public static String getModuleCategoryByKey(String moduleKey) {
		for(int i=0; i<MODULE_KEY_ARRAY.length; i++) {
			if(MODULE_KEY_ARRAY[i][0].equals(moduleKey)) {
				return MODULE_KEY_ARRAY[i][2];
			}
		}
		return null;
	}
	
	/**
	 * <br>Description: 根据模块key获取模块对应APP包名
	 * @param moduleKey
	 * @return
	 */
	public static String getModulePkgByKey(String moduleKey) {
		for(int i=0; i<MODULE_KEY_ARRAY.length; i++) {
			if(MODULE_KEY_ARRAY[i][0].equals(moduleKey)) {
				return MODULE_KEY_ARRAY[i][1];
			}
		}
		return null;
	}
	
	/**
	 * <br>Description: 根据模块对应包名获取模块key
	 * @param moduleKey
	 * @return
	 */
	public static String getModuleKeyByPkg(String modulePkg) {
		for(int i=0; i<MODULE_KEY_ARRAY.length; i++) {
			if(MODULE_KEY_ARRAY[i][1].equals(modulePkg)) {
				return MODULE_KEY_ARRAY[i][0];
			}
		}
		return null;
	}
	
	/**
	 * <br>Description: 创建模块包目录
	 */
	public static void createModuleDir() {
		for(int i=0; i<MODULE_KEY_ARRAY.length;i++) {
			File dir = new File(BaseConfig.MODULE_DIR + MODULE_KEY_ARRAY[i][0].replace("@", "/")+ "/");
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}

}
