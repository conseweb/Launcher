package com.nd.hilauncherdev.launcher;

import java.io.File;

import com.nd.hilauncherdev.kitset.config.ConfigDataBaseHelper;
import com.nd.hilauncherdev.launcher.BaseLauncherApplication;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.model.load.LauncherLoaderHelper;
import com.nd.hilauncherdev.launcher.support.DBHelperFactory;

public class LauncherApplication extends BaseLauncherApplication {
	
	/**
	 * 初始化数据库辅助类
	 */
	@Override
	public void initDBHelper(){
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
	public void initCrashHandler(){
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);
	}
	
	/**
	 * 初始化基础目录
	 */
	@Override
	public void createDefaultDir(){
		final String baseDir = BaseConfig.getBaseDir();
		File dir = new File(baseDir);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
//		dir = new File(NO_MEDIA);
//		if (!dir.isDirectory()) {
//			dir.mkdirs();
//		}
//		dir = new File(BASE_DIR_91CLOCKWEATHER);
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
	
}
