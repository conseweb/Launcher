package com.nd.launcherdev.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.nd.launcherdev.kitset.util.BaseBitmapUtils;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.kitset.util.StringUtil;
import com.nd.launcherdev.launcher.info.ApplicationInfo;

public class BaseAppDataFactory {
	/**
	 * Query the package manager for MAIN/LAUNCHER activities in the supplied
	 * package.
	 */
	public static List<ApplicationInfo> findActivitiesForPackage(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
		final List<ApplicationInfo> matches = new ArrayList<ApplicationInfo>();

		if (apps != null) {
			// Find all activities that match the packageName
			int count = apps.size();
			for (int i = 0; i < count; i++) {
				final ResolveInfo info = apps.get(i);
				final ActivityInfo activityInfo = info.activityInfo;
				if (!packageName.equals(activityInfo.packageName)) {
					continue;
				}

				ApplicationInfo result = resolveSimpleApplication(info);
				result.iconBitmap = BaseBitmapUtils.getDefaultAppIcon(context.getResources());
				result.usingFallbackIcon = true;
				matches.add(result);
			}
		}

		return matches;
	}
	
	protected static ApplicationInfo resolveSimpleApplication(ResolveInfo resolve) {
		ApplicationInfo info = new ApplicationInfo(resolve);
		String sourceDir = resolve.activityInfo.applicationInfo.sourceDir;
		try {
			if (!StringUtil.isEmpty(sourceDir)) {
				long installTime = new File(sourceDir).lastModified();
				info.installTime = installTime;
			}
			if ((resolve.activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
				info.isSystem = 1;
			}
		} catch (Exception e) {
			Log.e("BaseAppDataFactory", sourceDir + " can't open!");
		}
		return info;
	}
}
