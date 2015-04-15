package com.bitants.launcherdev.folder.model;

import android.view.View;

/**
 * 文件夹动画接口
 * @author pdw
 */
public interface FolderAnimationListener{
	/**
	 * 文件夹打开动画结束接口</br>
	 * Create On 2014-7-1上午11:32:53 </br>
	 * Author : pdw </br>
	 * @param view 文件夹图标
	 * void
	 */
	public void onOpenAnimationEnd(View view) ;
	
	/**
	 * 文件夹关闭动画结束接口 </br>
	 * Create On 2014-7-1上午11:33:48 </br>
	 * Author : pdw </br>
	 * @param view 文件夹图标
	 * void
	 */
	public void onCloseAnimationEnd(View view) ;
}
