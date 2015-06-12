package com.bitants.common.launcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.model.load.Callbacks;
import com.bitants.common.theme.ThemeManagerFactory;
import com.bitants.common.theme.ThemeManagerHelper;
import com.bitants.common.kitset.util.SystemUtil;
import com.bitants.common.launcher.model.load.LauncherLoaderHelper;
import com.bitants.common.launcher.support.BaseIconCache;
import com.bitants.common.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.common.theme.data.BaseThemeData;

public class BaseLauncherApplication extends Application {
	private static final String TAG = "LauncherApplication";

	public BaseLauncherModel mModel;
	public BaseIconCache mIconCache;

	/**
	 * 动态插件的Application
	 */
	private Map<String, Application> mPluginApplicationCache = new HashMap<String, Application>();
	
	@Override
	public void onCreate() {
		//VMRuntime.getRuntime().setMinimumHeapSize(4 * 1024 * 1024);
		super.onCreate();
		
		Log.e(TAG, "LauncherApplication.onCreate");
		//初始化基础设置
		initConfig();

		initDBHelper();
		
		//初始化基础目录
		createDefaultDir();
		//初始化打点统计

		initCrashHandler();

		/**
		 * 新增
		 */
		loadThemeIntentData();
		
		doItOnlyMainProcess();
	}
	
	private void initConfig(){
		LauncherConfig.init(this, getLauncherHelper());
	}

	/**
	 * 仅在主进程调用
	 */
	private void doItOnlyMainProcess() {
		String processName = SystemUtil.getCurProcessName(this);
		if (!this.getPackageName().equals(processName))
			return;
		
//		HiAnalytics.submitEvent(this, AnalyticsConstant.LAUNCHER_STARTUP);
		Log.e(TAG, "LauncherApplication.doItOnlyMainProcess");
		
		mIconCache = createIconCache(this);
		
		if(mModel == null){			
			mModel = createLauncherModel(mIconCache);
		}
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		registerReceiver(mModel, filter);
	}

	/**
	 * Receives notifications whenever the user favorites have changed.
	 * 导致桌面重刷，屏蔽
	 */
	@Deprecated
	final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			mModel.startLoader(BaseLauncherApplication.this, false, false, false);
		}
	};

	BaseLauncherModel setLauncher(Callbacks mLauncherBinder) {
		if(mModel == null){//修复部分机型出现的空指针问题
			if(mIconCache == null){
				mIconCache = createIconCache(this);
			}
			mModel = createLauncherModel(mIconCache);
		}
		mModel.initialize(mLauncherBinder);
		return mModel;
	}


	public BaseLauncherModel getModel() {
		return mModel;
	}
	
	public BaseIconCache getIconCache() {
		return mIconCache;
	}
	
	/**
	 * There's no guarantee that this function is ever called.
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();

		if (mModel != null) {
			unregisterReceiver(mModel);

//			HiAnalytics.submitEvent(this, AnalyticsConstant.LAUNCHER_DESTORY);
			Log.e(TAG, "LauncherApplication.onTerminate");
		}
		
		if (null != mPluginApplicationCache) {
			Collection<Application> entrys = mPluginApplicationCache.values();
			for (Application application : entrys) {
				if (null != application) {
					application.onTerminate();
				}
			}
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (null != mPluginApplicationCache) {
			Collection<Application> entrys = mPluginApplicationCache.values();
			for (Application application : entrys) {
				if (null != application) {
					application.onConfigurationChanged(newConfig);
				}
			}
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (null != mPluginApplicationCache) {
			Collection<Application> entrys = mPluginApplicationCache.values();
			for (Application application : entrys) {
				if (null != application) {
					application.onLowMemory();
				}
			}
		}
	}

	public Application getPluginApplication(String dexPath) {
		return mPluginApplicationCache.get(dexPath);
	}

	public void setPluginApplication(String dexPath, Application pluginApplication) {
		this.mPluginApplicationCache.put(dexPath, pluginApplication);
	}
	
	
	/**
	 * 初始化数据库辅助类
	 */
	public void initDBHelper(){
		
	}
	
	/**
	 * 初始化Launcher辅助类，主要是分离底层使用，内容随底层更新
	 */
	public LauncherLoaderHelper getLauncherHelper(){
		return null;
	}
	
	/**
	 * 初始化基础目录
	 */
	public void createDefaultDir(){
		
	}
	
	/**
	 * 初始化LauncherModel
	 * @param iconCache
	 * @return
	 */
	public BaseLauncherModel createLauncherModel(BaseIconCache iconCache){
		return new BaseLauncherModel(this, iconCache);
	}
	
	/**
	 * 初始化IconCache
	 * @param mContext
	 * @return
	 */
	public BaseIconCache createIconCache(Context mContext) {
		return new BaseIconCache(mContext);
	}

	/**
	 * 初始化异常捕获
	 */
	public void initCrashHandler() {

	}
	
	/**
	 * 加载主题数据
	 */
	public void loadThemeIntentData(){
		BaseThemeData.getInstance().buildThemeData();
		ThemeIconIntentAdaptation.getInstance().loadThemeIntentData();
		ThemeManagerFactory.getInstance().setThemeManagerHelper(new ThemeManagerHelper());
	}
}
