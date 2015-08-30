package com.bitants.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class SettingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();

        if (action.equals("android.intent.action.BOOT_COMPLETED")) {

            Intent intent1 = new Intent();
            intent1.setClass(context, QuickService.class);
            context.startService(intent1);

        } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {

            Intent intent2 = new Intent();
            intent2.setAction("com.settings.blu");
            context.sendBroadcast(intent2);

        } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {

            Intent intent2 = new Intent();
            intent2.setAction("com.settings.wangluo");
            context.sendBroadcast(intent2);

        } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {

            Intent intent2 = new Intent();
            intent2.setAction("com.settings.hot");
            context.sendBroadcast(intent2);

        } else if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {

            int vab = 0;
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            final int ringerMode = am.getRingerMode();

            switch (ringerMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //normal
                    vab = 1;
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //vibrate
                    vab = 2;
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //silent
                    vab = 0;
                    break;
            }

            Intent intent2 = new Intent();
            intent2.setAction("com.settings.vab");
            intent2.putExtra("vab", vab);
            context.sendBroadcast(intent2);

        }


    }


}
