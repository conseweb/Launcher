package com.bitants.common.framework.view.commonsliding;

import com.bitants.common.framework.view.commonsliding.datamodel.ICommonDataItem;


/**
 * CommonViewHolder
 */
public class CommonViewHolder {
	
	/**
	 * 记录View在所在数据集中的位置
	 */
	public int positionInData;
	
	/**
	 * 记录View在所在屏幕中的位置
	 */
	public int positionInScreen;
	
	/**
	 * 记录View在所在屏幕
	 */
	public int screen;
	
	public ICommonDataItem item;
}
