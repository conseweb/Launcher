package com.nd.launcherdev.framework.view.draggersliding.datamodel;

import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonData;

/**
 * 通用可拖拽滚动滑屏View数据接口
 */
public interface IDraggerData extends ICommonData {

	/**
	 * 设置数据集是否允许拖拽放置; 如:小部件只需拖拽至桌面，但不需要在集合中调整位置，此时可返回false
	 */
	public boolean isAcceptDrop();
	
	/**
	 * 设置是否处理拖拽放手
	 * @param isAcceptDrop
	 */
	public void setAcceptDrop(boolean isAcceptDrop);
	
}
