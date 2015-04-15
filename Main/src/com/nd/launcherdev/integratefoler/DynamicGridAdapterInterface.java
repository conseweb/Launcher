package com.nd.launcherdev.integratefoler;

import android.view.View;

public interface DynamicGridAdapterInterface {
	
	/**
	 * 总个数
	 * @return
	 */
	public int getCount();  
	/**
	 * 列数
	 */
	public int getColumnCount();
	
	public Object getItem(int position);
	
	public long getItemId(int position);
	
	public View getView(int position);
	
	/**
	 * originalPosition和newPosition处信息交换
	 * @param originalPosition
	 * @param newPosition
	 */
    public void reorderItems(int originalPosition, int newPosition);

}
