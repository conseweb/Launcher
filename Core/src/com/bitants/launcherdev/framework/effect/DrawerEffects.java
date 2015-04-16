package com.bitants.launcherdev.framework.effect;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.support.BitmapWeakReferences;
import com.bitants.launcher.R;
//import com.bitants.launcherdev.datamodel.ApplicationWeakReferences;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.framework.view.commonsliding.CommonSlidingView;
import com.bitants.launcherdev.framework.view.commonsliding.CommonViewHolder;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.support.BitmapWeakReferences;
import com.bitants.launcherdev.framework.view.commonsliding.CommonLayout;
import com.bitants.launcherdev.launcher.info.WidgetInfo;
import com.bitants.launcherdev.launcher.support.BitmapWeakReferences;

/**
 * CommonSlidingView 滑动特效
 */
public class DrawerEffects {
	private static DrawerEffects effects;
	private Camera camera;
	private Matrix matrix;
	private Matrix matrixTemp;

	private DrawerEffects() {
		camera = new Camera();
		matrix = new Matrix();
		matrixTemp = new Matrix();
	}

	public static DrawerEffects getInstance() {
		if (effects == null) {
			effects = new DrawerEffects();
		}
		return effects;
	}

	/**
	 * 绘制左边屏幕的特效
	 * @param canvas
	 * @param leftScreen
	 * @param drawingTime
	 * @param workspace
	 */
	public void drawLeftEffect(Canvas canvas, int leftScreen, long drawingTime, CommonSlidingView workspace) {
		camera.save();
		matrix.setTranslate(0, 0);
		int type = EffectsType.getCurrentDrawerEffect();
		switch (type) {
		case EffectsType.DEFAULT:
			processDefault(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.CASCADE:
			processCascadeRight(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.ROLL:
			processRoll(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.TURN:
			processTurn(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.WINDOWER:
			processWindower(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.CUBE_INSIDE:
			processDrapeInside(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.CUBE_OUTSIDE:
			processDrapeOutside(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.SHUTTER:
			processShutter(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.CHORD:
			processChord(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.BINARY_STAR:
			processBinaryStar(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.WHEEL:
			processWheel(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.GLOBAL:
			processGlobal(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.CYLINDER:
			processCylinder(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.TORNADO:
			processTornado(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.TRANSFER:
			ProcessTransfer(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.TURNTABLE:
			ProcessTurntable(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.SNAKE:
			ProcessSnake(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.TIMETUNNEL:
			processTimeTunnel(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.OPEN_DOOR:
			processOpenDoor(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.LG_CUBE_INSIDE:
			processLGDrapeInside(canvas, leftScreen, drawingTime, workspace, true);
			break;
		
		// ==================点心桌面特效==============================
		case EffectsType.DX_SQUASH:
			processSquash(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_CAROUSEL:
			processCarousel(canvas, leftScreen, drawingTime, workspace, true);
			break;		
		case EffectsType.DX_PAGEWAVE:
			processPageWave(canvas, leftScreen, drawingTime, workspace, true);
			break;	
		case EffectsType.DX_CROSSFADE:
			processCrossFade(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_WINDMILL:
			processWindMill(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_PAGEZOOM:
			processPageZoom(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_PAGESLIDEDOWN:
			processPageSlideDown(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_PAGESLIDEUP:
			processPageSlideUp(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_VERTICALSCROLLING:
			processVerticalScrolling(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_STAIRDOWNLEFT:
			processStairDownLeft(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_STAIRDOWNRIGHT:
			processStairDownRight(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_CUBEOUTSIDE:
			processCubeOutside(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_TURNTABLE:
			processTurnTable(canvas, leftScreen, drawingTime, workspace, true);
			break;		
		case EffectsType.DX_ROTATING:
			processRotating(canvas, leftScreen, drawingTime, workspace, true);
			break;
		case EffectsType.DX_LOUVERWINDOW:
			processLouverWindow(canvas, leftScreen, drawingTime, workspace, true);
			break;
		}
		camera.restore();
	}

	/**
	 * 绘制右边屏幕的特效
	 * @param canvas
	 * @param rightScreen
	 * @param drawingTime
	 * @param workspace
	 */
	public void drawRightEffect(Canvas canvas, int rightScreen, long drawingTime, CommonSlidingView workspace) {
		camera.save();
		matrix.setTranslate(0, 0);
		int type = EffectsType.getCurrentDrawerEffect();
		switch (type) {
		case EffectsType.DEFAULT:
			processDefault(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.CASCADE:
			processCascadeRight(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.ROLL:
			processRoll(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.TURN:
			processTurn(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.WINDOWER:
			processWindower(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.CUBE_INSIDE:
			processDrapeInside(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.CUBE_OUTSIDE:
			processDrapeOutside(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.SHUTTER:
			processShutter(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.CHORD:
			processChord(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.BINARY_STAR:
			processBinaryStar(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.WHEEL:
			processWheel(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.GLOBAL:
			processGlobal(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.CYLINDER:
			processCylinder(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.TORNADO:
			processTornado(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.TRANSFER:
			ProcessTransfer(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.TURNTABLE:
			ProcessTurntable(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.SNAKE:
			ProcessSnake(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.TIMETUNNEL:
			processTimeTunnel(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.OPEN_DOOR:
			processOpenDoor(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.LG_CUBE_INSIDE:
			processLGDrapeInside(canvas, rightScreen, drawingTime, workspace, false);
			break;
			
		// ==================点心桌面特效==============================
		case EffectsType.DX_SQUASH:
			processSquash(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_CAROUSEL:
			processCarousel(canvas, rightScreen, drawingTime, workspace, false);
			break;	
		case EffectsType.DX_PAGEWAVE:
			processPageWave(canvas, rightScreen, drawingTime, workspace, false);
			break;	
		case EffectsType.DX_CROSSFADE:
			processCrossFade(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_WINDMILL:
			processWindMill(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_PAGEZOOM:
			processPageZoom(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_PAGESLIDEDOWN:
			processPageSlideDown(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_PAGESLIDEUP:
			processPageSlideUp(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_VERTICALSCROLLING:
			processVerticalScrolling(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_STAIRDOWNLEFT:
			processStairDownLeft(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_STAIRDOWNRIGHT:
			processStairDownRight(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_CUBEOUTSIDE:
			processCubeOutside(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_TURNTABLE:
			processTurnTable(canvas, rightScreen, drawingTime, workspace, false);
			break;		
		case EffectsType.DX_ROTATING:
			processRotating(canvas, rightScreen, drawingTime, workspace, false);
			break;
		case EffectsType.DX_LOUVERWINDOW:
			processLouverWindow(canvas, rightScreen, drawingTime, workspace, false);
			break;
		}
		camera.restore();
	}

	/**
	 * 绘制默认特效
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processDefault(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);		
		canvas.save();
		int loopShift=countLoopShift(workspace,screen,isLeft);
		if (isLeft) {
			canvas.save();
			matrix.setTranslate(0, 0);
			matrix.preTranslate(loopShift,getTop(workspace, canvas));
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		} else {
			canvas.save();
			matrix.setTranslate(0, 0);
			matrix.preTranslate(loopShift, getTop(workspace, canvas));
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}

	private final int CASCADE_SAVEFLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

	/**
	 * 层叠（进出方向与4.0一致）
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processCascadeRight(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		int topPadding = 0;
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int loopShift=countLoopShift(workspace,screen,isLeft);
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		canvas.save();
		if (!isLeft) {
			// 画右屏
			canvas.save();
			camera.translate(0, 0, shiftingX * 0.5f);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, -height / 2 - topPadding);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2+width*screen-shiftingX+loopShift, height / 2 + topPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, height + topPadding, (int) (255 - 255 * shiftingX / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		} else {
			// 画左屏幕
			canvas.save();
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift+screen*width, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 翻滚
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processRoll(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		int topPadding =0;
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			float mDegree = -shiftingX * 180.0f / width;
			camera.translate(shiftingX * height / width * 1.5f, 0, 0);
			camera.rotateZ(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, -height / 2 - topPadding);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2+screen*width-shiftingX+loopShift, height / 2 + topPadding);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			float mDegree = -shiftingX * 180.0f / width;
			camera.translate(shiftingX * height / width * 1.5f, 0, 0);
			camera.rotateZ(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, -height / 2 - topPadding);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2+screen*width-shiftingX+loopShift, height / 2 + topPadding);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 翻转
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processTurn(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		int topPadding = 0;
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.translate(0, 0, width * 0.5f * (float) Math.sin(-shiftingX * Math.PI / width));
			camera.rotateY(shiftingX * 180 / width);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2+width*screen+loopShift-shiftingX, height / 2);
			canvas.concat(matrix);
			if (-shiftingX > width * 0.5f) {
				canvas.clipRect(0, 0, 0, 0);
			}
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.translate(0, 0, width * 0.5f * (float) Math.sin(shiftingX * Math.PI / width));
			camera.rotateY(-180 - (width - shiftingX) * 180 / width);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2+width*screen+loopShift-shiftingX, height / 2);
			canvas.concat(matrix);
			if ((width - shiftingX) <= width * 0.5f) {
				canvas.clipRect(0, 0, 0, 0);
			}
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 风车
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processWindower(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View view = workspace.getChildAt(screen);
		int width = view.getWidth();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			float mDegree = 25.0f * shiftingX / width;
			camera.rotateZ(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX, 0);
			matrix.preTranslate(-screen * width, getTop(workspace, canvas));
			matrix.postTranslate( screen * width- shiftingX+loopShift, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, view, drawingTime);
		} else {
			// 画右屏
			float mDegree = 25.0f * shiftingX / width;
			camera.rotateZ(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX - width, 0);
			matrix.preTranslate(-screen * width, getTop(workspace, canvas));
			matrix.postTranslate(  screen * width+width - shiftingX+loopShift, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, view, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 褶皱内
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processDrapeInside(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		float shiftingX = 0;
		int screenWidth;
		int screenHeight;
		float mDegree = 0;
		View view = workspace.getChildAt(screen);
		screenWidth = view.getWidth();
		screenHeight = view.getHeight();
		canvas.save();
		float alpha = 0;
		int drawAlpha = 0;
		float z = 0;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		if (isLeft) {
			// 显示左边屏幕
			shiftingX = countShiftingX(workspace, screen, isLeft);
			alpha = 255 - (shiftingX > 0 ? shiftingX : -shiftingX) / screenWidth * 200;
			mDegree = 45.0f * shiftingX / screenWidth;
			z = -(float) (screenWidth * Math.sin(mDegree * Math.PI / 180f));
			camera.translate(0, 0, z);
			camera.rotateY(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * screenWidth - screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.2f, 0);
			matrix.postTranslate(screenWidth+screen*screenWidth+loopShift-shiftingX, screenHeight / 2 + getTop(workspace, canvas));
			canvas.concat(matrix);
			// Drawable drawable =
			// workspace.getResources().getDrawable(com.bitants.launcher.R.drawable.edit_screen_bg);
			Drawable drawable = BitmapWeakReferences.getInstance().getDrapeInsideBg(view.getResources());
			drawable.setBounds(screen * screenWidth, view.getTop(), screen * screenWidth + screenWidth, view.getBottom());
			drawAlpha = (int) (255 * (shiftingX > 0 ? shiftingX : -shiftingX) / (screenWidth / 2f));
			drawable.setAlpha(drawAlpha < 255 ? drawAlpha : 255);
			drawable.draw(canvas);
			canvas.saveLayerAlpha(screen * screenWidth, 0, screen * screenWidth + screenWidth, canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, view, drawingTime);
			canvas.restore();
		} else {
			shiftingX = countShiftingX(workspace, screen, isLeft);
			alpha = 255 - (shiftingX > 0 ? shiftingX : -shiftingX) / screenWidth * 200;
			mDegree = 45.0f * shiftingX / screenWidth;
			z = (float) (screenWidth * Math.sin(mDegree * Math.PI / 180f));
			camera.translate(0, 0, z);
			camera.rotateY(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.2f, 0);
			matrix.postTranslate(screen*screenWidth+loopShift-shiftingX, screenHeight / 2 + getTop(workspace, canvas));
			canvas.concat(matrix);
			// Drawable drawable =
			// workspace.getResources().getDrawable(com.bitants.launcher.R.drawable.edit_screen_bg);
			Drawable drawable = BitmapWeakReferences.getInstance().getDrapeInsideBg(view.getResources());
			drawable.setBounds(screen * screenWidth, view.getTop(), screen * screenWidth + screenWidth, view.getBottom());
			drawAlpha = (int) (255 * (shiftingX > 0 ? shiftingX : -shiftingX) / (screenWidth / 2f));
			drawable.setAlpha(drawAlpha < 255 ? drawAlpha : 255);
			drawable.draw(canvas);
			canvas.saveLayerAlpha(screen * screenWidth, 0, screen * screenWidth + screenWidth, canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, view, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}
	
	/**
	 * 
	 * <br>Description:LG褶皱内特效
	 * <br>Author:zhenghonglin
	 * <br>Date:2014-1-6下午03:31:45
	 */
	private void processLGDrapeInside(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View view = workspace.getChildAt(screen);
		int screenWidth = view.getWidth();
		int screenHeight = view.getHeight();
		int topPadding = getTop(workspace,canvas);

		float shiftingX = countShiftingX(workspace, screen, isLeft);
		float mAngle = (float) ((-Math.sin(Math.abs(shiftingX)/screenWidth*Math.PI)+1)*30 + 8*Math.sin(Math.abs(shiftingX)/screenWidth*Math.PI));
//		float mAngle = (float) ((-Math.sin(Math.abs(shiftingX)/screenWidth*Math.PI)+1)*30 + 8);
		float mDegree = (float) (mAngle * Math.sin(  shiftingX / screenWidth * Math.PI / 2 ));
		float alpha = 255 - Math.abs(shiftingX)/ screenWidth * 100;
		float z = (float) ( (screenWidth -Math.abs(shiftingX))* Math.sin(Math.abs(mDegree) * Math.PI / 180f));
		
		canvas.save();
		camera.translate(0, 0, z);
		camera.rotateY(-mDegree);
		camera.getMatrix(matrix);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		if (isLeft) {
			// 显示左边屏幕
			matrix.preTranslate(- screenWidth, -screenHeight / 2);
			matrix.postTranslate(screenWidth + shiftingX , screenHeight / 2 + topPadding);
		} else {
			matrix.preTranslate(0, -screenHeight / 2);
			matrix.postTranslate(shiftingX , screenHeight / 2 + topPadding);
		}
		matrix.postTranslate(loopShift+screen*view.getWidth()-shiftingX, 0);
		canvas.concat(matrix);
		canvas.saveLayerAlpha(0, 0,canvas.getWidth(), canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
		view.draw(canvas);
		canvas.restore();
		canvas.restore();
	}

	/**
	 * 褶皱
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processDrapeOutside(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		float shiftingX = 0;
		int screenWidth;
		int screenHeight;
		float mDegree = 0;
		View view = workspace.getChildAt(screen);
		screenWidth = view.getWidth();
		screenHeight = view.getHeight();
		canvas.save();
		float alpha = 0;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		if (isLeft) {
			// 显示左边屏幕
			shiftingX = countShiftingX(workspace, screen, isLeft);
			alpha = 255 - (shiftingX > 0 ? shiftingX : -shiftingX) / screenWidth * 200;
			mDegree = 45.0f * shiftingX / screenWidth;
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * screenWidth - screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.3f, 0);
			matrix.postTranslate(screenWidth+screen*screenWidth+loopShift-shiftingX, screenHeight / 2 + getTop(workspace, canvas));
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * screenWidth, 0, screen * screenWidth + screenWidth, canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, view, drawingTime);
			canvas.restore();
		} else {
			shiftingX = countShiftingX(workspace, screen, isLeft);
			alpha = 255 - (shiftingX > 0 ? shiftingX : -shiftingX) / screenWidth * 200;
			mDegree = 45.0f * shiftingX / screenWidth;
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.3f, 0);
			matrix.postTranslate(0+screen*screenWidth+loopShift-shiftingX, screenHeight / 2 + getTop(workspace, canvas));
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * screenWidth, 0, screen * screenWidth + screenWidth, canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, view, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}

	
	/**
	 * 百叶窗
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processShutter(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = layout.getWidth() / 2;
		dest[1] = layout.getHeight() / 2;
		center[1] =   layout.getHeight() / 2;
		int topPadding =  getTop(workspace, canvas);
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		int loopShift=countLoopShift(workspace,screen,isLeft);
		canvas.save();
		if (isLeft) {
			canvas.save();
			if (shiftingX >= -layout.getWidth() / 2) {
				for (int y = 0; y < rowNum; y++) {
					for (int x = 0; x < columnNum; x++) {
						camera.save();
						matrix.setTranslate(0, 0);
						// View childView = layout.getChildViewByIndex(y *
						// columnNum + x);
						View childView = layout.getChildAt(y * columnNum + x);
						if (childView == null)
							continue;
						location[0] = childView.getLeft();
						location[1] = childView.getTop();
						center[0] = location[0] + childView.getWidth() / 2;
						float mDegree = -shiftingX * 180.0f / layout.getWidth();
						camera.translate(0, 0, -shiftingX * 0.5f);
						camera.rotateY(mDegree);
						camera.getMatrix(matrix);
						matrix.preTranslate(location[0], location[1]);
						matrix.preTranslate(-center[0], -center[1] + topPadding);
						matrix.postTranslate(center[0]+screen*layout.getWidth()+loopShift-shiftingX, center[1]);
						canvas.save();
						canvas.concat(matrix);
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
						drawAtCanvas(canvas, childView);
						canvas.restore();
						camera.restore();
					}
				}
			}
			canvas.restore();
		} else {
			canvas.save();
			if (shiftingX <= layout.getWidth() / 2) {
				for (int y = 0; y < rowNum; y++) {
					for (int x = 0; x < columnNum; x++) {
						camera.save();
						matrix.setTranslate(0, 0);
						// View childView = layout.getChildViewByIndex(y *
						// columnNum + x);
						View childView = layout.getChildAt(y * columnNum + x);
						if (childView == null)
							continue;
						location[0] = childView.getLeft();
						location[1] = childView.getTop();
						center[0] = location[0] + childView.getWidth() / 2;
						float mDegree = -shiftingX * 180.0f / layout.getWidth();
						camera.translate(0, 0, shiftingX * 0.5f);
						camera.rotateY(mDegree);
						camera.getMatrix(matrix);
						matrix.preTranslate(location[0], location[1]);
						matrix.preTranslate(-center[0], -center[1] + topPadding);
						matrix.postTranslate(center[0]+screen*layout.getWidth()+loopShift-shiftingX, center[1]);
						canvas.save();
						canvas.concat(matrix);
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
						drawAtCanvas(canvas, childView);
						canvas.restore();
						camera.restore();
					}
				}
			}
			canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 弦 新实现
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processChord(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int width = layout.getWidth();
		int topPadding =  getTop(workspace, canvas);
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = layout.getWidth() / 2;
		dest[1] = layout.getHeight() / 2 + topPadding;
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		center[1] = topPadding + layout.getHeight() / 2;
		canvas.save();
		int loopShift=countLoopShift(workspace,screen,isLeft);
		if (isLeft) {
			
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					float mDegree = 0;
					int distance = width + shiftingX;
					if (distance <= childView.getRight()) {
						mDegree = (childView.getRight() - distance) * 180.0f / childView.getWidth();
						if (mDegree >= 90)
							continue;
						camera.rotateY(-mDegree);
						camera.getMatrix(matrix);
						matrix.preTranslate(location[0], location[1]);
						matrix.preTranslate(-center[0], -center[1] + topPadding);
						matrix.postTranslate(center[0], center[1]);
					} else {
						matrix.preTranslate(location[0], location[1] + topPadding);
					}
					matrix.postTranslate(loopShift+screen*width-shiftingX, 0);
					canvas.save();
					canvas.concat(matrix);
					if (y == (rowNum - 1)) {
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					}
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		} else {
		
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					float mDegree = 0;
					int distance = shiftingX;
					if (distance <= childView.getRight()) {
						mDegree = (childView.getRight() - distance) * 180.0f / childView.getWidth();
						if (mDegree <= 90)
							continue;
						else if (mDegree > 90 && mDegree < 180) {
							camera.rotateY(-mDegree - 180);
							camera.getMatrix(matrix);
							matrix.preTranslate(location[0], location[1]);
							matrix.preTranslate(-center[0], -center[1] + topPadding);
							matrix.postTranslate(center[0], center[1]);
						} else {
							matrix.preTranslate(location[0], location[1] + topPadding);
						}
					} else {
						continue;
					}
					matrix.postTranslate(loopShift+screen*width-shiftingX, 0);
					canvas.save();
					canvas.concat(matrix);
					if (y == (rowNum - 1)) {
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					}
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
			
		}
		canvas.restore();
	}

	/**
	 * 双子星 新实现
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processBinaryStar(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int width = workspace.getWidth();
		int topPadding =  getTop(workspace, canvas);
		float cellWidth = getCellWidth(workspace);
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = layout.getWidth() / 2;
		dest[1] = layout.getHeight() / 2;
		canvas.save();
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < rowNum; y++) {
			for (int x = 0; x < columnNum; x++) {
				camera.save();
				matrix.setTranslate(0, 0);
				View childView = layout.getChildAt(y * columnNum + x);
				if (childView == null)
					continue;
				// view存在
				location[0] = childView.getLeft();
				location[1] = childView.getTop();
				center[0] = location[0] + childView.getWidth() / 2;
				center[1] = location[1] + childView.getHeight() / 2;
				float offsetX = 0;
				float offsetY = 0;
				float scale = 1;
				if (isLeft) {
					if (shiftingX < -layout.getWidth() / 2) {
						offsetX = (dest[0] - (center[0])) * 1.0f + (layout.getWidth() / 2 + shiftingX);
						offsetY = (dest[1] - center[1]) * 1.0f;
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						offsetX = (dest[0] - (center[0])) * -shiftingX * 1.0f / (layout.getWidth() * 0.5f);
						offsetY = (dest[1] - center[1]) * -shiftingX * 1.0f / (layout.getWidth() * 0.5f);
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * -shiftingX / (width / 2) + 1;
						}
					}
				} else {
					if (shiftingX > layout.getWidth() / 2) {
						offsetX = (dest[0] - (center[0])) * 1.0f + (shiftingX - layout.getWidth() / 2);
						offsetY = (dest[1] - center[1]) * 1.0f;
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						offsetX = (dest[0] - (center[0])) * shiftingX * 1.0f / (layout.getWidth() * 0.5f);
						offsetY = (dest[1] - center[1]) * shiftingX * 1.0f / (layout.getWidth() * 0.5f);
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * shiftingX / (width / 2) + 1;
						}
					}
				}
				camera.translate(offsetX, -offsetY, 0);
				camera.getMatrix(matrix);
				matrix.preScale(scale, scale);
				matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2 + topPadding);
				matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
				matrix.postTranslate(shiftingX + location[0]-shiftingX+screen*width+loopShift, location[1]);
				canvas.save();
				canvas.concat(matrix);
				canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
				drawAtCanvas(canvas, childView);
				camera.restore();
				canvas.restore();
			}
		}
		canvas.restore();
	}

	/**
	 * 车轮 新实现
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processWheel(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int width = layout.getWidth();
		int topPadding =  getTop(workspace, canvas);
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = layout.getWidth() / 2;
		dest[1] = layout.getHeight() / 2;
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		float cellWidth = getCellWidth(workspace);
		float cellHeight = getCellHeight(workspace);
		// 圆的半径
		float R = layout.getWidth() / 2 - cellHeight / 2;
		// 圆上第一个点的坐标
		float x1 = layout.getWidth() - cellHeight / 2;
		float y1 = layout.getHeight() / 2;
		double angle = 0;
		// angle = 2 * Math.PI / (columnNum * rowNum);
		angle = 2 * Math.PI / layout.getChildCount();
		// 第一个点旋转到位的时候 旋转的角度
		double firstAngle = Math.PI / 2;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		canvas.save();
		if (isLeft) {
			canvas.save();
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					center[1] = location[1] + childView.getHeight() / 2;
					// 圆上第(x*y+1)点的坐标
					double offsetAngle = angle * (y * columnNum + x);
					double cxn = x1 - R * (1 - Math.cos(offsetAngle));
					double cyn = y1 - R * Math.sin(offsetAngle);
					// 当旋转到圆上的时候 自身旋转的角度
					double rotateAngle = firstAngle + offsetAngle;
					// 当前屏上 第(x*y+1)点的中心坐标
					float xn = center[0];
					float yn = center[1];
					float scale = 1;
					// 屏幕上的点移动到圆上的点 位移加旋转过程 移动半个屏幕
					if (shiftingX < -layout.getWidth() / 2) {
						camera.translate((float) (cxn - xn), -(float) (cyn - yn), 0);
						camera.rotateZ((float) (rotateAngle * 180.0 / Math.PI));
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						camera.translate(-shiftingX * (float) (cxn - xn) / (layout.getWidth() * 0.5f), shiftingX * (float) (cyn - yn) / (layout.getWidth() * 0.5f), 0);
						camera.rotateZ(-shiftingX * (float) (rotateAngle * 180.0 / Math.PI) / (layout.getWidth() * 0.5f));
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * -shiftingX / (width / 2) + 1;
						}
					}
					camera.getMatrix(matrix);
					if (shiftingX < -layout.getWidth() / 2) {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
						matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
						matrix.postTranslate(location[0], location[1]);
						matrix.postRotate((shiftingX + layout.getWidth() / 2) * 90 / (layout.getWidth() * 0.5f), x1 - R, y1);
						matrix.postTranslate(shiftingX, topPadding);
					} else {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
						matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
						matrix.postTranslate(shiftingX + location[0], location[1] + topPadding);
					}
					matrix.postTranslate(loopShift-shiftingX+screen*width, 0);
					canvas.save();
					canvas.concat(matrix);
					if (y == (rowNum - 1)) {
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					}
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
			canvas.restore();
		} else {
			canvas.save();
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					center[1] = location[1] + childView.getHeight() / 2;
					// 圆上第(x*y+1)点的坐标
					double offsetAngle = angle * (y * columnNum + x);
					double cxn = x1 - R * (1 - Math.cos(offsetAngle));
					double cyn = y1 - R * Math.sin(offsetAngle);
					// 当旋转到圆上的时候 自身旋转的角度
					double rotateAngle = firstAngle + offsetAngle;
					// 当前屏上 第(x*y+1)点的中心坐标
					float xn = center[0];
					float yn = center[1];
					float scale = 1;
					// 屏幕上的点移动到圆上的点 位移加旋转过程 移动半个屏幕
					if (shiftingX > layout.getWidth() / 2) {
						camera.translate((float) (cxn - xn), -(float) (cyn - yn), 0);
						camera.rotateZ((float) (rotateAngle * 180.0 / Math.PI));
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						camera.translate(shiftingX * (float) (cxn - xn) / (layout.getWidth() * 0.5f), -shiftingX * (float) (cyn - yn) / (layout.getWidth() * 0.5f), 0);
						camera.rotateZ(shiftingX * (float) (rotateAngle * 180.0 / Math.PI) / (layout.getWidth() * 0.5f));
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * shiftingX / (width / 2) + 1;
						}
					}
					camera.getMatrix(matrix);
					if (shiftingX > layout.getWidth() / 2) {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
						matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
						matrix.postTranslate(location[0], location[1]);
						matrix.postRotate((shiftingX - layout.getWidth() / 2) * 90 / (layout.getWidth() * 0.5f), x1 - R, y1);
						matrix.postTranslate(shiftingX, topPadding);
					} else {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
						matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
						matrix.postTranslate(shiftingX + location[0], location[1] + topPadding);
					}
					matrix.postTranslate(loopShift-shiftingX+screen*width, 0);
					canvas.save();
					canvas.concat(matrix);
					if (y == (rowNum - 1)) {
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					}
					drawAtCanvas(canvas, childView);
					camera.restore();
					canvas.restore();
				}
			}
			canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 圆柱体
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void processCylinder(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		if (workspace.getTouchState() != 0)// 非放手状态下画正常的圆
		{
			drawCylinder(canvas, screen, drawingTime, workspace, isLeft);
		} else {
			CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
			if (layout.getLayoutNum() == workspace.getCurrentScreen()) {
				float shiftingX = countShiftingX(workspace, screen, isLeft);
				if (Math.abs(shiftingX) <= layout.getWidth() / 8) {
					drawCylinderAnimation(canvas, screen, drawingTime, workspace, isLeft);
				} else {
					drawCylinder(canvas, screen, drawingTime, workspace, isLeft);
				}
			}
		}
	}

	/**
	 * 圆柱
	 * @param canvas
	 * @param screen
	 * @param drawingTime
	 * @param workspace
	 * @param isLeft
	 */
	private void drawCylinder(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int width = layout.getWidth();
		int location[] = new int[2];
		int center[] = new int[2];
		ICommonData data;
		data = workspace.getData(screen);
		int rowNum = data.getRowNum();
		int columnNum = data.getColumnNum();
		float cellWidth = getCellWidth(workspace);
		float radius = width / 2 + cellWidth * 0.5f;
		int centerX = screen*width+width / 2;
		int centerY = layout.getHeight() / 2 ;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		float n = columnNum * 2;
		float perAngle = 360 / n;
		float firstRotate = (90 - perAngle / 2) - (shiftingX * 1.0f / width) * 180;
		canvas.save();
		boolean isWidget=true;
		for (int y = 0; y < rowNum; y++) {
			for (int x = 0; x < columnNum; x++) {
				camera.save();
				matrix.setTranslate(0, 0);
				View childView = layout.getChildAt(y * columnNum + x);
				if (childView == null)
					continue;
				location[0] = childView.getLeft();
				location[1] = childView.getTop();
				center[0] = location[0] + childView.getWidth() / 2;
				center[1] = location[1] + childView.getHeight() / 2;
				float mRotate = firstRotate - perAngle * x;
				camera.translate(0, 0, radius);
				camera.rotateY(-mRotate);
				camera.translate(0, 0, -radius);
				camera.getMatrix(matrix);
				matrix.preTranslate(location[0], location[1]);
				matrix.preTranslate(-center[0], -layout.getHeight() / 2);
				matrix.postTranslate(centerX+loopShift-shiftingX, centerY);
				canvas.save();
				canvas.concat(matrix);
				mRect.left=0;
				mRect.top=0;
				mRect.right=childView.getWidth();
				mRect.bottom=childView.getHeight();
				isWidget=isWidget(childView);
				if (mRotate < -90 || mRotate > 90 && workspace.getTouchState() != 0) {
					//drawAtCanvas(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)));
					drawAtCanvasEx(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)), mRect, isWidget);
				} else {
					drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
					//drawAtCanvas(canvas, childView);
				}
				camera.restore();
				canvas.restore();
			}
		}
		canvas.restore();
	}

	private void drawCylinderAnimation(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		Camera mCamera = camera;
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int cellWidth = getCellWidth(workspace);
		float shiftingX = countShiftingX(workspace, screen, isLeft);
		float fRoteY = 0;// Y轴上的旋转角度
		float R = layout.getWidth() / 2 + cellWidth * 0.5f;
		;
		int rowNum = 0;
		int columnNum = 0;
		float intervalColAngle = 0;
		float firstColRotate = 0;
		int cylinderCenterX = layout.getWidth() / 2;
		int cylinderCenterY = layout.getHeight() / 2;
		float finishRate = CountFinishRate(shiftingX, layout.getWidth());
		float viewCenterY = 0;
		float viewCenterX = 0;
		int location[] = new int[2];
		//float offsetY = getTop(workspace,canvas);
		ICommonData data;
		data = workspace.getData(screen);
		rowNum = data.getRowNum();
		columnNum = data.getColumnNum();
		intervalColAngle = 360f / (columnNum * 2f); // Ｙ轴上的相邻两片图像的间隔角度
		firstColRotate = 90 - intervalColAngle / 2f;
		Point mPositionArray[];
		mPositionArray = new Point[rowNum * columnNum];
		for (int i = 0; i < mPositionArray.length; i++) {
			mPositionArray[i] = new Point();
		}
		int loopShift=countLoopShift(workspace,screen,isLeft);
		float moveX = 0;// 要移回到原来的位置X的距离
		float moveY = 0;// 要移回到原来的位置Y的距离
		for (int y = 0; y < rowNum; y++) {
			for (int x = 0; x < columnNum; x++) {
				View childView = layout.getChildAt(y * columnNum + x);
				if (childView == null) {
					continue;
				}
				viewCenterX = childView.getWidth() / 2;
				viewCenterY = childView.getHeight() / 2;
				location[0] = childView.getLeft() + childView.getWidth() / 2;
				location[1] = childView.getTop() + childView.getHeight() / 2;
				// 求当前完成比率下的view的中心点所在的位置
				fRoteY = GetAngle((firstColRotate - intervalColAngle * x) * finishRate);
				matrix.setTranslate(0, 0);
				mCamera.save();
				mCamera.translate(0, 0, R);
				mCamera.rotateY(-fRoteY);
				mCamera.translate(0, 0, -R);
				mCamera.getMatrix(matrix);
				mCamera.restore();
				matrix.preTranslate(-viewCenterX, -(layout.getHeight() / 2 - childView.getTop()));
				Point currentPoint = new Point();
				CountPoint(matrix, viewCenterX, viewCenterY, currentPoint);
				// 求100%完成比率时view中心点所在的位置
				fRoteY = GetAngle(firstColRotate - intervalColAngle * x);
				matrixTemp.setTranslate(0, 0);
				mCamera.save();
				mCamera.translate(0, 0, R);
				mCamera.rotateY(-fRoteY);
				mCamera.translate(0, 0, -R);
				mCamera.getMatrix(matrixTemp);
				mCamera.restore();
				matrixTemp.preTranslate(-viewCenterX, -(layout.getHeight() / 2 - childView.getTop()));
				matrixTemp.postTranslate(cylinderCenterX, cylinderCenterY);
				Point finishPoint = new Point();
				CountPoint(matrixTemp, viewCenterX, viewCenterY, finishPoint);
				moveX = location[0] + (finishPoint.x - location[0]) * finishRate;
				moveY = location[1] + (finishPoint.y - location[1]) * finishRate;
				matrix.postTranslate(-currentPoint.x + moveX+loopShift-shiftingX+screen*layout.getWidth(), -currentPoint.y + moveY);
				canvas.save();
				canvas.concat(matrix);
				//canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
				mRect.left=0;
				mRect.top=0;
				mRect.right=childView.getWidth();
				mRect.bottom=childView.getHeight();
				drawAtCanvasEx(canvas, childView, 255, mRect, isWidget(childView));
				canvas.restore();
				//drawAtCanvas(canvas, childView);
			}// end for
		}// end for
	}

	// 圆
	private void processGlobal(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		if (workspace.getTouchState() == 2)// 非放手状态下画正常的圆
		{
			drawGlobal(canvas, screen, drawingTime, workspace, isLeft);
		} else {
			CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
			if (layout.getLayoutNum() == workspace.getCurrentScreen()) {
				float shiftingX = countShiftingX(workspace, screen, isLeft);
				if (Math.abs(shiftingX) <= layout.getWidth() / 8) {
					drawGlobeAnimation(canvas, screen, drawingTime, workspace, isLeft);
				} else {
					drawGlobal(canvas, screen, drawingTime, workspace, isLeft);
				}
			}
		}
	}

	// 圆
	private void drawGlobal(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		float shiftingX = 0;
		float scaleY = 0;
		float scaleX = 0;
		View view = workspace.getChildAt(screen);
		shiftingX = countShiftingX(workspace, screen, isLeft);
		CommonLayout layout = (CommonLayout) view;
		int cellWidth = getCellWidth(workspace);
		int cellHeight = getCellHeight(workspace);
		float radius = layout.getWidth() / 2 + cellWidth * 0.5f;
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		float col = columnNum * 2;
		// 列绕y轴转的角度
		float colAngle = 360 / col;
		// 行绕x轴转的角度
		float rowAngle = 90 / (rowNum - 1);
		float firstColRotate = (90 - colAngle / 2) - (shiftingX / layout.getWidth()) * 180;
		float firstRowRotate = 45;
		float centerX = layout.getWidth() / 2; // 圆心Ｘ
		float centerY = layout.getHeight() / 2;// 圆心Ｙ
		float viewCenterY = 0;
		float viewCenterX = 0;
		int topPadding = getTop(workspace, canvas);
		canvas.save();
		boolean isWidget=false;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < rowNum; y++) {
			for (int x = 0; x < columnNum; x++) {
				View childView = layout.getChildAt(y * columnNum + x);
				if (childView == null) {
					continue;
				}
				if (isWidget(childView)) {
					viewCenterX = childView.getWidth() / 2;
					viewCenterY = childView.getHeight() / 2;
					scaleX = (float) cellWidth / childView.getWidth();
					scaleY = (float) cellHeight / childView.getHeight();
				} else {
					viewCenterX = childView.getWidth() / 2;
					viewCenterY = childView.getHeight() / 2;
					scaleX = 1;
					scaleY = 1;
				}
				matrix.setTranslate(0, 0);
				camera.save();
				float mRotate = firstColRotate - colAngle * x;
				float mRowRotate = firstRowRotate - rowAngle * y;
				camera.translate(0, 0, radius);
				camera.rotateX(45 * workspace.getFingerOffsetY() / (layout.getHeight() * 0.5f));
				camera.rotateY(-mRotate);
				camera.rotateX(mRowRotate);
				camera.translate(0, 0, -radius);
				camera.getMatrix(matrix);
				matrix.preScale(scaleX, scaleY);
				matrix.preTranslate(-viewCenterX, -viewCenterY);
				matrix.postTranslate(centerX+loopShift+screen*layout.getWidth()-shiftingX, centerY + topPadding);
				canvas.save();
				canvas.concat(matrix);
				if (isWidget(childView)) {
					//canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					mRect.top=0;
					mRect.left=0;
					mRect.right=childView.getWidth();
					mRect.bottom=childView.getHeight();
					isWidget=true;
				} else {
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
					mRect.top=0;
					mRect.left=0;
					mRect.right=cellWidth;
					mRect.bottom=cellHeight;
					isWidget=false;
				}
				if (mRotate < -90 || mRotate > 90) {
					drawAtCanvasEx(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)), mRect, isWidget);
					//drawAtCanvas(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)));
				} else {
					drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
					//drawAtCanvas(canvas, childView);
				}
				camera.restore();
				canvas.restore();
			}
		}
		canvas.restore();
	}

	private void drawGlobeAnimation(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		Camera mCamera = camera;
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int cellWidth = getCellWidth(workspace);
		int cellHeight = getCellHeight(workspace);
		float shiftingX = countShiftingX(workspace, screen, isLeft);
		float fRoteY = 0;// Y轴上的旋转角度
		float fRoteX = 0;// 在X轴上的旋转角度
		float scaleY = 0;// 为了把每个长方形小块变成正方形小块，要把Y轴缩放
		float scaleX = 0;// 为了把每个长方形小块变成正方形小块，要把X轴缩放
		float R = layout.getWidth() / 2 + cellWidth * 0.5f;
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		float intervalColAngle = 360f / (columnNum * 2f); // Ｙ轴上的相邻两片图像的间隔角度
		float intervalRowAngle = 90.0f / (rowNum - 1);
		float firstColRotate = 0;
		float firstRowRotate = -45;
		int nGobleCentX = layout.getWidth() / 2;
		int nGobleCentY = layout.getHeight() / 2;
		int row = 0;
		int column = 0;
		float finishRate = CountFinishRate(shiftingX, layout.getWidth());
		// finishRate=0;
		float viewCenterY = 0;
		float viewCenterX = 0;
		int location[] = new int[2];
		row = workspace.getList().get(0).getRowNum();
		column = workspace.getList().get(0).getColumnNum();
		firstColRotate = 90 - intervalColAngle / 2f;
		Point mPositionArray[];
		mPositionArray = new Point[row * column];
		for (int i = 0; i < mPositionArray.length; i++) {
			mPositionArray[i] = new Point();
		}
		CountGlobePosition(row, column, R, nGobleCentX, nGobleCentY, mPositionArray);
		int topPadding =getTop(workspace, canvas);
		boolean isWidget;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < column; x++) {
				View childView = layout.getChildAt(y * column + x);
				if (childView == null) {
					continue;
				}
				if (isWidget(childView)) {
					scaleX = (float) cellWidth / childView.getWidth();
					scaleY = (float) cellHeight / childView.getHeight();
					scaleX = scaleX + (1 - scaleX) * (1 - finishRate);
					scaleY = scaleY + (1 - scaleY) * (1 - finishRate);
				} else {
					scaleX = 1;
					scaleY = 1;
				}
				viewCenterX = childView.getWidth() / 2;
				viewCenterY = childView.getHeight() / 2;
				location[0] = childView.getLeft() + childView.getWidth() / 2;
				location[1] = childView.getTop() + childView.getHeight() / 2;
				fRoteY = GetAngle((firstColRotate - intervalColAngle * x) * finishRate);
				fRoteX = (firstRowRotate + intervalRowAngle * y) * finishRate;
				matrix.setTranslate(0, 0);
				float moveX = 0;// 要移回到原来的位置X的距离
				float moveY = 0;// 要移回到原来的位置Y的距离
				mCamera.save();
				mCamera.translate(0, 0, R);
				mCamera.rotateY(-fRoteY);
				mCamera.rotateX(-fRoteX);
				mCamera.translate(0, 0, -R);
				mCamera.getMatrix(matrix);
				mCamera.restore();
				matrix.preScale(scaleX, scaleY);
				matrix.preTranslate(-viewCenterX, -viewCenterY);
				//
				Point destPoint = new Point();
				CountPoint(matrix, viewCenterX, viewCenterY, destPoint);
				moveX = location[0] + (mPositionArray[y * columnNum + x].x - location[0]) * finishRate;
				moveY = location[1] + (mPositionArray[y * columnNum + x].y - location[1]) * finishRate;
				matrix.postTranslate(-destPoint.x + moveX+loopShift+screen*layout.getWidth()-shiftingX, -destPoint.y + moveY + topPadding);
				canvas.save();
				canvas.concat(matrix);
				if (isWidget(childView)) {
					mRect.left=0;
					mRect.top=0;
					mRect.right=childView.getWidth();
					mRect.bottom=childView.getHeight();
					isWidget=true;
				} else {
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
					mRect.left=0;
					mRect.top=0;
					mRect.right=cellWidth;
					mRect.bottom=cellHeight;
					isWidget=false;
				}
				drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
				canvas.restore();
				//drawAtCanvas(canvas, childView);
			}
		}
	}

	void CountGlobePosition(int nRow, int nColumn, float nRadius, int nGobleCentX, int nGobleCentY, Point positionArray[]) {
		Camera mCamera = camera;
		float fRoteX = 0;
		float fRoteY = 0;
		float intervalColAngle = 360f / (nColumn * 2f); // Ｙ轴上的相邻两片图像的间隔角度
		float intervalRowAngle = 90 / (nRow - 1);
		float firstColRotate = 90 - intervalColAngle / 2f;
		float firstRowRotate = -45;
		for (int i = 0; i < nRow * nColumn; i++) {
			fRoteY = firstColRotate - intervalColAngle * (i % nColumn);
			fRoteX = firstRowRotate + intervalRowAngle * (i / nColumn);
			mCamera.save();
			mCamera.translate(0, 0, nRadius);
			mCamera.rotateY(-fRoteY);
			mCamera.rotateX(-fRoteX);
			mCamera.translate(0, 0, -nRadius);
			mCamera.getMatrix(matrix);
			mCamera.restore();
			matrix.postTranslate(nGobleCentX, nGobleCentY);
			CountPoint(matrix, 0, 0, positionArray[i]);
		}
	}

	/* 将角度转换到　-360 到360之间 */
	float GetAngle(float Angle) {
		Angle = Angle % 360;
		Angle = (Angle + 360) % 360;
		return Angle;
	}

	/* 原点乘以矩阵得到目标点 */
	void CountPoint(Matrix matrix, float x, float y, Point destPoint) {
		float matrixArrar[] = new float[9];
		float z = 0;
		matrix.getValues(matrixArrar);
		destPoint.x = (int) (matrixArrar[0] * x + matrixArrar[1] * y + matrixArrar[2]);
		destPoint.y = (int) (matrixArrar[3] * x + matrixArrar[4] * y + matrixArrar[5]);
		z = (matrixArrar[6] * x + matrixArrar[7] * y + matrixArrar[8]);
		destPoint.x = (int) (destPoint.x / z);
		destPoint.y = (int) (destPoint.y / z);
	}

	/*
	 * shiftingX CellLayout 在x轴上的偏移 width CellLayout的宽度
	 */
	float CountFinishRate(float shiftingX, int width) {
		float finishRate = 0;
		if (Math.abs(shiftingX) > width / 8) {
			finishRate = 1;
		} else {
			finishRate = Math.abs(shiftingX) / (width / 4f);
		}
		// Log.e("CountFinishRate", "偏移="+shiftingX+"完成度="+finishRate);
		return finishRate;
	}

	private int getCellWidth(CommonSlidingView workspace) {
		return workspace.getList().get(0).getChildViewWidth();
	}

	private int getCellHeight(CommonSlidingView workspace) {
		return workspace.getList().get(0).getChildViewHeight();
	}

	private boolean isWidget(View child) {
		if (child == null)
			return false;
		CommonViewHolder viewHolder = (CommonViewHolder) child.getTag(R.id.common_view_holder);
//		if (viewHolder.item instanceof LauncherWidgetInfo) {
		if (viewHolder.item instanceof WidgetInfo) {
			return true;
		}
		return false;
	}

	// private static int getTop(CommonSlidingView parent,Canvas canvas){
	// if(GpuControler.isGpuOpened(canvas)){
	// return parent.getTop();
	// }
	// return 0;
	// }
    /*** 由zhouhq修改
     * 把setmarix方法改成concat方法时，暂时保留这个函数，等一段时间使用后
     * 确定concat，不再需要getTop时，再删除
     * 目前先保留它，但是永远返回0
     */
	private int getTop(CommonSlidingView parent, Canvas canvas) {
//		if (Build.VERSION.SDK_INT < 14) {
//			return 0;
//		}
//		if (GpuControler.isOpenGpuMore(parent)) {
//			return parent.getTop();
//		}
		return 0;
	}

//	private int getTop(CommonSlidingView parent, Canvas canvas) {
//		if (Build.VERSION.SDK_INT < 14) {
//			float arrayValue[] = new float[9];
//			canvas.getMatrix().getValues(arrayValue);
//			return (int) arrayValue[5];
//		}
//		if (GpuControler.isOpenGpu(parent)) {
//			return parent.getTop();
//		}
//		return 0;
//	}
	

	private int countShiftingX(CommonSlidingView workspace, int screen, boolean isLeftScreen) {
		int length = 0;
		if (workspace.getCurrentData().isLock()) {
			length = workspace.getCurrentData().getPageNum() * workspace.getWidth();
		} else {
			length = workspace.getWidth() * workspace.getChildCount();
		}
		if (isLeftScreen) {
			return (screen * workspace.getWidth() - workspace.getScrollX() - length) % length;
		} else {
			return (screen * workspace.getWidth() - workspace.getScrollX() + length) % length;
		}
	}

	private void processTornado(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		if (workspace.getTouchState() == 2)// 非放手状态下画正常的圆
		{
			drawTornado(canvas, screen, drawingTime, workspace, isLeft);
		} else {
			CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
			if (layout.getLayoutNum() == workspace.getCurrentScreen()) {
				float shiftingX = countShiftingX(workspace, screen, isLeft);
				if (Math.abs(shiftingX) <= layout.getWidth() / 8) {
					drawTornadoAnimation(canvas, screen, drawingTime, workspace, isLeft);
				} else {
					drawTornado(canvas, screen, drawingTime, workspace, isLeft);
				}
			}
		}
	}

	float CountTornadoPosition(int colCount, int currentColumn, float nRadius, float tornadoCenterX) {
		float fRoteY = 0;
		float intervalColAngle = 360f / (colCount * 2f); // Ｙ轴上的相邻两片图像的间隔角度
		float firstColRotate = 90 - intervalColAngle / 2f;
		fRoteY = firstColRotate - intervalColAngle * currentColumn;
		float coordinateX = (float) (-nRadius * Math.sin(fRoteY / 180f * Math.PI));
		return coordinateX + tornadoCenterX;
	}

	void drawTornado(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		float shiftingX = countShiftingX(workspace, screen, isLeft);
		float tornadoCenterX = layout.getWidth() / 2;
		float viewCenterY = 0;
		float viewCenterX = 0;
		float coordinateX = 0;
		float coordinateZ = 0;
		float R = 0;// 半径
		float tiltAngle = 20;
		float scaleY = 0;// 为了把每个长方形小块变成正方形小块，要把Y轴缩放
		float scaleX = 0;// 为了把每个长方形小块变成正方形小块，要把X轴缩放
		float rotateY = 0;
		int cellWidth = getCellWidth(workspace);
		int cellHeight = getCellHeight(workspace);
		int row = workspace.getList().get(0).getRowNum();
		int column = workspace.getList().get(0).getColumnNum();
		float col = column * 2;
		float colAngle = 360 / col;
		float firstColRotate = (90 - colAngle / 2) + ((float) -shiftingX / layout.getWidth()) * 180;
		float tanVlaue = (float) Math.tan(tiltAngle / 180f * Math.PI);
		int topPadding = getTop(workspace, canvas);
		canvas.save();
		boolean isWidget=true;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < column; x++) {
				View childView = layout.getChildAt(y * column + x);
				if (childView == null) {
					continue;
				}
				if (isWidget(childView)) {
					scaleX = (float) cellWidth / childView.getWidth();
					scaleY = (float) cellHeight / childView.getHeight();
				} else {
					scaleX = 1;
					scaleY = 1;
				}
				viewCenterX = childView.getWidth() / 2;
				viewCenterY = childView.getHeight() / 2;
				rotateY = GetAngle(firstColRotate - colAngle * x);
				R = (layout.getHeight() - childView.getTop() - viewCenterY) * tanVlaue;
				coordinateX = (float) (-R * Math.sin(-rotateY / 180f * Math.PI));
				// coordinateX=0
				coordinateZ = (float) (-R * Math.cos(-rotateY / 180f * Math.PI)) + layout.getWidth() * 0.5f;
				camera.save();
				camera.translate(-coordinateX, 0, coordinateZ);
				camera.rotateY(-rotateY);
				camera.rotateX(-tiltAngle);
				camera.getMatrix(matrix);
				camera.restore();
				matrix.preScale(scaleX, scaleY);
				matrix.preTranslate(-viewCenterX, -viewCenterY);
				matrix.postTranslate(tornadoCenterX+loopShift+screen*layout.getWidth()-shiftingX, topPadding + viewCenterY + childView.getTop());
				
				canvas.save();
				canvas.concat(matrix);
				if (isWidget(childView)) {
					mRect.left=0;
					mRect.top=0;
					mRect.right=childView.getWidth();
					mRect.bottom=childView.getHeight();
					isWidget=true;
					//canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
				} else {
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
					mRect.left=0;
					mRect.top=0;
					mRect.right=cellWidth;
					mRect.bottom=cellHeight;
					isWidget=false;
				}
				if (rotateY > 90 && rotateY < 270) {
					//drawAtCanvas(canvas, childView, 100);
					drawAtCanvasEx(canvas, childView, 200, mRect, isWidget);
				} else {
					drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
					//drawAtCanvas(canvas, childView);
				}
				canvas.restore();
			}// end for (int x = 0; x < layout.getViewColumnNum()
		}// endfor (int y = 0; y < layout.getViewRowNum()
		canvas.restore();
	}// end void processTornado(
		//

	private void drawTornadoAnimation(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		float shiftingX = countShiftingX(workspace, screen, isLeft);
		float rotateY = 0;// Y轴上的旋转角度
		float rotateX = 0;// 在X轴上的旋转角度
		float R = 0;
		int row = workspace.getList().get(0).getRowNum();
		int column = workspace.getList().get(0).getColumnNum();
		float intervalColAngle = 360f / (column * 2f); // Ｙ轴上的相邻两片图像的间隔角度
		float firstColRotate = 0;
		float moveX = 0;// 要移回到原来的位置X的距离
		float finishRate = 0;
		float viewCenterY = 0;
		float viewCenterX = 0;
		float tornadoCenterX = 0;
		float finalyPosition = 0;
		float tiltAngle = 20;
		int cellWidth = getCellWidth(workspace);
		int cellHeight = getCellHeight(workspace);
		finishRate = CountFinishRate(shiftingX, layout.getWidth());
		tornadoCenterX = layout.getWidth() / 2;
		firstColRotate = 90 - intervalColAngle / 2f;
		int topPadding = getTop(workspace, canvas);
		boolean isWidget=true;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < column; x++) {
				View childView = layout.getChildAt(y * column + x);
				if (childView == null) {
					continue;
				}
				if (isWidget(childView)) {
				} else {
					viewCenterX = childView.getWidth() / 2;
					viewCenterY = childView.getHeight() / 2;
				}
				rotateY = GetAngle((firstColRotate - intervalColAngle * x) * finishRate);
				rotateX = 20 * finishRate;
				matrix.setTranslate(0, 0);
				R = (layout.getHeight() - childView.getTop() - viewCenterY) * (float) Math.tan(tiltAngle / 180f * Math.PI);
				finalyPosition = CountTornadoPosition(column, x, R, tornadoCenterX);
				camera.save();
				camera.rotateY(-rotateY);
				camera.rotateX(-rotateX);
				camera.getMatrix(matrix);
				camera.restore();
				moveX = (childView.getLeft() + viewCenterX) + (finalyPosition - childView.getLeft() - viewCenterX) * finishRate;
				matrix.preTranslate(-viewCenterX, -viewCenterY);
				matrix.postTranslate(moveX+loopShift+screen*layout.getWidth()-shiftingX, topPadding + childView.getTop() + viewCenterY);
				canvas.save();
				canvas.concat(matrix);
				if (isWidget(childView)) {
					mRect.left=0;
					mRect.top=0;
					mRect.right=childView.getWidth();
					mRect.bottom=childView.getHeight();
					isWidget=true;
					//canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
				} else {
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
					mRect.left=0;
					mRect.top=0;
					mRect.right=cellWidth;
					mRect.bottom=cellHeight;
					isWidget=false;
				}
				//drawAtCanvas(canvas, childView);
				drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
				canvas.restore();
			}
		}
	}

	private void drawAtCanvas(Canvas canvas, View view) {
		// Bitmap bitmap = view.getDrawingCache();
		// if(bitmap != null){
		// canvas.drawBitmap(bitmap, new Matrix(), null);
		// }else{
		// view.draw(canvas);
		// }
		view.draw(canvas);
	}

	private void drawAtCanvas(Canvas canvas, View view, int alpha) {
		// Bitmap bitmap = view.getDrawingCache();
		// if(bitmap != null){
		// Paint paint = new Paint();
		// paint.setAlpha(alpha);
		// canvas.drawBitmap(bitmap, new Matrix(), paint);
		// }else{
		// canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(),
		// alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		// view.draw(canvas);
		// canvas.restore();
		// }
		canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		view.draw(canvas);
		canvas.restore();
	}

	private void ProcessTurntable(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		float shiftingX = 0;
		shiftingX = countShiftingX(workspace, screen, isLeft);
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int topPadding =  getTop(workspace, canvas);
		ICommonData data;
		data = workspace.getData(screen);
		int row = data.getRowNum();
		int column = data.getColumnNum();
		float viewCenterY = 0;
		float viewCenterX = 0;
		float degrees = shiftingX / layout.getWidth() * 180;
		canvas.save();
		float rate = 0;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < column; x++) {
				rate = shiftingX / layout.getWidth();
				View childView = layout.getChildAt(y * column + x);
				if (childView == null) {
					continue;
				}
				viewCenterX = childView.getWidth() / 2;
				viewCenterY = childView.getHeight() / 2;
				matrix.setTranslate(0, 0);
				if (x == 0 || x == column - 1 || y == 0 || y == row - 1) {
					float finalPostY = 0;
					float finalEndPostY = 0;
					for (int i = 0; i < 1; i++) {
						if (y == 0 && x != column - 1) {
							matrix.postTranslate(-layout.getWidth() * rate, 0);
							break;
						}
						if (x == 0) {
							matrix.postTranslate(0, layout.getHeight() * rate);
							finalPostY = childView.getTop() + layout.getHeight() * rate;
							finalPostY = finalPostY > 0 ? 0 : -finalPostY;
							finalEndPostY = childView.getBottom() + layout.getHeight() * rate;
							break;
						}
						if (x == column - 1 && y != row - 1) {
							matrix.postTranslate(0, -layout.getHeight() * rate);
							finalPostY = childView.getTop() - layout.getHeight() * rate;
							finalPostY = finalPostY > 0 ? 0 : -finalPostY;
							finalEndPostY = childView.getBottom() - layout.getHeight() * rate;
							break;
						}
						if (y == row - 1) {
							matrix.postTranslate(layout.getWidth() * rate, 0);
							break;
						}
					}
					int exceedValue = 0;
					if (finalEndPostY - layout.getHeight() > 0) {
						exceedValue = (int) (finalEndPostY - layout.getHeight());
					} else {
						exceedValue = 0;
					}
					matrix.postTranslate(childView.getLeft()+loopShift+screen*layout.getWidth()-shiftingX, childView.getTop() + topPadding);
					canvas.save();
					canvas.concat(matrix);
					canvas.clipRect(0, finalPostY, childView.getWidth(), childView.getHeight() - exceedValue, Region.Op.REPLACE);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				} else {
					if ((isLeft && shiftingX >= -layout.getWidth() / 2) || (!isLeft && shiftingX < layout.getWidth() / 2)) {
						camera.save();
						camera.rotateY(degrees);
						camera.getMatrix(matrix);
						camera.restore();
						matrix.preTranslate(-viewCenterX, -viewCenterY);
						matrix.postTranslate(childView.getLeft(), childView.getTop());
						matrix.postTranslate(viewCenterX+loopShift+screen*layout.getWidth()-shiftingX, viewCenterY + topPadding);
						canvas.save();
						canvas.concat(matrix);
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
						drawAtCanvas(canvas, childView);
						canvas.restore();
					}
				}
			}// for
		}// for
		canvas.restore();
	}// ProcessTurntable

	private void ProcessTransfer(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		ICommonData data;
		data = workspace.getData(screen);
		if (data == null) {
			return;
		}
		canvas.save();
		float shiftingX = 0;
		shiftingX = countShiftingX(workspace, screen, isLeft);
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int row = data.getRowNum();
		int column = data.getColumnNum();
		int topPadding =  getTop(workspace, canvas);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < column; x++) {
				View childView = layout.getChildAt(y * column + x);
				if (childView == null) {
					continue;
				}
				float moveDistanceX = 0;
				if (y % 2 == 0) {
					moveDistanceX = shiftingX;
				} else {
					moveDistanceX = -shiftingX;
				}
				matrix.setTranslate(0, 0);
				matrix.preTranslate(childView.getLeft() + moveDistanceX, childView.getTop() + topPadding);
				matrix.postTranslate(loopShift+screen*layout.getWidth()-shiftingX, 0);
				canvas.save();
				canvas.concat(matrix);
				canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
				drawAtCanvas(canvas, childView);
				canvas.restore();
			}// for
		}// for
		canvas.restore();
	}// ProcessTurntable
		// 贪吃蛇CommonSlidingView

	private void ProcessSnake(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		canvas.save();
		float shiftingX = 0;
		shiftingX = countShiftingX(workspace, screen, isLeft);
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		ICommonData data;
		data = workspace.getData(screen);
		int row = data.getRowNum();
		int column = data.getColumnNum();
		// float rate=Math.abs(shiftingX/layout.getWidth());
		float rate = -shiftingX / layout.getWidth();
		int rowPadding = data.getRowPadding();
		int columnPadding = data.getColumnPadding();
		int viewH = data.getActualChildViewHeight();
		int viewW = data.getActualChildViewWidth();
		float toppadding =  getTop(workspace, canvas) + rowPadding;
		int pathH = layout.getWidth() - viewW - columnPadding * 2;
		int pathV = viewH + rowPadding;
		int moveMaxDistance = pathH + (pathH + pathV) * (row - 1) + viewW + columnPadding;
		Point point = new Point();
		int loopShift=countLoopShift(workspace,screen,isLeft);
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < column; x++) {
				View childView = layout.getChildAt(y * column + x);
				if (childView == null) {
					continue;
				}
				matrix.setTranslate(0, 0);
				GetPost(childView, row, y, (int) (rate * moveMaxDistance), pathH, pathV, point, columnPadding);
				if (point.x != MY_INFINITE) {
					matrix.preTranslate(point.x + columnPadding, point.y + toppadding);
					matrix.postTranslate(loopShift+screen*layout.getWidth()-shiftingX, 0);
					canvas.save();
					canvas.concat(matrix);
					canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				}
			}// for
		}// for
		canvas.restore();
	}// ProcessTurntable


	public final int MY_INFINITE = -1000;
	/**
	 * 获取获取移动的距离 包含图标位置本身移动的距离和滑屏时实时图标应该移动的距离
	 * @param childView
	 * @param row
	 * @param pathH
	 * @param pathV
	 * @param realTimeMove
	 * @param leftPadding
	 */
	public int GetMoveDistance(View childView, int row, int pathH, int pathV, int realTimeMove, int leftPadding) {
		int moveDistance = 0;
		moveDistance = row * (pathH + pathV);
		if (row % 2 == 0) {
			moveDistance += pathH - childView.getLeft() + leftPadding;
		} else {
			moveDistance += childView.getLeft() - leftPadding;
		}
		return moveDistance + realTimeMove;
	}

	/**
	 * 通过最终移动的距离，计算所在的行中的位置 ，或者所在列的位置 列的可能性只有两种，一个在最左边，一个在最右边,如下图的两第竖线
	 * ___________ | |__________ | ___________|
	 * @param childView
	 * @param countY
	 * @param whichRow
	 * @param moveDistance
	 * @param pathH
	 * @param pathV
	 * @param point
	 * @param leftPadding
	 */
	public void GetPost(View childView, int countY, int whichRow, int moveDistance, int pathH, int pathV, Point point, int leftPadding) {
		int totalMoveD = 0;
		totalMoveD = GetMoveDistance(childView, whichRow, pathH, pathV, moveDistance, leftPadding);
		point.x = 0;
		point.y = 0;
		// 当totalMoveD为负时位置到达 小时 -1 行时，不显示图标
		if (totalMoveD <= -(pathH + pathV)) {
			point.x = MY_INFINITE;
			return;
		}
		int row = 0;
		int surplusDistance = 0;
		row = totalMoveD / (pathH + pathV);
		surplusDistance = totalMoveD % (pathH + pathV);
		// 偶数行,0 2 4 ...
		if (row % 2 == 0) {
			if (surplusDistance <= pathH)// 在行上
			{
				point.x = pathH - surplusDistance;
				point.y = row * pathV;
			} else // 在列上
			{
				if (row == countY - 1) {
					point.x = -(surplusDistance - pathH);
					point.y = row * pathV;
				} else {
					point.x = 0;
					point.y = row * pathV + surplusDistance - pathH;
				}
			}
		} else // 奇数行 ,1 3 5...
		{
			if (surplusDistance <= pathH)// 在行上
			{
				point.x = surplusDistance;
				point.y = row * pathV;
			} else // 在列上
			{
				if (row == countY - 1) {
					point.x = pathH + surplusDistance - pathH;
					point.y = row * pathV;
				} else {
					point.x = pathH;
					point.y = row * pathV + surplusDistance - pathH;
				}
			}
		}// end else 奇数
			// 超出的部分将画把点设置负的足够大
		if (row > countY - 1) {
			point.x = MY_INFINITE;
		}
	}
	/**隧道*/
	void processTimeTunnel(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft)
	{
		canvas.save();
		float shiftingX = 0;
		shiftingX = countShiftingX(workspace, screen, isLeft);
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		int topPadding =  getTop(workspace, canvas);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		
		if(isLeft)
		{
			float scale=1+0.5f*(-shiftingX/width);
			//Log.e("zhou", "scale"+scale+"m="+shiftingX+" w="+width);
			//scale=scale>1.5?1.5f:scale;
			int alpha=(int) (255*(1+  shiftingX / width));
			alpha=alpha>0?alpha:0;
			matrix.setTranslate(0, 0);
			matrix.preTranslate(-width , -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postScale(scale, scale);
			matrix.postTranslate(shiftingX*0.5f, 0);
			matrix.postTranslate(width+loopShift+screen*width-shiftingX, height / 2 + topPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, height + topPadding, alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		else {
			
			float scale=1-0.5f*(shiftingX/width);
			
			int alpha=(int) (255*(1-(shiftingX*1.2f) / width));
			alpha=alpha>0?alpha:0;
			matrix.setTranslate(0, 0);
			matrix.preTranslate(0, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postScale(scale, scale);
			matrix.postTranslate(screen*width+loopShift, height / 2 + topPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, height + topPadding, alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
			}
		canvas.restore();
		
		
	}
	
	
	/** 翻书 */
	void processOpenDoor(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {

		canvas.save();
		float shiftingX = 0;
		shiftingX = countShiftingX(workspace, screen, isLeft);
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		float topPadding = 0;
		float angle = 0;
		float z = 0;
		float scale = 0;
		// 当前的在屏幕的宽度应该为如下
		int loopShift = countLoopShift(workspace, screen, isLeft);
		if (isLeft) {
			matrix.setTranslate(0, 0);
			angle = 90 * shiftingX / width;

			camera.save();

			camera.rotateY(angle);
			camera.getMatrix(matrix);
			camera.restore();
			scale = 1 + 1f * shiftingX / width;
			matrix.preScale(scale, 1);
			matrix.preTranslate(0, -height * 0.5f);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, height * 0.5f + getTop(workspace, canvas));
			int alpha = (int) (255 * (1 + shiftingX / width));
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 1, screen * width + width, height + topPadding, alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();

		} else {

			matrix.setTranslate(0, 0);
			angle = 90 * shiftingX / width;
			camera.save();
			camera.rotateY(-angle);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(0, -height / 2);
			matrix.preTranslate(-screen * width - width, 0);
			matrix.postTranslate(width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, height / 2 + getTop(workspace, canvas));
			canvas.concat(matrix);
			int alpha = (int) (255 * (1 - shiftingX / width));
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width,height + topPadding, alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}
	Paint paint=new Paint();
	private Rect mRect=new Rect();

	private void drawAtCanvasEx(Canvas canvas, View view, int alpha, Rect rect, boolean isWidget) {
		if (!isWidget) {

			if (alpha != 255) {
				canvas.saveLayerAlpha(0, 0, view.getWidth(), view.getHeight(), alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
			}
			view.draw(canvas);
			if (alpha != 255) {
				canvas.restore();
			}
			return;
		}
		Bitmap bitmap = view.getDrawingCache();
		if (bitmap == null) {
			view.buildDrawingCache();
		}
		bitmap = view.getDrawingCache();
		if (bitmap != null) {
			paint.setAlpha(alpha);
			canvas.drawBitmap(bitmap, rect, rect, paint);
		} else {
			view.draw(canvas);
		}
	}
	private int countLoopShift(CommonSlidingView workspace, int screen, boolean isLeftScreen) {
		
		
		int scrollX=0;
		int firstScreen=0;
		int lastScreen=0;
		int screenCount = 0;
		if (workspace.getCurrentData().isLock()) {
			screenCount = workspace.getCurrentData().getPageNum();
			firstScreen=workspace.getDataPageInfo(workspace.getCurrentData())[0];
			lastScreen= workspace.getDataPageInfo(workspace.getCurrentData())[1] - 1;
			scrollX=workspace.getScrollX()-firstScreen*workspace.getWidth();
		} else {
			screenCount =  workspace.getChildCount();
			scrollX=workspace.getScrollX();
			firstScreen=0;
			lastScreen=screenCount-1;
		}
		if(scrollX<0)
		{
			//左屏
			if (isLeftScreen && (screen == lastScreen)) {
				// 左屏为最后一屏时
				return -screenCount * workspace.getWidth();

			} 
		}
	
		if(scrollX>0)
		{
			 if(!isLeftScreen && (screen == firstScreen)){
				// 右屏为第一屏时
					return screenCount * workspace.getWidth();
				
			}	
		}
		return 0;
	}
	
	
	// ========================================点心桌面特效======================================================
	/**
	 * 点心桌面特效 折扇
	 */
	private void processSquash(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX, 0);
			float sx = Math.abs((width + shiftingX)) * 1.0f / width;
			matrix.preScale(sx, 1.0f, width, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX - width, 0);
			float sx = Math.abs((shiftingX - width)) * 1.0f / width;
			matrix.preScale(sx, 1.0f);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width + loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 淡入淡出
	 */
	private void processCrossFade(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX - width, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width + loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 荷兰风车
	 */
	private void processWindMill(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			float degree = -Math.abs(shiftingX) * 15.0f / width;
			camera.rotateY(degree);
			// camera.translate(0, 0, width * 0.5f * (float) Math.sin(
			// Math.abs(degree) * Math.PI / 180));
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2 + loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		} else {
			// 画右屏
			float degree = -Math.abs(width - shiftingX) * 15 / width + 15;
			camera.rotateY(degree);
			// camera.translate(0, 0, width * 0.5f * (float) Math.sin(
			// Math.abs(degree) * Math.PI / 180));
			camera.getMatrix(matrix);
			matrix.preTranslate(-width / 2, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(-shiftingX + width / 2 + loopShift + screen * width, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 时空穿越
	 */
	private void processPageZoom(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			float sx = 1 + Math.abs(shiftingX) * 1.0f / width;
			matrix.preScale(sx, sx);
			matrix.preTranslate(-width / 2, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2 + loopShift + screen * width - shiftingX, height / 2);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			float sx = 1 - Math.abs(shiftingX) * 1.0f / width;
			matrix.preScale(sx, sx);
			matrix.preTranslate(-width / 2, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width / 2 + loopShift + screen * width - shiftingX, height / 2);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 滑行(向下)
	 */
	private void processPageSlideDown(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			matrix.preTranslate(0, Math.abs(shiftingX) * height * 1.0f / width);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			matrix.preTranslate(0, -Math.abs(shiftingX) * height * 1.0f / width);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 滑行(向上)
	 */
	private void processPageSlideUp(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			matrix.preTranslate(0, -Math.abs(shiftingX) * height * 1.0f / width);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			matrix.preTranslate(0, Math.abs(shiftingX) * height * 1.0f / width);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 滑梯
	 */
	private void processVerticalScrolling(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			matrix.preTranslate(0, Math.abs(shiftingX) * height * 1.0f / width);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			matrix.preTranslate(0, -Math.abs(shiftingX) * height * 1.0f / width);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift + screen * width, 0);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 楼梯(向下)
	 */
	private void processStairDownLeft(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			float scale = 1 - 0.5f * (Math.abs(shiftingX) * 1.0f / width);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-width, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width + loopShift + screen * width, height / 2);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			float scale = 1 + 0.1f * (Math.abs(shiftingX) * 1.0f / width);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-width, -height / 2);
			// matrix.preTranslate(shiftingX - width, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width + loopShift + screen * width, height / 2);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 楼梯(向上)
	 */
	private void processStairDownRight(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		View layout = workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift = countLoopShift(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			float scale = 1 + 0.1f * (Math.abs(shiftingX) * 1.0f / width);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-width, -height / 2);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width + loopShift + screen * width, height / 2);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			// float scale=1+0.1f*(Math.abs(shiftingX)*1.0f/width);
			// matrix.preScale(scale, scale);
			float scale = 1 - 0.5f * (Math.abs(shiftingX) * 1.0f / width);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-width, -height / 2);
			// matrix.preTranslate(shiftingX - width, 0);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width + loopShift + screen * width, height / 2);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, layout, drawingTime);
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 摩天轮
	 */
	private void processRotating(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = layout.getWidth() / 2;
		dest[1] = layout.getHeight() / 2;
		canvas.save();
		int loopShift = countLoopShift(workspace, screen, isLeft);
		if (isLeft) {
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					center[1] = location[1] + childView.getHeight() / 2;
					float mDegree = Math.abs(shiftingX) * 360.0f / layout.getWidth();
					camera.getMatrix(matrix);
					matrix.preRotate(mDegree);
					matrix.preTranslate(-dest[0], -dest[1]);
					matrix.preTranslate(location[0], location[1]);

					matrix.preTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.preRotate(-mDegree);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);

					matrix.postTranslate(dest[0] + loopShift + screen * layout.getWidth(), dest[1] + layout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		} else {
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					center[1] = location[1] + childView.getHeight() / 2;
					float mDegree = -Math.abs(shiftingX) * 360.0f / layout.getWidth();
					camera.getMatrix(matrix);
					matrix.preRotate(mDegree);
					matrix.preTranslate(-dest[0], -dest[1]);
					matrix.preTranslate(location[0], location[1]);

					matrix.preTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.preRotate(-mDegree);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);

					matrix.postTranslate(dest[0] + loopShift + screen * layout.getWidth(), dest[1] + layout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 百叶窗
	 */
	private void processLouverWindow(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int location[] = new int[2];
		canvas.save();
		int loopShift = countLoopShift(workspace, screen, isLeft);
		boolean isWidget = false;
		if (isLeft) {
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;

					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 90.0f / layout.getWidth();
					camera.rotateX(mDegree);
					camera.getMatrix(matrix);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
					matrix.postTranslate(childView.getWidth() / 2 + location[0] + loopShift + screen * layout.getWidth() - shiftingX, childView.getHeight() / 2 + location[1] + layout.getTop());
					canvas.save();
					canvas.concat(matrix);

					if (isWidget(childView)) {
						mRect.left = 0;
						mRect.right = childView.getWidth();
						mRect.top = 0;
						mRect.bottom = childView.getHeight();
						isWidget = true;
					} else {
						mRect.left = 0;
						mRect.right = childView.getWidth();
						mRect.top = 0;
						mRect.bottom = childView.getHeight();
						isWidget = false;
					}
					int alpha = (int) (255.0f - 255.0f / layout.getWidth() * Math.abs(shiftingX));
					drawAtCanvasEx(canvas, childView, alpha, mRect, isWidget);

					canvas.restore();
					camera.restore();
				}
			}
		} else {
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 90.0f / layout.getWidth();
					camera.rotateX(mDegree);
					camera.getMatrix(matrix);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
					matrix.postTranslate(childView.getWidth() / 2 + location[0] + loopShift + screen * layout.getWidth() - shiftingX, childView.getHeight() / 2 + location[1] + layout.getTop());
					canvas.save();
					canvas.concat(matrix);

					if (isWidget(childView)) {
						mRect.left = 0;
						mRect.right = childView.getWidth();
						mRect.top = 0;
						mRect.bottom = childView.getHeight();
						isWidget = true;
					} else {
						mRect.left = 0;
						mRect.right = childView.getWidth();
						mRect.top = 0;
						mRect.bottom = childView.getHeight();
						isWidget = false;
					}
					int alpha = (int) (255.0f - 255.0f / layout.getWidth() * Math.abs(shiftingX));

					drawAtCanvasEx(canvas, childView, alpha, mRect, isWidget);
					canvas.restore();
					camera.restore();
				}
			}
		}
		canvas.restore();
	}

	/**
	 * 点心桌面特效 旋转翻页
	 */
	private void processPageWave(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int rowNum = workspace.getList().get(0).getRowNum();
		int columnNum = workspace.getList().get(0).getColumnNum();
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = layout.getWidth() / 2;
		dest[1] = layout.getHeight() / 2;
		center[1] = layout.getHeight() / 2;
		canvas.save();
		int loopShift = countLoopShift(workspace, screen, isLeft);
		if (isLeft) {
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 180.0f / layout.getWidth();
					camera.rotateY(-mDegree);
					camera.getMatrix(matrix);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
					matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.postTranslate(location[0], location[1]);
					matrix.postTranslate(loopShift + screen * layout.getWidth(), layout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		} else {
			for (int y = 0; y < rowNum; y++) {
				for (int x = 0; x < columnNum; x++) {
					camera.save();
					matrix.setTranslate(0, 0);
					View childView = layout.getChildAt(y * columnNum + x);
					if (childView == null)
						continue;
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 180.0f / layout.getWidth();
					camera.rotateY(-mDegree);
					camera.getMatrix(matrix);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
					matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.postTranslate(location[0], location[1]);
					matrix.postTranslate(loopShift + screen * layout.getWidth(), layout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		}
		canvas.restore();
	}
	
	/**
	 * 点心特效 旋转木马
	 */
	private void processCarousel(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout layout = (CommonLayout) workspace.getChildAt(screen);
		int width = layout.getWidth();
		int height = layout.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * width, 0);
			float sx = 1 + Math.abs(shiftingX)*1.0f/width;
			matrix.postScale(sx, sx, width/2, height/2);
			matrix.postTranslate(screen * width - shiftingX, 0);
			matrix.postTranslate(Math.abs(shiftingX), 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		} else {
			// 画右屏
			camera.getMatrix(matrix);
			matrix.preTranslate(shiftingX - width, 0);
			matrix.preTranslate(-screen * width, 0);
			float sx = 1 - Math.abs(shiftingX)*1.0f/width;
			matrix.postScale(sx, sx, 0, height/2);
			matrix.postTranslate(width + screen * width - shiftingX, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * width, 0, screen * width + width, canvas.getHeight(), (int) (255 - 255 * Math.abs(shiftingX) / width), CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, layout, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}
	
	/**
	 * 立方体
	 */
	private void processCubeOutside(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		float shiftingX = 0;
		int screenWidth;
		int screenHeight;
		float mDegree = 0;
		CommonLayout view = (CommonLayout) workspace.getChildAt(screen);
		screenWidth = view.getWidth();
		screenHeight = view.getHeight();
		canvas.save();
		float alpha = 0;
		int loopShift=countLoopShift(workspace,screen,isLeft);
		if (isLeft) {
			// 显示左边屏幕
			shiftingX = countShiftingX(workspace, screen, isLeft);
			alpha = 255 - (shiftingX > 0 ? shiftingX : -shiftingX) / screenWidth * 200;
			mDegree = 90.0f * shiftingX / screenWidth;
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * screenWidth - screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.0f, 0);
			matrix.postTranslate(screenWidth+loopShift+screen*screenWidth-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * screenWidth, 0, screen * screenWidth + screenWidth, canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, view, drawingTime);
			canvas.restore();
		} else {
			shiftingX = countShiftingX(workspace, screen, isLeft);
			alpha = 255 - (shiftingX > 0 ? shiftingX : -shiftingX) / screenWidth * 200;
			mDegree = 90.0f * shiftingX / screenWidth;
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-screen * screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.0f, 0);
			matrix.postTranslate(loopShift+screen*screenWidth-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(screen * screenWidth, 0, screen * screenWidth + screenWidth, canvas.getHeight(), (int) alpha, CASCADE_SAVEFLAGS);
			workspace.callDrawChild(canvas, view, drawingTime);
			canvas.restore();
		}
		canvas.restore();
	}
	
	/**
	 * 扇面
	 */
	private void processTurnTable(Canvas canvas, int screen, long drawingTime, CommonSlidingView workspace, boolean isLeft) {
		CommonLayout view = (CommonLayout) workspace.getChildAt(screen);
		int width = view.getWidth();
		int height = view.getHeight();
		/**
		 * 相当于原先cellLayout 中shiftingX
		 */
		int shiftingX = countShiftingX(workspace, screen, isLeft);
		int loopShift=countLoopShift(workspace,screen,isLeft);
		canvas.save();
		if (isLeft) {
			// 画左屏
			float mDegree = 25.0f * shiftingX / width;
			camera.rotateZ(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width/2, -height);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(loopShift+screen*width+width/2, height);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, view, drawingTime);
		} else {
			// 画右屏
			float mDegree = 25.0f * shiftingX / width;
			camera.rotateZ(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-width/2, -height);
			matrix.preTranslate(-screen * width, 0);
			matrix.postTranslate(width/2+loopShift+screen*width, height);
			canvas.concat(matrix);
			workspace.callDrawChild(canvas, view, drawingTime);
		}
		canvas.restore();
	}
}
