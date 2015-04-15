package com.nd.launcherdev.launcher.model.load;


import com.nd.launcherdev.launcher.BaseLauncher;
import com.nd.launcherdev.launcher.BaseLauncher;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.config.LauncherConfig;
import com.nd.launcherdev.launcher.model.BaseLauncherModel;

import android.content.Context;
import android.util.Log;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.model.BaseLauncherModel;

/**
 * Description: 桌面启动时，异步数据加载 Author: guojy Date: 2012-11-1 下午04:01:35
 */
public class LauncherPreLoader {

	private final String TAG = "LauncherPreLoader";
	
	private LoaderThread mLoaderThread;
	private BaseLauncherModel mModel;
	private LauncherLoaderHelper mLauncherLoaderHelper;
	private Callbacks mCallbacks;
	private Context mContext;
	
	public LauncherPreLoader(Callbacks callbacks, BaseLauncherModel model, Context context) {
		mCallbacks = callbacks;
		mModel = model;
		mLauncherLoaderHelper = LauncherConfig.getLauncherHelper();
		mContext = context;
	}
	
	public void start(){
		mLoaderThread = new LoaderThread();
		mLoaderThread.start();
	}

	/**
	 * Runnable for the thread that loads the contents of the launcher: -
	 * workspace icons - widgets - all apps icons
	 */
	class LoaderThread extends Thread {
		private LauncherLoader mLauncherLoader;
		
		LoaderThread() {
			mLauncherLoader = new LauncherLoader(mModel, mCallbacks, mLauncherLoaderHelper, mContext);
		}

		public void run() {
			Log.e(TAG, "start pre loadWorkspace");
			mLauncherLoader.loadWorkspace(mContext);
			if(BaseConfig.getBaseLauncher() == null)
				return;
			BaseLauncher mLauncher = BaseConfig.getBaseLauncher();
			mLauncher.setHasLoadWorkspace(true);

			if (!mLauncher.hasSetupWorkspace()) {
				return;
			}

			if (mLauncher.allowToBindWorkspace()) {
				Log.e(TAG, "start bindWorkspace");
				mLauncherLoader.bindWorkspace();
			}
		}
	}

}
