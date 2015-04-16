package com.bitants.launcherdev.push.model;

/**
 * Created by michael on 2015-04-16.
 */
public class PushMsg {
    private long id;
    private boolean isRead;
    private String type;
    private String value;
    private long time;
    public static final String TYPE_COMPAIGN = "compaign";
    public static final String TYPE_NOTIFY = "notify";
    public static final String TYPE_POPUP = "popup";
    public static final String TYPE_NOTIFY_ICON = "notify_icon";

    public PushMsg() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
