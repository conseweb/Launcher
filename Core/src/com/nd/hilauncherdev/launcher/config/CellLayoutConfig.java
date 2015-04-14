package com.nd.hilauncherdev.launcher.config;

import java.lang.reflect.Method;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.ReflectUtil;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.kitset.util.StatusBarUtil;
import com.nd.hilauncherdev.launcher.config.preference.BaseConfigPreferences;
import com.nd.hilauncherdev.launcher.config.preference.BaseSettingsPreference;

/**
 * 
 * Description: CellLayout基本配置信息
 * Author: guojy
 * Date: 2013-5-9 下午4:59:49
 */
public class CellLayoutConfig{
	public static String TAG = "CellLayoutConfig";
	
	public static boolean mPortrait = true;//横竖屏，默认为竖屏
	
	public static int portMarginTop;
	public static int portMarginBottom;
	public static int portMarginLeft;
	public static int portMarginRight;
	
	public static int landMarginTop;
	public static int landMarginBottom;
	public static int landMarginLeft;
	public static int landMarginRight;
	
	/**
	 * cellLayout单元格大小
	 */
	public static int cellWidth;
	public static int cellHeight;
	//列间距
	public static int mCellGapX = 0;
	//行间距
	public static int mCellGapY = 0;
		
	public final static int cellWidth4x4 = 80;//屏幕数是4*4时的单元格宽度(dp) 用于计算系统小部件所占的格数
	public final static int cellHeight4x4 = 100;//屏幕数是4*4时的单元格高度(dp)
	
	public final static int defaultResolutionX = 480;
	public final static int defaultResolutionY = 800;
	public final static int defaultCountX = 4;
	public final static int defaultCountY = 4;
	
	public static int xCount = defaultCountX;
	public static int yCount = defaultCountY;

	public static void initForScene(Context mContext, int x, int y, int width, int height){
		//竖屏的上下左右边距
		portMarginLeft = x;
		portMarginTop = y;
		int[] screenWH = ScreenUtil.getScreenWH();
		portMarginRight = screenWH[0] - portMarginLeft - width;
		portMarginBottom = screenWH[1] - portMarginTop - height;
		//横屏的上下左右边距
		landMarginLeft = portMarginLeft;
		landMarginTop = portMarginTop;
		landMarginRight = portMarginRight;
		landMarginBottom = portMarginBottom;
				
		xCount = defaultCountX;
		yCount = defaultCountY;
		if(ScreenUtil.isExLardgeScreen()){// 如果是大屏幕，则将屏幕设置为5x4的
			yCount = 5;
		}
		cellWidth = width / xCount;
		cellHeight = height / yCount;
//		cellWidth4x4 = width /defaultCountX;
//		cellHeight4x4 = height /defaultCountY;
	}
	
	public static void init(Context mContext, boolean reset){
		try {
			//竖屏的上下左右边距
			portMarginLeft = 0;
			portMarginTop = mContext.getResources().getDimensionPixelSize(R.dimen.workspaceStartPadding);
			portMarginRight = 0;
			portMarginBottom = mContext.getResources().getDimensionPixelSize(R.dimen.workspaceEndPadding);
			if(BaseConfig.getBaseLauncher() != null && BaseConfig.getBaseLauncher().isTranslucentActionBar()){
				BaseConfig.setLauncherBottomPadding(StatusBarUtil.getTranslucentActionBarHeight(BaseConfig.getBaseLauncher()));
				portMarginBottom += BaseConfig.getLauncherBottomPadding();
			}
			if(LauncherConfig.getLauncherHelper() != null && !LauncherConfig.getLauncherHelper().isShowDockbarText()){
				portMarginBottom -= ScreenUtil.dip2px(mContext, 20);
			}
			//横屏的上下左右边距
			landMarginLeft = 0;
			landMarginTop = portMarginTop;
			landMarginRight = 0;
			landMarginBottom = portMarginBottom;
			
			//确定CellLayout行列数
			int[] countXY = BaseSettingsPreference.getInstance().getScreenCountXY();
			xCount = countXY[0];
			yCount = countXY[1];
			
			//获取cellLayout单元格大小
			if(xCount > 0 && yCount > 0){
				//Log.e("initinitinitinitinit", "init");
				int[] screenWH = ScreenUtil.getScreenWH();
				cellWidth = BaseConfigPreferences.getInstance().getCellLayoutCellWidth();
				if(cellWidth <= 0 || reset){
					//Log.e("initinitinitinitinit", "" + cellWidth);
					cellWidth = (screenWH[0] - getMarginLeft() - getMarginRight()- mCellGapX * (xCount - 1)) / xCount;
					BaseConfigPreferences.getInstance().setCellLayoutCellWidth(cellWidth);
				}
				cellHeight = BaseConfigPreferences.getInstance().getCellLayoutCellHeight();
				if(cellHeight < 0 || reset){
					//Log.e("initinitinitinitinit", "" + cellHeight);
					cellHeight = (screenWH[1] - getMarginTop() - getMarginBottom()- mCellGapY * yCount) / yCount;
					BaseConfigPreferences.getInstance().setCellLayoutCellHeight(cellHeight);
				}
//				cellWidth4x4 = (screenWH[0] - getMarginLeft() - getMarginRight()) /defaultCountX;
//				cellHeight4x4 =(screenWH[1] - getMarginTop() - getMarginBottom()) /defaultCountY;
			}
		} catch (Exception e) {
			Log.e("initCellLayoutConifg Exception", e.toString());
		}
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
	
	/**
	 * 每行摆放图标的个数
	 * @return
	 */
	public static int getCountX() {
		return mPortrait ? xCount : yCount;
	}

	/**
	 * 每列摆放图标的个数
	 * @return
	 */
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
	
	public static int[] getXY(int cellX, int cellY){
		int x = cellX * (cellWidth + mCellGapX) + getMarginLeft(); 
		int y = cellY * (cellHeight + mCellGapY) + getMarginTop(); 
		return new int[]{x, y};
	}
	
	public static int[] getCenterXY(int cellX, int cellY, int cellWidth, int cellHeight){
		int x = cellX * (cellWidth + mCellGapX) + getMarginLeft() + cellWidth / 2; 
		int y = cellY * (cellHeight + mCellGapY) + getMarginTop() + cellHeight / 2;
		return new int[]{x, y};
	}
	
	public static int[] getCellXY(int x, int y){
		int fix = 3;
		return pointToCell(x + fix , y + fix, cellWidth, cellHeight);
	}
	
	public static int[] pointToCell(int x, int y, int cellWidth, int cellHeight) {
		if(cellWidth == 0 || cellHeight == 0)
			return new int[]{0, 0};
		
		final int marginLeft = getMarginLeft();
		final int marginTop = getMarginTop();

		float cellWidthF = cellWidth + mCellGapX;
		float cellHeightF = cellHeight + mCellGapY;
		int[] result = new int[2];
		result[0] = Math.round((x - marginLeft) / cellWidthF);
		result[1] = Math.round((y - marginTop) / cellHeightF);

		if (result[0] < 0)
			result[0] = 0;
//		if (result[0] >= xCount)
//			result[0] = xCount - 1;
		
		if (result[1] < 0)
			result[1] = 0;
//		if (result[1] >= yCount)
//			result[1] = yCount - 1;
		
		return result;
	}
	
	public static void resetXYCount(int xCount, int yCount){
		reset(-1, -1, -1, -1, xCount, yCount);
	}
	
	/**
	 * Description: 用于换算spanXY
	 * Author: guojy
	 * Date: 2013-8-16 下午3:57:58
	 */
	public static int[] spanXYMather(int spanX, int spanY, Object item){
		if(BaseConfig.isOnScene()){
			if(LauncherConfig.getLauncherHelper() != null)
				return LauncherConfig.getLauncherHelper().spanXYMatherForScene(spanX, spanY, 
						cellWidth + mCellGapX, cellHeight + mCellGapY, item);
		}
		
		return new int[]{spanX, spanY};
	}
	
	public static int[] whToSpanXY(int width, int height){
		return new int[]{Math.round((float)width/(cellWidth + mCellGapX)), Math.round((float)height/(cellHeight + mCellGapY))};
	}
	
	public static int[] getSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minWidth, info.minHeight);
    }

    public static int[] getMinSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, getMinResizeWidth(info), getMinResizeHeight(info));
    }

	private static int[] getSpanForWidget(Context context, ComponentName component, int minWidth,
            int minHeight) {
        //Rect padding = getDefaultPaddingForWidget(context, component);
        // We want to account for the extra amount of padding that we are adding to the widget
        // to ensure that it gets the full amount of space that it has requested
      /*  int requiredWidth = minWidth + padding.left + padding.right;
        int requiredHeight = minHeight + padding.top + padding.bottom;
        float currentDensity = context.getResources().getDisplayMetrics().density;*/
        return whToSpanXYEx(context, minWidth, minHeight);
    }
	
	/**
	 * 小部件所需空间计算
	 * @author Michael
	 * Date:2013-10-8上午9:17:14
	 *  @param context
	 *  @param component
	 *  @return
	 */
	static Rect getDefaultPaddingForWidget(Context context, ComponentName component){
		try {
			Method method = AppWidgetHostView.class.getMethod("getDefaultPaddingForWidget", new Class[] { Context.class,
					ComponentName.class, Rect.class});
			return (Rect) method.invoke(null,  new Object[]{context, component, null});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Rect(0, 0, 0, 0);
	}
	
	/**
	 * 反射调用minResizeWidth值
	 * @author Michael
	 * Date:2013-11-13下午5:16:52
	 *  @param appWidgetProviderInfo
	 *  @return
	 */
	private static int getMinResizeWidth(AppWidgetProviderInfo appWidgetProviderInfo){
		try{
			return (Integer)(ReflectUtil.getFieldValueByFieldName(appWidgetProviderInfo, "minResizeWidth"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return appWidgetProviderInfo.minWidth;
	}
	
	/**
	 * 反射调用minResizeHeight值
	 * @author Michael
	 * Date:2013-11-13下午5:16:52
	 *  @param appWidgetProviderInfo
	 *  @return
	 */
	private static int getMinResizeHeight(AppWidgetProviderInfo appWidgetProviderInfo){
		try{
			return (Integer)(ReflectUtil.getFieldValueByFieldName(appWidgetProviderInfo, "minResizeHeight"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return appWidgetProviderInfo.minHeight;
	}
	
	
	public static int[] whToSpanXYEx(Context context, int width, int height){
		// Always assume we're working with the smallest span to make sure we
        // reserve enough space in both orientations.
        int actualWidth = ScreenUtil.dip2px(context, cellWidth4x4);
        int actualHeight = ScreenUtil.dip2px(context, cellHeight4x4); 
        int smallerSize = Math.min(actualWidth, actualHeight);
        // Always round up to next largest cell
        int spanX = (width+smallerSize) / smallerSize;
        int spanY = (height+smallerSize) / smallerSize;
		return new int[]{spanX, spanY};
	}
	
	public static int[] getXYForMoveDesk(int cellX, int cellY, int countX, int countY){
		int[] screenWH = ScreenUtil.getScreenWH();
		int cellWidth = (screenWH[0] - getMarginLeft() - getMarginRight()) / countX;
		int cellHeight = (screenWH[1] - getMarginTop() - getMarginBottom()) / countY;
		
		int x = cellX * cellWidth + getMarginLeft(); 
		int y = cellY * cellHeight + getMarginTop(); 
		return new int[]{x, y};
	}
	
	public static int[] spanXYToWhForMoveDesk(int spanX, int spanY, int countX, int countY){
		int[] screenWH = ScreenUtil.getScreenWH();
		int cellWidth = (screenWH[0] - getMarginLeft() - getMarginRight()) / countX;
		int cellHeight = (screenWH[1] - getMarginTop() - getMarginBottom()) / countY;
		
		return new int[]{spanX*cellWidth, spanY*cellHeight};
	}
	
	public static void resetMarginTop(int marginTop){
		reset(-1, marginTop, -1, -1, -1, -1);
	}
	
	public static void resetMarginBottom(int marginBottom){
		reset(-1, -1, -1, marginBottom, -1, -1);
	}
	/**
	 * Description: 如传入参数小于0， 则不改变原参数值
	 * Author: guojy
	 * Date: 2013-8-6 上午11:04:16
	 */
	private static void reset(int marginLeft, int marginTop, int marginRight, int marginBottom, int countX, int countY){
		portMarginLeft = marginLeft >= 0 ? marginLeft : portMarginLeft;
		portMarginTop = marginTop >= 0 ? marginTop : portMarginTop;
		portMarginRight = marginRight >= 0 ? marginRight : portMarginRight;
		portMarginBottom = marginBottom >= 0 ? marginBottom : portMarginBottom;
		
		if(countX > 0 && countY > 0){
			xCount = countX;
			yCount = countY;
		}
		
		resetCellWH();
	}
	
	public static void resetCellWH(){
		if(xCount > 0 && yCount > 0 && !BaseConfig.isOnScene()){
			int[] screenWH = ScreenUtil.getScreenWH();
			cellWidth = (screenWH[0] - getMarginLeft() - getMarginRight() - mCellGapX * (xCount - 1)) / xCount;
			cellHeight = (screenWH[1] - getMarginTop() - getMarginBottom() - mCellGapY * yCount) / yCount;
			BaseConfigPreferences.getInstance().setCellLayoutCellWidth(cellWidth);
			BaseConfigPreferences.getInstance().setCellLayoutCellHeight(cellHeight);
//			cellWidth4x4 = (screenWH[0] - getMarginLeft() - getMarginRight()) /defaultCountX;
//			cellHeight4x4 =(screenWH[1] - getMarginTop() - getMarginBottom()) /defaultCountY;
		}
	}
}
