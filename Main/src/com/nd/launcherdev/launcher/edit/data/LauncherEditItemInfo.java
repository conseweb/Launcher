package com.nd.launcherdev.launcher.edit.data;

import android.graphics.drawable.Drawable;

import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;

/**
 *
 * @author Anson
 */
public class LauncherEditItemInfo implements ICommonDataItem {
	
	public String title;
	
	public Drawable icon;
	
	public int type;

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPosition(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFolder() {
		// TODO Auto-generated method stub
		return false;
	}

}
