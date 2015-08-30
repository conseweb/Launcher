package com.bitants.common.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.bitants.common.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/**
 * Created by michael on 15/6/6.
 */
public class MyUtils {

    public Bitmap takeScreenshot(View v) {
        View rootView = v.findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            ALog.e("Error", e, e.getMessage());
        } catch (IOException e) {
            ALog.e("Error", e, e.getMessage());
        }
    }

    /**
     * 为程序创建桌面快捷方式
     */
    private void addShortcut(Context ctx, int iconId){
//        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//
//        //快捷方式的名称
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, ctx.getString(R.string.app_name));
//        shortcut.putExtra("duplicate", false); //不允许重复创建
//
//        /****************************此方法已失效*************************/
//        //ComponentName comp = new ComponentName(this.getPackageName(), "."+this.getLocalClassName());
//        //shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));  　　
//        /******************************end*******************************/
//        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
//        shortcutIntent.setClassName(this, this.getClass().getName());
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//
//        //快捷方式的图标
//        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(ctx, iconId);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//
//        ctx.sendBroadcast(shortcut);
    }

//    private boolean hasShortcut() {
//        boolean isInstallShortcut = false;
//        final ContentResolver cr = activity.getContentResolver();
//        final String AUTHORITY = "com.android.launcher.settings";
//        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
//        Cursor c = cr.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?",
//                new String[]{mapViewActivity.getString(R.string.app_name).trim()}, null);
//        if (c != null && c.getCount() > 0) {
//            isInstallShortcut = true;
//        }
//        return isInstallShortcut;
//    }

//    public static void createSystemSwitcherShortCut(Context context, String title) {
//        final Intent addIntent = new Intent(
//                "com.android.launcher.action.INSTALL_SHORTCUT");
//        final Parcelable icon = Intent.ShortcutIconResource.fromContext(
//                context, R.drawable.ic_switcher_shortcut); // 获取快捷键的图标
//        addIntent.putExtra("duplicate", false);
//        final Intent myIntent = new Intent(context,
//                SystemSwitcherActivity.class);
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
//                title);// 快捷方式的标题
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
//        context.sendBroadcast(addIntent);
//    }

    /**
     * 删除程序的快捷方式
     */
    private void delShortcut(){
//        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
//
//        //快捷方式的名称
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
//        String appClass = this.getPackageName() + "." +this.getLocalClassName();
//        ComponentName comp = new ComponentName(this.getPackageName(), appClass);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
//
//        sendBroadcast(shortcut);

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static InputStream bitmapToInputStream(Bitmap bitmap) {
        int size = bitmap.getHeight() * bitmap.getRowBytes();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(buffer);
        return new ByteArrayInputStream(buffer.array());
    }

    private static void doInStatusBar(Context mContext, String methodName) {
        try {
            Object service = mContext.getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod(methodName);
            expand.invoke(service);
        } catch (Exception e) {
            ALog.e("Exception", e, e.getMessage());
        }
    }

    /**
     * 显示消息中心
     */
    public static void openStatusBar(Context mContext) {
        // 判断系统版本号
        String methodName = (Build.VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
        doInStatusBar(mContext, methodName);
    }

    /**
     * 关闭消息中心
     */
    public static void closeStatusBar(Context mContext) {
        // 判断系统版本号
        String methodName = (Build.VERSION.SDK_INT <= 16) ? "collapse" : "collapsePanels";
        doInStatusBar(mContext, methodName);
    }

}
