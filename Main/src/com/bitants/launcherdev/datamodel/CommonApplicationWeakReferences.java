package com.bitants.launcherdev.datamodel;

import android.content.res.Resources;
import android.graphics.Bitmap;
import com.bitants.launcherdev.kitset.util.BitmapUtils;

import java.lang.ref.WeakReference;

/**
 * 全局图片缓存 <br>
 */
public class CommonApplicationWeakReferences {
	private static CommonApplicationWeakReferences instance;

	private WeakReference<Bitmap> defAppIcon;

	private CommonApplicationWeakReferences() {

	}

	public static CommonApplicationWeakReferences getInstance() {
		if (instance == null)
			instance = new CommonApplicationWeakReferences();

		return instance;
	}

	public Bitmap getDefAppIcon(Resources res) {
		if (defAppIcon == null)
			defAppIcon = new WeakReference<Bitmap>(BitmapUtils.getDefaultAppIcon(res));

		if (defAppIcon.get() == null) {
			defAppIcon.clear();
			defAppIcon = null;
			defAppIcon = new WeakReference<Bitmap>(BitmapUtils.getDefaultAppIcon(res));
		}

		return defAppIcon.get();
	}

}
