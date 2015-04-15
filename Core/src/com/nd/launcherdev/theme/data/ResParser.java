package com.nd.launcherdev.theme.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.nd.android.pandahome2.R;
import com.nd.launcherdev.kitset.util.BaseBitmapUtils;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.theme.module.ModuleConstant;
import com.nd.launcherdev.theme.parse.EncodeTools;

/**
 * <br>
 * Description: 主题资源解析 <br>
 * Author:caizp <br>
 * Date:2011-7-1下午02:42:18
 */
public class ResParser {
	
	/**
	 * 主题资源分辨率(hdpi)
	 */
	private final static int THEME_RES_DENSITY = 240;
	
	/**
	 * 读取外部的图标
	 * 
	 * @param ctx
	 *            Context
	 * @param key 图片的key值，用在tag对应文件是加密文件时读取图片
	 * @param tag 图片路径或在apk包中的资源ID
	 * @param isThemeIconKey 是否主题大图标
	 * @param needScaleIcon 是否需要缩放图标
	 * @return Drawable
	 */
	public static Drawable getIconDrawable(Context ctx, String key, String tag, boolean isThemeIconKey, boolean needScaleIcon) {
		Drawable d = getImageDrawable(ctx, key, tag, 0, false);
		if (d != null) {
			// 非默认情景模式下，不缩放主题图标 caizp 2013-8-12
			if(!needScaleIcon){
				return d;
			};
			int drawableWidth = d.getIntrinsicWidth();
			int drawableHeight = d.getIntrinsicHeight();
			int largeIconSize = (int) ctx.getResources().getDimensionPixelSize(R.dimen.app_background_size);
			if(isThemeIconKey && (drawableWidth >= largeIconSize
					&& drawableHeight >= largeIconSize)){//大图标主题
				Bitmap b = BaseBitmapUtils.createSpecialIconBitmap(d, ctx, largeIconSize);
				return new BitmapDrawable(ctx.getResources(), b);
			}
			Bitmap b = BaseBitmapUtils.createIconBitmap(d, ctx, false);
			return new BitmapDrawable(ctx.getResources(), b);
		}
		return null;
	}

	/**
	 * 读取图片drawable
	 * 
	 * @param ctx
	 * @param key 图片的key值，用在tag对应文件是加密文件时读取图片
	 * @param tag 图片路径或在apk包中的资源ID
	 *            
	 * @param density
	 * @param nodpi 是否读原图片
	 * @return
	 */
	public static Drawable getImageDrawable(Context ctx, String key, String tag, float density, boolean nodpi) {
		if (tag == null || ThemeGlobal.DEFAULT_VALUE.equals(tag)) {
			return null;
		}

		int tagAT = tag.indexOf('@');
		int tagOR = tag.indexOf('|');
		Bitmap bp = null;
		Bitmap result = null;
		if (tagAT < 0 && tagOR < 0) {
			// 读取文件处理
			try {
				if (density == -1) {// 读取Drawer背景图时的特殊处理
					Options opts = new Options();
					opts.inJustDecodeBounds = true;
					int height = opts.outHeight;
					int width = opts.outWidth;
					if (height > 480 || width > 320) {
						if (height >= width) {
							opts.inSampleSize = height / 480 + 1;
						} else {
							opts.inSampleSize = width / 320 + 1;
						}
						opts.outHeight = height / opts.inSampleSize;
						opts.outWidth = width / opts.inSampleSize;
					}
					opts.inJustDecodeBounds = false;
					if (new File(tag).exists()) {
						bp = BitmapFactory.decodeFile(tag, opts);
					}
				} else {
					if (new File(tag).exists()) {
						if(tag.endsWith(ThemeGlobal.GUARDED_RES)) {//获取加密资源 caizp 2014-7-18
							InputStream is = BaseBitmapUtils.bytes2InputStream(EncodeTools.getResource(tag, key));
							if(null != is) {
								bp = BitmapFactory.decodeStream(is);
							}
						} else {
							InputStream is = new FileInputStream(tag);
							bp = BitmapFactory.decodeStream(is);
						}
					}
				}
			} catch (OutOfMemoryError e) {// 内存溢出处理
				e.printStackTrace();
				return null;
			} catch (Throwable t) {
				t.printStackTrace();
			}

			if (density > 0) {
				if (null != bp) {
					try{
						result = Bitmap.createBitmap((int) (bp.getWidth()), (int) (bp.getHeight()), Config.ARGB_8888);
						Rect rect = new Rect(0, 0, bp.getWidth(), bp.getHeight());
						Canvas canvas = new Canvas(result);
						Paint paint = new Paint();
						paint.setAntiAlias(true);
						canvas.drawBitmap(bp, rect, rect, paint);
						bp.recycle();
					} catch (OutOfMemoryError e) {// 内存溢出处理
						e.printStackTrace();
						bp.setDensity(THEME_RES_DENSITY);
						return new BitmapDrawable(ctx.getResources(), bp);
					} catch (NullPointerException e){//result为空导致Canvas为空指针的处理。
						e.printStackTrace();
						bp.setDensity(THEME_RES_DENSITY);
						return new BitmapDrawable(ctx.getResources(), bp);
					}
				}
			} else {
				if (null != bp) {
					bp.setDensity(THEME_RES_DENSITY);
					return new BitmapDrawable(ctx.getResources(), bp);
				}
			}
			if (result != null) {
				if(nodpi){
					return new BitmapDrawable(result);
				}
				result.setDensity(THEME_RES_DENSITY);
				return new BitmapDrawable(ctx.getResources(), result);
			} else {
				return null;
			}
		}

		if (tagAT > 0) {
			// 读res
			String[] arr = tag.split("@");
			if (arr.length == 2) {
				String pname = arr[0];
				String id = arr[1];
				int resId = StringUtil.parseInt(id, 0);
				if (resId != 0) {
					try {
						Context ct;
						// 开头为@时
						if (StringUtil.isEmpty(pname)) {
							ct = ctx;
						} else {
							ct = ctx.createPackageContext(pname, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
						}
						InputStream in = ct.getResources().openRawResource(resId);
						bp = BitmapFactory.decodeStream(in);
						if (bp != null) {
							if(nodpi){
								return new BitmapDrawable(bp);
							}
							bp.setDensity(THEME_RES_DENSITY);
							return new BitmapDrawable(ctx.getResources(), bp);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}
		}

		if (tagOR > 0) {
			// 读asset
			String[] s = tag.split("\\|");
			AssetManager am = null;
			String path = "";
			if (s.length != 2) {
				return null;
			}
			if (s[0].length() == 0) {
				am = ctx.getAssets();
			} else {
				path = s[1];
				Context packCtx;
				try {
					packCtx = ctx.createPackageContext(s[0], Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
					am = packCtx.getAssets();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				InputStream is = am.open(path);
				if (is != null) {
					bp = BitmapFactory.decodeStream(is);
					if (bp != null) {
						if(nodpi){
							return new BitmapDrawable(bp);
						}
						bp.setDensity(THEME_RES_DENSITY);
						return new BitmapDrawable(ctx.getResources(), bp);
					} else {
						return null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * <br>Description: 根据主题或模块目录扫描获取图片或图标所在位置
	 * <br>Author:caizp
	 * <br>Date:2014-6-19下午1:59:49
	 * @param themePath 图片或图标所属主题根目录
	 * @param key 图片或图标key
	 * @param moduleKey 图片或图标所属模块
	 * @param supportV6 是否V6新结构主题
	 * @param scanEncryptFile 是否扫描加密文件
	 * @return
	 */
	public static String getDrawablePath(String themePath, String key, String moduleKey, boolean supportV6, boolean scanEncryptFile) {
		String filePath = "";
		int[] screenWH = ScreenUtil.getScreenWH();
		String[] scanFileNames = {ThemeGlobal.GUARDED_RES, key+ThemeGlobal.CONVERTED_SUFFIX_PNG, key+ThemeGlobal.CONVERTED_SUFFIX_JPG, key+ThemeGlobal.SUFFIX_PNG, key+ThemeGlobal.SUFFIX_JPG};
		if(ModuleConstant.MODULE_ICONS.equals(moduleKey) || ModuleConstant.MODULE_WALLPAPER.equals(moduleKey)) {
			if(supportV6) {//新结构主题资源
				int index = 1;
				if(scanEncryptFile){//扫描加密资源
					index = 0;
				}
				if("preview".equals(key)) {//读取预览图
					for(int i=1; i<scanFileNames.length; i++) {
						filePath = themePath + moduleKey + "/" + scanFileNames[i];
						if(new File(filePath).exists()) {
							return filePath;
						}
					}
				}
				for(int i=index; i<scanFileNames.length; i++) {
					filePath = themePath + moduleKey + "/" + "drawable-" + screenWH[1] + "x" + screenWH[0] + "/" + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
				if(ScreenUtil.isMLargeScreen()) {
					for(int i=index; i<scanFileNames.length; i++) {
						filePath = themePath + moduleKey + "/" + "drawable-xhdpi/" + scanFileNames[i];
						if(new File(filePath).exists()) {
							return filePath;
						}
					}
				}
				for(int i=index; i<scanFileNames.length; i++) {
					filePath = themePath + moduleKey + "/" + "drawable-hdpi/" + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
				for(int i=index; i<scanFileNames.length; i++) {
					filePath = themePath + moduleKey + "/" + "drawable-xhdpi/" + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
				for(int i=index; i<scanFileNames.length; i++) {
					filePath = themePath + ThemeGlobal.THEME_APT_DRAWABLE_DIR + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
			} else {//旧主题资源
				for(int i=1; i<scanFileNames.length; i++) {
					filePath = themePath + ThemeGlobal.THEME_APT_DRAWABLE_DIR + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
			}
		} else {
			if(supportV6) {//新结构主题未加密资源
				for(int i=1; i<scanFileNames.length; i++) {
					filePath = themePath + moduleKey.replace("@", "/") + "/" + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
				for(int i=1; i<scanFileNames.length; i++) {
					filePath = themePath + ThemeGlobal.THEME_APT_DRAWABLE_DIR + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
			} else {//旧主题资源
				for(int i=1; i<scanFileNames.length; i++) {
					filePath = themePath + ThemeGlobal.THEME_APT_DRAWABLE_DIR + scanFileNames[i];
					if(new File(filePath).exists()) {
						return filePath;
					}
				}
			}
		}
		return null;
	}

}
