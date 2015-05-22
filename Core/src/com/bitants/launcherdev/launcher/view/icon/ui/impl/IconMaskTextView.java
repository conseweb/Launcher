package com.bitants.launcherdev.launcher.view.icon.ui.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.FolderAni;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.framework.AnyCallbacks.OnDragEventCallback;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.preference.BaseSettingsPreference;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.FolderAni;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.launcher.view.DragView;
import com.bitants.launcherdev.launcher.view.icon.ui.FolderAni;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconView;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStrategy;

import java.lang.ref.WeakReference;

/**
 * 桌面专用应用程序图标，其他情况请使用 <br>
 */
public class IconMaskTextView extends LauncherIconView implements OnDragEventCallback {
	/**
	 * 合成文件夹动画
	 */
	private FolderAni folderAni;

	/**
	 * 重新获取图标次数
	 */
	private int reacquireCount = 0;
	

	/**
	 * 是否支持合并文件夹，目前仅用于控制最近安装与最近打开
	 */
	private boolean folderAvailable = true;
	private boolean folderNotAvailableHint = false;

	/**
	 * 是否被点击
	 */
	boolean isOnTouchScaleState = false;

	/**
	 * 点击态实现
	 * 
	 * */
	private ClickStateShow mClickStateShow;
	
	//相关的消息View
	private WeakReference<View> notificationViewReference;
	
	private Runnable refreshRunnable = new Runnable() {
		@Override
		public void run() {
			if (icon == null) {
				refreshUI();
				reacquireCount++;
			}
			invalidate();
		}
	};
	
	public IconMaskTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IconMaskTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IconMaskTextView(Context context) {
		super(context);
	}

	/**
	 * 初始化参数
	 */
	protected void initParams(AttributeSet attrs) {
		initParamsDefault(attrs);
		folderAni = new FolderAni(getContext(), data, config);
		mClickStateShow = new ClickStateShow();
	}


	/**
	 * 改变数据 并重新设置各个绘画策略所需数据
	 * 
	 * @param w
	 * @param h
	 */
	@Override
	protected void initValue(int w, int h) {
		data.updateData(w, h);
		if (BaseConfig.isOnScene()) {
			LauncherConfig.getLauncherHelper().initSceneWH(this, config, true);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/**
		 * 图标丢失修复机制
		 */
		if (icon == null) {
			if (reacquireCount < 3) {
				handler.postDelayed(refreshRunnable, 500);
			}
			return;
		} else {
			reacquireCount = 0;
		}
		if (isOnTouchScaleState) {
			isOnTouchScaleState = false;
			mClickStateShow.draw(canvas, getWidth()/2, getWidth()/2);
		}
		if (iconType != null) {
			iconType.ajustConfig(getContext(), appInfo, config);
		}
		if(appInfo != null){
			config.setCustomIcon(appInfo.customIcon);
		}
		SparseArray<DrawStrategy> containter = config.getContainter();
		Rect iconRect = data.getIconRect(config);
		Rect maskRect = data.getMaskRect(config);
		boolean isLargeIconMode = config.isLargeIconMode();
		boolean isDefaultTheme = BaseIconCache.isDefaultThemeWithDefaultModuleId(getContext());
		int restoreCount = -1;
		config.setDrawNotMergeFoler(folderNotAvailableHint);
		for(int i = 0; i < containter.size(); i++) {
			  int key = containter.keyAt(i); 
			  DrawStrategy drawStrategy = containter.get(key);
			  drawStrategy.draw(canvas, config, data, iconRect, maskRect, isLargeIconMode, isDefaultTheme);
			  if(key == DrawPriority.Prepare.ordinal()){//画文件夹合并动画
				  restoreCount = drawingAni(canvas);
			  }
		}
		if(restoreCount != -1){
			canvas.restoreToCount(restoreCount);
		}
		if (iconType != null) {
			iconType.drawCanvas(getContext(), appInfo, canvas, this, config, data);
		}
		
	}

	/**
	 * 合成文件夹动画
	 */
	private int drawingAni(Canvas canvas) {
		if (folderAni.isFolderEnterAni() || folderAni.isFolderExitAni()) {
			long aniDiffTime = System.currentTimeMillis() - folderAni.getAniBeginTime();
			folderAni.setAniDiffTime(aniDiffTime);
			if (aniDiffTime >= BaseConfig.ANI_255) {
				if (folderAni.timeExceed()) {
					return -1;
				}
			} else if (folderAni.isFolderEnterAni()) {
				folderAni.folerEnterAni();
				invalidate();
			} else if (folderAni.isFolderExitAni()) {
				folderAni.folderExitAni();
				invalidate();
			}
			if ((getScrollX() | getScrollY()) == 0) {
				folderAni.getmAnimationBackground().draw(canvas);
				return -1;
			} else {
				canvas.translate(getScrollX(), getScrollY());
				folderAni.getmAnimationBackground().draw(canvas);
				canvas.translate(-getScrollX(), -getScrollY());
				return -1;
			}
		}
		return -1;
	}

	private boolean notAllowMergeFolder(View v){
		if(!folderAvailable)
			return true;
		if(v == null || BaseConfig.getBaseLauncher() == null)
			return false;
		
		return !(v.getTag() instanceof ApplicationInfo) || BaseConfig.getBaseLauncher().mWorkspace.isAllAppsIndependence((ItemInfo) v.getTag());
	}
	
	@Override
	public void onDropAni(DragView view) {
		data.iconPaint.setAlpha(255);
		config.setDrawText(true);
		folderAni.setFolderEnterAni(false);
		folderAni.setFolderExitAni(false);
		data.setAni(false);
		invalidate();
	}

	@Override
	public void onEnterAni(DragView view) {
		if (this.getVisibility() != View.VISIBLE)
			return;
		if (notAllowMergeFolder(view.getDragingView())){
			onNotAllowDragEnter();
			return;
		}

		data.iconPaint.setAlpha(55);
		folderAni.setFolderEnterAni(true);
		folderAni.setFolderExitAni(false);
		view.update(DragView.MODE_MIN);
		folderAni.setAniBeginTime(System.currentTimeMillis());
		invalidate();
	}

	@Override
	public void onExitAni(DragView view) {
		if (this.getVisibility() != View.VISIBLE)
			return;
		if (notAllowMergeFolder(view.getDragingView())){
			onNotAllowDragExit();
			return;
		}

		data.iconPaint.setAlpha(255);
		config.setDrawTextBackgroundForce(BaseSettingsPreference.getInstance().isShowTitleBackaground());
		folderAni.setFolderEnterAni(false);
		folderAni.setFolderExitAni(true);
		if(view != null){			
			view.update(DragView.MODE_NORMAL);
		}
		folderAni.setAniBeginTime(System.currentTimeMillis());
		data.isAni = false;
		invalidate();
	}

	@Override
	public void udpateIconConfig() {
		config.setDrawTextBackground(BaseSettingsPreference.getInstance().isShowTitleBackaground());
		initValue(this.getWidth(), this.getHeight());
		folderAni.updateAnimationBg(getContext());
		this.invalidate();
	}

	@Override
	public void updateNewMaskConfig() {
		/*if (appInfo == null || !LauncherNewMaskHelper.getNewInstance().shouldDrawNewMask(getContext(), appInfo)) {
			config.setDrawNewMask(false);
			this.invalidate();
		}*/
	}

	@Override
	public void updateHintConfig(int hintCount) {
		if (hintCount > 0) {
			config.setDrawHint(true);
			data.mHint = hintCount;
		} else {
			config.setDrawHint(false);
		}
		invalidate();
	}

	@Override
	public void updateDraw() {
		this.invalidate();
	}

	@Override
	public void updateText() {
		super.updateText();
		setText(savedText);
		initValue(this.getWidth(), this.getHeight());
		invalidate();
	}

	public void setFolderAvailable(boolean folderAvailable) {
		this.folderAvailable = folderAvailable;
	}

	public boolean isOnMergeFolderAni() {
		return folderAni.isOnMergeFolderAni();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isLocked = LauncherConfig.getLauncherHelper().isLockedView(this);
		if (!isLocked) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isOnTouchScaleState = true;
				this.invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isOnTouchScaleState = false;
				this.invalidate();
				break;
			case MotionEvent.ACTION_CANCEL:
				isOnTouchScaleState = false;
				this.invalidate();
				break;
			default:
				break;

			}
		}
		return super.onTouchEvent(event);
	}


	@Override
	protected LauncherIconData createIconMaskData(AttributeSet attrs) {
		return new LauncherIconData(getContext());
	}

    public WeakReference<View> getNotificationViewReference() {
        return notificationViewReference;
    }

    public void setNotificationViewReference(WeakReference<View> notificationViewReference) {
        this.notificationViewReference = notificationViewReference;
    }
    
	public void onNotAllowDragEnter() {
		folderNotAvailableHint = true;
		invalidate();
	}

	public void onNotAllowDragExit() {
		folderNotAvailableHint = false;
		invalidate();
	}
}