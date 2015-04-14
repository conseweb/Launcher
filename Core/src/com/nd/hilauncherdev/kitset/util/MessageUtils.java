package com.nd.hilauncherdev.kitset.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * 消息处理工具类
 */
public class MessageUtils {
	
	public static Toast toast;

	/**
	 * 描述:
	 * 
	 * @author linqiang(866116)
	 * @Since 2012-10-23
	 * @param text
	 */
	public static void showOnlyToast(Context mContext, final String text) {
		if (toast == null) {
			toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.show();
	}

	/**
	 * 描述:
	 * 
	 * @author linqiang(866116)
	 * @Since 2012-10-23
	 * @param text
	 */
	public static void showOnlyToast(Context mContext, final int text) {
		if (toast == null) {
			toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.show();
	}
	
	public static void makeShortToast(Context ctx, CharSequence cs) {
		Toast.makeText(ctx, cs, Toast.LENGTH_SHORT).show();
	}
	
	public static void makeShortToast(Context ctx, int cs) {
		Toast.makeText(ctx, cs, Toast.LENGTH_SHORT).show();
	}
	
	public static void makeLongToast(Context ctx, int cs) {
		Toast.makeText(ctx, cs, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 发送广播消息
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param msg 内容
	 * @param intent 广播Intent
	 */
	public static void makeBroadcastNotification(Context ctx, int icon, int title, int msg, Intent intent) {
		NotificationManager nManager = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);

		Notification notif = new Notification(icon, ctx.getString(title), System.currentTimeMillis());
		PendingIntent pIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notif.flags = Notification.FLAG_AUTO_CANCEL;
		notif.setLatestEventInfo(ctx, ctx.getResources().getString(title), ctx.getString(msg), pIntent);
		nManager.notify(title, notif);
	}
	
	/**
	 * 发送Activity消息
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param msg 内容
	 * @param intent 广播Intent
	 */
	public static void makeActivityNotification(Context ctx, int icon, int title, int msg, Intent intent, boolean ifSoundVibarate) {
		NotificationManager nManager = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);

		Notification notif = new Notification(icon, ctx.getString(title), System.currentTimeMillis());
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notif.flags = Notification.FLAG_AUTO_CANCEL;
		notif.setLatestEventInfo(ctx, ctx.getResources().getString(title), ctx.getString(msg), pIntent);
		if (ifSoundVibarate) {
			notif.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		}
		nManager.notify(title, notif);
	}
	
	
	/**
	 * 发送常驻通知，点击可消失
	 * @param ctx
	 * @param icon
	 * @param title
	 * @param msg
	 * @param intent
	 * @param ifSoundVibarate
	 */
	public static void makeActivityNotificationOnGoingOnce(Context ctx, int icon, int title, int msg, Intent intent, boolean ifSoundVibarate) {
		NotificationManager nManager = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);

		Notification notif = new Notification(icon, ctx.getString(title), System.currentTimeMillis());
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		notif.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
		notif.setLatestEventInfo(ctx, ctx.getResources().getString(title), ctx.getString(msg), pIntent);
		if (ifSoundVibarate) {
			notif.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		}
		nManager.notify(title, notif);
	}
	
	
	/**
	 * 发送通知,支持自定义声音
	 * @param ctx
	 * @param icon
	 * @param title
	 * @param msg
	 * @param intent
	 * @param soundUri 自定义声音资源的Uri
	 */
	public static void makeActivityNotification(Context ctx, int icon, int title, int msg, Intent intent, Uri soundUri) {
		NotificationManager nManager = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);

		Notification notif = new Notification(icon, ctx.getString(title), System.currentTimeMillis());
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notif.flags = Notification.FLAG_AUTO_CANCEL;
		notif.setLatestEventInfo(ctx, ctx.getResources().getString(title), ctx.getString(msg), pIntent);
		if (soundUri!=null){
			notif.sound = soundUri;
		}
		nManager.notify(title, notif);
	}
}
