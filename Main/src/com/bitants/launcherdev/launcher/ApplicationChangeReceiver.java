package com.bitants.launcherdev.launcher;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.bitants.launcherdev.launcher.broadcast.HiBroadcastStaticReceiver;

public class ApplicationChangeReceiver extends HiBroadcastStaticReceiver {
	
	@Override
	public void onReceiveHandler(Context ctx, Intent intent) {
		if (null == intent)
			return;
		final String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			final String packageName = intent.getData().getSchemeSpecificPart();
			final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

			if (packageName == null || packageName.length() == 0) {
				return;
			}
			
			if (ctx.getPackageName().equals(packageName)) {
				return;
			}

			if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
				
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				if (!replacing) {
					// 桌面未启动的情况，删除应用程序桌面上相关图标 caizp 2013-3-3
					ContentResolver cr = ctx.getContentResolver();
					String where = LauncherSettings.Favorites.INTENT + " like ?";
					String[] selectionArgs = { "%" + packageName + "%" };
					cr.delete(LauncherSettings.Favorites.getContentUriNoNotify(), where, selectionArgs);
				}
			} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				if (!replacing) {
					
				} 
			}

			LauncherApplication app = ((LauncherApplication) ctx.getApplicationContext());
			if (app.mModel != null) {
				app.mModel.onReceive(ctx, intent);
			}
		}
	}
}
