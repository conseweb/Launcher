package com.bitants.launcherdev.launcher.view;

import com.bitants.launcherdev.launcher.screens.DragLayer;
import com.bitants.launcherdev.launcher.screens.DragLayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import com.bitants.launcherdev.launcher.screens.DragLayer;

public class DragLayerView extends View {

	protected DragLayer mDragLayer = null;
	
	protected Handler handler = new Handler();
	
	public View dragingView = null;
	
	public DragLayerView(Context context) {
		super(context);
	}

	public void setDragLayer(DragLayer mDragLayer) {
		this.mDragLayer = mDragLayer;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (dragingView != null) {
			setMeasuredDimension(dragingView.getWidth(), dragingView.getHeight());
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	public void show(int x, int y, int width, int height) {
		if(mDragLayer == null)
			return;
		
		mDragLayer.addView(this);
		DragLayer.LayoutParams lp = new DragLayer.LayoutParams(width, height, x, y);
		setLayoutParams(lp);
	}
	
	public void move(int x, int y) {
		DragLayer.LayoutParams lp = (DragLayer.LayoutParams)getLayoutParams();
		if(lp == null)
			return;
		lp.x = x;
		lp.y = y;
		requestLayout();
	}
	
	public void remove(){
		if(mDragLayer == null)
			return;
		mDragLayer.removeView(this);
	}
	
	public int[] getViewXY(){
		DragLayer.LayoutParams lp = (DragLayer.LayoutParams)getLayoutParams();
		if(lp == null)
			return new int[]{0, 0};
		
		return new int[]{lp.x, lp.y};
	}
	
	public Rect getViewRect(){
		DragLayer.LayoutParams lp = (DragLayer.LayoutParams)getLayoutParams();
		if(lp == null)
			return new Rect();
		return new Rect(lp.x, lp.y, lp.x + lp.width, lp.y + lp.height);
	}
	
	public View getDragingView() {
		return dragingView;
	}
	
	public void setDragingView(View dragingView) {
		this.dragingView = dragingView;
	}
	
	protected void onDraw(Canvas canvas) {
		if(dragingView != null){
			dragingView.draw(canvas);
		}
	}
}
