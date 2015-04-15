package com.nd.launcherdev.folder.model;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.nd.launcherdev.folder.ZscaleAnimation;
import com.nd.launcherdev.folder.model.stylehelper.AbstractFolderStyleHelper;
import com.nd.launcherdev.folder.model.stylehelper.AbstractFolderStyleHelper;
import com.nd.launcherdev.folder.view.FolderView;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.launcher.Launcher;
import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.nd.launcherdev.launcher.view.icon.ui.impl.IconMaskTextView;
import com.nd.launcherdev.folder.ZscaleAnimation;
import com.nd.launcherdev.folder.view.FolderView;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.launcher.Launcher;
import com.nd.launcherdev.launcher.view.icon.ui.impl.IconMaskTextView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.bitants.launcher.R;

public class FullScreenFolderStyleHelper extends AbstractFolderStyleHelper {

	/**
	 * 动画时长
	 */
	private int mAnimTimeOut = 300;

	/**
	 * 底层文件夹内容布局
	 */
	private LinearLayout.LayoutParams lp;
	private int[] mLocationTemp = new int[2];
	private int[] mPivotTemp = new int[2];

	private FolderSwitchController mController;

	/**
	 * 底层文件夹布局视图
	 */
	private FolderView mFolderContentLayout;
	//是否要重新计算
	private boolean reCul;
	private DecelerateInterpolator mInterpolator = new DecelerateInterpolator();
	private ColorDrawable mBackground = new ColorDrawable(Color.parseColor("#000000"));
	
	public FullScreenFolderStyleHelper(Launcher launcher,
			FolderSwitchController controller) {
		super(launcher);
		mController = controller;
		mLauncher = launcher;
		mDragLayer = mLauncher.getDragLayer();
		mBackground.setAlpha(165);
		initFolderView();
	}

	private View mTitleLayout;
//	private View mBottomLayout;
	private void initFolderView() {
		if (mFolderView == null) {
			mFolderView = mDragLayer
					.findViewById(R.id.folder_switch_fullscreen_layout);
			mFolderContentLayout = (FolderView) mFolderView
					.findViewById(R.id.folder_layout);
			mTitleLayout = mFolderContentLayout.findViewById(R.id.title_layout);
//			mBottomLayout = mFolderView.findViewById(R.id.bottom_layout);
		}
	}

	@Override
	public void onOpen() {
		final int height = mController.initFolderContents() + ScreenUtil.dip2px(mLauncher, 20);
		lp = (LinearLayout.LayoutParams) mFolderContentLayout.getLayoutParams();
		int iconSize = mLauncher.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		int cellHeiht = iconSize * 2 ;
		int extraHeight = 52 + 5 ;
		int top = 3 * cellHeiht + (int) (extraHeight * ScreenUtil.getDensity());
		lp.topMargin = getTopMargin(top);
		lp.height = height;
		mController.getFolderHelper().onFolderOpen(FolderIconTextView.FOLDER_STYLE_FULL_SCREEN);
		animationOpenFullscreenStyleFolder();
	}

	@Override
	public void onClose() {
		animationCloseFullscreenStyleFolder();
		if(mFolderContentLayout.isDynamic(mFolderContentLayout.getUserFolderInfo()))
		{
			fullScreenAndFadeInFolderIcon(mController.mClickedView);
		}
//		fullScreenAndFadeInFolderIcon(mController.mClickedView);
//			if(mController.mOpenFolderFrom == FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER){
//				mLauncher.visiableWorkspace();
//			}else{
//				((DrawerMainView)((Launcher)mLauncher).mDrawer).setVisibility(View.VISIBLE);
//			}
	}

	@Override
	public void onCloseWithoutAnimation() {
	}
	
	@Override
	public void onAddApps2Folder() {
		if (mController.getFolderInfo().getSize() <= 0) {
			mController.closeFolderWithoutAnimation(true);
			return;
		}

		final int height = mController.initFolderContents() + ScreenUtil.dip2px(mLauncher, 20);
		mFolderContentLayout.findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
		mFolderContentLayout.findViewById(R.id.line_layout).setVisibility(View.VISIBLE);
		lp = (LinearLayout.LayoutParams) mFolderContentLayout.getLayoutParams();
		int iconSize = mLauncher.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		int cellHeiht = iconSize * 2 ;
		int extraHeight = 52 + 5 ;
		int top = 3 * cellHeiht + (int) (extraHeight * ScreenUtil.getDensity());
		lp.topMargin = getTopMargin(top);
		lp.height = height;
		reCul = true;
		//刷新文件夹内容视图
		mFolderContentLayout.refresh();
	}

	@Override
	public View getFolderView() {
		return mFolderView;
	}

	private int getTopMargin(int contentHeight) {
		int[] loc = mLocationTemp;
		mController.mClickedView.getLocationOnScreen(loc);
		int height = ScreenUtil.getScreenWH()[1];
		mPivotTemp[0] = loc[0] + mController.mClickedView.getWidth() / 2;
		mPivotTemp[1] = loc[1] + mController.mClickedView.getHeight() / 2;
		return (height - contentHeight) / 2;
	}

	/**
	 * 全屏打开文件夹
	 */
	private void animationOpenFullscreenStyleFolder() {
		if (mFolderView == null)
			return;
//		fullscreenStyleFolderAnimation(mFolderView.findViewById(R.id.ani_layout),0,mAnimTimeOut,openAnimListener);
		fullscreenStyleFolderAnimation(0);
	}

	// 打开动画监听器
	private AnimationListener openAnimListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			if (mController.mClickedView != null)
				mController.mClickedView.setClickable(false);
			mFolderView.findViewById(R.id.bg_layout).setBackgroundDrawable(mBackground);
//			if(mController.mOpenFolderFrom != FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER){
//				((DrawerMainView)((Launcher)mLauncher).mDrawer).setVisibility(View.INVISIBLE);
//			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (mController.mClickedView != null)
				mController.mClickedView.setClickable(true);
			
			mFolderContentLayout.findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
			mFolderContentLayout.findViewById(R.id.line_layout).setVisibility(View.VISIBLE);
			// mFolderView.setBackgroundDrawable(mBackground);
		}

	};

	/**
	 * 动画关闭全屏风格文件夹
	 */
	private void animationCloseFullscreenStyleFolder() {
		if (mFolderView == null)
			return;
		
//		fullscreenStyleFolderAnimation(mFolderView.findViewById(R.id.ani_layout),1,mAnimTimeOut,closeAnimListener);
		fullscreenStyleFolderAnimation(1);
//		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
//		alphaAnimation.setDuration(mAnimTimeOut);
//		alphaAnimation.setAnimationListener(closeAnimListener1);
//		mFolderView.findViewById(R.id.bg_layout).startAnimation(alphaAnimation);
	}

	// 关闭动画监听器
	private AnimationListener closeAnimListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
//			AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
//			alphaAnimation.setDuration(mAnimTimeOut);
//			mFolderView.findViewById(R.id.bg_layout).startAnimation(alphaAnimation);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
//			((Launcher)mLauncher).zoomed(0.0f);
			if (mController.getFolderHelper() != null)
				mController.getFolderHelper().onFolderClose(mController.getFolderInfo(), mController.mIsAddApp2Folder);
			if (mFolderView != null){
				mFolderView.setVisibility(View.GONE);
				mFolderView.findViewById(R.id.bg_layout).setBackgroundDrawable(null);
			}
			if(!mFolderContentLayout.isDynamic(mFolderContentLayout.getUserFolderInfo()))
			{
				FolderIconTextView iconTextView = (FolderIconTextView) mController.mClickedView;
				iconTextView.onBeginDrawOutFolderAni();
			}
		}
	};
	// 关闭动画监听器
	private AnimationListener closeAnimListener1 = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
//			fullscreenStyleFolderAnimation(mFolderView.findViewById(R.id.ani_layout),1,mAnimTimeOut,closeAnimListener);
			fullscreenStyleFolderAnimation(1);
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			
		}
	};

	/**
	 * 缩小并淡入图标
	 */
	private void fullScreenAndFadeInFolderIcon(final View v) {
		if (v == null)
			return;
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.05f, 1.0f, 1.05f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(150);
		scaleAnimation.setInterpolator(new AccelerateInterpolator());
		scaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		v.startAnimation(scaleAnimation);
	}
	
	private int offsetY , preInfoCount , destH;
	
	public void fullscreenStyleFolderAnimation(View view, int direct,
			int duration, AnimationListener listener) {
		int boderW = ScreenUtil.dip2px(mLauncher, 40);
		int topBoder = ScreenUtil.dip2px(mLauncher, 45) + lp.topMargin;
		int bottomBoder;
		Rect destRect = new Rect();
		Rect iconRect = null;
		if(!mFolderContentLayout.isDynamic(mFolderContentLayout.getUserFolderInfo())){
			FolderIconTextView iconTextView = (FolderIconTextView) mController.mClickedView;
			iconRect = iconTextView.getIconRect();
			int[] loc = new int[2];
			iconTextView.getLocationOnScreen(loc);
			destRect.left = loc[0] + iconRect.left;
			destRect.top = loc[1] + iconRect.top;
//			destRect.left = iconTextView.getLeft() + iconRect.left;
//			destRect.top = iconTextView.getTop() + iconRect.top;
		}else{
			IconMaskTextView iconTextView = (IconMaskTextView) mController.mClickedView;
			iconRect = iconTextView.getIconRect();
			int[] loc = new int[2];
			iconTextView.getLocationOnScreen(loc);
			destRect.left = loc[0] + iconRect.left;
			destRect.top = loc[1] + iconRect.top;
//			destRect.left = iconTextView.getLeft() + iconRect.left;
//			destRect.top = iconTextView.getTop() + iconRect.top;
		}
		
		destRect.right = destRect.left + iconRect.width();
		
		if(direct == 0){
			reCul = false;
			preInfoCount = mController.getFolderInfo().contents.size();
			destH = iconRect.height() + ScreenUtil.dip2px(mLauncher, 3) * 2;
			if(mController.getFolderInfo().contents.size() < 3){
				destH = destH / 3;
			}else if(mController.getFolderInfo().contents.size() < 6){
				destH = (int)(destH / 1.5);
			}
		}else {
			if(reCul || preInfoCount < mController.getFolderInfo().contents.size()){
				destH = iconRect.height() + ScreenUtil.dip2px(mLauncher, 3);
				if(mController.getFolderInfo().contents.size() < 3){
					destH = destH / 3;
				}else if(mController.getFolderInfo().contents.size() < 6){
					destH = (int)(destH / 1.5);
				}
			}
		}
		destRect.bottom = destRect.top + destH;

		Rect srcRect = new Rect();
		srcRect.left = 0;
		srcRect.right = ScreenUtil.getScreenWH()[0];
		srcRect.top = 0;
		srcRect.bottom = ScreenUtil.getScreenWH()[1];

		// view.setBackgroundColor(0xffff0000);
		bottomBoder = srcRect.height() - lp.topMargin - lp.height;
		
		if(direct == 0){
			if(mController.getOpenFolderFrom() == FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER){
				View cellLayout = (View) mController.mClickedView.getParent();
				offsetY = cellLayout.getTop();
			}else{
				View cellLayout = (View) mController.mClickedView.getParent().getParent();
				offsetY = cellLayout.getTop();
			}
		}
		
		ZscaleAnimation anim = new ZscaleAnimation(srcRect, destRect,
				boderW, topBoder, bottomBoder, direct, offsetY);
		anim.setDuration(duration);
		anim.setInterpolator(mInterpolator);
		if (listener != null) {
			anim.setAnimationListener(listener);
		}
		view.startAnimation(anim);
	}
	
	/**
	 * 动画时用来保存文件夹所在的位置
	 * 
	 * */
	private Rect mPostRect = null;
	public void fullscreenStyleFolderAnimation(int direct) {
		if (lp == null) {
			calcuateHeightAndMargin();
			if (lp == null) {
				return;
			}
		}
		
		int boderW = ScreenUtil.dip2px(mLauncher, 20);
		int topBoder = mTitleLayout.getMeasuredHeight(); // ScreenUtil.dip2px(mLauncher,
														// 45) ;
		int bottomBoder;
		Rect destRect;
		if (direct == 0) {
			mPostRect = null;
		}
		if (mPostRect == null) {
			destRect = new Rect();
			mPostRect = destRect;
			Rect iconRect = null;
			if (!FolderView.isDynamic(mFolderContentLayout.getUserFolderInfo())) {
				FolderIconTextView iconTextView = (FolderIconTextView) mController.mClickedView;
				iconRect = iconTextView.getIconRect();
				int[] loc = new int[2];
				iconTextView.getLocationOnScreen(loc);
				destRect.left = loc[0] + iconRect.left;
				destRect.top = loc[1] + iconRect.top;
				// destRect.left = iconTextView.getLeft() + iconRect.left;
				// destRect.top = iconTextView.getTop() + iconRect.top;
			} else {
				IconMaskTextView iconTextView = (IconMaskTextView) mController.mClickedView;
				iconRect = iconTextView.getIconRect();
				int[] loc = new int[2];
				iconTextView.getLocationOnScreen(loc);
				destRect.left = loc[0] + iconRect.left;
				destRect.top = loc[1] + iconRect.top;
				// destRect.left = iconTextView.getLeft() + iconRect.left;
				// destRect.top = iconTextView.getTop() + iconRect.top;
			}

			destRect.right = destRect.left + iconRect.width();

			if (direct == 0) {
				reCul = false;
				preInfoCount = mController.getFolderInfo().contents.size();
				destH = iconRect.height() + ScreenUtil.dip2px(mLauncher, 3) * 2;
				if (mController.getFolderInfo().contents.size() <= 3) {
					destH = destH / 3;
				} else if (mController.getFolderInfo().contents.size() <= 6) {
					destH = (int) (destH / 1.5);
				}
			} else {
				if (reCul || preInfoCount < mController.getFolderInfo().contents.size()) {
					destH = iconRect.height() + ScreenUtil.dip2px(mLauncher, 3);
					if (mController.getFolderInfo().contents.size() <= 3) {
						destH = destH / 3;
					} else if (mController.getFolderInfo().contents.size() <= 6) {
						destH = (int) (destH / 1.5);
					}
				}
			}
			destRect.bottom = destRect.top + destH;
		} else {
			destRect = mPostRect;
		}
		Rect srcRect = new Rect();
		srcRect.left = ScreenUtil.dip2px(mLauncher, 20);
		srcRect.right = ScreenUtil.getScreenWH()[0] - ScreenUtil.dip2px(mLauncher, 20);
		srcRect.top = lp.topMargin;
		srcRect.bottom = srcRect.top + lp.height;

		bottomBoder = srcRect.height() - lp.height;

		float scaleX = (float) destRect.width() / (srcRect.width() - boderW * 2);
		float scaleY = (float) destRect.height() / (srcRect.height() - topBoder - bottomBoder);
		float moveX = -(srcRect.width() / 2) * (1 - scaleX) - boderW * scaleX;
		float moveY = -(srcRect.height() / 2) * (1 - scaleY) + offsetY - topBoder * scaleY;

		// 动画
		AnimatorSet set = new AnimatorSet();
		if (direct == 0) {
			set.playTogether(ObjectAnimator.ofFloat(mFolderContentLayout, "x", destRect.left + moveX, srcRect.left),
					ObjectAnimator.ofFloat(mFolderContentLayout, "y", destRect.top + moveY, srcRect.top), 
					ObjectAnimator.ofFloat(mFolderContentLayout, "scaleX", scaleX, 1),
					ObjectAnimator.ofFloat(mFolderContentLayout, "scaleY", scaleY, 1));
			set.addListener(openAnimatorListener);
		} else {
			set.playTogether(ObjectAnimator.ofFloat(mFolderContentLayout, "x", srcRect.left, destRect.left + moveX),
					ObjectAnimator.ofFloat(mFolderContentLayout, "y", srcRect.top, destRect.top + moveY), 
					ObjectAnimator.ofFloat(mFolderContentLayout, "scaleX", 1, scaleX),
					ObjectAnimator.ofFloat(mFolderContentLayout, "scaleY", 1, scaleY));
			set.addListener(closeAnimatorListener);
			mPostRect = null;
		}
		set.setDuration(250);
//		set.setInterpolator(mInterpolator);
		set.start();
	}
	

	private void calcuateHeightAndMargin() {
		final int height = mController.initFolderContents() + ScreenUtil.dip2px(mLauncher, 20);
		lp = (LinearLayout.LayoutParams) mFolderContentLayout.getLayoutParams();
		int iconSize = mLauncher.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		int cellHeiht = iconSize * 2;
//		int extraHeight = 52 + 5;
//		int top = 3 * cellHeiht + (int) (extraHeight * ScreenUtil.getDensity());
		lp.topMargin = ScreenUtil.dip2px(mLauncher, 25 + 60); // getTopMargin(top);
		int[] screenWH = ScreenUtil.getScreenWH();
		if (screenWH != null) {
//			mBottomLayout.measure(MeasureSpec.makeMeasureSpec(screenWH[0], MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(screenWH[1], MeasureSpec.AT_MOST));
			mTitleLayout.measure(MeasureSpec.makeMeasureSpec(screenWH[0], MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(screenWH[1], MeasureSpec.AT_MOST));
//			lp.height = screenWH[1] - mBottomLayout.getMeasuredHeight() - lp.topMargin;
			lp.height = screenWH[1] - lp.topMargin;
			int lightbarHeight = (mFolderContentLayout.shouldShowLightbar() ? ScreenUtil.dip2px(mLauncher, 15) : 0);
			int slidingViewHeight = lp.height - mTitleLayout.getMeasuredHeight() - lightbarHeight;
			int maxRow = mFolderContentLayout.getMaxRows();
//			if (FolderView.hasThemeRecommandApp(mLauncher, mFolderContentLayout.getUserFolderInfo())) {
//				maxRow += 1;
//			}
			int cellHeight = slidingViewHeight / maxRow;
			int deductCellNum = mFolderContentLayout.getRowNum() > 0 ? (maxRow - mFolderContentLayout.getRowNum()) : 0;
			int deductCellHeight = deductCellNum * cellHeight;
			lp.height -= deductCellHeight;
//			if (FolderView.hasThemeRecommandApp(mLauncher, mFolderContentLayout.getUserFolderInfo())) {
//				lp.height += cellHeiht + (int) (20 * ScreenUtil.getDensity());
//			}

			View slidingView = mFolderContentLayout.getFolderSlidingView();
			if (slidingView != null) {
				ViewGroup.LayoutParams slidingLp = slidingView.getLayoutParams();
				slidingLp.height = slidingViewHeight - deductCellHeight;
			}
		} else {
			lp.height = height;
		}
	}

	
	// 关闭动画监听器
	private AnimatorListener closeAnimatorListener = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator arg0) {
		}

		@Override
		public void onAnimationRepeat(Animator arg0) {

		}

		@Override
		public void onAnimationEnd(Animator arg0) {
//				boolean updateNextOpen = mFolderContentLayout.getFolderSlidingView().isNeedReLoadContent();
//				if (updateNextOpen) {
//					currentFolderId = ItemInfo.NO_ID;
//					mFolderContentLayout.getFolderSlidingView().setNeedReLoadContent(false);
//				}
			if (mController.getFolderHelper() != null) {
//					mController.getFolderHelper().onFolderClose(mController.getFolderInfo(), mController.mIsAddApp2Folder || !updateNextOpen);
				mController.getFolderHelper().onFolderClose(mController.getFolderInfo(), mController.mIsAddApp2Folder);
			}
			if (!FolderView.isDynamic(mFolderContentLayout.getUserFolderInfo())) {
				FolderIconTextView iconTextView = (FolderIconTextView) mController.mClickedView;
				// iconTextView.onBeginDrawOutFolderAni();
				iconTextView.setNotDrawIcon(false);
				iconTextView.invalidate();
			}
		}

		@Override
		public void onAnimationCancel(Animator arg0) {

		}
	};

	// 打开动画监听器
	private AnimatorListener openAnimatorListener = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator arg0) {
			if (mController.mClickedView != null)
				mController.mClickedView.setClickable(false);
		}

		@Override
		public void onAnimationRepeat(Animator arg0) {

		}

		@Override
		public void onAnimationEnd(Animator arg0) {
			if (mController.mClickedView != null) {
				mController.mClickedView.setClickable(true);
			}
			
//				if (mFolderContentLayout.getFolderSlidingView().getAddView() != null) {
//					mFolderContentLayout.getFolderSlidingView().getAddView().setVisibility(View.VISIBLE);
//				}

			mTitleLayout.setVisibility(View.VISIBLE);
//				if (mController.isFolderEditable()) {
//					mBottomLayout.setVisibility(View.VISIBLE);
//				}
//				
//				if (mController.mOpenFolderFrom != FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER) {
//					if(mController.isEditMode()){
//						setBottomLayoutVisibility(View.INVISIBLE);
//					}
//				}
		}

		@Override
		public void onAnimationCancel(Animator arg0) {

		}
	};
}
