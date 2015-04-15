package com.bitants.launcherdev.launcher.model.load;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.bitants.launcherdev.kitset.util.AndroidPackageUtils;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.nd.android.pandahome2.R;
import com.bitants.launcherdev.kitset.util.AndroidPackageUtils;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.kitset.util.AndroidPackageUtils;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.touch.DropTarget;

/**
 * 
 * Description: Launcher绑定View添加到桌面上
 * Author: guojy
 * Date: 2013-9-12 下午3:20:52
 */
public class LauncherBinder implements Callbacks{

	protected BaseLauncher mLauncher;
	private AppWidgetManager mAppWidgetManager;
	
	/**
	 * 桌面加载进度条
	 */
	private ProgressDialog loadingProgress;
	private boolean isShowLoadingProgress = false;
	
	List<BindListener> mListeners = new ArrayList<BindListener>();
	
	public interface BindListener {
		void onFinish();
	}
	
	public LauncherBinder(BaseLauncher mLauncher){
		this.mLauncher = mLauncher;
		this.mAppWidgetManager = AppWidgetManager.getInstance(mLauncher);
	}
	
	@Override
	public void startBinding() {
		// 显示加载进度条
		if (isShowLoadingProgress) {
			if(loadingProgress != null && loadingProgress.isShowing()){
				try{
					loadingProgress.dismiss();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			loadingProgress = new ProgressDialog(mLauncher);
			loadingProgress.setMessage(mLauncher.getString(R.string.common_loading));
			loadingProgress.setCancelable(true);
			loadingProgress.show();
		}

		final ScreenViewGroup workspace = mLauncher.getScreenViewGroup();
		int count = workspace.getChildCount();
		for (int i = 0; i < count; i++) {
			((ViewGroup) workspace.getChildAt(i)).removeAllViewsInLayout();
		}

		final BaseMagicDockbar dockbar = mLauncher.getDockbar();
		count = dockbar.getChildCount();
		for (int i = 0; i < count; i++) {
			((ViewGroup) dockbar.getChildAt(i)).removeAllViewsInLayout();
		}
	}

	@Override
	public void finishBindingItems() {
		if (mLauncher.getSavedState() != null) {
			final ScreenViewGroup workspace = mLauncher.getScreenViewGroup();
			if (workspace != null && !workspace.hasFocus()) {
				View child = workspace.getChildAt(workspace.getCurrentScreen());
				if (child != null) {
					child.requestFocus();
				}
			}

			mLauncher.setSavedState(null);
		}

		mLauncher.setWorkspaceLoading(false);

		if (isShowLoadingProgress && !mLauncher.isFinishing() && loadingProgress != null && loadingProgress.isShowing()) {
			try {
				loadingProgress.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		isShowLoadingProgress = false;
		
		mLauncher.setIsFinishBinding(true);
		DeferredHandler handler = new DeferredHandler();
		for (final BindListener listener : mListeners) {
			handler.post(new Runnable() {
				public void run() {
					listener.onFinish();
				}
			});
		}
	}
	
	public void addListener(BindListener listener) {
		if (listener == null)
			return;
		
		mListeners.add(listener);
	}
	
	@Override
	public void bindItems(List<ItemInfo> shortcuts, int start, int end) {
		if (shortcuts == null) {
			return;
		}

		if (start < 0 || end > shortcuts.size()) {
			return;
		}
		LauncherConfig.getLauncherHelper().bindItems(shortcuts, start, end, mLauncher, mLauncher.getScreenViewGroup(), mLauncher.getDockbar());
	}

	@Override
	public void bindAllApplications(List<ApplicationInfo> apps) {
		mLauncher.bindAllAppsForDrawer(apps);
	}
	
	@Override
	public void bindAppsAdded(List<ApplicationInfo> apps, String packageName) {
		mLauncher.addNewInstallApps(apps, packageName);
	}

	@Override
	public void bindAppsUpdated(List<ApplicationInfo> apps, String packageName) {
		if (StringUtil.isEmpty(packageName) || mLauncher.getPackageName().equals(packageName)) {
			return;
		}

		if (null != apps && apps.size() != 0) {
			LauncherConfig.getLauncherHelper().updateAppsInWorkspace(apps, mLauncher);
			mLauncher.updateAppsForDrawer(apps, packageName);
			if (mLauncher.mDockbar != null)
				mLauncher.mDockbar.updateItemInDockbar(apps);
		}
		
		mLauncher.updatePandaWidget(packageName);
	}
	
	@Override
	public void bindAppsRemoved(String packageName) {
		if (AndroidPackageUtils.isPkgInstalled(mLauncher, packageName)) {// 若没被删除，则返回
			return;
		}

		//防止被卸载的图标还显示在桌面或文件夹上
		if(mLauncher.getDragController().isDragging()){
			mLauncher.getDragController().cancelDrag();
		}else if(mLauncher.isFolderOpened()){			
			mLauncher.closeFolder();
		}
		
		mLauncher.removePandaWidget(packageName);
		
		removeAppsInWorkspace(packageName);
		mLauncher.removeAppForDrawer(packageName);
		mLauncher.mDockbar.removeInDockbar(packageName);
	}
	
	@Override
	public void bindAppWidget(WidgetInfo item) {
		View view = mLauncher.createAppWidgetView(item);
		if (view == null)
			bindStanderWidget(item);
		else {
			mLauncher.getScreenViewGroup().addInScreen(view, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
			mLauncher.ifNeedCache(item, view);
		}
	}

	private void bindStanderWidget(WidgetInfo item) {
		if (item.itemType != BaseLauncherSettings.Favorites.ITEM_TYPE_APPWIDGET)
			return;
		
		final int appWidgetId = item.appWidgetId;
		final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
		item.hostView = mLauncher.getAppWidgetHost().createView(mLauncher, appWidgetId, appWidgetInfo);
		item.hostView.setAppWidget(appWidgetId, appWidgetInfo);
		item.hostView.setTag(item);
		mLauncher.getScreenViewGroup().addInScreen(item.hostView, item.screen, item.cellX, item.cellY, item.spanX, item.spanY);
	}
	
	@Override
	public boolean isAllAppsVisible() {
		return mLauncher.isAllAppsVisible();
	}
	
	public void setShowLoadingProgress(boolean isShowLoadingProgress) {
		this.isShowLoadingProgress = isShowLoadingProgress;
	}
	
	/**
	 * 卸载应用时调用删除
	 * <br>Author:ryan
	 * <br>Date:2012-7-25上午09:57:45
	 */
	public static void removeAppsInWorkspace(final String packageName) {
		final BaseLauncher mLauncher = BaseConfig.getBaseLauncher();
		if(mLauncher == null)
			return;
		final ScreenViewGroup mWorkspace = mLauncher.getScreenViewGroup();
		final int count = mWorkspace.getChildCount();
		final AppWidgetManager widgets = AppWidgetManager.getInstance(mLauncher);
		
		for (int i = 0; i < count; i++) {
			final CellLayout layout = (CellLayout) mWorkspace.getChildAt(i);

			// Avoid ANRs by treating each screen separately
			mWorkspace.post(new Runnable() {
				public void run() {
					final ArrayList<View> childrenToRemove = new ArrayList<View>();
					int childCount = layout.getChildCount();
					for (int j = 0; j < childCount; j++) {
						final View view = layout.getChildAt(j);
						Object tag = view.getTag();

						if (tag instanceof ApplicationInfo) {
							final ApplicationInfo info = (ApplicationInfo) tag;
							if (isAllowToRemoveOnUninstall(info, packageName)) {
								BaseLauncherModel.deleteItemFromDatabase(mLauncher, info);
								childrenToRemove.add(view);
							}
						} else if (tag instanceof FolderInfo) {
							final FolderInfo info = (FolderInfo) tag;
							final List<ApplicationInfo> contents = info.contents;
							final ArrayList<ApplicationInfo> toRemove = new ArrayList<ApplicationInfo>(1);
							final int contentsCount = contents.size();

							for (int k = 0; k < contentsCount; k++) {
								final ApplicationInfo info2 = contents.get(k);
								if (isAllowToRemoveOnUninstall(info2, packageName)) {
									toRemove.add(info2);
									BaseLauncherModel.deleteItemFromDatabase(mLauncher, info2);
								}
							}

							contents.removeAll(toRemove);
							if(toRemove.size() > 0){								
								view.invalidate();
							}
							
							if (info.getSize() <= 0) {
								BaseLauncherModel.deleteItemFromDatabase(mLauncher, info);
								childrenToRemove.add(view);
							}
						} else if (tag instanceof WidgetInfo) {
							final WidgetInfo info = (WidgetInfo) tag;
							AppWidgetProviderInfo appWidgetProviderInfo = widgets.getAppWidgetInfo(info.appWidgetId);
							if (appWidgetProviderInfo == null && info.hostView != null) {//适配4.*固件
								appWidgetProviderInfo = info.hostView.getAppWidgetInfo();
							}
							if (appWidgetProviderInfo != null && packageName.equals(appWidgetProviderInfo.provider.getPackageName())) {
								BaseLauncherModel.deleteItemFromDatabase(mLauncher, info);
								childrenToRemove.add(view);
							}
						}
					}
					//删除被卸载的view并刷新
					childCount = childrenToRemove.size();
					if (childCount > 0) {
						for (int j = 0; j < childCount; j++) {
							View child = childrenToRemove.get(j);
							layout.removeViewInLayout(child);
							if (child instanceof DropTarget) {
								mWorkspace.getDragController().removeDropTarget((DropTarget) child);
							}
						}
						layout.requestLayout();
						layout.invalidate();
					}
				}
			});
		}
	}
	
	public static boolean isAllowToRemoveOnUninstall(ApplicationInfo info, String uninstallPkgName){
		if(BaseConfig.getBaseLauncher() == null)
			return false;
		String selfPkgName = BaseConfig.getBaseLauncher().getPackageName();
		Intent intent = info.intent;
		ComponentName name = intent.getComponent();
		return (name != null && !uninstallPkgName.equals(selfPkgName) && uninstallPkgName.equals(name.getPackageName()))
				|| (info.iconResource != null && uninstallPkgName.equals(info.iconResource.resourceName));
	}
}
