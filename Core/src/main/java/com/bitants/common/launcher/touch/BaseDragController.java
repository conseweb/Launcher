package com.bitants.common.launcher.touch;

import java.util.ArrayList;

import com.bitants.common.framework.OnKeyDownListenner;
import com.bitants.common.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.DragLayer;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.view.BaseShortcutMenu;
import com.bitants.common.kitset.GpuControler;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.screens.preview.PreviewEditAdvancedController;
import com.bitants.common.launcher.view.DragView;
import com.bitants.common.R;
import com.bitants.common.launcher.screens.DeleteZone;
import com.bitants.common.launcher.screens.preview.PreviewCellView;
import com.bitants.common.launcher.screens.preview.PreviewWorkspace;
import com.bitants.common.launcher.support.BaseCellLayoutHelper;
import com.bitants.common.launcher.view.BaseDeleteZoneTextView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseDragController implements DragLayerEventHandler, OnKeyDownListenner {
	static final String TAG = "BaseDragController";
	protected Context mContext;
	protected Handler handler = new Handler();
	protected DragView mDragView;
	
	/** X offset from the upper-left corner of the cell to where we touched. */
	protected float mTouchOffsetX;

	/** Y offset from the upper-left corner of the cell to where we touched. */
	protected float mTouchOffsetY;
	
	/** X coordinate of the down event. */
	protected float mMotionDownX;

	/** Y coordinate of the down event. */
	protected float mMotionDownY;
	
	protected boolean mDragging;
	protected View mMoveTarget;
	/**
	 * 是否处于拖动可响应状态
	 */
	private boolean isInAction = false;
	protected View mOriginator;
	protected final int[] mCoordinatesTemp = new int[2];
	
	/**
	 * 拖动层
	 */
	protected DragLayer mDragLayer;

	protected ScreenViewGroup mWorkspace;
	
	protected Paint mOriginPaint;// dragview的原始画笔
	
	private Vibrator mVibrator;
	private static final int VIBRATE_DURATION = 35;
	
	protected final Paint mTrashPaint = new Paint();
	
	private InputMethodManager mInputMethodManager;
	protected IBinder mWindowToken;
	
	/** Where the drag originated */
	protected DragSource mDragSource;

	/** The data associated with the object being dragged */
	protected Object mDragInfo;
	
	protected int scroll_zone = 20;
	protected int springScroll_zone = scroll_zone;
	/**
	 * 是否可用格子，可用则画出绿色边框
	 */
	protected boolean isAvaiableCell = false;
	
	/**
	 * 甩动图标到卸载删除区域
	 */
	private int mFlingToDeleteThresholdVelocity;
	private static final float MAX_FLING_DEGREES = 35f;
	private int mActionDragOverMaxVelocity;//拖动速度快于该值时，不响应onDragOver
	
	protected float mLastMotionX;
	protected float mLastMotionY;
	private float xyDiffRate;
	private static final int SNAP_VELOCITY = 600;
	/**
	 * 第二个手指down时x坐标
	 */
	private float point1DownX = -1;
	/**
	 * 第二个手指down时的时间
	 */
	private float point1DownTime = -1;
	
	private DisplayMetrics mDisplayMetrics = new DisplayMetrics();
	private VelocityTracker mVelocityTracker;
	private DropTarget mLastDropTarget;
	/**
	 * 最近一次手指在屏幕上的坐标
	 */
	private float lastMoveX, lastMoveY;
	
	private BaseDeleteZoneTextView mDeleteZoneTextView;
	private BaseDeleteZoneTextView mUninstallZoneTextView;
	
	/**
	 * 屏幕滚动
	 */
	private static final int SCROLL_DELAY = 600;
	private static final int SCROLL_OUTSIDE_ZONE = 0;
	private static final int SCROLL_WAITING_IN_ZONE = 1;

	protected static final int SCROLL_LEFT = 0;
	protected static final int SCROLL_RIGHT = 1;
	private int mScrollState = SCROLL_OUTSIDE_ZONE;
	private ScrollRunnable mScrollRunnable = new ScrollRunnable();
	
	protected ArrayList<DragScroller> mDragScrollers = new ArrayList<DragScroller>();
	protected RectF mDeleteRegion;
	
	/** Who can receive drop events */
	private ArrayList<DropTarget> mDropTargets = new ArrayList<DropTarget>();
	
	/**
	 * 图标长按菜单
	 */
	private BaseShortcutMenu shortcutMenu;
	// temporaries to avoid gc thrash
	private Rect mRectTemp = new Rect();
	
	DropTarget dropTarget;
	
	protected Runnable hideViewRunnable = new HideRunnable();
	protected class  HideRunnable implements Runnable{
		@Override
		public void run() {
//			mOriginator.setVisibility(View.GONE);
			mOriginator.setSelected(false);
			mOriginator.setPressed(false);
			
			if (android.os.Build.VERSION.SDK_INT >= 18) {	//hjiang，解决4.3固件上预览屏幕时，长按屏幕消失的问题
				mDragView.invalidate();
			}
		}
		
	}
	
	public BaseDragController(Context context) {
		mContext = context;
		Resources r = mContext.getResources();
		mTrashPaint.setFilterBitmap(true);
		mTrashPaint.setAntiAlias(true);
		mTrashPaint.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.icon_color_filter_in_move_bar), PorterDuff.Mode.SRC_ATOP));
		
		float density = r.getDisplayMetrics().density;
		scroll_zone = (int) (20 * density);
        mFlingToDeleteThresholdVelocity = (int) (-1500 * density);
        mActionDragOverMaxVelocity = (int) (-500 * density);
	}
	
	public BaseDragController(Context context, Object obj) {
		mContext = context;
		mTrashPaint.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.icon_color_filter_in_move_bar), PorterDuff.Mode.SRC_ATOP));
	}
	
	public void setDragLayer(DragLayer dragLayer) {
		mDragLayer = dragLayer;
	}
	
	public void startDrag(View v, DragSource source) {
		if (!isInAction() || mDragView != null)
			return;
		
		showDeleteZone(v.getTag());
		
		int[] loc = mCoordinatesTemp;
		v.getLocationOnScreen(loc);
		mOriginator = v;
		
		mOriginator.setVisibility(View.GONE);
		handler.postDelayed(hideViewRunnable, 50);
		onStartDrag(v, loc[0], loc[1], v.getWidth(), v.getHeight(), source, v.getTag());
		//显示DragView
		if (isOnSpringMode()) {
			mDragView = new DragView(mContext, v, (int)mTouchOffsetX, (int)mTouchOffsetY, 
					(int) (getSpringScale() * v.getWidth()), (int) (getSpringScale() * v.getHeight()));
			mDragView.setSprignScale(getSpringScale());
		}else{
			mDragView = new DragView(mContext, v, (int)mTouchOffsetX, (int)mTouchOffsetY, v.getWidth(), v.getHeight());
		}
		mDragView.setDragLayer(mDragLayer);
		mDragView.show((int) mMotionDownX, (int) mMotionDownY);
		
		initDragOutline();
		vibrator();
	}
	
	public void onStartDrag(View v, int screenX, int screenY, int dragViewWidth, int dragViewHeight, DragSource source, Object dragInfo) {
		if (mDragView != null) {
			return;
		}
		
		// 隐藏软键盘
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(mWindowToken, 0);

		springScroll_zone = mWorkspace.getSpringPageSplit() + scroll_zone;
		mDragging = true;
		mDragSource = source;
		mDragInfo = dragInfo;
		isAvaiableCell = false;
		
		boolean isSpringMode = isOnSpringMode() && isWorkspace(source);//是否为桌面的编辑模式
		if (isSpringMode) {
			mWorkspace.destoryCurrentChildHardwareLayer();
			
			screenX = screenX - mWorkspace.getAdjustXBySpringMode();// 编辑模式下，调整mDragView坐标位置
			int[] downXY = new int[]{(int) mMotionDownX, (int) mMotionDownY};
			BaseCellLayoutHelper.springToNormalCoordinateEx(downXY);
			mTouchOffsetX = downXY[0] - screenX;
			mTouchOffsetY = downXY[1] - screenY;
			
			mTouchOffsetX = (int) (getSpringScale() * mTouchOffsetX);
			mTouchOffsetY = (int) (getSpringScale() * mTouchOffsetY);
//			dragViewWidth = (int) (getSpringScale() * dragViewWidth);
//			dragViewHeight = (int) (getSpringScale() * dragViewHeight);
		}else{
			mTouchOffsetX = mMotionDownX - screenX;
			mTouchOffsetY = mMotionDownY - screenY;
		}
		
//		mDragView = new DragViewWrapper(mContext, v, (int)mTouchOffsetX, (int)mTouchOffsetY, 
//				dragViewWidth, dragViewHeight);
//		mDragView.setWorkspace(mWorkspace);
//		mDragView.setDragLayer(mDragLayer);
//		mDragView.show((ViewGroup) mDragSource, (int) mMotionDownX, (int) mMotionDownY);
//		if (isSpringMode) {
//			mDragView.setSprignScale(mWorkspace.getSpringScale());
//		}
	}
	
	/**
	 * 显示顶部删除区
	 * @param o 拖拽对象
	 */
	protected void showDeleteZone(Object o){
		showDeleteZone(o, false, false);
	}
	
	/**
	 * 显示顶部删除区
	 * @param o 拖拽对象
	 * @param showDelete 强制显示删除
	 * @param showUninstall 强制显示卸载
	 */
	protected void showDeleteZone(Object o, boolean showDelete, boolean showUninstall){
		mWorkspace.getLauncher().setupDeleteZone();
		DeleteZone deleteZone = mWorkspace.getLauncher().getDeleteZone();
		if(deleteZone != null){	
			deleteZone.setShowDelete(showDelete);
			deleteZone.setShowUninstall(showUninstall);
			deleteZone.show(o);
		}
	}
	
	/**
	 * 隐藏顶部删除区
	 */
	protected void hideDeleteZone(){
		if(mWorkspace.getLauncher().getDeleteZone() != null){
			mWorkspace.getLauncher().getDeleteZone().hide();
		}
	}
	
	/**
	 * 是否响应拖动
	 * @return
	 */
	public boolean isInAction() {
		return isInAction;
	}
	
	protected void initDragOutline(){
		// 保存原始画笔
		mOriginPaint = mDragView.getPaint();
//		DragHelper.getInstance().initDragOUtline(mDragView,mWorkspace);
		mWorkspace.initDragOutline(mDragView, mTrashPaint, mOriginPaint);
	}
	
	public void vibrator(){
		if (mVibrator == null)
			mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE); 
		
		mVibrator.vibrate(VIBRATE_DURATION);
	}
	
	public boolean isOnSpringMode(){
		return mWorkspace.isOnSpringMode();
	}
	
	public float getSpringScale(){
		return mWorkspace.getSpringScale();
	}
	
	public void setWorkspace(ScreenViewGroup mWorkspace) {
		this.mWorkspace = mWorkspace;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return mDragging;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();

		if (action == MotionEvent.ACTION_DOWN) {
			recordScreenSize();
		}

		final int screenX = clamp((int) ev.getRawX(), 0, mDisplayMetrics.widthPixels);
		final int screenY = clamp((int) ev.getRawY(), 0, mDisplayMetrics.heightPixels);

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mMotionDownX = screenX;
			mMotionDownY = screenY;
			mLastDropTarget = null;
			isInAction = true;
			lastMoveX = ev.getX();
			lastMoveY = ev.getY();
			dismissShortcutMenu();
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			showShorcutMenuOnActionUpIfNeed();
			if (mDragging) {
				drop(screenX, screenY);
			} else{
				dropOnOtherState(screenX, screenY);
			}
			endDrag();
			break;
		}
		Log.e(TAG,"onInterceptTouchEvent return "+mDragging +","+action);
		return mDragging;
	}
	
	public void cancelDrag() {
		endDrag();
	}
	
	protected void endDrag() {
		endDrag(true);
	}
	
	public void endDrag(boolean animateDragView){
		isInAction = false;
		if(!mDragging)
			return;
		
		mDragging = false;
		handler.removeCallbacks(hideViewRunnable);
		if (animateDragView && isAnimateOnEndDrag()) {
			animateDragViewToPositon();
		}
		
		if (isCleanDragViewOnEndDrag()) {
			cleanDragView();
		}

		hideDeleteZone();
		mWorkspace.onDragEnd(mDragSource);
		mWorkspace.getLauncher().getDockbar().clean();
//		DragHelper.getInstance().dragOutlineAnimateOut();
	}
	
	public void cleanDragView(){
		if (mDragView != null) {
			mDragView.setPaint(mOriginPaint);
			mDragView.remove();
			mDragView = null;
		}
	}
	
	private void recordScreenSize() {
		((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
	}

	/**
	 * Clamp val to be &gt;= min and &lt; max.
	 */
	protected static int clamp(int val, int min, int max) {
		if (val < min) {
			return min;
		} else if (val >= max) {
			return max - 1;
		} else {
			return val;
		}
	}
	
	/**
	 * Call this from a drag source view.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mDragging) {
			mWorkspace.getLauncher().closeFolder();
			Log.e(TAG,"onTouchEvent not mDragging return false");
			return false;
		}

		final int action = ev.getAction();
		final int screenX = clamp((int) ev.getRawX(), 0, mDisplayMetrics.widthPixels);
		final int screenY = clamp((int) ev.getRawY(), 0, mDisplayMetrics.heightPixels);

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// Log.e(TAG, "DragController.ACTION_DOWN");

			// Remember where the motion event started
			mMotionDownX = screenX;
			mMotionDownY = screenY;

			mLastMotionX = screenX;
			mLastMotionY = screenY;
			//处理按下时左右边框
			handleDownEventForBar(screenX, screenY);
			

			break;
		case MotionEvent.ACTION_MOVE:
			// Log.e(TAG, "DragController.ACTION_MOVE");
			// 长按图标菜单
			if (Math.abs(ev.getX() - lastMoveX) > 20 || Math.abs(ev.getY() - lastMoveY) > 20) {
				dismissShortcutMenu();
				
				handleOnDragMove();
			}

			int moveX = (int) ev.getRawX();
			int moveY = (int) ev.getRawY();
			mDragView.move(moveX, moveY);

			float xDiff = mLastMotionX - screenX;
			float yDiff = mLastMotionY - screenY;
			if(yDiff != 0){
				xyDiffRate = xDiff/yDiff;
			}
			
			mLastMotionX = screenX;
			mLastMotionY = screenY;

			// Drop on someone?
			final int[] coordinates = mCoordinatesTemp;
			dropTarget = findDropTarget(screenX, screenY, coordinates);
			isAvaiableCell = false;
			if (dropTarget != null) {

				if (dropTarget instanceof PreviewCellView) {
					dropTarget.acceptDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
				}

				if (mLastDropTarget == dropTarget) {
					if(isWorkspace(dropTarget)){
						final VelocityTracker vTracker = mVelocityTracker;
						vTracker.computeCurrentVelocity(1000);
						int velocityY = (int) vTracker.getYVelocity();
						ScreenViewGroup w = (ScreenViewGroup)dropTarget;
						if(velocityY < 0 && velocityY < mActionDragOverMaxVelocity){//拖动速度快时，不响应合并文件夹动画
							Log.i(TAG, "Drag too fast, so ignore onDragOver. velocityY:" + velocityY);
							w.setAllowAnimateMerFolder(false);
						}else{
							w.setAllowAnimateMerFolder(true);
						}
					}
					dropTarget.onDragOver(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
				} else {
					if (mLastDropTarget != null && isLastTargetActionOnMoveToDiff(dropTarget, mLastDropTarget)) {
						/**
						 * 标记为在ACTION_MOVE中触发onDragExit
						 */
						mDragView.setTag(getDragViewTagId(), Boolean.valueOf(true));
						mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
					}
					dropTarget.onDragEnter(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
				}
				
				if (isWorkspace(dropTarget)) {
					drawCellLayoutStuff((ScreenViewGroup)dropTarget, coordinates);//绘制Celllayout上的拖动光亮背景和编辑模式背景框
				}			
			} else {
				if (mLastDropTarget != null) {
					/**
					 * 标记为在ACTION_MOVE中触发onDragExit
					 */
					mDragView.setTag(getDragViewTagId(), Boolean.valueOf(true));
					mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
				}
			}
//			mDragLayer.invalidate();
			mLastDropTarget = dropTarget;

			// Scroll, maybe, but not if we're in the delete region.
			handleMoveEventForBar(screenX, screenY);

			break;
		case MotionEvent.ACTION_UP:
			showShorcutMenuOnActionUpIfNeed();
			handler.removeCallbacks(mScrollRunnable);
			setScrollBarVisiblityEx(true, true, false);
			if (!mDragging) {
				endDrag();
				break;
			}
			
			PointF vec = isFlingingToDelete();
            if (vec != null) {
            	dropOnFlingToDeleteTarget(screenX, screenY, vec);
            }else{
            	drop(screenX, screenY);
            	endDrag();
            }

			break;
		case MotionEvent.ACTION_CANCEL:
			cancelDrag();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			point1DownX = ev.getX(1);
			point1DownTime = SystemClock.uptimeMillis();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			dismissShortcutMenu();
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity(1);
			if (!(mContext instanceof BaseLauncher))
				return true;
			BaseLauncher launcher = (BaseLauncher) mContext;
			if (launcher.isWorkspaceVisable() && !launcher.isFolderOpened()) {
				ScreenViewGroup workspace = launcher.getScreenViewGroup();
				if(velocityX == 0){
					//为了适配g13手机   获取不到velocityX速度
					float point1UpX = ev.getX(1);
					float point1UpTime = SystemClock.uptimeMillis();
					velocityX =  (int)( (point1UpX - point1DownX)/( (point1UpTime - point1DownTime)/1000 ));
				}
				
				if (velocityX > SNAP_VELOCITY && workspace.getCurrentScreen() > 0) {
					workspace.getChildAt(workspace.getCurrentScreen()).invalidate();
					workspace.snapToScreen(workspace.getCurrentScreen() - 1);
					workspace.changeDragOutline(null);
				} else if (velocityX < -SNAP_VELOCITY && workspace.getCurrentScreen() < workspace.getChildCount() - 1) {
					workspace.getChildAt(workspace.getCurrentScreen()).invalidate();
					workspace.snapToScreen(workspace.getCurrentScreen() + 1);
					workspace.changeDragOutline(null);
				}
			}else{
				onOtherActionPointerUp(velocityX);
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
			break;

		}
		Log.e(TAG,"onTouchEvent  return true action:"+ action);
		return true;
	}
	
	/**
	 * 处理放手响应
	 * @param x
	 * @param y
	 */
	protected void drop(float x, float y){
		final int[] coordinates = mCoordinatesTemp;
		if (mWorkspace.isOnSpringMode()) {// 编辑模式下，调整drop后坐标位置
			BaseCellLayoutHelper.springToNormalCoordinateEx(coordinates);
		}

		// Log.e(TAG, "DragController.drop");
		dropTarget = findDropTarget((int) x, (int) y, coordinates);
	
		if(handleMultiDragOnDrop(x, y, coordinates, dropTarget))
			return;
		
		if (dropTarget != null) {
//			if (mDragSource instanceof DrawerSlidingView 
//					&& (dropTarget instanceof MoveToLauncherZone || dropTarget instanceof DeleteZoneTextView) && wk.isOnSpringMode()) {
//				// 从程序匣子拖出的应用没有drop到workspace，退回到匣子
//				mDragView.setPaint(mOriginPaint);// 还原
//				Toast.makeText(mContext, R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
//				wk.changeToNormalMode();
//				return;
//			}
			if (dropTarget instanceof PreviewWorkspace) {// 拖动图标到屏幕预览可放置区域外
				if (((BaseLauncher) mContext).getPreviewEditController().getPreviewMode() == PreviewEditAdvancedController.DROP_PREVIEW_MODE) {
					Toast.makeText(mContext, R.string.message_preview_fail_drag_to_screen, Toast.LENGTH_SHORT).show();
					((BaseLauncher) mContext).getPreviewEditController().stopDesktopEdit();
				}
			}
			if (dropTarget instanceof PreviewCellView) {// 拖动图标到屏幕预览区
				dropTarget.onDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
				return;
			}
	
			if(dropTarget instanceof BaseMagicDockbar){
				((BaseMagicDockbar) dropTarget).setOnHandlerDrop(true);
			}
			/**
			 * 标记为在非ACTION_MOVE中触发onDragExit
			 */
			if (mDragView != null) {
				mDragView.setTag(R.id.drager_controller_on_drag_exit_in_action_move, Boolean.valueOf(false));
			}
	
			dropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
	
			if (dropTarget.acceptDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo)) {
				dropTarget.onDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
				mDragSource.onDropCompleted((View) dropTarget, true);
				return;
			} else {
				mDragSource.onDropCompleted((View) dropTarget, false);
				return;
			}
		} else if(mDragSource instanceof BaseMagicDockbar){//适配MX3等 底部按钮区域
			((BaseMagicDockbar)mDragSource).restoreReorder();
		} else {
			handleNotFindTargetOnDrop();
		}
	}
	
	/**
	 * Description: 处理甩动图标到顶部删除卸载区域
	 */
	private void dropOnFlingToDeleteTarget(int x, int y, PointF vel) {
        final int[] coordinates = mCoordinatesTemp;
        if (isOnSpringMode()) {// 编辑模式下，调整drop后坐标位置
        	BaseCellLayoutHelper.springToNormalCoordinateEx(coordinates);
		}
        
        if (mDragView != null) {
        	mDragView.setTag(getDragViewTagId(), Boolean.valueOf(false));
        }
        
        //判断是甩到卸载区还是删除区
        BaseDeleteZoneTextView targetZone = mDeleteZoneTextView;
		boolean isFlingToDeleteZone = true;
		int[] loc = new int[2];
        mDragView.getLocationOnScreen(loc);
        int endY = - loc[1];
		int endX = -(int)(loc[1] * xyDiffRate); 
		
		if(mUninstallZoneTextView.getVisibility() == View.VISIBLE && loc[0] + endX < mUninstallZoneTextView.getWidth()){
			targetZone = mUninstallZoneTextView;
			isFlingToDeleteZone = false;
		}
		
		targetZone.onDragExit(mDragSource, coordinates[0], coordinates[1], 
        		(int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
		targetZone.onFlingToDelete(mDragSource, coordinates[0], coordinates[1], 
        		(int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo, endX, endY, isFlingToDeleteZone);
    }
	
	public void setDeleteZoneTextView(BaseDeleteZoneTextView mDeleteZoneTextView) {
		this.mDeleteZoneTextView = mDeleteZoneTextView;
	}
	
	public void setUninstallZoneTextView(BaseDeleteZoneTextView mUninstallZoneTextView) {
		this.mUninstallZoneTextView = mUninstallZoneTextView;
	}
	
	/**
	 * Description: 判断用户是否甩动图标到顶部删除卸载区域
	 */
	private PointF isFlingingToDelete() {
		//判断是否允许甩动到删除卸载区
		if (!(mContext instanceof BaseLauncher))
			return null;
		BaseLauncher launcher = (BaseLauncher) mContext;
		if(!launcher.isDeleteZoneVisible())
			return null;
		
		ViewConfiguration config = ViewConfiguration.get(mContext);
		mVelocityTracker.computeCurrentVelocity(1000, config.getScaledMaximumFlingVelocity());
		if (mVelocityTracker.getYVelocity() < mFlingToDeleteThresholdVelocity) {
			PointF vel = new PointF(mVelocityTracker.getXVelocity(),
                    mVelocityTracker.getYVelocity());
            PointF upVec = new PointF(0f, -1f);
            float theta = (float) Math.acos(((vel.x * upVec.x) + (vel.y * upVec.y)) /
                    (vel.length() * upVec.length()));
            if (theta <= Math.toRadians(MAX_FLING_DEGREES)) {
                return vel;
            }
		}
        return null;
    }

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		return mMoveTarget != null && mMoveTarget.dispatchUnhandledMove(focused, direction);
	}

	public boolean isDragging() {
		return mDragging;
	}
	
	public void addDragScoller(DragScroller scroller) {
		mDragScrollers.add(scroller);
	}

	public void setWindowToken(IBinder token) {
		mWindowToken = token;
	}

	/**
	 * @return the mDragInfo
	 */
	public Object getDragInfo() {
		return mDragInfo;
	}

	/**
	 * @param mOriginator
	 *            the mOriginator to set
	 */
	public void setOriginator(View mOriginator) {
		this.mOriginator = mOriginator;
	}

	public View getOriginator() {
		return mOriginator;
	}

	/**
	 * @param mDragSource
	 *            the mDragSource to set
	 */
	public void setDragSource(DragSource mDragSource) {
		this.mDragSource = mDragSource;
	}
	/**
	 * @return the mDragView
	 */
	public DragView getDragView() {
		return mDragView;
	}

	/**
	 * @return the mCoordinatesTemp
	 */
	public int[] getDragCoordinates() {
		return mCoordinatesTemp;
	}

	/**
	 * @return the mTouchOffsetX
	 */
	public float getTouchOffsetX() {
		return mTouchOffsetX;
	}

	/**
	 * @return the mTouchOffsetY
	 */
	public float getTouchOffsetY() {
		return mTouchOffsetY;
	}
	
	public void setDeleteRegion(RectF region) {
		mDeleteRegion = region;
	}


	public int getScrollZone() {
		return scroll_zone;
	}
	
	/**按下时，处理BAR的显示情况*/
	public void handleDownEventForBar(int screenX,int screenY)
	{
		if (screenX < scroll_zone || screenX>(mDragLayer.getWidth()- scroll_zone)) 
		{	
			mScrollState = SCROLL_WAITING_IN_ZONE;
			if (!isFolderOpened() && ((BaseLauncher) mContext).isAllAppsVisible()) {
				/**
				 * 显示匣子边框
				 */
				if (screenX < scroll_zone) {
				/**左边*/
					setScrollBarVisiblityEx(true, false, true);
				} else if (screenX >mDragLayer.getWidth()- scroll_zone) {
					/**右边*/
					setScrollBarVisiblityEx(false, false, true);
				}
				handler.postDelayed(mScrollRunnable, SCROLL_DELAY);
			} else {
				if (!isFolderOpened()) {
					if (screenX < scroll_zone) {
						/**左边*/
						setScrollBarVisiblityEx(true, true, true);
					} else if (screenX >mDragLayer.getWidth() - scroll_zone) {
						/**右边*/
						setScrollBarVisiblityEx(false, true, true);
					}
				}
				handler.postDelayed(mScrollRunnable, SCROLL_DELAY);
			}
			
		} 
		else {
			mScrollState = SCROLL_OUTSIDE_ZONE;
		}
	}
	
	/**移动时，处理BAR的显示情况*/
	public void handleMoveEventForBar(int screenX,int screenY)
	{
		// Scroll, maybe, but not if we're in the delete region.
		boolean inDeleteRegion = false;
		if (mDeleteRegion != null) {
			inDeleteRegion = mDeleteRegion.contains(screenX, screenY);
		}

		if (!inDeleteRegion && (screenX < scroll_zone || (isOnSpringMode() && screenX < springScroll_zone))) {// 编辑模式下用springScroll_zone
			if (mScrollState == SCROLL_OUTSIDE_ZONE) {
				if (!isFolderOpened() && ((BaseLauncher) mContext).isAllAppsVisible()) {
					/**
					 * 显示匣子左边
					 */
					mScrollState = SCROLL_WAITING_IN_ZONE;
					mScrollRunnable.setDirection(SCROLL_LEFT);
					setScrollBarVisiblityEx(true, false, true);
					handler.postDelayed(mScrollRunnable, SCROLL_DELAY);
					
				} else {
					/**
					 * 显示桌面左边
					 */
					mScrollState = SCROLL_WAITING_IN_ZONE;
					mScrollRunnable.setDirection(SCROLL_LEFT);
					if (!isFolderOpened()) {
					
						setScrollBarVisiblityEx(true, true, true);
					}
					handler.postDelayed(mScrollRunnable, SCROLL_DELAY);
				}
			}
		} else if (!inDeleteRegion && (screenX > mDragLayer.getWidth() - scroll_zone || (isOnSpringMode() && screenX > mDragLayer.getWidth() - springScroll_zone))) {// 编辑模式下用springScroll_zone
			if (mScrollState == SCROLL_OUTSIDE_ZONE) {
				if (!isFolderOpened() && ((BaseLauncher) mContext).isAllAppsVisible()) {
					/**
					 * 显示匣子右边
					 */
					mScrollState = SCROLL_WAITING_IN_ZONE;
					mScrollRunnable.setDirection(SCROLL_RIGHT);
					setScrollBarVisiblityEx(false, false, true);
					handler.postDelayed(mScrollRunnable, SCROLL_DELAY);

				} else {
					/**
					 * 显示桌面右边
					 */
					mScrollState = SCROLL_WAITING_IN_ZONE;
					mScrollRunnable.setDirection(SCROLL_RIGHT);
					if (!isFolderOpened()) {
						setScrollBarVisiblityEx(false, true, true);
					}
					handler.postDelayed(mScrollRunnable, SCROLL_DELAY);
				}
			}
		} else {
			/**
			 * 隐藏所有 
			 */
			if (mScrollState == SCROLL_WAITING_IN_ZONE) {
				mScrollState = SCROLL_OUTSIDE_ZONE;
				mScrollRunnable.setDirection(SCROLL_RIGHT);
				handler.removeCallbacks(mScrollRunnable);
				mDragView.setPaint(null);
				setScrollBarVisiblityEx(true, true, false);
			}
		}
	}
	
	/**
	 * <br>
	 * Description: 设置滚动边界的显示 <br>
	 *
	 * @param isLeft  是否是左边栏
	 * @param isWorkspace 是否在屏幕上，false 表示在匣子中
	 * @param isVisible   显示或者隐藏，如果是false 时，前两个参数 忽略
	 */
	private void setScrollBarVisiblityEx(boolean isLeft, boolean isWorkspace,
			boolean isVisible) {

		if (isVisible) {
			if(!allowShowMoveBar(isLeft))
				return;
			if (isLeft) {

				mDragLayer.getDragLayerStuff().drawMoveToLeftBar();
			} else {
				mDragLayer.getDragLayerStuff().drawMoveToRightBar();
			}
			if (mDragView != null) {
				mDragView.setPaint(mTrashPaint);
			}
		} else {
			mDragLayer.getDragLayerStuff().hideMoveBar();
		}
	}
	
	private class ScrollRunnable implements Runnable {
		private int mDirection;

		ScrollRunnable() {
		}

		public void run() {
			final ArrayList<DragScroller> dragScrollers = mDragScrollers;
			final int count = dragScrollers.size();
			for (int i = 0; i < count; i++) {
				DragScroller scroller = dragScrollers.get(i);
				if (scroller != null) {
					if (isWorkspace(scroller)) {// 清除光亮图标
						((ScreenViewGroup) scroller).cleanDragOutline();
					}
					if (isWorkspace(scroller) && !isOnSpringMode() && !isFolderOpened()) {
						if(!onWorkspaceScrollEdge(scroller, mDirection)){
							continue;
						}
//						View view = (View) scroller;
//						if (view.getVisibility() != View.VISIBLE)
//							continue;
//						// 缩放控件拖动时的大小
//						float scaleWidth = ScreenUtil.getCurrentScreenWidth(mContext) / 3;
//						if (null != mDragView && mDragView.getWidth() > scaleWidth) {
//							float scale = scaleWidth / (float) mDragView.getWidth();
//							mDragView.setPreviewScale(scale);
//							mDragView.update(DragView.MODE_PREVIEW);
//						}
//						// 进入屏幕预览图标拖放模式 caizp 2012-6-27
//						((BaseLauncher) mContext).getPreviewEditController().startDesktopEdit(PreviewEditAdvancedController.DROP_PREVIEW_MODE);
					} else {
						if(!onOtherScrollEdge(scroller, mDirection)){
							continue;
						}
					}
					mScrollState = SCROLL_OUTSIDE_ZONE;
					break;
				}
			}
			/**
			 * 删除所有的边框
			 * */
			setScrollBarVisiblityEx(true, true, false);
			
			isAvaiableCell = false;
		}

		void setDirection(int direction) {
			mDirection = direction;
		}
	}
	
	public void addDropTarget(DropTarget target) {
		mDropTargets.add(target);
	}

	public void removeDropTarget(DropTarget target) {
		mDropTargets.remove(target);
	}
	
	public ArrayList<DropTarget> getDropTargets() {
		return mDropTargets;
	}
	
	/**
	 * Sets the view that should handle move events.
	 */
	public void setMoveTarget(View view) {
		mMoveTarget = view;
	}
	
	public DragSource getDragSource() {
		return mDragSource;
	}
	
	/**
	 * 是否Workspace
	 * @param v
	 * @return
	 */
	public boolean isWorkspace(Object v){
		return v != null && v instanceof ScreenViewGroup;
	}
	
	/**
	 * 是否打开文件夹
	 * @return
	 */
	protected boolean isFolderOpened() {
		return mWorkspace.getLauncher().isFolderOpened();
	}
	
	public int getDragViewTagId(){
		return R.id.drager_controller_on_drag_exit_in_action_move;
	}
	
	/**
	 * 寻找拖动目标区
	 * @param x
	 * @param y
	 * @param dropCoordinates
	 * @return
	 */
	protected DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
		final Rect r = mRectTemp;

		final ArrayList<DropTarget> dropTargets = getDropTargets();
		final int count = dropTargets.size();
		for (int i = count - 1; i >= 0; i--) {
			final DropTarget target = dropTargets.get(i);
			if (isFromDrawerFolderOnFindDropTarget(target)) {
				return target;
			}
			if (target.getState() == DropTarget.UNAVAIABLE)
				continue;
			if(target instanceof View && ((View)target).getVisibility() != View.VISIBLE)
				continue;
			target.getHitRect(r);
			target.getLocationOnScreen(dropCoordinates);
			r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());
			if (r.contains(x, y)) {
				dropCoordinates[0] = x - dropCoordinates[0];
				dropCoordinates[1] = y - dropCoordinates[1];
				return target;
			}
		}
		return null;
	}
	
	/**
	 * 绘制Celllayout上的拖动光亮投影和编辑模式背景框
	 */
	public void drawCellLayoutStuff(ScreenViewGroup screenViewGroup, int[] coordinates){
		if(BaseConfig.isOnScene()){
			isAvaiableCell = true;
			return;
		}
		ScreenViewGroup wk = screenViewGroup;
		int currentScreen = wk.getCurrentScreen();
		CellLayout cl = wk.getCellLayoutAt(currentScreen);
		if (wk.acceptDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo)) {
			if(!wk.isDragOnApplication()){//若拖动app到另一个app上方，不绘制光亮投影
				Rect foundEstimate = wk.estimateDropLocation(mDragSource, mDragLayer.getScrollX() + (int) mLastMotionX, 
						mDragLayer.getScrollY() + (int) mLastMotionY, (int) mTouchOffsetX,
						(int) mTouchOffsetY, mDragView, mDragInfo);
				if (foundEstimate != null) {
					isAvaiableCell = true;
//					if(cl.isOnReorderAnimation()){
//						foundEstimate = null;
//					}
					if(!wk.isOnMergeFolerAnimation() && !mDragView.isOnScaleAnimation){
//						cl.changeDragOutline(foundEstimate);
//						cl.setDragOutline(mDragView, mTrashPaint, mOriginPaint);
						boolean changeTargetCell = cl.changeDragOutline(foundEstimate);
						cl.setDragOutline(mDragView, mTrashPaint, mOriginPaint);
						if(wk.isOnSpringMode()){//编辑模式下
							wk.enableCurrentChildHardwareLayer();
							cl.invalidate();
						}else if(changeTargetCell){//普通模式下
							cl.invalidateDragOutlineZone();
//							cl.invalidate();
						}
					}
				}
				
			}

			cl.setAcceptDropOnSpringMode();
		} else {// 无法放入目标区
			if(!wk.acceptDropForReorder(mDragInfo)){
				cl.setNotDropOnSpringMode();
			}
		}
		// 取消CellLayout无法drop的标示
		int leftScreen = currentScreen - 1;
		if (leftScreen >= 0) {
			wk.getCellLayoutAt(leftScreen).setAcceptDropOnSpringMode();
		}
		int rightScreen = currentScreen + 1;
		if (rightScreen <= wk.getChildCount() - 1) {
			wk.getCellLayoutAt(rightScreen).setAcceptDropOnSpringMode();
		}
		
		if(wk.isOnSpringMode() && GpuControler.isOpenGpu(wk)){
			wk.invalidate();
		}
	}
	/**
	 * 隐藏图标的菜单栏
	 */
	public void dismissShortcutMenu() {
		if (isShortcutMenuShowing()) {
			shortcutMenu.dismiss();
		}
	}

	/**
	 * 是否展现菜单栏
	 */
	public boolean isShortcutMenuShowing() {
		if (shortcutMenu != null) {
			return shortcutMenu.isShowing();
		}
		return false;
	}

	public boolean onKeyDownProcess(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
			if (shortcutMenu != null && shortcutMenu.isShowing()) {
				shortcutMenu.dismiss();
				return true;
			}
		}

		if (shortcutMenu != null)
			shortcutMenu.dismiss();
		
		return false;
	}
	
	public void showShortcutMenu(CellLayout.CellInfo cellInfo, View view, DropTarget dt) {
		if (shortcutMenu == null)
			shortcutMenu = createShortcutMenu();
		
		shortcutMenu.show(cellInfo, view, dt);
	}
	
	public BaseShortcutMenu getShortcutMenu(){
		return shortcutMenu;
	}
	
	/**
	 * 移动到Workspace左右边缘时的响应操作
	 * @param scroller
	 * @return
	 */
	public boolean onWorkspaceScrollEdge(DragScroller scroller, int mDirection){
		View view = (View) scroller;
		if (view.getVisibility() != View.VISIBLE)
			return false;
		// 缩放控件拖动时的大小
		float scaleWidth = ScreenUtil.getCurrentScreenWidth(mContext) / 3;
		if (null != mDragView && mDragView.getWidth() > scaleWidth) {
			float scale = scaleWidth / (float) mDragView.getWidth();
			mDragView.setPreviewScale(scale);
			mDragView.update(DragView.MODE_PREVIEW);
		}
		// 进入屏幕预览图标拖放模式 caizp 2012-6-27
		((BaseLauncher) mContext).getPreviewEditController().startDesktopEdit(PreviewEditAdvancedController.DROP_PREVIEW_MODE);
		return true;
	}
	
	/**
	 * 移动到其它可滚屏工作区左右边缘时的响应操作
	 * @param scroller
	 * @param mDirection
	 * @return
	 */
	public boolean onOtherScrollEdge(DragScroller scroller, int mDirection){
		if (scroller instanceof View) {
			View view = (View) scroller;
			if (view.getVisibility() != View.VISIBLE) {
				return false;
			} else if (isFolderOpened() && !(isDragFromFolder(view))) {
				return false;
			}
		}
		if (mDirection == SCROLL_LEFT) {
			scroller.scrollLeft();
		} else {
			scroller.scrollRight();
		}
		return true;
	}
	
	public DropTarget getDropTarget() {
		return dropTarget;
	}
	
	//================================================子类可重载下列方法===============================================//
	
	/**
	 * 移动到屏幕边缘时，是否允许显示指示条
	 * @param isLeft
	 * @return
	 */
	protected boolean allowShowMoveBar(boolean isLeft) {
		// 屏幕缩放模式
		if (isOnSpringMode())
			return false;
		// 屏幕预览模式
		if (((BaseLauncher) mContext).getPreviewEditController().isPreviewMode())
			return false;
		return true;
	}
	
	/**
	 * 从slidingView拖起，如文件夹里、匣子等
	 * @param v
	 * @param source
	 * @param dragInfo
	 * @param list
	 */
	public void startDragFromSlidingView(View v, DragSource source, Object dragInfo, ArrayList<DraggerChooseItem> list) {
		
	}
	
	/**
	 * 获取多选拖动的list
	 * @return
	 */
	public ArrayList<ApplicationInfo> getAppList() {
		return null;
	}
	
	/**
	 * 处理其它情况的放手(目前用于安卓桌面多选情况)
	 * @param screenX
	 * @param screenY
	 */
	public void dropOnOtherState(int screenX, int screenY){
		
	}
	
	/**
	 * 放手时，处理多选拖动(目前用于安卓桌面多选情况)
	 * @return
	 */
	public boolean handleMultiDragOnDrop(float x, float y, int[] coordinates, DropTarget dropTarget){
		return false;
	}
	
	/**
	 * 放手时，没有找到目标区的其它处理
	 */
	public void handleNotFindTargetOnDrop(){
		
	}
	
	/**
	 * 拖动放手后显示图标的菜单栏
	 */
	public void showShorcutMenuOnActionUpIfNeed(){
	}
	
	
	/**
	 * 是否允许放手后图标动画移到目标位置
	 * @return
	 */
	public boolean isAnimateOnEndDrag(){
		return mOriginator != null;
	}
	
	/**
	 * 是否清理DragView信息
	 * @return
	 */
	public boolean isCleanDragViewOnEndDrag(){
		return true;
	}
	
	/**
	 * 放手后图标动画移到目标位置
	 */
	protected void animateDragViewToPositon() {
	}
	
	/**
	 * 拖动时，是否响应LastDropTarget的onDragExit
	 * @param dropTarget
	 * @param mLastDropTarget
	 * @return
	 */
	public boolean isLastTargetActionOnMoveToDiff(DropTarget dropTarget, DropTarget mLastDropTarget){
		return true;
	}
	
	/**
	 * 双指触屏放手响应
	 * @param velocityX
	 */
	public void onOtherActionPointerUp(int velocityX){
	}
	
	/**
	 * 是否在DragLayer层布局拖动view
	 * @return
	 */
	@Override
	public boolean isLayoutDragView() {
		return isDragging();
	}
	
	/**
	 * 创建菜单
	 * @param cellInfo
	 * @param view
	 * @param dt
	 */
	public BaseShortcutMenu createShortcutMenu() {
		return null;
	}
	
	public boolean isDragFromFolder(Object v){
		return false;
	}
	
	/**
	 * 是否多选拖动
	 * @return
	 */
	public boolean isOnMultiSelectedDrag(){
		return false;
	}
	
	public boolean isDragFromDrawer(Object source){
		return false;
	}
	
	/**
	 * 在查找拖拽目标区域时，判断是否来自匣子文件夹
	 * @param target
	 * @return
	 */
	public boolean isFromDrawerFolderOnFindDropTarget(DropTarget target){
		return false;
	}
	
	/**
	 * 长按拖动时，处理一些状态，如去除菜单栏显示、小部件编辑态等
	 */
	public void handleOnDragMove(){
		
	}
}
