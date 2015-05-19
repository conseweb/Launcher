package com.bitants.launcherdev.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Scroller;
import com.bitants.launcherdev.datamodel.CommonGlobal;

/**
 * 支持横向滑屏ViewGroup
 */
public class MyPhoneViewPager extends ViewGroup implements ISlidingConflict {
	/**
	 * 页面选中的回调接口
	 */
	public interface OnPageSelectedListenner {
		/**
		 * 更新时回调
		 */
		void onUpdate(int selectedPage);
	}
	/**
	 * 达到最后一页时继续往前滑动的回调
	 */
	public interface OnTouchedLastPageListenner {
		/**
		 * 更新时回调
		 */
		void onUpdate();
	}
	
	static final String TAG = "ViewPager";
	
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int SNAP_VELOCITY = 600;

	private int mCurScreen;
	private int mTouchSlop;
	private int init = CommonGlobal.NO_DATA;
	private int mTouchState = TOUCH_STATE_REST;
	private boolean isFirstLayout = true;
	private float mLastMotionX, mLastMotionY;

	private MyPhoneViewPagerTab tab;
	private Scroller mScroller;
	/**
	 * 页面选中的回调监听
	 */
	private OnPageSelectedListenner onPageSelectedListenner;
	/**
	 * 达到最后一页时继续往前滑动的回调
	 */
	private OnTouchedLastPageListenner onTouchedLastPageListenner;
	private VelocityTracker mVelocityTracker;
	
	/**
	 * 子View是否支持横向滑动
	 */
	private boolean isHorizontalSlidingInChild = false;
	
	/**
	 * 子View是否循环滚动
	 */
	private boolean isChildEndlessScrolling = false;
	
	/**
	 * 支持横向滑动的子View总页数
	 */
	private int childViewTotalScreen = 0;
	
	/**
	 * 支持横向滑动的子View当前所在页
	 */
	private int childViewCurrentScreen = 0;
	/**
	 * 是否让子视图滑动。
	 */
	private boolean isChildScrolling = true;

	private ISlidingConflict isc;
	
	public void setSlidingParent(ISlidingConflict isc) {
		this.isc = isc;
	}

	public MyPhoneViewPager(Context context) {
		super(context);
		initWorkspace(context);
	}

	public MyPhoneViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWorkspace(context);
	}

	public MyPhoneViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWorkspace(context);
	}

	private void initWorkspace(Context ctx) {
		mScroller = new Scroller(ctx);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (isFirstLayout) {
        	if (init != CommonGlobal.NO_DATA && init >= 0 && init < getChildCount())
        		scrollTo(init * width, 0);
        	else
        		scrollTo((getChildCount() - 1) / 2 * width, 0);
        	
        	isFirstLayout = false;
        }
	}

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
            	final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
	}

	public void snapToDestination() {
		int screenWidth = getWidth();
		int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	/**
	 * 跳转到指定屏幕,带动画效果
	 * @param whichScreen
	 */
	public void snapToScreen(int whichScreen) {
		if (whichScreen >= getChildCount() && onTouchedLastPageListenner != null) {
			onTouchedLastPageListenner.onUpdate();
		}
			
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			int duration = Math.abs(delta) * 2;
			if (duration > 500)
				duration = 500;
			
			mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
			mCurScreen = whichScreen;
			invalidate();
		}
	}

	/**
	 * 跳转到指定屏幕,不带动画效果
	 * @param whichScreen
	 */
	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	/**
	 * 处理滑动动画
	 * @see android.view.View#computeScroll()
	 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			tab.scrollHighLight(mScroller.getCurrX());
			// 判断滑动是否停止
			if (mScroller.isFinished()) {
				// 停止时更新选中的Tab标签
				tab.updateSelected();
				if (onPageSelectedListenner != null)
					onPageSelectedListenner.onUpdate(mCurScreen);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
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
			mLastMotionX = x;
			mLastMotionY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
				
			mLastMotionX = x;
			mLastMotionY = y;
			if(isChildScrolling){
				scrollBy(deltaX, 0);
				tab.scrollHighLight(this.getScrollX());
			}
			break;

		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);

			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				if(isChildScrolling){
					snapToScreen(mCurScreen - 1);
				}
			} else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
				if(isChildScrolling){
					snapToScreen(mCurScreen + 1);
				}
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST) && !isHorizontalSlidingInChild) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			final int yDiff = (int) Math.abs(mLastMotionY - y);
			if (xDiff > mTouchSlop && yDiff < mTouchSlop && xDiff > yDiff) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			clearChildSlidingViewState();
			if (isc != null) {
				isc.setChildSlidingViewState(true, false, getChildCount(), mCurScreen);
			}
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		boolean b = mTouchState != TOUCH_STATE_REST;
		if (b) {
			final int xDiff = (int) (mLastMotionX - x); 
			if (isHorizontalSlidingInChild && Math.abs(xDiff) > mTouchSlop) {
				if (isChildEndlessScrolling) {
					return false;
				}				
				if (xDiff < 0 && childViewCurrentScreen == 0) {
					return true;
				} else if (xDiff > 0 && childViewCurrentScreen == childViewTotalScreen - 1) {
					return true;
				} else {
					return false;
				}
			}
		} 
		return b;
	}

	public void setTab(MyPhoneViewPagerTab tab) {
		this.tab = tab;
	}
	
	public void setInitTab(int tab) {
		this.init = tab;
		mCurScreen = tab;
	}
	
	/**
	 * 获得当前屏幕序号
	 */
	public int getCurrentScreen() {
		return mCurScreen;
	}

	/**
	 * 加入页面选中时的回调接口
	 * @param onPageSelectedListenner
	 */
	public void setOnPageSelectedListenner(OnPageSelectedListenner onPageSelectedListenner) {
		this.onPageSelectedListenner = onPageSelectedListenner;
	}
	
	/**
	 * 加入达到最后一页时继续往前滑动的回调接口
	 * @param onTouchedLastPageListenner
	 */
	public void setOnTouchedLastPageListenner(OnTouchedLastPageListenner onTouchedLastPageListenner) {
		this.onTouchedLastPageListenner = onTouchedLastPageListenner;
	}
	
	@Override
	public void setChildSlidingViewState(boolean isHorizontalSlidingInChild, boolean isChildEndlessScrolling, 
			int childViewTotalScreen, int childViewCurrentScreen) {
		if (childViewTotalScreen <= 0 || childViewCurrentScreen < 0 || childViewCurrentScreen >= childViewTotalScreen) {
			return;
		}
		this.isHorizontalSlidingInChild = isHorizontalSlidingInChild;
		this.isChildEndlessScrolling = isChildEndlessScrolling;
		this.childViewTotalScreen = childViewTotalScreen;
		this.childViewCurrentScreen = childViewCurrentScreen;
	}
	
	@Override
	public void clearChildSlidingViewState() {
		this.isHorizontalSlidingInChild = false;
		this.isChildEndlessScrolling = false;
		this.childViewTotalScreen = 0;
		this.childViewCurrentScreen = 0;
	}

	public void setChildScrolling(boolean isChildScrolling){
		this.isChildScrolling = isChildScrolling;
	}
}
