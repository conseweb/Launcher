package com.bitants.launcherdev.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.bitants.launcher.R;
import com.bitants.launcherdev.folder.model.FolderHelper;
import com.bitants.launcherdev.framework.AnyCallbacks;
import com.bitants.launcherdev.framework.view.bubble.LauncherBubbleManager;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLightbar;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.ConfigFactory;
import com.bitants.launcherdev.launcher.config.preference.BaseConfigPreferences;
import com.bitants.launcherdev.launcher.edit.LauncherEditView;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.info.PandaWidgetInfo;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.screens.dockbar.LightBarInterface;
import com.bitants.launcherdev.launcher.screens.dockbar.MagicDockbarRelativeLayout;
import com.bitants.launcherdev.launcher.screens.preview.PreviewCellInfo;
import com.bitants.launcherdev.launcher.screens.preview.PreviewEditAdvancedController;
import com.bitants.launcherdev.launcher.touch.DragScroller;
import com.bitants.launcherdev.launcher.touch.DropTarget;

import java.util.ArrayList;

public class Workspace extends ScreenViewGroup implements DragScroller, AnyCallbacks.OnFolderDragOutCallback {
	
	private WorkspaceHelper mWorkspaceHelper;
	private CommonLightbar springLightbar;// workspace编辑模式指示灯

	public Workspace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Workspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		Context mContext = getContext();
		
		mDefaultScreen = BaseConfigPreferences.getInstance().getDefaultScreen(ScreenViewGroup.DEFAULT_SCREEN);
		int count = ConfigFactory.getScreenCount(mContext);
		if (count == BaseConfig.NO_DATA)
			count = 5;
		for (int i = 0; i < count; i++) {
			CellLayout cl = new CellLayout(mContext);
			addView(cl);
			cl.setCellLayoutLocation(i);
			cl.setWorkspace(this);
		}
		mCurrentScreen = mDefaultScreen;
		
		setSpringGapAndSplit(1/12f, 1/60f);
	}
	
	@Override
	public boolean onPinch(){
		Launcher launcher = ((Launcher)mLauncher);
		PreviewEditAdvancedController previewController = launcher.getPreviewEditController();
		previewController.startDesktopEdit(PreviewEditAdvancedController.EDIT_PREVIEW_MODE);
		return true;
	}
	
	@Override
	public View createViewByItemInfo(ItemInfo itemInfo){
		View view = null;
		switch (itemInfo.itemType) {
		case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION: //普通app
		case LauncherSettings.Favorites.ITEM_TYPE_HI_APPLICATION: //“我的手机”里的应用，如我的壁纸、我的电池等
		case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT: //1."91快捷"里的图标，如应用列表、通知栏开关等; 2."一键装机"和"热门游戏"
		case LauncherSettings.Favorites.ITEM_TYPE_INDEPENDENCE:
		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT: //系统的快捷方式，如书签
			view = mLauncher.createCommonAppView((ApplicationInfo) itemInfo);
			break;
		case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER: //自定义文件夹
			view = mLauncher.createFolderIconTextViewFromXML((ViewGroup) getChildAt(mCurrentScreen), itemInfo);
			break;
			
		case LauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET: //匣子里的自定义桌面小部件
			view = mLauncher.createAppWidgetView((PandaWidgetInfo)itemInfo);
			break;
//		case LauncherSettings.Favorites.ITEM_TYPE_PANDA_SHORTCUT:  
//		case LauncherSettings.Favorites.ITEM_TYPE_SYSTEM_SHORTCUT:
//			LauncherDrawerWidgetHelper.processDropPandaShortcutFromDrawer(mLauncher, (ItemInfo) dragInfo, this.getCurrentScreen(), mTargetCell);
//			break;
		default:
			throw new IllegalStateException("Unknown item type: " + itemInfo.itemType);
		}
		
		return view;
	}
	
	@Override
	public void initWorkspaceDragAndDrop(CellLayout.CellInfo mDragInfo){
		mWorkspaceDragAndDrop = new WorkspaceDragAndDropOnDefault(this, mDragInfo);
	}
	
	/**
	 * Description: 进入屏幕编辑模式
	 */
	public void changeToSpringMode(){
		changeToSpringMode(true, LauncherEditView.TAB_ADD);
	}
	
	/**
	 * Description: 从其它地方添加View到Workspace
	 */
	public void addViewInCurrentScreenFromOutside(View view, CellLayout cellLayout, ItemInfo info){
		addViewInScreenFromOutside(view, cellLayout, info, mCurrentScreen);
	}
	
	private void addViewInScreenFromOutside(View view, CellLayout cellLayout, ItemInfo info, int targetScreen){
		cellLayout.addView(view);
		view.setHapticFeedbackEnabled(false);
		view.setOnLongClickListener(mLongClickListener);
		if (view instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) view);
		}
		
		cellLayout.onDrop(view, new int[]{info.cellX, info.cellY}, null, info);
	}
	
	@Override
	public void setLauncher(BaseLauncher launcher) {
		super.setLauncher(launcher);
		mWorkspaceHelper = new WorkspaceHelper((Launcher)mLauncher);
	}
	
	public WorkspaceHelper getWorkspaceHelper() {
		return mWorkspaceHelper;
	}
	
	@Override
	public void setLightBar(LightBarInterface lightbar) {
		super.setLightBar(lightbar);
		//显示当前屏指示灯
		post(new Runnable(){
			@Override
			public void run() {
				updateLightbar();
			}
		});
	}
	
	@Override
	public void updateLightbar(){
		if(isOnSpringMode())
			return;
		lightbar.scrollHighLight(mScrollX);
	}
	
	//移除默认空屏幕
	public void checkWorkSpaceEmptyRemove(){
		if(getChildCount()>0){
			Boolean isdefaut=true;
			for(int i=0;i<getChildCount();i++){
				
				CellLayout view=getCellLayoutAt(i);
				if(view!=null&&view.getChildCount()<=0){
					removeScreenFromWorkspace(i);
					deleteScreen(i);
				}else{
					if(isdefaut){
						setCurrentScreen(i);
						setDefaultScreen(i);
						isdefaut=false;
					}
					
				}
				
			}
			
		}
		
	}
	
	private void deleteScreen(final int position) {
		int currentScreen = getCurrentScreen();
		int defaultScreen = getDefaultScreen();
		if(position < currentScreen) {
			currentScreen--;
		}
		if(position < defaultScreen) {
			defaultScreen--;
		}
		
		if (getChildCount()-1 <= currentScreen) {
			currentScreen = 0;
		}
		if (getChildCount()-1 <= defaultScreen) {
			defaultScreen = 0;
		}
		setCurrentScreen(currentScreen);
		setDefaultScreen(defaultScreen);
		//修改Workspace数据
		removeScreenFromWorkspace(position);
	}
	
	@Override
	public void removeScreenFromWorkspace(int i){
		super.removeScreenFromWorkspace(i);
		getWorkspaceHelper().removeScreenFromWorkspace(i);
	}
	
	@Override
	public void createScreenToWorkSpace(){
		super.createScreenToWorkSpace();
		getWorkspaceHelper().createScreenToWorkSpace();
	}
	
	@Override
	public void inflateSpringLightbar() {
        Context mContext = getContext();
		if (springLightbar != null) {
			return;
		}

		View.inflate(mContext, R.layout.launcher_spring_lightbar, mLauncher.getDragLayer());
		springLightbar = (CommonLightbar) mLauncher.getDragLayer().findViewById(R.id.spring_lightbar);
		springLightbar.setNormalLighter(getResources().getDrawable(R.drawable.spring_lightbar_normal));
		springLightbar.setSelectedLighter(getResources().getDrawable(R.drawable.spring_lightbar_checked));
		setSpringLightbar(springLightbar);
	}
	
	@Override
	public void hideEditor(){
		if(((Launcher)mLauncher).editor != null){
			((Launcher)mLauncher).editor.hideWithAnimation();
			
			((MagicDockbarRelativeLayout)((Launcher)mLauncher).getBottomContainer()).showWithAnimation();
		}else{//防止Launcher被中断后，无法显示dock栏
			showLightbarAndDockbar();
		}
	}
	
	@Override
	public void showEditor(float f, String tab){
		Launcher launcher = (Launcher)mLauncher;
		if(launcher.editor == null){
			launcher.setupEditor();
		}
		if(launcher.editor == null){
			return;
		}
		
		launcher.editor.getLayoutParams().height = (int) f;
		launcher.editor.showWithAnimation(tab);
		
		((MagicDockbarRelativeLayout)launcher.getBottomContainer()).hideWithAnimation();
	}

	
	/**
	 * 从文件夹拖出前
	 * @param arg0
	 * @param arg1
	 */
	@Override
	public void onBeforeDragOut(FolderInfo arg0, ArrayList<Object> arg1) {
		mLauncher.closeFolder();
	}

	/**
	 * 从文件夹拖出时
	 * @param folderInfo
	 * @param items
	 */
	@Override
	public void onDragOut(FolderInfo folderInfo, ArrayList<Object> items) {
		/**
		 *  这里只处理当拖拽文件夹内容时，文件夹中只有一个app的情况
		 **/
		if (items == null) return;
		FolderInfo folder = (FolderInfo)folderInfo;
		if(folder.getSize() == 1){
			folder.remove((ApplicationInfo)items.get(0));
			folder.checkFolderState();
		}
	}

	@Override
	public void onDrop(View target,FolderInfo folder, ArrayList<Object> items) {
		initWorkspaceDragAndDropIfNotExsit(null);
		mWorkspaceDragAndDrop.onDrop(target, folder, items);
	}

	@Override
	public boolean onFlingOut(FolderInfo folderInfo, ArrayList<Object> items) {
		FolderInfo folder = (FolderInfo)folderInfo;
		if(folder.getSize() == 0) return false ;
		
		if (items == null) return false;
		Object item = items.get(0);
		
		//fling出文件夹的动作
		int index = mScroller.isFinished() ? mCurrentScreen : mNextScreen;
		boolean success = onDropFolderExternal(index,folder, item);
		
//		if(folder.getProxyView() != null && folder.getProxyView().getTag() instanceof AnythingInfo)
//			return false ;
		
		if(success){//只处理拖拽成功的
			FolderHelper.removeDragApp(folder, (ApplicationInfo) item);
			if (folder.contents.size() > 1) {
				folder.invalidate();
			}
			folder.checkFolderState();
		}
		return success ;
	}
	
	@Override
	public void onActionUp(){
         ((Launcher)mLauncher).getLauncherMenu().updateMenu();
	}
	
	@Override
	public void handleOnDragOverOrReorder(View v) {
		LauncherBubbleManager.getInstance().dismissBubble(v);
	}
	
	public void changeToSpringMode(String tab){
		changeToSpringMode(true,tab);
	}
}
