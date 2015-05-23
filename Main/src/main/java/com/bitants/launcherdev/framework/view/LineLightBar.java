package com.bitants.launcherdev.framework.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.bitants.common.framework.view.BaseLineLightBar;
import com.bitants.launcher.R;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.theme.ThemeManagerFactory;
import com.bitants.common.theme.assit.ThemeUIRefreshAssit;
import com.bitants.common.theme.assit.ThemeUIRefreshListener;
import com.bitants.common.theme.data.BaseThemeData;

/**
 * 仅适用于主屏幕指示灯
 */
public class LineLightBar extends BaseLineLightBar implements ThemeUIRefreshListener {
	
	public LineLightBar(Context context) {
		super(context);
		init(context);
	}

	public LineLightBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LineLightBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		setShownNavScreen(BaseSettingsPreference.getInstance().isShowNavigationView());
		setSearchIconDrawable(context.getResources().getDrawable(R.drawable.launcher_light_navigation));
		applyTheme();		
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ThemeUIRefreshAssit.getInstance().registerRefreshListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		ThemeUIRefreshAssit.getInstance().unregisterRefreshListener(this);
	}
	
	/**
	 * 应用主题皮肤
	 * @see com.bitants.common.theme.assit.ThemeUIRefreshListener#applyTheme()
	 */
	@Override
	public void applyTheme() {
		setLineBar(false);
//		setLineBar(ThemeManagerFactory.getInstance().isLineLight());
//		isLineBar = ThemeManager.isLineLight();
		if (isLineBar()) {
			setLineAndHlLineDrawable(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.LAUNCHER_LIGHT_LINE),
					getLauncherLightHl(getContext()));
		} else {
			setNotLineAndHlLineDrawable(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.LAUNCHER_LIGHT_NORMAL),
					ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.LAUNCHER_LIGHT_SELECTED));
		}
		
	}
	
	public static Drawable getLauncherLightHl(Context ctx) {
		Drawable d = ThemeManagerFactory.getInstance().getCurrentTheme().getDrawableByKey(BaseThemeData.HOME_LIGHT_HL.replace("home", "launcher"), false, false);
		if(null == d){
			d = ctx.getResources().getDrawable(R.drawable.home_light_hl);
		}
		return d;
	}
}
