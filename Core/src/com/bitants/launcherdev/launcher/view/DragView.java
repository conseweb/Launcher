package com.bitants.launcherdev.launcher.view;

import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.launcher.config.BaseConfig;

public class DragView extends DragLayerView {
	static final String TAG = "DragView";
	
	// Number of pixels to add to the dragged item for scaling. Should be even
	// for pixel alignment.
	private static final int DRAG_SCALE = 0;

	protected Paint mPaint;
	private int mRegistrationX;
	private int mRegistrationY;

//	protected SymmetricalLinearTween mTween;
	public float mScale;
	protected float mAnimationScale = 1.0f;
	private float mPreviewScale = 1.0f;
	private float mSprignScale = 1.0f;
	
	private int bmWidth, bmHeight;
	
	/**
	 * 以下变量由HiLauncherEX定义
	 */
	public static final int MODE_NORMAL = 0;
	public static final int MODE_MIN = 1;
	public static final int MODE_PREVIEW = 2;
	public static final int MODE_SPRING = 3;
	public static final float SCALE_MIN = 0.75f;
	private int aniMode;
	private long beginTime;
	
	private final static long ANI_255 = 155;
	public final static float NO_SCALE = -1.0f;
	private float onEventScale = NO_SCALE;
	
	private boolean isModeMin = false;
	protected boolean isRemovedDragView = false; 
	
	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
										    Canvas.CLIP_SAVE_FLAG |
										    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
										    Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
										    Canvas.CLIP_TO_LAYER_SAVE_FLAG;
	/**
	 * 防止DragView移除后再次重绘，引起内存泄露问题
	 */
	//private boolean isRemoved = false;
	
	private Drawable dragViewBackground = null;
	
	public boolean isOnScaleAnimation = false;
	
	private boolean useBackgroundDrawable = false;

	/**
	 * Construct the drag view.
	 * <p>
	 * The registration point is the point inside our view that the touch events
	 * should be centered upon.
	 * 
	 * @param context
	 *            A context
	 * @param dragingView
	 *            The view that we're dragging around. We scale it up when we
	 *            draw it.
	 * @param registrationX
	 *            The x coordinate of the registration point.
	 * @param registrationY
	 *            The y coordinate of the registration point.
	 */
	public DragView(Context context, View view, int registrationX, int registrationY, 
			int width, int height) {
		super(context);
		
		mScale = (width + DRAG_SCALE) / width;

		dragingView = view;
		dragViewBackground = dragingView.getBackground();
		dragingView.setBackgroundDrawable(null);

		// The point in our scaled bitmap that the touch events are located
		mRegistrationX = registrationX + (DRAG_SCALE / 2);
		mRegistrationY = registrationY + (DRAG_SCALE / 2);

		bmWidth = width;
		bmHeight = height;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isRemovedDragView && dragingView != null) {			
			int alpha = mPaint.getAlpha();
			mPaint.setAlpha(155);
			canvas.saveLayer(null, mPaint, LAYER_FLAGS);
			if (onEventScale != NO_SCALE)  {
				drawingAni(canvas);//Dragview需要缩放时
			}
			onDrawMultiSelected(canvas);
			if(useBackgroundDrawable){
				dragingView.setBackgroundDrawable(dragViewBackground);
			}
			dragingView.draw(canvas);
			mPaint.setAlpha(alpha);
		}
	}

	/**
	 * 由于可能引起dragingView.draw死锁，故屏蔽
	 * <br>Author:ryan
	 * <br>Date:2012-8-26上午11:29:06
	 * @param canvas
	 */
	private void drawingAni(Canvas canvas) {
		long aniDiffTime = System.currentTimeMillis() - beginTime;
		if (aniDiffTime >= ANI_255 && aniMode == MODE_NORMAL) {
			canvas.scale(onEventScale, onEventScale);
			onEventScale = NO_SCALE;
			isOnScaleAnimation = false;
			return;
		}
		if(aniMode == MODE_SPRING){
			canvas.scale(mSprignScale, mSprignScale);
			isOnScaleAnimation = false;
			return;
		}
		if (aniMode == MODE_MIN) {
			aniDiffTime = Math.min(aniDiffTime, ANI_255);
			onEventScale = mAnimationScale - (mAnimationScale - SCALE_MIN ) * aniDiffTime / ANI_255;
		} else if (aniMode == MODE_PREVIEW){
			onEventScale = mPreviewScale;
		} else {
			onEventScale = SCALE_MIN + (mAnimationScale - SCALE_MIN ) * aniDiffTime / ANI_255;
		}
		
		canvas.scale(onEventScale, onEventScale);
		isOnScaleAnimation = true;
		invalidate();
	}
	
	public void setPreviewScale(float scale){
		mPreviewScale = scale;
	}

	public void setSprignScale(float scale){
		mSprignScale = scale;
		onEventScale = mSprignScale;
		aniMode = MODE_SPRING;
	}
	
	public void setPaint(Paint paint) {
		setPaint(paint, true);
	}
	
	public void setPaint(Paint paint, boolean isNeedInvalidate) {
		if (paint != null) {
			mPaint = paint;
		} else {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
		}
		if (isNeedInvalidate) {
			invalidate();
		}
	}
	
	public Paint getPaint() {
		return mPaint;
	}
	
	public float getEventScale(){
		return onEventScale;
	}

	
	public void onDrawMultiSelected(Canvas canvas){
		
	}
	
	public void show(int touchX, int touchY) {
		int width, height;
		width = dragingView.getWidth();
        height = dragingView.getHeight();
		int[] touch = {touchX, touchY};
		adjustTouchLocation(touch);
        super.show(touch[0], touch[1], width, height);
        
		if (BaseConfig.isOnScene()) {
			GpuControler.enableSoftwareLayers(this);
		}
	}

	/**
	 * Move the window containing this view.
	 * 
	 * @param touchX
	 *            the x coordinate the user touched in screen coordinates
	 * @param touchY
	 *            the y coordinate the user touched in screen coordinates
	 */
	public void move(int touchX, int touchY) {
		int[] touch = {touchX, touchY};
		adjustTouchLocation(touch);
		super.move(touch[0], touch[1]);
	}
	
	public int getAniMode(){
		return aniMode;
	}
	
	public void update(int state) {
		if (state == MODE_MIN) {
			isModeMin = true;
		} else {
			isModeMin = false;
		}
//		if (mWorkspace.isOnSpringMode())
//			return;
		beginTime = System.currentTimeMillis();
		aniMode = state;
		onEventScale = mAnimationScale;
		invalidate();
		
	}


	public void remove() {
		if(mDragLayer == null)
			return;
		
		dragingView.setBackgroundDrawable(dragViewBackground);
		
//		if (android.os.Build.VERSION.SDK_INT < 18) { //hjiang4.3合并文件夹时放手时会崩溃的所以要加这段保护一下
			mDragLayer.removeView(this);
//		} else {
//			handler.postDelayed(new Runnable() {	
//				
//				@Override
//				public void run() {
//					mDragLayer.removeView(DragView.this);
//				}
//			}, 600);	
//		}
		
		isRemovedDragView = true;
	}

	public int[] getDragCenterPoints() {
		return new int[]{getViewRect().centerX(), getViewRect().centerY()};
	}
	
	/**
	 * Description: 校正Dragview位置
	 * Author: guojy
	 * Date: 2012-8-31 下午03:57:52
	 */
	protected void adjustTouchLocation(int[] touch){
		if(aniMode == MODE_PREVIEW){//预览时
			touch[0] -= (int)(bmWidth*mPreviewScale)/2;
			touch[1] -= (int)(bmHeight*mPreviewScale)/2;
		}else{
			touch[0] -= mRegistrationX;
			touch[1] -= mRegistrationY;
		}
	}
	
	/**
	 * 判断是否有缩小，若是缩小，则不画投影
	 * @return
	 */
	public boolean isModeMin(){
		return isModeMin;
	}
	
	public void setUseBackgroundDrawable(boolean useBackgroundDrawable) {
		this.useBackgroundDrawable = useBackgroundDrawable;
	}
}
