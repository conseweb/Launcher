package com.bitants.launcherdev.launcher.screens.preview;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.OnKeyDownListenner;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewClickListener;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewLongClickListener;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnSwitchDataListener;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.framework.view.draggersliding.datamodel.DraggerSlidingViewData;
import com.bitants.launcherdev.kitset.util.HiAnimationUtils;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.kitset.util.StatusBarUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.touch.BaseDragController;
import com.bitants.launcherdev.kitset.util.ScreenUtil;

/**
 * <br>Description: 屏幕预览， 可拖拉排序， 删除，放置拖动项
 */
public class PreviewEditAdvancedController implements OnKeyDownListenner,
		OnSwitchDataListener, OnCommonSlidingViewClickListener,
		OnCommonSlidingViewLongClickListener {
	
	/**
	 * 屏幕编辑模式
	 */
	public static final int EDIT_PREVIEW_MODE = 0;
	
	/**
	 * 拖动项放置模式
	 */
	public static final int DROP_PREVIEW_MODE = 1;
	
	/**
	 * 预览界面行数
	 */
	public static final int MAX_PREVIEW_ROW = 3;
	
	/**
	 * 预览界面列数
	 */
	public static final int MAX_PREVIEW_COL= 3;
	
	private static final int ANIMATION_DURATION = 200;
	
	/**
	 * 是否处于预览状态(编辑或放置模式)
	 */
	private boolean mIsPreviewMode = false;
	
	/**
	 * 当前预览状态
	 */
	private int mPreviewMode = EDIT_PREVIEW_MODE;
	
	private BaseLauncher mLauncher;
	public ScreenViewGroup mWorkspace;
	private BaseDragController mDragController;
	
	private AnimationSet mInAnimation;
	private AnimationSet mOutAnimation;
	
	public View mScreensEditor;
	private TextView notifyIsFullScreenZone;
    private boolean isShowNotifyZone = false;
	private PreviewWorkspace slidingView;
	
	private LayoutInflater mInflater;
	private boolean isFirstLayout = true;
	/**
	 * 总的屏幕数
	 */
	private int screenCount = 1;

	public PreviewEditAdvancedController(BaseLauncher launcher) {
		mLauncher = launcher;
		mWorkspace = mLauncher.getScreenViewGroup();
		mInflater = mLauncher.getLayoutInflater();
	}
	
	public void setDragController(BaseDragController dragController){
		mDragController = dragController;
	}
	
	/**
	 * <br>Description: 初始化动画
	 */
	private void createAnimations() {
		// 出现,从上到下,透明度与位置变化
		if (mInAnimation == null) {
			mInAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mInAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
//			animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
			animationSet.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f));
			animationSet.setDuration(ANIMATION_DURATION);
		}
		// 消失,从下到上,透明度与位置变化
		if (mOutAnimation == null) {
			mOutAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mOutAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
//			animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
			animationSet.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f));
			animationSet.setDuration(ANIMATION_DURATION);
		}
	}
	
	private static class FastAnimationSet extends AnimationSet {
		FastAnimationSet() {
			super(false);
		}

		@Override
		public boolean willChangeTransformationMatrix() {
			return true;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}
	
	public int getPreviewMode() {
		return mPreviewMode;
	}

	/**
	 * <br>Description: 开启桌面预览模式
	 * @param previewMode 0.屏幕编辑模式  1.拖动项放置模式
	 */
	public void startDesktopEdit(int previewMode) {
		mPreviewMode = previewMode;
		if (!mIsPreviewMode) {
			mIsPreviewMode = true;
			final ScreenViewGroup workspace = mWorkspace;
			if (workspace == null)
				return;
			
			workspace.cleanAndRevertReorder();
			
			//隐藏桌面
			mLauncher.invisiableWorkspace();
			if(Build.VERSION.SDK_INT < 14){//修复因第三方动画类引入引起的bug
				mLauncher.getWorkspaceLayer().scrollToHideDockBar();
			}
			createAnimations();
			try {
				initPreview();
			} catch (OutOfMemoryError e) {
				stopDesktopEdit();
				return;
			}

			if(mDragController.isDragging()){
				//放在DragView的下一层
				int count = mLauncher.getDragLayer().getChildCount();
				mLauncher.getDragLayer().addView(mScreensEditor, count-1);
			}else{
				mLauncher.getDragLayer().addView(mScreensEditor);
			}
			
			slidingView.setVisibility(View.VISIBLE);
			mScreensEditor.startAnimation(createEnterAnimation(mWorkspace.getCurrentScreen()));
			
			//第一次屏幕编辑显示引导
            /*if(!ApplicationPreferences.getHasEditScreenHint(mLauncher)){
            	//显示引导界面
            	//设置已经显示
            	handler.postDelayed(new Runnable() {					
					@Override
					public void run() {		            	
		            	IHomeUtil.showGuide(mLauncher,mDragLayer,R.drawable.guide_change_group);
		            	ApplicationPreferences.setHasEditScreenHint(mLauncher, true);						
					}
				}, 1000);
            }*/
		}
	}
	
	public void refreshPreviewView() {
		if(null != slidingView) {
			slidingView.refresh();
		}
	}

	/**
	 * <br>Description:离开桌面预览模式
	 */
	public void stopDesktopEdit() {
		if (!mIsPreviewMode)
			return;
		mIsPreviewMode = false;
		//mWorkspace.clearChildrenCache();
		//显示桌面
		mLauncher.visiableWorkspace();
		if(Build.VERSION.SDK_INT < 14){//修复因第三方动画类引入引起的bug
			mLauncher.getWorkspaceLayer().scrollToShowDockbar();
		}
		//刷新屏幕索引 caizp 2012-11-7
		for (int i = 0; i < mLauncher.getScreenViewGroup().getChildCount(); i++) {
			CellLayout cellLayout = (CellLayout) mLauncher.getScreenViewGroup().getChildAt(i);
			cellLayout.resetScreen();
		}
		
		if (mScreensEditor != null) {
			slidingView.setVisibility(View.GONE);
			slidingView.clearDropTargetList();
			mLauncher.getDragLayer().removeView(mScreensEditor);
		}
		System.gc();
	}

	/**
	 * <br>Description: 是否处于预览状态(编辑或放置模式)
	 * @return
	 */
	public boolean isPreviewMode() {
		return mIsPreviewMode;
	}

	/**
	 * <br>Description: 初始化预览界面
	 */
	private void initPreview() {
		if(isFirstLayout){
			mScreensEditor = mInflater.inflate(R.layout.preview_workspace, null);
//			ColorDrawable bg = new ColorDrawable(0xb0000000);
//			mScreensEditor.setBackgroundDrawable(bg);
			notifyIsFullScreenZone = (TextView)mScreensEditor.findViewById(R.id.notify_full_zone);
			slidingView = (PreviewWorkspace) mScreensEditor.findViewById(R.id.sliding_view);
			
			slidingView.setLauncher(mLauncher);
			slidingView.setPreviewEditAdvancedController(this);
			slidingView.setOnItemClickListener(this);
			slidingView.setOnItemLongClickListener(this);
			slidingView.setOnSwitchDataListener(this);
			
			slidingView.setDragController(mDragController);
			mDragController.addDropTarget(slidingView);
			mDragController.addDragScoller(slidingView);
			
			isFirstLayout = false;
		}
		
		screenCount = mWorkspace.getChildCount();
		//根据屏幕个数构建预览视图
		List<ICommonDataItem> cellInfos = new ArrayList<ICommonDataItem>();
		for (int i = 0; i < screenCount; i++) {
			PreviewCellInfo cellInfo = new PreviewCellInfo();
			cellInfo.setPosition(i);
			cellInfo.setCellType(PreviewCellInfo.TYPE_NORMAL_SCREEN);
			cellInfos.add(cellInfo);
		}
		//编辑模式且屏幕个数小于最大屏幕数才显示"添加"按钮
		if(screenCount < ScreenViewGroup.MAX_SCREEN){
			PreviewCellInfo cellInfo = new PreviewCellInfo();
			cellInfo.setPosition(screenCount);
			cellInfo.setCellType(PreviewCellInfo.TYPE_ADD_SCREEN);
			cellInfos.add(cellInfo);
		}
				
		slidingView.setPreviewMode(mPreviewMode);		
		List<ICommonData> dataList = slidingView.getList();
		if (dataList == null) {
			DraggerSlidingViewData mScreenData = new DraggerSlidingViewData(ScreenUtil.dip2px(mLauncher, 200),
					ScreenUtil.dip2px(mLauncher, 530), MAX_PREVIEW_ROW, MAX_PREVIEW_COL, cellInfos);
			mScreenData.setAcceptDrop(true);
			ArrayList<ICommonData> datas = new ArrayList<ICommonData>();
			datas.add(mScreenData);
			slidingView.setList(datas);
		} else {
			List<ICommonDataItem> items = dataList.get(0).getDataList();
			items.clear();
			items.addAll(cellInfos);
			slidingView.reLayout();
		}
	}

	/**
	 * <br>Description: 监听回退键，退出屏幕预览
	 * @see com.bitants.launcherdev.framework.OnKeyDownListenner#onKeyDownProcess(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDownProcess(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK || !mIsPreviewMode) 
			return false;
		
		stopDesktopEdit();
		mWorkspace.setAllowLongPress(true);
		if (mWorkspace.getCurrentScreen() >= mWorkspace.getChildCount()){
			mWorkspace.snapToScreen(mWorkspace.getChildCount() - 1, 0, false, true, true);
		}else{
			mWorkspace.snapToScreen(mWorkspace.getCurrentScreen(), 0, false, true, true);
		}
		return true;
	}
	

	/**
	 * <br>Description: 屏幕缩略图长按事件响应
	 * @see com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewLongClickListener#onItemLongClick(android.view.View, int, int, int, com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData)
	 */
	@Override
	public boolean onItemLongClick(View v, int positionInData,
			int positionInScreen, int screen, ICommonData data) {
		if (!BaseConfig.allowEdit(slidingView.getContext()) || BaseConfig.isOnScene()) {
			return true;
		}
		//编辑模式且屏幕个数小于最大屏幕数才显示"添加"按钮
		if(mWorkspace.getChildCount() < ScreenViewGroup.MAX_SCREEN){
			//长按"添加"按钮
			if(positionInData == data.getDataList().size()-1){
				return false;
			}
			
		}
		PreviewCellInfo cellInfo = (PreviewCellInfo) v.getTag();
		slidingView.startDrag(v, positionInData, positionInScreen,
				cellInfo);
		return false;
	}

	/**
	 * <br>Description: 屏幕缩略图点击事件响应
	 * @see com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewClickListener#onItemClick(android.view.View, int, int, int, com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData)
	 */
	@Override
	public void onItemClick(View v, int positionInData, int positionInScreen,
			int screen, ICommonData data) {
		PreviewCellInfo cellInfo = (PreviewCellInfo) v.getTag();
		if(null != cellInfo && cellInfo.getCellType() == PreviewCellInfo.TYPE_ADD_SCREEN){
			slidingView.addBlankScreen();
//			HiAnalytics.submitEvent(mLauncher, AnalyticsConstant.LAUNCHER_MENU_SCREEN_PREVIEW_ADDSCREEN);
			return;
		}
		stopDesktopEdit();
		//跳转到screen
		mWorkspace.snapToScreen(positionInData, 0, false, true, true);
//		mWorkspace.clearChildrenCache();
	}
	
	@Override
	public void onSwitchData(List<ICommonData> list, int fromPosition,
			int toPosition) {
		
	}
	
	/**
	 * 屏幕管理界面进入动画，向屏幕所在的位置由大到小收缩
	 * @param whichScreen
	 * @return
	 */
	private AnimationSet createEnterAnimation(int whichScreen){
		whichScreen %= 9 ;      
		float pivotX = 0.5f ;
		float pivotY = 0.5f ;
		final int row = whichScreen / 3 ;
		final int column = whichScreen % 3 ;
		pivotX = column * 0.5f ;
		pivotY = row * 0.5f ;
		return HiAnimationUtils.createScaleEnterAnamation(1.5f, 1.0f, 1.5f, 1.0f, pivotX, pivotY, 500, new AccelerateDecelerateInterpolator());
    }
	
	/**
	 * <br>Description: 显示屏幕空间已满提示
	 */
	public void showNotifyIsFullScreenZone(){
    	if(isShowNotifyZone) return;
    	StatusBarUtil.toggleStateBar(mLauncher, false);
    	notifyIsFullScreenZone.startAnimation(mInAnimation);
    	notifyIsFullScreenZone.setVisibility(View.VISIBLE);
    	isShowNotifyZone = true;
    }
	
	/**
	 * <br>Description: 隐藏屏幕空间已满提示
	 */
    public void closeNotifyIsFullScreenZone(){
    	if(isShowNotifyZone){
    		StatusBarUtil.toggleStateBar(mLauncher, true);
    		notifyIsFullScreenZone.startAnimation(mOutAnimation);
        	notifyIsFullScreenZone.setVisibility(View.INVISIBLE);
        	isShowNotifyZone = false;
    	}
    }
	
}
