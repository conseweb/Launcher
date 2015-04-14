/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.nd.hilauncherdev.launcher.info;

import com.nd.hilauncherdev.launcher.model.BaseLauncherSettings;

import android.content.ContentValues;

/**
 * Represents an item in the launcher.
 */
public class ItemInfo {
	
	public static final int CONTAINER_DRAWER = 0;
	
	protected static final String BLANK = "";
    
	public static final int NO_ID = -1;
    
    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;
    
    /**
     * One of {@link BaseLauncherSettings.Favorites#ITEM_TYPE_APPLICATION},
     * {@link BaseLauncherSettings.Favorites#ITEM_TYPE_SHORTCUT},
     * {@link BaseLauncherSettings.Favorites#ITEM_TYPE_USER_FOLDER}, or
     * {@link BaseLauncherSettings.Favorites#ITEM_TYPE_APPWIDGET}.
     */
    public int itemType;
    
    /**
     * The id of the container that holds this item. For the desktop, this will be 
     * {@link BaseLauncherSettings.Favorites#CONTAINER_DESKTOP}. For the all applications folder it
     * will be {@link #NO_ID} (since it is not stored in the settings DB). For user folders
     * it will be the id of the folder.
     */
    public long container = NO_ID;
    
    /**
     * Iindicates the screen in which the shortcut appears.
     */
    public int screen = -1;
    
    /**
     * Indicates the X position of the associated cell.
     */
    public int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    public int cellY = -1;

    /**
     * Indicates the X cell span.
     */
    public int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    public int spanY = 1;

    /**
     * Indicates whether the item is a gesture.
     */
//    boolean isGesture = false;
    
    public ItemInfo() {
    	
    }

    public ItemInfo(ItemInfo info) {
        id = info.id;
        cellX = info.cellX;
        cellY = info.cellY;
        spanX = info.spanX;
        spanY = info.spanY;
        screen = info.screen;
        itemType = info.itemType;
        container = info.container;
    }

    /**
     * Write the fields of this item to the DB
     * 
     * @param values
     */
    public void onAddToDatabase(ContentValues values) { 
        values.put(BaseLauncherSettings.BaseLauncherColumns.ITEM_TYPE, itemType);
        values.put(BaseLauncherSettings.Favorites.CONTAINER, container);
        values.put(BaseLauncherSettings.Favorites.SCREEN, screen);
        values.put(BaseLauncherSettings.Favorites.CELLX, cellX);
        values.put(BaseLauncherSettings.Favorites.CELLY, cellY);
        values.put(BaseLauncherSettings.Favorites.SPANX, spanX);
        values.put(BaseLauncherSettings.Favorites.SPANY, spanY);
            
    }

    @Override
    public String toString() {
        return "Item(id=" + this.id + " type=" + this.itemType + ")";
    }
    
    public ItemInfo copy() {
    	return new ItemInfo(this);
    }
}
