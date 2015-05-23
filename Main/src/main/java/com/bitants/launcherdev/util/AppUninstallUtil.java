package com.bitants.launcherdev.util;

import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.launcherdev.launcher.Launcher;


/**
 * 应用卸载工具，仅供桌面视图使用！
 * 匣子，文件夹等
 */
public class AppUninstallUtil {

	/**
	 * 在Launcher进程里卸载应用程序
	 * @param mLauncher
	 * @param packageName
	 */
	public static void uninstallAppByLauncher(Launcher mLauncher, String packageName) {
		if (mLauncher == null)
			return;

		mLauncher.setUninstallPackageName(packageName);
		AndroidPackageUtils.uninstallApp(mLauncher, packageName);
	}

}
