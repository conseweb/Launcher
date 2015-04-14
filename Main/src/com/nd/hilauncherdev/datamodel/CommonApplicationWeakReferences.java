package com.nd.hilauncherdev.datamodel;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.nd.hilauncherdev.kitset.util.BitmapUtils;

/**
 * 全局图片缓存 <br>
 * Author:ryan <br>
 * Date:2012-5-18上午11:58:30
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
