package com.bitants.launcherdev.launcher;

import android.widget.Toast;

import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.support.BaseCellLayoutHelper;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;

/**
 * Description: 
 * Author: guojy
 * Date: 2012-7-16 下午03:51:00
 */
public class CellLayoutHelper extends BaseCellLayoutHelper{

	public CellLayoutHelper() {
	}
	
	/**
	 * Description: 在当前屏为添加app找位置
	 * Author: guojy
	 * Date: 2013-7-30 下午4:14:03
	 */
	public static int[] findPositionCellXY(Launcher mLauncher){
		CellLayout cl =  mLauncher.mWorkspace.getCurrentCellLayout();
		if(cl == null)
			return null;
		
		int[] cellXY = cl.findVacantCellFromBottom(1, 1, null);
		if(cellXY == null){
			Toast.makeText(mLauncher, R.string.out_of_space, Toast.LENGTH_SHORT).show();
			return null;
		}else{				
			return cl.findVacantCellFromBottom(1, 1, null);
		}
	}
	
	/**
	 * Description: 在当前屏为添加Widget找位置
	 * Author: guojy
	 * Date: 2013-7-30 下午4:14:03
	 */
	public static int[] findWidgetPositionCellXY(Launcher mLauncher, int spanX, int spanY, ItemInfo item){
		CellLayout cl =  mLauncher.mWorkspace.getCurrentCellLayout();
		int[] cellXY = cl.findFirstVacantCell(spanX, spanY, null, true);
		if (cellXY == null) {
			Toast.makeText(mLauncher, R.string.out_of_space, Toast.LENGTH_SHORT).show();
			return null;
		}
		return cellXY;
	}
	
}
