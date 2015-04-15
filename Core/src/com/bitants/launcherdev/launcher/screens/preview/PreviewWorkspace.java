package com.bitants.launcherdev.launcher.screens.preview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitants.launcherdev.core.view.PreviewImageView;
import com.bitants.launcherdev.framework.ViewFactory;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerLayout;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcher.R;
import com.bitants.launcherdev.core.view.PreviewImageView;
import com.bitants.launcherdev.framework.ViewFactory;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerLayout;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.touch.DropTarget;
import com.bitants.launcherdev.core.view.PreviewImageView;
import com.bitants.launcherdev.framework.ViewFactory;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerLayout;
import com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.touch.DropTarget;

/**
 * <br>Description: 屏幕预览管理视图
 * <br>Author:caizp
 * <br>Date:2012-6-20下午09:12:17
 */
public class PreviewWorkspace extends DraggerSlidingView {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private View mDragView;
	private int mPreviewMode;
	private BaseLauncher mLauncher;
	private PreviewEditAdvancedController mPreviewEditAdvancedController;
	private ArrayList<DropTarget> dropTargetList = new ArrayList<DropTarget>();
	
	private int state = DropTarget.AVAIABLE;
	private int lastDragItemPosition = -1;
	
	/**
	 * 拖动的是否当前屏
	 */
	private boolean isDragCurrentScreen = false;

	public PreviewWorkspace(Context context) {
		this(context, null);
	}
	
	public PreviewWorkspace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PreviewWorkspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		setEndlessScrolling(false);
	}
	
	public void setPreviewMode(int previewMode){
		mPreviewMode = previewMode;
	}
	
	public void setLauncher(BaseLauncher launcher){
		mLauncher = launcher;
	}
	
	public void setPreviewEditAdvancedController(PreviewEditAdvancedController previewEditAdvancedController){
		mPreviewEditAdvancedController = previewEditAdvancedController;
	}

	@Override
	public int getState() {
		return state;
	}
	
	public int setState(int state) {
		return this.state = state;
	}
	
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			setState(DropTarget.AVAIABLE);
		} else {
			setState(DropTarget.UNAVAIABLE);
		}
	}
	
//	@Override
//	protected void dispatchDraw(Canvas canvas) {
//		int sc = -1;
//		if (BaseConfig.iconMask != null && BaseSettingsPreference.getInstance().isIconMaskEnabled())
//			sc = canvas.saveLayer(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), null, Canvas.ALL_SAVE_FLAG);
//		
//		super.dispatchDraw(canvas);
//		
//		if ( sc != -1 && BaseConfig.iconMask != null && BaseSettingsPreference.getInstance().isIconMaskEnabled())
//			canvas.restoreToCount(sc);
//	}

	/**
	 * <br>Description: 交换屏幕后Workspace数据处理
	 * <br>Author:caizp
	 * <br>Date:2012-6-26下午05:57:11
	 * @see com.bitants.launcherdev.framework.view.draggersliding.DraggerSlidingView#onDataChanged(com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData, com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder)
	 */
	@Override
	public void onDataChanged(ICommonData data,	CommonViewHolder originalViewHolder) {
		if(-1 == lastDragItemPosition || lastDragItemPosition == originalViewHolder.positionInData)return;
				
		//移动桌面数据
		mLauncher.getScreenViewGroup().moveItemPositionsOnScreenChange(lastDragItemPosition, originalViewHolder.positionInData);
		
		//移动桌面视图
		mLauncher.getScreenViewGroup().exchangeScreen(lastDragItemPosition, originalViewHolder.positionInData);
		
		mLauncher.getScreenViewGroup().resetCellLayoutStuffOnScreenChange();
		
		//移动的是主屏
		int defaultScreen = mLauncher.getScreenViewGroup().getDefaultScreen();
		if(lastDragItemPosition == defaultScreen){
			setDefaultScreen(originalViewHolder.positionInData);
		}else{
			if ((defaultScreen >= lastDragItemPosition && defaultScreen <= originalViewHolder.positionInData)
					|| (defaultScreen <= lastDragItemPosition && defaultScreen >= originalViewHolder.positionInData)) {
				if (lastDragItemPosition > originalViewHolder.positionInData) {
					setDefaultScreen(mLauncher.getScreenViewGroup().getDefaultScreen() + 1);
				} else if (lastDragItemPosition < originalViewHolder.positionInData) {
					setDefaultScreen(mLauncher.getScreenViewGroup().getDefaultScreen() - 1);
				}
			}
		}
		int currentScreen = mLauncher.getScreenViewGroup().getCurrentScreen();
		//移动的是当前屏
		if(lastDragItemPosition == mLauncher.getScreenViewGroup().getCurrentScreen()){
			//修改Workspace数据
			mLauncher.getScreenViewGroup().setCurrentScreen(originalViewHolder.positionInData);
		}else{
			if ((currentScreen >= lastDragItemPosition && currentScreen <= originalViewHolder.positionInData)
					|| (currentScreen <= lastDragItemPosition && currentScreen >= originalViewHolder.positionInData)) {
				if(lastDragItemPosition > originalViewHolder.positionInData){
					mLauncher.getScreenViewGroup().setCurrentScreen(mLauncher.getScreenViewGroup().getCurrentScreen() + 1);
				}else if(lastDragItemPosition < originalViewHolder.positionInData){
					mLauncher.getScreenViewGroup().setCurrentScreen(mLauncher.getScreenViewGroup().getCurrentScreen() - 1);
				}
			}
		}
	}

	/**
	 * <br>Description: 刷新布局
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午09:20:48
	 */
	public void refresh(){
		this.pageViews.clear();
		removeAllViews();
//		reLayout(getCurrentScreen());
		reLayout();
	}

	/**
	 * <br>Description: 屏幕缩略视图
	 * <br>Author:caizp
	 * <br>Date:2012-6-20下午09:15:34
	 * @see com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView#onGetItemView(com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData, int)
	 */
	@Override
	public View onGetItemView(ICommonData data, final int position) {
		final PreviewCellInfo cellInfo = (PreviewCellInfo) data.getDataList().get(position);
		final PreviewCellView view = (PreviewCellView) mInflater.inflate(R.layout.preview_cell_item, null, false);
		if(mPreviewMode == PreviewEditAdvancedController.DROP_PREVIEW_MODE){
			mLauncher.getDragController().addDropTarget(view);
			dropTargetList.add(view);
		}
		view.setPreviewEditAdvancedController(mPreviewEditAdvancedController);
		PreviewImageView previewScreen = (PreviewImageView) view.findViewById(R.id.screen_preview);
		View screenBg = (View) view.findViewById(R.id.screen_bg);
		ImageButton homeBtn = (ImageButton) view.findViewById(R.id.home_btn);
		ImageButton delBtn = (ImageButton) view.findViewById(R.id.del_btn);
		if((data.getDataList().size()-1) == position){
			if(cellInfo.getCellType() == PreviewCellInfo.TYPE_ADD_SCREEN){
				//"增加屏幕"按钮
				screenBg.setBackgroundResource(R.drawable.preview_border_selector);
				ImageView previewAdd = (ImageView) view.findViewById(R.id.screen_add);
				previewAdd.setImageResource(R.drawable.preview_add_btn);
				view.setScreenIndex(position);
				view.setLastView(true);
				view.setTag(cellInfo);
				return view;
			}
		}
		//拖动项放置模式不显示设置主屏和删除按钮
		if(mPreviewMode == PreviewEditAdvancedController.DROP_PREVIEW_MODE){
			homeBtn.setVisibility(View.INVISIBLE);
			delBtn.setVisibility(View.INVISIBLE);
		}
		
		if ((!BaseLauncher.hasDrawer) && (mLauncher.getScreenViewGroup().getCellLayoutAt(position).getChildCount() > 0)) {
			delBtn.setVisibility(View.INVISIBLE);
		}
		
		screenBg.setBackgroundResource(R.drawable.preview_border);
		previewScreen.setWillBeDrawedGroup(mLauncher.getScreenViewGroup().getCellLayoutAt(position));
		if(position == mLauncher.getScreenViewGroup().getCurrentScreen()){
			screenBg.setBackgroundResource(R.drawable.preview_border_light);
		}
		homeBtn.setImageResource(R.drawable.preview_home_btn_selector);
		if(position == mLauncher.getScreenViewGroup().getDefaultScreen()){
			homeBtn.setImageResource(R.drawable.preview_home_btn_light);
		}
		delBtn.setImageResource(R.drawable.preview_del_btn_selector);
		//设置主屏
		homeBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (!BaseConfig.allowEdit(getContext())) {
					return;
				}
				CommonViewHolder holder = (CommonViewHolder) ((View)v.getParent().getParent()).getTag(R.id.common_view_holder);
				setDefaultScreen(holder.positionInData);
//				HiAnalytics.submitEvent(mLauncher, AnalyticsConstant.LAUNCHER_MENU_SCREEN_PREVIEW_SET_MAINSCREEN);
			}
			
		});
		//删除屏幕
		delBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (!BaseConfig.allowEdit(getContext())) {
					return;
				}
				CommonViewHolder holder = (CommonViewHolder) ((View)v.getParent().getParent()).getTag(R.id.common_view_holder);
				removeScreenIfNeed(holder.positionInData);
//				HiAnalytics.submitEvent(mLauncher, AnalyticsConstant.LAUNCHER_MENU_SCREEN_PREVIEW_DELSCREEN);
			}
			
		});
		view.setScreenIndex(position);
		view.setTag(cellInfo);
		return view;
	}
	
	/**
	 * <br>Description: 清除所有的dropTarget
	 * <br>Author:caizp
	 * <br>Date:2012-6-27下午04:39:50
	 */
	public void clearDropTargetList(){
		if(mPreviewMode == PreviewEditAdvancedController.DROP_PREVIEW_MODE){
			for(int i=0; i<dropTargetList.size(); i++){
				mLauncher.getDragController().removeDropTarget(dropTargetList.get(i));
			}
		}
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		//编辑模式下增加"添加"按钮
		if(mLauncher.getScreenViewGroup().getChildCount() < ScreenViewGroup.MAX_SCREEN){
			if (getHandler() != null) {
				getHandler().postDelayed(new Runnable() {

					@Override
					public void run() {
						addAddButton();
					}
				}, 500);
			}
		}
		if(null != mDragView){
			View screenBg = (View) mDragView.findViewById(R.id.screen_bg);
			if(isDragCurrentScreen){
				screenBg.setBackgroundResource(R.drawable.preview_border_light);
				isDragCurrentScreen = false;
			}else{
				screenBg.setBackgroundResource(R.drawable.preview_border);
			}
		}
		super.onDropCompleted(target, success);
	}

	@Override
	public void startDrag(View view, int positionInData, int positionInScreen,
			Object dragInfo) {
		//编辑模式下去除"添加"按钮
		if(mLauncher.getScreenViewGroup().getChildCount() < ScreenViewGroup.MAX_SCREEN){
			removeAddButton();
		}
		mDragView = view;
		View screenBg = (View) view.findViewById(R.id.screen_bg);
		screenBg.setBackgroundResource(R.drawable.preview_border_drag);
		if(positionInData == mLauncher.getScreenViewGroup().getCurrentScreen()){
			isDragCurrentScreen = true;
		}
		super.startDrag(view, positionInData, positionInScreen, dragInfo);
		lastDragItemPosition = positionInData;
	}
	
	/**
	 * <br>Description: 新增空白屏幕
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午05:55:59
	 */
	public void addBlankScreen(){
		
		if (!BaseConfig.allowEdit(getContext())) {
			return;
		}
		
		//修改预览数据
		removeAddButton();
		PreviewCellInfo cellInfo = new PreviewCellInfo();
		final int position = getData(0).getDataList().size();
		cellInfo.setPosition(position);
		cellInfo.setCellType(PreviewCellInfo.TYPE_NORMAL_SCREEN);
		final PreviewCellView view = (PreviewCellView) mInflater.inflate(R.layout.preview_cell_item, null, false);
		View screenBg = (View) view.findViewById(R.id.screen_bg);
		view.setScreenIndex(position);
		view.setPreviewEditAdvancedController(mPreviewEditAdvancedController);
		ImageButton homeBtn = (ImageButton) view.findViewById(R.id.home_btn);
		ImageButton delBtn = (ImageButton) view.findViewById(R.id.del_btn);
		//"增加屏幕"按钮
		screenBg.setBackgroundResource(R.drawable.preview_border);
		homeBtn.setImageResource(R.drawable.preview_home_btn_selector);
		delBtn.setImageResource(R.drawable.preview_del_btn_selector);
		//设置主屏
		homeBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 编辑被锁定项目不可执行
				if (!BaseConfig.allowEdit(getContext())) {
					return;
				}
				CommonViewHolder holder = (CommonViewHolder) ((View)v.getParent().getParent()).getTag(R.id.common_view_holder);
				setDefaultScreen(holder.positionInData);
//				HiAnalytics.submitEvent(mLauncher, AnalyticsConstant.LAUNCHER_MENU_SCREEN_PREVIEW_SET_MAINSCREEN);
			}
			
		});
		//删除屏幕
		delBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 编辑被锁定项目不可执行
				if (!BaseConfig.allowEdit(getContext())) {
					return;
				}
				CommonViewHolder holder = (CommonViewHolder) ((View)v.getParent().getParent()).getTag(R.id.common_view_holder);
				removeScreenIfNeed(holder.positionInData);
//				HiAnalytics.submitEvent(mLauncher, AnalyticsConstant.LAUNCHER_MENU_SCREEN_PREVIEW_DELSCREEN);
			}
			
		});
		view.setTag(cellInfo);
		getData(0).getDataList().add(cellInfo);
		//刷新预览布局
		refresh();
		//修改Workspace数据
		mLauncher.getScreenViewGroup().createScreenToWorkSpace();
		
		//屏幕数不超过最大则增加"添加"按钮
		if(getData(0).getDataList().size() < ScreenViewGroup.MAX_SCREEN){
			addAddButton();
		}
	}
	
	/**
	 * <br>Description: 刷新预览界面当前屏幕
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午09:11:28
	 * @param position
	 */
	private void refreshPreviewCurrentScreen(int position){
		//修改预览数据
		int pageCount = getChildCount();
		for (int page = 0; page < pageCount; page++) {
			DraggerLayout layout = (DraggerLayout) getChildAt(page);
			for(int i=0; i<layout.getChildCount(); i++){
				View v = layout.getChildAt(i);
				View screenBg = (View) v.findViewById(R.id.screen_bg);
				PreviewCellInfo cellInfo = (PreviewCellInfo)v.getTag();
				if(null != cellInfo && cellInfo.getCellType() != PreviewCellInfo.TYPE_ADD_SCREEN){
					if(i == position){
						screenBg.setBackgroundResource(R.drawable.preview_border_light);
					}else{
						screenBg.setBackgroundResource(R.drawable.preview_border);
					}
				}
			}
		}
	}
	
	/**
	 * <br>Description: 设置默认屏幕
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午09:11:28
	 * @param position
	 */
	private void setDefaultScreen(int position){
		//修改预览数据
		int pageCount = getChildCount();
		for (int page = 0; page < pageCount; page++) {
			DraggerLayout layout = (DraggerLayout) getChildAt(page);
			for(int i=0; i<layout.getChildCount(); i++){
				View v = layout.getChildAt(i);
				PreviewCellInfo cellInfo = (PreviewCellInfo)v.getTag();
				if(null != cellInfo && cellInfo.getCellType() != PreviewCellInfo.TYPE_ADD_SCREEN){
					ImageButton homeBtn = (ImageButton) v.findViewById(R.id.home_btn);
					if(cellInfo.getPosition() == position){
						homeBtn.setImageResource(R.drawable.preview_home_btn_light);
					}else{
						homeBtn.setImageResource(R.drawable.preview_home_btn_selector);
					}
				}
			}
		}
		//修改Workspace数据
		mLauncher.getScreenViewGroup().setDefaultScreen(position);
	}
	
	/**
	 * <br>Description: 删除屏幕
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午06:04:59
	 * @param position
	 */
	private void removeScreenIfNeed(final int position){
		if (getData(0).getDataList().size() == 2) {
			Toast.makeText(mLauncher, R.string.message_preview_cannot_delete_screen, Toast.LENGTH_SHORT).show();
			return;
		}
		//屏幕有数据，确认要删除
		CellLayout cellLayout = (CellLayout)mLauncher.getScreenViewGroup().getChildAt(position);
		if(cellLayout.getChildCount()>0){
			for(int i = 0; i < cellLayout.getChildCount(); i ++){//含有应用列表无法删除
				if(mLauncher.getScreenViewGroup().isAllAppsIndependence(cellLayout.getChildAt(i))){
					ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip),
                            mContext.getString(R.string.message_preview_delete_screen_not_allow),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },
                            true).show();
					return;
				}
			}
			ViewFactory.getAlertDialog(mContext, mContext.getString(R.string.common_tip), mContext.getString(R.string.message_preview_delete_screen), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteScreen(position);
				}
			}).show();
		}else{
			deleteScreen(position);
		}
	}
	
	/**
	 * <br>Description: 删除屏幕并处理视图与数据
	 * <br>Author:caizp
	 * <br>Date:2012-6-26上午10:51:27
	 * @param position
	 */
	private void deleteScreen(final int position) {
		//修改预览数据
		getData(0).getDataList().remove(position);
		
		//目前不支持大于9屏的动画
		boolean isAnimation = false;
		if(getChildCount() == 1){
			DraggerLayout layout = (DraggerLayout) getChildAt(0);
			View removeView = layout.getChildAt(position);
			layout.removeView(removeView);
			layout.startViewAnimation(removeView, layout.getChildAt(layout.getChildCount() - 1), true, ScreenUtil.getCurrentScreenWidth(mContext));
			isAnimation = true;
		}
		
		int currentScreen = mLauncher.getScreenViewGroup().getCurrentScreen();
		int defaultScreen = mLauncher.getScreenViewGroup().getDefaultScreen();
		if(position < currentScreen) {
			currentScreen--;
		}
		if(position < defaultScreen) {
			defaultScreen--;
		}
		
		if (mLauncher.getScreenViewGroup().getChildCount()-1 <= currentScreen) {
			currentScreen = 0;
		}
		if (mLauncher.getScreenViewGroup().getChildCount()-1 <= defaultScreen) {
			defaultScreen = 0;
		}
		refreshPreviewCurrentScreen(currentScreen);
		mLauncher.getScreenViewGroup().setCurrentScreen(currentScreen);
		setDefaultScreen(defaultScreen);
		PreviewCellInfo lastCellInfo = (PreviewCellInfo)getData(0).getDataList().get(getData(0).getDataList().size()-1);
		if(lastCellInfo.getCellType() != PreviewCellInfo.TYPE_ADD_SCREEN && getChildCount() == 1){
			addAddButton();
		} else if(!isAnimation){
			refresh();
		}
		//修改Workspace数据
		mLauncher.getScreenViewGroup().removeScreenFromWorkspace(position);
	}
	
	/**
	 * <br>Description: 增加"添加"按钮
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午05:47:11
	 */
	private void addAddButton(){
		PreviewCellInfo cellInfo = new PreviewCellInfo();
		cellInfo.setPosition(getData(0).getDataList().size());
		cellInfo.setCellType(PreviewCellInfo.TYPE_ADD_SCREEN);
		final PreviewCellView addView = (PreviewCellView) mInflater.inflate(R.layout.preview_cell_item, null, false);
		addView.findViewById(R.id.screen_bg).setBackgroundResource(R.drawable.preview_border_selector);
		ImageView previewAdd = (ImageView) addView.findViewById(R.id.screen_add);
		previewAdd.setImageResource(R.drawable.preview_add_btn);
		addView.setPreviewEditAdvancedController(mPreviewEditAdvancedController);
		//"增加屏幕"按钮
		addView.setScreenIndex(getData(0).getDataList().size());
		addView.setLastView(true);
		addView.setTag(cellInfo);
		getData(0).getDataList().add(cellInfo);
		//刷新布局
		refresh();
	}
	
	/**
	 * <br>Description: 删除"添加"按钮
	 * <br>Author:caizp
	 * <br>Date:2012-6-25下午05:47:32
	 */
	private void removeAddButton(){
		try {
			int lastScreen = getChildCount() - 1;
			if (lastScreen < 0)
				return;
			DraggerLayout layout = (DraggerLayout) getChildAt(lastScreen);
			List<ICommonDataItem> dataList = getData(0).getDataList();
			View addView = layout.getChildAt(layout.getChildCount() - 1);
			dataList.remove(dataList.size()-1);
			layout.removeView(addView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
