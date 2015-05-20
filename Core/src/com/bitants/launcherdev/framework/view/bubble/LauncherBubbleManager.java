package com.bitants.launcherdev.framework.view.bubble;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import com.bitants.launcherdev.framework.view.bubble.BubbleItem;
import com.bitants.launcherdev.framework.view.bubble.BubbleItemBuilder;
import com.bitants.launcherdev.framework.view.bubble.LauncherBubbleView;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings.Favorites;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.CellLayout.LayoutParams;
import com.bitants.launcherdev.push.PushManager;
import com.bitants.launcherdev.push.PushMsgHandler;
import com.bitants.launcherdev.push.model.PopupPushInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by michael on 2015-04-16.
 */
public class LauncherBubbleManager {
    private static LauncherBubbleManager instance = new LauncherBubbleManager();
    private static HashMap<View, BubbleItem> mBubbles = null;
    private static Object lock = new Object();

    private LauncherBubbleManager() {
    }

    public static LauncherBubbleManager getInstance() {
        return instance;
    }

    public boolean addBubble(View v, BubbleItem notify) {
        Object var3 = lock;
        synchronized(lock) {
            if(mBubbles == null) {
                mBubbles = new HashMap();
            }

            if(mBubbles.containsKey(v)) {
                return false;
            } else {
                mBubbles.put(v, notify);
                return true;
            }
        }
    }

    public void dismissBubbleAndRecord(View v) {
        int pushId = this.dismissBubble(v);
        if(pushId > 0) {
//            PushMsgHandler.setPushMsgRead((long)pushId);
//            PushManager.getInstance().getPushSDKAdapter().statClickPushNotification("" + pushId);
        }

    }

    public int dismissBubble(View v) {
        if(mBubbles == null) {
            return 0;
        } else {
            int pushId = 0;
            Object var3 = lock;
            synchronized(lock) {
                View host = v;
                BubbleItem item = null;
                if(v instanceof LauncherBubbleView) {
                    item = ((LauncherBubbleView)v).getBubbleItem();
                    if(item != null) {
                        host = item.getHostView();
                        if(item.getIntent() != null) {
                            Intent intent = item.getIntent();
                            intent.addFlags(268435456);
                            v.getContext().startActivity(intent);
                        }
                    }
                }

                if(mBubbles.size() > 0 && mBubbles.containsKey(host)) {
                    if(item == null) {
                        item = (BubbleItem)mBubbles.get(host);
                    }

                    this.removeBubbleView(((BubbleItem)mBubbles.get(host)).getLauncherNotificationView());
                    mBubbles.remove(host);
                } else if(v instanceof LauncherBubbleView) {
                    this.removeBubbleView((LauncherBubbleView)v);
                }

                if(item != null && item.isPushed()) {
                    pushId = item.getPushId();
                }

                return pushId;
            }
        }
    }

    public void dimissAllBubbles() {
        if(mBubbles != null) {
            ArrayList list = new ArrayList();
            Iterator var3 = mBubbles.keySet().iterator();

            View v;
            while(var3.hasNext()) {
                v = (View)var3.next();
                list.add(v);
            }

            var3 = list.iterator();

            while(var3.hasNext()) {
                v = (View)var3.next();
                this.dismissBubble(v);
            }

        }
    }

    private boolean addBubble(View v, PopupPushInfo info) {
        try {
            BubbleItemBuilder e = new BubbleItemBuilder(v);
            if(!StringUtil.isEmpty(info.getDuration()) && Integer.valueOf(info.getDuration()).intValue() > 0) {
                e.buildDuration((long)Integer.valueOf(info.getDuration()).intValue());
            }

            if(!StringUtil.isEmpty(info.getIntent())) {
                e.buildIntent(Intent.parseUri(info.getIntent(), 0));
            }

            return this.addBubble(v, e.buildContent(info.getContent()).buildSetPushed(true).buildPushID(info.getId()).build());
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public void addBubbles(List<PopupPushInfo> list) {
        if(list != null) {
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                PopupPushInfo info = (PopupPushInfo)var3.next();
                View v = this.getFirstTargetViewOnWorkspace(info.getTarget());
                if(v != null) {
                    this.addBubble(v, info);
                }
            }

        }
    }

    public void showBubblesAgain() {
        List list = PushMsgHandler.getNotClickedPopupBubbles();
        boolean show = false;
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            PopupPushInfo info = (PopupPushInfo)var4.next();
            View v = this.getFirstTargetViewOnWorkspace(info.getTarget());
            if(v != null && this.addBubble(v, info)) {
                show = true;
            }
        }

        if(show) {
            this.showAllBubbles();
        }

    }

    public void showAllBubbles() {
        if(mBubbles == null) {
            this.addBubbles(PushMsgHandler.getNotClickedPopupBubbles());
        }

        if(mBubbles != null && mBubbles.size() != 0) {
            Iterator var2 = mBubbles.keySet().iterator();

            while(var2.hasNext()) {
                View v = (View)var2.next();
                this.showBubble((BubbleItem)mBubbles.get(v), v);
            }

        }
    }

    private void showBubble(BubbleItem mBubbleItem, View hostView) {
        if(hostView.getParent() != null && hostView.getLayoutParams() instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams)hostView.getLayoutParams();
            CellLayout cl = (CellLayout)hostView.getParent();
            this.showBubble(mBubbleItem, hostView, cl.mCellLayoutLocation, lp.cellX, lp.cellY, lp.spanX, lp.spanY);
        }
    }

    private void showBubble(BubbleItem mBubbleItem, View hostView, int screen, int cellX, int cellY, int spanX, int spanY) {
        if(mBubbleItem != null && !mBubbleItem.hasShowed) {
            mBubbleItem.hasShowed = true;
            LauncherBubbleView bubbleView = PushManager.getInstance().getPushSDKAdapter().createLauncherBubbleView(mBubbleItem.getContent(), screen, hostView);
            if(bubbleView != null) {
                bubbleView.setBubbleItem(mBubbleItem);
                this.addBubbleViewToWorkspace(bubbleView, screen);
                mBubbleItem.setLauncherNotificationView(bubbleView);
            }
        }
    }

    private void removeBubbleView(LauncherBubbleView bubbleView) {
        if(bubbleView != null) {
            bubbleView.dismiss();
        }
    }

    private void addBubbleViewToWorkspace(LauncherBubbleView bubbleView, int screen) {
        if(BaseConfig.getBaseLauncher() != null && bubbleView != null) {
            bubbleView.addToParent(BaseConfig.getBaseLauncher().mWorkspace.getCellLayoutAt(screen));
        }
    }

    private View getFirstTargetViewOnWorkspace(String intentKey) {
        Cursor c = null;

        try {
            ContentResolver e = BaseConfig.getApplicationContext().getContentResolver();
            c = e.query(Favorites.getContentUri(), (String[])null, "container=-100 AND intent like\'%" + intentKey + "%\'", (String[])null, (String)null);
            int screenIndex = c.getColumnIndexOrThrow("screen");
            int cellXIndex = c.getColumnIndexOrThrow("cellX");
            int cellYIndex = c.getColumnIndexOrThrow("cellY");
            if(c.moveToNext()) {
                CellLayout cl = BaseConfig.getBaseLauncher().getScreenViewGroup().getCellLayoutAt(c.getInt(screenIndex));
                View var9 = cl.getChildAt(c.getInt(cellXIndex), c.getInt(cellYIndex));
                return var9;
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            if(c != null) {
                c.close();
            }

        }

        return null;
    }
}
