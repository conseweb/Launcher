package com.bitants.common.launcher.broadcast;

import android.content.Context;
import android.content.Intent;

import com.bitants.common.utils.ALog;

/**
 * 桌面广播静态注册父类(增加异常捕获处理)
 */
public abstract class AntBroadcastStaticReceiver extends AntBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			onReceiveHandler(context, intent);
		}catch(Exception e){
			ALog.e("Error", e, e.getMessage());
		}

	}
	
	public abstract void onReceiveHandler(Context context, Intent intent);
}
