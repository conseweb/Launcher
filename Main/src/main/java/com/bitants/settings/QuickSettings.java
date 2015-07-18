package com.bitants.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bitants.launcher.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class QuickSettings extends Activity {

    static ActivityManager mActivityManager;

    private static ToggleButton mwifi, mlig, m3g, mrota, mfly, mhot, mclo, mblu, mcal, mcam, mla, ml2, mvab, mset, mloc, mgps;
    private static TextView vabt, wifit, mclose, l2t;

    PackageManager pm;
    private static PowerManager pmm;
    private static ContentResolver cr;
    private static WifiManager mWm;
    private static BluetoothAdapter mBluetoothAdapter;
    LocationManager alm;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static String pac = "";
    private int b, qf, qf2, bl;
    // private static String f20,f21,f22;
    private MyReceiver receiver;
    private ConnectivityManager mConnectivityManager;

    private IntentFilter mIntentFilter;
    private static AudioManager mAudioManager;
    private static int volume = 0, mode;
    private Camera mCamera;
    private Parameters parameters;

    /**
     * 是否fly
     */
    public boolean isfly = false;
    public boolean is3g = false;
    public boolean isblu = false;
    public boolean iswifi = false;
    public int isvab = 0;

    int ran = 1;

    public class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("com.settings.xuan")) {
                //屏幕旋转
                if (getRotationStatus(QuickSettings.this) == 0) {
                    mrota.setChecked(false);
                } else {
                    mrota.setChecked(true);
                }
                ;

                Log.i("", "收到xuan");

            } else if (intent.getAction().equals("com.settings.gps")) {

                if (hasGPSDevice(QuickSettings.this)) {
                    if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        mgps.setChecked(true);
                    } else {
                        mgps.setChecked(false);
                    }
                    ;
                } else {
                    mgps.setChecked(false);
                }
                Log.i("", "收到gps");

            } else if (intent.getAction().equals("com.settings.vab")) {

                Bundle b = intent.getExtras();
                int vab = b.getInt("vab", 0);

                //mode=mAudioManager.getRingerMode();
                switch (vab) {
                    case 1:
                        mvab.setBackgroundResource(R.drawable.ant_settings_rightone_on_nor);
                        vabt.setText(getString(R.string.ant_set_vab2));
                        break;
                    case 0:
                        mvab.setBackgroundResource(R.drawable.ant_settings_cilent_on_nor);
                        vabt.setText(getString(R.string.ant_set_vab1));
                        //qing.setText("情景模式:(静音)");
                        break;
                    case 2:
                        mvab.setBackgroundResource(R.drawable.ant_settings_ringtone_vibrate_nor);
                        vabt.setText(getString(R.string.ant_set_vab));
                        //qing.setText("情景模式:(震动)");
                        break;
                }
                ;
                Log.i("", "收到vab");
            } else if (intent.getAction().equals("com.settings.hot")) {

                if (getWifiApState(QuickSettings.this) == 13) {
                    qf2 = 1;
                    mhot.setChecked(true);
                } else if (qf2 == 1) {
                    mhot.setChecked(false);
                    qf2 = 0;
                }
                ;
                Log.i("", "收到hot");

            } else if (intent.getAction().equals("com.settings.blu")) {


                //蓝牙开关有问题
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    mblu.setChecked(false);
                    bl = 0;
                } else if (!mBluetoothAdapter.isEnabled()) {
                    mblu.setChecked(false);
                    bl = 0;// 本地蓝牙没开
                } else {
                    mblu.setChecked(true);
                    bl = 1;
                }
                ;
                Log.i("", "收到blu");
            } else if (intent.getAction().equals("com.settings.l2")) {


                int mode = getScreenMode();
                int li = getScreenBrightness();

                if (li > 0 & li <= 120) {
                    ml2.setChecked(false);
                    l2t.setText(getString(R.string.ant_set_l21));
                } else if (li > 120 & li <= 255) {
                    ml2.setChecked(true);
                    l2t.setText(getString(R.string.ant_set_l22));
                }

                if (mode == 1) {
                    mla.setChecked(true);
                } else {
                    mla.setChecked(false);
                }
                ;
                Log.i("", "收到l2");
            } else if (intent.getAction().equals("com.settings.fly")) {

                if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")) {

                    mfly.setChecked(false);

                } else {
                    mfly.setChecked(true);
                    //qing.setText("情景模式:(飞行模式)");
                }
                Log.i("", "收到fly");
            } else if (intent.getAction().equals("com.settings.wangluo")) {

                if (isWiFi()) {
                    qf = 1;
                    mwifi.setChecked(true);
                    WifiInfo info = mWm.getConnectionInfo();
                    String wifiId = info != null ? info.getSSID() : null;
                    int len = wifiId.length();
                    if (len >= 5) {
                        wifiId = wifiId.substring(1, len - 1);
                    }
                    wifit.setText(wifiId);
                } else {
                    mwifi.setChecked(false);
                    wifit.setText("WLAN");
                    qf = 0;
                }
                ;

                if (getMobileDataStatus()) {
                    m3g.setChecked(true);
                } else {
                    m3g.setChecked(false);
                }
                ;
                Log.i("", "收到3gwifi");
            }
        }
    }

    //得到屏幕旋转的状态
    private int getRotationStatus(Context context) {
        int status = 0;
        try {
            status = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return status;
    }

    private void setRotationStatus(ContentResolver resolver, int status) {
        //得到uri
        Uri uri = Settings.System.getUriFor("accelerometer_rotation");
        //沟通设置status的值改变屏幕旋转设置
        Settings.System.putInt(resolver, "accelerometer_rotation", status);
        //通知改变
        resolver.notifyChange(uri, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 18) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout); 7 relativeLayout.setPadding(0, getActionBarHeight()+getStatusBarHeight(), 0, 0); 8
        }
        Intent intent1 = new Intent();
        intent1.setClass(this, QuickService.class);
        // stopService(intent1);
        startService(intent1);

        pmm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);


        sp = getSharedPreferences("SP", MODE_PRIVATE);
        editor = sp.edit();

        //注册接收器
        receiver = new MyReceiver();

        mWm = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);

        mIntentFilter = new IntentFilter();
        // 添加广播接收器过滤的广播
        mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mode = mAudioManager.getRingerMode();
        //   int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //  final int currentRing = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        cr = getContentResolver();

        mCamera = Camera.open();
        parameters = mCamera.getParameters();
        pm = this.getPackageManager();//获得包管理器

        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
        setContentView(R.layout.setting);

        mclose = (TextView) findViewById(R.id.mclose);

        mclose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                finish();

            }
        });

        vabt = (TextView) findViewById(R.id.vabt);
        wifit = (TextView) findViewById(R.id.wifit);
        l2t = (TextView) findViewById(R.id.l2t);

        mloc = (ToggleButton) findViewById(R.id.loc);
        mloc.setSelected(true);
        mloc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(QuickSettings.this, LockActivity.class);
                finish();
                startActivity(intent);

            }
        });

        mclo = (ToggleButton) findViewById(R.id.clo);
        mclo.setSelected(true);
        mclo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String pac = getclock();
                if (pac.equals("") || pac.equals(null)) {
                } else {
                    try {
                        Intent intent6 = new Intent();
                        intent6 = pm.getLaunchIntentForPackage(pac);
                        intent6.setAction(Intent.ACTION_MAIN);
                        intent6.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent6);
                    } catch (Exception e) {

                    }
                    finish();
                }
            }
        });

        mset = (ToggleButton) findViewById(R.id.set);
        mset.setSelected(true);
        mset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Intent settings = new Intent(Settings.ACTION_SETTINGS);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(settings);
                finish();
            }
        });

        mcam = (ToggleButton) findViewById(R.id.cam);
        mcam.setSelected(true);
        mcam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mCamera.release();
                Intent intentCamera = new Intent();
                intentCamera.setAction("android.media.action.STILL_IMAGE_CAMERA");
                intentCamera.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intentCamera);
            }
        });
        mcal = (ToggleButton) findViewById(R.id.cal);
        mcal.setSelected(true);
        mcal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String pac = getcalc();
                if (pac.equals("") || pac.equals(null)) {
                } else {
                    try {
                        Intent intent6 = new Intent();
                        intent6 = pm.getLaunchIntentForPackage(pac);
                        intent6.setAction(Intent.ACTION_MAIN);
                        intent6.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent6);
                    } catch (Exception e) {

                    }
                    finish();
                }
            }
        });

        //wifi
        mwifi = (ToggleButton) findViewById(R.id.wifi);
        mwifi.setSelected(true);
        mwifi.setOnCheckedChangeListener(mChangeListener2);

        mwifi.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } else {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                return false;
            }
        });


        //hotwifi
        mhot = (ToggleButton) findViewById(R.id.hot);
        mhot.setSelected(true);
        mhot.setOnCheckedChangeListener(mChangeListener20);

        mhot.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));

                return false;
            }
        });

        //fly
        mfly = (ToggleButton) findViewById(R.id.fly);
        // mfly.setSelected(true);

        mfly.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));

            }
        });
        mvab = (ToggleButton) findViewById(R.id.vab);
        mvab.setSelected(true);
        mvab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mode = mAudioManager.getRingerMode();
                switch (mode) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        mvab.setBackgroundResource(R.drawable.ant_settings_ringtone_vibrate_nor);
                        //qing.setText("情景模式:(震动)");
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        //mAudioManager.setStreamVolume(AudioManager.STREAM_RING, currentRing, 1);
                        mvab.setBackgroundResource(R.drawable.ant_settings_rightone_on_nor);
                        //qing.setText("情景模式:(正常)");

                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        //mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 1);
                        mvab.setBackgroundResource(R.drawable.ant_settings_cilent_on_nor);
                        //qing.setText("情景模式:(静音)");
                        break;
                    default:
                        break;
                }
                ;


            }
        });


        if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")) {

            mfly.setChecked(false);

            switch (mode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    mvab.setBackgroundResource(R.drawable.ant_settings_rightone_on_nor);
                    vabt.setText(getString(R.string.ant_set_vab2));
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    mvab.setBackgroundResource(R.drawable.ant_settings_cilent_on_nor);
                    vabt.setText(getString(R.string.ant_set_vab1));
                    //qing.setText("情景模式:(静音)");
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    mvab.setBackgroundResource(R.drawable.ant_settings_ringtone_vibrate_nor);
                    vabt.setText(getString(R.string.ant_set_vab));
                    //qing.setText("情景模式:(震动)");
                    break;
            }
            ;


        } else {
            mfly.setChecked(true);
            //qing.setText("情景模式:(飞行模式)");
        }


        //2g 3g
        m3g = (ToggleButton) findViewById(R.id.edge);
        m3g.setSelected(true);
        m3g.setOnCheckedChangeListener(mChangeListener3);
        m3g.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));

                return false;
            }
        });

        //gps
        mgps = (ToggleButton) findViewById(R.id.gps);
        mgps.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        });


        //蓝牙
        mblu = (ToggleButton) findViewById(R.id.blue);
        mblu.setSelected(true);
        mblu.setOnCheckedChangeListener(mChangeListener5);
        mblu.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

                return false;
            }
        });


        //屏幕旋转
        mrota = (ToggleButton) findViewById(R.id.xuan);
        mrota.setSelected(true);
        mrota.setOnCheckedChangeListener(mChangeListener6);

        //自动亮度
        mla = (ToggleButton) findViewById(R.id.la);
        mla.setSelected(true);
        mla.setOnCheckedChangeListener(mChangeListener8);

        ml2 = (ToggleButton) findViewById(R.id.l2);
        ml2.setSelected(true);
        ml2.setOnCheckedChangeListener(mChangeListener80);

        int mode = getScreenMode();
        int li = getScreenBrightness();

        if (li > 0 & li <= 120) {
            ml2.setChecked(false);
            l2t.setText(getString(R.string.ant_set_l21));
        } else if (li > 120 & li <= 255) {
            ml2.setChecked(true);
            l2t.setText(getString(R.string.ant_set_l22));
        }

        if (mode == 1) {
            mla.setChecked(true);
        } else {
            mla.setChecked(false);
        }
        ;


        //手电筒
        mlig = (ToggleButton) findViewById(R.id.lig);
        mlig.setSelected(true);
        mlig.setOnCheckedChangeListener(mChangeListener7);

        mlig.setChecked(false);


        if (getWifiApState(QuickSettings.this) == 13) {
            qf2 = 1;
            mhot.setChecked(true);
        } else {
            mhot.setChecked(false);
            qf2 = 0;
        }
        ;

        if (isWiFi()) {
            qf = 1;
            mwifi.setChecked(true);
            WifiInfo info = mWm.getConnectionInfo();
            String wifiId = info != null ? info.getSSID() : null;
            int len = wifiId.length();
            if (len >= 5) {
                wifiId = wifiId.substring(1, len - 1);
            }
            wifit.setText(wifiId);
        } else {
            mwifi.setChecked(false);
            wifit.setText("WLAN");
            qf = 0;
        }
        ;

        if (getMobileDataStatus()) {
            m3g.setChecked(true);
        } else {
            m3g.setChecked(false);
        }
        ;

        if (hasGPSDevice(QuickSettings.this)) {
            if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mgps.setBackgroundResource(R.drawable.ant_settings_gps_on_nor);
            } else {
                mgps.setBackgroundResource(R.drawable.ant_settings_gps_off_nor);
            }
            ;
        } else {
            mgps.setBackgroundResource(R.drawable.ant_settings_gps_off_nor);
        }


        //蓝牙开关有问题
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mblu.setChecked(false);
            bl = 0;
        } else if (!mBluetoothAdapter.isEnabled()) {
            mblu.setChecked(false);
            bl = 0;// 本地蓝牙没开
        } else {
            mblu.setChecked(true);
            bl = 1;
        }
        ;


        //屏幕旋转
        if (getRotationStatus(this) == 0) {
            mrota.setChecked(false);
        } else {
            mrota.setChecked(true);
        }
        ;

        //  cr = getContentResolver();

        //注册接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.settings.hot");
        filter.addAction("com.settings.blu");
        filter.addAction("com.settings.wangluo");
        filter.addAction("com.settings.gps");
        filter.addAction("com.settings.xuan");
        filter.addAction("com.settings.vab");
        filter.addAction("com.settings.l2");
        filter.addAction("com.settings.fly");
        registerReceiver(receiver, filter);

    }


    //Wifi开关
    private OnCheckedChangeListener mChangeListener2 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (qf == 1) {
                } else {
                    setWifi(false);
                    setWifi(true);
                }
            } else {
                setWifi(false);
                qf = 0;
            }
        }
    };
    //热点开关
    private OnCheckedChangeListener mChangeListener20 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (qf2 == 0) {
                    Toast.makeText(QuickSettings.this, getString(R.string.ant_set_openhot), Toast.LENGTH_LONG).show();
                    setWifiApEnabled(true);
                }
            } else {
                setWifiApEnabled(false);
                qf2 = 0;
            }
        }
    };

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


    //la自动开关
    private OnCheckedChangeListener mChangeListener8 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (getScreenMode() == 1) {
                } else {
                    setScreenMode(1);
                }
            } else {
                if (getScreenMode() == 1) {
                    setScreenMode(0);
                }
                ;
            }
        }
    };

    //l2自动开关
    private OnCheckedChangeListener mChangeListener80 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                int li = getScreenBrightness();
                if (li > 0 & li <= 120) {
                    saveScreenBrightness(240);
                    setScreenBrightness(240);
                }

            } else {
                int li = getScreenBrightness();

                if (li > 120 & li <= 255) {
                    saveScreenBrightness(110);
                    setScreenBrightness(110);
                }
            }
        }
    };


    //网络开关
    private OnCheckedChangeListener mChangeListener3 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (getMobileDataStatus()) {
                } else {
                    setMobileDataStatus(true);
                }
            } else {
                if (getMobileDataStatus()) {
                    setMobileDataStatus(false);
                }
                ;
            }
        }
    };


    //蓝牙 开关
    private OnCheckedChangeListener mChangeListener5 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(QuickSettings.this, getString(R.string.ant_set_noblue), Toast.LENGTH_SHORT).show();
                } else if (bl == 0) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    //直接打开系统的蓝牙设置面板
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0x1);
                    //直接打开蓝牙
                    mBluetoothAdapter.enable();
                    //关闭蓝牙
                    //adapter.disable();
                    //打开本机的蓝牙发现功能（默认打开120秒，可以将时间最多延长至300秒）
                    Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);//设置持续时间（最多300秒）
                    startActivityForResult(discoveryIntent, 0x1);
                }
            } else {
                mBluetoothAdapter.disable();
            }
        }
    };

    //屏幕旋转 开关
    private OnCheckedChangeListener mChangeListener6 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (getRotationStatus(QuickSettings.this) == 0) {

                    setRotationStatus(getContentResolver(), 1);
                }
            } else {
                if (getRotationStatus(QuickSettings.this) == 1) {

                    setRotationStatus(getContentResolver(), 0);
                }

            }
        }
    };

    //
    private OnCheckedChangeListener mChangeListener7 = new OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                openLight();
            } else {
                closeLight();

            }
        }
    };


    /**
     * 鎵撳紑鎵嬬數
     *
     * @author
     */
    private void openLight() {
        parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    /**
     * 关闭手电
     *
     * @author
     */
    private void closeLight() {
        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
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


//setWifiApEnabled(true);

    //wifi热点开关
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
//wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            mWm.setWifiEnabled(false);
        }
        Method method1 = null;

        if (enabled == true) {
            try {
                method1 = mWm.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                WifiConfiguration netConfig = new WifiConfiguration();

                netConfig.SSID = "HotWifi";
                netConfig.preSharedKey = "";

                netConfig.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.TKIP);

                method1.invoke(mWm, netConfig, true);

            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
        } else {
            try {
                Method method = mWm.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method.invoke(mWm);

                Method method2 = mWm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(mWm, config, false);
            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
        }
        return enabled;

    }


    public boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }


    public void turnGPSOn() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.sendBroadcast(intent);

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
        }
    }

    /**
     * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private int getScreenMode() {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException) {

        }
        return screenMode;
    }

    /**
     * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private void setScreenMode(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    private void saveScreenBrightness(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    private void setScreenBrightness(int paramInt) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }


    public int getWifiApState(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);

            return i;
        } catch (Exception e) {

            return 14;
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


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //  Intent i = new Intent(QuickSettings.this,FloatService.class);

        // stopService(i);startService(i);
        //解除观察变化

        editor.putInt("ff", 0);
        editor.commit();
        mCamera.release();


    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //infos.clear();
        unregisterReceiver(receiver);
        mCamera.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (hasGPSDevice(QuickSettings.this)) {
            if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mgps.setBackgroundResource(R.drawable.ant_settings_gps_on_nor);
            } else {
                mgps.setBackgroundResource(R.drawable.ant_settings_gps_off_nor);
            }
            ;
        } else {
            mgps.setBackgroundResource(R.drawable.ant_settings_gps_off_nor);
        }


        if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
            mfly.setBackgroundResource(R.drawable.ant_settings_airplane_on_nor);
        } else {
            mfly.setBackgroundResource(R.drawable.ant_settings_airplane_off_nor);
        }
        //注册接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.settings.hot");
        filter.addAction("com.settings.blu");
        filter.addAction("com.settings.wangluo");
        filter.addAction("com.settings.gps");
        filter.addAction("com.settings.xuan");
        filter.addAction("com.settings.vab");
        filter.addAction("com.settings.l2");
        filter.addAction("com.settings.fly");

        registerReceiver(receiver, filter);


    }

    private String getclock() {
        PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);


        try {
            for (int i = 0; i < apps.size(); i++) {
                ResolveInfo pInfo = apps.get(i);
                String pac = pInfo.activityInfo.packageName;
                if (haspac(pac, "clock", "deskclock")) {
                    return pac;
                }

            }

        } catch (Exception ex) {
            return "";
        }

        return "";
    }

    private String getcalc() {
        PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);


        try {
            for (int i = 0; i < apps.size(); i++) {
                ResolveInfo pInfo = apps.get(i);
                String pac = pInfo.activityInfo.packageName;
                if (haspac(pac, "calculator", "calculator2")) {
                    return pac;
                }

            }

        } catch (Exception ex) {
            return "";
        }

        return "";
    }

    public static boolean haspac(String src, String dest, String dest2) {
        boolean flag = false;
        if (src.contains(dest) || src.contains(dest)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取当前系统SDK版本号
     */
    public static int getSystemVersion() {
            /*获取当前系统的android版本号*/
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    public void Sblue() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(QuickSettings.this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //直接打开系统的蓝牙设置面板
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 0x1);
        //直接打开蓝牙
        mBluetoothAdapter.enable();
        //关闭蓝牙
        //adapter.disable();
        //打开本机的蓝牙发现功能（默认打开120秒，可以将时间最多延长至300秒）
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);//设置持续时间（最多300秒）
        startActivityForResult(discoveryIntent, 0x1);

    }


}