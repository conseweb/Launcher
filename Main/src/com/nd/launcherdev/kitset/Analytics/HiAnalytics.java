package com.nd.launcherdev.kitset.Analytics;

import android.content.Context;
import android.util.Log;

import com.nd.analytics.NdAnalytics;
import com.nd.analytics.NdAnalyticsSettings;
import com.nd.launcherdev.framework.httplib.DxHttpClient;
import com.nd.launcherdev.kitset.util.TelephoneUtil;
import com.nd.launcherdev.kitset.util.ThreadUtil;
import com.nd.launcherdev.framework.httplib.DxHttpClient;
import com.nd.launcherdev.kitset.util.TelephoneUtil;

/**
 * 功能统计封装类
 */
public class HiAnalytics {
	private static int init = -1;
	private static final String TAG = "HiAnalytics";
	/*
	 * 用户统计数据上传后的广播
	 */
	public static final String START_UP_ACTION = "com.nd.analytics.startup";
	public static final String START_UP_EXTRA = "startup_result";

	/**
	 *区别分支版本升级统计key
	 */
	public static final String CONST_STRING_KEY_ANALYTICS_WAY = "key_analytics_way";
	
	/**
	 * 产品ID,暂用熊猫2.7的id
	 */
	public static final int AppId = 114556;

	/**
	 * 产品Key,暂用熊猫2.7的key
	 */
	public static final String AppKey = "1a17861612dd64dbb94bdeece97c6c086f2b54c6cb7abdfb";


	/**
	 * 统计分析初始化
	 * @param context
	 *            不能使用Application Context
	 */
	public static void init(Context context) {
		if (init != -1)
			return;
		
		// 初始化数据分析
		NdAnalyticsSettings settings = new NdAnalyticsSettings();
		settings.setAppId(AppId);
		settings.setAppKey(AppKey);

		
		NdAnalytics.setReportStartupOnlyOnceADay(true);
		NdAnalytics.initialize(context, settings);
		
		init = 1;
		Log.e(TAG, "=============================HiAnalytics.init=============================");
	}


	/**
	 * 
	 * 提交统计事件
	 * @param context
	 * @param eventId
	 *            事件key_id，小于50个字符
	 * @param label
	 *            事件描述信息，小于50个字符
	 */
	@Deprecated
	public static void submitEvent(Context context, String eventId, String label) {
//		NdAnalytics.onEvent(context, eventId, label);
	}

	/**
	 * 提交统计事件
	 * @param context
	 * @param eventId
	 *            事件key_id，小于50个字符
	 */
	@Deprecated
	public static void submitEvent(Context context, String eventId) {
//		submitEvent(context, eventId, "");
	}

	
	public static void submitEvent(Context context, int eventId, String label) {
		NdAnalytics.onEvent(context, eventId, label);
	}
	
	
	public static void submitEvent(Context context, int eventId) {
		submitEvent(context, eventId, "");
	}
	/**
	 * 释放资源
	 */
	@Deprecated
	public static void release() {
//		NdAnalytics.release();
	}
	
	/**
	 * 获取手机的CUID
	 * @param ctx
	 * @return
	 */
	public static String getCUID(Context ctx){
		String cuid = "";
		try{
			cuid = NdAnalytics.getCUID(ctx);
			cuid = cuid == null ? "" : cuid;
		} catch (Exception e){
			e.printStackTrace();
		}
		return cuid;
	}
	
	public static void startUp(Context ctx) {
		NdAnalytics.startup(ctx);
		Log.e(TAG, "=============================HiAnalytics.startUp=============================");
	}
	
	public static String getChannel(Context ctx) {
		return NdAnalytics.getChannel(ctx);
	}
	
	/**
	 * 实时统计
	 * Create On 2014-8-11上午11:29:20
	 * Author : pdw
	 * @param ctx
	 * @param eventId
	 * @param label
	 */
	public static void submitEventRealtime(final Context ctx, final int eventId, final String label) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				try {
					if (TelephoneUtil.isNetworkAvailable(ctx)) {
						DxHttpClient.stateActivityRealTime(ctx, eventId, label, false);
					} /*else {
						submitEvent(ctx, eventId, label);
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
