package com.nd.hilauncherdev.launcher.screens.dockbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.launcher.BaseLauncher;


/**
 *
 * @author Anson
 */
public class MagicDockbarRelativeLayout extends RelativeLayout {
	
	private BaseLauncher launcher;

	public MagicDockbarRelativeLayout(Context context) {
		super(context);
	}
	
	public MagicDockbarRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MagicDockbarRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setLauncher(BaseLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/**
		 * 文件夹处于打开状态则关闭
		 */
		if (launcher.isFolderOpened()) {
			launcher.closeFolder();
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	public void showWithAnimation(){
		Animation showAni = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in);
		showAni.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});
		startAnimation(showAni);
	}
	
	public void hideWithAnimation() {
		Animation hideAni = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out);
		hideAni.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				setVisibility(View.GONE);
			}
		});
		startAnimation(hideAni);
	}
	
//	@Override
//	public void setVisibility(int visibility) {
//		super.setVisibility(visibility);
//		if(visibility != View.VISIBLE || launcher == null)
//			return;
//		launcher.updateViewLayoutOnWindowLevel();
//	}
}
