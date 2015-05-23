package com.bitants.common.launcher.touch;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

import com.bitants.common.framework.AnyCallbacks;
import com.bitants.common.kitset.util.MessageUtils;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.screens.preview.PreviewCellInfo;
import com.bitants.common.launcher.touch.outline.DragHelper;
import com.bitants.common.launcher.view.DragView;
import com.bitants.common.R;
import com.bitants.common.launcher.support.BaseCellLayoutHelper;

/**
 * Description: 处理默认桌面上Workspace的拖放
 */
public class WorkspaceDragAndDropImpl implements WorkspaceDragAndDrop{
	protected ScreenViewGroup mWorkspace;
	protected BaseLauncher mLauncher;
	protected BaseDragController mDragController;
	protected Context mContext;
	
	protected CellLayout mDragTargetLayout;
	protected View mLastDragOverView;
	
	/**
	 * 拖动图标时图标互换位置处理
	 */
	protected static long REORDER_ACTION_TIMEOUT_DEFAULT = 400; //缺省拖动时触发图标位置移动的响应时间
	protected static long REORDER_ACTION_TIMEOUT = REORDER_ACTION_TIMEOUT_DEFAULT; //拖动时触发图标位置移动的响应时间
	protected static final int DRAG_MODE_NORMAL = 0; //正常拖动图标
    protected static final int DRAG_MODE_FOLDER = 1;//拖动图标合成或进入文件夹
    protected int mDragMode = DRAG_MODE_NORMAL;
    //上一次拖动图标的目标放置位置
    protected int mLastReorderX = -1;
    protected int mLastReorderY = -1;
    protected final Alarm mReorderAlarm = new Alarm();//图标互换延迟处理线程
    protected int[] mDragViewVisualCenter;//被拖动图标的中心点坐标
	protected int[] lastTargetCell = {-1, -1};//上一次拖动图标的目标放置位置
	public boolean allowRevertReorder = true;//是否允许图标还原到原来位置
	
	protected float mMaxDistanceForFolderCreation;//小于该距离时进行合并文件夹动画
	protected AnyCallbacks.OnDragEventCallback dragOverViewFolder = null; //拖动时覆盖的view，用于合成文件夹
	protected boolean isDragOnApplication = false;//是否拖动app到另一个app上方
	protected boolean allowAnimateMergeFolder = true;//标示是否允许合并文件夹动画
	
	
	protected CellLayout.CellInfo mDragInfo;
	protected int[] mTargetCell;
	protected Rect mTargetRect = new Rect();

	/**
	 * Target drop area calculated during last acceptDrop call.
	 */
	protected int[] mTargetCellOnFolder;
	
	/**
	 * true为安卓4.0的挤动方式，false为类ios的挤动方式(目前适用于无匣子桌面)
	 */
	public static boolean androidStyleReorder = true;
	
	
	public WorkspaceDragAndDropImpl(ScreenViewGroup mWorkspace, CellLayout.CellInfo mDragInfo){
		this.mWorkspace = mWorkspace;
		this.mLauncher = mWorkspace.getLauncher();
		this.mDragController = mWorkspace.getDragController();
		this.mContext = mLauncher;
		this.mDragInfo = mDragInfo;
		
		mDragMode = DRAG_MODE_NORMAL;
		mMaxDistanceForFolderCreation = (0.65f * mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size));
		REORDER_ACTION_TIMEOUT = REORDER_ACTION_TIMEOUT_DEFAULT;
	}
	
	//=======================================处理workspace上的拖拽响应==============================//
	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo){
		if (dragInfo instanceof PreviewCellInfo)
			return false;
		
		/**
		 * 从文件夹fling出来的逻辑由接口{@link #OnFolderDragOutCallback}回调
		 */
		if (mDragController.isDragFromFolder(source) && mLauncher.isFolderOpened())
			return true;
		
		final CellLayout layout = mWorkspace.getCurrentDropLayout();
		if(layout == null)
			return false;
		int[] vacantCell = null;
		int[] span = getDragInfoSpanXY(dragInfo);
		final View ignoreView = mDragInfo == null ? null : mDragInfo.cell;
		boolean allowMergeFolder = false;
		if(!mWorkspace.isAllAppsIndependence((ItemInfo) dragInfo)){//防止拖动应用列表图标到满屏时丢失
			if(source instanceof BaseMagicDockbar || mDragController.isDragFromFolder(source)){
				final int[] mDragViewVisualCenter = dragView.getDragCenterPoints();
				mTargetCell = mWorkspace.findNearestArea(mDragViewVisualCenter[0], mDragViewVisualCenter[1], 1, 1, layout, mTargetCell);
				View dragOverView = layout.getChildAt(mTargetCell[0], mTargetCell[1]);
				if(dragOverView != null && dragOverView instanceof AnyCallbacks.OnDragEventCallback && !mWorkspace.isRealFolder(dragOverView.getTag())
						&& !mWorkspace.isAllAppsIndependence(dragOverView) && !(dragInfo instanceof FolderInfo))
					allowMergeFolder = true;
			}else{
				allowMergeFolder = dragInfo instanceof ApplicationInfo;
			}
		}
		
		if(allowMergeFolder){//拖动app到CellLayout，CellLayout虽已无空间但该CellLayout含有app或folder，就允许拖动到该CellLayout
			layout.initOccupied(ignoreView, true);
		}else{
			layout.initOccupied(ignoreView, false);
		}
		
		vacantCell = layout.findFirstVacantCell(span[0], span[1], ignoreView, false);
		
		 
		return vacantCell != null;
	}
	
	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo){
		mWorkspace.clearVacantCache();
		mDragTargetLayout = mWorkspace.getCurrentDropLayout();
	}
	
	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo){
		mWorkspace.clearVacantCache();

		/**
		 * 文件夹打开时桌面不处理over和exit的文件夹动画
		 */
		if (mDragController.isDragFromFolder(source) && mLauncher.isFolderOpened()) {
			return;
		}

		if ((dragInfo instanceof ApplicationInfo  || dragView.getDragingView() instanceof AnyCallbacks.OnDragEventCallback)
			 && mLastDragOverView != null) {
			if (mLastDragOverView instanceof AnyCallbacks.OnDragEventCallback) {
				CellLayout.LayoutParams lp = (CellLayout.LayoutParams) mLastDragOverView.getLayoutParams();
				if(!lp.isOnReorderAnimation && !lp.isOnPending){					
					((AnyCallbacks.OnDragEventCallback) mLastDragOverView).onExitAni(dragView);
				}
			}
		}
		//离开文件夹时清除轮廓
		DragHelper.getInstance().dragOutlineAnimateOut();
		mDragTargetLayout = null;
		mLastDragOverView = null;
	}
	
	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		if (mDragTargetLayout == null)
			return;
		
		/**
		 * 文件夹打开时桌面不处理over和exit的文件夹动画
		 */
		if (mDragController.isDragFromFolder(source) && mLauncher.isFolderOpened()) {
			return;
		}

		mDragViewVisualCenter = dragView.getDragCenterPoints();
		
		//1.查找dragview的目标位置的左上cell位置，并记录
		mDragTargetLayout = mWorkspace.getCurrentDropLayout();
		ItemInfo item = (ItemInfo) dragInfo;
		int dragItemSpanX = item.spanX;
		int dragItemSpanY = item.spanY;
		
		mTargetCell = mWorkspace.findNearestArea(mDragViewVisualCenter[0], mDragViewVisualCenter[1], 
				dragItemSpanX, dragItemSpanY, mDragTargetLayout, mTargetCell);
		int targetCellX = mTargetCell[0];
		int targetCellY = mTargetCell[1];
		final View dragOverView = mDragTargetLayout.getChildAt(mTargetCell[0], mTargetCell[1]);
		if(dragOverView != null)
			mWorkspace.handleOnDragOverOrReorder(dragOverView);
		//2.处理文件夹情况
		if(handleMergeFolderAndWidget(source, x, y, xOffset, yOffset, dragView, dragInfo, dragOverView)){
			return;
		}
		//当从匣子中拖动多个图标到界面时 不执行占用动画
		if(isOnMultiSelectedDraging()){
			return;
		}
		
		
		//3.判断目标位置是否被占用，被哪些view占用
		final View child = (mDragInfo == null) ? null : mDragInfo.cell;
		
		boolean hasIntersectingViews = mDragTargetLayout.isNearestDropLocationOccupied((int)
                mDragViewVisualCenter[0], (int) mDragViewVisualCenter[1], dragItemSpanX,
                dragItemSpanY, child, mTargetCell);
		
		//4.目标位置被占用处理，进行图标移动
		boolean reorderAni = false;
		boolean revertAni = false;
		if(hasIntersectingViews && mDragMode != DRAG_MODE_FOLDER &&  !mReorderAlarm.alarmPending() 
				&& (mLastReorderX != targetCellX || mLastReorderY != targetCellY)
				&& !mDragTargetLayout.isOnReorderAnimation()){
			reorderAni = true;
			
			int minSpanX = dragItemSpanX;
            int minSpanY = dragItemSpanY;
            boolean notDefaultStyle = !androidStyleReorder && isSpan1X1View(dragOverView) && (dragItemSpanX == 1 && dragItemSpanY == 1);
            ReorderAlarmListener listener = new ReorderAlarmListener(mDragViewVisualCenter,
                    minSpanX, minSpanY, dragItemSpanX, dragItemSpanY, dragView, child, notDefaultStyle);
            mReorderAlarm.setOnAlarmListener(listener);
            mReorderAlarm.setAlarm(REORDER_ACTION_TIMEOUT);
		}
		
		//5. 恢复图标移动状态
		if(androidStyleReorder){
			if (lastTargetCell[0] != targetCellX || lastTargetCell[1] != targetCellY) {
				lastTargetCell[0] = targetCellX;
				lastTargetCell[1] = targetCellY;
				allowRevertReorder = true;
			}
			if(allowRevertReorder){
				revertAni = mDragTargetLayout.revertReorderOnDragOver(targetCellX, 
						targetCellX + dragItemSpanX, targetCellY, targetCellY + dragItemSpanY, reorderAni);
				allowRevertReorder = false;
			}
		}
		
		//6. 更新GPU硬件缓存
        if(mWorkspace.isOnSpringMode() && !mDragTargetLayout.isOnReorderAnimation()){
        	if(reorderAni && !revertAni && !mDragTargetLayout.isItemPlacementDirty()){
        		mWorkspace.destoryCurrentChildHardwareLayer();
            }else if(revertAni){
            	mWorkspace.enableCurrentChildHardwareLayer();
            }
        }
        
	}
	
	//是否大小为1X1View
	private boolean isSpan1X1View(View v){
		if(v == null || v.getTag() == null || !(v.getTag() instanceof ItemInfo)){
			return true;
		}
		ItemInfo item = (ItemInfo)v.getTag();
		return item.spanX == 1 && item.spanY == 1;
	}
		
	//处理生成、进入或退出文件夹动画; 同时降低与小部件换位时的灵敏度
	public boolean handleMergeFolderAndWidget(DragSource source, int x, int y, int xOffset, int yOffset, 
			DragView dragView, Object dragInfo,  View dragOverView){
		if(mDragTargetLayout.isOnReorderAnimation() || mDragTargetLayout.isItemPlacementDirty()
				|| dragInfo instanceof FolderInfo){//如果处于移动动画中，或者拖动的是文件夹，不进行文件夹动画
			isDragOnApplication = false;
			return false;
		}
		
		if(isNotAllowToMergeFolder(dragOverView))
			return false;
		
		boolean allowHandleMerg = (dragInfo instanceof ApplicationInfo || dragView.getDragingView() instanceof AnyCallbacks.OnDragEventCallback)
				&& (dragOverView instanceof AnyCallbacks.OnDragEventCallback);
		if(dragOverView != null && dragOverView.getVisibility() == View.VISIBLE){
			int[] loc = {mDragViewVisualCenter[0], mDragViewVisualCenter[1]};
    		if(mWorkspace.isOnSpringMode()){//编辑模式下调整坐标
    			BaseCellLayoutHelper.springToNormalCoordinateEx(loc);
    		}
    		
			if(allowHandleMerg){//dragOverView文件夹处理
				dragOverViewFolder = (AnyCallbacks.OnDragEventCallback)dragOverView;
				isDragOnApplication = true;
				
				//计算dragview中心点与目标位置中心的距离
				float targetCellDistance = mDragTargetLayout.getDistanceFromCell(loc[0], loc[1], mTargetCell);
				
				//生成或进入文件夹动画
				if (dragOverView != mDragController.getOriginator() //防止被拖动view也产生文件夹动画
					&& targetCellDistance < mMaxDistanceForFolderCreation && mDragMode != DRAG_MODE_FOLDER 
					&& allowAnimateMergeFolder) {
						cancelReorderAlarm();
						
						mWorkspace.destoryCurrentChildHardwareLayer();
						
						mWorkspace.getCurrentCellLayout().resetDragAvailableCell();
						
						((AnyCallbacks.OnDragEventCallback) dragOverView).onEnterAni(dragView);
						//进入文件夹时，清除轮廓
						DragHelper.getInstance().dragOutlineAnimateOut();
						mDragMode = DRAG_MODE_FOLDER;
						
						mLastDragOverView = dragOverView;
						return true;
				}
				
				//退出文件夹动画
				if(targetCellDistance >= mMaxDistanceForFolderCreation && mDragMode == DRAG_MODE_FOLDER ){
					if (mLastDragOverView != null && dragOverView instanceof AnyCallbacks.OnDragEventCallback) {
						mWorkspace.destoryCurrentChildHardwareLayer();
						((AnyCallbacks.OnDragEventCallback) mLastDragOverView).onExitAni(dragView);
					} 
					mDragMode = DRAG_MODE_NORMAL;
					mLastDragOverView = (dragOverView != null && dragOverView.getVisibility() == View.VISIBLE) ? dragOverView : null;
					return true;
				}
			}else {//dragOverView是小部件处理,降低与小部件换位时的灵敏度
				if(isFolderWidget(dragOverView)){
					isDragOnApplication = true;
				}else{					
					isDragOnApplication = false;
				}
				
				int[] dragOverLoc = new int[2];
				dragOverView.getLocationInWindow(dragOverLoc);
				float margin = dragOverView.getHeight()*0.4f;
				float centerY = dragOverLoc[1] + dragOverView.getHeight()/2;
				if(Math.abs(loc[1] - centerY) < margin){
					return false;
				}else{
					return true;
				}
			}
			
		}else{
			isDragOnApplication = false;
		}
		//防止出现快速滑动后，没有退出文件夹动画
		if(mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null && dragOverView != mLastDragOverView){
			if (dragInfo instanceof ApplicationInfo || dragView.getDragingView() instanceof AnyCallbacks.OnDragEventCallback) {
				mWorkspace.destoryCurrentChildHardwareLayer();
				((AnyCallbacks.OnDragEventCallback) mLastDragOverView).onExitAni(dragView);
			} 
			mDragMode = DRAG_MODE_NORMAL;
			mLastDragOverView = (dragOverView != null && dragOverView.getVisibility() == View.VISIBLE) ? dragOverView : null;
			return true;
		}
		
		mLastDragOverView = (dragOverView != null && dragOverView.getVisibility() == View.VISIBLE) ? dragOverView : null;
		return false;
	}

	class ReorderAlarmListener implements Alarm.OnAlarmListener {
	    int[] dragViewCenter;
        int minSpanX, minSpanY, spanX, spanY;
        DragView dragView;
        View child;
        private boolean notDefaultStyle = false;

        public ReorderAlarmListener(int[] dragViewCenter, int minSpanX, int minSpanY, int spanX,
                int spanY, DragView dragView, View child, boolean notDefaultStyle) {
            this.dragViewCenter = dragViewCenter;
            this.minSpanX = minSpanX;
            this.minSpanY = minSpanY;
            this.spanX = spanX;
            this.spanY = spanY;
            this.child = child;
            this.dragView = dragView;
            this.notDefaultStyle = notDefaultStyle;
        }

        public void onAlarm(Alarm alarm) {
        	if(mDragMode == DRAG_MODE_FOLDER || !mDragController.isDragging()){//处于文件夹动画或是没有拖动view时，不进行互换位置动画
        		mWorkspace.cleanupReorder();
        		REORDER_ACTION_TIMEOUT = 50;
        		return;
        	}
        		
        	REORDER_ACTION_TIMEOUT = REORDER_ACTION_TIMEOUT_DEFAULT;
        	mDragTargetLayout = mWorkspace.getCurrentCellLayout();
            mTargetCell =  mWorkspace.findNearestArea(mDragViewVisualCenter[0], mDragViewVisualCenter[1], spanX, spanY, 
            		mDragTargetLayout, mTargetCell);
            mLastReorderX = mTargetCell[0];
            mLastReorderY = mTargetCell[1];

            int[] loc = {mDragViewVisualCenter[0], mDragViewVisualCenter[1]};
    		if( mWorkspace.isOnSpringMode()){//编辑模式下调整坐标
    			BaseCellLayoutHelper.springToNormalCoordinateEx(loc);
    		}
    		
            mDragTargetLayout.createArea(loc[0], loc[1], minSpanX, minSpanY, spanX, spanY, child, notDefaultStyle);
        }
	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		final CellLayout cellLayout = mWorkspace.getCurrentDropLayout();
		boolean isDragFromWorkspace = source instanceof ScreenViewGroup;//是否Workspace内拖动
		
		if (!isDragFromWorkspace) {
			if(isOnMultiSelectedDraging()){
				ArrayList<ApplicationInfo> appList = mLauncher.getDragController().getAppList();
				onDropExternal(x - xOffset, y - yOffset, dragInfo, cellLayout, dragView, appList);
				appList.clear();
				if(mDragController.isDragFromDrawer(source)){
					onDropFromDrawer(source);
				}
			}else{
				onDropExternal(x - xOffset, y - yOffset, dragInfo, cellLayout, dragView);
			}
		} else {
			if (mDragInfo != null) {				
				onDropInternal(cellLayout, x, y, xOffset, yOffset, dragView, dragInfo);
			}
		}
		handleReorderPendingViews();
	}
	
	/**
	 * Description: 处理在Workspace内拖动图标
	 */
	public void onDropInternal(CellLayout cellLayout, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo){
		final int[] mDragViewVisualCenter = dragView.getDragCenterPoints();
		mTargetCell = mWorkspace.findNearestArea(mDragViewVisualCenter[0], mDragViewVisualCenter[1], 1, 1, cellLayout, mTargetCell);
		if (mWorkspace.isHansntMoved(mDragTargetLayout, mTargetCell)) {
			return;
		}

		final View dragOverView = cellLayout.getChildAt(mTargetCell[0], mTargetCell[1]);
		if (createUserFolderIfNecessary(dragInfo, cellLayout, dragOverView, dragView, mTargetCell, false, null))
			return;

		if (addToExistingFolderIfNecessary(dragView, dragInfo, cellLayout, mTargetCell, false, null)) {
			return;
		}

		final View cell = mDragInfo.cell;
		mTargetCell = mWorkspace.estimateDropCell(x - xOffset, y - yOffset, mDragInfo.spanX, mDragInfo.spanY, cell, cellLayout, mTargetCell);
		if (mTargetCell == null) {
			Toast.makeText(mContext, R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
			return;
		}

		mTargetRect = mWorkspace.estimateDropLocation(mTargetCell, null);
		
		//判断是否当前屏内移动
		int index = mWorkspace.getCurrentDropLayoutIndex();
		CellLayout originalCellLayout = null;
		
		if (index != mDragInfo.screen) {//跨屏移动
			originalCellLayout = (CellLayout) mWorkspace.getChildAt(mDragInfo.screen);
		}

		int[] xy = cellLayout.getAndReLayoutCellXY(cell, mTargetCell);
		cellLayout.onDrop(cell, xy, originalCellLayout);
		
	}
	
	/**
	 * Description: 处理在其他地方(非Workspace)拖动图标到桌面
	 */
	public void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout, DragView dragView) {
		//处理生成或合并文件夹
		if (dragView != null) {
			final int[] mDragViewVisualCenter = dragView.getDragCenterPoints();
			mTargetCell = mWorkspace.findNearestArea(mDragViewVisualCenter[0], mDragViewVisualCenter[1], 1, 1, cellLayout, mTargetCell);
			final View dragOverView = cellLayout.getChildAt(mTargetCell[0], mTargetCell[1]);
			if (createUserFolderIfNecessary(dragInfo, cellLayout, dragOverView, dragView, mTargetCell, true, null))
				return;

			if (addToExistingFolderIfNecessary(dragView, dragInfo, cellLayout, mTargetCell, true, null)) {
				return;
			}
		}
		//将图标放置到目标位置
		ItemInfo info = (ItemInfo) dragInfo;
		int[] targetCell = mWorkspace.estimateDropCell(x, y, info.spanX, info.spanY, null, cellLayout, null);
		if(targetCell == null){
			Toast.makeText(mContext, R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
			return;
		}
		dropToScreenExternal(cellLayout, dragInfo, targetCell);
	}
	
	public int[] getDragInfoSpanXY(Object dragInfo){
		int spanX = mDragInfo == null ? 1 : mDragInfo.spanX;
		int spanY = mDragInfo == null ? 1 : mDragInfo.spanY;
		return new int[]{spanX, spanY};
	}

	public void handleReorderPendingViews(){
		CellLayout cl = mWorkspace.getCurrentCellLayout();
		int count = cl.getChildCount();
		for(int i = 0; i < count; i ++){
			View child = cl.getChildAt(i);
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
			if(lp.isOnPending){//图标不在原来位置时
				//如果不在进行位移动画，则清除抖动动画
				if(!lp.isOnReorderAnimation){
					child.clearAnimation();
				}
				int[] xy = new int[]{lp.tmpCellX, lp.tmpCellY};
				cl.onDrop(child, xy, null);
			}
		}
		
		cl.setItemPlacementDirty(false);
		
		mWorkspace.cleanReorderAllState();
	}
	
	
	
	//=======================================处理打开文件夹后拖出==============================//
	@Override
	public boolean onDropFolderExternal(int screen, Object item) {
		final CellLayout cellLayout = (CellLayout) mWorkspace.getChildAt(screen);

		final ItemInfo info = (ItemInfo) item;
		findCellForFolderDragout(cellLayout, info);

		if (mTargetCellOnFolder == null) {
			MessageUtils.makeShortToast(mContext, R.string.folder_drag_out_error);
			return false;
		} else {
			try{
				dropToScreenExternal(cellLayout, item, mTargetCellOnFolder);
				return true;
			}catch(Exception ex){
				MessageUtils.makeShortToast(mContext, 
						R.string.message_preview_fail_drag_to_screen);
				ex.printStackTrace();
				return false ;
			}
		}
	}
	
	/**
	 * <p>查找桌面的空cell,专为文件夹拖拽所用</p>
	 * 
	 * <p>date: 2012-8-10 下午07:30:25
	 * @param cellLayout
	 * @param info
	 */
	public void findCellForFolderDragout(final CellLayout cellLayout,
			final ItemInfo info) {
		info.spanX = 1;
		info.spanY = 1;
		// int[] vacantCell = cellLayout.findFirstVacantCell(info.spanX,
		// info.spanY, null);
		final BaseDragController dragController = mLauncher.mDragController;
		
		DragView dragView = dragController.getDragView();
		int[] dragCoordinates = dragController.getDragCoordinates();
		int[] dragViewCenter = dragView.getDragCenterPoints();
		final int offsetX = (int) dragController.getTouchOffsetX();
		final int offsetY = (int) dragController.getTouchOffsetY();
		
		mTargetCellOnFolder = mWorkspace.findNearestArea(dragViewCenter[0], dragViewCenter[1], 1,1, cellLayout, mTargetCellOnFolder);
		mTargetCellOnFolder = mWorkspace.estimateDropCell(dragCoordinates[0] - offsetX,
				dragCoordinates[1] - offsetY, 1, 1, null,
				cellLayout, mTargetCellOnFolder);
	}
	
	@Override
	public void onDrop(View target, FolderInfo folderInfo, ArrayList<Object> items) {
		
	}
	
	
	//=======================================处理屏幕预览上的拖放==============================//
	@Override
	public void dropDockbarItemToScreenFromPreview(CellLayout cellLayout,
			Object dragInfo, int[] targetCell) {
		dropToScreenExternal(cellLayout, dragInfo, targetCell);
	}
	
	/**
	 * <br>
	 * Description:从桌面将拖动的item放入目标屏的目标位置上 <br>
	 *
	 * @param index
	 *            目标屏
	 * @param targetCell
	 *            目标位置
	 * @return false失败， true成功
	 */
	@Override
	public boolean dropItemToScreenFromPreview(int index, int[] targetCell) {
		if (targetCell == null)
			return false;
		mWorkspace.changeToNormalMode();
		final View cell = mDragInfo.cell;

		final CellLayout cellLayout = (CellLayout) mWorkspace.getChildAt(index);
		
		
		if (index != mDragInfo.screen) {
			final CellLayout originalCellLayout = (CellLayout) mWorkspace.getChildAt(mDragInfo.screen);
			originalCellLayout.removeView(cell);
			cellLayout.addView(cell);
		}

		int[] cellXY = cellLayout.getAndReLayoutCellXY(cell, targetCell);
		cellLayout.onDrop(cell, cellXY, null);
		return true;
	}
	
	@Override
	public void dropToScreenFromDrawerPreview(int screen, Object mDragInfo, ArrayList<ApplicationInfo> appList){
		
	}
	
	
	
	//=======================================获取放手后的目标区域==============================//
	@Override
	public int[] getTargetCell() {
		return mTargetCell;
	}
	
	@Override
	public Rect getTargetRect() {
		return mTargetRect;
	}

	@Override
	public void cleanDragInfo() {
		mDragInfo = null;
	}
	
	
	
	//=======================================文件夹合并动画处理==============================//
	@Override
	public void setAllowAnimateMergeFolder(boolean allowAnimateMergeFolder) {
		this.allowAnimateMergeFolder = allowAnimateMergeFolder;
	}
	
	//是否在进行合并文件的动画
	@Override
	public boolean isOnMergeFolerAnimation(){
		return dragOverViewFolder != null && dragOverViewFolder.isOnMergeFolderAni();
	}
	
	@Override
	public void restoreFolderAnimation(){
		try{
			if(mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null && mLastDragOverView instanceof AnyCallbacks.OnDragEventCallback){
				mWorkspace.destoryCurrentChildHardwareLayer();
				((AnyCallbacks.OnDragEventCallback) mLastDragOverView).onExitAni(null);
				mDragMode = DRAG_MODE_NORMAL;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//=======================================拖动时挤动其它图标==============================//
	@Override
	public boolean acceptDropForReorder(Object dragInfo) {
		int[] span = getDragInfoSpanXY(dragInfo);
		final CellLayout layout = mWorkspace.getCurrentDropLayout();
		if(layout == null)
			return false;
		return layout.isVacantForReorder(span[0], span[1], null, false);
	}
	
	@Override
	public void cancelReorderAlarm(){
		mReorderAlarm.cancelAlarm();
		
		mLastReorderX = -1;
        mLastReorderY = -1;
	}
	
	//是否拖动app到另一个app上方
	@Override
	public boolean isDragOnApplication(){
		return isDragOnApplication;
	}

	@Override
	public void setAllowRevertReorder(boolean allow) {
		this.allowRevertReorder = allow;
	}
	
	
	
	
	//==========================================可复写的方法==========================//
	/**
	 * 是否处于多选状态
	 * @return
	 */
	public boolean isOnMultiSelectedDraging(){
		return false;
	}
	
	/**
	 * 拖动过程中，是否不允许合并文件夹动画
	 * @return
	 */
	public boolean isNotAllowToMergeFolder(View dragOverView){
		return mWorkspace.isOnSpringMode();
	}
	
	/**
	 * 是否是文件夹小部件，如安卓桌面的4x1文件夹
	 * @param v
	 * @return
	 */
	public boolean isFolderWidget(View v){
		return false;
	}
	
	/**
	 * Description: 将在其他地方(非Workspace，如文件夹、匣子、预览区)拖动的图标放置到桌面目标位置
	 */
	public void dropToScreenExternal(CellLayout cellLayout, Object dragInfo, int[] targetCell) {
		
	}
	
	/**
	 * Description: 处理在其他地方(非Workspace，如文件夹、匣子、预览区)批量拖动图标到桌面
	 */
	public void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout, DragView dragView, ArrayList<ApplicationInfo> appList) {
		
	}
	
	/**
	 * 处理从匣子里拖到Workspace时的onDrop事件
	 * @param source
	 */
	public void onDropFromDrawer(DragSource source){
	}
	
	/**
	 * 拖动放手后是否创建文件夹
	 * @param dragInfo
	 * @param cellLayout
	 * @param dragOverView
	 * @param dragView
	 * @param targetCell
	 * @param external
	 * @param appList
	 * @return
	 */
	public boolean createUserFolderIfNecessary(Object dragInfo, CellLayout cellLayout, View dragOverView, DragView dragView, int[] targetCell, boolean external, ArrayList<ApplicationInfo> appList) {
		return false;
	}
	
	/**
	 * 拖动放手后是否加入文件夹
	 * @param dragView
	 * @param dragObject
	 * @param target
	 * @param targetCell
	 * @param external
	 * @param drItems
	 * @return
	 */
	public boolean addToExistingFolderIfNecessary(DragView dragView, Object dragObject, CellLayout target, int[] targetCell, boolean external, ArrayList<ApplicationInfo> drItems) {
		return false;
	}
}
