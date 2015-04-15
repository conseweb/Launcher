package com.nd.launcherdev.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.touch.DragSource;
import com.nd.launcherdev.launcher.view.BaseDeleteZoneTextView;
import com.nd.launcherdev.launcher.view.DragView;
import com.nd.launcherdev.launcher.view.DragView;
import com.nd.launcherdev.util.AppUninstallUtil;
import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.util.AppUninstallUtil;

public class DeleteZoneTextView extends BaseDeleteZoneTextView {
	private CellLayout.CellInfo mDragInfo;
	
	public DeleteZoneTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DeleteZoneTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		/**
		 * 不允许删除【应用列表】快捷方式
		 */
		if(mLauncher.getScreenViewGroup().isAllAppsIndependence((ItemInfo) dragInfo) || mLauncher.isAllAppsVisible())
			return false;
		
		return super.acceptDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		/**
		 * 删除【最近安装】【最近打开】给出提示
		 */
//		if (dragInfo instanceof AnythingInfo) {
//			removeRecentFolderWithAlert(dragInfo);
//			return ;
//		}
		
		if(dragView != null){
			removeItemFromWorkspace(source, dragInfo, dragView.getDragingView());
		}else{
			removeItemFromWorkspace(source, dragInfo, null);
		}
		
		super.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}
	
	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		
//		if (mLauncher.isAllAppsVisible() || (source instanceof FolderSlidingView && mLauncher.isFolderOpened()))
//			return;
//		if(mLauncher.isOnSpringMode() && source instanceof DrawerSlidingView)
//			return;
//		if (mLauncher.getScreenViewGroup().isAllAppsIndependence((ItemInfo) dragInfo))
//			return;
		
		super.onDragEnter(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
//		if (mLauncher.isAllAppsVisible() || (source instanceof FolderSlidingView && mLauncher.isFolderOpened()))
//			return;
//		
//		if (mLauncher.getScreenViewGroup().isAllAppsIndependence((ItemInfo) dragInfo))
//			return;
		
		super.onDragExit(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	private void removeItemFromWorkspace(DragSource source, Object dragInfo, View v){
		Launcher launcher = (Launcher)mLauncher;
		final ItemInfo item = (ItemInfo) dragInfo;
		if(isDeleteZone()){//删除
			if (item.container == -1)
				return;

			((Workspace)launcher.getScreenViewGroup()).getWorkspaceHelper().removeItemFromWorkspace(dragInfo, v);
			launcher.isDeleteZone = true;
		}else{
//			if (ShortcutMenu.isAppInfo(item))
//				return;
			ApplicationInfo applicationInfo = (ApplicationInfo) item;
			AppUninstallUtil.uninstallAppByLauncher(launcher, applicationInfo.componentName.getPackageName());
			launcher.isDeleteZone = false;
		}
	}
}
