package com.nd.hilauncherdev.integratefoler;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class IntegrateFolderTitleStripBak extends View implements ViewPager.OnPageChangeListener {

	private ViewPager mViewPager; // a
	private PagerAdapter mPageAdapter; // b
	private int mScrollState = 0; // c
	private int d = -1; // d
	private Paint mPaint = new Paint(1); // e
	private float mNonePrimaryAlpha = 1.0f; // f
	private Paint.FontMetrics mFontMetrics = new Paint.FontMetrics(); // g
	private float mBaseLine; // h
	private ArrayList<Rect> mRectList = new ArrayList(); // i
	private float mCenterX; // j
	// 0 是当前的透明值 1是其他的
	private int[] mAlphaArray = new int[2];// k
	private int mScrollPosition; // l
	// 0 是当前的缩放值 1是其他的
	private float[] mScaleArray = new float[2];// m
	private float n = 1.5F; // n

	public IntegrateFolderTitleStripBak(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		TypedArray localTypedArray = context.obtainStyledAttributes(attrs, null, defStyleAttr, 0);
//		setTextSize(localTypedArray.getDimensionPixelSize(2, (int) TypedValue.applyDimension(2, 14.0F, context.getResources().getDisplayMetrics())));
//		setTextColor(localTypedArray.getColor(5, -1));
//		localTypedArray.recycle();
		Log.e("IntegrateFolderTitleStrip","3===:");
		setTextSize(50);
		setTextColor(Color.WHITE);
		initPaint();
	}

	public IntegrateFolderTitleStripBak(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		Log.e("IntegrateFolderTitleStrip","2===:");
	}

	// ViewPager.OnPageChangeListener 接口  滚动过程中调用   0到1 滑动position为0  1到0滑动 position为0 position按照前面显示的那页去确定的
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		Log.e("IntegrateFolderTitleStrip","onPageScrolled:"+position+","+positionOffset+","+positionOffsetPixels);
		//positionOffset 相对于一个页面的百分比
		update(position, positionOffset);
	}

	// ViewPager.OnPageChangeListener 接口   滑动超过一半  滑动完毕后（state =2）调用该方法
	@Override
	public void onPageSelected(int position) {
		Log.e("IntegrateFolderTitleStrip","onPageSelected:"+position+","+mScrollState);
		if (mScrollState != 0) {
			return;
		}
		update(position, 0.0f);
	}

	// ViewPager.OnPageChangeListener 接口   开始滑动和结束滑动回调这个方法
	//有三种状态（0，1，2）。state ==1的时辰默示正在滑动，state==2的时辰默示滑动完毕了，state==0的时辰默示什么都没做。
	@Override
	public void onPageScrollStateChanged(int state) {
		this.mScrollState = state;
		Log.e("IntegrateFolderTitleStrip","onPageScrollStateChanged:"+state);

	}

	public void setTextColor(int color) {
		this.mPaint.setColor(color);
	}

	public void setTextSize(float textSize) {
		this.mPaint.setTextSize(textSize);
		this.mPaint.getFontMetrics(mFontMetrics);
	}

	public void setViewPager(ViewPager viewpager) {
		this.mViewPager = viewpager;
		if (viewpager == null)
			return;
		this.mPageAdapter = mViewPager.getAdapter();
		initRectList();
		update(mViewPager.getCurrentItem(), 0.0F);
	}

	public void setNonePrimaryAlpha(float alpha) {
		mNonePrimaryAlpha = alpha;
		invalidate();
	}

	/**
	 * 初始化Paint d()
	 */
	private void initPaint() {
		mPaint.setTextAlign(Paint.Align.CENTER);
		// 设置字体
		// Typeface localTypeface = kC.a(this.mContext);
		// if (localTypeface == null)
		// return;
		// this.e.setTypeface(localTypeface);
	}

	/**
	 * 返回标题文字大小 public float a()
	 * 
	 * @return
	 */
	public float getTextSize() {
		return mPaint.getTextSize();
	}
	//offset 滑动距离相对于一个页面的百分比
	private void update(int position, float offset) {
		if ((position >= 0) && (position < mRectList.size()))
			mCenterX = (-((Rect) mRectList.get(position)).exactCenterX());
		if ((mRectList.size() > 1) && (position < -1 + mRectList.size())) // 不是第一个并且不是不是最后一个
			mCenterX -= offset * (((Rect) mRectList.get(position + 1)).exactCenterX() - ((Rect) mRectList.get(position)).exactCenterX());
		mScrollPosition = position;
		mAlphaArray[0] = (int) (255.0F * (mNonePrimaryAlpha + (1.0F - mNonePrimaryAlpha) * (1.0F - offset)));
		mAlphaArray[1] = (int) (255.0F * (mNonePrimaryAlpha + offset * (1.0F - mNonePrimaryAlpha)));
		mScaleArray[0] = (1.0F + (this.n - 1.0F) * (1.0F - offset));
		mScaleArray[1] = (1.0F + offset * (this.n - 1.0F));
		invalidate();
	}

	/**
	 * 返回一个页面值
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int a(float x, float y) {
		if ((x < 0.0F) || (y > getHeight()))
			return -1;
		int curItem = mViewPager.getCurrentItem();
		int halfWidth = getWidth() / 2;
		for (int i3 = 0; i3 < 5; ++i3) {
			int i4 = 2 + (curItem - i3);
			if ((i4 >= 0) && (i4 < mRectList.size()) && (((Rect) mRectList.get(i4)).contains((int) (x - mCenterX - halfWidth), 1)))
				return i4;
		}
		return -1;
	}

	/**
	 * 初始化mRectList e()
	 */
	public void initRectList() {
		mRectList.clear();
		if (mPageAdapter == null || mPageAdapter.getCount() <= 0) {
			return;
		}
		int count = mPageAdapter.getCount();
		/**
		 * 一个textsize的宽度
		 */
		int textSizeWidth = (int) (1.8F * this.mPaint.getTextSize());
		int index = 0;
		int left = 0;
		while (index < count) {
			CharSequence text = mPageAdapter.getPageTitle(index);
			Log.e("IntegrateFolderTitleStrip","text:"+text);
			if (text.length() > 9) {
				text = text.subSequence(0, 8) + "...";
			}
			int textWidth = (int) (0.5f + mPaint.measureText(text, 0, text.length()));
			mRectList.add(new Rect(left, 0, left + textWidth, 2));
			left = left + textWidth + textSizeWidth;
			index++;
		}
	}

	public int getBaseline() {
		return (int) mBaseLine;
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mViewPager = null;
		mPageAdapter = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (mScrollState == 0) {
			if (event.getAction() != MotionEvent.ACTION_UP) {
				for (this.d = a(event.getX(), event.getY());; this.d = -1)
					;
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if ((mPageAdapter == null) || (mViewPager == null) || (mPageAdapter.getCount() == 0)) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		// 测量的整条的宽高
		CharSequence localCharSequence = null;
		if (View.MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY)
			localCharSequence = mPageAdapter.getPageTitle(0);
		localCharSequence = mPageAdapter.getPageTitle(0);
		for (int width = resolveSize((int) (3.0F * mPaint.measureText(localCharSequence, 0, localCharSequence.length())) + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);; width = View.MeasureSpec
				.getSize(widthMeasureSpec)) {
			int height = resolveSize((int) (mFontMetrics.bottom - mFontMetrics.top + getPaddingTop() + getPaddingBottom()), heightMeasureSpec);
			setMeasuredDimension(width, height);
			mBaseLine = (getPaddingTop() + (height - getPaddingTop() - getPaddingBottom()) / 2.0F - (mFontMetrics.top + mFontMetrics.bottom) * Math.max(this.n, 1.0F) / 2.0F);
			update(mViewPager.getCurrentItem(), 0.0F);
			return;
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mPageAdapter == null)
			return;
		int halfWidth;
		int i2;
		int i3;
		int alpha;
		int curItem;
		int pageCount;
		int i7;
		halfWidth = getWidth() / 2;
		i2 = (int) (getPaddingLeft() - mCenterX - halfWidth);
		i3 = (int) (getWidth() - getPaddingRight() - mCenterX - halfWidth);
		// 不是当前选项透明值
		alpha = (int) (255.0F * mNonePrimaryAlpha);   //i4
		curItem = mViewPager.getCurrentItem();
		pageCount = mPageAdapter.getCount();

		Rect localRect = null;
		Object localObject = "";
		float scale = 0;

		for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
			if ((pageIndex >= 0) && (pageIndex < pageCount)) {
				localRect = (Rect) mRectList.get(pageIndex);
				if (localRect.intersects(i2, 0, i3, 1)) {
					localObject = mPageAdapter.getPageTitle(pageIndex);
					if (((CharSequence) localObject).length() > 9)
						localObject = ((CharSequence) localObject).subSequence(0, 8) + "...";
					if (pageIndex == mScrollPosition +1) {
						mPaint.setAlpha(mAlphaArray[1]);
						scale = mScaleArray[1];
//						mPaint.setAlpha(mAlphaArray[1]);
					} else if(pageIndex == mScrollPosition){
						mPaint.setAlpha(mAlphaArray[0]);
						scale = mScaleArray[0];
					} else {
						mPaint.setAlpha(alpha);
						scale = 1.0F;
					}
					Log.e("zhengdraw",curItem+","+mScrollPosition+","+pageIndex+","+scale);
				}
//				localObject = mPageAdapter.getPageTitle(pageIndex);
//				if (((CharSequence) localObject).length() > 9)
//					localObject = ((CharSequence) localObject).subSequence(0, 8) + "...";
//				if (pageIndex == mScrollPosition +1) {
//					mPaint.setAlpha(mAlphaArray[1]);
//					scale = mScaleArray[1];
////					mPaint.setAlpha(mAlphaArray[1]);
//				} else if(pageIndex == mScrollPosition){
//					mPaint.setAlpha(mAlphaArray[0]);
//					scale = mScaleArray[0];
//				} else {
//					mPaint.setAlpha(alpha);
//					scale = 1.0F;
//				}
//				Log.e("zhengdraw",curItem+","+mScrollPosition+","+pageIndex+","+scale);
			}
			float f2 = localRect.exactCenterX() + mCenterX + halfWidth;
			float f3 = mBaseLine;
			if (scale != 1.0F) {
				canvas.save();
				canvas.scale(scale, scale, f2, f3);
			}
			canvas.drawText((CharSequence) localObject, 0, ((CharSequence) localObject).length(), f2, f3, mPaint);
			if (scale != 1.0F)
				canvas.restore();
		}

	}

}
