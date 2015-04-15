package com.bitants.launcherdev.core.view;

import com.bitants.launcherdev.launcher.screens.CellLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Author:ryan <br>
 * Date:2011-6-16上午10:57:17
 */
public class PreviewImageView extends ImageView {
	private CellLayout cellLayout;
	public static final float topPaddingRate = 0.25f; //用于控制toppadding大小

	public PreviewImageView(Context context) {
		super(context);
	}

	public PreviewImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PreviewImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (cellLayout != null) {
			float heightRate = cellLayout.getRateByWorkspaceHeight();
			float previewCellLayoutHeight = (float)getMeasuredHeight() * heightRate;
			float canvasTranslateY = (getMeasuredHeight() - previewCellLayoutHeight) * topPaddingRate;//设置预览的celllayout在PreviewImageView上的toppadding高度，可调整
			
			canvas.scale((float)getMeasuredWidth() / cellLayout.getMeasuredWidth(), previewCellLayoutHeight / cellLayout.getMeasuredHeight(), 0, canvasTranslateY);
			cellLayout.dispatchDraw(canvas);
		}
	}

	public void setWillBeDrawedGroup(CellLayout mWillBeDrawedGroup) {
		cellLayout = mWillBeDrawedGroup;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

}
