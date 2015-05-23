package com.bitants.common.launcher.model.load;

import java.util.ArrayList;
import java.util.HashMap;

import com.bitants.common.kitset.util.ThreadUtil;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.info.WidgetInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.support.BaseIconCache;
import com.bitants.common.launcher.config.preference.BaseConfigPreferences;
import com.bitants.common.launcher.BaseLauncherApplication;

import android.content.Context;
import android.util.Log;

/**
 * Description: 加载和绑定Workspace图标
 */
public class LauncherLoader{
	final static String TAG = "LauncherLoader";
	final static ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
	final static ArrayList<ItemInfo> mDockItems = new ArrayList<ItemInfo>();
	final static ArrayList<WidgetInfo> mAppWidgets = new ArrayList<WidgetInfo>();
	final static HashMap<Long, FolderInfo> mFolders = new HashMap<Long, FolderInfo>();

	final static ArrayList<ItemInfo> mCurrentItems = new ArrayList<ItemInfo>();
	final static ArrayList<WidgetInfo> mCurrentAppWidgets = new ArrayList<WidgetInfo>();
	final static ArrayList<ItemInfo> mCurrentDockItems = new ArrayList<ItemInfo>();

	private Callbacks mCallbacks;

	private DeferredHandler mHandler = new DeferredHandler();
	private BaseLauncherModel mModel;
	
	private final Object mLock = new Object();
	
	private int mCurrentScreen;
	
	private boolean mStopped;
	private Context mContext;
	
	private LauncherLoaderHelper mLauncherLoaderHelper;
	
	public void setCurrentScreen(int mCurrentScreen) {
		this.mCurrentScreen = mCurrentScreen;
	}
	
	public boolean isStopped() {
		return mStopped;
	}

	public void setStopped(boolean mStopped) {
		this.mStopped = mStopped;
	}
	
	public ArrayList<ItemInfo> getDockitems() {
		return mDockItems;
	}

	public HashMap<Long, FolderInfo> getFolders() {
		return mFolders;
	}

	public ArrayList<ItemInfo> getCurrentDockitems() {
		return mCurrentDockItems;
	}
	
	public LauncherLoader(BaseLauncherModel model, Callbacks callbacks, LauncherLoaderHelper launcherLoaderHelper, Context context) {
		mCallbacks = callbacks;
		mModel = model;
		mLauncherLoaderHelper = launcherLoaderHelper;
		mContext = context;
		
		mCurrentScreen = BaseConfigPreferences.getInstance().getDefaultScreen();
	}
	
	/**
	 * Gets the callbacks object. If we've been stopped, or if the launcher
	 * object has somehow been garbage collected, return null instead. Pass
	 * in the Callbacks object that was around when the deferred message was
	 * scheduled, and if there's a new Callbacks object around then also
	 * return null. This will save us from calling onto it with data that
	 * will be ignored.
	 */
	public Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
		synchronized (mLock) {
			if (mStopped) {
				return null;
			}

			if (mCallbacks == null) {
				return null;
			}

			final Callbacks callbacks = mCallbacks;
			if (callbacks != oldCallbacks) {
				return null;
			}
			if (callbacks == null) {
				Log.w(TAG, "no mCallbacks");
				return null;
			}

			return callbacks;
		}
	}
	
	// check & update map of what's occupied; used to discard
	// overlapping/invalid items
//	@Deprecated
//	private boolean checkItemPlacement(ItemInfo occupied[][][], ItemInfo item) {
//		if (item.container != LauncherSettings.Favorites.CONTAINER_DESKTOP) {
//			return true;
//		}
//
//		int offsetX = item.cellX + item.spanX - Launcher.NUMBER_CELLS_X;
//		if (offsetX > 0) {
//			if (item.spanX != 1 && offsetX < item.spanX) {
//				item.spanX -= offsetX;
//			} else {
//				return false;
//			}
//		}
//
//		int offsetY = item.cellY + item.spanY - Launcher.NUMBER_CELLS_Y;
//		if (offsetY > 0) {
//			if (item.spanY != 1 && offsetY < item.spanY) {
//				item.spanY -= offsetY;
//			} else {
//				return false;
//			}
//		}
//		for (int x = item.cellX; x < (item.cellX + item.spanX); x++) {
//			for (int y = item.cellY; y < (item.cellY + item.spanY); y++) {
//				if (occupied[item.screen][x][y] != null) {
//					// Log.e(TAG, "Error loading shortcut " + item +
//					// " into cell (" + item.screen + ":" + x + "," + y +
//					// ") occupied by " + occupied[item.screen][x][y]);
//					return false;
//				}
//			}
//		}
//		for (int x = item.cellX; x < (item.cellX + item.spanX); x++) {
//			for (int y = item.cellY; y < (item.cellY + item.spanY); y++) {
//				occupied[item.screen][x][y] = item;
//			}
//		}
//		return true;
//	}
	
	public void loadWorkspace(Context context) {
		mItems.clear();
		mAppWidgets.clear();
		mFolders.clear();
		mDockItems.clear();
		mCurrentItems.clear();
		mCurrentAppWidgets.clear();
		mCurrentDockItems.clear();
		
		boolean success = mLauncherLoaderHelper.loadFavoritesDataFromDB(context, this, mModel);
		if(!success){
			// 不采用多线程加载桌面
			BaseSettingsPreference.getInstance().setAsyncLoadLauncherData(false);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

//	private boolean isOutOfBounds(int cellX, int cellY) {
//		return cellX > Launcher.NUMBER_CELLS_X - 1 || cellY > Launcher.NUMBER_CELLS_Y - 1;
//	}

	/**
	 * modified by zhl 添加boolean isCurrentScrren参数 如果item在当前屏 加入列表头
	 */
	public void addToItemsList(final ItemInfo itemInfo) {
//		if (!isOutOfBounds(itemInfo.cellX, itemInfo.cellY)) {
			if (itemInfo.screen == mCurrentScreen)
				mCurrentItems.add(itemInfo);
			else
				mItems.add(itemInfo);
//		}
	}

	public void addToAppWidgetList(WidgetInfo appwidgetInfo) {
//		if (!isOutOfBounds(appwidgetInfo.cellX, appwidgetInfo.cellY)) {
			if(appwidgetInfo.screen == mCurrentScreen){
				mCurrentAppWidgets.add(appwidgetInfo);
			}else{
				mAppWidgets.add(appwidgetInfo);
			}
//		}
	}

	/**
	 * 优先加载Dock栏，当前屏，桌面图标
	 */
	public void loadIconFirst(ArrayList<ItemInfo> mCurrentDockItems,  ArrayList<ItemInfo> mCurrentItems){
		for (final ItemInfo item : mCurrentDockItems) {
			if (!(item instanceof ApplicationInfo)) {
				continue;
			}
			if (LauncherConfig.getLauncherHelper().isMyPhoneItem(item))
				continue;
			
			ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    final BaseIconCache mIconCache = (BaseIconCache) ((BaseLauncherApplication) mContext.getApplicationContext()).mIconCache;
                    mIconCache.getTitleAndIcon((ApplicationInfo) item);
                }
            });
		}
		
		for (final ItemInfo item : mCurrentItems) {
			if (!(item instanceof ApplicationInfo)) {
				continue;
			}
			if (LauncherConfig.getLauncherHelper().isMyPhoneItem(item))
				continue;
			
			ThreadUtil.execute(new Runnable() {
				@Override
				public void run() {
					final BaseIconCache mIconCache = (BaseIconCache) ((BaseLauncherApplication) mContext.getApplicationContext()).mIconCache;
					mIconCache.getTitleAndIcon((ApplicationInfo) item);
				}
			});
		}
//		for (final ItemInfo item : mItems) {
//		if (!(item instanceof ApplicationInfo)) {
//			continue;
//		}
//		
//		ThreadUtil.execute(new Runnable() {
//			@Override
//			public void run() {
//				final IconCache mIconCache = ((LauncherApplication) mLauncher.getApplicationContext()).mIconCache;
//				mIconCache.getTitleAndIcon((ApplicationInfo) item);
//			}
//		});
//	}
	}
	/**
	 * Read everything out of our database.
	 */
	public void bindWorkspace() {
		Log.w(TAG, "bindWorkspace");
		loadIconFirst(mCurrentDockItems, mCurrentItems);
		
		// Don't use these two variables in any of the callback runnables.
		// Otherwise we hold a reference to them.
		final Callbacks oldCallbacks = mCallbacks;
		if (oldCallbacks == null) {
			// This launcher has exited and nobody bothered to tell us. Just bail.
			Log.w(TAG, "LoaderThread running with no launcher");
			return;
		}
		
		int N;
		// Tell the workspace that we're about to start firing items at
		// it
		mHandler.post(new Runnable() {
			public void run() {
				Callbacks callbacks = tryGetCallbacks(oldCallbacks);
				if(callbacks != null){					
					callbacks.startBinding();
				}
			}
		});
		
		// bind apps to the dockbar
		mHandler.post(new Runnable() {
			public void run() {
				Callbacks callbacks = tryGetCallbacks(oldCallbacks);
				if(callbacks != null){						
					callbacks.bindItems(mCurrentDockItems, 0, mCurrentDockItems.size());
				}
			}
		});
		
		//bind apps to current screen and folder
		mHandler.post(new Runnable() {
			public void run() {
				Callbacks callbacks = tryGetCallbacks(oldCallbacks);
				if(callbacks != null){						
					callbacks.bindItems(mCurrentItems, 0, mCurrentItems.size());
				}
			}
		});
		
		N = mCurrentAppWidgets.size();
		for (int i = 0; i < N; i++) {
			final WidgetInfo widget = mCurrentAppWidgets.get(i);
			mHandler.post(new Runnable() {
				public void run() {
					Callbacks callbacks = tryGetCallbacks(oldCallbacks);
					if(callbacks != null){							
						callbacks.bindAppWidget(widget);
					}
				}
			});
		}
		
		int ITEMS_CHUNK = 16;
		N = mItems.size();
		for (int i = 0; i < N; i += ITEMS_CHUNK) {
			final int start = i;
			final int chunkSize = (i+ITEMS_CHUNK <= N) ? ITEMS_CHUNK : (N-i);
			mHandler.post(new Runnable() {
				public void run() {
					Callbacks callbacks = tryGetCallbacks(oldCallbacks);
					if(callbacks != null){							
						callbacks.bindItems(mItems, start, start+chunkSize);
					}
				}
			});
		}
							
		N = mAppWidgets.size();
		for (int i = 0; i < N; i++) {
			final WidgetInfo widget = mAppWidgets.get(i);
			mHandler.post(new Runnable() {
				public void run() {
					Callbacks callbacks = tryGetCallbacks(oldCallbacks);
					if(callbacks != null){							
						callbacks.bindAppWidget(widget);
					}
				}
			});
		}
		
		mHandler.post(new Runnable() {
			public void run() {
				Callbacks callbacks = tryGetCallbacks(oldCallbacks);
				if(callbacks != null){						
					callbacks.bindItems(mDockItems, 0, mDockItems.size());
				}
			}
		});
		
		// Tell the workspace that we're done.
		mHandler.post(new Runnable() {
			public void run() {
				Callbacks callbacks = tryGetCallbacks(oldCallbacks);
				if(callbacks != null){						
					callbacks.finishBindingItems();
				}
			}
		});
	}
}
