package com.bitants.common.launcher.info;

import com.bitants.common.launcher.model.BaseLauncherSettings;

import android.content.ContentValues;

/**
 * 91小部件
 */
public class PandaWidgetPreviewInfo extends WidgetInfo {
	public int iconRes;
	public String layoutXml;

	public PandaWidgetPreviewInfo() {
		this.itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_PANDA_PREVIEW_WIDGET;
		this.container = BaseLauncherSettings.Favorites.CONTAINER_DESKTOP;
	}
	
	public PandaWidgetPreviewInfo(PandaWidgetPreviewInfo info) {
    	super(info);
    	iconRes = info.iconRes;
    	layoutXml = info.layoutXml;
    }

	@Override
	public void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);
		values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_RESOURCE, layoutXml);
		values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE, iconRes);
	}
	
	public PandaWidgetPreviewInfo copy() {
    	return new PandaWidgetPreviewInfo(this);
    }

}
