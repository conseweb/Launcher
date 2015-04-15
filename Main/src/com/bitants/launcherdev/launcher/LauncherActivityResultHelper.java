package com.bitants.launcherdev.launcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.support.BaseCellLayoutHelper;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.launcher.support.BaseCellLayoutHelper;


public class LauncherActivityResultHelper {
	
	public static final int REQUEST_PICK_APPWIDGET = 1;
	public static final int REQUEST_CREATE_APPWIDGET = 2;

	public static void onActivityResult(int requestCode, int resultCode, Intent data, final Launcher mLauncher) {
		mLauncher.setWorkspaceLocked(false);

		boolean isCompleate = process(mLauncher, data, requestCode, resultCode);
		if (isCompleate)
			return;

		final Workspace mWorkspace = mLauncher.getWorkspace();
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
//			case REQUEST_PICK_APPLICATION:
//				completeAddApplication(this, data, mAddItemCellInfo);
//				break;
//			case REQUEST_PICK_SHORTCUT://系统快捷方式
//				processShortcut(data, mLauncher);
//				break;
//			case REQUEST_CREATE_SHORTCUT://系统快捷方式
//				if (mWorkspace.isOnSpringAddScreen()) {// 增加到编辑模式下的最后一屏(“新增”屏)
//					mWorkspace.animationSpringModeReboot();
//					final Intent intent = data;
//					mWorkspace.postDelayed(new Runnable() {
//						public void run() {
//							completeAddShortcut(intent, mWorkspace.getCurrentScreen(), mLauncher);
//						}
//					}, 500);
//				} else {
//					completeAddShortcut(data, mWorkspace.getCurrentScreen(), mLauncher);
//				}
//				break;
//			case REQUEST_PICK_LIVE_FOLDER:
//				break;
//			case REQUEST_CREATE_LIVE_FOLDER:
//				break;
			case REQUEST_PICK_APPWIDGET:
				addAppWidget(data, mLauncher);
				break;
			case REQUEST_CREATE_APPWIDGET://系统小部件
				if (mWorkspace.isOnSpringAddScreen()) {// 增加到编辑模式下的最后一屏(“新增”屏)
					mWorkspace.animationSpringModeReboot();
					final Intent intent = data;
					mWorkspace.postDelayed(new Runnable() {
						public void run() {
							completeAddAppWidget(intent, mLauncher);
						}
					}, 500);
				} else {
					completeAddAppWidget(data, mLauncher);
				}

				break;
//			case REQUEST_BIND_APPWIDGET:
//				int appWidgetId = data != null ?
//	                    data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
//	            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
//               	if(appWidgetId == -1){
//               		 if(mLauncher.mPendingAddWidgetId != -1){
//   	            		 intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mLauncher.mPendingAddWidgetId);
//   	                	 addAppWidget(intent, mLauncher);
//               		 }
//               	}else{
//               		 intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//                   	 addAppWidget(intent, mLauncher);
//               	}
//				break;
			}
		}
	}
	
	
	/**
	 * 处理自定义Launcher.startActivityForResult结果
	 */
	public static boolean process(Launcher launcher, Intent result, int requestCode, int resultCode) {
		if (resultCode == Activity.RESULT_CANCELED)
			return processCancled(launcher, result, requestCode);
		else
			return processOk(launcher, result, requestCode);
	}
	
	/**
	 * resultCode == RESULT_CANCELED
	 */
	private static boolean processCancled(Launcher launcher, Intent data, int requestCode) {
		boolean result = false;
		switch (requestCode) {
		case REQUEST_PICK_APPWIDGET:
		case REQUEST_CREATE_APPWIDGET:
			result = processAppWidget(launcher, data);
			break;
			
//		case REQUEST_PICK_CONTACT:  // 选择联系人取消
//			cancelToDeleteContact(launcher, false);
//			break;
		}
		return result;
	}
	
	/**
	 * resultCode == RESULT_OK
	 */
	private static boolean processOk(final Launcher launcher, final Intent data, int requestCode) {
		return false;
	}
	
	private static void addAppWidget(Intent data, Launcher mLauncher) {
		int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		AppWidgetProviderInfo appWidget = mLauncher.getAppWidgetManager().getAppWidgetInfo(appWidgetId);

		if (appWidget.configure != null) {
			// Launch over to configure widget, if needed
			Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidget.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

			SystemUtil.startActivityForResultSafely(mLauncher, intent, REQUEST_CREATE_APPWIDGET);
		} else {
			// Otherwise just add it
			onActivityResult(REQUEST_CREATE_APPWIDGET, Activity.RESULT_OK, data, mLauncher);
		}
	}
	
	/**
	 * 处理小部件
	 */
	private static boolean processAppWidget(Launcher launcher, Intent data) {
		if (data != null) {
			int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			if (appWidgetId != -1) {
				launcher.getAppWidgetHost().deleteAppWidgetId(appWidgetId);
			}
		}

		return true;
	}
	
	/**
	 * 从标题中获取widget的长和宽
	 * @author Michael
	 * Date:2013-11-14下午2:37:11
	 *  @param s
	 *  @return
	 */
	public static int[] getXY(String s){
		try{
			Pattern pattern = Pattern.compile("\\(*[0-9]x[0-9]\\)*");
	        Matcher matcher = pattern.matcher(s);
			boolean rtn = matcher.find();
			if(rtn){
				String x = matcher.group().substring(1, 2);
				String y = matcher.group().substring(3, 4);
				return new int[]{Integer.parseInt(x), Integer.parseInt(y)};
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Add a widget to the workspace.
	 * 
	 * @param data
	 *            The intent describing the appWidgetId.
	 * @param cellInfo
	 *            The position on screen where to create the widget.
	 */
	private static void completeAddAppWidget(Intent data, Launcher mLauncher) {
		final Workspace mWorkspace = mLauncher.getWorkspace();
		mWorkspace.destoryCurrentChildHardwareLayer();
		
		Bundle extras = data.getExtras();
		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

		AppWidgetProviderInfo appWidgetInfo = mLauncher.getAppWidgetManager().getAppWidgetInfo(appWidgetId);
		int[] spans = getXY(appWidgetInfo.label);
		if(spans == null){
			spans = new int[2];
			int[] spanXY = CellLayoutConfig.getSpanForWidget(mLauncher, appWidgetInfo);
            int[] minSpanXY = CellLayoutConfig.getMinSpanForWidget(mLauncher, appWidgetInfo);
            spans[0] = Math.min(spanXY[0], minSpanXY[0]);
            spans[1] = Math.min(spanXY[1], minSpanXY[1]);
		}else{
			if(appWidgetInfo.minWidth > appWidgetInfo.minHeight){
				int minSpanX = Math.max(spans[0], spans[1]);
				int minSpanY = Math.min(spans[0], spans[1]);
				spans[0] = minSpanX;
				spans[1] = minSpanY;
			}else{
				int minSpanX = Math.min(spans[0], spans[1]);
				int minSpanY = Math.max(spans[0], spans[1]);
				spans[0] = minSpanX;
				spans[1] = minSpanY;
			}
		}
		
		if (spans[0] > CellLayoutConfig.getCountX())
			spans[0] = CellLayoutConfig.getCountX();
		if (spans[1] > CellLayoutConfig.getCountY())
			spans[1] = CellLayoutConfig.getCountY();

		// 查找空闲区
		int[] cellXY = BaseCellLayoutHelper.findCellXYForWidget(mLauncher, spans[0], spans[1], null);
		if (cellXY == null) {
			if (appWidgetId != -1)
				mLauncher.getAppWidgetHost().deleteAppWidgetId(appWidgetId);
			return;
		}

		WidgetInfo launcherInfo = new WidgetInfo(appWidgetId);
		launcherInfo.cellX = cellXY[0];
		launcherInfo.cellY = cellXY[1];
		int[] wh = CellLayoutConfig.spanXYMather(spans[0], spans[1], null);
		launcherInfo.spanX = wh[0];
		launcherInfo.spanY = wh[1];
		
		launcherInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		launcherInfo.screen = mWorkspace.getCurrentScreen();
		BaseLauncherModel.addItemToDatabase(mLauncher, launcherInfo, false);
		
		if (!mLauncher.isRestoring()) {
			launcherInfo.hostView = mLauncher.getAppWidgetHost().createView(mLauncher, appWidgetId, appWidgetInfo);

			launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
			launcherInfo.hostView.setTag(launcherInfo);

			mWorkspace.addInCurrentScreen(launcherInfo.hostView,
					cellXY[0], cellXY[1], launcherInfo.spanX, launcherInfo.spanY, mLauncher.isWorkspaceLocked(), false);
		}
		// 刷新编辑模式的当前页面
		mLauncher.delayRefreshWorkspaceSpringScreen(500);
	}
}
