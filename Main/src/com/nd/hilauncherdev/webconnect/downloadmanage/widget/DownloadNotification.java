package com.nd.hilauncherdev.webconnect.downloadmanage.widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.nd.hilauncherdev.kitset.util.TelephoneUtil;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.BaseDownloadInfo;
import com.nd.hilauncherdev.webconnect.downloadmanage.model.DownloadServerServiceConnection;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;
import com.bitants.launcher.R;

/**
 * 
 * @author anson
 */
public class DownloadNotification {

	private static final int sNotifyId = R.string.application_name;

	/**
	 * <br>
	 * Description: 下载成功通知 <br>
	 * Author:caizp <br>
	 * Date:2010-11-4上午10:34:09
	 * 
	 * @param context
	 * @param position
	 * @param title
	 * @param pIntentApply
	 *            对应应用按钮行为
	 */
	public static void downloadCompletedNotification(Context context, int position, String title, String content, PendingIntent pIntent) {

		// NotificationManager nManager = (NotificationManager)
		// context.getSystemService(Service.NOTIFICATION_SERVICE);
		//
		// // RemoteViews view = new RemoteViews(context.getPackageName(),
		// R.layout.widget_download_complete_notify);
		// // view.setImageViewResource(R.id.widget_notify_icon,
		// R.drawable.myphone_wp_apply);
		// // view.setTextViewText(R.id.widget_notify_title, title);
		// // view.setTextViewText(R.id.widget_notify_content, content);
		// //
		// // if (content == null || "".equals(content)) {
		// // view.setViewVisibility(R.id.widget_notify_content, View.GONE);
		// // } else {
		// // view.setViewVisibility(R.id.widget_notify_content, View.VISIBLE);
		// // }
		//
		// Notification notification = new Notification();
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.icon = R.drawable.logo_mini;
		// // notification.contentView = view;
		// if (pIntent == null) //{
		// pIntent = PendingIntent.getBroadcast(context, 0, new Intent(""), 0);
		// // } else {
		// // notification.contentIntent = pIntent ;
		// // }
		// notification.setLatestEventInfo(context, title, content, pIntent);
		// nManager.notify(position, notification);

		notify(context, title, TYPE_FINISH, pIntent);
	}

	/**
	 * <br>
	 * Description: 下载失败通知 <br>
	 * Author:caizp <br>
	 * Date:2010-11-4上午10:34:29
	 * 
	 * @param context
	 * @param position
	 * @param widgetItem
	 */
	public static void downloadFailedNotification(Context context, int position, String title, PendingIntent pIntent) {
//		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
//
//		Notification notif = new Notification(android.R.drawable.stat_notify_error, context.getResources().getString(R.string.common_download_failed), System.currentTimeMillis());
//
//		notif.flags = Notification.FLAG_AUTO_CANCEL;
//		notif.setLatestEventInfo(context, title, context.getResources().getString(R.string.common_download_failed), pIntent);
//		nManager.notify(position, notif);

		notify(context, title, TYPE_FAILED, pIntent);
	}

	/**
	 * <br>
	 * Description: 取消下载通知 <br>
	 * Author:caizp <br>
	 * Date:2010-11-4上午10:34:57
	 * 
	 * @param context
	 * @param position
	 * @param widgetItem
	 */
	public static void downloadCancelledNotification(Context context, int position) {
//		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
//		// 清除通知栏
//		nManager.cancel(position);
		
		delete(context, position);
	}

	/**
	 * <br>
	 * Description: 下载中 <br>
	 * Author:caizp <br>
	 * Date:2010-11-4上午10:35:14
	 * 
	 * @param context
	 * @param position
	 * @param title
	 * @param pIntent
	 */
	public static void downloadRunningNotification(Context context, int position, String title, String content, PendingIntent pIntent) {
//		NotificationManager nManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
//
//		RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_download_notify);
//		view.setImageViewResource(R.id.widget_notify_icon, R.drawable.myphone_common_download);
//		view.setTextViewText(R.id.widget_notify_title, title);
//		view.setTextViewText(R.id.widget_notify_content, content);
//
//		Notification notification = new Notification();
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.icon = R.drawable.myphone_common_download;
//		notification.contentView = view;
//		if (pIntent == null) {
//			notification.contentIntent = PendingIntent.getBroadcast(context, 0, new Intent(""), 0);
//		} else {
//			notification.contentIntent = pIntent;
//		}
//		nManager.notify(position, notification);
	}

	/**
	 * <br>
	 * Description: 下载过程中进度条更新 <br>
	 * Author:caizp <br>
	 * Date:2010-11-4上午10:35:14
	 * 
	 * @param context
	 * @param position
	 * @param title
	 * @param pIntent
	 */
	public static void downloadRunningNotificationWithProgress(Context context, int position, String title, String content, PendingIntent pIntent, int progress) {
		// NotificationManager nManager = (NotificationManager)
		// context.getSystemService(Service.NOTIFICATION_SERVICE);
		//
		// RemoteViews view = new RemoteViews(context.getPackageName(),
		// R.layout.widget_download_progress_notify);
		// view.setImageViewResource(R.id.widget_image,
		// R.drawable.myphone_common_download);
		// view.setTextViewText(R.id.widget_percent, progress + "%");
		// view.setTextViewText(R.id.widget_name, title);
		// view.setProgressBar(R.id.widget_progress, 100, progress, false);
		//
		// Notification notification = new Notification();
		// // notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.icon = R.drawable.myphone_common_download;
		// notification.contentView = view;
		// if (pIntent == null) {
		// notification.contentIntent = PendingIntent.getBroadcast(context, 0,
		// new Intent(""), 0);
		// } else {
		// notification.contentIntent = pIntent ;
		// }
		// nManager.notify(position, notification);
	}

	/**
	 * 下载开始 
	 * hjiang
	 */
	public static void downloadBeganNotification(Context context, int position, String title, String content, PendingIntent pIntent, int progress) {
		notify(context, title, TYPE_START, pIntent);
	}
	
	public static final int TYPE_START = 0;
	public static final int TYPE_STOP = 1;
	public static final int TYPE_PAUSE = 2;
	public static final int TYPE_FINISH = 3;
	public static final int TYPE_INSTALL = 4;
	public static final int TYPE_ADD_QUEUE = 5;
	public static final int TYPE_DELETE = 6;
	public static final int TYPE_FAILED = 7;

	private static void notify(Context ctx, String name, int type, PendingIntent pIntent) {
		switch (type) {
		case TYPE_START:
			start(ctx, name, pIntent);
			break;
		case TYPE_FINISH:
			finish(ctx, name, pIntent);
			break;
		case TYPE_FAILED:
			failed(ctx, name, pIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * 任务开始下载，刷新通知栏 
	 * hjiang
	 */
	private static void start(Context ctx, String name, PendingIntent pIntent) {
		String info = ctx.getString(R.string.download_notify_start);
		show(ctx, name, info, android.R.drawable.stat_sys_download, pIntent);
	}

	/**
	 * 任务被移除，刷新通知栏 
	 * hjiang
	 */
	private static void delete(Context ctx, int position) {
		
		DownloadServerServiceConnection downloadService = new DownloadServerServiceConnection(ctx);
		Map<String, BaseDownloadInfo> srcMap = downloadService.getDownloadTasks();
		if (srcMap == null) {
			return;
		}
	    Collection<BaseDownloadInfo> c = srcMap.values();
	    Iterator<BaseDownloadInfo> it = c.iterator();
	    BaseDownloadInfo info;
		int state;
		int count = 0;
	    for (; it.hasNext();) {
	    	info = it.next();
	    	state = info.getState();
	    	if ((DownloadState.STATE_DOWNLOADING == state || DownloadState.STATE_WAITING == state)
	    		&& Math.abs(info.getDownloadUrl().hashCode()) != position)
	    		count++;
	    }
	    if(count == 0) {
			NotificationManager mNM = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);
			doDelete(ctx, mNM);
	    }
	}

	/**
	 * 任务完成，刷新通知栏 
	 * hjiang
	 */
	private static void finish(Context ctx, String name, PendingIntent pIntent) {
		String info = ctx.getString(R.string.download_notify_finish);
		show(ctx, name, info, android.R.drawable.stat_sys_download_done, pIntent);
	}
	
	/**
	 * 任务失败，刷新通知栏 
	 * hjiang
	 */
	private static void failed(Context ctx, String name, PendingIntent pIntent) {
		String info = TelephoneUtil.isSdcardExist() ? ctx.getString(R.string.download_notify_failed) : ctx.getString(R.string.download_notify_failed_sdcard_noexist);
		show(ctx, name, info, android.R.drawable.stat_sys_download_done, pIntent);
	}

	/**
	 * 刷新通知栏 
	 * hjiang
	 */
	private static void show(Context ctx, String name, String info, int drawable, PendingIntent pIntent) {
		String title = getTipTitleString(name);
		String content = getTipContentString(ctx);
		if (name != null) {
			show(ctx, name + info, title, content, drawable, pIntent);
		} else {
			show(ctx, null, title, content, drawable, pIntent);
		}
	}

	/**
	 * 刷新通知栏 
	 * hjiang
	 */
	private static void show(Context ctx, String ticker, String title, String content, int drawableId, PendingIntent pIntent) {
		Notification notification;
		if (ticker == null) {
			notification = new Notification();
			notification.icon = drawableId;
			notification.when = System.currentTimeMillis();
		} else {
			notification = new Notification(drawableId, ticker, System.currentTimeMillis());
		}

		if (pIntent == null)
			pIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(""), 0);
		notification.setLatestEventInfo(ctx, title, content, pIntent);
//		notification.flags = Notification.FLAG_AUTO_CANCEL;

		NotificationManager mNM = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);
		doDelete(ctx, mNM);

		try {
			mNM.notify(sNotifyId, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除通知栏 
	 * hjiang
	 */
	private static void doDelete(Context ctx, NotificationManager mNM) {
		mNM.cancel(sNotifyId);
	}

	/**
	 * 获取通知栏的标题 
	 * hjiang
	 */
	private static String getTipTitleString(String name) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name);
		return buffer.toString();
	}

	/**
	 * 获取通知栏显示的内容 
	 * hjiang
	 */
	private static String getTipContentString(Context ctx) {

		int done = 0;
		int ing = 0;
		
		DownloadServerServiceConnection downloadService = new DownloadServerServiceConnection(ctx);
		Map<String, BaseDownloadInfo> srcMap = downloadService.getDownloadTasks();
	    Collection<BaseDownloadInfo> c = srcMap.values();
	    Iterator<BaseDownloadInfo> it = c.iterator();
	    BaseDownloadInfo info;
		int state;
	    for (; it.hasNext();) {
	    	info = it.next();
	    	state = info.getState();
	    	if (DownloadState.STATE_DOWNLOADING == state)
	    		ing++;
	    	else if (DownloadState.STATE_FINISHED == state)
	    		done++;
	    }
		
		StringBuffer buffer = new StringBuffer(ctx.getString(R.string.download_notify_task));
		if (ing > 0) {
			buffer.append(ctx.getString(R.string.download_notify_in_queue, "" + ing));
		}
		if (done > 0) {
			buffer.append(ctx.getString(R.string.download_notify_finished, "" + done));
		}

		buffer.append(ctx.getString(R.string.download_notify_click_and_look));
		return buffer.toString();
	}
}
