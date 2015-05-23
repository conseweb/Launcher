package com.bitants.launcherdev.datamodel;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.common.launcher.config.BaseConfig;

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
	 */
	public static boolean isShowDockbarText() {
		return true;
	}
	
}
