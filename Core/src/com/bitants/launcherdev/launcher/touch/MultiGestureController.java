package com.bitants.launcherdev.launcher.touch;

import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;

/**
 * 多指手势判断
 * @author DingXiaohui
 * @date 2013-7-29
 * @time 上午11:21:54
 * @Description 
 *
 */
public class MultiGestureController {
	public static final String TAG = "MultiGestureController";
	
	/* 水平偏移阈值 */
	public static final int MULTI_DIST_MIN_X = 10;
	/* 垂直偏移阈值 */
	public static final int MULTI_DIST_MIN_Y = 30;
	/* 判读的偏移量 在屏幕分辨率中 所占的百分比 */
	public static final float MULTI_DIST_OFFSET_FACTOR = 0.125f;
	
	/* 判读 双指分离或者合拢 所需的位移量的百分比 */
	public static final float MULTI_SHIFT_OFFSET_FACTOR = 0.45f;
	
	/* 双指 上或下 滑动 需要的 最小速度 */
	public static final int MIN_VELOCITY = 300;
	
	/* 手势类型 */
	public static enum GestureType {
		MULTI_UP, // 双指向上
		MULTI_DOWN, // 双指向下
		OTHER, // 非 双指向上 或者 双指向下 的其他行为，不处理
		NONE // 无法判断
	}

	private int mMulitTouchOffsetMinX = MULTI_DIST_MIN_X;
	private int mMultiTouchOffsetMinY = MULTI_DIST_MIN_Y;
	
	private int mMultiTouchShiftOffset = 0;
	
	private float mOrigXFingerOne = 0.0f; // 水平方向 触点1 起始位置
	private float mOrigYFingerOne = 0.0f; // 垂直方向 触点1 起始位置
	private float mOrigXFingerTwo = 0.0f; // 水平方向 触点2 起始位置
	private float mOrigYFingerTwo = 0.0f; // 垂直方向 触点1 起始位置
	
	private float mCurrXFingerOne = 0.0f; // 水平方向 触点1 当前位置
	private float mCurrYFingerOne = 0.0f; // 垂直方向 触点1 当前位置
	private float mCurrXFingerTwo = 0.0f; // 水平方向 触点2 当前位置
	private float mCurrYFingerTwo = 0.0f; // 垂直方向 触点2 当前位置
	
	private GestureType mCurrentGestureType = GestureType.NONE;
	
	/* 被手势判定 的对象 */
	private MultiGestureDispatcher mDispatcher;
	/* 是否 已经标记起点 */
	private boolean mMultiOriginalMarked = false;
	/* 是否 已经执行回调 */
	private boolean mInvoked = false;
	
	public MultiGestureController(MultiGestureDispatcher dispatcher) {
		mDispatcher = dispatcher;
		
		int screenDim[] = TelephoneUtil.getScreenResolutionXY(BaseConfig.getApplicationContext());
		int screenWidth = screenDim[ 0 ];
		int screenHeight = screenDim[ 1 ];
		
		int xOffset = Math.max( (int) ( screenWidth * MULTI_DIST_OFFSET_FACTOR), 
				MULTI_DIST_MIN_X );
		
		mMulitTouchOffsetMinX = xOffset;
		
		int shift = Math.min( (int) (screenWidth * MULTI_SHIFT_OFFSET_FACTOR),
				(int) (screenHeight * MULTI_SHIFT_OFFSET_FACTOR) );
		
		mMultiTouchShiftOffset = shift;
	}
	
	/**
	 * 重新绑定 被判定对象
	 * @author DingXiaohui
	 * @date 2013-7-29
	 * @time 上午11:28:17
	 * @Description 
	 * 
	 * @param dispatcher
	 */
	public void bind(MultiGestureDispatcher dispatcher) {
		mDispatcher = dispatcher;
	}
	
	
	public void dispatchActionDown(MotionEvent event) {
		if ( event.getPointerCount() > 1 && !mMultiOriginalMarked ) {
			// 双指移动，标记起点
			mOrigXFingerOne = event.getX( 0 );
			mOrigXFingerTwo = event.getX( 1 );
			
			mOrigYFingerOne = event.getY( 0 );
			mOrigYFingerTwo = event.getY( 1 );
			
			mMultiOriginalMarked = true;
		}
	}
	
	/**
	 * 判定 移动中的手势是否双指上滑/下滑，若是响应事件event
	 * @param tracker
	 * @param event
	 * @param maxVelocity
	 * @return
	 */
	public boolean actionMultiDownOrMultiUp(VelocityTracker tracker, MotionEvent event, int maxVelocity) {
		try {
			if ( event.getPointerCount() > 1 ) {
				if ( !mMultiOriginalMarked ) {
					// 双指移动，标记起点
					mOrigXFingerOne = event.getX( 0 );
					mOrigXFingerTwo = event.getX( 1 );
					
					mOrigYFingerOne = event.getY( 0 );
					mOrigYFingerTwo = event.getY( 1 );
					
					mMultiOriginalMarked = true;
				}
				
				// 移动中更新当前点位置
				mCurrXFingerOne = event.getX( 0 );
				mCurrXFingerTwo = event.getX( 1 );
				
				mCurrYFingerOne = event.getY( 0 );
				mCurrYFingerTwo = event.getY( 1 );
				
				return matchType( tracker, event, maxVelocity );
			}
		} catch (Exception e) {
			Log.e( TAG, "dispatchActionMove-->" + e );
		}
		return false;
	}
	
	/**
	 * 判定 离开屏幕时的手势
	 * @author DingXiaohui
	 * @date 2013-7-29
	 * @time 上午11:29:31
	 * @Description 
	 * 
	 * @param event
	 */
	public void dispatchActionUp(MotionEvent event) {
		reset();
	}

	/**
	 * 是否双指上滑/下滑
	 * @param tracker
	 * @param event
	 * @param maxVelocity
	 * @return
	 */
	private boolean matchType(VelocityTracker tracker, MotionEvent event, int maxVelocity) {
		boolean result = false;
		tracker.computeCurrentVelocity(1000, maxVelocity);
		
		final int velocityXOne = (int) tracker.getXVelocity( 0 );
		final int absVelocityXOne = Math.abs( velocityXOne );
		
		final int velocityXTow = (int) tracker.getXVelocity( 1 );
		final int absVelocityXTwo = Math.abs( velocityXTow );
		
		final int velocityYOne = (int) tracker.getYVelocity( 0 );
		final int absVelocityYOne = Math.abs( velocityYOne );
		
		final int velocityYTwo = (int) tracker.getYVelocity( 1 );
		final int absVelocityYTwo = Math.abs( velocityYTwo );
		
		
		final int movementXOne = (int)(mCurrXFingerOne - mOrigXFingerOne);
		final int absXMovementOne = Math.abs( movementXOne );
		
		final int movementXTwo = (int)(mCurrXFingerTwo - mOrigXFingerTwo);
		final int absXMovementTwo = Math.abs( movementXTwo );
		
		final int movementYOne = (int)(mCurrYFingerOne - mOrigYFingerOne);
		final int absYMovementOne = Math.abs( (int)(mCurrYFingerOne - mOrigYFingerOne) );
		
		final int movementYTwo = (int)(mCurrYFingerTwo - mOrigYFingerTwo);
		final int absYMovementTwo = Math.abs( (int)(mCurrYFingerTwo - mOrigYFingerTwo) );
		
		if ( mCurrentGestureType == GestureType.NONE && 
				absVelocityYOne > absVelocityXOne && // 手指1的垂直移动速度 大于 手指1的水平移动速度 
				absVelocityYTwo > absVelocityXTwo && // 手指2的垂直移动速度 大于 手指2的水平移动速度
				absYMovementOne > absXMovementOne && // 手指1的垂直移动量 大于 手指1的水平移动量 
				absYMovementTwo > absXMovementTwo // 手指2的垂直移动量 大于 手指2的水平移动量 
				) {
				
			if ( velocityYOne >= MIN_VELOCITY // 手指1的 垂直移动速度 大于 最小移动速度（正）
					&& velocityYTwo >= MIN_VELOCITY // 手指2的 垂直移动速度 大于 最小移动速度（正）
					) {
					
				// 判断为 双指下滑
				mCurrentGestureType = GestureType.MULTI_DOWN;
				result = true;
				
				Log.i( TAG, "matchType :: MULTI_DOWN :: By velocity");

			} else if ( movementYOne >= mMultiTouchOffsetMinY && // 手指1的垂直移动距离 大于 最小垂直判定距离（正）
					movementYTwo >= mMultiTouchOffsetMinY // 手指2的垂直移动距离 大于 最小垂直判定距离（正）
					) {
					
				// 判断为 双指下滑
				mCurrentGestureType = GestureType.MULTI_DOWN;
				result = true;
				
				Log.i( TAG, "matchType :: MULTI_DOWN :: By distance");
				
			} else if ( velocityYOne <= -MIN_VELOCITY // 手指1的 垂直移动速度 大于 最小移动速度（负）
					&& velocityYTwo <= -MIN_VELOCITY // 手指2的 垂直移动速度 大于 最小移动速度（负）
					) {

				// 判断为 双指上滑
				mCurrentGestureType = GestureType.MULTI_UP;
				result = true;
				
				Log.i( TAG, "matchType :: MULTI_UP :: By velocity");

			} else if ( movementYOne <= -mMultiTouchOffsetMinY && // 手指1的垂直移动距离 大于 最小垂直判定距离（负）
					movementYTwo <= -mMultiTouchOffsetMinY // 手指2的垂直移动距离 大于 最小垂直判定距离（负） 
					) {
					
				// 判断为 双指上滑
				mCurrentGestureType = GestureType.MULTI_UP;
				result = true;
			
				Log.i( TAG, "matchType :: MULTI_UP :: By distance");
				
			}
		} else if ( Math.abs( mCurrXFingerOne - mCurrXFingerTwo ) > mMultiTouchShiftOffset || // 手指1和手指2的当前 水平距离 之间的跨度 大于 标量
				Math.abs( mCurrYFingerOne - mCurrYFingerTwo ) > mMultiTouchShiftOffset // 手指1和手指2的当前 垂直距离 之间的跨度 大于 标量 
				) {
			
			mCurrentGestureType = GestureType.OTHER; // 判定为 其他可能的行为
		}
		
		if ( result ) {
			invoke( event );
		}
		
		return result;
	}
	
	/**
	 * 根据判定接口，执行回调
	 * @author DingXiaohui
	 * @date 2013-7-29
	 * @time 上午11:30:09
	 * @Description 
	 * 
	 * @param event
	 */
	private void invoke(MotionEvent event) {
		if ( !mInvoked ) {
			
			switch ( mCurrentGestureType ) {
			case MULTI_DOWN:
				mDispatcher.onMultiDown( event );
				break;
				
			case MULTI_UP:
				mDispatcher.onMultiUp( event );
				break;
			
			default:
				break;
			}
			
			if ( mCurrentGestureType != GestureType.NONE ) {
				mInvoked = true;
			}
			
		}
	}
	
	/**
	 * 重置 所有数值
	 * @author DingXiaohui
	 * @date 2013-7-29
	 * @time 上午11:29:55
	 * @Description 
	 *
	 */
	private void reset() {
		mOrigYFingerOne = 0.0f;
		mOrigYFingerTwo = 0.0f;
		mCurrYFingerOne = 0.0f;
		mCurrYFingerTwo = 0.0f;
		
		mMultiOriginalMarked = false;
		mInvoked = false;
		
		mCurrentGestureType = GestureType.NONE;
	}
}
