package com.bitants.launcherdev.launcher;

import android.app.NotificationManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.avos.avoscloud.AVAnalytics;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.launcher.R;
import com.bitants.launcherdev.app.AppInfoIntentCommandAdapter;
import com.bitants.launcherdev.app.CustomIntentSwitcherController;
import com.bitants.launcherdev.app.IntentCommand;
import com.bitants.launcherdev.datamodel.Global;
import com.bitants.launcherdev.folder.LauncherFolderReceiverFactory;
import com.bitants.launcherdev.folder.model.FolderSwitchController;
import com.bitants.common.framework.OnKeyDownListenner;
import com.bitants.common.framework.view.bubble.LauncherBubbleManager;
import com.bitants.launcherdev.integratefoler.IntegrateFolder;
import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.common.kitset.util.StatusBarUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.kitset.util.WallpaperUtil;
import com.bitants.launcherdev.launcher.LauncherSettings.Favorites;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.launcherdev.launcher.edit.LauncherEditView;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.DeleteZone;
import com.bitants.common.launcher.support.LauncherOnStartDispatcher;
import com.bitants.common.launcher.support.WallpaperHelper;
import com.bitants.common.launcher.touch.BaseDragController;
import com.bitants.common.launcher.view.BaseDeleteZoneTextView;
import com.bitants.common.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.common.launcher.view.icon.ui.util.BeansContainer;
import com.bitants.launcherdev.menu.LauncherMenu;
//import PushManager;
//import com.bitants.launcherdev.push.PushSDKAdapter;
import com.bitants.common.theme.adaption.ThemeIconIntentAdaptation;
import com.bitants.launcherdev.util.ActivityActionUtil;

import java.io.File;
import java.util.List;

import hugo.weaving.DebugLog;

public class Launcher extends BaseLauncher {

	public LauncherEditView editor;
	/**
	 * 记录点击的view,当打开、删除加密文件夹时,用于验证密码成功后的回调参数
	 */
	public View mClickView;
	
	private FolderSwitchController mFolderOpenController; // 文件夹控制器
	
	/**
	 * 集成文件夹
	 */
	private IntegrateFolder integrateFolder;
	
	public Handler handler = new Handler();
	
	public LauncherMenu mLauncherMenu;
	
	private View mNavigationView;
	
	public CustomIntentSwitcherController mCustomIntentSwitcherController;

    @DebugLog
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 跟踪统计应用的打开情况
		AVAnalytics.trackAppOpened(getIntent());
		BeansContainer.getInstance().setFolderReceiverFactory(new LauncherFolderReceiverFactory());
	}

    @DebugLog
	@Override
	public void setupReadMeForNewUser(){
		try {
			WallpaperManager.getInstance(this).setResource(R.drawable.wallpaper);
			/**
			 * 拷贝默认壁纸至sd卡
			 */
			File f = new File(WallpaperUtil.getWPPicHome());
			if (f == null || !f.exists()) {
				f.mkdirs();
			}
			BaseBitmapUtils.saveStream2file(getResources().openRawResource(R.drawable.wallpaper), 
					f.getAbsolutePath() + "/default_wallpaper.jpg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @DebugLog
	public boolean onLongClick(View v) {
		// 编辑被锁定桌面上的任何长按事件都不响应
		if (!BaseConfig.allowEdit(getBaseContext())) {
			return true;
		}
		
		mClickView = v;

		if (isWorkspaceLocked()) {
			return false;
		}

		if (!(v instanceof CellLayout)) {
			v = (View) v.getParent();
		}

		CellLayout.CellInfo cellInfo = (CellLayout.CellInfo) v.getTag();

		// This happens when long clicking an item with the dpad/trackball
		if (cellInfo == null) {
			return true;
		}

		if (mWorkspace.allowLongPress()) {
			if (cellInfo.cell == null) {
				// User long pressed on empty space
				if (!mWorkspace.isOnSpringMode()) {
					mWorkspace.setAllowLongPress(false);
					mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
					//长按桌面空白处直接打开桌面菜单
					getLauncherMenu().updateMenu();
					if(mDragController != null)
						mDragController.vibrator();
				} else {
					mWorkspace.cancelLongPressAction();
				}
			} else {
				//setupDeleteZone();
				LauncherBubbleManager.getInstance().dismissBubble(cellInfo.cell);
				mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
				mWorkspace.startDrag(cellInfo);
			}
		}
		return true;
	}
	@Override
	protected void onStart() {
		super.onStart();
//		LauncherOnStartDispatcher.getInstance().addListener(PushManager.getInstance()); //注册推送接收监听器
		LauncherOnStartDispatcher.getInstance().dispatch(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StatusBarUtil.toggleStateBar(this, true);
		
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				LauncherBubbleManager.getInstance().showAllBubbles();
			}
		}, 2000);
	}
	
	/**
	 * 新建文件夹 <br>
	 */
	public FolderIconTextView addFolder(CellLayout layout, long container, final int screen, int cellX, int cellY, String folderTitle) {
		int[] cellXY = new int[]{cellX, cellY};
		return addFolder(layout, container, screen, cellXY, folderTitle);
	}
	
	public FolderIconTextView addFolder(CellLayout layout, long container, final int screen, int[] cellXY, String folderTitle) {
		final FolderInfo folderInfo = new FolderInfo();
		folderInfo.title = StringUtil.isEmpty(folderTitle) ? getText(R.string.folder_name) : folderTitle;
		// Update the model
		folderInfo.container = container;
		folderInfo.screen = screen;
		
		folderInfo.cellX = cellXY[0];
		folderInfo.cellY = cellXY[1];
		int[] wh = CellLayoutConfig.spanXYMather(1, 1, null); 
		folderInfo.spanX = wh[0];
		folderInfo.spanY = wh[1];
		
		BaseLauncherModel.addItemToDatabase(this, folderInfo, false);

		// Create the view
		FolderIconTextView newFolder = (FolderIconTextView) createFolderIconTextViewFromXML(layout, folderInfo);
		mWorkspace.addInScreen(newFolder, screen, folderInfo.cellX, folderInfo.cellY, 
				folderInfo.spanX, folderInfo.spanY, isWorkspaceLocked(), false);
		return newFolder;
	}
	
	public void onClick(View v) {
		//点击后删除消息
		LauncherBubbleManager.getInstance().dismissBubbleAndRecord(v);
		mClickView = v;		
		Object tag = v.getTag();
		if (tag instanceof ApplicationInfo) {
			final ApplicationInfo appInfo = (ApplicationInfo) tag;
			final Intent intent = appInfo.intent;
			int[] pos = new int[2];
			v.getLocationOnScreen(pos);
			final int itemType = appInfo.itemType;
			if (itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT || itemType == Favorites.ITEM_TYPE_INDEPENDENCE) {
				mCustomIntentSwitcherController.registerCustomIntent(new AppInfoIntentCommandAdapter(appInfo));
				mCustomIntentSwitcherController.onAction(this, appInfo, IntentCommand.ACTION_FROM_SHORTCUT);
			}

			// yuf@2012.12.20 防止托盘默认4应用的intent 字串值被添加 bnds=[] 导致一些判断无法识别
			if (!ThemeIconIntentAdaptation.isDefaultDockAppByUri(intent.toUri(0)))
				intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight()));
			ActivityActionUtil.startActivitySafelyForRecored(v, this, intent);
			
		} else if (tag instanceof FolderInfo) {
			if (v instanceof FolderIconTextView) {
				((FolderIconTextView)v).setShowNewFlag(false);
			}
//原先文件夹			handleFolderClick((FolderInfo) tag, v);
			handleIntegrateFolderClick((FolderInfo) tag, v);
		} 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = super.onKeyDown(keyCode, event);
		boolean handleBack = false;

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (handleBackHere(event)) {
				
				if (mWorkspace.isOnSpringMode()) {
					handleBack = true;
					/**
					 *  编辑情况下，返回键特殊处理
					 */
//					if (editor != null && editor.getLauncherEditWidgetTopLayout().getVisibility() == View.VISIBLE) {
//						if(editor != null){
//							editor.showViewByBackKeyDown();
//						}
//					} else{
//						mWorkspace.changeToNormalMode();
//					}
					mWorkspace.changeToNormalMode();
				}
				if (!event.isCanceled() && isOnWidgetEditMode()) {
					handleBack = true;
					stopWidgetEdit();
				}
				if (!event.isCanceled() && mWorkspaceLayer.isOnZeroView()) {
					handleBack = true;
					getWorkspaceLayer().snapToWorkspaceDefaultScreen();
//					mNavigationView.onBackKeyDown();
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) { // 添加搜索键事件处理
			for (OnKeyDownListenner lis : getOnkeydownLisList()) {
				if (lis == null)
					continue;

				if (lis.onKeyDownProcess(keyCode, event))
					return true;
			}
		}

		if (!mWorkspace.isOnSpringMode()) {// 编辑模式下屏蔽事件
			for (OnKeyDownListenner lis : getOnkeydownLisList()) {
				if (lis != null)
					handleBack = handleBack || lis.onKeyDownProcess(keyCode, event);
			}
		}

		// 返回主屏
		if (keyCode == KeyEvent.KEYCODE_BACK && !handleBack) {
			if (!mWorkspaceLayer.isOnZeroView() && mWorkspace.getDefaultScreen() == mWorkspace.getCurrentScreen()) {
				WallpaperHelper.notUpdateWorkspace();
				mWorkspaceLayer.snapToZeroView();
			} else {
				mWorkspace.requestFocus();
				mWorkspace.snapToScreen(mWorkspace.getDefaultScreen());
				mDockbar.snapToScreen(1);
			}
		}

		if (!handled && acceptFilter() && keyCode != KeyEvent.KEYCODE_ENTER) {
			boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace, getDefaultKeySsb(), keyCode, event);
			if (gotKey && getDefaultKeySsb() != null && getDefaultKeySsb().length() > 0) {
				return onSearchRequested();
			}
		}

		// Eat the long press event so the keyboard doesn't come up.
		if ((keyCode == KeyEvent.KEYCODE_MENU && (event.isLongPress() || isOnWidgetEditMode()))) {
			return true;
		}
		
		return handled;
	}
	
	/**
	 * 是否拦截返回键 <br>
	 */
	private boolean handleBackHere(KeyEvent event) {
		boolean result = mWorkspace.isOnSpringMode() || previewEditController.isPreviewMode() || (!event.isCanceled() && isOnWidgetEditMode()) || !event.isCanceled()
				&& mWorkspaceLayer.isOnZeroView();

//		if (mFolderOpenController != null)
//			result = result || mFolderOpenController.isEditMode() || isFolderOpened();
		
		return result;
	}
	
	void handleFolderClick(FolderInfo folderInfo, View view) {
		setupFolder();
		if (!folderInfo.opened) {
			closeFolder();
			openFolder(folderInfo, view);
		} 
	}
	
	/**
	 * 处理集成文件夹
	 * @param folderInfo
	 * @param view
	 */
	void handleIntegrateFolderClick(FolderInfo folderInfo, View view) {
		setupIntegrateFolder();
		if (!folderInfo.opened) {
			closeIntegrateFolder();
			openIntegrateFolder(folderInfo, view);
		} 
	}
	
	/**
	 * 打开集成文件夹
	 * @param folderInfo
	 * @param view
	 */
	public void openIntegrateFolder(FolderInfo folderInfo, View view) {
		if (view == null)
			return;
		if (folderInfo instanceof FolderInfo) {
			integrateFolder.openFolder(view, (FolderInfo) folderInfo);
		}
	}

	/**
	 * 关闭集成文件夹
	 */
	public void closeIntegrateFolder() {
		if (integrateFolder != null)
			integrateFolder.closeFolder();
	}

	/**
	 * 初始化集成文件夹
	 */
	private void setupIntegrateFolder() {
		if (integrateFolder != null) {
			return;
		}
		integrateFolder = IntegrateFolder.fromXml(this);
		//设置拖动控制器
//		mFolderOpenController.setDragController((DragController)mDragController);
//		((DragController)mDragController).setFolderOpenController(mFolderOpenController);
		//设置keydown监听
		addOnKeyDownListener(integrateFolder);
	}

	public FolderSwitchController getFolderCotroller() {
		setupFolder();
		return mFolderOpenController;
	}
	
	@Override
	public void setupHelper() {
		super.setupHelper();
		mCustomIntentSwitcherController = CustomIntentSwitcherController.getNewInstance();

	}
	
	/**
	 * 初始化文件夹
	 */
	public void setupFolder() {
		if (mFolderOpenController != null) {
			return;
		}

		View.inflate(this, R.layout.launcher_folder_open_fullscreen_style, mDragLayer);
		mFolderOpenController = new FolderSwitchController(this);
		mFolderOpenController.setDragController((DragController)mDragController);
		((DragController)mDragController).setFolderOpenController(mFolderOpenController);

		addOnKeyDownListener(mFolderOpenController);
	}
	
	private void openFolder(FolderInfo folderInfo, View view) {
		if (view == null)
			return;
		if (folderInfo instanceof FolderInfo) {
			mFolderOpenController.setClickFolder(view, (FolderInfo) folderInfo);
			mFolderOpenController.openFolder(FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER);
		}
	}
	
	public void closeFolder() {
		if (getFolderCotroller() != null)
			getFolderCotroller().closeFolder();
	}
	
	@Override
	public boolean isFolderOpened() {
		return mFolderOpenController != null && mFolderOpenController.isFolderOpened();
	}
	
	public void renameFolder(long id, String name) {
		if (!TextUtils.isEmpty(name) || id != FolderInfo.INVALIDATE_FOLDER_ID) {
			if (mClickView != null && mClickView instanceof FolderIconTextView) { // 先查找之前记录的view
				FolderIconTextView folderIcon = (FolderIconTextView) mClickView;
				FolderInfo folderInfo = (FolderInfo) folderIcon.getTag();
				if(folderInfo != null && folderInfo.id == id){
					//存库
					folderInfo.title = name;
					BaseLauncherModel.updateItemInDatabase(this, folderInfo);
					//刷新view
					folderIcon.setText(name);
					folderIcon.invalidate();
				}
			}
		}
	}
	
	public LauncherMenu getLauncherMenu() {
		if(mLauncherMenu == null){
			mLauncherMenu = new LauncherMenu(this);
		}
		return mLauncherMenu;
	}
	
	public Workspace getWorkspace(){
		return (Workspace) mWorkspace;
	}
	
	@Override
	public BaseDragController createDragController(){
		return new DragController(this);
	}
	
	/**
	 * 初始化编辑模式栏
	 */
    @DebugLog
	public void setupEditor() {
		if (editor != null) {
			return;
		}

//		editorStub.inflate();
		View.inflate(this, R.layout.launcher_edit_view, mDragLayer);
		editor = (LauncherEditView) mDragLayer.findViewById(R.id.launcher_editor);
		editor.setLauncher(this);
		editor.setDockbar(mDockbar);
		editor.setLauncherLineLightBar(lightbar);
		// editor.setLauncherCommonLightBar(commonLightbar);
	}
	
	void closeSystemDialogs() {
		try {
			getWindow().closeAllPanels();
			setWorkspaceLocked(false);
		} catch (Exception e) {
			e.printStackTrace();
			setWorkspaceLocked(false);
		}
	}
	
	/**
	 * 初始化垃圾桶 <br>
	 */
	@Override
	public DeleteZone inflateDeleteZone() {
		View.inflate(this, R.layout.launcher_delete_zone, mDragLayer);
		return (DeleteZone) mDragLayer.findViewById(R.id.launcher_delete_zone);
	}
	
	@Override
	public BaseDeleteZoneTextView getDeleteZoneTextView() {
		return (BaseDeleteZoneTextView) findViewById(R.id.im_delete);
	}
	
	@Override
	public BaseDeleteZoneTextView getUninstallZoneTextView() {
		return (BaseDeleteZoneTextView) findViewById(R.id.im_uninstallIm);
	}

    @DebugLog
	@Override
	public void onCreateEnd(){
		addOnKeyDownListener(mDragController);
		addOnKeyDownListener(previewEditController);
		// 添加推送SDK适配器
        // TODO: init or setup push sdk
//		PushManager.getInstance().setPushSDKAdapter(new PushSDKAdapter());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LauncherActivityResultHelper.onActivityResult(requestCode, resultCode, data, this);
	}
	
	@Override
	public View inflateLauncherMenu(){
		return View.inflate(this, R.layout.launcher_menu_view, null);
	}
	
	@Override
	public void dismissBottomMenu(){
		getLauncherMenu().dismiss();
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		boolean flag = true;
		if (isFolderOpened() || mWorkspace.isOnSpringMode() || previewEditController.isPreviewMode() || mWorkspaceLayer.isOnZeroView()) {
			flag = false;
		} else {
			flag = true;
		}

		if (flag) {
			getLauncherMenu().updateMenu();
		}
		return false;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (null == intent)
			return;
		
		super.onNewIntent(intent);
		
		// Close the menu
		if (Intent.ACTION_MAIN.equals(intent.getAction())) {
			// also will cancel mWaitingForResult.
			closeSystemDialogs();

			boolean alreadyOnHome = ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			boolean allAppsVisible = isAllAppsVisible();
			if (mNavigationView != null && mWorkspaceLayer.isOnZeroView() && alreadyOnHome && !allAppsVisible) {
//				mNavigationView.onBackKeyDown();
			} else if (mWorkspace != null && !mWorkspace.isDefaultScreenShowing() && alreadyOnHome && !allAppsVisible) {
				mWorkspace.moveToDefaultScreen(alreadyOnHome && !allAppsVisible);
			}

			/**
			 * 在匣子打开文件夹的情况下，摁home键返回桌面，会导致匣子关掉，并导致一系列匣子文件夹的错误<br>
			 * 处理方案：如果重返桌面时匣子文件夹打开状态则关掉
			 */
			if (mFolderOpenController != null && mFolderOpenController.isFolderOpened() && mFolderOpenController.getOpenFolderFrom() == FolderSwitchController.OPEN_FOLDER_FROM_DRAWER) {
				mFolderOpenController.closeFolderWithoutAnimation(false);
			}
			
			final View v = getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}
	}
	
	@Override
	public void addNewInstallApps(List<ApplicationInfo> apps, String packageName){
		for(ApplicationInfo info : apps){
			int[] pageInfo = LauncherProviderHelper.findVacantCell4AppAdd(getBaseContext());
			if (pageInfo == null) {
				Log.e(Global.TAG, "can't find cell for new app");
				continue;
			} 
			int page = pageInfo[0];
			int cellX = pageInfo[1];
			int cellY = pageInfo[2];
			info.screen = page;
			info.cellX = cellX;
			info.cellY = cellY;
			info.spanX = 1;
			info.spanY = 1;
			BaseLauncherModel.addOrMoveItemInDatabase(getBaseContext(), info, LauncherSettings.Favorites.CONTAINER_DESKTOP);
			
			View view = mWorkspace.createViewByItemInfo(info);
			if (view == null)
				return;
			((Workspace)mWorkspace).addInScreen(view, page, cellX, cellY, 1, 1);
			
			//FIXME 在编辑模式下可能会有刷新的问题
		}
	}
	
	public void setClickView(View v) {
		mClickView = v;
	}
	
	
	/**
	 * 处理桌面重启
	 */
	public void restartMoboLauncher() {
		//先回退到桌面
		Intent intent = getIntent();
		if(intent == null ){
			return;
		}
		startActivity(intent);
		//杀死进程
		NotificationManager nManager = (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);
		nManager.cancelAll();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
