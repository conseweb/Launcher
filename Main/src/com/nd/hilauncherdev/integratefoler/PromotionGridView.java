package com.nd.hilauncherdev.integratefoler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridView;

public class PromotionGridView extends GridView {

	public PromotionGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PromotionGridView(Context context) {
		super(context, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		return super.onTouchEvent(ev);
		boolean result = super.onTouchEvent(ev);
		Log.e("PromotionGridView","onTouchEvent===="+ev.getAction()+","+result);
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
			Log.e("PromotionGridView","onTouchEvent more return false====");
			return false;
		}
		return result;
		// if(ev.getAction() == MotionEvent.ACTION_DOWN){
		// x1 = ev.getX();
		// y1 = ev.getY();
		// }
		// if (ev.getAction() == MotionEvent.ACTION_UP) {
		// x2 = ev.getX();
		// y2 = ev.getY();
		// if (Math.abs(x1 - x2) < 6) {
		// return false;// 距离较小，当作click事件来处理
		// }
		// if(Math.abs(x1 - x2) >60){ // 真正的onTouch事件
		// }
		// }
		// return true;
	}

	float x1, y1, x2, y2;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = super.onInterceptTouchEvent(ev);
		Log.e("PromotionGridView","onInterceptTouchEvent===="+ev.getAction()+","+result);
		return result;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		Log.e("PromotionGridView","dispatchTouchEvent===="+ev.getAction());
//		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//			return true;
//		}
//		boolean result =super.dispatchTouchEvent(ev);
//		Log.e("PromotionGridView","dispatchTouchEvent==result:"+result);
//		return result;
//	}
}
