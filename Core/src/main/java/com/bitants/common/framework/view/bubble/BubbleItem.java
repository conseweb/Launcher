package com.bitants.common.framework.view.bubble;

/**
 * Created by michael on 15/4/16.
 */
import android.content.Intent;
import android.view.View;

public class BubbleItem {
    private Intent intent;
    private long duration;
    private String content;
    private String target;
    private LauncherBubbleView mLauncherNotificationView;
    public boolean hasShowed = false;
    private boolean isPushed = false;
    private int pushId;
    public View hostView;

    public BubbleItem() {
    }

    public View getHostView() {
        return this.hostView;
    }

    public void setHostView(View hostView) {
        this.hostView = hostView;
    }

    public LauncherBubbleView getLauncherNotificationView() {
        return this.mLauncherNotificationView;
    }

    public void setLauncherNotificationView(LauncherBubbleView mLauncherNotificationView) {
        this.mLauncherNotificationView = mLauncherNotificationView;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void setIntent(Intent pendingIntent) {
        this.intent = pendingIntent;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTarget(String intentUri) {
        this.target = intentUri;
    }

    public String getTarget() {
        return this.target;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPushed() {
        return this.isPushed;
    }

    public void setPushed(boolean isPushed) {
        this.isPushed = isPushed;
    }

    public int getPushId() {
        return this.pushId;
    }

    public void setPushId(int pushId) {
        this.pushId = pushId;
    }
}
