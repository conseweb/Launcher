package com.bitants.common.push.model;

/**
 * Created by michael on 2015-04-16.
 */
public class CompaigPushInfo {
    private String id;
    private String title;
    private String Content;
    private String url;
    private String forpeople;
    private String iconUrl;

    public CompaigPushInfo() {
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForpeople() {
        return this.forpeople;
    }

    public void setForpeople(String forpeople) {
        this.forpeople = forpeople;
    }
}
