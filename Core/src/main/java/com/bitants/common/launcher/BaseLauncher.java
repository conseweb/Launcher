package com.bitants.common.launcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.bitants.common.core.widget.PandaWidgetViewContainer;
import com.bitants.common.framework.OnKeyDownListenner;
import com.bitants.common.framework.view.BaseLineLightBar;
import com.bitants.common.kitset.util.StatusBarUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.launcher.broadcast.LauncherBroadcastReceiverManager;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.CellLayoutConfig;
import com.bitants.common.launcher.config.ConfigFactory;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.info.WidgetInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.launcher.model.BaseLauncherSettings;
import com.bitants.common.launcher.model.load.LauncherBinder;
import com.bitants.common.launcher.model.load.LauncherPreLoader;
import com.bitants.common.launcher.screens.DeleteZone;
import com.bitants.common.launcher.screens.DragLayer;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.launcher.screens.SingleViewGroup;
import com.bitants.common.launcher.screens.WorkspaceLayer;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.screens.preview.PreviewEditAdvancedController;
import com.bitants.common.launcher.support.BaseLauncherViewHelper;
import com.bitants.common.launcher.support.DragLayerStuffDrawer;
import com.bitants.common.launcher.support.LauncherAppWidgetHost;
import com.bitants.common.launcher.support.LauncherWidgetEditHelper;
import com.bitants.common.launcher.support.WallpaperHelper;
import com.bitants.common.launcher.touch.BaseDragController;
import com.bitants.common.kitset.GpuControler;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.launcher.broadcast.LauncherBroadcastControl;
import com.bitants.common.launcher.config.preference.BaseConfigPreferences;
import com.bitants.common.launcher.screens.dockbar.DockbarCellLayoutConfig;
import com.bitants.common.launcher.screens.dockbar.MagicDockbarRelativeLayout;
import com.bitants.common.R;
import com.bitants.common.launcher.view.BaseDeleteZoneTextView;
import com.bitants.common.launcher.support.BaseIconCache;

public class BaseLauncher extends Activity implements View.OnClickListener, OnLongClickListener{

	static final boolean PROFILE_STARTUP = false;
	
	private boolean mWorkspaceLoading = true;
	/**
	 * 是否加载完毕
	 */
	private boolean mIsFinishBinding = false;
	/**
	 * 是否已初始化workspace
	 */
	private boolean hasSetupWorkspace = false;
	private final Object hasSetupWorkspaceLock = new Object();
	
	/**
	 * 是否已加载workspace数据
	 */
	private boolean hasLoadWorkspace = false;
	private final Object hasLoadWorkspaceLock = new Object();
	/**
	 * 是否已绑定workspace上各应用
	 */
	private boolean startBindWorkspace = false;
	private final Object startBindWorkspaceLock = new Object();
	
	
	private Bundle mSavedState;
//	private Bundle mSavedInstanceState;
	
	
	public BaseDragController mDragController;
	
	public DragLayer mDragLayer;
	public BaseLauncherModel mModel;
	
	public static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
	
	private boolean isWorkspaceLocked;
	public ScreenViewGroup mWorkspace;
	public WorkspaceLayer mWorkspaceLayer;
	public BaseMagicDockbar mDockbar;
	public BaseLineLightBar lightbar;
	public DeleteZone mDeleteZone;
	public ViewGroup mZeroViewGroup;
	
	BaseIconCache mIconCache;
	LauncherBinder mLauncherBinder;
	protected MagicDockbarRelativeLayout bottomContainer;
	
	public LauncherAppWidgetHost mAppWidgetHost;
	
	/**
	 * 桌面启动时，异步数据加载
	 */
	private LauncherPreLoader mLauncherPreLoader;
	
	private AppWidgetManager mAppWidgetManager;
	
	public static final int APPWIDGET_HOST_ID = 1024;
	
	private SpannableStringBuilder mDefaultKeySsb = null;
	private boolean mRestoring;
	
	private static final String PREFERENCES = "launcher.preferences";
	
	private String uninstallPackageName = null;// 在桌面上或匣子里被卸载的应用的包名
	protected WallpaperHelper mWallpaperHelper;
	
	public List<PandaWidgetViewContainer> pandaWidgets = new ArrayList<PandaWidgetViewContainer>();
	
	/**
	 * 屏幕预览/管理控制器
	 */
	public PreviewEditAdvancedController previewEditController;
	public LauncherWidgetEditHelper widgetEditHelper; // 小插件处理
	
	public boolean isDeleteZone; // 放入回收站与卸载类型
	
	private List<OnKeyDownListenner> onkeydownLisList = new ArrayList<OnKeyDownListenner>();
	
	/**
	 * 快捷菜单与menu时盖上一层阴影
	 */
	private View topShadowView;
	
	public static boolean hasDrawer = true;//是否有匣子
	
	protected boolean isNewInstall = false;//是否初次安装加载桌面
	protected boolean isUpdateInstall = false;//是否升级安装加载桌面
	
	protected boolean reLayoutWorkspaceAndDockbar = false;
	
	protected boolean isTranslucentStatusBar = false;
	protected boolean isTranslucentActionBar = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (PROFILE_STARTUP) {
			android.os.Debug.startMethodTracing(Environment.getExternalStorageDirectory().getAbsolutePath() + "/launcher");
		}
		super.onCreate(savedInstanceState);
		
		onPreCreateStart();
		
		onCreateStart();
		initGlobalSomething();
		processThemeIssue();//待修改
		loadWorspaceFirst();
		setupHelper();//
		
		setContentView();
		
		setupViews();//
		register();//

		loadDataIfNeed(savedInstanceState);

		bindService();//
		
		GpuControler.openGpu(this);

		TelephoneUtil.logPhoneState();
		
		if (PROFILE_STARTUP) {
			android.os.Debug.stopMethodTracing();
		}
		recordLauncherCreateTime();
		
		onCreateEnd();
	}
	
	/**
	 * 优先级最高的操作
	 */
	protected void onPreCreateStart(){
//		isNewInstall = ConfigFactory.isNewInstall(this);
		//确定是否升级或新安装
		welcomeOrUpdate();
		BaseConfig.setBaseLauncher(this);
		LauncherConfig.initCellConfig(this, isNewInstall);
	}
	
	/**
	 * 打开新手欢迎页或升级提示页，升级用户还会进行数据升级、更换默认壁纸等操作
	 */
	protected void welcomeOrUpdate() {
		ConfigFactory.maybeShowReadme(this, new ConfigFactory.ConfigCallback() {
            @Override
            public boolean onAction() {
                isNewInstall = true;
                Log.w("Launcher", "isNewInstall");
                // 记录用户第一次使用桌面的时间 caizp 2012-12-4
                BaseConfigPreferences.getInstance().setFirstLaunchTime(System.currentTimeMillis());
                // 新安装用户不显示更新日志
                String curVersionName = TelephoneUtil.getVersionName(BaseLauncher.this);
                BaseConfigPreferences.getInstance().setVersionShowed(curVersionName, true);

                int curVersionCode = TelephoneUtil.getVersionCode((BaseLauncher.this));
                // 记录版本号
                BaseConfigPreferences.getInstance().setVersionCodeShowed(curVersionCode, true);
                // 记录新用户初次版本号
                BaseConfigPreferences.getInstance().setVersionCodeFrom(curVersionCode);

                setupReadMeForNewUser();

                BaseConfigPreferences.getInstance().setLastVersionCode(curVersionCode);

                BaseConfigPreferences.getInstance().setDefaultScreen(ScreenViewGroup.DEFAULT_SCREEN);
                return true;
            }
        }, new ConfigFactory.ConfigCallback() {
            @Override
            public boolean onAction() {
                int curVersionCode = TelephoneUtil.getVersionCode((BaseLauncher.this));
                if (!BaseConfigPreferences.getInstance().isVersionCodeShowed(curVersionCode)) {
                    isUpdateInstall = true;
                    Log.w("Launcher", "isUpdateInstall");
                    String curVersionName = TelephoneUtil.getVersionName(BaseLauncher.this);

                    BaseConfigPreferences.getInstance().setVersionShowed(curVersionName, true);
                    // 记录版本号
                    BaseConfigPreferences.getInstance().setVersionCodeShowed(curVersionCode, true);

                    setupReadMeForOldUser();

                    BaseConfigPreferences.getInstance().setLastVersionCode(curVersionCode);
                }
                return false;
            }
        });
		
	}
	
	/**
	 * 初始化数据，拖动，图标，字体等 <br>
	 */
	protected void initGlobalSomething() {
		BaseLauncherApplication app = ((BaseLauncherApplication) getApplication());
		mLauncherBinder = new LauncherBinder(this);
		mModel = app.setLauncher(mLauncherBinder);
		mModel.setLauncher(this);
		mIconCache = app.getIconCache();
		mDragController = createDragController();
		BaseConfig.resetDefaultFontMeasureSize();
		LauncherBroadcastReceiverManager.getInstance().setLauncher(this);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); 
	}
	
	/**
	 * 预加载桌面数据 <br>
	 */
	private void loadWorspaceFirst() {
		if(isAsyncLoadLauncherData()){
			mLauncherPreLoader = new LauncherPreLoader(mLauncherBinder, mModel, this);
			mLauncherPreLoader.start();
		}
	}
	
	/**
	 * 是否异步加载桌面数据
	 * @return
	 */
	private boolean isAsyncLoadLauncherData(){
		return BaseSettingsPreference.getInstance().isAsyncLoadLauncherData() && !isNewInstall && !isUpdateInstall;
	}
	
	void processThemeIssue() {
		
	}
	
	protected void setupHelper() {
		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
		mAppWidgetHost.startListening();
		
		widgetEditHelper = new LauncherWidgetEditHelper(this);
	}
	
	void setContentView(){
		setContentView(R.layout.launcher);
	}
	
	void setupViews() {// long start = System.currentTimeMillis();
		BaseDragController dragController = mDragController;

		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		final DragLayer dragLayer = mDragLayer;

		dragController.setDragLayer(dragLayer);		
		dragLayer.setDragController(dragController);
		
		DragLayerStuffDrawer mDragLayerStuff = new DragLayerStuffDrawer();
		mDragLayerStuff.setDragLayer(dragLayer);
		dragLayer.setDragLayerStuff(mDragLayerStuff);


		mWorkspaceLayer = (WorkspaceLayer) dragLayer.findViewById(R.id.workspace_layer);
		mWorkspaceLayer.setLauncher(this);

		mWorkspace = (ScreenViewGroup) dragLayer.findViewById(R.id.workspace);
//		final ScreenViewGroup workspace = (ScreenViewGroup) mWorkspace;
		mWorkspace.setHapticFeedbackEnabled(false);
		// Log.e(TAG, "mWorkspace:" + (System.currentTimeMillis() - start));
		mWallpaperHelper = WallpaperHelper.getInstance();
		mWallpaperHelper.setWorkspaceLayer(mWorkspaceLayer);
		mWallpaperHelper.setWorkspace(mWorkspace);
		mWallpaperHelper.setDragLayer(mDragLayer);
		mWorkspace.setWallpaperHelper(mWallpaperHelper);
		mWorkspaceLayer.setWallpaperHelper(mWallpaperHelper);
		mDragLayerStuff.setWallPaperHelper(mWallpaperHelper);

		mDockbar = (BaseMagicDockbar) dragLayer.findViewById(R.id.quick_start_bar);
		bottomContainer = (MagicDockbarRelativeLayout) dragLayer.findViewById(R.id.lightbar_container);
		if(isTranslucentActionBar()){
			FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) bottomContainer.getLayoutParams();
			lp.bottomMargin = BaseConfig.getLauncherBottomPadding();
		}
		
		
		mWorkspace.setOnLongClickListener(this);
		mWorkspace.setDragController(dragController);
		mWorkspace.setLauncher(this);

		dragController.addDragScoller(mWorkspace);
		// dragController.setDragListener(deleteZone);
		dragController.setWorkspace(mWorkspace);
		dragController.setMoveTarget(mWorkspace);

		// The order here is bottom to top.
		dragController.addDropTarget(mWorkspace);
		// dragController.addDropTarget(deleteZone);
		//条形指示灯
		lightbar = (BaseLineLightBar) dragLayer.findViewById(R.id.lightbar);
		lightbar.setLinkedView(mWorkspace);
		lightbar.setLauncher(this);
		mWorkspace.setLightBar(lightbar);
		
		bottomContainer.setLauncher(this);
		mDockbar.setDragController(mDragController);
		mDockbar.setLauncher(this);
		dragController.addDropTarget(mDockbar);
		
		previewEditController = new PreviewEditAdvancedController(this);
		previewEditController.setDragController(mDragController);
		
		setupZeroView();
		
		isTranslucentStatusBar = StatusBarUtil.translucentStatusBar(this);
		
		//校正dock栏高度
		fixDockBarLayout();
	}
	
	void register() {
		
	}
	
	/**
	 * 预加载未完成则重新加载 <br>
	 */
	private void loadDataIfNeed(Bundle savedInstanceState) {
		setSavedState(savedInstanceState);
		setHasSetupWorkspace(true);

		if (!mRestoring) {
			mModel.startLoader(this, true, false, isAsyncLoadLauncherData());
		}
		// For handling default keys
		mDefaultKeySsb = new SpannableStringBuilder();
		Selection.setSelection(mDefaultKeySsb, 0);
	}
	
	/**
	 * 启动服务
	 */
	void bindService() {
		
	}
	
	/**
	 * 先填充0屏 <br>
	 */
	private void setupZeroView() {
		if (mZeroViewGroup != null)
			return;

		mZeroViewGroup = new SingleViewGroup(this);
		if (mWorkspaceLayer.getChildCount() <= 1) {
			mWorkspaceLayer.addView(mZeroViewGroup, 0);
			mWorkspaceLayer.setZeroView();
			mWorkspaceLayer.setLoadZeroView(true);
		}
		inflateZeroView();
	}
	
	public void setupDeleteZone() {
		if(mDeleteZone == null){			
			mDeleteZone = inflateDeleteZone();
			if(mDeleteZone != null){
				mDeleteZone.setLauncher(this);
				mDeleteZone.setDragController(mDragController);
			}
		}else{
			mDeleteZone.reset();
			mDeleteZone.bringToFront();
		}
	}
	
	public DeleteZone getDeleteZone() {
		return mDeleteZone;
	}

	public ViewGroup getZeroViewGroup() {
		return mZeroViewGroup;
	}
	
	/**
	 * 记录用户启动时间
	 */
	private void recordLauncherCreateTime() {
		// 保存桌面显示完成的时间
		BaseConfigPreferences.getInstance().setLauncherCreateTime(System.currentTimeMillis());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// 清除可能存在的celllayout图标移动动画
		mWorkspace.cleanReorderAllState();
		mWorkspace.restoreFolderAnimation();
		if(mDockbar != null){			
			mDockbar.restoreReorderEx();
		}
		refreshWorkspaceSpringScreen();
		if (isRestoring()) {
			final int[] countXY = BaseSettingsPreference.getInstance().getScreenCountXY();
			if (countXY[0] != CellLayoutConfig.getCountX() || countXY[1] != CellLayoutConfig.getCountY()) {
				// 重置CellLayout的XY行列数
				CellLayoutConfig.resetXYCount(countXY[0], countXY[1]);
				mWorkspace.reLayoutAllCellLayout();
			}
			setRestoring(false);
		}

		// 如果之前进行卸载应用操作，删除该应用在桌面和匣子里的图标
		if (uninstallPackageName != null) {
			mLauncherBinder.bindAppsRemoved(uninstallPackageName);
			uninstallPackageName = null;
		}
		
		// 在屏幕预览模式下进入其他界面返回，刷新屏幕预览界面，解决添加按钮不显示的问题 caizp 2013-9-11
		if (null != previewEditController && previewEditController.isPreviewMode()) {
			previewEditController.refreshPreviewView();
		}
				
		if(!isOnSpringMode() && BaseConfigPreferences.getInstance().hasSpringAddScreen()){
			int index = mWorkspace.getChildCount() - 1;
			if(mWorkspace.getCellLayoutAt(index).getChildCount() == 0){                				
				BaseConfigPreferences.getInstance().setHasSpringAddScreen(false);
				mWorkspace.removeScreenFromWorkspace(index);
			}
		}
		
//		if (Build.VERSION.SDK_INT >= 14) {// android4.0以上，壁纸自绘
//			BaseConfig.isDrawWallPaper = true;
//			WallpaperInfo infoHelper = mWallpaperHelper.getWallpaperManager().getWallpaperInfo();
//			if (infoHelper != null) {
//				BaseConfig.isDrawWallPaper = false;
//			}
//		}
		int wh[];
		wh= ScreenUtil.getScreenWH();
		if (wh[0] == 320 && wh[1] == 480 ) {
			BaseConfig.isDrawWallPaper = true;
			WallpaperInfo infoHelper = mWallpaperHelper.getWallpaperManager().getWallpaperInfo();
			if (infoHelper != null) {
				BaseConfig.isDrawWallPaper = false;
			}
		}
		
		if (reLayoutWorkspaceAndDockbar) {
			showOrHideDockBarText();
			reLayoutWorkspaceAndDockbar = false;
		}
		
		if(!updateViewLayoutOnWindowLevel()){
			StatusBarUtil.toggleStateBar(this, true);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// 用户统计数据上传
//		LauncherConfig.getLauncherHelper().startUpHiAnalytics(this);
		
		LauncherBroadcastControl.sendBrocdcastLauncherOnstart(this);//桌面启动广播
		
		WallpaperHelper.getInstance().suggestWallpaperDimensions(this);
	}
	
	/**
	 * 桌面数据是否加载完成 <br>
	 */
	public boolean hasLoadWorkspace() {
		synchronized (hasLoadWorkspaceLock) {
			return hasLoadWorkspace;
		}
	}

	public void setHasLoadWorkspace(boolean hasLoadWorkspace) {
		synchronized (hasLoadWorkspaceLock) {
			this.hasLoadWorkspace = hasLoadWorkspace;
		}
	}
	
	public boolean hasSetupWorkspace() {
		synchronized (hasSetupWorkspaceLock) {
			return hasSetupWorkspace;
		}
	}

	public void setHasSetupWorkspace(boolean hasSetupWorkspace) {
		synchronized (hasSetupWorkspaceLock) {
			this.hasSetupWorkspace = hasSetupWorkspace;
		}
	}
	
	public void setIsFinishBinding(boolean isFinish) {
		mIsFinishBinding = isFinish;
	}
	
	public boolean isFinishBinding() {
		return mIsFinishBinding;
	}
	
	public boolean isWorkspaceLoading() {
		return mWorkspaceLoading;
	}

	public void setWorkspaceLoading(boolean mWorkspaceLoading) {
		this.mWorkspaceLoading = mWorkspaceLoading;
	}
	
	/**
	 * 是否允许绑定桌面 <br>
	 */
	public boolean allowToBindWorkspace() {
		synchronized (startBindWorkspaceLock) {
			if (!hasLoadWorkspace() || startBindWorkspace) {
				return false;
			} else {
				startBindWorkspace = true;
				return true;
			}
		}
	}
	
	
	public Bundle getSavedState() {
		return mSavedState;
	}

	public void setSavedState(Bundle mSavedState) {
		this.mSavedState = mSavedState;
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// Do not call super here
//		mSavedInstanceState = savedInstanceState;
	}
	
	public BaseDragController getDragController() {
		return mDragController;
	}
	
	public DragLayer getDragLayer(){
		return mDragLayer;
	}
	
	public BaseLauncherModel getLauncherModel() {
		return mModel;
	}
	
	public void setWorkspaceLocked(boolean locked){
		isWorkspaceLocked = locked;
	}
	
	public boolean isWorkspaceLocked() {
		return isWorkspaceLocked;
	}
	
	public void lockSnapToScreenTmp(){
		lockSnapToScreenTmp(500);
	}
	
	public void lockSnapToScreenTmp(int duration){
		if(mWorkspace != null){			
			isWorkspaceLocked = true;
			mWorkspace.postDelayed(new Runnable(){
				@Override
				public void run() {
					isWorkspaceLocked = false;
				}
			}, duration);
		}
		
		if(mWorkspaceLayer != null){
			mWorkspaceLayer.setLockSnapToScreen(true);
			mWorkspaceLayer.postDelayed(new Runnable(){
				@Override
				public void run() {
					mWorkspaceLayer.setLockSnapToScreen(false);
				}
			}, duration);
		}
	}
	
	public void delayRefreshWorkspaceSpringScreen(int interval) {
		if (mWorkspace.isOnSpringMode()) {
			mWorkspace.delayRefreshSpringScreen(interval);
		}
	}
	
	public WorkspaceLayer getWorkspaceLayer() {
		return mWorkspaceLayer;
	}
	
	public BaseMagicDockbar getDockbar() {
		return mDockbar;
	}
	
	public BaseLineLightBar getLightbar() {
		return lightbar;
	}
	
	public LauncherAppWidgetHost getAppWidgetHost() {
		return mAppWidgetHost;
	}
	
	public LauncherBinder getLauncherBinder() {
		return mLauncherBinder;
	}
	
	public void setShowLoadingProgress(boolean isShowLoadingProgress) {
		mLauncherBinder.setShowLoadingProgress(isShowLoadingProgress);
	}
	
	public AppWidgetManager getAppWidgetManager() {
		return mAppWidgetManager;
	}
	
	public String getTypedText() {
		return mDefaultKeySsb.toString();
	}

	public void clearTypedText() {
		mDefaultKeySsb.clear();
		mDefaultKeySsb.clearSpans();
		Selection.setSelection(mDefaultKeySsb, 0);
	}
	
	public SpannableStringBuilder getDefaultKeySsb() {
		return mDefaultKeySsb;
	}

	public boolean isRestoring() {
		return mRestoring;
	}

	public void setRestoring(boolean mRestoring) {
		this.mRestoring = mRestoring;
	}
	
	/**
	 * 语言变化不做动作，先屏蔽 <br>
	 *
	 * @deprecated
	 */
	void checkForLocaleChange() {
		final LocaleConfiguration localeConfiguration = new LocaleConfiguration();
		readConfiguration(this, localeConfiguration);

		final Configuration configuration = getResources().getConfiguration();

		final String previousLocale = localeConfiguration.locale;
		final String locale = configuration.locale.toString();

		final int previousMcc = localeConfiguration.mcc;
		final int mcc = configuration.mcc;

		final int previousMnc = localeConfiguration.mnc;
		final int mnc = configuration.mnc;

		boolean localeChanged = !locale.equals(previousLocale) || mcc != previousMcc || mnc != previousMnc;

		if (localeChanged) {
			localeConfiguration.locale = locale;
			localeConfiguration.mcc = mcc;
			localeConfiguration.mnc = mnc;

			writeConfiguration(this, localeConfiguration);
			mIconCache.flush();
		}
	}

	private static class LocaleConfiguration {
		public String locale;
		public int mcc = -1;
		public int mnc = -1;
	}

	private static void readConfiguration(Context context, LocaleConfiguration configuration) {
		DataInputStream in = null;
		try {
			in = new DataInputStream(context.openFileInput(PREFERENCES));
			configuration.locale = in.readUTF();
			configuration.mcc = in.readInt();
			configuration.mnc = in.readInt();
		} catch (FileNotFoundException e) {
			// Ignore
		} catch (IOException e) {
			// Ignore
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	private static void writeConfiguration(Context context, LocaleConfiguration configuration) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(context.openFileOutput(PREFERENCES, MODE_PRIVATE));
			out.writeUTF(configuration.locale);
			out.writeInt(configuration.mcc);
			out.writeInt(configuration.mnc);
			out.flush();
		} catch (FileNotFoundException e) {
			// Ignore
		} catch (IOException e) {
			// noinspection ResultOfMethodCallIgnored
			context.getFileStreamPath(PREFERENCES).delete();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}
	
	public void setUninstallPackageName(String uninstallPackageName) {
		this.uninstallPackageName = uninstallPackageName;
	}
	
	/**
	 * Description: 刷新编辑模式页面
	 */
	public void refreshWorkspaceSpringScreen() {
		if (mWorkspace.isOnSpringMode()) {
			mWorkspace.refreshSpringScreen();
		}
	}
	
	/**
	 * Description: 是否处于屏幕编辑模式
	 */
	public boolean isOnSpringMode() {
		return mWorkspace.isOnSpringMode();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		// Flag the loader to stop early before switching
		mModel.stopLoader();
		return Boolean.TRUE;
	}
	
	/**
	 * Restores the previous state, if it exists.
	 * 
	 * @param savedState
	 *            The previous state. 减少加载时间，先屏蔽
	 * @deprecated
	 */
	void restoreState(Bundle savedState) {
//		if (savedState == null) {
//			return;
//		}
//
//		final boolean allApps = savedState.getBoolean(RUNTIME_STATE_ALL_APPS_FOLDER, false);
//		if (allApps) {
//			showAllApps(false);
//		}
//
//		final int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
//		if (currentScreen > -1) {
//			mWorkspace.setCurrentScreen(currentScreen);
//		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		
//		outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getCurrentScreen());
//		// should not do this if the drawer is currently closing.
//		if (isAllAppsVisible()) {
//			outState.putBoolean(RUNTIME_STATE_ALL_APPS_FOLDER, true);
//		}
//
//		if (mFolderInfo != null && isWorkspaceLocked()) {
//			outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
//			outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, mFolderInfo.id);
//		}
	}
	
	public WallpaperHelper getWallpaperHelper() {
		return mWallpaperHelper;
	}
	
	public void showNavigation(){
		getWorkspaceLayer().showZeroView();
	}
	
	public void hideNavigation(){
		getWorkspaceLayer().hideZeroView();
	}
	
	public void showBottomContainer(){
		bottomContainer.setVisibility(View.VISIBLE);
	}
	
	public void hideBottomContainer(){
		bottomContainer.setVisibility(View.INVISIBLE);
	}
	
	public ScreenViewGroup getScreenViewGroup(){
		return mWorkspace;
	}
	
	public boolean isWorkspaceVisable(){
		return mWorkspace.getVisibility() == View.VISIBLE;
	}
	
	public ViewGroup getBottomContainer() {
		return bottomContainer;
	}
	
	/**
	 * 缓存非标小部件
	 */
	public void ifNeedCache(WidgetInfo item, View view) {
		if (item.itemType != BaseLauncherSettings.Favorites.ITEM_TYPE_PANDA_WIDGET)
			return;
		
		if (!(view instanceof PandaWidgetViewContainer))
			return;
		
		PandaWidgetViewContainer result = (PandaWidgetViewContainer) view;
		if (getPackageName().equals(result.getWidgetPackage()))
			return;
		
		this.pandaWidgets.add(result);
		result.setHiViewGroup(mWorkspace);
	}
	
	/**
	 * 清理非标小部件
	 */
	public void ifNeedClearCache(View view) {
		if (view == null)
			return;
		
		if (!(view instanceof PandaWidgetViewContainer))
			return;
		
		this.pandaWidgets.remove(view);
	}
	
	public void invisiableWorkspace() {
		mWorkspaceLayer.setVisibility(View.INVISIBLE);
		mWorkspace.setVisibility(View.INVISIBLE);
		hideBottomContainer();
		if (mDeleteZone != null)
			mDeleteZone.setVisibility(View.INVISIBLE);

		mWorkspace.setFocusable(false);
		bottomContainer.setFocusable(false);
		
		mWorkspace.closeOnWorkspaceScreenListener();
	}
	
	public void visiableWorkspace() {
		mWorkspaceLayer.setVisibility(View.VISIBLE);
		mWorkspace.setVisibility(View.VISIBLE);
		if (!mWorkspace.isOnSpringMode()) {
			showBottomContainer();
		}
		mWorkspace.setFocusable(true);
		bottomContainer.setFocusable(true);
		
		mWorkspace.startOnWorkspaceScreenListener();
	}
	
	/**
	 * <br>
	 * Description: 获取屏幕预览/管理控制器 <br>
	 *
	 * @return
	 */
	public PreviewEditAdvancedController getPreviewEditController() {
		return previewEditController;
	}
	
	public boolean isDeleteZoneVisible() {
		if (mDeleteZone == null)
			return false;

		return mDeleteZone.getVisibility() == View.VISIBLE;
	}
	
	public void stopWidgetEdit(){
		if(widgetEditHelper != null){			
			widgetEditHelper.stopWidgetEdit();
		}
	}
	
	public boolean isOnWidgetEditMode(){
		if(widgetEditHelper == null){	
			return false;
		}
		return widgetEditHelper.mIsWidgetEditMode;
	}
	
	public boolean isPreviewMode(){
		return getPreviewEditController().isPreviewMode();
	}
	
	public boolean notActionWorkspaceLayerTouch(){
		if (isFolderOpened()) {
			closeFolder();
			return true;
		}
		if(isOnSpringMode() && isWorkspaceLocked()){
			return true;
		}
		return false;
	}
	
	public void addOnKeyDownListener(OnKeyDownListenner listener) {
		onkeydownLisList.add(listener);
	}
	
	public void addOnKeyDownListenerFirst(OnKeyDownListenner listener) {
		onkeydownLisList.add(0, listener);
	}
	
	public void removeOnKeyDownListener(OnKeyDownListenner listener) {
		onkeydownLisList.remove(listener);
	}
	
	public List<OnKeyDownListenner> getOnkeydownLisList() {
		return onkeydownLisList;
	}
	
	// We can't hide the IME if it was forced open. So don't bother
	/*
	 * @Override public void onWindowFocusChanged(boolean hasFocus) {
	 * super.onWindowFocusChanged(hasFocus);
	 * 
	 * if (hasFocus) { final InputMethodManager inputManager =
	 * (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	 * WindowManager.LayoutParams lp = getWindow().getAttributes();
	 * inputManager.hideSoftInputFromWindow(lp.token, 0, new
	 * android.os.ResultReceiver(new android.os.Handler()) { protected void
	 * onReceiveResult(int resultCode, Bundle resultData) { Log.d(TAG,
	 * "ResultReceiver got resultCode=" + resultCode); } }); Log.d(TAG,
	 * "called hideSoftInputFromWindow from onWindowFocusChanged"); } }
	 */

	public boolean acceptFilter() {
		final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		return !inputManager.isFullscreenMode();
	}
	
	public View getTopShadowView() {
		if (topShadowView != null)
			return topShadowView;

		topShadowView = new View(this);
		topShadowView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		topShadowView.setBackgroundColor(Color.parseColor("#AA000000"));
		topShadowView.setVisibility(View.GONE);

		mDragLayer.addView(topShadowView);
		return topShadowView;
	}
	
	public View createCommonAppView(ItemInfo itemInfo){
		return BaseLauncherViewHelper.createCommonAppView(this, (ApplicationInfo) itemInfo);
	}
	
	public View createFolderIconTextViewFromXML(ViewGroup parent, ItemInfo itemInfo){
		return BaseLauncherViewHelper.createFolderIconTextViewFromXML(this, parent, ((FolderInfo) itemInfo));
	}
	
	@Override
	public void onBackPressed() {

	}
	
	protected void fixDockBarLayout(){
		if(!BaseConfig.isOnScene()){
			if(LauncherConfig.getLauncherHelper().isShowDockbarText()){
				return;
			}
			int margin = -DockbarCellLayoutConfig.getDockBarHeightMargin(this);
			ViewGroup.LayoutParams lp = mDockbar.getLayoutParams();
			lp.height += margin;
			mDockbar.requestLayout();
			
			lp = bottomContainer.getLayoutParams();
			lp.height += margin;
			bottomContainer.requestLayout();
		}
	}
	
	//显示或隐藏dock上图标的title
	public void showOrHideDockBarText(){
		if(mDockbar == null || bottomContainer == null || mWorkspace == null || BaseConfig.isOnScene())
			return;
		boolean isShow = LauncherConfig.getLauncherHelper().isShowDockbarText();
		ViewGroup.LayoutParams lp = mDockbar.getLayoutParams();
		if(isShow == mDockbar.isShowAppTitle()){
			return;
		}
		mDockbar.setShowAppTitle(isShow);
		
		int margin  = isShow ? DockbarCellLayoutConfig.getDockBarHeightMargin(this) 
				: -DockbarCellLayoutConfig.getDockBarHeightMargin(this);
		lp.height += margin;
		mDockbar.changeCellLayoutConfig(DockbarCellLayoutConfig.getMarginTop() - margin);
		mDockbar.requestLayout();
		
		lp = bottomContainer.getLayoutParams();
		lp.height += margin;
		bottomContainer.requestLayout();
		
		mWorkspace.changeCellLayoutMarginBottom(CellLayoutConfig.getMarginBottom() + margin);
		mWorkspace.requestLayout();
		
		if(isOnSpringMode()){//校正编辑模式布局			
			mWorkspace.reLayoutSpringMode(margin);
		}
	}
	
	/**
	 * 重新进行Window布局，修正如输入框产生的布局异常
	 */
	public boolean updateViewLayoutOnWindowLevel(){
		if(bottomContainer == null || !mIsFinishBinding || mDragLayer == null)
			return false;
		int[] loc = new int[2];
		bottomContainer.getLocationOnScreen(loc);
		if(loc[1] + bottomContainer.getHeight() < mDragLayer.getHeight()){
			Log.e("fix dockbar", "relayout");
			final boolean toggle = BaseSettingsPreference.getInstance().isNotificationBarVisible() ? false : true;
			StatusBarUtil.toggleStateBar(BaseConfig.getBaseLauncher(), toggle);
			bottomContainer.postDelayed(new Runnable(){
				@Override
				public void run() {
					StatusBarUtil.toggleStateBar(BaseConfig.getBaseLauncher(), !toggle);
				}
			}, 700);
			return true;
		}
		return false;
	}

	/**
	 * 是否通知栏透明
	 * @return
	 */
	public boolean isTranslucentStatusBar() {
		return isTranslucentStatusBar;
	}
	
	/**
	 * 是否导航栏透明
	 * @return
	 */
	public boolean isTranslucentActionBar() {
		return isTranslucentActionBar;
	}
	
	//===========================桌面加载时使用============================//
	/**
	 * Launcher的oncreate()开始调用时
	 */
	public void onCreateStart(){
		
	}
	
	/**
	 *  Launcher的oncreate()调用结束后
	 */
	public void onCreateEnd(){
		
	}
	
	/**
	 * 新用户初次启动桌面时
	 */
	public void setupReadMeForNewUser(){
		
	}
	
	/**
	 * 升级户启动桌面时
	 */
	public void setupReadMeForOldUser(){
		
	}
	
	//======================用于渲染桌面其它视图组件=========================//
	/**
	 * 渲染顶部删除区
	 */
	public DeleteZone inflateDeleteZone() {
		return null;
	}
	
	/**
	 * 渲染0屏内容
	 */
	public void inflateZeroView() {
		
	}
	
	/**
	 * 渲染菜单栏
	 * @return
	 */
	public View inflateLauncherMenu(){
		return null;
	}
	
	
	
	/**
	 * 是否匣子可见
	 * @return
	 */
	public boolean isAllAppsVisible() {
		return false;
	}
	
	/**
	 * 滑向0屏隐藏dock栏时调用
	 * @param vg
	 */
	public void onHideDockbarForNavigation(ViewGroup vg){
		
	}

	/**
	 * 滑向第0屏时回调
	 */
	public void onSnapToNavigation(){
		
	}
	
	/**
	 * 从第0屏滑向Workspace时回调
	 */
	public void onSnapToWorkspace(){
		
	}
	
	/**
	 * 隐藏菜单栏
	 */
	public void dismissBottomMenu(){
		
	}
	
	/**
	 * 是否打开文件夹
	 * @return
	 */
	public boolean isFolderOpened() {
		return false;
	}
	
	public BaseDeleteZoneTextView getDeleteZoneTextView(){
		return null;
	}
	
	public BaseDeleteZoneTextView getUninstallZoneTextView(){
		return null;
	}

	@Override
	public void onClick(View v) {
		
	}
	
	public void closeFolder() {
		
	}

	public BaseDragController createDragController(){
		return null;
	}
	
	@Override
	public boolean onLongClick(View v) {
		return false;
	}
	
	//=========================以下接口用于当app安装/卸载/桌面加载时，重新绑定小部件、匣子内app=========================//
	public void updatePandaWidget(String packageName){
		
	}
	
	public void removePandaWidget(String packageName){
		
	}
	
	public void bindAllAppsForDrawer(List<ApplicationInfo> apps){
		
	}
	
	public void addNewInstallApps(List<ApplicationInfo> apps, String packageName){
		
	}
	
	public void updateAppsForDrawer(List<ApplicationInfo> apps, String packageName){
		
	}
	
	public void removeAppForDrawer(String packageName){
		
	}
	
	/**
	 * 创建小部件View
	 * @param itemInfo
	 * @return
	 */
	public View createAppWidgetView(ItemInfo itemInfo){
		return null;
	}
	
}
