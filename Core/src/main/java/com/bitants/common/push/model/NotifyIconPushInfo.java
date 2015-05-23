package com.bitants.common.push.model;

import com.bitants.common.kitset.util.StringUtil;

/**
 * Created by michael on 2015-04-16.
 */
public class NotifyIconPushInfo extends NotifyPushInfo {
    private String iconUrl;
    private String iconIntent;
    private String iconTitle;
    private boolean showIcon;
    private String iconPath;
    private String iconIntentNew;
    private String appendIcon;
    public static final int PUSH_ICON_TYPE_NOTIFICATION = 1;
    public static final int PUSH_ICON_TYPE_ICON = 2;
    public static final int PUSH_ICON_TYPE_REPLACE_INTENT = 4;
    public static final int PUSH_ICON_TYPE_APPEND_ICON = 8;

    public NotifyIconPushInfo() {
    }

    public String getIconPath() {
        return this.iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public boolean isShowIconImmediately() {
        return this.showIcon;
    }

    public void setShowIconImmediately(boolean genIconOnClick) {
        this.showIcon = genIconOnClick;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconIntent() {
        return this.iconIntent;
    }

    public void setIconIntent(String iconIntent) {
        this.iconIntent = iconIntent;
    }

    public String getIconTitle() {
        return this.iconTitle;
    }

    public void setIconTitle(String iconTitle) {
        this.iconTitle = iconTitle;
    }

    public String getIconIntentNew() {
        return this.iconIntentNew;
    }

    public void setIconIntentNew(String iconIntentNew) {
        this.iconIntentNew = iconIntentNew;
    }

    public String getAppendIcon() {
        return this.appendIcon;
    }

    public void setAppendIcon(String replaceIcon) {
        this.appendIcon = replaceIcon;
    }

    public int getType() {
        byte type = 1;
        if(!StringUtil.isEmpty(this.getIconIntentNew())) {
            type = 4;
        } else if(!StringUtil.isEmpty(this.getAppendIcon())) {
            type = 8;
        } else if(StringUtil.isEmpty(this.getTitle()) && StringUtil.isEmpty(this.getContent())) {
            type = 2;
        }

        return type;
    }
}
