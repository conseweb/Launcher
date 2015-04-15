package com.bitants.launcherdev.launcher.view.icon.ui.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.view.icon.ui.DockbarCellData;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;

/**
 * @author pdw
 * @date 2012-5-30 下午03:48:12 <br>
 * representting a app in dockbar
 */
public class DockbarCell extends LauncherIconView {
	/**
	 * 重新获取图标次数
	 */
	private int reacquireCount = 0;
	
	/**
	 * 是否被点击*/
	boolean isOnTouchScaleState=false;
	
	/**点击态实现
	 * 
	 * */
	private ClickStateShow mClickStateShow;

	private Runnable refreshUIRunnable = new Runnable() {
		@Override
		public void run() {
			if (icon == null) {
				refreshUI();
				reacquireCount++;
			}
			invalidate();
		}
	};
	
	public DockbarCell(Context context) {
		super(context);
	}
	
	/**
	 * 初始化参数
	 * @author Michael
	 * @createtime 2013-7-30
	 */
	@Override
	protected void initParams(AttributeSet attr){
		initParamsDefault(attr);
		mClickStateShow=new ClickStateShow();
	}
	
	/**
	 * 改变数据 并重新设置各个绘画策略所需数据
	 * @author Michael
	 * @createtime 2013-8-7 
	 * @param w
	 * @param h
	 */
	protected void initValue(int w, int h){
		if(!BaseConfig.isOnScene()){
			if(LauncherConfig.getLauncherHelper().isShowDockbarText()){
				config.setDrawText(true);
				config.setDrawTextBackground(BaseSettingsPreference.getInstance().isShowTitleBackaground());
				data.updateData(w, h);
			}else{
				config.setDrawText(false);
				config.setDrawTextBackground(false);
				((DockbarCellData)data).updateData(w, h, false);
			}
		}else{
			data.updateData(w, h);
			LauncherConfig.getLauncherHelper().initSceneWH(this, config, false);
		}
	}
	
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		/**
		 * 图标丢失修复机制
		 */
		if (icon == null) {
			if (reacquireCount < 3) {
				handler.postDelayed(refreshUIRunnable, 500);
			}
			return;
		} else {
			reacquireCount = 0;
		}
		if(isOnTouchScaleState){
			isOnTouchScaleState = false;
			mClickStateShow.draw(canvas, getWidth()/2, getWidth()/2);
		}
		super.drawCanvas(canvas, config, data);
	}
	
	/**
	 * 标题
	 * @author Michael
	 * @createtime 2013-7-31 
	 * @param text
	 */
	@Override
	public void setText(CharSequence text) {
		if(StringUtil.isEmpty(text))
			return;
		data.setTitle(text);
		savedText = text.toString();
	}


	@Override
	public void updateText() {
		super.updateText();
		setText(savedText);
		initValue(this.getWidth(), this.getHeight());
		invalidate();
	}

	@Override
	public void udpateIconConfig() {
		invalidate();
	}


	@Override
	public void updateNewMaskConfig() {
	}


	@Override
	public void updateHintConfig(int hintCount) {
		if(hintCount > 0){
			config.setDrawHint(true);
			data.mHint = hintCount;
		}else{
			config.setDrawHint(false);
		}
		invalidate();
	}


	@Override
	public void updateDraw() {
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			isOnTouchScaleState=true;
			this.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			isOnTouchScaleState=false;
			this.invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			isOnTouchScaleState=false;
			this.invalidate();
			break;
		default:
			break;
			
		}
		return super.onTouchEvent(event);
	}


	@Override
	protected LauncherIconData createIconMaskData(AttributeSet attrs) {
		return new DockbarCellData(getContext());
	}
	
	@Override
	public void setIconBitmap(Bitmap bitmap) {
		icon = bitmap;
		data.setIcon(bitmap);
	}
	
}
