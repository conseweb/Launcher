package com.bitants.common.framework.view.bubble;

import android.content.Intent;
import android.view.View;

/**
 * Created by michael on 15-4-16.
 */
public class BubbleItemBuilder {
    private BubbleItem bubbleItem = new BubbleItem();

    public BubbleItemBuilder(View hostView) {
        this.bubbleItem.setHostView(hostView);
    }

    public BubbleItem build() {
        return this.bubbleItem;
    }

    public BubbleItemBuilder buildIntent(Intent intent) {
        this.bubbleItem.setIntent(intent);
        return this;
    }

    public BubbleItemBuilder buildDuration(long dur) {
        this.bubbleItem.setDuration(dur);
        return this;
    }

    public BubbleItemBuilder buildContent(String content) {
        this.bubbleItem.setContent(content);
        return this;
    }

    public BubbleItemBuilder buildSetPushed(boolean isPushed) {
        this.bubbleItem.setPushed(isPushed);
        return this;
    }

    public BubbleItemBuilder buildPushID(int pushID) {
        this.bubbleItem.setPushId(pushID);
        return this;
    }
}
