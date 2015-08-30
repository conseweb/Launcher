package com.bitants.settings;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.bitants.launcher.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("NewApi")
public class QuickService extends Service {

    private MyReceiver2 receiver2;
    public final static int BUTTON_0 = 0;
    public final static int BUTTON_1 = 1;
    public final static int BUTTON_2 = 2;
    public final static int BUTTON_3 = 3;
    public final static int BUTTON_4 = 4;
    public final static int BUTTON_5 = 5;
    int notifyId = 101;
    /**
     * NotificationCompat 构造器
     */
    NotificationCompat.Builder mBuilder;
    /**
     * 是否fly
     */
    public boolean isfly = false;
    public boolean is3g = false;
    public boolean isblu = false;
    public boolean iswifi = false;
    public int isvab = 0, sdk = 0;
    /**
     * 通知栏按钮点击事件对应的ACTION
     */
    public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
    private NotificationManager mNotificationManager;
    /**
     * 通知栏按钮广播
     */
    private ButtonBroadcastReceiver bReceiver;
    //	private ConnectionChangeReceiver mReceiver;
    private ConnectivityManager mConnectivityManager;
    // 移动数据设置改变系统发送的广播
    //   private static final String NETWORK_CHANGE = "android.intent.action.ANY_DATA_STATE";
    //飞行模式设置改变系统发送的广播
    private static final String AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE";
    private IntentFilter mIntentFilter;
    private static AudioManager mAudioManager;
    private static int mode;

    PackageManager pm;
    private static ContentResolver cr;
    private static WifiManager mWm;
    private static BluetoothAdapter mBluetoothAdapter;
    private static RotationObserver mRotationObserver;
    private static LocationManager mLocationManager;
    private BrightObserver mBrightObserver;
    //飞行模式设置改变系统发送的广播


    //  private IntentFilter mIntentFilter;

    public class MyReceiver2 extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, Intent intent) {
            // TODO Auto-generated method stub
            //if(intent.getAction().equals("com.launcher.sms")){
            if (intent.getAction().equals("com.settings.vab")) {
                showButton(4);

            } else if (intent.getAction().equals("com.settings.blu")) {
                showButton(5);
            } else if (intent.getAction().equals("com.settings.wangluo")) {

                showButton2();
            }
        }
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initButtonReceiver();
        showButtonNotify();

        mWm = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);

        mIntentFilter = new IntentFilter();

        sdk = android.os.Build.VERSION.SDK_INT;
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mRotationObserver = new RotationObserver(new Handler());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mode = mAudioManager.getRingerMode();
        //   int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //  final int currentRing = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        cr = getContentResolver();

        pm = this.getPackageManager();//获得包管理器
        //亮度
        mBrightObserver = new BrightObserver(new Handler());
        mBrightObserver.startObserver();
        //旋转
        mRotationObserver = new RotationObserver(new Handler());
        mRotationObserver.startObserver();
        //registerReceiver();//网络监听


        //蓝牙开关问题
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);


        mIntentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ANDROID_INFO", "Service state changed");
                Intent intent2 = new Intent();
                intent2.setAction("com.settings.fly");
                sendBroadcast(intent2);
                showButton(1);
            }
        };
        registerReceiver(receiver, mIntentFilter);

        // 添加广播接收器过滤的广播
        //注册接收器
        receiver2 = new MyReceiver2();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.settings.blu");
        filter.addAction("com.settings.wangluo");
        filter.addAction("com.settings.vab");
        filter.addAction("com.settings.fly");

        registerReceiver(receiver2, filter);
    }

    private class BrightObserver extends ContentObserver {
        ContentResolver mResolver;

        public BrightObserver(Handler handler) {
            super(handler);
            mResolver = getContentResolver();
        }

        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            //屏幕旋转
            Intent intent2 = new Intent();
            intent2.setAction("com.settings.l2");
            sendBroadcast(intent2);
        }

        //注册观察
        public void startObserver() {
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.SCREEN_BRIGHTNESS), false,
                    this);
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false,
                    this);
        }

        //解除观察
        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }


    //观察屏幕旋转设置变化，类似于注册动态广播监听变化机制
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        public RotationObserver(Handler handler) {
            super(handler);
            mResolver = getContentResolver();
            // TODO Auto-generated constructor stub
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            //屏幕旋转
            Intent intent2 = new Intent();
            intent2.setAction("com.settings.xuan");
            sendBroadcast(intent2);
        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                    this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }


    private final ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            boolean enabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Intent intent2 = new Intent();
            intent2.setAction("com.settings.gps");
            sendBroadcast(intent2);
            System.out.println("gps enabled? " + enabled);
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        clearNotify(200);
        unregisterReceiver(receiver2);
        getContentResolver().unregisterContentObserver(mGpsMonitor);
        //	 this.unregisterReceiver(mReceiver);
        mRotationObserver.stopObserver();
        mBrightObserver.stopObserver();
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initButtonReceiver();
        showButtonNotify();

        mWm = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);

        mIntentFilter = new IntentFilter();

        sdk = android.os.Build.VERSION.SDK_INT;
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mRotationObserver = new RotationObserver(new Handler());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mode = mAudioManager.getRingerMode();
        //   int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //  final int currentRing = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        cr = getContentResolver();

        pm = this.getPackageManager();//获得包管理器
        //亮度
        mBrightObserver = new BrightObserver(new Handler());
        mBrightObserver.startObserver();
        //旋转
        mRotationObserver = new RotationObserver(new Handler());
        mRotationObserver.startObserver();
        //registerReceiver();//网络监听


        //蓝牙开关问题
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);


        mIntentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ANDROID_INFO", "Service state changed");
                Intent intent2 = new Intent();
                intent2.setAction("com.settings.fly");
                sendBroadcast(intent2);
                showButton(1);
            }
        };
        registerReceiver(receiver, mIntentFilter);

        // 添加广播接收器过滤的广播
        //注册接收器
        receiver2 = new MyReceiver2();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.settings.blu");
        filter.addAction("com.settings.wangluo");
        filter.addAction("com.settings.vab");
        filter.addAction("com.settings.fly");

        registerReceiver(receiver2, filter);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 带按钮的通知栏
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void showButtonNotify() {
        NotificationCompat.Builder mBuilder = new Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_5button);
        //	mRemoteViews.setImageViewResource(R.id.btn_custom_0, R.drawable.ant_settings_ic_launcher);
        //API3.0 以上的时候显示按钮
        //如果版本号低于（3。0），那么不显示按钮
        if (sdk <= 9) {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.INVISIBLE);


        } else {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
            //

            mRemoteViews.setTextViewText(R.id.btn_t_0, getString(R.string.ant_set_name));


            if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("1")) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_1, R.drawable.ant_settings_airplane_on_nor);
            } else {
                mRemoteViews.setImageViewResource(R.id.btn_custom_1, R.drawable.ant_settings_airplane_off_nor);
            }
            mRemoteViews.setTextViewText(R.id.btn_t_1, getString(R.string.ant_set_fly));

            // mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if (getMobileDataStatus()) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_2, R.drawable.ant_settings_gprs_on_nor);
            } else {
                mRemoteViews.setImageViewResource(R.id.btn_custom_2, R.drawable.ant_settings_gprs_off_nor);
            }
            ;
            mRemoteViews.setTextViewText(R.id.btn_t_2, getString(R.string.ant_set_3g));

            if (isWiFi()) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_3, R.drawable.ant_settings_wifi_on_nor);
                // mWm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                // int wifiState = wifiMgr.getWifiState();
                WifiInfo info = mWm.getConnectionInfo();
                String wifiId = info != null ? info.getSSID() : null;
                int len = wifiId.length();
                if (len >= 5) {
                    wifiId = wifiId.substring(1, len - 1);
                }
                if (wifiId.equals("unknown")) {
                    wifiId = getString(R.string.ant_set_wifi);
                }
                ;
                mRemoteViews.setTextViewText(R.id.btn_t_3, wifiId);
            } else {
                mRemoteViews.setTextViewText(R.id.btn_t_3, getString(R.string.ant_set_wifi));
                mRemoteViews.setImageViewResource(R.id.btn_custom_3, R.drawable.ant_settings_wifi_off_nor);
            }
            ;

            //	mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mode = mAudioManager.getRingerMode();
            //	mode=mAudioManager.getRingerMode();
            switch (mode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    mRemoteViews.setTextViewText(R.id.btn_t_4, getString(R.string.ant_set_vab2));
                    mRemoteViews.setImageViewResource(R.id.btn_custom_4, R.drawable.ant_settings_rightone_on_nor);
                    //qing.setText("情景模式:(震动)");
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    mRemoteViews.setTextViewText(R.id.btn_t_4, getString(R.string.ant_set_vab1));
                    mRemoteViews.setImageViewResource(R.id.btn_custom_4, R.drawable.ant_settings_cilent_on_nor);

                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    mRemoteViews.setTextViewText(R.id.btn_t_4, getString(R.string.ant_set_vab));
                    mRemoteViews.setImageViewResource(R.id.btn_custom_4, R.drawable.ant_settings_ringtone_vibrate_nor);

                    break;
                default:
                    break;
            }
            ;

            //蓝牙开关
            //  mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_5, R.drawable.ant_settings_bluetooth_off_nor);
            } else if (!mBluetoothAdapter.isEnabled()) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_5, R.drawable.ant_settings_bluetooth_off_nor);
            } else {
                mRemoteViews.setImageViewResource(R.id.btn_custom_5, R.drawable.ant_settings_bluetooth_on_nor);
            }
            ;
            mRemoteViews.setTextViewText(R.id.btn_t_5, getString(R.string.ant_set_blu));


            //Intent it2 = new Intent(this, QuickSettings.class);
            //it2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            // PendingIntent piIntent2 = PendingIntent.getActivity(this, 0, it2, 2);
            // it2.putExtra(INTENT_BUTTONID_TAG, BUTTON_0);
            //mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_0, piIntent2);


            Intent it3 = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            PendingIntent piIntent3 = PendingIntent.getActivity(this, 1, it3, 2);
            // it3.putExtra(INTENT_BUTTONID_TAG, BUTTON_0);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_1, piIntent3);

            //点击的事件处理
            Intent buttonIntent = new Intent(ACTION_BUTTON);


            buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_2);
            PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_2, intent_paly);

            buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_3);
            PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_3, intent_next);

            buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_4);
            PendingIntent intent_next2 = PendingIntent.getBroadcast(this, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_4, intent_next2);

            buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_5);
            PendingIntent intent_next3 = PendingIntent.getBroadcast(this, 5, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_5, intent_next3);
        }
        //点击通知之后需要跳转的页面
        Intent resultIntent = new Intent(this, QuickSettings.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //使用TaskStackBuilder为“通知页面”设置返回关系
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //为点击通知后打开的页面设定 返回 页面。（在manifest中指定）
        //  stackBuilder.addParentStack(QuickSettings.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //mBuilder.setContentIntent(pIntent);.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))

        mBuilder.setContent(mRemoteViews)

                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(getString(R.string.ant_set_notif))
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.ant_settings_ic_launcher)
                .setContentText(getString(R.string.ant_set_name))
                .setContentTitle(getString(R.string.ant_set_click));
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        //会报错，还在找解决思路
//			notify.contentView = mRemoteViews;
//			notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        mNotificationManager.notify(200, notify);
    }

    /**
     * 带按钮的通知栏
     */
    public void showButton2() {
        NotificationCompat.Builder mBuilder = new Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_5button);
        //mRemoteViews.setImageViewResource(R.id.btn_custom_0, R.drawable.ic_launcher);
        //API3.0 以上的时候显示按钮
        //如果版本号低于（3。0），那么不显示按钮
        if (sdk <= 9) {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.INVISIBLE);


        } else {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
            //


            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (getMobileDataStatus()) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_2, R.drawable.ant_settings_gprs_on_nor);
            } else {
                mRemoteViews.setImageViewResource(R.id.btn_custom_2, R.drawable.ant_settings_gprs_off_nor);
            }
            ;
            mRemoteViews.setTextViewText(R.id.btn_t_2, getString(R.string.ant_set_3g));

            if (isWiFi()) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_3, R.drawable.ant_settings_wifi_on_nor);
                mWm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                // int wifiState = wifiMgr.getWifiState();
                WifiInfo info = mWm.getConnectionInfo();
                String wifiId = info != null ? info.getSSID() : null;
                int len = wifiId.length();
                if (len >= 5) {
                    wifiId = wifiId.substring(1, len - 1);
                }
                if (wifiId.equals("unknown")) {
                    wifiId = getString(R.string.ant_set_wifi);
                }
                ;
                mRemoteViews.setTextViewText(R.id.btn_t_3, wifiId);
            } else {
                mRemoteViews.setTextViewText(R.id.btn_t_3, getString(R.string.ant_set_wifi));
                mRemoteViews.setImageViewResource(R.id.btn_custom_3, R.drawable.ant_settings_wifi_off_nor);
            }
            ;

        }

        //点击通知之后需要跳转的页面
        Intent resultIntent = new Intent(this, QuickSettings.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //使用TaskStackBuilder为“通知页面”设置返回关系
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //为点击通知后打开的页面设定 返回 页面。（在manifest中指定）
        // stackBuilder.addParentStack(QuickSettings.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //mBuilder.setContentIntent(pIntent);.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))

        mBuilder.setContent(mRemoteViews)

                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(getString(R.string.ant_set_notif))
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.ant_settings_ic_launcher)
                .setContentText(getString(R.string.ant_set_name))
                .setContentTitle(getString(R.string.ant_set_click));

        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        //会报错，还在找解决思路
//			notify.contentView = mRemoteViews;
//			notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        mNotificationManager.notify(200, notify);
    }

    /**
     * 带按钮的通知栏
     */
    public void showButton(int i) {
        NotificationCompat.Builder mBuilder = new Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_5button);
        //mRemoteViews.setImageViewResource(R.id.btn_custom_0, R.drawable.ic_launcher);
        //API3.0 以上的时候显示按钮
        //如果版本号低于（3。0），那么不显示按钮
        if (sdk <= 9) {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.INVISIBLE);
                        /*
						mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
						mBuilder.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
						.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
						.setTicker("快捷开关在通知栏")
						.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
						.setOngoing(true)
						.setSmallIcon(R.drawable.ant_settings_ic_launcher);
				Notification notify = mBuilder.build();
				notify.flags = Notification.FLAG_ONGOING_EVENT;
				//会报错，还在找解决思路
                notify.contentView = mRemoteViews;
//				notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
				mNotificationManager.notify(200, notify);
				*/
        } else {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
            //


            switch (i) {

                case 1:
                    if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("1")) {
                        mRemoteViews.setImageViewResource(R.id.btn_custom_1, R.drawable.ant_settings_airplane_on_nor);
                    } else {
                        mRemoteViews.setImageViewResource(R.id.btn_custom_1, R.drawable.ant_settings_airplane_off_nor);
                    }
                    mRemoteViews.setTextViewText(R.id.btn_t_1, getString(R.string.ant_set_fly));
                    break;

                case 2:
                    mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (getMobileDataStatus()) {
                        mRemoteViews.setImageViewResource(R.id.btn_custom_2, R.drawable.ant_settings_gprs_on_nor);
                    } else {
                        mRemoteViews.setImageViewResource(R.id.btn_custom_2, R.drawable.ant_settings_gprs_off_nor);
                    }
                    ;
                    mRemoteViews.setTextViewText(R.id.btn_t_2, getString(R.string.ant_set_3g));
                    break;
                case 3:
                    if (isWiFi()) {
                        mRemoteViews.setImageViewResource(R.id.btn_custom_3, R.drawable.ant_settings_wifi_on_nor);
                        mWm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        // int wifiState = wifiMgr.getWifiState();
                        WifiInfo info = mWm.getConnectionInfo();
                        String wifiId = info != null ? info.getSSID() : null;
                        int len = wifiId.length();
                        if (len >= 5) {
                            wifiId = wifiId.substring(1, len - 1);
                        }
                        if (wifiId.equals("unknown")) {
                            wifiId = getString(R.string.ant_set_wifi);
                        }
                        ;
                        mRemoteViews.setTextViewText(R.id.btn_t_3, wifiId);
                    } else {
                        mRemoteViews.setTextViewText(R.id.btn_t_3, getString(R.string.ant_set_wifi));
                        mRemoteViews.setImageViewResource(R.id.btn_custom_3, R.drawable.ant_settings_wifi_off_nor);
                    }
                    ;

                    break;
                case 4:
                    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mode = mAudioManager.getRingerMode();
                    //	mode=mAudioManager.getRingerMode();
                    switch (mode) {
                        case AudioManager.RINGER_MODE_NORMAL:
                            mRemoteViews.setTextViewText(R.id.btn_t_4, getString(R.string.ant_set_vab2));
                            mRemoteViews.setImageViewResource(R.id.btn_custom_4, R.drawable.ant_settings_rightone_on_nor);
                            //qing.setText("情景模式:(震动)");
                            break;
                        case AudioManager.RINGER_MODE_SILENT:
                            mRemoteViews.setTextViewText(R.id.btn_t_4, getString(R.string.ant_set_vab1));
                            mRemoteViews.setImageViewResource(R.id.btn_custom_4, R.drawable.ant_settings_cilent_on_nor);

                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            mRemoteViews.setTextViewText(R.id.btn_t_4, getString(R.string.ant_set_vab));
                            mRemoteViews.setImageViewResource(R.id.btn_custom_4, R.drawable.ant_settings_ringtone_vibrate_nor);

                            break;
                        default:
                            break;
                    }
                    ;
                    break;
                case 5:
                    //蓝牙开关
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        isblu = true;
                        mRemoteViews.setImageViewResource(R.id.btn_custom_5, R.drawable.ant_settings_bluetooth_off_nor);
                    } else if (!mBluetoothAdapter.isEnabled()) {
                        isblu = false;
                        mRemoteViews.setImageViewResource(R.id.btn_custom_5, R.drawable.ant_settings_bluetooth_off_nor);
                    } else {
                        isblu = true;
                        mRemoteViews.setImageViewResource(R.id.btn_custom_5, R.drawable.ant_settings_bluetooth_on_nor);
                    }
                    ;
                    mRemoteViews.setTextViewText(R.id.btn_t_5, getString(R.string.ant_set_blu));
                    break;
            }

            //点击通知之后需要跳转的页面
            Intent resultIntent = new Intent(this, QuickSettings.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            //使用TaskStackBuilder为“通知页面”设置返回关系
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //为点击通知后打开的页面设定 返回 页面。（在manifest中指定）
            // stackBuilder.addParentStack(QuickSettings.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            //mBuilder.setContentIntent(pIntent);.setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))

            mBuilder.setContent(mRemoteViews)

                    .setContentIntent(pIntent)
                    .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                    .setTicker(getString(R.string.ant_set_notif))
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ant_settings_ic_launcher)
                    .setContentText(getString(R.string.ant_set_name))
                    .setContentTitle(getString(R.string.ant_set_click));

            Notification notify = mBuilder.build();
            notify.flags = Notification.FLAG_ONGOING_EVENT;

            //会报错，还在找解决思路
//					notify.contentView = mRemoteViews;
//					notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
            mNotificationManager.notify(200, notify);

        }
    }

    /**
     * 带按钮的通知栏点击广播接收
     */
    public void initButtonReceiver() {
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);
    }

    /**
     * 清除当前创建的通知栏
     */
    public void clearNotify(int notifyId) {
        mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//			mNotification.cancel(getResources().getString(R.string.app_name));
    }

    public final static String INTENT_BUTTONID_TAG = "ButtonId";

    /**
     * 广播监听按钮点击时间
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(ACTION_BUTTON)) {
                //button idled by id
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_0:
                        Intent it2 = new Intent(QuickService.this, QuickSettings.class);
                        it2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(it2);
                        break;
                    case BUTTON_1:
                        if (isfly == false) {
                            isfly = true;
                        } else {
                            isfly = false;
                        }


                        if (Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0) {
                            if (sdk > 16) {
                                Intent intent1 = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                                intent1.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent1);

                                //  closeNotifications(getApplicationContext());

                            } else {

                                boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
                                Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
                                Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                                i.putExtra("state", !isEnabled);
                                sendBroadcast(i);
                                //mfly.setChecked(true);
                            }
                        } else {
                            if (sdk > 16) {
                                Intent intent1 = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                                intent1.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent1);
                                // closeNotifications(getApplicationContext());
                            } else {
                                // boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0;
                                Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
                                Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                                i.putExtra("state", false);
                                sendBroadcast(i);
                            }
                        }
                        // QuickService.this.startActivity(new Intent( android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                        // showButton(1);
                        break;
                    case BUTTON_2:
                        if (is3g == false) {
                            is3g = true;
                        } else {
                            is3g = false;
                        }
                        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (getMobileDataStatus()) {
                            setMobileDataStatus(false);
                        } else {
                            setMobileDataStatus(false);
                            setMobileDataStatus(true);
                        }
                        // showButton(2);
                        break;
                    case BUTTON_3:
                        if (iswifi == false) {
                            iswifi = true;
                        } else {
                            iswifi = false;
                        }

                        mWm = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
                        if (isWiFi()) {
                            setWifi(false);
                        } else {
                            setWifi(false);
                            setWifi(true);
                        }
                        // showButton(3);
                        break;
                    case BUTTON_4:
                        switch (isvab) {
                            case 0:
                                isvab = 1;
                                break;
                            case 1:
                                isvab = 2;
                                break;
                            case 2:
                                isvab = 0;
                                break;
                            default:
                                isvab = 0;
                                break;
                        }
                        mode = mAudioManager.getRingerMode();
                        switch (mode) {
                            case AudioManager.RINGER_MODE_NORMAL:
                                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                                //qing.setText("情景模式:(震动)");
                                break;
                            case AudioManager.RINGER_MODE_SILENT:
                                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                //mAudioManager.setStreamVolume(AudioManager.STREAM_RING, currentRing, 1);

                                //qing.setText("情景模式:(正常)");

                                break;
                            case AudioManager.RINGER_MODE_VIBRATE:
                                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                //mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 1);

                                //qing.setText("情景模式:(静音)");
                                break;
                            default:
                                break;
                        }
                        ;//showButton(4);

                        break;
                    case BUTTON_5:
                        // if (isblu==false){isblu=true;}else{isblu=false;}
                        // final Intent settings = new Intent(QuickService.this,Startbluetooth.class);
                        //   settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        //           Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        //    startActivity(settings);

                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {

                        } else {
                            if (isblu == false) {
                                mBluetoothAdapter.enable();
                                // isblu=true;
                            }

                            if (isblu == true) {
                                mBluetoothAdapter.disable();
                                // isblu=false;
                            }
                            ;
                        }
                        // closeNotifications(getApplicationContext());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }


    public void setWifi(boolean isEnable) {

        //
        if (mWm == null) {
            mWm = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
            return;
        }
        if (isEnable) {// 开启wifi
            if (!mWm.isWifiEnabled()) {
                mWm.setWifiEnabled(true);
            }
        } else {
            // 关闭 wifi
            if (mWm.isWifiEnabled()) {
                mWm.setWifiEnabled(false);
            }
        }

    }


    //获取移动数据开关状态
    private boolean getMobileDataStatus() {
        String methodName = "getMobileDataEnabled";
        Class cmClass = mConnectivityManager.getClass();
        Boolean isOpen = null;

        try {
            Method method = cmClass.getMethod(methodName, new Class[0]);

            isOpen = (Boolean) method.invoke(mConnectivityManager, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    // 通过反射实现开启或关闭移动数据
    private void setMobileDataStatus(boolean enabled) {
        try {
            Class<?> conMgrClass = Class.forName(mConnectivityManager.getClass().getName());
            //得到ConnectivityManager类的成员变量mService（ConnectivityService类型）
            Field iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            //mService成员初始化
            Object iConMgr = iConMgrField.get(mConnectivityManager);
            //得到mService对应的Class对象
            Class<?> iConMgrClass = Class.forName(iConMgr.getClass().getName());
    	      /*得到mService的setMobileDataEnabled(该方法在android源码的ConnectivityService类中实现)， 
    	       * 该方法的参数为布尔型，所以第二个参数为Boolean.TYPE 
    	       */
            Method setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
                    "setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
    	      /*调用ConnectivityManager的setMobileDataEnabled方法（方法是隐藏的）， 
    	       * 实际上该方法的实现是在ConnectivityService(系统服务实现类)中的 
    	       */
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // .........
    public boolean isWiFi() {

        if (mConnectivityManager != null) {
            NetworkInfo[] infos = mConnectivityManager.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    //备份下面的代码   && ni.isConnected()
                    if (ni.getTypeName().equals("WIFI") && ni.isConnectedOrConnecting()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
