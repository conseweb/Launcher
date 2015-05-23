package com.bitants.common.launcher.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.view.icon.ui.impl.DockbarCell;
import com.bitants.common.R;
import com.bitants.common.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.common.launcher.view.icon.ui.impl.IconMaskTextView;

public class BaseLauncherViewHelper {

	/**
	 * Description: 创建桌面上普通app、“我的手机”里的app、系统快捷、91快捷、自定义app(热门软件、热门游戏等)的View
	 */
	public static View createCommonAppView(BaseLauncher mLauncher, ApplicationInfo info) {
		if(mLauncher == null)
			return null;
		
		ScreenViewGroup mWorkspace = mLauncher.getScreenViewGroup();
		ViewGroup parent = (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen());
		if (parent == null)
			parent = (ViewGroup) mWorkspace.getChildAt(0);

		IconMaskTextView favorite = createIconMaskTextViewWithoutIcon(mLauncher, info.title, info, parent);
		favorite.setIconBitmap(info.iconBitmap);
		
		if(mLauncher.getScreenViewGroup().isAllAppsIndependence(info)){
			favorite.setFolderAvailable(false);
		}
		
		return favorite;
	}
	
	/**
	 * Description: 创建IconMaskTextView，未设置icon 
	 */
	public static IconMaskTextView createIconMaskTextViewWithoutIcon(BaseLauncher mLauncher, CharSequence title, ItemInfo info, ViewGroup parent) {
		if(mLauncher == null)
			return null;
		
		IconMaskTextView favorite = new IconMaskTextView(mLauncher);
		ViewGroup.MarginLayoutParams vm = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, 
				ViewGroup.MarginLayoutParams.MATCH_PARENT);
		CellLayout.LayoutParams cl = LauncherConfig.getLauncherHelper().newCellLayoutLayoutParams(vm);
		cl.setMargins(ScreenUtil.dip2px(mLauncher, 3), ScreenUtil.dip2px(mLauncher, 5),
				ScreenUtil.dip2px(mLauncher, 3), ScreenUtil.dip2px(mLauncher, 5));
		favorite.setLayoutParams(cl);
		favorite.setPadding(ScreenUtil.dip2px(mLauncher, 2), 0, ScreenUtil.dip2px(mLauncher, 2), 0);
		favorite.setText(title);
		favorite.setTag(info);
		favorite.setOnClickListener(mLauncher);
		return favorite;
	}

	/**
	 * Description: 创建dock栏上的应用View
	 */
	public static View createDockShortcut(BaseLauncher mLauncher, ApplicationInfo app) {
		if(mLauncher == null)
			return null;
		
		DockbarCell shortcut = null;
		shortcut = new DockbarCell(mLauncher);
		shortcut.setIconBitmap(app.iconBitmap);
		shortcut.setTag(app);
		shortcut.setText(app.title);
		shortcut.setOnClickListener(mLauncher);
		return shortcut;
	}
	
	/**
	 * Description: 创建文件夹View(目前用于桌面搬家)
	 */
	public static FolderIconTextView createFolderIconTextViewFromContext(BaseLauncher mLauncher, FolderInfo folderInfo) {
		FolderIconTextView folder = new FolderIconTextView(mLauncher);
		createFolderIconTextViewContent(mLauncher, folder, folderInfo);
		folder.setPadding(ScreenUtil.dip2px(mLauncher, 2), 0, ScreenUtil.dip2px(mLauncher, 2), 0);
		return folder;
	}
	
	/**
	 * Description: 根据配置文件创建文件夹View
	 */
	public static FolderIconTextView createFolderIconTextViewFromXML(BaseLauncher mLauncher, ViewGroup parent, FolderInfo folderInfo){
		FolderIconTextView folder = (FolderIconTextView) LayoutInflater.from(mLauncher).inflate(R.layout.folder_icon, parent, false);
		createFolderIconTextViewContent(mLauncher, folder, folderInfo);
		return folder;
	}
	
	public static void createFolderIconTextViewContent(BaseLauncher mLauncher, FolderIconTextView folder, FolderInfo folderInfo){
		folder.setText(folderInfo.title);
		folder.setTag(folderInfo);
		folder.setOnClickListener(mLauncher);
		folder.mInfo = folderInfo;
		folder.mLauncher = mLauncher;
		folderInfo.setFolderIcon(folder);
	}
	
	/**
	 * Description: 创建带布局LayoutParams的文件夹View
	 */
	public static View createFolderIconTextView(BaseLauncher mLauncher, FolderInfo folderInfo) {
		FolderIconTextView folder = new FolderIconTextView(mLauncher);
		ViewGroup.MarginLayoutParams vm = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, 
				ViewGroup.MarginLayoutParams.MATCH_PARENT);
		CellLayout.LayoutParams cl = LauncherConfig.getLauncherHelper().newCellLayoutLayoutParams(vm);
		cl.setMargins(ScreenUtil.dip2px(mLauncher, 3), ScreenUtil.dip2px(mLauncher, 5), ScreenUtil.dip2px(mLauncher, 3), ScreenUtil.dip2px(mLauncher, 5));
		createFolderIconTextViewContent(mLauncher, folder, folderInfo);
		folder.setLayoutParams(cl);
		folder.setPadding(ScreenUtil.dip2px(mLauncher, 2), 0, ScreenUtil.dip2px(mLauncher, 2), 0);
		return folder;
	}
}
