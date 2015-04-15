package com.nd.launcherdev.launcher.support;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.nd.android.pandahome2.R;
import com.nd.launcherdev.kitset.util.BaseBitmapUtils;

/**
 * 图片缓存 
 */
public class BitmapWeakReferences {
	private static BitmapWeakReferences instance;

	private WeakReference<Bitmap> defAppIcon;
	private WeakReference<Drawable> drapeInsideBg;
	
	private BitmapWeakReferences() {

	}

	public static BitmapWeakReferences getInstance() {
		if (instance == null)
			instance = new BitmapWeakReferences();

		return instance;
	}


	
	public Drawable getDrapeInsideBg(Resources res){
		if(null == drapeInsideBg || null == drapeInsideBg.get()){
			drapeInsideBg = new WeakReference<Drawable>(res.getDrawable(R.drawable.edit_screen_bg));
		}
		return drapeInsideBg.get();
	}
	public Bitmap getDefAppIcon(Resources res) {
		if (defAppIcon == null)
			defAppIcon = new WeakReference<Bitmap>(BaseBitmapUtils.getDefaultAppIcon(res));

		if (defAppIcon.get() == null) {
			defAppIcon.clear();
			defAppIcon = null;
			defAppIcon = new WeakReference<Bitmap>(BaseBitmapUtils.getDefaultAppIcon(res));
		}

		return defAppIcon.get();
	}
}
