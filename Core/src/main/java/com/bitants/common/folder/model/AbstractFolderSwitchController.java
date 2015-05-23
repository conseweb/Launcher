package com.bitants.common.folder.model;

import java.util.ArrayList;

import android.view.View;

import com.bitants.common.app.SerializableAppInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.folder.model.stylehelper.AbstractFolderStyleHelper;

/**
 * 文件夹逻辑控制器
 * 负责文件夹各项操作的分发:打开、关闭、批量添加、重命名、加密、排序...
 */
public abstract class AbstractFolderSwitchController {

	//整个文件夹的可视视图
	protected View mFolderView;
	
	//文件夹数据结构
	protected FolderInfo mFolderInfo;
	
	protected AbstractFolderSwitchController() {
	}
	
	/**
	 * 文件夹是否打开
	 * @return true 文件夹打开
	 */
	public boolean isFolderOpened() {
		return (mFolderView != null && mFolderView.getVisibility() == View.VISIBLE);
	}
	
	/**
	 * 获取文件夹数据信息
	 */
	public FolderInfo getFolderInfo(){
		return mFolderInfo;
	}
	
	/**
	 * 添加app到文件夹中
	 * @param list 应用列表
	 */
	public void addApps2Folder(ArrayList<SerializableAppInfo> list) {
		IFolderHelper folderHelper = getFolderHelper();
		if (folderHelper != null) {
			folderHelper.addApps2Folder(mFolderInfo, list);
		}
		
		AbstractFolderStyleHelper folderStyleHelper = getFolderStyleHelper();
		if (folderStyleHelper != null) {
			folderStyleHelper.onAddApps2Folder();
		}
	}
	
	/**
	 * 处理文件夹风格切换
	 */
	public abstract void handleStyleChanged();
	
	/**
	 * 获取文件夹打开样式的辅助类
	 */
	protected abstract AbstractFolderStyleHelper getFolderStyleHelper();
	
	/**
	 * 获取文件夹具体打开逻辑的辅助类
	 */
	protected abstract IFolderHelper getFolderHelper();
}
