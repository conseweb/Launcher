package com.nd.hilauncherdev.framework.view.draggersliding.datamodel;

import java.util.List;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.CommonSlidingViewData;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;

/**
 * 通用可拖拽滚动滑屏View
 */
public class DraggerSlidingViewData extends CommonSlidingViewData implements
		IDraggerData {
	
	public DraggerSlidingViewData(int childViewWidth, int childViewHeight,
			int columnNum, int rowNum, List<ICommonDataItem> dataList) {
		super(childViewWidth, childViewHeight, columnNum, rowNum, dataList);
	}

	private boolean isAcceptDrop;

	@Override
	public void setAcceptDrop(boolean isAcceptDrop) {
		this.isAcceptDrop = isAcceptDrop;
	}

	@Override
	public boolean isAcceptDrop() {
		return isAcceptDrop;
	}

}
