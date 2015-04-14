package com.nd.hilauncherdev.launcher.screens;


import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.launcher.BaseLauncher;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.theme.assit.ThemeUIRefreshAssit;
import com.nd.hilauncherdev.theme.assit.ThemeUIRefreshListener;

/**
 * 桌面右键菜单
 * 
 * @author youy
 * 
 */
public class BaseLauncherMenu implements ThemeUIRefreshListener{
	public PopupWindow menuWindow;
	public BaseLauncher context;
	public LayoutInflater inflater;
	public View layout;

	private boolean isFirst = true;


	public BaseLauncherMenu(BaseLauncher context) {
		this.context = context;
		init();
	}

	private void init() {
		// 整个区域
		inflater = LayoutInflater.from(context);
		layout = context.inflateLauncherMenu();
		if(layout == null)
			return;
		menuWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		menuWindow.setHeight(context.getResources().getDimensionPixelSize(R.dimen.bottommenu_height));
		//menuWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_bg));
		applyTheme();
		ThemeUIRefreshAssit.getInstance().registerRefreshListener(this);
		menuWindow.setAnimationStyle(R.style.menuAnimation);
		menuWindow.setOutsideTouchable(true);
		menuWindow.setFocusable(true);
		menuWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				hideTopShadow();
			}
		});

		layout.setFocusableInTouchMode(true);
		
		setContent();
	}
	
	public boolean throughFirewall(int position)
	{
		boolean r=false;
		switch (position) {
		case 0:
		case 1:
		case 2:
		case 3:
			//点击 添加、壁纸、主题、特效四个菜单时，要判断是否处理编辑锁定模式
			r=BaseConfig.allowEdit(context);
			break;
		default:
			r=true;
			break;
		}
		return r;
		
	}

	public void updateMenu() {
		//ReadMeFactory.maybeShowMenu(context);

		if (!menuWindow.isShowing()) {
			menuWindow.dismiss();
			//menuWindow.setAnimationStyle(R.style.menuAnimation);
			menuWindow.showAtLocation(context.getScreenViewGroup(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			if (menuWindow.isShowing()) {
				showTopShadow();
			}

			if (isFirst) {
				isFirst = false;
				layout.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
						if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_MENU) {
							if (menuWindow.isShowing()) {
								menuWindow.dismiss();
							}
						}
						return false;
					}
				});
			}
		} else {
			menuWindow.dismiss();
		}
	}


	public void fontChange() {
	}

	public void dismiss() {
		menuWindow.dismiss();
	}

	public void hideTopShadow(){
		if (context.getTopShadowView().getVisibility() == View.VISIBLE) {
			context.getTopShadowView().startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out_fast));
			context.getTopShadowView().setVisibility(View.GONE);
		}
	}

	public void showTopShadow(){
		if (context.getTopShadowView().getVisibility() == View.GONE) {
			context.getTopShadowView().setVisibility(View.VISIBLE);
			context.getTopShadowView().startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_slow));
		}
	}
	
	public void setContent(){
		
	}
	
	@Override
	public void applyTheme() {
	}
}
