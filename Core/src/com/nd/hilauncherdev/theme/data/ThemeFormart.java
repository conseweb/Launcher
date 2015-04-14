package com.nd.hilauncherdev.theme.data;

/**
 * Version 1.0
 *
 * =============================================================
 * Revision History
 * 
 * Modification                    Tracking
 * Date           Author           Number      Description of changes
 * ----------     --------------   ---------   -------------------------
 * 2009-9-23         yangbin            代码重构
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.nd.hilauncherdev.kitset.util.BaseBitmapUtils;
import com.nd.hilauncherdev.kitset.util.ScreenUtil;
import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.support.FastBitmapDrawable;

/**
 * 主题格式化类
 * 
 * @author yangbin
 */
public class ThemeFormart {

	public final static int DEFAULT = 0;

	public final static int ICON = 1;

	public final static int SCREEN = 2;

	public final static int WALLPAPER = 3;

	/**
	 * TAG
	 */
	private final static String TAG = "ThemeFormart";

	/**
	 * AUTO_SAVE_FIXICON 获取图标
	 */
	public static boolean AUTO_SAVE_FIXICON = true;

	/**
	 * AUTO_ICON_SUFFIX
	 */
	public static String AUTO_ICON_SUFFIX = ".icon_";

	/**
	 * AUTO_SAVE_FIXWALL 获取壁纸
	 */
	public static boolean AUTO_SAVE_FIXWALL = true;

	/**
	 * AUTO_WALLPAPER_SUFFIX
	 */
	public static String AUTO_WALLPAPER_SUFFIX = ".wall_";

	/**
	 * 返回-1为非alpha值
	 * 
	 * @param src
	 * @return
	 */
	public static int parseAlpha(String src) {
		int ret = -1;
		try {
			ret = Integer.parseInt(src);
		} catch (Exception e) {
		}
		if ((ret >= 0) && (ret <= 255)) {
			return ret;
		}
		return -1;
	}

	/**
	 * 将配置的字体转为Typeface对象
	 * 
	 * @param context
	 * @param sfont
	 * @return
	 */
	public static Typeface parseFont(Context context, String sfont) {
		Typeface tf = null;
		if ((sfont == null) || (sfont.length() == 0)) {
			tf = null;
		} else {
			// 路径以@开头
			if (sfont.charAt(0) == '@') {
			} else {
				tf = Typeface.create(sfont, Typeface.NORMAL);
			}
		}
		return tf;
	}

	/**
	 * 颜色解析
	 * 
	 * @param scolor
	 * @return
	 */
	public static int parseColor(String scolor) {
		if ((scolor == null) || (scolor.length() == 0)) {
			return -1;
		}
		int color = -1;
		try {
			color = Color.parseColor(scolor);
		} catch (Exception e) {
			Log.w(TAG, "erro color:" + scolor);
		}
		return color;
	}

	/**
	 * 创建fix wall bitmap
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createFixWallBitmap(String path, int width, int height ) {
        Bitmap bitmap = null;
        //获取宽高
        int[] wh = getImageWH(BaseBitmapUtils.getImageInputStream(BaseConfig.getApplicationContext(), BaseThemeData.WALLPAPER, path));
        if ((wh[0] == -1) || (wh[1] == -1)) {
            return null;
        }

        // 转为壁纸大小
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int inSampleSize = Math.min(wh[0]/width, wh[1]/height);
            options.inSampleSize = inSampleSize;
            InputStream is = BaseBitmapUtils.getImageInputStream(BaseConfig.getApplicationContext(), BaseThemeData.WALLPAPER, path);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            bitmap = BaseBitmapUtils.toSizeBitmap(bitmap, width, height );
        } catch (Throwable e) {
            Log.w(TAG, "createFixWallBitmap Throwable.", e);
            return null;
        } 
        return bitmap;
    }

	/**
	 * 创建fix wall bitmap
	 * 
	 * @param path
	 * @param autoSave
	 * @return
	 */
	public static Bitmap createFixWallBitmap(String path) {
        Bitmap bitmap = null;
        //获取宽高
        int[] wh = getImageWH(BaseBitmapUtils.getImageInputStream(BaseConfig.getApplicationContext(), BaseThemeData.WALLPAPER, path));
        if ((wh[0] == -1) || (wh[1] == -1)) {
            return null;
        }

        // 转为壁纸大小
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] wallpaperWH = ScreenUtil.getWallpaperWH();
            int inSampleSize = Math.min(wh[0]/wallpaperWH[0], wh[1]/wallpaperWH[1]);
            options.inSampleSize = inSampleSize;
            InputStream is = BaseBitmapUtils.getImageInputStream(BaseConfig.getApplicationContext(), BaseThemeData.WALLPAPER, path);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            bitmap = BaseBitmapUtils.toSizeBitmap(bitmap, wallpaperWH[0], wallpaperWH[1]);
        } catch (Throwable e) {
            Log.w(TAG, "createFixWallDrawable2 Throwable.", e);
        }

        return bitmap;
    }

	/**
	 * 获取图像的宽高
	 * 
	 * @param path
	 * @return
	 */
	public static int[] getImageWH(String path) {
		int[] wh = { -1, -1 };
		if (path == null) {
			return wh;
		}
		File file = new File(path);
		if (file.exists() && !file.isDirectory()) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				InputStream is = new FileInputStream(path);
				BitmapFactory.decodeStream(is, null, options);
				wh[0] = options.outWidth;
				wh[1] = options.outHeight;
			} catch (Throwable e) {
				Log.w(TAG, "getImageWH Throwable.", e);
			}
		}
		return wh;
	}

	public static int[] getImageWH(InputStream is) {
		int[] wh = { -1, -1 };
		if (is == null) {
			return wh;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(is, null, options);
			wh[0] = options.outWidth;
			wh[1] = options.outHeight;
		} catch (Throwable e) {
			Log.w(TAG, "getImageWH Throwable.", e);
		}
		return wh;
	}

	/**
	 * 生成主题缩略图
	 * @param ctx
	 * @param themeId 主题ID
	 * @param filename
	 * @return
	 */
	public static boolean createThemeThumbnail( Context ctx, String themeId ) {
		try{
			BasePandaTheme theme = new BasePandaTheme(BaseConfig.getApplicationContext(), themeId, false);
			ThemeResourceWrapper wrapper = theme.getWrapper();
			
			String previewPath = "";
			if(theme.getType() == ThemeType.DEFAULT){
				previewPath = BaseConfig.THEME_DIR + theme.getAptPath() + ThemeGlobal.THEME_APT_DRAWABLE_DIR + BaseThemeData.THUMBNAIL + ThemeGlobal.CONVERTED_SUFFIX_JPG;
			}else if(theme.getType() == ThemeType.PANDAHOME){
				previewPath = BaseConfig.THEME_THUMB_DIR + themeId + ThemeGlobal.CONVERTED_SUFFIX_JPG;
			}
			if(new File(previewPath).exists()){
				return true;
			}
			
			//主题中已存在缩略图
			Drawable thumb = wrapper.getKeyDrawable(BaseThemeData.THUMBNAIL, false);
			if(null != thumb){
				if(theme.getType() == ThemeType.PANDAHOME){//APK主题的预览图保存至指定目录下
					BaseBitmapUtils.saveBitmap2file( BaseBitmapUtils.drawable2Bitmap(thumb), previewPath );
					return true;
				}
				return true;
			}
					
			int iThumbNailWidth = 97;
			int iThumbNailHeight = 148;
			
			int px = ScreenUtil.dip2px( ctx, iThumbNailWidth );
			int py = ScreenUtil.dip2px( ctx, iThumbNailHeight );
			
			final Bitmap newBitmap = Bitmap.createBitmap( px, py, Bitmap.Config.RGB_565 );
			Canvas canvas = new Canvas( newBitmap );

			//获取壁纸
			final Bitmap wallPaper = wrapper.getWallpaperBitmap( px, py );
			
			if( null == wallPaper || wallPaper.isRecycled() ){
				BaseBitmapUtils.destoryBitmap(newBitmap);
				return false;
			}
			
			//将壁纸缩放成屏幕大小的图片	
//			Bitmap scaleWallPaper = Bitmap.createScaledBitmap(wallPaper, screenWidth, screenHeight, true );
			canvas.drawBitmap( wallPaper, 0, 0, null );
			
			
			//计算每个图标占用的宽度
			int iconSize = (px-25)/4;
			
			//画图标
			int iCount = 1;
			int top = 10;
			int left = 5;
			final List<Bitmap> listIcon = getThemeIcons( wrapper );
			for( Bitmap icon : listIcon ) {
				Bitmap scaleIcon = scaleThemeIcon( icon, iconSize, iconSize );
				canvas.drawBitmap( scaleIcon, left, top, null );
				left = left + iconSize + 5;
				if( ( iCount++ % 4 ) == 0  ) {
					top = top + iconSize + 10;
					left = 5;
					iCount = 1;
				} 
				BaseBitmapUtils.destoryBitmap( scaleIcon );
			}
			if(null == newBitmap)return false;
			BaseBitmapUtils.saveBitmap2file( newBitmap, previewPath );

			BaseBitmapUtils.destoryBitmap( wallPaper );
			BaseBitmapUtils.destoryBitmap( newBitmap );
			for( Bitmap icon : listIcon ) {
				BaseBitmapUtils.destoryBitmap( icon );
			}
			return true;
		} catch( Exception e ) { 
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 缩放图标
	 * @param icon
	 * @param scale 比率
	 * @return
	 */
	private static Bitmap scaleThemeIcon( Bitmap icon, int width, int height ) {
		Bitmap scaleIcon = Bitmap.createScaledBitmap( icon, width, height, true);
		BaseBitmapUtils.destoryBitmap( icon );
		return scaleIcon;
	}
	/**
	 * 获取主题中的所有图标
	 * @param wrapper
	 * @return
	 */
	public static List<Bitmap> getThemeIcons( ThemeResourceWrapper wrapper ) {
		List<Bitmap> list = new ArrayList<Bitmap>();
		for( String app : BaseThemeData.themeThumbApps ) {
			Drawable drawable = wrapper.getIconDrawable( app, false );
			if( null == drawable ) continue;
			if( drawable instanceof FastBitmapDrawable ) {
				list.add( ( (FastBitmapDrawable) drawable).getBitmap() );
			}else if(drawable instanceof BitmapDrawable) {
				list.add(( (BitmapDrawable) drawable).getBitmap());
			}
		}
		return list;
	}

}
