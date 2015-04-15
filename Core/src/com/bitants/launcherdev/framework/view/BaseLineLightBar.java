package com.bitants.launcherdev.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.screens.dockbar.LightBarInterface;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.screens.dockbar.LightBarInterface;

/**
 * 仅适用于主屏幕指示灯
 */
public class BaseLineLightBar extends View implements LightBarInterface {
	private int lineWidth,width,height, hlLineWidth, lineHeight, hlLineHeight, hlLineTop, hlLineLeft, lineTop;
	private int size = 1;
	private int searchHeight, searchWidth;
	private float distanceScale;
	private Drawable line, hlLine,search;
	private View v;
	/**
	 * 是否显示导航屏（第0屏）
	 */
	private boolean isShownNavScreen;

	private int scrollLength;
	
	private boolean isMoving = false;
	private Rect rect;
	/**
	 * 搜索图标和指示灯线的间距
	 */
	private int paddding = -1;
	
	private BaseLauncher launcher;
	
	private boolean isLineBar;// 是否是圆形指示灯
	protected int currentPos;// 当前选中的屏幕
	private int allWidth;// 圆形指示灯的总长度
	
	public void setLauncher(BaseLauncher launcher){
		this.launcher = launcher;
	}
	public BaseLineLightBar(Context context) {
		super(context);
	}

	public BaseLineLightBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseLineLightBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public boolean isShownNavScreen() {
		return isShownNavScreen;
	}
	
	public void setShownNavScreen(boolean isShownNavScreen) {
		this.isShownNavScreen = isShownNavScreen;
	}
	
	/**
	 * 重置是否显示导航屏
	 */
	public void resetIsShownNavScreen(){
		isShownNavScreen =  BaseSettingsPreference.getInstance().isShowNavigationView();
		reInit();
		scrollHighLight(scrollLength);
	}
	
	private void reInit() {
		if (isLineBar) {
			if (isShownNavScreen) {
				lineWidth = (int) (width - searchWidth - paddding);
			} else {
				lineWidth = width;
			}
			hlLineWidth = lineWidth / size;
			lineTop = (height - lineHeight) / 2;
			hlLineTop = (height - hlLineHeight) / 2;

			if (isShownNavScreen) {
				line.setBounds(searchWidth + paddding, lineTop, searchWidth + paddding + lineWidth, lineTop + lineHeight);
			} else {
				line.setBounds(0, lineTop, lineWidth, lineTop + lineHeight);
			}
		} else {
			allWidth = lineWidth * size + paddding * (size - 1);
		}
	}

//	@Override
//	protected void onAttachedToWindow() {
//		super.onAttachedToWindow();
//		ThemeUIRefreshAssit.getInstance().registerRefreshListener(this);
//	}
//
//	@Override
//	protected void onDetachedFromWindow() {
//		super.onDetachedFromWindow();
//		ThemeUIRefreshAssit.getInstance().unregisterRefreshListener(this);
//	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		reInit();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (isLineBar) {
			if (isShownNavScreen) {
				rect.set(0, 0, searchWidth, searchHeight);
				search.setBounds(rect);
				search.draw(canvas);
			}
			line.draw(canvas);
			hlLine.draw(canvas);
			return;
		}
		// 圆形指示灯add by youy 2014.3.5
		int left = (width - allWidth) / 2;
		int top = (height - lineHeight) / 2;
		if (isShownNavScreen) {
			rect.set(left - searchWidth - paddding, (height - searchHeight) / 2, left - paddding, (height + searchHeight) / 2);
			search.setBounds(rect);
			search.draw(canvas);
		}
		for (int i = 0; i < size; i++) {
			Drawable d = line;
			int posWidth = lineWidth;
			int posHeight = lineHeight;
			if (currentPos == i + 1) {
				d = hlLine;
				posWidth = hlLineWidth;
				posHeight = hlLineHeight;
			}
			d.setBounds(left, top, left + posWidth, top + posHeight);
			d.draw(canvas);
			left += posWidth + paddding;
		}

	}
	
	/**
	 * 设置指示数量
	 * @param size 数量
	 */
	public void setSize(int size) {
		if(size <= 0)
			return;
		this.size = size;
		distanceScale = 1.0f / size;
		if (isLineBar) {
			hlLineWidth = lineWidth / size;
		} else {
			allWidth = lineWidth * size + paddding * (size - 1);
		}
	}
	
	/**
	 * 设置关联的view
	 * @param v
	 */
	public void setLinkedView(View v) {
		this.v = v;
	}
	
	/**
	 * 滚动指示灯
	 * @param scrollX 滚动距离
	 */
	public void scrollHighLight(int scrollX) {
		scrollLength = scrollX;
		if (v != null && v.getMeasuredWidth() != 0) {
			if (isLineBar) {
				hlLineLeft = (int) (scrollX * ((float) lineWidth / v.getMeasuredWidth()) * distanceScale);
				if (isShownNavScreen) {
					hlLine.setBounds(hlLineLeft + searchWidth + paddding, hlLineTop, hlLineLeft + searchWidth + paddding + hlLineWidth, hlLineTop + hlLineHeight);
				} else {
					hlLine.setBounds(hlLineLeft, hlLineTop, hlLineLeft + hlLineWidth, hlLineTop + hlLineHeight);
				}
			} else {
				currentPos = (int) scrollX / width + 1;
			}
			invalidate();
		}
	}

//	/**
//	 * 应用主题皮肤
//	 * @see ThemeUIRefreshListener#applyTheme()
//	 */
//	@Override
//	public void applyTheme() {
//		line = ThemeManager.getThemeDrawable(ThemeData.LAUNCHER_LIGHT_LINE);
//		hlLine = ThemeCompatibleResAssit.getLauncherLightHl(getContext());
//		lineHeight = line.getIntrinsicHeight();
//		hlLineHeight = hlLine.getIntrinsicHeight();
//		if (v != null && v.getMeasuredWidth() != 0) {
//			hlLineLeft = (int) (scrollLength * ((float) lineWidth / v.getMeasuredWidth()) * distanceScale);
//		}
//		if(isShownNavScreen){
//			line.setBounds(searchWidth, lineTop, lineWidth, lineTop + lineHeight);
//			hlLine.setBounds(hlLineLeft + searchWidth + paddding, hlLineTop, hlLineLeft + searchWidth + paddding + hlLineWidth, hlLineTop + hlLineHeight);
//		}else{
//			line.setBounds(0, lineTop, lineWidth, lineTop + lineHeight);
//			hlLine.setBounds(hlLineLeft, hlLineTop, hlLineLeft + hlLineWidth, hlLineTop + hlLineHeight);
//		}
//	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		if(!isShownNavScreen){
			return false;
		}
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			isMoving = false;
			break;
		case MotionEvent.ACTION_MOVE:	
			isMoving = true;
			break;
		case MotionEvent.ACTION_UP:
			isMoving = false;
			if(rect.contains(x,y)){
				launcher.getWorkspaceLayer().snapToZeroView();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			isMoving = false;
			break;
		}
		/**
		 * 如果点击下去并且移动 不触发点击事情  由于 事件没有消费完成 return false 接下来的move up事件将会传递给上一层处理
		 * 点击并且没有移动 触发点击事件 事件消费完成 return true  TouchEvent传递结束
		 */
		return !isMoving;
	}
	
	public void setSearchIconDrawable(Drawable d){
		if(d == null)
			return;
		search = d;
		searchWidth = search.getIntrinsicWidth();
		searchHeight = search.getIntrinsicHeight();
		rect = new Rect(0,0,searchWidth,searchHeight);
		if(paddding < 0){			
			paddding = (int)(searchWidth*0.35f);	
		}
	}
	
	public void setPaddding(int paddding) {
		this.paddding = paddding;
	}
	
	public void setLineAndHlLineDrawable(Drawable lineDrawable, Drawable hlLineDrawable){
		if(null == lineDrawable || null == hlLineDrawable) return;
		line = lineDrawable;
		hlLine = hlLineDrawable;
		lineHeight = line.getIntrinsicHeight();
		hlLineHeight = hlLine.getIntrinsicHeight();
		if (v != null && v.getMeasuredWidth() != 0) {
			hlLineLeft = (int) (scrollLength * ((float) lineWidth / v.getMeasuredWidth()) * distanceScale);
		}
		if(isShownNavScreen()){
			line.setBounds(searchWidth, lineTop, lineWidth, lineTop + lineHeight);
			hlLine.setBounds(hlLineLeft + searchWidth + paddding, hlLineTop, hlLineLeft + searchWidth + paddding + hlLineWidth, hlLineTop + hlLineHeight);
		}else{
			line.setBounds(0, lineTop, lineWidth, lineTop + lineHeight);
			hlLine.setBounds(hlLineLeft, hlLineTop, hlLineLeft + hlLineWidth, hlLineTop + hlLineHeight);
		}
	}
	
	public void setNotLineAndHlLineDrawable(Drawable lineDrawable, Drawable hlLineDrawable){
		if(null == lineDrawable || null == hlLineDrawable) return;
		line = lineDrawable;
		hlLine = hlLineDrawable;
		hlLineWidth = hlLine.getIntrinsicWidth();
		hlLineHeight = hlLine.getIntrinsicHeight();
		lineWidth = line.getIntrinsicWidth();
		lineHeight = line.getIntrinsicHeight();
	}
	
	public boolean isLineBar() {
		return isLineBar;
	}
	
	public void setLineBar(boolean isLineBar) {
		this.isLineBar = isLineBar;
	}
}
