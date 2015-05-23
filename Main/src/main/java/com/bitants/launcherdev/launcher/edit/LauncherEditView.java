package com.bitants.launcherdev.launcher.edit;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bitants.launcher.R;
import com.bitants.common.framework.view.BaseLineLightBar;
import com.bitants.common.framework.view.commonsliding.CommonLightbar;
import com.bitants.common.framework.view.commonsliding.CommonSlidingView.OnCommonSlidingViewClickListener;
import com.bitants.common.framework.view.commonsliding.datamodel.CommonSlidingViewData;
import com.bitants.common.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.common.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.launcher.Launcher;
import com.bitants.launcherdev.launcher.LauncherActivityResultHelper;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditEffectItemInfo;
import com.bitants.common.launcher.screens.dockbar.BaseMagicDockbar;
import com.bitants.common.launcher.support.BaseCellLayoutHelper;
import com.bitants.launcherdev.widget.LauncherWidgetInfo;

import java.util.ArrayList;
import java.util.List;

public class LauncherEditView extends RelativeLayout implements OnCommonSlidingViewClickListener {

	private Launcher mLauncher;

	private BaseMagicDockbar dockbar;

	private BaseLineLightBar launcherLineLightBar;
	
	private LauncherEditSlidingView slidingView;
	private CommonLightbar lightbar;
	
	/**
	 * 编辑状态下，初始上方2个选项layout
	 */
	public LinearLayout launcher_edit_top_layout;
	/**
	 * SlidingView和指示灯显示区
	 */
	private View content_area;
	
	private CheckedTextView addBtn;
	private CheckedTextView individalBtn;
	
	/**
	 * 添加数据集
	 */
	private CommonSlidingViewData addData;
	private CommonSlidingViewData data;
	private int iconSize;
	private int launcher_edit_cell_col, launcher_edit_cell_row, launcher_edit_widget_cell_col, launcher_edit_widget_cell_row;
	
	public static final String TAB_ADD = "add";//添加小部件
	public static final String TAB_EFFECT = "effect";//特效选择
	public static final String TAB_THEME = "theme";//手机美化
	
	private List<ICommonDataItem> commonDataItems =  new ArrayList<ICommonDataItem>();
	private List<ICommonDataItem> widgetDataItems = new ArrayList<ICommonDataItem>();
    private Context mContext;

    public LauncherEditView(Context context) {
		super(context);
        mContext = context;
	}

	public LauncherEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LauncherEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLauncher(Launcher mLauncher) {
		this.mLauncher = mLauncher;
	}

	public void setDockbar(BaseMagicDockbar dockbar) {
		this.dockbar = dockbar;
	}

	public void setLauncherLineLightBar(BaseLineLightBar lineLightBar) {
		this.launcherLineLightBar = lineLightBar;
	}
	
	/**
	 * Tab点击事件
	 */
	private OnClickListener onTabClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == addBtn) {
				/**
				 * 应用
				 */
				if (addBtn.isChecked()) {
					return;
				}
				showAddWidgetDataView();
			}else if (v == individalBtn) {
				/**
				 * 个性化
				 */
				if (individalBtn.isChecked()) {
					return;
				}
			}
		}
	};
	
	@Override
	protected void onFinishInflate() {
		addBtn = (CheckedTextView) findViewById(R.id.launcher_edit_add_btn);
		individalBtn = (CheckedTextView) findViewById(R.id.launcher_edit_individal_btn);

		setupLightbar(mContext);
		slidingView = (LauncherEditSlidingView) findViewById(R.id.launcher_edit_sliding_view);
		slidingView.setEndlessScrolling(false);
		slidingView.setSplitCommonLightbar(lightbar);
		slidingView.setOnItemClickListener(this);
		slidingView.setLauncherEditView(this);

		launcher_edit_top_layout = (LinearLayout) findViewById(R.id.launcher_edit_top_layout);

		
		content_area=findViewById(R.id.content_area);
		

		/**
		 * 添加按键监听
		 */
		addBtn.setOnClickListener(onTabClickListener);
		individalBtn.setOnClickListener(onTabClickListener);

		launcher_edit_cell_col = 4;
		launcher_edit_cell_row = 2;
		launcher_edit_widget_cell_col = 2;
		launcher_edit_widget_cell_row = 2;
		restoreViews();
		initDefaultView();
	}
	
	/**
	 * 初始化指示灯
	 */
	private void setupLightbar(Context context) {
		lightbar = (CommonLightbar) findViewById(R.id.launcher_edit_lightbar);
		lightbar.setNormalLighter(context.getResources().getDrawable(R.drawable.spring_lightbar_normal));
		lightbar.setSelectedLighter(context.getResources().getDrawable(R.drawable.spring_lightbar_checked));
	}
	
	/**
	 * 将Tab恢复默认状态
	 */
	private void restoreViews() {
		//SlidingView和指示灯显示
		content_area.setVisibility(View.VISIBLE);
		if(mLauncher != null){			
			mLauncher.setWorkspaceLocked(false);
		}
		
//		slidingView.clearFocusedView();
		addBtn.setChecked(false);
		individalBtn.setChecked(false);
	}
	
	private void initDefaultView() {
		List<ICommonData> list = new ArrayList<ICommonData>();
		slidingView.setList(list);
		iconSize = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		data = new CommonSlidingViewData(iconSize, iconSize, launcher_edit_cell_col, launcher_edit_cell_row, commonDataItems);
	}
	
	/**
	 * 
	 * 展示小部件
	 * 
	 */
	private void showAddWidgetDataView() {
		restoreViews();
		addBtn.setChecked(true);
		slidingView.getList().clear();
		slidingView.getList().add(assembleWidgetData());
		relayout();
	}
	
	/**
	 * 
	 * 展示美化手机
	 * 
	 */
	private void showThemeDataView() {
		restoreViews();
		addBtn.setChecked(true);
		slidingView.getList().clear();
		slidingView.getList().add(assembleThemeData());
		relayout();
	}
	
	
	/**
	 * 
	 * 展示美化手机
	 * 
	 */
	private void showWallpaperDataView() {
		restoreViews();
		addBtn.setChecked(true);
		slidingView.getList().clear();
		slidingView.getList().add(assembleWallpaperData());
		relayout();
	}
	
	/**
	 * 
	 * 展示滑屏特效选择
	 * 
	 */
	private void showSlideEffectDataView() {
		restoreViews();
		addBtn.setChecked(true);
		slidingView.getList().clear();
		slidingView.getList().add(assembleSlideEffectData());
		relayout();
	}
	
	
	/**
	 * 组装小部件数据集
	 */
	private ICommonData assembleWidgetData() {
		data.setChildViewHeight(2*iconSize);
		data.setChildViewWidth(4*iconSize);
		data.setColumnNum(4);
		data.setRowNum(1);
		if(widgetDataItems.isEmpty()){
		   widgetDataItems.addAll(LauncherEditDataFactory.getWidgetDataInfos(mContext));
		}
		data.getDataList().clear();
		data.getDataList().addAll(widgetDataItems);
		data.setTag(TAB_ADD);
		return data;
	}
	
	
	/**
	 * 组装小部件数据集
	 */
	private ICommonData assembleThemeData() {
		data.setChildViewHeight(2*iconSize);
		data.setChildViewWidth(4*iconSize);
		data.setColumnNum(4);
		data.setRowNum(1);
		data.getDataList().clear();
		data.getDataList().addAll(LauncherEditDataFactory.getThemeDataInfos(mContext));
		data.setTag(TAB_THEME);
		return data;
	}
	
	/**
	 * 组装滑屏特效数据集
	 */
	private ICommonData assembleWallpaperData() {
		data.setChildViewHeight(2*iconSize);
		data.setChildViewWidth(4*iconSize);
		data.setColumnNum(4);
		data.setRowNum(1);
		data.getDataList().clear();
		data.getDataList().addAll(LauncherEditDataFactory.getWallpaperDataInfos(mContext));
		data.setTag(TAB_EFFECT);
		return data;
	}
	
	/**
	 * 组装滑屏特效数据集
	 */
	private ICommonData assembleSlideEffectData() {
		data.setChildViewHeight(2*iconSize);
		data.setChildViewWidth(4*iconSize);
		data.setColumnNum(4);
		data.setRowNum(1);
		data.getDataList().clear();
		data.getDataList().addAll(LauncherEditDataFactory.getSlideEffectDataInfos(mContext));
		data.setTag(TAB_EFFECT);
		return data;
	}
	
	/**
	 * 立即布局
	 */
	public void relayout(){
		// TODO Auto-generated method stub
		slidingView.setList(slidingView.getList());
		slidingView.go2FirstScreen();
		lightbar.update(0);
	}
	
	/**
	 * 立即布局
	 * @param screen
	 */
	public void relayout(final int screen){
		// TODO Auto-generated method stub
		slidingView.setList(slidingView.getList());
		ICommonData data = slidingView.getData(screen);

		if(data != null && screen != -1){
			int pageNums = slidingView.getData(screen).getPageNum();
			if(screen < pageNums){
				slidingView.go2Screen(screen);
				lightbar.update(screen);
				return;
			}
		}
		slidingView.go2FirstScreen();
		lightbar.update(0);
		
	}
	@Override
	public void onItemClick(View v, final int positionInData, int positionInScreen, int screen, final ICommonData data) {
		if(data == null || data.getDataList() == null || positionInData > data.getDataList().size()-1 || data.getDataList().size() == 0){
			return;
		}
		ICommonDataItem dataItem = data.getDataList().get(positionInData);

		if (dataItem instanceof LauncherWidgetInfo) {
			final LauncherWidgetInfo item = (LauncherWidgetInfo) dataItem;
			if (item.type == LauncherWidgetInfo.TYPE_SYSTEM) {
				if (BaseCellLayoutHelper.findCellXYForApp(mLauncher) == null) {
					return;
				}
//				setAllowClick(false);
//				if (Build.VERSION.SDK_INT < 16) {
					showAppWidgetPickPage(mLauncher);
//					Toast.makeText(mContext, R.string.common_loading,
//							Toast.LENGTH_SHORT).show();
//				} else {
//					showSystemWidgetViewCategory(0);
//					setAllowClick(true);
//				}
			}
		}else if(dataItem instanceof LauncherEditEffectItemInfo){
			final LauncherEditEffectItemInfo item = (LauncherEditEffectItemInfo) dataItem;
			BaseSettingsPreference.getInstance().setScreenScrollEffects(item.type);
			mLauncher.getWorkspace().changeToNormalMode();
			hideWithAnimation();
		}
	}
	
	
	/**
	 * 显示添加小部件页面
	 */
	public void showAppWidgetPickPage(Launcher mLauncher) {
		int appWidgetId = mLauncher.getAppWidgetHost().allocateAppWidgetId();

		Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// start the pick activity
		mLauncher.startActivityForResult(pickIntent, LauncherActivityResultHelper.REQUEST_PICK_APPWIDGET);
//		setAllowClick(true, 1000);
	}
	
	/**
	 * Description: 带动画显示
	 */
	public void showWithAnimation(final String tab) {
		if (this.getVisibility() != VISIBLE) {
			Animation showAni = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
			showAni.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
					show(tab);
				}
				public void onAnimationRepeat(Animation animation) {
				}
				public void onAnimationEnd(Animation animation) {
//					if(!mLauncher.isOnSpringMode()){
//						hideWithAnimation();
//					}
				}
			});
			startAnimation(showAni);
		}
	}

	/**
	 * Description: 带动画退出
	 */
	public void hideWithAnimation() {
		if (this.getVisibility() == VISIBLE) {
			Animation hideAni = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
			hideAni.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}
				public void onAnimationRepeat(Animation animation) {
				}
				public void onAnimationEnd(Animation animation) {
					hide();
				}
			});
			startAnimation(hideAni);
		} else {
			dockbar.setVisibility(View.VISIBLE);
			launcherLineLightBar.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 更新显示具体内容
	 */
	public void show(String tag) {		
		setVisibility(View.VISIBLE);
		if (TAB_ADD.equals(tag)) {
			showAddWidgetDataView();
		}
		else if (TAB_THEME.equals(tag)) {
			showThemeDataView();
		}
		else if (TAB_EFFECT.equals(tag)) {
			showSlideEffectDataView();
		}
	}
	
	/**
	 * 更新显示具体内容
	 */
	private void hide() {
		setVisibility(View.GONE);
//		dockbar.setVisibility(View.VISIBLE);
//		launcherLineLightBar.setVisibility(View.VISIBLE);
		clear();
	}
	
	/**
	 * 退出编辑模式 清除无用数据
	 */
	private void clear(){
//		themeList.clear();
//		wallpaperList.clear();
//		installedWidgetList.clear();
//		systemWidgetList.clear();
//		commonDataItems.clear();
//		data.getDataList().clear();
//		slidingView.getList().clear();
//		slidingView.clearData();
	}
}
