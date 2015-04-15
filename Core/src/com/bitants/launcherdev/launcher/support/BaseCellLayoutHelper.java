package com.bitants.launcherdev.launcher.support;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.IconMaskTextView;
import com.nd.android.pandahome2.R;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.CellLayout.CellInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout.LayoutParams;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.IconMaskTextView;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.IconMaskTextView;

public class BaseCellLayoutHelper {
	public static int leftDiffNormalAndSpring;
	public static int topDiffNormalAndSpring;
	public static int springTopMargin;
	
	public static float springScale;

	/*
	 * 普通模式下cell的宽高和行间距
	 */
	public static int cellWidth;
	public static int cellHeight;
	/**
	 * 行间距
	 */
	public static int cellGapY;
	/**
	 * 列间距
	 */
	public static int cellGapX;
	
	/**
     * 编辑模式下，celllayout的中心点坐标
     */
	public static int springMiddleX;
	public static int springMiddleY;
	public static int springPageTranslationY;
	
	/**
	 * 编辑模式动画前的页面宽和高
	 */
	public static int mCellLayoutWidthEx;
	public static int mCellLayoutHeightEx;
	
	/**
     * 编辑模式下的背景图、添加和删除屏按钮
     */
	public Drawable springScreenBackground;
	public Drawable springNoVacantScreenBackground;
	public Drawable springAddScreenBtn;
	public Drawable springDelScreenBtn;
	
	
	public BaseCellLayoutHelper() {
		
	}
	
	/**
	 * Description: 获取编辑模式下cell坐标位置
	 * Author: guojy
	 * Date: 2012-7-16 上午10:26:29
	 */
	public static void getSpringCellToPoint(int cellX, int cellY, int[] result){
		int topMargin = topDiffNormalAndSpring + springTopMargin;
		
		int scaleCellW = (int) (springScale * (cellWidth + cellGapX));
		int scaleCellH = (int) (springScale * cellHeight);
		int scaleGapY = (int) (springScale * cellGapY);
		
		result[0] = leftDiffNormalAndSpring + cellX * (scaleCellW);
		if(cellY == CellLayoutConfig.getCountY()){
			result[1] = topMargin + (cellY - 1) * (scaleCellH + scaleGapY) + scaleCellH;
		}else{
			result[1] = topMargin + cellY * (scaleCellH + scaleGapY);
		}
	}
	
	/**
	 * Description: 编辑模式Rect匹配
	 * Author: guojy
	 * Date: 2013-7-27 下午3:19:17
	 */
	public static void springToNormalRectEx(Rect r){
		r.offset(0, -springTopMargin);
		springToNormalRect(r);
		r.offset(0, springTopMargin);
	}
	
	public static void springToNormalRect(Rect r){
		r.left = (int) ((r.left - leftDiffNormalAndSpring)/springScale);
		r.right = (int) ((r.right - leftDiffNormalAndSpring)/springScale);
		r.top = (int) ((r.top - topDiffNormalAndSpring)/springScale);
		r.bottom = (int) ((r.bottom - topDiffNormalAndSpring)/springScale);
	}
	
	/**
	 * author:Michael
	 * date:2013-5-6
	 * 修改计算空cell的顺序为从下到上，从左到右
	 * @param vacant
	 * @param spanX
	 * @param spanY
	 * @param xCount
	 * @param yCount
	 * @param occupied
	 * @return
	 */
	public static boolean findVacantCell(int[] vacant, int spanX, int spanY, int xCount, int yCount, boolean[][] occupied) {

		for (int x = xCount-1; x >= 0; x--) {
			for (int y = 0; y < yCount; y++) {
				boolean available = !occupied[y][x];
				out: for (int i = x; i > x - spanX + 1 && x > 0; i--) {
					for (int j = y; j < y + spanY - 1 && y < yCount; j++) {
						if(i < 0 || j < 0 || i >= xCount || j >= yCount)continue;
						available = available && !occupied[j][i];
						if (!available)
							break out;
					}
				}

				if (available) {
					vacant[0] = y;
					vacant[1] = x;
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Description: 编辑模式坐标匹配（使得编辑模式缩放后的坐标匹配缩放前的坐标）
	 * Author: guojy
	 * Date: 2012-7-15 下午05:48:04
	 */
	//location为屏幕坐标
	public static void springToNormalCoordinateEx(int[] location){
		location[1] -= springTopMargin;
		springToNormalCoordinate(location);
		location[1] += springTopMargin;
	}
	//location为CellLayout内相对坐标
	public static void springToNormalCoordinate(int[] location){
		location[0] = (int) ((location[0] - leftDiffNormalAndSpring)/springScale);
		location[1] = (int) ((location[1] - topDiffNormalAndSpring)/springScale);
	}
	
	/**
	 * Description: 编辑模式坐标匹配（使得正常模式下坐标匹配编辑模式坐标）
	 * Author: guojy
	 * Date: 2013-7-22 上午10:59:34
	 */
	//location为屏幕坐标
	public static void normalToSpringCoordinateEx(int[] location){
		location[1] -= springTopMargin;
		normalToSpringCoordinate(location);
		location[1] += springTopMargin;
	}
	//location为CellLayout内相对坐标
	public static void normalToSpringCoordinate(int[] location){
		location[0] = (int) (location[0]*springScale + leftDiffNormalAndSpring);
		location[1] = (int) (location[1]*springScale + topDiffNormalAndSpring);
	}
	
	/**
	 * Description: 在当前屏为添加app找位置
	 * Author: guojy
	 * Date: 2013-7-30 下午4:14:03
	 */
	public static int[] findCellXYForApp(BaseLauncher mLauncher){
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
	public static int[] findCellXYForWidget(BaseLauncher mLauncher, int spanX, int spanY, ItemInfo item){
		CellLayout cl =  mLauncher.mWorkspace.getCurrentCellLayout();
		int[] cellXY = cl.findFirstVacantCell(spanX, spanY, null, true);
		if (cellXY == null) {
			Toast.makeText(mLauncher, R.string.out_of_space, Toast.LENGTH_SHORT).show();
			return null;
		}
		return cellXY;
	}
	
	/**
	 * Description: 统计CellLayout中单元格的占用情况
	 * Author: guojy
	 * Date: 2012-8-29 下午02:28:36
	 */
	public static void findOccupiedCells(boolean[][] occupied, View ignoreView, CellLayout cl) {
		if(occupied == null){
			occupied = new boolean[CellLayoutConfig.getCountX()][CellLayoutConfig.getCountY()];
		}
		
		int xLen = occupied.length;
		int yLen = occupied[0].length;
		
		for (int x = 0; x < xLen; x++) {
			for (int y = 0; y < yLen; y++) {
				occupied[x][y] = false;
			}
		}

		int count = cl.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = cl.getChildAt(i);
			if (child.equals(ignoreView)) {
				continue;
			}
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
			
			int cellX = lp.cellX;
			int cellY = lp.cellY;
			if(lp.isOnPending){
				cellX = lp.tmpCellX;
				cellY = lp.tmpCellY;
			}

			int spanX = cellX + lp.spanX;
			int spanY = cellY + lp.spanY;
			for (int x = cellX; x < spanX  && x < xLen; x++) {
				for (int y = cellY; y < spanY && y < yLen; y++) {
					if(x < 0 || y < 0 ||x >= xLen || y >= yLen)continue;
					occupied[x][y] = true;
				}
			}
		}
	}
	
	/**
	 * Description: 统计CellLayout中单元格的占用情况，统计时不统计app和文件夹
	 * Author: guojy
	 * Date: 2012-8-29 下午02:28:36
	 */
	public static void findOccupiedCellsForApp(boolean[][] occupied, View ignoreView, CellLayout cl) {
		if(occupied == null){
			occupied = new boolean[CellLayoutConfig.getCountX()][CellLayoutConfig.getCountY()];
		}
		int xLen = occupied.length;
		int yLen = occupied[0].length;
		
		for (int x = 0; x < xLen; x++) {
			for (int y = 0; y < yLen; y++) {
				occupied[x][y] = false;
			}
		}

		int count = cl.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = cl.getChildAt(i);
			if (child instanceof FolderIconTextView || child instanceof IconMaskTextView
					|| child.equals(ignoreView)) {
				continue;
			}
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
			
			int cellX = lp.cellX;
			int cellY = lp.cellY;
			if(lp.isOnPending){
				cellX = lp.tmpCellX;
				cellY = lp.tmpCellY;
			}

			int spanX = cellX + lp.spanX;
			int spanY = cellY + lp.spanY;
			for (int x = cellX; x < spanX  && x < xLen; x++) {
				for (int y = cellY; y < spanY && y < yLen; y++) {
					if(x < 0 || y < 0 ||x >= xLen || y >= yLen)continue;
					occupied[x][y] = true;
				}
			}
		}
	}
	
	/**
	 * Description: celllayout中的View移动位置后，是否有空闲区域可以摆放目标部件
	 * Author: guojy
	 * Date: 2013-1-25 上午10:57:32
	 */
	public static boolean isVacantForReorder(boolean[][] mOccupied, int spanX, int spanY){
		int xLen = mOccupied.length;
		int yLen = mOccupied[0].length;
		int vacantCount = 0;
		for(int j = 0; j < yLen; j++){
			for(int i = 0; i < xLen; i++){
				if(!mOccupied[i][j]){
					vacantCount++;
				}
			}
		}
		return vacantCount >= spanX * spanY;
	}
	

	/**
	 * Description: 从CellLayout顶部开始找可以摆放该部件的第一个位置
	 * Author: guojy
	 * Date: 2012-8-29 下午02:31:06
	 * @param spanX 部件的span长度
	 * @param spanY 部件的span高度
	 */
	public static int[] findFirstVacantCell(boolean[][] mOccupied, int spanX, int spanY){
		try{
			int xLen = mOccupied.length;
			int yLen = mOccupied[0].length;
			for(int j = 0; j < yLen; j++){
				for(int i = 0; i < xLen; i++){
					if(isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
						return new int[]{i, j};
					}
					
				}
			}
			return null;
		} catch (Exception e) {
			Log.e("findFirstVacantCell", e.toString());
			return null;
		}
		
	}
	/**
	 * Description: 从CellLayout底部开始找可以摆放该部件的位置
	 * Author: guojy
	 * Date: 2013-1-16 上午9:42:48
	 */
	public static int[] findVacantCellFromBottom(boolean[][] mOccupied, int spanX, int spanY){
		try{
			int xLen = mOccupied.length;
			int yLen = mOccupied[0].length;
			for(int j = yLen - 1; j >= 0; j--){
				for(int i = 0; i < xLen; i++){
					if(isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
						return new int[]{i, j};
					}
					
				}
			}
			return null;
		} catch (Exception e) {
			Log.e("findVacantCellFromBottom", e.toString());
			return null;
		}
		
	}
	
	/**
	 * Description: 从CellLayout的特定位置开始找可以摆放该部件的位置
	 * Author: guojy
	 * Date: 2013-1-16 下午4:45:49
	 */
	public static int[] findVacantCellFromTargetCell(boolean[][] mOccupied, int spanX, int spanY, int targetCellX, int targetCellY){
		try{
			int yLen = mOccupied[0].length;
			int[] result = null;
			int startFindCellX = targetCellX;
			int startFindCellY = targetCellY;
			//查找策略：先查找目标位置所在的9宫格内是否有空闲位置;如果没有，再查找目标位置所在行是否有空闲位置;如果没有，再从目标行的上下行依次查找
			result = findVacantCellOnNineGrids(startFindCellX, startFindCellY, spanX, spanY, mOccupied);
			if(result != null){
				return result;
			}
			
			for(int k = 1; k < yLen; k++){
				result = findVacantCellOnLine(startFindCellX, startFindCellY, spanX, spanY, mOccupied);
				if(result != null){
					return result;
				}
				
				startFindCellY = targetCellY + k;
				result = findVacantCellOnLine(startFindCellX, startFindCellY, spanX, spanY, mOccupied);
				if(result != null){
					return result;
				}
				
				startFindCellY = targetCellY - k;
				result = findVacantCellOnLine(startFindCellX, startFindCellY, spanX, spanY, mOccupied);
				if(result != null){
					return result;
				}
			}
			
			return result;
		} catch (Exception e) {
			Log.e("findVacantCellFromTargetCell", e.toString());
			return null;
		}
	}
	/**
	 * Description: 查找目标位置所在的9宫格内的空闲位置
	 * Author: guojy
	 * Date: 2013-1-22 上午10:14:18
	 */
	private static int[] findVacantCellOnNineGrids(int targetCellX, int targetCellY, int spanX, int spanY, boolean[][] mOccupied){
		int i = targetCellX;
		int j = targetCellY;
		int[][] traversal = new int[][]{
				{i, j}, 
				{i - 1, j}, 
				{i + 1, j}, 
				{i, j - 1}, 
				{i, j + 1}, 
				{i - 1, j - 1},
				{i - 1, j + 1},
				{i + 1, j - 1},
				{i + 1, j + 1}};
		for(int k = 0; k < traversal.length; k ++){
			if(isTargetCellVacant(traversal[k][0], traversal[k][1], spanX, spanY, mOccupied)){
				return new int[]{traversal[k][0], traversal[k][1]};
			}
		}
		return null;
	}
	/**
	 * Description: 查找目标位置所在行是否有空闲位置(先查找目标位置是否可以，再从目标位置左右两边开始找)
	 * Author: guojy
	 * Date: 2013-1-16 下午5:19:06
	 */
	private static int[] findVacantCellOnLine(int targetCellX, int targetCellY, int spanX, int spanY, boolean[][] mOccupied){
		int xLen = mOccupied.length;
		int yLen = mOccupied[0].length;
		if(targetCellX < 0 || targetCellY < 0 || targetCellX >= xLen || targetCellY >= yLen){//越界判断
			return null;
		}
		
		for(int k = 1; k < xLen; k++){
			int i = targetCellX;
			int j = targetCellY;
			if(isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
				return new int[]{i, j};
			}
			
			i = targetCellX + k;
			if(i < xLen && isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
				return new int[]{i, j};
			}
			
			i = targetCellX - k;
			if(i >= 0 && isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
				return new int[]{i, j};
			}
		}
		
		return null;
	}
	/**
	 * Description: 查找目标位置是否空闲
	 * Author: guojy
	 * Date: 2013-1-16 下午5:19:17
	 */
	private static boolean isTargetCellVacant(int i, int j, int spanX, int spanY, boolean[][] mOccupied){
		int xLen = mOccupied.length;
		int yLen = mOccupied[0].length;
		if(i < 0 || j < 0 || i >= xLen || j >= yLen){//越界判断
			return false;
		}
		
		boolean flag = true;
		for(int m = 0; m < spanX; m++){
			for(int n = 0; n < spanY; n++){
				if(i+m >= xLen || j+n >= yLen || mOccupied[i+m][j+n]){
					flag = false;
					break;
				}
			}
			if(!flag) break;
		}
		
		return flag;
	}
	
	/**
	 * Description: 找出CellLayout上可以摆放该部件的所有位置
	 * Author: guojy
	 * Date: 2012-9-21 下午03:37:53
	 */
	public static ArrayList<int[]> findAllVacantCell(boolean[][] mOccupied, int spanX, int spanY){
		ArrayList<int[]> list = new ArrayList<int[]>();
		try{
			int xLen = mOccupied.length;
			int yLen = mOccupied[0].length;
			for(int j = 0; j < yLen; j++){
				for(int i = 0; i < xLen; i++){
					if(isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
						list.add(new int[]{i, j});
					}
					
				}
			}
			return list;
		} catch (Exception e) {
			Log.e("findAllVacantCell", e.toString());
			return list;
		}
		
	}
	
	/**
	 * Description: 按从上到下，从左到右找出CellLayout上可以摆放该部件的所有位置
	 * Author: Michael
	 * Date: 2013-4-26 下午17:17:53
	 */
	public static ArrayList<int[]> findAllVacantCellFromBottom(boolean[][] mOccupied, int spanX, int spanY){
		ArrayList<int[]> list = new ArrayList<int[]>();
		try{
			int xLen = mOccupied.length;
			int yLen = mOccupied[0].length;
			for(int j = yLen - 1; j >= 0; j--){
				for(int i = 0; i < xLen; i++){
					if(isTargetCellVacant(i, j, spanX, spanY, mOccupied)){
						list.add(new int[]{i, j});
					}
					
				}
			}
			return list;
		} catch (Exception e) {
			Log.e("findAllVacantCell", e.toString());
			return list;
		}
		
	}
	
	/**
	 * Description: 根据拖动app的大小，找出CellLayout上所有可放置该app区域
	 * Author: guojy
	 * Date: 2012-9-7 上午10:06:15
	 */
	public static CellLayout.CellInfo findAllVacantCells(boolean[][] occupied, int spanX, int spanY, int xCount, int yCount, int screen) {
		CellLayout.CellInfo cellInfo = new CellLayout.CellInfo();

		cellInfo.cellX = -1;
		cellInfo.cellY = -1;
		cellInfo.spanY = 0;
		cellInfo.spanX = 0;
		cellInfo.maxVacantSpanX = Integer.MIN_VALUE;
		cellInfo.maxVacantSpanXSpanY = Integer.MIN_VALUE;
		cellInfo.maxVacantSpanY = Integer.MIN_VALUE;
		cellInfo.maxVacantSpanYSpanX = Integer.MIN_VALUE;
		cellInfo.screen = screen;

		//找到最下(从下到上，从左到右)的空cell
		for (int i = yCount-1; i >= 0 ; i--) {
			for (int j = 0; j < xCount; j++) {
				if (occupied[j][i]) continue;
				//判断是否空闲
				boolean flag = true;
				for(int m = 0; m < spanX; m++){
					for(int n = 0; n < spanY; n++){
						if(j+m >= xCount || i+n >= yCount || occupied[j+m][i+n]){
							flag = false;
							break;
						}
					}
					if(!flag) break;
				}
				//添加
				if(flag){
					CellLayout.CellInfo.VacantCell cell = CellLayout.CellInfo.VacantCell.acquire();
					cell.cellX = j;
					cell.cellY = i;
					cell.spanX = spanX;
					cell.spanY = spanY;
					if (cell.spanX > cellInfo.maxVacantSpanX) {
						cellInfo.maxVacantSpanX = cell.spanX;
						cellInfo.maxVacantSpanXSpanY = cell.spanY;
					}
					if (cell.spanY > cellInfo.maxVacantSpanY) {
						cellInfo.maxVacantSpanY = cell.spanY;
						cellInfo.maxVacantSpanYSpanX = cell.spanX;
					}
					cellInfo.vacantCells.add(cell);
				}
				
			}
		}

		cellInfo.valid = cellInfo.vacantCells.size() > 0;

		return cellInfo;
	}
	
	
	
	public void initCellLayoutHelper() {
		Resources res = BaseConfig.getApplicationContext().getResources();
		springScreenBackground = res.getDrawable(R.drawable.edit_screen_bg);
		springNoVacantScreenBackground = res.getDrawable(R.drawable.edit_screen_full_bg);
		springDelScreenBtn = res.getDrawable(R.drawable.edit_screen_del_btn);
		springAddScreenBtn = res.getDrawable(R.drawable.edit_screen_add_btn);
	}
	public void cleanCellLayoutHelper(){
		springScreenBackground = null;
		springNoVacantScreenBackground = null;
		springDelScreenBtn = null;
		springAddScreenBtn = null;
		System.gc();
	}
	/**
	 * Description: 绘制编辑模式的屏背景
	 * Author: guojy
	 * Date: 2012-7-24 下午09:35:13
	 */
	public void drawSpringBackground(int alpha, Canvas canvas){
		springScreenBackground = BaseConfig.getApplicationContext().getResources().getDrawable(R.drawable.edit_screen_bg);
		setBackgroundBounds(springScreenBackground);
		springScreenBackground.setAlpha(alpha);
		springScreenBackground.draw(canvas);
	}
	/**
	 * Description: 绘制编辑模式的无空余间屏背景
	 * Author: guojy
	 * Date: 2012-7-24 下午09:35:25
	 */
	public void drawSpringNoVacantBackground(int alpha, Canvas canvas){
		if(springNoVacantScreenBackground == null){
			springNoVacantScreenBackground = BaseConfig.getApplicationContext().getResources().getDrawable(R.drawable.edit_screen_full_bg);
		}
		setBackgroundBounds(springNoVacantScreenBackground);
		springNoVacantScreenBackground.draw(canvas);
	}
	/**
	 * Description: 设置背景图位置
	 * Author: guojy
	 * Date: 2012-8-1 上午09:34:18
	 */
	private void setBackgroundBounds(Drawable bg){
//		final int fixLeftBgPic = ScreenUtil.dip2px(mContext, 3);//背景图有渐进变色的边距，用于校正该背景图边距
//		final int fixRightBgPic = ScreenUtil.dip2px(mContext, 3);
//		final int fixTopBgPic = ScreenUtil.dip2px(mContext, 3);
//		final int fixBottomBgPic = ScreenUtil.dip2px(mContext, 3);
//		bg.setBounds(-fixLeftBgPic, -fixTopBgPic, mCellLayoutWidthEx+fixRightBgPic, mCellLayoutHeightEx+fixBottomBgPic);
		bg.setBounds(0, 0, mCellLayoutWidthEx, mCellLayoutHeightEx);
	}
	/**
	 * Description: 绘制编辑模式的删除按钮，返回按钮的触点感应区
	 * Author: guojy
	 * Date: 2012-7-24 下午09:33:31
	 */
	public Rect drawSpringDelBtn(int alpha, Canvas canvas){
		int delBtnWidth = mCellLayoutWidthEx/10;
		int delBtnHeight = delBtnWidth;

		Context mContext = BaseConfig.getApplicationContext();
		int paddingTop = ScreenUtil.dip2px(mContext, 5);
		int paddingRight = ScreenUtil.dip2px(mContext, 2);
		int paddingLeft = mCellLayoutWidthEx - delBtnWidth - paddingRight;
		if(springDelScreenBtn == null){
			springDelScreenBtn = mContext.getResources().getDrawable(R.drawable.edit_screen_del_btn);
		}
		springDelScreenBtn.setBounds(paddingLeft, paddingTop, paddingLeft + delBtnWidth, paddingTop + delBtnHeight);
		springDelScreenBtn.setAlpha(alpha);
        springDelScreenBtn.draw(canvas);
        
        final int fixDelTouch = 45;//增加删除按钮感应范围，使得更易被点击
        return new Rect(paddingLeft-fixDelTouch, springTopMargin+paddingTop-fixDelTouch, 
        		mCellLayoutWidthEx, springTopMargin+paddingTop + delBtnHeight+fixDelTouch);
	}
	/**
	 * Description: 绘制编辑模式的添加屏按钮
	 * Author: guojy
	 * Date: 2012-7-24 下午09:34:50
	 */
	public void drawSpringAddBtn(int alpha, Canvas canvas){
		int addBtnWidth = mCellLayoutWidthEx/2;
		int addBtnHeight = addBtnWidth;
		
		int paddingTop = springMiddleY + springPageTranslationY - springTopMargin - addBtnHeight/2;
		int paddingLeft = springMiddleX-addBtnWidth/2;
		if(springAddScreenBtn == null){
			springAddScreenBtn = BaseConfig.getApplicationContext().getResources().getDrawable(R.drawable.edit_screen_add_btn);
		}
		springAddScreenBtn.setBounds(paddingLeft, paddingTop, paddingLeft + addBtnWidth, paddingTop + addBtnHeight);
		springAddScreenBtn.setAlpha((int)alpha);
		springAddScreenBtn.draw(canvas);
	}
	
	
	
	/**
	 * 从目标cell的当前行的开始查找 优先顺序如下数字显示
	 *  ......4.........
	 *  ...2..cell...1..
	 *  .....3..........
	 * @author Michael
	 * @createtime 2013-5-28 
	 * @param VacantCells 未被占用的cell
	 * @param currentXCell 目标cell的横坐标
	 * @param currentYCell 目标cell的纵坐标
	 * @return 返回排序后的cell
	 */
	public static void sortVacantCell(ArrayList<int[]> vacantCells, int currentXCell, int currentYCell){
		if(vacantCells == null){
			return ;
		}
		
		ArrayList<SortCell> list = new ArrayList<SortCell>();
		for (int[] is : vacantCells) {
			int xdelta = currentXCell - is[0];
			int ydelta = currentYCell - is[1];
			SortCell sortCell = new SortCell(is);
			int sortIndex = 1;
			if(xdelta > 0){
				sortIndex = sortIndex + xdelta;
			}else if(xdelta < 0){
				sortIndex = sortIndex + Math.abs(xdelta)*20;
			}
			
			if(ydelta > 0){
				sortIndex = sortIndex + ydelta*2000;
			}else if(ydelta < 0){
				sortIndex = sortIndex + Math.abs(ydelta)*200;
			}
			sortCell.setSortIndex(sortIndex);
			list.add(sortCell);
		}
		Collections.sort(list);
		vacantCells.clear();
		for (SortCell sortCell : list) {
			vacantCells.add(sortCell.getCell());
		}
	}
	
	public static class SortCell implements Comparable<SortCell>{
		private int[] cell;
		private int sortIndex;
		@Override
		public int compareTo(SortCell another) {
			if(another == null)
				return -1;
			// TODO Auto-generated method stub
			if(sortIndex - another.sortIndex == 0){
				return 0;
			}else if(sortIndex - another.sortIndex > 0){
				return 1;
			}else{
				return -1;
			}
		}
		public int getSortIndex() {
			return sortIndex;
		}
		public SortCell(int[] cell) {
			super();
			this.cell = cell;
		}
		
		public void setSortIndex(int sortIndex) {
			this.sortIndex = sortIndex;
		}
		public int[] getCell() {
			return cell;
		}
		
	}
}
