package com.bitants.common.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;

import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.kitset.util.ThreadUtil;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.push.model.CompaigPushInfo;
import com.bitants.common.push.model.NotifyIconPushInfo;
import com.bitants.common.framework.view.bubble.LauncherBubbleManager;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.config.preference.BaseConfigPreferences;
import com.bitants.common.launcher.support.LauncherOnStartDispatcher.OnLauncherStartListener;
import com.bitants.common.push.model.NotifyBigPicPushInfo;
import com.bitants.common.push.model.NotifyPushInfo;
import com.bitants.common.push.model.PopupPushInfo;
import com.bitants.common.push.model.ServerConfigInfo;
import java.util.Iterator;
import java.util.List;

/**
 * Created by michael on 2015-04-16.
 */
public class PushManager implements OnLauncherStartListener {
    private static PushManager mPushManager = new PushManager();
    private PushManager.WorkThread sWorkerThread = new PushManager.WorkThread("launcher-push");
    private Handler workThreadHandler;
    private Runnable fetchPushMsgRunnable;
    private int notify_id = 0;
    private boolean hasStart = false;
    private List<NotifyIconPushInfo> appendIconList;
    private PushSDKAdapterInterface mPushSDKAdapter;
    private Handler notificationHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                Context e = BaseConfig.getApplicationContext();
                if(msg.obj instanceof CompaigPushInfo) {
                    PushManager.this.mPushSDKAdapter.sendCompaignNotification(e, (CompaigPushInfo)msg.obj);
                } else {
                    NotifyPushInfo info = (NotifyPushInfo)msg.obj;
                    switch(info.getType()) {
                        case 1:
                            PushManager.this.sendCommonNotification(e, info);
                            break;
                        case 2:
                            if(VERSION.SDK_INT >= 16 && !TelephoneUtil.isMIMoble()) {
                                PushManager.this.sendBigPicNotification(e, (NotifyBigPicPushInfo)info);
                            } else {
                                PushManager.this.sendCommonNotification(e, info);
                            }
                    }
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }
    };

    public void setPushSDKAdapter(PushSDKAdapterInterface mPushSDKAdapter) {
        this.mPushSDKAdapter = mPushSDKAdapter;
    }

    public PushSDKAdapterInterface getPushSDKAdapter() {
        return this.mPushSDKAdapter;
    }

    public static PushManager getInstance() {
        return mPushManager;
    }

    private PushManager() {
        this.sWorkerThread.start();
        this.workThreadHandler = new Handler(this.sWorkerThread.getLooper());
        this.fetchPushMsgRunnable = new Runnable() {
            public void run() {
                PushManager.this.workThreadHandler.removeCallbacks(PushManager.this.fetchPushMsgRunnable);
                int defaultInterva = PushManager.this.mPushSDKAdapter.getPushInterval();
                int interval = defaultInterva * 1000;
                if(TelephoneUtil.isNetworkAvailable(BaseConfig.getApplicationContext()) && System.currentTimeMillis() - BaseConfigPreferences.getInstance().getFirstLaunchTime() > 86400000L) {
                    try {
                        ServerConfigInfo e = PushMsgHandler.fetchPushMsg();
                        if(e != null) {
                            int newInterval = e.getFetchInterval();
                            if(newInterval > 1000 && newInterval != defaultInterva) {
                                PushManager.this.mPushSDKAdapter.setPushInterval(newInterval);
                                interval = newInterval * 1000;
                            }

                            PushManager.this.mPushSDKAdapter.handleCompaignNotificationPushReceive(e, PushManager.this.notificationHandler);
                            PushManager.this.handleNotificationPushReceive(e.getNotifyPushInfo());
                            PushManager.this.handlePopupPushReceive(e.getPopupPushInfo());
                            PushManager.this.handleIconPushReceive(e.getNotifyIconPushInfo());
                        }

                        PushManager.this.workThreadHandler.postDelayed(PushManager.this.fetchPushMsgRunnable, (long)interval);
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }

                } else {
                    PushManager.this.workThreadHandler.postDelayed(PushManager.this.fetchPushMsgRunnable, (long)interval);
                }
            }
        };
    }

    public int getNotificationId() {
        return this.mPushSDKAdapter.getNotificationIconResourceId() + this.notify_id++;
    }

    private void sendCommonNotification(Context ctx, NotifyPushInfo item) {
        try {
            NotificationManager e = (NotificationManager)ctx.getSystemService("notification");
            Notification notif = null;
            if(VERSION.SDK_INT >= 14 && !TelephoneUtil.isMIMoble()) {
                notif = PushUtil.makeNotificationForSDKLevelAbove14(this.mPushSDKAdapter, ctx, item);
            } else {
                notif = new Notification();
                notif.icon = this.mPushSDKAdapter.getNotificationMiniIconResourceId();
                notif.tickerText = item.getTitle();
                notif.contentView = this.mPushSDKAdapter.makeRemoteViewsForSDKLevelLow14(item);
            }

            int pIntentFlag = PushUtil.getPendingIntentFlag(item);
            String intent = item.getIntent();
            PendingIntent pIntent = null;
            if(intent.length() > 0) {
                if(item instanceof NotifyIconPushInfo) {
                    NotifyIconPushInfo iconInfo = (NotifyIconPushInfo)item;
                    if(!iconInfo.isShowIconImmediately()) {
                        pIntent = PendingIntent.getActivity(ctx, 0, PushMsgRedirectActivity.getIntentForPushIcon(ctx, iconInfo), pIntentFlag);
                    }
                }

                if(pIntent == null) {
                    pIntent = PendingIntent.getActivity(ctx, 0, PushMsgRedirectActivity.getIntent(ctx, intent, item.getId(), item.getNotifyIconPath()), pIntentFlag);
                }
            }

            notif.flags = PushUtil.getNotificationFlag(item);
            notif.contentIntent = pIntent;
            e.notify(this.getNotificationId(), notif);
            this.mPushSDKAdapter.statReceivePushNotification("" + item.getId());
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    private void sendBigPicNotification(Context ctx, NotifyBigPicPushInfo item) {
        try {
            String e = item.getBigPicPath();
            Bitmap bitmap = null;
            if(!StringUtil.isAnyEmpty(new String[]{e})) {
                bitmap = BitmapFactory.decodeFile(e);
            }

            if(bitmap == null) {
                this.sendCommonNotification(ctx, item);
                return;
            }

            Notification notif = PushUtil.genBigPicNotificationAboveLevel16(this.mPushSDKAdapter, ctx, item, bitmap);
            notif.flags = PushUtil.getNotificationFlag(item);
            int pIntentFlag = PushUtil.getPendingIntentFlag(item);
            if(item.getIntent().length() > 0) {
                PendingIntent nManager = PendingIntent.getActivity(ctx, 0, PushMsgRedirectActivity.getIntent(ctx, item.getIntent(), item.getId(), item.getNotifyIconPath()), pIntentFlag);
                notif.contentIntent = nManager;
            }

            NotificationManager nManager1 = (NotificationManager)ctx.getSystemService("notification");
            nManager1.notify(this.getNotificationId(), notif);
            this.mPushSDKAdapter.statReceivePushNotification("" + item.getId());
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    public void handlePopupPushReceive(List<PopupPushInfo> popupInfo) {
        if(popupInfo != null && popupInfo.size() != 0) {
            try {
                if(BaseConfig.getBaseLauncher() == null) {
                    return;
                }

                Iterator var3 = popupInfo.iterator();

                while(var3.hasNext()) {
                    PopupPushInfo e = (PopupPushInfo)var3.next();
                    this.mPushSDKAdapter.statReceivePushNotification("" + e.getId());
                }

                LauncherBubbleManager.getInstance().addBubbles(popupInfo);
                ScreenViewGroup e1 = BaseConfig.getBaseLauncher().getScreenViewGroup();
                if(e1 != null && e1.isShown()) {
                    e1.post(new Runnable() {
                        public void run() {
                            LauncherBubbleManager.getInstance().showAllBubbles();
                        }
                    });
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }
    }

    private void handleNotificationPushReceive(List<NotifyPushInfo> notifyPushList) {
        if(notifyPushList != null && notifyPushList.size() != 0) {
            try {
                Iterator var3 = notifyPushList.iterator();

                while(var3.hasNext()) {
                    final NotifyPushInfo e = (NotifyPushInfo)var3.next();
                    ThreadUtil.executeMore(new Runnable() {
                        public void run() {
                            int type = e.getType();
                            PushUtil.downloadNotificationIcon(PushManager.this.mPushSDKAdapter, e);
                            switch (type) {
                                case 1:
                                    PushManager.this.sendMessage(e);
                                    break;
                                case 2:
                                    PushUtil.downloadNotificationBigPic(PushManager.this.mPushSDKAdapter, e);
                                    PushManager.this.sendMessage(e);
                                case 3:
                                default:
                                    break;
                                case 4:
                                    PushManager.this.mPushSDKAdapter.handlePushNotificationToMenu();
                                    return;
                            }

                        }
                    });
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }
    }

    private void handleIconPushReceive(List<NotifyIconPushInfo> notifyIconList) {
        if(notifyIconList != null && notifyIconList.size() != 0) {
            try {
                Iterator var3 = notifyIconList.iterator();

                while(var3.hasNext()) {
                    final NotifyIconPushInfo e = (NotifyIconPushInfo)var3.next();
                    if(!StringUtil.isEmpty(e.getIconUrl())) {
                        ThreadUtil.executeMore(new Runnable() {
                            public void run() {
                                try {
                                    int ex = e.getType();
                                    switch(ex) {
                                        case 1:
                                            PushUtil.downloadNotificationIcon(PushManager.this.mPushSDKAdapter, e);
                                            PushUtil.downloadPushIcon(PushManager.this.mPushSDKAdapter, e);
                                            if(e.isShowIconImmediately()) {
                                                PushUtil.addPushIconInWorkspace(e.getIconTitle(), e.getIconIntent(), e.getIconPath(), "" + e.getId());
                                            }

                                            PushManager.this.sendMessage(e);
                                            return;
                                        case 2:
                                            PushUtil.downloadPushIcon(PushManager.this.mPushSDKAdapter, e);
                                            PushUtil.addPushIconInWorkspace(e.getIconTitle(), e.getIconIntent(), e.getIconPath(), "" + e.getId());
                                        case 3:
                                        case 5:
                                        case 6:
                                        case 7:
                                        default:
                                            break;
                                        case 4:
                                            PushUtil.updatePushIconIntent(e.getIconIntent(), e.getIconIntentNew(), false);
                                            break;
                                        case 8:
                                            PushUtil.downloadPushIcon(PushManager.this.mPushSDKAdapter, e);
                                            PushUtil.addAppendIconInWorkspace(e.getIconIntent(), e.getAppendIcon(), e.getIconPath(), "" + e.getId());
                                            PushManager.this.addToAppendIconList(e);
                                    }

                                    PushManager.this.mPushSDKAdapter.statReceivePushNotification("" + e.getId());
                                } catch (Exception var2) {
                                    var2.printStackTrace();
                                }

                            }
                        });
                    }
                }
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }
    }

    private void sendMessage(Object item) {
        Message msg = new Message();
        msg.obj = item;
        this.notificationHandler.sendMessage(msg);
    }

    public Handler getThreadHandler() {
        return this.workThreadHandler;
    }

    public void addToAppendIconList(NotifyIconPushInfo item) {
        if(this.appendIconList == null) {
            this.appendIconList = PushMsgHandler.getNotReadPushAppendIcon();
        }

        this.appendIconList.add(item);
    }

    public void showAppendIconOnLauncherStart() {
        this.appendIconList = PushMsgHandler.getNotReadPushAppendIcon();
        if(this.appendIconList != null && this.appendIconList.size() != 0) {
            Iterator var2 = this.appendIconList.iterator();

            while(var2.hasNext()) {
                NotifyIconPushInfo info = (NotifyIconPushInfo)var2.next();
                PushUtil.addAppendIconInWorkspace(info.getIconIntent(), info.getAppendIcon(), info.getIconPath(), "" + info.getId());
            }

        }
    }

    public void dismissAppendIcon(String idStr, View v) {
        try {
            int e = Integer.valueOf(idStr).intValue();
            if(this.appendIconList == null || this.appendIconList.size() == 0 || e <= 0 || v == null) {
                return;
            }

            Iterator var5 = this.appendIconList.iterator();

            while(var5.hasNext()) {
                NotifyIconPushInfo info = (NotifyIconPushInfo)var5.next();
                if(info.getId() == e) {
                    PushMsgHandler.setPushMsgRead((long)e);
                    if(v.getParent() != null) {
                        ((ViewGroup)v.getParent()).removeView(v);
                    }

                    return;
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public void onLauncherStart(Context ctx) {
        if(!this.hasStart && !BaseConfig.isOnScene()) {
            this.hasStart = true;
            this.workThreadHandler.removeCallbacks(this.fetchPushMsgRunnable);
            this.workThreadHandler.post(this.fetchPushMsgRunnable);
        }
    }

    public int getType() {
        return 0;
    }

    public static class WorkThread extends HandlerThread {
        public WorkThread(String name) {
            super(name);
        }

        public void run() {
            Process.setThreadPriority(10);
            super.run();
        }
    }
}
