package com.bitants.launcherdev.launcher.support;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;
import com.bitants.launcherdev.launcher.config.LauncherConfig;

/**
 * 间隔8小时发送一次活跃度统计
 */
public class UserStateService extends Service {
	/**
	 * 一分钟
	 */
	final static int ONE_MIN_MILLISECOND = 60000;
	/**
	 * 一小时对应的毫秒数
	 */
	private final static int ONE_HOUR_MILLISECOND = 3600000;
	private static final String TAG = "Hello 91Launcher";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		long updateTime = ONE_HOUR_MILLISECOND * 8;
		
		Intent updateIntent = new Intent();
		updateIntent.setClass(this, UserStateService.class);
		PendingIntent pending = PendingIntent.getService(this, 0, updateIntent, 0);

		Time time = new Time();
		long nowMillis = System.currentTimeMillis();
		time.set(nowMillis + updateTime);
		long updateTimes = time.toMillis(true);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, updateTimes, pending);
		
		// 用户统计数据上传
		LauncherConfig.getLauncherHelper().startUpHiAnalytics(this);
		
		Log.e(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
}
