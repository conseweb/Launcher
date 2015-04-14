package com.nd.hilauncherdev.launcher.view.icon.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.nd.hilauncherdev.kitset.util.BaseBitmapUtils;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.config.preference.BaseSettingsPreference;
import com.nd.hilauncherdev.launcher.view.icon.ui.folder.FolderIconTextView;

/**
 * 文件夹合并类
 * @author Michael
 * @createtime 2013-8-8
 */
public class FolderAni {
	
	public static final float ANI_SCALE = 1.2f;
	
	private boolean folderEnterAni = false;
	
	private boolean folderExitAni = false;
	
	private long aniBeginTime, aniDiffTime;
	
	private Drawable mAnimationBackground;
	
	private LauncherIconData data;
	
	private LauncherIconViewConfig config;
	
	public FolderAni(Context context, LauncherIconData data,  LauncherIconViewConfig config) {
		super();
		Resources res = context.getResources();
		if (BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_IPHONE) {
			this.mAnimationBackground = new BitmapDrawable(res,LauncherIconSoftReferences.getInstance().getDefIconFolderBackground(res));
		} else if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_ANDROID_4){
			this.mAnimationBackground = new BitmapDrawable(res,LauncherIconSoftReferences.getInstance().getDefIconAndroidFolderBackground(res));
		} else {
			this.mAnimationBackground = new BitmapDrawable(res, LauncherIconSoftReferences.getInstance().getDefIconFullScreenFolderBackground(res));
		}
		this.data = data;
		this.config = config;
	}
	
	public void setNormalBounds(){
		setBounds(ANI_SCALE);
	}
	
	
	public void setBounds(float scale){
		int iconSize = 0;
		int iconCenterY = 0;
		if(config.isLargeIconMode()){
			iconSize = data.iconRects.maxRectAndScale.rect.height();
			iconCenterY = data.iconRects.maxRectAndScale.rect.top + iconSize/2;
		}else{
			if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE){
				iconSize = data.iconRects.mediumRectAndScale.rect.height();
				iconCenterY = data.iconRects.mediumRectAndScale.rect.top + iconSize/2;
			}else{
				iconSize = data.iconRects.minRectAndScale.rect.height();
				iconCenterY = data.iconRects.minRectAndScale.rect.top + iconSize/2;
			}
		}
		mAnimationBackground.setBounds(BaseBitmapUtils.caculateRect(data.viewWidth/2, 
				iconCenterY, 
				iconSize * scale, 
				iconSize * scale));
	}
	
	/**
	 * 判断是否返回
	 * @author Michael
	 * @createtime 2013-8-9 
	 * @return
	 */
	public boolean timeExceed(){
		if (folderExitAni) {
			config.setDrawText(true);
			data.setAni(false);
			data.textBackgroundPanit.setAlpha(LauncherIconDataCache.TEXT_BACKGROUND_ALPHA);
			folderExitAni = false;
			return true;
		}
		setNormalBounds();
		return false;
	}
	
	
	public void folerEnterAni(){
		float scale = aniDiffTime * ANI_SCALE / BaseConfig.ANI_255;
		final int alpha = (int) (255 - aniDiffTime);
		final float shadow = alpha / 255;
		setBounds(scale);
		data.setAni(true);
		data.getAlphaPaint().setAlpha(alpha);
		data.getAlphaPaint().setShadowLayer(shadow, 1, 1, Color.BLACK);
		data.textBackgroundPanit.setAlpha(alpha);
	}
	
	public void folderExitAni(){
		float scale = ANI_SCALE - (aniDiffTime * ANI_SCALE / BaseConfig.ANI_255);
		setBounds(scale);
		data.setAni(true);
		final float shadow = aniDiffTime / 255;
		data.getAlphaPaint().setAlpha((int) (aniDiffTime));
		data.getAlphaPaint().setShadowLayer(shadow, 1, 1, Color.BLACK);
		data.textBackgroundPanit.setAlpha((int) ((float) LauncherIconDataCache.TEXT_BACKGROUND_ALPHA * aniDiffTime / 255));
		
	}
	
	
	


	/**
	 * 是否处于合并状态
	 * @author Michael
	 * @createtime 2013-8-8 
	 * @return
	 */
	public boolean isOnMergeFolderAni(){
		return folderEnterAni || folderExitAni;
	}


	public boolean isFolderEnterAni() {
		return folderEnterAni;
	}


	public void setFolderEnterAni(boolean folderEnterAni) {
		this.folderEnterAni = folderEnterAni;
	}


	public boolean isFolderExitAni() {
		return folderExitAni;
	}


	public void setFolderExitAni(boolean folderExitAni) {
		this.folderExitAni = folderExitAni;
	}


	public long getAniBeginTime() {
		return aniBeginTime;
	}


	public void setAniBeginTime(long aniBeginTime) {
		this.aniBeginTime = aniBeginTime;
	}


	public long getAniDiffTime() {
		return aniDiffTime;
	}


	public void setAniDiffTime(long aniDiffTime) {
		this.aniDiffTime = aniDiffTime;
	}


	public Drawable getmAnimationBackground() {
		return mAnimationBackground;
	}

	public void setmAnimationBackground(Drawable mAnimationBackground) {
		this.mAnimationBackground = mAnimationBackground;
	}
	
	
	public void updateAnimationBg(Context context){
		Resources res = context.getResources();
		if (BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_IPHONE) {
			this.mAnimationBackground = new BitmapDrawable(res,LauncherIconSoftReferences.getInstance().getDefIconFolderBackground(res));
		} else if(BaseSettingsPreference.getInstance().getFolderStyle() == FolderIconTextView.FOLDER_STYLE_ANDROID_4){
			this.mAnimationBackground = new BitmapDrawable(res,LauncherIconSoftReferences.getInstance().getDefIconAndroidFolderBackground(res));
		} else {
			this.mAnimationBackground = new BitmapDrawable(res, LauncherIconSoftReferences.getInstance().getDefIconFullScreenFolderBackground(res));
		}
	}
}
