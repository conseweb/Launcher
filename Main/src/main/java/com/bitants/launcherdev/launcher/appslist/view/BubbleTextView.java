package com.bitants.launcherdev.launcher.appslist.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import com.bitants.launcher.R;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.appslist.AppslistActivity;

public class BubbleTextView extends View {
	private Paint paint;
	private String lableName = "";
	private Drawable drawable;
	private int mwidth;
	private boolean hasMeasured;

	public BubbleTextView(Context context) {
		super(context);
		init();
		drawable = context.getResources().getDrawable(R.drawable.ic_launcher);
		drawable.setCallback(null);
	}

	public BubbleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		drawable = context.getResources().getDrawable(R.drawable.ic_launcher);
		drawable.setCallback(null);

	}

	private void init() {
		hasMeasured = false;
		this.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					mwidth = BubbleTextView.this.getMeasuredWidth()-AppslistActivity.Apppadding;
					updateTitle();
					hasMeasured = true;
				}
				return true;

			}
		});
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(AppslistActivity.AppstextSize);
	}
	
	private void updateTitle(){
		if (mwidth!=0){
			if (!StringUtil.isEmpty(lableName)){
				int textWidth = (int) paint.measureText(lableName);
				if (mwidth != 0 && textWidth > mwidth) {
					int mid = 0;
					for (int i = 0; i < lableName.length(); i++) {
						int len = (int) paint.measureText(lableName, 0, i);
						if (len > mwidth) {
							mid = i;
							break;
						}
					}
					if (mid != 0) {
						lableName = lableName.substring(0, mid - 1);
					}
				}
			}
		}
	}

	public void setTextViewTitle(String titleName) {
		lableName = titleName;
		updateTitle();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawable.setBounds(AppslistActivity.Apppadding, AppslistActivity.Apppadding, getWidth() - 2 * AppslistActivity.Apppadding, getWidth() - 2 * AppslistActivity.Apppadding);
		drawable.draw(canvas);
		canvas.drawText(lableName, getWidth() / 2, (int) (getHeight() * 2.4 / 3), paint);
		super.onDraw(canvas);
	}

	public void setBubbleTextIcon(Drawable icon) {
		if (icon != null)
			drawable = icon;
		invalidate();
	}
}
