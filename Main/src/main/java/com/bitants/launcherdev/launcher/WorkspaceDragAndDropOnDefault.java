package com.bitants.launcherdev.launcher;

import android.view.View;
import android.widget.Toast;
import com.bitants.launcher.R;
import com.bitants.launcherdev.folder.model.FolderHelper;
import com.bitants.launcherdev.framework.AnyCallbacks.OnDragEventCallback;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.support.BaseCellLayoutHelper;
import com.bitants.launcherdev.launcher.touch.BaseDragController;
import com.bitants.launcherdev.launcher.touch.WorkspaceDragAndDropImpl;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.IconMaskTextView;

import java.util.ArrayList;

/**
 * Description: 处理默认桌面上Workspace的拖放
 */
public class WorkspaceDragAndDropOnDefault extends WorkspaceDragAndDropImpl {
	
	public WorkspaceDragAndDropOnDefault(Workspace mWorkspace, CellLayout.CellInfo mDragInfo){
		super(mWorkspace, mDragInfo);
//		androidStyleReorder = false;
	}
	
	//=======================================处理workspace上的拖拽响应==============================//
	@Override
	public boolean createUserFolderIfNecessary(Object dragInfo, CellLayout cellLayout, View dragOverView, DragView dragView, int[] targetCell, boolean external, ArrayList<ApplicationInfo> appList) {
		if (mWorkspace.isAllAppsIndependence((ItemInfo)dragInfo) || mWorkspace.isAllAppsIndependence(dragOverView)
				|| !willCreateUserFolder((ItemInfo) dragInfo, dragOverView, cellLayout, mTargetCell, false))
			return false;
		ApplicationInfo appInfo = (ApplicationInfo) dragInfo ;
		
		//如果是从匣子中拖出来的copy一份
		if (((ApplicationInfo) dragInfo).container == ItemInfo.NO_ID) {
			appInfo = appInfo.copy();
		}

		if (dragOverView instanceof OnDragEventCallback) {
			((OnDragEventCallback) dragOverView).onDropAni(dragView);
		}

		if (!external) {
			mWorkspace.getParentCellLayoutForView(mDragInfo.cell).removeView(mDragInfo.cell);
		}

		cellLayout.removeView(dragOverView);
		FolderIconTextView fi = ((Launcher)mLauncher).addFolder(cellLayout, LauncherSettings.Favorites.CONTAINER_DESKTOP, 
				mWorkspace.indexOfChild(cellLayout), targetCell[0], targetCell[1], "");
		if(appList != null && appList.size() > 0){
			ArrayList<ApplicationInfo> items = new ArrayList<ApplicationInfo>();
			for (ApplicationInfo applicationInfo : appList) {
				items.add(applicationInfo.copy());
			}
			fi.addItems(items);
		}else{
			fi.addItem(appInfo);
		}
		
		fi.addItem((ApplicationInfo) dragOverView.getTag());
		
		return true;
	}
	
	boolean willCreateUserFolder(ItemInfo info, View dropOverView, CellLayout dragTargetLayout, int[] targetCell, boolean considerTimeout) {
		if (dropOverView == null || !(dropOverView.getTag() instanceof ApplicationInfo))
			return false;

		boolean hasntMoved = false;
		if (mDragInfo != null) {
			hasntMoved = mWorkspace.isHansntMoved(dragTargetLayout, targetCell);
		}

		if (hasntMoved || considerTimeout) {
			return false;
		}

		return info instanceof ApplicationInfo;
	}

	@Override
	public boolean addToExistingFolderIfNecessary(DragView dragView, Object dragObject, CellLayout target, int[] targetCell, boolean external, ArrayList<ApplicationInfo> drItems) {
		if(mWorkspace.isAllAppsIndependence((ItemInfo)dragObject))
			return false;
		
		View dropOverView = target.getChildAt(targetCell[0], targetCell[1]);
		if (dropOverView instanceof FolderIconTextView) {
			FolderIconTextView fi = (FolderIconTextView) dropOverView;
			if (fi.acceptDrop(dragObject)) {
				if (dropOverView instanceof OnDragEventCallback) {
					((OnDragEventCallback) dropOverView).onDropAni(dragView);
				}
				if (!external)
					mWorkspace.getParentCellLayoutForView(mDragInfo.cell).removeView(mDragInfo.cell);
				
				ApplicationInfo info = (ApplicationInfo) dragObject ;
				
				//如果是从匣子中拖出来的copy一份
				if (((ApplicationInfo) dragObject).container == ItemInfo.NO_ID) {
					info = info.copy();
				}
				if(drItems != null && drItems.size() > 0){
					ArrayList<ApplicationInfo> items = new ArrayList<ApplicationInfo>();
					for (ApplicationInfo applicationInfo : drItems) {
						items.add(applicationInfo.copy());
					}
					fi.addItems(items);
				}else{
					fi.addItem(info);
				}
				/**
				 * 拖动图标进入文件夹统计
				 */
//				MoAnalytics.submitEvent(mContext, AnalyticsConstant.FOLDER_MERGE_INTO, FolderAnalysisConstants.FOLDER_ANALYSIS_LABEL_LAUNCHER);
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout, DragView dragView, ArrayList<ApplicationInfo> appList) {
		//处理生成或合并文件夹
		if(dragView != null){
			final int[] mDragViewVisualCenter = dragView.getDragCenterPoints();
			mTargetCell = mWorkspace.findNearestArea(mDragViewVisualCenter[0], mDragViewVisualCenter[1], 1, 1, cellLayout, mTargetCell);
			final View dragOverView = cellLayout.getChildAt(mTargetCell[0], mTargetCell[1]);
			if (createUserFolderIfNecessary(dragInfo, cellLayout, dragOverView, dragView, mTargetCell, true, appList))
				return;

			if (addToExistingFolderIfNecessary(dragView, dragInfo, cellLayout, mTargetCell, true, appList)) {
				return;
			}
			
			if(appList != null){
				if(appList.size() == 1){
					ItemInfo info = (ItemInfo) dragInfo;
					int[] targetCell = mWorkspace.estimateDropCell(x, y, info.spanX, info.spanY, null, cellLayout, null);
					if(targetCell == null){
						Toast.makeText(mContext, R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
						return;
					}
					dropToScreenExternal(cellLayout, dragInfo, targetCell);
				}else if(appList.size() > 1){
					ItemInfo info = (ItemInfo) appList.get(0);
					int[] targetCell = mWorkspace.estimateDropCell(x, y, info.spanX, info.spanY, null, cellLayout, null);
					if(targetCell == null){
						Toast.makeText(mContext, R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
						return;
					}
					dropToScreenExternal(cellLayout, dragInfo, targetCell);
					appList.remove(0);
					ArrayList<int[]> vacantCells = cellLayout.findAllVacantCellFromBottom(1, 1, null);
					BaseCellLayoutHelper.sortVacantCell(vacantCells, targetCell[0], targetCell[1]);
					
					if(vacantCells.size() == 0){//生成文件夹将其它的图标
						createUserFolderIfNecessary(dragInfo, cellLayout, cellLayout.getChildAt(targetCell[0], targetCell[1]), dragView, targetCell, true, appList);
					}else if(vacantCells.size() < appList.size()){//剩下的空cell小于所要添加的图标
						for(int i=0; i< vacantCells.size()-1; i++){
							dropToScreenExternal(cellLayout, appList.get(i), vacantCells.get(i));
						}
						String folderTitle = mContext.getResources().getString(R.string.folder_name);
						FolderIconTextView fi = ((Launcher)mLauncher).addFolder(cellLayout, LauncherSettings.Favorites.CONTAINER_DESKTOP, 
								mWorkspace.getCurrentScreen(), vacantCells.get(vacantCells.size()-1)[0], 
								vacantCells.get(vacantCells.size()-1)[1], folderTitle);
						ArrayList<ApplicationInfo> items = new ArrayList<ApplicationInfo>();
						
						for (int j = vacantCells.size()-1; j< appList.size(); j++ ) {
							items.add(appList.get(j).copy());
						}
						fi.addItems(items);
						fi.refresh();
					}else{
						for(int i=0; i< appList.size(); i++){
							dropToScreenExternal(cellLayout, appList.get(i), vacantCells.get(i));
						}
					}
				}
			}
			
		}
		
	}
	
	/**
	 * Description: 将在其他地方(非Workspace)拖动的图标放置到桌面目标位置
	 */
	@Override
	public void dropToScreenExternal(CellLayout cellLayout, Object dragInfo, int[] targetCell) {
		dropToScreenExternal(cellLayout, dragInfo, targetCell, mWorkspace.getCurrentScreen());
	}
	
	private void dropToScreenExternal(CellLayout cellLayout, Object dragInfo, int[] targetCell, int screen) {
		if(null == dragInfo || !(dragInfo instanceof ItemInfo))
			return;
		
		ItemInfo info = (ItemInfo) dragInfo;
		info.cellX = targetCell[0];
		info.cellY = targetCell[1];
		
//		if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET) {//拖动匣子中小部件特殊处理
//			PandaWidgetInfo pandaWidgetInfo = WorkspaceHelper.transformToPandaWidgetInfo(mLauncher,(LauncherWidgetInfo) dragInfo);
//			View view = mWorkspace.createViewByItemInfo(pandaWidgetInfo);
//			if (view == null)
//				return;
//			pandaWidgetInfo.screen = screen;
//			
//			pandaWidgetInfo.cellX = targetCell[0];
//			pandaWidgetInfo.cellY = targetCell[1];
//			((Launcher)mLauncher).addCustomWidgetToScreen(pandaWidgetInfo, view, screen);
//			if(info instanceof LauncherWidgetInfo)
//				MoAnalytics.submitEvent(mContext, AnalyticsConstant.WIDGET_ADD_TO_LAUNCHER_FROM_DRAWER, ((LauncherWidgetInfo)info).getTitle());
//		} else if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_SHORTCUT) {
//			View view = LauncherWidgetHelper.createShortcutByLauncherWidget(mLauncher, (LauncherWidgetInfo) info);
//			if (view == null || view.getTag() == null)
//				return;
//			
//			((Workspace)mWorkspace).addViewInCurrentScreenFromOutside(view, cellLayout, (ItemInfo) view.getTag());
//			if(info instanceof LauncherWidgetInfo)
//				MoAnalytics.submitEvent(mContext, AnalyticsConstant.WIDGET_ADD_TO_LAUNCHER_FROM_DRAWER, ((LauncherWidgetInfo)info).getTitle());
//		} else {
			//拷贝ItemInfo，防止后面对它的修改影响到原来ItemInfo!
//			if (!(dragInfo instanceof LauncherWidgetInfo) && info.container == ItemInfo.NO_ID) {
			if (info.container == ItemInfo.NO_ID) {
				info = info.copy();
				info.container = ItemInfo.NO_ID ;
			}
			View view = mWorkspace.createViewByItemInfo(info);
			if (view == null)
				return;
			
			((Workspace)mWorkspace).addViewInCurrentScreenFromOutside(view, cellLayout, info);
//		}
	}

	
	
	
	//=========================================从文件夹中拖拽出控制==================================//
	/**
	 * <p>查找拖拽app时，DragView经过的底部桌面图标</p>
	 * 
	 * @param cellLayout
	 * @param info
	 * @return
	 */
	private View getDragOverView(CellLayout cellLayout,ItemInfo info) {
		final BaseDragController dragController = mLauncher.mDragController;
		final DragView dragView = dragController.getDragView();
		int[] dragViewCenter = dragView.getDragCenterPoints();
		mTargetCellOnFolder = mWorkspace.findNearestArea(dragViewCenter[0], dragViewCenter[1], 1,1, cellLayout, mTargetCellOnFolder);
		return cellLayout.getChildAt(mTargetCellOnFolder[0], mTargetCellOnFolder[1]);
	}
	
	@Override
	public void onDrop(View target, FolderInfo folderInfo, ArrayList<Object> items) {
		FolderInfo folder = (FolderInfo)folderInfo;
		if (items == null) return;
		Object item = items.get(0);
		
		if(folder.getSize() < 2 
//				|| (folder.getProxyView() != null && folder.getProxyView().getTag() instanceof AnythingInfo)
				|| !(item instanceof ApplicationInfo))return ;
		
		if (target instanceof Workspace) {
			final CellLayout cellLayout = mWorkspace.getCurrentDropLayout() ;
			
			findCellForFolderDragout(cellLayout, (ApplicationInfo) item);
			
			/**
			 * 屏幕没有空间了
			 */
			if (mTargetCellOnFolder == null) { //桌面没有空间
				final View view = getDragOverView(cellLayout, (ApplicationInfo) item);
				/**
				 * 1、拖拽到应用程序上可合成文件夹
				 * 2、拖拽到文件夹上可放入文件夹
				 */
				if (!(view instanceof IconMaskTextView) && !(view instanceof FolderIconTextView)) {
					//MessageUtils.makeShortToast(mContext, R.string.folder_drag_out_error);
					return ;
				}
			}
		}
		
		FolderHelper.removeDragApp(folder, (ApplicationInfo) item);
		folder.checkFolderState();
		if (folder.getSize() > 1)
			folder.mFolderIcon.refresh();
	}
	
	
	
}
