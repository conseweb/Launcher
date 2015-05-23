package com.bitants.common.kitset.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.support.WallpaperHelper;
import com.bitants.common.theme.data.BaseThemeData;

/**
 * 壁纸工具类
 */
public final class WallpaperUtil {
	private static final String TAG = "WallpaperUtil";

	/**
	 * 非线程应用壁纸
	 * @param ctx
	 * @param wallpaperFileFullName
	 */
	public static void applyWallpaper(final Context ctx, final String wallpaperFileFullName) {
		if (null == wallpaperFileFullName || wallpaperFileFullName.equals(""))
			return;

		try {
			// 获取宽高
			int[] wh = BaseBitmapUtils.getImageWH(wallpaperFileFullName);
			if (ScreenUtil.isLargeScreen()) {// 大分辨率使用壁纸流加快速度
				if (wh[0] <= 960 && wh[1] <= 800) {
					WallpaperManager.getInstance(ctx).setStream(
							new FileInputStream(wallpaperFileFullName));
					return;
				}
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			int[] wallpaperWH = ScreenUtil.getWallpaperWH();
			int inSampleSize = Math.max(wh[0]/wallpaperWH[0], wh[1]/wallpaperWH[1]);
			options.inSampleSize = 1;
			options.inJustDecodeBounds = false;
			if (inSampleSize < 1) {
				inSampleSize = 1;
			}
			options.inSampleSize = inSampleSize;
			Bitmap bitmap = BitmapFactory.decodeFile(wallpaperFileFullName, options);
			if (null != bitmap) {
//				int mSrcBitmapWidth = bitmap.getWidth();
//				int mSrcBitmapHeight = bitmap.getHeight();
//				Bitmap croppedImage = Bitmap.createBitmap(bitmap, 0, 0, mSrcBitmapWidth, mSrcBitmapHeight, null, false);
//				WallpaperManager.getInstance(ctx).setBitmap(croppedImage);
				WallpaperManager.getInstance(ctx).setBitmap(bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 超大分辨率手机，需要修正因系统逻辑产生的"应用壁纸出现黑边"问题
	 */
	public static boolean needFixApplyWallpaper(Context ctx, String path){
		InputStream wallpaperInputStream = null;
		InputStream wallpaperInputStream1 = null;
		try{
			boolean needFixed = false;
			
			wallpaperInputStream = BaseBitmapUtils.getImageInputStream(ctx, null, path);
			int[] wh = BaseBitmapUtils.getImageWH(wallpaperInputStream);
			wallpaperInputStream.close();
			wallpaperInputStream = null;
			
			int width = WallpaperHelper.getInstance().getWallpaperManager().getDesiredMinimumWidth();
			int height = WallpaperHelper.getInstance().getWallpaperManager().getDesiredMinimumHeight();
			int deltaw = width - wh[0];
			int deltah = height - wh[1];
			if(deltaw > 0 || deltah > 0){
				float scale = 1.0f;
	            if (deltaw > deltah) {
	                scale = width / (float)wh[0];
	            } else {
	                scale = height / (float)wh[1];
	            }
	            int newH = (int) (wh[1] * scale);
	            if(newH < height){
	            	needFixed = true;
	            }
			}
			needFixed = true;
			if(needFixed){
				wallpaperInputStream1 = BaseBitmapUtils.getImageInputStream(ctx, null, path);
				Bitmap b = BitmapFactory.decodeStream(wallpaperInputStream1);
				float s1 = width / (float)wh[0];
				float s2 = height / (float)wh[1];
				float s = s1 > s2 ? s1 : s2;
				WallpaperManager.getInstance(ctx).setBitmap(BaseBitmapUtils.resizeImage(b, s));
			}
			
			return needFixed;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(null != wallpaperInputStream) {
				try {
					wallpaperInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != wallpaperInputStream1) {
				try {
					wallpaperInputStream1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 直接读取文件流应用壁纸
	 * @param ctx
	 * @param path
	 */
	public static void applyWallpaperDirectly(final Context ctx, final String path){
		if (null == path || path.equals(""))
			return;
		try {
			WallpaperManager.getInstance(ctx).setStream(new FileInputStream(path));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 应用壁纸(指定输入流)[参考安卓桌面的代码]
	 * @param ctx
	 * @param wallpaperInputStream1
	 * @param wallpaperInputStream2
	 */
	public static void applyWallpaperInThread(final Context ctx, final String wallpaperTag) {
		ThreadUtil.executeMore(new Runnable() {

			@Override
			public void run() {
				applyWallpaperFromTheme(ctx, wallpaperTag);
			}
		});
	}
	
	public static boolean isLiveWallpaperRunning(Context context,
			String tagetServiceName, String packageName) {
		WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(context);
		WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		if (wallpaperInfo != null && packageName != null) {
			String wallPaperPkg = wallpaperInfo.getComponent().getPackageName();
			String currentLiveWallpaperServiceName = wallpaperInfo
					.getServiceName();
			if (currentLiveWallpaperServiceName.equals(tagetServiceName)
					&& packageName.equals("" + wallPaperPkg)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 应用主题壁纸
	 * @param ctx
	 * @param wallpaperInputStream1 用于获取宽高
	 * @param wallpaperInputStream2 用于应用壁纸
	 */
	public static void applyWallpaperFromTheme(final Context ctx, final String wallpaperTag) {
		if (null == wallpaperTag)
			return;
		InputStream wallpaperInputStream1 = null;
		InputStream wallpaperInputStream2 = null;
		try {
			wallpaperInputStream1 = BaseBitmapUtils.getImageInputStream(ctx, BaseThemeData.WALLPAPER, wallpaperTag);
			if (BaseSettingsPreference.getInstance().isSupportLiveWP()
					&& isLiveWallpaperRunning(ctx, "org.cocos2dx.lib.Cocos2dxGLWallpaperService",
							"cn.com.nd.s.single.lock.livewallpaper")) {
				BaseBitmapUtils.saveStream2file(wallpaperInputStream1,
						Environment.getExternalStorageDirectory()
								+ "/PandaHome2/curWallpaper.b");
				return;
			}
			if (ScreenUtil.isLargeScreen()) {// 大分辨率使用壁纸流加快速度
				WallpaperManager.getInstance(ctx).setStream(wallpaperInputStream1);
				return;
			}
			int[] wh = BaseBitmapUtils.getImageWH(wallpaperInputStream1);
			wallpaperInputStream1.close();
			Log.e(TAG, "wh[0]:" + wh[0] + "wh[1]:" + wh[1]);
			BitmapFactory.Options options = new BitmapFactory.Options();
			int[] wallpaperWH = ScreenUtil.getWallpaperWH();
			Log.e(TAG, "wallpaperWH[0]:" + wallpaperWH[0] + "wallpaperWH[1]:" + wallpaperWH[1]);
			int inSampleSize = Math.max(wh[0] / wallpaperWH[0], wh[1] / wallpaperWH[1]);
			options.inSampleSize = 1;
			options.inJustDecodeBounds = false;
			if (inSampleSize < 1) {
				inSampleSize = 1;
			}
			options.inSampleSize = inSampleSize;
			Log.e(TAG, "options.inSampleSize:" + inSampleSize);
			wallpaperInputStream2 = BaseBitmapUtils.getImageInputStream(ctx, BaseThemeData.WALLPAPER, wallpaperTag);
			Bitmap bitmap = BitmapFactory.decodeStream(wallpaperInputStream2, null, options);
			if (null != bitmap) {
				bitmap = BaseBitmapUtils.toSizeBitmap(bitmap, wallpaperWH[0], wallpaperWH[1]);
				WallpaperManager.getInstance(ctx).setBitmap(bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != wallpaperInputStream2) {
				try {
					wallpaperInputStream2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * 在高清分辨率上应用壁纸
	 * @param ctx
	 * @param wallpaper
	 */
	public static void applyLargeScreenWallpaper(final Context ctx, Bitmap wallpaper){
		try {
			int[] screenWH = ScreenUtil.getScreenWH();
			int mSrcBitmapWidth = wallpaper.getWidth();
			int mSrcBitmapHeight = wallpaper.getHeight();
			float scaleSrc = Math.max((float) screenWH[0] / mSrcBitmapWidth, (float) screenWH[1] / mSrcBitmapHeight);
			Matrix matrix = new Matrix();
			matrix.postScale(scaleSrc, scaleSrc);
			WallpaperManager.getInstance(ctx).setBitmap(Bitmap.createBitmap(wallpaper, 0, 0, mSrcBitmapWidth, mSrcBitmapHeight, matrix, true));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	/**
	 * 转换分辨率适配服务器壁纸后台要求
	 * @param realWH
	 * realWH[0] 宽
	 * realWH[1] 高
	 * @return int[]
	 */
	public static int[] CoverToNetWallPaperWH(int[] realWH){
		
		if (realWH==null || realWH.length<2){
			int[] defaultWH = { 320, 480 };
			return defaultWH;			
		}
		
		if (realWH[1]==1184 || realWH[1]==1280 || realWH[1]==1208) {
			realWH[1]=1280;
			if (realWH[0]==768) {
				realWH[0]=800;
			}
		}else if(realWH[1]==1776 || realWH[1]==1920){
			realWH[0]= 720;
			realWH[1]= 1280;
		}
		
		return realWH;
	}
	
	/**
	 * 转换分辨率适配服务器壁纸后台要求
	 * @param width
	 * @param height
	 * @return int[]
	 */
	public static int[] CoverToNetWallPaperWH(int width, int height){
		
		int[] realWH = { width, height };
		return CoverToNetWallPaperWH(realWH);
	}
	
	/**
	 * 滤镜存放目录
	 */
	public final static String FILTERS_HOME = BaseConfig.WALLPAPER_BASE_DIR + "/Filters/";
	
	/**
	 * 获取本地壁纸原图目录（全路径）
	 * 
	 * @return
	 */
	public static String getWPPicHome() {
		return FileUtil.getPath(BaseConfig.PICTURES_HOME);
	}
	
	
	/**
	 * 获取滤镜目录（全路径）
	 * 
	 * @return
	 */
	public static String getFilterPicHome() {
		return FileUtil.getPath(FILTERS_HOME);
	}
	
	/**
	 * 描述:下载生成缩略图
	 * 
	 * @param url
	 * @param fileName
	 * @param savePath
	 * @return
	 */
	public static boolean downLoadImageAsThumb(URL url, int thumbWidth, int thumbHeight, String fileName, String savePath) {
		String tempFileName = "";
		if (url != null && savePath != null) {
			try {

				fileName = fileName == null ? FileUtil.getFileName(url.toString(), true) : fileName;
				tempFileName = fileName + ".temp";

				String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
				if (ext.equals("JPG")) {
					ext = "JPEG";
				}

				File f = new File(savePath + "/" + tempFileName);
				if (f.exists()) {
					f.delete();
				}
				URLConnection con = url.openConnection();
				// 连接超时时间设置为8秒
				con.setConnectTimeout(8 * 1000);
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6");
				InputStream is = con.getInputStream();
				if (con.getContentEncoding() != null && con.getContentEncoding().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(con.getInputStream());
				}
				OutputStream os = new FileOutputStream(f);
				Bitmap bitmap = null;
				try {
					bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(is), thumbWidth, thumbHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
					bitmap.compress(Bitmap.CompressFormat.valueOf(ext), 100, os);
					os.flush();
				} finally {
					try {
						os.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					os = null;
					is = null;
					con = null;
					url = null;
					FileUtil.renameFile(savePath + "/" + tempFileName, savePath + "/" + fileName);
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				File file = new File(savePath + "/" + tempFileName);
				if (file.exists()) {
					FileUtil.delFile(file.getAbsolutePath());
				}
				return false;
			}
		} else {
			return false;
		}
	}// end downLoadImageOnline
	
	/**
	 * 描述:下载网络图片
	 * 
	 * @param url
	 *            //图片的URL
	 * @param fileName
	 *            //要保存到本地的文件名
	 * @param savePath
	 *            //要保存的目录全路径
	 * @return
	 */
	public static boolean downLoadImageOnline(URL url, String fileName, String savePath) {
		String tempFileName = "";
		if (url != null && savePath != null) {
			try {

				fileName = fileName == null ? FileUtil.getFileName(url.toString(), true) : fileName;
				tempFileName = fileName + ".temp";
				File f = new File(savePath + "/" + tempFileName);
				if (!f.exists()) {
					URLConnection con = url.openConnection();
					// 连接超时时间设置为8秒
					con.setConnectTimeout(8 * 1000);
					con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6");
					// HttpURLConnection conn = (HttpURLConnection) con;
					// if (conn.getResponseCode() ==
					// java.net.HttpURLConnection.HTTP_NOT_FOUND) {
					// return java.net.HttpURLConnection.HTTP_NOT_FOUND;
					// }
					InputStream is = con.getInputStream();
					if (con.getContentEncoding() != null && con.getContentEncoding().equalsIgnoreCase("gzip")) {
						is = new GZIPInputStream(con.getInputStream());
					}
					byte[] bs = new byte[256];
					int len = -1;
					OutputStream os = new FileOutputStream(f);
					try {
						while ((len = is.read(bs)) != -1) {
							os.write(bs, 0, len);
						}
						// os.flush();
					} finally {
						try {
							os.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						try {
							is.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						os = null;
						is = null;
						con = null;
						url = null;
						FileUtil.renameFile(savePath + "/" + tempFileName, savePath + "/" + fileName);
					}
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				File file = new File(savePath + "/" + tempFileName);
				if (file.exists()) {
					FileUtil.delFile(file.getAbsolutePath());
				}
				return false;
			}
		} else {
			return false;
		}
	}// end downLoadImageOnline


	/**
	 * 描述:下载生成缩略图
	 * 
	 * @param url
	 * @param fileName
	 * @param savePath
	 * @return
	 */
	public static boolean downLoadImageAsThumb(URL url, String fileName, String savePath) {
		return downLoadImageAsThumb(url, 142, 118, fileName, savePath);
	}

	/**
	 * 描述:通过原图生成缩略图片
	 * 
	 * @param filePath
	 * @param thumbPath
	 * @return
	 */
	public static boolean createThumb(String sourcePath, String thumbPath) {
		if (!StringUtil.isEmpty(sourcePath)) {
			try {
				String ext = sourcePath.substring(sourcePath.lastIndexOf(".") + 1).toUpperCase();
				if (ext.equals("JPG")) {
					ext = "JPEG";
				}

				// 如果目标地址为空,则在原目录下建 一个
				if (StringUtil.isEmpty(thumbPath)) {
					String path = sourcePath.substring(0, sourcePath.lastIndexOf("/"));
					String fileName = FileUtil.getFileName(sourcePath, true);
					thumbPath = path + "/.thumb/" + fileName;
				}

				File f = new File(thumbPath);

				if (!f.exists()) {
					InputStream is = new FileInputStream(sourcePath);
					OutputStream os = new FileOutputStream(f);
					Bitmap bitmap = null;
					try {
						bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(is), 178, 118);
						bitmap.compress(Bitmap.CompressFormat.valueOf(ext), 100, os);
						os.flush();
					} finally {
						try {
							os.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						try {
							is.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						os = null;
						is = null;
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
						}
					}
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}// end downLoadImageOnline

	/**
	 * 获取下载的本地原图列表
	 * 
	 * @return
	 */
	public static List<String> getExistsWallpapers() {
		String path = WallpaperUtil.getWPPicHome();
		return getExistsFileNames(path, FileUtil.imagefileFilter, true);
	}
	
	private static List<String> getExistsFileNames(String dir, FileFilter fileFilter, boolean hasSuffix) {
		String path = dir;
		File file = new File(path);
		File[] files = file.listFiles(fileFilter);
		List<String> fileNameList = new ArrayList<String>();
		if (null != files) {
			Arrays.sort(files, new CompratorByLastModified());
			for (File tmpFile : files) {
				String tmppath = tmpFile.getAbsolutePath();
				String fileName = FileUtil.getFileName(tmppath, hasSuffix);
				fileNameList.add(fileName);
			}
		}
		return fileNameList;
	}
	
	static class CompratorByLastModified implements Comparator<File> {
		public int compare(File f1, File f2) {
			long diff = f1.lastModified() - f2.lastModified();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}

		public boolean equals(Object obj) {
			return true;
		}
	}
	/**
	 * 获取本地壁纸缩略图目录（全路径）
	 * 
	 * @return
	 */
	public static String getWPPicHomeCachePath() {
		String path = BaseConfig.getBaseDir() + "/myphone/wallpaper/.cache/local/thumb/";
		return FileUtil.getPath(path);
	}
	

}
