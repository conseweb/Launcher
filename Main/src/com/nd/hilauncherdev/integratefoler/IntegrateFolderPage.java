package com.nd.hilauncherdev.integratefoler;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.nd.hilauncherdev.launcher.DragController;
import com.nd.hilauncherdev.launcher.Launcher;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.info.FolderInfo;
import com.nd.hilauncherdev.launcher.support.BaseLauncherViewHelper;
import com.nd.hilauncherdev.launcher.touch.DragSource;
import com.nd.hilauncherdev.launcher.view.DragView;
import com.bitants.launcher.R;

public class IntegrateFolderPage extends ViewGroup {
	protected static final String TAG = "IntegrateFolderPage";
	private FolderInfo info;
	private IntegrateFolderGridView gridview;
	private PromotionLayout promotionLayout;
	private IntegrateFolder integrateFolder;
	private Launcher mLauncher;
	private Context context;
	private DragController mDragController;
	private List<ApplicationInfo> appInfoList;
	private OnLongClickListener longClickListener;
	private Scroller mScroller;

	public void setFolderInfo(FolderInfo info) {
		this.info = info;
	}

	public IntegrateFolderPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mScroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		mMaximumVelocity = ViewConfiguration.get(getContext())
				.getScaledMaximumFlingVelocity();
	}

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1; // 开始滑屏的状态
	private int mTouchState = TOUCH_STATE_REST; // 默认是什么都没做的状态

	private int SNAP_VELOCITY = 1000; // 最小的滑动速率
	private int mTouchSlop = 0; // 最小滑动距离，超过了，才认为开始滑动
	private float mLastionMotionY = 0; // 记住上次触摸屏的位置

	private float mBeginY = 0;
	// 处理触摸的速率
	private VelocityTracker mVelocityTracker = null;

	private int mMaximumVelocity;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int yDiff = (int) Math.abs(mLastionMotionY - y);
			// 超过了最小滑动距离，就可以认为开始滑动了
			if (yDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastionMotionY = y;
			mBeginY = y;
			Log.e(TAG, mScroller.isFinished() + "");
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		Log.e(TAG, mTouchState + "====" + TOUCH_STATE_REST);
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// 如果屏幕的动画还没结束，你就按下了，我们就结束上一次动画，即开始这次新ACTION_DOWN的动画
			if (mScroller != null) {
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
			}
			mLastionMotionY = y;
			mBeginY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// 加入阻尼
			int detaY = (int) (mLastionMotionY - y); // 每次滑动屏幕，屏幕应该移动的距离
			if (getScrollY() < 0) {
				float s = Math.abs(y - mBeginY) / (getHeight());
				Log.e("zhenghonglin", "---s==" + s);
				if (s > 1) {
					s = 1.0f;
				}
				int scrollToY = (int) (Math.sin(Math.PI / 2 * (s))
						* getHeight() * 0.25f);
				scrollTo(0, -scrollToY);
			} else if (getScrollY() > (totalHeight - getHeight())) {
				float s = Math.abs(y - mBeginY) / (getHeight());
				Log.e("zhenghonglin", "---s==" + s);
				if (s > 1) {
					s = 1.0f;
				}
				int scrollToY = (int) (Math.sin(Math.PI / 2 * (s))
						* getHeight() * 0.25f);
				scrollTo(0, totalHeight - getHeight() + scrollToY);
			} else {
				Log.e("zhenghonglin", "---detaY==" + detaY + "," + getScrollY());
				scrollBy(0, detaY);
			}
			mLastionMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			int velocityY = (int) velocityTracker.getYVelocity();
			startScroll(velocityY);
			// 回收VelocityTracker对象
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// 修正mTouchState值
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		default:
			break;
		}
		return true;
	}

	private void startScroll(int velocityY) {
		if (velocityY > SNAP_VELOCITY && getScrollY() > 0) {
			scrollUp(velocityY);
		} else if (velocityY < -SNAP_VELOCITY
				&& getScrollY() < (totalHeight - getHeight())) {
			scrollDown(velocityY);
		} else {
			scrollToDestination(velocityY);
		}
	}

	private void scrollToDestination(int velocity) {
		// 要有反弹效果
		Log.e(TAG, "snapToDestination===:" + velocity + "," + getScrollY());
//		if (getScrollY() < 0) {
//			int dy = 0 - getScrollY();
//			mScroller.startScroll(0, getScrollY(), 0, dy);
//		} else if (getScrollY() > (totalHeight - getHeight())) {
//			int dy = totalHeight - getHeight() - getScrollY();
//			mScroller.startScroll(0, getScrollY(), 0, dy);
//		}
//		invalidate();
		if(velocity <0 ){
			scrollDown(velocity);
		} else{
			scrollUp(velocity);
		}
	}

	/**
	 * 向下滚动 显示下面的内容 velocity为负
	 * 
	 * @param velocity
	 */
	private void scrollDown(int velocity) {
		float dy = Math.abs((totalHeight - getHeight()) * velocity * 2.0f
				/ mMaximumVelocity);
		Log.e("clarkzheng", dy + ",===" + velocity);
		if (getScrollY() + dy > (totalHeight - getHeight())) {
			dy = totalHeight - getHeight() - getScrollY();
			Log.e("clarkzheng", dy + ",111===");
		}
		int duration = (int) ((float) Math.abs(dy) / getHeight() * 1500);
		Log.e("clarkzheng", duration + ",111===");
		// mScroller.startScroll(0, getScrollY(), 0, (int)dy, duration);
		mScroller.startScroll(0, getScrollY(), 0, (int) dy);
		invalidate();
	}

	/**
	 * 向上滚动 显示上面的内容 velocity为正
	 * 
	 * @param velocity
	 */
	private void scrollUp(int velocity) {
		float dy = Math.abs((totalHeight - getHeight()) * velocity * 2.0f
				/ mMaximumVelocity);
		Log.e("clarkzheng", dy + ",snapDown===" + velocity);
		if (getScrollY() - dy < 0) {
			dy = getScrollY();
			Log.e("clarkzheng", dy + ",111=snapDown==");
		}
		int duration = (int) ((float) Math.abs(dy) / getHeight() * 1500);
		Log.e("clarkzheng", duration + ",111=snapDown==");
		// mScroller.startScroll(0, getScrollY(), 0, -(int)dy, duration);
		mScroller.startScroll(0, getScrollY(), 0, -(int) dy);
		// 由于触摸事件不会重新绘制View，所以此时需要手动刷新View 否则没效果
		invalidate();
	}

	@Override
	public void computeScroll() {
		Log.e(TAG, "computeScroll");
		if (mScroller.computeScrollOffset()) {
			Log.e(TAG, mScroller.getCurrX() + "======" + mScroller.getCurrY());
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mTouchState == TOUCH_STATE_REST) {
//			if (getScrollY() < 0) {
//				mScroller.startScroll(0, getScrollY(), 0, 0 - getScrollY());
//				postInvalidate();
//			}
		} else {
			Log.e(TAG, "have done the scoller -----");
		}

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		gridview = (IntegrateFolderGridView) findViewById(R.id.folder_content_grid);
		promotionLayout = PromotionLayout.fromXml(context, this, null);
		addView(promotionLayout);
	}

	private int totalHeight;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		gridview.layout(0, 0, gridview.getMeasuredWidth(),
				gridview.getMeasuredHeight());
		int top = gridview.getMeasuredHeight() + 100;
		promotionLayout.layout(0, top, promotionLayout.getMeasuredWidth(), top
				+ promotionLayout.getMeasuredHeight());
		totalHeight = top + promotionLayout.getMeasuredHeight();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public int measureWidth(int widthMeasureSpec) {
		int result = 0;
		int measureMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		switch (measureMode) {
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			result = width;
			break;
		default:
			break;
		}
		return result;
	}

	public int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int measureMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		switch (measureMode) {
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			result = height;
			break;
		default:
			break;
		}
		return result;
	}

	public static IntegrateFolderPage fromXml(Launcher launcher,
			ViewGroup viewGroup, IntegrateFolder integrateFolder,
			List<ApplicationInfo> appList) {
		// 进行初始化工作 相当于构造函数 这里进行一些初始化工作
		IntegrateFolderPage integrateFolderPage = (IntegrateFolderPage) LayoutInflater
				.from(launcher).inflate(R.layout.user_folder_integrate_page,
						viewGroup, false);
		integrateFolderPage.mLauncher = launcher;
		integrateFolderPage.setDragController((DragController) launcher
				.getDragController());
		integrateFolderPage.setOnLongClickListener(integrateFolder);
		integrateFolderPage.integrateFolder = integrateFolder;
		integrateFolderPage.initData(appList);
		return integrateFolderPage;
	}

	public void setOnLongClickListener(OnLongClickListener l) {
		longClickListener = l;
		gridview.setItemOnLongClickListener(longClickListener);
	}

	private void initData(List<ApplicationInfo> appList) {
		appInfoList = appList;
		FolderGridViewAdapter adapter = new FolderGridViewAdapter(mLauncher,
				appInfoList, 4);
		gridview.setAdapter(adapter);
		// gridview.setAdapter(new DynamicGridAdapterInterface() {
		//
		// public int itemCount() {
		// return appInfoList.size();
		// }
		//
		// public View view(int index) {
		// return BaseLauncherViewHelper.createCommonAppView(mLauncher,
		// appInfoList.get(index));
		// }
		//
		// public int getColumnCount() {
		// return 4;
		// }
		//
		// public void deleteItem(int itemIndex) {
		//
		// }
		//
		// public Object getItemAt(int index) {
		// return appInfoList.get(index);
		// }
		// });
	}

	public void setDragController(DragController mDragController) {
		this.mDragController = mDragController;
		gridview.setDragController(mDragController);
	}

	class FolderGridViewAdapter extends AbstractFolderGridAdapter {

		protected FolderGridViewAdapter(Context context, int columnCount) {
			super(context, columnCount);
		}

		public FolderGridViewAdapter(Context context, List<?> items,
				int columnCount) {
			super(context, items, columnCount);
		}

		@Override
		public View getView(int position) {
			View view = BaseLauncherViewHelper.createCommonAppView(mLauncher,
					(ApplicationInfo) getItem(position));
			view.setTag(R.string.app_name,
					((ApplicationInfo) getItem(position)).title);
			Log.e("NewTest1", "getView :position:" + position + ","
					+ ((ApplicationInfo) getItem(position)).title);
			return view;
		}
	}

	public void startDrag(View v, DragSource dragSource) {
		gridview.startDrag(v, dragSource);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		gridview.onDragOver(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		gridview.onDragEnter(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	public void onDropCompleted(View target, boolean success) {
		gridview.onDropCompleted(target, success);
	}
}
