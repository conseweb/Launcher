package com.nd.hilauncherdev.framework.view.commonview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.framework.view.MyPhoneViewPager;
import com.nd.hilauncherdev.framework.view.MyPhoneViewPagerTab;
import com.bitants.launcher.R;

/**
 * 我的手机统一布局
 */
public class MyphoneTabContainer extends LinearLayout {
	private final static int GREEN = 0 , RED = 1 , BLUE = 3;
	// 我的手机顶部高度
	private final static int TOP_H = 45;// 参见@dimen/myphone_top_content
	// ViewPagerTab高度
	private final static int TAB_H = 40;// 参见@dimen/myphone_common_tab_height
	// MARGIN_LEFT参见R.dimen.myphone_margin_left,TEXT_PADDING参见dimen/text_drawpadding
	private final static int TEXT_PADDING = 3;
	private final static int BOTTOM_H = 60;// @dimen/myphone_bottom_content
	private Context mContext;

	/*******************************************
	 * 头部Layout变量
	 *******************************************/
	// 头部Layout
	private LinearLayout mHead , mMenuLayout , mGoBackLayout;
	// 返回操作的ImageView
	private ImageView 	mMenu;
	// 标题
	private TextView mContainerTitle;
	
	/**
	 * 底部颜色
	 */
//	private int bottomColor = Color.WHITE;

	/*******************************************
	 * end 头部变量
	 *******************************************/

	/*******************************************
	 * 内容Layout变量
	 *******************************************/
	// 内容Layout
	private LinearLayout mContainer, mBottom;
	// ViewPagerTab
	private MyPhoneViewPagerTab mContainerPagerTab;
	// ViewPager
	private MyPhoneViewPager mContainerViewPager;

	/*******************************************
	 * end 内容Layout变量
	 *******************************************/

	/**
	 * 每一个底部操作项中外层都多包了一个RelativeLayout 因为一般会在底部操作项中加入一点标志 如:我的铃声下载管理中有下载个数标志,
	 * 新功能标志等,所以bottoms保存底部操作项
	 */
	private List<RelativeLayout> bottoms;

	public MyphoneTabContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MyphoneTabContainer(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public void setInitTab(int iTab){
		try {
			mContainerPagerTab.setInitTab(iTab);
			mContainerViewPager.setInitTab(iTab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化工作
	 */
	private void init() {
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setOrientation(VERTICAL);
//		this.setBackgroundColor(R.color.myphone_common_bg_color);
//		this.setBackgroundResource(R.drawable.myphone_bg_color);
		// 初始化头部
		initHead();
		// 初始化ViewPaper
		initViewPaper();
	}

	/**
	 * 初始化头部
	 */
	private void initHead() {
		
		// 头部Layout
		mHead = new LinearLayout(mContext);
		mHead.setOrientation(HORIZONTAL);
		mHead.setGravity(Gravity.CENTER_VERTICAL);
		mHead.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, TOP_H)));
		mHead.setBackgroundResource(R.drawable.common_title_bg);
		// 返回操作的ImageView
		
		mGoBackLayout = new LinearLayout(mContext);
		mGoBackLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		mGoBackLayout.setGravity(Gravity.CENTER);
		mGoBackLayout.setBackgroundResource(R.drawable.common_menu_selector);
		
		ImageView mGoback = new ImageView(mContext);
		mGoback.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mGoback.setImageResource(R.drawable.common_back);
		mGoBackLayout.addView(mGoback);
		mHead.addView(mGoBackLayout);
		
		// 标题
		mContainerTitle = new TextView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		mContainerTitle.setLayoutParams(params);
		mContainerTitle.setGravity(Gravity.CENTER);
		mContainerTitle.setTextSize(20);// R.dimens.myphone_title_text_size
		mContainerTitle.setTextColor(Color.WHITE);// R.color.myphone_common_title_color1
		mHead.addView(mContainerTitle);
		
		mMenuLayout = new LinearLayout(mContext);
		//dip2px(mContext, 50)
		mMenuLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		mMenuLayout.setGravity(Gravity.CENTER);
		mMenuLayout.setBackgroundResource(R.drawable.common_menu_selector);
		
		mMenu = new ImageView(mContext);
		mMenu.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mMenu.setImageResource(R.drawable.common_sort_menu);
		mMenuLayout.setVisibility(View.INVISIBLE);
		mMenuLayout.addView(mMenu);
		mHead.addView(mMenuLayout);
		
		addView(mHead);
	}

	/**
	 * 初始化ViewPaper
	 */
	private void initViewPaper() {
		// mPagerTab
		mContainerPagerTab = new MyPhoneViewPagerTab(mContext);
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, TAB_H));
		mContainerPagerTab.setLayoutParams(lparams);
		// ViewPager
		mContainerViewPager = new MyPhoneViewPager(mContext);
		lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		mContainerViewPager.setLayoutParams(lparams);
	}

	/**
	 * 初始化Container
	 * @param customViewPaper 自定义的ViewPaper,为null则默认的ViewPaper
	 * @param title
	 * @param views
	 * @param tabTitles
	 */
	public void initContainer(MyPhoneViewPager customViewPaper, String title, View[] views, String[] tabTitles) {

		RelativeLayout layout = new RelativeLayout(mContext);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
		layout.setBackgroundResource(R.drawable.myphone_bg_color);
		this.addView(layout);

		// 内容Layout
		mContainer = new LinearLayout(mContext);
		mContainer.setOrientation(VERTICAL);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mContainer.setLayoutParams(params);
		layout.addView(mContainer);

		// ViewPagerTab添加到mContainer中
		mContainer.addView(mContainerPagerTab);
		// 不为null则使用自定义ViewPaper
		if (customViewPaper != null)
			mContainerViewPager = customViewPaper;
		// ViewPager添加到mContainer中
		mContainer.addView(mContainerViewPager);

		// 添加Tabs
		createTabs(title, views, tabTitles);
	}

	/**
	 * 添加Tabs
	 * @param title
	 * @param views
	 * @param tabTitles
	 */
	private void createTabs(String title, View[] views, String[] tabTitles) {
		mContainerTitle.setText(title);
		if (views != null)
			for (View view : views) {
				mContainerViewPager.addView(view);
			}
		mContainerPagerTab.addTitle(tabTitles);
		mContainerPagerTab.setViewpager(mContainerViewPager);
		mContainerViewPager.setTab(mContainerPagerTab);
	}

	/**
	 * 添加底部操作栏(可以没有底部操作栏)
	 * @param texts
	 * @param srcs
	 * @param listeners 可以为空
	 */
	public void initBottom(String[] texts, int[] srcs, OnClickListener[] listeners) {
		if (texts == null) {
			return;
		}
		if (bottoms == null) {
			bottoms = new ArrayList<RelativeLayout>();
		}
		// 自定义底部
		mBottom = new LinearLayout(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, BOTTOM_H));
		mBottom.setGravity(Gravity.CENTER);
		mBottom.setOrientation(HORIZONTAL);
		mBottom.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, BOTTOM_H)));
		mBottom.setBackgroundResource(R.drawable.common_btn_layout_bg);
		mBottom.setPadding(dip2px(mContext, 6), dip2px(mContext, 6), 0 ,dip2px(mContext, 6));
		mBottom.setLayoutParams(params);
		
		mContainer.addView(mBottom);
		int res = -1;
		// 参见@color/myphone_bottom_black_bg_text_color
//				bottomColor = Color.parseColor("#d3d3d3");
		// 添加底部操作
		for (int i = 0; i < texts.length; i++) {
			if (srcs == null) {
				res = -1;
			} else {
				res = srcs[i];
			}
			if (listeners == null || listeners[i] == null) {
				mBottom.addView(createBottom(texts[i], res, null, BLUE));
			} else {
				mBottom.addView(createBottom(texts[i], res, listeners[i], BLUE));
			}// end if-else
		}// end for
	}

	/**
	 * 创建底部操作项
	 * @param text
	 * @param src
	 * @param listener
	 * @return RelativeLayout
	 */
	private RelativeLayout createBottom(String text, int src, OnClickListener listener,  int style) {
		RelativeLayout layout = new RelativeLayout(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		lp.setMargins(0, 0, dip2px(mContext, 6), 0);
		layout.setLayoutParams(lp);
		LinearLayout innerLayout = new LinearLayout(mContext);
		innerLayout.setId(199);
		innerLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if(style == GREEN){
			innerLayout.setBackgroundResource(R.drawable.common_btn_green_selector);
		}else if(style == RED){
			innerLayout.setBackgroundResource(R.drawable.common_btn_red_selector);
		}else{
			innerLayout.setBackgroundResource(R.drawable.common_btn_blue_selector);
		}
		innerLayout.setGravity(Gravity.CENTER);
//		innerLayout.setClickable(true);
//		innerLayout.setFocusable(true);
		
		if (src > 0) {
			ImageView im = new ImageView(mContext);
			im.setBackgroundResource(src);
			innerLayout.addView(im);
		}
		
		TextView tv = new TextView(mContext);
		tv.setId(299);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(18);// 参见@dimen/myphone_title_bottom_text_size
		tv.setTextColor(getResources().getColor(R.color.white));// 参见@color/white
		tv.setText(text);
//		tv.setClickable(true);
//		tv.setFocusable(true);
//		tv.setDuplicateParentStateEnabled(true);
		
//		innerLayout.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View arg0, MotionEvent event) {
//				int action = event.getAction();
//				if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
//					tv.setTextColor(getResources().getColor(R.color.common_btn_text_color));
//				} else if (action == MotionEvent.ACTION_DOWN) {
//					tv.setTextColor(getResources().getColor(R.color.white));
//				}
//				return false;
//			}
//		});
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(dip2px(mContext, TEXT_PADDING), 0, 0, 0);// @dimen/text_drawpadding
		tv.setLayoutParams(params);
		innerLayout.addView(tv);
		innerLayout.setOnClickListener(listener);
		layout.addView(innerLayout);
		// 保存
		bottoms.add(layout);
		return layout;
	}

	/**
	 * 向外提供一个头部添加子View的接口
	 * @param child
	 */
	public void addViewIntoHead(View child) {
		mMenu.setVisibility(View.GONE);
		if (child != null)
			mHead.addView(child);
	}

	public MyPhoneViewPagerTab getPagerTab() {
		return mContainerPagerTab;
	}

	public MyPhoneViewPager getViewPager() {
		return mContainerViewPager;
	}

	/**
	 * 设置返回操作监听
	 * @param listener
	 */
	public void setGoBackListener(OnClickListener listener) {
		mGoBackLayout.setOnClickListener(listener);
	}

	/**
	 * 向外提供一个底部添加子View的接口
	 * @param index 第几个操作项
	 * @param child
	 */
	public void addViewIntoBottom(int index, View child) {
		if (child != null)
			bottoms.get(index).addView(child);
	}

	/**
	 * 向外提供一个Container添加子View的接口
	 * @param child
	 */
	public void addViewIntoContainer(View child) {
		if (child != null)
			mContainerViewPager.addView(child);
	}

	/**
	 * 将dip转换为px
	 * @param cxt
	 * @param dipValue
	 * @return int
	 */
	public static int dip2px(Context cxt, float dipValue) {
		final float scale = cxt.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	/**
	 * 头部返回是否可见
	 * @param visibility
	 */
	public void setGobackVisibility(int visibility) {
		mGoBackLayout.setVisibility(visibility);
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		mContainerTitle.setText(title);
	}
	
	/**
	 * 设置标题文字位置
	 * @param gravity
	 */
	public void setTitleGravity(int gravity){
		mContainerTitle.setGravity(gravity);
	}
	
	/**
	 * 菜单是否可见
	 * @param visibility
	 */
	public void setMenuVisibility(int visibility) {
		mMenuLayout.setVisibility(visibility);
	}
	
	/**
	 * 修改菜单图片
	 * @param resid
	 */
	public void setMenuImageResource(int resid){
		mMenu.setImageResource(resid);
	}
	
	/**
	 * 设置菜单点击事件
	 * @param listener
	 */
	public void setMenuListener(OnClickListener listener) {
		mMenuLayout.setOnClickListener(listener);
	}

}
