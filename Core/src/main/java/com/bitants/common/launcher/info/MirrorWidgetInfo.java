package com.bitants.common.launcher.info;

import com.bitants.common.launcher.model.BaseLauncherSettings;

import android.content.ContentValues;

/**
 * 小部件
 */
public class MirrorWidgetInfo extends WidgetInfo {
	public String layoutResString;
	
	/**
	 * 小部件是否捕获上下划动手势（用于屏蔽桌面的上下手势）
	 */
	public boolean isCatchVerticalGesture = false;

	public MirrorWidgetInfo() {
		this.itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET;
		this.container = BaseLauncherSettings.Favorites.CONTAINER_DESKTOP;
	}

	public MirrorWidgetInfo(MirrorWidgetInfo info) {
		super(info);
		layoutResString = info.layoutResString;
		isCatchVerticalGesture = info.isCatchVerticalGesture;
	}

	public MirrorWidgetInfo(MirrorWidgetPreviewInfo info) {
		this.id = info.id;
		this.screen = info.screen;
		this.cellX = info.cellX;
		this.cellY = info.cellY;
		this.spanX = info.spanX;
		this.spanY = info.spanY;
		this.pandaWidgetPackage = info.pandaWidgetPackage;
		this.itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET;
		this.container = BaseLauncherSettings.Favorites.CONTAINER_DESKTOP;
	}

	@Override
	public void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);
		values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_RESOURCE, layoutResString);
	}

	public MirrorWidgetInfo copy() {
		return new MirrorWidgetInfo(this);
	}
}
