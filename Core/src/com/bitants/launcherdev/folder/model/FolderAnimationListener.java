package com.bitants.launcherdev.folder.model;

import android.view.View;

/**
 * 文件夹动画接口
 */
public interface FolderAnimationListener{
	/**
	 * 文件夹打开动画结束接口</br>
	 * @param view 文件夹图标
	 * void
	 */
	public void onOpenAnimationEnd(View view) ;
	
	/**
	 * 文件夹关闭动画结束接口 </br>
	 * @param view 文件夹图标
	 * void
	 */
	public void onCloseAnimationEnd(View view) ;
}
