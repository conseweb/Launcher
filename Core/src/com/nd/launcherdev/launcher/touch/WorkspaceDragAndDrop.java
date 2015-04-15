package com.nd.launcherdev.launcher.touch;

import java.util.ArrayList;

import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.info.FolderInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.view.DragView;


import android.graphics.Rect;
import android.view.View;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.info.FolderInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.view.DragView;

public interface WorkspaceDragAndDrop {
	//=======================================处理workspace上的拖拽响应==============================//
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);
	
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);
	
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);
	
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);
	
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo);

	
	//=======================================处理打开文件夹后拖出==============================//
	/**
	 * 添加从文件夹拖拽出来的app
	 */
	public boolean onDropFolderExternal(int screen, Object item);
	/**
	 * 从文件夹拖拽出来松手后回调
	 */
	public void onDrop(View target,FolderInfo folder, ArrayList<Object> items);
	
	
	//=======================================处理屏幕预览上的拖放==============================//
	/**
	 * 处理拖动dockbar上图标到屏幕预览上
	 */
	public void dropDockbarItemToScreenFromPreview(CellLayout cellLayout, Object dragInfo, int[] targetCell);
	/**
	 * 从桌面将拖动的item到屏幕预览上
	 */
	public boolean dropItemToScreenFromPreview(int index, int[] targetCell);
	/**
	 * 处理拖动item到匣子底部的预览上
	 */
	public void dropToScreenFromDrawerPreview(int screen, Object mDragInfo, ArrayList<ApplicationInfo> appList);
	
	
	//=======================================获取放手后的目标区域==============================//
	public int[] getTargetCell();
	public Rect getTargetRect();
	public void cleanDragInfo();
	
	
	//=======================================文件夹合并动画处理==============================//
	/**
	 * 是否允许合并文件夹动画
	 */
	public void setAllowAnimateMergeFolder(boolean allowAnimateMergeFolder);
	/**
	 * 是否在进行合并文件的动画
	 */
	public boolean isOnMergeFolerAnimation();
	
	/**
	 * 回到桌面时，还原可能存在的文件夹动画
	 */
	public void restoreFolderAnimation();
	
	//=======================================拖动时挤动其它图标==============================//
	/**
	 * celllayout中的View移动位置后，是否有空闲区接收目标部件
	 */
	public boolean acceptDropForReorder(Object dragInfo);
	/**
	 * 是否拖动app到另一个app上方
	 */
	public boolean isDragOnApplication();
	/**
	 * 取消图标挤动响应
	 */
	public void cancelReorderAlarm();
	/**
	 * 是否允许拖动时进行图标挤动
	 */
	public void setAllowRevertReorder(boolean allow);
}
