package com.bitants.launcherdev.launcher.screens.preview;

import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;

/**
 * <br>Description: 屏幕预览缩略图信息
 * <br>Author:caizp
 * <br>Date:2012-6-21下午02:05:54
 */
public class PreviewCellInfo extends ItemInfo implements ICommonDataItem {
	
	/**
	 * "正常屏幕"类型
	 */
	public static final int TYPE_NORMAL_SCREEN = 0;
	
	/**
	 * "添加屏幕"类型
	 */
	public static final int TYPE_ADD_SCREEN = 1;

	/**
	 * 缩略图位置
	 */
	private int mPosition;
	
	/**
	 * 缩略图类型(0.正常屏幕  1.添加屏幕)
	 */
	private int mCellType = TYPE_NORMAL_SCREEN;
	
	@Override
	public int getPosition() {
		return mPosition;
	}

	@Override
	public void setPosition(int position) {
		mPosition = position;
	}

	@Override
	public boolean isFolder() {
		return false;
	}
	
	/**
	 * <br>Description: 获取缩略图类型
	 * <br>Author:caizp
	 * <br>Date:2012-6-21下午02:14:05
	 * @return
	 */
	public int getCellType() {
		return mCellType;
	}
	
	/**
	 * <br>Description: 设置缩略图类型
	 * <br>Author:caizp
	 * <br>Date:2012-6-21下午02:14:19
	 * @param cellType
	 */
	public void setCellType(int cellType){
		mCellType = cellType;
	}

}
