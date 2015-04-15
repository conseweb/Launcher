package com.nd.launcherdev.launcher;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.nd.launcherdev.datamodel.Global;
import com.nd.launcherdev.kitset.util.ThreadUtil;
import com.nd.launcherdev.launcher.config.ConfigFactory;
import com.nd.launcherdev.launcher.info.FolderInfo;
import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.info.WidgetInfo;
import com.nd.launcherdev.launcher.model.BaseLauncherModel;
import com.nd.launcherdev.launcher.model.BaseLauncherSettings;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.screens.ScreenViewGroup;
import com.nd.launcherdev.launcher.screens.ScreenViewGroup;
import com.nd.launcherdev.launcher.support.LauncherAppWidgetHost;
import com.nd.launcherdev.datamodel.Global;
import com.nd.launcherdev.launcher.info.FolderInfo;
import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.info.WidgetInfo;
import com.nd.launcherdev.launcher.model.BaseLauncherModel;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.support.LauncherAppWidgetHost;

public class WorkspaceHelper {

	private BaseLauncher mLauncher;
	private ScreenViewGroup mWorkspace;

	public WorkspaceHelper(BaseLauncher launcher) {
		mLauncher = launcher;
		if (null != mLauncher) {
			mWorkspace = launcher.getScreenViewGroup();
		}
	}
	/**
	 * 删除Workspace数据
	 * @param dragInfo
	 * @param v
	 */
	public void removeItemFromWorkspace(Object dragInfo, View v){
		Launcher launcher = (Launcher)mLauncher;
		final ItemInfo item = (ItemInfo) dragInfo;
		if (item.container == -1)
			return;
		
		if (item.container == BaseLauncherSettings.Favorites.CONTAINER_DESKTOP) {
			if(item instanceof WidgetInfo) {
				
				final WidgetInfo launcherAppWidgetInfo = (WidgetInfo) item;
				final LauncherAppWidgetHost appWidgetHost = launcher.getAppWidgetHost();
				if (appWidgetHost != null) {
					appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
				}
				((WidgetInfo)item).hostView = null;
			}
		}
		
		if (item instanceof FolderInfo) {
			final FolderInfo userFolderInfo = (FolderInfo) item;
			BaseLauncherModel.deleteUserFolderContentsFromDatabase(launcher, userFolderInfo);
		}
		BaseLauncherModel.deleteItemFromDatabase(launcher, item);
	}
	
	/**
	 * <br>
	 * Description: 往Workspace末端添加空白屏 <br>
	 * Author:caizp <br>
	 * Date:2012-6-26上午11:54:01
	 * 
	 * @return
	 */
	public CellLayout createScreenToWorkSpace() {
		CellLayout cell = new CellLayout(mLauncher);
		cell.setWorkspace(mWorkspace);
		cell.setCountXY();
		mWorkspace.addView(cell);
		cell.setCellLayoutLocation(mWorkspace.getChildCount() - 1);
		
		cell.setOnLongClickListener(mWorkspace.getOnLongClickListener());
		
		if (null != mWorkspace.getLightBar()) {
			 mWorkspace.getLightBar().setSize(mWorkspace.getChildCount());
		}
		
		cell.destroyHardwareLayer();
		ConfigFactory.saveScreenCount(mLauncher, mWorkspace.getChildCount());
		return cell;
	}
	
	
	public void removeScreenFromWorkspace(final int screenIndex) {
		final CellLayout layout = (CellLayout) mWorkspace.getChildAt(screenIndex);
		final boolean isLastScreen = screenIndex == mWorkspace.getChildCount() - 1 ;

		mWorkspace.removeViewAt(screenIndex);
		layout.destroyHardwareLayer();
		
		if (null != mWorkspace.getLightBar()) {
			 mWorkspace.getLightBar().setSize(mWorkspace.getChildCount());
		}
		ConfigFactory.saveScreenCount(mLauncher, mWorkspace.getChildCount());
		mWorkspace.invalidate();
//			mLauncher.setupDeleteZone();
		// 异步删除数据
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				int viewCount = layout.getChildCount();
				for (int i = 0; i < viewCount; i++) {
					removeItemFromWorkspace(layout.getChildAt(i).getTag(), layout.getChildAt(i));
					mLauncher.ifNeedClearCache(layout.getChildAt(i));
				}
				if(viewCount > 0){					
					layout.removeAllViewsInLayout();
				}
				if (!isLastScreen) {
					final int count = mWorkspace.getChildCount();
					moveScreenPosition(screenIndex, count);
					resetItemScreen(screenIndex, count,true);
				}
			}
		});
	}

	
	private void moveScreenPosition(int screenStart, int screenEnd) {
		// 获取数据库对象
		SQLiteDatabase launcherDB = new LauncherProvider.DatabaseHelper(mLauncher).getWritableDatabase();
		final String screenField = LauncherSettings.Favorites.SCREEN;
		final String container = LauncherSettings.Favorites.CONTAINER;
		final int container_desktop = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		String sql = "";
		String tableName = LauncherProvider.TABLE_FAVORITES;
		try {
			if (screenStart < screenEnd) {
				// 起始屏位置小于目的屏位置，从起始屏之后到目的屏都往前移一屏，再将起始屏移到目的屏
				// update favorites set screen=screen-1 where screen>screenStart
				// and screen<=screenEnd
				sql = "update "+tableName+" set " + screenField + "=" + screenField + "-1 where " + screenField + ">" + screenStart + " and " + screenField + "<=" + screenEnd
					+ " and " + container + " =" + container_desktop;
			} else {
				// 起始屏位置大于目的屏位置，从目的屏到起始屏之前的屏都往后移一屏，再将起始屏移到目的屏
				// update favorites set screen=screen+1 where screen>=screenEnd
				// and screen<screenStart
				sql = "update "+tableName+" set " + screenField + "=" + screenField + "+1 where " + screenField + ">=" + screenEnd + " and " + screenField + "<" + screenStart
					+ " and " + container + " =" + container_desktop;
			}
			
			launcherDB.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != launcherDB) {
				launcherDB.close();
			}
		}
	}
	
	/**
	 * <br>
	 * Description: 屏幕上的数据项移动 <br>
	 * Author:caizp <br>
	 * Date:2012-6-26下午03:06:50
	 * 
	 * @param screenStart
	 *            开始屏
	 * @param screenEnd
	 *            目的屏
	 */
	public void moveItemPositions(int screenStart, int screenEnd) {
		// 获取屏
		CellLayout startLayout = (CellLayout) mWorkspace.getChildAt(screenStart);

		// 屏上的元素个数
		int childCount = startLayout.getChildCount();
		// 将开始屏数据取出,并将屏幕设置为目的屏，放入集合
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		for (int j = 0; j < childCount; j++) {
			final View view = startLayout.getChildAt(j);
			Object tag = view.getTag();
			if (tag == null || !(tag instanceof ItemInfo)) continue ;
			final ItemInfo item = (ItemInfo) tag;
			item.screen = screenEnd;// 屏位置设置成目的屏
			itemList.add(item);
		}
		
		moveScreenPosition(screenStart, screenEnd);
		boolean isAsc ;
		if (screenStart < screenEnd) {
			screenStart++ ;
			isAsc = true ;
		} else {
			screenStart-- ;
			isAsc = false ;
		}

		resetItemScreen(screenStart, screenEnd,isAsc);

		// 将修改到目的屏的数据更新到数据库
		for (ItemInfo item : itemList) {
			if (item != null) {
				item.container = item.container == ItemInfo.NO_ID ? LauncherSettings.Favorites.CONTAINER_DESKTOP : item.container;
				BaseLauncherModel.moveItemInDatabase(mLauncher, item);
			}
		}
		itemList.clear();
	}
	
	/**
	 * <p>桌面删除某一屏后，重置后面屏幕的screen属性</p>
	 * 
	 * <p>date: 2012-9-27 上午11:41:13
	 * @author pdw
	 * @param startScreen
	 * @param screen
	 * @param isAsc 是否从小屏下标拖动到大屏下标
	 */
	private void resetItemScreen(int startScreen,int endScreen,boolean isAsc) {
		int start ;
		int end ;
		if (startScreen < endScreen) {
			start = startScreen ;
			end = endScreen ;
		} else {
			start = endScreen ;
			end = startScreen ;
		}
		int childCount = 0 ;
		for (; start <= end ; start++) {
			final CellLayout cellLayout = (CellLayout) mWorkspace.getChildAt(start);
			if (cellLayout == null) continue ;
			childCount = cellLayout.getChildCount();
			for (int j = 0; j < childCount; j++) {
				final View view = cellLayout.getChildAt(j);
				Object tag = view.getTag();
				if (tag == null || !(tag instanceof ItemInfo)) continue ;
				final ItemInfo item = (ItemInfo) tag;
				if (isAsc) {
					item.screen--;
				} else {
					item.screen++;
				}
			}
		}
	}
	
	public static int[] getVacantCellFromBottom(){
		SQLiteDatabase db = null;
		try {
			db = new LauncherProvider.DatabaseHelper(Global.getApplicationContext()).getWritableDatabase();
			return LauncherProviderHelper.getVacantCellFromBottom(db, 1, 1, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
		return null;
	}
}
