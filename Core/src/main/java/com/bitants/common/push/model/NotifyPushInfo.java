package com.bitants.common.push.model;

import com.bitants.common.kitset.util.StringUtil;

/**
 * Created by michael on 2015-04-16.
 */
public class NotifyPushInfo {
    private int id;
    private String title;
    private String content;
    private String intent;
    private boolean persist = false;
    private boolean target = false;
    private String notifyIcon;
    private String notifyIconPath;
    private String pos;
    public static final int PUSH_COMMON_NOTIFICATION = 1;
    public static final int PUSH_BIG_PIC_NOTIFICATION = 2;
    public static final int PUSH_MENU_NOTIFICATION = 4;

    public NotifyPushInfo() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTarget() {
        return this.target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }

    public boolean isPersist() {
        return this.persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIntent() {
        return this.intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getNotifyIcon() {
        return this.notifyIcon;
    }

    public void setNotifyIcon(String notifyIcon) {
        this.notifyIcon = notifyIcon;
    }

    public String getNotifyIconPath() {
        return this.notifyIconPath;
    }

    public void setNotifyIconPath(String notifyIconPath) {
        this.notifyIconPath = notifyIconPath;
    }

    public String getPos() {
        return this.pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public int getType() {
        byte type = 1;
        if(!StringUtil.isEmpty(this.getPos()) && String.valueOf(4).equals(this.getPos())) {
            type = 4;
        }

        return type;
    }
}
