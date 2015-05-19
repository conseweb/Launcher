package com.bitants.launcherdev.launcher.appslist.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.bitants.launcherdev.launcher.appslist.AppslistActivity;

public class AlphalBetLabel extends View {
	private Paint paint;
	private String mAlphalbet = "";

	public AlphalBetLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);
		paint.setTextAlign(Align.CENTER);
	}

	public void setAlphalBet(String str) {
		if (str != null)
			this.mAlphalbet = str;
		else
			this.mAlphalbet = "";
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mAlphalbet != "") {
			RectF rectF = new RectF();
			rectF.top = AppslistActivity.Apppadding + 5;
			rectF.left = AppslistActivity.Apppadding + 5;
			rectF.right = getWidth() - rectF.top;
			rectF.bottom = getWidth() - rectF.top;
			paint.setColor(Color.parseColor("#22000000"));
			canvas.drawRoundRect(rectF, AppslistActivity.Apppadding * 2, AppslistActivity.Apppadding * 2, paint);
		}
		paint.setColor(Color.WHITE);
		paint.setTextSize(getHeight() / 4);
		canvas.drawText(mAlphalbet, getWidth()/2, getWidth()/2 + (AppslistActivity.Apppadding + 5), paint);
		super.onDraw(canvas);
	}
}
