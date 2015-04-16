package com.bitants.launcherdev.framework.view.bubble;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bitants.launcherdev.framework.view.bubble.BubbleItem;
import com.bitants.launcherdev.framework.view.bubble.LauncherBubbleManager;
import java.lang.ref.WeakReference;

/**
 * Created by michael on 2015-04-16.
 */
public class LauncherBubbleView extends TextView {
    private String Content;
    private WeakReference<View> hostViewReference;
    private BubbleItem mBubbleItem;
    private ImageView angleView;

    public LauncherBubbleView(Context context) {
        super(context);
        this.init();
    }

    private void init() {
        this.setTextColor(-16777216);
    }

    public void setContent(String content) {
        this.Content = content;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public WeakReference<View> getHostViewReference() {
        return this.hostViewReference;
    }

    public void setHostViewReference(WeakReference<View> hostViewReference) {
        this.hostViewReference = hostViewReference;
    }

    public String getContent() {
        return this.Content;
    }

    public BubbleItem getBubbleItem() {
        return this.mBubbleItem;
    }

    public void setBubbleItem(BubbleItem notify) {
        this.mBubbleItem = notify;
        long dur = notify.getDuration();
        if(dur > 0L) {
            this.postDelayed(new Runnable() {
                public void run() {
                    LauncherBubbleManager.getInstance().dismissBubble(LauncherBubbleView.this.mBubbleItem.getHostView());
                }
            }, dur);
        }

    }

    public ImageView getAngleView() {
        return this.angleView;
    }

    public void setAngleView(ImageView angleView) {
        this.angleView = angleView;
    }

    public void dismiss() {
        if(this.angleView != null && this.angleView.getParent() != null) {
            ((ViewGroup)this.angleView.getParent()).removeView(this.angleView);
        }

        if(this.getParent() != null) {
            ((ViewGroup)this.getParent()).removeView(this);
        }

    }

    public void addToParent(ViewGroup parent) {
        if(parent != null) {
            parent.addView(this);
            parent.addView(this.angleView);
        }
    }
}
