package com.nd.launcherdev.framework.view.draggersliding;

import android.content.Context;
import android.graphics.Canvas;

import com.nd.launcherdev.kitset.GpuControler;
import com.nd.launcherdev.kitset.GpuControler;

/**
 *
 * @author Anson
 */
public class DrawerLayout extends DraggerLayout {
	
	public DrawerLayout(Context context, DraggerSlidingView workspace) {
		super(context, workspace);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

	}
	
	public void enableHardwareLayers() {
		GpuControler.enableHardwareLayers(this);
	}
	
    public void destroyHardwareLayer() {
    	GpuControler.destroyHardwareLayer(this);
    }
}
