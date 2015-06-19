/**
 *
 */
package com.bitants.common.launcher.view.icon.ui;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.theme.ThemeManagerFactory;
import com.bitants.common.theme.data.BaseThemeData;
import com.bitants.common.R;
import com.bitants.common.kitset.util.BaseBitmapUtils;


/**
 *
 */
public class LauncherIconSoftReferences {

	private static LauncherIconSoftReferences instance;
	
	private LauncherIconSoftReferences() {

	}

	public static LauncherIconSoftReferences getInstance() {
		if (instance == null)
			instance = new LauncherIconSoftReferences();

		return instance;
	}
	
	
	private SoftReference<Bitmap> defAppNoticeBg;
	
	private WeakReference<Bitmap> defIconFolderBackground;
	private WeakReference<Bitmap> defIconAndroidFolderBackground;
	private WeakReference<Bitmap> defIconFullScreenFolderBackground;
	private WeakReference<Bitmap> defIconFolderOpen;
	private WeakReference<Bitmap> defIconFolderClose;
	
	//热门游戏和热门软件右上角图标
	private SoftReference<Bitmap> softAndGameUpdateIcon;
	
	/**
	 * 文件夹加密蒙版
	 */
	private WeakReference<Bitmap> defIconFolderEncriptMask;
	private WeakReference<Bitmap> defIconAndroidFolderEncriptMask;
	private WeakReference<Bitmap> defIconFullScreenFolderEncriptMask;
	
	/**
	 * 大图标蒙板
	 */
	private SparseArray<SoftReference<Bitmap>> largeIconBackgrounds = new SparseArray<SoftReference<Bitmap>>();
	
	
	/**
	 * 匣子新安装图标
	 */
	private SoftReference<Bitmap> drawerNewInstallFlagIcon;
	
	/**
	 * 匣子新功能图标
	 */
	private SoftReference<Bitmap> drawerNewFunctionFlagIcon;
	
	public Bitmap getDefNoticeBg() {
		if (defAppNoticeBg == null){
			Resources res = BaseConfig.getApplicationContext().getResources();
			defAppNoticeBg = new SoftReference<Bitmap>(BitmapFactory.decodeResource(res, R.drawable.app_notice_bg));
			return defAppNoticeBg.get();
		}
		if (defAppNoticeBg.get() == null) {
			defAppNoticeBg.clear();
			defAppNoticeBg = null;
			Resources res = BaseConfig.getApplicationContext().getResources();
			defAppNoticeBg = new SoftReference<Bitmap>(BitmapFactory.decodeResource(res, R.drawable.app_notice_bg));
			return defAppNoticeBg.get();
		}

		return defAppNoticeBg.get();
	}
	
	/**
	 * 
	 * <br>Description:获取热门游戏和热门软件右上角图标（有更新通知）
	 * @param res
	 * @return
	 */
	public Bitmap getSoftAndGameUpdateIcon() {
		if(softAndGameUpdateIcon == null){
			Context mContext = BaseConfig.getApplicationContext();
			softAndGameUpdateIcon = new SoftReference<Bitmap>(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.update_red_dot));
			return softAndGameUpdateIcon.get();
		}else if(softAndGameUpdateIcon.get() == null){
			softAndGameUpdateIcon.clear();
			softAndGameUpdateIcon = null;
			Context mContext = BaseConfig.getApplicationContext();
			softAndGameUpdateIcon = new SoftReference<Bitmap>(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.update_red_dot));
		}
		return softAndGameUpdateIcon.get();
	}
	
	
	public Bitmap getLargeIconBackGround(int resId){
		SoftReference<Bitmap> w = largeIconBackgrounds.get(resId);
		Bitmap bitmap;
		if(w != null && w.get() != null){
			// Log.v("cache", "in cache");
			 bitmap = w.get();
		}else{
			// Log.v("cache", "not in cache");
			Context context = BaseConfig.getApplicationContext();
			bitmap = ((BitmapDrawable) context.getResources().getDrawable(resId)).getBitmap();
			largeIconBackgrounds.put(resId, new SoftReference<Bitmap>(bitmap));
		}
		return bitmap;
	}
	
	/**
	 * 获取匣子中新安装图标
	 * @param mContext
	 * @return
	 */
	public Bitmap getDrawerNewInstallFlagIcon() {
		if(drawerNewInstallFlagIcon == null){
			Context mContext = BaseConfig.getApplicationContext();
			drawerNewInstallFlagIcon = new SoftReference<Bitmap>(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.new_installed_flag));
			return drawerNewInstallFlagIcon.get();
		}else if(drawerNewInstallFlagIcon.get() == null){
			drawerNewInstallFlagIcon.clear();
			drawerNewInstallFlagIcon = null;
			Context mContext = BaseConfig.getApplicationContext();
			drawerNewInstallFlagIcon = new SoftReference<Bitmap>(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.new_installed_flag));
		}
		return drawerNewInstallFlagIcon.get();
	}

	/**
	 * 获取匣子中新功能图标
	 * @return
	 */
	public Bitmap getDrawerNewFunctionFlagIcon() {
		
		if(drawerNewFunctionFlagIcon == null){
			Context mContext = BaseConfig.getApplicationContext();
			drawerNewFunctionFlagIcon = new SoftReference<Bitmap>(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.new_label_red));
			return drawerNewFunctionFlagIcon.get();
		}else if(drawerNewFunctionFlagIcon.get() == null){
			drawerNewFunctionFlagIcon.clear();
			drawerNewFunctionFlagIcon = null;
			Context mContext = BaseConfig.getApplicationContext();
			drawerNewFunctionFlagIcon = new SoftReference<Bitmap>(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.new_label_red));
		}
		return drawerNewFunctionFlagIcon.get();
	}
	
	public Bitmap getDefIconFolderBackground(Resources res) {
		if (defIconFolderBackground == null)
			defIconFolderBackground = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_BACKGROUND)));

		if (defIconFolderBackground.get() == null) {
			defIconFolderBackground.clear();
			defIconFolderBackground = null;
			defIconFolderBackground = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_BACKGROUND)));
		}

		return defIconFolderBackground.get();
	}
	
	public Bitmap getDefIconAndroidFolderBackground(Resources res) {
		if (defIconAndroidFolderBackground == null)
			defIconAndroidFolderBackground = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.ANDROID_FOLDER_BACKGROUND)));

		if (defIconAndroidFolderBackground.get() == null) {
			defIconAndroidFolderBackground.clear();
			defIconAndroidFolderBackground = null;
			defIconAndroidFolderBackground = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.ANDROID_FOLDER_BACKGROUND)));
		}

		return defIconAndroidFolderBackground.get();
	}

	
	/**
	 * 重新获取文件夹背景
	 */
	public Bitmap resetDefIconFolderBackground(Resources res) {
		defIconFolderBackground = null;
		return getDefIconFolderBackground(res);
	}
	
	public Bitmap resetDefIconAndroidFolderBackground(Resources res) {
		defIconAndroidFolderBackground = null;
		return getDefIconAndroidFolderBackground(res);
	}
	
	public Bitmap resetDefIconFullScreenFolderBackground(Resources res) {
		defIconFullScreenFolderBackground = null;
		return getDefIconFullScreenFolderBackground(res);
	}
	
	public Bitmap getDefIconFolderOpen(Resources res) {
		if (defIconFolderOpen == null)
			defIconFolderOpen = new WeakReference<Bitmap>(BitmapFactory.decodeResource(res, R.drawable.folder_foreground_open));

		if (defIconFolderOpen.get() == null) {
			defIconFolderOpen.clear();
			defIconFolderOpen = null;
			defIconFolderOpen = new WeakReference<Bitmap>(BitmapFactory.decodeResource(res, R.drawable.folder_foreground_open));
		}

		return defIconFolderOpen.get();
	}

	public Bitmap getDefIconFolderClose(Resources res) {
		if (defIconFolderClose == null)
			defIconFolderClose = new WeakReference<Bitmap>(BitmapFactory.decodeResource(res, R.drawable.folder_foreground_closed));

		if (defIconFolderClose.get() == null) {
			defIconFolderClose.clear();
			defIconFolderClose = null;
			defIconFolderClose = new WeakReference<Bitmap>(BitmapFactory.decodeResource(res, R.drawable.folder_foreground_closed));
		}

		return defIconFolderClose.get();
	}
	
	/**
	 * 文件夹加密蒙版
	 */
	public Bitmap getDefIconFolderEncriptMask(Resources res) {
		if (defIconFolderEncriptMask == null)
			defIconFolderEncriptMask = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_ENCRIPT_MASK)));

		if (defIconFolderEncriptMask.get() == null) { 
			defIconFolderEncriptMask.clear();
			defIconFolderEncriptMask = null;
			defIconFolderEncriptMask = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_ENCRIPT_MASK)));
		}

		return defIconFolderEncriptMask.get();
	}
	
	public Bitmap getDefIconAndroidFolderEncriptMask(Resources res) {
		if (defIconAndroidFolderEncriptMask == null)
			defIconAndroidFolderEncriptMask = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.ANDROID_FOLDER_ENCRIPT_MASK)));

		if (defIconAndroidFolderEncriptMask.get() == null) { 
			defIconAndroidFolderEncriptMask.clear();
			defIconAndroidFolderEncriptMask = null;
			defIconAndroidFolderEncriptMask = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.ANDROID_FOLDER_ENCRIPT_MASK)));
		}

		return defIconAndroidFolderEncriptMask.get();
	}
	
	
	
	/**
	 * 重新获取文件夹加密蒙版
	 */
	public Bitmap resetDefIconFolderEncriptMask(Resources res) {
		defIconFolderEncriptMask = null;
		return getDefIconFolderEncriptMask(res);
	}
	
	public Bitmap resetDefIconAndroidFolderEncriptMask(Resources res) {
		defIconAndroidFolderEncriptMask = null;
		return getDefIconAndroidFolderEncriptMask(res);
	}
	
	public Bitmap resetDefIconFullScreenFolderEncriptMask(Resources res) {
		defIconFullScreenFolderEncriptMask = null;
		return getDefIconFullScreenFolderEncriptMask(res);
	}
	
	public Bitmap getDefIconFullScreenFolderBackground(Resources res) {
		if (defIconFullScreenFolderBackground == null)
			defIconFullScreenFolderBackground = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_BACKGROUND)));

		if (defIconFullScreenFolderBackground.get() == null) {
			defIconFullScreenFolderBackground.clear();
			defIconFullScreenFolderBackground = null;
			defIconFullScreenFolderBackground = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_BACKGROUND)));
		}

		return defIconFullScreenFolderBackground.get();
	}
	
	public Bitmap getDefIconFullScreenFolderEncriptMask(Resources res) {
		if (defIconFullScreenFolderEncriptMask == null)
			defIconFullScreenFolderEncriptMask = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_ENCRIPT_MASK)));

		if (defIconFullScreenFolderEncriptMask.get() == null) { 
			defIconFullScreenFolderEncriptMask.clear();
			defIconFullScreenFolderEncriptMask = null;
			defIconFullScreenFolderEncriptMask = new WeakReference<Bitmap>(BaseBitmapUtils.drawable2Bitmap(ThemeManagerFactory.getInstance().getThemeDrawable(BaseThemeData.FOLDER_ENCRIPT_MASK)));
		}

		return defIconFullScreenFolderEncriptMask.get();
	}
}
