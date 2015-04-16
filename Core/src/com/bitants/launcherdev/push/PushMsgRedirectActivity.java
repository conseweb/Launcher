package com.bitants.launcherdev.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.push.PushManager;
import com.bitants.launcherdev.push.PushUtil;
import com.bitants.launcherdev.push.model.NotifyIconPushInfo;

/**
 * Created by michael on 2015-04-16.
 */
public class PushMsgRedirectActivity extends Activity {
    public static final String EXTRA_INTENT = "extra_intent";
    public static final String EXTRA_PUSH_ID = "extra_push_id";
    public static final String EXTRA_PUSH_NOTIFY_ICON = "extra_push_notify_icon";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_TYPE_ADD_ICON = "extra_add_icon";
    public static final String EXTRA_ADD_ICON_TITLE = "extra_add_icon_title";
    public static final String EXTRA_ADD_ICON_INTENT = "extra_add_icon_intent";
    public static final String EXTRA_ADD_ICON_PATH = "extra_add_icon_path";

    public PushMsgRedirectActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String e = "" + this.getIntent().getIntExtra("extra_push_id", 0);
            PushManager.getInstance().getPushSDKAdapter().statClickPushNotification(e);
            String type = this.getIntent().getStringExtra("extra_type");
            String intentStr = this.getIntent().getStringExtra("extra_intent");
            Intent intent = StringUtil.isEmpty(intentStr)?null:Intent.parseUri(intentStr, 0);
            if(!StringUtil.isEmpty(type) && "extra_add_icon".equalsIgnoreCase(type)) {
                PushUtil.addPushIconInWorkspace(this.getIntent().getStringExtra("extra_add_icon_title"), this.getIntent().getStringExtra("extra_add_icon_intent"), this.getIntent().getStringExtra("extra_add_icon_path"), e);
            }

            if(intentStr.contains("PushMsgRedirectActivity") && intent != null && intent.getStringExtra("url") != null) {
                String url = intent.getStringExtra("url");
                if(intent.getBooleanExtra("DownloadManager", false)) {
                    PushManager.getInstance().getPushSDKAdapter().redirectToDownloadManager(intent.getStringExtra("title"), "push_download_" + System.currentTimeMillis() + ".apk", intent.getStringExtra("pkgName"), url, this.getIntent().getStringExtra("extra_push_notify_icon"), intent.getIntExtra("sp", -1));
                    this.finish();
                    return;
                }

                intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            }

            if(intent != null) {
                SystemUtil.startActivitySafely(this, intent);
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        this.finish();
    }

    public static Intent getIntentForPushIcon(Context ctx, NotifyIconPushInfo iconInfo) {
        Intent intentTools = new Intent(ctx, PushMsgRedirectActivity.class);
        intentTools.putExtra("extra_intent", iconInfo.getIntent());
        intentTools.putExtra("extra_type", "extra_add_icon");
        intentTools.putExtra("extra_add_icon_title", iconInfo.getIconTitle());
        intentTools.putExtra("extra_add_icon_intent", iconInfo.getIconIntent());
        intentTools.putExtra("extra_add_icon_path", iconInfo.getIconPath());
        intentTools.putExtra("extra_push_id", iconInfo.getId());
        return intentTools;
    }

    public static Intent getIntent(Context ctx, String intent, int pushID, String path) {
        Intent rIntent = new Intent(ctx, PushMsgRedirectActivity.class);
        rIntent.putExtra("extra_intent", intent);
        rIntent.putExtra("extra_push_id", pushID);
        if(!StringUtil.isEmpty(path)) {
            rIntent.putExtra("extra_push_notify_icon", path);
        }

        return rIntent;
    }
}
