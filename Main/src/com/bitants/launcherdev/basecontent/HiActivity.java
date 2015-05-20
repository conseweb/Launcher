package com.bitants.launcherdev.basecontent;

import android.app.Activity;
import android.os.Bundle;
import com.bitants.launcherdev.kitset.Analytics.HiAnalytics;

/**
 * 1. 功能统计初始化
 * <br>Author:ryan
 * <br>Date:2012-7-23上午10:02:21
 */
public class HiActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		HiAnalytics.init(this);
	}
}
