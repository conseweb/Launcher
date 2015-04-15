package com.bitants.launcherdev.launcher.screens;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.ConfigFactory;
import com.bitants.launcherdev.launcher.support.WallpaperHelper;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.ConfigFactory;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.support.WallpaperHelper;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.ConfigFactory;
import com.bitants.launcherdev.launcher.support.WallpaperHelper;

/**
 * Description: Author: guojy Date: 2012-10-11 下午02:42:07
 */
public class WorkspaceLayer extends ViewGroup {
	static final String TAG = "WorkspaceLayer";

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;// 水平方向滑动
	private boolean hasSetScrolling;// 是否已设置滑屏方向
	private static final int SNAP_VELOCITY = 300;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private float mLastMotionX, mLastMotionY;
	private int mCurrentScreen;
	private int mTouchState = TOUCH_STATE_REST;

	private static final int INVALID_SCREEN = -999;
	private int mNextScreen = INVALID_SCREEN;

	private boolean isEndlessScrolling = true;

	private BaseLauncher launcher;
	private ScreenViewGroup workspace;
	private boolean isFirstLayout = true;

	private static final float BASELINE_FLING_VELOCITY = 2500.f;
	private static final float FLING_VELOCITY_INFLUENCE = 0.4f;

	private static final int NAVIGATION_POSOTION = 0; // 导航页的位置
	private static final int WORKSPCAE_POSITION = 1; // workspace的位置
	private boolean isShowZeroView = false;// 是否显示导航页

	private int mTouchSlop;
	private int mMaximumVelocity;

	private int screenWidth;

	private boolean cancelEvent = false;
	private boolean isWorkspaceFirstToZeroView = false;// 是否workspace第一屏滑向导航屏,用于连续滑动时做特殊处理
	private boolean isWorkspaceLastToZeroView = false;// 是否workspace最后一屏滑向导航屏
	private boolean isZeroViewToWrokspaceFirst = false;// 是否导航屏滑向workspace第一屏
	private boolean isZeroViewToWrokspaceLast = false;// 是否导航屏滑向workspace最后一屏
	private WallpaperHelper mWallpaperHelper;
	/**
	 * 第0屏幕 背景alpha透明百分比
	 */
	private float bgAlphaP = 0.67f;
	private boolean loadZeroView = false; // 是否已加载导航页

	private boolean lockSnapToScreen = false;
	

	public WorkspaceLayer(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(context);
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	public void setZeroView() {
		if (BaseSettingsPreference.getInstance().isShowNavigationView()) {
			if (getChildAt(0).getVisibility() != View.VISIBLE) {
				getChildAt(0).setVisibility(View.VISIBLE);
			}
			isShowZeroView = true;
		} else {
			if (getChildAt(0).getVisibility() != View.INVISIBLE) {
				getChildAt(0).setVisibility(View.INVISIBLE);
			}

			if (isShowZeroView && mCurrentScreen == NAVIGATION_POSOTION) {// 如果当前在第0屏更改设置
				mCurrentScreen = WORKSPCAE_POSITION;
				scrollTo(screenWidth, 0);
				showDockbarImmediately();
			}

			isShowZeroView = false;
		}
	}

	public void resetZeroView() {
		setZeroView();
		invalidate();
	}

	public void showZeroView() {
		if (getChildAt(0).getVisibility() != View.VISIBLE && BaseSettingsPreference.getInstance().isShowNavigationView()) {
			getChildAt(0).setVisibility(View.VISIBLE);
			isShowZeroView = true;
			invalidate();
		}
	}

	public void hideZeroView() {
		if (getChildAt(0).getVisibility() != View.INVISIBLE) {
			getChildAt(0).setVisibility(View.INVISIBLE);
			isShowZeroView = false;
			invalidate();
		}
	}

	public boolean isShowZeroView() {
		return isShowZeroView;
	}

	public void setShowZeroView(boolean isShowNavigation) {
		this.isShowZeroView = isShowNavigation;
	}

	public void setLauncher(BaseLauncher launcher) {
		this.launcher = launcher;
	}

	public boolean isEndlessScrolling() {
		return isEndlessScrolling;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setWallpaperHelper(WallpaperHelper mWallpaperHelper) {
		this.mWallpaperHelper = mWallpaperHelper;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		screenWidth = MeasureSpec.getSize(widthMeasureSpec);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View v = getChildAt(i);
			if (v instanceof ScreenViewGroup) {
				workspace = (ScreenViewGroup) getChildAt(i);
			}

			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (isFirstLayout) {// 打开workspace默认屏
			scrollTo(screenWidth, 0);
			mCurrentScreen = loadZeroView ? WORKSPCAE_POSITION : 0;
			isFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			final int childWidth = child.getMeasuredWidth();
			child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
			childLeft += childWidth;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), 0);
			mWallpaperHelper.updateWallpaperOffset(getContext(), getScrollX(), false);
			postInvalidate();
			if (mScroller.isFinished()) {// 处理Workspace上的View监听
				if (!isShowZeroView || isOnWorkspace()) {
					workspace.startOnWorkspaceScreenListener();
				} else {
					workspace.closeOnWorkspaceScreenListener();
				}
			}
		} else if (mNextScreen != INVALID_SCREEN) {
			if (mNextScreen == -1 && true) {
				mCurrentScreen = getChildCount() - 1;
				scrollTo(mCurrentScreen * getWidth(), 0);
			} else if (mNextScreen == getChildCount() && true) {
				mCurrentScreen = 0;
				scrollTo(0, 0);
			} else {
				mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
			}

			mNextScreen = INVALID_SCREEN;
		} else if (mTouchState == TOUCH_STATE_SCROLLING) {
			mWallpaperHelper.updateWallpaperOffset(getContext(), getScrollX(), false);
			postInvalidate();
		}
	}

  
	@Override
	public void dispatchDraw(Canvas canvas) {
		isEndlessScrolling = BaseSettingsPreference.getInstance().isRollingCycle();
		// Log.e(TAG, "CellLayout.dispatchDraw");

		int childCount = getChildCount();
		boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
		// FIXME 写法有问题
		if (fastDraw) {
			if (loadZeroView && isOnZeroView()) {
				canvas.drawColor(genColorValue(255));
				scrollToHideDockBar();
			} else {
				scrollToShowDockbar();
			}
			drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
			mWallpaperHelper.cleanWallpaper();
		} else {
			long drawingTime = getDrawingTime();
			int width = getWidth();
			float scrollPos = (float) getScrollX() / width;

			int leftScreen;
			int rightScreen;
			boolean isScrollToRight = false;
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
			// 壁纸循环滚动处理
			if (ConfigFactory.isWallpaperRolling(getContext())) {
				if (isScreenValid(leftScreen)) {
					if (rightScreen == 0 && !isScrollToRight) {
						mWallpaperHelper.updateRollingCycleWallpaper(canvas, isScrollToRight, getScrollX(), getRight(), getLeft());
					}
				}
				if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
					if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {
						mWallpaperHelper.updateRollingCycleWallpaper(canvas, isScrollToRight, getScrollX(), getRight(), getLeft());
					}
				}
			}

			if (isScreenValid(leftScreen)) {

				if (leftScreen == 0) {
					processZeroScreenAnim(canvas, true);
				}
				if (rightScreen == 0 && !isScrollToRight) {// 第一屏滑向最后一屏
					int offset = childCount * width;
					canvas.translate(-offset, 0);
					drawChild(canvas, getChildAt(leftScreen), drawingTime);
					canvas.translate(+offset, 0);
				} else {
					drawChild(canvas, getChildAt(leftScreen), drawingTime);
				}

			}
			if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
				if (rightScreen == 0) {
					processZeroScreenAnim(canvas, false);
				}
				if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {// 最后一屏滑向第一屏
					int offset = childCount * width;
					canvas.translate(+offset, 0);
					drawChild(canvas, getChildAt(rightScreen), drawingTime);
					canvas.translate(-offset, 0);
				} else {
					drawChild(canvas, getChildAt(rightScreen), drawingTime);
				}

			}
		}
	}

//	@Override
//	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//		Matrix matrix = canvas.getMatrix();
//		float[] values = new float[9];
//		matrix.getValues(values);
//		values[5] = 0.0f;
//		matrix.setValues(values);
//		canvas.setMatrix(matrix);
//		return super.drawChild(canvas, child, drawingTime);
//	}

	private boolean isScreenValid(int screen) {
		return screen >= 0 && screen < getChildCount();
	}

	public void snapToScreen(int whichScreen, int velocity) {
		if(isLockSnapToScreen())
			return;
		
		whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, getChildCount() - (isEndlessScrolling ? 0 : 1)));
		
		if(NAVIGATION_POSOTION == whichScreen || getChildCount() == whichScreen){//滑向第0屏时，隐藏新手引导
			launcher.onSnapToNavigation();
		}else{
			launcher.onSnapToWorkspace();
		}
		
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			final int screenDelta = Math.max(1, Math.abs(whichScreen - mCurrentScreen));
			int duration = (screenDelta + 1) * 150;
			velocity = Math.abs(velocity);
			if (velocity == 0)
				velocity = (int) BASELINE_FLING_VELOCITY;

			if (velocity > 0) {
				duration += (duration / (velocity / BASELINE_FLING_VELOCITY)) * FLING_VELOCITY_INFLUENCE;
			} else {
				duration += 200;
			}

			mScroller.startScroll(getScrollX(), 0, delta, 0, duration);

			mNextScreen = whichScreen;

			if (mNextScreen == -1 && isEndlessScrolling) {
				mCurrentScreen = getChildCount() - 1;
			} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
				mCurrentScreen = 0;
			} else {
				mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
			}

			if(mCurrentScreen == 0){
				launcher.dismissBottomMenu();
			}
			
			invalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(launcher.notActionWorkspaceLayerTouch() || isLockSnapToScreen())
			return true;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {// 取消动画
				mScroller.abortAnimation();
				scrollTo(mScroller.getFinalX(), 0);
			}

			mLastMotionX = x;
			mLastMotionY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			// 记录导航屏连续滑向Workspace最后一屏状态
			if (isEndlessScrolling && deltaX < 0 && isOnWorkspace() && !isWorkspaceLastToZeroView && workspace.getCurrentScreen() != 0 && workspace.getChildCount() > 1) {
				isZeroViewToWrokspaceLast = true;
			}

			if (cancelEvent || mTouchState != TOUCH_STATE_SCROLLING) {
				break;
			}
			// 取消EditText焦点，防止重绘
			// this.requestFocus();
			SystemUtil.hideKeyboard(this);

			mLastMotionX = x;
			mLastMotionY = y;

			// workspace切换celllayout
			if (mCurrentScreen == NAVIGATION_POSOTION && getScrollX() + deltaX > 0) {// 滑向workspace第一屏
				if (workspace.getCurrentScreen() != 0) {
					workspace.scrollToScreen(0);
				}
			}
			if (isEndlessScrolling && mCurrentScreen == NAVIGATION_POSOTION && getScrollX() + deltaX < 0) {// 滑向workspace最后一屏
				if (workspace.getCurrentScreen() != workspace.getChildCount() - 1) {
					workspace.scrollToScreen(workspace.getChildCount() - 1);
				}
			}

			// 防止wrokspace第一屏或最后一屏连续左右move时绘到导航页
			if (((isOnWorkspaceFirstScreen() && getScrollX() + deltaX > screenWidth) || (isOnWorkspaceLastScreen() && getScrollX() + deltaX < screenWidth)) && workspace.getChildCount() > 1) {
				break;
			}

			scrollBy(deltaX, 0);

			// 用于处理连续滑动状态
			if (deltaX > 0 && isOnZeroView()) {
				isZeroViewToWrokspaceFirst = true;
			}

			break;

		case MotionEvent.ACTION_UP:
			if (cancelEvent) {
				cancelEvent = false;
				break;
			}
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

			int childCount = getChildCount();

			int velocityX = (int) velocityTracker.getXVelocity();
			int targetScreen;
			int velocity = velocityX;
			if (!loadZeroView || !BaseSettingsPreference.getInstance().isShowNavigationView()) {
				return true;
			}

			// 导航屏连续滑向workspace最后一屏做特殊处理
			if (isZeroViewToWrokspaceLast && velocityX > SNAP_VELOCITY && isOnWorkspaceLastScreen() && workspace.getChildCount() > 1) {
				WallpaperHelper.onlyUpdateWorkspace();
				workspace.snapToScreen(workspace.getChildCount() - 2, velocity, false, false, false);
				isZeroViewToWrokspaceLast = false;
				break;
			}

			if (velocityX > SNAP_VELOCITY && (isOnZeroView() || isOnWorkspaceFirstScreen())) {// 向右滑
				targetScreen = mCurrentScreen - 1;
			} else if (velocityX < -SNAP_VELOCITY && isOnZeroView()) {// 向左滑
				targetScreen = mCurrentScreen + 1;
			} else {
				if (velocityX > SNAP_VELOCITY || velocityX < -SNAP_VELOCITY) {
					targetScreen = getScrollX() > 0 ? childCount : -1;// 第一屏与最后一屏间切换
					// 校正连续左右move后的错误目标屏
					if (((isOnWorkspaceFirstScreen() && velocityX < -SNAP_VELOCITY) || (isOnWorkspaceLastScreen() && velocityX > SNAP_VELOCITY)) && targetScreen == childCount
							&& workspace.getChildCount() > 1) {
						targetScreen = WORKSPCAE_POSITION;
					}
				} else {
					targetScreen = (int) Math.floor((getScrollX() + (screenWidth / 2)) / (float) screenWidth);
					velocity = 0;
				}
			}

			// 1.边界处理：
			// 防止workspace没有切换celllayout
			if (isEndlessScrolling && targetScreen == WORKSPCAE_POSITION && workspace.getCurrentScreen() != 0 && targetScreen != mCurrentScreen) {
				workspace.snapToScreen(0, 0, false, true, false);
			}
			if (isEndlessScrolling && targetScreen == -1 && workspace.getCurrentScreen() != workspace.getChildCount() - 1 && targetScreen != mCurrentScreen) {
				workspace.snapToScreen(workspace.getChildCount() - 1, 0, false, true, false);
			}
			// 防止连续滑动时，workspace第一屏没有归位
			if (workspace.getCurrentScreen() == 0 && targetScreen == WORKSPCAE_POSITION && workspace.getScrollX() != 0) {
				workspace.snapToScreen(0, 0, false, true, false);
			}

			// 2.滑动屏
			snapToScreen(targetScreen, velocity);
			// 3.隐藏或显示dock栏
			if (mCurrentScreen == NAVIGATION_POSOTION) {
				hideDockbarImmediately();
			}
			if (mCurrentScreen == WORKSPCAE_POSITION || mCurrentScreen == childCount) {
				showDockbarImmediately();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			hasSetScrolling = false;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			hasSetScrolling = false;

			// 事情取消，默认移向第0屏导航屏
			mScroller.abortAnimation();
			mNextScreen = NAVIGATION_POSOTION;
			scrollTo(0, 0);
			hideDockbarImmediately();
			break;
		}

		return true;
	}

	public boolean isLockSnapToScreen() {
		return lockSnapToScreen;
	}

	public void setLockSnapToScreen(boolean lockSnapToScreen) {
		this.lockSnapToScreen = lockSnapToScreen;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/**
		 * 文件夹处于打开状态则交给onTouchEvent事件进行关闭
		 */
		if (launcher.isFolderOpened() || isLockSnapToScreen()) {
			return true;
		}

		if (!isShowZeroView) {// 没有导航页
			return false;
		}

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			final int yDiff = (int) Math.abs(mLastMotionY - y);

			// 记录上次滑动的方向，用于连续滑时的判断
			if (mLastMotionX - x < 0 && (!isOnWorkspace() || isOnWorkspaceFirstScreen() || isOnWorkspaceSecondScreen())) {
				isWorkspaceFirstToZeroView = true;
			}
			if (mLastMotionX - x > 0 && (!isOnWorkspace() || isOnWorkspaceLastScreen())) {
				isWorkspaceLastToZeroView = true;
			}

			// 在导航屏滑动或者workspace第一屏滑向导航屏或者workspace最后一屏滑向导航屏时
			if (!isOnWorkspace() || (mLastMotionX - x < -mTouchSlop && isOnWorkspaceFirstScreen() || (mLastMotionX - x > mTouchSlop && isOnWorkspaceLastScreen()))) {

				if ((xDiff <= mTouchSlop && yDiff <= mTouchSlop) || hasSetScrolling) {
					break;
				}

				/**
				 * 当子View拦截手势时，滑屏角度小于50度才可横向滑屏
				 */
				double res = (double) yDiff / xDiff;
				double moveDegree = Math.atan(res) / 3.14 * 180;
				if (xDiff > mTouchSlop && moveDegree < 50) {
					mTouchState = TOUCH_STATE_SCROLLING;
					hasSetScrolling = true;
					WallpaperHelper.notUpdateWorkspace();
				} else if (yDiff > mTouchSlop) {
					hasSetScrolling = true;
					if (isOnZeroView()) {
						// this.requestFocus();
						SystemUtil.hideKeyboard(this);
					}
				}
			} else if (!isOnZeroView()) {
				WallpaperHelper.onlyUpdateWorkspace();
			}
			break;

		case MotionEvent.ACTION_DOWN:
			hasSetScrolling = false;

			mLastMotionX = x;
			mLastMotionY = y;

			// 连续滑时防止导航屏盖在workspace上面
			if (!workspace.getScroller().isFinished()) {
				workspace.getScroller().abortAnimation();
				workspace.scrollTo(workspace.getScroller().getFinalX(), 0);
				showDockbarImmediately();
			}

			// 在导航屏滑动或者workspace第一屏连续滑向导航屏或者workspace最后一屏连续滑向导航屏时
			if (!isZeroViewToWrokspaceLast && !isZeroViewToWrokspaceFirst && (!isOnWorkspace() || (isWorkspaceFirstToZeroView || isWorkspaceLastToZeroView))) {
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			} else {
				// 连续滑时防止导航屏盖在workspace上面
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
					scrollTo(mScroller.getFinalX(), 0);
					if (isOnWorkspace()) {
						showDockbarImmediately();
					}
				}

				mTouchState = TOUCH_STATE_REST;
			}
			resetFlingState();
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			hasSetScrolling = false;

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	private void resetFlingState() {
		isWorkspaceFirstToZeroView = false;
		isWorkspaceLastToZeroView = false;
		isZeroViewToWrokspaceFirst = false;
		// isNavigationToWrokspaceLast = false;
	}

	public boolean isOnZeroView() {
		if (!isShowZeroView) {
			return false;
		}
		return mCurrentScreen == NAVIGATION_POSOTION;
	}

	private boolean isOnWorkspace() {
		return mCurrentScreen == WORKSPCAE_POSITION;
	}

	private boolean isOnWorkspaceFirstScreen() {
		return isOnWorkspace() && workspace.getCurrentScreen() == 0;
	}

	private boolean isOnWorkspaceSecondScreen() {
		if (workspace.getChildCount() == 1)
			return false;
		return isOnWorkspace() && workspace.getCurrentScreen() == 1;
	}

	private boolean isOnWorkspaceLastScreen() {
		if (!isEndlessScrolling)
			return false;
		return isOnWorkspace() && workspace.getCurrentScreen() == workspace.getChildCount() - 1;
	}

	private void hideDockbarImmediately() {
		scrollToHideDockBar();
		launcher.onHideDockbarForNavigation(this);
	}

	private void showDockbarImmediately() {
		scrollToShowDockbar();
		if (!BaseConfig.isZh()) {// 隐藏软键盘 caizp 2012-11-16
			SystemUtil.hideKeyboard(this);
		}
	}

	public void snapToWorkspaceDefaultScreen() {
		snapToWorkspaceScreen(workspace.getDefaultScreen());
	}
	
	public void snapToWorkspaceScreen(int screen) {
		snapToScreen(WORKSPCAE_POSITION, 0);
		showDockbarImmediately();

		WallpaperHelper.onlyUpdateWorkspace();
		if (workspace.getCurrentScreen() != screen) {
			workspace.snapToScreen(screen, 0, false, true, false);
		} else {
			mWallpaperHelper.updateWallpaperOffset(getContext(), screen * screenWidth, true);// 需更新壁纸
		}
	}

	public void snapToZeroView() {
		if (!isShowZeroView) {
			return;
		}
		WallpaperHelper.notUpdateWorkspace();
		snapToScreen(NAVIGATION_POSOTION, 0);
		hideDockbarImmediately();
	}

	private void processZeroScreenAnim(Canvas canvas, boolean isLeftSceen) {
		if (!isShowZeroView) {
			return;
		}
		int visibleWidth = getWidth() - Math.abs(countShiftingX(this, 0, isLeftSceen));
		if (isLeftSceen) {
			processZeroScreenAnim(canvas, visibleWidth);
		} else {
			if (isEndlessScrolling) {
				processZeroScreenAnim(canvas, visibleWidth);
			} else {
				canvas.drawColor(genColorValue(255));
			}
		}
	}

	private void processZeroScreenAnim(Canvas canvas, int visibleWidth) {
		float offsetY = launcher.getBottomContainer().getHeight() * visibleWidth / (getWidth() * 0.5f);
		launcher.getBottomContainer().scrollTo(0, -(int) offsetY);
		// 更换透明度
		float alpha = 255.0f * visibleWidth / getWidth();
		canvas.drawColor(genColorValue(alpha));
	}

	private int genColorValue(float alpha) {
		return Color.argb((int) (alpha * bgAlphaP), 0, 0, 0);
	}

	private int countShiftingX(WorkspaceLayer workspace, int screen, boolean isLeftScreen) {
		int length = workspace.getWidth() * workspace.getChildCount();
		if (isLeftScreen) {
			return (screen * workspace.getWidth() - workspace.getScrollX() - length) % length;
		} else {
			return (screen * workspace.getWidth() - workspace.getScrollX() + length) % length;
		}
	}

	public void setLoadZeroView(boolean loadNavigationView) {
		this.loadZeroView = loadNavigationView;
		this.mCurrentScreen = WORKSPCAE_POSITION;
	}

	public void scrollToHideDockBar() {
		if (launcher != null && launcher.getBottomContainer() != null) {
			launcher.getBottomContainer().scrollTo(0, -1000);
		}
	}

	public void scrollToShowDockbar() {
		if (launcher != null && launcher.getBottomContainer() != null && launcher.getBottomContainer().getScrollY() != 0) {
			if(launcher.getBottomContainer().getVisibility() != View.VISIBLE){
				launcher.showBottomContainer();
			}
			launcher.getBottomContainer().scrollTo(0, 0);
		}
	}
	
	public void setBgAlphaP(float bgAlphaP) {
		this.bgAlphaP = bgAlphaP;
	}
}
