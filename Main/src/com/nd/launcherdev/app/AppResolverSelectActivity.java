package com.nd.launcherdev.app;

import com.nd.launcherdev.kitset.util.SystemUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.nd.launcherdev.kitset.util.SystemUtil;

public class AppResolverSelectActivity extends Activity {

	private Context mContext;
	
	/**
	 * 是否总是弹出程序打开选择框
	 */
//	private boolean isAlwaysSelect = false;
	/**
	 * 是否设置默认应用程序模式(点击列表项时表示设置该项为默认打开程序)
	 */
//	private boolean isSetDefaultMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Intent intent = new Intent(getIntent());
//		if (DefaultAppAssit.BROWSER_SHORTCUT_SPECIAL_INTENT.equalsIgnoreCase(getIntent().toString())
//				|| DefaultAppAssit.BROWSER_SHORTCUT_SPECIAL_INTENT_EX.equalsIgnoreCase(getIntent().toString())) {
//			if (BaseConfig.isZh()) {
//				intent.setData(Uri.parse(String.format(WidgetSearchUtil.BAIDU_SEARCH_URL, "")));
//			}
//		}
		if (DefaultAppAssit.SMS_SHORTCUT_SPECIAL_INTENT.equalsIgnoreCase(getIntent().toString())
				|| DefaultAppAssit.SMS_SHORTCUT_SPECIAL_INTENT_EX.equalsIgnoreCase(getIntent().toString())) {
			intent.setType("vnd.android-dir/mms-sms");
		}
//		isAlwaysSelect = intent.getBooleanExtra("is_always_select", false);
//		isSetDefaultMode = intent.getBooleanExtra("is_set_default_mode", false);
		intent.setComponent(null);
		//修改为使用系统打开方式 caizp 2013-6-3
		SystemUtil.startActivitySafely(mContext, intent);
		finish();
	}
}
