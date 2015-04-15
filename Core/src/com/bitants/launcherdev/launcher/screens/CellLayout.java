package com.bitants.launcherdev.launcher.screens;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.bitants.launcherdev.core.view.HiViewGroup;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.core.view.HiViewGroup;
import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.support.BaseCellLayoutHelper;
import com.bitants.launcherdev.launcher.support.CellLayoutReorder;
import com.bitants.launcherdev.launcher.support.CellLayoutReorder;
import com.bitants.launcherdev.launcher.support.OnWorkspaceScreenListener;
import com.bitants.launcherdev.launcher.touch.outline.DragHelper;
import com.bitants.launcherdev.launcher.touch.outline.DragHelper;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.core.view.HiViewGroup;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.info.ItemInfo;

public class CellLayout extends HiViewGroup implements ScreenViewGroup.HDSwitchView{
	private static boolean mPortrait;

	protected int mCellLayoutWidth;
	protected int mCellLayoutHeight;
	
	protected int mCellWidth;
	protected int mCellHeight;
	
	//列间距
	private static int mCellGapX = 0;
	//行间距
	private static int mCellGapY = 0;
	
	/**
	 * CellLayout在workspace中的位置
	 */
	public int mCellLayoutLocation;
	
	private final Rect mRect = new Rect();
	private final CellInfo mCellInfo = new CellInfo();

	int[] mCellXY = new int[2];
	public boolean[][] mOccupied;

	private boolean mLastDownOnOccupiedCell = false;

	/**
	 * 编辑模式下,删除按钮的缩放动画前位置
	 */
	private Rect springDelScreenBtnLocation;
	/**
	 * 编辑模式下，是否为“添加”屏
	 */
	protected boolean isSpringAddScreen;
	/**
	 * 编辑模式下，CellLayout是否已经无空闲区域
	 */
	private boolean notDropOnSpringMode;

	/**
	 * 编辑屏中心点X轴坐标（相对于整个画布的坐标）
	 */
	private int mSpringScreenCenterX;
	/**
	 * 编辑模式下绘图滤波，防锯齿
	 */
	private PaintFlagsDrawFilter antiAliasFilter;
	/**
	 * 进入或退出编辑模式开始动画的时间
	 */
	private long springAnimationStartTime = 0;
	
	private int mCountX, mCountY;
    
    protected ScreenViewGroup mWorkspace;
    
    
    private int[] mArray;    
    private int[] mArrayHelper; 
    
    /**
     * 用于绘制被拖动View的目标光亮区域
     */
  	private DragView mDragView = null;
//  	private Paint mDragSrcPaint = null;
//  	private Paint mDragNewPaint = null;
  	private Rect mDragAvailableCell = null;
  	private Rect mLastDragAvailableCell = null;
    
  	private CellLayoutReorder mCellLayoutReorder;
  	
//  	protected boolean isDrawDragOutline = true; //拖动图标时绘制光亮背景
  	protected boolean supportCountXYChange = true; //支持行列数变换
  	
    //透明图层设置
  	protected static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
  		    Canvas.CLIP_SAVE_FLAG |
  		    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
  		    Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
  		    Canvas.CLIP_TO_LAYER_SAVE_FLAG;
  	
	public void setWorkspace(ScreenViewGroup workspace) {
		this.mWorkspace = workspace;
		mCellLayoutReorder.setWorkspace(workspace);
	}
    
	public CellLayout(Context context) {
		super(context);
		setWillNotDraw(false);
        
        setHapticFeedbackEnabled(false);
		setAlwaysDrawnWithCacheEnabled(false);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		antiAliasFilter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
		mCellLayoutReorder = new CellLayoutReorder(this);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		if(widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY){
			mCellLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
			mCellLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
		}else{
//			throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
			Log.w("CellLayout", "CellLayout cannot have EXACTLY dimensions");
			int[] screenWH = ScreenUtil.getScreenWH();
			mCellLayoutWidth = screenWH[0];
			mCellLayoutHeight = screenWH[1] - CellLayoutConfig.getMarginTop() - CellLayoutConfig.getMarginBottom();
			if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
				mCellLayoutWidth = Math.min(mCellLayoutWidth, MeasureSpec.getSize(widthMeasureSpec));
				mCellLayoutHeight = Math.min(mCellLayoutHeight, MeasureSpec.getSize(heightMeasureSpec));
			}
		}

		setCountXY();//在修改了行列数之后，要重新计算每个子view的高宽	
		
		//待配置，针对不同分辨率
//		mCellGapX = CellLayoutConfig.mCellGapX = mCellLayoutWidth / 40;
//		mCellGapY = CellLayoutConfig.mCellGapY = mCellLayoutHeight / 40;
		
		mCellWidth = (mCellLayoutWidth - mCellGapX * (getCountX() - 1))/ getCountX();
		mCellHeight = (mCellLayoutHeight - mCellGapY * getCountY()) / getCountY();
		
		onMeasureChild();
		
		mCellLayoutReorder.setupCellSize(getCellWidth(), getCellHeight(), mCellGapX, mCellGapY);
		
		setMeasuredDimension(mCellLayoutWidth, mCellLayoutHeight);
	}

	protected void onMeasureChild(){
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			int childWidthMeasureSpec = lp.isOnXYAndWHMode ? MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY) : 
				MeasureSpec.makeMeasureSpec(lp.spanX * mCellWidth + (lp.spanX - 1) * mCellGapX, MeasureSpec.EXACTLY);
			int childheightMeasureSpec = lp.isOnXYAndWHMode ? MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY) : 
				MeasureSpec.makeMeasureSpec(lp.spanY * mCellHeight + (lp.spanY - 1) * mCellGapY, MeasureSpec.EXACTLY);
			child.measure(childWidthMeasureSpec, childheightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(mWorkspace.isOnEnterSpringModeAnimation()) return;
		if(getCountX() == 0 || getCountY() == 0){
			return;
		}
		int cellW = (r - l - mCellGapX * (getCountX() - 1)) / getCountX();
		int cellH = (b - t - mCellGapY * getCountY()) / getCountY();
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
				if(lp.isOnXYAndWHMode){
					child.layout(lp.x, lp.y, lp.x + lp.width, lp.y + lp.height);
					continue;
				}
				
				if(mCellLayoutReorder.interruptReorderAnimation(lp)){//若之前动画被中断，清理抖动动画
					child.clearAnimation();
				}
				
				if(supportCountXYChange && (lp.cellX >= getCountX() || lp.cellY >= getCountY())){//行列数变换时,超出的部分不显示
					child.setVisibility(View.GONE);
					continue;
				}

				int left, top;
				if(lp.isOnReorderAnimation){//还处于动画中时，不布局
					left = lp.x - getMarginLeft();
					top = lp.y - getMarginTop();
				}else{
					left = lp.cellX * (cellW + mCellGapX);
					top = lp.cellY * (cellH + mCellGapY);
					if(lp.isOnPending){
						left = lp.tmpCellX * (cellW + mCellGapX);
						top = lp.tmpCellY * (cellH + mCellGapY);
					}
				}
				child.layout(left, top, 
						left + lp.spanX * cellW + (lp.spanX - 1) * mCellGapX, 
						top + lp.spanY * cellH + (lp.spanY - 1) * mCellGapY);
				
				if (lp.dropped) {
					lp.dropped = false;
				}
			}	
		}
		initArray();
	}
	
	@Override
	public void dispatchDraw(Canvas canvas) {
		// 屏幕预览
		if (mWorkspace.isOnPreviewMode()) {
//			canvas.saveLayer(null, PaintUtils.getStaticAlphaPaint(255), LAYER_FLAGS);
			super.dispatchDraw(canvas);
			return;
		}
		if (mWorkspace.isOnSpringAnimation()) {// 打开编辑模式或者退出编辑模式
			canvas.save();
			// 透明度
			int alpha = getSpringAnimationAlpha();
			if(alpha != 0){
				drawSpringBackground(canvas, alpha);
			}
//			canvas.saveLayer(null, PaintUtils.getStaticAlphaPaint(255), LAYER_FLAGS);
			canvas.setDrawFilter(antiAliasFilter);// 抗图片锯齿
			super.dispatchDraw(canvas);
			drawSpringDelBtn(canvas, alpha);
			canvas.restore();
			if(GpuControler.isOpenGpu(this)){//GPU开启时强制刷新				
				invalidate();
			}
			return;
		}
		if (mWorkspace.isOnSpringMode()) {// 编辑模式下特效处理
			drawSpringBackground(canvas, 255);
//			canvas.saveLayer(null, PaintUtils.getStaticAlphaPaint(255), LAYER_FLAGS);
			
			canvas.save();

			canvas.setDrawFilter(antiAliasFilter);// 抗图片锯齿
			super.dispatchDraw(canvas);
			drawSpringDelBtn(canvas, 255);
			canvas.restore();
		} else {// 普通模式
			if(mWorkspace.needSaveLayerOnDispatchDraw()){
//				canvas.saveLayer(null, PaintUtils.getStaticAlphaPaint(255), LAYER_FLAGS);
			}
			super.dispatchDraw(canvas);
		}
		
//		if(isDrawDragOutline){
//			//绘制拖动View的光亮图标
//			drawDragOutline(canvas);
//		}
		DragHelper.getInstance().dispatch(canvas,this);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final CellInfo cellInfo = mCellInfo;

		if (action == MotionEvent.ACTION_DOWN) {
			final Rect frame = mRect;
			
			int x = (int) ev.getX() + getScrollX();
			int y = (int) ev.getY() + getScrollY();
			if(mWorkspace.isOnSpringMode()){//编辑模式下，长按图标时调整触点偏移量
				int[] loc = {x, y};
				BaseCellLayoutHelper.springToNormalCoordinate(loc);
				x = loc[0];
				y = loc[1];
			}
			
			final int count = getChildCount();
			boolean found = false;
			for (int i = count - 1; i >= 0; i--) {
				final View child = getChildAt(i);

				if ((child.getVisibility()) == VISIBLE || child.getAnimation() != null) {
					child.getHitRect(frame);
					if (frame.contains(x, y)) {
						final LayoutParams lp = (LayoutParams) child.getLayoutParams();
						cellInfo.cell = child;
						cellInfo.cellX = lp.cellX;
						cellInfo.cellY = lp.cellY;
						cellInfo.spanX = lp.spanX;
						cellInfo.spanY = lp.spanY;
						cellInfo.valid = true;
						found = true;
						//mDirtyTag = false;
						break;
					}
				}
			}

			mLastDownOnOccupiedCell = found;

			if (!found) {
				int cellXY[] = CellLayoutConfig.pointToCell(x, y, getCellWidth(), getCellHeight());
				mCellXY = cellXY;

				final int xCount = getCountX();
				final int yCount = getCountY();

				final boolean[][] occupied = mOccupied;
				BaseCellLayoutHelper.findOccupiedCells(occupied, null, this);

				cellInfo.cell = null;
				cellInfo.cellX = cellXY[0];
				cellInfo.cellY = cellXY[1];
				cellInfo.spanX = 1;
				cellInfo.spanY = 1;
				cellInfo.valid = cellXY[0] >= 0 && cellXY[1] >= 0 && cellXY[0] < xCount && cellXY[1] < yCount && !occupied[cellXY[0]][cellXY[1]];

				// Instead of finding the interesting vacant cells here, wait until a
				// caller invokes getTag() to retrieve the result. Finding the vacant
				// cells is a bit expensive and can generate many new objects, it's
				// therefore better to defer it until we know we actually need it.
				//mDirtyTag = true;
			}
			setTag(cellInfo);
		} else if (action == MotionEvent.ACTION_UP) {
			cellInfo.cell = null;
			cellInfo.cellX = -1;
			cellInfo.cellY = -1;
			cellInfo.spanX = 0;
			cellInfo.spanY = 0;
			cellInfo.valid = false;
			//mDirtyTag = false;
			setTag(cellInfo);
		}

		if(mWorkspace.isOnSpringMode()){//编辑模式下，屏蔽事件往下传递
			return true;
		}
		
		return false;
	}
	
	public boolean lastDownOnOccupiedCell() {
		return mLastDownOnOccupiedCell;
	}
	
	public void setCountXY() {
		mPortrait = true;
		mCountX = getCountX();
		mCountY = getCountY();
		mOccupied = new boolean[mCountX][mCountY];
		
		
		mCellLayoutReorder.setTmpOccupied(mPortrait, mCountX, mCountY);
		mCellLayoutReorder.setupCountXY(mCountX, mCountY);
	}

	public int getCountX() {
		return CellLayoutConfig.getCountX();
	}

	public int getCountY() {
		return CellLayoutConfig.getCountY();
	}
	
	public int getCellWidth() {
		return mCellWidth;
	}

	public int getCellHeight() {
		return mCellHeight;
	}

	public static int getMarginLeft() {
		return CellLayoutConfig.getMarginLeft();
	}

	public static int getMarginTop() {
		return CellLayoutConfig.getMarginTop();
	}

	public static int getMarginRight() {
		return CellLayoutConfig.getMarginRight();
	}

	public static int getMarginBottom() {
		return CellLayoutConfig.getMarginBottom();
	}
	
	public int getScreen() {
		return mCellInfo.screen;
	}
	
	/**
	 * 获取行间距
	 */
	public int getCellGapY() {
		return mCellGapY;
	}

	/**
	 * 获取列间距
	 */
	public int getCellGapX() {
		return mCellGapX;
	}

	public int getCellLayoutWidth(){
		return mCellLayoutWidth;
	}
	
	public int getCellLayoutHeight(){
		return mCellLayoutHeight;
	}
	
	public View getChildAt(int cellX, int cellY) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();

			if(lp.isOnPending){
				continue;
			}
			
			if ((lp.cellX <= cellX) && (cellX < lp.cellX + lp.spanX) && (lp.cellY <= cellY) && (cellY < lp.cellY + lp.spanY)) {
				return child;
			}
		}
		return null;
	}
	
	public View getChildAt(int[] xy) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();

			if(lp.isOnPending){
				continue;
			}
			
			if (lp.x == xy[0] && lp.y == xy[1]) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * CellLayout的高与Workspace的比例
	 * @return
	 */
	public float getRateByWorkspaceHeight(){
		return (float)mCellLayoutHeight/(float)mWorkspace.getHeight();
	}
	
	public int getCellLayoutLocation() {
		return mCellLayoutLocation;
	}

	public void setCellLayoutLocation(int mCellLayoutLocation) {
		this.mCellLayoutLocation = mCellLayoutLocation;
	}
	
	@Override
	public void cancelLongPress() {
		super.cancelLongPress();

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			child.cancelLongPress();
		}
	}

	@Override
	public void requestChildFocus(View child, View focused) {
		super.requestChildFocus(child, focused);
		if (child != null) {
			Rect r = new Rect();
			child.getDrawingRect(r);
			requestRectangleOnScreen(r);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		resetScreen();
	}
	
	/**
	 * <br>Description: 重置屏幕索引
	 * <br>Author:caizp
	 * <br>Date:2012-11-7上午11:28:10
	 */
	public void resetScreen() {
		mCellInfo.screen = ((ViewGroup) getParent()).indexOfChild(this);
	}
	
	@Override
	public CellInfo getTag() {
		return (CellInfo) super.getTag();
	}
	
	public void addView(View child, int cellX, int cellY, int spanX, int spanY, boolean insert){
		if(child != null){
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
			if (lp == null) {
				lp = new CellLayout.LayoutParams(cellX, cellY, spanX, spanY, getCellWidth(), getCellHeight());
			} else {
				lp.setup(cellX, cellY, spanX, spanY, getCellWidth(), getCellHeight());
			}
			addView(child, insert ? 0 : -1, lp);
		}
	}
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if(mWorkspace != null && child instanceof OnWorkspaceScreenListener){			
			mWorkspace.addOnWorkspaceListener(mCellLayoutLocation, (OnWorkspaceScreenListener) child);
		}
		super.addView(child, index, params);
	}
	
	@Override
	public void removeView(View view) {
		if(mWorkspace != null && view instanceof OnWorkspaceScreenListener){			
			mWorkspace.removeOnWorkspaceListener(mCellLayoutLocation, (OnWorkspaceScreenListener) view);
		}
		super.removeView(view);
	}
	
	@Override
	public void removeViewInLayout(View view) {
		if(mWorkspace != null && view instanceof OnWorkspaceScreenListener){			
			mWorkspace.removeOnWorkspaceListener(mCellLayoutLocation, (OnWorkspaceScreenListener) view);
		}
		super.removeViewInLayout(view);
	}
	
	/**
	 * Description: 用于Workspace内拖动，且CellLayout基于Cell布局时
	 * Author: guojy
	 * Date: 2013-7-15 上午11:12:37
	 */
	public void onDropChild(View child, int[] cellXY) {
		if (child != null) {
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			onDropChild(child, cellXY, new int[]{lp.spanX, lp.spanY});
		}
	}

	
	public void onDropChild(View child, int[] cellXY, int[] spanXY) {
		if (child == null)
			return;
		
		LayoutParams lp = (LayoutParams) child.getLayoutParams();
		lp.setup(cellXY[0], cellXY[1], spanXY[0], spanXY[1], getCellWidth(), getCellHeight());
		lp.tmpX = -1;
        lp.tmpY = -1;
		lp.isDragging = false;
		lp.dropped = true;
		lp.isOnPending = false;
		child.requestLayout();
		invalidate();
	}
	
	public int[] getAndReLayoutCellXY(View child, int[] targetCell){
		CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
		lp.cellX = lp.tmpCellX = targetCell[0];
		lp.cellY = lp.tmpCellY = targetCell[1];
		return new int[]{lp.cellX, lp.cellY};
	}
	
	public void onDrop(View cell, int[] cellXY, CellLayout originalCellLayout){
		onDrop(cell, cellXY, originalCellLayout, (ItemInfo) cell.getTag());
	}
	
	public void onDrop(View cell, int[] cellXY, CellLayout originalCellLayout, ItemInfo info){
		if (originalCellLayout != null) {//非当前屏内移动
			originalCellLayout.removeView(cell);
			addView(cell);
		}
		
		if(info.container != BaseLauncherSettings.Favorites.CONTAINER_DESKTOP){
			LayoutParams lp = (LayoutParams) cell.getLayoutParams();
			int[] wh = spanXYMatcher(lp.spanX, lp.spanY, info);
			info.spanX = lp.spanX = wh[0];
			info.spanY = lp.spanY =wh[1];
			
			onDropChild(cell, cellXY, new int[]{lp.spanX, lp.spanY});
		}else{
			onDropChild(cell, cellXY);
		}
		info.screen = mCellLayoutLocation;
		info.cellX = cellXY[0];
		info.cellY = cellXY[1];
		BaseLauncherModel.addOrMoveItemInDatabase(getContext(), info, BaseLauncherSettings.Favorites.CONTAINER_DESKTOP);
	}
	
	public int[] spanXYMatcher(int spanX, int spanY, Object info){
		return new int[]{spanX, spanY};
	}
	
	public void resetDragging(View child){
		if (child != null) {
			((LayoutParams) child.getLayoutParams()).isDragging = false;
		}
	}
	
	public void onDropAborted(View child) {
		if (child != null) {			
			invalidate();
		}
	}

	/**
	 * Start dragging the specified child
	 * 
	 * @param child
	 *            The child that is being dragged
	 */
	public void onDragChild(View child) {
		LayoutParams lp = (LayoutParams) child.getLayoutParams();
		lp.isDragging = true;
		
		mCellLayoutReorder.reset();
	}

	
	
	//=================================拖动View的光亮图标=====================================//
	//绘制拖动View的光亮图标
//	private void drawDragOutline(Canvas canvas){
//		if(!mWorkspace.isDragOnApplication() && mWorkspace.getDragController().isDragging() 
//				&& mWorkspace.getCurrentScreen() == mCellLayoutLocation
//				&& mDragView != null && mDragSrcPaint != null && mDragAvailableCell != null && !mDragView.isModeMin() ){
//			int width = mDragView.getWidth();
//			int height = mDragView.getHeight();
//			if(((DragViewWrapper)mDragView).isWidgetDragViewFromDrawer()){//从匣子中拖出小部件特殊处理
//				width = (int) (width * 1/mWorkspace.getSpringScale());
//				height = (int) (height * 1/mWorkspace.getSpringScale());
//			}
//			
//			float left = mDragAvailableCell.left + (mDragAvailableCell.right - mDragAvailableCell.left - width) / 2.0f;
//			float top = mDragAvailableCell.top + (mDragAvailableCell.bottom - mDragAvailableCell.top - height) / 2.0f; 
//			
//			mDragView.setPaint(mDragNewPaint, false);
//			canvas.save();
//			canvas.translate(left, top);
//			//桌面编辑模式下拖动和从匣子中拖出小部件时，画布缩放
//			if(mWorkspace.isOnSpringMode() 
//					&& (mDragView.getEventScale() != DragView.NO_SCALE || mWorkspace.isWidgetDragViewFromDrawer(mDragView))){
//				float scale = 1/mWorkspace.getSpringScale();
//				canvas.scale(scale, scale);
//			}
//			
//			mDragView.draw(canvas);
//			canvas.restore();
//			mDragView.setPaint(mDragSrcPaint, false);
//			//Log.e("draw outline", "draw outline");
//		}
//	}
	
	public void invalidateDragOutlineZone(){
		if(mDragAvailableCell != null){
			invalidate(mDragAvailableCell);
		}
		if(mLastDragAvailableCell != null){
			invalidate(mLastDragAvailableCell);
		}
	}
	
	//初始化光亮图标信息
	public void initDragOutline(DragView mDragView, Paint mDragViewNewPaint, Paint mDragViewSrcPaint){
		DragHelper.getInstance().initDragOUtline(mDragView,mWorkspace);
		
		cleanDragOutlineState();
		setDragOutline(mDragView, mDragViewNewPaint, mDragViewSrcPaint);
		
		if(!BaseConfig.isOnScene()){			
			DragHelper.getInstance().fadeInOnStartDrag(mDragView, this, mWorkspace);
		}
	}
	//清除光亮图标
	public void cleanDragOutline(){
		if(BaseConfig.isOnScene())
			return;
		
		if(mDragView == null){
			return;
		}
		cleanDragOutlineState();
		invalidate();
	}
	private void cleanDragOutlineState(){
		mDragView = null;
		mDragAvailableCell = null;
		DragHelper.getInstance().clearAllScreensOutline();
//		mDragSrcPaint = null;
//		mDragNewPaint = null;
	}
	//是否改变光亮图标位置
	public boolean changeDragOutline(Rect availableCell){
		if(availableCell == null){
			mDragAvailableCell = null;
			return true;
		}
		
		availableCell.offset(0, -mWorkspace.getTopPadding());
		if(mDragAvailableCell == null){
			mDragAvailableCell = new Rect(availableCell);
			DragHelper.getInstance().fadeInIfNeed(mDragAvailableCell,this);
			return true;
		}
		
		if(mDragAvailableCell.equals(availableCell)){
			return false;
		}else{
			mLastDragAvailableCell = mDragAvailableCell;
			mDragAvailableCell = new Rect(availableCell);
			DragHelper.getInstance().fadeInAndFadeOut(mDragAvailableCell,this);
			return true;
		}
	}
	
	//重置亮图标位置并刷新
	public void resetDragAvailableCell(){
		mLastDragAvailableCell = mDragAvailableCell;
		mDragAvailableCell = null;
		invalidateDragOutlineZone();
	}
	
	public void setDragOutline(DragView mDragView, Paint mDragViewNewPaint, Paint mDragViewSrcPaint) {
		this.mDragView = mDragView;
//		this.mDragNewPaint = mDragViewNewPaint;
//		this.mDragSrcPaint = mDragViewSrcPaint;
	}
	 

	
	//=================================编辑模式=====================================//
	public int getSpringScreenCenterX(){
		return mSpringScreenCenterX;
	}
	
	public void setSpringScreenCenterX(int mSpringScreenCenterX){
		this.mSpringScreenCenterX = mSpringScreenCenterX;
	}
	
	public void setNotDropOnSpringMode() {
		if(!notDropOnSpringMode){
			notDropOnSpringMode = true;
			invalidate();
		}
	}
	public void setAcceptDropOnSpringMode(){
		if(notDropOnSpringMode){
			notDropOnSpringMode = false;
			invalidate();
		}
	}
	
	public void setSpringAddScreen(boolean isSpringAddScreen) {
		this.isSpringAddScreen = isSpringAddScreen;
	}

	public boolean isSpringAddScreen() {
		return isSpringAddScreen;
	}
	/**
	 * Description: 是否点击到编辑模式的删除屏按钮
	 * Author: guojy
	 * Date: 2012-7-18 下午05:59:44
	 */
	public boolean isOnSpringDelScreenBtn(int x, int y){
		if(springDelScreenBtnLocation == null){
			return false;
		}
		return springDelScreenBtnLocation.contains(x, y);
	}
	
	public void drawSpringBackground(Canvas canvas, int alpha){
		//绘制背景图
		if(notDropOnSpringMode){
			mWorkspace.drawSpringNoVacantBackground(255, canvas);
		}else{
			mWorkspace.drawSpringBackground(alpha, canvas);
		}
		//绘制添加屏按钮
		if(isSpringAddScreen){
			mWorkspace.drawSpringAddBtn(alpha, canvas);
		}
	}
	
	public void drawSpringDelBtn(Canvas canvas, int alpha){
		//绘制编辑时的删除按钮
		if(!BaseLauncher.hasDrawer && getChildCount() > 0)
			return;
		
		if(mWorkspace.getWorkspaceSpring().isNotDrawDelBtn())
			return;
		
		if(!isSpringAddScreen && mWorkspace.getChildCount() > 2){
			springDelScreenBtnLocation = mWorkspace.drawSpringDelBtn(alpha, canvas);
		}
	}
	
	public void setSpringAnimationStartTime(long springAnimationStartTime) {
		this.springAnimationStartTime = springAnimationStartTime;
	}
	
	private int getSpringAnimationAlpha(){
		int alpha = 255;
		if(springAnimationStartTime == 0){
			springAnimationStartTime = System.currentTimeMillis();
		}
		int diff = (int) (System.currentTimeMillis() - springAnimationStartTime);
		if(diff < 0){
			diff = 0;
		}else if(diff > 255){
			diff = 255;
		}
		
		if(mWorkspace.isOnEnterSpringModeAnimation()){//进入编辑模式
			if(GpuControler.isOpenGpu(this)){
				alpha = (int) (System.currentTimeMillis() - springAnimationStartTime);
				alpha = Math.min(alpha, 255);
			}else{
				alpha = diff;
			}
		}else{
			alpha = 255 - diff;
		}
		
		return alpha;
	}
	
	//========================================滑屏特效使用===============================//
	/**
	 * 初始化mArray mArrayHelper 数组
	 * mArray 中存储指定下班所存储的子view的indext
	 * author:zhenghonglin
	 */
	public void initArray(){
		mArray = new int[getCountX()*getCountY()];
		mArrayHelper = new int[getCountX()*getCountY()];
	    for(int i=0; i<getChildCount(); i++){
	    	View view = getChildAt(i);
	    	LayoutParams params = (LayoutParams) view.getLayoutParams();    	
    		for(int j=params.cellY;j<params.cellY+params.spanY;j++){
    			for(int k=params.cellX; k<params.cellX+params.spanX; k++){
    				try{
        				mArray[j*getCountX()+k] = i+1;
        				mArrayHelper[j*getCountX()+k] = (j-params.cellY)*params.spanX+(k-params.cellX)+1;
    				}catch(ArrayIndexOutOfBoundsException ex){
    					continue;
    				}
    			}
    		}
	    }
	}
	
	public View getChildViewByIndex(int index){
		if(mArray!=null)
		{
			try {
				return getChildAt(mArray[index]-1);
			} catch (Exception e) {
				initArray();
				return getChildAt(mArray[index]-1);
			}
			
		}
		else {
			return null;
		}
	}
	
	public int getWidgetViewByIndex(int index){
		return mArrayHelper[index];
	}

	/**
	 * 
	 * <br>Description:获取当前CellLayout的index
	 * <br>Author:zhenghonglin
	 */
	public int getLayoutIndex() {
		return mCellLayoutLocation;
	}

	
	
	//====================================性能优化===================================//
	@Override
	protected void setChildrenDrawingCacheEnabled(boolean enabled) {
		super.setChildrenDrawingCacheEnabled(enabled);
//		final int count = getChildCount();
//		for (int i = 0; i < count; i++) {
//			final View view = getChildAt(i);
//			Object tag = view.getTag();
//			if (tag == null)
//				continue;
//			if (!(tag instanceof WidgetInfo))
//				continue;
//			
//			view.setDrawingCacheEnabled(enabled);
//		}
	}

	@Override
	protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
		super.setChildrenDrawnWithCacheEnabled(enabled);
	}
	
	public void enableHardwareLayers() {
		GpuControler.enableHardwareLayers(this);
	}
	
    public void destroyHardwareLayer() {
    	GpuControler.destroyHardwareLayer(this);
    }
    
	
	//====================================自定义布局===================================//
	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new CellLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof CellLayout.LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new CellLayout.LayoutParams(p);
	}
	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams {
		/**
		 * Horizontal location of the item in the grid.
		 */
		@ViewDebug.ExportedProperty
		public int cellX;

		/**
		 * Vertical location of the item in the grid.
		 */
		@ViewDebug.ExportedProperty
		public int cellY;
        
		/**
		 * Number of cells spanned horizontally by the item.
		 */
		@ViewDebug.ExportedProperty
		public int spanX;

		/**
		 * Number of cells spanned vertically by the item.
		 */
		@ViewDebug.ExportedProperty
		public int spanY;

		/**
		 * Is this item currently being dragged
		 */
		public boolean isDragging;

		/**
		 * view的定位坐标(x, y)
		 */
		public int x;
		public int y;
		/**
		 * (x, y)坐标的类型，0表示左上点，1表示右上点， 2表示右下点， 3
		 */
		public int xyType = 0; 
		
//		public int width;
//		public int height;
		
		//动画移动后的cell位置
        public int tmpCellX;
        public int tmpCellY;
        //动画移动后的坐标位置
        public int tmpX = -1;
        public int tmpY = -1;
		
		//用于标示是否已经动画移到其它位置
		public boolean isOnPending = false;
		//是否处于图标拖动引起的位置变换动画中
		public boolean isOnReorderAnimation = false;
		
		public boolean regenerateId;

		public boolean dropped;
		
		/**
		 * 是否直接根据xy和高宽进行布局
		 */
		public boolean isOnXYAndWHMode = false;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			spanX = 1;
			spanY = 1;
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
			spanX = 1;
			spanY = 1;
		}
		
		public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan, int cellW, int cellH) {
			super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			setup(cellX, cellY, cellHSpan, cellVSpan, cellW, cellH);
		}

		public void setup(int cellX, int cellY, int cellHSpan, int cellVSpan, int cellW, int cellH){
			this.cellX = cellX;
			this.cellY = cellY;
			this.spanX = cellHSpan;
			this.spanY = cellVSpan;
			this.width = cellW * cellHSpan + mCellGapX * (cellHSpan - 1);
			this.height = cellH * cellVSpan + mCellGapY * (cellVSpan - 1);
			this.x = (cellW + mCellGapX) * cellX + getMarginLeft(); 
			this.y = (cellH + mCellGapY) * cellY + getMarginTop();
		}

		public void resetXY(int cellW, int cellH){
			this.x = (cellW + mCellGapX) * cellX + getMarginLeft(); 
			this.y = (cellH + mCellGapY) * cellY + getMarginTop();
		}
		
		public int[] getNewXY(int cellX, int cellY, int cellWidth, int cellHeight, int cellW, int cellH){
			int[] xy = new int[2]; 
			xy[0] = cellX * (cellW + mCellGapX) + getMarginLeft() ; 
			xy[1] = cellY * (cellH + mCellGapY) + getMarginTop(); 
			return xy;
		}
		
	}

	
	//====================================CellLayout信息类===================================//
	public static final class CellInfo implements ContextMenu.ContextMenuInfo {
		/**
		 * See View.AttachInfo.InvalidateInfo for futher explanations about the
		 * recycling mechanism. In this case, we recycle the vacant cells
		 * instances because up to several hundreds can be instanciated when the
		 * user long presses an empty cell.
		 */
		public static final class VacantCell {
			public int cellX;
			public int cellY;
			public int spanX;
			public int spanY;

			// We can create up to 523 vacant cells on a 4x4 grid, 100 seems
			// like a reasonable compromise given the size of a VacantCell and
			// the fact that the user is not likely to touch an empty 4x4 grid
			// very often
			private static final int POOL_LIMIT = 100;
			private static final Object sLock = new Object();

			private static int sAcquiredCount = 0;
			private static VacantCell sRoot;

			private VacantCell next;

			public static VacantCell acquire() {
				synchronized (sLock) {
					if (sRoot == null) {
						return new VacantCell();
					}

					VacantCell info = sRoot;
					sRoot = info.next;
					sAcquiredCount--;

					return info;
				}
			}

			public void release() {
				synchronized (sLock) {
					if (sAcquiredCount < POOL_LIMIT) {
						sAcquiredCount++;
						next = sRoot;
						sRoot = this;
					}
				}
			}

			@Override
			public String toString() {
				return "VacantCell[x=" + cellX + ", y=" + cellY + ", spanX=" + spanX + ", spanY=" + spanY + "]";
			}
		}

		public View cell;
		public int cellX;
		public int cellY;
		public int spanX;
		public int spanY;
		public int screen;
		public boolean valid;

		public final ArrayList<VacantCell> vacantCells = new ArrayList<VacantCell>(VacantCell.POOL_LIMIT);
		public int maxVacantSpanX;
		public int maxVacantSpanXSpanY;
		public int maxVacantSpanY;
		public int maxVacantSpanYSpanX;
		final Rect current = new Rect();

		void clearVacantCells() {
			final ArrayList<VacantCell> list = vacantCells;
			final int count = list.size();

			for (int i = 0; i < count; i++)
				list.get(i).release();

			list.clear();
		}

		@Override
		public String toString() {
			return "Cell[view=" + (cell == null ? "null" : cell.getClass()) + ", x=" + cellX + ", y=" + cellY + "]";
		}
	}

	
	
	//====================================拖动桌面图标位置查找===================================//
	/**
	 * 找出页面上可以摆放该部件的第一个位置
	 * @param spanX 部件的span长度
	 * @param spanY 部件的span高度
	 * @return
	 */
	public int[] findFirstVacantCell(int spanX, int spanY, View ignoreView, boolean resetOccupied){
		if(resetOccupied){
			findOccupiedCells(ignoreView);
		}
		return BaseCellLayoutHelper.findFirstVacantCell(mOccupied, spanX, spanY);
	}
	/**
	 * Description: 从CellLayout底部开始找可以摆放该部件的位置
	 * Author: guojy
	 * Date: 2013-1-16 上午9:55:17
	 */
	public int[] findVacantCellFromBottom(int spanX, int spanY, View ignoreView){
		findOccupiedCells(ignoreView);
		return BaseCellLayoutHelper.findVacantCellFromBottom(mOccupied, spanX, spanY);
	}
	/**
	 * Description: 从CellLayout的特定位置(targetCellX, targetCellY)开始找可以摆放该部件的位置
	 * Author: guojy
	 * Date: 2013-1-17 上午11:42:06
	 */
	public int[] findVacantCellFromTarget(int targetCellX, int targetCellY, int spanX, int spanY, View ignoreView){
		findOccupiedCells(ignoreView);
		return findVacantCellFromTarget(mOccupied, targetCellX, targetCellY, spanX, spanY);
	}
	
	public int[] findVacantCellFromTarget(boolean[][] occupied, int targetCellX, int targetCellY, int spanX, int spanY){
		return BaseCellLayoutHelper.findVacantCellFromTargetCell(occupied, spanX, spanY, targetCellX, targetCellY);
	}
	
	public ArrayList<int[]> findAllVacantCellFromBottom(int spanX, int spanY, View ignoreView){
		findOccupiedCells(ignoreView);
		return BaseCellLayoutHelper.findAllVacantCellFromBottom(mOccupied, spanX, spanY);
	}
	
	//重新设置当前CellLayout的mOccupied
	private void findOccupiedCells(View ignoreView){
		BaseCellLayoutHelper.findOccupiedCells(mOccupied, ignoreView, this);
	}
	
	/**
	 * Description: 根据拖动app的大小，找出CellLayout上所有可放置该app区域
	 * Author: guojy
	 * Date: 2012-9-7 上午10:06:15
	 */
	public CellInfo findAllVacantCells(boolean[] occupiedCells, int spanX, int spanY) {
		final int xCount = getCountX();
		final int yCount = getCountY();

		boolean[][] occupied;
		if (occupiedCells != null) {
			occupied = new boolean[xCount][yCount];
			for (int y = 0; y < yCount; y++) {
				for (int x = 0; x < xCount; x++) {
					occupied[x][y] = occupiedCells[y * xCount + x];
				}
			}
		} else {
			occupied = mOccupied;
		}

		return BaseCellLayoutHelper.findAllVacantCells(occupied, spanX, spanY, xCount, yCount, mCellInfo.screen);
	}
	
	/**
	 * Find a vacant area that will fit the given bounds nearest the requested
	 * cell location.
	 * @param recycle  
	 * 			Previously returned value to possibly recycle.
	 */
	public int[] findNearestVacantArea(int pixelX, int pixelY, int spanX, int spanY, CellInfo vacantCells, int[] recycle) {

		// Keep track of best-scoring drop area
		final int[] bestXY = recycle != null ? recycle : new int[2];
		final int[] cellXY = mCellXY;
		double bestDistance = Double.MAX_VALUE;

		// Bail early if vacant cells aren't valid
		if (!vacantCells.valid) {
			return null;
		}

		// Look across all vacant cells for best fit
		final int size = vacantCells.vacantCells.size();
		for (int i = 0; i < size; i++) {
			final CellInfo.VacantCell cell = vacantCells.vacantCells.get(i);

			// Reject if vacant cell isn't our exact size
			if (cell.spanX != spanX || cell.spanY != spanY) {
				continue;
			}

			// Score is center distance from requested pixel
			if(mWorkspace.isOnSpringMode()){
				BaseCellLayoutHelper.getSpringCellToPoint(cell.cellX, cell.cellY, cellXY);
			}else{
				cellXY[0] = cell.cellX * (getCellWidth() + mCellGapX);
				cellXY[1] = cell.cellY * (getCellHeight() + mCellGapY) + getMarginTop();
			}

			double distance = Math.sqrt(Math.pow(cellXY[0] - pixelX, 2) + Math.pow(cellXY[1] - pixelY, 2));
			if (distance <= bestDistance) {
				bestDistance = distance;
				bestXY[0] = cell.cellX;
				bestXY[1] = cell.cellY;
			}
		}

		// Return null if no suitable location found
		if (bestDistance < Double.MAX_VALUE) {
			return bestXY;
		} else {
			return null;
		}
	}
	
	/**
	 * Find a vacant area that will fit the given bounds nearest the requested
	 * cell location.
	 * @param ignoreOccupied
	 *            If true, the result can be an occupied cell
	 * @param result
	 *            Array in which to place the result, or null (in which case a
	 *            new array will be allocated)
	 */
	public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, View ignoreView, boolean ignoreOccupied, int[] result) {
		
		// mark space take by ignoreView as available (method checks if ignoreView is null)
		markCellsAsUnoccupiedForView(ignoreView);

		// For items with a spanX / spanY > 1, the passed in point (pixelX, pixelY) corresponds
		// to the center of the item, but we are searching based on the top-left cell, so
		// we translate the point over to correspond to the top-left.
		pixelX -= (getCellWidth() + mCellGapX) * (spanX - 1) / 2f;
		pixelY -= (getCellHeight() + mCellGapY) * (spanY - 1) / 2f;

		// Keep track of best-scoring drop area
		final int[] bestXY = result != null ? result : new int[2];
		double bestDistance = Double.MAX_VALUE;

		final int countX = mCountX;
		final int countY = mCountY;
		final boolean[][] occupied = mOccupied;

		for (int y = 0; y < countY - (spanY - 1); y++) {
			inner: for (int x = 0; x < countX - (spanX - 1); x++) {
				if (ignoreOccupied) {
					for (int i = 0; i < spanX; i++) {
						for (int j = 0; j < spanY; j++) {
							if (occupied[x + i][y + j]) {
								// small optimization: we can skip to after the column we just found an occupied cell
								x += i;
								continue inner;
							}
						}
					}
				}
				final int[] centerXY = CellLayoutConfig.getCenterXY(x, y, getCellWidth(), getCellHeight());

				double distance = Math.sqrt(Math.pow(centerXY[0] - pixelX, 2) + Math.pow(centerXY[1] - pixelY, 2));
				if (distance <= bestDistance) {
					bestDistance = distance;
					bestXY[0] = x;
					bestXY[1] = y;
				}
			}
		}
		// re-mark space taken by ignoreView as occupied
		markCellsAsOccupiedForView(ignoreView);

		// Return -1, -1 if no suitable location found
		if (bestDistance == Double.MAX_VALUE) {
			bestXY[0] = -1;
			bestXY[1] = -1;
		}
		return bestXY;
	}
	
	public int getVacantCellSum() {
		final int xCount = getCountX();
		final int yCount = getCountY();
		
		findOccupiedCells(null);
		final boolean[][] occupied = mOccupied;

		int size = 0;
		for (int y = 0; y < yCount; y++) {
			for (int x = 0; x < xCount; x++) {
				if(!occupied[x][y]){
					size ++;
				}
			}
		}
		return size;
	}
	
	public void markCellsAsUnoccupiedForView(View view) {
		if (view == null || view.getParent() != this)
			return;
		LayoutParams lp = (LayoutParams) view.getLayoutParams();
		markCellsForView(lp.cellX, lp.cellY, lp.spanX, lp.spanY, false);
	}

    public void markCellsAsOccupiedForView(View view) {
        if (view == null || view.getParent() != this) return;
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        markCellsForView(lp.cellX, lp.cellY, lp.spanX, lp.spanY, true);
    }
    
    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean value) {
		for (int x = cellX; x < cellX + spanX && x < mCountX; x++) {
			for (int y = cellY; y < cellY + spanY && y < mCountY; y++) {
				mOccupied[x][y] = value;
			}
		}
	}
    
    /**
	 * Description: 
	 * Author: guojy
	 * Date: 2012-9-7 上午09:44:43
	 */
	public void initOccupied(View ignoreView, boolean isVerifyAcceptDropApp){
		if(isVerifyAcceptDropApp){
			//判断是否允许在编辑模式下拖动app放到CellLayout 
			//判断条件：1.CellLayout有空间；2.CellLayout虽已无空间但该CellLayout含有app或folder，就允许拖动到该CellLayout
			BaseCellLayoutHelper.findOccupiedCellsForApp(mOccupied, ignoreView, this);
		}else{
			findOccupiedCells(ignoreView);
		}
	}
	
	//============================================拖动桌面图标的挤动动画实现=======================================================//
	public void setItemPlacementDirty(boolean dirty){
		mCellLayoutReorder.setItemPlacementDirty(dirty);
	}
	
	public void setOnReorderAnimation(boolean isOnReorderAnimation){
		mCellLayoutReorder.setOnReorderAnimation(isOnReorderAnimation);
	}
	
	public boolean isOnReorderAnimation() {
		return mCellLayoutReorder.isOnReorderAnimation();
	}
	
	public boolean isNearestDropLocationOccupied(int pixelX, int pixelY, int spanX, int spanY,
            View dragView, int[] targetCell) {
		return mCellLayoutReorder.isNearestDropLocationOccupied(pixelX, pixelY, spanX, spanY, dragView, targetCell);
	}
	
	public boolean revertReorderOnDragOver(int minCellX, int maxCellX, int minCellY, int maxCellY, boolean reorderAni) {
		return mCellLayoutReorder.revertReorderOnDragOver(minCellX, maxCellX, minCellY, maxCellY, reorderAni);
	}
	
	public boolean isItemPlacementDirty() {
		return mCellLayoutReorder.isItemPlacementDirty();
	}
	
	public void revertTempState() {
		mCellLayoutReorder.revertTempState(); 
	}
	
	public void cleanReorderAnimations(){
	  mCellLayoutReorder.cleanReorderAnimations();
	}
	
	//计算(x,y)与cell中心位置的距离
    public float getDistanceFromCell(int x, int y, int[] cell) {
    	return mCellLayoutReorder.getDistanceFromCell(x, y, cell);
    }
    
    public void createArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            View child, boolean notDefaultStyle) {
		mCellLayoutReorder.createArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, child, notDefaultStyle);
	}

    public void handleReorderPendingViews(int screen){
		mCellLayoutReorder.handleReorderPendingViews(getContext(), screen);
	}
    
    public boolean isOnReorderHintAnimation(){
    	return mCellLayoutReorder.isOnReorderHintAnimation();
    }
	/**
  	 * Description: celllayout中的View移动位置后，是否有空闲区域可以摆放目标部件
  	 * Author: guojy
  	 * Date: 2013-1-25 上午10:57:23
  	 */
  	public boolean isVacantForReorder(int spanX, int spanY, View ignoreView, boolean resetOccupied){
  		if(resetOccupied){
			findOccupiedCells(ignoreView);
		}
  		return BaseCellLayoutHelper.isVacantForReorder(mOccupied, spanX, spanY);
  	}
  	
  	public int[] findVacantXYForSceneLayout(ItemInfo info){
  		return null;
  	}
  	
  	
}
