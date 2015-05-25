package com.bitants.launcherdev.launcher;

import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitants.common.launcher.BaseLauncher;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.info.WidgetInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.model.load.Callbacks;
import com.bitants.common.launcher.model.load.DeferredHandler;
import com.bitants.common.launcher.model.load.LauncherLoader;
import com.bitants.common.launcher.model.load.LauncherLoaderHelper;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.support.BaseIconCache;
import com.bitants.common.launcher.support.BaseLauncherViewHelper;
import com.bitants.common.launcher.touch.DropTarget;
import com.bitants.common.launcher.view.icon.ui.impl.IconMaskTextView;
import com.bitants.launcherdev.widget.LauncherWidgetInfo;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.e;

public class LauncherLoaderHelperImpl extends LauncherLoaderHelper{

	private static LauncherLoaderHelperImpl instance;
	
	private LauncherLoaderHelperImpl(){};
	
	public static LauncherLoaderHelperImpl getInstance(){
		if(instance == null){
			instance = new LauncherLoaderHelperImpl();
		}
		return instance;
	}
	
	@Override
	public boolean loadFavoritesDataFromDB(Context context, LauncherLoader loader,
			BaseLauncherModel mModel) {
		final String TAG = "loadData";
		final ContentResolver contentResolver = context.getContentResolver();
		final PackageManager manager = context.getPackageManager();
		
		Cursor c;
		try {
			c = contentResolver.query(LauncherSettings.Favorites.getContentUri(), null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (null == c)
			return false;

		final ArrayList<Long> itemsToRemove = new ArrayList<Long>();
		try {
			final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
			final int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
			final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
			final int iconTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_TYPE);
			final int iconIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
			final int iconPackageIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_PACKAGE);
			final int iconResourceIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE);
			final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
			final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
			final int appWidgetIdIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_ID);
			final int screenIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
			
			final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
			final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
			final int spanXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANX);
			final int spanYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANY);
			
//			final int uriIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.URI);
//			final int displayModeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.DISPLAY_MODE);

			ApplicationInfo appInfo;
			String intentDescription;
			WidgetInfo appWidgetInfo;
			int container;
			long id;
			Intent intent;

			while (!loader.isStopped() && c.moveToNext()) {
				try {
					int itemType = c.getInt(itemTypeIndex);

					switch (itemType) {
//					case LauncherSettings.Favorites.ITEM_TYPE_ANYTHING:
//						AnythingInfo anythinginfo = new AnythingInfo();
//						anythinginfo.id = c.getLong(idIndex);
//						container = c.getInt(containerIndex);
//						anythinginfo.container = container;
//						anythinginfo.screen = c.getInt(screenIndex);
//						anythinginfo.cellX = c.getInt(cellXIndex);
//						anythinginfo.cellY = c.getInt(cellYIndex);
//						anythinginfo.spanX = c.getInt(spanXIndex);
//						anythinginfo.spanY = c.getInt(spanYIndex);
//						anythinginfo.flag = c.getInt(iconTypeIndex);
//						anythinginfo.itemType = itemType;
//						loader.addToItemsList(anythinginfo);
//						break;
					case LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
					case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT:
					case LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE:
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
						}
//						else if (itemType == LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION) {
//							ApplicationInfo app = MyPhoneDataFactory.getAppFromIntent(context, intent);
//							appInfo = new ApplicationInfo(app);
//						} 
						else {
							appInfo = mModel.getApplicationInfo(manager, intent, context, c, iconIndex, titleIndex);
						}

						if (appInfo != null) {
							mModel.updateSavedIcon(context, appInfo, c, iconIndex);

							appInfo.intent = intent;
							appInfo.id = c.getLong(idIndex);
							container = c.getInt(containerIndex);
							appInfo.container = container;
							appInfo.screen = c.getInt(screenIndex);

							appInfo.cellX = c.getInt(cellXIndex);
							appInfo.cellY = c.getInt(cellYIndex);
							appInfo.spanX = c.getInt(spanXIndex);
							appInfo.spanY = c.getInt(spanYIndex);
							// check & update map of what's occupied
							// if (!checkItemPlacement(occupied, appInfo)) {
							// break;
							// }

							switch (container) {
							case LauncherSettings.Favorites.CONTAINER_DESKTOP:
								loader.addToItemsList(appInfo);
								break;
							case LauncherSettings.Favorites.CONTAINER_DOCKBAR:
//								mDockItems.add(appInfo);
								if (appInfo.screen == 1)
									loader.getCurrentDockitems().add(appInfo);
								else
									loader.getDockitems().add(appInfo);
								break;
							default:
								// Item is in a user folder
								FolderInfo folderInfo = mModel.findOrMakeUserFolder(loader.getFolders(), container);
								folderInfo.add(appInfo);
								break;
							}
						} else {
							// Failed to load the shortcut, probably
							// because the
							// activity manager couldn't resolve it
							// (maybe the app
							// was uninstalled), or the db row was
							// somehow screwed up.
							// Delete it.
							Log.e("Loader", "Error loading shortcut " + id + ", removing it");
							contentResolver.delete(LauncherSettings.Favorites.getContentUri(id, false), null, null);
						}
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
					case LauncherSettings.Favorites.ITEM_TYPE_THEME_APP_FOLDER:
					case LauncherSettings.Favorites.ITEM_TYPE_LAUNCHER_RECOMMEND_FOLDER:
						id = c.getLong(idIndex);
						FolderInfo folderInfo = mModel.findOrMakeUserFolder(loader.getFolders(), id);
						folderInfo.itemType = itemType;

						folderInfo.title = c.getString(titleIndex);

						folderInfo.id = id;
						container = c.getInt(containerIndex);
						folderInfo.container = container;
						folderInfo.screen = c.getInt(screenIndex);

						folderInfo.cellX = c.getInt(cellXIndex);
						folderInfo.cellY = c.getInt(cellYIndex);
						folderInfo.spanX = c.getInt(spanXIndex);
						folderInfo.spanY = c.getInt(spanYIndex);
						
//						if (FolderEncriptHelper.getNewInstance().isFolderEncript(id, FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER)) {
//							folderInfo.isEncript = true;
//						}

						// check & update map of what's occupied
//						if (!checkItemPlacement(occupied, folderInfo)) {
//							break;
//						}

						switch (container) {
						case LauncherSettings.Favorites.CONTAINER_DESKTOP:
							loader.addToItemsList(folderInfo);
							break;
						case LauncherSettings.Favorites.CONTAINER_DOCKBAR:
							if (folderInfo.screen == 1)
								loader.getCurrentDockitems().add(folderInfo);
							else
								loader.getDockitems().add(folderInfo);
							break;
						}

						loader.getFolders().put(folderInfo.id, folderInfo);
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
					case LauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET:
					case LauncherSettings.Favorites.ITEM_TYPE_PANDA_PREVIEW_WIDGET:
						// Read all Launcher-specific widget details
						appWidgetInfo = mModel.getAppWidgetInfo(itemType, c, appWidgetIdIndex, iconPackageIndex, iconResourceIndex, iconTypeIndex, titleIndex);
						appWidgetInfo.id = c.getLong(idIndex);
						appWidgetInfo.screen = c.getInt(screenIndex);
						appWidgetInfo.cellX = c.getInt(cellXIndex);
						appWidgetInfo.cellY = c.getInt(cellYIndex);
						appWidgetInfo.spanX = c.getInt(spanXIndex);
						appWidgetInfo.spanY = c.getInt(spanYIndex);

						container = c.getInt(containerIndex);
						if (container != LauncherSettings.Favorites.CONTAINER_DESKTOP) {
							Log.e(TAG, "Widget found where container " + "!= CONTAINER_DESKTOP -- ignoring!");
							continue;
						}
						appWidgetInfo.container = c.getInt(containerIndex);

						// check & update map of what's occupied
//						if (!checkItemPlacement(occupied, appWidgetInfo)) {
//							break;
//						}

						loader.addToAppWidgetList(appWidgetInfo);
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_WIDGET_SEARCH_SHORTCUT:
						LauncherWidgetInfo launchewidgetinfo=LauncherWidgetInfo.makeClockWidgetInfo();
						launchewidgetinfo.id=c.getLong(idIndex);
						launchewidgetinfo.container=c.getInt(containerIndex);
						launchewidgetinfo.screen=c.getInt(screenIndex);
						launchewidgetinfo.cellX= c.getInt(cellXIndex);
						launchewidgetinfo.cellY=c.getInt(cellYIndex);
						loader.addToItemsList(launchewidgetinfo);
						break;
					default:
						// LauncherAppWidgetInfo appwidgetInfo =
						// LauncherWidgetHelper.createWidgetInfo(itemType);
						// if (appwidgetInfo == null)
						// break;
						WidgetInfo appwidgetInfo = new WidgetInfo();
						appwidgetInfo.itemType = itemType;

						container = c.getInt(containerIndex);
						if (container != LauncherSettings.Favorites.CONTAINER_DESKTOP) {
							e(TAG, "Widget found where container " + "!= CONTAINER_DESKTOP  ignoring!");
							continue;
						}
						appwidgetInfo.id = c.getLong(idIndex);
						appwidgetInfo.screen = c.getInt(screenIndex);
						appwidgetInfo.container = container;
						appwidgetInfo.cellX = c.getInt(cellXIndex);
						appwidgetInfo.cellY = c.getInt(cellYIndex);
						appwidgetInfo.spanX = c.getInt(spanXIndex);
						appwidgetInfo.spanY = c.getInt(spanYIndex);
						appwidgetInfo.appWidgetId = c.getInt(appWidgetIdIndex);
//						if (!checkItemPlacement(occupied, appwidgetInfo)) {
//							break;
//						}

						loader.addToAppWidgetList(appwidgetInfo);
						break;
					}
				} catch (Exception e) {
					Log.w(TAG, "Desktop items loading interrupted:", e);
				}
			}
		} finally {
			c.close();
		}

		if (itemsToRemove.size() > 0) {
			ContentProviderClient client = contentResolver.acquireContentProviderClient(LauncherSettings.Favorites.getContentUri());
			// Remove dead items
			for (long id : itemsToRemove) {
				// Don't notify content observers
				try {
					client.delete(LauncherSettings.Favorites.getContentUri(id, false), null, null);
				} catch (RemoteException e) {
					Log.w(TAG, "Could not remove id = " + id);
				}
			}
		}
		
		return true;
	}

	@Override
	public void bindItems(List<ItemInfo> shortcuts, int start, int end,
			BaseLauncher mLauncher, ScreenViewGroup workspace,
			BaseMagicDockbar mDockbar) {
		for (int i = start; i < end && i < shortcuts.size(); i++) {
			final ItemInfo item = shortcuts.get(i);
			switch (item.itemType) {
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION:
				final View appShortcut;
				if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					appShortcut = mLauncher.createCommonAppView((ApplicationInfo) item);
					workspace.addInScreen(appShortcut, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
				} else if (item.container == LauncherSettings.Favorites.CONTAINER_DOCKBAR) {
					appShortcut = BaseLauncherViewHelper.createDockShortcut(mLauncher, (ApplicationInfo) item);
					mDockbar.addInDockbar(appShortcut, item.screen, item.cellX, item.cellY, item.spanX, item.spanY, false);
				}
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT:
			case LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE:
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
				final View shortcut;
				if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					shortcut = mLauncher.createCommonAppView((ApplicationInfo) item);
					workspace.addInScreen(shortcut, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
				} else if (item.container == LauncherSettings.Favorites.CONTAINER_DOCKBAR) {
					shortcut = BaseLauncherViewHelper.createDockShortcut(mLauncher, (ApplicationInfo) item);
					mDockbar.addInDockbar(shortcut, item.screen, item.cellX, item.cellY, item.spanX, item.spanY, false);
				}
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
			case LauncherSettings.Favorites.ITEM_TYPE_THEME_APP_FOLDER:
			case LauncherSettings.Favorites.ITEM_TYPE_LAUNCHER_RECOMMEND_FOLDER:
				final View newFolder;
				if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					newFolder = BaseLauncherViewHelper.createFolderIconTextView(mLauncher, (FolderInfo) item); 
					workspace.addInScreen(newFolder, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
				}else{
					newFolder = BaseLauncherViewHelper.createFolderIconTextViewFromContext(mLauncher, (FolderInfo) item);
					mDockbar.addInDockbar(newFolder, item.screen, item.cellX, item.cellY, item.spanX, item.spanY, false);
				}
				
				break;
				
			case LauncherSettings.Favorites.ITEM_TYPE_WIDGET_SEARCH_SHORTCUT:

				View view = mLauncher.createWidgetFqFromXML((LauncherWidgetInfo)item);
				if (view == null)
					return;
				workspace.addInScreen(view, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
				break;
			default:
//				final View view = BaseLauncherViewHelper.createAnythingAppView(mLauncher, item);
//				if (view != null)
//					workspace.addInScreen(view, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
			}
		}
	}

	@Override
	public void updateAppsInWorkspace(List<ApplicationInfo> apps, BaseLauncher mLauncher) {
		if (apps == null)
			return;
		
		try {
//			LauncherApplication application = (LauncherApplication) mLauncher.getApplicationContext();
//			IconCache mIconCache = application.getIconCache();
			BaseIconCache mIconCache = BaseConfig.getIconCache();
//			final Workspace mWorkspace = mLauncher.mWorkspace;
			final ScreenViewGroup mWorkspace = mLauncher.getScreenViewGroup();
			final int count = mWorkspace.getChildCount();
			final ArrayList<View> childrenToRemove = new ArrayList<View>();
			for (int i = 0; i < count; i++) {
				final CellLayout layout = (CellLayout) mWorkspace.getChildAt(i);
				int childCount = layout.getChildCount();
				int updateCount = 0;
				for (int j = 0; j < childCount; j++) {
					final View view = layout.getChildAt(j);
					Object tag = view.getTag();
					if (tag == null)
						continue;

					if (tag instanceof ApplicationInfo) {
						ApplicationInfo info = (ApplicationInfo) tag;
						final Intent intent = info.intent;
						if (intent == null || intent.getComponent() == null)
							continue;

						final ComponentName name = intent.getComponent();
						if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
							final int appCount = apps.size();
							for (int k = 0; k < appCount; k++) {
								ApplicationInfo app = apps.get(k);
								if (app.componentName.equals(name)) {
									info.iconBitmap = mIconCache.refreshTheCache(info);
									//info.iconBitmap = mIconCache.getIcon(info);
									((IconMaskTextView) view).setIconBitmap(info.iconBitmap);
									view.invalidate();
									updateCount++;
								} else if (app.componentName.getPackageName().equals(name.getPackageName())) {// 覆盖安装后应用的入口类名匹配不上
									if (1 == appCount) {// 若应用只匹配出一个入口，则把图标更新为该入口
										info.setActivity(app.componentName);
										info.iconBitmap = mIconCache.getIcon(info);
										((IconMaskTextView) view).setIconBitmap(info.iconBitmap);
										view.invalidate();
										BaseLauncherModel.updateItemInDatabase(mLauncher, info);
										updateCount++;
									} else {// 匹配出多个入口，则删除该图标 caizp 2013-4-2
										BaseLauncherModel.deleteItemFromDatabase(mLauncher, info);
										childrenToRemove.add(view);
									}
								}
							}
						}
					} else if (tag instanceof FolderInfo) {
						final FolderInfo info = (FolderInfo) tag;
						final List<ApplicationInfo> contents = info.contents;
						ArrayList<ApplicationInfo> toRemove = null;
						final int contentsCount = contents.size();

						for (int k = 0; k < contentsCount; k++) {
							final ApplicationInfo applicationInfo = contents.get(k);
							final Intent intent = applicationInfo.intent;
							if (intent == null || intent.getComponent() == null)
								continue;

							final ComponentName name = intent.getComponent();

							if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
								final int appCount = apps.size();
								for (int m = 0; m < appCount; m++) {
									ApplicationInfo app = apps.get(m);
									if (app.componentName.equals(name)) {
										applicationInfo.iconBitmap = mIconCache.refreshTheCache(applicationInfo);
										//applicationInfo.iconBitmap = mIconCache.getIcon(applicationInfo);
										view.invalidate();
									} else if (app.componentName.getPackageName().equals(name.getPackageName())) {// 覆盖安装后应用的入口类名匹配不上
										if (1 == appCount) {// 若应用只匹配出一个入口，则把图标更新为该入口
											applicationInfo.setActivity(app.componentName);
											applicationInfo.iconBitmap = mIconCache.getIcon(applicationInfo);
											view.invalidate();
											BaseLauncherModel.updateItemInDatabase(mLauncher, applicationInfo);
										} else {// 匹配出多个入口，则删除该图标 caizp 2013-4-2
											BaseLauncherModel.deleteItemFromDatabase(mLauncher, applicationInfo);
											if (toRemove == null)
												toRemove = new ArrayList<ApplicationInfo>();
											
											toRemove.add(applicationInfo);
										}
									}
								}
							}
						}
						
						if (toRemove != null)
							contents.removeAll(toRemove);
					}
				}

				childCount = childrenToRemove.size();
				for (int j = 0; j < childCount; j++) {
					View child = childrenToRemove.get(j);
					layout.removeViewInLayout(child);
					if (child instanceof DropTarget) {
						mLauncher.getDragController().removeDropTarget((DropTarget) child);
					}
				}
				if (childCount > 0 || updateCount > 0) {
					layout.requestLayout();
					layout.invalidate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(mLauncher, "Updating workspace is someting wrong:)", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public CellLayout.LayoutParams newCellLayoutLayoutParams(ViewGroup.MarginLayoutParams vm){
		 return new CellLayout.LayoutParams(vm);
	}
	
	@Override
	public boolean isShowDockbarText() {
		return Global.isShowDockbarText();
	}

	@Override
	public void initHiAnalytics(Context mContext) {
		
	}

	@Override
	public void startUpHiAnalytics(Context mContext) {
	}

	@Override
	public void loadAndBindAllApps(Callbacks callback, DeferredHandler handler,
			Context mContext) {
	}

	@Override
	public boolean isNewInstallApp(ApplicationInfo info){
		//TODO 处理显示新安装flag的逻辑
		return false;
	}
}
