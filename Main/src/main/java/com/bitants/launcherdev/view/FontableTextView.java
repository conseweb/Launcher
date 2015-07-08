package com.bitants.launcherdev.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.bitants.common.kitset.util.PaintUtils2;
import com.bitants.common.launcher.broadcast.AntBroadcastReceiver;
import com.bitants.common.launcher.config.preference.SettingsConstants;

/**
 * <p>类说明：  支持从配置中读取字体的TextView</p>
 * @version 1.0
 */
public class FontableTextView extends TextView {
	
	private RefreshFontReceiver mRefreshFontReceiver;

	public FontableTextView(Context context) {
		super(context);
		setTypeface();
	}

	public FontableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface();
	}

	public FontableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface();
	}
	
	private void setTypeface() {
		Typeface tf = PaintUtils2.getTypeFace();
		setTypeface(tf);
		
		int defHei = getFontMesureHeight(getTextSize());
		int curHei = getFontMesureHeight(getPaint());
		
		int textHei = curHei < defHei ? defHei : curHei;
		setHeight(textHei + 17);
	}
	
	private int getFontMesureHeight(float fontSize) {
		Paint p = new Paint();
		
		p.setTextSize(fontSize);
		return getFontMesureHeight(p);
	}
	
	private int getFontMesureHeight(Paint p) {
//		FontMetrics fm = p.getFontMetrics();  
////		return (int) Math.ceil(fm.descent - fm.ascent);
//		return (int) Math.ceil(fm.descent - fm.top) + 12;  
		
		return p.getFontMetricsInt(null);
	}
	 
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

        Context mContext = getContext();
		// 注册字体刷新广播过虑器 yuf@2012.11.09
		mRefreshFontReceiver = new RefreshFontReceiver();
		IntentFilter filter = new IntentFilter(SettingsConstants.ACTION_REFRESH_APP_NAME);
		mContext.registerReceiver(mRefreshFontReceiver, filter);
	}
	
	public void setText(CharSequence text, boolean loadTypeface) {
		if (loadTypeface)
			setTypeface();
		
		super.setText(text);
	}
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Context mContext = getContext();
		if (mRefreshFontReceiver != null) {
			mContext.unregisterReceiver(mRefreshFontReceiver);
			
			mRefreshFontReceiver = null;
		}
	}
	
	
	private class RefreshFontReceiver extends AntBroadcastReceiver {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			if (null != intent.getAction() && SettingsConstants
					.ACTION_REFRESH_APP_NAME.equals(intent.getAction()) ) {
				setTypeface();
			}
		}
	}

}
