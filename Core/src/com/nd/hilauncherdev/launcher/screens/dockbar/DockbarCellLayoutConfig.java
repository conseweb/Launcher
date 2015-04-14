package com.nd.hilauncherdev.launcher.screens.dockbar;

import android.content.Context;
import android.util.Log;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.config.LauncherConfig;
import com.nd.hilauncherdev.launcher.config.preference.BaseConfigPreferences;

/**
 * Description: DockbarCellLayoutConfig基本配置信息
 * Author: guojy
 * Date: 2013-5-13 下午2:36:07
 */
public class DockbarCellLayoutConfig{
	private static boolean mPortrait = true;//横竖屏
	
	private static int portMarginTop;
	private static int portMarginBottom;
	private static int portMarginLeft;
	private static int portMarginRight;
	
	private static int landMarginTop;
	private static int landMarginBottom;
	private static int landMarginLeft;
	private static int landMarginRight;
	
	private static int xCount;
	private static int yCount;
	/**
	 * cellLayout单元格大小
	 */
	private static int cellWidth;
	private static int cellHeight;
	
	private final static int defaultCountX = 5;
	private final static int defaultCountY = 1;
	
	private static int dockBarHeightMargin = 0; //dock栏显示app文字时和不显示app文字时的高度差
	
	
	public static void initForScene(Context mContext, int x, int y, int width, int height){
		xCount = defaultCountX;
		yCount = defaultCountY;
		
		//竖屏的上下左右边距
		portMarginLeft = x;
		portMarginTop = y;
		int[] screenWH = ScreenUtil.getScreenWH();
		portMarginRight = Math.max(screenWH[0] - portMarginLeft - width, 0);
		portMarginBottom = Math.max(screenWH[1] - portMarginTop - height, 0);
		//横屏的上下左右边距
		landMarginLeft = portMarginLeft;
		landMarginTop = portMarginTop;
		landMarginRight = portMarginRight;
		landMarginBottom = portMarginBottom;
		
		cellWidth = width / xCount;
		cellHeight = height / yCount;
		
		initDockBarHeightMargin(mContext);
	}
	
	public static void init(Context mContext, boolean reset){
		try {
			int[] screenWH = ScreenUtil.getScreenWH();
			int screenW = screenWH[0];
			int screenH = screenWH[1];
			//竖屏的上下左右边距
			int portHeight = mContext.getResources().getDimensionPixelSize(R.dimen.button_bar_height); 
			initDockBarHeightMargin(mContext);
			if(LauncherConfig.getLauncherHelper() != null && !LauncherConfig.getLauncherHelper().isShowDockbarText()){
				portHeight -= getDockBarHeightMargin(mContext);
			}
			portMarginLeft = 0;
			portMarginTop = screenH - portHeight;
			portMarginRight = 0;
			portMarginBottom = 0;
			//横屏的上下左右边距
			int landHeight = portHeight;
			landMarginLeft = 0;
			landMarginTop = screenH - landHeight;
			landMarginRight = 0;
			landMarginBottom = 0;
			
			xCount = defaultCountX;
			yCount = defaultCountY;
			
			//获取cellLayout单元格大小
			cellWidth = BaseConfigPreferences.getInstance().getDockbarCellWidth();
			if(cellWidth <= 0 || reset){				
				cellWidth = (screenW - getMarginLeft() - getMarginRight()) / xCount;
				BaseConfigPreferences.getInstance().setDockbarCellWidth(cellWidth);
			}
			cellHeight = BaseConfigPreferences.getInstance().getDockbarCellHeight();
			if(cellHeight <= 0 || reset){
				int height = mPortrait ? portHeight : landHeight;
				cellHeight = height / yCount;
				BaseConfigPreferences.getInstance().setDockbarCellHeight(cellHeight);
			}
		} catch (Exception e) {
			Log.e("DockbarCellLayoutConfig Exception", e.toString());
		}
		
		
	}
	
	public static int[] getXY(int cellX, int cellY){
		int x = cellX * DockbarCellLayoutConfig.cellWidth + getMarginLeft(); 
		int y = cellY * DockbarCellLayoutConfig.cellHeight + getMarginTop(); 
		return new int[]{x, y};
	}
	
	public static int[] getCellXY(int x, int y, int width, int height){
		int fix = 20;
		return pointToCell(x + fix , y + fix);
	}
	
	public static int[] pointToCell(int x, int y) {
		if(cellWidth == 0 || cellHeight == 0)
			return new int[]{0, 0};
		
		final int marginLeft = getMarginLeft();
		final int marginTop = getMarginTop();

		int[] result = new int[2];
		result[0] = (x - marginLeft) / cellWidth;
		result[1] = (y - marginTop) / cellHeight;

		if (result[0] < 0)
			result[0] = 0;
		if (result[0] >= xCount)
			result[0] = xCount - 1;
		
		if (result[1] < 0)
			result[1] = 0;
		if (result[1] >= yCount)
			result[1] = yCount - 1;
		
		return result;
	}
	
	public static int[] spanXYToWh(int spanX, int spanY){
		return new int[]{spanX*cellWidth, spanY*cellHeight};
	}
	
	public static int[] whToSpanXY(int width, int height){
		return new int[]{Math.round((float)width/cellWidth), Math.round((float)height/cellHeight)};
	}
	
	
	public static int getMarginLeft() {
		return mPortrait ? portMarginLeft : landMarginLeft;
	}

	public static int getMarginTop() {
		return mPortrait ? portMarginTop : landMarginTop;
	}

	public static int getMarginRight() {
		return mPortrait ? portMarginRight : landMarginRight;
	}

	public static int getMarginBottom() {
		return mPortrait ? portMarginBottom : landMarginBottom;
	}
	
	public static int getCountX() {
		return mPortrait ? xCount : yCount;
	}

	public static int getCountY() {
		return mPortrait ? yCount : xCount;
	}
	
	public static int getCellWidth() {
		return cellWidth;
	}

	public static int getCellHeight() {
		return cellHeight;
	}
	
	public static void resetPortrait(boolean portrait){
		mPortrait = portrait;
	}
	
	public static void resetMarginTop(int marginTop){
		reset(-1, marginTop, -1, -1, -1, -1);
	}
	
	public static void resetMarginTop(Context mContext){
		int[] screenWH = ScreenUtil.getScreenWH();
		int screenH = screenWH[1];
		//竖屏的上下左右边距
		int portHeight = mContext.getResources().getDimensionPixelSize(R.dimen.button_bar_height); 
		initDockBarHeightMargin(mContext);
		if(!LauncherConfig.getLauncherHelper().isShowDockbarText()){
			portHeight -= getDockBarHeightMargin(mContext);
		}
		resetMarginTop(screenH - portHeight);
	}
	
	public static void reset(int marginLeft, int marginTop, int marginRight, int marginBottom, int countX, int countY){
		portMarginLeft = marginLeft >= 0 ? marginLeft : portMarginLeft;
		portMarginTop = marginTop >= 0 ? marginTop : portMarginTop;
		portMarginRight = marginRight >= 0 ? marginRight : portMarginRight;
		portMarginBottom = marginBottom >= 0 ? marginBottom : portMarginBottom;
		
		if(countX > 0 && countY > 0){
			xCount = countX;
			yCount = countY;
		}
		
		if(xCount > 0 && yCount > 0 && !BaseConfig.isOnScene()){
			int[] screenWH = ScreenUtil.getScreenWH();
			cellWidth = (screenWH[0] - getMarginLeft() - getMarginRight()) / xCount;
			cellHeight = (screenWH[1] - getMarginTop() - getMarginBottom())  / yCount;
			BaseConfigPreferences.getInstance().setDockbarCellWidth(cellWidth);
			BaseConfigPreferences.getInstance().setDockbarCellHeight(cellHeight);
		}
		
	}
	
	private static void initDockBarHeightMargin(Context mContext){
		dockBarHeightMargin = ScreenUtil.dip2px(mContext, 20);
	}
	
	public static int getDockBarHeightMargin(Context mContext) {
		if(dockBarHeightMargin == 0){
			initDockBarHeightMargin(mContext);
		}
		return dockBarHeightMargin;
	}
	
	/**
	 * Description: 返回dockbar子view的(CellXY, SpanXY)信息，用于从主桌面搬数据
	 * Author: guojy
	 * Date: 2013-8-13 下午7:41:46
	 */
	public static int[] getChildCellXYAndSpanXY(int dockbarWidth, int dockbarHeight, int pos){
		int cellWidth = dockbarWidth/5;
		int cellHeight = dockbarHeight;
		int[] screenWH = ScreenUtil.getScreenWH();
		int left = Math.max(screenWH[0] - dockbarWidth, 0);
		int top = 0;
		return new int[]{left + cellWidth * pos, top, cellWidth, cellHeight};
	}
}
