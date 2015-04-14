package com.nd.hilauncherdev.launcher.edit;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.hilauncherdev.launcher.edit.data.LauncherEditEffectItemInfo;
import com.nd.hilauncherdev.launcher.edit.data.LauncherEditThemeItemInfo;
import com.nd.hilauncherdev.launcher.edit.data.LauncherEditWallpaperItemInfo;
import com.nd.hilauncherdev.widget.LauncherWidgetInfo;

/**
 * 帮助类
 * @author wgm
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
