package com.bitants.launcherdev.push.model;

import com.bitants.launcherdev.push.model.NotifyPushInfo;
/**
 * Created by michael on 2015-04-16.
 */
public class NotifyBigPicPushInfo extends NotifyPushInfo {
    private String bigPicUrl;
    private String bigPicPath;
    private String btn1_text;
    private String btn1_intent;
    private String btn2_text;
    private String btn2_intent;

    public NotifyBigPicPushInfo() {
    }

    public String getBigPicUrl() {
        return this.bigPicUrl;
    }

    public void setBigPicUrl(String url) {
        this.bigPicUrl = url;
    }

    public String getBtn1_text() {
        return this.btn1_text;
    }

    public void setBtn1_text(String btn1_text) {
        this.btn1_text = btn1_text;
    }

    public String getBtn1_intent() {
        return this.btn1_intent;
    }

    public void setBtn1_intent(String btn1_intent) {
        this.btn1_intent = btn1_intent;
    }

    public String getBtn2_text() {
        return this.btn2_text;
    }

    public void setBtn2_text(String btn2_text) {
        this.btn2_text = btn2_text;
    }

    public String getBtn2_intent() {
        return this.btn2_intent;
    }

    public void setBtn2_intent(String btn2_intent) {
        this.btn2_intent = btn2_intent;
    }

    public String getBigPicPath() {
        return this.bigPicPath;
    }

    public void setBigPicPath(String path) {
        this.bigPicPath = path;
    }

    public int getType() {
        return 2;
    }
}
