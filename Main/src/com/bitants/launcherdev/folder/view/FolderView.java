package com.bitants.launcherdev.folder.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.framework.view.draggersliding.datamodel.DraggerSlidingViewData;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.app.SerializableAppInfo;
import com.bitants.launcherdev.folder.model.FolderSwitchController;
import com.bitants.launcherdev.framework.AnyCallbacks.CommonSlidingViewCallback;
import com.bitants.launcherdev.framework.AnyCallbacks.OnFolderDragOutCallback;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLightbar;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewClickListener;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewLongClickListener;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.framework.view.draggersliding.datamodel.DraggerSlidingViewData;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.touch.DragSource;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.bitants.launcherdev.util.ActivityActionUtil;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerChooseItem;
import com.bitants.launcherdev.framework.view.draggersliding.datamodel.DraggerSlidingViewData;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.DragController;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.info.FolderInfo;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.util.ActivityActionUtil;

/**
 * 文件夹显示视图，包括文件夹名字输入框，添加app按钮，横向滑屏的app列表
 * 
 * @author pdw
 * @date 2012-5-24 下午04:39:43 
 **/
public class FolderView extends RelativeLayout implements
		OnCommonSlidingViewClickListener, OnCommonSlidingViewLongClickListener,
        DropTarget {

	private TextView mFolderName;
	// 指示灯
	private CommonLightbar mLightbar;
	// 横向滑屏app列表
	private FolderSlidingView mFolderGirdView;
	// 打开文件夹共享，指示灯图片
	private static Drawable mLightChecked;
	private static Drawable mLightNormal;
	// 最大列数
	private int mMaxCols = FolderSwitchController.MAX_COL ;
	// 最大行数
	private int mMaxRows = FolderSwitchController.MAX_ROW ;
	
	private int mIconSize ;
	// 文件夹内的应用数据集
	private DraggerSlidingViewData mFolderData ;
	// 文件夹信息
	private FolderInfo mFolderInfo;

	private Launcher mLauncher;
	
//	private TextView mAddMore ;
	
//	private TextView mEncript ;
	
	private TextClickListener mListener = new TextClickListener();
	
	private List<SerializableAppInfo> mTempSerAppsList = new ArrayList<SerializableAppInfo>();
	
	private int mDoubleTapTimeout ;
	
	//target状态
	private int state ;
	
	private DragController mDragController;
	
	private boolean isSetup = false;
	
	private View clickView , titleLayout;
	
	/**
	 * 是否可点击，用于在文件夹打开动画未完成前屏蔽单击事件
	 */
	private boolean isClickable = true;

	public FolderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = context.getResources();
		mLightChecked = res.getDrawable(R.drawable.spring_lightbar_checked);
		mLightNormal = res.getDrawable(R.drawable.spring_lightbar_normal);
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			mMaxCols = 3;
		}else{
			mMaxCols = 4;
		}
		mIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		if (ScreenUtil.isLowScreen()) {
			mMaxRows = 2 ;
		}
		mFolderData = new DraggerSlidingViewData(
				(int) (mIconSize * 1.68f), mIconSize * 2, mMaxCols, mMaxRows, new ArrayList<ICommonDataItem>());
		mFolderData.setAcceptDrop(true);
		mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
	}

	@Override
	protected void onFinishInflate() {
		titleLayout = findViewById(R.id.title_layout);
		mFolderName = (TextView) findViewById(R.id.folder_name);
		mLightbar = (CommonLightbar) findViewById(R.id.light_bar);
		mFolderGirdView = (FolderSlidingView) findViewById(R.id.folder_scroll_view);
//		mAddMore = (TextView) findViewById(R.id.add_more);
//		mEncript = (TextView) findViewById(R.id.folder_encript);
		mFolderGirdView.setOnItemClickListener(this);
		mFolderGirdView.setOnItemLongClickListener(this);
		mLightbar.setNormalLighter(mLightNormal);
		mLightbar.setSelectedLighter(mLightChecked);
		mFolderGirdView.setCommonLightbar(mLightbar);
		
		/** 设置点击监听器 **/
//		mAddMore.setOnClickListener(mListener);
		mFolderName.setOnClickListener(mListener);
//		mEncript.setOnClickListener(mListener);
//		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
//			mAddMore.setVisibility(View.GONE);
//		}
	}
	
	class TextClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			/**
			 * 【最近安装】，【最近打开】不支持如下操作
			 */
			if (isDynamic(mFolderInfo))
				return ;
			
//			Intent intent = null;
//			int analysisId = AnalyticsConstant.INVALID ;
			
			switch (v.getId()) {
//			/**
//			 * 批量添加
//			 */
//			case R.id.add_more:
//				if (mLauncher.getFolderCotroller().getOpenFolderFrom() == FolderSwitchController.OPEN_FOLDER_FROM_DRAWER &&
//						!ConfigFactory.isInitApps(mContext)) {
//					MessageUtils.makeShortToast(mContext, R.string.drawer_apps_not_init_tips);
//					return;
//				}
////				analysisId = AnalyticsConstant.FOLDER_ICON_MORE ;
//				mAddMore.setClickable(false);
//				getHandler().postDelayed(new EnableClickRunnable(mAddMore), mDoubleTapTimeout);
//				
//				mFolderGirdView.clearDraggerChooseList();
//				
//				intent = createAddMoreIntent();
//				SystemUtil.startActivityForResultSafely(mLauncher, intent,
//						LauncherActivityResultHelper.REQUEST_FOLDER_ADD_MORE);
//				break;
			
			/**
			 * 文件夹重命名
			 */
			case R.id.folder_name:
//				analysisId = AnalyticsConstant.FOLDER_RENAME ;
				mFolderName.setClickable(false);
				getHandler().postDelayed(new EnableClickRunnable(mFolderName), mDoubleTapTimeout);
//				if(SettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
					rename();
//				}else{
//					intent = new Intent(mContext, FolderRenameActivity.class);
//					intent.putExtra("name", mFolderInfo.title);
//					intent.putExtra("id", mFolderInfo.id);
//					SystemUtil.startActivityForResultSafely(mLauncher, intent,
//							LauncherActivityResultHelper.REQUEST_FOLDER_RENAME);
//					mLauncher.overridePendingTransition(R.anim.zoom_enter_activity,
//							0);
//				}
				break;
			
			/**
			 * 文件夹加密
			 */
//			case R.id.folder_encript:
////				analysisId = AnalyticsConstant.FOLDER_ENCRYPT ;
//				mEncript.setClickable(false);
//				getHandler().postDelayed(new EnableClickRunnable(mEncript), mDoubleTapTimeout);
//				
//				intent = new Intent(mContext,FolderEncriptTypeChooseActivity.class);
//				intent.putExtra("id", mFolderInfo.id);
//				intent.putExtra("type", mLauncher.getFolderCotroller().getOpenFolderFrom());
//				intent.putExtra("name", mFolderInfo.title);
//				SystemUtil.startActivityForResultSafely(mLauncher, intent, 
//						LauncherActivityResultHelper.REQUEST_FOLDER_ENCRIPT_CHOOSE);
//				break ;
			default:
				break;
			}
			/**
			 * 行为统计
			 */
//			if (analysisId != AnalyticsConstant.INVALID)
//				HiAnalytics.submitEvent(mContext, analysisId);
		}

	}
	
	/**
	 * 防止用户多次单击重命名，批量添加按钮
	 * @author pdw
	 */
	private class EnableClickRunnable implements Runnable{
		
		private View mView ;
		
		public EnableClickRunnable(View mView){
			this.mView = mView ;
		}

		@Override
		public void run() {
			mView.setClickable(true);
		}
	}
	
//	private Intent createAddMoreIntent(){
//		int openSrc = mLauncher.getFolderCotroller().getOpenFolderFrom();
//		Intent intent = new Intent(mContext, AppChooseDialogActivity.class);
//		final List<SerializableAppInfo> selectedAppsInfoList = mTempSerAppsList ;
//		selectedAppsInfoList.clear();
//		
//		for(ApplicationInfo app : mFolderInfo.contents){
//			if (openSrc == FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER &&
//					app.itemType != Favorites.ITEM_TYPE_APPLICATION) //过滤掉文件夹中的快捷方式
//				continue ;
//			selectedAppsInfoList.add(app.makeSerializable());
//		}
//		
//		/**
//		 * 如果是匣子得过滤掉其他文件夹的app
//		 */
//		if(openSrc == FolderSwitchController.OPEN_FOLDER_FROM_DRAWER) {
//			List<SerializableAppInfo> filterList = new ArrayList<SerializableAppInfo>() ;
//			DrawerDataFactory.loadAppsInOtherFoldersOrIsHidden(mContext,mFolderInfo,filterList);
//			intent.putExtra(AppChooseDialogActivity.FILTER_LIST, (Serializable) filterList);
//		}
//		intent.putExtra(AppChooseDialogActivity.TITLE, mFolderInfo.title);
//		intent.putExtra(AppChooseDialogActivity.SELECTED_LIST, (Serializable) selectedAppsInfoList);
//		return intent ;
//	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	/**
	 * 隐藏指示灯 <br>
	 * create at 2012-5-24 下午05:09:30 <br>
	 * modify at 2012-5-24 下午05:09:30
	 */
	public void invisibleLightbar() {
		mLightbar.setVisibility(GONE);
	}

	/**
	 * 显示指示灯 <br>
	 * create at 2012-5-24 下午05:09:58 <br>
	 * modify at 2012-5-24 下午05:09:58
	 */
	public void visibleLightbar() {
		mLightbar.setVisibility(VISIBLE);
	}

	/**
	 * 显示文件夹程序图标 <br>
	 * create at 2012-5-24 下午05:11:38 <br>
	 * modify at 2012-5-24 下午05:11:38
	 */
	public void showFolderApps(List<ApplicationInfo> apps) {
		int size = apps.size();
		List<ICommonDataItem> items = mFolderData.getDataList();
		items.clear();
		items.addAll(apps);
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN)
		{	
			titleLayout.setVisibility(View.INVISIBLE);
			findViewById(R.id.line_layout).setVisibility(View.INVISIBLE);
//			if(!isDynamic(mFolderInfo)){
//				size += 1;
//				items.add(mFolderGirdView.getAddAppInfo());
//			}
		}
		mFolderData.setRowNum(getRows(size));
		mFolderData.setColumnNum(getMaxCols());
		ArrayList<ICommonData> datas = new ArrayList<ICommonData>();
		datas.add(mFolderData);
		mFolderGirdView.go2FirstScreen();
		mLightbar.update(0);
		mFolderGirdView.setList(datas);
	}

	public int getRowNum() {
		return mFolderData.getRowNum();
	}

	/**
	 * 设置行数 <br>
	 * create at 2012-5-24 下午05:15:24 <br>
	 * modify at 2012-5-24 下午05:15:24
	 * 
	 * @param row
	 */
	void setRowNum(int row) {
		mFolderData.setRowNum(row);
	}

	public void setChildViewHeight(int height) {
		mFolderData.setChildViewHeight(height);
	}

	/**
	 * 获取行数 <br>
	 * create at 2012-5-24 下午05:21:40 <br>
	 * modify at 2012-5-24 下午05:21:40
	 * 
	 * @param size
	 * @return
	 */
	private int getRows(int size) {
		int row = size / mMaxCols;
		if (size % mMaxCols != 0) {
			row++;
		}
		if (row > mMaxRows) {
			row = mMaxRows;
		}
		return row;
	}

	@Override
	public boolean onItemLongClick(View v, int positionInData,
			int positionInScreen, int screen, ICommonData data) {
		
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			if(mFolderGirdView.getAddView() == v){
//				mAddMore.performClick();
				return true;
			}
			if(!isDynamic(mFolderInfo) && mFolderGirdView.getAddView() != null){
				CommonLayout layout = mFolderGirdView.getCommonLayout(mFolderGirdView.getPage() - 1);
				layout.removeView(mFolderGirdView.getAddView());
			}
		}
		
		//编辑被锁定文件夹中的任何长按事件都不响应
		if (!BaseConfig.allowEdit(getContext()) || !mLauncher.isFolderOpened()) {
			return true;
		}
		CommonViewHolder holder = (CommonViewHolder) v
				.getTag(R.id.common_view_holder);
		if (mCallbacks != null)
			mCallbacks.onViewLongClick(v);
//		v.findViewById(R.id.animation_layout).clearAnimation();
		
		if(mLauncher.getFolderCotroller().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			mFolderGirdView.settleBackground();
		}
		
		if(isDynamic(mFolderInfo)){
			mLauncher.closeFolder();
			//modify by linqiang 桌面动态文件夹内图标长按后不显示垃圾桶,拖拽出来后显示垃圾桶
			/*if (mLauncher.getFolderCotroller().getOpenFolderFrom() == FolderSwitchController.OPEN_FOLDER_FROM_LAUNCHER){
				
			}*/
		}else{
			if(mLauncher.getFolderCotroller().getOpenFolderFrom() == FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER){
				if(holder.item instanceof ApplicationInfo){
//					ApplicationInfo info = (ApplicationInfo)holder.item;
					clickView = v;
//					mDragController.setShowShortcutMenu(true);
//					mDragController.setMenuTriggerSource(this);
				}
			}
		}
		
		/**
		 * Drop后是否已完成重新布局 且 DragView是否已完成动画
		 */
		if (mFolderGirdView.isNeedRelayoutAfterDrop() || mFolderGirdView.isInAnimation()) 
			return true;
		
		ApplicationInfo app = (ApplicationInfo) holder.item;
		if (mFolderGirdView.getDraggerChooseItem(app) != null) {
			/**
			 * 拖拽多项
			 */
			ArrayList<DraggerChooseItem> list = mFolderGirdView.getDraggerChooseList();
			for (DraggerChooseItem cItem : list) {
				ApplicationInfo aInfo = cItem.getInfo();
				if (aInfo == app) {
					list.remove(cItem);
					list.add(0, cItem);
					break;
				}
			}
			mFolderGirdView.startDrag(v, positionInData, positionInScreen, holder.item, list);
		} else {
			/**
			 * 拖拽单项
			 */
			mFolderGirdView.startDrag(v, positionInData, positionInScreen, holder.item);
		}
		
		return true;
	}

	@Override
	public void onItemClick(View v, int positionInData, int positionInScreen,
			int screen, ICommonData data) {
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			if(mFolderGirdView.getAddView() == v){
//				mAddMore.performClick();
				return;
			}
		}
		
		if (!isClickable) {
			return;
		}
		
		CommonViewHolder holder = (CommonViewHolder) v.getTag(R.id.common_view_holder);
		ApplicationInfo info = (ApplicationInfo) holder.item;
		
		//编辑模式下不允许点击app
//		if (mFolderGirdView.isEditMode()) {
//			/**
//			 * 选择程序
//			 */
//			boolean isSuccess = mFolderGirdView.chooseItem(data, (FolderBoxedViewGroup) v, info);
//			if (!isSuccess) {
//				/**
//				 * 多选项个数已达到最大限制
//				 */
//				MessageUtils.makeShortToast(mContext, R.string.drawer_multi_choose_reached_limit_tips);
//			}
//			return ;
//		}
		
		
		mLauncher.getFolderCotroller().closeFolderWithoutAnimation(false);
		
//		if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT
//				|| info.itemType == Favorites.ITEM_TYPE_INDEPENDENCE) {
//			CustomIntentSwitcherController customIntentSwitcherController = CustomIntentSwitcherController.getNewInstance();
//			customIntentSwitcherController.registerCustomIntent(new AppInfoIntentCommandAdapter(info));
//			customIntentSwitcherController.onAction(mLauncher,info, IntentCommand.ACTION_FROM_UNKNOW);
//		} else {
			ActivityActionUtil.startActivitySafelyForRecored(mLauncher, info.intent);
//		}
		
		if (mCallbacks != null)
			mCallbacks.onViewClick(v);
	}
	
	/**
	 * @param mLauncher
	 *            the mLauncher to set
	 */
	public void setLauncher(Launcher mLauncher) {
		this.mLauncher = mLauncher;
		mFolderGirdView.setLauncher(mLauncher);
	}

	/**
	 * @param mDragController
	 *            the mDragController to set
	 */
	public void setDragController(DragController mDragController) {
		this.mDragController = mDragController;
		mFolderGirdView.setDragController(mDragController);
	}
	
	public void setupDragController() {
		if (!isSetup) {
			this.mDragController.addDragScoller(mFolderGirdView);			
			// 设置droptarget
			this.mDragController.addDropTarget(this);
			this.mDragController.addDropTarget(mFolderGirdView);
			isSetup = true;
		}
	}

	/**
	 * @return the mMaxCols
	 */
	public int getMaxCols() {
		return mMaxCols;
	}
	
	/**
	 * @return the mMaxRows
	 */
	public int getMaxRows() {
		return mMaxRows;
	}

	/**
	 * 绑定文件夹信息 <br>
	 * create at 2012-5-29 上午11:09:18 <br>
	 * modify at 2012-5-29 上午11:09:18
	 * 
	 * @param folderInfo
	 * @param focusIndex 文件夹中需被高亮的app索引
	 */
	public void bind(FolderInfo folderInfo,int focusIndex) {
		this.mFolderInfo = folderInfo;
		mFolderGirdView.bind(folderInfo,focusIndex);
		mFolderName.setText(folderInfo.title);
	}

	public void onClose() {
		if (mFolderInfo != null)
			mFolderInfo.opened = false;
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			if(mFolderGirdView.getAddView() != null){
				mFolderGirdView.getAddView().setVisibility(View.GONE);
			}
			titleLayout.setVisibility(View.INVISIBLE);
			findViewById(R.id.line_layout).setVisibility(View.INVISIBLE);
			findViewById(R.id.folder_rename_ok).setVisibility(View.GONE);
			findViewById(R.id.edit_folder_name).setVisibility(View.GONE);
//			if (!isDynamic(mFolderInfo) 
//					&& mFolderInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_THEME_APP_FOLDER
//					&& mFolderInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_LAUNCHER_RECOMMEND_FOLDER){
//				mEncript.setVisibility(View.VISIBLE);
//			}
			mFolderName.setVisibility(View.VISIBLE);
			((InputMethodManager) mLauncher
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(mFolderName.getWindowToken(),0);
		}
	}
	
	@Override
	public int getState() {
		return state;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {

	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		mFolderGirdView.onDragEnter(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		mFolderGirdView.onDragExit(source, x, y, xOffset, yOffset, dragView, dragInfo);
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		return false;
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			setState(AVAIABLE);
		} else {
			setState(UNAVAIABLE);
		}
		mFolderGirdView.setVisibility(visibility);
	}

	private CommonSlidingViewCallback mCallbacks;

	public void setCallback(CommonSlidingViewCallback callback) {
		mCallbacks = callback;
	}

	/**
	 * @return the mIsEditMode
	 */
	public boolean isEditMode() {
		return mFolderGirdView.isEditMode();
	}

	/**
	 * @param mIsEditMode
	 *            the mIsEditMode to set
	 */
	public void setEditMode(boolean mIsEditMode) {
		mFolderGirdView.setIsEditMode(mIsEditMode);
	}
	
	public void setDragOutCallback(OnFolderDragOutCallback callback){
		mFolderGirdView.setDragoutCallback(callback);
	}

	public void setTitle(String name) {
		mFolderName.setText(name);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/**
		 * 拦截掉文件夹内没有view响应的触屏事件，防止误操作关闭文件夹
		 */
		return true ;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state ;
	}
	
	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}
	
	public FolderInfo getUserFolderInfo(){
		return mFolderInfo;
	}
	
	public FolderSlidingView getFolderSlidingView(){
		return mFolderGirdView;
	}
	
	public void removeCloseFolderRunnable() {
		mFolderGirdView.removeCloseFolderRunnable();
	}
	
	/**
	 * 显示长按菜单
	 * @author Michael
	 * @createtime 2013-7-17
	 */
	public void showShortcutMenu(){
		if(clickView != null){
			mDragController.showShortcutMenu(null, clickView, this);
			clickView = null;
		}
	}

	public void setMaxCols(int maxCols) {
		this.mMaxCols = maxCols;
	}
	
	/**
	 * 刷新视图
	 */
	public void refresh() {
		if (shouldShowLightbar()) {
			visibleLightbar();
		} else {
			invisibleLightbar();
		}
	}
	
	/**
	 * 获取文件夹视图布局页数
	 * @param appCount 文件夹内的应用数
	 * @return 文件夹视图布局页数
	 */
	private int getPageCount(int appCount) {
		if (appCount <= 1)
			return 0 ;
		return 1 + (appCount - 1 ) / (getMaxCols() * getMaxRows()) ;
	}
	
	/**
	 * 是否显示指示器
	 */
	public boolean shouldShowLightbar() {
		if (mFolderInfo == null)
			return false ;
		int size = mFolderInfo.getSize();
		if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_FULL_SCREEN){
			size += 1;
		}
		return getPageCount(size) > 1 ;
	}
	
	private void rename(){
		final TextView save = (TextView) findViewById(R.id.folder_rename_ok);
		final EditText edit = (EditText) findViewById(R.id.edit_folder_name);
		save.setVisibility(View.VISIBLE);
		edit.setVisibility(View.VISIBLE);
//		mEncript.setVisibility(View.GONE);
		mFolderName.setVisibility(View.GONE);
		edit.setText(mFolderInfo.title);
		Editable editable = edit.getText();
		Selection.setSelection(editable, 0,editable.length());
		edit.setFocusable(true);
		edit.requestFocus();
		((InputMethodManager) mLauncher
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.showSoftInput(edit,InputMethodManager.SHOW_FORCED);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((InputMethodManager) mLauncher
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(edit.getWindowToken(),0);
//				if (mLauncher.isFolderOpened()) {
				mLauncher.getFolderCotroller().renameFolder(edit.getText().toString());
//				} else {
//					mLauncher.renameFolder(id, edit.getText().toString());
//				}
				save.setVisibility(View.GONE);
				edit.setVisibility(View.GONE);
//				if (!isDynamic(mFolderInfo) 
//						&& mFolderInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_THEME_APP_FOLDER
//						&& mFolderInfo.itemType != LauncherSettings.Favorites.ITEM_TYPE_LAUNCHER_RECOMMEND_FOLDER){
//					mEncript.setVisibility(View.VISIBLE);
//				}
				mFolderName.setVisibility(View.VISIBLE);
			}
		});
	}
	
//	public boolean isDynamic(FolderInfo folder) {
////		if (folder.getProxyView() != null
////				&& folder.getProxyView().getTag() instanceof AnythingInfo)
////			return true;
//		return false;
//	}
	
	
	public static boolean isDynamic(FolderInfo folder) {
		if (folder == null) {
			return false;
		}
		
//		return (folder.getProxyView() != null && folder.getProxyView().getTag() instanceof AnythingInfo);
		return false;
	}
}
