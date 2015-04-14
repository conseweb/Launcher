package com.nd.hilauncherdev.launcher.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.config.CellLayoutConfig;
import com.nd.hilauncherdev.launcher.info.ItemInfo;
import com.nd.hilauncherdev.launcher.model.BaseLauncherModel;
import com.nd.hilauncherdev.launcher.screens.CellLayout;
import com.nd.hilauncherdev.launcher.screens.ScreenViewGroup;
import com.nd.hilauncherdev.launcher.screens.CellLayout.LayoutParams;
import com.nd.hilauncherdev.launcher.touch.WorkspaceDragAndDropImpl;
import com.nd.hilauncherdev.launcher.view.icon.ui.IconSizeManager;
import com.nd.hilauncherdev.framework.AnyCallbacks.OnDragEventCallback;
import com.nd.hilauncherdev.kitset.GpuControler;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
//import com.nd.hilauncherdev.notification.LauncherNotificationHelper;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * 
 * Description: 桌面拖动时挤动其它图标位移实现
 * Author: guojy
 * Date: 2013-5-7 下午7:04:15
 */
public class CellLayoutReorder {
	/**
	 * 拖动图标时图标互换位置处理
	 */
    private int[] mDirectionVector = new int[2];//受影响view动画移动方向{x, y}：1 往前或上移；  -1往后或下移；
    private Rect mOccupiedRect = new Rect();
    
    private final int[] mTmpPoint = new int[2];
    int[] mTempLocation = new int[2];
    boolean[][] mTmpOccupied;
    
    private ArrayList<View> mIntersectingViews = new ArrayList<View>();//所有受拖动图标影响，需要动画移动位置view的集合
    
    private boolean mItemPlacementDirty = false; //cellLayout是否处于view移动动画还原状态
    public static final int REORDER_ANIMATION_TRANS_DURATION = 150;//图标移动动画时间
    public static final long REORDER_ANIMATION_HINT_DURATION = 300;//图标抖动动画时间
    public boolean isOnReorderAnimation = false;//是否处于图标拖动引起的位置变换动画中
	private HashMap<View, ReorderHintAnimation> mShakeAnimators = new HashMap<View, ReorderHintAnimation>();//抖动动画Map，用于固件4.0以上
	private HashMap<View, Animation> mReorderAnimations = new HashMap<View, Animation>();//抖动动画Map，用于固件4.0以下
	private static final float REORDER_HINT_MAGNITUDE = 0.12f;
	private static final int REORDER_ANIMATION_DURATION = 150;
	private float mReorderHintAnimationMagnitude;
	private long adjustHintAnimationTime = 0L;
	
	private final Stack<Rect> mTempRectStack = new Stack<Rect>();
    
    private CellLayout mCellLayout;
    private ScreenViewGroup mWorkspace;
    private int mCountX, mCountY;
    private int mCellWidth;
	private int mCellHeight;
	private int mCellGapX, mCellGapY;
	
	/**
	 * 用于Iphone风格挤动
	 */
	private int[] lastTarget = {-1, -1};
    
	public CellLayoutReorder(CellLayout mCellLayout){
		this.mCellLayout = mCellLayout;
	}
    
	public void setupCellSize(int mCellWidth, int mCellHeight, int mCellGapX, int mCellGapY){
		this.mCellWidth = mCellWidth;
		this.mCellHeight = mCellHeight;
		this.mCellGapX = mCellGapX;
		this.mCellGapY = mCellGapY;
	}
    
	public void setupCountXY(int mCountX, int mCountY){
		this.mCountX = mCountX;
		this.mCountY = mCountY;
	}
	
	public void setWorkspace(ScreenViewGroup mWorkspace) {
		this.mWorkspace = mWorkspace;
	}

	public void setTmpOccupied(boolean isPortrait, int mShortAxisCells, int mLongAxisCells){
		if (isPortrait) {
			mTmpOccupied = new boolean[mShortAxisCells][mLongAxisCells];
		} else {
			mTmpOccupied = new boolean[mLongAxisCells][mShortAxisCells];
		}
	}

	
	//============================================拖动桌面图标的挤动动画实现=======================================================//
    private class CellAndSpan {
        int cellX, cellY;
        int spanX, spanY;

        public CellAndSpan(int x, int y, int spanX, int spanY) {
            this.cellX = x;
            this.cellY = y;
            this.spanX = spanX;
            this.spanY = spanY;
        }
    }
    
    //记录cellLayout内位置情况详细信息，用于拖动图标时移动受影响的view的位置
    private class ItemConfiguration {
        HashMap<View, CellAndSpan> map = new HashMap<View, CellAndSpan>();
        boolean isSolution = false;
        int dragViewSpanX, dragViewSpanY;

        int area() {
            return dragViewSpanX * dragViewSpanY;
        }
    }
    
    //计算(x,y)与cell中心位置的距离
    public float getDistanceFromCell(int x, int y, int[] cell) {
    	int[] mTmpPoint = CellLayoutConfig.getCenterXY(cell[0], cell[1], mCellLayout.getCellWidth(), mCellLayout.getCellHeight());
        float distance = (float) Math.sqrt( Math.pow(x - mTmpPoint[0], 2) +
                Math.pow(y - mTmpPoint[1], 2));
        return distance;
    }
    
    //目标位置是否被占用
    public boolean isNearestDropLocationOccupied(int pixelX, int pixelY, int spanX, int spanY,
            View dragView, int[] targetCell) {
//    	result = findNearestArea(pixelX, pixelY, spanX, spanY, result);
        getViewsIntersectingRegion(targetCell[0], targetCell[1], spanX, spanY, dragView, null,
                mIntersectingViews);
        return mIntersectingViews.size() > 0;
    }
    /**
     * Find a starting cell position that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreView Considers space occupied by this view as unoccupied
     * @param result Previously returned value to possibly recycle.
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    public int[] findNearestArea(
            int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return mCellLayout.findNearestArea(pixelX, pixelY, spanX, spanY, null, false, result);
    }
    
    //根据给定的区域，获取该区域内所有view集合
    private void getViewsIntersectingRegion(int cellX, int cellY, int spanX, int spanY,
            View dragView, Rect boundingRect, ArrayList<View> intersectingViews) {
        if (boundingRect != null) {
            boundingRect.set(cellX, cellY, cellX + spanX, cellY + spanY);
        }
        intersectingViews.clear();
        Rect r0 = new Rect(cellX, cellY, cellX + spanX, cellY + spanY);
        Rect r1 = new Rect();
        ViewGroup cl = mWorkspace.getCurrentCellLayout();
        final int count = cl.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = cl.getChildAt(i);
            if (child == dragView) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
        	r1.set(lp.cellX, lp.cellY, lp.cellX + lp.spanX, lp.cellY + lp.spanY);
        	boolean isIntersect = false;
        	if(child instanceof OnDragEventCallback){
        		isIntersect = Rect.intersects(r0, r1);
        	}else if(mWorkspace.getDragController() !=null &&
        			mWorkspace.getDragController().getDragView() != null){//降低与小部件换位时的灵敏度
        		int[] loc = mWorkspace.getDragController().getDragView().getDragCenterPoints();
        		if(mWorkspace.isOnSpringMode()){//编辑模式下调整坐标
        			BaseCellLayoutHelper.springToNormalCoordinate(loc);
        		}
        		int[] dragOverLoc = new int[2];
        		child.getLocationInWindow(dragOverLoc);
				float margin = child.getHeight()*0.4f;
				float centerY = dragOverLoc[1] + child.getHeight()/2;
				
        		if(Rect.intersects(r0, r1) && (Math.abs(loc[1] - centerY) < margin)){
        			isIntersect = true;
        		}
        	}
        	
            if (isIntersect) {
                mIntersectingViews.add(child);
                if (boundingRect != null) {
                    boundingRect.union(r1);
                }
            }
        }
    }
 
    
    //拖动图标时，移动受影响的view的位置
    /**
     * 
     * @param pixelX
     * @param pixelY
     * @param minSpanX
     * @param minSpanY
     * @param spanX
     * @param spanY
     * @param child
     * @param notDefaultStyle 是否android4.0默认的挤动方式
     */
    public void createArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            View child, boolean notDefaultStyle) {
        // First we determine if things have moved enough to cause a different layout
//        result = findNearestArea(pixelX, pixelY, spanX, spanY, result);


        //计算被dragview覆盖的view要移动的方向
        getDirectionVectorForDrop(pixelX, pixelY, spanX, spanY, child, mDirectionVector);
        
        ItemConfiguration swapSolution;
        if(notDefaultStyle){
            swapSolution = simpleSwapForApp(pixelX, pixelY, minSpanX, minSpanY,
                    spanX,  spanY, mDirectionVector, child,  true,  new ItemConfiguration());
        }else{
        	swapSolution = simpleSwap(pixelX, pixelY, minSpanX, minSpanY,
                    spanX,  spanY, mDirectionVector, child,  true,  new ItemConfiguration());
        }
       

        // We attempt the approach which doesn't shuffle views at all
        ItemConfiguration noShuffleSolution = findConfigurationNoShuffle(pixelX, pixelY, minSpanX,
                minSpanY, spanX, spanY, child, new ItemConfiguration());

        ItemConfiguration finalSolution = null;
        if (swapSolution.isSolution && swapSolution.area() >= noShuffleSolution.area()) {
            finalSolution = swapSolution;
        } else if (noShuffleSolution.isSolution) {
            finalSolution = noShuffleSolution;
        }

        if (finalSolution != null) {
            setItemPlacementDirty(true);
            animateItemsToSolution(finalSolution, child);
        } 

//        requestLayout();
    }
    
    //生成cellLayout内view的目标位置详细信息，用于view动画移动
    ItemConfiguration simpleSwap(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX,
            int spanY, int[] direction, View child, boolean decX, ItemConfiguration solution) {
        // 记录当前各cell位置情况到solution
        copyCurrentStateToSolution(solution);
        // 记录当前cell的占用情况到mTmpOccupied，不包括被拖动的view
        mCellLayout.initOccupied(child, false);
        copyOccupiedArray(mTmpOccupied);

        int result[] = new int[2];
        result = findNearestArea(pixelX, pixelY, spanX, spanY, result);

        boolean success = false;
        // First we try the exact nearest position of the item being dragged,
        // we will then want to try to move this around to other neighbouring positions
        success = rearrangementExists(result[0], result[1], spanX, spanY, direction, child,
                solution);

        if (!success) {
            // We try shrinking the widget down to size in an alternating pattern, shrink 1 in
            // x, then 1 in y etc.
            if (spanX > minSpanX && (minSpanY == spanY || decX)) {
                return simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX - 1, spanY, direction,
                        child, false, solution);
            } else if (spanY > minSpanY) {
                return simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY - 1, direction,
                        child, true, solution);
            }
            solution.isSolution = false;
        } else {
            solution.isSolution = true;
            solution.dragViewSpanX = spanX;
            solution.dragViewSpanY = spanY;
        }
        return solution;
    }
   
    //用于类似Iphone风格的View挤动
    ItemConfiguration simpleSwapForApp(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX,
            int spanY, int[] direction, View child, boolean decX, ItemConfiguration solution) {
    	boolean isDragOverScreen = false;
    	if(child != null){
    		if(!(child.getTag() instanceof ItemInfo) || direction[0] == 0){
        		solution.isSolution = false;
        		return solution;
        	}
        	ItemInfo item = (ItemInfo)child.getTag();
        	isDragOverScreen = item.screen != mCellLayout.mCellLayoutLocation;//是否跨屏拖动
    	}else{//拖动托盘图标到workspace
    		isDragOverScreen = true;
    	}
    	
    	
    	//1.根据坐标，计算目标位置
    	mCellLayout.initOccupied(child, false);
        copyOccupiedArray(mTmpOccupied);
        final int[] target = findNearestArea(pixelX, pixelY, spanX, spanY, null);
        View tempView = getReorderChildAt(target[0], target[1]);
//        if(tempView == null && !mItemPlacementDirty){
    	if(tempView == null){
        	solution.isSolution = false;
    		return solution;
        }
        
        boolean isPre = false; //目标位置是否在原位置之前
        if(child != null){
        	int[] orgin = {lastTarget[0], lastTarget[1]};
        	if(lastTarget[0] < 0){
        		CellLayout.LayoutParams cl = (LayoutParams) child.getLayoutParams();
        		orgin = new int[]{cl.cellX, cl.cellY};
        	}
            if(orgin[1] > target[1] || (orgin[1] == target[1] && orgin[0] > target[0])){
            	isPre = true;
            }
        }else{
        	isPre = true;
        }
        
        
        if(isDragOverScreen){//跨屏拖动时，若目标位置之后有空位，挤动往右滑，否则往左滑；若目标屏满屏，则不挤动
        	boolean hasVacant = false;
        	int x = target[0];
        	for (int y = target[1]; y < mCountY; y++) {
                for (; x < mCountX; x++) {
                	if(!mTmpOccupied[x][y]){
                		hasVacant = true;
                		break;
                	}
                }
                x = 0;
        	}
        	isPre = hasVacant;
        }
        
        //2.修正目标位置
        if(direction[0] == -1){//往左挤
        	boolean hasVacantOnLeft = false;
			for(int i = target[0] - 1; i >= 0; i --){
				if(!mTmpOccupied[i][target[1]]){
					hasVacantOnLeft = true;
					break;
				}
			}
			if(target[0]+1 >= mCountX){
				solution.isSolution = false;
        		return solution;
			}
			//如果目标位置左边没空位，并且不是这种情况：目标位置右边被占用，目标位置在原位置之后
			if(!hasVacantOnLeft && !(mTmpOccupied[target[0]+1][target[1]] && !isPre)){
				if(isPre && mTmpOccupied[target[0]+1][target[1]]){
					target[0] ++;
				}else{
//					solution.isSolution = false;
//	        		return solution;
				}
			}
			//如果目标位置左边有空位，并且目标位置右边没被占用
			if(hasVacantOnLeft && !mTmpOccupied[target[0]+1][target[1]]){
				isPre = false;
			}
				
        }else if(direction[0] == 1){//往右挤
        	boolean hasVacantOnRight = false;
			for(int i = target[0] + 1; i < mCountX; i ++){
				if(!mTmpOccupied[i][target[1]]){
					hasVacantOnRight = true;
					break;
				}
			}
			if(target[0]-1 < 0){
				solution.isSolution = false;
        		return solution;
			}
			//如果目标位置右边没空位，并且不是这种情况：目标位置左边被占用，目标位置在原位置之前
			if(!hasVacantOnRight && !(mTmpOccupied[target[0]-1][target[1]] && isPre)){
				if(!isPre && mTmpOccupied[target[0]-1][target[1]]){
					target[0] --;
				}else{
//					solution.isSolution = false;
//	        		return solution;
				}
				
			}
			//如果目标位置右边有空位，并且目标位置左边没被占用
			if(hasVacantOnRight && !mTmpOccupied[target[0]-1][target[1]]){
				isPre = true;
			}
        }
        
        //3.计算受影响View的移动位置
        ArrayList<int[]> vacantCellList = new ArrayList<int[]>();//目标位置之后所有可放置app的Cell位置(小部件所占位置不能放置app)
		ArrayList<View> allRemoveViewList = new ArrayList<View>();//目标位置之后所有需要移动位置的app
		boolean isFinished = false;
    	int x = target[0];
        if(isPre){
        	for (int y = target[1]; y < mCountY; y++) {
                for (; x < mCountX; x++) {
                	if(!mTmpOccupied[x][y]){
                		vacantCellList.add(new int[]{x, y});
                		isFinished = true;
                		break;
                	}else{
  	  					View v = getReorderChildAt(x, y);
//  	  					View v = mCellLayout.getChildAt(x, y);
  	  					if(v == null)
  	  						continue;
  	  					LayoutParams lp3 = (LayoutParams) v.getLayoutParams();
  	  					if(lp3.spanX == 1 && lp3.spanY == 1){
  	  						if(x != target[0] || y != target[1]){
  	  							vacantCellList.add(new int[]{x, y});
  	  						}
  	  						allRemoveViewList.add(v);
  	  					}
                	}
                }
                if(isFinished){
                	break;
                }
                x = 0;
        	}
        }else{
        	for (int y = target[1]; y >= 0; y --) {
                for (; x >= 0; x --) {
                	if(!mTmpOccupied[x][y]){
                		vacantCellList.add(new int[]{x, y});
                		isFinished = true;
                		break;
                	}else{
  	  					View v = getReorderChildAt(x, y);
//  	  					View v = mCellLayout.getChildAt(x, y);
  	  					if(v == null)
  	  						continue;
  	  					LayoutParams lp3 = (LayoutParams) v.getLayoutParams();
  	  					if(lp3.spanX == 1 && lp3.spanY == 1){
  	  						if(x != target[0] || y != target[1]){
  	  							vacantCellList.add(new int[]{x, y});
  	  						}
  	  						allRemoveViewList.add(v);
  	  					}
                	}
                }
                if(isFinished){
                	break;
                }
                x = mCountX - 1;
        	}
        }
        
        if(vacantCellList.size() !=  allRemoveViewList.size()){//防止在位移动画过程中挤动或是跨屏移动到满屏celllayout时
    		solution.isSolution = false;
    		return solution;
        }
        
        int vacantSize = vacantCellList.size();
		for(int i = 0; i < vacantSize; i ++){
			int[] newPS = vacantCellList.get(i);
			solution.map.put(allRemoveViewList.get(i), new CellAndSpan(newPS[0], newPS[1], 1, 1));
		}
		solution.dragViewSpanX = 1;
        solution.dragViewSpanY = 1;
    	solution.isSolution = true;
    	lastTarget = new int[]{target[0], target[1]};
    	return solution;
    }
    
    public void reset(){
    	lastTarget = new int[]{-1, -1};
    }
    
    
    private void copyCurrentStateToSolution(ItemConfiguration solution) {
    	ViewGroup cl = mWorkspace.getCurrentCellLayout();
        int childCount = cl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = cl.getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            CellAndSpan c;
        	if (lp.isOnPending) {
                c = new CellAndSpan(lp.tmpCellX, lp.tmpCellY, lp.spanX, lp.spanY);
            } else {
                c = new CellAndSpan(lp.cellX, lp.cellY, lp.spanX, lp.spanY);
            }
            solution.map.put(child, c);
        }
    }
    
    private void copyOccupiedArray(boolean[][] occupied) {
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                occupied[i][j] = mCellLayout.mOccupied[i][j];
            }
        }
    }
    //重新安排cellLayout内view的位置，保存位置信息，用于view动画移动
    private boolean rearrangementExists(int cellX, int cellY, int spanX, int spanY, int[] direction,
            View ignoreView, ItemConfiguration solution) {
        // Return early if get invalid cell positions
        if (cellX < 0 || cellY < 0) return false;

        mIntersectingViews.clear();
        mOccupiedRect.set(cellX, cellY, cellX + spanX, cellY + spanY);

        // Mark the desired location of the view currently being dragged.
        if (ignoreView != null) {
            CellAndSpan c = solution.map.get(ignoreView);
            if (c != null) {
                c.cellX = cellX;
                c.cellY = cellY;
            }
        }
        Rect r0 = new Rect(cellX, cellY, cellX + spanX, cellY + spanY);
        Rect r1 = new Rect();
        for (View child: solution.map.keySet()) {
            if (child == ignoreView) continue;
            CellAndSpan c = solution.map.get(child);
            //LayoutParams lp = (LayoutParams) child.getLayoutParams();
            r1.set(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
            if (Rect.intersects(r0, r1)) {
                mIntersectingViews.add(child);
            }
        }

        // First we try to find a solution which respects the push mechanic. That is, 
        // we try to find a solution such that no displaced item travels through another item
        // without also displacing that item.
        if (attemptPushInDirection(mIntersectingViews, mOccupiedRect, direction, ignoreView,
                solution)) {
            return true;
        }

        // Next we try moving the views as a block, but without requiring the push mechanic.
        if (addViewsToTempLocation(mIntersectingViews, mOccupiedRect, direction, false, ignoreView,
                solution)) {
            return true;
        }

        // Ok, they couldn't move as a block, let's move them individually
        for (View v : mIntersectingViews) {
            if (!addViewToTempLocation(v, mOccupiedRect, direction, solution)) {
                return false;
            }
        }
        return true;
    }
    
    // This method tries to find a reordering solution which satisfies the push mechanic by trying
    // to push items in each of the cardinal directions, in an order based on the direction vector
    // passed.
    private boolean attemptPushInDirection(ArrayList<View> intersectingViews, Rect occupied,
            int[] direction, View ignoreView, ItemConfiguration solution) {
        if ((Math.abs(direction[0]) + Math.abs(direction[1])) > 1) {
            // If the direction vector has two non-zero components, we try pushing 
            // separately in each of the components.
            int temp = direction[1];
            direction[1] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            direction[1] = temp;
            temp = direction[0];
            direction[0] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // Revert the direction
            direction[0] = temp;

            // Now we try pushing in each component of the opposite direction
            direction[0] *= -1;
            direction[1] *= -1;
            temp = direction[1];
            direction[1] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }

            direction[1] = temp;
            temp = direction[0];
            direction[0] = 0;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // revert the direction
            direction[0] = temp;
            direction[0] *= -1;
            direction[1] *= -1;
            
        } else {
            // If the direction vector has a single non-zero component, we push first in the
            // direction of the vector
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }

            // Then we try the opposite direction
            direction[0] *= -1;
            direction[1] *= -1;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // Switch the direction back
            direction[0] *= -1;
            direction[1] *= -1;
            
            // If we have failed to find a push solution with the above, then we try 
            // to find a solution by pushing along the perpendicular axis.

            // Swap the components
            int temp = direction[1];
            direction[1] = direction[0];
            direction[0] = temp;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }

            // Then we try the opposite direction
            direction[0] *= -1;
            direction[1] *= -1;
            if (addViewsToTempLocation(intersectingViews, occupied, direction, true,
                    ignoreView, solution)) {
                return true;
            }
            // Switch the direction back
            direction[0] *= -1;
            direction[1] *= -1;

            // Swap the components back
            temp = direction[1];
            direction[1] = direction[0];
            direction[0] = temp;
        }
        return false;
    }
    
    private boolean addViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop,
            int[] direction, boolean push, View dragView, ItemConfiguration currentState) {
        if (views.size() == 0) return true;

        boolean success = false;
        Rect boundingRect = null;
        // We construct a rect which represents the entire group of views passed in
        for (View v: views) {
            CellAndSpan c = currentState.map.get(v);
            if (boundingRect == null) {
                boundingRect = new Rect(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
            } else {
                boundingRect.union(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
            }
        }

        @SuppressWarnings("unchecked")
        ArrayList<View> dup = (ArrayList<View>) views.clone();
        // We try and expand the group of views in the direction vector passed, based on
        // whether they are physically adjacent, ie. based on "push mechanics".
        while (push && addViewInDirection(dup, boundingRect, direction, mTmpOccupied, dragView,
                currentState)) {
        }

        // Mark the occupied state as false for the group of views we want to move.
        for (View v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.cellX, c.cellY, c.spanX, c.spanY, mTmpOccupied, false);
        }

        boolean[][] blockOccupied = new boolean[boundingRect.width()][boundingRect.height()];
        int top = boundingRect.top;
        int left = boundingRect.left;
        // We mark more precisely which parts of the bounding rect are truly occupied, allowing
        // for tetris-style interlocking.
        for (View v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.cellX - left, c.cellY - top, c.spanX, c.spanY, blockOccupied, true);
        }

        markCellsForRect(rectOccupiedByPotentialDrop, mTmpOccupied, true);

        if (push) {
            findNearestAreaInDirection(boundingRect.left, boundingRect.top, boundingRect.width(),
                    boundingRect.height(), direction, mTmpOccupied, blockOccupied, mTempLocation);
        } else {
            findNearestArea(boundingRect.left, boundingRect.top, boundingRect.width(),
                    boundingRect.height(), direction, mTmpOccupied, blockOccupied, mTempLocation);
        }

        // If we successfuly found a location by pushing the block of views, we commit it
        if (mTempLocation[0] >= 0 && mTempLocation[1] >= 0) {
            int deltaX = mTempLocation[0] - boundingRect.left;
            int deltaY = mTempLocation[1] - boundingRect.top;
            for (View v: dup) {
                CellAndSpan c = currentState.map.get(v);
                c.cellX += deltaX;
                c.cellY += deltaY;
            }
            success = true;
        }

        // In either case, we set the occupied array as marked for the location of the views
        for (View v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.cellX, c.cellY, c.spanX, c.spanY, mTmpOccupied, true);
        }
        return success;
    }
    
    
    // This method looks in the specified direction to see if there is an additional view
    // immediately adjecent in that direction
    private boolean addViewInDirection(ArrayList<View> views, Rect boundingRect, int[] direction,
            boolean[][] occupied, View dragView, ItemConfiguration currentState) {
        boolean found = false;

        ViewGroup cl = mWorkspace.getCurrentCellLayout();
        
        int childCount = cl.getChildCount();
        Rect r0 = new Rect(boundingRect);
        Rect r1 = new Rect();

        int deltaX = 0;
        int deltaY = 0;
        if (direction[1] < 0) {
            r0.set(r0.left, r0.top - 1, r0.right, r0.bottom);
            deltaY = -1;
        } else if (direction[1] > 0) {
            r0.set(r0.left, r0.top, r0.right, r0.bottom + 1);
            deltaY = 1;
        } else if (direction[0] < 0) {
            r0.set(r0.left - 1, r0.top, r0.right, r0.bottom);
            deltaX = -1;
        } else if (direction[0] > 0) {
            r0.set(r0.left, r0.top, r0.right + 1, r0.bottom);
            deltaX = 1;
        }

        for (int i = 0; i < childCount; i++) {
            View child = cl.getChildAt(i);
            if (views.contains(child) || child == dragView) continue;
            CellAndSpan c = currentState.map.get(child);

            //LayoutParams lp = (LayoutParams) child.getLayoutParams();
            r1.set(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
            if (Rect.intersects(r0, r1)) {
                boolean pushed = false;
                for (int x = c.cellX; x < c.cellX + c.spanX; x++) {
                    for (int y = c.cellY; y < c.cellY + c.spanY; y++) {
                        boolean inBounds = x - deltaX >= 0 && x -deltaX < mCountX
                                && y - deltaY >= 0 && y - deltaY < mCountY;
                        if (inBounds && occupied[x - deltaX][y - deltaY]) {
                            pushed = true;
                        }
                    }
                }
                if (pushed) {
                    views.add(child);
                    boundingRect.union(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
                    found = true;
                }
            }
        }
        return found;
    }
    
    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied,
            boolean value) {
        if (cellX < 0 || cellY < 0) return;
        for (int x = cellX; x < cellX + spanX && x < mCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCountY; y++) {
                occupied[x][y] = value;
            }
        }
    }
    
    private void markCellsForRect(Rect r, boolean[][] occupied, boolean value) {
        markCellsForView(r.left, r.top, r.width(), r.height(), occupied, value);
    }
    
    private int[] findNearestAreaInDirection(int cellX, int cellY, int spanX, int spanY, 
            int[] direction,boolean[][] occupied,
            boolean blockOccupied[][], int[] result) {
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        bestXY[0] = -1;
        bestXY[1] = -1;
        float bestDistance = Float.MAX_VALUE;

        // We use this to march in a single direction
        if ((direction[0] != 0 && direction[1] != 0) ||
                (direction[0] == 0 && direction[1] == 0)) {
            return bestXY;
        }

        // This will only incrememnet one of x or y based on the assertion above
        int x = cellX + direction[0];
        int y = cellY + direction[1];
        while (x >= 0 && x + spanX <= mCountX && y >= 0 && y + spanY <= mCountY) {

            boolean fail = false;
            for (int i = 0; i < spanX; i++) {
                for (int j = 0; j < spanY; j++) {
                    if (occupied[x + i][y + j] && (blockOccupied == null || blockOccupied[i][j])) {
                        fail = true;                    
                    }
                }
            }
            if (!fail) {
                float distance = (float)
                        Math.sqrt((x - cellX) * (x - cellX) + (y - cellY) * (y - cellY));
                if (Float.compare(distance,  bestDistance) < 0) {
                    bestDistance = distance;
                    bestXY[0] = x;
                    bestXY[1] = y;
                }
            }
            x += direction[0];
            y += direction[1];
        }
        return bestXY;
    }
    
    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location, and will also weigh in a suggested direction vector of the
     * desired location. This method computers distance based on unit grid distances,
     * not pixel distances.
     *
     * @param cellX The X cell nearest to which you want to search for a vacant area.
     * @param cellY The Y cell nearest which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param direction The favored direction in which the views should move from x, y
     * @param exactDirectionOnly If this parameter is true, then only solutions where the direction
     *        matches exactly. Otherwise we find the best matching direction.
     * @param occoupied The array which represents which cells in the CellLayout are occupied
     * @param blockOccupied The array which represents which cells in the specified block (cellX,
     *        cellY, spanX, spanY) are occupied. This is used when try to move a group of views. 
     * @param result Array in which to place the result, or null (in which case a new array will
     *        be allocated)
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    private int[] findNearestArea(int cellX, int cellY, int spanX, int spanY, int[] direction,
            boolean[][] occupied, boolean blockOccupied[][], int[] result) {
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        float bestDistance = Float.MAX_VALUE;
        int bestDirectionScore = Integer.MIN_VALUE;

        final int countX = mCountX;
        final int countY = mCountY;

        for (int y = 0; y < countY - (spanY - 1); y++) {
            inner:
            for (int x = 0; x < countX - (spanX - 1); x++) {
                // First, let's see if this thing fits anywhere
                for (int i = 0; i < spanX; i++) {
                    for (int j = 0; j < spanY; j++) {
                        if (occupied[x + i][y + j] && (blockOccupied == null || blockOccupied[i][j])) {
                            continue inner;
                        }
                    }
                }

                float distance = (float)
                        Math.sqrt((x - cellX) * (x - cellX) + (y - cellY) * (y - cellY));
                int[] curDirection = mTmpPoint;
                computeDirectionVector(x - cellX, y - cellY, curDirection);
                // The direction score is just the dot product of the two candidate direction
                // and that passed in.
                int curDirectionScore = direction[0] * curDirection[0] +
                        direction[1] * curDirection[1];
                boolean exactDirectionOnly = false;
                boolean directionMatches = direction[0] == curDirection[0] &&
                        direction[0] == curDirection[0];
                if ((directionMatches || !exactDirectionOnly) &&
                        Float.compare(distance,  bestDistance) < 0 || (Float.compare(distance,
                        bestDistance) == 0 && curDirectionScore > bestDirectionScore)) {
                    bestDistance = distance;
                    bestDirectionScore = curDirectionScore;
                    bestXY[0] = x;
                    bestXY[1] = y;
                }
            }
        }

        // Return -1, -1 if no suitable location found
        if (bestDistance == Float.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        return bestXY;
    }
    
    /*
     * Returns a pair (x, y), where x,y are in {-1, 0, 1} corresponding to vector between
     * the provided point and the provided cell
     */
    private void computeDirectionVector(float deltaX, float deltaY, int[] result) {
        double angle = Math.atan(((float) deltaY) / deltaX);

        result[0] = 0;
        result[1] = 0;
        if (Math.abs(Math.cos(angle)) > 0.5f) {
            result[0] = (int) Math.signum(deltaX);
        }
        if (Math.abs(Math.sin(angle)) > 0.5f) {
            result[1] = (int) Math.signum(deltaY);
        }
    }
    
    private boolean addViewToTempLocation(View v, Rect rectOccupiedByPotentialDrop,
            int[] direction, ItemConfiguration currentState) {
        CellAndSpan c = currentState.map.get(v);
        boolean success = false;
        markCellsForView(c.cellX, c.cellY, c.spanX, c.spanY, mTmpOccupied, false);
        markCellsForRect(rectOccupiedByPotentialDrop, mTmpOccupied, true);

        findNearestArea(c.cellX, c.cellY, c.spanX, c.spanY, direction, mTmpOccupied, null, mTempLocation);

        if (mTempLocation[0] >= 0 && mTempLocation[1] >= 0) {
            c.cellX = mTempLocation[0];
            c.cellY = mTempLocation[1];
            success = true;

        }
        markCellsForView(c.cellX, c.cellY, c.spanX, c.spanY, mTmpOccupied, true);
        return success;
    }
    
    ItemConfiguration findConfigurationNoShuffle(int pixelX, int pixelY, int minSpanX, int minSpanY,
            int spanX, int spanY, View dragView, ItemConfiguration solution) {
        int[] result = new int[2];
        int[] resultSpan = new int[2];
        findNearestVacantArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, null, result,
                resultSpan);
        if (result[0] >= 0 && result[1] >= 0) {
            copyCurrentStateToSolution(solution);
            solution.dragViewSpanX = resultSpan[0];
            solution.dragViewSpanY = resultSpan[1];
            solution.isSolution = true;
        } else {
            solution.isSolution = false;
        }
        return solution;
    }
    
    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param minSpanX The minimum horizontal span required
     * @param minSpanY The minimum vertical span required
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreView Considers space occupied by this view as unoccupied
     * @param result Previously returned value to possibly recycle.
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    int[] findNearestVacantArea(int pixelX, int pixelY, int minSpanX, int minSpanY,
            int spanX, int spanY, View ignoreView, int[] result, int[] resultSpan) {
        return findNearestArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, ignoreView, true,
                result, resultSpan, mCellLayout.mOccupied);
    }
    
    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param minSpanX The minimum horizontal span required
     * @param minSpanY The minimum vertical span required
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreOccupied If true, the result can be an occupied cell
     * @param result Array in which to place the result, or null (in which case a new array will
     *        be allocated)
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            View ignoreView, boolean ignoreOccupied, int[] result, int[] resultSpan,
            boolean[][] occupied) {
        lazyInitTempRectStack();
        // mark space take by ignoreView as available (method checks if ignoreView is null)
//        markCellsAsUnoccupiedForView(ignoreView, occupied);
        mCellLayout.markCellsAsUnoccupiedForView(ignoreView);

        // For items with a spanX / spanY > 1, the passed in point (pixelX, pixelY) corresponds
        // to the center of the item, but we are searching based on the top-left cell, so
        // we translate the point over to correspond to the top-left.
        pixelX -= (mCellWidth + mCellGapX) * (spanX - 1) / 2f;
        pixelY -= (mCellHeight+ mCellGapY) * (spanY - 1) / 2f;

        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        double bestDistance = Double.MAX_VALUE;
        final Rect bestRect = new Rect(-1, -1, -1, -1);
        final Stack<Rect> validRegions = new Stack<Rect>();

        final int countX = mCountX;
        final int countY = mCountY;

        if (minSpanX <= 0 || minSpanY <= 0 || spanX <= 0 || spanY <= 0 ||
                spanX < minSpanX || spanY < minSpanY) {
            return bestXY;
        }

        for (int y = 0; y < countY - (minSpanY - 1); y++) {
            inner:
            for (int x = 0; x < countX - (minSpanX - 1); x++) {
                int ySize = -1;
                int xSize = -1;
                if (ignoreOccupied) {
                    // First, let's see if this thing fits anywhere
                    for (int i = 0; i < minSpanX; i++) {
                        for (int j = 0; j < minSpanY; j++) {
//                            if (occupied[x + i][y + j]) {
                        	if (mCellLayout.mOccupied[x + i][y + j]) {
                                continue inner;
                            }
                        }
                    }
                    xSize = minSpanX;
                    ySize = minSpanY;

                    // We know that the item will fit at _some_ acceptable size, now let's see
                    // how big we can make it. We'll alternate between incrementing x and y spans
                    // until we hit a limit.
                    boolean incX = true;
                    boolean hitMaxX = xSize >= spanX;
                    boolean hitMaxY = ySize >= spanY;
                    while (!(hitMaxX && hitMaxY)) {
                        if (incX && !hitMaxX) {
                            for (int j = 0; j < ySize; j++) {
//                                if (x + xSize > countX -1 || occupied[x + xSize][y + j]) {
                            	if (x + xSize > countX -1 || mCellLayout.mOccupied[x + xSize][y + j]) {
                                    // We can't move out horizontally
                                    hitMaxX = true;
                                }
                            }
                            if (!hitMaxX) {
                                xSize++;
                            }
                        } else if (!hitMaxY) {
                            for (int i = 0; i < xSize; i++) {
                                if (y + ySize > countY - 1 || mCellLayout.mOccupied[x + i][y + ySize]) {
//                            	if (y + ySize > countY - 1 || occupied[x + i][y + ySize]) {
                                    // We can't move out vertically
                                    hitMaxY = true;
                                }
                            }
                            if (!hitMaxY) {
                                ySize++;
                            }
                        }
                        hitMaxX |= xSize >= spanX;
                        hitMaxY |= ySize >= spanY;
                        incX = !incX;
                    }
                    incX = true;
                    hitMaxX = xSize >= spanX;
                    hitMaxY = ySize >= spanY;
                }
                
                final int[] centerXY = CellLayoutConfig.getCenterXY(x, y, mCellLayout.getCellWidth(), mCellLayout.getCellHeight());
                // We verify that the current rect is not a sub-rect of any of our previous
                // candidates. In this case, the current rect is disqualified in favour of the
                // containing rect.
                Rect currentRect;
                if(!mTempRectStack.isEmpty()){                	
                	currentRect = mTempRectStack.pop();
                }else{
                	currentRect = new Rect();
                }
                currentRect.set(x, y, x + xSize, y + ySize);
                boolean contained = false;
                for (Rect r : validRegions) {
                    if (r.contains(currentRect)) {
                        contained = true;
                        break;
                    }
                }
                validRegions.push(currentRect);
                double distance = Math.sqrt(Math.pow(centerXY[0] - pixelX, 2)
                        + Math.pow(centerXY[1] - pixelY, 2));

                if ((distance <= bestDistance && !contained) ||
                        currentRect.contains(bestRect)) {
                    bestDistance = distance;
                    bestXY[0] = x;
                    bestXY[1] = y;
                    if (resultSpan != null) {
                        resultSpan[0] = xSize;
                        resultSpan[1] = ySize;
                    }
                    bestRect.set(currentRect);
                }
            }
        }
        // re-mark space taken by ignoreView as occupied
//        markCellsAsOccupiedForView(ignoreView, occupied);
        mCellLayout.markCellsAsOccupiedForView(ignoreView);

        // Return -1, -1 if no suitable location found
        if (bestDistance == Double.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        recycleTempRects(validRegions);
        return bestXY;
    }
    
    private void lazyInitTempRectStack() {
        if (mTempRectStack.isEmpty()) {
            for (int i = 0; i < mCountX * mCountY; i++) {
                mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> used) {
        while (!used.isEmpty()) {
            mTempRectStack.push(used.pop());
        }
    }
    
    private void animateItemsToSolution(ItemConfiguration solution, View dragView) {

        boolean[][] occupied = mTmpOccupied;
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                occupied[i][j] = false;
            }
        }

        ViewGroup cl = mWorkspace.getCurrentCellLayout();
        int childCount = cl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = cl.getChildAt(i);
            if (child == dragView) continue;
            CellAndSpan c = solution.map.get(child);
            if (c != null) {
            	mWorkspace.enableCurrentChildHardwareLayer();
                animateChildToPosition(child, c.cellX, c.cellY);
                markCellsForView(c.cellX, c.cellY, c.spanX, c.spanY, occupied, true);
            }
        }
        cleanWorkspaceReorder();
    }
    
    private boolean animateChildToPosition(final View child, final int cellX, final int cellY) {
    	if(mWorkspace.getVisibility() != View.VISIBLE){
    		//Log.e("animateChildToPosition", "cancel");
    		return true;
    	}
    	return animateChildToPosition(child, cellX, cellY, false);
    }

    private boolean animateChildToPositionByRevert(final View child, final int cellX, final int cellY) {
    	return animateChildToPosition(child, cellX, cellY, false);
    }
    
    private boolean animateChildToPosition(final View child, final int cellX, final int cellY, final boolean needToDestroy) {
    	mWorkspace.handleOnDragOverOrReorder(child);
    	if(WorkspaceDragAndDropImpl.androidStyleReorder){
    		if(Build.VERSION.SDK_INT < 14){
    			return animateChildToPosition_androidStyle_Low14(child, cellX, cellY, needToDestroy);
    		}else{
    			return animateChildToPosition_androidStyle(child, cellX, cellY, needToDestroy);
    		}
    	}else{
    		return animateChildToPosition_iosStyle(child, cellX, cellY, needToDestroy);
    	}
    }
    
    class ReorderHintAnimation {
        View child;
        float finalDeltaX;
        float finalDeltaY;
        float initDeltaX;
        float initDeltaY;
        float finalScale;
        float initScale;
        Animator a;

        int[] mTmpPoint = new int[2];
        
        public ReorderHintAnimation(View child, int cellX0, int cellY0, int cellX1, int cellY1,
                int spanX, int spanY) {
        	mTmpPoint = CellLayoutConfig.getCenterXY(cellX0, cellY0, spanX, spanY);
            final int x0 = mTmpPoint[0];
            final int y0 = mTmpPoint[1];
            mTmpPoint = CellLayoutConfig.getCenterXY(cellX1, cellY1, spanX, spanY);
            final int x1 = mTmpPoint[0];
            final int y1 = mTmpPoint[1];
            final int dX = x1 - x0;
            final int dY = y1 - y0;
            finalDeltaX = 0;
            finalDeltaY = 0;
            if (dX == dY && dX == 0) {
            } else {
            	mReorderHintAnimationMagnitude = (REORDER_HINT_MAGNITUDE * IconSizeManager.getIconSizeBySp(BaseConfig.getApplicationContext()));
                if (dY == 0) {
                    finalDeltaX = - Math.signum(dX) * mReorderHintAnimationMagnitude;
                } else if (dX == 0) {
                    finalDeltaY = - Math.signum(dY) * mReorderHintAnimationMagnitude;
                } else {
                    double angle = Math.atan( (float) (dY) / dX);
                    finalDeltaX = (int) (- Math.signum(dX) *
                            Math.abs(Math.cos(angle) * mReorderHintAnimationMagnitude));
                    finalDeltaY = (int) (- Math.signum(dY) *
                            Math.abs(Math.sin(angle) * mReorderHintAnimationMagnitude));
                }
            }
            
            initDeltaX = ViewHelper.getTranslationX(child);
            initDeltaY = ViewHelper.getTranslationY(child);
            finalScale = 1.0f - 4.0f / child.getWidth();
            initScale = ViewHelper.getScaleX(child);
            ViewHelper.setPivotY(child, child.getMeasuredHeight() * 0.5f);
            ViewHelper.setPivotX(child, child.getMeasuredWidth() * 0.5f);
            
            this.child = child;
        }

        void startOrCompleteAnimation() {
            if (mShakeAnimators.containsKey(child)) {
                ReorderHintAnimation oldAnimation = mShakeAnimators.get(child);
                oldAnimation.cancel();
                mShakeAnimators.remove(child);
                if (finalDeltaX == 0 && finalDeltaY == 0) {
                    completeAnimationImmediately();
                    return;
                }
            }
            if (finalDeltaX == 0 && finalDeltaY == 0) {
                return;
            }
            ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
            a = va;
            va.setRepeatMode(ValueAnimator.REVERSE);
            va.setRepeatCount(ValueAnimator.INFINITE);
            va.setDuration(REORDER_ANIMATION_HINT_DURATION);
            va.setStartDelay((int) (Math.random() * 60));
            va.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float r = ((Float) animation.getAnimatedValue()).floatValue();
                    float x = r * finalDeltaX + (1 - r) * initDeltaX;
                    float y = r * finalDeltaY + (1 - r) * initDeltaY;
                    float s = r * finalScale + (1 - r) * initScale;
                    
                    ViewHelper.setTranslationX(child, x);
                    ViewHelper.setTranslationY(child, y);
                    ViewHelper.setScaleX(child, s);
                    ViewHelper.setScaleY(child, s);
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                public void onAnimationRepeat(Animator animation) {
                    // We make sure to end only after a full period
                    initDeltaX = 0;
                    initDeltaY = 0;
                    initScale = 1.0f;
                }
            });
            mShakeAnimators.put(child, this);
            va.start();
        }

        private void cancel() {
            if (a != null) {
                a.cancel();
            }
        }

        private void completeAnimationImmediately() {
            if (a != null) {
                a.cancel();
            }

            AnimatorSet s = new AnimatorSet();
            a = s;
            s.playTogether(
                ObjectAnimator.ofFloat(child, "scaleX", 1f),
                ObjectAnimator.ofFloat(child, "scaleY", 1f),
                ObjectAnimator.ofFloat(child, "translationX", 0f),
                ObjectAnimator.ofFloat(child, "translationY", 0f)
            );
            s.setDuration(REORDER_ANIMATION_DURATION);
            s.setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f));
            s.start();
        }
    }
    
    /**
     * 用于同步抖动动画摇摆节奏
     * @param dragView
     * @param delay
     */
    private void adjustHintAnimations(View dragView, int delay) {
    	long curTime = System.currentTimeMillis();
    	if(curTime - adjustHintAnimationTime < REORDER_ANIMATION_TRANS_DURATION)
    		return;
    	adjustHintAnimationTime = curTime;
        int childCount = mCellLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = mCellLayout.getChildAt(i);
            if (child == dragView) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.isOnPending) {
                ReorderHintAnimation rha = new ReorderHintAnimation(child, lp.cellX, lp.cellY,
                        lp.tmpCellX, lp.tmpCellY, lp.spanX, lp.spanY);
                rha.startOrCompleteAnimation();
            }
        }
    }
    
    private void completeAndClearReorderHintAnimations() {
        for (ReorderHintAnimation a: mShakeAnimators.values()) {
            a.completeAnimationImmediately();
        }
        mShakeAnimators.clear();
    }
    
    private boolean animateChildToPosition_androidStyle(final View child, final int cellX, final int cellY, final boolean needToDestroy) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if(!lp.isOnPending){        	
        	lp.resetXY(mWorkspace.getCurrentCellLayout().getCellWidth(), mWorkspace.getCurrentCellLayout().getCellHeight());
        }
        final int oldX = lp.x;
        final int oldY = lp.y;
        
        //计算新位置坐标
        int[] xy = lp.getNewXY(cellX, cellY, mCellWidth, mCellHeight, mCellLayout.getCellWidth(), mCellLayout.getCellHeight());
        final int newX = xy[0];
        final int newY = xy[1];

        if (oldX == newX && oldY == newY) {
            return true;
        }
        
        lp.tmpX = newX;
        lp.tmpY = newY;
        
        lp.tmpCellX = cellX;
        lp.tmpCellY = cellY;
        
        
    	if(cellX == lp.cellX && cellY == lp.cellY){//移回原来位置
        	lp.isOnPending = false;
        }else{//移到其它位置
        	lp.isOnPending = true;
        	
        }
    	
        //图标移动位置动画
        lp.isOnReorderAnimation = true;
        
        
		ValueAnimator mAnimator = ValueAnimator.ofFloat(0f,1f);
		mAnimator.setDuration(REORDER_ANIMATION_TRANS_DURATION);
		mAnimator.addUpdateListener(new AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				lp.x = (int) (oldX + (newX - oldX) * value);
				lp.y = (int) (oldY + (newY - oldY) * value);
				mWorkspace.getCurrentCellLayout().requestLayout();
			}
		});
		mAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
		    public void onAnimationStart(Animator animation) {
				isOnReorderAnimation = true;
			}
			@Override
		    public void onAnimationEnd(Animator animation) {
				lp.isOnReorderAnimation = false;
				isOnReorderAnimation = false;
				
				child.clearAnimation();
				
				if(!mWorkspace.getDragController().isDragging() && child.getParent() instanceof CellLayout){//拖动结束动画未结束时，回到原位置
					int[] xy = new int[]{lp.cellX, lp.cellY};
					((CellLayout)child.getParent()).onDrop(child, xy, null);
					return;
				}
				
				lp.x = newX;
				lp.y = newY;
				child.requestLayout();
				
				ReorderHintAnimation rha = new ReorderHintAnimation(child, lp.cellX, lp.cellY,
						cellX, cellY, lp.spanX, lp.spanY);
				rha.startOrCompleteAnimation();
				
				if(lp.isOnPending){//抖动动画
					adjustHintAnimations(child, REORDER_ANIMATION_DURATION);
					
				}else{
					if(needToDestroy && mShakeAnimators.size() == 0){//当没有View在进行抖动动画，并且接下来要进行新的动画位移时
						mWorkspace.destoryCurrentChildHardwareLayer();
					}
					mWorkspace.setAllowRevertReorder(true);
				}
				
				
				if(mWorkspace.isOnSpringMode() && !mWorkspace.getDragController().isDragging()){//防止编辑模式下可能出现的卡屏
					mWorkspace.refreshSpringScreen();
				}
				if(mWorkspace.isOnSpringMode() && GpuControler.isOpenGpu(mWorkspace)&& mWorkspace.getDragController().isDragging()){
					mWorkspace.invalidate();
				}
				mWorkspace.clearVacantCache();//清理位置占用情况缓存，使得正确绘制图标光亮目标位置
			}
		});
		mAnimator.setTarget(child);
		mAnimator.start();
        return true;
    }

    
    private boolean animateChildToPosition_androidStyle_Low14(final View child, final int cellX, final int cellY, final boolean needToDestroy) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        
        final int oldX = lp.x;
        final int oldY = lp.y;
        
        //计算新位置坐标
//        int[] xy = lp.getNewXY(cellX, cellY, mCellWidth, mCellHeight, mWidthGap, mHeightGap, 
//        		mCellLayout.getLeftPadding(), mCellLayout.getRightPadding());
        int[] xy = lp.getNewXY(cellX, cellY, mCellWidth, mCellHeight, mCellLayout.getCellWidth(), mCellLayout.getCellHeight());
        final int newX = xy[0];
        final int newY = xy[1];

        if (oldX == newX && oldY == newY) {
            return true;
        }
        lp.tmpX = newX;
        lp.tmpY = newY;
        
        lp.tmpCellX = cellX;
        lp.tmpCellY = cellY;
        
        final AnimationSet animatorSet = new AnimationSet(true);
        
        //图标抖动动画
    	if(cellX == lp.cellX && cellY == lp.cellY){//移回原来位置
        	lp.isOnPending = false;
        	if(mReorderAnimations.containsKey(child)){
        		mReorderAnimations.remove(child);
        	}
//                	child.clearAnimation();
        }else{//移到其它位置
        	lp.isOnPending = true;
        	//跳跃动画
        	float xx = mCellLayout.getCellLayoutWidth()/480f * 3;
        	float yy = mCellLayout.getCellLayoutHeight()/620f * 4;
        	
        	int tranX = (newX - lp.x == 0) ? 0 : (int)xx;
        	int tranY = (newY - lp.y == 0) ? 0 : (int)yy;
        	TranslateAnimation anim = new TranslateAnimation(-tranX, tranX, -tranY, tranY);
    		anim.setDuration(REORDER_ANIMATION_HINT_DURATION);
    		anim.setRepeatMode(Animation.REVERSE);
    		anim.setRepeatCount(Animation.INFINITE);
    		animatorSet.addAnimation(anim);
    		
    		
        	Animation sacleAni = new ScaleAnimation(1.0f, 0.98f, 1.0f, 0.98f, 
            		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        	sacleAni.setDuration(REORDER_ANIMATION_HINT_DURATION);
        	sacleAni.setRepeatMode(Animation.REVERSE);
        	sacleAni.setRepeatCount(Animation.INFINITE);
    		animatorSet.addAnimation(sacleAni);
        }
    	
        //图标移动位置动画
        lp.isOnReorderAnimation = true;
        isOnReorderAnimation = true;
        
        
        TranslateAnimation trans = new TranslateAnimation(0, newX - oldX, 0, newY - oldY);
		trans.setDuration(REORDER_ANIMATION_TRANS_DURATION);
		trans.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				lp.isOnReorderAnimation = false;
				isOnReorderAnimation = false;
				
				child.clearAnimation();
				
				if(!mWorkspace.getDragController().isDragging() && child.getParent() instanceof CellLayout){//拖动结束动画未结束时，回到原位置
					int[] xy = new int[]{lp.cellX, lp.cellY};
					((CellLayout)child.getParent()).onDrop(child, xy, null);
					return;
				}
				
				lp.x = newX;
				lp.y = newY;
				child.requestLayout();
				
				if(lp.isOnPending){//抖动动画
					child.startAnimation(animatorSet);
					
					if(!mReorderAnimations.containsKey(child)){
						mReorderAnimations.put(child, animatorSet);
					}
					//同步抖动动画帧时间
					Iterator<Entry<View, Animation>> iter = mReorderAnimations.entrySet().iterator(); 
					while (iter.hasNext()) { 
						Entry<View, Animation> entry = iter.next(); 
					    entry.getValue().setStartTime(AnimationUtils.currentAnimationTimeMillis());
					} 
				}else{
					if(needToDestroy && mReorderAnimations.size() == 0){//当没有View在进行抖动动画，并且接下来要进行新的动画位移时
						mWorkspace.destoryCurrentChildHardwareLayer();
					}
					mWorkspace.setAllowRevertReorder(true);
				}
				
				
				if(mWorkspace.isOnSpringMode() && !mWorkspace.getDragController().isDragging()){//防止编辑模式下可能出现的卡屏
					mWorkspace.refreshSpringScreen();
				}
				if(mWorkspace.isOnSpringMode() && GpuControler.isOpenGpu(mWorkspace)&& mWorkspace.getDragController().isDragging()){
					mWorkspace.invalidate();
				}
				mWorkspace.clearVacantCache();//清理位置占用情况缓存，使得正确绘制图标光亮目标位置
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}  
			
		});
        child.startAnimation(trans);
		
        return true;
    }
    
    private boolean animateChildToPosition_iosStyle(final View child, final int cellX, final int cellY, final boolean needToDestroy) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        
        int oldCellX = lp.cellX;
        int oldCellY = lp.cellY;
        final int newCellX = cellX;
        final int newCellY = cellY;

        if (oldCellX == newCellX && oldCellY == newCellY) {
        	return true;
        }
        lp.cellX = cellX;
        lp.cellY = cellY;
        
        //图标移动位置动画
        lp.isOnReorderAnimation = true;
        final int xDelta = (newCellX - oldCellX) * (mCellWidth + mCellGapX);
        final int yDelta = (newCellY - oldCellY) * (mCellHeight+ mCellGapY);
        final int oldX = lp.x;
        final int oldY = lp.y;
		ValueAnimator mAnimator = ValueAnimator.ofFloat(0f,1f);
		mAnimator.setDuration(REORDER_ANIMATION_TRANS_DURATION);
		mAnimator.addUpdateListener(new AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				lp.x = (int) (oldX + xDelta * value);
				lp.y = (int) (oldY + yDelta * value);
				child.requestLayout();
			}
		});
		mAnimator.setDuration(REORDER_ANIMATION_TRANS_DURATION);
		mAnimator.addListener(new AnimatorListener(){
			@Override
			public void onAnimationStart(Animator arg0) {
				isOnReorderAnimation = true;
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				lp.isOnReorderAnimation = false;
				isOnReorderAnimation = false;
				
				//child.clearAnimation();
				lp.cellX = newCellX;
				lp.cellY = newCellY;
				child.requestLayout();
				
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						ItemInfo info = (ItemInfo) child.getTag();
						info.screen = mWorkspace.getCurrentScreen();
						info.cellX = newCellX;
						info.cellY = newCellY;
						BaseLauncherModel.moveItemInDatabase(BaseConfig.getApplicationContext(), info);
					}
				});
				
				if(!lp.isOnPending){
					if(needToDestroy){
						mWorkspace.destoryCurrentChildHardwareLayer();
					}
				}
				
				if(mWorkspace.isOnSpringMode() && !mWorkspace.getDragController().isDragging()){//防止编辑模式下可能出现的卡屏
					mWorkspace.refreshSpringScreen();
				}
				if(mWorkspace.isOnSpringMode() && GpuControler.isOpenGpu(mWorkspace)&& mWorkspace.getDragController().isDragging()){
					mWorkspace.invalidate();
				}
				mWorkspace.clearVacantCache();//清理位置占用情况缓存，使得正确绘制图标光亮目标位置
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
			}

		});
		mAnimator.setTarget(child);
		mAnimator.start();
		
		
        return true;
    }
    
    public void cleanReorderAnimations(){
    	mReorderAnimations.clear();
    }
    
    public boolean isOnReorderHintAnimation(){
    	return mReorderAnimations.size() > 0;
    }
    
    
    //判断是否快速滑动导致移动动画中断
    public boolean interruptReorderAnimation(LayoutParams lp){
    	if(lp.isOnReorderAnimation)
    		return false;
    	if(lp.tmpX == -1 && lp.tmpY == -1)
    		return false;
    	if(lp.tmpX != lp.x || lp.tmpY != lp.y)
    		return true;
    	return false;
    }
	
	/* This seems like it should be obvious and straight-forward, but when the direction vector
    needs to match with the notion of the dragView pushing other views, we have to employ
    a slightly more subtle notion of the direction vector. The question is what two points is
    the vector between? The center of the dragView and its desired destination? Not quite, as
    this doesn't necessarily coincide with the interaction of the dragView and items occupying
    those cells. Instead we use some heuristics to often lock the vector to up, down, left
    or right, which helps make pushing feel right.
    */
    private void getDirectionVectorForDrop(int dragViewCenterX, int dragViewCenterY, int spanX,
            int spanY, View dragView, int[] resultDirection) {
    	if(spanX == 0 || spanY == 0){//处理异常情况
    		resultDirection[0] = 1;
            resultDirection[1] = 0;
            return;
    	}
    	
        int[] targetDestination = new int[2];
        findNearestArea(dragViewCenterX, dragViewCenterY, spanX, spanY, targetDestination);
        Rect dragRect = new Rect();
        regionToRect(targetDestination[0], targetDestination[1], spanX, spanY, dragRect);
        dragRect.offset(dragViewCenterX - dragRect.centerX(), dragViewCenterY - dragRect.centerY());

        Rect dropRegionRect = new Rect();
        getViewsIntersectingRegion(targetDestination[0], targetDestination[1], spanX, spanY,
                dragView, dropRegionRect, mIntersectingViews);

        int dropRegionSpanX = dropRegionRect.width();
        int dropRegionSpanY = dropRegionRect.height();

        regionToRect(dropRegionRect.left, dropRegionRect.top, dropRegionRect.width(),
                dropRegionRect.height(), dropRegionRect);

        int deltaX = (dropRegionRect.centerX() - dragViewCenterX) / spanX;
        int deltaY = (dropRegionRect.centerY() - dragViewCenterY) / spanY;

        if (dropRegionSpanX == mCountX || spanX == mCountX) {
            deltaX = 0;
        }
        if (dropRegionSpanY == mCountY || spanY == mCountY) {
            deltaY = 0;
        }

        if (deltaX == 0 && deltaY == 0) {
            // No idea what to do, give a random direction.
            resultDirection[0] = 1;
            resultDirection[1] = 0;
        } else {
            computeDirectionVector(deltaX, deltaY, resultDirection);
        }
    }
    
	/**
	 * Given a cell coordinate and span fills out a corresponding pixel rect
	 * 
	 * @param cellX
	 *            X coordinate of the cell
	 * @param cellY
	 *            Y coordinate of the cell
	 * @param result
	 *            Rect in which to write the result
	 */
    public void regionToRect(int cellX, int cellY, int spanX, int spanY, Rect result) {
		final int leftMargin = CellLayoutConfig.getMarginLeft();
		final int topMargin = CellLayoutConfig.getMarginTop();
		final int left = leftMargin + cellX * (mCellWidth + mCellGapX);
		final int top = topMargin + cellY * (mCellHeight+ mCellGapY);
		result.set(left, top, left
				+ spanX * (mCellWidth + mCellGapX), top
				+ spanY * (mCellHeight+ mCellGapY));
	}

    //拖动结束后，还原图标移动的状态
	public void revertTempState() {
//		if (!isItemPlacementDirty())
//			return;
		if(WorkspaceDragAndDropImpl.androidStyleReorder){
			final int count = mCellLayout.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = mCellLayout.getChildAt(i);
				if (child.getVisibility() == View.GONE) continue;
				LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp.isOnPending) {
//					lp.tmpCellX = lp.cellX;
//					lp.tmpCellY = lp.cellY;
					animateChildToPositionByRevert(child, lp.cellX, lp.cellY);
				}
			}
		}
		
		
		cleanWorkspaceReorder();
		completeAndClearReorderHintAnimations();
		setItemPlacementDirty(false);
		isOnReorderAnimation = false;
	}
	
	public boolean revertReorderOnDragOver(int minCellX, int maxCellX, int minCellY, int maxCellY, boolean reorderAni) {
		if (!resetItemPlacementDirty())
			return false;
		
		mWorkspace.cancelReorderAlarm();
		
		List<View> revertList = new ArrayList<View>();
		revertChildViews(minCellX, maxCellX, minCellY, maxCellY, reorderAni, revertList);
		//不使用while循环，最多查找3次
		if(revertList.size() > 0){
			int oldSize = revertList.size();
			revertChildViews(minCellX, maxCellX, minCellY, maxCellY, reorderAni, revertList);
			if(revertList.size() > oldSize){
				revertChildViews(minCellX, maxCellX, minCellY, maxCellY, reorderAni, revertList);
			}
		}
		
		return revertList.size() > 0;
	}

	private void revertChildViews(int minCellX, int maxCellX, int minCellY, int maxCellY, boolean reorderAni, List<View> revertList){
		boolean isAllRevert = true;//是否全部恢复到原来位置
		ViewGroup cl = mWorkspace.getCurrentCellLayout();
		final int count = cl.getChildCount();
		for (int k = 0; k < count; k++) {
			View child = cl.getChildAt(k);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			if (child.getVisibility() == View.GONE || !lp.isOnPending) 
				continue;
			
			boolean allowRevert = true;
			//判断原来位置是否为拖动View目标区，若是不允许回到原来位置
			for(int m = 0; m < lp.spanX; m ++){
				for(int n = 0; n < lp.spanY; n ++){
					if(lp.cellX + m >= minCellX && lp.cellX + m < maxCellX 
							&& lp.cellY + n >= minCellY && lp.cellY + n < maxCellY){
						allowRevert = false;
						isAllRevert = false;
						break;
					}
				}
				if(!allowRevert) break;
			}
			//判断要恢复的位置是否被占用，被占用则不恢复
			if(allowRevert){
				int x = lp.cellX;
				int y = lp.cellY;
				for(int i = 0; i < lp.spanX; i ++){
					for(int j = 0; j < lp.spanY; j ++){
						View targetView = getReorderChildAt(x + i, y + j);
//						if(targetView != null && targetView != child){
						if(targetView != null && targetView != child && !revertList.contains(targetView)){
							allowRevert = false;
							isAllRevert = false;
							break;
						}
					}
					if(!allowRevert)
						break;
				}
			}
			
			if(allowRevert){
				mWorkspace.enableCurrentChildHardwareLayer();
				animateChildToPosition(child, lp.cellX, lp.cellY, reorderAni);
				revertList.add(child);
			}
		}
		
		if(isAllRevert){
			setItemPlacementDirty(false);
		}
	}
	
	
	public boolean resetItemPlacementDirty(){
		boolean isDirty = false;
		final int count = mCellLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mCellLayout.getChildAt(i);
			if(child.getVisibility() != View.VISIBLE)
				continue;
			
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
			if(lp.isOnPending){
				isDirty = true;
				break;
			}

		};
		setItemPlacementDirty(isDirty);
		return isDirty;
	}
	
	//用于在拖动时查找view
	private View getReorderChildAt(int mCellX, int mCellY) {
    	final int count = mCellLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mCellLayout.getChildAt(i);
			if(child.getVisibility() != View.VISIBLE)
				continue;
			
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();

			int cellX = lp.isOnPending ? lp.tmpCellX : lp.cellX;
			int cellY = lp.isOnPending ? lp.tmpCellY : lp.cellY;
			
			if ((cellX <= mCellX) && (mCellX < cellX + lp.spanX) && (cellY <= mCellY) && (mCellY < cellY + lp.spanY)) {
				return child;
			}
		}
		return null;
    }
	
	public void setItemPlacementDirty(boolean dirty) {
		mItemPlacementDirty = dirty;
	}

	public boolean isItemPlacementDirty() {
		if(!WorkspaceDragAndDropImpl.androidStyleReorder)
			return false;
		return mItemPlacementDirty;
	}
	
	public boolean needRevertReorder(int[] targetCell, int spanX, int spanY){
		for(int i = 0; i < spanX; i ++){
			for(int j = 0; j < spanY; j ++){
				
			}
		}
		return false;
	}
	
    //清理拖动图标时，其它view移动信息和状态
    private void cleanWorkspaceReorder(){
    	if(mWorkspace == null || mCellLayout == null)
    		return;
		final int count = mCellLayout.getChildCount();
		boolean needToRestore = true;
		for (int i = 0; i < count; i++) {
			View child = mCellLayout.getChildAt(i);
			if (child.getVisibility() == View.GONE) continue;
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			if (lp.isOnPending) {
				needToRestore = false;
				break;
			}
		}
		
		if(needToRestore){
			mWorkspace.cleanupReorder();
		}
    }
    
    public boolean isOnReorderAnimation() {
		return isOnReorderAnimation;
	}


	public void setOnReorderAnimation(boolean isOnReorderAnimation) {
		this.isOnReorderAnimation = isOnReorderAnimation;
	}
	
	
	public void handleReorderPendingViews(Context context, int screen){
		int count = mCellLayout.getChildCount();
		for(int i = 0; i < count; i ++){
			View child = mCellLayout.getChildAt(i);
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
			if(lp.isOnPending){//图标不在原来位置时
				//如果不在进行位移动画，则清除抖动动画
				if(!lp.isOnReorderAnimation){
					child.clearAnimation();
				}
				
				int[] xy = new int[]{lp.tmpCellX, lp.tmpCellY};
				mCellLayout.onDrop(child, xy, null);
				
//				int cellX = lp.tmpCellX;
//				int cellY = lp.tmpCellY;
//				mCellLayout.onDropChild(child, new int[]{cellX, cellY});
//				
//				final ItemInfo info = (ItemInfo) child.getTag();
//				LauncherModel.moveItemInDatabase(context, info, LauncherSettings.Favorites.CONTAINER_DESKTOP, screen, cellX, cellY);
			}
		}
		
		setItemPlacementDirty(false);
	}
}
