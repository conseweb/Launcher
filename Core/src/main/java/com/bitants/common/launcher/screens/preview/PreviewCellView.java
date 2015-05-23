package com.bitants.common.launcher.screens.preview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.touch.DropTarget;
import com.bitants.common.core.view.PreviewImageView;
import com.bitants.common.kitset.GpuControler;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.touch.DragSource;
import com.bitants.common.launcher.view.DragView;
import com.bitants.common.R;

/**
 * <br>Description: 预览界面屏幕缩略图，支持桌面拖动项放置
 */
public class PreviewCellView extends FrameLayout implements DropTarget {

	private boolean isLastView = false;//是否为最后一个添加屏
	private boolean isFull = false;//是否该页面已没有空间
	private int[] vacantCell = null;
	private int[] xy = null;
	private boolean onDragOver = false;
	
	private PreviewEditAdvancedController mPreviewEditAdvancedController;
	private int screenIndex = -1;
	
	public void setLastView(boolean isLastView) {
		this.isLastView = isLastView;
	}

	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}
	
	public void setPreviewEditAdvancedController(PreviewEditAdvancedController previewEditAdvancedController){
		mPreviewEditAdvancedController = previewEditAdvancedController;
	}
	
	public void setScreenIndex(int screenIndex){
		this.screenIndex = screenIndex;
	}
	
	public PreviewCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * 跳转至停留页面
	 */
	private Runnable snapToDropScreenRunnable = new Runnable() {
		public void run() {
			mPreviewEditAdvancedController.mWorkspace.snapToScreen(screenIndex, 0, false, true, true);
			mPreviewEditAdvancedController.stopDesktopEdit();
			DragView dragView = mPreviewEditAdvancedController.mWorkspace
					.getLauncher().getDragController().getDragView();
			if (null != dragView) {
				dragView.update(DragView.MODE_NORMAL);
			}
		}
	};
	
	private Runnable animationRunnable =  new Runnable() {
		public void run() {
			if(onDragOver){
				startSelectedAnimation();
			}else{
				startUnselectedAnimation();
			}
		}
	};

	private void startSelectedAnimation(){
		Rect r = new Rect();
		getLocalVisibleRect(r);
		Animation ani = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, r.exactCenterX(), r.exactCenterY());
		ani.setDuration(150);
		ani.setFillAfter(true);
		startAnimation(ani);
	}
	
	private void startUnselectedAnimation(){
		Rect r = new Rect();
		getLocalVisibleRect(r);
		Animation ani = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, r.exactCenterX(), r.exactCenterY());
		ani.setDuration(150);
		ani.setFillAfter(true);
		startAnimation(ani);
	}
	
	public void setSelectedBackground(){
		findViewById(R.id.screen_bg).setBackgroundResource(R.drawable.preview_border_light);
	}
	
	public void setUnselectedBackground(){
		findViewById(R.id.screen_bg).setBackgroundResource(R.drawable.preview_border);
	}
	
	public void setAddScreenSelectedBackground(){
		findViewById(R.id.screen_bg).setBackgroundResource(R.drawable.preview_border_drag);
	}
	
	public void setAddScreenUnselectedBackground(){
		findViewById(R.id.screen_bg).setBackgroundResource(R.drawable.preview_border_selector);
	}

	@Override
	public int getState() {
		return 0;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		removeCallbacks(snapToDropScreenRunnable);
		ScreenViewGroup mWorkspace = mPreviewEditAdvancedController.mWorkspace;
		if(isLastView && !BaseConfig.isOnScene()){//添加一个屏幕,情景模式不支持新增屏上添加
			screenIndex = mWorkspace.getChildCount();
			mWorkspace.createScreenToWorkSpace();
		}
		mPreviewEditAdvancedController.stopDesktopEdit();
		if(!isFull && (vacantCell != null || xy != null)){
			mWorkspace.snapToScreen(screenIndex, 0, false, true, true);
			if(!BaseConfig.isOnScene()){
				if(source instanceof ScreenViewGroup){//从桌面的一个屏幕拖到另一个屏幕
					mWorkspace.dropItemToScreenFromPreview(screenIndex, vacantCell);
					return;
				}else if (mWorkspace.getLauncher().getDragController().isDragFromDrawer(source)) {// 从匣子拖到屏幕上
					ArrayList<ApplicationInfo> appList = mWorkspace.getLauncher().getDragController().getAppList();
					mWorkspace.dropToScreenFromDrawerPreview(screenIndex, dragInfo, appList);
				}else if(mWorkspace.getLauncher().getDragController().isDragFromFolder(source)){//从桌面的文件夹里拖到屏幕上
					mWorkspace.onDropFolderExternal(screenIndex,null, dragInfo);
				}else if(source instanceof BaseMagicDockbar){//从DOCK栏拖到屏幕上
					mWorkspace.dropDockbarItemToScreenFromPreview(mWorkspace.getCellLayoutAt(screenIndex), dragInfo, vacantCell);
				}
			}else{
				if(source instanceof ScreenViewGroup){//从桌面的一个屏幕拖到另一个屏幕
					mWorkspace.dropItemToScreenFromPreview(screenIndex, xy);
					return;
				}else if(mWorkspace.getLauncher().getDragController().isDragFromFolder(source)){//从桌面的文件夹里拖到屏幕上
					mWorkspace.onDropFolderExternal(screenIndex,null, dragInfo);
				}else if(source instanceof BaseMagicDockbar){//从DOCK栏拖到屏幕上
					mWorkspace.dropDockbarItemToScreenFromPreview(mWorkspace.getCellLayoutAt(screenIndex), dragInfo, xy);
				}else if(mWorkspace.getLauncher().getDragController().isDragFromDrawer(source)) {//从匣子拖到屏幕上
					ArrayList<ApplicationInfo> appList = mWorkspace.getLauncher().getDragController().getAppList();
					mWorkspace.dropToScreenFromDrawerPreview(screenIndex, dragInfo, appList);
				}
			}
			
			source.onDropCompleted(this, true);
		}else {//页面已满，拖动失败提示
			mPreviewEditAdvancedController.closeNotifyIsFullScreenZone();
			Toast.makeText(getContext(), R.string.message_preview_fail_drag_to_screen, Toast.LENGTH_SHORT).show();
			mWorkspace.snapToScreen(mWorkspace.getCurrentScreen(), 0, false, true, true);
			source.onDropCompleted(this, false);
		}
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		if(!isLastView && !isFull){
			postDelayed(snapToDropScreenRunnable, 2000);
		}
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		if(onDragOver) return;
		
		if(!isLastView && !isFull){
			onDragOver = true;
			GpuControler.destroyHardwareLayer(this);
			setSelectedBackground();
			postDelayed(animationRunnable, 100);
		}else if(isLastView){
			onDragOver = true;
			GpuControler.destroyHardwareLayer(this);
			setAddScreenSelectedBackground();
			postDelayed(animationRunnable, 100);
		}
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		if(null != snapToDropScreenRunnable){
			removeCallbacks(snapToDropScreenRunnable);
		}
		if(!isLastView && !isFull && onDragOver){
			onDragOver = false;
			GpuControler.enableHardwareLayers(this);//防止Gpu下出现动画抖动
			setUnselectedBackground();
			postDelayed(animationRunnable, 10);
		}else if(isLastView && onDragOver){
			onDragOver = false;
			GpuControler.enableHardwareLayers(this);
			setAddScreenUnselectedBackground();
			postDelayed(animationRunnable, 10);
		}else if(isFull){
			mPreviewEditAdvancedController.closeNotifyIsFullScreenZone();
		}
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		//计算drop的目标位置
		//1.计算在celllayout开始查找的位置
		ScreenViewGroup mWorkspace = mPreviewEditAdvancedController.mWorkspace;
		CellLayout cellLayout =  (CellLayout) mWorkspace.getChildAt(screenIndex);
		if(!BaseConfig.isOnScene()){
			boolean isAddScreen = false;
			if(cellLayout == null){//如果是最后一屏"添加屏"
				cellLayout =  (CellLayout) mWorkspace.getChildAt(0);
				isAddScreen = true;
			}
			int[] targetXY = getTargetCellXY(x, y, cellLayout);
			
			//2.从celllayout的开始位置查找出drop的最终目标位置
			ItemInfo itemInfo = (ItemInfo)dragInfo;
			int[] spanXY = new int[]{itemInfo.spanX, itemInfo.spanY};
			int spanX = spanXY[0];
			int spanY = spanXY[1];
			if(!isAddScreen){
				vacantCell = cellLayout.findVacantCellFromTarget(targetXY[0], targetXY[1], 
						spanX, spanY, dragView.getDragingView());
			}else{//如果是最后一屏"添加屏"
				boolean[][] mOccupied = new boolean[CellLayoutConfig.getCountX()][CellLayoutConfig.getCountY()];
				vacantCell = cellLayout.findVacantCellFromTarget(mOccupied, targetXY[0], targetXY[1], spanX, spanY);
			}
			
			//如果celllayout无空闲位置
			if(vacantCell == null && !cellLayout.isVacantForReorder(spanX, spanY, dragView.getDragingView(), true)) {
				isFull = true;
				mPreviewEditAdvancedController.showNotifyIsFullScreenZone();
			}else{
				mPreviewEditAdvancedController.closeNotifyIsFullScreenZone();
			}
		}else{//情景桌面下
			if(cellLayout != null){
				xy = cellLayout.findVacantXYForSceneLayout((ItemInfo) dragInfo);
			}else{//如果是最后一屏"添加屏"
				return false;
			}
			if(xy == null){
				isFull = true;
				mPreviewEditAdvancedController.showNotifyIsFullScreenZone();
			}else{
				mPreviewEditAdvancedController.closeNotifyIsFullScreenZone();
			}
		}
		
		
		return true;
	}

	/**
	 * Description: 根据图标在预览图上的位置，计算出该图标在CellLayout上的对应位置
	 */
	private int[] getTargetCellXY(int x, int y, CellLayout cellLayout){
		float cellLayoutMargin = getResources().getDimensionPixelSize(R.dimen.celllayout_preview_margin);//预览图中celllayout的margin
		float cellLayoutWidth = getWidth() - 2 * cellLayoutMargin;//预览图中celllayout的宽
		float cellLayoutHeight = getHeight() * cellLayout.getRateByWorkspaceHeight();//预览图中celllayout的高
		float marginHeight = getHeight() - cellLayoutHeight;
		float topMargin = cellLayoutMargin + marginHeight * PreviewImageView.topPaddingRate;
		int cellX = CellLayoutConfig.getCountX() - 1;
		int cellY = CellLayoutConfig.getCountY() - 1;
		
		float comRateX = (x - cellLayoutMargin) / cellLayoutWidth;
		for(int i = 0; i < CellLayoutConfig.getCountX(); i++){
			if(comRateX < (i + 1) * (1.0f / CellLayoutConfig.getCountX())){
				cellX = i;
				break;
			}
		}
		
		float comRateY = (y - topMargin) / cellLayoutHeight;
		for(int j = 0; j < CellLayoutConfig.getCountY(); j++){
			if(comRateY < (j + 1) * (1.0f / CellLayoutConfig.getCountY())){
				cellY = j;
				break;
			}
		}
		
		return new int[]{cellX, cellY};
	}
	
	//hjiang 不可见时去掉背景
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		
		if (visibility != View.VISIBLE) {
			setBackgroundResource(0);
		}
	}
}
