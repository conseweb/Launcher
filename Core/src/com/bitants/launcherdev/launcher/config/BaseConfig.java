package com.bitants.launcherdev.launcher.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Environment;
import android.widget.Toast;

import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.BaseLauncherApplication;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.theme.pref.ThemeSharePref;

public class BaseConfig {
	public static final String TAG = "com.bitants.launcherdev";

	public static final int NO_DATA = -1;
	public static final float NO_DATA_FLOAT = -1.0f;

	public static final int ALPHA_155 = 155;
	public final static long ANI_255 = 255;
	
	/**
	 * 是否处于Apt类型情景桌面
	 */
	private static boolean isOnAptScene = false;
	
	protected static Context applicationContext;

	protected static BaseLauncher mLauncher;
	
	/**
	 * 是否自绘壁纸(android4.0以上，壁纸自绘)
	 */
    public static boolean isDrawWallPaper = false;
    
	/**
	 * 语言是否初始化，防止不同process错误
	 */
	private static int isZhInit = -1;
	private static boolean isZh = true;

	/** BASE_DIR 读取config.xml */
	private static String BASE_DIR = Environment.getExternalStorageDirectory() + "/bitants";
	
	/** 防止图库扫描 */
	public static String NO_MEDIA = getBaseDir() + "/.nomedia";

	/** 91天气秀皮肤存放目录 */
	public static String BASE_DIR_CLOCKWEATHER = getBaseDir() + "/clockandweather/skins/";

	/** 主题资源根目录 */
	public static String THEME_DIR = getBaseDir() + "/Themes/";
	
	/**
	 * 模块包目录
	 */
	public static String MODULE_DIR = getBaseDir() + "/module/";
	
	/**
	 * 主题兼容预览图根目录
	 */
	public static String THEME_THUMB_DIR = getBaseDir() + "/LocalThemeThumbnail/";
	
	/**THEME_HOME*/
    public static String CACHES_HOME = getBaseDir() + "/caches/";
    
    public static String TEMP_DIR = getBaseDir() + "/tmp/";
	
	/** 备份目录 */
	public static String BACKUP_DIR = getBaseDir() + "/Backup/";
	
	/**
	 * wifi自动下载目录
	 */
	public static final String WIFI_DOWNLOAD_PATH = getBaseDir() + "/WifiDownload/";
	
	/**
	 * 壁纸 sdcard根目录
	 */
	public static String WALLPAPER_BASE_DIR = getBaseDir() + "/myphone/wallpaper";
	
	/**
	 * 壁纸存放目录
	 */
	public static String PICTURES_HOME = WALLPAPER_BASE_DIR + "/Pictures/";
	
	/**
	 * root权限的命令行文件名，可执行root权限的命令
	 */
	public static final String SUPER_SHELL_FILE_NAME = "super_shell";
	/**
	 * 调用 root权限的shell时传进的参数，过滤权限，防止其他应用的操作
	 */
	public static final String SUPER_SHELL_PERMISSION = "com.bitants.launcher.permission.SUPER_SHELL";
	
	/**
	 * 默认字体测量大小
	 */
	public static int defaultFontMeasureSize = 33;
	
	/**
	 * 全局图标底板
	 */
	public static Bitmap iconBackground;
	/**
	 * 全局图标面板
	 */
	public static Bitmap iconFrontground;
	/**
	 * 全局图标切割蒙板
	 */
	public static Bitmap iconMask;
	
	/**
	 * 主题资源解密是否可用(用于判断加密主题是否可用)
	 */
	public static boolean isThemeDecoderAvaliable = true;
	
	/**
	 * Launcher布局的底部padding
	 */
	private static int launcherBottomPadding = 0; 
	
	public static String getBaseDir(){
		if(!LauncherConfig.isInitConfig()){
			LauncherConfig.init(applicationContext);
		}
		if(LauncherConfig.isInitConfig()){			
			return BASE_DIR;
		}else{
			throw new RuntimeException("getBaseDir() Exception"); 
		}
	}

	public static void initDir(String base_app_dir) {
		BASE_DIR = Environment.getExternalStorageDirectory() + "/"
				+ base_app_dir;

		/** 防止图库扫描 */
		NO_MEDIA = BASE_DIR + "/.nomedia";
		/** 91天气秀皮肤存放目录 */
		BASE_DIR_CLOCKWEATHER = BASE_DIR + "/clockandweather/skins/";
		/** 主题资源根目录 */
		THEME_DIR = BASE_DIR + "/Themes/";
		/**
		 * 模块包目录
		 */
		MODULE_DIR = BASE_DIR + "/module/";

		/**
		 * 主题兼容预览图根目录
		 */
		THEME_THUMB_DIR = BASE_DIR + "/LocalThemeThumbnail/";

		/** THEME_HOME */
		CACHES_HOME = BASE_DIR + "/caches/";

		TEMP_DIR = BASE_DIR + "/tmp/";

		/** 备份目录 */
		BACKUP_DIR = BASE_DIR + "/Backup/";

		/**
		 * 壁纸 sdcard根目录
		 */
		WALLPAPER_BASE_DIR = BASE_DIR + "/myphone/wallpaper";
		/**
		 * 壁纸存放目录
		 */
		PICTURES_HOME = WALLPAPER_BASE_DIR + "/Pictures/";
	}
	
	public static Context getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(Context ctx) {
		applicationContext = ctx;
	}
	
	/**
	 * 跨进程调用可能为NULL!!! <br>
	 *
	 * @return
	 */
	public static BaseLauncher getBaseLauncher() {
		return mLauncher;
	}

	public static void setBaseLauncher(BaseLauncher launcher) {
		mLauncher = launcher;
	}
	
	/**
	 * 是否处于apt情景桌面
	 * @return
	 */
	public static boolean isOnScene() {
		return isOnAptScene;
	}

	public static void setOnScene(boolean isOnScene) {
		isOnAptScene = isOnScene;
	}
	
	/**
	 * 是否中文环境
	 */
	public static boolean isZh() {
		if (isZhInit != -1) {
			return isZh;
		}

		if (applicationContext == null)
			return isZh;

		isZhInit = 0;
		isZh = TelephoneUtil.isZh(applicationContext);
		return isZh;
	}
	
	/**
	 * <br>
	 * Description: 重置默认字体测量大小 <br>
	 */
	public static void resetDefaultFontMeasureSize() {
		Paint paint = new Paint();
		paint.setTextSize(((BaseLauncherApplication) getApplicationContext().getApplicationContext()).getResources().getDimensionPixelSize(R.dimen.text_size));
		defaultFontMeasureSize = paint.getFontMetricsInt(null);
	}

	/**
	 *  @return
	 */
	public static boolean isLargeIconMode() {
		boolean isLargeIconEnabled = BaseSettingsPreference.getInstance().isLargeIconEnabled();
		if (!isLargeIconEnabled)
			return false;
		
		boolean isDefaultTheme = ThemeSharePref.getInstance(applicationContext).isDefaultTheme();
		if (isDefaultTheme)
			return true;
		
		boolean isLargeIconTheme = BaseSettingsPreference.getInstance().isLargeIconTheme();
		if (!isDefaultTheme && isLargeIconTheme)
			return true;
			
		return false;
	}
	
	
	/**
	 * 是否为编辑锁定状态
	 * @param context
	 * @return true表示处于锁定状态，不允许编辑
	 */
	public static boolean allowEdit(Context context) {
		if (BaseConfigPreferences.getInstance().getIsEditLock()) {
			Toast.makeText(context, context.getText(R.string.edit_lock_toast_lock), Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取IconCache接口
	 *  @return
	 */
	public static BaseIconCache getIconCache(){
		BaseLauncherApplication application = (BaseLauncherApplication) applicationContext.getApplicationContext();
		return application.getIconCache();
	}
	
	/**
	 * 获取Data目录下桌面文件夹路径
	 * @return
	 */
	public static String getApplicationDataPath(){
		return Environment.getDataDirectory() + "/data/" + getApplicationContext().getPackageName();
	}
	
	public static int getLauncherBottomPadding() {
		return launcherBottomPadding;
	}
	
	public static void setLauncherBottomPadding(int launcherBottomPadding) {
		BaseConfig.launcherBottomPadding = launcherBottomPadding;
	}
}
