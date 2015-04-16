package com.bitants.launcherdev.push;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;
import com.bitants.launcherdev.framework.view.bubble.LauncherBubbleView;
import com.bitants.launcherdev.push.model.CompaigPushInfo;
import com.bitants.launcherdev.push.model.NotifyPushInfo;
import com.bitants.launcherdev.push.model.ServerConfigInfo;
import java.util.List;
import org.json.JSONArray;

/**
 * Created by michael on 2015-04-16.
 */
public abstract class PushSDKAdapterInterface {
    public PushSDKAdapterInterface() {
    }

    public abstract String getPushMsg();

    public abstract int getPushInterval();

    public abstract void setPushInterval(int var1);

    public abstract int getNotificationMiniIconResourceId();

    public abstract int getNotificationIconResourceId();

    public abstract RemoteViews makeRemoteViewsForSDKLevelLow14(NotifyPushInfo var1);

    public abstract String getDownloadImageBasePath();

    public abstract void statReceivePushNotification(String var1);

    public abstract void statClickPushNotification(String var1);

    public abstract int[] getWorkspaceVacantCellFromBottom();

    public abstract void setLastCommonPushedId(int var1);

    public abstract int getLastCommonPushedId();

    public abstract void setLastPopupPushedId(int var1);

    public abstract int getLastPopupPushedId();

    public abstract void setLastIconPushedId(int var1);

    public abstract int getLastIconPushedId();

    public abstract void redirectToDownloadManager(String var1, String var2, String var3, String var4, String var5, int var6);

    public abstract LauncherBubbleView createLauncherBubbleView(String var1, int var2, View var3);

    public void handlePushNotificationToMenu() {
    }

    public int getBigPicNotificationFirstIconResourceId() {
        return 0;
    }

    public int getBigPicNotificationSecondIconResourceId() {
        return 0;
    }

    public List<CompaigPushInfo> parseCompaignNotification(JSONArray compaignArray) {
        return null;
    }

    public void sendCompaignNotification(Context ctx, CompaigPushInfo item) {
    }

    public void handleCompaignNotificationPushReceive(ServerConfigInfo serverConfigInfo, Handler notificationHandler) {
    }
}
