package com.bitants.launcherdev.kitset.Analytics;

import android.content.Context;
import android.util.Log;
import com.bitants.launcherdev.framework.httplib.DxHttpClient;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.kitset.util.ThreadUtil;
//import com.nd.analytics.NdAnalytics;
//import com.nd.analytics.NdAnalyticsSettings;

import java.util.UUID;

/**
 * 功能统计封装类
 */
public class MoAnalytics {
	private static int init = -1;
	private static final String TAG = "MoAnalytics";
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
	 * 产品ID
	 */
	public static final int AppId = 1927446;

	/**
	 * 产品Key
	 */
	public static final String AppKey = "e48ecc9d444c1a3508e139a8378ea917b9c5b23362f8c26f";


	/**
	 * 统计分析初始化
	 * @param context
	 *            不能使用Application Context
	 */
	public static void init(Context context) {
		if (init != -1)
			return;
		
		// 初始化数据分析
//		NdAnalyticsSettings settings = new NdAnalyticsSettings();
//		settings.setAppId(AppId);
//		settings.setAppKey(AppKey);

		
//		NdAnalytics.setReportStartupOnlyOnceADay(true);
//		NdAnalytics.initialize(context, settings);
		
		init = 1;
		Log.e(TAG, "=============================Analytics.init=============================");
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
//		NdAnalytics.onEvent(context, eventId, label);
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

//    public String getIdentity() {
//        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
//        String identity = preference.getString("identity", null);
//        if (identity == null) {
//            identity = java.util.UUID.randomUUID().toString();
//            preference.edit().putString("identity", identity);
//        }
//        return identity;
//    }
	
	/**
	 * 获取手机的CUID
	 * @param ctx
	 * @return
	 */
	public static String getCUID(Context ctx){
		String cuid = "";
		try{
//			cuid = NdAnalytics.getCUID(ctx);
            cuid = UUID.randomUUID().toString();
			cuid = cuid == null ? "" : cuid;
		} catch (Exception e){
			e.printStackTrace();
		}
		return cuid;
	}
	
	public static void startUp(Context ctx) {
//		NdAnalytics.startup(ctx);
		Log.e(TAG, "=============================MoAnalytics.startUp=============================");
	}
	
	public static String getChannel(Context ctx) {
//		return NdAnalytics.getChannel(ctx);
		return "";
	}
	
	/**
	 * 实时统计
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
