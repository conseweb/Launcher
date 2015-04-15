package com.bitants.launcherdev.folder.model.stylehelper;

import android.view.View;

import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.screens.DragLayer;
import com.bitants.launcherdev.folder.model.FolderAnimationListener;

/**
 * 文件夹样式辅助类
 */
public abstract class AbstractFolderStyleHelper {

	protected BaseLauncher mLauncher;
	protected DragLayer mDragLayer;
	protected View mFolderView;
	
	protected AbstractFolderStyleHelper(BaseLauncher launcher) {
		mLauncher = launcher;
		mDragLayer = mLauncher.getDragLayer();
	}
	
	public abstract void onOpen();
	
	public abstract void onClose();

	public abstract void onCloseWithoutAnimation();
	
	/**
	 * 文件夹加密状态改变的回调
	 */
	public void onEncriptChanged() {
		
	}
	
	/**
	 * 添加app到文件夹中
	 */
	public abstract void onAddApps2Folder();
	
	/**
	 *  获取整个文件夹的可视视图
	 */
	public abstract View getFolderView();
	
	/**
	 * 是否处于关闭/打开动画中
	 * @return
	 */
	public boolean isOnAnimation(){
		return false;
	}
	
	/**
	 * 文件夹打开回调
	 * @param listener
	 */
	public void onOpen(FolderAnimationListener listener) {
		
	}
	
	/**
	 * 文件夹关闭回调
	 * @param listener
	 */
	public void onClose(FolderAnimationListener listener) {
		
	}

    public void changeViewToUpgradeState() {

    }

    public void changeViewToNormalState() {

    }
}
