/**
 *
 */
package com.bitants.common.theme.data;

import java.util.HashMap;

import com.bitants.common.theme.module.ModuleConstant;

/**
 *
 */
public class BaseThemeData {
	public static String THEME_COMP_PKG = "com.bitants.launcher";
	public static String THEME_APP_SELECT_ACTIVITY = "com.bitants.launcherdev.app.AppResolverSelectActivity";
	
	//=======================下列属性是可配置的config.xml========================//
	/** 默认的浏览器Intent */
	public static String INTENT_BROWSER = "http://www.google.com#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;component=com.bitants.launcher/com.bitants.launcherdev.app.activity.AppResolverSelectActivity;B.is_always_select=false;end";
	/** 默认的电话Intent*/
	public static String INTENT_PHONE = "#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;component=com.bitants.launcher/com.bitants.launcherdev.app.activity.AppResolverSelectActivity;B.is_always_select=false;end";
	/** 默认的联系人Intent */
	public static String INTENT_CONTACTS = "content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;component=com.bitants.launcher/com.bitants.launcherdev.app.activity.AppResolverSelectActivity;B.is_always_select=false;end";
	/** 默认的短信Intent */
	public static String INTENT_MMS = "content://mms-sms/#Intent;action=android.intent.action.MAIN;launchFlags=0x10000000;component=com.bitants.launcher/com.bitants.launcherdev.app.activity.AppResolverSelectActivity;B.is_always_select=false;end";
	
	public static void init(){
		String THEME_COMP_APP_SELECT_ACTIVITY = THEME_COMP_PKG + "/" + THEME_APP_SELECT_ACTIVITY;
		
		/** 默认的浏览器Intent */
		INTENT_BROWSER = "http://www.google.com#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;component=" + THEME_COMP_APP_SELECT_ACTIVITY + ";B.is_always_select=false;end";
		/** 默认的电话Intent*/
		INTENT_PHONE = "#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;component=" + THEME_COMP_APP_SELECT_ACTIVITY + ";B.is_always_select=false;end";
		/** 默认的联系人Intent */
		INTENT_CONTACTS = "content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;component=" + THEME_COMP_APP_SELECT_ACTIVITY + ";B.is_always_select=false;end";
		/** 默认的短信Intent */
		INTENT_MMS = "content://mms-sms/#Intent;action=android.intent.action.MAIN;launchFlags=0x10000000;component=" + THEME_COMP_APP_SELECT_ACTIVITY + ";B.is_always_select=false;end";
	}
	
	
	/** 图标文字颜色 */
	public static final String TEXT_COLOR = "text_color";
	/** 图标文字大小 */
	public static final String TEXT_SIZE = "text_size";
	

	/** 地图 */
	public static final String ICON_MAPS = "com.google.android.apps.maps|com.google.android.maps.mapsactivity";
	/** 浏览器 */
	public static final String ICON_BROWSER = "com.android.browser|com.android.browser.browseractivity";
	/** 电话*/
	public static final String ICON_PHONE = "com.android.contacts|com.android.contacts.dialtactsactivity";
	/** 联系人 */
	public static final String ICON_CONTACTS = "com.android.contacts|com.android.contacts.dialtactscontactsentryactivity";
	/** 设置 */
	public static final String ICON_SETTINGS = "com.android.settings|com.android.settings.settings";
	/** 短信 */
	public static final String ICON_MMS = "com.android.mms|com.android.mms.ui.conversationlist";
	/** 相机 */
	public static final String ICON_CAMERA = "com.android.camera|com.android.camera.camera";
	/** 日历 */
	public static final String ICON_CALENDAR = "com.android.calendar|com.android.calendar.calendaractivity";
	/** 图库 */
	public static final String ICON_GALLERYPICKER = "com.android.camera|com.android.camera.gallerypicker";
	/** 时钟 */
	public static final String ICON_ALARMCLOCK = "com.android.alarmclock|com.android.alarmclock.alarmclock";
	/** 电子邮件 */
	public static final String ICON_EMAIL = "com.android.email|com.android.email.activity.welcome";
	/** 计算器 */
	public static final String ICON_CALCULATOR = "com.android.calculator2|com.android.calculator2.calculator";
	/** 音乐 */
	public static final String ICON_MUSIC = "com.android.music|com.android.music.musicbrowseractivity";
	/** 摄相机 */
	public static final String ICON_VIDEO_CAMERA = "com.android.camera|com.android.camera.videocamera";
	/** 语音拨号 */
	public static final String ICON_VOICE_DIALER = "com.android.voicedialer|com.android.voicedialer.voicedialeractivity";
	/** 语音搜索 */
	public static final String ICON_VOICE_SEARCH = "com.google.android.voicesearch|com.google.android.voicesearch.recognitionactivity";
	/** 电子市场 */
	public static final String ICON_GOOGLE_PLAY = "com.android.vending|com.android.vending.assetbrowseractivity";
	/** 天气 */
	public static final String ICON_WEATHER = "com.android.weather|com.android.weather.weatheractivity";
	/** 淘宝 */
	public static final String ICON_TAOBAO = "com.taobao.taobao|com.taobao.tao.mainactivity2";
	/** Camera360 */
	public static final String ICON_CAMERA360 = "vstudio.android.camera360|vstudio.android.camera360.gphotomain";
	/** 安卓市场 */
	public static final String ICON_HIAPK = "com.hiapk.marketpho|com.hiapk.marketpho.marketmainframe";
	/** 微信 */
	public static final String ICON_MM = "com.tencent.mm|com.tencent.mm.ui.launcherui";
	/** 新浪微博 */
	public static final String ICON_WEIBO = "com.sina.weibo|com.sina.weibo.splashactivity";
	/** QQ */
	public static final String ICON_QQ = "com.tencent.qq|com.tencent.qq.splashactivity";
	/** 91手机助手 */
	public static final String ICON_PANDASPACE = "com.dragon.android.pandaspace|com.dragon.android.pandaspace.main.LoadingActivity";
	/** Facebook */
	public static final String ICON_FACEBOOK = "com.facebook.katana|com.facebook.katana.loginactivity";
	/** Twitter */
	public static final String ICON_TWITTER = "com.twitter.android|com.twitter.android.startactivity";
	/** Skype */
	public static final String ICON_SKYPE = "com.skype.rover|com.skype.rover.main";
	/** 应用商店 */
	public static final String ICON_APP_STORE = "com.bitants.launcher|com.bitants.launcherdev.appstore.AppStoreSwitchActivity";
	
	/** 图标蒙板前景 */
	public static final String PANDA_ICON_FOREGROUND_MASK = "panda_icon_foreground_mask";
	/** 图标切割蒙板 */
	public static final String PANDA_ICON_CUT_MASK = "panda_icon_cut_mask";
	/** 图标蒙板背景 */
	public static final String PANDA_ICON_BACKGROUND_MASK = "panda_icon_background_mask";
	/** 文件夹图标背景 */
	public static final String PANDA_FOLDER_BACKGROUND = "panda_folder_background";
	/** 文件夹图标关闭前景 */
	public static final String PANDA_FOLDER_FOREGROUND_CLOSED = "panda_folder_foreground_closed";
	/** 文件夹图标打开前景 */
	public static final String PANDA_FOLDER_FOREGROUND_OPEN = "panda_folder_foreground_open";
	/** 文件夹加锁图标 */
	public static final String PANDA_FOLDER_ENCRIPT_MASK = "panda_folder_encript_mask";
	/** android 4.0风格文件夹图标背景 */
	public static final String PANDA_ANDROID_FOLDER_BACKGROUND = "panda_android_folder_background";
	/** android 4.0风格文件夹加锁图标 */
	public static final String PANDA_ANDROID_FOLDER_ENCRIPT_MASK = "panda_android_folder_encript_mask";
	
	/**
	 * 壁纸(wallpaper.jpg)
	 */
	public static final String WALLPAPER = "wallpaper";
	/**
	 * 匣子背景
	 */
	public static final String DRAWER = "drawer";
	/**
	 * 主题缩略图(thumbnail.jpg)
	 */
	public static final String THUMBNAIL = "thumbnail";
	/**
	 * 主题预览图0
	 */
	public static final String PREVIEW0 = "preview0";
	/**
	 * 主题预览图1
	 */
	public static final String PREVIEW1 = "preview1";
	/**
	 * 主题预览图2
	 */
	public static final String PREVIEW2 = "preview2";
	
	/** 
	 * 指示灯类型   0:默认横条指示灯  1:打点指示灯
	 */
	public static final String LAUNCHER_LIGHT_TYPE = "launcher_light_type";
	/**
	 * 指示灯选中屏背景(横条型)
	 * 备注：launcher_light_hl被部分杀毒软件识别为病毒标识，故增加home_light_hl.replace("home", "launcher")处理
	 */
	public static final String HOME_LIGHT_HL = "home_light_hl";//"launcher_light_hl";
	/**
	 * 指示灯背景条(横条型)
	 */
	public static final String LAUNCHER_LIGHT_LINE = "launcher_light_line";
	/**
	 * 指示灯选中屏(打点型)
	 */
	public static final String LAUNCHER_LIGHT_SELECTED = "launcher_light_selected";
	/**
	 * 指示灯未选中屏(打点型)
	 */
	public static final String LAUNCHER_LIGHT_NORMAL = "launcher_light_normal";
	
	//-------锁屏壁纸兼容---------begin//
	
	/** 91智能锁主题壁纸(jpg) */
	public static final String ZNS_LOCK_BG = "lock_bg";
	
	/** 安卓锁屏主题壁纸(png) */
	public static final String PANDA_LOCK_MAIN_BACKGROUND = "panda_lock_main_background";
	
	//-------锁屏壁纸兼容---------end//
	
	/**
	 * 图标key集合
	 */
	public static final String[] iconKeys = { ICON_MAPS, ICON_BROWSER,
			ICON_PHONE, ICON_CONTACTS, ICON_SETTINGS, ICON_MMS, ICON_CAMERA,
			ICON_CALENDAR, ICON_GALLERYPICKER, ICON_ALARMCLOCK, ICON_EMAIL,
			ICON_CALCULATOR, ICON_MUSIC, ICON_VIDEO_CAMERA, ICON_VOICE_DIALER,
			ICON_VOICE_SEARCH, ICON_GOOGLE_PLAY, ICON_WEATHER, ICON_TAOBAO,
			ICON_CAMERA360, ICON_HIAPK, ICON_MM, ICON_WEIBO, ICON_QQ,
			ICON_PANDASPACE, ICON_FACEBOOK, ICON_TWITTER, ICON_SKYPE };
	
	/**
	 * 生成主题预览图时用到的图标
	 */
	public static final String[] themeThumbApps = new String[] { ICON_PHONE,
			ICON_CONTACTS, ICON_MMS, ICON_BROWSER, ICON_ALARMCLOCK, ICON_EMAIL,
			ICON_MAPS, ICON_GOOGLE_PLAY, ICON_CALCULATOR, ICON_CAMERA,
			ICON_GALLERYPICKER, ICON_VIDEO_CAMERA, ICON_MUSIC, ICON_SETTINGS,
			ICON_VOICE_DIALER, ICON_VOICE_SEARCH };
	
	/**
	 * 后缀为JPG的图片key集合
	 */
    public static HashMap<String,String> jpgKeysMap = new HashMap<String, String>();
	public static final String[] jpgDrawableKeys = { WALLPAPER, DRAWER,
			THUMBNAIL, PREVIEW0, PREVIEW1, PREVIEW2 };
    
    public static HashMap<String, String> largeIconMap = new HashMap<String, String>();
    
    public static HashMap<String, String> drawableMap = new HashMap<String, String>();
    
    public static HashMap<String, String> nodpiDrawableMap = new HashMap<String, String>();
    
    private static BaseThemeData baseThemeData;
    
    public static BaseThemeData getInstance() {
    	if(null == baseThemeData) {
    		baseThemeData = new BaseThemeData();
    	}
    	return baseThemeData;
    }
    
    /**
     * <br>Description: 构建主题映射数据
     */
    public void buildThemeData() {
    	// 后缀为JPG的图片
    	for(int i=0; i<jpgDrawableKeys.length; i++){
    		jpgKeysMap.put(jpgDrawableKeys[i], "");
    	}
    	// 主题大图标
    	for(int i=0; i<iconKeys.length; i++){
    		largeIconMap.put(iconKeys[i], ModuleConstant.MODULE_ICONS);
    	}
    	largeIconMap.put(PANDA_ICON_FOREGROUND_MASK, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_ICON_CUT_MASK, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_ICON_BACKGROUND_MASK, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_FOLDER_BACKGROUND, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_FOLDER_FOREGROUND_CLOSED, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_FOLDER_FOREGROUND_OPEN, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_FOLDER_ENCRIPT_MASK, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_ANDROID_FOLDER_BACKGROUND, ModuleConstant.MODULE_ICONS);
		largeIconMap.put(PANDA_ANDROID_FOLDER_ENCRIPT_MASK, ModuleConstant.MODULE_ICONS);
    	// 主题图片
    	drawableMap.put(THUMBNAIL, "");
		drawableMap.put(PREVIEW0, "");
		drawableMap.put(PREVIEW1, "");
		drawableMap.put(PREVIEW2, "");
		drawableMap.put(HOME_LIGHT_HL.replace("home", "launcher"), ModuleConstant.MODULE_HOME);
		drawableMap.put(LAUNCHER_LIGHT_LINE, ModuleConstant.MODULE_HOME);
		drawableMap.put(LAUNCHER_LIGHT_SELECTED, ModuleConstant.MODULE_HOME);
		drawableMap.put(LAUNCHER_LIGHT_NORMAL, ModuleConstant.MODULE_HOME);
    }
    
    
}
