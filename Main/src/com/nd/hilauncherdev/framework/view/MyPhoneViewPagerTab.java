package com.nd.hilauncherdev.framework.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nd.hilauncherdev.datamodel.CommonGlobal;
import com.nd.hilauncherdev.kitset.util.ColorUtil;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.bitants.launcher.R;

/**
 * 与MyPhoneViewPager联动的Tab
 */
public class MyPhoneViewPagerTab extends View {

	public static int DEFAULT_THEME = 0, RED_THEME = 1;
	// 主题
	private int theme = DEFAULT_THEME;

	private int selectColor, defaultColor;
	int selectShadowColor, defaultShadowColor;
	private float maxWidth = 0, distanceScale, touchedTab = -1;
	private int selectTab = CommonGlobal.NO_DATA;
	/**
	 * logo图标与文字的间距
	 */
	private int drawablePadding , topPadding;
	private boolean showMore;
	private int hlLineLeft, lineTop, textMargin, textHeight, textTop, hlLineWidth, hlLineInitCenter;

	private Context ctx;
	private PopupWindow popup;
	private MyPhoneViewPager viewpager;
	private Drawable line, hlLine, more,lineBg , selectedLineBg;
	private TextPaint textPaint = new TextPaint();
	private Map<String, String> showingTitle = new HashMap<String, String>();
	private List<TabInfo> tabs = new ArrayList<TabInfo>();
	
	// tab 之间的分割线
	private Drawable tabDivideImg;
	private int width;

	public MyPhoneViewPagerTab(Context context) {
		super(context);
		init(context);
	}

	public MyPhoneViewPagerTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyPhoneViewPagerTab(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		Resources res = context.getResources();
		selectColor = res.getColor(R.color.common_title_little_text_color);
		selectShadowColor = ColorUtil.antiColorAlpha(255, selectColor);
//		defaultColor =  Color.parseColor("#315b81");
		defaultColor =  res.getColor(R.color.common_little_text_color);
		defaultShadowColor = ColorUtil.antiColorAlpha(255, defaultColor);

		textPaint.setAntiAlias(true);
		textPaint.setColor(defaultColor);
		// textPaint.setShadowLayer(1, 1, 1, defaultShadowColor);
		textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize));
		textHeight = textPaint.getFontMetricsInt(null);
		textMargin = textHeight / 2;

		line = context.getResources().getDrawable(R.drawable.tab_bottom);
		hlLine = context.getResources().getDrawable(R.drawable.tab_scroll_tip);
		more = context.getResources().getDrawable(R.drawable.myphone_common_tab_more);
		tabDivideImg = context.getResources().getDrawable(R.drawable.tab_split);
		ctx = context;
		drawablePadding = ScreenUtil.dip2px(ctx, 10);
		topPadding = ScreenUtil.dip2px(ctx, 32);
		
		this.setBackgroundResource(R.drawable.tab_bg);
//		lineBg = context.getResources().getDrawable(R.drawable.tab_selected_bg);
		lineBg = context.getResources().getDrawable(R.drawable.tab_bottom);
		selectedLineBg = context.getResources().getDrawable(R.drawable.tab_selected_bg);
	}

	/**
	 * 默认显示的TAB
	 * @param logo  图标资源ID
	 * @param title
	 */
	public void addIconAndTitile(int logo[], String title[]) {
		final int N = title.length;
		for (int i = 0; i < N; i++) {
			showingTitle.put(title[i], "");
			TabInfo tab = new TabInfo();
			tab.logo = ctx.getResources().getDrawable(logo[i]);
			tab.title = title[i];
			tab.width = textPaint.measureText(tab.title);
			tabs.add(tab);
			if (tab.width > maxWidth)
				maxWidth = tab.width;
		}

		if (selectTab == CommonGlobal.NO_DATA)
			selectTab = (N - 1) / 2;
	}

	/**
	 * 默认显示的TAB
	 * @param title
	 */
	public void addTitle(String title[]) {
		final int N = title.length;
		for (int i = 0; i < N; i++) {
			showingTitle.put(title[i], "");
			TabInfo tab = new TabInfo();
			tab.title = title[i];
			tab.textWidth = textPaint.measureText(tab.title);
			tab.width = textPaint.measureText(tab.title);
			tabs.add(tab);
			if (tab.width > maxWidth)
				maxWidth = tab.width;
		}

		if (selectTab == CommonGlobal.NO_DATA)
			selectTab = (N - 1) / 2;

		// hlLineWidth = (int) (maxWidth * 3 / 2);
	}


	public void setTextSize(int size) {
		textPaint.setTextSize(size);
	}

	class TabInfo {
		Drawable logo;
		String title;
		float textWidth;
		float textHeight;
		float textLeft;
		float width;
		float left, top, center;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int N = tabs.size();
		if (N <= 1)
			return;
		width = MeasureSpec.getSize(widthMeasureSpec);
		maxWidth = width / N;
		
		
		final int height = MeasureSpec.getSize(heightMeasureSpec);

		textTop = (height - (textHeight * 2 + textMargin)) / 2 + textHeight + textMargin;
		lineTop = textTop + textMargin;
		float margin = (width - N * maxWidth) / N;
		float startLeft = 0;
		for (int i = 0; i < N; i++) {
			TabInfo info = tabs.get(i);
			info.width = maxWidth;
			info.left = startLeft + (maxWidth + margin) * i + (maxWidth - info.width) / 2;
			info.center = info.left + info.width / 2;
			info.top = textTop;
			info.textLeft = info.left + maxWidth / 2 - info.textWidth / 2;
		}
		hlLineWidth = width / tabs.size();
		hlLineLeft = (int) (tabs.get(0).center - hlLineWidth / 2);
		distanceScale = (tabs.get(1).center - tabs.get(0).center) / width;
		line.setBounds(0, lineTop + hlLine.getIntrinsicHeight() / 2, width, lineTop + hlLine.getIntrinsicHeight());
		hlLineInitCenter = (int) (tabs.get(selectTab).center - hlLineWidth / 2);
		hlLine.setBounds(hlLineInitCenter + hlLineWidth / 2 - 10, lineTop, hlLineInitCenter + hlLineWidth / 2 + 10, lineTop + hlLine.getIntrinsicHeight());
		lineBg.setBounds(hlLineInitCenter, lineTop - topPadding, hlLineInitCenter + hlLineWidth, lineTop + hlLine.getIntrinsicHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final int N = tabs.size();
		if (N <= 1)
			return;
		
		lineBg.draw(canvas);
		
		Rect bgRect = lineBg.getBounds();
		for (int i = 0; i < N; i++) {
			TabInfo info = tabs.get(i);
			
			if (i == selectTab || i == (int)touchedTab) {
				textPaint.setColor(selectColor);
				// textPaint.setShadowLayer(1, 1, 1, selectShadowColor);
			} else {
				textPaint.setColor(defaultColor);
				// textPaint.setShadowLayer(1, 1, 1, defaultShadowColor);
			}
			
			if(i == touchedTab && selectTab != (int)touchedTab ){
				int hlLineInitCenter = (int) (tabs.get((int)touchedTab).center - hlLineWidth / 2);
				selectedLineBg.setBounds(hlLineInitCenter, lineTop - topPadding, hlLineInitCenter + hlLineWidth, lineTop + hlLine.getIntrinsicHeight());
				selectedLineBg.draw(canvas);
			}
			
			if (null != info.logo) {
				final int left = (int) info.left - drawablePadding - ctx.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize);
				int top = textTop - info.logo.getIntrinsicHeight() / 2 - 8;
				info.logo.setBounds(left, top, left + info.logo.getIntrinsicWidth(), top + info.logo.getIntrinsicHeight());
				info.logo.draw(canvas);
			}
			String textTitle = info.title;
			char[] cs = textTitle.toCharArray();
			float leftToal = info.textLeft;
			for(int cj = 0 ; cj < cs.length ; cj++){
				char c = cs[cj];
				float wordW = textPaint.measureText(c + "");
				float temp = leftToal + wordW;
				Rect rect = new Rect((int)temp, (int)(info.top), (int)temp, (int)(info.top + 5));
				if (bgRect.contains(rect)) {
					textPaint.setColor(selectColor);
				} else {
					textPaint.setColor(defaultColor);
				}
				canvas.drawText(c + "", leftToal, info.top, textPaint);
				leftToal = temp;
			}

			if (showMore && i == selectTab) {
				final int left = (int) info.left - ctx.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize);
				int top = textTop - more.getIntrinsicHeight() / 2 - 8;
				more.setBounds(left, top, left + more.getIntrinsicWidth(), top + more.getIntrinsicHeight());
				more.draw(canvas);
			}

		}

//		line.draw(canvas);
//		hlLine.draw(canvas);
		// 画tab之间的分割线
		if (null != tabDivideImg) {
			int divideWidth = ScreenUtil.dip2px(ctx, 1);
			int divideHeight = textHeight;
			int divideTop = textTop - textHeight;
			for (int i = 0; i < N - 1; i++) {
				int divideLeft = (i + 1) * hlLineWidth - divideWidth / 2;
				tabDivideImg.setBounds(divideLeft, divideTop - divideHeight / 2, divideLeft + divideWidth, divideTop + divideHeight * 2);
				tabDivideImg.draw(canvas);
			}
		}
	}

	public void scrollHighLight(int scrollX) {
		hlLineInitCenter = hlLineLeft + (int) (scrollX * distanceScale);
		hlLine.setBounds(hlLineInitCenter + hlLineWidth / 2 - 10, lineTop, hlLineInitCenter + hlLineWidth / 2 + 10, lineTop + hlLine.getIntrinsicHeight());
		
		lineBg.setBounds(hlLineInitCenter, lineTop - topPadding, hlLineInitCenter + hlLineWidth, lineTop + hlLine.getIntrinsicHeight());
		invalidate();
	}

	public void updateSelected() {
		Rect bound = hlLine.getBounds();
		final int y = bound.top + (bound.bottom - bound.top) / 2;
		for (int i = 0; i < tabs.size(); i++) {
			if (!bound.contains((int) tabs.get(i).center, y))
				continue;

			selectTab = i;
			invalidate();
			break;
		}
	}

	public int getSelectedTab() {
		return selectTab;
	}

	public void setViewpager(MyPhoneViewPager viewpager) {
		this.viewpager = viewpager;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			TabInfo hintInfo = getHitRangeInfo(event);
			if (hintInfo != null) {
				final int touchedTab = tabs.indexOf(hintInfo);
				if (touchedTab == selectTab)
					showMore(hintInfo);
				else
					viewpager.snapToScreen(tabs.indexOf(hintInfo));
			}
			touchedTab = -1;
			invalidate();
		} else if (action == MotionEvent.ACTION_DOWN) {
			TabInfo hintInfo = getHitRangeInfo(event);
			if (hintInfo != null) {
				touchedTab = tabs.indexOf(hintInfo);
				invalidate();
			}
		}
		return true;
	}

	/**
	 * 弹出未被显示的TAB
	 */
	private void showMore(TabInfo hintInfo) {
		if (popup == null)
			return;

		popup.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.frame_viewpager_tab_popup_bg));
		popup.setFocusable(true);
		int xoff = (int) (hlLineInitCenter + hlLineWidth / 2 - popup.getWidth() / 2);
		xoff = xoff < 0 ? 0 : xoff;
		popup.showAsDropDown(this, xoff, (int) this.getTop());
		popup.update();
		// popup.setAnimationStyle(R.style.ShortCutMenuGrowFromTop);
	}

	private TabInfo getHitRangeInfo(MotionEvent e) {
		int x = Math.round(e.getX());
		int y = Math.round(e.getY());
		Rect hitRect;
		for (int index = 0; index < tabs.size(); index++) {
			TabInfo info = tabs.get(index);
			hitRect = new Rect((int) (index * info.width), (int) (info.top - textHeight - textMargin), (int) ((index + 1) * info.width), (int) (info.top + textMargin));
			if (hitRect.contains(x, y)) {
				return info;
			}
		}

		return null;
	}

	/**
	 * 指定默认显示的TAB
	 * @param tab
	 */
	public void setInitTab(int tab) {
		this.selectTab = tab;
	}

	/**
	 * 未显示TAB列表
	 */
	class TitleItemAdapter extends ArrayAdapter<String> {
		private LayoutInflater mInflater;

		public TitleItemAdapter(Context context, List<String> addressList) {
			super(context, 0, addressList);
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String title = getItem(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.framework_viewpager_tab_title_item, parent, false);
			}

			TextView titleview = (TextView) convertView.findViewById(R.id.frame_viewpager_tab_title_item_text);
			titleview.setText(title);
			return convertView;
		}
	}

	public int getTheme() {
		return theme;
	}

	public void setTheme(int theme) {
		this.theme = theme;
	}

	/**
	 * 跳转到指定屏幕,不带动画效果
	 * @param index
	 */
	public void setToScreen(int index) {
		if (index < 0 || index > tabs.size()-1)
			return;
		
		viewpager.snapToScreen(index);
		invalidate();
	}
	
	/****************************************************************
	 * end add by linqiang(866116) 添加排序功能TAB
	 ****************************************************************/

	public void setTabDivideImg(Drawable drawable) {
		tabDivideImg = drawable;
	}

	public void setLineImg(Drawable drawable) {
		line = drawable;
	}

	public void setHlLineImg(Drawable drawable) {
		hlLine = drawable;
	}

	public void setSelectColor(int color) {
		selectColor = color;
	}

}
