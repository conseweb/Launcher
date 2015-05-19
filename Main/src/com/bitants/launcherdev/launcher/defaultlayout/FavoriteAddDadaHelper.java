package com.bitants.launcherdev.launcher.defaultlayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import com.bitants.launcherdev.app.CustomIntent;
import com.bitants.launcherdev.kitset.util.BitmapUtils;
import com.bitants.launcherdev.launcher.LauncherProvider;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;

import java.net.URISyntaxException;

/**
 * 新安装桌面 添加桌面数据时调用 
 * @author Michael
 *
 */
class FavoriteAddDadaHelper {
	
	static boolean addUriDockShortcut2Db(Context mContext, SQLiteDatabase db, ContentValues values, String uri, String iconPackage, String iconResource, int iconResId, int titleResId) {
		Resources r = mContext.getResources();
		Bitmap bitmap = null;
		Intent intent;
		try {
			intent = Intent.parseUri(uri, 0);
		} catch (URISyntaxException e) {
			return false; // Oh well
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		values.put(Favorites.INTENT, intent.toUri(0));
		values.put(Favorites.TITLE, r.getString(titleResId));
		values.put(Favorites.CONTAINER, Favorites.CONTAINER_DOCKBAR);
		if (CustomIntent.ACTION_OPEN_DRAWER.equals(intent.getAction())) {
			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_INDEPENDENCE);
		} else if (intent.getComponent() == null) {
			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_CUSTOM_INTENT);
		} else {
			values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_SHORTCUT);
		}
		values.put(Favorites.ICON_TYPE, Favorites.ICON_TYPE_RESOURCE);
		values.put(Favorites.ICON_PACKAGE, iconPackage);
		values.put(Favorites.ICON_RESOURCE, iconResource);
		if (iconResId != 0) {
			values.put(Favorites.ICON_TYPE, Favorites.ICON_TYPE_BITMAP);
			bitmap = BitmapUtils.createIconBitmapThumbnail(r.getDrawable(iconResId), mContext);
			if (bitmap != null) {
				values.put(Favorites.ICON, BitmapUtils.bitmap2Bytes(bitmap));
			}
		}

		db.insert(LauncherProvider.TABLE_FAVORITES, null, values);
		return true;
	}
}
