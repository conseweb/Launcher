package com.nd.launcherdev.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.nd.launcherdev.kitset.util.AndroidPackageUtils;
import com.nd.launcherdev.kitset.util.BaseBitmapUtils;
import com.nd.launcherdev.kitset.util.ReflectUtil;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.support.BaseIconCache;
import com.nd.launcherdev.theme.ThemeManagerFactory;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.support.BaseIconCache;
import com.nd.launcherdev.theme.ThemeManagerFactory;

/**
 * 所有主题都放在一个文件夹中根据文件名称来区分不同的主题
 * 
 * @author Michael Date:2014-4-24下午4:51:47
 * 
 */
public class DiskCache {

	private static DiskCache instance;

	public static final String APK_TYPE = "1";

	public static final String OTHER_ICON_TYPE = "2";

	private static final String SPLIT_STRING = ";";

	private static final long fixLastModifiedTime = 1;

	/**
	 * 缓存文件存在于sd卡上的路径
	 */
	private static String cachePath = BaseConfig.getBaseDir() + "/.cache/icons/";

	/**
	 * 缓存数据信息
	 */
	private ConcurrentHashMap<String, DiskInfo> cacheDataInfo = new ConcurrentHashMap<String, DiskInfo>();

	/**
	 * @author Michael Date:2014-4-25
	 * @return
	 */
	public static DiskCache getInstance() {
		if (instance == null) {
			instance = new DiskCache();
		}
		return instance;
	}

	/**
	 * 初始化缓存信息
	 */
	private DiskCache() {
		initCacheDataInfo();
	}

	/**
	 * 从disk缓存中获取图标
	 * 
	 * @author Michael Date:2014-4-25
	 * @param iconType
	 * @param key
	 * @param themeName
	 * @param info
	 * @return
	 */
	public Bitmap getBitmapFromDiskCache(String iconType, String key, String themeName, ResolveInfo info, BaseIconCache iconCache, boolean useIconMask) {
		if (iconType.equals(OTHER_ICON_TYPE)) {
			return getBitmapFromDiskCacheByOtherIconType(key, themeName, iconCache, useIconMask);
		} else if (iconType.equals(APK_TYPE)) {// apk类型图片
			return getBitmapFromDiskCacheByApkIconType(key, themeName, info, iconCache, useIconMask);
		}
		return iconCache.getmDefaultIcon();
	}

	/**
	 * 从diskcache中获取图标: 快捷、我的手机、推荐等图标类型
	 * 
	 * @author Michael Date:2014-4-25
	 * @param key
	 * @param themeName
	 * @return
	 */
	private Bitmap getBitmapFromDiskCacheByOtherIconType(final String key, final String themeName, BaseIconCache iconCache, boolean useIconMask) {
		DiskInfo diskInfo = (DiskInfo) cacheDataInfo.get(key);
		if (diskInfo != null) {// 在缓存文件中
			try {
				File file = new File(cachePath + diskInfo.fileName);
				if (file.exists()) {
					if (!StringUtil.isEmpty(themeName) && themeName.equals(diskInfo.themename)) {// 主题吻合
						Bitmap bitmap = BitmapFactory.decodeFile(cachePath + diskInfo.fileName);
						return bitmap;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 需要重新生成图片 并将信息存入缓存Map中
		Drawable drawable = ThemeManagerFactory.getInstance().getCurrentTheme().getWrapper().getDefaultResource(key);
		if (drawable != null) {
			Bitmap bitmap = BaseBitmapUtils.drawable2Bitmap(drawable);
			final Bitmap newBitmap = BaseBitmapUtils.createIconBitmapFor91Icon(bitmap, BaseConfig.getApplicationContext());
			if (newBitmap != null) {// 将bitmap写入到sd卡中
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						FileOutputStream fileOutputStream = null;
						try {
							String fileName = createCacheFileName(OTHER_ICON_TYPE, key, themeName);
							File file = new File(cachePath + fileName);
							if (!file.exists()) {
								file.createNewFile();
							}
							fileOutputStream = new FileOutputStream(file);
							newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
							fileOutputStream.flush();
							// 将信息记录到Map中
							isValidCacheFileName(fileName, cacheDataInfo, true);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (fileOutputStream != null) {
								try {
									fileOutputStream.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

				};
				DiskCacheTaskManager.getInstance().postTask(key, runnable);
			}
			return newBitmap;
		}
		return iconCache.getmDefaultIcon();
	}

	/**
	 * 从diskcache中获取图标: apk图标类型
	 * 
	 * @author Michael Date:2014-4-25下午2:47:20
	 * @param key
	 * @param themeName
	 * @return
	 */
	private Bitmap getBitmapFromDiskCacheByApkIconType(final String key, final String themeName, final ResolveInfo info, BaseIconCache iconCache,
			boolean useIconMask) {
		DiskInfo diskInfo = (DiskInfo) cacheDataInfo.get(key);
		final PackageManager mPackageManager = iconCache.getmPackageManager();
		if (diskInfo != null) {// 在缓存文件中
			try {
				File file = new File(cachePath + diskInfo.fileName);
				if (file.exists()) {
					if (!StringUtil.isEmpty(themeName) && themeName.equals(diskInfo.themename)
							&& apkFileLastModifiedIsMatch(diskInfo.lastmodifiedTime, info, mPackageManager)) {// 主题吻合
						Bitmap bitmap = BitmapFactory.decodeFile(cachePath + diskInfo.fileName);
						return bitmap;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 需要重新生成图片 并将信息存入缓存Map中
		Drawable drawable = iconCache.loadDrawable(info);
		if (drawable != null) {
			Bitmap bitmap = BaseBitmapUtils.createIconBitmapThumbnail(drawable, BaseConfig.getApplicationContext());
			final Bitmap newBitmap = BaseBitmapUtils.createIconBitmapForApkIcon(bitmap, BaseConfig.getApplicationContext());
			if (newBitmap != null) {// 将bitmap写入到sd卡中
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						FileOutputStream fileOutputStream = null;
						try {
							String fileName = createCacheFileName(APK_TYPE, key, themeName, info, mPackageManager);
							File file = new File(cachePath + fileName);
							if (!file.exists()) {
								file.createNewFile();
							}
							fileOutputStream = new FileOutputStream(file);
							newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
							fileOutputStream.flush();
							// 将信息记录到Map中
							isValidCacheFileName(fileName, cacheDataInfo, true);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (fileOutputStream != null) {
								try {
									fileOutputStream.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

				};

				DiskCacheTaskManager.getInstance().postTask(key, runnable);
			}
			return newBitmap;
		}
		return iconCache.getmDefaultIcon();
	}

	/**
	 * 缓存文件中的记录的apk安装时间与实际的apk安装时间进行比对 符合返回true
	 * 
	 * @author Michael Date:2014-4-25
	 * @param cacheFileRecordLastModifedTime
	 * @return
	 */
	private static boolean apkFileLastModifiedIsMatch(String cacheFileRecordLastModifedTime, ResolveInfo resolveInfo, PackageManager mPackageManager) {
		try {
			return apkFileLastModifiedIsMatch(Long.parseLong(cacheFileRecordLastModifedTime), resolveInfo, mPackageManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 缓存文件中的记录的apk安装时间与实际的apk安装时间进行比对 符合返回true
	 * 
	 * @author Michael Date:2014-4-25下午3:00:11
	 * @param cacheFileRecordLastModifedTime
	 * @return
	 */
	private static boolean apkFileLastModifiedIsMatch(long cacheFileRecordLastModifedTime, ResolveInfo resolveInfo, PackageManager mPackageManager) {
		long time = getApkInstallTime(resolveInfo, mPackageManager);
		// 当2.2及以下需要从sourcedir中获取文件 有的系统apk未root获取不到的情况下返回0
		if (time == 0 || cacheFileRecordLastModifedTime == time) {
			return true;
		}
		return false;
	}

	/**
	 * 获取apk安装时间
	 * 
	 * @author Michael Date:2014-4-25下午3:40:29
	 * @param resolveInfo
	 * @return
	 */
	public static long getApkInstallTime(ResolveInfo resolveInfo, PackageManager mpaManager) {
		if (resolveInfo == null) {
			return -1;
		}
		// android 2.3以上根据ApplicationInfo中的firstInstallTime字段
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				PackageInfo packageInfo = mpaManager.getPackageInfo(resolveInfo.activityInfo.packageName, 0);
				long lastModifiedTime = (Long) ReflectUtil.getFieldValueByFieldName(packageInfo, "lastUpdateTime");
				return lastModifiedTime;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// 2.2及以下需要从sourcedir中获取文件 有的系统apk未root获取不到 我们默认时间是正确的
			String fileName = resolveInfo.activityInfo.applicationInfo.sourceDir;
			try {
				File file = new File(fileName);
				if (file.exists()) {
					long lastModifiedTime = file.lastModified();
					return lastModifiedTime;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
		return -1;
	}

	/**
	 * 创建缓存文件名
	 * 
	 * @author Michael Date:2014-4-25下午2:27:45
	 * @param iconType
	 * @param key
	 * @param themeName
	 * @return
	 */
	public static String createCacheFileName(String iconType, String key, String themeName) {
		return iconType + SPLIT_STRING + key + SPLIT_STRING + fixLastModifiedTime + SPLIT_STRING + themeName;
	}

	/**
	 * 创建Apk缓存文件名
	 * 
	 * @author Michael Date:2014-4-25
	 * @param iconType
	 * @param key
	 * @param themeName
	 * @return
	 */
	public static String createCacheFileName(String iconType, String key, String themeName, ResolveInfo resolveInfo, PackageManager mPackageManager) {
		long lastModifiedTime = getApkInstallTime(resolveInfo, mPackageManager);
		if (lastModifiedTime == 0 || lastModifiedTime == -1) {
			lastModifiedTime = fixLastModifiedTime;
		}
		return iconType + SPLIT_STRING + key + SPLIT_STRING + lastModifiedTime + SPLIT_STRING + themeName;
	}

	/**
	 * @author Michael Date:2014-4-24
	 * @param componentName
	 * @return
	 */
	public static String getCacheFilename(ComponentName componentName) {
		if (componentName == null)
			return null;
		String packageName = componentName.getPackageName();
		String shortClassName = componentName.getShortClassName();
		return packageName + "_" + shortClassName;
	}

	/**
	 * 初始化cacheInfo信息
	 * 
	 * @author Michael Date:2014-4-24
	 */
	private void initCacheDataInfo() {
		try {
			// Debug.startMethodTracing("initCacheDataInfo");
			// FileUtil.createDir(BaseConfig.BASE_DIR);
			File file = new File(cachePath);
			if (!file.exists()) {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdir();
				}
				file.mkdir();
			}
			if (file.isDirectory()) {
				for (File subFile : file.listFiles()) {
					isValidCacheFile(subFile, cacheDataInfo);
				}
			} else {
				file.delete();
				file.mkdir();
			}
			// Debug.stopMethodTracing();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否是有效的缓存文件 如果不是则删除
	 * 
	 * @author Michael Date:2014-4-25
	 * @param file
	 * @return
	 */
	public static boolean isValidCacheFile(File file, ConcurrentHashMap<String, DiskInfo> map) {
		try {
			if (file != null) {
				if (file.isDirectory()) {// 是文件夹删除
					file.delete();
					return false;
				}

				final String name = file.getName();
				// Log.e(APK_TYPE, name);
				DiskCacheTaskManager.getInstance().postCheckFileValid(name, new Runnable() {

					@Override
					public void run() {
						// Log.e(OTHER_ICON_TYPE, name);
						String strs[] = name.split(SPLIT_STRING, 4);
						if (strs.length < 4) {
							File file = new File(cachePath + name);
							if (file.exists()) {
								file.delete();
							}
						}
						try {
							String iconType = strs[0];
							if (APK_TYPE.equals(iconType)) { // apk类型的图标
								String fileName = strs[1];
								int index = fileName.indexOf("_.");
								if (index != -1) {
									String packageName = fileName.substring(0, index).replace("_", ".");
									// Log.e(APK_TYPE, packageName);
									if (!AndroidPackageUtils.isPkgInstalled(BaseConfig.getApplicationContext(), packageName)) {
										File file = new File(cachePath + name);
										if (file.exists()) {
											file.delete();
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
				if (isValidCacheFileName(name, map, false)) {// 无效的缓存文件
					return true;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 0:icontype 1:filename 2:lastmodifiedTime 3:themename 是否是有效的缓存文件名称
	 * 
	 * @author Michael Date:2014-4-25上午11:23:47
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static boolean isValidCacheFileName(String name, ConcurrentHashMap<String, DiskInfo> map, boolean deleteOtherFile) {
		if (StringUtil.isEmpty(name)) {
			return false;
		}

		String strs[] = name.split(SPLIT_STRING, 4);
		if (strs.length < 4) {
			return false;
		}
		try {
			String iconType = strs[0];
			if (APK_TYPE.equals(iconType) || OTHER_ICON_TYPE.equals(iconType)) { // apk类型的图标
				String key = strs[1];
				if (deleteOtherFile) { // 已存在相同key的文件
					DiskInfo diskInfo = map.get(key);
					if (diskInfo != null) {
						File file = new File(cachePath + diskInfo.fileName);
						if (file.exists()) {
							file.delete();
						}
					}
				}
				DiskInfo diskInfo = new DiskInfo();
				diskInfo.fileName = name;
				diskInfo.iconType = iconType;
				diskInfo.themename = strs[3];
				String apkLastModifiedTimeInMS = strs[2];
				diskInfo.lastmodifiedTime = Long.parseLong(apkLastModifiedTimeInMS);
				map.put(key, diskInfo);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
