package com.bitants.launcherdev.kitset;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.bitants.launcherdev.analysis.AppAnalysisConstant;
import com.bitants.launcherdev.analysis.distribute.AppDistributePool;
import com.bitants.launcherdev.analysis.distribute.AppDistributeService;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.kitset.util.ThreadUtil;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.launcherdev.net.ThemeHttpCommon;

/**
 * description: 应用分发统计接口<br/>
 */
public class AppDistributeUtil {

    public static final String TAG = AppDistributeUtil.class.getSimpleName();

    /**
     * 应用分发，下载开始
     * @param ctx
     * @param packName
     * @param sp
     */
    public static void logAppDisDownloadStart(final Context ctx, final String packName, final int sp) {
        Log.e(TAG, "packName:" + packName + "|sp:" + sp + "|APP_DISTRIBUTE_STEP_START");
        logAppDis(ctx, packName, sp, AppAnalysisConstant.APP_DISTRIBUTE_STEP_START);
    }

    /**
     * 应用分发，浏览
     * @param ctx
     * @param packName
     * @param sp
     */
    public static void logAppDisBrowse(final Context ctx, final String packName, final int sp) {
        Log.e(TAG, "packName:" + packName + "|sp:" + sp + "|APP_DISTRIBUTE_STEP_BROWSE");
        logAppDis(ctx, packName, sp, AppAnalysisConstant.APP_DISTRIBUTE_STEP_BROWSE);
    }

    /**
     * 应用分发，下载成功
     * @param ctx
     * @param packName
     * @param sp
     */
    public static void logAppDisDownloadSucc(final Context ctx, final String packName, final int sp) {
        Log.e(TAG, "packName:" + packName + "|sp:" + sp + "|APP_DISTRIBUTE_STEP_DOWNLOAD_SUCCESS");
        logAppDis(ctx, packName, sp, AppAnalysisConstant.APP_DISTRIBUTE_STEP_DOWNLOAD_SUCCESS);
        AppDistributeService.getInstance().addRecord(packName, sp);
    }

    /**
     * 应用分发，安装成功
     * @param ctx
     * @param packName
     * @param sp
     */
    public static void logAppDisInstallSucc(final Context ctx, final String packName, final int sp) {
        Log.e(TAG, "packName:" + packName + "|sp:" + sp + "|APP_DISTRIBUTE_STEP_INSTALLED_SUCCESS");
        logAppDis(ctx, packName, sp, AppAnalysisConstant.APP_DISTRIBUTE_STEP_INSTALLED_SUCCESS);
    }

    /**
     * 应用分发，下载失败
     * @param ctx
     * @param packName
     * @param sp
     */
    public static void logAppDisDownloadFail(final Context ctx, final String packName, final int sp) {
        Log.e(TAG, "packName:" + packName + "|sp:" + sp + "|APP_DISTRIBUTE_STEP_DOWNLOAD_FAILED");
        logAppDis(ctx, packName, sp, AppAnalysisConstant.APP_DISTRIBUTE_STEP_DOWNLOAD_FAILED);
    }

    /**
     * 应用分发，激活
     * @param ctx
     * @param packName
     */
    public static void logAppDisActive(final Context ctx, final String packName) {
        AppDistributeService.AppDistributeRecord record = AppDistributeService.getInstance().getRecord(packName);
        if(record != null && record.getState() == AppDistributePool.STATE_INSTALL_SUC) {
            Log.e(TAG, "packName:" + packName + "|sp:" + record.getSp() + "|APP_DISTRIBUTE_STEP_ACTIVE");
            logAppDis(ctx, packName, record.getSp(), AppAnalysisConstant.APP_DISTRIBUTE_STEP_ACTIVE);
            AppDistributeService.getInstance().deleteRecord(record.getPkg());
        }
    }

    private static void logAppDis(final Context ctx, final String packName, final int sp, final int stateStep) {
        if (TelephoneUtil.isNetworkAvailable(ctx) && !StringUtil.isEmpty(packName) &&
                AppDistributePreference.getInstance().isAppDistributeAllow()) {
            ThreadUtil.executeMore(new Runnable() {
                @Override
                public void run() {
                    ThemeHttpCommon.postAppDistributeState(ctx, sp, packName, stateStep);
                }
            });
        }
    }

    public static class AppDistributePreference {
        public static final String NAME = "app_distribute_sp";
        private boolean isAppDistributeAllow = true;
        private boolean isSubmitEvent = true;
        private static AppDistributePreference appDistributePreference;
        private SharedPreferences sp;

        /**
         * 用户分发
         */
        private static final String KEY_APP_DISTRIBUTE = "key_app_distribute";

        /**
         * 是否打点
         */
        private static final String KEY_APP_SUBMIT_EVENT = "key_app_submit_event";

        public synchronized static AppDistributePreference getInstance() {
            if (appDistributePreference == null) {
                appDistributePreference = new AppDistributePreference();
            }
            return appDistributePreference;
        }

        private AppDistributePreference() {
            sp = BaseConfig.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
            isAppDistributeAllow = sp.getBoolean(KEY_APP_DISTRIBUTE, true);
            isSubmitEvent = sp.getBoolean(KEY_APP_SUBMIT_EVENT, true);
        }

        public boolean isAppDistributeAllow() {
            return isAppDistributeAllow;
        }

        public void setAppDistributeAllow(boolean isAppDistributeAllow) {
            this.isAppDistributeAllow = isAppDistributeAllow;
            sp.edit().putBoolean(KEY_APP_DISTRIBUTE, isAppDistributeAllow).commit();
        }

        public boolean isSubmitEvent() {
            return isSubmitEvent;
        }

        public void setSubmitEvent(boolean isSubmitEvent) {
            this.isSubmitEvent = isSubmitEvent;
            sp.edit().putBoolean(KEY_APP_SUBMIT_EVENT, isSubmitEvent).commit();
        }
    }
}
