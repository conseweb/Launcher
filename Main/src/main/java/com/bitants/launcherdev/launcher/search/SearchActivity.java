package com.bitants.launcherdev.launcher.search;

import android.app.Activity;
import android.os.Bundle;

import com.avos.avoscloud.AVAnalytics;
import com.bitants.launcher.R;

public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 跟踪统计应用的打开情况
        AVAnalytics.trackAppOpened(getIntent());
    }


}
