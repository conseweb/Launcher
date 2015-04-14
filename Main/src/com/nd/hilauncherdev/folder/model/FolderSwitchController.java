package com.nd.hilauncherdev.folder.model;

import android.content.Context;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.nd.hilauncherdev.folder.model.AbstractFolderSwitchController;
import com.nd.hilauncherdev.folder.model.IFolderHelper;
import com.nd.hilauncherdev.folder.model.stylehelper.AbstractFolderStyleHelper;
import com.nd.hilauncherdev.folder.view.FolderView;
import com.nd.hilauncherdev.framework.OnKeyDownListenner;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonLightbar;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.launcher.DragController;
import com.nd.hilauncherdev.launcher.Launcher;
import com.nd.hilauncherdev.launcher.LauncherSettings;
import com.nd.hilauncherdev.launcher.Workspace;
import com.nd.hilauncherdev.launcher.config.preference.BaseSettingsPreference;
import com.nd.hilauncherdev.launcher.info.FolderInfo;
import com.nd.hilauncherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.simon.android.pandahome2.R;

/**
 * <p>文件夹逻辑控制器<p>
 * <p>负责文件夹各项操作的分发:打开、关闭、批量添加、重命名、加密、排序...<p>
 * @author pdw
 * @date 2012-5-23 下午02:51:08 <br>
 *       
 */
public class FolderSwitchController extends AbstractFolderSwitchController implements OnKeyDownListenner {
	
	/**
	 *  从匣子打开文件夹
	 */
	public final static int OPEN_FOLDER_FROM_DRAWER = 2;
	/**
	 * 重置文件夹拖拽
	 */
	public final static int FOLDER_DRAG_TYPE_RESET = 0 ;
	/**
	 * fling 向外重复拖拽
	 */
	public final static int FOLDER_DRAG_TYPE_FLING = 1 ;
	/**
	 * 向外单次拖拽,文件夹自动关闭
	 */
	public final static int FOLDER_DRAG_TYPE_DRAG_OUT = 2 ;
	/**
	 * 向文件夹批量添加app
	 */
	public final static int FOLDER_ADD_APPS_BATCH = 3 ;
	/**
	 * 拖拽文件内容使文件夹消失的时间间隔,500毫秒
	 */
	public final static long CLOSE_FOLDER_DURATION = 700 ;
	
	/**
	 * 文件夹布局的最大行数
	 */
	public static int MAX_ROW = 3 ;
	
	/**
	 * 文件夹布局的最大列数
	 */
	public static int MAX_COL = 4 ;

	private Launcher mLauncher;
	
	private DragController mDragController;
		
	/**
	 *  底层文件夹布局视图
	 */
	private FolderView mFolderContentLayout;
	
	/**
	 *  点击的文件夹视图（如FolderIconTextView）
	 */
	View mClickedView;
		
	/**
	 * 负责文件夹具体逻辑的帮助接口
	 */
	private IFolderHelper mFolderHelper;

	private boolean mIsFullScreen = false;
	
	private boolean mIsEditMode = false ;
	
	int mOpenFolderFrom ;
	
	/**
	 * 关闭文件夹的动画时长
	 */
	private int mCloseDuration = 400 ;
	
	private long mLastClostTime = 0 ;
	
	/**
	 * 是否添加应用到文件夹
	 */
	boolean mIsAddApp2Folder = false ;
	
	/**
	 * 批量添加按钮
	 */
//	private TextView mAddMore ;
	
	/**
	 * 加密按钮
	 */
//	private TextView mEncript ;
	
	/**
	 * 具体文件夹逻辑帮助类生产工厂
	 */
	private FolderHelperFactory mFolderHelperFactory ;
	
	/**
	 * 指示灯
	 */
	private CommonLightbar mCommonLightbar ;
	
	int mFolderContentViewHeight;
	
	/**
	 * 文件夹打开风格
	 */
	private int mFolderStyle = FolderIconTextView.FOLDER_STYLE_FULL_SCREEN;
		
	private AbstractFolderStyleHelper mFolderStyleHelper;
	
	public FolderSwitchController(Launcher mLauncher) {
		if (ScreenUtil.isLowScreen())
			MAX_ROW = 2 ;
		mFolderHelperFactory = new FolderHelperFactory(mLauncher);
		this.mLauncher = mLauncher;
		WindowManager.LayoutParams attrs = mLauncher.getWindow().getAttributes();
		if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) // 全屏
				== WindowManager.LayoutParams.FLAG_FULLSCREEN) {
			mIsFullScreen = true;
		}
		
		initFolderStyle();
	}
	
	public FolderHelperFactory getFolderHelperFactory() {
		return mFolderHelperFactory;
	}
	
	/**
	 * 设置点击的文件夹view,点击文件夹，{@link #openFolder()}方法之前调用 <br>
	 * 
	 * @param clickedView 被点击的文件夹视图
	 */
	public void setClickFolder(View clickedView, FolderInfo folderInfo) {
		setClickFolder(clickedView, folderInfo, -1);
	}
	
	/**
	 * 设置点击的文件夹view,点击文件夹，{@link #openFolder()}方法之前调用
	 * @param clickedView 被点击的文件夹视图
	 * @param folderInfo 文件夹的数据结构
	 * @param focusIndex 文件夹中需高亮的app索引
	 */
	public void setClickFolder(View clickedView, FolderInfo folderInfo ,int focusIndex) {
		mClickedView = clickedView;
		mFolderInfo = folderInfo;
		mFolderContentLayout.bind(folderInfo, focusIndex);
		setSomeButtonVisibility();
	}

	private void setSomeButtonVisibility() {
		if (
//				(mFolderInfo.getProxyView() != null && mFolderInfo.getProxyView().getTag() instanceof AnythingInfo) ||
				mFolderInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_THEME_APP_FOLDER
			|| mFolderInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_LAUNCHER_RECOMMEND_FOLDER){
//			mAddMore.setVisibility(View.GONE);
//			mEncript.setVisibility(View.GONE);
		} else {
			if(mFolderStyle == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
				mFolderContentLayout.setMaxCols(3);
//				mAddMore.setVisibility(View.GONE);
			}else{
				mFolderContentLayout.setMaxCols(MAX_COL);
//				mAddMore.setVisibility(View.VISIBLE);
			}
//			mEncript.setVisibility(View.VISIBLE);
		}
		if (mFolderContentLayout.shouldShowLightbar()) {
			mCommonLightbar.setVisibility(View.VISIBLE);
		} else {
			mCommonLightbar.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置拖拽控制器 <br>
	 * create at 2012-5-25 上午10:59:43 <br>
	 * modify at 2012-5-25 上午10:59:43
	 * 
	 * @param mDragController
	 */
	public void setDragController(DragController mDragController) {
		this.mDragController = mDragController;
		mFolderContentLayout.setDragController(mDragController);
	}

	/**
	 * 打开文件夹层 <br>
	 * create at 2012-5-24 下午06:05:04 <br>
	 * modify at 2012-5-24 下午06:05:04
	 * 
	 * @param openScr
	 *            从哪打开 {@link OPEN_FOLDER_FROM_LAUNCHER},
	 *            {@link OPEN_FOLDER_FROM_DRAWER}
	 */
	public void openFolder(int openScr) {
		mFolderContentLayout.setupDragController();
		initFolderOpen(openScr);
		
		if (mFolderStyleHelper != null) {
			mFolderStyleHelper.onOpen();
		}
	}
	
	/**
	 * <p>打开桌面文件夹</p>
	 * 
	 * <p>date: 2012-8-14 下午02:47:39
	 */
	public void openLauncherFolder() {
		openFolder(FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER);
	}
	
	/**
	 * <p>打开匣子文件夹</p>
	 * 
	 * <p>date: 2012-8-14 下午02:48:49
	 * @param isEditMode 是否编辑模式
	 */
	public void openDrawerFolder(boolean isEditMode) {
		openFolder(OPEN_FOLDER_FROM_DRAWER, isEditMode);
	}
	
	/**
	 * 打开文件夹层 <br>
	 * create at 2012-5-24 下午06:05:04 <br>
	 * modify at 2012-5-24 下午06:05:04
	 * 
	 * @param openScr
	 *            从哪打开 {@link OPEN_FOLDER_FROM_LAUNCHER},
	 *            {@link OPEN_FOLDER_FROM_DRAWER}
	 * @param isEditMode 编辑模式
	 */
	public void openFolder(int openScr, boolean isEditMode) {
		mOpenFolderFrom = openScr;
		setEditMode(isEditMode);
		openFolder(openScr);		
	}

	/**
	 * <p>初始化具体的文件夹逻辑帮助类</p>
	 * 
	 * <p>date: 2012-8-14 下午02:46:06
	 * @param openSrc
	 */
	private void initFolderOpen(int openSrc) {
		mFolderHelper = mFolderHelperFactory.getFolderHelper(openSrc);
		switch (openSrc) {
		case FolderSwitchController.OPEN_FOLDER_FROM_DRAWER:
//			mFolderContentLayout.setCallback((DrawerMainView)mLauncher.getDrawer());
//			mFolderContentLayout.setDragOutCallback((OnFolderDragOutCallback) mLauncher.getDrawer());
			break;
		case FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER:
			mIsEditMode = false ;
			mFolderContentLayout.setDragOutCallback((Workspace)mLauncher.getScreenViewGroup());
			break;
		}
		mOpenFolderFrom = openSrc ;
		mFolderHelper.setClickView(mClickedView);
		mFolderHelper.setIsFullScreen(mIsFullScreen);
	}

	/**
	 * 正常关闭文件夹 <br>
	 * 
	 * create at 2012-5-25 上午11:00:27 <br>
	 * modify at 2012-5-25 上午11:00:27
	 */
	public boolean closeFolder() {
		mIsAddApp2Folder = false ;
		return close();
	}
	
	/**
	 * <p>关闭文件夹，没有动画效果</p>
	 * 
	 * <p>date: 2012-8-8 下午04:50:28
	 * @param isAddApp2Folder 是否使用文件夹中的批量添加
	 * @return
	 */
	public void closeFolderWithoutAnimation(boolean isAddApp2Folder) {
		mIsAddApp2Folder = isAddApp2Folder ;
		
		//控制调用的时间频率
		if(Math.abs(System.currentTimeMillis() - mLastClostTime) < mCloseDuration)
			return ;
		
		mLastClostTime = System.currentTimeMillis();
		if (mFolderView != null && mFolderView.getParent() != null
				&& mFolderView.getVisibility() == View.VISIBLE) {
			hindSoftInput();
			mFolderContentLayout.onClose();
			mFolderHelper.onFolderClose(mFolderInfo,mIsAddApp2Folder);
			
			if (mFolderStyleHelper != null) {
				mFolderStyleHelper.onCloseWithoutAnimation();
			}

			//清除文件夹中多选项列表
			mFolderContentLayout.getFolderSlidingView().clearDraggerChooseList();
		}
	}
	
	/**
	 * 关闭文件夹
	 * <br>
	 * @see {@link #closeFolder()} 
	 * <br>创建于 2012-7-11 上午10:48:12
	 * @param isAddApp2Folder 是否是由添加app到文件夹中引起的文件夹关闭
	 */
	public void closeFolder(boolean isAddApp2Folder) {
		mIsAddApp2Folder = isAddApp2Folder ;
		close();
	}
	
	private boolean close(){
		if(Math.abs(System.currentTimeMillis() - mLastClostTime) < mCloseDuration)
			return false;
		mLastClostTime = System.currentTimeMillis();
		if (mFolderView != null && mFolderView.getParent() != null
				&& mFolderView.getVisibility() == View.VISIBLE) {
			hindSoftInput();
			mFolderContentLayout.onClose();
			
			if (mFolderStyleHelper != null) {
				mFolderStyleHelper.onClose();
			}
			
			/**
			 * 清除文件夹中多选项列表
			 */
			mFolderContentLayout.getFolderSlidingView().clearDraggerChooseList();
			//mLauncher.trackLauncherCurOperSrcAct(ReadMeStateManager.OPER_SRC_ACT_CLOSE_FOLDER);
			return true;
		}
		return false;
	}

	/**
	 * 隐藏软键盘
	 */
	private void hindSoftInput() {
		IBinder binder = mFolderView.getWindowToken();
		((InputMethodManager) mLauncher
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(binder,
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public boolean onKeyDownProcess(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mOpenFolderFrom == FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER) {
			return closeFolder();
		}
		return false;
	}
	/**
	 * 设置通用滑屏组件的编辑模式
	 * <br>create at 2012-6-15 下午07:59:31
	 * <br>modify at 2012-6-15 下午07:59:31
	 * @param isEditMode
	 */
	public void setEditMode(boolean isEditMode) {
		if(mOpenFolderFrom == OPEN_FOLDER_FROM_DRAWER){
			mIsEditMode = isEditMode ;
			mFolderContentLayout.setEditMode(isEditMode);
		}
	}

	/**
	 * @return the mIsEditMode
	 */
	public boolean isEditMode() {
		return mIsEditMode;
	}

	/**
	 * @return 从哪打开<br>
	 * {@link #OPEN_FOLDER_FROM_DRAWER} 从匣子打开<br>
	 * {@link #OPEN_FOLDER_FROM_LAUNCHER} 从桌面打开
	 */
	public int getOpenFolderFrom() {
		return mOpenFolderFrom;
	}

	private void setTitle(String name) {
		mFolderContentLayout.setTitle(name);
	}
	
	public FolderView getFolderView() {
		return mFolderContentLayout ;
	}
	
	/**
	 * 重命名文件名
	 *  
	 * <br>create at 2012-7-10 下午03:49:40
	 * <br>modify at 2012-7-10 下午03:49:40
	 * @param id
	 * @param name
	 */
	public void renameFolder(String name) {
		setTitle(name);
		mFolderHelper.renameFolder(mFolderInfo,name);
	}

	/**
	 * <p>打开加密文件夹回调</p>
	 */
	public void handleFolderClickDirectly(int type) {
		initFolderOpen(type);
		mFolderHelper.openFolderCallback(500);
	}

	/**
	 * <p>删除加密文件夹回调</p>
	 */
	public void handleFolderDeleteDirectly(int type) {
		initFolderOpen(type);
		mFolderHelper.deleteFolderCallback();
	}

	/**
	 * <p>加密文件夹成功后回调</p>
	 */
	public void handlerEncriptyFolder(int type) {
		initFolderOpen(type);
		mFolderHelper.encriptFolderCallback();
	}
	
	public int getFolderStyle() {
		return mFolderStyle;
	}
		
	public void removeCloseFolderRunnable() {
    	mFolderContentLayout.removeCloseFolderRunnable();
    }
	
	/**
	 * Description: GPU开启时，匣子中cellLayout需更新硬件层离屏缓存
	 * Author: guojy
	 * Date: 2012-11-26 上午11:11:44
	 */
	void destroySlidingViewHardwareLayer(){
//		if(mLauncher.getDrawer() != null && mLauncher.getDrawer().isVisible()){
//			((DrawerMainView)mLauncher.getDrawer()).destroySlidingViewHardwareLayer();
//		}
	}
	
	/**
	 * 初始化文件夹内容
	 * @return 文件夹视图的高宽
	 */
	int initFolderContents() {
		if (mClickedView == null)
			return 300;

		final FolderInfo userInfo = mFolderInfo;
		if (userInfo == null)
			return 300;

		int iconSize = mLauncher.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		int cellHeiht = iconSize * 2 ;
		
		if(mFolderStyle == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			mFolderContentLayout.setMaxCols(3);
		}else{
			mFolderContentLayout.setMaxCols(MAX_COL);
		}
		
		mFolderContentLayout.showFolderApps(userInfo.contents);
		mFolderContentLayout.setChildViewHeight(cellHeiht);
		/**
		 * (52 , 5) 代表 (文件夹数据框、指示灯的高度,用于横屏视图用于padding计算的高度)
		 */
		int extraHeight = 52 + 5 ;
		if (mFolderStyle != FolderIconTextView.FOLDER_STYLE_FULL_SCREEN && !mFolderContentLayout.shouldShowLightbar()) {
			extraHeight = extraHeight - 20 - 5; //减掉指示灯的高度,用于横屏视图padding计算的高度
		}
		final int clipHeight = mFolderContentLayout.getRowNum() * cellHeiht + (int) (extraHeight * ScreenUtil.getDensity());
		mFolderContentViewHeight = clipHeight;
		return clipHeight;
	}

	/**
	 * 初始化与文件夹样式相关的内容
	 */
	private void initFolderStyle() {
		mFolderStyle = BaseSettingsPreference.getInstance().getFolderStyle();
//		if (mFolderStyle == FolderIconTextView.FOLDER_STYLE_IPHONE) {
//			mFolderStyleHelper = new IPhoneFolderStyleHelper(mLauncher, this);
//		} else if(mFolderStyle == FolderIconTextView.FOLDER_STYLE_ANDROID_4){
//			mFolderStyleHelper = new AndroidFolderStyleHelper(mLauncher, this);
//		} else {
			mFolderStyleHelper = new FullScreenFolderStyleHelper(mLauncher, this);
//		}
		
		mFolderView = mFolderStyleHelper.getFolderView();
//		mAddMore = (TextView) mFolderView.findViewById(R.id.add_more);
//		mEncript = (TextView) mFolderView.findViewById(R.id.folder_encript);
		mCommonLightbar = (CommonLightbar) mFolderView.findViewById(R.id.light_bar);
		
		mFolderContentLayout = (FolderView) mFolderView.findViewById(R.id.folder_layout);
		mFolderContentLayout.setVisibility(View.GONE);
		mFolderContentLayout.setLauncher(mLauncher);
		if (mDragController != null) {
			setDragController(mDragController);
		}
		mFolderHelperFactory.init(mLauncher, mFolderStyle);
	}
	
	@Override
	protected AbstractFolderStyleHelper getFolderStyleHelper() {
		return mFolderStyleHelper;
	}

	@Override
	protected IFolderHelper getFolderHelper() {
		return mFolderHelper;
	}

	@Override
	public void handleStyleChanged() {
		initFolderStyle();
	}
}
