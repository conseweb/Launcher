/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bitants.common.launcher.info;

import com.bitants.common.launcher.model.BaseLauncherSettings;

import android.appwidget.AppWidgetHostView;
import android.content.ContentValues;

/**
 * Represents a widget, which just contains an identifier.
 */
public class WidgetInfo extends ItemInfo {

    /**
     * Identifier for this widget when talking with
     * {@link android.appwidget.AppWidgetManager} for updates.
     */
    public int appWidgetId;
    
    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    public AppWidgetHostView hostView = null;
    
    public String pandaWidgetPackage;
	public String title;
	
    public WidgetInfo() {
    	
    }
    
    public WidgetInfo(WidgetInfo info) {
    	super(info);
    	appWidgetId = info.appWidgetId;
    	title = info.title;
    	pandaWidgetPackage = info.pandaWidgetPackage;    	
    	hostView = info.hostView;
    }

    public WidgetInfo(int appWidgetId) {
        itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_APPWIDGET;
        this.appWidgetId = appWidgetId;
    }
    
    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(BaseLauncherSettings.Favorites.APPWIDGET_ID, appWidgetId);
        values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_PACKAGE, pandaWidgetPackage);
		values.put(BaseLauncherSettings.BaseLauncherColumns.TITLE, title);
    }

    @Override
    public String toString() {
        return "AppWidget(id=" + Integer.toString(appWidgetId) + ")";
    }
    
    public WidgetInfo copy() {
    	return new WidgetInfo(this);
    }
}
