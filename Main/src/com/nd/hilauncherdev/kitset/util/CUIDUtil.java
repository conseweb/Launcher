package com.nd.hilauncherdev.kitset.util;

import java.net.URLEncoder;

import com.nd.hilauncherdev.kitset.Analytics.HiAnalytics;
import com.nd.hilauncherdev.launcher.config.BaseConfig;

/**
 * description: <br/>
 * author: Administrator<br/>
 * data: 2014/8/25<br/>
 */
public class CUIDUtil {

    private static String CUID = "";

    private static String CUID_PART = "";


    public static String getCUIDPART() {
        if (StringUtil.isEmpty(CUID_PART) && BaseConfig.getApplicationContext() != null) {
            try {
                CUID = HiAnalytics.getCUID(BaseConfig.getApplicationContext());
                String CUID_encode = URLEncoder.encode(CUID, "UTF-8");
                if (!StringUtil.isEmpty(CUID_encode)) {
                    CUID_PART = "&CUID=" + CUID_encode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CUID_PART;
    }


    public static String getCUID() {
        if(StringUtil.isEmpty(CUID) && BaseConfig.getApplicationContext() != null){
            try {
                CUID = HiAnalytics.getCUID(BaseConfig.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CUID;
    }


}
