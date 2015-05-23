package com.bitants.launcherdev.launcher;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import com.bitants.launcherdev.folder.model.FolderSwitchController;
import com.bitants.launcherdev.folder.view.FolderSlidingView;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.dockbar.DockbarCellLayout;
import com.bitants.launcherdev.launcher.screens.preview.PreviewCellView;
import com.bitants.launcherdev.launcher.touch.BaseDragController;
import com.bitants.launcherdev.launcher.touch.DragScroller;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;

import java.util.ArrayList;

public class DragController extends BaseDragController{

	private FolderSwitchController mFolderOpenController;
	private final int[] mCoordinatesTemp2 = new int[2];
	
	public DragController(Context context) {
		super(context);
	}

	/**
	 * 从slidingView拖起，如文件夹里、匣子等
	 * @param v
	 * @param source
	 * @param dragInfo
	 * @param list
	 */
	public void startDragFromSlidingView(View v, DragSource source, Object dragInfo, ArrayList<DraggerChooseItem> list) {
		if (!isInAction())
			return;
		
//		waitingRelayoutSlidingView = false;
//		isAnimationAfterDrop = true;
//		isClearDraggerChooseListAfterDrop = true;
//		draggerChooseList = list;
//		if(list != null){
//			for (DraggerChooseItem draggerChooseItem : list) {
//				appList.add(draggerChooseItem.getInfo());
//			}
//		}
		showDeleteZone(source, dragInfo);
		
		int[] loc = mCoordinatesTemp;
		v.getLocationOnScreen(loc);
		int screenX = loc[0];
		int screenY = loc[1];
		
		mOriginator = v;
		mOriginator.setVisibility(View.GONE);
		handler.postDelayed(hideViewRunnable, 50);
		onStartDrag(v, screenX, screenY, v.getWidth(), v.getHeight(), source, dragInfo);
		
//		DragViewWrapper dragViewWrapper = new DragViewWrapper(mContext, v, (int)mTouchOffsetX, (int)mTouchOffsetY, 
//				v.getWidth(), v.getHeight(), draggerChooseList);
//		dragViewWrapper.setWorkspace((Workspace)mWorkspace);
		DragView dragViewWrapper = new DragView(mContext, v, (int)mTouchOffsetX, (int)mTouchOffsetY, 
				v.getWidth(), v.getHeight());
		dragViewWrapper.setDragLayer(mDragLayer);
		dragViewWrapper.show((int) mMotionDownX, (int) mMotionDownY);
		mDragView = dragViewWrapper;
		
		initDragOutline();
		vibrator();
	}
	
	/**
	 * 显示顶部删除区域
	 * @param source
	 * @param dragInfo
	 */
	protected void showDeleteZone(DragSource source, Object dragInfo){
		//如果是桌面打开的文件夹才显示删除区域FolderSlidingView
		if(source instanceof FolderSlidingView &&
				((Launcher)mWorkspace.getLauncher()).getFolderCotroller().getOpenFolderFrom() == FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER){
			super.showDeleteZone(dragInfo);
		}
	}

	/**
	 * 放手后图标动画移到目标位置
	 */
	protected void animateDragViewToPositon() {
		Workspace wk = (Workspace) mWorkspace;
		final ViewGroup parent = (ViewGroup) mOriginator.getParent();
		if(mDragView != null && mOriginator.getParent() != null && !(parent instanceof DockbarCellLayout)){
			int screenWidth = ScreenUtil.getCurrentScreenWidth(mContext);
			int originatorInScreen = 0;
			int[] startCoordinate = new int[2];
			int[] dropCoordinate = new int[2];
			if (mOriginator.getTag() != null && mOriginator.getTag() instanceof ItemInfo) {// 桌面
				originatorInScreen = ((ItemInfo) mOriginator.getTag()).screen;
				mDragView.getLocationOnScreen(startCoordinate);
				dropCoordinate = mCoordinatesTemp2;
				mOriginator.getLocationOnScreen(dropCoordinate);
				if (isAvaiableCell && wk.getTargetRect() != null) {// 在桌面上拖动后找到放置位置
					Rect r = wk.getTargetRect();
					float left = r.left + (r.right - r.left - mDragView.getWidth()) / 2.0f;
					float top = r.top + (r.bottom - r.top - mDragView.getHeight()) / 2.0f;
					dropCoordinate[0] = (int) left;
					dropCoordinate[1] = (int) top;
				}
				if (parent != null) {
					final ViewGroup workspace = (ViewGroup) parent.getParent();
					int currentScreen = 0;
					if (!(mOriginator instanceof PreviewCellView)) {
						currentScreen = Math.round((float) workspace.getScrollX() / screenWidth);
						if (currentScreen < 0) {
							currentScreen = workspace.getChildCount() - 1;
						}
					}
					// 若从匣子拖动view到workspace，则取消回到原来位置的动画
					if (originatorInScreen == currentScreen && !wk.isOnSpringMode()) {
						TranslateAnimation translateAnimation = new TranslateAnimation(startCoordinate[0] - dropCoordinate[0], 0, startCoordinate[1] - dropCoordinate[1], 0);
						AlphaAnimation alphaAnimation = new AlphaAnimation((float)155 / 255, 1.0f);

						AnimationSet set = new AnimationSet(true);
						set.addAnimation(translateAnimation);
						set.addAnimation(alphaAnimation);
						set.setDuration(200);

						mOriginator.bringToFront();
						set.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation ani) {
							}

							@Override
							public void onAnimationRepeat(Animation ani) {
							}

							@Override
							public void onAnimationEnd(Animation ani) {
							}
						});
						mOriginator.startAnimation(set);
					}
				}
			}
		}
		
		mOriginator.setVisibility(View.VISIBLE);
	}
	
	void setFolderOpenController(FolderSwitchController mFolderOpenController) {
		this.mFolderOpenController = mFolderOpenController;
	}
	
	
	
	
	/**
	 * 拖动屏幕边缘，不进入预览模式
	 * @param scroller
	 * @param mDirection
	 * @return
	 */
	@Override
	public boolean onWorkspaceScrollEdge(DragScroller scroller, int mDirection){
		View view = (View) scroller;
		if (view.getVisibility() != View.VISIBLE)
			return false;
		if (mDirection == SCROLL_LEFT) {
			scroller.scrollLeft();
		} else {
			scroller.scrollRight();
		}
		return true;
	}
	
	/**
	 * 拖动到屏幕边缘，不显示提示条
	 * @param isLeft
	 * @return
	 */
	@Override
	protected boolean allowShowMoveBar(boolean isLeft) {
		return false;
	}
	
	@Override
	public boolean isDragFromFolder(Object v){
		return v instanceof FolderSlidingView;
	}
}
