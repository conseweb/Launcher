package com.nd.hilauncherdev.launcher.screens.dockbar;

public interface LightBarInterface {

	/**
	 * 滚动指示灯
	 * @param scrollX
	 */
	public void scrollHighLight(int scrollX);
	
	/**
	 *  设置指示数量
	 * @param size
	 */
	public void setSize(int size);
	
}
