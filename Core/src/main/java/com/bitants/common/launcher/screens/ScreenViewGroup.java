package com.bitants.common.launcher.screens;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Scroller;
import android.widget.Toast;

import com.bitants.common.framework.effect.ScreenEffects;
import com.bitants.common.kitset.util.HiAnimationUtils;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.WidgetInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.support.BaseCellLayoutHelper;
import com.bitants.common.launcher.support.OnWorkspaceScreenListener;
import com.bitants.common.launcher.support.WorkspaceSpring;
import com.bitants.common.launcher.touch.BaseDragController;
import com.bitants.common.launcher.touch.DragSource;
import com.bitants.common.launcher.touch.MultiGestureDispatcher;
import com.bitants.common.launcher.touch.outline.DragHelper;
import com.bitants.common.core.view.HiViewGroup;
import com.bitants.common.framework.effect.EffectsType;
import com.bitants.common.framework.effect.SpringEffectsFactory;
import com.bitants.common.framework.view.commonsliding.CommonLightbar;
import com.bitants.common.kitset.GpuControler;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.common.launcher.config.ConfigFactory;
import com.bitants.common.launcher.config.preference.BaseConfigPreferences;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.model.BaseLauncherSettings;
import com.bitants.common.launcher.screens.dockbar.LightBarInterface;
import com.bitants.common.launcher.view.DragView;
import com.bitants.common.launcher.view.icon.ui.impl.IconMaskTextView;
import com.bitants.common.R;
import com.bitants.common.launcher.support.CellLayoutReorder;
import com.bitants.common.launcher.support.WallpaperHelper;
import com.bitants.common.launcher.touch.DragScroller;
import com.bitants.common.launcher.touch.DropTarget;
import com.bitants.common.launcher.touch.MultiGestureController;
import com.bitants.common.launcher.touch.WorkspaceDragAndDrop;

public class ScreenViewGroup extends HiViewGroup implements DragScroller, MultiGestureDispatcher, DropTarget, DragSource {
	private static final String TAG = "ScreenViewGroup";
	
	protected int screenWidth, screenHeight;
	protected int pageHeight, pageWidth;
	protected int topPadding, bottomPadding;
	
	private WorkspaceOvershootInterpolator mScrollInterpolator;
	protected Scroller mScroller;
	private int mTouchSlop;
	
	/**
	 * 初次安装时的最大屏幕数
	 */
	public static int MAX_SCREEN = 9;
	/**
	 * 初次安装时的屏幕总数
	 */
	public static int DEFAULT_SCREEN_COUNT = 4;
	/**
	 * 初次安装时的默认屏
	 */
	public static int DEFAULT_SCREEN = 1;
	
	
	/**
	 * 默认屏
	 */
    protected int mDefaultScreen = 1;
	/**
	 * 当前屏
	 */
    protected int mCurrentScreen = 1;
	
	/**
	 * 滑屏流畅控制
	 */
	private static final float NANOTIME_DIV = 1000000000.0f;
	private static final float SMOOTHING_SPEED = 0.75f;
	private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math.log(SMOOTHING_SPEED));
	private float mSmoothingTime;
	protected float mTouchX;
	
	private static final int INVALID_SCREEN = -999;
	protected int mNextScreen = INVALID_SCREEN;
	/**
	 * 手势状态
	 */
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private final static int TOUCH_SWIPE_DOWN_GESTURE = 2;
	private final static int TOUCH_SWIPE_UP_GESTURE = 3;
	private int mTouchState = TOUCH_STATE_REST;
	
	/**
	 * 循环滚动
	 */
	protected boolean isEndlessScrolling = false;
	
	/**
	 * NORMAL_MODE 正常模式
	 * SPRING_MODE 编辑模式
	 */
	private static final int NORMAL_MODE = 1;
	private static final int SPRING_MODE = 2;
	private static int state = NORMAL_MODE; 
	
	/**
	 * 滑屏速度
	 */
	private VelocityTracker mVelocityTracker;
	private int mMaximumVelocity;
	private static final float BASELINE_FLING_VELOCITY = 2500.f;
	private static final float FLING_VELOCITY_INFLUENCE = 0.3f;
	private static final int SNAP_VELOCITY = 300;//超过该值时，屏幕滑动
	
	private static final int NORMAL_SNAP_VELOCITY_BY_DIP = 950;	//正常滑屏速率(dip)
	private int mNormalSnapVelocity; ////正常滑屏速率(px)
	
	/**
	 * 上次触屏坐标
	 */
	private float mLastMotionX, mLastMotionY;
	/**
	 * 手指在y轴的移动距离
	 */
	private int fingerOffsetY;
	
	/**
	 * 当子View拦截手势时，最后一次触摸操作是否为ACTION_DOWN
	 */
	private boolean mIsLastActionDownWhenChildCatchTouchEvent = false;
	
	private static final int INVALID_POINTER = -1;
	private int mActivePointerId = INVALID_POINTER;
	/**
	 * 最后一次MotionEvent.ActionDown事件时的workspace状态
	 */
	private int lastActionDownState = NORMAL_MODE;
	/**
	 * 是否多点触屏
	 */
	private boolean isMultiTouch = false;
	private float oldMultiTouchDist;
	/**
	 * 多点触屏判断值
	 */
	private static final float MULTI_DIST = 10f;
	/**
	 * 手势管理
	 */
	private MultiGestureController mGestureController = new MultiGestureController( this );
	private boolean mAllowLongPress = true;
	/**
	 * 放手时坐标
	 */
	protected int[] mTouchUpLocation = new int[2];
	/**
	 * 控制是否处理捏手势
	 */
	private boolean shouldActionPinch = false;
	/**
	 * 控制是否处理展开手势
	 */
	private boolean shouldActionSpread = false;
	
	protected CellLayout.CellInfo mVacantCache = null;
	protected OnLongClickListener mLongClickListener;
	protected BaseDragController mDragController;
	protected LightBarInterface lightbar;
	protected WorkspaceSpring mWorkspaceSpring;
	private WallpaperHelper mWallpaperHelper;
	protected BaseLauncher mLauncher;
	
	private boolean mFirstLayout = true;
	
	protected boolean cleanDragInfoOnDrop = true;
	/**
	 * CellInfo for the cell that is currently being dragged
	 */
	protected CellLayout.CellInfo mDragInfo;
	
	protected WorkspaceDragAndDrop mWorkspaceDragAndDrop;
	
	/**
	 * 是否可被DragController找到
	 */
	private int dropState = AVAIABLE;
	
	/**
	 * 屏幕编辑模式Helper
	 */
	BaseCellLayoutHelper cellLayoutHelper;
	
	private boolean isDropViewFromDrawer = false;//标示是否从匣子拖动view并drop到桌面
	
	//View在Workspace当前屏和离开Workspace当前屏时的回调监听集合
	private SparseArray<ArrayList<OnWorkspaceScreenListener>> onWorkspaceListener
		= new SparseArray<ArrayList<OnWorkspaceScreenListener>>();
	private int lastListenerScreen = -1;
		
	private static class WorkspaceOvershootInterpolator implements Interpolator {
		// private static final float DEFAULT_TENSION = 1.3f;
		private static final float DEFAULT_TENSION = 0f;
		private float mTension;

		public WorkspaceOvershootInterpolator() {
			mTension = DEFAULT_TENSION;
		}

		public void setDistance(int distance) {
			mTension = distance > 0 ? DEFAULT_TENSION / distance : DEFAULT_TENSION;
		}

		public void disableSettle() {
			mTension = 0.f;
		}

		public float getInterpolation(float t) {
			// _o(t) = t * t * ((tension + 1) * t + tension)
			// o(t) = _o(t - 1) + 1
			t -= 1.0f;
			return t * t * ((mTension + 1) * t + mTension) + 1.0f;
		}
	}
	
	/**
	 * 可打开硬件加速缓存View
	 */
	public interface HDSwitchView{
		public void enableHardwareLayers();
	    public void destroyHardwareLayer();
	}
	
	public ScreenViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScreenViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setHapticFeedbackEnabled(false);
		mScrollInterpolator = new WorkspaceOvershootInterpolator();
		mScroller = new Scroller(getContext(), mScrollInterpolator);

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		mNormalSnapVelocity = ScreenUtil.dip2px(getContext(), NORMAL_SNAP_VELOCITY_BY_DIP);
		
		topPadding = CellLayoutConfig.getMarginTop();
		bottomPadding = CellLayoutConfig.getMarginBottom();
		
		cellLayoutHelper = new BaseCellLayoutHelper();
		mWorkspaceSpring = new WorkspaceSpring(this);
	}

	@Override
	public void addView(View child, int index, LayoutParams params) {
		validateHDSwitchView(child);
		super.addView(child, index, params);
		((HDSwitchView)child).enableHardwareLayers();
	}

	@Override
	public void addView(View child) {
		validateHDSwitchView(child);
		super.addView(child);
		((HDSwitchView)child).enableHardwareLayers();
	}

	@Override
	public void addView(View child, int index) {
		validateHDSwitchView(child);
		super.addView(child, index);
		((HDSwitchView)child).enableHardwareLayers();
	}

	@Override
	public void addView(View child, int width, int height) {
		validateHDSwitchView(child);
		super.addView(child, width, height);
		((HDSwitchView)child).enableHardwareLayers();
	}

	@Override
	public void addView(View child, LayoutParams params) {
		validateHDSwitchView(child);
		super.addView(child, params);
		((HDSwitchView)child).enableHardwareLayers();
	}
	
	public void validateHDSwitchView(View child){
		if (!(child instanceof HDSwitchView)) {
			throw new IllegalArgumentException("A ScreenViewGroup can only have HDSwitchView children.");
		}
	}
	
	public boolean isDefaultScreenShowing() {
		return mCurrentScreen == mDefaultScreen;
	}
	
	public int getDefaultScreen() {
		return mDefaultScreen;
	}
	
	public int getCurrentScreen() {
		return mCurrentScreen;
	}
	
	public void setCurrentScreen(int currentScreen) {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		clearVacantCache();
		mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
		scrollTo(mCurrentScreen * getWidth(), 0);
		if(!mWorkspaceSpring.isLockUpdateWallPaper()){
			updateWallpaperOffset();
		}
		invalidate();
	}
	
	public void addInCurrentScreen(View child, int cellX, int cellY, int spanX, int spanY, boolean insert, boolean ani) {
		addInScreen(child, mCurrentScreen, cellX, cellY, spanX, spanY, insert, ani);
	}
	
	public void addInScreen(View child, int screen, int cellX, int cellY, int spanX, int spanY) {
		addInScreen(child, screen, cellX, cellY, spanX, spanY, false, false);
	}
	
	public void addInScreen(View child, int screen, int cellX, int cellY, int spanX, int spanY, boolean insert, boolean ani) {
		if (screen < 0) {
			Log.e(TAG, "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child");
			return;
		}
		while (screen >= getChildCount() && screen < MAX_SCREEN){//加载桌面时屏幕数不够时自增屏幕 caizp 2012-8-7
			createScreenToWorkSpace();
		}

		clearVacantCache();

		final CellLayout group = (CellLayout) getChildAt(screen);
		if(group == null)
			return;
		group.addView(child, cellX, cellY, spanX, spanY, insert);
		
		child.setHapticFeedbackEnabled(false);
		child.setOnLongClickListener(mLongClickListener);
		
		if (child instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) child);
		}
		if (ani) {
			child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.workspace_app_enter));
		}
	}
	
	public void addViewInScreenFromDockbar(View view, CellLayout cellLayout, ItemInfo info, int targetScreen){
		cellLayout.addView(view);
		view.setHapticFeedbackEnabled(false);
		view.setOnLongClickListener(mLongClickListener);
		if (view instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) view);
		}
		cellLayout.onDrop(view, new int[]{info.cellX, info.cellY}, null, info);
	}
	
	public void addInScreenWithNoEvent(View child, int screen, int cellX, int cellY, int spanX, int spanY, boolean insert, boolean ani) {
		if (screen < 0) {
			Log.e("ScreenViewGroup", "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child");
			return;
		}
		final CellLayout group = (CellLayout) getChildAt(screen);
		group.addView(child, cellX, cellY, spanX, spanY, insert);
	}
	
	public void clearVacantCache() {
		if (mVacantCache != null) {
			mVacantCache.clearVacantCells();
			mVacantCache = null;
		}
	}

	public void setOnLongClickListener(OnLongClickListener l) {
		mLongClickListener = l;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).setOnLongClickListener(l);
		}
	}

	public OnLongClickListener getOnLongClickListener() {
		return mLongClickListener;
	}

	public void setLightBar(LightBarInterface lightbar) {
		this.lightbar = lightbar;
		lightbar.setSize(getChildCount());
	}

	public LightBarInterface getLightBar() {
		return lightbar;
	}
	
	public void updateLightbar(){
		lightbar.scrollHighLight(getScrollX());
	}
	
	@Override
	public void computeScroll() {		
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY()); //hjiang
			updateLightbar();
			updateWallpaperOffset();
			postInvalidate();
			if(mScroller.isFinished()){
				//Log.e("mScroller.isFinished()", "mScroller.isFinished()");
				startOnWorkspaceScreenListener();
			}
		} else if (mNextScreen != INVALID_SCREEN) {
			/**
			 * 屏幕循环滚动
			 * 
			 */
			if (mNextScreen == -1 && isEndlessScrolling) {
				// mCurrentScreen = getChildCount() - 1;
				scrollTo(mCurrentScreen * getWidth(), getScrollY());
			} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
				// mCurrentScreen = 0;
				scrollTo(0, getScrollY());
			} else {
				// mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
			}
			// Launcher.setScreen(mCurrentScreen);
			updateLightbar();			
			// commonLightbar.update(mCurrentScreen);
			mNextScreen = INVALID_SCREEN;
			clearChildrenCache();
//			if(mTouchState == TOUCH_STATE_REST){//滑屏结束后清缓存
//				clearChildrenCache();
//			}
			clearChildrenCacheNoGpu();
		} else if (mTouchState == TOUCH_STATE_SCROLLING) {
			final float now = System.nanoTime() / NANOTIME_DIV;
			final float e = (float) Math.exp((now - mSmoothingTime) / SMOOTHING_CONSTANT);
			final float dx = mTouchX - getScrollX();
//			mScrollX += dx * e;
			scrollBy((int) (dx * e), 0);
			mSmoothingTime = now;
			updateLightbar();
			// Keep generating points as long as we're more than 1px away from
			// the target
			if (dx > 1.f || dx < -1.f) {
				updateWallpaperOffset();
//				postInvalidate();
			}
		}
		//更新屏幕编辑指示灯
		mWorkspaceSpring.updateSpringLightbar();
	}
	
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		mTouchX = x;
		mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
	}
	
	@Override
	public void dispatchDraw(Canvas canvas) {
//		Log.e(TAG, "Workspace.dispatchDraw");
		boolean restore = false;
		int restoreCount = 0;

		// ViewGroup.dispatchDraw() supports many features we don't need:
		// clip to padding, layout animation, animation listener, disappearing
		// children, etc. The following implementation attempts to fast-track
		// the drawing dispatch by drawing only what we know needs to be drawn.
		// If we are not scrolling or flinging, draw only the current screen.
		final long drawingTime = getDrawingTime();
		final int childCount = getChildCount();

		if (!isOnSpringMode()) {
			boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
			if (fastDraw) {
				//hjiang 退出编辑模式时，绘制所有屏，释放编辑模式背景图
//				if (isOnExitSpringModeAnimation()) {
//					for (int i = 0; i < childCount; i++) {
//						callDrawChild(canvas, (CellLayout)getChildAt(i), getDrawingTime());
//					}
//				} else {
//					callDrawChild(canvas, (CellLayout)getChildAt(mCurrentScreen), getDrawingTime());
//				}
				callDrawChild(canvas, (CellLayout)getChildAt(mCurrentScreen), getDrawingTime());
				cleanWallpaper();
			} else {
				// long begin = System.currentTimeMillis();
				final float scrollPos = (float) getScrollX() / getWidth();
				int leftScreen = (int) scrollPos;
				int rightScreen = leftScreen + 1;

				/**
				 * 屏幕循环滚动
				 * 
				 */
				/*
				 * if (leftScreen >= 0) { drawChild(canvas,
				 * getChildAt(leftScreen), drawingTime); } if (scrollPos !=
				 * leftScreen && rightScreen < getChildCount()) {
				 * drawChild(canvas, getChildAt(rightScreen), drawingTime); }
				 */

				boolean isScrollToRight = false;

				isEndlessScrolling = isRollingCycle();
				if (isEndlessScrolling && childCount < 2) {
					isEndlessScrolling = false;
				}

				if (scrollPos < 0 && isEndlessScrolling) {
					leftScreen = childCount - 1;
					rightScreen = 0;
				} else if (scrollPos < 0) {
					leftScreen = -1;
					rightScreen = 0;
				} else {
					leftScreen = Math.min((int) scrollPos, childCount - 1);
					rightScreen = leftScreen + 1;
					if (isEndlessScrolling) {
						rightScreen = rightScreen % childCount;
						isScrollToRight = true;
					}
				}

				if (isWallpaperRolling()) {
					if (isScreenValid(leftScreen)) {
						if (rightScreen == 0 && !isScrollToRight) {
							updateRollingCycleWallpaper(canvas, isScrollToRight, getScrollX(), getRight(), getLeft());
						}
					}
					if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
						if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {
							updateRollingCycleWallpaper(canvas, isScrollToRight, getScrollX(), getRight(), getLeft());
						}
					}
				}

				if(EffectsType.isLeftScreenFirst()){
					drawLeftScreen(canvas, drawingTime, childCount, leftScreen, rightScreen, isScrollToRight);
					drawRightScreen(canvas, drawingTime, childCount, scrollPos, leftScreen, rightScreen, isScrollToRight);
				}else{
					drawRightScreen(canvas, drawingTime, childCount, scrollPos, leftScreen, rightScreen, isScrollToRight);
					drawLeftScreen(canvas, drawingTime, childCount, leftScreen, rightScreen, isScrollToRight);					
				}

			}
		} else {
			if (!mWorkspaceSpring.isSpringReboot()) {
				int left = Math.max(mCurrentScreen - 1, 0);
				int right = Math.min(mCurrentScreen + 1, childCount - 1);
				for (int i = left; i <= right; i++) {
					int[] loc = new int[2];
					getChildAt(i).getLocationOnScreen(loc);
					loc[0] -= mWorkspaceSpring.getAdjustXBySpringMode();
					if (i == left && loc[0] < -(mWorkspaceSpring.getSpringPageSplit() + pageWidth)) {
						continue;
					}
					if (i == right && loc[0] > mWorkspaceSpring.getSpringPageSplit() + pageWidth) {
						continue;
					}
					//Log.e("draw i :", " " + i);
//					SpringEffectsFactory.getInstance().processEffect(canvas, (CellLayout)getChildAt(i), 
//							this, drawingTime, mWorkspaceSpring.getAdjustXBySpringMode(i));
					drawSpringScreen(i, canvas, drawingTime);
				}
			} else {
				for (int i = 0; i <= childCount - 1; i++) {
					if(i >= mCurrentScreen-1 && i <= mCurrentScreen+1){
//						SpringEffectsFactory.getInstance().processEffect(canvas, (CellLayout)getChildAt(i), 
//								this, drawingTime, mWorkspaceSpring.getAdjustXBySpringMode(i));
						drawSpringScreen(i, canvas, drawingTime);
					}else{//防止出现闪屏
						callDrawChild(canvas, (CellLayout)getChildAt(i), drawingTime);
					}
				}
			}
			
			
		}

		if (restore) {
			canvas.restoreToCount(restoreCount);
		}
	}
	
	public void drawRightScreen(Canvas canvas, final long drawingTime, final int childCount, final float scrollPos, int leftScreen, int rightScreen, boolean isScrollToRight) {
		if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
			if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {
				int offset = childCount * screenWidth;
				ScreenEffects.getInstance().drawRightEffect(canvas, rightScreen, drawingTime, this, offset);
			} else {
				if (rightScreen != mCurrentScreen + 2) {// 防止弹力时绘到前一张
					ScreenEffects.getInstance().drawRightEffect(canvas, rightScreen, drawingTime, this, 0);
				}
			}
		}
	}

	public void drawLeftScreen(Canvas canvas, final long drawingTime, final int childCount, int leftScreen, int rightScreen, boolean isScrollToRight) {
		if (isScreenValid(leftScreen)) {
			if (rightScreen == 0 && !isScrollToRight) {
				int offset = childCount * screenWidth;
				ScreenEffects.getInstance().drawLeftEffect(canvas, leftScreen, drawingTime, this, -offset);
			} else {
				if (leftScreen != mCurrentScreen - 2) {// 防止弹力时绘到前一张
					ScreenEffects.getInstance().drawLeftEffect(canvas, leftScreen, drawingTime, this, 0);
				}
			}
		}
	}
	
	public boolean callDrawChild(Canvas canvas, View view, long drawingTime){
		if(!isOpenGpu(this)){//4.0以上固件若判断GPU未开启，重新查询GPU开启情况(桌面重置后，可能会有需要这种判断)
			GpuControler.isOpenGpuMore(this);
		}
		//硬件加速缓存若关闭则开启
		if(isOpenGpu(this) && !isOnSpringMode() && hasDestroyHardwareLayers(view) && view instanceof HDSwitchView){
			((HDSwitchView)view).enableHardwareLayers();
		}
		try {//非标小插件dispatchDraw方法异常捕获 caizp 2013-1-15
			return drawChild(canvas,view,drawingTime);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isScreenValid(int screen) {
		return screen >= 0 && screen < getChildCount();
	}
	
	public boolean isOnSpringMode(){
		return state == SPRING_MODE;
	}
	
	protected void setNormalState(){
		state = NORMAL_MODE;
	}
	
	protected void setSpringState(){
		state = SPRING_MODE;
	}
	
	public int getScreenWidth(){
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	public int getPageHeight(){
		return pageHeight;
	}
	
	public int getPageWidth(){
		return pageWidth;
	}
	
	public int getTopPadding(){
		return topPadding;
	}
	
	public int getBottomPadding(){
		return bottomPadding;
	}
	
	public int getWorkspaceTopPadding(){
		return CellLayoutConfig.getMarginTop();
	}
	
	public int getWorkspaceBottomPadding(){
		return CellLayoutConfig.getMarginBottom();
	}
	
	public int getWorkspaceHeight(){
		return getHeight() - getWorkspaceTopPadding() - getWorkspaceBottomPadding();
	}
	
	//快速显示目标屏
	public void scrollToScreen(int whichScreen) {
		mCurrentScreen = whichScreen;
		scrollTo(whichScreen * screenWidth, 0);
		updateLightbar();
	}

	public int getFingerOffsetY() {
		return fingerOffsetY;
	}

	public int getTouchState() {
		return mTouchState;
	}

	/**
	 * 取消长按响应
	 */
	public void cancelLongPressAction() {
		lastActionDownState = NORMAL_MODE;
	}

	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	public void setAllowLongPress(boolean allowLongPress) {
		mAllowLongPress = allowLongPress;
	}

	public Scroller getScroller() {
		return mScroller;
	}
		
	public void setDragController(BaseDragController dragController) {
		mDragController = dragController;
	}

	public BaseDragController getDragController() {
		return mDragController;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
//		computeScroll();
		mDragController.setWindowToken(getWindowToken());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(isOnEnterSpringModeAnimation()) return;
		
		topPadding = CellLayoutConfig.getMarginTop();
		bottomPadding = CellLayoutConfig.getMarginBottom();
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int hight = MeasureSpec.getSize(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
			int[] wh = ScreenUtil.getScreenWH();
			width = wh[0];
			hight = wh[1];
		}
		
		screenWidth = width;
		screenHeight = hight;
		//编辑模式下，裁去celllayout上下padding
		pageHeight = hight - topPadding - bottomPadding;
		pageWidth = width;
		
		
		//设置编辑模式相关参数
		mWorkspaceSpring.countSpringLightbarPost();
		mWorkspaceSpring.setSpringPageSplitAndGap();

		//CellLayout设定高和宽
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY));
		}

		if (mFirstLayout) {
			setHorizontalScrollBarEnabled(false);
			scrollTo(mCurrentScreen * getModeWidth(), 0);
			setHorizontalScrollBarEnabled(true);
			if(BaseSettingsPreference.getInstance().isShowNavigationView()){
				mLauncher.getWorkspaceLayer().setShowZeroView(true);//由于导航页延后加载，先设置用于壁纸定位
			}
			resetWallpaperOffset();
			mFirstLayout = false;
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childWidth = getPageWidth();
		final int childTop = getTopPadding();
		final int childHeight = getPageHeight();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			
			final CellLayout child = (CellLayout)getChildAt(i);
			
			if (child.getVisibility() == View.GONE) continue;
			child.setCellLayoutLocation(i);
			final int childLeft = i*childWidth;
			child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
			child.setCountXY();
		}
	}
	
	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentScreen || !mScroller.isFinished()) {
			if (!isLocked()) {
				// snapToScreen(screen);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentScreen() > 0) {
				snapToScreen(getCurrentScreen() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentScreen() < getChildCount() - 1) {
				snapToScreen(getCurrentScreen() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}
	
	@Override
	public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
		if (!isAllAppsVisible()) {
			if (getChildAt(mCurrentScreen) == null)
				return;

			getChildAt(mCurrentScreen).addFocusables(views, direction);
			if (direction == View.FOCUS_LEFT) {
				if (mCurrentScreen > 0) {
					getChildAt(mCurrentScreen - 1).addFocusables(views, direction);
				}
			} else if (direction == View.FOCUS_RIGHT) {
				if (mCurrentScreen < getChildCount() - 1) {
					getChildAt(mCurrentScreen + 1).addFocusables(views, direction);
				}
			}
		}
	}
	
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		final Matrix imageMatrix = t.getMatrix();
		imageMatrix.preScale(0.5f, 0.5f);
		return true;
	}
	
	/**
	 * If one of our descendant views decides that it could be focused now, only
	 * pass that along if it's on the current screen.
	 * 
	 * This happens when live folders requery, and if they're off screen, they
	 * end up calling requestFocus, which pulls it on screen.
	 */
	@Override
	public void focusableViewAvailable(View focused) {
		View current = getChildAt(mCurrentScreen);
		View v = focused;
		while (true) {
			if (v == current) {
				super.focusableViewAvailable(focused);
				return;
			}
			if (v == this) {
				return;
			}
			ViewParent parent = v.getParent();
			if (parent instanceof View) {
				v = (View) v.getParent();
			} else {
				return;
			}
		}
	}
	
	/**
	 * Description: 刷新编辑模式的当前页面
	 */
	public void refreshSpringScreen(){
		snapToScreen(mCurrentScreen);
	}
	/**
	 * Description: 延迟刷新编辑模式的当前页面，用于无法及时加载的情况
	 */
	public void delayRefreshSpringScreen(int interval){		
		postDelayed(new Runnable() {
			public void run() {
				refreshSpringScreen();
			}
		}, interval);
	}
	
	public void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0, false, false, false);
	}
	
	public void snapToScreen(int whichScreen, int velocity, boolean settle, boolean immidiately, boolean showAnim) {
		if(isLocked())
			return;

		enableChildrenCacheNoGpu();
		
		if(mDragController.isDragging()){
			cleanReorderAllState();//清除拖动状态
		}
		
		int screenCount = getChildCount();

		// whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() -
		// 1));
		whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, screenCount - (isEndlessScrolling ? 0 : 1)));

		//clearVacantCache();
		//enableChildrenCache(mCurrentScreen, whichScreen);

		mNextScreen = whichScreen;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && whichScreen != mCurrentScreen && focusedChild == getChildAt(mCurrentScreen)) {
			focusedChild.clearFocus();
		}

		final int screenDelta = Math.max(1, Math.abs(whichScreen - mCurrentScreen));
		// final int newX = whichScreen * getWidth();
		final int newX = whichScreen * getModeWidth();
		final int delta = newX - getFixedScrollXBySpringMode();

		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}

		if (settle) {
			mScrollInterpolator.setDistance(screenDelta);
		} else {
			mScrollInterpolator.disableSettle();
		}

		int duration = getSnapToScreenDuration(velocity, screenDelta, delta);
		awakenScrollBars(duration);
		if (immidiately) {
			mScroller.startScroll(getScrollX(), 0, delta, 0, 0);
		} else {
			mScroller.startScroll(getScrollX(), 0, delta, 0, (int) (duration));
		}
		if (showAnim && whichScreen >= 0 && whichScreen < screenCount) {
			View view = getChildAt(whichScreen);
			view.startAnimation(createEnterAnimation(whichScreen));
		}
		
		/**
		 * snapToScreen时就改变mCurrentScreen, 使滑屏过程中可正确取到当前屏
		 * 
		 */
		if (mNextScreen == -1 && isEndlessScrolling) {
			mCurrentScreen = getChildCount() - 1;
		} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
			mCurrentScreen = 0;
		} else {
			mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
		}
		
		/*if (mCurrentScreen == 0 || mCurrentScreen == getChildCount() - 1) {
			ReadMeFactory.maybeShowTopMenu(mLauncher);
		}*/
		//滑到新的一屏的时候，清空旧的空余单元格信息。add by lx at 2012-09-25 10:21
		clearVacantCache();
		DragHelper.getInstance().clearOtherScreenOutline(getCurrentCellLayout());
		invalidate();
	}
	
	private int getSnapToScreenDuration(int velocity, int screenDelta, int delta){
		int duration;
		if(isOnSpringMode()){
			velocity = Math.abs(velocity);
			if (velocity == 0 )
				velocity = (int) BASELINE_FLING_VELOCITY;
			duration = (screenDelta + 1) * 100;
			if (velocity > 0) {
				duration += (duration / (velocity / BASELINE_FLING_VELOCITY)) * FLING_VELOCITY_INFLUENCE;
			} else {
				duration += 100;
			}
			
		}else{
			//hjiang 固定滑屏速度
			velocity = (velocity != 0) ? mNormalSnapVelocity : (int)Math.abs((float)delta /getWidth()*mNormalSnapVelocity);
			duration = (int)((float)Math.abs(delta) / velocity * 1000);
		}
		return duration;
	}
	
	/**
	 * 获得每次移动要走的距离
	 */
	public int getModeWidth() {
		if(isOnSpringMode()){
			return mWorkspaceSpring.getSnapMoveDistance();
		}
		return screenWidth;
	}
	
	/**
	 * Description: 定位屏幕时，在编辑模式下mScrollX需减去编辑模式的动画偏移值
	 */
	public int getFixedScrollXBySpringMode(){
		if(isOnSpringMode()){
			return getScrollX() + mWorkspaceSpring.getAdjustXBySpringMode(0);
		}else{
			return getScrollX();
		}
	}
	
	/**
	 * 显示scale载入动画
	 * 
	 * @param whichScreen
	 * @return
	 */
	AnimationSet createEnterAnimation(int whichScreen) {
		whichScreen %= 9;
		float pivotX = 0.5f;
		float pivotY = 0.5f;
		final int row = whichScreen / 3;
		final int column = whichScreen % 3;
		pivotX = column * 0.5f;
		pivotY = row * 0.5f;
		return HiAnimationUtils.createScaleEnterAnamation(0.0f, 1.0f, 0.0f, 1.0f, pivotX, pivotY, 150, new AccelerateDecelerateInterpolator());
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (isLocked() || isAllAppsVisible()) {
				return false;
			}
		}
		if(isOnSpringMode()){
			ev.offsetLocation(mWorkspaceSpring.getAdjustXBySpringMode(), 0);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isLocked() || isAllAppsVisible()) {
			Log.e(TAG,"onInterceptTouchEvent isLocked return false");
			return false;
		}

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			Log.e(TAG,"onInterceptTouchEvent isLocked action move return true");
			return true;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			boolean yMoved = yDiff > touchSlop;
			
			if (xMoved && mIsChildViewCatchTouchEvent) {
				/**
				 * 当子View拦截手势时，滑屏角度小于30度才可横向滑屏
				 */
				double res = (double) yDiff / xDiff;
				double moveDegree = Math.atan(res) / 3.14 * 180;
				xMoved = moveDegree < 30;
			}
			
			if (xMoved || yMoved) {
				if (xMoved && (!mIsChildViewCatchTouchEvent || (mIsChildViewCatchTouchEvent && mIsLastActionDownWhenChildCatchTouchEvent))) {
					// Scroll if the user moved far enough along the X axis
					mTouchState = TOUCH_STATE_SCROLLING;
					mLastMotionX = x;
					mTouchX = getScrollX();
					mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
					
					enableChildrenCacheNoGpu();
				} else if (yMoved && isMultiTouch) {
					mTouchState = TOUCH_STATE_SCROLLING;
				} else {
					if (!mIsChildViewCatchTouchEvent) {
						if ((y - mLastMotionY) > 0) {
							if (Math.abs(y - mLastMotionY) > (touchSlop * 2) && !isMultiTouch)
								mTouchState = TOUCH_SWIPE_DOWN_GESTURE;
						} else {
							if (Math.abs(y - mLastMotionY) > (touchSlop * 2) && !isMultiTouch)
								mTouchState = TOUCH_SWIPE_UP_GESTURE;
						}
					} else {
						mIsLastActionDownWhenChildCatchTouchEvent = false;
					}
				}
				// Either way, cancel any pending longpress
				if (mAllowLongPress) {
					mAllowLongPress = false;
					final View currentScreen = getChildAt(mCurrentScreen);
					currentScreen.cancelLongPress();
				}
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mActivePointerId = ev.getPointerId(0);
			mAllowLongPress = true;
			
			mIsChildViewCatchTouchEvent = false;
			mIsLastActionDownWhenChildCatchTouchEvent = true;

			lastActionDownState = state;
			/*
			 * If being flinged and user touches the screen, initiate drag;
			 * otherwise don't. mScroller.isFinished should be false when being
			 * flinged.
			 */
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			setEffect();
			mWorkspaceSpring.fixExitSpringModeAnimationState();
			//gpu开启时，编辑模式下进行操作时需移除离屏缓存
//			destoryAllChildsHardwareLayer();
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN:
			oldMultiTouchDist = multiTouchSpacing(ev);
			if (oldMultiTouchDist > MULTI_DIST) {
				isMultiTouch = true;
			}
			shouldActionPinch = true;
			shouldActionSpread = true;
			
			/* 双指手势判断 初始化 起点 */
			mGestureController.dispatchActionDown( ev );
			
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			isMultiTouch = false;
			int touchState = mTouchState;
			if (mTouchState != TOUCH_STATE_SCROLLING) {
				final CellLayout currentScreen = (CellLayout) getChildAt(mCurrentScreen);
				if (!currentScreen.lastDownOnOccupiedCell()) {
					getLocationOnScreen(mTouchUpLocation);
					// Send a tap to the wallpaper if the last down was on empty
					// space
					final int pointerIndex = ev.findPointerIndex(mActivePointerId);
//					getWallpaperManager().sendWallpaperCommand(getWindowToken(), "android.wallpaper.tap", mTempCell[0] + (int) ev.getX(pointerIndex), mTempCell[1] + (int) ev.getY(pointerIndex), 0, null);
					sendWallpaperCommand(getWindowToken(), "android.wallpaper.tap", 
							mTouchUpLocation[0] + (int) ev.getX(pointerIndex), mTouchUpLocation[1] + (int) ev.getY(pointerIndex), 0, null);
					
					onClickNoOccupiedCell(mCurrentScreen);
				}
			}

			// Release the drag
			//clearChildrenCache();
			clearChildrenCacheNoGpu();
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			mAllowLongPress = false;

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			if (touchState != TOUCH_STATE_SCROLLING
				&& action == MotionEvent.ACTION_UP) {
				if (onClickCell(mCurrentScreen)) {
					Log.e(TAG,"onInterceptTouchEvent onClickCell return true");
					return true;
				}
			}
			
			//编辑模式做特殊处理
			if(lastActionDownState == SPRING_MODE && action == MotionEvent.ACTION_UP){
				mWorkspaceSpring.handleOnSpringActionUp(ev);
				Log.e(TAG,"onInterceptTouchEvent SPRING_MODE return true");
				return true;
			}
			break;

		case MotionEvent.ACTION_POINTER_UP:
			isMultiTouch = false;
			onSecondaryPointerUp(ev);
			shouldActionPinch = false;
			shouldActionSpread = false;
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		Log.e(TAG,"onInterceptTouchEvent return "+(mTouchState != TOUCH_STATE_REST)+mTouchState);
		return mTouchState != TOUCH_STATE_REST;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isLocked()) {
			Log.e(TAG,"onTouchEvent isLocked return false");
			return false; // We don't want the events. Let them fall through to
			// the all apps view.
		}
		if (isAllAppsVisible()) {
			// Cancel any scrolling that is in progress.
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			snapToScreen(mCurrentScreen);
			Log.e(TAG,"onTouchEvent isAllAppsVisible return false");
			return false; // We don't want the events. Let them fall through to
			// the all apps view.
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		Log.e(TAG,"onTouchEvent  return true:action:"+action);
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
				scrollTo(mScroller.getFinalX(), 0);
			}

			// Remember where the motion event started
			mLastMotionX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldMultiTouchDist = multiTouchSpacing(ev);
			if (oldMultiTouchDist > MULTI_DIST) {
				isMultiTouch = true;
			}
			shouldActionPinch = true;
			shouldActionSpread = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				
				if (!isMultiTouch) {
					// Scroll to follow the motion event
					final int pointerIndex = ev.findPointerIndex(mActivePointerId);
					final float x = ev.getX(pointerIndex);
					final float deltaX = mLastMotionX - x;
					mLastMotionX = x;

					final float y = ev.getY(pointerIndex);
					// 设置球特效y轴的移动距离

					fingerOffsetY = (int) (mLastMotionY - y);
					if (deltaX < 0) {
						if (mTouchX > (isEndlessScrolling ? -screenWidth : -screenWidth / 2)) {
							if (mTouchX > 0)
								mTouchX += Math.max(-mTouchX, deltaX);
							else
								mTouchX += deltaX;
							mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
							invalidate();
						}
					} else if (deltaX > 0) {
						final float availableToScroll = getChildAt(getChildCount() - 1).getRight() - mTouchX - getModeWidth();
						if (availableToScroll > (isEndlessScrolling ? -screenWidth : -screenWidth / 2)) {
							if (availableToScroll > 0)
								mTouchX += Math.min(availableToScroll, deltaX);
							else
								mTouchX += deltaX;
							mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
							invalidate();
						}
					} else {
						awakenScrollBars();
					}
				} else {
					float newDist = multiTouchSpacing( ev );
					if (newDist > MULTI_DIST && !isOnSpringMode()) {
						/* 新的双指距离 大于 旧的双指距离的 65%（35%为偏移修正范围）*/
						if ( newDist > oldMultiTouchDist * 0.65f ) {
							/* 双指上下滑动的手势被判断后，则屏幕预览的操作 被拦截 */
							if ( getVisibility() == VISIBLE ) { // Workspace被隐藏，表示 当前的Preview模式被启动
								final VelocityTracker tracker = mVelocityTracker;
								shouldActionPinch = !mGestureController.actionMultiDownOrMultiUp(tracker, ev, mMaximumVelocity);	
							}
						} else if (mCurrentScreen * getWidth() == getScrollX() && shouldActionPinch) {
							if(onPinch()){									
								shouldActionPinch = false;
							}
						}
						
						if( newDist > oldMultiTouchDist * 1.4f && mCurrentScreen * getWidth() == getScrollX() && shouldActionSpread){
							if(onSpread()){
								shouldActionSpread = false;
							}
						}
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			/* 双指手势判断 结束 重置 */
			mGestureController.dispatchActionUp( ev );
			
			isMultiTouch = false;
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				final int velocityX = (int) velocityTracker.getXVelocity();
				// final int velocityX = (int)
				// velocityTracker.getXVelocity(mActivePointerId);
				final int screenWidth = getModeWidth();
//				final int whichScreen = (int) Math.floor((getScrollX() + (screenWidth / 2)) / (float) screenWidth);
				final int whichScreen = (int) Math.floor((getFixedScrollXBySpringMode() + (screenWidth / 2)) / (float) screenWidth);
				final float scrolledPos = (float) getFixedScrollXBySpringMode() / screenWidth;

				if (velocityX > SNAP_VELOCITY && mCurrentScreen > (isEndlessScrolling ? -1 : 0)) {
					// Fling hard enough to move left.
					// Don't fling across more than one screen at a time.
					final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
					// hjiang 解决加载时无法滑屏的问题
					if (bound == whichScreen)
						snapToScreen(whichScreen-1, velocityX, false, false, false);
					else
						snapToScreen(Math.min(whichScreen, bound), velocityX, false, false, false);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - (isEndlessScrolling ? 0 : 1)) {
					// Fling hard enough to move right
					// Don't fling across more than one screen at a time.
					final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
					// hjiang 解决加载时无法滑屏的问题
					if (bound == whichScreen)
						snapToScreen(whichScreen+1, velocityX, false, false, false);
					else
						snapToScreen(Math.max(whichScreen, bound), velocityX, false, false, false);
				} else {
					snapToScreen(whichScreen, 0, false, false, false);
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			} else {
				if (mTouchState == TOUCH_SWIPE_DOWN_GESTURE && !isOnSpringMode()) {//编辑模式下屏蔽
					onActionDown((int)mLastMotionY);
				} else if (mTouchState == TOUCH_SWIPE_UP_GESTURE && !isOnSpringMode()) {
					onActionUp();
				}
			}
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			isMultiTouch = false;
			onSecondaryPointerUp(ev);
			shouldActionPinch = false;
			shouldActionSpread = false;
			break;
		}

		return true;
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentScreen = mCurrentScreen;
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		if (savedState.currentScreen != -1) {
			mCurrentScreen = savedState.currentScreen;
		}
	}
	
	public void scrollLeft() {
		clearVacantCache();
		if (mScroller.isFinished()) {
			if (mCurrentScreen > 0)
				snapToScreen(mCurrentScreen - 1);
		} else {
			if (mNextScreen > 0)
				snapToScreen(mNextScreen - 1);
		}
	}
	
	public void scrollRight() {
		clearVacantCache();
		if (mScroller.isFinished()) {
			if (mCurrentScreen < getChildCount() - 1)
				snapToScreen(mCurrentScreen + 1);
		} else {
			if (mNextScreen < getChildCount() - 1)
				snapToScreen(mNextScreen + 1);
		}
	}

	public void moveToDefaultScreen(boolean animate) {
		if(isDefaultScreenShowing())
			return;
		if (animate) {
			snapToScreen(mDefaultScreen);
		} else {
			setCurrentScreen(mDefaultScreen);
		}
		if(getChildAt(mDefaultScreen) != null){
			getChildAt(mDefaultScreen).requestFocus();
		}
	}
	
	public static class SavedState extends BaseSavedState {
		int currentScreen = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentScreen = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(currentScreen);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
	
	protected void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = ev.getX(newPointerIndex);
			mLastMotionY = ev.getY(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}
	
	/**
	 * 计算双指触屏间距
	 * @param event
	 * @return
	 */
	protected float multiTouchSpacing(MotionEvent event) {
		if(event.getPointerCount() < 2){
			return 0;
		}
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	//清除全部离屏缓存
	public void destoryAllChildsHardwareLayer(){
		for(int i = 0; i < this.getChildCount(); i ++){
			destoryChildHardwareLayer(i);
		}
	}
	//清除当前屏离屏缓存
	public void destoryCurrentChildHardwareLayer(){
		destoryChildHardwareLayer(mCurrentScreen);
	}
	
	public void destoryChildHardwareLayer(int i){
		if (Build.VERSION.SDK_INT < 16) {//适配android4.0下可能出现异常
			return;
		}
		if(isOpenGpu(this) && isOnSpringMode()){
			((HDSwitchView)getChildAt(i)).destroyHardwareLayer();
		}
	}
	
	//设置当前屏离屏缓存
	public void enableCurrentChildHardwareLayer(){
		if(isOpenGpu(this) && isOnSpringMode()){
			((HDSwitchView)getChildAt(mCurrentScreen)).enableHardwareLayers();
		}
	}
	
	public void setSpringLightbar(CommonLightbar springLightbar) {
		mWorkspaceSpring.setSpringLightbar(springLightbar);
	}
	
	/**
	 * Description: 在编辑模式下重新进入编辑模式动画
	 */
	public void animationSpringModeReboot(){
		mWorkspaceSpring.animationSpringModeReboot();
	}
	
	/**
	 * Description: 获取编辑模式下,页面动画沿Y轴方向的距离
	 */
	public int getSpringPageTranslationY(){
		return mWorkspaceSpring.getSpringPageTranslationY();
	}
	
	/**
	 * Description: 获取编辑模式缩放后CellLayout的间距
	 */
	public int getSpringPageGap(){
		return mWorkspaceSpring.getSpringPageGap();
	}
	
	public boolean isSpringReboot(){
		return mWorkspaceSpring.isSpringReboot();
	}
	
	public boolean hasSpringAddScreen(){
		return mWorkspaceSpring.isHasSpringAddScreen();
	}
	
	/**
	 * 获取编辑模式的CellLayout缩放比例
	 * @return
	 */
	public float getSpringScale(){
		return mWorkspaceSpring.getSpringScale();
	}

	/**
	 * 获取编辑模式缩放后左右CellLayout露出的距离
	 */
	public int getSpringPageSplit(){
		return mWorkspaceSpring.getSpringPageSplit();
	}
	
	public int getAdjustXBySpringMode(){
		return mWorkspaceSpring.getAdjustXBySpringMode();
	}
	
	/**
	 * 是否处于打开或退出编辑模式的动画中
	 * @return
	 */
	public boolean isOnSpringAnimation(){
		return mWorkspaceSpring.isOnEnterSpringModeAnimation() || mWorkspaceSpring.isOnExitSpringModeAnimation();
	}
	
	/**
	 * 是否处于打开编辑模式的动画中
	 * @return
	 */
	public boolean isOnEnterSpringModeAnimation(){
		return mWorkspaceSpring.isOnEnterSpringModeAnimation();
	}
	
	/**
	 * 编辑模式下，当前屏的动画前与动画后的位置偏移量
	 * @return
	 */
	public boolean isWallpaperRolling(){
		return ConfigFactory.isWallpaperRolling(getContext());
	}
	
	public void setWallpaperHelper(WallpaperHelper mWallpaperHelper) {
		this.mWallpaperHelper = mWallpaperHelper;
	}
	
	public void resetWallpaperOffset(){
		if(ConfigFactory.isWallpaperRolling(getContext())){
			updateWallpaperOffset();
		}else{
			updateWallpaperToCenter();
		}
	}
	
	/**
	 * 将壁纸居中显示
	 */
	public void updateWallpaperToCenter() {
		mWallpaperHelper.updateWallpaperToCenter();
	}
	
	/**
	 * 处理退出编辑模式时的壁纸更新
	 */
	public void updateWallpaperForSpring() {
		mWallpaperHelper.updateWallpaperForSpring(getContext(), getScrollX(), getRight(), getLeft());
	}
	
	public void updateWallpaperOffset() {
		mWallpaperHelper.updateWallpaperOffset(getContext(), getScrollX(), true);
	}
	
	public void cleanWallpaper(){
		mWallpaperHelper.cleanWallpaper();
	}
	
	public void updateRollingCycleWallpaper(Canvas canvas, boolean isScrollToRight, int mScrollX, int mRight, int mLeft) {
		mWallpaperHelper.updateRollingCycleWallpaper(canvas, isScrollToRight, mScrollX, mRight, mLeft);
	}
	
	public void sendWallpaperCommand(IBinder windowToken, String action,
            int x, int y, int z, Bundle extras) {
		mWallpaperHelper.getWallpaperManager().sendWallpaperCommand(windowToken, action, x, y, z, extras);
	}
	
	
	/**
	 * Description: 如传入参数小于0， 则不改变原参数值
	 */
	public void changeCellLayoutMarginTop(int marginTop){
		CellLayoutConfig.resetMarginTop(marginTop);
		reLayoutAllCellLayout();
	}
	
	public void changeCellLayoutMarginBottom(int marginBottom){
		CellLayoutConfig.resetMarginBottom(marginBottom);
		reLayoutAllCellLayout();
	}
	
	public void reLayoutAllCellLayout(){
		int screenCount = getChildCount();
		for (int i = 0; i < screenCount; i++) {
            CellLayout cl = (CellLayout)getChildAt(i);
            int count = cl.getChildCount();
    		for(int j = 0; j < count; j ++){
    			View v = cl.getChildAt(j);
    			v.setVisibility(View.VISIBLE);
    			CellLayout.LayoutParams lp = (CellLayout.LayoutParams)v.getLayoutParams();
    			lp.setup(lp.cellX, lp.cellY, lp.spanX, lp.spanY, CellLayoutConfig.getCellWidth(), CellLayoutConfig.getCellHeight());
    			 
    		}
    		
            cl.requestLayout();
		}
	}
	
	public CellLayout.CellInfo findAllVacantCells(int spanX, int spanY) {
		return findAllVacantCells(null, spanX, spanY);
	}
	
	public CellLayout.CellInfo findAllVacantCells(boolean[] occupied, int spanX, int spanY) {
		CellLayout group = (CellLayout) getChildAt(mCurrentScreen);
		if (group != null) {
			group.initOccupied(null, false);
			return group.findAllVacantCells(occupied, spanX, spanY);
		}
		return null;
	}
	
	public CellLayout getCurrentCellLayout(){
		if((CellLayout) getChildAt(mCurrentScreen) == null)
			return (CellLayout) getChildAt(0);
		return (CellLayout) getChildAt(mCurrentScreen);
	}
	
	public CellLayout getCellLayoutAt(int index) {
		View view = super.getChildAt(index);
		if (view == null)
			return null;

		return (CellLayout) view;
	}
	
	/**
	 * 初始化光亮背景
	 */
	public void initDragOutline(DragView mDragView, Paint mTrashPaint, Paint mOriginPaint){
		getCurrentCellLayout().initDragOutline(mDragView, mTrashPaint, mOriginPaint);
	}
	
	/**
	 * 清除光亮背景
	 */
	public void cleanDragOutline(){
		getCurrentCellLayout().cleanDragOutline();
	}
	
	/**
	 * 改变光亮背景绘制区域
	 * @param availableCell
	 * @return
	 */
	public boolean changeDragOutline(Rect availableCell){
		return getCurrentCellLayout().changeDragOutline(availableCell);
	}
	
	/**
	 * 是否循环滚屏
	 * @return
	 */
	public boolean isRollingCycle(){
		return BaseSettingsPreference.getInstance().isRollingCycle();
	}
	
	/**
	 * 是否开启GPU
	 * @param v
	 * @return
	 */
	public boolean isOpenGpu(View v){
		return GpuControler.isOpenGpu(v);
	}
	
	/**
	 * 是否关闭硬件加速缓存
	 * @param v
	 * @return
	 */
	public boolean hasDestroyHardwareLayers(View v){
		return GpuControler.hasDestroyHardwareLayers(v);
	}
	
	public CellLayout.CellInfo getDragInfo() {
		return mDragInfo;
	}
	
	public boolean isHansntMoved(CellLayout dragTargetLayout, int[] targetCell) {
		if (mDragInfo == null || targetCell == null || dragTargetLayout == null)
			return false;

		return (mDragInfo.cellX == targetCell[0] && mDragInfo.cellY == targetCell[1]) && (getCellLayoutAt(mDragInfo.screen) == dragTargetLayout);
	}

	public void removeChildLayoutView(int screen, View theView) {
		if (screen < 0 || screen >= getChildCount())
			return;

		getCellLayoutAt(screen).removeView(theView);
		getCellLayoutAt(screen).destroyHardwareLayer();
	}
	
	/**
	 * Return the current {@link CellLayout}, correctly picking the destination
	 * screen while a scroll is in progress.
	 */
	public CellLayout getCurrentDropLayout() {
		int index = getCurrentDropLayoutIndex();
		return (CellLayout) getChildAt(index);
	}
	
	public int getCurrentDropLayoutIndex(){
		return mScroller.isFinished() ? mCurrentScreen : mNextScreen;
	}
	
	//是否为应用列表图标
	public boolean isAllAppsIndependence(View view){
		if(!(view instanceof IconMaskTextView))
			return false;
		
		Object tag = view.getTag();
		if (tag == null || !(tag instanceof ApplicationInfo))
			return false;
		return isAllAppsIndependence((ApplicationInfo)tag);
	}
	
	public boolean isOnSpringAddScreen(){
		return hasSpringAddScreen() && mCurrentScreen == getChildCount()-1;
	}
	
	public void addApplicationShortcut(ApplicationInfo info, int cellX, int cellY) {
		info.container = BaseLauncherSettings.Favorites.CONTAINER_DESKTOP;
		info.screen = mCurrentScreen;
		info.cellX = cellX;
		info.cellY = cellY;
		int[] wh = CellLayoutConfig.spanXYMather(1, 1, info);
		info.spanX = wh[0];
		info.spanY = wh[1];
		
		BaseLauncherModel.addItemToDatabase(mLauncher, info, false);
		View view = mLauncher.createCommonAppView((ApplicationInfo) info);
		addInScreen(view, mCurrentScreen, cellX, cellY, info.spanX, info.spanY);
	}
	
	public CellLayout getParentCellLayoutForView(View v) {
		final int N = getChildCount();
		for (int i = 0; i < N; i++) {
			if (getCellLayoutAt(i).indexOfChild(v) > -1) {
				return getCellLayoutAt(i);
			}
		}
		return null;
	}
	
	public void initWorkspaceDragAndDropIfNotExsit(CellLayout.CellInfo mDragInfo){
		if(mWorkspaceDragAndDrop != null)
			return;
		initWorkspaceDragAndDrop(mDragInfo);
	}
	
	/**
	 * 是否拖动app到另一个app上方
	 * @return
	 */
	public boolean isDragOnApplication() {
		return mWorkspaceDragAndDrop == null ? false : mWorkspaceDragAndDrop.isDragOnApplication();
	}
	
	public void cancelReorderAlarm(){
		if(mWorkspaceDragAndDrop != null){			
			mWorkspaceDragAndDrop.cancelReorderAlarm();
		}
	}
		
	public void setAllowRevertReorder(boolean allow){
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.setAllowRevertReorder(allow);
	}
	
	/**
	 * 处理从桌面将拖动的item到屏幕预览上
	 */
	public boolean dropItemToScreenFromPreview(int index, int[] targetCell) {
		initWorkspaceDragAndDropIfNotExsit(null);
		return mWorkspaceDragAndDrop.dropItemToScreenFromPreview(index, targetCell);
	}
	
	/**
	 * Description: 处理拖动dockbar上图标到屏幕预览上
	 */
	public void dropDockbarItemToScreenFromPreview(CellLayout cellLayout, Object dragInfo, int[] targetCell) {
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.dropDockbarItemToScreenFromPreview(cellLayout, dragInfo, targetCell);
	}
	
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.onDragEnter(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.onDragOver(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}
	
	/**
	 * Description: celllayout中的View移动位置后，是否有空闲区接收目标部件
	 */
	public boolean acceptDropForReorder(Object dragInfo){
		initWorkspaceDragAndDropIfNotExsit(null);
		return mWorkspaceDragAndDrop.acceptDropForReorder(dragInfo);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		initWorkspaceDragAndDropIfNotExsit(null);
		return mWorkspaceDragAndDrop.acceptDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	public Rect getTargetRect() {
		initWorkspaceDragAndDropIfNotExsit(null);
		return mWorkspaceDragAndDrop.getTargetRect();
	}
	
	//是否在进行合并文件的动画
	public boolean isOnMergeFolerAnimation(){
		return mWorkspaceDragAndDrop == null ? false : mWorkspaceDragAndDrop.isOnMergeFolerAnimation();
	}
		
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.onDragExit(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}
	
	/**
	 * 是否允许合成文件夹动画
	 * @param allowMegerFolder
	 */
	public void setAllowAnimateMerFolder(boolean allowMegerFolder) {
		if(mWorkspaceDragAndDrop != null){				
			mWorkspaceDragAndDrop.setAllowAnimateMergeFolder(allowMegerFolder);
		}
	}
	
	/**
	 * 回到桌面时，还原可能存在的文件夹动画
	 */
	public void restoreFolderAnimation(){
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.restoreFolderAnimation();
	}
	
	
	/**
	 * Description: 是否将应用成功drop到workpace的目标位置
	 */
	public boolean isSuccessOnDropWorkspace(){
		return mWorkspaceDragAndDrop == null ? false : mWorkspaceDragAndDrop.getTargetCell() != null;
	}
	
	
	/**
	 * 是否被锁定无法滑动
	 * @return
	 */
	public boolean isLocked(){
		return mLauncher.isWorkspaceLocked();
	}
	
	/**
	 * 是否匣子可见
	 * @return
	 */
	public boolean isAllAppsVisible(){
		return mLauncher.isAllAppsVisible();
	}
	
	public void showNavigation(){
		mLauncher.getWorkspaceLayer().showZeroView();
	}

	public void hideNavigation(){
		mLauncher.getWorkspaceLayer().hideZeroView();
	}
	
	public void showLightbarAndDockbar(){
		mLauncher.showBottomContainer();
	}

	public void hideLightbarAndDockbar(){
		mLauncher.hideBottomContainer();
	}
	
	public void setLauncher(BaseLauncher launcher) {
		mLauncher = launcher;
		mWorkspaceSpring.setLauncher(mLauncher);
	}
	
	public BaseLauncher getLauncher() {
		return mLauncher;	
	}
	
	public void destroyCurrentCellLayoutDrawingCache(){
		final CellLayout layout = getCurrentCellLayout();
		if(layout != null){
			layout.setChildrenDrawnWithCacheEnabled(false);		
			layout.setChildrenDrawingCacheEnabled(false);	
		}
	}
	
	public void startDrag(CellLayout.CellInfo cellInfo) {
		View child = cellInfo.cell;

		if (!child.isInTouchMode()) {
			return;
		}
		//删除View的消息提示 
//		if(LauncherNotificationHelper.deleteLauncherNotificationViewIfNeed(child)){
//			return;
//		}
		cleanDragInfoOnDrop = true;

		mDragInfo = cellInfo;
		mDragInfo.screen = mCurrentScreen;

		CellLayout current = ((CellLayout) getChildAt(mCurrentScreen));
		current.setItemPlacementDirty(false);
		current.setOnReorderAnimation(false);

		current.onDragChild(child);
		mDragController.startDrag(child, this);
		
		
		onDragStartEx(child);
		
		child = null;
		
		initWorkspaceDragAndDrop(mDragInfo);
	}
	
	/**
	 * Description: 从程序匣子进入编辑模式
	 */
	public void changeToSpringModeFromDrawer(int fromTab){
		if (isOnSpringMode())
			return;
		
		isEndlessScrolling = false;
		setSpringState();
		mWorkspaceSpring.changeToSpringModeFromDrawer(fromTab);
	}
	/**
	 * Description: 返回程序匣子时，退出编辑模式
	 */
	public void changeToNormalModeFromDrawer(boolean removeSpringAddScreen, boolean isShowDrawer) {
		if (!isOnSpringMode())
			return;

		isEndlessScrolling = BaseSettingsPreference.getInstance().isRollingCycle();
		setNormalState();
		
		mWorkspaceSpring.changeToNormalModeFromDrawer(removeSpringAddScreen, isShowDrawer);
	}
	
	public void changeToNormalMode(){
		changeToNormalMode(true);
	}
	
	/**
	 * @param removeSpringAddScreen 是否移除最后一个“添加”屏
	 * @return
	 */
	public void changeToNormalMode(boolean removeSpringAddScreen) {
		if (!isOnSpringMode())
			return;
		isEndlessScrolling = BaseSettingsPreference.getInstance().isRollingCycle();
		setNormalState();
		mWorkspaceSpring.animationNormalMode(removeSpringAddScreen);
	}
	
	/**
	 * @param createSpringAddScreen 是否增加一个“添加”屏
	 * @param tab - 默认显示数据集
	 * @return
	 */
	public void changeToSpringMode(boolean createSpringAddScreen, String tab) {
		if (mLauncher == null || isOnSpringMode())
			return;
		
		CellLayout cl = getCurrentCellLayout();
		if (cl == null)
			return;
		
		isEndlessScrolling = false;
		setSpringState();
		
		mWorkspaceSpring.changeToSpringMode(createSpringAddScreen, tab);
	}

	public void reLayoutSpringMode(int margin){
		initSpringResource(false, margin);
	}
	
	public int getState() {
		return dropState;
	}

	public void setState(int state) {
		this.dropState = state;
	}
	
	/**
	 * child 是否是widget
	 */
	private boolean isWidget(View child) {
		if (child == null)
			return false;
		ItemInfo item = (ItemInfo) child.getTag();
		if (item instanceof WidgetInfo) {
			return true;
		}
		return false;
	}
	
	/**
	 * 清理挤动状态
	 */
	public void cleanReorderAllState() {
		if(isOnScene())
			return;
		
		postDelayed(new Runnable() {
			public void run() {
				cleanAndRevertReorder();
			}
		}, CellLayoutReorder.REORDER_ANIMATION_TRANS_DURATION);
//		if (getCurrentCellLayout() != null) {
//			getCurrentCellLayout().revertTempState();
//			cleanupReorder();
//		}
	}
	
	public void cleanAndRevertReorder() {
		if(isOnScene())
			return;
		
		int count = getChildCount();
		for(int i = 0; i < count; i ++){
			if(getCellLayoutAt(i) != null){
				getCellLayoutAt(i).revertTempState();
			}
		}
		cleanupReorder();
	}
	
	public void cleanupReorder(){
		if(isOnScene())
			return;
		
		if(getCurrentCellLayout() != null){			
			getCurrentCellLayout().cleanReorderAnimations();
		}
		cancelReorderAlarm();
    }
	
	public boolean isOnScene(){
		return BaseConfig.isOnScene();
	}
	
	/**
	 * 添加从文件夹拖拽出来的app<br>
	 * @param folder 拖拽的文件夹数据结构
	 * @param item 拖拽的app
	 * @return 是否拖拽成功
	 */
	public boolean onDropFolderExternal(int screen, FolderInfo folder, Object item) {
		initWorkspaceDragAndDropIfNotExsit(null);
		return mWorkspaceDragAndDrop.onDropFolderExternal(screen, item);
	}
	
	/**
	 * Description: 处理拖动item到匣子底部的预览上
	 */
	public void dropToScreenFromDrawerPreview(int screen, Object mDragInfo, ArrayList<ApplicationInfo> appList){
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.dropToScreenFromDrawerPreview(screen, mDragInfo, appList);
	}
	
	/**
	 * <br>
	 * Description:交换桌面视图 <br>
	 *
	 * @param screenFrom
	 * @param screenTo
	 */
	public void exchangeScreen(int screenFrom, int screenTo) {
		// 交换桌面屏视图
		CellLayout layout = (CellLayout) getChildAt(screenFrom);
		LayoutParams lp = layout.getLayoutParams();
		detachViewFromParent(layout);
		attachViewToParent(layout, screenTo, lp);
		requestLayout();
	}
	
	/**
	 * 是否处于屏幕预览模式
	 * @return
	 */
	public boolean isOnPreviewMode(){
		return getLauncher().getPreviewEditController().isPreviewMode();
	}
	
	public void initSpringResource(boolean isFromDrawer, int heightFix){
		cellLayoutHelper.initCellLayoutHelper();
		
		int workspaceWidth = getScreenWidth();
		int pageHeight = getPageHeight() - heightFix; 
		BaseCellLayoutHelper.springScale = getSpringScale();
		BaseCellLayoutHelper.springPageTranslationY = getSpringPageTranslationY();
		BaseCellLayoutHelper.mCellLayoutWidthEx = workspaceWidth;
		BaseCellLayoutHelper.mCellLayoutHeightEx = pageHeight;
		BaseCellLayoutHelper.springTopMargin = getTopPadding();
		BaseCellLayoutHelper.springMiddleX = workspaceWidth/2;
		BaseCellLayoutHelper.springMiddleY = getTopPadding() + pageHeight/2 - getSpringPageTranslationY();
		
		BaseCellLayoutHelper.cellWidth = getCurrentCellLayout().getCellWidth();
		BaseCellLayoutHelper.cellHeight = getCurrentCellLayout().getCellHeight();
		BaseCellLayoutHelper.cellGapY = getCurrentCellLayout().getCellGapY();
		BaseCellLayoutHelper.cellGapX = getCurrentCellLayout().getCellGapX();
		BaseCellLayoutHelper.leftDiffNormalAndSpring = (int) (workspaceWidth * (1 - getSpringScale()) / 2);
		float scaleCenterY = isFromDrawer ? WorkspaceSpring.getSpringScaleForDrawerCenterY() : WorkspaceSpring.getSpringScaleCenterY();
		BaseCellLayoutHelper.topDiffNormalAndSpring = (int) (pageHeight * (1 - getSpringScale()) * scaleCenterY);
	}
	
	public void cleanSpringResource(){
		postDelayed(new Runnable() {
			public void run() {
				cellLayoutHelper.cleanCellLayoutHelper();
			}
		}, 500);
	}
	
	public void drawSpringBackground(int alpha, Canvas canvas){
		cellLayoutHelper.drawSpringBackground(alpha, canvas);
	}
	
	public void drawSpringNoVacantBackground(int alpha, Canvas canvas){
		cellLayoutHelper.drawSpringNoVacantBackground(255, canvas);
	}
	
	public void drawSpringAddBtn(int alpha, Canvas canvas){
		cellLayoutHelper.drawSpringAddBtn(alpha, canvas);
	}
	
	public Rect drawSpringDelBtn(int alpha, Canvas canvas){
		return cellLayoutHelper.drawSpringDelBtn(alpha, canvas);
	}
	
	public int getTopDiffNormalAndSpring(){
		return BaseCellLayoutHelper.topDiffNormalAndSpring;
	}
	
	public void drawSpringScreen(int i, Canvas canvas, long drawingTime){
		SpringEffectsFactory.getInstance().processEffect(canvas, (CellLayout)getChildAt(i),
				this, drawingTime, mWorkspaceSpring.getAdjustXBySpringMode(i));
	}
	
	/**
	 * 设置编辑模式间距
	 * @param split 编辑模式页缩进比例
	 * @param gap 编辑模式页间距比例
	 */
	public void setSpringGapAndSplit(float split, float gap){
		mWorkspaceSpring.setSpringPageSplitFactor(split);
		mWorkspaceSpring.setSpringGapFactor(gap);
	}
	
	/**
	 * 根据拖拽位置，寻找目标放置位置
	 * @param pixelX
	 * @param pixelY
	 * @param spanX
	 * @param spanY
	 * @param layout
	 * @param recycle
	 * @return
	 */
	public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, CellLayout layout, int[] recycle) {
		if(layout == null){
			return new int[]{-1, -1};
		}
		int[] loc = {pixelX, pixelY};
		if(isOnSpringMode()){//编辑模式下调整坐标
			BaseCellLayoutHelper.springToNormalCoordinateEx(loc);
		}
		return layout.findNearestArea(loc[0], loc[1], spanX, spanY, null, false, recycle);
	}
	
	/**
	 * 拖放到Workspace
	 * @param source
	 * @param x
	 * @param y
	 * @param xOffset
	 * @param yOffset
	 * @param dragView
	 * @param dragInfo
	 */
	@Override
	public void onDrop(final DragSource source, final int x, final int y, final int xOffset, final int yOffset, 
			final DragView dragView, final Object dragInfo) {
		if (mDragController.isDragFromFolder(source) && mLauncher.isFolderOpened())
			return;
		initWorkspaceDragAndDropIfNotExsit(null);
		
		if(!isOnSpringMode()){
			mWorkspaceDragAndDrop.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
		}else{
			if (source instanceof ScreenViewGroup) {
				if (getCellLayoutAt(mCurrentScreen).isSpringAddScreen()) {// 如果拖动到“添加”屏
					cleanDragInfoOnDrop = false;
					animationSpringModeReboot();
					postDelayed(new Runnable() {
						public void run() {
							mWorkspaceDragAndDrop.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
							cleanDragInfoOnDrop = true;
							cleanDragInfo();
						}
					}, 500);
				}else{
					mWorkspaceDragAndDrop.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
				}
			} else{
				mWorkspaceDragAndDrop.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
				if (mDragController.isDragFromDrawer(source) || mDragController.isDragFromFolder(source)) {// 从程序匣子拖出到桌面时，drop后恢复到正常模式
					isDropViewFromDrawer = true;
					if (getCellLayoutAt(mCurrentScreen).isSpringAddScreen()) {// 如果拖动到“添加”屏
						changeToNormalModeFromDrawer(false, false);
					} else {
						changeToNormalModeFromDrawer(true, false);
					}
				}
			}
			
		}
		
	}
	
	/**
	 * 从workspace拖出后放手
	 * @param target
	 * @param success
	 */
	@Override
	public void onDropCompleted(View target, boolean success) {
		clearVacantCache();
		if(mDragInfo != null){
			final CellLayout cellLayout = (CellLayout) getChildAt(mDragInfo.screen);
			cellLayout.resetDragging(mDragInfo.cell);
			if (success) {
				if (target != this && (mLauncher.isDeleteZone || target instanceof BaseMagicDockbar)) {
					cellLayout.removeView(mDragInfo.cell);
					mLauncher.ifNeedClearCache(mDragInfo.cell);
					mDragController.setOriginator(null);
					if (mDragInfo.cell instanceof DropTarget) {
						mDragController.removeDropTarget((DropTarget) mDragInfo.cell);
					}
//					if (target instanceof MagicDockbar) {
//						((MagicDockbar) target).exchangeAppWith(this,null);
//					}
				}
			} else {
					cellLayout.onDropAborted(mDragInfo.cell);
					//取消当前CellLayout无法drop的标示
					getCellLayoutAt(getCurrentScreen()).setAcceptDropOnSpringMode();
					if(isOnSpringMode()){						
						Toast.makeText(getContext(), R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
					}
			}
		}
		
		cleanReorderAllState();
	}
	
	public void cleanDragInfo(){
		if(cleanDragInfoOnDrop){			
			mDragInfo = null;
			if(mWorkspaceDragAndDrop != null){
				mWorkspaceDragAndDrop.cleanDragInfo();
			}
		}
	}
	
	public WorkspaceDragAndDrop getWorkspaceDragAndDrop(){
		return mWorkspaceDragAndDrop;
	}
	
	//是否从匣子拖动view并drop到workspace
	public boolean isDropViewFromDrawer() {
		return isDropViewFromDrawer;
	}

	public void resetIsDropViewFromDrawer() {
		isDropViewFromDrawer = false;
	}
	
	public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		final View ignoreView = mDragInfo == null ? null : mDragInfo.cell;
		int[] dragInfoSpanXY = getDragInfoSpanXY();
		CellLayout layout = getCurrentDropLayout();
		int[] mTargetCell = estimateDropCell(x - xOffset, y - yOffset, dragInfoSpanXY[0], dragInfoSpanXY[1], ignoreView, layout, mTouchUpLocation);
		if (mTargetCell == null) {
			return null;
		}
		
		return estimateDropLocation(mTargetCell, dragInfoSpanXY);
	}
	
	private int[] getDragInfoSpanXY(){
		int spanX = mDragInfo == null ? 1 : mDragInfo.spanX;
		int spanY = mDragInfo == null ? 1 : mDragInfo.spanY;
		return new int[]{spanX, spanY};
	}
	
	public Rect estimateDropLocation(int[] targetCell, int[] spanXY){
		if(spanXY == null){
			spanXY = getDragInfoSpanXY();
		}
		
		Rect location = new Rect();
//		int[] xy = CellLayoutConfig.getXY(targetCell[0], targetCell[1]);
//		location.left = xy[0];
//		location.top = xy[1];
		
		final CellLayout cellLayout = getCurrentDropLayout();
		int cellWidth = cellLayout.getCellWidth();
		int cellHeight = cellLayout.getCellHeight();
		if(cellWidth == 0 || cellHeight == 0){//防止celllayout还未渲染情况，如从匣子拖出小部件到添加屏
			cellWidth = getCellLayoutAt(0).getCellWidth();
			cellHeight = getCellLayoutAt(0).getCellHeight();
		}
		
		location.left = targetCell[0] * (cellWidth + cellLayout.getCellGapX());
		location.top = targetCell[1] * (cellHeight + cellLayout.getCellGapY()) + getTopPadding();
		
		location.right = location.left + cellWidth * spanXY[0] + cellLayout.getCellGapX() * (spanXY[0] - 1);
		location.bottom = location.top + cellHeight * spanXY[1] + cellLayout.getCellGapY() * (spanXY[1] - 1);
		return location;
	}

	/**
	 * Calculate the nearest cell where the given object would be dropped.
	 */
	public int[] estimateDropCell(int pixelX, int pixelY, int spanX, int spanY, View ignoreView, CellLayout layout, int[] recycle) {
		// Create vacant cell cache if none exists
		if (mVacantCache == null) {
			layout.initOccupied(ignoreView, false);
			mVacantCache = layout.findAllVacantCells(null, spanX, spanY);
		}

		// Find the best target drop location
		return layout.findNearestVacantArea(pixelX, pixelY, spanX, spanY, mVacantCache, recycle);
	}
	
	/**
	 * 是否为合理的屏幕值
	 */
	public static boolean isValidScreen(int screen) {
		return screen >=0 && screen < MAX_SCREEN;
	}
	
	public WorkspaceSpring getWorkspaceSpring() {
		return mWorkspaceSpring;
	}
	
	private void setEffect(){
		int effectValue = BaseSettingsPreference.getInstance().getScreenScrollEffects();
		EffectsType.setCurrentEffect(effectValue);
	}
	
	/**
	 * 拖动结束回调
	 * @param mDragSource
	 */
	public void onDragEnd(DragSource mDragSource){
		cleanDragInfo();
		resetIsDropViewFromDrawer();
		if (mDragSource instanceof ScreenViewGroup) {
			destoryCurrentChildHardwareLayer();
		}
		//防止光亮背景还存在
		getCurrentCellLayout().invalidate();
		DragHelper.getInstance().dragOutlineAnimateOut();
		setAllowAnimateMerFolder(true);
	}
	
	void enableChildrenCacheNoGpu() {
		if(GpuControler.isOpenGpu(this))
    		return;
		
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            layout.setChildrenDrawnWithCacheEnabled(true);
            layout.setChildrenDrawingCacheEnabled(true);
        }
    }

    void clearChildrenCacheNoGpu() {
    	if(GpuControler.isOpenGpu(this))
    		return;
    	
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            layout.setChildrenDrawnWithCacheEnabled(false);
        }
    }
    
    public void clearChildrenCache(){
		int currentEffect = EffectsType.getCurrentEffect();
		if (currentEffect >= EffectsType.GLOBAL && currentEffect <= EffectsType.TORNADO) {
			final int count = getChildCount();
			int childCount = 0;

			for (int i = 0; i < count; i++) {
				final CellLayout layout = (CellLayout) getChildAt(i);
				childCount = layout.getChildCount();
				for (int j = 0; j < childCount; j++) {
					View view = layout.getChildAt(j);
					if (isWidget(view) && (view.getDrawingCache() != null)) {
						view.destroyDrawingCache();
					}
				}
			}
		}
	}
    
    //增加监听view
  	public void addOnWorkspaceListener(int screen, OnWorkspaceScreenListener view) {
  		if(view == null || screen < 0 || screen >= getChildCount()) 
  			return;
  		ArrayList<OnWorkspaceScreenListener> list = onWorkspaceListener.get(screen);
  		if(list == null){
  			list = new ArrayList<OnWorkspaceScreenListener>();
  			onWorkspaceListener.put(screen, list);
  		}
  		if(!list.contains(view)){
  			list.add(view);
  		}
  	}
  	
  	//移除监听view
  	public void removeOnWorkspaceListener(int screen, OnWorkspaceScreenListener view) {
  		if(view == null || screen < 0 || screen >= getChildCount()) 
  			return;
  		ArrayList<OnWorkspaceScreenListener> list = onWorkspaceListener.get(screen);
  		if(list == null){
  			return;
  		}else {
  			list.remove(view);
  		}
  	}
  	//移除指定屏幕上所有View的监听
  	public void removeOnWorkspaceListener(int del){
  		if(onWorkspaceListener.size() == 0)
  			return;
  		
  		ArrayList<OnWorkspaceScreenListener> delSrceenListener = onWorkspaceListener.get(del);
  		if(delSrceenListener != null){
  			onWorkspaceListener.remove(del);
  		}
  		
  		for(int i = del; i <= getChildCount() - 2; i ++){
  			if(onWorkspaceListener.get(i+1) != null){
  				onWorkspaceListener.put(i, onWorkspaceListener.get(i+1));
  				onWorkspaceListener.remove(i + 1);
  			}
  		}
  		
  		if(onWorkspaceListener.get(getChildCount() - 1) != null)
  			onWorkspaceListener.remove(getChildCount() - 1);
  	}
  	
  	/**
  	 * Description: 在预览屏幕，将位置为start的屏幕，拖到end位置时，所注册的监听也要同步修改	
  	 */
  	public void changeOnWorkspaceScreenListener(int start, int end){
  		if(onWorkspaceListener.size() == 0 || start == end)
  			return;
  		
  		ArrayList<OnWorkspaceScreenListener> startSrceenListener = onWorkspaceListener.get(start);
  		if(startSrceenListener != null){
  			onWorkspaceListener.remove(start);
  		}
  		if(start < end){
  			for(int i = start; i <= end - 1; i ++){
  				if(onWorkspaceListener.get(i+1) != null){
  					onWorkspaceListener.put(i, onWorkspaceListener.get(i+1));
  					onWorkspaceListener.remove(i + 1);
  				}
  			}
  		}else if(start > end){
  			for(int i = start; i >= end + 1; i --){
  				if(onWorkspaceListener.get(i-1) != null){
  					onWorkspaceListener.put(i, onWorkspaceListener.get(i-1));
  					onWorkspaceListener.remove(i - 1);
  				}
  			}
  		}
  		
  		if(startSrceenListener != null){
  			onWorkspaceListener.put(end, startSrceenListener);
  		}
  	}
  	
  	//开启workspace当前屏上所有view的注册的监听
  	public void startOnWorkspaceScreenListener(){
  		if(lastListenerScreen != mCurrentScreen){
  			lastListenerScreen = mCurrentScreen;
  			handleOnWorkspaceScreenListener(mCurrentScreen);
  		}
  	}
  	
  	//关闭workspace上所有view的注册的监听
  	public void closeOnWorkspaceScreenListener(){
  		lastListenerScreen = -1;
  	}
  	
  	/**
  	 * Description: View监听处理
  	 * @param screen 该屏幕中的View启动监听，其它屏幕view关闭监听
  	 */
  	private void handleOnWorkspaceScreenListener(int screen){
  		if(onWorkspaceListener.size() == 0)
  			return;
  		for(int i = 0; i < getChildCount(); i++){
  			ArrayList<OnWorkspaceScreenListener> list = onWorkspaceListener.get(i);
  			if(list == null)
  				continue;
  			
  			if(i == screen){
  				for(OnWorkspaceScreenListener view : list){
  					callOnWorkspaceCurrentScreen(view);
  				}
  			}
//  			else{
//  				for(OnWorkspaceScreenListener view : list){
//  					callOffWorkspaceCurrentScreen(view);
//  				}
//  			}
  		}
  	}
  	
  	public void callOnWorkspaceCurrentScreen(OnWorkspaceScreenListener view){
  		if(view == null) 
  			return;
  		view.onWorkspaceCurrentScreen();
  	}
  	
//  	public void callOffWorkspaceCurrentScreen(OnWorkspaceScreenListener view){
//  		if(view == null) 
//  			return;
//  		view.offWorkspaceCurrentScreen();
//  	}
  	
	
	//================================================子类可重载下列方法===============================================//
	/**
	 * 设置默认屏
	 * @param defaultScreen
	 */
	public void setDefaultScreen(int defaultScreen) {
		mDefaultScreen = defaultScreen;
		BaseConfigPreferences.getInstance().setDefaultScreen(defaultScreen);
	}
	
	/**
	 * 单指上滑
	 */
	public void onActionUp(){
	}
	
	/**
	 * 单指下滑
	 * @param y
	 */
	public void onActionDown(int y){
	}
	
	/**
	 * 捏手势响应
	 * @param newDist
	 */
	public boolean onPinch(){
		return true;
	}
	
	/**
	 * 撑手势响应
	 * @param newDist
	 */
	public boolean onSpread(){
		return true;
	}
	
	/**
	 * 双指上滑
	 * @param event
	 */
	@Override
	public void onMultiUp(MotionEvent event) {
	}

	/**
	 * 双指下滑
	 * @param event
	 */
	@Override
	public void onMultiDown(MotionEvent event) {
	}	

	/**
	 * 点击空白位置
	 */
	protected void onClickNoOccupiedCell(int screen) {
	}
	
	/**
	 * 点击任意位置
	 * @return true表示不再向下传递事件
	 */
	protected boolean onClickCell(int screen) {
		return false;
	}
	
	/**
	 * 当前屏从编辑模式退出后回调
	 */
	public void onCurScreenAniNormalMode(){
		
	}
	
	/**
	 * 当前屏进入编辑模式后回调
	 */
	public void onCurScreenAniSpringMode(){
		
	}
	
	/**
	 * 退出编辑模式时，隐藏编辑栏
	 */
	public void hideEditor(){
		
	}
	
	/**
	 * 打开指定标签的编辑栏
	 * @param f 编辑栏高度
	 * @param tab 编辑栏标签
	 */
	public void showEditor(float f, String tab){
		
	}
	
	/**
	 * 恢复进入到编辑模式前的状态
	 */
	public void cleanSpringOtherStuff(){
		
	}
	
	/**
	 * 初始化退拽控制器
	 * @param mDragInfo
	 */
	public void initWorkspaceDragAndDrop(CellLayout.CellInfo mDragInfo){
		
	}
	
	/**
	 * 获取icon底部距离，用于绘制光亮背景
	 * @param v
	 * @return
	 */
	public int getIconBottom(View v){
		return 0;
	}
	
	/**
	 * 创建从匣子拖出小部件的光亮背景图
	 * @param v
	 * @param canvas
	 * @param padding
	 * @return
	 */
	public Bitmap createWidgetPreviewViewDragOutline(DragView v, Canvas canvas, int padding){
		return null;
	}
	
	/**
	 * 在刚拖动起来时，是否需要光亮背景渐变动画
	 * @param view
	 * @return
	 */
	public boolean isNeedFadeAnimation(View view){
		return true;
	}

	/**
	 * 移除screen
	 * @param i
	 */
	public void removeScreenFromWorkspace(int i){
		removeOnWorkspaceListener(i);
	}
	
	/**
	 * 添加screen
	 */
	public void createScreenToWorkSpace(){
	}
	
	/**
	 * 移动屏幕时，重置CellLayout其它内容，如情景桌面中的素材
	 */
	public void resetCellLayoutStuffOnScreenChange(){
		
	}
	
	/**
	 * 移动屏幕时，重置CellLayout上的位置数据
	 * @param screenStart
	 *            开始屏
	 * @param screenEnd
	 *            目的屏
	 */
	public void moveItemPositionsOnScreenChange(int screenStart, int screenEnd) {
		changeOnWorkspaceScreenListener(screenStart, screenEnd);
	}
	
	/**
	 * 获取空位，放置新安装app的快捷方式
	 * @return
	 */
	public int[] getLocationForNewInstallApp(Context mContext){
		return null;
	}

	/**
	 * 初始化和渲染编辑模式的指示灯
	 */
	public void inflateSpringLightbar() {
		
	}
	
	/**
	 * 是否为实时文件夹
	 * @param info
	 * @return
	 */
	public boolean isRealFolder(Object info){
		return false;
	}
	
	/**
	 * Description: 根据ItemInfo生成相应的View
	 */
	public View createViewByItemInfo(ItemInfo itemInfo){
		return null;
	}
	
	//是否为应用列表图标
	public boolean isAllAppsIndependence(ItemInfo item){
		return false;
	}
	
	/**
	 * 拖起时，一些额外操作，如设置菜单栏状态开关
	 * @param child
	 */
	public void onDragStartEx(View child){
	}
	
	/**
	 * 绘制CellLayout时，是否需要保存层，防止出现切割问题
	 * @return
	 */
	public boolean needSaveLayerOnDispatchDraw(){
		return BaseConfig.isDrawWallPaper;
	}
	
	/**
	 * 图标被拖动覆盖或开始挤动时的回调
	 * @param v
	 */
	public void handleOnDragOverOrReorder(View v){
		
	}
}
