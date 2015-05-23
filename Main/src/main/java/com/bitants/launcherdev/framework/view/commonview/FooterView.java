package com.bitants.launcherdev.framework.view.commonview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bitants.launcher.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 通用的头部
 */
public class FooterView extends LinearLayout{

	public final static float BOTTOM_H 	= 60f;// @dimen/myphone_bottom_content
	// MARGIN_LEFT参见R.dimen.myphone_margin_left,TEXT_PADDING参见dimen/text_drawpadding
	private final static int TEXT_PADDING = 3;
	private final static int GREEN = 0 , RED = 1 , BLUE = 3;
	
	private Context 	mContext;
	/**
	 * 每一个底部操作项中外层都多包了一个RelativeLayout 因为一般会在底部操作项中加入一点标志 如:我的铃声下载管理中有下载个数标志,
	 * 新功能标志等,所以bottoms保存底部操作项
	 */
	private List<RelativeLayout> bottoms;
	
	private ImageView selectIv;
	
	private LinearLayout mFooter;
	public FooterView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public FooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.setOrientation(HORIZONTAL);
		this.setGravity(Gravity.CENTER);
		mFooter = new LinearLayout(mContext);
		mFooter.setOrientation(HORIZONTAL);
		mFooter.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, BOTTOM_H)));
		mFooter.setBackgroundResource(R.drawable.common_btn_layout_bg);
		mFooter.setPadding(dip2px(mContext, 6), dip2px(mContext, 6), 0 ,dip2px(mContext, 6));
		this.addView(mFooter);
	}
	
	/**
	 * 创建单个按钮
	 * @param text
	 * @param resid
	 * @param listener
	 */
	public void createSingleButton(String text, int resid, OnClickListener listener){
		mFooter.removeAllViews();
		bottoms = new ArrayList<RelativeLayout>();
		RelativeLayout layout = new RelativeLayout(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		lp.setMargins(0, 0, dip2px(mContext, 6), 0);
		layout.setLayoutParams(lp);
		LinearLayout innerLayout = new LinearLayout(mContext);
		innerLayout.setId(199);
		innerLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		innerLayout.setBackgroundResource(R.drawable.common_btn_blue_selector);
		innerLayout.setGravity(Gravity.CENTER);
		
		if (resid > 0) {
			ImageView im = new ImageView(mContext);
			im.setBackgroundResource(resid);
			innerLayout.addView(im);
		}
		
		TextView tv = new TextView(mContext);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.CENTER);
		tv.setId(299);
		tv.setTextSize(18);
		tv.setTextColor(Color.WHITE);// 参见@color/white
		tv.setText(text);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(dip2px(mContext, TEXT_PADDING), 0, 0, 0);// @dimen/text_drawpadding
		tv.setLayoutParams(params);
		innerLayout.addView(tv);
		innerLayout.setOnClickListener(listener);
		layout.addView(innerLayout);
		// 保存
		bottoms.add(layout);
		mFooter.addView(layout);
	}
	
	/**
	 * 创建双按钮
	 * @param texts
	 * @param srcs
	 * @param listeners
	 */
	public void createButtons(String[] texts, int[] srcs, OnClickListener[] listeners) {
		mFooter.removeAllViews();
		bottoms = new ArrayList<RelativeLayout>();
		// 自定义底部
		int res = -1;
		// 添加底部操作
		for (int i = 0; i < texts.length; i++) {
			if (srcs == null) {
				res = -1;
			} else {
				res = srcs[i];
			}
			if (listeners == null || listeners[i] == null) {
				mFooter.addView(createBottom(texts[i], res, null , BLUE));
			} else {
				mFooter.addView(createBottom(texts[i], res, listeners[i] , BLUE));
			}// end if-else
		}// end for
	}
	
	
	
	/**
	 * 创建底部操作项
	 * 
	 * @param text
	 * @param src
	 * @param listener
	 * @param style
	 * @return RelativeLayout
	 */
	private RelativeLayout createBottom(String text, int src, OnClickListener listener, int style) {
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
		
		if (src > 0) {
			ImageView im = new ImageView(mContext);
			im.setBackgroundResource(src);
			innerLayout.addView(im);
		}
		
		TextView tv = new TextView(mContext);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.CENTER);
		tv.setId(299);
		tv.setTextSize(18);// 参见@dimen/myphone_title_bottom_text_size
		tv.setTextColor(Color.WHITE);// 参见@color/white
		tv.setText(text);
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
	 * 创建底部操作项
	 * 
	 * @param text
	 * @param src
	 * @param listener
	 * @return RelativeLayout
	 */
	private RelativeLayout createSelect(OnClickListener listener) {
		RelativeLayout layout = new RelativeLayout(mContext);
		LinearLayout innerLayout = new LinearLayout(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.setMargins(0, 0, dip2px(mContext, 6), 0);
		innerLayout.setLayoutParams(lp);
		innerLayout.setBackgroundResource(R.drawable.common_checkbox_bg_selector);
		innerLayout.setGravity(Gravity.CENTER);
		selectIv = new ImageView(mContext);
		selectIv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		selectIv.setBackgroundResource(R.drawable.common_checkbox_uncheck);
		innerLayout.addView(selectIv);
		innerLayout.setOnClickListener(listener);
		layout.addView(innerLayout);
		// 保存
//		bottoms.add(layout);
		return layout;
	}
	
	
	/**
	 * 
	 * @param texts
	 * @param srcs
	 * @param btnListeners
	 * @param selectListener
	 */
	public void createButtonsWithSelect(String[] texts, int[] srcs, OnClickListener[] btnListeners,OnClickListener selectListener) {
		mFooter.removeAllViews();
		bottoms = new ArrayList<RelativeLayout>();
		// 自定义底部
		int res = -1;
		// 添加底部操作
		RelativeLayout layout = new RelativeLayout(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(lp);
		mFooter.addView(layout);
		
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams( dip2px(mContext, 54f) , LayoutParams.MATCH_PARENT);
		rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		RelativeLayout right = createSelect(selectListener);
		right.setId(99);
		right.setLayoutParams(rl);
		layout.addView(right);
		
		RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout mFooterleft = new LinearLayout(mContext);
		mFooterleft.setOrientation(HORIZONTAL);
		ll.addRule(RelativeLayout.LEFT_OF, right.getId());
		mFooterleft.setLayoutParams(ll);
		layout.addView(mFooterleft);
		
		for (int i = 0; i < texts.length; i++) {
			if (srcs == null) {
				res = -1;
			} else {
				res = srcs[i];
			}
			
			int style = GREEN;
			if(i % 2 == 1){
				style = RED;
			}
			
			if (btnListeners == null || btnListeners[i] == null) {
				mFooterleft.addView(createBottom(texts[i], res, null , style ));
			} else {
				mFooterleft.addView(createBottom(texts[i], res, btnListeners[i] , style));
			}// end if-else
		}// end for
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
	 * 隐藏底部按钮
	 * @param index
	 * @param visibility
	 */
	public void setBottomVisibility(int index, int visibility) {
		if (index >= 0 && index < bottoms.size()) {
			bottoms.get(index).setVisibility(visibility);
		}
	}
	
	/**
	 * 修改按钮文字
	 * @param index
	 * @param text
	 */
	public void setBottomText(int index, String text) {
		if (index >= 0 && index < bottoms.size()) {
			TextView tv = (TextView)bottoms.get(index).findViewById(299);
			tv.setText(text);
		}
	}
	
	/**
	 * 设置按钮是否可用
	 * @param index
	 * @param enable
	 */
	public void setBottomEnabled(int index, boolean enable) {
		if (index >= 0 && index < bottoms.size()) {
			bottoms.get(index).findViewById(199).setEnabled(enable);
		}
	}
	
	@Override
	public void setSelected(boolean selected){
		if(selectIv != null){
			if(selected){
				selectIv.setBackgroundResource(R.drawable.common_checkbox_checked);
			}else{
				selectIv.setBackgroundResource(R.drawable.common_checkbox_uncheck);
			}
		}
	}
	
	/**
	 * 设置全选按钮是否可点击
	 * @param enabled
	 */
	public void setBottomSelectEnabled(boolean enabled){
		if(selectIv != null){
			selectIv.setEnabled(enabled);
		}
	}
	
}
