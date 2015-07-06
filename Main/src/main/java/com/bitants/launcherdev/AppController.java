package com.bitants.launcherdev;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.bitants.common.launcher.BaseLauncherApplication;
import com.bitants.common.utils.ALog;
import com.bitants.launcherdev.kitset.config.ConfigDataBaseHelper;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.model.load.LauncherLoaderHelper;
import com.bitants.common.launcher.support.DBHelperFactory;
import com.bitants.launcherdev.launcher.LauncherLoaderHelperImpl;

import java.io.File;

import hugo.weaving.DebugLog;

public class AppController extends BaseLauncherApplication {

	public static final String TAG = AppController.class.getSimpleName();

	private static AppController mInstance;

	@DebugLog
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		ALog.setTag("BitAntLauncher");
		ALog.setLevel(ALog.Level.I);
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	/**
	 * 初始化数据库辅助类
	 */
	@Override
	public void initDBHelper() {
		DBHelperFactory.getInstance().setConfigDataBaseHelper(new ConfigDataBaseHelper());
//		DBHelperFactory.getInstance().setAppDataBaseHelper(new AppDataBaseHelper());
//		DBHelperFactory.getInstance().setThemeDataBaseHelper(new LauncherThemeDataBaseHelper());
	}
	
	/**
	 * 初始化底层抽离辅助类
	 */
	@Override
	public LauncherLoaderHelper getLauncherHelper(){
		return LauncherLoaderHelperImpl.getInstance();
	}

	/**
	 * 初始化LauncherModel
	 * @param iconCache
	 * @return
	 */
//	@Override
//	public BaseLauncherModel createLauncherModel(BaseIconCache iconCache){
//		return new LauncherModel(this, iconCache);
//	}
	
	/**
	 * 初始化IconCache
	 * @param mContext
	 * @return
	 */
//	@Override
//	public BaseIconCache createIconCache(Context mContext) {
//		return new IconCache(mContext);
//	}
	
	/**
	 * 初始化异常捕获
	 */
	@Override
	public void initCrashHandler() {
        //如果使用美国节点，请加上这行代码
//		AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize(this, "5rm9p8r1l2nkmm1x6gl7ktv8hx1ravk1l8p7z162umd6ej68", "h7s6qj00c4ib3msko5wvkgzf6n4k65q5u8eg1v39sssq8qtq");
        AVAnalytics.enableCrashReport(this, true);
	}
	
	/**
	 * 初始化基础目录
	 */
	@Override
	public void createDefaultDir() {
		final String baseDir = BaseConfig.getBaseDir();
		File dir = new File(baseDir);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
//		dir = new File(NO_MEDIA);
//		if (!dir.isDirectory()) {
//			dir.mkdirs();
//		}
//		dir = new File(BASE_DIR_CLOCKWEATHER);
//		if (!dir.isDirectory()) {
//			dir.mkdirs();
//		}
//		dir = new File(THEME_DIR);
//		if (!dir.isDirectory()) {
//			dir.mkdirs();
//		}
//		dir = new File(THEME_THUMB_DIR);
//		if (!dir.isDirectory()) {
//			dir.mkdirs();
//		}
	}
	
	/**
	 *  加载主题数据
	 */
	@Override
	public void loadThemeIntentData(){
		super.loadThemeIntentData();
//		new ThemeData().buildThemeData();
//		ThemeManagerFactory.getInstance().setThemeManagerHelper(new LauncherThemeManagerHelper());
	}

//	public CrashReportingLibrary getCrashReporter() {
//		return mCrashReporter;
//	}
	
}
