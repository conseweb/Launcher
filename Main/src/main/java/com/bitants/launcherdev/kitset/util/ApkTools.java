package com.bitants.launcherdev.kitset.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.launcher.R;
import com.bitants.common.framework.ViewFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * APK包工具 类
 */
public class ApkTools {

	/**
	 * 在Assets下查找并安装 
	 * @param ctx
	 * @param pckApk
	 *            assest下的文件名，使用包名即可
	 * @param title
	 *            提示的Title
	 * @return boolean
	 */
	public static boolean installAssestApk(final Context ctx, final String pckApk, final String title) {
		return installAssestApk(ctx, pckApk, -1, ctx.getText(R.string.common_tip), ctx.getString(R.string.panda_widget_confirm_install, title), ctx.getText(R.string.common_button_confirm),
				ctx.getText(R.string.common_button_cancel));
	}

	/**
	 * 带apk信息提示的安装对话框
	 * @param ctx
	 * @param pckApk
	 * @param title
	 * @param tip
	 * @return boolean
	 */
	public static boolean installAssestApk(final Context ctx, final String pckApk, final int icon, final CharSequence title, final CharSequence tip, final CharSequence positive,
			final CharSequence negavite) {
		AssetManager am = ctx.getAssets();
		String[] files;
		try {
			files = am.list("");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		if (files == null)
			return false;

		boolean hasApk = false;
		for (String apk : files) {
			if (apk.equalsIgnoreCase(pckApk)) {
				hasApk = true;
				break;
			}
		}

		if (hasApk) {
			ViewFactory.getAlertDialog(ctx, icon, title, tip, positive, negavite, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ApkTools.copyAndInstallPkg(ctx, pckApk);
				}
			}, null).show();
			return true;
		}

		return false;
	}

	/**
	 * 从Assets安装一个应用
	 * @param context
	 * @param fileName
	 */
	public static void copyAndInstallPkg(Context context, String fileName) {
		AssetManager am = context.getAssets();
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = am.open(fileName);
			fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1)
				fos.write(buffer, 0, len);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// 安装APK
		installApplication(context, new File(context.getFileStreamPath(fileName).getAbsolutePath()));
	}

	/**
	 * 安装应用程序(必须在UI线程中调用，否则会出错)
	 * @param ctx
	 * @param mainFile
	 */
	public static void installApplication(Context ctx, File mainFile) {
		ApkInstaller.installApplicationShoudSilent(ctx, mainFile);
		/*
		 * try { Uri data = Uri.fromFile(mainFile); Intent intent = new
		 * Intent(Intent.ACTION_VIEW);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * intent.setDataAndType(data,
		 * "application/vnd.android.package-archive");
		 * ctx.startActivity(intent); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

	/**
	 * 利用反射机制构建一个未安装apk的Resource对象，来访问apk内的资源
	 * @param apkPath
	 * @param ctx
	 * @return 返回apk的Resource对象，异常时返回null
	 */
	public static Resources getUninstallAPKResources(String apkPath, Context ctx) {
		String PATH_AssetManager = "android.content.res.AssetManager";
		try {
			Class<?>[] typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Object[] valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			typeArgs = new Class[4];
			typeArgs[0] = File.class;
			typeArgs[1] = String.class;
			typeArgs[2] = DisplayMetrics.class;
			typeArgs[3] = Integer.TYPE;
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);
			Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[]) null);
			Object assetMag = assetMagCt.newInstance((Object[]) null);
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			Resources res = ctx.getResources();
			typeArgs = new Class[3];
			typeArgs[0] = assetMag.getClass();
			typeArgs[1] = res.getDisplayMetrics().getClass();
			typeArgs[2] = res.getConfiguration().getClass();
			Constructor<?> resCt = Resources.class.getConstructor(typeArgs);
			valueArgs = new Object[3];
			valueArgs[0] = assetMag;
			valueArgs[1] = res.getDisplayMetrics();
			valueArgs[2] = res.getConfiguration();
			res = (Resources) resCt.newInstance(valueArgs);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 是否已安装apk
	 * @param ctx
	 * @param apkFile apk包文件夹路径
	 * @return 已安装返回true，未安装或异常时返回false
	 */
	public static boolean hasInstallApk(Context ctx, String apkFile) {
		if (StringUtil.isEmpty(apkFile))
			return false;

		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pInfo_Uninstall = pm.getPackageArchiveInfo(apkFile, 0);
			PackageInfo pInfo_Install = pm.getPackageInfo(pInfo_Uninstall.packageName, 0);
			if (pInfo_Uninstall.versionCode == pInfo_Install.versionCode)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 获取apk包信息
	 * @param ctx
	 * @param apkFile apk包文件夹路径
	 * @return 没有获取到或异常时返回null
	 */
	public static android.content.pm.PackageInfo getPackageByArchiveFile(Context ctx, String apkFile) {
		if (StringUtil.isEmpty(apkFile))
			return null;
		final PackageManager pm = ctx.getPackageManager();
		final android.content.pm.PackageInfo pInfo = pm.getPackageArchiveInfo(apkFile, 0);
		if (pInfo != null)
			return pInfo;
		return null;
	}

	/**
	 * 判断apk文件是否能安装
	 * @param context
	 * @param apkFile APK文件的路径
	 * @return boolean
	 */
	public static boolean checkApkIfValidity(Context context, String apkFile) {
		if (StringUtil.isEmpty(apkFile))
			return false;
		final PackageManager pm = context.getPackageManager();
		final android.content.pm.PackageInfo pInfo = pm.getPackageArchiveInfo(apkFile, 0);
		if (pInfo == null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 获取未安装APK的版本信息
	 * @param context
	 * @param archiveFilePath APK文件的路径。如：/sdcard/mirrorhome/Downloads/XX.apk
	 * @return 成功返回版本号，失败返回-1
	 */
	public static int getUninatllApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			return info.versionCode;
		}
		return -1;
	}

	/**
	 * 判断安装的应用与apk包是否是同一个应用
	 * @param context
	 * @param packageName 安装的应用的包名
	 * @param apkFilePath apk包的路径
	 * @return boolean
	 */
	public static boolean isTheSameApp(Context context, String packageName, String apkFilePath) {
		try {
			PackageManager pm = context.getPackageManager();
			// 安装的应用信息
			PackageInfo installedPkgInfo = pm.getPackageInfo(packageName, 0);
			// APK包的应用信息
			final PackageInfo dlkPkgInfo = ApkTools.getPackageByArchiveFile(context, apkFilePath);
			if (installedPkgInfo.packageName.equals(dlkPkgInfo.packageName) && installedPkgInfo.versionCode == dlkPkgInfo.versionCode)
				return true;
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 获取安装包的唯一结识
	 * @param context
	 * @param apkFilePath
	 * @return 包名+版本号
	 */
	public static String getApkFileKey(Context context, String apkFilePath) {
		String key = null;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, 0);
			key = info.packageName + info.versionCode;
		} catch (Exception e) {
		}

		if (TextUtils.isEmpty(key))
			key = apkFilePath;

		return key;
	}

	/**
	 * 获取apk包的PackageInfo
	 * @param context
	 * @param apkFilePath
	 * @return 没有获取到或异常时返回null
	 */
	public static PackageInfo getApkFilePackageInfo(Context context, String apkFilePath) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, 0);
			return info;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 重写未安装方法
	 * @param ctx
	 * @param packname
	 * @param title
	 * @param text -1 = 不升级 0 = 升级 1 = 一键关屏升级 2= 一键关屏安装3 = 二维码扫描升级 4= 二维码扫描安装
	 * 
	 */
	public static void installWidgetApp(final Context ctx, final String packname, CharSequence title, CharSequence text) {
		AssetManager am = ctx.getAssets();
		String[] files;
		try {
			files = am.list("");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (files == null)
			return;

		boolean hasApk = false;
		for (String apk : files) {
			if (apk.equalsIgnoreCase(packname)) {
				hasApk = true;
				break;
			}
		}

		if (hasApk) {
			ViewFactory.getAlertDialog(ctx, -1, title, text, 
					ctx.getText(R.string.common_install_now), ctx.getText(R.string.common_button_cancel), 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							copyAndInstallPkg(ctx, packname);
						}
					}, null).show();
		}
	}
	
	/**
	 * 加载apk默认icon
	 * @param ctx
	 * @param packName
	 * @return
	 */
	public static Drawable loadApkIcon(Context ctx, String packName) {
		if (StringUtil.isEmpty(packName) || !AndroidPackageUtils.isPkgInstalled(ctx, packName)) {
			return null;
		}
		
		PackageManager pm = ctx.getPackageManager();
		try {
			return pm.getApplicationIcon(packName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
