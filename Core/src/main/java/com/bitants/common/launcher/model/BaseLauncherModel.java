package com.bitants.common.launcher.model;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.MirrorWidgetInfo;
import com.bitants.common.launcher.info.MirrorWidgetPreviewInfo;
import com.bitants.common.launcher.support.BaseIconCache;
import com.bitants.common.launcher.support.BitmapWeakReferences;
import com.bitants.common.R;
import com.bitants.common.app.BaseAppDataFactory;
import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.common.kitset.util.IconUtils;
import com.bitants.common.kitset.util.MessageUtils;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.BaseLauncherApplication;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.model.BaseLauncherSettings.Favorites;
import com.bitants.common.launcher.model.load.Callbacks;
import com.bitants.common.launcher.model.load.DeferredHandler;
import com.bitants.common.launcher.model.load.LauncherLoader;
import com.bitants.common.launcher.support.FastBitmapDrawable;
import com.bitants.common.theme.pref.ThemeSharePref;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.info.WidgetInfo;

public class BaseLauncherModel extends BroadcastReceiver{
	static final String TAG = "BaseLauncherModel";
	
	static final boolean DEBUG_LOADERS = false;
	static final boolean PROFILE_LOADERS = false;
	
	private final Object mLock = new Object();
	private Loader mLoader = new Loader();
	protected Callbacks mCallbacks;
	protected boolean mBeforeFirstLoad = true;
	protected DeferredHandler mHandler = new DeferredHandler();
	
	protected BaseLauncher mLauncher;
	private Bitmap mDefaultIcon;
	protected BaseIconCache mIconCache;
	protected BaseLauncherApplication mApp;

	private final Object mAllAppsListLock = new Object();
	
	public BaseLauncherModel(BaseLauncherApplication app, BaseIconCache iconCache) {
		mApp = app;
		mIconCache = iconCache;
	}
	
	public void setLauncher(BaseLauncher mLauncher) {
		this.mLauncher = mLauncher;
	}
	
	public void startLoader(Context context, boolean isLaunching, boolean worspaceOnly, boolean isAsync) {
		mLoader.startLoader(context, isLaunching, worspaceOnly, isAsync);
	}

	public void stopLoader() {
		mLoader.stopLoader();
	}
	
	public void initialize(Callbacks callbacks) {
		synchronized (mLock) {
			mCallbacks = callbacks;
		}
	}
	public void dumpState() {
//		Log.d(TAG, "mBeforeFirstLoad=" + mBeforeFirstLoad);
//		Log.d(TAG, "mCallbacks=" + mCallbacks);
		mLoader.dumpState();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Use the app as the context.
		context = mApp;

		boolean removed = false;
		boolean added = false;
		boolean modified = false;

		if (mBeforeFirstLoad) {
			return;
		}

		synchronized (mAllAppsListLock) {
			final String action = intent.getAction();

			if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				final String packageName = intent.getData().getSchemeSpecificPart();
				final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
				
				if (packageName == null || packageName.length() == 0) {
					return;
				}

				if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
					//不处理天天动听插件的ACTION_PACKAGE_CHANGED caizp 2014-03-14
					if(isActionOnPkgChanged(packageName)){
						modified = true;
					}
				} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
					if (!replacing) {
						removed = true;
					}
				} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
					if (!replacing) {
						added = true;
					} else {
						modified = true;
					}
				}
				final Callbacks callbacks = mCallbacks;
				if (callbacks == null) {
					Log.w(TAG, "Nobody to tell about the new app.  Launcher is probably loading.");
					return;
				}

				if (added) {
					final List<ApplicationInfo> addedFinal = BaseAppDataFactory.findActivitiesForPackage(context, packageName);
					mHandler.post(new Runnable() {
						public void run() {
							callbacks.bindAppsAdded(addedFinal, packageName);
						}
					});
				}
				if (modified) {
					final List<ApplicationInfo> modifiedFinal = BaseAppDataFactory.findActivitiesForPackage(context, packageName);
					mHandler.post(new Runnable() {
						public void run() {
							callbacks.bindAppsUpdated(modifiedFinal, packageName);
						}
					});
				}
				if (removed) {
					mHandler.post(new Runnable() {
						public void run() {
							callbacks.bindAppsRemoved(packageName);
						}
					});
				}
			} else {
				if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
					String packages[] = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
					if (packages == null || packages.length == 0) {
						return;
					}

					updateOnActionExternalAppAvailable(context);

					if (ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).isDefaultTheme()) {
						mLauncher.setShowLoadingProgress(true);
					}
					startLoader(context, false, false, false);
					//检测SD卡装载完成，非默认主题时重新应用主题 caizp 2012-9-20
					if (!ThemeSharePref.getInstance(BaseConfig.getApplicationContext()).isDefaultTheme()) {
						if (null != BaseConfig.getBaseLauncher()) {
							applyThemeNoWallpaperWithWaitDialog();
						}
					}
					MessageUtils.makeShortToast(context, R.string.hint_sdcard_available);
				} else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
					String packages[] = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
					if (packages == null || packages.length == 0) {
						return;
					}
					mLauncher.setShowLoadingProgress(true);
					startLoader(context, false, false, false);
				}
			}
		}
	}
	
	public static void addOrMoveItemInDatabase(Context context, ItemInfo item, long container) {
		if (item.container == ItemInfo.NO_ID) {
			// From all apps
			item.container = container;
			addItemToDatabase(context, item, false);
		} else {
			// From somewhere else
			item.container = container;
			moveItemInDatabase(context, item);
		}
	}
	
	
	public static void addFQItemInDatabase(Context context, ItemInfo item) {
	
			// From all apps
			addItemToDatabase(context, item, false);
		
	}
	
	public static void moveItemInDatabase(Context context, ItemInfo item) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		values.put(BaseLauncherSettings.Favorites.CONTAINER, item.container);
		values.put(Favorites.CELLX, item.cellX);
		values.put(Favorites.CELLY, item.cellY);
		values.put(Favorites.SPANX, item.spanX);
		values.put(Favorites.SPANY, item.spanY);
		values.put(BaseLauncherSettings.Favorites.SCREEN, item.screen);

		cr.update(BaseLauncherSettings.Favorites.getContentUri(item.id, false), values, null, null);
	}
	
	public static void addItemToDatabase(Context context, ItemInfo item, boolean notify) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		item.onAddToDatabase(values);
		
		if(BaseConfig.isOnScene()){
			LauncherConfig.getLauncherHelper().onAddSceneItemToDatabase(context, values);
		}

		Uri result = cr.insert(notify ? BaseLauncherSettings.Favorites.getContentUri() : BaseLauncherSettings.Favorites.getContentUriNoNotify(), values);

		if (result != null) {
			item.id = Integer.parseInt(result.getPathSegments().get(2));
		}
	}
	
	/**
	 * Update an item to the database in a specified container.
	 */
	public static void updateItemInDatabase(Context context, ItemInfo item) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		item.onAddToDatabase(values);

		cr.update(BaseLauncherSettings.Favorites.getContentUri(item.id, false), values, null, null);
	}
	
	
	public static void deleteItemFromDatabase(Context context, ItemInfo item) {
		final ContentResolver cr = context.getContentResolver();
		cr.delete(BaseLauncherSettings.Favorites.getContentUri(item.id, false), null, null);
	}
	
	/**
	 * Update an item to the database in a specified container.
	 */
	public static void updateItemUriInDatabase(Context context, long id, String uri) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		values.put(BaseLauncherSettings.BaseLauncherColumns.INTENT, uri);

		cr.update(BaseLauncherSettings.Favorites.getContentUri(id, false), values, null, null);
	}
	
	/**
	 * 批量增加Items
	 * @param context
	 * @param items
	 */
	public static void addItemsToDatabase(Context context, ArrayList<ApplicationInfo> items){
		ArrayList<ContentProviderOperation> batchOps = new ArrayList<ContentProviderOperation>();
		Uri uri = BaseLauncherSettings.Favorites.getContentUriNoNotify();
		for (ApplicationInfo applicationInfo : items) {
			Builder builder = ContentProviderOperation.newInsert(uri);
			applicationInfo.onAddToDatabaseEx(builder);
				
			builder.withValue(BaseLauncherSettings.Favorites.CELLX, applicationInfo.cellX);
			builder.withValue(BaseLauncherSettings.Favorites.CELLY, applicationInfo.cellY);
			int[] size = CellLayoutConfig.spanXYMather(applicationInfo.spanX, applicationInfo.spanY, applicationInfo);
			builder.withValue(BaseLauncherSettings.Favorites.SPANX, size[0]);
			builder.withValue(BaseLauncherSettings.Favorites.SPANY, size[1]);
			
			if(BaseConfig.isOnScene()){
				LauncherConfig.getLauncherHelper().onAddSceneItemsToDatabase(context, builder);
			}
			
			batchOps.add(builder.build());
		}
		if(items != null && items.size() > 0){
			try {
				ContentProviderResult[] opResults
				= context.getContentResolver().applyBatch(Favorites.AUTHORITY, batchOps);
				
				for(int i=0; i<opResults.length; i++){
					ItemInfo itemInfo = items.get(i);
					Uri result = opResults[i].uri;
					if (result != null) {
						itemInfo.id = Integer.parseInt(result.getPathSegments().get(2));
					}
				}
			} catch (RemoteException e) {
				Log.e(TAG, "add to database failed");
			} catch (OperationApplicationException e) {
				Log.e(TAG, "add to database failed");
			}
		}
	}
	
	/**
	 * 批量修改Items
	 * @param context
	 * @param items
	 */
	public static void updateItemsInDatabase(Context context, ArrayList<ApplicationInfo> items) {
		ArrayList<ContentProviderOperation> batchOps = new ArrayList<ContentProviderOperation>();

		for (ApplicationInfo applicationInfo : items) {
			Uri uri = BaseLauncherSettings.Favorites.getContentUri(applicationInfo.id, false);
			Builder builder = ContentProviderOperation.newUpdate(uri);
			applicationInfo.onAddToDatabaseEx(builder);
			
			builder.withValue(BaseLauncherSettings.Favorites.CELLX, applicationInfo.cellX);
			builder.withValue(BaseLauncherSettings.Favorites.CELLY, applicationInfo.cellY);
			int[] size = CellLayoutConfig.spanXYMather(applicationInfo.spanX, applicationInfo.spanY, applicationInfo);
			builder.withValue(BaseLauncherSettings.Favorites.SPANX, size[0]);
			builder.withValue(BaseLauncherSettings.Favorites.SPANY, size[1]);
			
			batchOps.add(builder.build());
		}
		if (items != null && items.size() > 0) {
			try {
				context.getContentResolver().applyBatch(Favorites.AUTHORITY, batchOps);
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.e(TAG, "update database failed");
			} catch (OperationApplicationException e) {
				e.printStackTrace();
				Log.e(TAG, "update database failed");
			}
		}
	}
	
	/**
	 * 批量删除
	 * @param context
	 * @param items
	 */
	public static void deleteItemsFromDatabase(Context context, ArrayList<? extends ItemInfo> items) {
		ArrayList<ContentProviderOperation> batchOps = new ArrayList<ContentProviderOperation>();
		for (ItemInfo itemInfo : items) {
			Uri uri = BaseLauncherSettings.Favorites.getContentUri(itemInfo.id, false);
			Builder builder = ContentProviderOperation.newDelete(uri);
			batchOps.add(builder.build());
		}
		if (items != null && items.size() > 0) {
			try {
				context.getContentResolver().applyBatch(Favorites.AUTHORITY, batchOps);
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.e(TAG, "update database failed");
			} catch (OperationApplicationException e) {
				e.printStackTrace();
				Log.e(TAG, "update database failed");
			}
		}
	}
	
	/**
	 * Remove the contents of the specified folder from the database
	 */
	public static void deleteUserFolderContentsFromDatabase(Context context, FolderInfo info) {
		final ContentResolver cr = context.getContentResolver();

		cr.delete(BaseLauncherSettings.Favorites.getContentUri(info.id, false), null, null);
		cr.delete(BaseLauncherSettings.Favorites.getContentUri(), BaseLauncherSettings.Favorites.CONTAINER + "=" + info.id, null);
	}
	
	/**
	 * 返回快捷方式 <br>
	 */
	public ApplicationInfo addShortcut(Context context, Intent data, int screen, int[] cellXY, boolean notify) {
		final ApplicationInfo info = infoFromShortcutIntent(context, data);
		info.container = BaseLauncherSettings.Favorites.CONTAINER_DESKTOP;
		info.screen = screen;
		info.cellX = cellXY[0];
		info.cellY = cellXY[1];
		int[] wh = CellLayoutConfig.spanXYMather(1, 1, info);
		info.spanX = wh[0];
		info.spanY = wh[1];
		addItemToDatabase(context, info,  notify);
		
		return info;
	}
	
	private ApplicationInfo infoFromShortcutIntent(Context context, Intent data) {
		Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		Parcelable bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);

		Bitmap icon = null;
		boolean customIcon = false;
		ShortcutIconResource iconResource = null;

		if (bitmap != null && bitmap instanceof Bitmap) {
			icon = BaseBitmapUtils.createIconBitmapThumbnail(new FastBitmapDrawable((Bitmap) bitmap), context);
			customIcon = true;
		} else {
			Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
			if (extra != null && extra instanceof ShortcutIconResource) {
				try {
					iconResource = (ShortcutIconResource) extra;
					final PackageManager packageManager = context.getPackageManager();
					Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
					final int id = resources.getIdentifier(iconResource.resourceName, null, null);
					icon = BaseBitmapUtils.createIconBitmapThumbnail(resources.getDrawable(id), context);
				} catch (Exception e) {
					Log.w(TAG, "Could not load shortcut icon: " + extra);
				}
			}
		}

		final ApplicationInfo info = new ApplicationInfo(BaseLauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT);

		if (icon == null) {
			icon = getFallbackIcon();
			info.usingFallbackIcon = true;
		}
		info.iconBitmap = icon;
		info.title = name;
		info.intent = intent;
		info.customIcon = customIcon;
		info.iconResource = iconResource;

		return info;
	}
	
	public Bitmap getFallbackIcon() {
		if (mDefaultIcon == null || mDefaultIcon.isRecycled())
			mDefaultIcon = BaseBitmapUtils.createIconBitmapThumbnail(mApp.getPackageManager().getDefaultActivityIcon(), mApp);

		return Bitmap.createBitmap(mDefaultIcon);
	}
	
	/**
	 * Return an existing FolderInfo object if we have encountered this ID
	 * previously, or make a new one.
	 */
	public FolderInfo findOrMakeUserFolder(HashMap<Long, FolderInfo> folders, long id) {
		// See if a placeholder was created for us already
		FolderInfo folderInfo = folders.get(id);
		if (folderInfo == null || !(folderInfo instanceof FolderInfo)) {
			// No placeholder -- create a new instance
			folderInfo = new FolderInfo();
			folders.put(id, folderInfo);
		}
		return (FolderInfo) folderInfo;
	}
	
	/**
	 * 传入屏幕索引，从数据库中获取该屏幕下所有桌面项，并抽象封装成ItemInfo对象列表
	 * 
	 * @param context
	 * @param screenIndex
	 * @return ArrayList<ItemInfo>
	 */
	public ArrayList<ItemInfo> getItemsByScreen(Context context, int screenIndex) {

		final ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(BaseLauncherSettings.Favorites.getContentUri(), null, "screen=? and container='-100'", new String[] { String.valueOf(screenIndex) }, null);
		ArrayList<ItemInfo> items = new ArrayList<ItemInfo>(20);

		try {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				ItemInfo it = new ItemInfo();
				it.id = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites._ID));
				it.container = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.CONTAINER));
				it.screen = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.SCREEN));
				it.cellX = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.CELLX));
				it.cellY = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.CELLY));
				it.spanX = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.SPANX));
				it.spanY = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.SPANY));
				
				items.add(it);
			}

		} catch (Exception e) {
			Log.e(TAG, "err in getItemsByScreen():" + e.toString());
			return null;
		} finally {
			c.close();
		}

		return items;
	}
	
	public static void resizeItemInDatabase(Context context, ItemInfo item) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		item.onAddToDatabase(values);
		cr.update(BaseLauncherSettings.Favorites.getContentUri(item.id, false), values, null, null);
	}
	
	/**
	 * Returns true if the shortcuts already exists in the database. we identify
	 * a shortcut by its title and intent.
	 */
	public static boolean shortcutExists(Context context, String title, Intent intent) {
		final ContentResolver cr = context.getContentResolver();
//		String pandaSpacePkg = "com.dragon.android.pandaspace";
//		String qqBrowserPkg = "com.tencent.mtt";
		Cursor c = null;
		String intentStr = intent.toUri(0);
		if (StringUtil.isEmpty(intentStr))
			return false;
		
//		if (intentStr.contains(pandaSpacePkg)) {
//			c = cr.query(LauncherSettings.Favorites.CONTENT_URI, new String[] { "intent" }, "intent like '%" + pandaSpacePkg + "%'", null, null);
//		} else if (intentStr.contains(qqBrowserPkg)) {
//			c = cr.query(LauncherSettings.Favorites.CONTENT_URI, new String[] { "intent" }, "intent like '%" + qqBrowserPkg + "%'", null, null);
//		} else {
//			c = cr.query(LauncherSettings.Favorites.CONTENT_URI, new String[] { "title", "intent" }, "title=? or intent=?", new String[] { title, intentStr }, null);
//		}
		
		c = cr.query(BaseLauncherSettings.Favorites.getContentUri(), new String[] { "title", "intent" }, 
				"title=? or intent=?", new String[] { title, intentStr }, null);
		boolean result = false;
		try {
			result = c.moveToFirst();
		} finally {
			c.close();
		}
		return result;
	}
	
	public class Loader {
		private LoaderThread mLoaderThread;

		ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
		ArrayList<ItemInfo> mDockItems = new ArrayList<ItemInfo>();
		ArrayList<WidgetInfo> mAppWidgets = new ArrayList<WidgetInfo>();
		HashMap<Long, FolderInfo> mFolders = new HashMap<Long, FolderInfo>();

		ArrayList<ItemInfo> mCurrentItems = new ArrayList<ItemInfo>();
		ArrayList<WidgetInfo> mCurrentAppWidgets = new ArrayList<WidgetInfo>();
		ArrayList<ItemInfo> mCurrentDockItems = new ArrayList<ItemInfo>();

		/**
		 * Call this from the ui thread so the handler is initialized on the
		 * correct thread.
		 */
		public Loader() {
		}

		public void startLoader(Context context, boolean isLaunching, boolean worspaceOnly, boolean isAsync) {
			synchronized (mLock) {
				if (DEBUG_LOADERS) {
					Log.d(TAG, "startLoader isLaunching=" + isLaunching);
				}

				// Don't bother to start the thread if we know it's not going to
				// do anything
				if (mCallbacks != null) {
					LoaderThread oldThread = mLoaderThread;
					if (oldThread != null) {
						if (oldThread.isLaunching()) {
							// don't downgrade isLaunching if we're already
							// running
							isLaunching = true;
						}
						oldThread.stopLocked();
					}
					mLoaderThread = new LoaderThread(context, oldThread, isLaunching, worspaceOnly, isAsync);
					mLoaderThread.start();
				}
			}
		}

		public void stopLoader() {
			synchronized (mLock) {
				if (mLoaderThread != null) {
					mLoaderThread.stopLocked();
				}
			}
		}

		/**
		 * Runnable for the thread that loads the contents of the launcher: -
		 * workspace icons - widgets - all apps icons
		 */
		class LoaderThread extends Thread {
			private Context mContext;
			private Thread mWaitThread;
			private boolean mIsLaunching;
			private boolean mWorspaceOnly;
			private boolean mLoadAndBindStepFinished;
			private boolean isAsync;

			private LauncherLoader mLauncherLoader;
			
			LoaderThread(Context context, Thread waitThread, boolean isLaunching, boolean worspaceOnly, boolean async) {
				mContext = context;
				mWaitThread = waitThread;
				mIsLaunching = isLaunching;
				mWorspaceOnly = worspaceOnly;
				isAsync = async;
				
				mLauncherLoader = new LauncherLoader(BaseLauncherModel.this, mLauncher.getLauncherBinder(), LauncherConfig.getLauncherHelper(), mContext);
			}

			boolean isLaunching() {
				return mIsLaunching;
			}

			/**
			 * If another LoaderThread was supplied, we need to wait for that to
			 * finish before we start our processing. This keeps the ordering of
			 * the setting and clearing of the dirty flags correct by making
			 * sure we don't start processing stuff until they've had a chance
			 * to re-set them. We do this waiting the worker thread, not the ui
			 * thread to avoid ANRs.
			 */
			private void waitForOtherThread() {
				if (mWaitThread != null) {
					boolean done = false;
					while (!done) {
						try {
							mWaitThread.join();
							done = true;
						} catch (InterruptedException ex) {
							// Ignore
						}
					}
					mWaitThread = null;
				}
			}

			private void loadAndBindWorkspace() {
				if (!isAsync) {
					Log.e(TAG, "start loadWorkspace and bindWorkspace");
					mLauncherLoader.loadWorkspace(mLauncher);
					mLauncherLoader.bindWorkspace();
				} else if (mLauncher.allowToBindWorkspace()) {
					Log.e(TAG, "start bindWorkspace");
					mLauncherLoader.bindWorkspace();
				}
			}

			public void run() {
				waitForOtherThread();

				// Optimize for end-user experience: if the Launcher is up and
				// // running with the
				// All Apps interface in the foreground, load All Apps first.
				// Otherwise, load the
				// workspace first (default).
				final Callbacks cbk = mCallbacks;
				final boolean loadWorkspaceFirst = cbk != null ? (!cbk.isAllAppsVisible()) : true;

				// Elevate priority when Home launches for the first time to
				// avoid
				// starving at boot time. Staring at a blank home is not cool.
				synchronized (mLock) {
					android.os.Process.setThreadPriority(mIsLaunching ? Process.THREAD_PRIORITY_DEFAULT : Process.THREAD_PRIORITY_BACKGROUND);
				}

				if (PROFILE_LOADERS) {
					android.os.Debug.startMethodTracing(Environment.getExternalStorageDirectory().getAbsolutePath() + "/launcher-loaders");
				}

				if (loadWorkspaceFirst) {
					if (DEBUG_LOADERS)
						Log.d(TAG, "step 1: loading workspace");
					loadAndBindWorkspace();
				} else {
					if (DEBUG_LOADERS)
						Log.d(TAG, "step 1: special: loading all apps");
					loadAndBindAllApps();
				}

				// Whew! Hard work done.
				synchronized (mLock) {
					if (mIsLaunching) {
						android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
					}
				}

				// second step
				if (loadWorkspaceFirst) {
					if (DEBUG_LOADERS)
						Log.d(TAG, "step 2: loading all apps");
					loadAndBindAllApps();
				} else {
					if (DEBUG_LOADERS)
						Log.d(TAG, "step 2: special: loading workspace");
					loadAndBindWorkspace();
				}

				// Clear out this reference, otherwise we end up holding it
				// until all of the
				// callback runnables are done.
				mContext = null;

				synchronized (mLock) {
					// Setting the reference is atomic, but we can't do it
					// inside the other critical
					// sections.
					mLoaderThread = null;
				}

				if (PROFILE_LOADERS) {
					android.os.Debug.stopMethodTracing();
				}

				// Trigger a gc to try to clean up after the stuff is done,
				// since the
				// renderscript allocations aren't charged to the java heap.
				mHandler.post(new Runnable() {
					public void run() {
						System.gc();
					}
				});
			}

			public void stopLocked() {
				synchronized (LoaderThread.this) {
					mLauncherLoader.setStopped(true);
					this.notify();
				}
			}

			private void loadAndBindAllApps() {
				if (mWorspaceOnly)
					return;

				mBeforeFirstLoad = false;
				LauncherConfig.getLauncherHelper().loadAndBindAllApps(mLauncherLoader.tryGetCallbacks(mCallbacks), mHandler, mContext);
			}
			
			public void dumpState() {
				Log.d(TAG, "mLoader.mLoaderThread.mContext=" + mContext);
				Log.d(TAG, "mLoader.mLoaderThread.mWaitThread=" + mWaitThread);
				Log.d(TAG, "mLoader.mLoaderThread.mIsLaunching=" + mIsLaunching);
				Log.d(TAG, "mLoader.mLoaderThread.mStopped=" + mLauncherLoader.isStopped());
				Log.d(TAG, "mLoader.mLoaderThread.mLoadAndBindStepFinished=" + mLoadAndBindStepFinished);
			}
		}

		public void dumpState() {
			Log.d(TAG, "mLoader.mItems size=" + mLoader.mItems.size());
			if (mLoaderThread != null) {
				mLoaderThread.dumpState();
			} else {
				Log.d(TAG, "mLoader.mLoaderThread=null");
			}
		}
	}
	
	public Bitmap getIconFromCursor(Cursor c, int iconIndex) {
		byte[] data = c.getBlob(iconIndex);
		if (data == null)
			return null;

		try {
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressLint("NewApi")
	public void updateSavedIcon(Context context, ApplicationInfo info,
			Cursor c, int iconIndex) {
		// If this icon doesn't have a custom icon, check to see
		// what's stored in the DB, and if it doesn't match what
		// we're going to show, store what we are going to show back
		// into the DB. We do this so when we're loading, if the
		// package manager can't find an icon (for example because
		// the app is on SD) then we can use that instead.
		if (info.onExternalStorage && !info.customIcon && !info.usingFallbackIcon) {
			boolean needSave;
			byte[] data = c.getBlob(iconIndex);
			try {
				if (data != null) {
					Bitmap saved = BitmapFactory.decodeByteArray(data, 0, data.length);
					Bitmap loaded = info.getIcon(mIconCache);
					needSave = !saved.sameAs(loaded);
				} else {
					needSave = true;
				}
			} catch (Exception e) {
				needSave = true;
			}
			if (needSave) {
				Log.d(TAG, "going to save icon bitmap for info=" + info);
				// This is slower than is ideal, but this only happens either
				// after the froyo OTA or when the app is updated with a new
				// icon.
				updateItemInDatabase(context, info);
			}
		}
	}

	public WidgetInfo getAppWidgetInfo(int itemType, Cursor c,
			int appWidgetIdIndex, int iconPackageIndex, int iconResourceIndex,
			int iconTypeIndex, int titleIndex) {
		int appWidgetId = c.getInt(appWidgetIdIndex);
		if (itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_MIRROR_WIDGET) {
			MirrorWidgetInfo result = new MirrorWidgetInfo();
			result.appWidgetId = appWidgetId;
			result.layoutResString = c.getString(iconResourceIndex);
			result.title = c.getString(titleIndex);
			result.pandaWidgetPackage = c.getString(iconPackageIndex);
			return result;
		} else if (itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_MIRROR_PREVIEW_WIDGET) {
			MirrorWidgetPreviewInfo result = new MirrorWidgetPreviewInfo();
			result.appWidgetId = appWidgetId;
			result.iconRes = c.getInt(iconTypeIndex);
			result.title = c.getString(titleIndex);
			result.layoutXml = c.getString(iconResourceIndex);
			result.pandaWidgetPackage = c.getString(iconPackageIndex);
			return result;
		}

		return new WidgetInfo(appWidgetId);

	}

	public ApplicationInfo getShortcutInfo(Cursor c, Context context,
			int iconTypeIndex, int iconPackageIndex, int iconResourceIndex,
			int iconIndex, int titleIndex, int itemType) {
		Bitmap icon = null;
		final ApplicationInfo info = new ApplicationInfo();

		info.itemType = itemType;
		info.title = c.getString(titleIndex);
		
		int iconType = c.getInt(iconTypeIndex);
		switch (iconType) {
		case BaseLauncherSettings.Favorites.ICON_TYPE_RESOURCE:
			String packageName = c.getString(iconPackageIndex);
			String resourceName = c.getString(iconResourceIndex);
			PackageManager packageManager = context.getPackageManager();
			info.customIcon = false;
			// the resource
			try {
				Resources resources = packageManager.getResourcesForApplication(packageName);
				if (resources != null) {
					final int id = resources.getIdentifier(resourceName, null, null);
					icon = IconUtils.createIconBitmap(resources.getDrawable(id), context);
				}
				
			} catch (Exception e) {
				
			}
			// the db
			if (icon == null) {
				icon = getIconFromCursor(c, iconIndex);
			}
			// the fallback icon
			if (icon == null) {
				icon = getFallbackIcon();
				info.usingFallbackIcon = true;
			}
			break;
		case BaseLauncherSettings.Favorites.ICON_TYPE_BITMAP:
			icon = getIconFromCursor(c, iconIndex);
			if (icon == null) {
				icon = getFallbackIcon();
				info.customIcon = false;
				info.usingFallbackIcon = true;
			} else {
				info.customIcon = true;
			}
			break;
		default:
			icon = getFallbackIcon();
			info.usingFallbackIcon = true;
			info.customIcon = false;
			break;
		}
		info.iconBitmap = icon;
		return info;
	}

	public ApplicationInfo getApplicationInfo(PackageManager manager,
			Intent intent, Context context, Cursor c, int iconIndex,
			int titleIndex) {
		Bitmap icon = null;
		final Resources res = context.getResources();
		final ApplicationInfo info = new ApplicationInfo();

		ComponentName componentName = intent.getComponent();
		if (componentName == null) {
			return null;
		}

		info.componentName = componentName;

		if (icon == null) {
			if (c != null) {
				icon = getIconFromCursor(c, iconIndex);
			}
		}
		// the fallback icon
		if (icon == null) {
			icon = BitmapWeakReferences.getInstance().getDefAppIcon(res);
			info.usingFallbackIcon = true;
		} else {
			info.customIcon = true;
		}
		info.iconBitmap = icon;
		if (StringUtil.isEmpty(info.title)) {
			if (c != null) {
				info.title = c.getString(titleIndex);
			}
		}
		info.itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
		return info;
	}

	public ApplicationInfo getApplicationInfoNotComponent(PackageManager manager, Context context, Cursor c, int iconIndex,
			int titleIndex, int iconResourceIndex) {
		Bitmap icon = null;
		final Resources res = context.getResources();
		final ApplicationInfo info = new ApplicationInfo();

		if (icon == null) {
			if (c != null) {
				icon = getIconFromCursor(c, iconIndex);
			}
		}
		// the fallback icon
		if (icon == null) {
			icon = BitmapWeakReferences.getInstance().getDefAppIcon(res);
			info.usingFallbackIcon = true;
		} else {
			info.customIcon = true;
		}
		info.iconBitmap = icon;
		if (StringUtil.isEmpty(info.title)) {
			if (c != null) {
				info.title = c.getString(titleIndex);
			}
		}
		info.itemType = BaseLauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
		info.statTag = c.getString(iconResourceIndex);//推送的图标，该字段保存推送的ID，用于打点统计
		return info;
	}
	
    /**
     * 桌面加载时获取我的手机ApplicationInfo
     * @param app   我的手机 applicationInfo
     * @param c     游标
     * @param iconIndex  icon字段的索引
     * @param titleIndex title字段的索引
     * @return ApplicationInfo
     */
    public ApplicationInfo getHiApplicationInfo(ApplicationInfo app, Cursor c, int iconIndex,
                                                int titleIndex) {
        if (app == null) {
            return null;
        }

        ApplicationInfo applicationInfo = new ApplicationInfo(app);
        Bitmap icon = null;

        if (c != null) {
            icon = getIconFromCursor(c, iconIndex);
        }

        // 支持自定义图标
        if (icon != null) {
            applicationInfo.customIcon = true;
            applicationInfo.iconBitmap = icon;
        }

        //支持自定义名称
        if (c != null) {
            String title = c.getString(titleIndex);
            if (!StringUtil.isEmpty(title)) {
                applicationInfo.title = title;
            }
        }

        return applicationInfo;
    }


    /**
	 * 获取文件夹内容（只供推荐文件夹调用）
	 */
	public static List<ApplicationInfo> getFolderContentById(Context context, long id) {
		List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();
		
		Cursor c = null;
		
		try {
			final ContentResolver cr = context.getContentResolver();
			c = cr.query(BaseLauncherSettings.Favorites.getContentUri(), null, "container=?", new String[] { String.valueOf(id) }, null);
			final int intentIndex = c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.INTENT);
			final int titleIndex = c.getColumnIndexOrThrow(BaseLauncherSettings.BaseLauncherColumns.TITLE);
			final int idIndex = c.getColumnIndexOrThrow(BaseLauncherSettings.BaseLauncherColumns._ID);
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
					int itemType = c.getInt(c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.ITEM_TYPE));
					if (itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT) {
						BaseLauncher launcher = BaseConfig.getBaseLauncher();
						if (launcher != null && launcher.getLauncherModel() != null) {
							ApplicationInfo info = launcher.getLauncherModel().getShortcutInfo(c, 
									                                                   context, 
									                                                   c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.ICON_TYPE), 
									                                                   c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.ICON_PACKAGE), 
									                                                   c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.ICON_RESOURCE), 
									                                                   c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.ICON), 
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
	 * 是否响应app包更改
	 * @param pkgName
	 * @return
	 */
	public boolean isActionOnPkgChanged(String pkgName){
		return true;
	}
	
	/**
	 * 接收移动app到SD卡广播后，处理app状态
	 * @param context
	 * @param mCallbacks
	 */
	public void updateOnActionExternalAppAvailable(Context context){
	}
	
	/**
	 * 重新应用主题
	 */
	public void applyThemeNoWallpaperWithWaitDialog(){
		
	}
}
