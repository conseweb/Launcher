package com.bitants.launcherdev.launcher;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import com.bitants.common.launcher.broadcast.AntBroadcastStaticReceiver;
import com.bitants.launcherdev.AppController;

public class ApplicationChangeReceiver extends AntBroadcastStaticReceiver {
	
	@Override
	public void onReceiveHandler(Context ctx, Intent intent) {
		if (null == intent)
			return;
		final String action = intent.getAction();
		if ( Intent.ACTION_PACKAGE_CHANGED.equals(action) ||
             Intent.ACTION_PACKAGE_REMOVED.equals(action) ||
             Intent.ACTION_PACKAGE_ADDED.equals(action) )
        {
			// TODO: need to update the all app list
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
					// 桌面未启动的情况，删除应用程序桌面上相关图标 
					ContentResolver cr = ctx.getContentResolver();
					String where = LauncherSettings.Favorites.INTENT + " like ?";
					String[] selectionArgs = { "%" + packageName + "%" };
					cr.delete(LauncherSettings.Favorites.getContentUriNoNotify(), where, selectionArgs);
				}
			} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				if (!replacing) {
					
				} 
			}

			AppController app = ((AppController) ctx.getApplicationContext());
			if (app.mModel != null) {
				app.mModel.onReceive(ctx, intent);
			}
		}
	}
}
