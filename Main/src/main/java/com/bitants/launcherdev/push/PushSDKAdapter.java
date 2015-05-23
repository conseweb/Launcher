package com.bitants.launcherdev.push;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import com.bitants.common.push.PushSDKAdapterInterface;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.httplib.GZipHttpUtil;
import com.bitants.common.framework.view.bubble.LauncherBubbleView;
import com.bitants.launcherdev.kitset.Analytics.AnalyticsConstant;
import com.bitants.launcherdev.kitset.Analytics.MoAnalytics;
import com.bitants.launcherdev.kitset.config.ConfigPreferences;
import com.bitants.launcherdev.kitset.util.CUIDUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.launcher.LauncherViewHelper;
import com.bitants.launcherdev.launcher.WorkspaceHelper;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.push.model.CompaigPushInfo;
import com.bitants.common.push.model.NotifyPushInfo;
import com.bitants.common.push.model.ServerConfigInfo;
import org.json.JSONArray;

import java.util.List;


/**
 * 推送SDK时的适配
 *
 */
public class PushSDKAdapter extends PushSDKAdapterInterface {

	/**
	 * 国内服务器
	 */
	public final static String Mirror_Space_Inland_Server = "http://sj.mojing.io/";
	
	 /**
     * 每日新鲜事hot接口
     */
    public static final String URL_DAILY_HOT_NEWS_TO91 = Mirror_Space_Inland_Server + "android/getdata.aspx?mt=4&tfv=40000&action=2" + CUIDUtil.getCUIDPART() + "&pid=106";
	
    public final static String CACHES_HOME_MARKET = BaseConfig.getBaseDir() + "/caches/space/";
    
    /**
	 * 获取服务端推送消息
	 * 
	 * @return
	 */
	@Override
	public String getPushMsg() {
		String url = PushSDKAdapter.URL_DAILY_HOT_NEWS_TO91;
		Context ctx = BaseConfig.getApplicationContext();
		String imsiNumber = TelephoneUtil.getIMSI(ctx);
		if (null == imsiNumber) {
			imsiNumber = "";
		}
		String imeiNumber = TelephoneUtil.getIMEI(ctx);
		if (null == imeiNumber) {
			imeiNumber = "";
		}
		url += "&imei=" + imeiNumber;
		url += "&imsi=" + imsiNumber;
		url += "&DivideVersion=" + TelephoneUtil.getVersionName(ctx, ctx.getPackageName());
		url += "&supfirm=" + TelephoneUtil.getFirmWareVersion();
		return GZipHttpUtil.post(url);
	}

	/**
	 * 获取保存推送图片的本地目录
	 * 
	 * @return
	 */
	@Override
	public String getDownloadImageBasePath() {
		return CACHES_HOME_MARKET;
	}

	/**
	 * 获取服务端推送消息的间隔时间
	 * 
	 * @return
	 */
	@Override
	public int getPushInterval() {
		return ConfigPreferences.getInstance().getPushInterval();
	}

	/**
	 * 设置服务端推送消息的间隔时间
	 * 
	 * @param time
	 * @return
	 */
	@Override
	public void setPushInterval(int time) {
		ConfigPreferences.getInstance().setPushInterval(time);
	}

	/**
	 * 设置推送通知的状态栏上icon的资源Id
	 * 
	 * @return
	 */
	@Override
	public int getNotificationMiniIconResourceId() {
		return R.drawable.launcher_notify_icon_mini;
	}

	/**
	 * 设置推送通知内容icon的资源Id
	 * 
	 * @return
	 */
	@Override
	public int getNotificationIconResourceId() {
		return R.drawable.launcher_notify_icon;
	}

	/**
	 * 设置固件4.0以下的推送通知内容RemoteViews(4.0以下通知默认样式不支持动态设置图标，需自己定义通知样式)
	 * 
	 * @param item
	 * @return
	 */
	@Override
	public RemoteViews makeRemoteViewsForSDKLevelLow14(NotifyPushInfo item) {
		Context ctx = BaseConfig.getApplicationContext();
		RemoteViews contentView = new RemoteViews(ctx.getPackageName(), R.layout.compagin_notification_view);
		contentView.setTextViewText(R.id.title, item.getTitle());
		if (!StringUtil.isEmpty(item.getNotifyIconPath())) {
			contentView.setImageViewBitmap(R.id.item_icon, BitmapFactory.decodeFile(item.getNotifyIconPath()));
		} else {
			contentView.setImageViewBitmap(R.id.item_icon, ((BitmapDrawable) ctx.getResources()
					.getDrawable(getNotificationIconResourceId())).getBitmap());
		}
		contentView.setTextViewText(R.id.content, item.getContent());
		return contentView;
	}

	/**
	 * 接收到推送通知打点统计
	 */
	@Override
	public void statReceivePushNotification(String tag) {
		tag += TelephoneUtil.isNetworkAvailable(BaseConfig.getApplicationContext()) ? "_net" : "_no_net";
		MoAnalytics.submitEvent(BaseConfig.getApplicationContext(), AnalyticsConstant.NOTIFICATION_MESSAGE_PUSH, tag);
	}

	/**
	 * 点击推送通知打点统计
	 */
	@Override
	public void statClickPushNotification(String tag) {
		tag += TelephoneUtil.isNetworkAvailable(BaseConfig.getApplicationContext()) ? "_net" : "_no_net";
		MoAnalytics.submitEvent(BaseConfig.getApplicationContext(), AnalyticsConstant.NOTIFICATION_MESSAGE_PUSH_CLICK, tag);
	}


	/**
	 * 设置最后收到的通知栏推送Id
	 * 
	 * @param id
	 */
	@Override
	public void setLastCommonPushedId(int id) {
		ConfigPreferences.getInstance().setPushNotifyId(id);
	}

	/**
	 * 获取最后收到的通知栏推送Id
	 * 
	 * @return
	 */
	@Override
	public int getLastCommonPushedId() {
		return ConfigPreferences.getInstance().getPushNotifyId();
	}

	/**
	 * 设置最后收到的冒泡推送Id
	 * 
	 * @param id
	 */
	@Override
	public void setLastPopupPushedId(int id) {
		ConfigPreferences.getInstance().setPushPopupId(id);
	}

	/**
	 * 获取最后收到的冒泡推送Id
	 * 
	 * @return
	 */
	@Override
	public int getLastPopupPushedId() {
		return ConfigPreferences.getInstance().getPushPopupId();
	}

	/**
	 * 设置最后收到的图标推送Id
	 * 
	 * @param id
	 */
	@Override
	public void setLastIconPushedId(int id) {
		ConfigPreferences.getInstance().setPushNotifyIconId(id);
	}

	/**
	 * 获取最后收到的图标推送Id
	 * 
	 * @return
	 */
	@Override
	public int getLastIconPushedId() {
		return ConfigPreferences.getInstance().getPushNotifyIconId();
	}

	/**
	 * 点击推送通知，跳转到下载管理下载
	 * 
	 * @param title
	 * @param name
	 * @param pkgName
	 */
	@Override
	public void redirectToDownloadManager(String title, String name, String pkgName, String downloadUrl, String iconPath, int sp) {
//		UriActions.downloadManagerAction(FileType.FILE_APK.getId(), downloadUrl, title, BaseConfig.WIFI_DOWNLOAD_PATH, name, iconPath,
//                pkgName, sp);
	}

	/**
	 * 创建冒泡View
	 * 
	 * @param hint
	 * @param screen
	 * @param hostView
	 * @return
	 */
	@Override
	public LauncherBubbleView createLauncherBubbleView(String hint, int screen, View hostView) {
		return LauncherViewHelper.createBubbleViewInWorkspace(hint, screen, hostView);
	}
	
	/**
	 * 处理推送消息到桌面菜单界面(不用可不实现)
	 */
	@Override
	public void handlePushNotificationToMenu(){
//		LauncherHomeMenu.isShowPushList = true;
	}
	
	/**
	 * 获取大图通知默认样式底部的第一个图标资源Id(不用可不配置)
	 * 
	 * @return
	 */
	@Override
	public int getBigPicNotificationFirstIconResourceId() {
		return R.drawable.launcher_notify_btn2;
	}
	
	/**
	 * 获取大图通知默认样式底部的第二个图标资源Id(不用可不配置)
	 * 
	 * @return
	 */
	@Override
	public int getBigPicNotificationSecondIconResourceId() {
		return R.drawable.launcher_notify_btn1;
	}

	/**
	 * 解析活动通知(用于兼容旧版魔镜桌面，其它桌面可不用实现)
	 * 
	 * @param compaignArray
	 */
	@Override
	public List<CompaigPushInfo> parseCompaignNotification(JSONArray compaignArray) {
		return null;
	}

	/**
	 * 发送活动通知(用于兼容旧版魔镜桌面，其它桌面可不用实现)
	 * 
	 * @param ctx
	 * @param item
	 */
	@Override
	public void sendCompaignNotification(Context ctx, CompaigPushInfo item) {
		

	}

	/**
	 * 发送活动通知前处理(用于兼容旧版魔镜桌面，其它桌面可不用实现)
	 * 
	 * @param serverConfigInfo
	 * @param notificationHandler
	 */
	@Override
	public void handleCompaignNotificationPushReceive(ServerConfigInfo serverConfigInfo, final Handler notificationHandler) {
		
	}

	/**
	 * 是否为推送图标
	 * @param appInfo
	 * @return
	 */
	public boolean isPushedIcon(ApplicationInfo appInfo){
		return appInfo.customIcon && !StringUtil.isEmpty(appInfo.statTag);
	}

	@Override
	public int[] getWorkspaceVacantCellFromBottom() {
		return WorkspaceHelper.getVacantCellFromBottom();
	}
}