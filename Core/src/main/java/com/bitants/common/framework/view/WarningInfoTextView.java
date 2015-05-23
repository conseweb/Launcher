package com.bitants.common.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 提示文字View
 */
public class WarningInfoTextView extends TextView {
	private String text;
	private int height;
	private int width;

	private String loadText = "";
	private int i = 0;
	private boolean isLoading = false;
	private boolean isRunning = false;
	private long lastTime = 0;

	public WarningInfoTextView(Context context) {
		super(context);
		init(context);
	}

	public WarningInfoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (!isLoading)
			return;
		if (text == null)
			return;
		if (height <= 0 || width <= 0) {
			width = (int) (getFontWidth(getPaint(), text) + getFontWidth(getPaint(), "...") * 2);
			setWidth(width);
			height = getHeight();
			return;
		}
		comLoadText();
		canvas.save();
		float fontWidth = getFontWidth(getPaint(), text);
		float fontHeight = getFontHeight(getPaint());
		float y = ((float) height + fontHeight) / 2;
		float x = ((float) width - fontWidth) / 2;
//		canvas.drawText(text, x, y, getPaint());
		float dot_x = (float) ((width - fontWidth) / 2 + fontWidth + 2);
		canvas.drawText(loadText, dot_x, y, getPaint());
		canvas.restore();
		if(!isRunning){
			isRunning = true;
			postDelayed(runnable, 500);
		}
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			long dis = System.currentTimeMillis() - lastTime;
			if(dis >= 500){
				lastTime = System.currentTimeMillis();
				invalidate();
			}else{
				postInvalidateDelayed(dis);
			}
			isRunning = false;
		}
	};

	private void comLoadText() {
		if (i >= 4) {
			i = 0;
		}
		loadTextText();
		i++;
	}

	private void loadTextText() {
		if (i == 0)
			loadText = "";
		else
			loadText += ".";
	}

	private float getFontHeight(Paint paint) {
		Paint.FontMetrics localFontMetrics = paint.getFontMetrics();
		return (float) Math.ceil(localFontMetrics.descent - localFontMetrics.ascent);
	}

	private float getFontWidth(Paint paint, String text) {
		return paint.measureText(text);
	}

	/**
	 * 设置提示文字，开始加载
	 * @param text 提示文字
	 */
	public void startProcess(String text) {
		this.text = text;
		isLoading = true;
		setText(text);
	}
}
