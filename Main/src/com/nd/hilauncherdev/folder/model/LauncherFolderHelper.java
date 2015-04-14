package com.nd.hilauncherdev.folder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.nd.hilauncherdev.app.SerializableAppInfo;
import com.nd.hilauncherdev.framework.view.BaseLineLightBar;
import com.nd.hilauncherdev.kitset.GpuControler;
import com.nd.hilauncherdev.kitset.util.AndroidPackageUtils;
import com.nd.hilauncherdev.kitset.util.BaseBitmapUtils;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.kitset.util.ThreadUtil;
import com.nd.hilauncherdev.launcher.Launcher;
import com.nd.hilauncherdev.launcher.LauncherSettings.Favorites;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.info.FolderInfo;
import com.nd.hilauncherdev.launcher.model.BaseLauncherModel;
import com.nd.hilauncherdev.launcher.screens.CellLayout;
import com.nd.hilauncherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.nd.hilauncherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.simon.android.pandahome2.R;

/**
 * 桌面打开文件夹逻辑类
 * @author pdw
 * @date 2012-6-4 下午03:38:48 
 */
public class LauncherFolderHelper extends AbstractFolderHelper {
	
	private Bitmap mClickBitmap = null ;
	
	private Bitmap mCellLayoutBitmap = null ;
	private int workspaceToppading;
	private int cellLayoutHeight;
	private CellLayout cellLayout;
	
	private Bitmap mLineLightBarBitmap = null ;
	private int lightBarTopPadding;
	private int lightBarHeight;
	private BaseLineLightBar lightBar;
//	private LineLightBar lightBar;
	
	private Bitmap mDockBarBitmap = null ;
	private int dockBarTopPadding;
	private int dockBarHeight;
	private BaseMagicDockbar dockBar;
	
	
	public LauncherFolderHelper(Launcher mLauncher) {
		mAlpha = 60 ;
		init(mLauncher);
	}
	
	@Override
	public Bitmap onPrepareAlphaBackground(int[] loc, Rect outVRect,
			boolean hilightClickView) {
		
		super.onPrepareAlphaBackground(loc, outVRect, hilightClickView);
		//获取点击的文件夹图标
		mClickBitmap = ScreenUtil.getViewBitmap(mFolderView);
		
		// 将当前屏幕视图
//		mDragLayerBitmap = ScreenUtil.getViewCache(mDragLayer);
		
		cellLayout = mLauncher.getWorkspace().getCurrentCellLayout();
		cellLayoutHeight = cellLayout.getHeight();
		workspaceToppading = mLauncher.getWorkspace().getWorkspaceTopPadding();
		if(GpuControler.isOpenGpu(cellLayout)){//Gpu开启时，清缓存
			mLauncher.getWorkspace().destroyCurrentCellLayoutDrawingCache();
		}
		mCellLayoutBitmap = ScreenUtil.getViewCache(cellLayout);

		int[] location = new int[2];
		lightBar = mLauncher.getLightbar();
		lightBar.getLocationOnScreen(location);
		lightBarTopPadding = location[1];
		lightBarHeight = lightBar.getHeight();
		mLineLightBarBitmap = ScreenUtil.getViewCache(lightBar);
		
		dockBar = mLauncher.getDockbar();
		dockBar.getLocationOnScreen(location);
		dockBarTopPadding = location[1];
		dockBarHeight = dockBar.getHeight();
		mDockBarBitmap = ScreenUtil.getViewCache(dockBar);
		return null ;
	}

	private int folderStyle;
	@Override
	public void onFolderOpen(int folderStyle) {
		this.folderStyle = folderStyle;
		if (folderStyle == FolderIconTextView.FOLDER_STYLE_IPHONE
				|| folderStyle == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN) {
			// 隐藏workspace
			mLauncher.invisiableWorkspace();
		}
		mTopFolderView.requestFocus();
		mTopFolderView.setVisibility(View.VISIBLE);
		mTopFolderView.findViewById(R.id.folder_layout).setVisibility(
				View.VISIBLE);
	}

	@Override
	public void onFolderClose(FolderInfo folderInfo, boolean isAddMore) {
		FolderInfo folder = (FolderInfo)folderInfo;
		mTopFolderView.setFocusable(false);
		mLauncher.visiableWorkspace();
		mFolderView.setVisibility(View.VISIBLE);
		mTopFolderView.setVisibility(View.GONE);
		mTopFolderView.findViewById(R.id.folder_layout).setVisibility(View.GONE);
		if (!isAddMore) {
			updateAppPosInFolder(folder);
		}
		
		if (folder.mFolderIcon != null) {
			folder.mFolderIcon.invalidate();
		}

		if(folderStyle == FolderIconTextView.FOLDER_STYLE_IPHONE){
			if (mClickBitmap != null && !mClickBitmap.isRecycled())
				mClickBitmap.recycle();

			// 释放bitmap缓存句柄
			mClickBitmap = null;
			mCellLayoutBitmap = null;
			mLineLightBarBitmap = null;
			mDockBarBitmap = null;

			if (cellLayout != null) {
				cellLayout.setDrawingCacheEnabled(false);
				cellLayout.destroyDrawingCache();
			}

			if (lightBar != null) {
				lightBar.setDrawingCacheEnabled(false);
				lightBar.destroyDrawingCache();
			}

			if (dockBar != null) {
				dockBar.setDrawingCacheEnabled(false);
				dockBar.destroyDrawingCache();
			}
		
			System.gc();		
		}
		
	}
	
	@Override
	public void addApps2Folder(final FolderInfo folderInfo,
			ArrayList<SerializableAppInfo> list) {
		
		if (list == null)
			return ;
		
		final FolderInfo userFolderInfo = (FolderInfo)folderInfo ;
		final FolderIconTextView folderIcon = userFolderInfo.mFolderIcon;
		
		if (list == null || list.size() == 0) {
			/**
			 *  删除该文件夹
			 */
			handleAppAdd(userFolderInfo,list);
			if (userFolderInfo.getSize() < 1) {
				userFolderInfo.checkFolderState();
			} else {
				folderIcon.refresh();
			}
		} else {
			
			handleAppAdd(userFolderInfo,list);
			
			
			for(SerializableAppInfo app : list){
				ApplicationInfo addOne = new ApplicationInfo();
				addOne.title = app.title ;
				addOne.spanX = 1;
				addOne.spanY = 1;
				addOne.setActivity(app.intent.getComponent());
				addOne.iconBitmap = BaseBitmapUtils.getDefaultAppIcon(mLauncher.getResources());
				folderIcon.addItem(addOne);
			}
			
			folderIcon.refresh();
		}
	}
	
	/**
	 * <p>交叉对比添加的应用程序和文件夹中的应用程序</p>
	 * 
	 * <p>date: 2012-9-28 上午11:36:34
	 * @author pdw
	 * @param userFolderInfo
	 */
	private void handleAppAdd(FolderInfo userFolderInfo,ArrayList<SerializableAppInfo> addList) {
		ArrayList<SerializableAppInfo> addApps = addList ;
		if (addApps == null)
			addApps = new ArrayList<SerializableAppInfo>(0);
		final List<ApplicationInfo> apps = userFolderInfo.contents ;
//		final ArrayList<ApplicationInfo> removed = new ArrayList<ApplicationInfo>();
		/**
		 * 迭代文件夹中的app交叉对比添加的app列表
		 * 如果比对成功则保留，并从添加列表中删除；
		 * 如果比对不成功则将该app从文件夹中删除
		 * 此步骤的主要目的是保留文件夹中原有app的顺序
		 */
		Iterator<ApplicationInfo> iterator = apps.iterator();
		while (iterator.hasNext()){
			final ApplicationInfo app = iterator.next();
			if (app.itemType != Favorites.ITEM_TYPE_APPLICATION ) //过滤掉桌面的快捷方式
				continue ; 
			else if (!AndroidPackageUtils.isPkgInstalled(mLauncher, app.componentName.getPackageName())) //hjiang，过滤未安装的应用，如推荐文件夹内的应用
				continue;
			final SerializableAppInfo seriInfo = new SerializableAppInfo(app);
			if (addApps.contains(seriInfo)) {
				addApps.remove(seriInfo);
				continue ;
			}
			BaseLauncherModel.deleteItemFromDatabase(mLauncher, app);
//			removed.add(app);
			iterator.remove();
		}
		
//		apps.removeAll(removed);
		
		/**
		 * 重新排列剩下的快捷方式位置
		 */
		for (int index = 0 ; index < apps.size() ; index++) {
			final ApplicationInfo app = apps.get(index);
			app.screen = index ;
			BaseLauncherModel.updateItemInDatabase(mLauncher, app);
		}
//		removed.clear() ;
	}
	
	@Override
	public void renameFolder(FolderInfo folderInfo, String name) {
		mLauncher.renameFolder(folderInfo.id, name);
	}
	
	/**
	 * 更新文件夹内app的顺序
	 * 
	 * <br>create at 2012-6-26 下午03:39:31
	 * <br>modify at 2012-6-26 下午03:39:31
	 * @param folder
	 */
	private void updateAppPosInFolder(final FolderInfo folder){
//		if (folder.getProxyView() != null && folder.getProxyView().getTag() instanceof AnythingInfo) //【最近打开】【最近安装】无需更新 
//			return ;
		//更新文件夹内app位置
		if(folder.contents.size() < 2)
			return ;
		
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				//必须先排序
				Collections.sort(folder.contents, new Comparator<ApplicationInfo>() {
					@Override
					public int compare(ApplicationInfo item1, ApplicationInfo item2) {
						return item1.getPosition() - item2.getPosition();
					}
				});	
				int screen = 0 ;
				for(int index = 0 ; index < folder.contents.size() ; index++){
					ApplicationInfo app = folder.contents.get(index);
					app.screen = screen++;
					app.cellX = 0;
					app.cellY = 0;
					BaseLauncherModel.addOrMoveItemInDatabase(mLauncher, app, app.container);
				}
			}
		});
		
	}

	@Override
	public void openFolderCallback(int delayMilli) {
//		mLauncher.handleFolderClickDirectly(delayMilli);
	}

	@Override
	public void deleteFolderCallback() {
//		mLauncher.handleFolderDeleteDirectly();
	}

	@Override
	public void encriptFolderCallback() {
//		mLauncher.handleFolderEncriptDirectly();
	}

	@Override
	public void clipTop(Canvas canvas,Rect clip,int animDistance,int topMargin,int alpha) {
		super.clipTop(canvas, clip, animDistance, topMargin, alpha);
		//Log.d(Global.TAG, "clipTop,topAnim->"+animDistance+",rect->"+clip.toString());
		canvas.save();
		canvas.translate(0, animDistance);
		canvas.clipRect(clip);

//		mSrcRect.left = 0 ;
//		mSrcRect.top = 0 ;
//		mSrcRect.right = mScreenWidth ;
//		mSrcRect.bottom = clip.bottom - clip.top;
//		
//		mDestRect.left = 0 ;
//		mDestRect.top = 0;
//		mDestRect.right = mScreenWidth ;
//		mDestRect.bottom = mSrcRect.bottom;
		
		mSrcRect.left = 0 ;
		mSrcRect.top = 0 ;
		mSrcRect.right = mScreenWidth ;
		mSrcRect.bottom = clip.bottom - clip.top - workspaceToppading;
		
		mDestRect.left = 0 ;
		mDestRect.top = workspaceToppading;
		mDestRect.right = mScreenWidth ;
		mDestRect.bottom = mSrcRect.bottom + workspaceToppading;
		
		// 将当前屏幕视图
		if (mCellLayoutBitmap != null){
			if(mCellLayoutBitmap.isRecycled()){
				mCellLayoutBitmap = ScreenUtil.getViewCache(cellLayout);
			}
			canvas.drawBitmap(mCellLayoutBitmap, mSrcRect, mDestRect, mPaint);
		}
		
		//绘制点击view
		if (mClickBitmap != null)
			canvas.drawBitmap(mClickBitmap, mLocation[0], mLocation[1] - mOutRect.top,
					null);
		
		canvas.restore();
	}

	@Override
	public void clipBottom(Canvas canvas,Rect clip,int animDistance,int topMargin,int alpha) {
		super.clipBottom(canvas, clip, animDistance, topMargin, alpha);
		//Log.d(Global.TAG, "clipBottom,bottomAnim->"+animDistance+",rect->"+clip.toString());
		canvas.save();
		canvas.translate(0, animDistance);
		canvas.clipRect(clip);
		
//		mSrcRect.left = 0 ;
//		mSrcRect.right = mScreenWidth ;
//		mSrcRect.top = clip.top;
//		mSrcRect.bottom = clip.bottom ;
//		
//		mDestRect.left = 0 ;
//		mDestRect.top = clip.top ;
//		mDestRect.right = mScreenWidth ;
//		mDestRect.bottom = clip.bottom ;
		
		// CellLayout视图
		mSrcRect.left = 0 ;
		mSrcRect.right = mScreenWidth ;
		mSrcRect.top = clip.top - workspaceToppading;
		mSrcRect.bottom = cellLayoutHeight;
		
		mDestRect.left = 0 ;
		mDestRect.top = clip.top ;
		mDestRect.right = mScreenWidth ;
		mDestRect.bottom = mDestRect.top + (cellLayoutHeight - mSrcRect.top);
		if (mCellLayoutBitmap != null)
			canvas.drawBitmap(mCellLayoutBitmap, mSrcRect, mDestRect, mPaint);
		
		// LightBar视图
		mSrcRect.left = 0;
		mSrcRect.right = mScreenWidth;
		mSrcRect.top = 0;
		mSrcRect.bottom = lightBarHeight;
		
		mDestRect.left = lightBar.getLeft();
		mDestRect.top = lightBarTopPadding ;
		mDestRect.right = lightBar.getRight();
		mDestRect.bottom = lightBarTopPadding + lightBarHeight;
		if (mLineLightBarBitmap != null)
			canvas.drawBitmap(mLineLightBarBitmap, null, mDestRect, mPaint);
		
		// DockBar视图
		mSrcRect.left = 0 ;
		mSrcRect.right = mScreenWidth ;
		mSrcRect.top = 0;
		mSrcRect.bottom = dockBarHeight;
		
		mDestRect.left = 0 ;
		mDestRect.top = dockBarTopPadding ;
		mDestRect.right = mScreenWidth ;
		mDestRect.bottom = dockBarTopPadding + dockBarHeight;
		if (mDockBarBitmap != null)
			canvas.drawBitmap(mDockBarBitmap, mSrcRect, mDestRect, mPaint);
		
		//绘制点击view
		if (mClickBitmap != null)
			canvas.drawBitmap(mClickBitmap, mLocation[0], mLocation[1] - mOutRect.top,
					null);
		
		canvas.restore();
	}
	
}
