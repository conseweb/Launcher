package com.nd.launcherdev.launcher.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * 需要与桌面交互的广播
 * <br>Author:ryan
 * <br>Date:2012-12-16下午04:53:04
 */
public class LauncherBroadcastControl {
	private static final String ACTION_WIDGET_INTERACTIVE = "nd.panda.custom.widget.interactive";
	private static final String ACTION_WIDGET_NONINTERACTIVE = "nd.panda.custom.widget.noninteractive";

	public static final String ACTION_LAUNCHER_SPRING_OFF = "com.nd.android.pandahome.external.spring.off";
	public static final String ACTION_LAUNCHER_SPRING_ON = "com.nd.android.pandahome.external.spring.on";

	/**
	 * Launcher onStart 调用
	 */
	public static void sendBrocdcastLauncherOnstart(Context ctx) {
		sendBrocdcastToWhiteDot(ctx, true);
		sendBrocdcastTo91Widget(ctx, ACTION_WIDGET_INTERACTIVE);
	}
	
	/**
	 * Launcher onStop 调用
	 */
	public static void sendBrocdcastLauncherOnstop(Context ctx) {
		sendBrocdcastToWhiteDot(ctx, false);
		sendBrocdcastTo91Widget(ctx, ACTION_WIDGET_NONINTERACTIVE);
	}
	
	/**
	 * 快点交互
	 */
	private static void sendBrocdcastToWhiteDot(Context ctx, boolean state) {
		Intent stopShortCutAssist = new Intent("com.nd.hilauncherdev.launcher.display_state");
		stopShortCutAssist.putExtra("is_launcher_display", state);
		ctx.sendBroadcast(stopShortCutAssist);
	}
	
	/**
	 * 插件交互
	 */
	private static void sendBrocdcastTo91Widget(Context ctx, String action) {
		Intent widgetIntent = new Intent(action);
		ctx.sendBroadcast(widgetIntent);
	}
	
	/**
	 * 桌面进入编辑模式
	 */
	public static void sendBroadcastOnSpringMode(Context ctx){
		sendBrocdcastTo91Widget(ctx, ACTION_LAUNCHER_SPRING_ON);
	}
	
	/**
	 * 桌面退出编辑模式
	 */
	public static void sendBroadcastOffSpringMode(Context ctx){
		sendBrocdcastTo91Widget(ctx, ACTION_LAUNCHER_SPRING_OFF);
	}
}
