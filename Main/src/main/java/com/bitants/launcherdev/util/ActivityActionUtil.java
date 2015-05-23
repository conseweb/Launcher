package com.bitants.launcherdev.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import com.bitants.launcher.R;
import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.kitset.util.SystemUtil;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;

/**
 * 桌面相关打开应用处理器
 */
public class ActivityActionUtil {
	private static final String TAG = "ActivityActionUtil";

	/**
	 * 打开APP并统计使用次数，仅供有统计次数需求使用
	 * @param ctx
	 * @param intent
	 */
	public static void startActivitySafelyForRecored(Context ctx, Intent intent) {
		startActivitySafelyForRecored(null, ctx, intent);
	}
	
	/**
	 * 打开APP并统计使用次数，仅供有统计次数需求使用
	 * @param v
	 * @param ctx
	 * @param intent
	 */
	public static void startActivitySafelyForRecored(View v, Context ctx, Intent intent) {
		if (intent == null) {
			SystemUtil.makeShortToast(ctx, R.string.dockbar_null_intent);
			return;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			ctx.startActivity(intent);
			//maybeUpdateUsedTime(ctx, intent.getComponent());
		} catch (ActivityNotFoundException e) {
			if (!maybeSometingHappened(v, intent.getComponent(), ctx)) {
				maybeRecommend(intent.getComponent(), ctx);
				Log.e(TAG, "Unable to launch. intent=" + intent, e);
			}
		} catch (SecurityException e) {
			makeSomethingFix(ctx, intent, e);
		} catch (Exception e) {
			SystemUtil.makeShortToast(ctx, R.string.dockbar_null_intent);
		}
	}
	
	private static boolean maybeSometingHappened(View v, ComponentName cn, Context ctx) {
		if (v == null || cn == null || v.getTag() == null)
			return false;
		
		Intent mainIntent = AndroidPackageUtils.getPackageMainIntent(ctx, cn.getPackageName());
		if (mainIntent == null)
			return false;
		
		Object obj = v.getTag();
		if (!(obj instanceof ApplicationInfo))
			return false;
		
		ApplicationInfo info = (ApplicationInfo)obj;
		if (info.intent == null)
			return false;
		
		info.intent = mainIntent;
		BaseLauncherModel.updateItemUriInDatabase(ctx, info.id, mainIntent.toUri(0));
		SystemUtil.startActivitySafely(ctx, mainIntent);
		return true;
	}

	private static void maybeRecommend(ComponentName cn, Context ctx) {
//		if (cn == null) {
//			SystemUtil.makeShortToast(ctx, R.string.activity_not_found);
//			return;
//		}
//		
//		RecommendManager.maybeDoSomething(ctx, cn);
	}

	/**
	 * 处理一些特殊情况
	 * @param ctx
	 * @param intent
	 * @param e
	 */
	private static void makeSomethingFix(Context ctx, Intent intent, SecurityException e) {
		// 适配S3等特殊机型的快速拨号功能
		String uri = intent.toUri(0);
		if (uri.contains("CALL_PRIVILEGED")) {
			uri = uri.replace("CALL_PRIVILEGED", "CALL");
			try {
				ctx.startActivity(Intent.parseUri(uri, 0));
			} catch (ActivityNotFoundException e2) {
				SystemUtil.makeShortToast(ctx, R.string.activity_not_found);
				Log.e(TAG, "Unable to launch. intent=" + intent, e);
			} catch (SecurityException e2) {
				SystemUtil.makeShortToast(ctx, R.string.activity_not_found);
				Log.e(TAG, e.getMessage());
			} catch (Exception e2) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			SystemUtil.makeShortToast(ctx, R.string.activity_not_found);
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 更新应用程序使用次数
	 * @param ctx
	 * @param component
	 */
	private static void maybeUpdateUsedTime(Context ctx, ComponentName component) {
//		if (component == null)
//			return;
//		if (ctx.getPackageName().equals(component.getPackageName()))
//			return;
//
//		BaseAppDataFactory.getInstance().updateUsedTime(ctx, component);
	}
}
