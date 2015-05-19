package com.bitants.launcherdev.launcher.edit.data;

import android.content.Context;
import com.bitants.launcher.R;



public class LauncherEditAddItemInfo extends LauncherEditItemInfo {
	
	/**
	 * 在线小部件
	 */
	public static final int TYPE_WIDGET_SHOW_MORE = 1;
	
	/**
	 * 系统小部件
	 */
	public static final int TYPE_WIDGET_SYSTEM = 2;
	
	/**
	 * 快捷小部件
	 */
	public static final int TYPE_WIDGET_SHORTCUT = 3;
	
	/**
	 * 应用列表
	 */
	public static final int TYPE_ADD_APPLIST = 3;
	
	/**
	 * 显示系统小部件
	 * 
	 * @return
	 */
	public static LauncherEditAddItemInfo makeWidgetSystemItemInfo(Context context) {
		LauncherEditAddItemInfo info = new LauncherEditAddItemInfo();
		info.title = context.getString(R.string.launcher_edit_add_system_widget);
		info.icon = context.getResources().getDrawable(R.drawable.edit_mode_widget_system);
		info.type = TYPE_WIDGET_SYSTEM;
		return info;
	}
	
	/**
	 * 添加应用列表
	 */
	public static LauncherEditAddItemInfo makeAddAppslistItemInfo(Context context) {
		LauncherEditAddItemInfo info = new LauncherEditAddItemInfo();
		info.title = context.getString(R.string.dockbar_dock_drawer);
		info.icon = context.getResources().getDrawable(R.drawable.main_dock_allapps);
		info.type = TYPE_ADD_APPLIST;
		return info;
	}
	
}
