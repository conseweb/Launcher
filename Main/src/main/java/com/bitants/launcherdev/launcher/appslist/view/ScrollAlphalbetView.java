package com.bitants.launcherdev.launcher.appslist.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bitants.launcherdev.launcher.appslist.AppslistActivity;

import java.util.ArrayList;

public class ScrollAlphalbetView extends ImageView {
	private Paint paint = null;
	private String[] mAlphaString = null;
	private int mLength = 0;
	private int startBackgroundIndex = 0;
	private int endBackgroundIndex = 0;
	private int choose = -1;
	private static boolean isListScroll = true;
	private int[] mCount;
	private ListView mList;

	private boolean mIsLoading = true;

	private int[] mStartPositions;

	public ScrollAlphalbetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setTextSize(AppslistActivity.AppstextSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.WHITE);
	}

	public void setLoading() {
		mIsLoading = true;
	}

	public void ConfigView(ListView list, TextView text) {
		mList = list;
	}

	public void ConfigData(String[] sections, int[] count, int[] counts, boolean visible) {
		// TODO configData
		if (sections != null)
			mAlphaString = sections;
		mLength = sections.length;
		if (count == null || count.length == 0) {
			setVisibility(View.GONE);
			return;
		}
		if (visible) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.GONE);
		}
		mCount = new int[count.length];
		System.arraycopy(count, 0, mCount, 0, count.length);

		int position = 0;
		int lineCount = 0;
		ArrayList<Integer> startArrays = new ArrayList<Integer>();
		int numPerLine = AllappsListview.NUM_PER_LINE;
		for (int i = 0; i < counts.length; i++) {
			int lines = (counts[i] % numPerLine == 0 ? counts[i] / numPerLine : counts[i] / numPerLine + 1);
			lineCount += lines;
			for (int j = 0; j < lines; j++) {
				int refPosition = position + j * numPerLine;
				startArrays.add(refPosition);
			}
			position += counts[i];
		}
		int startPositions[] = new int[lineCount];
		for (int i = 0; i < startPositions.length; i++) {
			startPositions[i] = startArrays.get(i);
		}
		mStartPositions = startPositions;
		mIsLoading = false;
	}

	public void setBackground(int first, int last, boolean isScroll) {

		startBackgroundIndex = first;
		endBackgroundIndex = last;
		this.isListScroll = isScroll;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		float height = getHeight();
		float singleHeight = height / mAlphaString.length;
		for (int i = 0; i < mAlphaString.length; i++) {
			if (startBackgroundIndex <= i &&  endBackgroundIndex >= i) {
				paint.setAlpha(255);
			}else{
				paint.setAlpha(100);
			}
			canvas.drawText(mAlphaString[i],AppslistActivity.AppstextSize, singleHeight * i + singleHeight / 2, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float nowY = event.getY();
		int action = event.getAction();
		int p;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			p = getPosition(nowY);
			if (p >= 0 && p <= mLength) {
				choose = p;
				isListScroll = false;
				doAlphaViewTrigger(p);
			}
			break;
		}

		return true;

	}

	public static boolean isListScroll() {
		return isListScroll;
	}

	public int getPosition(float nowY) {
		int position = (int) (nowY / getHeight() * mAlphaString.length);
		Log.i("position", "position:" + position);
		return position;
	}

	private void doAlphaViewTrigger(int p) {

		int pos = getSelection(p);
		if (pos != -1 && !mIsLoading) {
			if (pos > 0) {
				pos = pos - 1;
			}
			mList.setSelection(pos);
		}
	}

	private int getSelection(int p) {
		if (mCount == null || p < 0 || p >= mCount.length) {
			return -1;
		}
		int pos = getPositionForSection(mCount[p]);// mCount[p]为点击的p位置所对应的字母所对应的首个应用程序在总应用程序的位置
		//
		return pos;
	}

	public int getPositionForSection(int position) {
		int originalPosition = position;
		int length = mStartPositions.length;
		for (int i = 0; i <= length - 1; i++) {
			if (originalPosition == mStartPositions[i] + 1) {

				return i;
			}

		}
		return -1;
	}

	public void setChoose(int choose) {
		this.choose = choose;
	}

	public int getChoose() {
		// TODO Auto-generated method stub
		return choose;
	}

}
