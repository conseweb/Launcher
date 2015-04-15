package com.nd.launcherdev.framework.view.folderdraggersliding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.framework.view.draggersliding.DraggerLayout;
import com.nd.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.launcher.touch.DragSource;
import com.nd.launcherdev.launcher.view.DragView;
import com.nd.launcherdev.kitset.util.ScreenUtil;

/**
 * 元素可拖动、合并的CommonSlidingView (不允许跨数据集拖动、合并)
 */
public abstract class FolderDraggerSlidingView extends DraggerSlidingView {
	
	public static final int DROP_SUCCESS = 0;
	
	public static final int DROP_FAILED = 1;
	
	public static final int DROP_DO_NOTHING = 2;

	public FolderDraggerSlidingView(Context context) {
		super(context);
	}

	public FolderDraggerSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FolderDraggerSlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void initSelf(Context context) {
		super.initSelf(context);
		mergeOffsetX = mergeOffsetY = ScreenUtil.dip2px(context, 20);
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		View view = findViewByDraggerCoordinate(x, y, xOffset, yOffset, dragView);

		/**
		 * 当用户拖动到某view上在产生动画之前就松手，则表示失败
		 */
		if (!mDragController.isDragging()) {
			if (view != null)
				return false;
		}

		return super.acceptDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	@Override
	protected void drop(View originalView, View targetView, DragView dragView, int x, int y) {
		if (targetView == null) {
			super.drop(originalView, targetView, dragView, x, y);
			return;
		}
		final int currentScreen = getCurrentScreen();
		ICommonData data = getData(currentScreen);
		int[] oldInfo = getDataPageInfo(data);
		int dropState = handleMerge(originalView, targetView, dragView);
		if (dropState == DROP_SUCCESS) {
			DraggerLayout originalLayout = (DraggerLayout) getChildAt(mOriginalScreen);
			DraggerLayout currentLayout = (DraggerLayout) getChildAt(currentScreen);
			originalLayout.removeView(originalView);
			for (int screen = oldInfo[1] - 1; screen > mOriginalScreen; screen--) {
				DraggerLayout lastLayout = (DraggerLayout) getChildAt(screen);
				DraggerLayout previousLayout = (DraggerLayout) getChildAt(screen - 1);

				View view = lastLayout.getChildAt(0);
				lastLayout.removeView(view);
				if (mOriginalScreen < currentScreen && screen == currentScreen) {
					/**
					 * 从前面的页移到后面的页合并的情况
					 */
					lastLayout.startViewAnimation(lastLayout.getChildAt(lastLayout.getChildCount() - 1), targetView, true, pageWidth);
				} else {
					if (screen != oldInfo[1] - 1 || lastLayout.getChildCount() != 0) {
						/**
						 * 原数据集最后一页且子元素个数为0时，不重新布局
						 */
						lastLayout.reLayout(targetView);
					}
				}
				previousLayout.addView(view);
			}
			if (mOriginalScreen == currentScreen) {
				/**
				 * 页内合并
				 */
				if (currentScreen == oldInfo[1] - 1) {
					originalLayout.startViewAnimation(null, targetView, false, 0);
				} else {
					originalLayout.startViewAnimation(originalLayout.getChildAt(originalLayout.getChildCount() - 1), targetView, true, pageWidth);
				}
			} else {
				/**
				 * 非页内合并
				 */
				
				/**
				 * 从后面的页移到前面的页直接合并时，需通过该方法重新获取目标视图，避免显示异常
				 */
				currentLayout.startViewAnimation(null, targetView, false, 0);
				
				if (originalLayout.getChildCount() != 0) {
					/**
					 * 子元素个数为0时，不重新布局
					 */
					originalLayout.reLayout(targetView);
				}
			}

			/**
			 * 移除多余页数
			 */
			int[] newInfo = getDataPageInfo(data);
			if (newInfo[1] < oldInfo[1]) {
				if (oldInfo[1] != getChildCount()) {
					reLayout(newInfo[1]);
				} else {
					removeLayout(oldInfo[1] - 1);
					int screen = getCurrentScreen();
					if (screen > newInfo[1] - 1) {
						snapToScreen(newInfo[1] - 1);
					}
				}
			}

			mOriginalScreen = -1;
			mOriginalViewPositionInOriginalPage = -1;
		} else if (dropState == DROP_FAILED) {
			super.drop(originalView, targetView, dragView, x, y);			
		} else {
			mOriginalScreen = -1;
			mOriginalViewPositionInOriginalPage = -1;
		}
	}

	@Override
	protected void onDragCenter(View originalView, View targetView, DragView dragView) {
		if ((mDragInfo instanceof ICommonDataItem) && ((ICommonDataItem) mDragInfo).isFolder()) {
			super.onDragCenter(originalView, targetView, dragView);
			return;
		}
		Object targetInfo = targetView.getTag();
		if ((targetInfo instanceof ICommonDataItem) && ((ICommonDataItem) targetInfo).isFolder()) {
			onFolderMerge(originalView, targetView, dragView);
		} else {
			onIconMerge(originalView, targetView, dragView);
		}
	}

	@Override
	public void onMergeOutOfRange(View originalView, View targetView, DragView dragView) {
		onOutMerge(originalView, targetView, dragView);
	}

	/**
	 * 图标合并或图标拖至文件夹后会调用此函数
	 */
	protected abstract int handleMerge(View originalView, View targetView, DragView dragView);

	/**
	 * 图标拖拉至文件夹
	 * 
	 * @param targetView
	 */
	protected abstract void onFolderMerge(View originalView, View targetView, DragView dragView);

	/**
	 * 图标合并成文件夹
	 * 
	 * @param targetView
	 */
	protected abstract void onIconMerge(View originalView, View targetView, DragView dragView);

	/**
	 * 图标取消合并或图标拖出文件夹图标
	 */
	protected abstract void onOutMerge(View originalView, View targetView, DragView dragView);

}
