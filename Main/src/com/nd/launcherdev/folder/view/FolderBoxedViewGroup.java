package com.nd.launcherdev.folder.view;


import com.nd.launcherdev.launcher.config.BaseConfig;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class FolderBoxedViewGroup extends RelativeLayout {
	
	private final int CASCADE_SAVEFLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
	
	private boolean isInDrag = false;
	/**
	 * 是否被点击*/
	boolean isOnTouchScaleState=false;
	/**
	 * 点击地缩小的系数
	 * */
	float onTouchScale=0.9f;
	public FolderBoxedViewGroup(Context context) {
		super(context);
	}
	
	public FolderBoxedViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FolderBoxedViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (isInDrag) {
			canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), BaseConfig.ALPHA_155, CASCADE_SAVEFLAGS);
		}
		if(isOnTouchScaleState){
			isOnTouchScaleState = false;
			canvas.scale(onTouchScale, onTouchScale,this.getWidth()/2, this.getHeight()/2);
		}
		super.dispatchDraw(canvas);
		if (isInDrag) {
			canvas.restore();		
		}
	}
	
	public void setInDrag(boolean isInDrag) {
		this.isInDrag = isInDrag;
	}

	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isOnTouchScaleState = true;
			this.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			isOnTouchScaleState = false;
			this.invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			isOnTouchScaleState = false;
			this.invalidate();
			break;
		default:
			break;

		}
		return super.onTouchEvent(event);
	}
	
}
