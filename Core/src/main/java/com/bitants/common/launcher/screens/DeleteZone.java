package com.bitants.common.launcher.screens;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.RectF;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.kitset.util.StatusBarUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.config.LauncherConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.model.BaseLauncherSettings;
import com.bitants.common.launcher.touch.BaseDragController;
import com.bitants.common.launcher.view.icon.ui.util.IconTypeFactoryManager;
import com.bitants.common.kitset.util.SystemUtil;
import com.bitants.common.R;
import com.bitants.common.launcher.view.BaseDeleteZoneTextView;

/**
 * 可配置布局 如只有卸载 、背景可变换
 */
public class DeleteZone extends LinearLayout {
	private static final int ANIMATION_DURATION = 200;

	private final int[] mLocation = new int[2];
	public static final int DELETE_ZONE = 1;
	public static final int UNINSTALL_ZONE = 0;
	
	public BaseDeleteZoneTextView im_delete, im_uninstall;
	
	private BaseLauncher mLauncher;
	private boolean mTrashMode;

	private AnimationSet mInAnimation;
	private AnimationSet mOutAnimation;
	private Animation mHandleInAnimation;
	private Animation mHandleOutAnimation;

	private BaseDragController mDragController;

	private final RectF mRegion = new RectF();
	
	//显示卸载区
	private boolean isShowUninstall = false;
	//显示删除区
	private boolean isShowDelete = false;

	public DeleteZone(Context context) {
		super(context);
	}

	public DeleteZone(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setShowUninstall(boolean isNotShowUninstall) {
		this.isShowUninstall = isNotShowUninstall;
	}

	public void setShowDelete(boolean isNotShowDelete) {
		this.isShowDelete = isNotShowDelete;
	}

	public void reset(){
		isShowUninstall = false;
		isShowDelete = false;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		if(TelephoneUtil.isMeizuPhone42()){//防止隐藏底部Menu区，产生整体布局下移
			post(new Runnable(){
				@Override
				public void run() {
					getLayoutParams().height += ScreenUtil.dip2px(getContext(), 25);
					invalidate();
				}
			});
		}
	}
	
	public void show(Object info) {
		final ItemInfo item = (ItemInfo) info;

		/**
		 * 不允许删除【应用列表】快捷方式
		 */
		if (mLauncher.getScreenViewGroup().isAllAppsIndependence(item))
			return;

		if (item != null && !mLauncher.isPreviewMode()) {
			im_uninstall.setNotDragEnterState();
			im_delete.setNotDragEnterState();
			showDeleteZone(item);
			//使得拖拽过程中优先响应删除区
			mDragController.removeDropTarget(im_delete);
			mDragController.removeDropTarget(im_uninstall);
			mDragController.addDropTarget(im_uninstall);
			mDragController.addDropTarget(im_delete);
		}
	}
	
	public void hide() {
		if (this.getVisibility() == VISIBLE && mTrashMode) {
			mTrashMode = false;
			mDragController.setDeleteRegion(null);
			startAnimation(mOutAnimation);
			setVisibility(GONE);
		}
		
		if(!TelephoneUtil.isMeizuPhone42()){			
			StatusBarUtil.toggleStateBar(mLauncher, true);
		}
	}

	/**
	 * 是否不显示删除区
	 * @return
	 */
	private boolean isNotShowDelete(ItemInfo item) {
		if(isShowDelete)
			return false;
		
		if (BaseLauncher.hasDrawer){//如果有程序匣子，在桌面上可以显示，匣子里不显示删除区
			if(mLauncher.isAllAppsVisible()){
				return true;
			}else{
				return false;
			}
		}else{//没有程序匣子
			if(item instanceof FolderInfo)
				return true;
			
			if(item instanceof ApplicationInfo){
				if(LauncherConfig.getLauncherHelper().isMyPhoneItem(item))//桌面MyPhone类型
					return false;
				if(item.itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT)//桌面自带的快捷
					return false;
				if(isUninstalledApp(item))
					return false;
				if(item.itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_SHORTCUT 
						&& !IconTypeFactoryManager.isDockBarFourIcon((ApplicationInfo) item))//非dock栏快捷
					return false;
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 不显示卸载 <br>
	 */
	private boolean isNotShowUninstall(ItemInfo item) {
		if(isShowUninstall)
			return false;
		
		if (LauncherConfig.getLauncherHelper().isMyPhoneItem(item))
			return true;
		
		if(isUninstalledApp(item))
			return true;

		if(isSystemApp(item))
			return true;
		
		if(isLauncherItself(item))
			return true;
		
		if(isNoComponentApp(item))
			return true;
		
		return false;
	}
	
	private boolean isUninstalledApp(ItemInfo item){
		if (!(item instanceof ApplicationInfo))
			return true;
		
		ApplicationInfo app = (ApplicationInfo) item;
		if (app.intent == null)
			return true;

		ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(app.intent, getContext().getPackageManager());
		if (null == resolve || null == resolve.activityInfo || null == resolve.activityInfo.applicationInfo)
			return true;
		
		return false;
	}
	
	private boolean isLauncherItself(ItemInfo item){
		if (!(item instanceof ApplicationInfo))
			return false;
		
		ApplicationInfo app = (ApplicationInfo) item;
		if (app.intent == null || app.componentName == null)
			return false;
		
		return getContext().getPackageName().equals(app.componentName.getPackageName()) ||
				(app.intent.getComponent() != null && getContext().getPackageName().equals(app.intent.getComponent().getPackageName()));
	}
	
	private boolean isNoComponentApp(ItemInfo item){
		if (!(item instanceof ApplicationInfo))
			return false;
		ApplicationInfo app = (ApplicationInfo) item;
		
		return app.componentName == null;
	}
	
	private boolean isSystemApp(ItemInfo item){
		if (!(item instanceof ApplicationInfo))
			return true;
		
		ApplicationInfo app = (ApplicationInfo) item;
		if (app.intent == null)
			return true;

		ResolveInfo resolve = AndroidPackageUtils.getResolveInfo(app.intent, getContext().getPackageManager());
		if (null == resolve || null == resolve.activityInfo || null == resolve.activityInfo.applicationInfo)
			return false;
		return SystemUtil.isSystemApplication(resolve.activityInfo.applicationInfo.flags);
	}

	//modify by linqiang 桌面动态文件夹内图标长按后不显示垃圾桶,拖拽出来后显示垃圾桶
	private void showDeleteZone(Object info){
		if(this.getVisibility() != VISIBLE){
			final ItemInfo item = (ItemInfo) info;
			if(isNotShowDelete(item) && isNotShowUninstall(item)){//如果删除与卸载都不显示
				return;
			}
			mTrashMode = true;
			final int[] location = mLocation;
			getLocationOnScreen(location);
			mRegion.set(location[0], location[1], location[0] + getRight() - getLeft(), location[1] + getBottom() - getTop());
			mDragController.setDeleteRegion(mRegion);
			if (mInAnimation == null)
				createAnimations();
			
			startAnimation(mInAnimation);
			setVisibility(VISIBLE);
			if (isNotShowUninstall(item))
				im_uninstall.setVisibility(View.GONE);
			else
				im_uninstall.setVisibility(View.VISIBLE);
			
			if(isNotShowDelete(item)){
				im_delete.setVisibility(View.GONE);
			}else{
				im_delete.setVisibility(View.VISIBLE);
			}
			
			if(!TelephoneUtil.isMeizuPhone42()){						
				StatusBarUtil.toggleStateBar(mLauncher, false);
			}
		}
	}

	private void createAnimations() {
		// 出现,从上到下,透明度与位置变化
		if (mInAnimation == null) {
			mInAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mInAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
			animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
			animationSet.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f));
			animationSet.setDuration(ANIMATION_DURATION);
		}
		if (mHandleInAnimation == null) {
			mHandleInAnimation = new AlphaAnimation(1.0f, 0.0f);
			mHandleInAnimation.setDuration(ANIMATION_DURATION);
		}
		// 消失,从下到上,透明度与位置变化
		if (mOutAnimation == null) {
			mOutAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mOutAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
			animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
			animationSet.addAnimation(new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f));
			animationSet.setDuration(ANIMATION_DURATION);
		}
		if (mHandleOutAnimation == null) {
			mHandleOutAnimation = new AlphaAnimation(0.0f, 1.0f);
			mHandleOutAnimation.setFillAfter(true);
			mHandleOutAnimation.setDuration(ANIMATION_DURATION);
		}
	}

	public void setLauncher(BaseLauncher launcher) {
		mLauncher = launcher;

		im_delete = mLauncher.getDeleteZoneTextView();
		im_uninstall = mLauncher.getUninstallZoneTextView();

		im_delete.setmType(DeleteZone.DELETE_ZONE);
		im_uninstall.setmType(DeleteZone.UNINSTALL_ZONE);
		
		im_delete.setTransitionDrawable((TransitionDrawable) this.getResources().getDrawable(R.drawable.delete_zone_selector));
		im_uninstall.setTransitionDrawable((TransitionDrawable) this.getResources().getDrawable(R.drawable.uninstall_zone_selector));
		
		im_delete.setLauncher(launcher);
		im_uninstall.setLauncher(launcher);
	}

	public void setDragController(BaseDragController dragController) {
		mDragController = dragController;
//		dragController.addDropTarget(im_uninstall);
		im_uninstall.setDragController(dragController);
		dragController.setUninstallZoneTextView(im_uninstall);
		
//		dragController.addDropTarget(im_delete);
		im_delete.setDragController(dragController);
		dragController.setDeleteZoneTextView(im_delete);
		
	}

	private static class FastAnimationSet extends AnimationSet {
		FastAnimationSet() {
			super(false);
		}

		@Override
		public boolean willChangeTransformationMatrix() {
			return true;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}

	//hjiang 不可见时去掉背景
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		
		if (changedView == this)	{
			if (visibility == View.VISIBLE) {
				setBackgroundResource(R.drawable.delete_zone_bg);
			} else {
				setBackgroundResource(0);
			}
		}
	}
	
	public BaseDeleteZoneTextView getDeleteTextView() {
		return im_delete;
	}

	public BaseDeleteZoneTextView getUninstallTextView() {
		return im_uninstall;
	}
}
