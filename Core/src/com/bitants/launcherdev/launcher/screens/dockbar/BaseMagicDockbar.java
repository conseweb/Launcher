package com.bitants.launcherdev.launcher.screens.dockbar;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.AnyCallbacks.OnDragEventCallback;
import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.model.load.LauncherBinder;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.DeleteZone;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.screens.preview.PreviewWorkspace;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.launcher.support.BaseLauncherViewHelper;
import com.bitants.launcherdev.launcher.touch.Alarm;
import com.bitants.launcherdev.launcher.touch.BaseDragController;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.touch.WorkspaceDragAndDropImpl;
import com.bitants.launcherdev.launcher.view.BaseDeleteZoneTextView;
import com.bitants.launcherdev.launcher.view.DragLayerView;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.launcher.view.icon.ui.impl.DockbarCell;
import com.bitants.launcherdev.theme.assit.ThemeUIRefreshAssit;
import com.bitants.launcherdev.theme.assit.ThemeUIRefreshListener;

/**
 *
 * @author guojianyun
 */
public class BaseMagicDockbar extends DockbarSlidingView implements DragSource, DropTarget, OnLongClickListener, ThemeUIRefreshListener{
	
	/**
	 * 最大屏幕数
	 */
	public static int DEFAULT_SCREEN_COUNT = 3;
	/**
	 * 默认屏
	 */
	public static int DEFAULT_SCREEN = 1;
	/**
	 * 每屏幕最多图标数
	 */
	public static int DEFAULT_SCREEN_ITEM_COUNT = 5;

	// drag controller
	protected BaseDragController mDragController;
	
	private Drawable mReplaceBlackground = null ;
	
	private BaseIconCache mIconCache;
	
	private boolean showAppTitle;
	
	protected View mDragView;//dock栏上被拖动的view
	protected DragView outDragView;//外部拖动进来的View
	
	protected BaseLauncher mLauncher;
	
	//以下用于dock栏图标挤动
	private final Alarm mReorderAlarm = new Alarm();//图标互换延迟处理线程
	public final static int Drag_Over = 0;//dockbar内拖动
	public final static int Drag_Enter = 1;//从外部拖到dockbar内
	public final static int Drag_Enter_Over = 2;//从外部拖到dockbar内后拖动
	public final static int Drag_Exit = 3;//从dockbar拖出
	public final static int Drag_Back = 4;//从dockbar拖出后拖回
	public final static int Drag_Back_To_Workspace = 5;//从外部拖到dockbar内后拖到Workspace
	
	public static int lastAction = -1;
	private int lastTargetCellX = -1;
	private int dragEnterCellX = -1;
	
	public boolean isOnReorderAnimation = false;//是否处于图标拖动引起的位置变换动画中
	public boolean isOnExchangeAnimation = false;//是否在与Workspace进行图标交换动画
	private boolean isOnHandlerDrop = false;//是否在处理放手事件
	
	//以下用于dock栏和workspace交换图标
	private View mIconViewInDockbar;//dock栏上被交换的View
	private View mIconViewInWorkspace;//交换后，workspace上创建的View
	private final static int exchangeAnimationDuration = 150;
	private int replaceScreen = -1, replaceCellX = -1, replaceCellY = -1;
	private int[] replaceXY = null;
		
	private DragLayerView mDockToWpAnimationView = null;
	private boolean mDockToWpAnimationClear = false;
	private boolean needCleanReorder = false;//是否拖拽结束后，清除状态
	
	private Handler mHandler = new Handler();
	
	protected static final int DRAG_MODE_NORMAL = 0; //正常拖动图标
    protected static final int DRAG_MODE_FOLDER = 1;//拖动图标合成或进入文件夹
    protected int mDragMode = DRAG_MODE_NORMAL;
	private View mLastDragOverView;
	
	public BaseMagicDockbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);	
	}
	
	public BaseMagicDockbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCurrentScreen = mDefaultScreen = DEFAULT_SCREEN;
		mReplaceBlackground = context.getResources().getDrawable(R.drawable.dockbar_replace_background);
		mIconCache = BaseConfig.getIconCache();
		showAppTitle = isShowDockbarText();
		initDockbar(context);
		applyTheme();
		ThemeUIRefreshAssit.getInstance().registerRefreshListener(this);
	}

	private void initDockbar(Context context) {
		for (int i = 0; i < DEFAULT_SCREEN_COUNT; i++) {
			DockbarCellLayout cellDockbar = (DockbarCellLayout) inflate(getContext(), R.layout.maindock_celllayout, null);
			addView(cellDockbar);
		}
	}
	

	public void setDragController(BaseDragController dragger) {
		mDragController = dragger;
	}
	
	@Override
	protected void initSelf(Context ctx) {
		
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		cleanWorkspaceState();
		reset(null);
		
		if (!success) {
			cancelReorderOnDropFail();
			needCleanReorder = true;
			clean();
			return;
		}
		
		//确保所有View处于正确位置
		fixLayout();
		
		if (target instanceof BaseDeleteZoneTextView && mDragView != null) {
			Object tag = mDragView.getTag();
			//弹框提示卸载或删除时
			if (((BaseDeleteZoneTextView) target).getDeleteZoneType() == DeleteZone.UNINSTALL_ZONE || alertOnDelete(tag)){
				DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
				int count = dockbar.getChildCount();
				for(int i  = 0; i < count; i ++){//去重
					View v = isWrongCellX(i, false);
					if(v != null){
						DockbarCellLayout.LayoutParams tLP = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
						boolean flag = false;
						for(int j = i + 1; j < count; j ++ ){
							if(findCellLayoutChildViewOnCurScreen(j) == null){
								tLP.cellX = j;
								flag = true;
								break;
							}
						}
						if(!flag){
							for(int j = i - 1; j >= 0; j -- ){
								if(findCellLayoutChildViewOnCurScreen(j) == null){
									tLP.cellX = j;
									break;
								}
							}
						}
					}
				}
				return ;
			}
		}
		
		if (!mLauncher.getScreenViewGroup().isSuccessOnDropWorkspace() && target instanceof ScreenViewGroup
				&& !BaseConfig.isOnScene())
			return ;
			
		if(!(target instanceof BaseMagicDockbar)){			
			removeInDockbarOnDropCompleted(mDragView);
			mDragController.dismissShortcutMenu();
		}
		
		mDragView = null;
	}
	
	private void fixLayout(){
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		//修正可能存在的空位
		int count = cellLayout.getChildCount();
		for(int i = 0; i < count; i ++ ){//修正可能存在的空位
			if(findRealViewByCellX(i) != null)
				continue;
			for(int j = i+1; j < count + 1; j ++){
				View view = findRealViewByCellX(j);
				if(view != null){
					DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
					lp.cellX = i;
					final ItemInfo dragApp = (ItemInfo) view.getTag();
					addOrMoveCurrentScreenItemInDatabase(dragApp, lp.cellX);
					break;
				}
			}
		}
	}
	
	
	private void cleanWorkspaceState(){
		if(mLauncher.isOnSpringMode())
			return;
		//清除拖动View引起的动画和光亮状态
		mLauncher.getScreenViewGroup().cleanAndRevertReorder();
		((CellLayout)mLauncher.getScreenViewGroup().getCurrentCellLayout()).cleanDragOutline();
	}
	
	private void removeInDockbarOnDropCompleted(View view) {
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		if (dockbar == null)
			dockbar = (DockbarCellLayout) getChildAt(0);
		
		//修正cellX
		int count = dockbar.getChildCount();
		for(int i = 0; i < count; i ++ ){//修正可能存在的空位
			if(findCellLayoutChildViewOnCurScreen(i) != null)
				continue;
			for(int j = i+1; j < count + 1; j ++){
				View v = findCellLayoutChildViewOnCurScreen(j);
				if(v != null){
					DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) v.getLayoutParams();
					lp.cellX = i;
					break;
				}
			}
		}
		
		dockbar.removeView(view);
		count = dockbar.getChildCount();
		for(int i = 0; i < count; i ++){
			View v = dockbar.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
			ItemInfo item = (ItemInfo) v.getTag();
			int cellX = resetCellX(lp.cellX);
			if(cellX != item.cellX){				
				item.cellX = cellX;
				BaseLauncherModel.updateItemInDatabase(getContext(), item);
			}
		}
	}
	
	public void removeInDockbar(View view) {
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		if (dockbar == null)
			dockbar = (DockbarCellLayout) getChildAt(0);
		
		DockbarCellLayout.LayoutParams tLP = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
		int targetCellX = tLP.cellX;
		dockbar.removeView(view);
		
		int count = dockbar.getChildCount();
		for(int i = 0; i < count; i ++){
			View v = dockbar.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
			if(lp.cellX > targetCellX){
				lp.cellX --;
				ItemInfo item = (ItemInfo) v.getTag();
				item.cellX = resetCellX(lp.cellX);
				BaseLauncherModel.updateItemInDatabase(getContext(), item);
			}
		}
	}
	
	/**
	 * 删除卸载的应用程序
	 * @param packageName
	 */
	public void removeInDockbar(final String packageName) {
		final int count = getChildCount();
		for (int i = 0 ; i < count ; i++) {
			final int screen = i;
			final DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(screen);
			post(new Runnable() {
				@Override
				public void run() {
					int childCount = dockbar.getChildCount();
					final ArrayList<View> toMove = new ArrayList<View>();
					toMove.clear() ;
					
					for(int c = 0 ; c < childCount ; c++) {
						View childView = dockbar.getChildAt(c);
						Object obj = childView.getTag();
						if (obj == null)
							continue;
						if(obj instanceof ApplicationInfo){//app
							final ApplicationInfo info = (ApplicationInfo) obj;
							if (LauncherBinder.isAllowToRemoveOnUninstall(info, packageName)) {
								BaseLauncherModel.deleteItemFromDatabase(getContext(), info);
								toMove.add(childView);
							}
						}else if(obj instanceof FolderInfo){//文件夹
							final FolderInfo info = (FolderInfo) obj;
							final List<ApplicationInfo> contents = info.contents;
							final ArrayList<ApplicationInfo> toRemove = new ArrayList<ApplicationInfo>(1);
							final int contentsCount = contents.size();

							for (int k = 0; k < contentsCount; k++) {
								final ApplicationInfo info2 = contents.get(k);
								if (LauncherBinder.isAllowToRemoveOnUninstall(info2, packageName)) {
									toRemove.add(info2);
									BaseLauncherModel.deleteItemFromDatabase(mLauncher, info2);
								}
							}

							contents.removeAll(toRemove);
							if(toRemove.size() > 0){								
								childView.invalidate();
							}
							
							if (info.getSize() <= 0) {
								BaseLauncherModel.deleteItemFromDatabase(mLauncher, info);
								toMove.add(childView);
							}
						}
						
					}
					
					int rmSize = toMove.size();
					for (int j = 0; j < rmSize; j++) {
						View child = toMove.get(j);
						dockbar.removeViewInLayout(child);
						if (child instanceof DropTarget) {
							mDragController.removeDropTarget((DropTarget) child);
						}
					}

					//补空
					fixCellAfterRemove(screen);
					
					if (rmSize > 0) {
						dockbar.requestLayout();
						dockbar.invalidate();
					}
				}
			});
		}
	}
	
	/**
	 * 删除view后，补空修正剩余view的位置
	 * @param screen
	 */
	public void fixCellAfterRemove(int screen){
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(screen);
		int childCount = dockbar.getChildCount();
		for(int i = 0; i < childCount; i ++ ){//补空
			if(findCellLayoutChildView(i, screen) == null){
				for(int j = i+1; j < childCount + 1; j ++){
					View view = findCellLayoutChildView(j, screen);
					if(view != null){
						DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
						lp.cellX = i;
						ItemInfo item = (ItemInfo) view.getTag();
						item.cellX = resetCellX(lp.cellX);
						BaseLauncherModel.addOrMoveItemInDatabase(getContext(), item, BaseLauncherSettings.Favorites.CONTAINER_DOCKBAR);
						break;
					}
				}
			}
		}
	}
	
	private View createView(Object itemInfo){
		if(itemInfo instanceof ApplicationInfo){						
			return BaseLauncherViewHelper.createDockShortcut(mLauncher, (ApplicationInfo) itemInfo);
		}else if(itemInfo instanceof FolderInfo){
			return BaseLauncherViewHelper.createFolderIconTextViewFromContext(mLauncher, (FolderInfo)itemInfo);
		}
		return null;
	}
	
	/**
	 * 是否准备好接收拖放事件
	 * @return
	 */
	private boolean notReadyForDragAndDrop(){
		return mLauncher.isOnSpringMode() || mLauncher.getScreenViewGroup().getVisibility() != VISIBLE;
	}
	
	/**
	 * 是否允许该类型数据拖放到dock栏
	 * @param source
	 * @param dragInfo
	 * @return
	 */
	protected boolean notAllowDragOnDockbar(DragSource source, Object dragInfo){
		return !(dragInfo instanceof ApplicationInfo || dragInfo instanceof FolderInfo) 
				|| mDragController.isOnMultiSelectedDrag() || mLauncher.isFolderOpened();
	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		if(notReadyForDragAndDrop())
			return;
		cleanWorkspaceState();
		outDragView = null;
		
		int cellX = 0;
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		if (dockbar == null)
			return;
		
		int count = dockbar.getChildCount();
		
		if(isFullDockAndIOSReorderStyle(source)){
			ItemInfo dragApp = (ItemInfo) dragInfo ;
			if(count > 0){
				cellX = findCellX(x);
				View rmView = findCellLayoutChildViewOnCurScreen(cellX);
				if(rmView != null){
					ItemInfo dragOverInfo = (ItemInfo) rmView.getTag();
					dragOverInfo.screen = dragApp.screen;
					dragOverInfo.cellX = dragApp.cellX;
					dragOverInfo.cellY = dragApp.cellY;
					mLauncher.mWorkspace.addInScreen(createView(dragOverInfo), dragOverInfo.screen, dragOverInfo.cellX, dragOverInfo.cellY, 1, 1);
					BaseLauncherModel.addOrMoveItemInDatabase(getContext(), dragOverInfo, BaseLauncherSettings.Favorites.CONTAINER_DESKTOP);
					dockbar.removeViewInLayout(rmView);
				}
			}
			
			addInDockbarByCell(createView(dragApp), mCurrentScreen, cellX ,true);
			addOrMoveCurrentScreenItemInDatabase(dragApp, cellX);
			return;
		}
		
		//若匣子拖出，保存状态
		ItemInfo dragApp = (ItemInfo) dragInfo;
		boolean isFromDrawerFolder = false;
		long idFromDrawer = 0;
		if(dragApp.container == ItemInfo.NO_ID){//从匣子拖出，复制一份
			dragApp = dragApp.copy();
			dragApp.container = ItemInfo.NO_ID ;
			isFromDrawerFolder = dragApp instanceof FolderInfo;
			idFromDrawer = dragApp.id;
		}
		
		if(isFullDockAndDragFromDrawer(source)){//从匣子拖入时特殊处理，不将图标挤到Workspace
			if(count > 0){				
				cellX = findCellX(x);
				View rmView = findCellLayoutChildViewOnCurScreen(cellX);
				if(rmView != null){
					if(mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo)rmView.getTag())){//匣子入口图标不被替换		
						View lastView = findCellLayoutChildViewOnCurScreen(lastTargetCellX);
						if(lastView != null){
							lastView.setBackgroundResource(0);
							lastView.setBackgroundDrawable(null);
						}
						lastTargetCellX = -1;
						Toast.makeText(getContext(), R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
						return;
					}else if(rmView instanceof FolderIconTextView && addToExistingFolderIfNecessary(source, dragView, dragApp, rmView)){//文件夹
						reset(dragView);
						return;
					}
					BaseLauncherModel.deleteItemFromDatabase(getContext(), (ItemInfo) rmView.getTag());
					dockbar.removeViewInLayout(rmView);
				}
			}
			
			addInDockbarByCell(createView(dragApp), mCurrentScreen, cellX ,true);
			dragApp.container = ItemInfo.NO_ID;
			addOrMoveCurrentScreenItemInDatabase(dragApp, cellX);
			
			//如果是来自匣子的文件夹
			if(isFromDrawerFolder){
				addDrawerFolderItemsToDB(dragApp, idFromDrawer);
			}
			return;
		}
		
		int countTmp = count;
		if(countTmp != 0){			
			if(lastAction == Drag_Enter || lastAction == Drag_Enter_Over){				
				countTmp ++;
				cellX = findCellXAfterDragEnter(x);
			}else{
				cellX = findCellX(x);
				View tempView = findCellLayoutChildViewOnCurScreen(cellX);
				if(tempView != null && !(tempView instanceof FolderIconTextView)){//如果该位置被占用,查找空位
					for(int i = 0; i < countTmp; i ++){
						if(findCellLayoutChildViewOnCurScreen(i) == null){
							cellX = i;
							break;
						}
					}
				}
			}
		}
		View dragOverView = findCellLayoutChildViewOnCurScreen(cellX);
		if(addToExistingFolderIfNecessary(source, dragView, dragInfo, dragOverView)){
			reset(dragView);
			return;
		}
		
		if(dragOverView != null){//处理外部直接拖入放手的情况
			for(int i = 0; i < count; i ++){
				View v = dockbar.getChildAt(i);
				DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
				if(lp.cellX >= cellX)
					lp.cellX ++;
			}
		}
		
		if(mIconViewInDockbar != null){//处理与Workspace交换图标情况		
			dockbar.removeViewInLayout(mIconViewInDockbar);
			mIconViewInDockbar = null;
			mIconViewInWorkspace = null;
		}
		
		if(source instanceof BaseMagicDockbar){
			DockbarCellLayout.LayoutParams dragViewLP = (DockbarCellLayout.LayoutParams)dragView.getDragingView().getLayoutParams();
			dragViewLP.cellX = cellX;
		}
		
		count = dockbar.getChildCount();
		for(int i = 0; i < count; i ++){
			View v = dockbar.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
			ItemInfo item = (ItemInfo) v.getTag();
			int targetCellX = resetCellX(lp.cellX);
			if(targetCellX != item.cellX){				
				item.cellX = targetCellX;
				BaseLauncherModel.updateItemInDatabase(getContext(), item);
			}
		}
		
		if(!(source instanceof BaseMagicDockbar)){
			View shortcut = createView(dragApp);
			if(shortcut != null){
				addInDockbarByCell(shortcut, mCurrentScreen, cellX ,true);
			}
		}
		
		addOrMoveCurrentScreenItemInDatabase(dragApp, cellX);
		
		//如果是来自匣子的文件夹
		if(isFromDrawerFolder){
			addDrawerFolderItemsToDB(dragApp, idFromDrawer);
		}
		
		reset(dragView);
	}
	
	private void addDrawerFolderItemsToDB(ItemInfo dragApp, long idFromDrawer){
		FolderInfo folderInfo = (FolderInfo) dragApp;
		handleDropDrawerFolderEx(folderInfo, idFromDrawer);
		ArrayList<ApplicationInfo> applications = new ArrayList<ApplicationInfo>();
		for (int i = 0; i < folderInfo.contents.size(); i++) {
			ApplicationInfo appInfo = folderInfo.contents.get(i);
			appInfo.container = dragApp.id;
			appInfo.screen = i;
			applications.add(appInfo);
		}
		BaseLauncherModel.addItemsToDatabase(mLauncher, applications);
	}
	
	private boolean addToExistingFolderIfNecessary(DragSource source, DragView dragView, Object dragObject, View dropOverView){
		if(mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo)dragObject) 
				|| !(dropOverView instanceof FolderIconTextView)
				|| !(dragObject instanceof ApplicationInfo)
//				|| !(source instanceof ScreenViewGroup)
				){
			return false;
		}
		
		FolderIconTextView fi = (FolderIconTextView) dropOverView;
		if (fi.acceptDrop(dragObject)) {
			if (dropOverView instanceof OnDragEventCallback) {
				((OnDragEventCallback) dropOverView).onDropAni(dragView);
			}
//			CellLayout.LayoutParams lp = (CellLayout.LayoutParams)dropOverView.getLayoutParams();
//			if(lp.isOnReorderAnimation){//防止同时进行多个动画，产生多个View或动画不正确情况
//				dropOverView.clearAnimation();
//			}
			if(!mDragController.isDragFromDrawer(source)){				
				((ViewGroup)dragView.getDragingView().getParent()).removeView(dragView.getDragingView());
			}
			
			ApplicationInfo info = (ApplicationInfo) dragObject ;
			
			fi.addItem(info);
//			startFolderAnimation((DragController)mDragController,false,fi);
			startFolderAnimation(fi);
			
			return true;
		}
		
		return false;
		
	}
	
	private void addOrMoveCurrentScreenItemInDatabase(ItemInfo dragApp, int cellX){
		dragApp.screen = mCurrentScreen;
		dragApp.cellX = cellX;
		dragApp.cellY = 0;
		dragApp.spanX = 1;
		dragApp.spanY = 1;
		if(BaseConfig.isOnScene()){
			int[] wh = DockbarCellLayoutConfig.spanXYToWh(1, 1);
			dragApp.cellX = cellX * getCellWidth();
			dragApp.cellY = 0;
			dragApp.spanX = wh[0];
			dragApp.spanY = wh[1];
		}
		
		BaseLauncherModel.addOrMoveItemInDatabase(getContext(), dragApp, BaseLauncherSettings.Favorites.CONTAINER_DOCKBAR);
	}
	
	public void updateItemInDockbar(final List<ApplicationInfo> apps) {
		if (apps == null || mIconCache == null)
			return;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(i);
			post(new Runnable() {
				@Override
				public void run() {
					try {
						int childCount = dockbar.getChildCount();
						int updateCount = 0;
						final ArrayList<View> toMove = new ArrayList<View>();
						for (int c = 0; c < childCount; c++) {
							View childView = dockbar.getChildAt(c);
							Object obj = childView.getTag();
							if (obj == null)
								continue;

							if(obj instanceof ApplicationInfo){
								final ApplicationInfo info = (ApplicationInfo) obj;
								final Intent intent = info.intent;
								if (intent == null || intent.getComponent() == null)
									continue;

								final ComponentName name = intent.getComponent();
								if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
									final int appCount = apps.size();
									for (int k = 0; k < appCount; k++) {
										ApplicationInfo app = apps.get(k);
										if (app.componentName.equals(name)) {
											info.iconBitmap = mIconCache.refreshTheCache(info);
											//info.iconBitmap = mIconCache.getIcon(info);
											((DockbarCell) childView).setIconBitmap(info.iconBitmap);
											childView.invalidate();
											updateCount++;
										} else if (app.componentName.getPackageName().equals(name.getPackageName())) {// 覆盖安装后应用的入口类名匹配不上
											if (1 == appCount) {// 若应用只匹配出一个入口，则把图标更新为该入口
												info.setActivity(app.componentName);
												info.iconBitmap = mIconCache.getIcon(info);
												((DockbarCell) childView).setIconBitmap(info.iconBitmap);
												childView.invalidate();
												BaseLauncherModel.updateItemInDatabase(mLauncher, info);
												updateCount++;
											} else {// 匹配出多个入口，则删除该图标 caizp 2013-4-2
												BaseLauncherModel.deleteItemFromDatabase(mLauncher, info);
												toMove.add(childView);
											}
										}
									}
								}
							} else if (obj instanceof FolderInfo) {
								final FolderInfo info = (FolderInfo) obj;
								final List<ApplicationInfo> contents = info.contents;
								ArrayList<ApplicationInfo> toRemove = null;
								final int contentsCount = contents.size();

								for (int k = 0; k < contentsCount; k++) {
									final ApplicationInfo applicationInfo = contents.get(k);
									final Intent intent = applicationInfo.intent;
									if (intent == null || intent.getComponent() == null)
										continue;

									final ComponentName name = intent.getComponent();

									if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
										final int appCount = apps.size();
										for (int m = 0; m < appCount; m++) {
											ApplicationInfo app = apps.get(m);
											if (app.componentName.equals(name)) {
												applicationInfo.iconBitmap = mIconCache.refreshTheCache(applicationInfo);
												//applicationInfo.iconBitmap = mIconCache.getIcon(applicationInfo);
												childView.invalidate();
											} else if (app.componentName.getPackageName().equals(name.getPackageName())) {// 覆盖安装后应用的入口类名匹配不上
												if (1 == appCount) {// 若应用只匹配出一个入口，则把图标更新为该入口
													applicationInfo.setActivity(app.componentName);
													applicationInfo.iconBitmap = mIconCache.getIcon(applicationInfo);
													childView.invalidate();
													BaseLauncherModel.updateItemInDatabase(mLauncher, applicationInfo);
												} else {// 匹配出多个入口，则删除该图标 caizp 2013-4-2
													BaseLauncherModel.deleteItemFromDatabase(mLauncher, applicationInfo);
													if (toRemove == null)
														toRemove = new ArrayList<ApplicationInfo>();
													
													toRemove.add(applicationInfo);
												}
											}
										}
									}
								}
								
								if (toRemove != null)
									contents.removeAll(toRemove);
							}
							
						}

						childCount = toMove.size();
						for (int j = 0; j < childCount; j++) {
							View child = toMove.get(j);
							dockbar.removeViewInLayout(child);
							if (child instanceof DropTarget) {
								mDragController.removeDropTarget((DropTarget) child);
							}
						}

						if (childCount > 0 || updateCount > 0) {
							dockbar.requestLayout();
							dockbar.invalidate();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(mLauncher, "Updating workspace is someting wrong:)", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		if(notReadyForDragAndDrop())
			return;
		
		//清除光亮和celllayout中View位置移动动画
		cleanWorkspaceState();
		setOnHandlerDrop(false);
		
		if(!(source instanceof BaseMagicDockbar)){			
			outDragView = dragView;
		}
		
		if (notAllowDragOnDockbar(source, dragInfo))
			return;
		
		if(handleNoReorderMode(source, x, dragView, dragInfo))
			return;
		
		//处理文件夹情况
		ScreenViewGroup mWorkspace = mLauncher.mWorkspace;
		CellLayout cl = mWorkspace.getCurrentCellLayout();
		//是否需要与Workspace上的图标交换位置
		boolean isOnExchangeWorkspaceItemMode = !(source instanceof BaseMagicDockbar) && 
				((ViewGroup) getChildAt(mCurrentScreen)).getChildCount() == DEFAULT_SCREEN_ITEM_COUNT && !isOnExchangeAnimation
				&& !cl.isOnReorderAnimation() && !cl.isOnReorderHintAnimation()
				&& !cl.isItemPlacementDirty();
		if(handleMergeFolder(x, dragView, dragInfo) && !isOnExchangeWorkspaceItemMode)
			return;
		
		if(source instanceof BaseMagicDockbar){
			if(!mReorderAlarm.alarmPending() && !isOnReorderAnimation && mDragMode != DRAG_MODE_FOLDER){
				int targetCellX = findCellX(x);
				DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
				if(lastAction == Drag_Exit){
					//Log.e("onDragBack", "onDragBack");
					Reorder listener = new Reorder(Drag_Back, cellLayout, targetCellX, -1);
					mReorderAlarm.setOnAlarmListener(listener);
		            mReorderAlarm.setAlarm(200);
		            
		            lastTargetCellX = targetCellX;
					lastAction = Drag_Back;
				}else{
					if(findCellLayoutChildViewOnCurScreen(targetCellX) == null)
						return;
					if(lastTargetCellX == targetCellX && (lastAction == Drag_Over || lastAction == Drag_Back))
						return;
					int srcCellX = ((DockbarCellLayout.LayoutParams)dragView.getDragingView().getLayoutParams()).cellX;
					if(lastAction == -1 && targetCellX == srcCellX)
						return;
					if(lastAction == Drag_Over || lastAction == Drag_Back){
						srcCellX = lastTargetCellX;
					}
					if(findCellLayoutChildViewOnCurScreen(srcCellX) != null){//修正
						srcCellX = findInvisableViewCellX(dragView);
					}
					//Log.e("onDragOver", "onDragOver  " + srcCellX + "," + targetCellX + "," + lastTargetCellX);
					Reorder listener = new Reorder(Drag_Over, cellLayout, targetCellX, srcCellX);
					mReorderAlarm.setOnAlarmListener(listener);
		            mReorderAlarm.setAlarm(200);
		            
		            lastTargetCellX = targetCellX;
					lastAction = Drag_Over;
				}
				
			}
		} else {//从外部拖动图标
			needCleanReorder = true;
			//如果dock栏上5个图标，将进行交换操作
			if(isOnExchangeWorkspaceItemMode){
				int targetCellX = findCellX(x);
				View exchangeView = findCellLayoutChildViewOnCurScreen(targetCellX);
				if(exchangeView == null)
					return;
				
				if(replaceScreen < 0){//初次拖动时
					if(source instanceof ScreenViewGroup){
						View v = dragView.getDragingView();
						replaceScreen = ((ItemInfo)v.getTag()).screen;
					}else{
						int[] cellXY = findCellXYForExchange();
						if(cellXY != null){							
							replaceScreen = mWorkspace.getCurrentScreen();
						}
					}
				}
				
				//初次拖动时
				if(source instanceof ScreenViewGroup){
					if(replaceCellX < 0 || cl.getChildAt(replaceCellX, replaceCellY) != null){
						View v = dragView.getDragingView();
						CellLayout.LayoutParams lp = (CellLayout.LayoutParams)v.getLayoutParams();
						replaceCellX = lp.cellX;
						replaceCellY = lp.cellY;
						replaceXY = new int[2];
						v.getLocationOnScreen(replaceXY);
					}
				}else{//从匣子或文件夹拖出
					if(replaceCellX < 0){
						int[] cellXY = findCellXYForExchange();
						if(cellXY != null){
							replaceCellX = cellXY[0];
							replaceCellY = cellXY[1];
							replaceXY = new int[]{replaceCellX * (cl.getCellWidth() + cl.getCellGapX()),
									replaceCellY * (cl.getCellHeight() + cl.getCellGapY()) + mWorkspace.getTopPadding()};
						}
					}
					
				}
				
				if(replaceXY == null){
					return;
				}
				
				int[] targetXY = new int[]{replaceXY[0], replaceXY[1]};
				
				if(replaceXY != null && replaceScreen != mWorkspace.getCurrentScreen()){//拖动切换屏幕
					if(source instanceof ScreenViewGroup){//桌面
						targetXY[0] = replaceXY[0] + (replaceScreen - mWorkspace.getCurrentScreen()) * getWidth();
					}else{//文件夹或匣子
						int[] cellXY = findCellXYForExchange();
						if(cellXY != null){
							replaceScreen = mWorkspace.getCurrentScreen();
							replaceCellX = cellXY[0];
							replaceCellY = cellXY[1];
//							targetXY = CellLayoutConfig.getXY(replaceCellX, replaceCellY);
							targetXY = new int[]{replaceCellX * (cl.getCellWidth() + cl.getCellGapX()),
									replaceCellY * (cl.getCellHeight() + cl.getCellGapY()) + mWorkspace.getTopPadding()};
							replaceXY[0] = targetXY[0];
							replaceXY[1] = targetXY[1];
						}else{
							targetXY = replaceXY = null;
						}
					}
				}
				
				if(replaceScreen < 0 || replaceCellX < 0 || targetXY == null){
					return;
				}
				
				//开始动画
				if(mIconViewInDockbar == null && !isNotAllowToExchangeView(exchangeView, dragInfo)){
					mIconViewInDockbar = exchangeView;
					animationViewFromDockbarToWorkspace(mIconViewInDockbar, targetXY[0], targetXY[1]);
				}else{
					if(mIconViewInWorkspace == null){
						return;
					}
					if(animationViewFromWorkspaceToDockbar() && !isNotAllowToExchangeView(exchangeView, dragInfo)){
						//dock栏图标飞到Workspace动画
						mIconViewInDockbar = exchangeView;
						animationViewFromDockbarToWorkspace(mIconViewInDockbar, targetXY[0], targetXY[1]);
					}
				}
			}else if(((ViewGroup) getChildAt(mCurrentScreen)).getChildCount() < DEFAULT_SCREEN_ITEM_COUNT && 
					!mReorderAlarm.alarmPending() && !isOnReorderAnimation && mDragMode != DRAG_MODE_FOLDER){//如果dock栏上少于5个图标，从外部拖动图标时，将挤动出新位置
				DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
				int targetCellX = findCellXAfterDragEnter(x);
				if(lastAction == -1 || lastAction == Drag_Back_To_Workspace){
					if(lastTargetCellX == targetCellX && lastAction == Drag_Enter)
						return;
					//Log.e("Drag_Enter", "Drag_Enter " + lastAction);
					dragEnterCellX = targetCellX;
					lastTargetCellX = targetCellX;
					lastAction = Drag_Enter;
					
					Reorder listener = new Reorder(Drag_Enter, cellLayout, targetCellX, -1);
					mReorderAlarm.setOnAlarmListener(listener);
		            mReorderAlarm.setAlarm(200);
				}else{
					if(lastTargetCellX == targetCellX && (lastAction == Drag_Enter_Over || lastAction == Drag_Enter))
						return;
					int srcCellX = lastTargetCellX;
					//Log.e("Drag_Enter_Over", "Drag_Enter_Over " + srcCellX +"," + targetCellX);
					lastTargetCellX = targetCellX;
					lastAction = Drag_Enter_Over;
					
					Reorder listener = new Reorder(Drag_Enter_Over, cellLayout, targetCellX, srcCellX);
					mReorderAlarm.setOnAlarmListener(listener);
		            mReorderAlarm.setAlarm(200);
				}
			}
			
		}
	}
	
	/**
	 * 处理不进行挤动情况，如下：
	 * 1.dock栏有5个图标并且从匣子拖入
	 * 2.为类IOS风格挤动，且无空间情况
	 * 
	 * @param source
	 * @param x
	 * @param dragView
	 * @param dragInfo
	 * @return
	 */
	private boolean handleNoReorderMode(DragSource source, int x, DragView dragView, Object dragInfo){
		if(isFullDockAndDragFromDrawer(source) || isFullDockAndIOSReorderStyle(source)){
			int targetCellX = findCellX(x);
			View rmView = findCellLayoutChildViewOnCurScreen(targetCellX);
			if(rmView != null && !mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo)rmView.getTag()) && 
				(rmView instanceof DockbarCell || (rmView instanceof FolderIconTextView && 
						dragView.getDragingView() instanceof FolderIconTextView))){//设置替换背景
				rmView.setBackgroundResource(0);
				rmView.setBackgroundDrawable(mReplaceBlackground);
				
				
			}else if(rmView instanceof FolderIconTextView){
				handleMergeFolder(x, dragView, dragInfo);
			}
			
			if(lastTargetCellX != targetCellX){
				View lastView = findCellLayoutChildViewOnCurScreen(lastTargetCellX);
				if(lastView != null){
					lastView.setBackgroundResource(0);
					lastView.setBackgroundDrawable(null);
				}
				
				if(lastView instanceof FolderIconTextView && mDragMode == DRAG_MODE_FOLDER){
					((FolderIconTextView)lastView).onExitAni(dragView);
					mDragMode = DRAG_MODE_NORMAL;
				}
				
				lastTargetCellX = targetCellX;
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * 处理合并文件夹动画
	 * @param x
	 * @param dragView
	 * @param dragInfo
	 */
	private boolean handleMergeFolder(int x, DragView dragView, Object dragInfo){
		int dropCellX = findCellX(x);
		View dragOverView = findCellLayoutChildViewOnCurScreen(dropCellX);
		if(dragOverView != null && !isOnExchangeAnimation){
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)dragOverView.getLayoutParams();
			if(dragInfo instanceof ApplicationInfo && dragOverView instanceof FolderIconTextView
					&& !mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo)dragInfo)
					&& !lp.isOnReorderAnimation){//添加到文件夹
				int[] xy = dragView.getDragCenterPoints();
				int[] loc = new int[2];
				dragOverView.getLocationOnScreen(loc);
				loc[0] += dragOverView.getWidth()/2;
				loc[1] += dragOverView.getHeight()/2;
				float distance = (float) Math.sqrt( Math.pow(xy[0] - loc[0], 2) +
		                Math.pow(xy[1] - loc[1], 2));

				ViewGroup dockCellLayout = (ViewGroup) getChildAt(mCurrentScreen);
				final int count = dockCellLayout.getChildCount();
				if(count == 0)
					return false;
//				int cellWidth = dockCellLayout.getWidth() / count;
//				float mMaxDistanceForFolderCreation = (float) (0.45*cellWidth);
				float mMaxDistanceForFolderCreation = (0.5f * getContext().getResources().getDimensionPixelSize(R.dimen.app_icon_size));
				if(mDragMode != DRAG_MODE_FOLDER && distance < mMaxDistanceForFolderCreation){
					((FolderIconTextView)dragOverView).onEnterAni(dragView);
					mDragMode = DRAG_MODE_FOLDER;
					mLastDragOverView = dragOverView;
//					Log.e("onEnterAni", "000"+ ((FolderIconTextView)mLastDragOverView).text);
					return true;
				}
				if(mDragMode == DRAG_MODE_FOLDER && distance >= mMaxDistanceForFolderCreation && mLastDragOverView != null){
//					Log.e("onExitAni", "1" + ((FolderIconTextView)mLastDragOverView).text);
					((FolderIconTextView)mLastDragOverView).onExitAni(dragView);
					mDragMode = DRAG_MODE_NORMAL;
					mLastDragOverView = dragOverView;
					return true;
				}
			}
			
			if (mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null && dragOverView != mLastDragOverView){
//				Log.e("onExitAni", "2"+ ((FolderIconTextView)mLastDragOverView).text);
				((FolderIconTextView)mLastDragOverView).onExitAni(dragView);
				mDragMode = DRAG_MODE_NORMAL;
				mLastDragOverView = dragOverView;
				return true;
			}
		} else{
			if (mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null){
//				Log.e("onExitAni", "3"+ ((FolderIconTextView)mLastDragOverView).text);
				((FolderIconTextView)mLastDragOverView).onExitAni(dragView);
				mDragMode = DRAG_MODE_NORMAL;
				mLastDragOverView = dragOverView;
				return true;
			}
		}
		
		return mDragMode == DRAG_MODE_FOLDER;
	}
	
	/**
	 * 不允许dock栏与Workspace互换图标
	 * @param exchangeView
	 * @param dragInfo
	 * @return
	 */
	private boolean isNotAllowToExchangeView(View exchangeView, Object dragInfo){
		return exchangeView instanceof FolderIconTextView && dragInfo instanceof ApplicationInfo
				&& !mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo)dragInfo);
	}
	
	private int findInvisableViewCellX(DragView dragView){
		ViewGroup dockCellLayout = (ViewGroup) getChildAt(mCurrentScreen);
		int count = dockCellLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			if(findCellLayoutChildViewOnCurScreen(i) == null)
				return i;
		}
		return ((DockbarCellLayout.LayoutParams)dragView.getDragingView().getLayoutParams()).cellX;
	}
	
	private int findCellXAfterDragEnter(int x){
		ViewGroup dockCellLayout = (ViewGroup) getChildAt(mCurrentScreen);
		int count = dockCellLayout.getChildCount();
		if(count == 0)
			return 0;
		count ++;
		int cellWidth = dockCellLayout.getWidth() / count;
		for (int i = 0; i < count; i++) {
			if(x >= cellWidth * i && x <= cellWidth * (i + 1)){
				return i;
			}
		}
		return 0;
	}
	
	private int findCellX(int x){
		ViewGroup dockCellLayout = (ViewGroup) getChildAt(mCurrentScreen);
		final int count = dockCellLayout.getChildCount();
		if(count == 0)
			return 0;
		int cellWidth = dockCellLayout.getWidth() / count;
		for (int i = 0; i < count; i++) {
			if(x >= cellWidth * i && x <= cellWidth * (i + 1)){
				return i;
			}
		}
		return 0;
	}
	
	private int getCellWidthOnReorder(){
		ViewGroup dockCellLayout = (ViewGroup) getChildAt(mCurrentScreen);
		final int count = dockCellLayout.getChildCount();
		if(count == 0)
			return dockCellLayout.getWidth();
		return dockCellLayout.getWidth() / count;
	}
	
	public int resetCellX(int cellX){
		return BaseConfig.isOnScene() ? cellX * getCellWidth() : cellX;
	}
	
	private int getCellWidth(){
		return DockbarCellLayoutConfig.getCellWidth();
	}
	
	private int getCellHeight(){
		return DockbarCellLayoutConfig.getCellHeight();
	}

	/**
	 * 查找目标view
	 * @param x 手指的x坐标
	 * @return
	 */
	public View getDockCellAt(int x) {
		ViewGroup dockCellLayout = (ViewGroup) getChildAt(mCurrentScreen);
		final int count = dockCellLayout.getChildCount();
		if(count == 0)
			return null;
//		int cellWidth = DockbarCellLayoutConfig.getCellWidth();
		int cellWidth = dockCellLayout.getWidth() / count;
		if(BaseConfig.isOnScene()){
			cellWidth = 1;
		}
		for (int i = 0; i < count; i++) {
			View child = dockCellLayout.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)child.getLayoutParams();
			int width = cellWidth;
			if(lp.isOnPending){
				width = lp.width;
			}
			if(lp.cellX * width <= x && x <= lp.cellX * width + lp.spanX * width){
				return child;
			}
		}
		return null;
	}
	
	/**
	 * 查找该索引位置的View，不包括不可见View
	 * 
	 * @param cellX
	 * @return 该索引位置的dock,否则null
	 */
	public View findCellLayoutChildViewOnCurScreen(int cellX) {
		return findCellLayoutChildView(cellX, mCurrentScreen);
	}
	
	public View findCellLayoutChildView(int cellX, int screen) {
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(screen);
		if(dockbar == null)
			return null;
		for (int i = 0; i < dockbar.getChildCount(); i++) {
			View view = dockbar.getChildAt(i);
			if(view.getVisibility() != VISIBLE)
				continue;
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
			if (lp.cellX == cellX)
				return view;
		}
		return null;
	}
	
	/**
	 * 查找该索引位置的View，包括不可见View
	 * @param cellX
	 * @return
	 */
	public View findRealViewByCellX(int cellX) {
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		if(dockbar == null)
			return null;
		for (int i = 0; i < dockbar.getChildCount(); i++) {
			View view = dockbar.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
			if (lp.cellX == cellX)
				return view;
		}
		return null;
	}

	public View findViewByCellXOnDropFail(int cellX, View ingoreView) {
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		for (int i = 0; i < dockbar.getChildCount(); i++) {
			View view = dockbar.getChildAt(i);
			view.setVisibility(VISIBLE);
			if(ingoreView != null && view == ingoreView)
				continue;
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
			if (lp.cellX == cellX)
				return view;
		}
		return null;
	}
	
	
	public View isWrongCellX(int cellX, boolean ignoreInvisableView) {
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		ArrayList<View> list = new ArrayList<View>();
		for (int i = 0; i < dockbar.getChildCount(); i++) {
			View view = dockbar.getChildAt(i);
			if(ignoreInvisableView && view.getVisibility() != VISIBLE)
				continue;
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
			if (lp.cellX == cellX){
				list.add(view);
			}
			if(list.size() >= 2){
				for(View v : list){
					if(v.getVisibility() == VISIBLE)
						return v;
				}
			}
		}
		return null;
	}
	
	/**
	 * Description: 适用于基于Cell布局
	 * Author: guojy
	 * Date: 2013-5-14 下午3:36:12
	 */
	public void addInDockbarByCell(View child, int page, int cellX,boolean showAni) {
		if(!BaseConfig.isOnScene()){
			addInDockbar(child, page, cellX, 0, 1, 1, showAni);
		}else{			
			addInDockbar(child, page, cellX * getCellWidth(), 0, getCellWidth(), getCellHeight(), showAni);
		}
	}
	
	public void addInDockbar(View child, int page, int cellX, int cellY, int spanX, int spanY, boolean showAni) {
		if (page < 0 || page >= getChildCount()) {
			Log.e(BaseConfig.TAG, "dockbar: The page must be >= 0 and < " + getChildCount() + " (was " + page + "); skipping child");
			return;
		}
		
		DockbarCellLayout group = (DockbarCellLayout) getChildAt(page);
		
		group.addView(child, cellX, cellY, spanX, spanY);
		
		if (child instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) child);
		}
//		if (child != null && child instanceof DockbarCell) {
			child.setOnLongClickListener(this);
//		}
		
		if (showAni) {
			child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.workspace_app_enter));
		}
	}
	
	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		if (notAllowDragOnDockbar(source, dragInfo) || isOnHandlerDrop())
			return;
		
		//文件夹关闭动画
		if (mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null && mLastDragOverView instanceof FolderIconTextView){
			((FolderIconTextView)mLastDragOverView).onExitAni(dragView);
			mDragMode = DRAG_MODE_NORMAL;
			mLastDragOverView = null;
		}
		
		if(isFullDockAndDragFromDrawer(source) || isFullDockAndIOSReorderStyle(source)){
			View lastView = findCellLayoutChildViewOnCurScreen(lastTargetCellX);
			if(lastView != null){
				lastView.setBackgroundResource(0);
				lastView.setBackgroundDrawable(null);
				lastTargetCellX = -1;
			}
			return;
		}
		
		if(!(source instanceof BaseMagicDockbar)){			
			needCleanReorder = true;
		}
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		if(!(source instanceof BaseMagicDockbar) && cellLayout.getChildCount() == DEFAULT_SCREEN_ITEM_COUNT){
			if(isOnExchangeAnimation){
				//Log.e("ggggg", "gggggggg");
				if(mDockToWpAnimationView != null){
					mDockToWpAnimationClear = true;
					mDockToWpAnimationView.clearAnimation();
					replaceCellX = -1;
					isOnExchangeAnimation = false;
				}
					
				if(mIconViewInDockbar != null){
					mIconViewInDockbar.setVisibility(VISIBLE);
				}
				mIconViewInDockbar = null;
				lastTargetCellX = -1;
			}else{				
				animationViewFromWorkspaceToDockbar();
			}
			return;
		}
		
		if(!mReorderAlarm.alarmPending()){
			if(source instanceof BaseMagicDockbar){
				int iCellX = findCellX(x);
				if(lastAction == -1){
					iCellX = ((DockbarCellLayout.LayoutParams)dragView.getDragingView().getLayoutParams()).cellX;
				}
				if(lastAction == Drag_Exit){
					return;
				}
				//Log.e("onDragExit iCellX", "onDragExit " + iCellX);
				Reorder listener = new Reorder(Drag_Exit, cellLayout, iCellX, iCellX);
				mReorderAlarm.setOnAlarmListener(listener);
	            mReorderAlarm.setAlarm(20);
	            lastAction = Drag_Exit;
			}else{
				int iCellX = findCellXAfterDragEnter(x);
				if(iCellX == lastTargetCellX && lastAction == Drag_Back_To_Workspace){
					return;
				}
				if(lastAction == Drag_Enter){
					iCellX = dragEnterCellX;
				}
				//Log.e("Drag_Back_To_Workspace", "Drag_Back_To_Workspace" + iCellX);
				Reorder listener = new Reorder(Drag_Back_To_Workspace, cellLayout, iCellX, iCellX);
				mReorderAlarm.setOnAlarmListener(listener);
	            mReorderAlarm.setAlarm(20);
	            lastAction = Drag_Back_To_Workspace;
			}
		}else{
			if(lastAction == Drag_Enter  || lastAction == Drag_Enter_Over || lastAction == Drag_Over || lastAction == Drag_Back){
				//Log.e("cancelReorderOnDragExit", "cancelReorderOnDragExit");
				cancelReorderOnDragExit();
				
				if(source instanceof BaseMagicDockbar){
					lastAction = Drag_Exit;
				}else{
					lastAction = Drag_Back_To_Workspace;
				}
//				lastAction = -1;
			}
		}
		
		lastTargetCellX = -1;
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		if(notReadyForDragAndDrop())
			return false;
		if (notAllowDragOnDockbar(source, dragInfo)){
			cleanWorkspaceState();
			Toast.makeText(getContext(), R.string.dockbar_allow, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(isFullDockAndIOSReorderStyle(source)){
			return true;
		}
		if(dragInfo instanceof ApplicationInfo && !mLauncher.mWorkspace.isAllAppsIndependence((ItemInfo)dragInfo) 
				&& findCellLayoutChildViewOnCurScreen(findCellX(x)) instanceof FolderIconTextView)
			return true;
		
		DockbarCellLayout dockbar = (DockbarCellLayout) getChildAt(mCurrentScreen);
		int count = dockbar.getChildCount();
		
		if(!isFullDockAndDragFromDrawer(source) || (!mDragController.isDragFromDrawer(source) && findCellXYForExchange() == null)){
			if(count == DEFAULT_SCREEN_ITEM_COUNT){//当是5个图标全部可见时
				boolean hasInvisable = false;
				for(int i = 0; i < count; i ++){
					View v = dockbar.getChildAt(i);
					if(v.getVisibility() != VISIBLE){
						hasInvisable = true;
					}
				}
				if(!hasInvisable){
					cleanWorkspaceState();
					Toast.makeText(getContext(), R.string.spring_add_app_from_drawer_reset, Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean onLongClick(View v) {
		mDragView = v;
		mDragController.startDrag(v, this);
		return true;
	}
	
	@Override
	public int getState() {
		final int visibility = getVisibility();
		if (visibility != VISIBLE)
			return UNAVAIABLE;
		return AVAIABLE;
	}
	
	/**
	 * @param mLauncher
	 *            the mLauncher to set
	 */
	public void setLauncher(BaseLauncher mLauncher) {
		this.mLauncher = mLauncher;
	}
	
	public void show(boolean animate) {
		if (!BaseSettingsPreference.getInstance().isDockVisible()) {
			return;
		}
		if (this.getVisibility() != VISIBLE) {
			if (animate) {
				Animation showAni = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in);
				showAni.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
						setVisibility(VISIBLE);
					}
	
					public void onAnimationRepeat(Animation animation) {
					}
	
					public void onAnimationEnd(Animation animation) {
					}
				});
				startAnimation(showAni);
			} else {
				setVisibility(VISIBLE);
			}
		}
	}
	
	public void hide(boolean animate) {
		if (this.getVisibility() == VISIBLE) {
			if (animate) {
				Animation hideAni = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out);
				hideAni.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}
	
					public void onAnimationRepeat(Animation animation) {
					}
	
					public void onAnimationEnd(Animation animation) {
						setVisibility(GONE);
					}
				});
				startAnimation(hideAni);
			} else {
				setVisibility(GONE);
			}
		}
	}

	@Override
	public void setVisibility(int visibility) {
		if (!BaseSettingsPreference.getInstance().isDockVisible() && visibility == VISIBLE) {
			return;
		}
		super.setVisibility(visibility);
	}
	
	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		//硬件加速缓存若关闭则开启 解决自绘壁纸时出现的切割蒙板显示问题 by Michael 2013.11.11
		if(GpuControler.isOpenGpu(this) && GpuControler.hasDestroyHardwareLayers(this)
				&& BaseConfig.isDrawWallPaper){
			GpuControler.enableHardwareLayers(this);
		}
	}
	
	public void changeCellLayoutConfig(int marginTop){
		DockbarCellLayoutConfig.resetMarginTop( marginTop);
		reLayoutChildCell();
	}
	
	public void reLayoutChildCell(){
		int screenCount = getChildCount();
		for (int i = 0; i < screenCount; i++) {
			DockbarCellLayout cl = (DockbarCellLayout)getChildAt(i);
            cl.resetChildLayout();
            cl.requestLayout();
		}
	}
	
	public boolean isShowAppTitle() {
		return showAppTitle;
	}

	public void setShowAppTitle(boolean showAppTitle) {
		this.showAppTitle = showAppTitle;
	}
	
	public boolean hasSpaceForDrop(){
		return findCellXYForExchange() != null
				|| ((ViewGroup)getChildAt(mCurrentScreen)).getChildCount() != DEFAULT_SCREEN_ITEM_COUNT
				|| mIconViewInDockbar != null;
	}
	
	public boolean isOnHandlerDrop() {
		return isOnHandlerDrop;
	}

	public void setOnHandlerDrop(boolean isOnHandlerDrop) {
		this.isOnHandlerDrop = isOnHandlerDrop;
	}
	
	/**
	 * 恢复Dock栏交换图标异常情况
	 */
	public void restoreReorderEx(){
		//恢复异常情况下与Workspace交换图标未还原
		if(mIconViewInDockbar != null && mIconViewInWorkspace != null){
			final View fromView = mIconViewInWorkspace;
			final View toView = mIconViewInDockbar;
			toView.setVisibility(VISIBLE);
			fromView.setVisibility(INVISIBLE);
			if(fromView.getParent() != null){
				((ViewGroup)fromView.getParent()).removeView(fromView);
			}
			
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)toView.getLayoutParams();
			final ItemInfo dragApp = (ItemInfo) toView.getTag();
			addOrMoveCurrentScreenItemInDatabase(dragApp, lp.cellX);
			mIconViewInWorkspace = null;
			mIconViewInDockbar = null;
			
			DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(dragApp.screen);
			if(toView.getParent() != null){
				((ViewGroup)toView.getParent()).removeView(toView);
			}
			cellLayout.addView(toView);
			
			needCleanReorder = true;
			clean();
		}
		
		//文件夹关闭动画
		if (mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null && mLastDragOverView instanceof FolderIconTextView){
			((FolderIconTextView)mLastDragOverView).onExitAni(null);
			mDragMode = DRAG_MODE_NORMAL;
			mLastDragOverView = null;
		}
				
		//移除可能存在的替换提示背景
		if(outDragView != null){
			DockbarCellLayout curCellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
			if(curCellLayout.getChildCount() == DEFAULT_SCREEN_ITEM_COUNT){
				for(int i = 0; i < curCellLayout.getChildCount(); i ++){
					curCellLayout.getChildAt(i).setBackgroundResource(0);
					curCellLayout.getChildAt(i).setBackgroundDrawable(null);
				}
			}
		}
		
		return;
	}
	
	/**
	 * workspace与dock栏交换图标动画
	 * @return
	 */
	private boolean animationViewFromWorkspaceToDockbar(){
		if(mIconViewInDockbar == null || mIconViewInWorkspace == null){
			return false;
		}
		
		//判断是否因快速滑动，mIconViewInWorkspace还未成功添加到桌面
		int[] fromTmp = new int[2];
		final View fromView = mIconViewInWorkspace;
		final View toView = mIconViewInDockbar;
		fromView.getLocationOnScreen(fromTmp);
		final CellLayout cl = mLauncher.mWorkspace.getCurrentCellLayout();
		if(mLauncher.getDragController().getDropTarget() instanceof PreviewWorkspace
				|| (fromTmp[0] % getWidth() == 0 &&  fromTmp[1] == mLauncher.mWorkspace.getTopPadding()
				&& (((ItemInfo)fromView.getTag()).cellX != 0 || ((ItemInfo)fromView.getTag()).cellY != 0))){
			toView.setVisibility(VISIBLE);
			fromView.setVisibility(INVISIBLE);
			if(fromView.getParent() != null){
				((ViewGroup)fromView.getParent()).removeView(fromView);
			}
			
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)toView.getLayoutParams();
			final ItemInfo dragApp = (ItemInfo) toView.getTag();
			addOrMoveCurrentScreenItemInDatabase(dragApp, lp.cellX);
			mIconViewInWorkspace = null;
			mIconViewInDockbar = null;
			return true;
		}
				
		//Workspace图标飞回dock栏动画
		final DragLayerView aniView = new DragLayerView(getContext());
		aniView.setDragLayer(mLauncher.getDragLayer());
		aniView.setDragingView(fromView);
		
		int[] from = new int[2];
		fromView.getLocationOnScreen(from);
		final int[] to = new int[2];
		toView.getLocationOnScreen(to);
		
		//Log.e("xxx from to", from[0] +"," + from[1] +"  " + to[0] +"," + to[1]);
		if(from[0] == 0){
			CellLayout.LayoutParams lp = (CellLayout.LayoutParams)fromView.getLayoutParams();
			if(lp.cellX != 0){//mIconViewInWorkspace未在Workspace添加成功
				lastTargetCellX = -1;
				return false;
			}
		}
		isOnExchangeAnimation = true;
		to[0] -= (cl.getCellWidth() - getCellWidthOnReorder())/2;
		to[1] -= (cl.getCellHeight() - getCellHeight())/2;
		aniView.show(from[0], from[1], cl.getCellWidth(), cl.getCellHeight());
		TranslateAnimation translateAnimation = new TranslateAnimation(0, to[0] - from[0], 0, to[1] - from[1]);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				aniView.setVisibility(INVISIBLE);
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						aniView.remove();
					}
				});
				
				DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)toView.getLayoutParams();
				final ItemInfo dragApp = (ItemInfo) toView.getTag();
//					View shortcut = LauncherViewHelper.createDockShortcut(mLauncher, dragApp);
//					addInDockbarByCell(shortcut, mCurrentScreen, lp.cellX,true);
				if(findCellLayoutChildViewOnCurScreen(lp.cellX) != null){//修正
					for(int i = 0; i < ((ViewGroup) getChildAt(mCurrentScreen)).getChildCount(); i ++){
						if(findCellLayoutChildViewOnCurScreen(i) == null){
							lp.cellX = i;
							break;
						}
					}
					toView.requestLayout();
				}
				toView.setVisibility(VISIBLE);
				if(fromView.getParent() != null){
					((ViewGroup)fromView.getParent()).removeView(fromView);
				}
				addOrMoveCurrentScreenItemInDatabase(dragApp, lp.cellX);
				isOnExchangeAnimation = false;
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		translateAnimation.setFillAfter(true);
		translateAnimation.setDuration(exchangeAnimationDuration);
		aniView.startAnimation(translateAnimation);
		fromView.setVisibility(INVISIBLE);
		mIconViewInWorkspace = null;
		mIconViewInDockbar = null;
		return true;
	}
	
	/**
	 * dock栏与workspace交换图标动画
	 * @return
	 */
	private boolean animationViewFromDockbarToWorkspace(final View fromView, int x, int y){
		final DragLayerView aniView = new DragLayerView(getContext());
		aniView.setDragLayer(mLauncher.getDragLayer());
		aniView.setDragingView(fromView);
		
		mDockToWpAnimationView = aniView;
		
		int[] from = new int[2];
		fromView.getLocationOnScreen(from);
		
		isOnExchangeAnimation = true;
		final int targetCellX = replaceCellX;
		final int targetCellY = replaceCellY;
		//Log.e("from to", from[0] +"," + from[1] +"  " + x +"," + y);
		aniView.show(from[0], from[1], getCellWidthOnReorder(), getCellHeight());
		final CellLayout cl = mLauncher.mWorkspace.getCurrentCellLayout();
		final int targetScreen = replaceScreen;
		TranslateAnimation translateAnimation = new TranslateAnimation(0, x + (cl.getCellWidth() - getCellWidthOnReorder())/2 - from[0], 
				0, y + (cl.getCellHeight() - getCellHeight())/2 - from[1]);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mDockToWpAnimationView = null;
				
				aniView.setVisibility(INVISIBLE);
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						aniView.remove();
					}
				});
				if(mDockToWpAnimationClear){
					//Log.e("mDockToWpAnimationClear", "mDockToWpAnimationClear");
					mDockToWpAnimationClear = false;
					return;
				}
				ItemInfo item = (ItemInfo) fromView.getTag();
				item.cellX = targetCellX;
				item.cellY = targetCellY;
				mIconViewInWorkspace = mLauncher.mWorkspace.createViewByItemInfo(item);
				int screen = targetScreen;
				if(screen < 0){
					screen = mLauncher.mWorkspace.getCurrentScreen();
				}
				mLauncher.mWorkspace.addViewInScreenFromDockbar(mIconViewInWorkspace, 
						mLauncher.mWorkspace.getCellLayoutAt(screen), item, screen);
				isOnExchangeAnimation = false;
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		translateAnimation.setFillAfter(true);
		translateAnimation.setDuration(exchangeAnimationDuration);
		aniView.startAnimation(translateAnimation);
		
		fromView.setVisibility(INVISIBLE);
		return true;
	}
	
	/**
	 * 处理拖放失败情况
	 */
	private void cancelReorderOnDropFail(){
		if(mDragView == null){
			return;
		}
		
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		int count = cellLayout.getChildCount();
		DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)mDragView.getLayoutParams();
		mDragView.setVisibility(VISIBLE);
		View ignoreView = mDragView;
		for(int i = lp.cellX; i < count; i ++ ){
			View tView = findViewByCellXOnDropFail(i, ignoreView);
			if(tView != null){
				DockbarCellLayout.LayoutParams tLP = (DockbarCellLayout.LayoutParams)tView.getLayoutParams();
				tLP.cellX ++; 
				ignoreView = tView;
			}
		}
		cellLayout.requestLayout();
	}
	
	/**
	 * 拖出时，取消位移状态
	 */
	private void cancelReorderOnDragExit(){
		mReorderAlarm.cancelAlarm();
		isOnReorderAnimation = false;
		
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		int count = cellLayout.getChildCount();
		boolean hasInvisableView = false;
		for(int i = 0; i < count; i ++ ){//校正位置
			View view = cellLayout.getChildAt(i);
			if(view.getVisibility() != VISIBLE){
				hasInvisableView = true;
				continue;
			}
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
			if(lp.isOnReorderAnimation || lp.isOnPending){				
				view.clearAnimation();
				if(lp.preCellX >= 0){
					lp.cellX = lp.preCellX;
					view.requestLayout();
				}
			}
			lp.isOnPending = false;
			lp.isOnReorderAnimation = false;
			lp.preCellX = -1;
		}
		if(hasInvisableView && count > 1){//校正单元格跨度
			int cellW = cellLayout.getWidth() / (count - 1);
			for(int i = 0; i < count; i ++ ){
				View view = cellLayout.getChildAt(i);
				if(view.getVisibility() != VISIBLE)
					continue;
				DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
				lp.width = cellW;
				lp.isOnPending = true;
			}
		}
		
		for(int i = 0; i < count; i ++ ){//修正可能存在的空位
			if(findCellLayoutChildViewOnCurScreen(i) == null){
				for(int j = i+1; j < count + 1; j ++){
					View view = findCellLayoutChildViewOnCurScreen(j);
					if(view != null){
						DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
						lp.cellX = i;
						break;
					}
				}
			}
		}
		
		cellLayout.requestLayout();
	}
	
	/**
	 * 挤动位移后修正可能存在问题
	 */
	public void clean(){
		if(!needCleanReorder)
			return;
		needCleanReorder = false;
		
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				//Log.e("clean", "cleancleancleanclean");
				reset(null);
				
				DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
				//修复可能存在的不可见子View
				int count = cellLayout.getChildCount();
				View invisableView = null;
				for(int i = 0; i < count; i ++ ){
					View v = cellLayout.getChildAt(i);
					if(v != null && v.getVisibility() != VISIBLE){
						invisableView = v;
						break;
					}
				}
				if(invisableView != null){			
					ItemInfo item = (ItemInfo) invisableView.getTag();
					Cursor c = null;
					try{
						c = getContext().getContentResolver().query(BaseLauncherSettings.Favorites.getContentUri(), null, 
								BaseLauncherSettings.Favorites._ID + "=? and " 
								+ BaseLauncherSettings.Favorites.CONTAINER +" = " + BaseLauncherSettings.Favorites.CONTAINER_DOCKBAR, 
								new String[]{String.valueOf(item.id)}, null);
						if(c.getCount() > 0){
							invisableView.setVisibility(VISIBLE);
						}else{
							cellLayout.removeView(invisableView);
							count --;
						}
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						if(c != null){
							c.close();
						}
					}
					
				}
				
				//修正子View位置
				boolean needReqestLayout = false;
				for(int i = 0; i < count; i ++ ){//补空
					if(findCellLayoutChildViewOnCurScreen(i) == null){
						for(int j = i+1; j < count + 1; j ++){
							View view = findCellLayoutChildViewOnCurScreen(j);
							if(view != null){
								DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) view.getLayoutParams();
								lp.cellX = i;
								needReqestLayout = true;
								break;
							}
						}
					}
				}
				for(int i = 0; i < count; i ++ ){//去重
					View v = isWrongCellX(i, true);
					if(v != null){
						DockbarCellLayout.LayoutParams tLP = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
						boolean flag = false;
						for(int j = i + 1; j < count; j ++ ){
							if(findCellLayoutChildViewOnCurScreen(j) == null){
								tLP.cellX = j;
								flag = true;
								break;
							}
						}
						if(!flag){
							for(int j = i - 1; j >= 0; j -- ){
								if(findCellLayoutChildViewOnCurScreen(j) == null){
									tLP.cellX = j;
									break;
								}
							}
						}
						needReqestLayout = true;
					}
				}
				if(needReqestLayout || Build.VERSION.SDK_INT < 14){//2.*重新布局			
					cellLayout.requestLayout();
				}
				if(needReqestLayout){//入库
					for(int i = 0; i < count; i ++){
						View v = cellLayout.getChildAt(i);
						DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
						ItemInfo item = (ItemInfo) v.getTag();
						int targetCellX = resetCellX(lp.cellX);
						if(targetCellX != item.cellX){				
							item.cellX = targetCellX;
							BaseLauncherModel.updateItemInDatabase(getContext(), item);
						}
					}
				}
			}
			
		}, exchangeAnimationDuration*2);
	}
	
	/**
	 * 挤动动画后重置
	 */
	private void reset(DragView dragView){
		//清除挤动位移状态
		lastAction = -1;
		lastTargetCellX = -1;
		dragEnterCellX = -1;
		isOnReorderAnimation = false;
		isOnExchangeAnimation = false;
		setOnHandlerDrop(false);
		replaceScreen = replaceCellX = replaceCellY = -1;
		replaceXY = null;
		
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		int count = cellLayout.getChildCount();
		for(int i = 0; i < count; i ++ ){
			View view = cellLayout.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
			lp.isOnPending = false;
			lp.isOnReorderAnimation = false;
			lp.preCellX = -1;
		}
		
		//清除文件夹动画
		if (mDragMode == DRAG_MODE_FOLDER && mLastDragOverView != null && mLastDragOverView instanceof FolderIconTextView){
			((FolderIconTextView)mLastDragOverView).onExitAni(dragView);
		}
		mDragMode = DRAG_MODE_NORMAL;
		mLastDragOverView = null;
	}
	
	/**
	 * 还原默认状态
	 */
	public void restoreReorder(){
		DockbarCellLayout cellLayout = (DockbarCellLayout) getChildAt(mCurrentScreen);
		int count = cellLayout.getChildCount();
		for(int i = 0; i < count; i ++ ){
			View v = cellLayout.getChildAt(i);
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
			lp.isOnPending = false;
			lp.isOnReorderAnimation = false;
			lp.preCellX = -1;
			if(v.getVisibility() != VISIBLE){
				v.setVisibility(VISIBLE);
			}
		}
		
		for(int i = 0; i < count; i ++ ){//去重
			View v = isWrongCellX(i, true);
			if(v != null){
				DockbarCellLayout.LayoutParams tLP = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
				boolean flag = false;
				for(int j = i + 1; j < count; j ++ ){
					if(findCellLayoutChildViewOnCurScreen(j) == null){
						tLP.cellX = j;
						flag = true;
						break;
					}
				}
				if(!flag){
					for(int j = i - 1; j >= 0; j -- ){
						if(findCellLayoutChildViewOnCurScreen(j) == null){
							tLP.cellX = j;
							break;
						}
					}
				}
			}
		}
		
		cellLayout.requestLayout();
	}
	
	/**
	 * 用于dock栏图标挤动
	 */
	class Reorder implements Alarm.OnAlarmListener {
		private int action;
		private DockbarCellLayout cellLayout;
		private int iCellX;//移动到位置、要插入或移出的位置
		private int sCellX;//拖起的原位置
		
		public static final int REORDER_ANIMATION_TRANS_DURATION = 150;//图标移动动画时间
		
		public Reorder(int action, DockbarCellLayout cellLayout, int iCellX, int sCellX) {
			this.action = action;
			this.cellLayout = cellLayout;
			this.iCellX = iCellX;
			this.sCellX = sCellX;
		}
		
		//动画前校正可能存在的位置偏差
		private void valid(){
			int count = cellLayout.getChildCount();
			for(int i = 0; i < count; i ++ ){
				View v = cellLayout.getChildAt(i);
				DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
				if(lp.isOnReorderAnimation)
					v.clearAnimation();
			}
			//校正位置，去除重叠位置
			for(int i = 0; i < count; i ++ ){
				View v = isWrongCellX(i, true);
				if(v != null){
					DockbarCellLayout.LayoutParams tLP = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
					boolean flag = false;
					for(int j = i + 1; j < count; j ++ ){
						if(findCellLayoutChildViewOnCurScreen(j) == null){
							tLP.cellX = j;
							flag = true;
							break;
						}
					}
					if(!flag){
						for(int j = i - 1; j >= 0; j -- ){
							if(findCellLayoutChildViewOnCurScreen(j) == null){
								tLP.cellX = j;
								break;
							}
						}
					}
				}
			}
		}
		
		//查找正确iCellX
		private void findRightCellX(){
			int count = cellLayout.getChildCount();
			if(findCellLayoutChildViewOnCurScreen(iCellX) != null){
				for(int i = 0; i < count; i ++ ){
					if(findCellLayoutChildViewOnCurScreen(i) == null){
						iCellX = i;
						//Log.e("iCellX", "" + iCellX);
						break;
					}
				}
			}
		}

		@Override
		public void onAlarm(Alarm arg0) {
			if(!mDragController.isDragging() || cellLayout.getChildCount() == 0){
				return;
			}
			
			if(mDragMode == DRAG_MODE_FOLDER){
				lastTargetCellX = -1;
				lastAction = -1;
				return;
			}
			
			final int count = cellLayout.getChildCount();
			if(action == Drag_Enter){//拖入
				int oldCellW = cellLayout.getWidth() / count;
				int newCellW = cellLayout.getWidth() / (count + 1);
				
				valid();
				
				for(int i = 0; i < count; i ++ ){
					View view = cellLayout.getChildAt(i);
					if(view.getVisibility() != VISIBLE)
						continue;
					pushAnimateChildToPosition(view, oldCellW, newCellW);
				}
			}else if(action == Drag_Enter_Over){//拖入后拖动
				for(int i = 0; i < count; i ++ ){
					View view = cellLayout.getChildAt(i);
					if(view.getVisibility() != VISIBLE){
						continue;
					}
				}
				int cellW = cellLayout.getWidth() / (count + 1);
				if(sCellX < iCellX){
					for(int i = sCellX + 1; i <= iCellX; i ++ ){
						View view = findCellLayoutChildViewOnCurScreen(i);
						if(view == null)
							continue;
						animateChildToPosition(view, i-1, i, cellW);
					}
				}else{
					for(int i = sCellX - 1; i >= iCellX; i -- ){
						View view = findCellLayoutChildViewOnCurScreen(i);
						if(view == null)
							continue;
						animateChildToPosition(view, i+1, i, cellW);
					}
				}
				
			}else if(action == Drag_Over){//拖动
				if(sCellX == iCellX)
					return;   
				int cellW = cellLayout.getWidth() / count;
				if(sCellX < iCellX){
					for(int i = sCellX + 1; i <= iCellX; i ++ ){
						View view = findCellLayoutChildViewOnCurScreen(i);
						if(view == null)
							continue;
						animateChildToPosition(view, i-1, i, cellW);
					}
				}else{
					for(int i = sCellX - 1; i >= iCellX; i -- ){
						View view = findCellLayoutChildViewOnCurScreen(i);
						if(view == null)
							continue;
						animateChildToPosition(view, i+1, i, cellW);
					}
				}
			}else if(action == Drag_Exit){//拖出
				if(count <= 1)
					return;
				
				int oldCellW = cellLayout.getWidth() / count;
				int newCellW = cellLayout.getWidth() / (count - 1);
				
				valid();
				findRightCellX();
				
				for(int i = 0; i < count; i ++ ){
					View view = cellLayout.getChildAt(i);
					if(view.getVisibility() != VISIBLE)
						continue;
					pullAnimateChildToPosition(view, oldCellW, newCellW);
				}
			}else if(action == Drag_Back){//拖出后拖回
				if(count <= 1)
					return;
				
				int oldCellW = cellLayout.getWidth() / (count - 1);
				int newCellW = cellLayout.getWidth() / count;
				
				valid();
				
				for(int i = 0; i < count; i ++ ){
					View view = cellLayout.getChildAt(i);
					if(view.getVisibility() != VISIBLE)
						continue;
					pushAnimateChildToPosition(view, oldCellW, newCellW);
				}
			}else if(action == Drag_Back_To_Workspace){//拖入后拖回到Worksapce
				if(count <= 0)
					return;
				
				int oldCellW = cellLayout.getWidth() / (count + 1);
				int newCellW = cellLayout.getWidth() / count;
				
				valid();
				findRightCellX();
				
				for(int i = 0; i < count; i ++ ){
					View view = cellLayout.getChildAt(i);
					if(view.getVisibility() != VISIBLE)
						continue;
					pullAnimateChildToPosition(view, oldCellW, newCellW);
				}
			}
			
		}
		//位移动画
		private void animateChildToPosition(final View view, final int newCellX, int oldCellX, int cellW){
			final DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
			int newX = newCellX * cellW;
			int oldX = oldCellX * cellW;
			if(newX == oldX)
				return;
			
			startAnimation(view, lp, newX - oldX, newCellX);
		}
		
		//挤入动画
		private void pushAnimateChildToPosition(final View view, int oldCellW, final int newCellW){
			final DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
			int oldCellX = lp.cellX;
			int newCellX = lp.cellX;
			if(lp.cellX >= iCellX){
				newCellX = lp.cellX + 1;
			}
			int fix = (newCellW - oldCellW)/2;
			int newX = newCellX * newCellW + fix;
			int oldX = oldCellX * oldCellW;
			if(newX == oldX)
				return;
			if(lp.preCellX < 0){				
				lp.preCellX = lp.cellX;
			}
			lp.width = newCellW;
			lp.isOnPending = true;
			startAnimation(view, lp, newX - oldX, newCellX);
		}
		//拖出动画
		private void pullAnimateChildToPosition(final View view, int oldCellW, final int newCellW){
			final DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)view.getLayoutParams();
			int oldCellX = lp.cellX;
			int newCellX = lp.cellX;
			if(lp.cellX > iCellX){
				newCellX = lp.cellX - 1 ;
			}
			int fix = (newCellW - oldCellW)/2;
			int newX = newCellX * newCellW + fix;
			int oldX = oldCellX * oldCellW;
			if(newX == oldX)
				return;
			lp.width = newCellW;
			lp.isOnPending = true;
			startAnimation(view, lp, newX - oldX, newCellX);
		}
		
		private void startAnimation(final View view, final DockbarCellLayout.LayoutParams lp, int toX, int toCellX){
			lp.cellX = toCellX;
			lp.isOnReorderAnimation = true;
			isOnReorderAnimation = true;
			TranslateAnimation trans = new TranslateAnimation(0, toX, 0, 0);
			trans.setDuration(REORDER_ANIMATION_TRANS_DURATION);
			trans.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					lp.isOnReorderAnimation = false;
					isOnReorderAnimation = false;
					view.clearAnimation();
					view.requestLayout();
//					if(view instanceof FolderIconTextView && ((FolderIconTextView)view).isFolderEnterAni()){
//						Log.e("isOnMergeFolderAni 11111", "isOnMergeFolderAni");
//						((FolderIconTextView)view).onExitAni(null);
//					}
					if(!mDragController.isDragging()){//适配固件2.*,防止出现图标没刷新的丢失问题
						getChildAt(mCurrentScreen).invalidate();
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}
			});
			view.startAnimation(trans);
		}

	}
	
	/**
	 * 是否为类IOS风格挤动，且无空间情况
	 * @return
	 */
	private boolean isFullDockAndIOSReorderStyle(DragSource source){
		return !WorkspaceDragAndDropImpl.androidStyleReorder && !(source instanceof BaseMagicDockbar)
				&& ((ViewGroup) getChildAt(mCurrentScreen)).getChildCount() == DEFAULT_SCREEN_ITEM_COUNT;
	}
	

	public void snapToDefaultScreen(){
		if(mCurrentScreen != DEFAULT_SCREEN){			
			snapToScreen(DEFAULT_SCREEN);
		}
	}
	
	//==============================可实现的接口=====================================//
	/**
	 * 是否显示dock栏文字
	 * @return
	 */
	public boolean isShowDockbarText(){
		return false;
	}
	
	/**
	 * dock栏图标动画到Workspace时，查找目标位置
	 * @return
	 */
	public int[] findCellXYForExchange(){
		return null;
	}
	
	/**
	 * 是否dock栏有5个图标并且从匣子拖入
	 * @param source
	 * @param dragInfo
	 * @return
	 */
	public boolean isFullDockAndDragFromDrawer(DragSource source){
		return false;
	}
	
	/**
	 * 删除时，是否提示
	 * @param dragInfo
	 * @return
	 */
	public boolean alertOnDelete(Object dragInfo){
		return false;
	}

	/**
	 * 应用主题
	 */
	@Override
	public void applyTheme() {
		
	}
	
	/**
	 * 放入文件夹的放手动画
	 * @param iconTextView
	 */
	protected void startFolderAnimation(FolderIconTextView iconTextView){
		
	}

	/**
	 * 处理从匣子拖出文件夹的附带操作，如加密属性等
	 * @param folderInfo
	 * @param idFromDrawer 匣子内的对象id
	 */
	protected void handleDropDrawerFolderEx(FolderInfo folderInfo, long idFromDrawer){
		
	}
}
