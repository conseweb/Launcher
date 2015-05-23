package com.bitants.common.launcher.screens.preview;

import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.framework.view.commonsliding.datamodel.ICommonDataItem;

/**
 * <br>Description: 屏幕预览缩略图信息
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
	 * @return
	 */
	public int getCellType() {
		return mCellType;
	}
	
	/**
	 * <br>Description: 设置缩略图类型
	 * @param cellType
	 */
	public void setCellType(int cellType){
		mCellType = cellType;
	}

}
