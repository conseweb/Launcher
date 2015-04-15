package com.nd.launcherdev.launcher.view;

import com.nd.android.pandahome2.R;
import com.nd.launcherdev.launcher.BaseLauncher;
import com.nd.launcherdev.launcher.screens.DeleteZone;
import com.nd.launcherdev.launcher.touch.BaseDragController;
import com.nd.launcherdev.launcher.touch.DragSource;
import com.nd.launcherdev.launcher.touch.DropTarget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import com.nd.launcherdev.launcher.BaseLauncher;
import com.nd.launcherdev.launcher.screens.DeleteZone;
import com.nd.launcherdev.launcher.touch.BaseDragController;
import com.nd.launcherdev.launcher.touch.DragSource;
import com.nd.launcherdev.launcher.touch.DropTarget;

public class BaseDeleteZoneTextView extends View implements DropTarget {

//	private static final int TRANSITION_DURATION = 250;
	public BaseDragController mDragController;
	public BaseLauncher mLauncher;
	private int mType = DeleteZone.DELETE_ZONE;

	private String delText, unText;
	public int srcColor, startX, startDY, startTY, drawpadding, paddingBottom;
	public TransitionDrawable mTransition;
	public final Paint mTrashPaint = new Paint();
	public final Paint textPaint = new Paint();
	
	public boolean removeWithoutAlert = false;
	//mTransition中drawable的索引
	private int index = 0;
	
	public BaseDeleteZoneTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BaseDeleteZoneTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources res = context.getResources();
		srcColor = res.getColor(R.color.delete_color_filter);
		mTrashPaint.setColorFilter(new PorterDuffColorFilter(srcColor, PorterDuff.Mode.SRC_ATOP));
		drawpadding = context.getResources().getDimensionPixelSize(R.dimen.text_drawpadding);
		paddingBottom = context.getResources().getDimensionPixelSize(R.dimen.delete_zone_padding);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeleteZone, defStyle, 0);
		a.recycle();
		textPaint.setTextSize(res.getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize));
		textPaint.setColor(Color.WHITE);
		textPaint.setShadowLayer(1, 1, 1, Color.BLACK);
		textPaint.setAntiAlias(true);
		delText = context.getString(R.string.common_button_delete);
		unText = context.getString(R.string.common_button_uninstall);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int hight = MeasureSpec.getSize(heightMeasureSpec);

		if (mTransition == null)
			return;
		
		startX = (int) ((width - drawpadding - mTransition.getIntrinsicHeight() - textPaint.measureText(delText)) / 2);
		startDY = (hight - mTransition.getIntrinsicHeight()) / 2 - paddingBottom;
		startTY = (hight - textPaint.getFontMetricsInt(null)) / 2 + textPaint.getFontMetricsInt(null) - paddingBottom;
	}
	
	public void setLauncher(BaseLauncher launcher) {
		mLauncher = launcher;
	}

	public void setDragController(BaseDragController dragController) {
		mDragController = dragController;
	}

	public int getState() {
		return isShown() ? AVAIABLE : UNAVAIABLE;
	}
	
	public void setmType(int mType) {
		this.mType = mType;
	}

	public void setTransitionDrawable(TransitionDrawable transition) {
		this.mTransition = transition;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mTransition.getDrawable(index).setBounds(startX, startDY, startX + mTransition.getIntrinsicWidth(), startDY + mTransition.getIntrinsicHeight());
		mTransition.getDrawable(index).draw(canvas);
		if (mType == DeleteZone.DELETE_ZONE) {
			canvas.drawText(delText, startX + mTransition.getIntrinsicWidth() + drawpadding, startTY, textPaint);
		} else if (mType == DeleteZone.UNINSTALL_ZONE) {
			canvas.drawText(unText, startX + mTransition.getIntrinsicWidth() + drawpadding, startTY, textPaint);
		} 
	}

	/**
	 * @return the mType
	 */
	public int getDeleteZoneType() {
		return mType;
	}
	
	/**
	 * Description: 甩动图标到屏幕顶部区域进行删除
	 * Author: guojy
	 * Date: 2013-2-16 下午2:20:33
	 */
	public void onFlingToDelete(final DragSource source, final int x, final int y, final int xOffset, final int yOffset,
			final DragView dragView, final Object dragInfo, int endX, int endY, final boolean isFlingToDeleteZone) {
		removeWithoutAlert = false;
		
 		TranslateAnimation trans = new TranslateAnimation(0, endX, 0, endY);
		trans.setDuration(250);
		trans.setFillAfter(true);
		trans.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				//隐藏长按快捷菜单
				mDragController.dismissShortcutMenu();
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				post(new Runnable(){
					@Override
					public void run() {
						mLauncher.getScreenViewGroup().cleanReorderAllState();
						
						onDrop(source, x, y, xOffset, yOffset, null, dragInfo);
						source.onDropCompleted(BaseDeleteZoneTextView.this, true);
						boolean animateDragView = isFlingToDeleteZone ? !removeWithoutAlert : true;
						mDragController.endDrag(animateDragView);
					}
				});
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}  
			
		});
		dragView.startAnimation(trans);
    }

	public void setDragEnterState(){
		setIndex(1);
	}
	
	public void setNotDragEnterState(){
		setIndex(0);
	}
	
	private void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isDeleteZone(){
		return mType == DeleteZone.DELETE_ZONE;
	}
	
	
	
	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		// 隐藏弹出图标
		mDragController.dismissShortcutMenu();
		return true;
	}


	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		mLauncher.mWorkspace.cleanReorderAllState();
		mLauncher.mWorkspace.getCurrentCellLayout().cleanDragOutline();
		
		setDragEnterState();
		dragView.setPaint(mTrashPaint);
		textPaint.setColor(srcColor);
		invalidate();
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		setNotDragEnterState();
		dragView.setPaint(null);
		textPaint.setColor(Color.WHITE);
		invalidate();
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		removeWithoutAlert = true;
	}
}
