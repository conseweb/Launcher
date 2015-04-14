package com.nd.hilauncherdev.datamodel;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.nd.hilauncherdev.launcher.Launcher;
import com.nd.hilauncherdev.launcher.config.BaseConfig;

public class Global extends BaseConfig {

	/**
	 * 全局文件夹底板
	 */
	public static Bitmap folderBackground;
	/**
	 * 全局文件夹关闭时遮罩
	 */
	public static Bitmap folderForegroundClosed;
	/**
	 * 全局文件夹打开时遮罩
	 */
	public static Bitmap folderForegroundOpen;
	/**
	 * 文件夹动画绘制背景
	 */
	public static Drawable folderAniDrawable;

	public static Launcher getLauncher(){
		if(mLauncher == null)
			return null;
		
		return (Launcher) mLauncher;
	}

	/**
	 * Description: dockbar是否显示应用的名称
	 * Author: guojy
	 * Date: 2013-8-30 下午1:42:42
	 */
	public static boolean isShowDockbarText() {
		return true;
	}
	
}
