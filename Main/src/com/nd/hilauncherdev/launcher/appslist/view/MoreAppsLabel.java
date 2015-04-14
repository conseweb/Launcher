package com.nd.hilauncherdev.launcher.appslist.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bitants.launcher.R;

public class MoreAppsLabel extends View {
  private Paint paint;
  private boolean isTouch = false;
  private Drawable appPlay;
  private Drawable skipIcon;
  private Drawable pressBackgound;
  private Drawable unPressBackgound;
  private Context cxt; 
	public MoreAppsLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
		cxt = context;
		skipIcon = context.getResources().getDrawable(R.drawable.right_arrow);
		appPlay=context.getResources().getDrawable(R.drawable.play_appicon);
		pressBackgound=getResources().getDrawable(R.drawable.search_paste_right_press);
		unPressBackgound=getResources().getDrawable(R.drawable.search_paste_right);
		paint = new Paint();	
		paint.setColor(Color.BLACK);
		paint.setAlpha(255);
		paint.setAntiAlias(true);
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
	}
   @Override
	protected void onDraw(Canvas canvas) {
	   drawBounds(canvas);
	   drawBody(canvas);
		super.onDraw(canvas);
	}
   public void drawBounds(Canvas canvas)
   {
	   if(isTouch){
		   pressBackgound.setBounds(0,0,getWidth(),getHeight());
		   pressBackgound.draw(canvas);
	   }
	   else
		   {
		    unPressBackgound.setBounds(0,0,getWidth(),getHeight()); 
		    unPressBackgound.draw(canvas);
		   }
	   
   }
   public void drawBody(Canvas canvas)
   {
	   appPlay.setBounds(40, getHeight()/5, 80, getHeight()*4/5);
	   appPlay.draw(canvas);
	   skipIcon.setBounds(getWidth()-80,getHeight()/5,getWidth()-40,getHeight()*4/5);
	   skipIcon.draw(canvas);
	   Rect mBound = new Rect(0,0,getWidth(),getHeight());
	   FontMetricsInt fontMetrics = paint.getFontMetricsInt();  
       int baseline = mBound.top + (mBound.bottom - mBound.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  
       canvas.drawText(cxt.getString(R.string.launcher_appslist_appstore_text), getWidth()/2-40, baseline, paint);
      
   }
   @Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
	    case MotionEvent.ACTION_MOVE:
				isTouch = true;
				invalidate();
				return true;
	   case MotionEvent.ACTION_UP:
		        isTouch=false;
		        invalidate();
		        return true;

		}
		return super.onTouchEvent(event);
	}
}
