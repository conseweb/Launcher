package com.bitants.launcherdev.integratefoler;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;
import com.bitants.launcher.R;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class IntegrateFolderGridView extends ViewGroup implements DragSource, DropTarget {

	private final String TAG = "IntegrateFolderGridView";

	private final int INVALID_ID = -1;

	private View.OnLongClickListener mOnLongClickListener;

	private AbstractFolderGridAdapter adapter;

	private int columnCount;

	private int itemWidth;

	private int itemHeight;

	/**
	 * HoverCell 就是拖动起来的View 的当前边界坐标
	 */
	private Rect mHoverCellCurrentBounds;
	/**
	 * HoverCell 就是拖动起来的View 的最初边界坐标
	 */
	private Rect mHoverCellOriginalBounds;

	/**
	 * 从移动开始偏移的y总值
	 */
	private int mTotalOffsetY = 0;
	/**
	 * 从移动开始偏移的x总值
	 */
	private int mTotalOffsetX = 0;

	/**
	 * 手指点击下的坐标 x坐标 相对于父类view
	 */
	private int mDownX = -1;
	/**
	 * 手指点击下的坐标 y坐标 相对于父类view
	 */
	private int mDownY = -1;
	/**
	 * 移动后的y 相对于父类view
	 */
	private int mLastEventY = -1;
	/**
	 * 移动后的x 相对于父类view
	 */
	private int mLastEventX = -1;

	/**
	 * cell是否在移动 长按的view不可见的时候 设置view正在移动为true
	 */
	private boolean mCellIsMobile = false;

	/**
	 * 记录可见view的itemId集合 除了拖动起来的view
	 */
	private List<Long> idList = new ArrayList<Long>();

	/**
	 * 记录Adapter中的View的getItemId 移动的view的id
	 */
	private long mMobileItemId = INVALID_ID;

	private int mActivePointerId = INVALID_ID;

	private boolean mIsEditMode = false;

	private boolean mIsEditModeEnabled = true;

	private List<View> mViews = new ArrayList<View>();

	/**
	 * View和ItemId的map
	 */
	private HashMap<Long, View> mViewAndIdMap = new HashMap<Long, View>();

	private FolderGridObserver mObserver;

	/**
	 * 拖动控制器
	 */
	private DragController mDragController;
	
	private Context context;
	public IntegrateFolderGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		scroller = new Scroller(context);
	}

	public IntegrateFolderGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		scroller = new Scroller(context);
	}

	public IntegrateFolderGridView(Context context) {
		super(context);
		this.context = context;
		scroller = new Scroller(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (widthMode == MeasureSpec.UNSPECIFIED) {
			throw new IllegalArgumentException("widthMode.UNSPECIFIED");
		}
		if (heightMode == MeasureSpec.UNSPECIFIED) {
			throw new IllegalArgumentException("heightMode.UNSPECIFIED");
		}
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		columnCount = adapter.getColumnCount() <= 0 ? 1 : adapter.getColumnCount();
		itemWidth = widthSize / columnCount;
		itemHeight = itemWidth;
		int rowCount = adapter.getCount()%columnCount == 0 ?adapter.getCount()/columnCount:adapter.getCount()/columnCount+1;
		int heightSize = rowCount*itemHeight;
		// 测量内部view的宽高
		measureChildren(MeasureSpec.makeMeasureSpec((int) (itemWidth * 0.8f), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec((int) (itemHeight * 0.8f), MeasureSpec.AT_MOST));
		// 设置当前视图大小
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int col = 0;
		int row = 0;
		for (int position = 0; position < adapter.getCount(); position++) {
			// int position = positionOfItem(childIndex);
			// View child = getViewForId(getId(childIndex));
			View child = mViews.get(position);
			int left = 0;
			int top = 0;
			left = (col * itemWidth) + ((itemWidth - child.getMeasuredWidth()) / 2);
			top = (row * itemHeight) + ((itemHeight - child.getMeasuredHeight()) / 2);
			child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
			col++;
			if (col == columnCount) {
				col = 0;
				row++;
			}
		}
	}
	
	public void setAdapter(AbstractFolderGridAdapter adapter) {
		if (this.adapter != null) {
			this.adapter.unregisterDataSetObserver(mObserver);
		}
		this.adapter = adapter;
		if (this.adapter != null) {
			if (mObserver == null) {
				mObserver = new FolderGridObserver();
			}
			this.adapter.registerDataSetObserver(mObserver);
		}
		addChildViews();
	}

	private class FolderGridObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			onDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			onDataSetChanged();
		}
	}

	private void addChildViews() {
		Log.e(TAG, "添加视图View===========");
		if (adapter == null) {
			return;
		}
		for (int position = 0; position < adapter.getCount(); position++) {
			View view = adapter.getView(position);
			view.setTag(adapter.getItem(position));
			view.setOnLongClickListener(mOnLongClickListener);
			mViewAndIdMap.put(adapter.getItemId(position), view);
			mViews.add(view);
			removeView(view);
			addView(view);
		}
	}

	/**
	 * 数据改变的回掉
	 */
	public void onDataSetChanged() {
		Log.e(TAG, "数据改变的回掉=====");
		if (adapter == null) {
			return;
		}
		List<View> orderedViews = new ArrayList<View>();
		for (int position = 0; position < adapter.getCount(); position++) {
			// long itemId =
			// adapter.getItemId(adapter.getItems().get(position));
			long itemId = adapter.getItemId(position);
			orderedViews.add(mViewAndIdMap.get(itemId));
		}

		mViews.clear();
		for (View view : orderedViews) {
			if (view != null) {
				removeView(view);
				addView(view);
				mViews.add(view);
			}
		}
		// requestLayout();
	}

	public void setItemOnLongClickListener(View.OnLongClickListener listener) {
		this.mOnLongClickListener = listener;
	}

	private int getColumnCount() {
		return getAdapter().getColumnCount();
	}
	
	private int getCount(){
		return getAdapter().getCount();
	}

	/**
	 * 根据position取Adapter的itemId
	 * @param position
	 * @return
	 */
	private long getId(int position) {
		return getAdapter().getItemId(position);
	}

	
	public View getViewForId(long itemId) {
		if (adapter == null) {
			return null;
		}
		for (int position = 0; position < adapter.getCount(); position++) {
			View v = mViews.get(position);
			long id = adapter.getItemId(position);
			if (id == itemId) {
				return v;
			}
		}
		return null;
	}

	public int getPositionForID(long itemId) {
		View v = getViewForId(itemId);
		if (v == null) {
			return -1;
		} else {
			return getPositionForView(v);
		}
	}

	public int getPositionForView(View view) {
		return mViews.indexOf(view);
	}

	private Point getColumnAndRowForView(View view) {
		int pos = getPositionForView(view);
		int columns = getColumnCount();
		int column = pos % columns;
		int row = pos / columns;
		return new Point(column, row);
	}

	private DynamicGridAdapterInterface getAdapter() {
		return adapter;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = super.onInterceptTouchEvent(ev);
		return result;
		// final int action = ev.getAction();
		// if ((action == MotionEvent.ACTION_MOVE)) {
		// Log.e(TAG,"onInterceptTouchEvent+"+ev.getAction() +","+true);
		// return true;
		// }
		// Log.e(TAG,"onInterceptTouchEvent+"+ev.getAction() +","+result);
		// return true;
	}

	
	private Scroller scroller; 
	private VelocityTracker velocityTracker;
	private float mLastionMotionY = 0 ;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		float y = event.getY();
//		if(velocityTracker == null){
//			velocityTracker = VelocityTracker.obtain();
//		}
//		velocityTracker.addMovement(event);
//		int action = event.getAction();
//		switch(action){
//		case MotionEvent.ACTION_DOWN:
//			mLastionMotionY = y;
//			break;
//		case MotionEvent.ACTION_MOVE:
//			int detalY = (int) (mLastionMotionY-y);
//			int scrollY = getScrollY();
//			if (detalY < 0 && scrollY + detalY < 0) {
//				detalY = 0 - scrollY; 
//			} else {
//				detalY = getHeight() - scrollY;
//			}
//			scrollBy(0, detalY);
//			mLastionMotionY = y;
//			break;
//		case MotionEvent.ACTION_UP:
//			break;
//		default:
//			break;	
//		}
//		return true;
		return super.onTouchEvent(event);
	}
	
	//此处的moveBy是根据水平或是垂直排放的方向，
	//来选择是水平移动还是垂直移动
	public void moveBy(int deltaX, int deltaY) {
        if (Math.abs(deltaY) >= Math.abs(0))
            scrollBy(0, deltaY);
	}

	private void touchEventsEnded() {
		final View mobileView = getViewForId(mMobileItemId);
		if (mobileView != null && (mCellIsMobile /* || mIsWaitingForScrollFinish */)) {
			mCellIsMobile = false;
			// mIsWaitingForScrollFinish = false;
			// mIsMobileScrolling = false;
			mActivePointerId = INVALID_ID;

			// If the autoscroller has not completed scrolling, we need to wait
			// for it to
			// finish in order to determine the final location of where the
			// hover cell
			// should be animated to.
			// if (mScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
			// mIsWaitingForScrollFinish = true;
			// return;
			// }

			mHoverCellCurrentBounds.offsetTo(mobileView.getLeft(), mobileView.getTop());

			// if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			// animateBounds(mobileView);
			// } else {
			// mHoverCell.setBounds(mHoverCellCurrentBounds);
			// invalidate();
			// reset(mobileView);
			// }
			invalidate();
			reset(mobileView);
		} else {
			touchEventsCancelled();
		}
	}

	private void touchEventsCancelled() {
		View mobileView = getViewForId(mMobileItemId);
		if (mCellIsMobile) {
			reset(mobileView);
		}
		mCellIsMobile = false;
		// mIsMobileScrolling = false;
		mActivePointerId = INVALID_ID;

	}

	private void reset(View mobileView) {
		idList.clear();
		mMobileItemId = INVALID_ID;
		mobileView.setVisibility(View.VISIBLE);
		// mHoverCell = null;
		// ugly fix for unclear disappearing items after reorder
		for (int i = 0; i < getAdapter().getCount(); i++) {
			View child = getChildAt(i);
			if (child != null) {
				child.setVisibility(View.VISIBLE);
			}
		}
		invalidate();
	}

	public void startEditMode(int position) {
		Log.e(TAG, "startEditMode===" + position);
		if (!mIsEditModeEnabled)
			return;
		Log.e(TAG, "startEditMode===");
		/**
		 * AbsListView方法 阻拦父层的View截获touch事务 之后的事件直接传递给DynamicGrid的onTouchEvent
		 */
		// requestDisallowInterceptTouchEvent(true);
		if (position != -1) {
			startDragAtPosition(position);
		}
		mIsEditMode = true;
		// if (mEditModeChangeListener != null)
		// mEditModeChangeListener.onEditModeChanged(true);
	}

	/**
	 * 开始拖动指定position的View
	 * 
	 * @param position
	 */
	private void startDragAtPosition(int position) {
		mTotalOffsetY = 0;
		mTotalOffsetX = 0;
		// 得到position对应的selectedView
		int itemNum = position;
		View selectedView = getChildAt(itemNum);
		if (selectedView != null) {
			mMobileItemId = getAdapter().getItemId(position);
			// if (mSelectedItemBitmapCreationListener != null)
			// mSelectedItemBitmapCreationListener.onPreSelectedItemBitmapCreation(selectedView,
			// position, mMobileItemId);

			getAndAddHoverView(selectedView);

			// if (mSelectedItemBitmapCreationListener != null)
			// mSelectedItemBitmapCreationListener.onPostSelectedItemBitmapCreation(selectedView,
			// position, mMobileItemId);
			// 将拖动的view 设置为隐藏占用位置 这里isPostHoneycomb（） 有待测试
			// if (isPostHoneycomb())
			selectedView.setVisibility(View.INVISIBLE);
			// 长按的view不可见的时候 设置view正在移动为true
			mCellIsMobile = true;
			updateNeighborViewsForId(mMobileItemId);
			// if (mDragListener != null) {
			// mDragListener.onDragStarted(position);
			// }
		}
	}

	/**
	 * Creates the hover cell with the appropriate bitmap and of appropriate
	 * size. The hover cell's BitmapDrawable is drawn on top of the bitmap every
	 * single time an invalidate call is made. 根据v生成BitmapDrawable
	 */
	private void getAndAddHoverView(View v) {

		int w = v.getWidth();
		int h = v.getHeight();
		int top = v.getTop();
		int left = v.getLeft();
		mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
		mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);
	}

	/**
	 * 根据itemId 更新相邻view 就是把可见view 的itemId加到idList中去 idList 不包含当前拖动的view
	 * 
	 * @param itemId
	 */
	private void updateNeighborViewsForId(long itemId) {
		idList.clear();
		int draggedPos = getPositionForID(itemId);
		for (int pos = 0; pos < getAdapter().getCount(); pos++) {
			if (draggedPos != pos /* && getAdapterInterface().canReorder(pos) */) {
				idList.add(getId(pos));
			}
		}
	}

	public void setDragController(DragController mDragController) {
		this.mDragController = mDragController;
	}

	public void startDrag(View v, DragSource dragSource) {
		startEditMode(getPositionForView(v));
		mDragController.startDrag(v, dragSource);
	}

	@Override
	public int getState() {
		return 0;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		mDownX = (int) x;
		mDownY = (int) y;
	}

	/**
	 * x y 是开始拖动的时候 是相对与getLocationOnScreen 屏幕的位置 拖动过程中是 target
	 * 相对与getLocationOnScreen 屏幕的位置 按效果 x y 是拖动view的相对左上角的坐标 xOffset yOffset
	 * 是手指点击位置距离该view左上角的偏移
	 */
	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		// 拖动过程中不停调用
		if (mCellIsMobile) {
			// mLastEventY = (int) event.getY(pointerIndex);
			// mLastEventX = (int) event.getX(pointerIndex);
			mLastEventY = y;
			mLastEventX = x;
			int deltaY = mLastEventY - mDownY;
			int deltaX = mLastEventX - mDownX;

			mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left + deltaX + mTotalOffsetX, mHoverCellOriginalBounds.top + deltaY + mTotalOffsetY);
			// mHoverCell.setBounds(mHoverCellCurrentBounds);
			handleCellSwitch();
		}
	}

	private View mMobileView;

	// used to distinguish straight line and diagonal switching
	private int mOverlapIfSwitchStraightLine = 0;

	public static final int INVALID_POSITION = -1;

	private static final int MOVE_DURATION = 100;

	private boolean mReorderAnimation;

	private boolean mHoverAnimation;

	/**
	 * 交换动画 移动过程会一直调用
	 */
	private void handleCellSwitch() {
		final int deltaY = mLastEventY - mDownY;
		final int deltaX = mLastEventX - mDownX;
		Log.e("NewTest", "handleCellSwitch:" + mDownX + "," + mDownY + "," + mLastEventX + "," + mLastEventY);
		final int deltaYTotal = mHoverCellOriginalBounds.centerY() + mTotalOffsetY + deltaY;
		final int deltaXTotal = mHoverCellOriginalBounds.centerX() + mTotalOffsetX + deltaX;
		mMobileView = getViewForId(mMobileItemId);
		View targetView = null;
		float vX = 0;
		float vY = 0;
		Point mobileColumnRowPair = getColumnAndRowForView(mMobileView);
		// 找目标view 只找一个view
		for (Long id : idList) {
			View view = getViewForId(id);
			if (view != null) {
				Point targetColumnRowPair = getColumnAndRowForView(view);
				if ((aboveRight(targetColumnRowPair, mobileColumnRowPair) && deltaYTotal < view.getBottom() && deltaXTotal > view.getLeft() || aboveLeft(targetColumnRowPair, mobileColumnRowPair)
						&& deltaYTotal < view.getBottom() && deltaXTotal < view.getRight() || belowRight(targetColumnRowPair, mobileColumnRowPair) && deltaYTotal > view.getTop()
						&& deltaXTotal > view.getLeft() || belowLeft(targetColumnRowPair, mobileColumnRowPair) && deltaYTotal > view.getTop() && deltaXTotal < view.getRight()
						|| above(targetColumnRowPair, mobileColumnRowPair) && deltaYTotal < view.getBottom() - mOverlapIfSwitchStraightLine || below(targetColumnRowPair, mobileColumnRowPair)
						&& deltaYTotal > view.getTop() + mOverlapIfSwitchStraightLine || right(targetColumnRowPair, mobileColumnRowPair) && deltaXTotal > view.getLeft() + mOverlapIfSwitchStraightLine || left(
						targetColumnRowPair, mobileColumnRowPair) && deltaXTotal < view.getRight() - mOverlapIfSwitchStraightLine)) {
					// 取宽度 高度相减 一般都为0
					float xDiff = Math.abs(DynamicGridUtils.getViewX(view) - DynamicGridUtils.getViewX(mMobileView));
					float yDiff = Math.abs(DynamicGridUtils.getViewY(view) - DynamicGridUtils.getViewY(mMobileView));
					Log.e("zhenghonglin", view.getLeft() + "," + view.getTop() + "," + view.getRight() + "," + view.getBottom() + ",");
					Log.e("zhenghonglin", targetColumnRowPair + "," + mobileColumnRowPair + "," + xDiff + "," + yDiff);
					if (xDiff >= vX && yDiff >= vY) {
						vX = xDiff;
						vY = yDiff;
						targetView = view;
						Log.e("zhenghonglin", getPositionForView(targetView) + "," + getPositionForView(mMobileView));

					}
				}
			}
		}
		if (targetView != null) {
			final int originalPosition = getPositionForView(mMobileView);
			int targetPosition = getPositionForView(targetView);
			final DynamicGridAdapterInterface adapter = getAdapter();
			if (targetPosition == INVALID_POSITION /*
													 * || !adapter.canReorder(
													 * originalPosition) ||
													 * !adapter
													 * .canReorder(targetPosition
													 * )
													 */) {
				updateNeighborViewsForId(mMobileItemId);
				return;
			}
			// 交换list中的数据
			Log.e(TAG, "reorderElements+============" + originalPosition + "," + targetPosition);
			reorderElements(originalPosition, targetPosition);
			/**
			 * 移动后 如果有找到新的位置 mDownX mDownY更新
			 */
			mDownY = mLastEventY;
			mDownX = mLastEventX;

			SwitchCellAnimator switchCellAnimator;

			// if (isPostHoneycomb() && isPreLollipop()) //Between Android 3.0
			// and Android L
			// switchCellAnimator = new KitKatSwitchCellAnimator(deltaX,
			// deltaY);
			// else if (isPreLollipop()) //Before Android 3.0
			// switchCellAnimator = new PreHoneycombCellAnimator(deltaX,
			// deltaY);
			// else //Android L
			// switchCellAnimator = new LSwitchCellAnimator(deltaX, deltaY);

			switchCellAnimator = new LSwitchCellAnimator(deltaX, deltaY);
			updateNeighborViewsForId(mMobileItemId);
			// 最后执行动画
			switchCellAnimator.animateSwitchCell(originalPosition, targetPosition);
		}
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		Log.e(TAG, "requestLayout+============");
		// printInfo();
	}

	private void printInfo() {
		if (getAdapter() == null || getAdapter().getCount() != 3) {
			return;
		}
		for (int pos = 0; pos < getAdapter().getCount(); pos++) {
			View view = getViewForId(getId(pos));
			String title = null;
			if (view == null) {
				title = "null=";
			} else {
				title = (String) view.getTag(R.string.app_name);
			}
			ApplicationInfo itemInfo = (ApplicationInfo) getAdapter().getItem(pos);
			Log.e("supertest", "pos:" + pos + ",id:" + getId(pos) + ",title:" + title + ",title1:" + itemInfo.title);
		}
		Log.e("supertest", "===============================");
		DynamicGridAdapterInterface adapter = getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			View v = getChildAt(i);
			if (v == null) {
				Log.e("supertest", "i:" + i + ",null");
			} else {
				ApplicationInfo itemInfo = (ApplicationInfo) v.getTag();
				Log.e("supertest", "i:" + i + ",id:" + getId(i) + ",title:" + itemInfo.title);
			}

		}
		Log.e("supertest", "===============================");
	}

	private interface SwitchCellAnimator {
		void animateSwitchCell(final int originalPosition, final int targetPosition);
	}

	private class PreHoneycombCellAnimator implements SwitchCellAnimator {
		private int mDeltaY;
		private int mDeltaX;

		public PreHoneycombCellAnimator(int deltaX, int deltaY) {
			mDeltaX = deltaX;
			mDeltaY = deltaY;
		}

		public void animateSwitchCell(int originalPosition, int targetPosition) {
			mTotalOffsetY += mDeltaY;
			mTotalOffsetX += mDeltaX;
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
//		GpuControler.enableHardwareLayers(this);
	}

	/**
	 * A {@link org.askerov.dynamicgrid.DynamicGridView.SwitchCellAnimator} for
	 * versions KitKat and below.
	 */
	private class KitKatSwitchCellAnimator implements SwitchCellAnimator {

		private int mDeltaY;
		private int mDeltaX;

		public KitKatSwitchCellAnimator(int deltaX, int deltaY) {
			mDeltaX = deltaX;
			mDeltaY = deltaY;
		}

		public void animateSwitchCell(final int originalPosition, final int targetPosition) {
			assert mMobileView != null;
			getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(mMobileView, originalPosition, targetPosition));
			mMobileView = getViewForId(mMobileItemId);
		}

		private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

			private final View mPreviousMobileView;
			private final int mOriginalPosition;
			private final int mTargetPosition;

			AnimateSwitchViewOnPreDrawListener(final View previousMobileView, final int originalPosition, final int targetPosition) {
				mPreviousMobileView = previousMobileView;
				mOriginalPosition = originalPosition;
				mTargetPosition = targetPosition;
			}

			public boolean onPreDraw() {
				getViewTreeObserver().removeOnPreDrawListener(this);

				mTotalOffsetY += mDeltaY;
				mTotalOffsetX += mDeltaX;

				animateReorder(mOriginalPosition, mTargetPosition);

				mPreviousMobileView.setVisibility(View.VISIBLE);

				if (mMobileView != null) {
					mMobileView.setVisibility(View.INVISIBLE);
				}
				return true;
			}
		}
	}

	private void animateReorder(final int oldPosition, final int newPosition) {
		boolean isForward = newPosition > oldPosition;
		List<Animator> resultList = new LinkedList<Animator>();
		if (isForward) {
			printInfo();
			for (int pos = Math.min(oldPosition, newPosition); pos < Math.max(oldPosition, newPosition); pos++) {
				View view = getViewForId(getId(pos));
				if ((pos + 1) % getColumnCount() == 0) {
					resultList.add(createTranslationAnimations(view, -view.getWidth() * (getColumnCount() - 1), 0, view.getHeight(), 0));
				} else {
					resultList.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
				}
			}
		} else {
			for (int pos = Math.max(oldPosition, newPosition); pos > Math.min(oldPosition, newPosition); pos--) {
				View view = getViewForId(getId(pos));
				if ((pos + getColumnCount()) % getColumnCount() == 0) {
					resultList.add(createTranslationAnimations(view, view.getWidth() * (getColumnCount() - 1), 0, -view.getHeight(), 0));
				} else {
					resultList.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
				}
			}
		}

		AnimatorSet resultSet = new AnimatorSet();
		resultSet.playTogether(resultList);
		resultSet.setDuration(MOVE_DURATION);
		resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
		resultSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mReorderAnimation = true;
				updateEnableState();
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mReorderAnimation = false;
				updateEnableState();
			}
		});
		resultSet.start();
	}

	private void updateEnableState() {
		setEnabled(!mHoverAnimation && !mReorderAnimation);
	}

	private AnimatorSet createTranslationAnimations(View view, float startX, float endX, float startY, float endY) {
		ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
		ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playTogether(animX, animY);
		return animSetXY;
	}

	/**
	 * A {@link org.askerov.dynamicgrid.DynamicGridView.SwitchCellAnimator} for
	 * versions L and above.
	 */
	private class LSwitchCellAnimator implements SwitchCellAnimator {

		private int mDeltaY;
		private int mDeltaX;

		public LSwitchCellAnimator(int deltaX, int deltaY) {
			mDeltaX = deltaX;
			mDeltaY = deltaY;
		}

		public void animateSwitchCell(final int originalPosition, final int targetPosition) {
			getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(originalPosition, targetPosition));
		}

		private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
			private final int mOriginalPosition;
			private final int mTargetPosition;

			AnimateSwitchViewOnPreDrawListener(final int originalPosition, final int targetPosition) {
				mOriginalPosition = originalPosition;
				mTargetPosition = targetPosition;
			}

			public boolean onPreDraw() {
				getViewTreeObserver().removeOnPreDrawListener(this);

				mTotalOffsetY += mDeltaY;
				mTotalOffsetX += mDeltaX;

				animateReorder(mOriginalPosition, mTargetPosition);

				assert mMobileView != null;
				mMobileView.setVisibility(View.VISIBLE);
				mMobileView = getViewForId(mMobileItemId);
				assert mMobileView != null;
				mMobileView.setVisibility(View.INVISIBLE);
				return true;
			}
		}
	}

	private void reorderElements(int originalPosition, int targetPosition) {
		// if (mDragListener != null)
		// mDragListener.onDragPositionsChanged(originalPosition,
		// targetPosition);
		Log.e("NewTest", "reorderElements:" + originalPosition + "," + targetPosition);
		getAdapter().reorderItems(originalPosition, targetPosition);
	}

	private boolean belowLeft(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
	}

	private boolean belowRight(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
	}

	private boolean aboveLeft(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
	}

	private boolean aboveRight(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
	}

	private boolean above(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x == mobileColumnRowPair.x;
	}

	private boolean below(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x == mobileColumnRowPair.x;
	}

	private boolean right(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y == mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
	}

	private boolean left(Point targetColumnRowPair, Point mobileColumnRowPair) {
		return targetColumnRowPair.y == mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		Log.e(TAG, "onDropCompleted:" + success);
		if (success) {
			touchEventsEnded();
		}
	}

}
