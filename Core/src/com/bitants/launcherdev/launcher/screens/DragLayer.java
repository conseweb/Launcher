/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bitants.launcherdev.launcher.screens;


import com.bitants.launcherdev.launcher.support.DragLayerStuffDrawer;
import com.bitants.launcherdev.launcher.support.DragLayerStuffDrawer;
import com.bitants.launcherdev.launcher.touch.DragLayerEventHandler;
import com.bitants.launcherdev.launcher.view.DragLayerView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.bitants.launcherdev.launcher.support.DragLayerStuffDrawer;

/**
 * A ViewGroup that coordinated dragging across its dscendants
 */
public class DragLayer extends FrameLayout {
	DragLayerEventHandler mDragController;
	DragLayerStuffDrawer mDragLayerStuffDrawer;
	private OnTouchListener onMagicTouchListener;

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragController(DragLayerEventHandler controller) {
        mDragController = controller;
    }
    
    public void  setDragLayerStuff(DragLayerStuffDrawer dragLayerStuff) {
		mDragLayerStuffDrawer = dragLayerStuff;
	}
    
    public DragLayerStuffDrawer getDragLayerStuff() {
		return mDragLayerStuffDrawer;
	}
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragController.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDragController.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mDragController.dispatchUnhandledMove(focused, direction);
    }

	@Override
	protected void dispatchDraw(Canvas canvas) {
		//Log.e("DragLayer", "DragLayer.dispatchDraw");
		mDragLayerStuffDrawer.drawOnBackground(canvas);
		super.dispatchDraw(canvas);
		mDragLayerStuffDrawer.draw(canvas);
	}
	
	public static class LayoutParams extends FrameLayout.LayoutParams {
        public int x, y;
        public LayoutParams(int width, int height) {
            super(width, height);
        }
        
        public LayoutParams(int width, int height, int x, int y) {
            super(width, height);
            this.x = x;
            this.y = y;
        }
    }
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		//添加DragView
		if(mDragController.isLayoutDragView()){
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				if (child instanceof DragLayerView) {
					final LayoutParams lp = (LayoutParams) child.getLayoutParams();
					child.layout(lp.x, lp.y, lp.x + lp.width, lp.y + lp.height);
				}	
			}
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent) {
		if (this.onMagicTouchListener != null) {
			this.onMagicTouchListener.onTouch(this, motionEvent);
		}
		return super.dispatchTouchEvent(motionEvent);
	}
	
	public void setMagicTouchListener(OnTouchListener onTouchListener) {
		this.onMagicTouchListener = onTouchListener;
	}
}
