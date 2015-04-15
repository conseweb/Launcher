package com.nd.launcherdev.launcher.touch.outline;

import java.lang.reflect.Method;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.screens.ScreenViewGroup;
import com.nd.launcherdev.launcher.view.DragView;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.view.DragView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class DragHelper {

	/**
	 * 保存轮廓
	 * */
	private Bitmap mOutLineBitmap = null;
	/**
	 * 拖放的view 上边和网格框上边的距离
	 * */
	private int mLocationGapX = 0;
	/**
	 * 拖放的view 左边和网格框上边的距离
	 * */
	private int mLocationGapY = 0;
	/**
	 * 每个轮廓的左上角的X和Y值
	 * */
	private Point[] mDragOutlines = new Point[4];
	/**
	 * 每个轮廓的透明度
	 * */
	private float[] mDragOutlineAlphas = new float[mDragOutlines.length];
	/**
	 * 每个轮廓关联的动画对象
	 * */
	private InterruptibleInOutAnimator[] mDragOutlineAnims = new InterruptibleInOutAnimator[mDragOutlines.length];
	/**
	 * 当前做执行的动画对象
	 * */
	private int mDragOutlineCurrent = 0;
	private final Paint mDragOutlinePaint = new Paint();
	/**加速度
	 * 
	 */
	private Interpolator mEaseOutInterpolator;
	/**
	 *是否有过一次轮廓产生 
	 * */
	boolean mInitDragoutline = true;
	private Rect mTempRect = new Rect();
	/**
	 * 单例自己
	 * */
	private static DragHelper own;

	private DragHelper() {
		initAnimation();
	}
	
	/**轮廓颜色*/
	private final int OUTLINE_COLOR=0xffc7a8ff;
	/**
	 * 获取实例
	 * */
	public static DragHelper getInstance() {
		if (own == null) {
			own = new DragHelper();
		}
		return own;
	}

	/** 保存高亮轮廓 */
	public void setOutLineBitmap(Bitmap bitmap) {
		mOutLineBitmap = bitmap;
	}

	/** 设置间隔 */
	public void setLocationGap(int gapX, int gapY) {
		mLocationGapX = gapX;
		mLocationGapY = gapY;
	}

	/** 获取高亮轮廓 */
	public Bitmap getOutLineBitmap() {
		return mOutLineBitmap;
	}

	/** 获取间隔 */
	public int getLocationGapX() {
		return mLocationGapX;
	}

	/** 获取间隔 */
	public int getLocationGapY() {
		return mLocationGapY;
	}

	/** 拖放结束时，光亮图标渐隐动画 */
	public void dragOutlineAnimateOut() {

//		final int oldIndex = mDragOutlineCurrent;

		for(int i = 0; i < mDragOutlineAnims.length; i ++){
			if (mDragOutlineAnims[i] != null && !mDragOutlineAnims[i].isOut()) {
				mDragOutlineAnims[i].animateOut();
			}
		}
	}
	
	/**
	 * 清除所有光亮动画
	 */
	public void clearAllScreensOutline(){
		clearOtherScreenOutline(null);
	}
	
	/**
	 * 清除除指定屏外，其它屏的光亮动画
	 * @param curCellLayout 除指定屏，可为null
	 */
	public void clearOtherScreenOutline(CellLayout curCellLayout){
		for(int i = 0; i < mDragOutlineAnims.length; i ++){
			if (mDragOutlineAnims[i] != null && !mDragOutlineAnims[i].isOut()
					&& mDragOutlineAnims[i].getCellLayout() != null && mDragOutlineAnims[i].getCellLayout() != curCellLayout) {
				mDragOutlineAnims[i].clear();
			}
		}
	}

	/** 初始化拖放的动画类 */
	private void initAnimation() {
		// 拖放时的残影
		for (int i = 0; i < mDragOutlines.length; i++) {
			mDragOutlines[i] = new Point(-1, -1);
		}
		mEaseOutInterpolator = new DecelerateInterpolator(2.5f);
		final int duration = 1000;
		final float fromAlphaValue = 0;
		final float toAlphaValue = 250;

		Arrays.fill(mDragOutlineAlphas, fromAlphaValue);
		for (int i = 0; i < mDragOutlineAnims.length; i++) {
			final InterruptibleInOutAnimator anim = new InterruptibleInOutAnimator(duration, fromAlphaValue, toAlphaValue);
			anim.setLogTag("A" + i);
			anim.getAnimator().setInterpolator(mEaseOutInterpolator);
			final int thisIndex = i;
			anim.getAnimator().addUpdateListener(new AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					final Bitmap outline = (Bitmap) anim.getTag();
					if (outline == null) {
						animation.cancel();
					} else {
						mDragOutlineAlphas[thisIndex] = (Float) animation.getAnimatedValue();
						if(anim.getCellLayout() != null){
							final int left = mDragOutlines[thisIndex].x;
							final int top = mDragOutlines[thisIndex].y;
							((CellLayout)anim.getCellLayout()).enableHardwareLayers();
							anim.getCellLayout().invalidate(left, top, left + outline.getWidth(), top + outline.getHeight());
						}
						
					}
				}
			});
			anim.getAnimator().addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if ((Float) ((ValueAnimator) animation).getAnimatedValue() == 0f) {
						anim.setTag(null);
//						((CellLayout)anim.getCellLayout()).enableHardwareLayers();
						if(anim.getCellLayout() != null){
							((CellLayout)anim.getCellLayout()).destroyHardwareLayer();
							anim.getCellLayout().invalidate();
							anim.setCellLayout(null);
						}
					}
				}
			});
			mDragOutlineAnims[i] = anim;
		}
	}

	/**
	 * 1.开始拖动时创建一个轮廓显示动画 2.只有在桌面拖动时，需要显示，从匣子中拖动的以及从dock拖动的不会显示动画
	 * */
	public void fadeInOnStartDrag(DragView mDragView, CellLayout cellLayout, ScreenViewGroup mWorkspace) {
		int x;
		int y;
		View view = mDragView.getDragingView();
		if (view != null && mWorkspace.isNeedFadeAnimation(view)) {
			x = mDragView.getDragingView().getLeft() + (mDragView.getDragingView().getWidth() - mOutLineBitmap.getWidth()) / 2;
			y = mDragView.getDragingView().getTop() + (mDragView.getDragingView().getHeight() - mOutLineBitmap.getHeight()) / 2;
			mDragOutlines[mDragOutlineCurrent].set(x, y);
			mDragOutlineAnims[mDragOutlineCurrent].setTag(mOutLineBitmap);
			mDragOutlineAnims[mDragOutlineCurrent].setCellLayout(cellLayout);
			mDragOutlineAnims[mDragOutlineCurrent].animateIn();
			mInitDragoutline = true;
		}
	}

	/**
	 * 生成一个显示动画，同时将上一个显示动画改变成为消失动画
	 * */
	public void fadeInAndFadeOut(Rect mDragAvailableCell, CellLayout cellLayout) {
		final int oldIndex = mDragOutlineCurrent;
		int x;
		int y;
		if(!mDragOutlineAnims[oldIndex].isOut()){			
			mDragOutlineAnims[oldIndex].animateOut();
		}
		mDragOutlineCurrent = (oldIndex + 1) % mDragOutlines.length;

		x = mDragAvailableCell.left + (mDragAvailableCell.width() - mOutLineBitmap.getWidth()) / 2;
		y = mDragAvailableCell.top + (mDragAvailableCell.height() - mOutLineBitmap.getHeight()) / 2;
		mDragOutlines[mDragOutlineCurrent].set(x, y);
		mDragOutlineAnims[mDragOutlineCurrent].setTag(mOutLineBitmap);
		mDragOutlineAnims[mDragOutlineCurrent].setCellLayout(cellLayout);
		mDragOutlineAnims[mDragOutlineCurrent].animateIn();

	}

	/**
	 * 假如在拖放开始时，未生成一个显示动画 则用这个函数生成一个一个显示动画 例如从 dock拖放View时，startDrag时就不会生成显示动画
	 * 在调用这个函数后就是生成显示动画
	 * */
	public void fadeInIfNeed(Rect mDragAvailableCell, CellLayout cellLayout) {
		if(mDragAvailableCell == null || mOutLineBitmap == null)
			return;
		
		int x = 0;
		int y = 0;
		if (!mInitDragoutline) {
			x = mDragAvailableCell.left + (mDragAvailableCell.width() - mOutLineBitmap.getWidth()) / 2;
			y = mDragAvailableCell.top + (mDragAvailableCell.height() - mOutLineBitmap.getHeight()) / 2;
			mDragOutlines[mDragOutlineCurrent].set(x, y);
			mDragOutlineAnims[mDragOutlineCurrent].setTag(mOutLineBitmap);
			mDragOutlineAnims[mDragOutlineCurrent].setCellLayout(cellLayout);
			mDragOutlineAnims[mDragOutlineCurrent].animateIn();
		} else {
			mInitDragoutline = false;
		}
	}

	/** 绘制各个残影 */
	public void dispatch(Canvas canvas, CellLayout cellLayout) {
		final Paint paint = mDragOutlinePaint;
		for (int i = 0; i < mDragOutlines.length; i++) {
			final float alpha = mDragOutlineAlphas[i];
			if (alpha > 0) {
				CellLayout layout = mDragOutlineAnims[i].getCellLayout();
				if (layout == cellLayout) {
					final Point p = mDragOutlines[i];
					final Bitmap b = (Bitmap) mDragOutlineAnims[i].getTag();
					paint.setAlpha((int) (alpha + .5f));
					canvas.drawBitmap(b, p.x, p.y, paint);
				}
			}
		}
	}

	/**
	 * 初始化当前被拖动的图标的轮廓
	 * */
	public void initDragOUtline(DragView mDragView, ScreenViewGroup workspace) {
		Canvas canvas = new Canvas();
		Point point = new Point();
		countLocationGap(mDragView, point);
		Bitmap outlineBitmap = createDragOutline(mDragView, canvas, 30, point, workspace);

		setLocationGap(point.x, point.y);
		setOutLineBitmap(outlineBitmap);
	}

	/**
	 * 计算左边空白位置
	 * */
	void countLocationGap(DragView v, Point p) {
		int gapX = 0;
		int gapY = 0;
		p.x = gapX;
		p.y = gapY;
	}

	/**
	 * 创建轮廓
	 * */
	private Bitmap createDragOutline(DragView v, Canvas canvas, int padding, Point point, ScreenViewGroup workspace) {
		Bitmap b = workspace.createWidgetPreviewViewDragOutline(v, canvas, padding);
		if(b == null){//如果不是从匣子中拖出小部件
			View view = v.getDragingView();
			b = Bitmap.createBitmap(view.getWidth() + padding, view.getHeight() + padding, Bitmap.Config.ARGB_4444);
			canvas.setBitmap(b);
			drawDragView(view, canvas, padding, true, workspace);
			createOutLine(b, canvas);
			//2.3.3 htc手机执行这行代码是报空指，暂时删除了
			//canvas.setBitmap(null);
		}
		return b;
	}

	public void createOutLine(Bitmap src, Canvas canvas) {
		int outlineColor = OUTLINE_COLOR;
		int tempoffset[] = new int[2];
		Paint paint = new Paint();
		/**
		 * 三行代码是产生一个原图片对应的黑色图片,CreateClipTable(180, 255)是产生一个过180-255的过滤表 将过滤设置到
		 * 画笔中，在调用时extractAlpha函数时再用这个画笔，产生的效果如下所述 设想原图片中 颜色 C=ARGB, 当 A<180 时
		 * A=0，R=0，G=0,B=0; 当A>=180 && A<=255 时 ， A=(A-180)/(255f-180)*255
		 * ,R=0，G=0,B=0; 最终效果就是图片透明度在 180以下的像素变成完全透明，透明到180到200 的透明度重新计算，颜色为黑色
		 * 产生效果如图1
		 * */
		MaskFilter filter = null;
		try {
			Class c = Class.forName("android.graphics.TableMaskFilter");
			Method CreateClipTable = c.getDeclaredMethod("CreateClipTable", new Class[] { int.class, int.class });
			filter = (MaskFilter) CreateClipTable.invoke(c, new Object[] { 1, 255 });
		} catch (Exception e) {
			e.printStackTrace();
		}
//		TableMaskFilter filter = TableMaskFilter.CreateClipTable(1, 255);
		
		paint.setMaskFilter(filter);
		Bitmap blackBitmap = src.extractAlpha(paint, tempoffset);

		/**
		 * 以下代码是产生一个轮廓,它是在上一面得到的全黑色的基础上，得到全黑图的轮廓 用到了 BlurMaskFilter(1,
		 * BlurMaskFilter.Blur.OUTER)，其它1表示半径，也就是轮廓的宽度，
		 * BlurMaskFilter.Blur.OUTER，表示轮廓是产生在黑色图片的外围 产生效果如图2
		 * */
		BlurMaskFilter blurFilter = new BlurMaskFilter(3, BlurMaskFilter.Blur.OUTER);
		paint.setMaskFilter(blurFilter);
		Bitmap OutLineBitmap = blackBitmap.extractAlpha(paint, tempoffset);
		paint.setColor(outlineColor);
		paint.setMaskFilter(null);
		Matrix matrix = new Matrix();
		matrix.preTranslate(tempoffset[0], tempoffset[1]);
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		canvas.drawBitmap(OutLineBitmap, matrix, paint);

		/**
		 * 释放相关的Bitmap对象
		 * */

		blackBitmap.recycle();
		OutLineBitmap.recycle();
		blackBitmap = null;
		OutLineBitmap = null;
	}

	/**
	 * 把View画到一个Canvas,实际效果就是保存在一个图片上
	 * */
	private void drawDragView(View v, Canvas destCanvas, int padding, boolean pruneToDrawable, ScreenViewGroup workspace) {
		final Rect clipRect = mTempRect;
		v.getDrawingRect(clipRect);
		int bottom = workspace.getIconBottom(v);
		if (bottom > 0) {
			clipRect.bottom = bottom;
		}
		destCanvas.save();
		destCanvas.translate(-v.getScrollX() + padding / 2, -v.getScrollY() + padding / 2);
		destCanvas.clipRect(clipRect, Op.REPLACE);
		v.draw(destCanvas);
		destCanvas.restore();
	}

	

}
