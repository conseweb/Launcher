package com.nd.hilauncherdev.core.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * <p>类说明: Custom TextView Touch Event Listener </p>
 * <p>创建时间：2012-8-29 下午06:05:35</p>
 * @author yuf
 * @version 1.0
 */
public class TextViewTouchListener implements OnTouchListener {

	private int normal = 0xFF000000;
	private int clicked = 0xFFFFFFFF;
	
	public TextViewTouchListener(int normal, int clicked) {
		this.normal = normal;
		this.clicked = clicked;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
	    switch(motionEvent.getAction()){            
	            case MotionEvent.ACTION_DOWN:
	            ((TextView)view).setTextColor(clicked);
	                break;          
	            case MotionEvent.ACTION_CANCEL:             
	            case MotionEvent.ACTION_UP:
	            ((TextView)view).setTextColor(normal); 
	                break;
	    } 
        return false;   
    } 

}
