package com.bitants.settings;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bitants.common.utils.ALog;


public class LockReceiver extends DeviceAdminReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ALog.i("--->DeviceAdminReceiver !!", "1");
		super.onReceive(context, intent);
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		ALog.i("--->DeviceAdminReceiver !!", "2");
		super.onEnabled(context, intent);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		ALog.i("--->DeviceAdminReceiver !!", "3");
		super.onDisabled(context, intent);
	}

}