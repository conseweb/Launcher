package com.bitants.launcherdev.launcher.edit;

import com.bitants.common.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditEffectItemInfo;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditThemeItemInfo;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditWallpaperItemInfo;
import com.bitants.launcherdev.widget.LauncherWidgetInfo;

/**
 * 帮助类
 */
public class LauncherEditHelper {

	public static boolean isSystemWidgetItem(ICommonDataItem item) {
		return (item instanceof LauncherWidgetInfo) &&
				((LauncherWidgetInfo)item).getType() == LauncherWidgetInfo.TYPE_SYSTEM;
	}
	
	public static boolean isThemeItem(ICommonDataItem item) {
		return item instanceof LauncherEditThemeItemInfo;
	}
	
	public static boolean isWallpaperItem(ICommonDataItem item) {
		return item instanceof LauncherEditWallpaperItemInfo;
	}
	
	public static boolean isEffectItem(ICommonDataItem item) {
		return item instanceof LauncherEditEffectItemInfo;
	}	
	
}
