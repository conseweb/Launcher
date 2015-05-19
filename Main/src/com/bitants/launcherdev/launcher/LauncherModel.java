package com.bitants.launcherdev.launcher;

import android.content.*;
import android.content.ContentProviderOperation.Builder;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.support.BaseIconCache;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LauncherModel extends BaseLauncherModel {
	static final String TAG = "LauncherModel";

	public LauncherModel(LauncherApplication app, BaseIconCache iconCache) {
		super(app, iconCache);
	}

	/**
	 * 获取文件夹内容（只供推荐文件夹调用）
	 * @author Ryan
	 */
	public static List<ApplicationInfo> getFolderContentById(Context context, long id) {
		List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();
		
		Cursor c = null;
		
		try {
			final ContentResolver cr = context.getContentResolver();
			c = cr.query(LauncherSettings.Favorites.getContentUri(), null, "container=?", new String[] { String.valueOf(id) }, null);
			final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
			final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.TITLE);
			final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns._ID);
			while (c.moveToNext()) {
				String intentDescription = c.getString(intentIndex);
				if (intentDescription == null)
					continue;
				
				Intent intent = null;
				try {
					intent = Intent.parseUri(intentDescription, 0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
					continue;
				}
				
				if (intent.getComponent() == null || intent.getComponent().getPackageName() == null) {
					// 解决“每日新鲜事”等图标在换主题后，从推荐应用文件夹内消失的问题
					int itemType = c.getInt(c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE));
					if (itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT) {
						Launcher launcher = Global.getLauncher();
						if (launcher != null && launcher.getLauncherModel() != null) {
							ApplicationInfo info = launcher.getLauncherModel().getShortcutInfo(c, 
									                                                   context, 
									                                                   c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_TYPE), 
									                                                   c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_PACKAGE), 
									                                                   c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE), 
									                                                   c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON), 
									                                                   titleIndex, 
									                                                   itemType);
							if (info != null) {
								info.intent = intent;
								info.id = c.getInt(idIndex);
								info.container = id;
								result.add(info);
							}
						}
					}
					
					continue;
				}
								
				ApplicationInfo info = new ApplicationInfo();
				info.intent = intent;
				info.componentName= intent.getComponent();
				info.title = c.getString(titleIndex);
				info.id = c.getInt(idIndex);
				info.container = id;
				result.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
		
		return result;
	}
	
	/**
	 * 根据应用类型加载应用项
	 * @author wangguomei
	 */
	public static List<ApplicationInfo> loadItemsByTypeForLocale(Context context) {
		final ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(LauncherSettings.Favorites.getContentUri(), null, "itemType=? or itemType=? or itemType=?", 
				new String[] {String.valueOf(LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT),
				              String.valueOf(LauncherSettings.Favorites.ITEM_TYPE_APPLICATION),
				              String.valueOf(LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE)}, null);
		
		List<ApplicationInfo> items = new ArrayList<ApplicationInfo>(20);
		try {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				ApplicationInfo appInfo = new ApplicationInfo();
				appInfo.id = c.getInt(c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID));
				appInfo.title = c.getString(c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE));
				try {
					appInfo.intent = Intent.parseUri(c.getString(c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT)),0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				appInfo.container = c.getInt(c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER));
				appInfo.itemType = c.getInt(c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE));
				items.add(appInfo);
			}
		} catch (Exception e) {
			Log.e(TAG, "err in loadItemsByTypeForLocale():" + e.toString());
		} finally {
			c.close();
		}
		
		return items;
	}
	
	/**
	 * 批量更新应用项名称
	 * @author Michael
	 */
	public static void batchUpdateItemTitleById(Context context, List<ApplicationInfo> items) {
		
		if (items == null || items.isEmpty())
			return;
		
		ArrayList<ContentProviderOperation> batchOps = new ArrayList<ContentProviderOperation>();

		for (ApplicationInfo applicationInfo : items) {
			Uri uri = LauncherSettings.Favorites.getContentUri(applicationInfo.id, false);
			Builder builder = ContentProviderOperation.newUpdate(uri);
			builder.withValue(LauncherSettings.Favorites.TITLE, applicationInfo.title);
			
			batchOps.add(builder.build());
		}
		
		try {
			context.getContentResolver().applyBatch(Favorites.AUTHORITY, batchOps);
		} catch (RemoteException e) {
			Log.e(TAG, "update database failed",e);
		} catch (OperationApplicationException e) {
			Log.e(TAG, "update database failed",e);
		}
	}
	
	@Override
	public void applyThemeNoWallpaperWithWaitDialog(){
		//TODO 应用主题
	}
	
	/**
	 * 获取DOCK栏 iteminfo
	 * @param mContext
	 * @param screen
	 * @param result
	 */
	public static void addDockbarItem(Context mContext, int screen,List<ItemInfo> result){
		final ContentResolver contentResolver = mContext.getContentResolver();
		Cursor c = null;
		c = contentResolver.query(LauncherSettings.Favorites.getContentUri(), null, null, null, null);
		if (null == c)
			return ;
		//寻找放置位置
		Launcher launcher = Global.getLauncher();
		BaseLauncherModel mModel = launcher.getLauncherModel();
		final PackageManager manager = mContext.getPackageManager();
		Map<Integer, FolderInfo> FolderMap = new LinkedHashMap<Integer, FolderInfo>();
		final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
		final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
		final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
		final int iconTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_TYPE);
		final int iconIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
		final int iconPackageIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_PACKAGE);
		final int iconResourceIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE);
		final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
		final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
		final int screenIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
		
		final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
		final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
		final int spanXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANX);
		final int spanYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANY);
		ApplicationInfo appInfo;
		int container;
		long itemid;
		String intentDescription;
		Intent intent;
		int dockscreen;
		try {
			while (c.moveToNext()) {
				int itemtype = c.getInt(itemTypeIndex);
				switch(itemtype){
					case LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
					case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT:
					case LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE:
						itemid = c.getLong(idIndex);
						intentDescription = c.getString(intentIndex);
						if (intentDescription == null) {
							contentResolver.delete(LauncherSettings.Favorites.getContentUri(itemid, false), null, null);
							continue;
						}
						try {
							intent = Intent.parseUri(intentDescription, 0);
						} catch (URISyntaxException e) {
							continue;
						}
						
						if (itemtype == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || itemtype == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT
								|| itemtype == LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE) {
							appInfo = mModel.getShortcutInfo(c, mContext, iconTypeIndex, iconPackageIndex, iconResourceIndex, iconIndex, titleIndex, itemtype);
						} else {
							appInfo = mModel.getApplicationInfo(manager, intent, mContext, c, iconIndex, titleIndex);
						}

						if (appInfo != null) {
							mModel.updateSavedIcon(mContext, appInfo, c, iconIndex);

							appInfo.intent = intent;
							appInfo.id = c.getLong(idIndex);
							container = c.getInt(containerIndex);
							appInfo.container = container;
							appInfo.screen = c.getInt(screenIndex);

							appInfo.cellX = c.getInt(cellXIndex);
							appInfo.cellY = c.getInt(cellYIndex);
							appInfo.spanX = c.getInt(spanXIndex);
							appInfo.spanY = c.getInt(spanYIndex);
							
							switch (container) {
							case LauncherSettings.Favorites.CONTAINER_DESKTOP:
								break;
							//  dock栏
							case LauncherSettings.Favorites.CONTAINER_DOCKBAR:
								if (appInfo.screen == screen){
									result.add(appInfo);
								}	
								break;
							// 不是 dock栏且不是桌面  （文件夹）
							default:
								FolderInfo FInfo = FolderMap.get(container);
								if (null == FInfo){
									FInfo = new FolderInfo();
									FolderMap.put(container,FInfo);
								}
								FInfo.contents.add(appInfo);
								break;
							}
						} else {
							contentResolver.delete(LauncherSettings.Favorites.getContentUri(itemid, false), null, null);
						}
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
					case LauncherSettings.Favorites.ITEM_TYPE_THEME_APP_FOLDER:
					case LauncherSettings.Favorites.ITEM_TYPE_LAUNCHER_RECOMMEND_FOLDER:
						container = c.getInt(containerIndex);
						itemid =  c.getLong(idIndex);
						dockscreen = c.getInt(screenIndex);
						// dock栏上的文件夹
						if(container == LauncherSettings.Favorites.CONTAINER_DOCKBAR && dockscreen == screen){
							FolderInfo folderInfo = FolderMap.get((int)itemid);
							if (null == folderInfo){
								folderInfo = new FolderInfo();
								FolderMap.put((int)itemid,folderInfo);
							}

							folderInfo.itemType = c.getInt(itemTypeIndex);

							folderInfo.title = c.getString(titleIndex);

							folderInfo.id = itemid;
							container = c.getInt(containerIndex);
							folderInfo.container = container;
							folderInfo.screen = c.getInt(screenIndex);

							folderInfo.cellX = c.getInt(cellXIndex);
							folderInfo.cellY = c.getInt(cellYIndex);
							folderInfo.spanX = c.getInt(spanXIndex);
							folderInfo.spanY = c.getInt(spanYIndex);
							
							result.add(folderInfo);
						}
						break;
						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
	}
}
