package com.bitants.common.push;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.model.BaseLauncherModel;
import com.bitants.common.framework.view.bubble.LauncherBubbleManager;
import com.bitants.common.kitset.util.FileUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.info.ItemInfo;
import com.bitants.common.launcher.model.BaseLauncherSettings.Favorites;
import com.bitants.common.launcher.screens.ScreenViewGroup;
import com.bitants.common.push.model.NotifyBigPicPushInfo;
import com.bitants.common.push.model.NotifyIconPushInfo;
import com.bitants.common.push.model.NotifyPushInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by michael on 2015-04-16.
 */
public class PushUtil {
    public PushUtil() {
    }

    public static Notification makeNotificationForSDKLevelAbove14(PushSDKAdapterInterface mPushSDKAdapter, Context ctx, NotifyPushInfo item) throws Exception {
        Class builderClass = Class.forName("android.app.Notification$Builder");
        Constructor con = builderClass.getConstructor(new Class[]{Context.class});
        Object builderObj = con.newInstance(new Object[]{ctx});
        Method setContentTitle = builderClass.getDeclaredMethod("setContentTitle", new Class[]{CharSequence.class});
        setContentTitle.invoke(builderObj, new Object[]{item.getTitle()});
        Method setContentText = builderClass.getDeclaredMethod("setContentText", new Class[]{CharSequence.class});
        setContentText.invoke(builderObj, new Object[]{item.getContent()});
        Method setSmallIcon = builderClass.getDeclaredMethod("setSmallIcon", new Class[]{Integer.TYPE});
        setSmallIcon.invoke(builderObj, new Object[]{Integer.valueOf(mPushSDKAdapter.getNotificationMiniIconResourceId())});
        Method setLargeIcon = builderClass.getDeclaredMethod("setLargeIcon", new Class[]{Bitmap.class});
        if(!StringUtil.isEmpty(item.getNotifyIconPath())) {
            setLargeIcon.invoke(builderObj, new Object[]{BitmapFactory.decodeFile(item.getNotifyIconPath())});
        } else {
            setLargeIcon.invoke(builderObj, new Object[]{((BitmapDrawable)ctx.getResources().getDrawable(mPushSDKAdapter.getNotificationIconResourceId())).getBitmap()});
        }

        Method build = builderClass.getDeclaredMethod("build", new Class[0]);
        return (Notification)build.invoke(builderObj, new Object[0]);
    }

    public static Notification genBigPicNotificationAboveLevel16(PushSDKAdapterInterface mPushSDKAdapter, Context ctx, NotifyBigPicPushInfo item, Bitmap bitmap) throws Exception {
        Class builderClass = Class.forName("android.app.Notification$Builder");
        Constructor con = builderClass.getConstructor(new Class[]{Context.class});
        Object builderObj = con.newInstance(new Object[]{ctx});
        Method setContentTitle = builderClass.getDeclaredMethod("setContentTitle", new Class[]{CharSequence.class});
        setContentTitle.invoke(builderObj, new Object[]{item.getTitle()});
        Method setContentText = builderClass.getDeclaredMethod("setContentText", new Class[]{CharSequence.class});
        setContentText.invoke(builderObj, new Object[]{item.getContent()});
        Method setSmallIcon = builderClass.getDeclaredMethod("setSmallIcon", new Class[]{Integer.TYPE});
        setSmallIcon.invoke(builderObj, new Object[]{Integer.valueOf(mPushSDKAdapter.getNotificationMiniIconResourceId())});
        Method setLargeIcon = builderClass.getDeclaredMethod("setLargeIcon", new Class[]{Bitmap.class});
        if(!StringUtil.isEmpty(item.getNotifyIconPath())) {
            setLargeIcon.invoke(builderObj, new Object[]{BitmapFactory.decodeFile(item.getNotifyIconPath())});
        } else {
            setLargeIcon.invoke(builderObj, new Object[]{((BitmapDrawable)ctx.getResources().getDrawable(mPushSDKAdapter.getNotificationIconResourceId())).getBitmap()});
        }

        Object bigPictureStyle = Class.forName("android.app.Notification$BigPictureStyle").newInstance();
        Method bigPicture = bigPictureStyle.getClass().getDeclaredMethod("bigPicture", new Class[]{Bitmap.class});
        bigPicture.invoke(bigPictureStyle, new Object[]{bitmap});
        Method setSummaryText = bigPictureStyle.getClass().getDeclaredMethod("setSummaryText", new Class[]{CharSequence.class});
        setSummaryText.invoke(bigPictureStyle, new Object[]{item.getContent()});
        Method setStyle = builderClass.getDeclaredMethod("setStyle", new Class[]{Class.forName("android.app.Notification$Style")});
        setStyle.invoke(builderObj, new Object[]{bigPictureStyle});
        Method build;
        if(!StringUtil.isAnyEmpty(new String[]{item.getBtn1_text()})) {
            build = builderClass.getDeclaredMethod("addAction", new Class[]{Integer.TYPE, CharSequence.class, PendingIntent.class});
            build.invoke(builderObj, new Object[]{Integer.valueOf(mPushSDKAdapter.getBigPicNotificationFirstIconResourceId()), item.getBtn1_text(), PendingIntent.getActivity(ctx, 0, Intent.parseUri(item.getBtn1_intent(), 0), 134217728)});
        }

        if(!StringUtil.isAnyEmpty(new String[]{item.getBtn2_text()})) {
            build = builderClass.getDeclaredMethod("addAction", new Class[]{Integer.TYPE, CharSequence.class, PendingIntent.class});
            build.invoke(builderObj, new Object[]{Integer.valueOf(mPushSDKAdapter.getBigPicNotificationSecondIconResourceId()), item.getBtn2_text(), PendingIntent.getActivity(ctx, 0, Intent.parseUri(item.getBtn2_intent(), 0), 134217728)});
        }

        build = builderClass.getDeclaredMethod("build", new Class[0]);
        Notification notif = (Notification)build.invoke(builderObj, new Object[0]);
        Field PRIORITY_MAX = Class.forName("android.app.Notification").getDeclaredField("PRIORITY_MAX");
        Method setPriority = builderClass.getDeclaredMethod("setPriority", new Class[]{Integer.TYPE});
        setPriority.invoke(builderObj, new Object[]{Integer.valueOf(PRIORITY_MAX.getInt(notif))});
        return notif;
    }

    public static int getNotificationFlag(NotifyPushInfo item) {
        return item.isPersist()?2:16;
    }

    public static int getPendingIntentFlag(NotifyPushInfo item) {
        return item.isPersist()?134217728:268435456;
    }

    public static void downloadNotificationIcon(PushSDKAdapterInterface mPushSDKAdapter, NotifyPushInfo item) {
        try {
            if(StringUtil.isEmpty(item.getNotifyIcon())) {
                return;
            }

            String e = getNotifyIconPath(mPushSDKAdapter, item);
            FileUtil.downloadFileByURL(item.getNotifyIcon(), e);
            item.setNotifyIconPath(e);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void downloadNotificationBigPic(PushSDKAdapterInterface mPushSDKAdapter, NotifyPushInfo item) {
        try {
            NotifyBigPicPushInfo e = (NotifyBigPicPushInfo)item;
            String iconUrl = e.getBigPicUrl();
            if(StringUtil.isEmpty(iconUrl)) {
                return;
            }

            String fileName = mPushSDKAdapter.getDownloadImageBasePath() + "notify" + "_bigpic_" + item.getId();
            FileUtil.downloadFileByURL(iconUrl, fileName);
            e.setBigPicPath(fileName);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static void downloadPushIcon(PushSDKAdapterInterface mPushSDKAdapter, NotifyIconPushInfo item) {
        try {
            String e = item.getIconUrl();
            if(StringUtil.isEmpty(e)) {
                return;
            }

            String iconPath = getPushIconPath(mPushSDKAdapter, item);
            FileUtil.downloadFileByURL(e, iconPath);
            item.setIconPath(iconPath);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static String getNotifyIconPath(PushSDKAdapterInterface mPushSDKAdapter, NotifyPushInfo item) {
        return mPushSDKAdapter.getDownloadImageBasePath() + "notify" + "_" + item.getId();
    }

    public static String getPushIconPath(PushSDKAdapterInterface mPushSDKAdapter, NotifyPushInfo item) {
        return mPushSDKAdapter.getDownloadImageBasePath() + "notify_icon" + "_" + item.getId();
    }

    public static void addPushIconInWorkspace(String title, String intent, String path, String statTag) {
        try {
            int[] e = PushManager.getInstance().getPushSDKAdapter().getWorkspaceVacantCellFromBottom();
            if(e == null) {
                return;
            }

            final ApplicationInfo info = new ApplicationInfo();
            info.title = title;
            info.itemType = 1;
            info.intent = Intent.parseUri(intent, 0);
            info.customIcon = true;
            info.iconBitmap = BitmapFactory.decodeFile(path);
            if(info.iconBitmap == null) {
                return;
            }

            info.container = -100L;
            info.screen = e[0];
            info.cellX = e[1];
            info.cellY = e[2];
            info.spanX = 1;
            info.spanY = 1;
            info.statTag = statTag;
            BaseLauncherModel.addItemToDatabase(BaseConfig.getApplicationContext(), info, false);
            final ScreenViewGroup wk = BaseConfig.getBaseLauncher().getScreenViewGroup();
            wk.post(new Runnable() {
                public void run() {
                    View view = BaseConfig.getBaseLauncher().createCommonAppView(info);
                    wk.addInScreen(view, info.screen, info.cellX, info.cellY, info.spanX, info.spanY);
                    wk.getCellLayoutAt(info.screen).invalidate();
                }
            });
            wk.postDelayed(new Runnable() {
                public void run() {
                    LauncherBubbleManager.getInstance().showBubblesAgain();
                }
            }, 600L);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public static void updataPushIconIntent(String pkgName) {
        Cursor c = null;

        try {
            ContentResolver e = BaseConfig.getApplicationContext().getContentResolver();
            c = e.query(Favorites.getContentUri(), new String[]{"intent"}, "intent like \'%" + pkgName + "%\' and " + "intent" + " like \'%AppMarketAppDetailActivity%\'", (String[])null, (String)null);
            if(c != null && c.moveToFirst()) {
                PackageManager pm = BaseConfig.getApplicationContext().getPackageManager();
                List resolves = AndroidPackageUtils.queryMainIntentActivity(pm);
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.setClassName(pkgName, ((ResolveInfo)resolves.get(0)).activityInfo.name);
                intent.setFlags(270532608);
                updatePushIconIntent(c.getString(0), intent.toUri(0), true);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            if(c != null) {
                c.close();
            }

        }

    }

    public static void updatePushIconIntent(String oldIntent, String newIntent, boolean changeToApplicationType) {
        Cursor c = null;
        Cursor c2 = null;

        try {
            ContentResolver e = BaseConfig.getApplicationContext().getContentResolver();
            c = e.query(Favorites.getContentUri(), new String[]{"_id", "container", "screen", "cellX", "cellY"}, "intent=\'" + oldIntent + "\'", (String[])null, (String)null);
            if(c != null && c.getCount() == 0) {
                c = e.query(Favorites.getContentUri(), new String[]{"_id", "container", "screen", "cellX", "cellY"}, "intent like \'%" + oldIntent + "%\'", (String[])null, (String)null);
            }

            if(c != null && c.moveToFirst()) {
                long id = c.getLong(0);
                ContentValues values = new ContentValues();
                values.put("intent", newIntent);
                if(changeToApplicationType) {
                    values.put("itemType", Integer.valueOf(0));
                }

                e.update(Favorites.getContentUri(id, false), values, (String)null, (String[])null);
                int con = c.getInt(1);
                int screen = c.getInt(2);
                int cellX = c.getInt(3);
                int cellY = c.getInt(4);
                View v = null;
                if(con == -100) {
                    v = BaseConfig.getBaseLauncher().getScreenViewGroup().getCellLayoutAt(screen).getChildAt(cellX, cellY);
                } else if(con == -101) {
                    v = BaseConfig.getBaseLauncher().getDockbar().findCellLayoutChildView(cellX, screen);
                } else {
                    c2 = e.query(Favorites.getContentUri(), new String[]{"_id", "container", "screen", "cellX", "cellY"}, "_id=" + con, (String[])null, (String)null);
                    if(c2 != null && c2.moveToFirst()) {
                        int appInfo = c2.getInt(1);
                        int folderInfo = c2.getInt(2);
                        int info = c2.getInt(3);
                        int cellY2 = c2.getInt(4);
                        if(appInfo == -100) {
                            v = BaseConfig.getBaseLauncher().getScreenViewGroup().getCellLayoutAt(folderInfo).getChildAt(info, cellY2);
                        } else if(appInfo == -101) {
                            v = BaseConfig.getBaseLauncher().getDockbar().findCellLayoutChildView(info, folderInfo);
                        }
                    }
                }

                if(v == null) {
                    return;
                }

                ApplicationInfo appInfo1 = null;
                if(v.getTag() instanceof ApplicationInfo) {
                    appInfo1 = (ApplicationInfo)v.getTag();
                } else if(v.getTag() instanceof FolderInfo) {
                    FolderInfo folderInfo1 = (FolderInfo)v.getTag();
                    Iterator cellY21 = folderInfo1.contents.iterator();

                    while(cellY21.hasNext()) {
                        ApplicationInfo info1 = (ApplicationInfo)cellY21.next();
                        if(info1.intent != null && oldIntent.equals(info1.intent.toUri(0))) {
                            appInfo1 = info1;
                            break;
                        }
                    }
                }

                if(appInfo1 != null) {
                    appInfo1.intent = Intent.parseUri(newIntent, 0);
                    if(changeToApplicationType) {
                        appInfo1.itemType = 0;
                        appInfo1.componentName = appInfo1.intent.getComponent();
                    }
                }
            }
        } catch (Exception var21) {
            var21.printStackTrace();
        } finally {
            if(c2 != null) {
                c2.close();
            }

            if(c != null) {
                c.close();
            }

        }

    }

    public static void addAppendIconInWorkspace(String pushIconIntent, String appendIconIntent, String path, String statTag) {
        Cursor c = null;

        try {
            ContentResolver e = BaseConfig.getApplicationContext().getContentResolver();
            c = e.query(Favorites.getContentUri(), new String[]{"_id", "container", "screen", "cellX", "cellY"}, "intent like \'%" + appendIconIntent + "%\'", (String[])null, (String)null);
            if(c == null || !c.moveToFirst()) {
                return;
            }

            int con = c.getInt(1);
            int screen = c.getInt(2);
            int cellX = c.getInt(3);
            int cellY = c.getInt(4);
            if(con != -100) {
                return;
            }

            final ApplicationInfo info = new ApplicationInfo();
            info.title = " ";
            info.itemType = 1;
            info.intent = Intent.parseUri(pushIconIntent, 0);
            info.customIcon = true;
            info.iconBitmap = BitmapFactory.decodeFile(path);
            if(info.iconBitmap != null) {
                info.container = -100L;
                info.screen = screen;
                info.cellX = cellX;
                info.cellY = cellY;
                info.spanX = 1;
                info.spanY = 1;
                info.statTag = statTag;
                final ScreenViewGroup wk = BaseConfig.getBaseLauncher().getScreenViewGroup();
                wk.post(new Runnable() {
                    public void run() {
                        View view = BaseConfig.getBaseLauncher().createCommonAppView(info);
                        wk.addInScreen(view, info.screen, info.cellX, info.cellY, info.spanX, info.spanY);
                        wk.getCellLayoutAt(info.screen).invalidate();
                    }
                });
                return;
            }
        } catch (Exception var15) {
            var15.printStackTrace();
            return;
        } finally {
            if(c != null) {
                c.close();
            }

        }

    }

    public static boolean isPushedIcon(ItemInfo item) {
        if(!(item instanceof ApplicationInfo)) {
            return false;
        } else {
            ApplicationInfo appInfo = (ApplicationInfo)item;
            return appInfo.customIcon && !StringUtil.isEmpty(appInfo.statTag);
        }
    }
}
