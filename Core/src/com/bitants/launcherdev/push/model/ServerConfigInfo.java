package com.bitants.launcherdev.push.model;

import com.bitants.launcherdev.push.model.CompaigPushInfo;
import com.bitants.launcherdev.push.model.NotifyIconPushInfo;
import com.bitants.launcherdev.push.model.NotifyPushInfo;
import com.bitants.launcherdev.push.model.PopupPushInfo;
import java.util.List;

/**
 * Created by michael on 2015-04-16.
 */
public class ServerConfigInfo {
    private List<CompaigPushInfo> compaigPushInfo;
    private List<NotifyPushInfo> notifyPushInfo;
    private List<PopupPushInfo> popupPushInfo;
    private List<NotifyIconPushInfo> notifyIconPushInfo;
    private int fetchInterval = 0;
    private int isShowHotIcon = 0;

    public ServerConfigInfo() {
    }

    public int isShowHotIcon() {
        return this.isShowHotIcon;
    }

    public void setShowHotIcon(int isShowHotIcon) {
        this.isShowHotIcon = isShowHotIcon;
    }

    public int getFetchInterval() {
        return this.fetchInterval;
    }

    public void setFetchInterval(int fetchInterval) {
        this.fetchInterval = fetchInterval;
    }

    public List<CompaigPushInfo> getCompaigPushInfo() {
        return this.compaigPushInfo;
    }

    public void setCompaigPushInfo(List<CompaigPushInfo> compaigPushInfo) {
        this.compaigPushInfo = compaigPushInfo;
    }

    public List<NotifyPushInfo> getNotifyPushInfo() {
        return this.notifyPushInfo;
    }

    public void setNotifyPushInfo(List<NotifyPushInfo> notifyPushInfo) {
        this.notifyPushInfo = notifyPushInfo;
    }

    public List<PopupPushInfo> getPopupPushInfo() {
        return this.popupPushInfo;
    }

    public void setPopupPushInfo(List<PopupPushInfo> popupPushInfo) {
        this.popupPushInfo = popupPushInfo;
    }

    public List<NotifyIconPushInfo> getNotifyIconPushInfo() {
        return this.notifyIconPushInfo;
    }

    public void setNotifyIconPushInfo(List<NotifyIconPushInfo> notifyIconPushInfo) {
        this.notifyIconPushInfo = notifyIconPushInfo;
    }
}
