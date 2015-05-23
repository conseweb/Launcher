package com.bitants.launcherdev.kitset.util;

import com.bitants.common.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.Analytics.MoAnalytics;
import com.bitants.common.launcher.config.BaseConfig;

import java.net.URLEncoder;

/**
 * description: <br/>
 */
public class CUIDUtil {

    private static String CUID = "";

    private static String CUID_PART = "";


    public static String getCUIDPART() {
        if (StringUtil.isEmpty(CUID_PART) && BaseConfig.getApplicationContext() != null) {
            try {
                CUID = MoAnalytics.getCUID(BaseConfig.getApplicationContext());
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
                CUID = MoAnalytics.getCUID(BaseConfig.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CUID;
    }


}
