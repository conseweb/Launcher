package com.nd.launcherdev.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.nd.launcherdev.framework.view.dialog.CommonDialog;
import com.nd.launcherdev.launcher.Launcher;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.framework.view.dialog.CommonDialog;
import com.nd.launcherdev.launcher.Launcher;
import com.nd.launcherdev.launcher.info.ApplicationInfo;

/**
 * 桌面快捷方式适配器,非桌面快捷方式请不要使用
 * 
 * @author pdw
 * @date 2012-06-03 11:23:00
 */
public class AppInfoIntentCommandAdapter implements IntentCommand {

	private ApplicationInfo mAppInfo;

	private Context context;
	private CommonDialog commonDialog;
	public AppInfoIntentCommandAdapter(ApplicationInfo mAppInfo) {
		this.mAppInfo = mAppInfo;
	}

	/**
	 * 具体的跳转逻辑 <br>
	 * 每添加一个自定义快捷方式需在这里加上action的动作
	 * 
	 * @ctx
	 */
	@Override
	public void action(final Context ctx, int actionFrom) {
		this.context = ctx;
		final String action = mAppInfo.intent.getAction();
		if (ctx instanceof Launcher) {
			if (CustomIntent.ACTION_OPEN_DRAWER.equals(action)) { // 打开匣子
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(context, "AppslistActivity"));
				context.startActivity(intent);
			} 
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntentCommand))
			return false;
		IntentCommand com = (IntentCommand) o;
		return this.getCommandAction().equals(com.getCommandAction());
	}

	@Override
	public String getCommandAction() {
		return mAppInfo.intent.getAction();
	}
	
	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			}
			return false;
		}
	});
	@Override
	public void action4LongClick(Context ctx, int actionFrom) {

	}
}
