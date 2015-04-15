package com.bitants.launcherdev.folder.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.bitants.launcherdev.launcher.DeleteZoneTextView;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.screens.DeleteZone;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.AppMaskTextView;
import com.bitants.launcherdev.folder.model.FolderHelper;
import com.bitants.launcherdev.folder.model.FolderHelper;
import com.bitants.launcherdev.folder.model.FolderSwitchController;
import com.bitants.launcherdev.framework.AnyCallbacks.OnFolderDragOutCallback;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.bitants.launcherdev.launcher.DeleteZoneTextView;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.screens.DeleteZone;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.AppMaskTextView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.bitants.launcherdev.launcher.DeleteZoneTextView;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.screens.DeleteZone;
import com.bitants.launcherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.AppMaskTextView;

/**
 * represent folder's apps view
 * 
 * @author pdw
 * @date 2012-5-23 下午08:49:08
 */
public class FolderSlidingView extends DraggerSlidingView {

	private FolderInfo mFolderInfo;
	private Launcher mLauncher;

	private boolean mIsEditMode = false;
	private boolean mClosingFolder = false;

	private int state = DropTarget.AVAIABLE;
	// 文件夹中需被高亮背景的app索引
	private int mFocusIndex = -1;

	private View mFocusView;
	
	/**
	 * 应用程序编辑模式下多选列表
	 */
	private ArrayList<DraggerChooseItem> draggerChooseList = new ArrayList<DraggerChooseItem>();
	
	private ArrayList<Object> tempItems = new ArrayList<Object>();
	
	/**
	 * 是否在拖拽放手后需要重新布局，并在布局后做散开动画
	 */
	private boolean isNeedRelayoutAfterDrop = false;
	
	/**
	 * 是否正在做聚拢或散开动画
	 */
	private boolean isInAnimation = false;

	private Runnable mCloseFolderTask = new CloseFolderRunnable();

//	private ApplicationInfo addAppInfo = new ApplicationInfo();
	
	private View addView;
	
	public FolderSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		addAppInfo.iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder_add_icon);
//		addAppInfo.isSystem = 1;
//		addAppInfo.customIcon = true;
//		addAppInfo.useIconMask = false;
	}

	private View createViewFromLauncher(ICommonData data, int position){
		AppMaskTextView itv = new AppMaskTextView(mContext);
		final ApplicationInfo info = (ApplicationInfo) data.getDataList().get(position);
		itv.setText(info.title);
		itv.setTag(info);
		itv.setIconBitmap(info.iconBitmap);
		itv.setLazy(info.usingFallbackIcon);
		if (position == mFocusIndex) {
			itv.setSelected(true);
			mFocusView = itv;
		}
		
		if (isNeedRelayoutAfterDrop) {
			DraggerChooseItem dItem = getDraggerChooseItem(info);
			if (dItem != null) {
				/**
				 * 拖拽多选项放置后若需重新布局，则布局时多选项中的相关view不显示，布局结束后通过散开动画显示
				 */
				dItem.setView(itv);
				itv.setVisibility(INVISIBLE);
			}
		} else if (draggerChooseList != null && draggerChooseList.size() > 0) {
			DraggerChooseItem dItem = getDraggerChooseItem(info);
			if (dItem != null) {
				dItem.setView(itv);
			}
		}
//		if(info == addAppInfo){
//			addView = itv;
//			addView.setVisibility(View.VISIBLE);
//		}
		return itv;
	}
	
	@Override
	public View onGetItemView(ICommonData data, int position) {
		return createViewFromLauncher(data, position);
	}
//	@Override
//	public View onGetItemView(ICommonData data, int position) {
//		final View v = mLayoutInflater.inflate(R.layout.folder_application_boxed, this, false);
//		AppMaskTextView itv = (AppMaskTextView) v.findViewById(R.id.item_view);
//		final ApplicationInfo info = (ApplicationInfo) data.getDataList().get(position);
//		itv.setText(info.title);
//		itv.setTag(info);
//		itv.setIconBitmap(info.iconBitmap);
//		itv.setLazy(info.usingFallbackIcon);
////		ImageView iv = (ImageView) v.findViewById(R.id.item_close_btn);
////		ImageView choosedIcon = (ImageView) v.findViewById(R.id.item_choosed_icon);
////		choosedIcon.setVisibility(INVISIBLE);
////		iv.setOnClickListener(new OnClickListener() {
////			@Override
////			public void onClick(View v) {
////				// 先关闭文件夹
////				mLauncher.getFolderCotroller().closeFolder();
////
////				if (mIsEditMode) {
////					if (info != null && info.componentName != null && AndroidPackageUtils.isPkgInstalled(mContext, info.componentName.getPackageName())) {
////						/**
////						 * 删除程序
////						 */
////						((DrawerMainView) mLauncher.getDrawer()).recordRemovePackageName(info.componentName.getPackageName());
////						AppUninstallUtil.uninstallAppByLauncher(mLauncher, info.componentName.getPackageName());
////					} else {
////						/**
////						 * 程序不存在, 直接删除记录
////						 */
////						List<ApplicationInfo> listAppInfos = new ArrayList<ApplicationInfo>();
////						listAppInfos.add(info);
////						DrawerDataFactory.deleteApps(mContext, listAppInfos);
////						mCurrentData.getDataList().remove(info);
////						reLayout(getCurrentScreen());
////					}
////				}
////			}
////		});
//		/**
//		 * 删除按钮
//		 */
////		if (mIsEditMode) {
////			if (!(info.isSystem == 1)) {
////				iv.setVisibility(VISIBLE);
////			}
////			handler.postDelayed(new Runnable() {
////
////				@Override
////				public void run() {
////					Animation shake = DrawerUtil.getShakeAnimation(mContext);
////					if (shake != null) {
////						v.findViewById(R.id.animation_layout).startAnimation(shake);
////					}
////				}
////			}, 500);
////		} else {
////			iv.setVisibility(INVISIBLE);
////		}
//		if (position == mFocusIndex) {
//			v.setSelected(true);
//			mFocusView = v;
//		}
//		
////		if (isNeedRelayoutAfterDrop) {
////			DraggerChooseItem dItem = getDraggerChooseItem(info);
////			if (dItem != null) {
////				/**
////				 * 拖拽多选项放置后若需重新布局，则布局时多选项中的相关view不显示，布局结束后通过散开动画显示
////				 */
////				dItem.setView(v);
////				v.setVisibility(INVISIBLE);
////			}
////		} else if (draggerChooseList != null && draggerChooseList.size() > 0) {
////			DraggerChooseItem dItem = getDraggerChooseItem(info);
////			if (dItem != null) {
////				dItem.setView(v);
//////				iv.setVisibility(INVISIBLE);
//////				choosedIcon.setVisibility(VISIBLE);
////			}
////		}
////		
////		if(info == addAppInfo){
////			addView = v;
////			addView.setVisibility(View.VISIBLE);
////		}
//		
//		return v;
//	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		if (dragInfo instanceof ApplicationInfo && draggerChooseList != null && draggerChooseList.size() > 1 && getDraggerChooseItem((ApplicationInfo) dragInfo) != null) {
			/**
			 * 拖拽多项
			 */
			isNeedRelayoutAfterDrop = true;
		} else {
			/**
			 * 拖拽单项
			 */
			isNeedRelayoutAfterDrop = false;
		}
		
		if (isNeedRelayoutAfterDrop) {		
			/**
			 * 拖拽多选项
			 */
			CommonViewHolder holder = (CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder);
			if (holder.positionInData == mCurrentData.getDataList().indexOf(holder.item)) {
				/**
				 * 没有移动位置
				 */
				isNeedRelayoutAfterDrop = false;
			} else {
				/**
				 * 匣子需重新布局，拖拽图标返回动画需等待布局后再进行
				 */
//				((DragController)mDragController).setWaitingRelayoutSlidingView(true);
			}			
		} else {
			/**
			 * 拖拽单项
			 */
			super.onDrop(source, x, y, xOffset, yOffset, dragView, dragInfo);
		}
	}

	@Override
	public void onDataChanged(ICommonData data, CommonViewHolder originalViewHolder) {

		/**
		 * 排序统计
		 */
		
		if (!isNeedRelayoutAfterDrop && originalViewHolder.positionInData == mCurrentData.getDataList().indexOf(originalViewHolder.item)) {
			/**
			 * 未发生位置变化则不清除多选拖拽列表
			 */
//			((DragController)mDragController).setClearDraggerChooseListAfterDrop(false);
			return;
		}
		
		Collections.sort(data.getDataList(), new Comparator<ICommonDataItem>() {
			@Override
			public int compare(ICommonDataItem item1, ICommonDataItem item2) {
				return item1.getPosition() - item2.getPosition();
			}

		});
		
		List<ICommonDataItem> dataList = data.getDataList();
		if (dataList.indexOf(originalViewHolder.item) != -1 && originalViewHolder.item instanceof ApplicationInfo && getDraggerChooseItem((ApplicationInfo) originalViewHolder.item) != null) {
			/**
			 * 拖拽项处于文件夹中，且拖拽项是处于多选项中的程序图标
			 */
			
			/**
			 * 从数据列表中移除多选项
			 */
			for (int i = 1; i < draggerChooseList.size(); i++) {
				DraggerChooseItem item = draggerChooseList.get(i);
				ApplicationInfo info = item.getInfo();
				dataList.remove(info);
			}	
							
			/**
			 * 将多选项添加至数据列表中指定位置
			 */
			for (int i = 1; i < draggerChooseList.size(); i++) {
				DraggerChooseItem item = draggerChooseList.get(i);
				ApplicationInfo info = item.getInfo();
				dataList.add(dataList.indexOf(originalViewHolder.item) + i, info);
			}
			
			/**
			 * 设置正确的position信息
			 */
			for (int i = 0; i < dataList.size(); i++) {
				ICommonDataItem item = dataList.get(i);
				item.setPosition(i);
			}
		}
		
//		DrawerDataFactory.updateAppInfosPosition(mContext, data.getDataList());
		
		if (isNeedRelayoutAfterDrop) {
			/**
			 * 延时布局，避免图标正在做移动动画(单页布局)的同时进行全局布局，从而产生图标重叠的异常显示情况
			 */
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					reLayout();
				}
			}, moveDelay);				
		}
	}
	
	/**
	 * 清除多选列表
	 */
	public void clearDraggerChooseList() {
//		clearDraggerChooseList(null, true, true);
	}
	
	/**
	 * 清除多选列表
	 */
//	public void clearDraggerChooseList(View ingoreView, boolean isClearChoosed, boolean isClearInDrag) {
//		isNeedRelayoutAfterDrop = false;
//		for (DraggerChooseItem item : draggerChooseList) {
//			View v = item.getView();
//			if (v == null || v == ingoreView) continue;
//			if (v instanceof FolderBoxedViewGroup) {
//				ImageView closeBtn = (ImageView) v.findViewById(R.id.item_close_btn);
//				ImageView choosedIcon = (ImageView) v.findViewById(R.id.item_choosed_icon);
//				CommonViewHolder viewHolder = (CommonViewHolder) v.getTag(R.id.common_view_holder);
//				if (viewHolder == null)
//					continue;
//				boolean isSystem = false;
//				if (viewHolder.item instanceof ApplicationInfo) {
//					isSystem = ((ApplicationInfo) viewHolder.item).isSystem == 1;
//				}
//				if (isClearChoosed) {
//					if (!isSystem) {
//						closeBtn.setVisibility(VISIBLE);
//					}
//					choosedIcon.setVisibility(INVISIBLE);
//				}
//				if (isClearInDrag) {
//					((FolderBoxedViewGroup) v).setInDrag(false);
//				}
//				v.postInvalidate();
//			}
//		}
//		draggerChooseList.clear();
//		
//	}

	public void go2FirstScreen() {
		setCurrentScreen(0);
		scrollTo(0, 0);
		this.pageViews.clear();
		removeAllViews();
	}

	@Override
	public int getState() {
		return state;
	}

	public int setState(int state) {
		return this.state = state;
	}

	/**
	 * @param mDragInfo
	 *            the mDragInfo to set
	 */
	public void setDragInfo(ApplicationInfo mDragInfo) {
		this.mDragInfo = mDragInfo;
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		removeBackground();
		
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
//			addView.setVisibility(View.VISIBLE);
			if(!isDynamic(mFolderInfo) && addView != null){
				CommonLayout layout = getCommonLayout(getPage() - 1);
				layout.addView(addView);
			}
		}
		
		if (mCloseFolderTask != null) {
			handler.removeCallbacks(mCloseFolderTask);
		}

		final ApplicationInfo app = (ApplicationInfo) mDragInfo;
		
		if (success && target == this) {
			/**
			 * 拖拽成功且目标view为folderSlidingView
			 */
			CommonViewHolder holder = (CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder);
			onDataChanged(mCurrentData, holder);
		}

		/** 以下情况不处理 **/
		if (mClosingFolder || !success || target instanceof FolderSlidingView) {
			return;
		}
		
		/**
		 * 处理文件夹后再拖动到卸载区
		 */
		if (target instanceof DeleteZoneTextView && ((DeleteZoneTextView) target).getDeleteZoneType() == DeleteZone.UNINSTALL_ZONE ) {
			if(mLauncher.isFolderOpened()){
				mLauncher.closeFolder();
			}
			return;
		}

		/**
		 * 优先处理和dockbar的交换操作
		 */
		if (target instanceof BaseMagicDockbar) {
			final BaseMagicDockbar dockbar = (BaseMagicDockbar) target;
			mLauncher.closeFolder();
//			if (!dockbar.isDragFromDrawerFolder(this)) {
				FolderHelper.removeDragApp(mFolderInfo, app);
				mFolderInfo.checkFolderState();
//			}

			if (mFolderInfo.getSize() > 1) {
				mFolderInfo.mFolderIcon.invalidate();
			}
			return;
		}
		
		tempItems.clear();
		if (getDraggerChooseItem(app) != null) {
			/**
			 * 拖拽项是处于多选项中的程序图标
			 */
			for (DraggerChooseItem item : draggerChooseList) {
				ApplicationInfo info = item.getInfo();
				tempItems.add(info);
			}
		} else {
			tempItems.add(app);
		}

		/**
		 * 桌面特殊处理，当持久拖动时，放开时才处理文件夹的内容 此时文件夹已关闭
		 **/
		if (getVisibility() != VISIBLE) {
			mDragoutCallback.onDrop(target, mFolderInfo, tempItems);
			return;
		}

		/**
		 * 多次fling拖拽 文件夹不关闭
		 */
		if (mDragInfo != null && mDragInfo instanceof ApplicationInfo) {	
			
			if (mFolderInfo.contents.size() <= 2 || (getDraggerChooseItem(app) != null && draggerChooseList.size() >= mFolderInfo.contents.size() - 1)) {
				/**
				 * 文件夹个数少于2个，或拖拽项个数与文件夹个数相同或只少一个，则关闭文件夹(关闭文件夹过程中会清除拖拽多选项列表)
				 */
				mLauncher.closeFolder();
			} else {			
				/**
				 * 清除拖拽多选项列表
				 */
				clearDraggerChooseList();
			}
			if(target instanceof DeleteZoneTextView){
				if(mFolderInfo.getSize() > 0 && tempItems != null){
					Object item = tempItems.get(0);
					FolderHelper.removeDragApp(mFolderInfo, (ApplicationInfo) item);
					if (mFolderInfo.contents.size() > 1) {
						mFolderInfo.invalidate();
					}
					mFolderInfo.checkFolderState();
				}
				List<ICommonDataItem> apps = mCurrentData.getDataList();
				if (this.mOriginalView != null) {
					this.mOriginalView.setTag(R.id.common_view_holder, null);
				}
				for (Object item : tempItems) {
					FolderHelper.removeDragApp(apps, (ApplicationInfo) item);
				}
				reLayout();
			}else{//如果目标target不是DeleteZoneTextView 处理通过fling手势往文件夹外仍
				boolean flag = mDragoutCallback.onFlingOut(mFolderInfo, tempItems);
				if (flag) {
					List<ICommonDataItem> apps = mCurrentData.getDataList();
					if (this.mOriginalView != null) {
						this.mOriginalView.setTag(R.id.common_view_holder, null);
					}
					for (Object item : tempItems) {
						FolderHelper.removeDragApp(apps, (ApplicationInfo) item);
					}
					reLayout();
				}
			}
		}
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			setState(DropTarget.AVAIABLE);
		} else {
			removeBackground();
			setState(DropTarget.UNAVAIABLE);
		}
	}

	/**
	 * @return the mIsEditMode
	 */
	public boolean isEditMode() {
		return mIsEditMode;
	}

	/**
	 * 可见（不可见）删除按钮
	 * 
	 * @param mIsEditMode
	 *            the mIsEditMode to set
	 */
	public void setIsEditMode(boolean mIsEditMode) {
		if (this.mIsEditMode == mIsEditMode) {
			return;
		}
		this.mIsEditMode = mIsEditMode;
		if (!mIsEditMode) {
			clearDraggerChooseList();
		}
		final FolderSwitchController mFolderOpenController = mLauncher.getFolderCotroller();
		if (mFolderOpenController != null && mFolderOpenController.getOpenFolderFrom() == FolderSwitchController.OPEN_FOLDER_FROM_DRAWER) {
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				ViewGroup page = (ViewGroup) getChildAt(i);
				int childrenCount = page.getChildCount();
				for (int j = 0; j < childrenCount; j++) {
					View view = page.getChildAt(j);
//					ViewGroup animationLayout = (ViewGroup) view.findViewById(R.id.animation_layout);
//					ImageView iv = (ImageView) view.findViewById(R.id.item_close_btn);
					CommonViewHolder viewHolder = (CommonViewHolder) view.getTag(R.id.common_view_holder);
					if (viewHolder == null)
						continue;
					boolean isSystem = false;
					if (viewHolder.item instanceof ApplicationInfo) {
						isSystem = ((ApplicationInfo) viewHolder.item).isSystem == 1;
					}
					if (mIsEditMode) {
//						if (!isSystem) {
//							iv.setVisibility(VISIBLE);
//						}
//						Animation shake = DrawerUtil.getShakeAnimation(mContext);
//						if (shake != null) {
//							animationLayout.findViewById(R.id.animation_layout).startAnimation(shake);
//						}
					} else {
//						iv.setVisibility(INVISIBLE);
//						animationLayout.clearAnimation();
					}
				}
			}
		}
	}

	/**
	 * 去除焦点背景
	 */
	public void clearFocusedView() {
		if (mFocusView != null) {
			mFocusView.setSelected(false);
			mFocusView = null;
		}
	}

	/**
	 * 设置焦点背景
	 */
	public void setFocusedView(View v) {
		if (mFocusView == v) {
			return;
		}
		clearFocusedView();
		mFocusView = v;
		mFocusView.setSelected(true);
	}

	public void setLauncher(Launcher mLauncher) {
		this.mLauncher = mLauncher;
	}

	/**
	 * 通过延时handler来实现手势拖拽时，文件夹的关闭状态。 <br>
	 * 通过fling手势拖拽时，不会关闭文件夹 <br>
	 * 拖拽后在workspace,drawer长时间停留时，关闭文件夹 <br>
	 * 时间间隔设置{@link FolderSwitchController#CLOSE_FOLDER_DURATION}
	 * 
	 * @author pdw
	 * 
	 */
	class CloseFolderRunnable implements Runnable {
		@Override
		public void run() {
			mClosingFolder = true;
			setTargetState(DropTarget.UNAVAIABLE);
			// modify by linqiang 桌面动态文件夹内图标长按后不显示垃圾桶,拖拽出来后显示垃圾桶
//			if (mLauncher.getFolderCotroller().getOpenFolderFrom() == FolderSwitchController.OPEN_FOLDER_FROM_LAUNCHER){
//				mLauncher.showDeleteZone(mDragInfo);
//			}
			
			closeFolder();
			mClosingFolder = false;
		}
	}

	private void setTargetState(int state) {
		// 设置folderSlidingView target状态
		setState(state);
		// 设置folderView target状态
		final FolderView folderView = mLauncher.getFolderCotroller().getFolderView();
		folderView.setState(state);
	}

	private void closeFolder() {
		tempItems.clear();
		if (getDraggerChooseItem((ApplicationInfo) mDragInfo) != null) {
			/**
			 * 拖拽项是处于多选项中的程序图标
			 */
			for (DraggerChooseItem item : draggerChooseList) {
				ApplicationInfo info = item.getInfo();
				tempItems.add(info);
			}
		} else {
			tempItems.add(mDragInfo);
		}
		mDragoutCallback.onBeforeDragOut(mFolderInfo, tempItems);		
		mDragoutCallback.onDragOut(mFolderInfo, tempItems);
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		super.onDragEnter(source, x, y, xOffset, yOffset, dragView, dragInfo);
		if (mCloseFolderTask != null)
			handler.removeCallbacks(mCloseFolderTask);
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		super.onDragExit(source, x, y, xOffset, yOffset, dragView, dragInfo);
		handler.postDelayed(mCloseFolderTask, FolderSwitchController.CLOSE_FOLDER_DURATION);
	}

	void bind(FolderInfo userFolder, int focusIndex) {
		this.mFolderInfo = userFolder;
		mFocusIndex = focusIndex;
	}

	private OnFolderDragOutCallback mDragoutCallback;

	/**
	 * @param mDragoutCallback
	 *            the mDragoutCallback to set
	 */
	public void setDragoutCallback(OnFolderDragOutCallback mDragoutCallback) {
		this.mDragoutCallback = mDragoutCallback;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/**
		 * 触碰后清除view的焦点背景
		 */
		clearFocusedView();
		return super.onInterceptTouchEvent(ev);
	}

	void scrollToScreen(int whichScreen) {
		setCurrentScreen(whichScreen);
		scrollTo(whichScreen * pageWidth, 0);
		if (lightbar != null) {
			lightbar.update(whichScreen);
		}
	}

	@Override
	protected void onLayoutChildrenAfter() {
		if (mFocusIndex != -1) {
			ICommonData data = getData(0);
			int pageTotalCount = data.getRowNum() * data.getColumnNum();
			final int whichScreen = mFocusIndex / pageTotalCount;
			scrollToScreen(whichScreen);
		}
		super.onLayoutChildrenAfter();
		
		if (isNeedRelayoutAfterDrop) {
			/**
			 * 重新布局后进行拖拽图标返回动画
			 */
			isNeedRelayoutAfterDrop = false;
//			((DragController)mDragController).disperseChooseItemsAfterRelayout(getCurrentScreen(), draggerChooseList.get(0).getView(), this);
		} 
	}

	/**
	 * @return the mFolderInfo
	 */
	public FolderInfo getFolderInfo() {
		return mFolderInfo;
	}

	public View getOriginalView() {
		return mOriginalView;
	}
	
	/**
	 * 编辑模式下多选
	 * @param v
	 * @param info
	 */
//	public boolean chooseItem(ICommonData data, FolderBoxedViewGroup v, ApplicationInfo info) {
//		boolean isSuccess = false;
//		boolean isFound = false;
//		
//		ImageView closeBtn = (ImageView) v.findViewById(R.id.item_close_btn);
//		ImageView choosedIcon = (ImageView) v.findViewById(R.id.item_choosed_icon);
//		CommonViewHolder viewHolder = (CommonViewHolder) v.getTag(R.id.common_view_holder);
//		if (viewHolder == null)
//			return true;
//		boolean isSystem = false;
//		if (viewHolder.item instanceof ApplicationInfo) {
//			isSystem = ((ApplicationInfo) viewHolder.item).isSystem == 1;
//		}		
//		
//		for (DraggerChooseItem item : draggerChooseList) {
//			ApplicationInfo aInfo = item.getInfo();
//			if (aInfo == info) {
//				draggerChooseList.remove(item);
//				choosedIcon.setVisibility(INVISIBLE);
//				if (!isSystem) {
//					closeBtn.setVisibility(VISIBLE);
//				}
//				isFound = true;
//				isSuccess = true;
//				break;
//			}
//		}
//		int limit = data.getColumnNum() * data.getRowNum();
//		if (!isFound && draggerChooseList.size() < limit) {
//			DraggerChooseItem item = new DraggerChooseItem(v, info);
//			draggerChooseList.add(item);
//			choosedIcon.setVisibility(VISIBLE);
//			closeBtn.setVisibility(INVISIBLE);
//			isSuccess = true;
//		}
//		v.postInvalidate();	
//		
////		if (draggerChooseList != null && mLauncher.getDrawer() != null) {
////		    ((DrawerMainView)mLauncher.getDrawer()).
////		        watcherFolderSlidingViewChooseItems(draggerChooseList);
////		}
//		
//		return isSuccess;
//	}
	
	/**
	 * 获取编辑模式下拖拽多选项中的对象
	 * @param v
	 * @param info
	 * @return 若未找到则返回null
	 */
	public DraggerChooseItem getDraggerChooseItem(ApplicationInfo info) {
		if (draggerChooseList == null || draggerChooseList.size() <= 1) {
			return null;
		}
		
		for (DraggerChooseItem item : draggerChooseList) {
			ApplicationInfo aInfo = item.getInfo();
			if (aInfo == info) {
				return item;
			}
		}
		return null;
	}

	public ArrayList<DraggerChooseItem> getDraggerChooseList() {
		return draggerChooseList;
	}	
	
	public boolean isNeedRelayoutAfterDrop() {
		return isNeedRelayoutAfterDrop;
	}
	
	/**
	 * 是否正在做聚拢或散开动画
	 * @return
	 */
	public boolean isInAnimation() {
		return isInAnimation;
	}
	
	public void setInAnimation(boolean isInAnimation) {
		this.isInAnimation = isInAnimation;
	}
	
	public void removeCloseFolderRunnable() {
		if (mCloseFolderTask != null) {
			handler.removeCallbacks(mCloseFolderTask);
		}
	}
	
	/**
	 * 判断是否为匣子里的文件夹
	 * @return
	 */
	public boolean isFolderInDrawer(){
//		return mDragoutCallback != null && mDragoutCallback instanceof DrawerMainView;
		return false;
	}
	
	/**
	 * 设置长按后的可拖动区域显示
	 */
	public void settleBackground() {
		setBackgroundResource(R.drawable.folder_fullscreen_content_bg);
	}
	
	/**
	 * 去除长按背景
	 */
	public void removeBackground() {
		setBackgroundResource(0);
	}
	
	public int getPage(){
		return getPageCount();
	}

//	public ApplicationInfo getAddAppInfo() {
//		return addAppInfo;
//	}

	public View getAddView() {
		return addView;
	}
	
	public boolean isDynamic(FolderInfo folder) {
//		if (folder.getProxyView() != null
//				&& folder.getProxyView().getTag() instanceof AnythingInfo)
//			return true;
		return false;
	}
}
