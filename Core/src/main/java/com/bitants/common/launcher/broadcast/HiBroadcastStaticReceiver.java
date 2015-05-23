package com.bitants.common.launcher.broadcast;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 桌面广播静态注册父类(增加异常捕获处理)
 */
public abstract class HiBroadcastStaticReceiver extends HiBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			onReceiveHandler(context, intent);
		}catch(Exception e){
			Log.e("HiBroadcastStaticReceiver", e.toString());
		}

	}
	
	public abstract void onReceiveHandler(Context context, Intent intent);
}
