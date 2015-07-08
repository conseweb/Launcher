package com.bitants.launcherdev.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.avos.avoscloud.AVAnalytics;
import com.bitants.launcher.R;

/**
 * Created by michael on 15/6/5.
 */
public class SplashActivity extends Activity {

    private static final long SPLASH_DISPLAY_TIME = 2500L; /* 3 seconds */
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // allow user to click and dismiss the splash screen prematurely
        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, Launcher.class));
//                SplashActivity.this.finish();
                overridePendingTransition(R.anim.mainfadein,
                        R.anim.splashfadeout);
            }
        });

        // 跟踪统计应用的打开情况
        AVAnalytics.trackAppOpened(getIntent());


        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, Launcher.class);
                SplashActivity.this.startActivity(mainIntent);

//                SplashActivity.this.finish();
                overridePendingTransition(R.anim.mainfadein,
                        R.anim.splashfadeout);
            }
        };

        mHandler.postDelayed(mRunnable, SPLASH_DISPLAY_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, SPLASH_DISPLAY_TIME);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

}
