package com.nd.launcherdev.framework.effect;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.View;

import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.info.WidgetInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.screens.CellLayout.LayoutParams;
import com.nd.launcherdev.launcher.screens.ScreenViewGroup;
import com.nd.launcherdev.launcher.info.ItemInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;

/**
 * 屏幕编辑模式下的特效处理
 */
public class SpringEffectsFactory {

	private static SpringEffectsFactory instance = null;

	private ScreenViewGroup mWorkspace;
	private CellLayout mCellLayout;
	/**
	 * 正常模式下CellLayout的长和宽
	 */
	private int mCellLayoutWidth, mCellLayoutHeight;
	/**
	 * 编辑模式缩放前CellLayout的长和宽
	 */
	private int mCellLayoutSpringWidthEx, mCellLayoutSpringHeightEx;
	/**
	 * 编辑模式的缩放比例
	 */
	private float springScale;
	/**
	 * 编辑模式缩放后CellLayout的宽和高
	 */
	private int mCellLayoutSpringWidth;
	/**
	 * 编辑模式缩放后左右CellLayout露出的距离，等同于Workspace中的springPageSplit
	 */
	private int mSpringCellLayoutSplit;
	/**
	 * 编辑模式缩放后左右CellLayout的间距，等同于Workspace中的springPageGap
	 */
	private int mSpringCellLayoutGap;
	/**
	 * 编辑模式下滑动一屏的距离，同于Workspace中的springOneScreenDistance
	 */
	private int mSpringOneScreenDistance;
	/**
	 * 编辑模式缩放后CellLayout距离Workspace左边的距离
	 */
	float mCellLayoutLeftPadding;
	/**
	 * 相当于EffectsFactory中的arrayValue[5]
	 */
	float mCellLayoutTopPadding;
	/**
	 * 编辑模式缩放后，移动CellLayout的偏移量，相当于EffectsFactory中的arrayValue[2]
	 */
	float mSpringDistanceToLeft;

	private long drawingTime;
	/**
	 * 位置偏移量
	 */
	private int cellLayoutLocationOffset;

	Camera camera;
	Matrix matrix;
	Point point;
	Matrix parentMatrix;

	private SpringEffectsFactory() {
		camera = new Camera();
		matrix = new Matrix();
		point = new Point();
		parentMatrix = new Matrix();
	}

	public static SpringEffectsFactory getInstance() {
		if (null == instance) {
			instance = new SpringEffectsFactory();
		}
		return instance;
	}

	/**
	 * 绘制特效
	 * 
	 * @param canvas
	 * @param layout
	 * @param mWorkspace
	 * @param drawingTime
	 * @param offSet
	 */
	public void processEffect(Canvas canvas, CellLayout layout, ScreenViewGroup screenViewGroup, long drawingTime, int offSet) {
		this.mWorkspace = screenViewGroup;
		this.mCellLayout = layout;
		this.springScale = mWorkspace.getSpringScale();
		this.mSpringCellLayoutSplit = mWorkspace.getSpringPageSplit();
		this.mSpringCellLayoutGap = mWorkspace.getSpringPageGap();

		this.mCellLayoutWidth = mWorkspace.getWidth();
		this.mCellLayoutHeight = mWorkspace.getHeight();

		this.mCellLayoutSpringWidthEx = mWorkspace.getPageWidth();
		this.mCellLayoutSpringHeightEx = mWorkspace.getPageHeight();

		this.mCellLayoutSpringWidth = (int) (mCellLayoutSpringWidthEx * springScale);

		this.mSpringOneScreenDistance = mCellLayoutSpringWidth + mWorkspace.getSpringPageGap();

		this.mCellLayoutLeftPadding = (1 - springScale) * mCellLayoutSpringWidthEx / 2;
		this.mCellLayoutTopPadding = mWorkspace.getTopPadding() - mWorkspace.getSpringPageTranslationY();
		this.mSpringDistanceToLeft = mCellLayout.getSpringScreenCenterX() - mWorkspace.getScrollX() - mCellLayoutSpringWidthEx / 2;
		this.drawingTime = drawingTime;
		this.cellLayoutLocationOffset = mCellLayout.getCellLayoutLocation() * mCellLayoutWidth - offSet;

		processEffect(canvas, layout);

		mCellLayout = null; // hjiang
	}

	/**
	 * 绘制特效
	 * 
	 * @param canvas
	 * @param layout
	 */
	public void processEffect(Canvas canvas, CellLayout layout) {
		if (EffectsType.getCurrentEffect() == EffectsType.DEFAULT || 
				(mWorkspace.getWorkspaceSpring() != null && mWorkspace.getWorkspaceSpring().isSpringFromDrawer())) {
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			return;
		}
		// 未滑屏状态下,使用默认特效
		if (Math.abs(mSpringDistanceToLeft) == mSpringOneScreenDistance || (mSpringDistanceToLeft == 0 && getCellLayoutLocation() == getWorkspaceCurrentScreen())) {
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			return;
		}

		processEffect(canvas, EffectsType.getCurrentEffect());
	}

	/**
	 * 采用自绘壁纸的情况，为防止切割蒙板透明到系统壁纸，要新建一个层用来绘制图标 这样才不会影响到自绘壁纸
	 * */
	private int saveLayer(Canvas canvas, ScreenViewGroup workspace, int type) {
		int savecount = 0;
		if ((type >= EffectsType.SHUTTER && type <= EffectsType.SNAKE) ||  (type >= EffectsType.DX_ROTATING && type <= EffectsType.DX_PAGEWAVE) ) {
			drawSpringScreenBackground(canvas);
//			if (BaseConfig.isDrawWallPaper) {
				savecount = canvas.save();
				int w = 0, h = 0;
				float left=0;
				w = workspace.getCurrentCellLayout().getWidth();
				h = workspace.getHeight();
				left=cellLayoutLocationOffset-mSpringDistanceToLeft;
				canvas.saveLayer(left, 0,left+w, h, null, Canvas.ALL_SAVE_FLAG);
				return savecount;
//			} else {
//				return 0;
//			}
		}
		return 0;
	}

	void restoreToCount(Canvas canvas, int savecount, int type) {
//		if (BaseConfig.isDrawWallPaper && type >= EffectsType.SHUTTER && type <= EffectsType.SNAKE) {
		if (  (type >= EffectsType.SHUTTER && type <= EffectsType.SNAKE) ||  (type >= EffectsType.DX_ROTATING && type <= EffectsType.DX_PAGEWAVE)  ) {
			canvas.restoreToCount(savecount);
		}
	}

	private void processEffect(Canvas canvas, int type) {
		int save=canvas.save();
		int savecount = saveLayer(canvas, mWorkspace, type);
		switch (type) {
		case EffectsType.DEFAULT:
			return;
		case EffectsType.CASCADE:
			processCascade(canvas);
			break;
		case EffectsType.ROLL:
			processRoll(canvas);
			break;
		case EffectsType.TURN:
			processTurn(canvas);
			break;
		case EffectsType.WINDOWER:
			processWinnower(canvas);
			break;
		case EffectsType.CUBE_INSIDE:
			processDrapeInside(canvas);
			break;
		case EffectsType.CUBE_OUTSIDE:
			processDrapeOutside(canvas);
			break;
		case EffectsType.SHUTTER:
			processShutter(canvas);
			break;
		case EffectsType.CHORD:
			processChord(canvas);
			break;
		case EffectsType.BINARY_STAR:
			processBinaryStar(canvas);
			break;
		case EffectsType.WHEEL:
			processWheel(canvas);
			break;
		case EffectsType.GLOBAL:
			processGlobal(canvas);
			break;
		case EffectsType.CYLINDER:
			processCylinder(canvas);
			break;
		case EffectsType.TORNADO:
			drawTornado(canvas);
			break;
		case EffectsType.TRANSFER:
			ProcessTransfer(canvas);
			break;
		case EffectsType.TURNTABLE:
			ProcessTurntable(canvas);
			break;
		case EffectsType.SNAKE:
			ProcessSnake(canvas);
			break;
		case EffectsType.TIMETUNNEL:
			processTimetunnel(canvas);
			break;
		case EffectsType.OPEN_DOOR:
			processOpenDoor(canvas);
			break;
		case EffectsType.LG_CUBE_INSIDE:
			processLGDrapeInside(canvas);
			break;
		// ==================点心桌面特效==============================
		case EffectsType.DX_SQUASH:
			processSquash(canvas);
			break;
		case EffectsType.DX_CAROUSEL:
			processCarousel(canvas);
			break;		
		case EffectsType.DX_PAGEWAVE:
			processPageWave(canvas);	
			break;
		case EffectsType.DX_CROSSFADE:
			processCrossFade(canvas);
			break;
		case EffectsType.DX_WINDMILL:
			processWindMill(canvas);
			break;
		case EffectsType.DX_PAGEZOOM:
			processPageZoom(canvas);
			break;
		case EffectsType.DX_PAGESLIDEDOWN:
			processPageSlideDown(canvas);
			break;
		case EffectsType.DX_PAGESLIDEUP:
			processPageSlideUp(canvas);
			break;
		case EffectsType.DX_VERTICALSCROLLING:
			processVerticalScrolling(canvas);
			break;
		case EffectsType.DX_STAIRDOWNLEFT:
			processStairDownLeft(canvas);
			break;
		case EffectsType.DX_STAIRDOWNRIGHT:
			processStairDownRight(canvas);
			break;
		case EffectsType.DX_CUBEOUTSIDE:
			processCubeOutside(canvas);
			break;
		case EffectsType.DX_TURNTABLE:
			processTurnTable(canvas);
			break;		
		case EffectsType.DX_ROTATING:
			processRotating(canvas);
			break;
		case EffectsType.DX_LOUVERWINDOW:
			processLouverWindow(canvas);
			break;		
		}
		canvas.restoreToCount(save);
		restoreToCount(canvas, savecount, type);
	}

	private static final int CASCADE_SAVEFLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
			| Canvas.CLIP_TO_LAYER_SAVE_FLAG;

	/**
	 * 获取与屏幕左边的距离
	 * 
	 * @return 距离
	 */
	public int getDistanceToScreenLeft() {
		return mCellLayout.getSpringScreenCenterX() - mWorkspace.getScrollX();
	}

	/**
	 * 获取workspace当前屏
	 * 
	 * @return 当前屏
	 */
	public int getWorkspaceCurrentScreen() {
		return mWorkspace.getCurrentScreen();
	}

	/**
	 * 获取CellLayout在workspace中的位置
	 * 
	 * @return 位置
	 */
	public int getCellLayoutLocation() {
		return mCellLayout.getCellLayoutLocation();
	}

	/**
	 * 层叠
	 * 
	 * @param canvas
	 */
	private void processCascade(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth /2 ;
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			camera.save();
			camera.translate(0, 0, mSpringDistanceToLeft * 0.5f);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding, (int) (255 - 255 * mSpringDistanceToLeft
					/ mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 翻滚
	 * 
	 * @param canvas
	 */
	private void processRoll(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth /2 ;
		camera.save();
		if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			// 显示右边屏幕
			float mDegree = -mSpringDistanceToLeft * 180.0f / (mCellLayoutSpringWidth + mSpringCellLayoutGap);

			camera.translate(mSpringDistanceToLeft / mCellLayoutSpringWidthEx * mCellLayoutSpringHeightEx * 1.25f, 0, 0);
			camera.rotateZ(mDegree);
			camera.getMatrix(matrix);

			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 显示左边屏幕
			float mDegree = -mSpringDistanceToLeft * 180.0f / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
		
			camera.translate(mSpringDistanceToLeft / mCellLayoutSpringWidthEx * mCellLayoutSpringHeightEx * 1.25f, 0, 0);
			camera.rotateZ(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		}
		camera.restore();
	}

	/**
	 * 翻转
	 * 
	 * @param canvas
	 */
	private void processTurn(Canvas canvas) {
		int loc = 0;
		int current = 0;
		float z = 0;// 正方体旋转的半径
		float mDegree = 0L;

		float originalDistance;// 未移动时， celllayou 中心点到可视点的距离
		loc = getCellLayoutLocation();
		current = mWorkspace.getCurrentScreen();
		if (loc == current) {

			originalDistance = mCellLayoutWidth / 2;
			// 角度 -180-0-180
			mDegree = -((getDistanceToScreenLeft() - originalDistance) / mCellLayoutWidth) * 180;
			z = (float) (mCellLayoutSpringWidth / 2 * Math.sin(mDegree * Math.PI / 180));
			z = z < 0 ? z : -z;// 无论是角度是正是负，Z轴永远是要向里面移动的
			if (mDegree == 0) {
				return;
			} else if (mDegree < -90 || mDegree > 90) {
				return;
			}
		} else if (loc == current - 1) {// left
			originalDistance = -(int) (mCellLayoutWidth * springScale / 2 - mSpringCellLayoutSplit);
			// 360 度到0 度,在0到90 区间内才允许显示
			mDegree = 180 - ((getDistanceToScreenLeft() - originalDistance) / (mCellLayoutWidth + mSpringCellLayoutGap)) * 180;

			z = (float) (mCellLayoutSpringWidth / 2 * Math.sin(mDegree * Math.PI / 180));
			z = z < 0 ? z : -z;// 无论是角度是正是负，Z轴永远是要向里面移动的
			if ((int) mDegree == 0) {
				return;
			} else if (!(mDegree > 0 && mDegree < 90)) {
				return;
			}
		} else if (loc == current + 1) {// right

			originalDistance = (int) (mCellLayoutWidth * springScale / 2 + mCellLayoutWidth - mSpringCellLayoutSplit);
			// 角度为-360到0度，在 0 和 -90 的区间才允许显示
			mDegree = -180 - ((getDistanceToScreenLeft() - originalDistance) / (mCellLayoutWidth + mSpringCellLayoutGap)) * 180;
			z = (float) (mCellLayoutSpringWidth / 2 * Math.sin(mDegree * Math.PI / 180));
			z = z < 0 ? z : -z;// 无论是角度是正是负，Z轴永远是要向里面移动的
			if ((int) mDegree == 90) {
				return;
			} else if (!(mDegree < 0 && mDegree > -90)) {
				return;
			}
		} else {

			return;
		}

		matrix.setTranslate(0, 0);
		camera.save();
		camera.translate(0, 0, -z);
		camera.rotateY(-mDegree);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-cellLayoutLocationOffset, 0);
		matrix.preTranslate(-mCellLayoutWidth / 2.0f, -mCellLayoutSpringHeightEx / 2);
		matrix.postTranslate(mCellLayoutWidth / 2.0f+cellLayoutLocationOffset-mSpringDistanceToLeft, mCellLayoutSpringHeightEx / 2);
		canvas.concat(matrix);
		mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
	}

	/**
	 * 风车 根据移动距离 绕右下角 旋转
	 * 
	 * @param canvas
	 */
	private void processWinnower(Canvas canvas) {
		matrix.setTranslate(0, 0);
		camera.save();
		float R = 0;// 风车转时的半径
		R = (float) (mCellLayoutSpringWidth / 2 / Math.tan(15 * Math.PI / 180) + mCellLayoutSpringHeightEx / 2f);
		if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			// 显示右边屏幕
			float mDegree = mSpringDistanceToLeft * 30.0f / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
			camera.translate(0, (float) (R), 0);
			camera.rotateZ(mDegree);
			camera.translate(0, (float) (-R), 0);
			camera.getMatrix(matrix);

			matrix.preTranslate(-mCellLayoutWidth / 2, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.preTranslate(-cellLayoutLocationOffset, 0);
			matrix.postTranslate(mCellLayoutWidth / 2+cellLayoutLocationOffset-mSpringDistanceToLeft, mCellLayoutHeight / 2 + mCellLayoutTopPadding);

			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 显示左边屏幕
			float mDegree = mSpringDistanceToLeft * 30.0f / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
			camera.translate(0, (float) (R), 0);
			camera.rotateZ(mDegree);
			camera.translate(0, (float) (-R), 0);
			camera.getMatrix(matrix);

			matrix.preTranslate(-mCellLayoutWidth / 2, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.preTranslate(-cellLayoutLocationOffset, 0);
			matrix.postTranslate(mCellLayoutWidth / 2+cellLayoutLocationOffset-mSpringDistanceToLeft, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		}
		camera.restore();
	}

	/**
	 * 褶皱内
	 * 
	 * @param canvas
	 */
	private void processDrapeInside(Canvas canvas) {
		float shiftingX = mSpringDistanceToLeft;
		int screenWidth;
		int screenHeight;
		float mDegree = 0;

		screenWidth = mCellLayout.getWidth();
		screenHeight = mCellLayout.getHeight();
		float z = 0;
		matrix.setTranslate(0, 0);
		camera.save();
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 显示左边屏幕
			mDegree = 45.0f * shiftingX / screenWidth;
			z = -(float) (screenWidth * Math.sin(mDegree * Math.PI / 180f));
			camera.translate(0, 0, z);
			camera.rotateY(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-cellLayoutLocationOffset - screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.2f, 0);
			matrix.postTranslate(screenWidth+cellLayoutLocationOffset-shiftingX, screenHeight / 2);
			Log.e("zhou", "a = "+cellLayoutLocationOffset+" b= "+shiftingX+" c= "+canvas.getMatrix());
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {

			mDegree = 45.0f * shiftingX / screenWidth;
			z = (float) (screenWidth * Math.sin(mDegree * Math.PI / 180f));
			camera.translate(0, 0, z);
			camera.rotateY(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-cellLayoutLocationOffset, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.2f, 0);
			matrix.postTranslate(cellLayoutLocationOffset-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		}
		camera.restore();
	}

	/**
	 * 
	 * <br>
	 * Description:LG褶皱内特效 <br>
	 * Author:zhenghonglin <br>
	 * Date:2014-1-6下午03:31:45
	 */
	private void processLGDrapeInside(Canvas canvas) {
		int screenWidth = mCellLayout.getWidth();
		int screenHeight = mCellLayout.getHeight();
		float shiftingX = mSpringDistanceToLeft;
		float mAngle = (float) ((-Math.sin(Math.abs(shiftingX) / screenWidth * Math.PI) + 1) * 30 + 8 * Math.sin(Math.abs(shiftingX) / screenWidth * Math.PI));
		// float mAngle = (float)
		// ((-Math.sin(Math.abs(shiftingX)/screenWidth*Math.PI)+1)*30 + 8);
		float mDegree = (float) (mAngle * Math.sin(shiftingX / screenWidth * Math.PI / 2));
		float z = (float) ((screenWidth - Math.abs(shiftingX)) * Math.sin(Math.abs(mDegree) * Math.PI / 180f));
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		matrix.setTranslate(0, 0);
		camera.save();
		camera.translate(0, 0, z);
		camera.rotateY(-mDegree);
		camera.getMatrix(matrix);
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 显示左边屏幕
			matrix.preTranslate(-centX - screenWidth / 2, -screenHeight / 2);
			matrix.postTranslate(centX + screenWidth / 2, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			matrix.preTranslate(-centX + screenWidth / 2, -screenHeight / 2);
			matrix.postTranslate(centX - screenWidth / 2, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		}
		camera.restore();
	}

	/**
	 * 褶皱
	 * 
	 * @param canvas
	 */
	private void processDrapeOutside(Canvas canvas) {
		float shiftingX = mSpringDistanceToLeft;
		int screenWidth;
		int screenHeight;
		float mDegree = 0;
		screenWidth = mCellLayoutWidth;
		screenHeight = mCellLayoutHeight;
		matrix.setTranslate(0, 0);
		camera.save();
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 显示左边屏幕
			mDegree = 45.0f * shiftingX / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-cellLayoutLocationOffset - screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.3f, 0);
			matrix.postTranslate(screenWidth+cellLayoutLocationOffset-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			mDegree = 45.0f * shiftingX / screenWidth;
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-cellLayoutLocationOffset, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.3f, 0);
			matrix.postTranslate(cellLayoutLocationOffset-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		}
		camera.restore();
	}

	/**
	 * 百叶窗
	 * 
	 * @param canvas
	 */
	private void processShutter(Canvas canvas) {

		int shiftingX = (int) mSpringDistanceToLeft;
		int location[] = new int[2];
		float center[] = new float[2];

		if (mSpringDistanceToLeft >= 0 && mSpringDistanceToLeft < mCellLayout.getWidth()) {
			// 显示右边屏幕
			if (mSpringDistanceToLeft <= mCellLayout.getWidth() * springScale / 2) {
				for (int y = 0; y < mCellLayout.getCountY(); y++) {
					for (int x = 0; x < mCellLayout.getCountX(); x++) {

						View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
						if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
							continue;
						matrix.setTranslate(0, 0);
						location[0] = childView.getLeft();
						location[1] = childView.getTop();
						center[0] = childView.getWidth() / 2;
						center[1] = childView.getHeight() / 2;

						float mDegree = -shiftingX * 180.0f / (mCellLayout.getWidth() * springScale - mSpringCellLayoutGap);
						camera.save();
						camera.translate(0, 0, shiftingX * 0.5f);
						camera.rotateY(mDegree);
						camera.getMatrix(matrix);
						camera.restore();
						matrix.preTranslate(0, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
						matrix.preTranslate(-center[0], -center[1]);
						matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, 0);
						matrix.postScale(springScale, springScale);
						matrix.postTranslate(-shiftingX+cellLayoutLocationOffset, 0);
					    canvas.save();
						matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
						canvas.concat(matrix);
						drawAtCanvas(canvas, childView);
						canvas.restore();
					}
				}
			}
		} else if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayout.getWidth()) {
			// 显示左边屏幕
			if (mSpringDistanceToLeft >= -mCellLayout.getWidth() * springScale / 2) {
				for (int y = 0; y < mCellLayout.getCountY(); y++) {
					for (int x = 0; x < mCellLayout.getCountX(); x++) {

						View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
						if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
							continue;
						matrix.setTranslate(0, 0);
						location[0] = childView.getLeft();
						location[1] = childView.getTop();
						center[0] = childView.getWidth() / 2;
						center[1] = childView.getHeight() / 2;

						float mDegree = -mSpringDistanceToLeft * 180.0f / (mCellLayout.getWidth() * springScale - mSpringCellLayoutGap);
						camera.save();
						camera.translate(0, 0, -mSpringDistanceToLeft * 0.5f);
						camera.rotateY(mDegree);
						camera.getMatrix(matrix);
						camera.restore();

						matrix.preTranslate(0, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
						matrix.preTranslate(-center[0], -center[1]);
						matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, 0);
						matrix.postScale(springScale, springScale);
						matrix.postTranslate(mCellLayout.getWidth() / 2.0f-shiftingX+cellLayoutLocationOffset, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
						canvas.save();
						canvas.concat(matrix);
						drawAtCanvas(canvas, childView);
						canvas.restore();
					}
				}
			}
		}
	}

	/**
	 * 弦
	 * 
	 * @param canvas
	 */
	private void processChord(Canvas canvas) {
		int shiftingX = (int) (mSpringDistanceToLeft / springScale);
		int width = mCellLayout.getWidth();
		int location[] = new int[2];
		int center[] = new int[2];

		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayout.getWidth()) {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {

					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = childView.getWidth() / 2;
					center[1] = childView.getHeight() / 2;

					float mDegree = 0;
					int distance = width + shiftingX;
					if (distance <= childView.getRight()) {
						mDegree = (childView.getRight() - distance) * 180.0f / childView.getWidth();
						if (mDegree >= 90)
							continue;
						camera.save();
						camera.rotateY(-mDegree);
						camera.getMatrix(matrix);
						camera.restore();
					}
					matrix.preTranslate(0, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
					matrix.preTranslate(-center[0], -center[1]);
					matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, 0);
					matrix.postScale(springScale, springScale);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f-mSpringDistanceToLeft+cellLayoutLocationOffset, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				}
			}
		} else if (mSpringDistanceToLeft >= 0 && mSpringDistanceToLeft < mCellLayout.getWidth()) {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {

					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = childView.getWidth() / 2;
					center[1] = childView.getHeight() / 2;

					float mDegree = 0;
					int distance = shiftingX;
					if (distance <= childView.getRight()) {
						mDegree = (childView.getRight() - distance) * 180.0f / childView.getWidth();
						if (mDegree <= 90)
							continue;
						else if (mDegree > 90 && mDegree < 180) {
							camera.save();
							camera.rotateY(-mDegree - 180);
							camera.getMatrix(matrix);
							camera.restore();
						}
					} else {
						continue;
					}
					matrix.preTranslate(0, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
					matrix.preTranslate(-center[0], -center[1]);
					matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, 0);
					matrix.postScale(springScale, springScale);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f-mSpringDistanceToLeft+cellLayoutLocationOffset, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				}
			}
		}
	}

	/**
	 * 双子星
	 * 
	 * @param canvas
	 */
	private void processBinaryStar(Canvas canvas) {
		int shiftingX = (int) (mSpringDistanceToLeft);

		float cellWidth = mCellLayout.getCellWidth();
		int location[] = new int[2];
		int center[] = new int[2];
		int centerInLayout[] = new int[2];
		int dest[] = new int[2];
		dest[0] = mCellLayout.getWidth() / 2;
		dest[1] = mCellLayout.getHeight() / 2;
		float gapWithScreen = mCellLayout.getWidth() * (1 - springScale) / 2.0f;

		for (int y = 0; y < mCellLayout.getCountY(); y++) {
			for (int x = 0; x < mCellLayout.getCountX(); x++) {

				View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
				if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
					continue;
				// view存在
				matrix.setTranslate(0, 0);
				location[0] = childView.getLeft();
				location[1] = childView.getTop();
				center[0] = childView.getWidth() / 2;
				center[1] = childView.getHeight() / 2;
				centerInLayout[0] = location[0] + childView.getWidth() / 2;
				centerInLayout[1] = location[1] + childView.getHeight() / 2;
				float offsetX = 0;
				float offsetY = 0;
				float scale = 1;

				if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
					if (shiftingX < -(mCellLayout.getWidth() / 2 - gapWithScreen)) {
						offsetX = (dest[0] - (centerInLayout[0])) * 1.0f + (mCellLayout.getWidth() / 2 + shiftingX - gapWithScreen);
						offsetY = (dest[1] - centerInLayout[1]) * 1.0f;

						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						offsetX = (dest[0] - (centerInLayout[0])) * -shiftingX * 1.0f / (mCellLayout.getWidth() * 0.5f - gapWithScreen);
						offsetY = (dest[1] - centerInLayout[1]) * -shiftingX * 1.0f / (mCellLayout.getWidth() * 0.5f - gapWithScreen);

						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * -shiftingX / (mCellLayout.getWidth() * 0.5f - gapWithScreen) + 1;
						}
					}
				} else if (mSpringDistanceToLeft >= 0 && mSpringDistanceToLeft < mCellLayout.getWidth()) {
					if (shiftingX > (mCellLayout.getWidth() / 2 - gapWithScreen)) {
						offsetX = (dest[0] - (centerInLayout[0])) * 1.0f + (shiftingX - (mCellLayout.getWidth() / 2 - gapWithScreen));
						offsetY = (dest[1] - centerInLayout[1]) * 1.0f;
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						offsetX = (dest[0] - (centerInLayout[0])) * shiftingX * 1.0f / (mCellLayout.getWidth() * 0.5f - gapWithScreen);
						offsetY = (dest[1] - centerInLayout[1]) * shiftingX * 1.0f / (mCellLayout.getWidth() * 0.5f - gapWithScreen);
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * shiftingX / (mCellLayout.getWidth() * 0.5f - gapWithScreen) + 1;
						}
					}
				} else {
					continue;
				}
				camera.save();
				camera.translate(offsetX, -offsetY, 0);
				camera.getMatrix(matrix);
				camera.restore();
				matrix.preTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
				matrix.preScale(scale, scale);
				matrix.preTranslate(-center[0], -center[1]);
				matrix.postScale(springScale, springScale);
				matrix.postTranslate(shiftingX + mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
				matrix.postTranslate(-shiftingX+cellLayoutLocationOffset, 0);
				canvas.save();
				canvas.concat(matrix);
				drawAtCanvas(canvas, childView);
				canvas.restore();
			}
		}
	}

	/**
	 * child 是否是widget
	 */
	private static boolean isWidget(View child) {
		if (child == null)
			return false;
		ItemInfo item = (ItemInfo) child.getTag();
		if (item instanceof WidgetInfo) {
			return true;
		}
		return false;
	}

	/**
	 * Description: 滚咕噜滚(车轮) Author: guojy Date: 2012-7-22 下午05:03:09
	 */
	private void processWheel(Canvas canvas) {
		int shiftingX = (int) (mSpringDistanceToLeft);
		int topPadding = mCellLayout.getTop();

		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		int centerInLayout[] = new int[2];
		float gapWithScreen = mCellLayout.getWidth() * (1 - springScale) / 2.0f;
		dest[0] = mCellLayout.getWidth() / 2;
		dest[1] = mCellLayout.getHeight() / 2 + topPadding;

		float cellWidth = mCellLayout.getCellWidth();
		float cellHeight = mCellLayout.getCellHeight();
		// 圆的半径
		float R = mCellLayout.getWidth() / 2 - cellHeight / 2;
		// 圆上第一个点的坐标
		float x1 = mCellLayout.getWidth() - cellHeight / 2;
		float y1 = mCellLayout.getHeight() / 2;

		double angle = 0;
		angle = 2 * Math.PI / (mCellLayout.getCountX() * mCellLayout.getCountY());
		// 第一个点旋转到位的时候 旋转的角度
		double firstAngle = Math.PI / 2;

		if (shiftingX < 0) {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {

					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = childView.getWidth() / 2;
					center[1] = childView.getHeight() / 2;

					centerInLayout[0] = location[0] + childView.getWidth() / 2;
					centerInLayout[1] = location[1] + childView.getHeight() / 2;

					// 圆上第(x*y+1)点的坐标
					double offsetAngle = angle * (y * mCellLayout.getCountX() + x);
					double cxn = x1 - R * (1 - Math.cos(offsetAngle));
					double cyn = y1 - R * Math.sin(offsetAngle);
					// 当旋转到圆上的时候 自身旋转的角度
					double rotateAngle = firstAngle + offsetAngle;
					// 当前屏上 第(x*y+1)点的中心坐标
					float xn = centerInLayout[0];
					float yn = centerInLayout[1];

					float scale = 1;
					camera.save();
					// 屏幕上的点移动到圆上的点 位移加旋转过程 移动半个屏幕
					if (shiftingX < -(mCellLayout.getWidth() / 2 - gapWithScreen)) {

						camera.translate((float) (cxn - xn), -(float) (cyn - yn), 0);
						camera.rotateZ((float) (rotateAngle * 180.0 / Math.PI));
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						camera.translate(-shiftingX * (float) (cxn - xn) / (mCellLayout.getWidth() * 0.5f - gapWithScreen), shiftingX * (float) (cyn - yn)
								/ (mCellLayout.getWidth() * 0.5f - gapWithScreen), 0);
						camera.rotateZ(-shiftingX * (float) (rotateAngle * 180.0 / Math.PI) / (mCellLayout.getWidth() * 0.5f - gapWithScreen));
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * -shiftingX / (mCellLayout.getWidth() * 0.5f - gapWithScreen) + 1;
						}
					}
					camera.getMatrix(matrix);
					camera.restore();

					if (shiftingX < -(mCellLayout.getWidth() / 2 - gapWithScreen)) {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-center[0], -center[1]);
						matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
						matrix.postScale(springScale, springScale);
						matrix.postRotate((shiftingX + mCellLayout.getWidth() / 2 - gapWithScreen) * 90 / (mCellLayout.getWidth() * 0.5f - gapWithScreen), 0, mCellLayout.getHeight() / 4.0f);
						matrix.postTranslate(shiftingX + mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					} else {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-center[0], -center[1]);
						matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
						matrix.postScale(springScale, springScale);
						matrix.postTranslate(shiftingX + mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					}
					matrix.postTranslate(-shiftingX+cellLayoutLocationOffset, 0);
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				}
			}
		} else {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {
					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					matrix.setTranslate(0, 0);
					camera.save();
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = childView.getWidth() / 2;
					center[1] = childView.getHeight() / 2;

					centerInLayout[0] = location[0] + childView.getWidth() / 2;
					centerInLayout[1] = location[1] + childView.getHeight() / 2;

					// 圆上第(x*y+1)点的坐标
					double offsetAngle = angle * (y * mCellLayout.getCountX() + x);
					double cxn = x1 - R * (1 - Math.cos(offsetAngle));
					double cyn = y1 - R * Math.sin(offsetAngle);
					// 当旋转到圆上的时候 自身旋转的角度
					double rotateAngle = firstAngle + offsetAngle;
					// 当前屏上 第(x*y+1)点的中心坐标
					float xn = centerInLayout[0];
					float yn = centerInLayout[1];

					float scale = 1;
					// 屏幕上的点移动到圆上的点 位移加旋转过程 移动半个屏幕
					if (shiftingX > (mCellLayout.getWidth() / 2 - gapWithScreen)) {
						camera.translate((float) (cxn - xn), -(float) (cyn - yn), 0);
						camera.rotateZ((float) (rotateAngle * 180.0 / Math.PI));
						if (isWidget(childView)) {
							scale = cellWidth / childView.getWidth();
						}
					} else {
						camera.translate(shiftingX * (float) (cxn - xn) / (mCellLayout.getWidth() * 0.5f - gapWithScreen), -shiftingX * (float) (cyn - yn)
								/ (mCellLayout.getWidth() * 0.5f - gapWithScreen), 0);
						camera.rotateZ(shiftingX * (float) (rotateAngle * 180.0 / Math.PI) / (mCellLayout.getWidth() * 0.5f - gapWithScreen));
						if (isWidget(childView)) {
							scale = (cellWidth / childView.getWidth() - 1) * shiftingX / (mCellLayout.getWidth() * 0.5f - gapWithScreen) + 1;
						}
					}
					camera.getMatrix(matrix);
					camera.restore();
					if (shiftingX > (mCellLayout.getWidth() / 2 - gapWithScreen)) {
						matrix.preScale(scale, scale);
						matrix.preTranslate(-center[0], -center[1]);
						matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
						matrix.postScale(springScale, springScale);
						matrix.postRotate((shiftingX - mCellLayout.getWidth() / 2 + gapWithScreen) * 90 / (mCellLayout.getWidth() * 0.5f - gapWithScreen), 0, mCellLayout.getHeight() / 4.0f);
						matrix.postTranslate(shiftingX + mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());

					} else {

						matrix.preScale(scale, scale);
						matrix.preTranslate(-center[0], -center[1]);
						matrix.postTranslate(location[0] + center[0] - mCellLayout.getWidth() / 2.0f, location[1] + center[1] - mCellLayout.getHeight() / 10.0f);
						matrix.postScale(springScale, springScale);
						matrix.postTranslate(shiftingX + mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					}
					matrix.postTranslate(-shiftingX+cellLayoutLocationOffset, 0);
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				}
			}
		}
	}

	private void processGlobal(Canvas canvas) {
		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;
		CellLayout layout = mCellLayout;
		int cellWidth = layout.getCellWidth();
		int cellHeight = layout.getCellHeight();
		float radius = (layout.getWidth() / 2 + cellWidth * 0.5f) * springScale;

		float col = layout.getCountX() * 2;

		// 列绕y轴转的角度
		float colAngle = 360 / col;
		// 行绕x轴转的角度
		float rowAngle = 90 / (layout.getCountY() - 1);

		float firstColRotate = (90 - colAngle / 2) - (shiftingX / layout.getWidth()) * 180;
		float firstRowRotate = 45;
		float centerX = layout.getWidth() / 2; // 圆心Ｘ
		int topPadding = mCellLayout.getTop() - mWorkspace.getSpringPageTranslationY();
		float centerY = layout.getHeight() / 2 + topPadding;// 圆心Ｙ

		float viewCenterY = 0;
		float viewCenterX = 0;
		boolean isWidget=true;
		for (int y = 0; y < layout.getCountY(); y++) {
			for (int x = 0; x < layout.getCountX(); x++) {

				View childView = layout.getChildViewByIndex(y * layout.getCountX() + x);
				if (childView == null) {
					continue;
				}
				if (isWidget(childView)) {
					CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
					int cellHSpan = params.spanX;
					int i = layout.getWidgetViewByIndex(y * layout.getCountX() + x);
					viewCenterX = ((i - 1) % cellHSpan) * cellWidth + 0.5f * cellWidth;
					viewCenterY = ((i - 1) / cellHSpan) * cellHeight + 0.5f * cellHeight;
				} else {
					viewCenterX = childView.getWidth() / 2;
					viewCenterY = childView.getHeight() / 2;
				}

				float mRotate = firstColRotate - colAngle * x;
				float mRowRotate = firstRowRotate - rowAngle * y;
				matrix.setTranslate(0, 0);
				camera.save();
				camera.translate(0, 0, radius);
				camera.rotateX(45 * mWorkspace.getFingerOffsetY() / (layout.getHeight() * 0.5f));
				camera.rotateY(-mRotate);
				camera.rotateX(mRowRotate);
				camera.translate(0, 0, -radius);
				camera.getMatrix(matrix);
				camera.restore();

				matrix.preScale(springScale, springScale);
				matrix.preTranslate(-viewCenterX, -viewCenterY);
				matrix.postTranslate(centerX-shiftingX+cellLayoutLocationOffset, centerY);
				canvas.save();
				canvas.concat(matrix);
				
				if (isWidget(childView)) {
					CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
					int cellHSpan = params.spanX;
					int i = layout.getWidgetViewByIndex(y * layout.getCountX() + x);
					//canvas.clipRect(((i - 1) % cellHSpan) * cellWidth, ((i - 1) / cellHSpan) * cellHeight, ((i - 1) % cellHSpan) * cellWidth + cellWidth, ((i - 1) / cellHSpan) * cellHeight
						//	+ cellHeight, Region.Op.REPLACE);
					mRect.left=((i - 1) % cellHSpan) * cellWidth;
					mRect.right=((i-1)  % cellHSpan) * cellWidth+cellWidth;	
					mRect.top=((i - 1) / cellHSpan) * cellHeight;
					mRect.bottom=((i-1)  / cellHSpan) * cellHeight+cellHeight;
					isWidget=true;
				} else {
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
					mRect.left=0;
					mRect.right=cellWidth;
					mRect.top=0;
					mRect.bottom=cellHeight;
					isWidget=false;
				}
				if (mRotate < -90 || mRotate > 90) {
					//canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)), Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
					//drawAtCanvas(canvas, childView);
					//canvas.restore();
					drawAtCanvasEx(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)), mRect, isWidget);
				} else {
					drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
					//drawAtCanvas(canvas, childView);
				}
				canvas.restore();
			}
		}
	}

	/**
	 * 圆柱
	 */
	private void processCylinder(Canvas canvas) {
		int shiftingX = (int) mSpringDistanceToLeft;
		int width = mCellLayout.getWidth();
		int topPadding = mCellLayout.getTop() - mWorkspace.getSpringPageTranslationY();

		int center = 0;

		int rowNum = mCellLayout.getCountY();
		int columnNum = mCellLayout.getCountX();

		int cellWidth = mCellLayout.getCellWidth();
		int cellHeight = mCellLayout.getCellHeight();

		float radius = (width / 2 + cellWidth * 0.5f) * springScale;
		int cylinderCenterX = width / 2;
		int cylinderCenterY = mCellLayout.getHeight() / 2 + topPadding;

		float n = columnNum * 2;

		if (mWorkspace.getTouchState() == 0) {
			// 根据释放时候 移动的距离 来改变n的值
			n = n + (float) Math.cos(Math.atan(shiftingX * 0.25f)) * n;

			if (mCellLayout.getLayoutIndex() != mWorkspace.getCurrentScreen()) {
				return;
			}
		}
		float perAngle = 360 / n;
		float firstRotate = (90 - perAngle / 2) - (shiftingX * 1.0f / width) * 180;
		boolean isWidget=true;
		for (int y = 0; y < rowNum; y++) {
			for (int x = 0; x < columnNum; x++) {

				View childView = mCellLayout.getChildViewByIndex(y * columnNum + x);
				if (childView == null)
					continue;
				matrix.setTranslate(0, 0);
				if (isWidget(childView)) {
					CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
					int cellHSpan = params.spanX;
					int i = mCellLayout.getWidgetViewByIndex(y * columnNum + x);
					center = (i - 1) % cellHSpan * cellWidth + cellWidth / 2;
				} else {
					center = childView.getWidth() / 2;
				}
				float mRotate = firstRotate - perAngle * x;
				camera.save();
				camera.translate(0, 0, radius);
				camera.rotateY(-mRotate);
				camera.translate(0, 0, -radius);
				camera.getMatrix(matrix);
				camera.restore();

				matrix.preScale(springScale, springScale);
				matrix.preTranslate(-center, -(mCellLayout.getHeight() / 2 - childView.getTop()));
				matrix.postTranslate(cylinderCenterX-shiftingX+cellLayoutLocationOffset, cylinderCenterY);
				canvas.save();
				canvas.concat(matrix);

				if (isWidget(childView)) {
					CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
					int cellHSpan = params.spanX;
					int i = mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x);
					//canvas.clipRect(((i - 1) % cellHSpan) * cellWidth, ((i - 1) / cellHSpan) * cellHeight, ((i - 1) % cellHSpan) * cellWidth + cellWidth, ((i - 1) / cellHSpan) * cellHeight
						//	+ cellHeight, Region.Op.REPLACE);
					mRect.left=((i - 1) % cellHSpan) * cellWidth;
					mRect.right=((i-1)  % cellHSpan) * cellWidth+cellWidth;	
					mRect.top=((i - 1) / cellHSpan) * cellHeight;
					mRect.bottom=((i-1)  / cellHSpan) * cellHeight+cellHeight;
					isWidget=true;
				} else {
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
					mRect.left=0;
					mRect.right=cellWidth;	
					mRect.top=0;
					mRect.bottom=cellHeight;
					isWidget=false;

				}
				if (mRotate < -90 || mRotate > 90 && mWorkspace.getTouchState() != 0) {
					//drawAtCanvas(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)));
					drawAtCanvasEx(canvas, childView, (int) (255 + 240 * Math.cos(mRotate * Math.PI / 180)), mRect, isWidget);
				} else {
					//drawAtCanvas(canvas, childView);
					drawAtCanvasEx(canvas, childView, 255, mRect, isWidget);
				}
				canvas.restore();
			}// for (int x = 0; x < columnNum; x++)

		}// end for (int y = 0; y < rowNum; y++)
	}

	private static void drawAtCanvas(Canvas canvas, View view) {
		view.draw(canvas);
	}

	private static void drawAtCanvas(Canvas canvas, View view, int alpha) {
		canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		view.draw(canvas);
		canvas.restore();
	}

	/**
	 * Description: 编辑模式特效下，绘制当前屏背景 Author: guojy Date: 2012-9-18 下午03:50:38
	 */
	private void drawSpringScreenBackground(Canvas canvas) {
		int loc = getCellLayoutLocation();
		int current = mWorkspace.getCurrentScreen();
		matrix.setTranslate(0, 0);
		canvas.save();
		if (loc == current) {

			matrix.preScale(springScale, springScale);
			matrix.preTranslate(-mCellLayout.getWidth() / 2, -mCellLayout.getHeight() / 2);
			matrix.postTranslate(mCellLayout.getWidth() / 2-mSpringDistanceToLeft+cellLayoutLocationOffset, mCellLayout.getHeight() / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			//canvas.clipRect(0, 0, canvas.getWidth(), mCellLayout.getHeight(), Region.Op.REPLACE);
			mCellLayout.drawSpringBackground(canvas, 255);
		}
		canvas.restore();
	}

	void drawTornado(Canvas canvas) {
		CellLayout layout = mCellLayout;
		float shiftingX = mSpringDistanceToLeft;
		float tornadoCenterX = layout.getWidth() / 2;
		float viewCenterY = 0;
		float viewCenterX = 0;
		float coordinateX = 0;
		float coordinateZ = 0;
		float scaleCoordinateY = 0;// 缩放后的Y轴位置
		float R = 0;// 半径
		int location[] = new int[2];
		float tiltAngle = 20;

		float rotateY = 0;
		float col = layout.getCountX() * 2;
		float colAngle = 360 / col;
		float firstColRotate = (90 - colAngle / 2) + ((float) -shiftingX / layout.getWidth()) * 180;
		int cellWidth = layout.getCellWidth();
		int cellHeight = layout.getCellHeight();
		float tanVlaue = (float) Math.tan(tiltAngle / 180f * Math.PI);
		boolean isWidget=false;
		for (int y = 0; y < layout.getCountY(); y++) {
			for (int x = 0; x < layout.getCountX(); x++) {

				View childView = layout.getChildViewByIndex(y * layout.getCountX() + x);
				if (childView == null) {
					continue;
				}
				matrix.setTranslate(0, 0);
				if (isWidget(childView)) {
					CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
					int cellHSpan = params.spanX;
					int i = layout.getWidgetViewByIndex(y * layout.getCountX() + x);
					viewCenterX = ((i - 1) % cellHSpan) * cellWidth + 0.5f * cellWidth;
					viewCenterY = ((i - 1) / cellHSpan) * cellHeight + 0.5f * cellHeight;

				} else {
					viewCenterX = childView.getWidth() / 2;
					viewCenterY = childView.getHeight() / 2;

				}
				rotateY = GetAngle(firstColRotate - colAngle * x);

				R = (layout.getHeight() - childView.getTop() - viewCenterY) * tanVlaue * springScale;
				coordinateX = (float) (-R * Math.sin(rotateY / 180f * Math.PI));
				// coordinateX=0
				coordinateZ = (float) (-R * Math.cos(rotateY / 180f * Math.PI)) + 120;
				layout.getLocationInWindow(location);
				camera.save();
				camera.translate(coordinateX, 0, coordinateZ);
				camera.rotateY(-rotateY);
				camera.rotateX(-tiltAngle);
				camera.getMatrix(matrix);
				camera.restore();
				matrix.preScale(springScale, springScale);
				matrix.preTranslate(-viewCenterX, -viewCenterY);
				scaleCoordinateY = layout.getHeight() / 2 * springScale + cellHeight * 0.5f * springScale + (viewCenterY + childView.getTop() - layout.getHeight() / 2) * springScale;
				matrix.postTranslate(tornadoCenterX-shiftingX+cellLayoutLocationOffset, location[1] + scaleCoordinateY);
				canvas.save();
				canvas.concat(matrix);
				if (isWidget(childView)) {
					CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
					int cellHSpan = params.spanX;
					int i = layout.getWidgetViewByIndex(y * layout.getCountX() + x);
					//canvas.clipRect(((i - 1) % cellHSpan) * cellWidth, ((i - 1) / cellHSpan) * cellHeight, ((i - 1) % cellHSpan) * cellWidth + cellWidth, ((i - 1) / cellHSpan) * cellHeight
						//	+ cellHeight, Region.Op.REPLACE);
					mRect.left=((i - 1) % cellHSpan) * cellWidth;
					mRect.right=((i-1)  % cellHSpan) * cellWidth+cellWidth;	
					mRect.top=((i - 1) / cellHSpan) * cellHeight;
					mRect.bottom=((i-1)  / cellHSpan) * cellHeight+cellHeight;
					isWidget=true;
				} else {
					mRect.top=0;
					mRect.left=0;
					mRect.right=cellWidth;
					mRect.bottom=cellHeight;
					isWidget=false;
					//canvas.clipRect(0, 0, cellWidth, cellHeight, Region.Op.REPLACE);
				}
				if (rotateY > 90 && rotateY < 270) {
					//drawAtCanvas(canvas, childView, 100);
					drawAtCanvasEx(canvas, childView, 100, mRect,isWidget);
				} else {
					//drawAtCanvas(canvas, childView);
					drawAtCanvasEx(canvas, childView, 255, mRect,isWidget);
				}
				canvas.restore();
			}// end for (int x = 0; x < layout.getCountX()
		}// endfor (int y = 0; y < layout.getViewRowNum()
	}// end void processTornado(

	float GetAngle(float Angle) {
		Angle = Angle % 360;
		Angle = (Angle + 360) % 360;
		return Angle;
	}

	private void ProcessTurntable(Canvas canvas) {

		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;
		CellLayout layout = mCellLayout;
		int cellWidth = layout.getCellWidth();
		int cellHeight = layout.getCellHeight();

		float viewCenterY = 0;
		float viewCenterX = 0;
		float springWidth = layout.getWidth() * springScale;
		float degrees = shiftingX / springWidth * 180;
		int topPadding = mCellLayout.getTop() - mWorkspace.getSpringPageTranslationY();
		for (int y = 0; y < layout.getCountY(); y++) {
			for (int x = 0; x < layout.getCountX(); x++) {

				View childView = layout.getChildViewByIndex(y * layout.getCountX() + x);
				if (childView == null || layout.getWidgetViewByIndex(y * layout.getCountX() + x) != 1) {
					continue;
				}
				matrix.setTranslate(0, 0);
				CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
				int cellHSpan = params.spanX;
				if (isWidget(childView)) {
					int i = layout.getWidgetViewByIndex(y * layout.getCountX() + x);
					viewCenterX = ((i - 1) % cellHSpan) * cellWidth + 0.5f * cellWidth;
					viewCenterY = ((i - 1) / cellHSpan) * cellHeight + 0.5f * cellHeight;
				} else {
					viewCenterX = childView.getWidth() / 2;
					viewCenterY = childView.getHeight() / 2;
				}
				if (params.cellX == 0 || params.cellX == layout.getCountX() - 1 || params.cellY == 0 || params.cellY == layout.getCountY() - 1) {
					canvas.save();
					ProcessRound(canvas, childView, params.cellX, params.cellY, layout, matrix, shiftingX / layout.getWidth());
					canvas.restore();
				} else {
					if ((shiftingX <= 0 && shiftingX >= -springWidth / 2) || (shiftingX > 0 && shiftingX < springWidth / 2)) {
						camera.save();
						camera.rotateY(degrees);
						camera.getMatrix(matrix);
						camera.restore();
						matrix.preTranslate(-viewCenterX, -viewCenterY);
						matrix.postTranslate(childView.getLeft() + viewCenterX - layout.getWidth() / 2, childView.getTop() + viewCenterY - layout.getHeight() / 2);
						matrix.postScale(springScale, springScale);
						matrix.postTranslate(layout.getWidth() / 2-shiftingX+cellLayoutLocationOffset, layout.getHeight() / 2 + topPadding);
						canvas.save();
						canvas.concat(matrix);
						canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
						drawAtCanvas(canvas, childView);
						canvas.restore();
					}
				}
			}// for
		}// for
	}// ProcessTurntable

	public void ProcessRound(Canvas canvas, View childView, int cellX, int cellY, CellLayout layout, Matrix matrix, float rate) {

		float finalPostY = 0;
		float moveX = 0;
		float moveY = 0;
		int topPadding = mCellLayout.getTop() - mWorkspace.getSpringPageTranslationY();
		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;
		for (int i = 0; i < 1; i++) {

			if (cellY == 0 && cellX != layout.getCountX() - 1) {
				moveX = -layout.getWidth() * rate;
				break;
			}

			if (cellX == 0) {
				moveY = layout.getHeight() * rate;
				finalPostY = childView.getTop() + childView.getHeight() + layout.getHeight() * rate;
				break;
			}

			if (cellX == layout.getCountX() - 1 && cellY != layout.getCountY() - 1) {
				moveY = -layout.getHeight() * rate;
				finalPostY = childView.getTop() + childView.getHeight() - layout.getHeight() * rate;
				break;
			}

			if (cellY == layout.getCountY() - 1) {
				moveX = layout.getWidth() * rate;
				break;
			}

		}
		matrix.setTranslate(0, 0);
		// 防止超出到dock 栏
		if (finalPostY < layout.getHeight()) {
			matrix.preTranslate(childView.getLeft() - layout.getWidth() / 2, childView.getTop() - layout.getHeight() / 2);
			matrix.postScale(springScale, springScale);
			matrix.postTranslate(moveX + layout.getWidth() / 2-shiftingX+cellLayoutLocationOffset, moveY + layout.getHeight() / 2 + topPadding);
			canvas.concat(matrix);
			canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
			drawAtCanvas(canvas, childView);
		}

	}

	private void ProcessTransfer(Canvas canvas) {
		matrix.setTranslate(0, 0);
		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;
		CellLayout layout = mCellLayout;

		// float toppadding=layout.getTop();
		int topPadding = mCellLayout.getTop() - mWorkspace.getSpringPageTranslationY();
		for (int y = 0; y < layout.getCountY(); y++) {
			for (int x = 0; x < layout.getCountX(); x++) {

				View childView = layout.getChildViewByIndex(y * layout.getCountX() + x);
				if (childView == null || layout.getWidgetViewByIndex(y * layout.getCountX() + x) != 1) {
					continue;
				}
				float moveDistanceX = 0;

				CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
				if (params.cellY % 2 == 0) {
					moveDistanceX = shiftingX;
				} else {
					moveDistanceX = -shiftingX;
				}
				matrix.setTranslate(0, 0);
				matrix.preTranslate(-layout.getWidth() / 2 + childView.getLeft(), -layout.getHeight() / 2 + childView.getTop());
				matrix.postScale(springScale, springScale);
				matrix.postTranslate(layout.getWidth() / 2-shiftingX+cellLayoutLocationOffset + moveDistanceX, layout.getHeight() / 2 + topPadding);
				canvas.save();
				canvas.concat(matrix);
				canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
				drawAtCanvas(canvas, childView);
				canvas.restore();

			}// for
		}// for
	}// ProcessTurntable

	// 贪吃蛇
	private void ProcessSnake(Canvas canvas) {
		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;
		CellLayout layout = mCellLayout;

		float topPadding = mCellLayout.getTop() - mWorkspace.getSpringPageTranslationY();
		// float rate=Math.abs(shiftingX/layout.getWidth());
		float rate = -shiftingX / (layout.getWidth() * springScale);
		int pathH = layout.getWidth() - layout.getCellWidth();
		int pathV = layout.getCellHeight() + layout.getCellGapY();
		int moveMaxDistance = pathH + (pathH + pathV) * (layout.getCountY() - 1) + layout.getCellWidth();
		for (int y = 0; y < layout.getCountY(); y++) {
			for (int x = 0; x < layout.getCountX(); x++) {
				View childView = layout.getChildViewByIndex(y * layout.getCountX() + x);
				if (childView == null || layout.getWidgetViewByIndex(y * layout.getCountX() + x) != 1) {
					continue;
				}
				matrix.setTranslate(0, 0);
				CellLayout.LayoutParams params = (CellLayout.LayoutParams) childView.getLayoutParams();
				GetPost(childView, layout.getCountY(), params, (int) (rate * moveMaxDistance), pathH, pathV, point);
				if (point.x != MY_INFINITE) {
					matrix.preTranslate(point.x, point.y);
					matrix.postTranslate(-layout.getWidth() / 2, -layout.getHeight() / 2);
					matrix.postScale(springScale, springScale);
					matrix.postTranslate(layout.getWidth() / 2-shiftingX+cellLayoutLocationOffset, layout.getHeight() / 2 + topPadding);
					canvas.save();
					canvas.concat(matrix);
					canvas.clipRect(0, 0, childView.getWidth(), childView.getHeight(), Region.Op.REPLACE);
					drawAtCanvas(canvas, childView);
					canvas.restore();
				}
			}// for
		}// for
	}// ProcessTurntable

	public static final int MY_INFINITE = -1000;

	/**
	 * 获取获取移动的距离 包含图标位置本身移动的距离和滑屏时实时图标应该移动的距离
	 * 
	 * @param childView
	 * @param params
	 * @param pathH
	 * @param pathV
	 * @param realTimeMove
	 */
	public static int GetMoveDistance(View childView, CellLayout.LayoutParams params, int pathH, int pathV, int realTimeMove) {
		int moveDistance = 0;
		moveDistance = params.cellY * (pathH + pathV);
		if (params.cellY % 2 == 0) {
			moveDistance += pathH - (childView.getLeft() - params.leftMargin);
		} else {
			moveDistance += childView.getLeft() - params.leftMargin;
		}
		return moveDistance + realTimeMove;
	}

	/**
	 * 通过最终移动的距离，计算所在的行中的位置 ，或者所在列的位置 列的可能性只有两种，一个在最左边，一个在最右边,如下图的两第竖线
	 * ___________ | |__________ | ___________|
	 * 
	 * @param childView
	 * @param countY
	 * @param params
	 * @param moveDistance
	 * @param pathH
	 * @param pathV
	 * @param point
	 */
	public static void GetPost(View childView, int countY, CellLayout.LayoutParams params, int moveDistance, int pathH, int pathV, Point point) {
		int totalMoveD = 0;
		point.x = 0;
		point.y = 0;

		totalMoveD = GetMoveDistance(childView, params, pathH, pathV, moveDistance);
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
				point.x = pathH - surplusDistance + params.leftMargin;
				point.y = row * pathV + params.topMargin;
			} else // 在列上
			{
				if (row == countY - 1) {
					point.x = params.leftMargin - (surplusDistance - pathH);
					point.y = row * pathV + params.topMargin;
				} else {
					point.x = params.leftMargin;
					point.y = row * pathV + surplusDistance - pathH + params.topMargin;
				}
			}
		} else // 奇数行 ,1 3 5...
		{
			if (surplusDistance <= pathH)// 在行上
			{
				point.x = surplusDistance + params.leftMargin;
				point.y = row * pathV + params.topMargin;
			} else // 在列上
			{
				if (row == countY - 1) {
					point.x = pathH + surplusDistance - pathH + params.leftMargin;
					point.y = row * pathV + params.topMargin;
				} else {
					point.x = pathH + params.leftMargin;
					point.y = row * pathV + surplusDistance - pathH + params.topMargin;
				}
			}
		}// end else 奇数
			// 超出的部分将画把点设置负的足够大
		if (row > countY - 1) {
			point.x = MY_INFINITE;
		}

	}

	/** 时光隧道 */
	private void processTimetunnel(Canvas canvas) {

		canvas.save();
		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;
		int width = mCellLayoutWidth;
		int height = mCellLayoutHeight;

		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			float scale = 1 + 0.5f * (-shiftingX / width);
			// Log.e("zhou", "scale"+scale+"m="+shiftingX+" w="+width);
			// scale=scale>1.5?1.5f:scale;

			int alpha = (int) (255 * (1 + shiftingX / width));
			alpha = alpha > 0 ? alpha : 0;
			matrix.setTranslate(0, 0);
			matrix.preTranslate(-width, -height * 0.3f);
			matrix.preTranslate(-cellLayoutLocationOffset, 0);
			matrix.postScale(scale, scale);
			matrix.postTranslate(shiftingX * 0.5f, 0);
			matrix.postTranslate(width+cellLayoutLocationOffset-mSpringDistanceToLeft, height * 0.3f);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + width, height, alpha, CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();

		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {

			float scale = 1 - 0.6f * (shiftingX / width);
			int alpha = (int) (255 * (1 - (shiftingX * 1.2f) / width));
			alpha = alpha > 0 ? alpha : 0;
			matrix.setTranslate(0, 0);
			matrix.preTranslate(0, -height * 0.3f);
			matrix.preTranslate(-cellLayoutLocationOffset, 0);
			matrix.postScale(scale, scale);
			matrix.postTranslate(shiftingX, 0);
			matrix.postTranslate(cellLayoutLocationOffset-mSpringDistanceToLeft, height * 0.3f);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + width, height, alpha, CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();

		}
		canvas.restore();
	}

	/** 翻书 */
	void processOpenDoor(Canvas canvas) {

		canvas.save();
		float shiftingX = 0;
		shiftingX = mSpringDistanceToLeft;

		int width = mCellLayoutWidth;
		int height = mCellLayoutHeight;

		float angle = 0;
		float scale = 0;
		// 当前的在屏幕的宽度应该为如下

		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			matrix.setTranslate(0, 0);
			angle = 90 * shiftingX / width;

			camera.save();

			camera.rotateY(angle);
			camera.getMatrix(matrix);
			camera.restore();
			scale = 1 + 1f * shiftingX / width;
			matrix.preScale(scale, 1);
			matrix.preTranslate(0, -height * 0.3f);
			matrix.preTranslate(-cellLayoutLocationOffset, 0);
			matrix.postTranslate(cellLayoutLocationOffset-mSpringDistanceToLeft, height * 0.3f);
			int alpha = (int) (255 * (1 + shiftingX / width));
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + width, height, alpha, CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();

		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {

			matrix.setTranslate(0, 0);
			angle = 90 * shiftingX / width;
			camera.save();
			camera.rotateY(-angle);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(0, -height * 0.3f);
			matrix.preTranslate(-cellLayoutLocationOffset - width, 0);
			matrix.postTranslate(width, 0);
			matrix.postTranslate(cellLayoutLocationOffset-mSpringDistanceToLeft, height * 0.3f);
			canvas.concat(matrix);
			int alpha = (int) (255 * (1 - shiftingX / width));
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + width, height, alpha, CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
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
	
	// ========================================点心桌面特效======================================================
	/**
	 * 点心桌面特效 折扇
	 */
	private void processSquash(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;

		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			float sx = 1 - Math.abs((mSpringDistanceToLeft)) * 1.0f / mCellLayoutSpringWidth;
			matrix.preScale(sx, 1.0f, -mCellLayoutSpringWidth / 2, 0);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			float sx = 1 - Math.abs((mSpringDistanceToLeft)) * 1.0f / mCellLayoutSpringWidth;
			matrix.preScale(sx, 1.0f, mCellLayoutSpringWidth / 2, 0);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 淡入淡出
	 */
	private void processCrossFade(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		}

	}

	/**
	 * 点心桌面特效 荷兰风车
	 */
	private void processWindMill(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;

		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			camera.save();
			float degree = -Math.abs(mSpringDistanceToLeft) * 15.0f / mCellLayoutSpringWidth;
			camera.rotateY(degree);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			camera.save();
			float degree = -Math.abs(mCellLayoutSpringWidth - mSpringDistanceToLeft) * 15 / mCellLayoutSpringWidth + 15;
			camera.rotateY(degree);
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 时空穿越
	 */
	private void processPageZoom(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;

		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			camera.save();
			camera.getMatrix(matrix);
			camera.restore();
			float sx = 1 + Math.abs(mSpringDistanceToLeft) * 1.0f / mCellLayoutSpringWidth;
			matrix.preScale(sx, sx);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			camera.save();
			camera.getMatrix(matrix);
			camera.restore();
			float sx = 1 - Math.abs(mSpringDistanceToLeft) * 1.0f / mCellLayoutSpringWidth;
			matrix.preScale(sx, sx);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 滑行(向下)
	 */
	private void processPageSlideDown(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		int mCellLayoutSpringHeight = (int) (mCellLayoutSpringHeightEx * springScale);
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			matrix.preTranslate(0, Math.abs(mSpringDistanceToLeft) * mCellLayoutSpringHeight * 1.0f / mCellLayoutSpringWidth);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			matrix.preTranslate(0, -Math.abs(mSpringDistanceToLeft) * mCellLayoutSpringHeight * 1.0f / mCellLayoutSpringWidth);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 滑行(向上)
	 */
	private void processPageSlideUp(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		int mCellLayoutSpringHeight = (int) (mCellLayoutSpringHeightEx * springScale);
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			matrix.preTranslate(0, -Math.abs(mSpringDistanceToLeft) * mCellLayoutSpringHeight * 1.0f / mCellLayoutSpringWidth);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			matrix.preTranslate(0, Math.abs(mSpringDistanceToLeft) * mCellLayoutSpringHeight * 1.0f / mCellLayoutSpringWidth);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 滑梯
	 */
	private void processVerticalScrolling(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		int mCellLayoutSpringHeight = (int) (mCellLayoutSpringHeightEx * springScale);
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			matrix.preTranslate(0, Math.abs(mSpringDistanceToLeft) * mCellLayoutSpringHeight * 1.0f / mCellLayoutSpringWidth);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			// matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			matrix.preTranslate(0, -Math.abs(mSpringDistanceToLeft) * mCellLayoutSpringHeight * 1.0f / mCellLayoutSpringWidth);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			// matrix.postTranslate(-mSpringDistanceToLeft, 0);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 楼梯(向下)
	 */
	private void processStairDownLeft(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			float scale = 1 - 0.5f * (Math.abs(mSpringDistanceToLeft) * 1.0f / mCellLayoutSpringWidth);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			float scale = 1 + 0.1f * (Math.abs(mSpringDistanceToLeft) * 1.0f / mCellLayoutSpringWidth);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 楼梯(向上)
	 */
	private void processStairDownRight(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			float scale = 1 + 0.1f * (Math.abs(mSpringDistanceToLeft) * 1.0f / mCellLayoutSpringWidth);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			float scale = 1 - 0.5f * (Math.abs(mSpringDistanceToLeft) * 1.0f / mCellLayoutSpringWidth);
			matrix.preScale(scale, scale);
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
		}
	}

	/**
	 * 点心桌面特效 摩天轮
	 */
	private void processRotating(Canvas canvas) {
		int shiftingX = (int) (mSpringDistanceToLeft);
		int topPadding = mCellLayout.getTop();
		int location[] = new int[2];
		int center[] = new int[2];
		int dest[] = new int[2];
		dest[0] = mCellLayout.getWidth() / 2;
		dest[1] = mCellLayout.getHeight() / 2 + topPadding;
		if (shiftingX < 0 && shiftingX>=-mCellLayoutSpringWidth) {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {
					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					camera.save();
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					center[1] = location[1] + childView.getHeight() / 2;
					float mDegree = Math.abs(shiftingX) * 360.0f / mCellLayoutSpringWidth;
					camera.rotateZ(-mDegree);
					camera.getMatrix(matrix);
//						matrix.preRotate(mDegree);
					matrix.preTranslate(-mCellLayout.getWidth() / 2, -mCellLayout.getHeight() / 10);
					matrix.preTranslate(location[0], location[1]);

					matrix.preTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.preRotate(-mDegree);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);

					matrix.postScale(springScale, springScale);
					matrix.postTranslate(cellLayoutLocationOffset, 0);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		} else if (shiftingX >= 0 && shiftingX < mCellLayoutSpringWidth)  {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {
					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					camera.save();
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					center[0] = location[0] + childView.getWidth() / 2;
					center[1] = location[1] + childView.getHeight() / 2;
					float mDegree = -Math.abs(shiftingX) * 360.0f /mCellLayoutSpringWidth;
					camera.rotateZ(-mDegree);
					camera.getMatrix(matrix);

//						matrix.preRotate(mDegree);
					matrix.preTranslate(-mCellLayout.getWidth() / 2, -mCellLayout.getHeight() / 10);
					matrix.preTranslate(location[0], location[1]);

					matrix.preTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.preRotate(-mDegree);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);

					matrix.postScale(springScale, springScale);
					matrix.postTranslate(cellLayoutLocationOffset, 0);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		}
	}

	/**
	 * 点心桌面特效 百叶窗
	 */
	private void processLouverWindow(Canvas canvas) {
		int shiftingX = (int) (mSpringDistanceToLeft);
		int location[] = new int[2];
		boolean isWidget = false;
		if (shiftingX < 0 && shiftingX >= -mCellLayoutSpringWidth) {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {
					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					camera.save();
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 90.0f / mCellLayoutSpringWidth;
					camera.rotateX(mDegree);
					camera.getMatrix(matrix);

					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
					matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.postTranslate(location[0], location[1]);
					matrix.postTranslate(-mCellLayout.getWidth() / 2.0f, -mCellLayout.getHeight() / 10.0f);
					matrix.postScale(springScale, springScale);
					matrix.postTranslate(-shiftingX + cellLayoutLocationOffset, 0);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
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
					int alpha = (int) (255.0f - 255.0f / mCellLayoutSpringWidth * Math.abs(shiftingX));
					drawAtCanvasEx(canvas, childView, alpha, mRect, isWidget);

					canvas.restore();
					camera.restore();
				}
			}
		} else if (shiftingX >= 0 && shiftingX < mCellLayoutSpringWidth) {
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {
					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					camera.save();
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 90.0f / mCellLayoutSpringWidth;
					camera.rotateX(mDegree);
					camera.getMatrix(matrix);

					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight() / 2);
					matrix.postTranslate(childView.getWidth() / 2, childView.getHeight() / 2);
					matrix.postTranslate(location[0], location[1]);
					matrix.postTranslate(-mCellLayout.getWidth() / 2.0f, -mCellLayout.getHeight() / 10.0f);
					matrix.postScale(springScale, springScale);
					matrix.postTranslate(-shiftingX+cellLayoutLocationOffset, 0);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f + mCellLayout.getTop());
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
					int alpha = (int) (255.0f - 255.0f / mCellLayoutSpringWidth * Math.abs(shiftingX));
					drawAtCanvasEx(canvas, childView, alpha, mRect, isWidget);

					canvas.restore();
					camera.restore();
				}
			}
		}
	}
	
	/**
	 * 点心桌面特效  旋转翻页
	 */
	private void processPageWave(Canvas canvas) {
		int shiftingX = (int) (mSpringDistanceToLeft);
		int location[] = new int[2];
		if (shiftingX < 0 && shiftingX >= -mCellLayoutSpringWidth) {
				for (int y = 0; y < mCellLayout.getCountY(); y++) {
					for (int x = 0; x < mCellLayout.getCountX(); x++) {
						View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
						if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
							continue;
						camera.save();
						matrix.setTranslate(0, 0);
						location[0] = childView.getLeft();
						location[1] = childView.getTop();
						float mDegree = -shiftingX * 180.0f / mCellLayoutSpringWidth;
						camera.rotateY(-mDegree);
						camera.getMatrix(matrix);
						matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight()/2);
						
						matrix.postTranslate(childView.getWidth() / 2, childView.getHeight()/2);
						matrix.postTranslate(location[0], location[1]);
						matrix.postTranslate(-mCellLayout.getWidth() / 2.0f, -mCellLayout.getHeight() / 10.0f);
						matrix.postScale(springScale, springScale);
						matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f);
						matrix.postTranslate(cellLayoutLocationOffset,mCellLayout.getTop());
						canvas.save();
						canvas.concat(matrix);
						drawAtCanvas(canvas, childView);
						canvas.restore();
						camera.restore();
					}
				}
		} else if (shiftingX >= 0 && shiftingX < mCellLayoutSpringWidth){
			for (int y = 0; y < mCellLayout.getCountY(); y++) {
				for (int x = 0; x < mCellLayout.getCountX(); x++) {
					View childView = mCellLayout.getChildViewByIndex(y * mCellLayout.getCountX() + x);
					if (childView == null || mCellLayout.getWidgetViewByIndex(y * mCellLayout.getCountX() + x) != 1)
						continue;
					camera.save();
					matrix.setTranslate(0, 0);
					location[0] = childView.getLeft();
					location[1] = childView.getTop();
					float mDegree = -shiftingX * 180.0f / mCellLayoutSpringWidth;
					camera.rotateY(-mDegree);
					camera.getMatrix(matrix);
					matrix.preTranslate(-childView.getWidth() / 2, -childView.getHeight()/2);
					matrix.postTranslate(childView.getWidth() / 2, childView.getHeight()/2);
					matrix.postTranslate(location[0], location[1]);
					matrix.postTranslate(-mCellLayout.getWidth() / 2.0f, -mCellLayout.getHeight() / 10.0f);
					matrix.postScale(springScale, springScale);
					matrix.postTranslate(mCellLayout.getWidth() / 2.0f, mCellLayout.getHeight() / 10.0f);
					matrix.postTranslate(cellLayoutLocationOffset,mCellLayout.getTop());
					canvas.save();
					canvas.concat(matrix);
					drawAtCanvas(canvas, childView);
					canvas.restore();
					camera.restore();
				}
			}
		}
	}
	
	/**
	 * 点心特效 旋转木马
	 */
	private void processCarousel(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth /2 ;
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			camera.save();
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			float sx = 1 + Math.abs(mSpringDistanceToLeft)*1.0f/mCellLayoutSpringWidth;
			matrix.postScale(sx, sx);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			matrix.postTranslate(-mSpringDistanceToLeft, 0);
			matrix.postTranslate(Math.abs(mSpringDistanceToLeft), 0);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding, (int) (255 - 255 * Math.abs(mSpringDistanceToLeft)
					/ mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth){
			camera.save();
			camera.getMatrix(matrix);
			camera.restore();
			matrix.preTranslate(-centX, -mCellLayoutHeight / 2 - mCellLayoutTopPadding);
			float sx = 1 - Math.abs(mSpringDistanceToLeft)*1.0f/mCellLayoutSpringWidth;
//				matrix.postScale(sx, sx,  -mCellLayoutWidth / 2,0);
			matrix.postScale(sx, sx);
			matrix.postTranslate(centX, mCellLayoutHeight / 2 + mCellLayoutTopPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding, (int) (255 - 255 * Math.abs(mSpringDistanceToLeft)
					/ mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		}
	}
	
	/**
	 * 立方体
	 */
	private void processCubeOutside(Canvas canvas) {
		float shiftingX = mSpringDistanceToLeft;
		int screenWidth;
		int screenHeight;
		float mDegree = 0;
		screenWidth = mCellLayoutWidth;
		screenHeight = mCellLayoutHeight;
		matrix.setTranslate(0, 0);
		camera.save();
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 显示左边屏幕
			mDegree = 90.0f * shiftingX / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-cellLayoutLocationOffset - screenWidth, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.2f, 0);
			matrix.postTranslate(screenWidth+cellLayoutLocationOffset-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			mDegree = 90.0f * shiftingX / screenWidth;
			camera.rotateY(mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-cellLayoutLocationOffset, -screenHeight / 2);
			matrix.postTranslate(shiftingX * 1.2f, 0);
			matrix.postTranslate(cellLayoutLocationOffset-shiftingX, screenHeight / 2);
			canvas.concat(matrix);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
		}
		camera.restore();
	}
	
	/**
	 * 扇面
	 */
	private void processTurnTable(Canvas canvas) {
		matrix.setTranslate(0, 0);
		int centX = cellLayoutLocationOffset + mCellLayoutWidth / 2;
		camera.save();
		if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
			// 画左屏
			float mDegree = 25.0f * mSpringDistanceToLeft / mCellLayoutSpringWidth;
			camera.rotateZ(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-centX, -mCellLayoutHeight - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight + mCellLayoutTopPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		} else if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
			// 画右屏
			float mDegree = 25.0f * mSpringDistanceToLeft / mCellLayoutSpringWidth;
			camera.rotateZ(-mDegree);
			camera.getMatrix(matrix);
			matrix.preTranslate(-centX, -mCellLayoutHeight - mCellLayoutTopPadding);
			matrix.postTranslate(centX, mCellLayoutHeight + mCellLayoutTopPadding);
			canvas.concat(matrix);
			canvas.saveLayerAlpha(cellLayoutLocationOffset, 0, cellLayoutLocationOffset + mCellLayoutWidth, mCellLayoutHeight + mCellLayoutTopPadding,
					(int) (255 - 255 * Math.abs(mSpringDistanceToLeft) / mCellLayoutSpringWidth), CASCADE_SAVEFLAGS);
			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
			canvas.restore();
			canvas.restore();
		}
		camera.restore();
		
		
		
		
//		matrix.setTranslate(0, 0);
//		camera.save();
//		float R = 0;// 风车转时的半径
//		R = (float) (mCellLayoutSpringWidth / 2 / Math.tan(15 * Math.PI / 180) + mCellLayoutSpringHeightEx / 2f);
//		if (mSpringDistanceToLeft > 0 && mSpringDistanceToLeft <= mCellLayoutSpringWidth) {
//			// 显示右边屏幕
//			float mDegree = mSpringDistanceToLeft * 30.0f / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
//			camera.translate(0, (float) (R), 0);
//			camera.rotateZ(mDegree);
//			camera.translate(0, (float) (-R), 0);
//			camera.getMatrix(matrix);
//
//			matrix.preTranslate(-mCellLayoutWidth / 2, -mCellLayoutHeight  - mCellLayoutTopPadding);
//			matrix.preTranslate(-cellLayoutLocationOffset, 0);
//			matrix.postTranslate(mCellLayoutWidth / 2+cellLayoutLocationOffset, mCellLayoutHeight + mCellLayoutTopPadding);
//
//			canvas.concat(matrix);
//			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
//		} else if (mSpringDistanceToLeft < 0 && mSpringDistanceToLeft >= -mCellLayoutSpringWidth) {
//			// 显示左边屏幕
//			float mDegree = mSpringDistanceToLeft * 30.0f / (mCellLayoutSpringWidth + mSpringCellLayoutGap);
//			camera.translate(0, (float) (R), 0);
//			camera.rotateZ(mDegree);
//			camera.translate(0, (float) (-R), 0);
//			camera.getMatrix(matrix);
//
//			matrix.preTranslate(-mCellLayoutWidth / 2, -mCellLayoutHeight - mCellLayoutTopPadding);
//			matrix.preTranslate(-cellLayoutLocationOffset, 0);
//			matrix.postTranslate(mCellLayoutWidth / 2+cellLayoutLocationOffset, mCellLayoutHeight + mCellLayoutTopPadding);
//			canvas.concat(matrix);
//			mWorkspace.callDrawChild(canvas, mCellLayout, drawingTime);
//		}
//		camera.restore();
	}
}
