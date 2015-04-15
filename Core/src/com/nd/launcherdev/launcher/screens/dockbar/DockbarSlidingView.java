package com.nd.launcherdev.launcher.screens.dockbar;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 支持循环滚屏组件
 * 
 * @author pdw
 */
public abstract class DockbarSlidingView extends ViewGroup {

	protected static final String TAG = "DockbarSlidingView";

	protected static final int INVALID_SCREEN = -999;

	private static final float BASELINE_FLING_VELOCITY = 2500.f;

	private static final float FLING_VELOCITY_INFLUENCE = 0.4f;

	/**
	 * Fling灵敏度
	 */
	public static final int SNAP_VELOCITY = 600;

	private static final int TOUCH_STATE_REST = 0;

	private static final int TOUCH_STATE_DOWN = 1;

	private static final int TOUCH_STATE_SCROLLING = 2;

	private static final int TOUCH_STATE_DONE_WAITING = 3;

	private final static int TOUCH_STATE_FLING_DOWN = 4;

	private final static int TOUCH_STATE_FLING_UP = 5;

	private int mTouchState = TOUCH_STATE_REST;

	/**
	 * mTouchState是否不可再更改标记位，在判定fling up/down时使用
	 */
	private boolean isTouchStateLocked = false;

	private int mTouchSlop;

	private int mMaximumVelocity;

	private float mLastMotionX;

	private float mLastMotionY;
	
	private VelocityTracker mVelocityTracker;

	protected Scroller mScroller;

	/**
	 * 当前页
	 */
	protected int mCurrentScreen = 1;
	
	protected int mDefaultScreen = mCurrentScreen ;

	/**
	 * 目标页 - 标记位作用 e.g. 若当前有3页，下标为0 - 2，循环滚动时，mNextScreen的范围是-1 - 3
	 */
	protected int mNextScreen = INVALID_SCREEN;

	/**
	 * 页宽
	 */
	protected int pageWidth;

	/**
	 * 页高
	 */
	protected int pageHeight;

	/**
	 * 是否锁定布局，若锁定，则在onLayout中不会调用layoutChildren()
	 */
	protected boolean isLockLayout = false;

	/**
	 * 从startPage页开始布局
	 */
	protected int startPage = 0;

	/**
	 * 循环滚动
	 */
	private boolean isEndlessScrolling = false;

	protected Handler handler = new Handler();

	public DockbarSlidingView(Context context) {
		super(context);
		initWorkspace(context);
	}

	public DockbarSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWorkspace(context);
	}

	public DockbarSlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWorkspace(context);
	}

	public void initWorkspace(Context context) {
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		/**
		 * configuration.getScaledTouchSlop() == 24, 提高滚动灵敏度
		 */
		mTouchSlop = configuration.getScaledTouchSlop();

		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mScroller = new Scroller(getContext());

		initSelf(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		//final int height = MeasureSpec.getSize(heightMeasureSpec);
//		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		if (widthMode != MeasureSpec.EXACTLY) {
//			throw new IllegalStateException(
//					"ScrollView only canmCurScreen run at EXACTLY mode!");
//		}
//
//		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//		if (heightMode != MeasureSpec.EXACTLY) {
//			throw new IllegalStateException(
//					"ScrollView only can run at EXACTLY mode!");
//		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		scrollTo(mCurrentScreen * width, 0);

		pageWidth = this.getMeasuredWidth();
		pageHeight = this.getMeasuredHeight();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//super.onLayout(changed, l, t, r, b);
		final int childCount = getChildCount();
		int childLeft = 0;
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
		if (mNextScreen == INVALID_SCREEN) {
			snapToScreen(mCurrentScreen);
		}
	}

	/**
	 * 判定滚屏或是fling up/down事件
	 */
	protected void dealTouchStateInActionMove(float x, float y) {
		/**
		 * 是否监听Fling事件
		 */
		boolean isListenFling = false;

		final int xDiff = (int) Math.abs(mLastMotionX - x);
		final int yDiff = (int) Math.abs(mLastMotionY - y);

		if (!isTouchStateLocked) {
			if (!isListenFling) {
				if (xDiff > mTouchSlop && mTouchState != TOUCH_STATE_DONE_WAITING) {
					mTouchState = TOUCH_STATE_SCROLLING;
					isTouchStateLocked = true;
				}
			} else {
				if (xDiff > mTouchSlop && mTouchState != TOUCH_STATE_DONE_WAITING) {
					mTouchState = TOUCH_STATE_SCROLLING;
					isTouchStateLocked = true;
				} else {
					if ((y - mLastMotionY) > 0) {
						if (yDiff > (mTouchSlop * 2)) {
							mTouchState = TOUCH_STATE_FLING_DOWN;
							isTouchStateLocked = true;
						}
					} else {
						if (yDiff > (mTouchSlop * 2)) {
							mTouchState = TOUCH_STATE_FLING_UP;
							isTouchStateLocked = true;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			/**
			 * ACTION_DOWN在一个子view上时，判定fling up/down
			 */
			dealTouchStateInActionMove(x, y);
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			if (mScroller.isFinished()) {
				mTouchState = TOUCH_STATE_REST;
				isTouchStateLocked = false;
			} else {
				mTouchState = TOUCH_STATE_SCROLLING;
				isTouchStateLocked = true;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(getChildCount() <= 1)
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
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mTouchState = TOUCH_STATE_DOWN;
			mLastMotionX = x;
			mLastMotionY = y;
			
			break;

		case MotionEvent.ACTION_MOVE:

			/**
			 * ACTION_DOWN在空白处时，判定fling up/down
			 */
			dealTouchStateInActionMove(x, y);

			if ((mTouchState != TOUCH_STATE_SCROLLING && mTouchState != TOUCH_STATE_DOWN) || !isTouchStateLocked) {
				break;
			}

			int deltaX = (int) (mLastMotionX - x);

			mLastMotionX = x;
			mLastMotionY = y;

			if (deltaX < 0) {
				/**
				 * 向左滑
				 */
				if (getScrollX() > (isEndlessScrolling ? -pageWidth : -pageWidth / 2)) {
					scrollBy(deltaX, 0);
				}
				
			} else if (deltaX > 0) {
				/**
				 * 向右滑
				 */
				final int availableToScroll = (getPageCount() - 1) * pageWidth - getScrollX() + (isEndlessScrolling ? pageWidth : pageWidth / 2);
				if (availableToScroll > 0) {
					scrollBy(deltaX, 0);
				}

			}

			break;

		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_FLING_DOWN || mTouchState == TOUCH_STATE_FLING_UP) {
				snapToScreen(mCurrentScreen);
			} else {
				mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

				int velocityX = (int) mVelocityTracker.getXVelocity();

				int whichScreen = (int) Math.floor((getScrollX() + (pageWidth / 2)) / (float) pageWidth);
				final float scrolledPos = (float) getScrollX() / pageWidth;

				if (velocityX > SNAP_VELOCITY && mCurrentScreen > (isEndlessScrolling ? -1 : 0)) {
					final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
					whichScreen = Math.min(whichScreen, bound);
					snapToScreen(whichScreen, velocityX);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - (isEndlessScrolling ? 0 : 1)) {
					final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
					whichScreen = Math.max(whichScreen, bound);
					snapToScreen(whichScreen, velocityX);
				} else {
					snapToScreen(whichScreen);
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			snapToScreen(mCurrentScreen);
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if(getChildCount() <= 1)
			return;
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			if (mNextScreen == -1 && isEndlessScrolling) {
				setCurrentScreen(getChildCount() - 1);
				scrollTo(getChildCount() * pageWidth + getScrollX(), getScrollY());
			} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
				setCurrentScreen(0);
				scrollTo(getScrollX() - getChildCount() * pageWidth, getScrollY());
			} else {
				setCurrentScreen(Math.max(0, Math.min(mNextScreen, getChildCount() - 1)));
			}
			mNextScreen = INVALID_SCREEN;
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		boolean restore = false;
		int restoreCount = 0;

		// ViewGroup.dispatchDraw() supports many features we don't need:
		// clip to padding, layout animation, animation listener, disappearing
		// children, etc. The following implementation attempts to fast-track
		// the drawing dispatch by drawing only what we know needs to be drawn.

		if (isEndlessScrolling && getChildCount() < 2) {
			/**
			 * 小于两屏时，不循环滚动
			 */
			isEndlessScrolling = false;
		} 

		boolean fastDraw = mTouchState != TOUCH_STATE_DOWN && mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
		// If we are not scrolling or flinging, draw only the current screen
		if (fastDraw) {
			View v = getChildAt(mCurrentScreen);
			if (v != null) {
				drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
			} else {
				super.dispatchDraw(canvas);
			}
		} else {
			final long drawingTime = getDrawingTime();
			final int width = pageWidth;
			final float scrollPos = (float) getScrollX() / width;
			int leftScreen = (int) scrollPos;
			int rightScreen = leftScreen + 1;

			/**
			 * 屏幕循环滚动
			 */
			boolean isScrollToRight = false;
			int childCount = getChildCount();

			if (scrollPos < 0 && isEndlessScrolling) {
				leftScreen = childCount - 1;
				rightScreen = 0;
			} else {
				leftScreen = Math.min((int) scrollPos, childCount - 1);
				rightScreen = leftScreen + 1;
				if (isEndlessScrolling) {
					rightScreen = rightScreen % childCount;
					isScrollToRight = true;
				}
			}

			if (isScreenValid(leftScreen)) {
				if (rightScreen == 0 && !isScrollToRight) {
					int offset = childCount * width;
					canvas.translate(-offset, 0);
					drawChild(canvas, getChildAt(leftScreen), drawingTime);
					canvas.translate(+offset, 0);
				} else {
					drawChild(canvas, getChildAt(leftScreen), drawingTime);
				}
			}
			if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
				if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {
					int offset = childCount * width;
					canvas.translate(+offset, 0);
					drawChild(canvas, getChildAt(rightScreen), drawingTime);
					canvas.translate(-offset, 0);
				} else {
					drawChild(canvas, getChildAt(rightScreen), drawingTime);
				}
			}
		}

		if (restore) {
			canvas.restoreToCount(restoreCount);
		}
	}

	/**
	 * 获取手指在屏幕的滑动距离
	 */
	public boolean adjustDirection() {
		if (mTouchState != TOUCH_STATE_DOWN && mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN)
			return true;
		else
			return false;
	}

	private boolean isScreenValid(int screen) {
		return screen >= 0 && screen < getChildCount();
	}

	public void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0);
	}

	public void snapToScreen(int whichScreen, int velocity) {

		whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, getChildCount() - (isEndlessScrolling ? 0 : 1)));
		if (getScrollX() != (whichScreen * pageWidth)) {

			mNextScreen = whichScreen;

			final int delta = whichScreen * pageWidth - getScrollX();
			final int screenDelta = Math.max(1, Math.abs(whichScreen - mCurrentScreen));
			int duration = (screenDelta + 1) * 150;

			velocity = Math.abs(velocity);
			if (velocity > 0) {
				duration += (duration / (velocity / BASELINE_FLING_VELOCITY)) * FLING_VELOCITY_INFLUENCE;
			} else {
				duration += 200;
			}

			mScroller.startScroll(getScrollX(), 0, delta, 0, duration);

			/**
			 * 计算真实目标屏
			 */
			int destToScreen = 0;
			
			if (mNextScreen == -1 && isEndlessScrolling) {
				destToScreen = getChildCount() - 1;
			} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
				destToScreen = 0;
			} else {
				destToScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
			}
			
			if (mCurrentScreen != destToScreen) { //滑动到另一屏才算滑屏操作
				statSnapToScreen(destToScreen);
			}
			
			mCurrentScreen = destToScreen;

			invalidate();
		}
	}

	public void scrollLeft() {
		snapToScreen(mCurrentScreen - 1);
	}

	public void scrollRight() {
		snapToScreen(mCurrentScreen + 1);
	}

	public int getPageCount() {
		return getChildCount() ;
	}

	public int getCurScreen() {
		return mCurrentScreen;
	}

	protected void setCurrentScreen(int mCurrentScreen) {
		this.mCurrentScreen = mCurrentScreen;
	}

	public void setEndlessScrolling(boolean isEndlessScrolling) {
		this.isEndlessScrolling = isEndlessScrolling;
	}

	/**
	 * 子类初始化动作,将在构造函数中调用
	 */
	protected abstract void initSelf(Context ctx);
	
	public int getTouchState(){
		return mTouchState;
	}

	/**
	 * 统计滑屏次数
	 */
	public void statSnapToScreen(int destToScreen){
		
	}
}
