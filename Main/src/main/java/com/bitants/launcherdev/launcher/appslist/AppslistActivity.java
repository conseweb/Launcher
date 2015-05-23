package com.bitants.launcherdev.launcher.appslist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.bitants.launcher.R;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.LauncherSettings;
import com.bitants.launcherdev.launcher.appslist.utils.CellLayoutItem;
import com.bitants.launcherdev.launcher.appslist.view.AllappsListview;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.common.theme.assit.BaseThemeAssit;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class AppslistActivity extends Activity {
	private AllappsListview appLinearLayout;
	private Launcher launcher;
	public static int AppstextSize = 20;
	public static int Apppadding = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		launcher = Global.getLauncher();
		launcher.invisiableWorkspace();
		setContentView(R.layout.dx_appslist_content);
		appLinearLayout = (AllappsListview) this.findViewById(R.id.appLinear);
		appLinearLayout.setApps(loadFavoritesData(), getRecentUsingApps());
		AppstextSize = ScreenUtil.sp2px(this.getApplicationContext(), 12);
		Apppadding = ScreenUtil.sp2px(this.getApplicationContext(), 6);

	}

	public ArrayList<CellLayoutItem> getRecentUsingApps() {
		ArrayList<CellLayoutItem> appLists = new ArrayList<CellLayoutItem>();
		ArrayList<CellLayoutItem> allappLists = loadFavoritesData();
		for (int i = 0; i < 4; i++) {
			appLists.add(allappLists.get(i));
		}
		return appLists;
	}

	private ArrayList<CellLayoutItem> loadFavoritesData() {
		ArrayList<CellLayoutItem> appLists = new ArrayList<CellLayoutItem>();
		final ContentResolver contentResolver = getContentResolver();
		Cursor c;
		try {
			c = contentResolver.query(LauncherSettings.Favorites.getContentUri(), null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return appLists;
		}
		if (null == c)
			return appLists;
		try {
			final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
			final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
			final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
			final int iconTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_TYPE);
			final int iconIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
			final int iconPackageIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_PACKAGE);
			final int iconResourceIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE);
			final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
			Context context = this.getApplicationContext();
			String intentDescription;
			long id;
			Intent intent;
			BaseLauncherModel mModel = launcher.getLauncherModel();
			final PackageManager manager = context.getPackageManager();
			ApplicationInfo appInfo;
			CellLayoutItem cellItem;
			while (c.moveToNext()) {
				try {
					int itemType = c.getInt(itemTypeIndex);
					switch (itemType) {
					case LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
					case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT:
						id = c.getLong(idIndex);
						intentDescription = c.getString(intentIndex);
						if (intentDescription == null) {
							contentResolver.delete(LauncherSettings.Favorites.getContentUri(id, false), null, null);
							continue;
						}
						try {
							intent = Intent.parseUri(intentDescription, 0);
						} catch (URISyntaxException e) {
							continue;
						}
						if (itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT
								|| itemType == LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE) {
							appInfo = mModel.getShortcutInfo(c, context, iconTypeIndex, iconPackageIndex, iconResourceIndex, iconIndex, titleIndex, itemType);
							if (appInfo != null) {
								appInfo.intent = intent;
								String themeKey = ThemeIconIntentAdaptation.getDefaultDockAppThemeKey(appInfo.intent.toUri(0));
								if (themeKey != null) {
									appInfo.intent.removeExtra("sourceBounds");
									appInfo.iconBitmap = BaseThemeAssit.getDefaultDockAppIcon(context, themeKey, appInfo);

								}
							}
						} else {
							appInfo = mModel.getApplicationInfo(manager, intent, context, c, iconIndex, titleIndex);
							if (appInfo != null) {
								appInfo.intent = intent;
								BaseConfig.getIconCache().getTitleAndIcon(appInfo);
							}
						}
						if (appInfo != null) {
							cellItem = new CellLayoutItem();
							cellItem.setAppInfo(appInfo);
							cellItem.setIocn(new BitmapDrawable(context.getResources(),appInfo.iconBitmap));
							appLists.add(cellItem);
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {

		}
		return appLists;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		launcher.visiableWorkspace();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
