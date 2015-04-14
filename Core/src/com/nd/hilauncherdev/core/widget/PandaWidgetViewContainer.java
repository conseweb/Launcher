/**
 * 
 */
package com.nd.hilauncherdev.core.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.core.view.HiViewGroup;
import com.nd.hilauncherdev.launcher.config.LauncherConfig;
import com.nd.hilauncherdev.launcher.support.OnWorkspaceScreenListener;

/**
 * 包装处理桌面长按事件
 */
public class PandaWidgetViewContainer extends LinearLayout implements OnWorkspaceScreenListener{
	/** 非标widget的IntentFilter的Category值，供桌面查询非标widget */
	private static final String PANDA_WIDGET_CATEGORY_QUERY_INTENT = "com.nd.android.pandahome.widget.category";
	/** 非标widget的IntentFilter的Category值，供桌面查询非标widget是否要捕获上下划动手势 */
	private static final String PANDA_WIDGET_CATCH_VERTICAL_GESTRUE_CATEGORY_QUERY_INTENT = "com.nd.android.pandahome.widget.CATCH_VERTICAL_GESTURE";

	public View widgetView;
	
	private HiViewGroup ws;
	private String widgetPackage;
	private CheckForLongPress mPendingCheckForLongPress;
	private boolean mHasPerformedLongPress, mInteractionTouch = false;

	private boolean callBackOnWorkspaceScreen = true;//是否允许滑到Workspace当前屏时进行回调
	
	/**
	 * A constructor for this class.
	 */
	public PandaWidgetViewContainer(Context context, View view) {
		super(context);
		this.addView(view);

		widgetView = view;
	}

	public View getWidgetView() {
		return widgetView;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		checkIsCatchVerticalGesture();
	}

	/**
	 * 检测该非标小部件，是否要捕捉上下划动的手势
	 */
	private void checkIsCatchVerticalGesture() {
		if (widgetPackage == null)
			return;
		
		if (widgetPackage.equals(getContext().getPackageName()))
			return;
		
		
		PackageManager pm = getContext().getPackageManager();
		try {
			Intent intent = new Intent(PANDA_WIDGET_CATEGORY_QUERY_INTENT);
			intent.addCategory(PANDA_WIDGET_CATCH_VERTICAL_GESTRUE_CATEGORY_QUERY_INTENT);
			intent.setPackage(widgetPackage);
			List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
			if (list != null && list.size() > 0)
				mInteractionTouch = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理长击时候事件传递问题。
	 */
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mInteractionTouch && ws != null) {
			ws.setIsChildCatchTouchEvent(true);
			return super.onInterceptTouchEvent(ev);
		}

		if (mHasPerformedLongPress) {
			mHasPerformedLongPress = false;
			return true;
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			postCheckForLongClick();
			break;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mHasPerformedLongPress = false;
			if (mPendingCheckForLongPress != null) {
				removeCallbacks(mPendingCheckForLongPress);
			}
			break;
		}
		return false;
	}

	class CheckForLongPress implements Runnable {
		private int mOriginalWindowAttachCount;

		public void run() {
			if ((getParent() != null) && hasWindowFocus() && mOriginalWindowAttachCount == getWindowAttachCount() && !mHasPerformedLongPress) {
				if (performLongClick()) {
					mHasPerformedLongPress = true;
				}
			}
		}

		public void rememberWindowAttachCount() {
			mOriginalWindowAttachCount = getWindowAttachCount();
		}
	}

	private void postCheckForLongClick() {
		mHasPerformedLongPress = false;

		if (mPendingCheckForLongPress == null) {
			mPendingCheckForLongPress = new CheckForLongPress();
		}
		mPendingCheckForLongPress.rememberWindowAttachCount();
		postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
	}

	@Override
	public void cancelLongPress() {
		super.cancelLongPress();

		mHasPerformedLongPress = false;
		if (mPendingCheckForLongPress != null) {
			removeCallbacks(mPendingCheckForLongPress);
		}
	}

	public void setWidgetPackage(String widgetPackage) {
		this.widgetPackage = widgetPackage;
	}
	
	public String getWidgetPackage() {
		return widgetPackage;
	}
	
	public void setHiViewGroup(HiViewGroup hi) {
		this.ws = hi;
	}

	public void setInteractionTouch(boolean mInteractionTouch) {
		this.mInteractionTouch = mInteractionTouch;
	}

	@Override
	public void onWorkspaceCurrentScreen() {
		if(callBackOnWorkspaceScreen){			
			callBackOnWorkspaceScreen = LauncherConfig.getLauncherHelper().onWorkspaceCurrentScreen(this);
		}
	}
}
