package com.nd.launcherdev.launcher.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.nd.android.pandahome2.R;
import com.nd.launcherdev.launcher.screens.DragLayer;

/**
 * Description: 控制DragLayer层上的素材绘制
 * Author: guojy
 * Date: 2013-10-14 上午10:42:14
 */
public class DragLayerStuffDrawer{
	private DragLayer mDragLayer;
	
	//用于绘制跨屏移动的提示条
	Rect mDescRect = new Rect();
	Paint paint = new Paint();
	int mBarState = 0;
	Bitmap mLetfBarBitmap;
	Bitmap mRightBarBitmap;
	WallpaperHelper mWallpaperHelper;
	public void setDragLayer(DragLayer mDragLayer) {
		this.mDragLayer = mDragLayer;
	}
	
	public void drawOnBackground(Canvas arg0) {
		mWallpaperHelper.drawWallPaper(arg0);
	}
	
	public void draw(Canvas canvas){
		drawBar(canvas);
	}
	
	public void hideMoveBar(){
		setBarState(0);
	}
	
	public void drawMoveToLeftBar(){
		setBarState(1);
	}
	
	public void drawMoveToRightBar(){
		setBarState(2);
	}
	
	/**
	 *0为不显示侧边栏，1为显示左边，除些以外都显示右边栏
	 **/
	private void setBarState(int state)
	{
		int oldState=mBarState;
		mBarState=state;
		if(state==0)
		{
			mLetfBarBitmap=null;
			mRightBarBitmap=null;
		}
		if(oldState!=mBarState)
		{
			mDragLayer.invalidate();
		}
	}
	

	private void drawBar(Canvas canvas) {
		if (mBarState != 0) {
			if (mBarState == 1) {
				if (mLetfBarBitmap == null) {

					mLetfBarBitmap = BitmapFactory.decodeResource(mDragLayer.getResources(), R.drawable.move_to_left_screen_bar_bg);
				}
				Matrix matrix = canvas.getMatrix();
				if (mLetfBarBitmap != null) {
					mDescRect.left = 0;
					mDescRect.top = 0;
					mDescRect.bottom = mDragLayer.getHeight();
					mDescRect.right = mLetfBarBitmap.getWidth();
					canvas.save();
					matrix.setTranslate(0, 0);
					canvas.concat(matrix);
					canvas.drawBitmap(mLetfBarBitmap, null, mDescRect, paint);
					canvas.restore();
				}
				//Log.e("zhou","画左边");
			} else {
				if (mRightBarBitmap == null) {

					mRightBarBitmap = BitmapFactory.decodeResource(mDragLayer.getResources(), R.drawable.move_to_right_screen_bar_bg);
				}
				Matrix matrix = canvas.getMatrix();
				if (mRightBarBitmap != null) {
					mDescRect.left = 0;
					mDescRect.top = 0;
					mDescRect.bottom = mDragLayer.getHeight();
					mDescRect.right = mRightBarBitmap.getWidth();
					canvas.save();
					matrix.setTranslate(mDragLayer.getWidth() - mRightBarBitmap.getWidth(), 0);
					canvas.concat(matrix);
					canvas.drawBitmap(mRightBarBitmap, null, mDescRect, paint);
					//Log.e("zhou","画右边");
					canvas.restore();
				}
			}

		}else {
			//Log.e("zhou","不画隐藏");
		}
	}
	public void setWallPaperHelper(WallpaperHelper helper)
	{
		mWallpaperHelper=helper;
	}
}
