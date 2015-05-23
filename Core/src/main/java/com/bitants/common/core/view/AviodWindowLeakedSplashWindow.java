/**
 *
 */
package com.bitants.common.core.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

/**
 * 避免出现 WindowLeaked异常
 */
public class AviodWindowLeakedSplashWindow extends ProgressDialog {
    /**
     * listener
     */
    private View.OnClickListener preLis;
    private View.OnClickListener postLis;

    /**
     * actionthread
     */
    private ActionThread actionthread = null;

    private long id;

    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public AviodWindowLeakedSplashWindow(Context ctx, String msg, View.OnClickListener preLis, View.OnClickListener postLis, boolean isCacelable) {
		super(ctx);
        this.setCancelable(isCacelable);
        this.preLis = preLis;
        this.postLis = postLis;
        this.setMessage(msg);
        initSplashWindow();
	}

	public AviodWindowLeakedSplashWindow(Context ctx, String title, String msg, View.OnClickListener preLis, View.OnClickListener postLis, boolean isCacelable) {
		super(ctx);
        this.setCancelable(isCacelable);
        this.preLis = preLis;
        this.postLis = postLis;
        this.setMessage(msg);
        initSplashWindow();
	}
    
    public AviodWindowLeakedSplashWindow(Context ctx, String title, String msg, View.OnClickListener preLis, View.OnClickListener postLis) {
        super(ctx);
        this.setCancelable(false);
        this.preLis = preLis;
        this.postLis = postLis;
        if (title != null) {
            setTitle(title);
        }
        this.setMessage(msg);
        initSplashWindow();
    }
    
    /**
     * show出等待框并启动事件线程
     */
    private void initSplashWindow() {
        this.show();// 出现等待框
        actionthread = new ActionThread(preLis, postLis);
        actionthread.start();//启动事件线程
    }

    /**
     * 
     */
    private void closeWindow() {
        this.dismiss();
    }

    /**
     * 
     *执行事件的线程
     */
    private class ActionThread extends Thread {
        private View.OnClickListener preAction = null;
        private View.OnClickListener postAction = null;

        public ActionThread(View.OnClickListener preAction, View.OnClickListener postAction) {
        	this.preAction = preAction;
        	this.postAction = postAction;
        }

        @Override
		public void run() {
            try {
            	if (preAction != null)
            		preAction.onClick(null);
            	
                closeWindow();
                
                if (postAction != null)
                	postAction.onClick(null);
            } catch (Throwable ex) {
                ex.printStackTrace();
                closeWindow();
            } 
        }
    }
}
