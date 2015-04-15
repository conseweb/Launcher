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

package com.bitants.launcherdev.launcher.info;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ContentProviderOperation.Builder;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;

import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.app.SerializableAppInfo;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.support.BaseIconCache;

public class ApplicationInfo extends ItemInfo implements ICommonDataItem {
	
	public static final int APP_TYPE_APP = 0;

	public static final int APP_TYPE_FOLDER = 1;

	/**
	 * The application name.
	 */
	public CharSequence title = BLANK;

	/**
	 * The application name in pinyin
	 */
	public CharSequence pinyin;

	/**
	 * A bitmap version of the application icon.
	 */
	public Bitmap iconBitmap;

	public ComponentName componentName;

	public int pos;

	/**
	 * 0普通应用，1系统应用
	 */
	public int isSystem;

	/**
	 * 0显示，1隐藏
	 */
	public int isHidden;

	public long installTime = 0;

	/**
	 * app使用次数
	 */
	public int used_time = 0;

	public Intent intent;

	/**
	 * 是否懒加载图片
	 */
	public boolean usingFallbackIcon;

	/**
	 * Indicates whether the icon comes from an application's resource (if
	 * false) or from a custom Bitmap (if true.)
	 */
	public boolean customIcon;

	/**
	 * Indicates whether the shortcut is on external storage and may go away at
	 * any time.
	 */
	public boolean onExternalStorage;

	/**
	 * If isShortcut=true and customIcon=false, this contains a reference to the
	 * shortcut icon as an application's resource.
	 */
	public Intent.ShortcutIconResource iconResource;

	/**
	 * 是否使用蒙板(默认使用，有自定义图标或主题图标时不使用)
	 */
	public boolean useIconMask = true;
	
	/**
	 * 匣子中应用程序的container
	 */
	public long drawerContainer = CONTAINER_DRAWER;
	
	/**
	 * 用于打点统计的tag
	 */
	public String statTag;

	public ApplicationInfo() {
		itemType = BaseLauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
	}

	public ApplicationInfo(int type) {
		itemType = type;
	}

	public ApplicationInfo(ApplicationInfo info) {
		super(info);
		title = info.title;
		iconBitmap = info.iconBitmap;
		componentName = info.componentName;
		pos = info.pos;
		installTime = info.installTime;
		intent = info.intent;
		usingFallbackIcon = info.usingFallbackIcon;
		customIcon = info.customIcon;
		onExternalStorage = info.onExternalStorage;
		iconResource = info.iconResource;
		drawerContainer = info.drawerContainer;
		useIconMask = info.useIconMask;
	}

	/**
	 * Must not hold the Context.
	 */
	public ApplicationInfo(ResolveInfo info) {
		this.componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
		this.container = ItemInfo.NO_ID;
		this.setActivity(componentName);
	}

	/**
	 * Must not hold the Context.
	 */
	public ApplicationInfo(ResolveInfo info, BaseIconCache iconCache) {
		this.componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);

		this.container = ItemInfo.NO_ID;
		this.setActivity(componentName);

		iconCache.getTitleAndIcon(this, info);
	}

	public ApplicationInfo(SerializableAppInfo info) {
		title = info.title;
		usingFallbackIcon = true;
		id = info.id;
        if(info.itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT) {
            intent = info.intent;
        }else {
            setActivity(info.intent.getComponent());
        }
		itemType = info.itemType;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof ApplicationInfo))
			return false;

		ApplicationInfo other = (ApplicationInfo) o;
		if (componentName != null) {
			return componentName.equals(other.componentName);
		}else {
            if(intent != null && intent.getAction() != null && itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT) {
                return (itemType == other.itemType) && intent.getAction().equals(other.intent.getAction());
            }
        }

		return id == other.id && itemType == other.itemType;
	}

	public Bitmap getIcon(BaseIconCache iconCache) {
		if (iconBitmap == null) {
			iconBitmap = iconCache.getIcon(this);
		}
		return iconBitmap;
	}

	public ApplicationInfo copy() {
		return new ApplicationInfo(this);
	}
	
	public void onAddToDatabaseEx(Builder builder) {
		if (builder != null) {
			builder.withValue(BaseLauncherSettings.BaseLauncherColumns.ITEM_TYPE, itemType);
			builder.withValue(BaseLauncherSettings.Favorites.CONTAINER, container);
			builder.withValue(BaseLauncherSettings.Favorites.SCREEN, screen);
			String titleStr = title != null ? title.toString() : null;
			builder.withValue(BaseLauncherSettings.BaseLauncherColumns.TITLE,
					titleStr);
			String intentUri = intent != null ? intent.toUri(0) : null;
			builder.withValue(BaseLauncherSettings.BaseLauncherColumns.INTENT,
					intentUri);
			if (customIcon) {
				builder.withValue(
						BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE,
						BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP);
				byte[] data = flattenBitmap(iconBitmap);
				builder.withValue(BaseLauncherSettings.Favorites.ICON, data);
			} else {
				if (onExternalStorage && !usingFallbackIcon) {
					byte[] data = flattenBitmap(iconBitmap);
					builder.withValue(BaseLauncherSettings.Favorites.ICON, data);
				}
				builder.withValue(
						BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE,
						BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
				if (iconResource != null) {
					builder.withValue(
							BaseLauncherSettings.BaseLauncherColumns.ICON_PACKAGE,
							iconResource.packageName);
					builder.withValue(
							BaseLauncherSettings.BaseLauncherColumns.ICON_RESOURCE,
							iconResource.resourceName);
				}
			}
		}

	}
	
	/**
	 * Creates the application intent based on a component name and various
	 * launch flags. Sets {@link #itemType} to
	 * {@link BaseLauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
	 * 
	 * @param className
	 *            the class name of the component representing the intent
	 *
	 */
	public final void setActivity(ComponentName className) {
		this.componentName = className;

		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(className);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		itemType = BaseLauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
	}

	@Override
	public String toString() {
		if (title != null)
			return "ApplicationInfo(title=" + title.toString() + ")";
		else if (componentName != null)
			return componentName.toString();

		return id + "";
	}


	public static void dumpApplicationInfoList(String tag, String label, List<ApplicationInfo> list) {
		Log.d(tag, label + " size=" + list.size());
		for (ApplicationInfo info : list) {
			Log.d(tag, "   title=\"" + info.title + " iconBitmap=" + info.iconBitmap);
		}
	}

	public SerializableAppInfo makeSerializable() {
		return new SerializableAppInfo(this);
	}

	@Override
	public void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);

		String titleStr = title != null ? title.toString() : null;
		values.put(BaseLauncherSettings.BaseLauncherColumns.TITLE, titleStr);

		String uri = intent != null ? intent.toUri(0) : null;
		values.put(BaseLauncherSettings.BaseLauncherColumns.INTENT, uri);

		if (customIcon) {
			values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE, BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP);
			writeBitmap(values, iconBitmap);
			if(!StringUtil.isEmpty(statTag)){//适配推送类型图标，存放推送ID用于统计
				values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_RESOURCE, statTag);
			}
		} else {
			if (onExternalStorage && !usingFallbackIcon) {
				writeBitmap(values, iconBitmap);
			}
			values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE, BaseLauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
			if (iconResource != null) {
				values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_PACKAGE, iconResource.packageName);
				values.put(BaseLauncherSettings.BaseLauncherColumns.ICON_RESOURCE, iconResource.resourceName);
			}
		}
	}

	@Override
	public int getPosition() {
		return pos;
	}

	@Override
	public void setPosition(int position) {
		this.pos = position;
	}

	@Override
	public boolean isFolder() {
		return itemType == APP_TYPE_FOLDER;
	}
	
	static byte[] flattenBitmap(Bitmap bitmap) {
        // Try go guesstimate how much space the icon will take when serialized
        // to avoid unnecessary allocations/copies during the write.
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w("Favorite", "Could not write icon");
            return null;
        }
    }

    static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
            byte[] data = flattenBitmap(bitmap);
            values.put(BaseLauncherSettings.Favorites.ICON, data);
        }
    }
}
