package com.bitants.launcherdev.launcher.support;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.bitants.launcherdev.kitset.GpuControler;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.launcher.BaseLauncher;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.model.BaseLauncherModel;
import com.bitants.launcherdev.launcher.model.BaseLauncherSettings;
import com.bitants.launcherdev.launcher.screens.CellLayout;
import com.bitants.launcherdev.launcher.screens.ScreenViewGroup;
import com.bitants.launcherdev.launcher.view.ResizeWidgetView;
import com.bitants.launcherdev.launcher.view.ResizeWidgetView.OnClickOutsideListener;

/**
 * Widget大小编辑 <br>
 * Author:ryan <br>
 * Date:2012-5-31上午10:17:58
 */
public class LauncherWidgetEditHelper {
	public boolean mIsWidgetEditMode;
	private ResizeWidgetView mWidgetEditor;
	private ItemInfo mBaseWidgetInfo;

	private BaseLauncher mLauncher;

	public LauncherWidgetEditHelper(BaseLauncher mLauncher) {
		this.mLauncher = mLauncher;
	}

	/**
	 * 编辑调整特定类型的widget的大小
	 */
	public void editWidget(final View widget) {
		final ScreenViewGroup mWorkspace = mLauncher.getScreenViewGroup();
		final BaseLauncherModel mModel = mLauncher.getLauncherModel();
		if (mWorkspace == null)
			return;

		mIsWidgetEditMode = true;
		final CellLayout cellLayout = (CellLayout) mWorkspace.getChildAt(mWorkspace.getCurrentScreen());
		if (cellLayout == null) return;
		
		final int cellLayoutTopMargin = mWorkspace.getTopPadding();
		// 当前屏幕下的所有项缓存，每次启动widget编辑时获取一次
		final ArrayList<ItemInfo> curScreenItems = mModel.getItemsByScreen(mLauncher, cellLayout.getScreen());
		
		mBaseWidgetInfo = (ItemInfo) widget.getTag();
		final Intent motosize = new Intent("com.motorola.blur.home.ACTION_SET_WIDGET_SIZE");
		motosize.putExtra("com.motorola.blur.home.EXTRA_NEW_WIDGET", true);
		final int minw = cellLayout.getWidth() / cellLayout.getCountX();
		final int minh = cellLayout.getHeight() / cellLayout.getCountY();
		mWidgetEditor = new ResizeWidgetView(mLauncher);
		GpuControler.enableSoftwareLayers(mWidgetEditor);
		// Create a default HightlightView if we found no face in the
		// picture.
		int leftMargin = CellLayout.getMarginLeft();
		int topMargin = CellLayout.getMarginTop();
		int rightMargin = CellLayout.getMarginRight();
		final Rect cellLayoutRect = new Rect(leftMargin, topMargin, 
				cellLayout.getWidth() - leftMargin - rightMargin, 
				topMargin+cellLayout.getHeight());

		int width = (mBaseWidgetInfo.spanX * minw);
		int height = (mBaseWidgetInfo.spanY * minh);
		final int x = mBaseWidgetInfo.cellX * minw;
		final int y = mBaseWidgetInfo.cellY * minh;
		
		final int[] spans = new int[] { 1, 1 };
		final int[] position = new int[] { 1, 1 };
		final CellLayout.LayoutParams lp = (CellLayout.LayoutParams) widget.getLayoutParams();
		RectF widgetRect = new RectF(x + ScreenUtil.dip2px(mLauncher, 5), y + ScreenUtil.dip2px(mLauncher, 10) + cellLayoutTopMargin,
				x + width - ScreenUtil.dip2px(mLauncher, 5), y + height+ cellLayoutTopMargin);
		//初始化
		mWidgetEditor.setup(null, cellLayoutRect, widgetRect, false, false, minw - 10, minh - 10);
		mLauncher.mDragLayer.addView(mWidgetEditor);
		//设置Widget编辑框重绘监听器
		mWidgetEditor.setOnValidateSizingRect(new ResizeWidgetView.OnSizeChangedListener() {
			@Override
			public void onTrigger(RectF r) {
				float relativeLeft = r.left;
				float relativeTop = r.top - cellLayoutTopMargin;
				final float left = Math.round(relativeLeft / minw) * minw + ScreenUtil.dip2px(mLauncher, 5);
				final float top = Math.round(relativeTop / minh) * minh + ScreenUtil.dip2px(mLauncher, 10) + cellLayoutTopMargin;
				final float right = left + (Math.max(Math.round(r.width() / (minw)), 1) * minw) - ScreenUtil.dip2px(mLauncher, 5);
				final float bottom = top + (Math.max(Math.round(r.height() / (minh)), 1) * minh) - ScreenUtil.dip2px(mLauncher, 10);

				r.set(left, top, right, bottom);
			}
		});
		//设置Widget缩放监听器
		final Rect checkRect = new Rect();
		final long _itemId = mBaseWidgetInfo.id;
		mWidgetEditor.setOnSizeChangedListener(new ResizeWidgetView.OnSizeChangedListener() {
			@Override
			public void onTrigger(RectF r) {
				int[] tmpspans = { Math.max(Math.round(r.width() / (minw)), 1), Math.max(Math.round(r.height() / (minh)), 1) };
				float relativeLeft = r.left;
				float relativeTop = r.top - cellLayoutTopMargin;
				int[] tmpposition = { Math.round(relativeLeft / minw), Math.round(relativeTop / minh) };
				checkRect.set(tmpposition[0], tmpposition[1], tmpposition[0] + tmpspans[0], tmpposition[1] + tmpspans[1]);

				// 计算位置是否被占用
				boolean ocupada = isOccupiedArea(curScreenItems, _itemId, checkRect);
				if (ocupada) {
					mWidgetEditor.setColliding(true);
					return;
				}
				
				mWidgetEditor.setColliding(false);
				if (tmpposition[0] != position[0] || tmpposition[1] != position[1] || tmpspans[0] != spans[0] || tmpspans[1] != spans[1]) {
					position[0] = tmpposition[0];
					position[1] = tmpposition[1];
					spans[0] = tmpspans[0];
					spans[1] = tmpspans[1];
					lp.setup(position[0], position[1], spans[0], spans[1], cellLayout.getCellWidth(), cellLayout.getCellHeight());
					widget.setLayoutParams(lp);
					mBaseWidgetInfo.cellX = position[0];
					mBaseWidgetInfo.cellY = position[1];
					mBaseWidgetInfo.spanX = spans[0];
					mBaseWidgetInfo.spanY = spans[1];
					widget.setTag(mBaseWidgetInfo);
					// send the broadcast
					motosize.putExtra("spanX", spans[0]);
					motosize.putExtra("spanY", spans[1]);
					mLauncher.sendBroadcast(motosize);
					Log.d("RESIZEHANDLER", "sent resize broadcast");
				}
			}
		});
		//设置点击Widget外区域监听器
		mWidgetEditor.setOnClickOutsideListener(new OnClickOutsideListener() {
			
			@Override
			public void onTrigger() {
				stopWidgetEdit();
			}
		});
	}

	boolean isOccupiedArea(final ArrayList<ItemInfo> items, long id, Rect rect) {
		Rect r = new Rect();
		for (int i = 0; i < items.size(); i++) {
			ItemInfo it = items.get(i);
			if (id == it.id) { // 本身
				continue;
			}

			r.set(it.cellX, it.cellY, it.cellX + it.spanX, it.cellY + it.spanY);
			if (rect.intersect(r)) {
				return true;
			}
		}
		return false;
	}

	public void stopWidgetEdit() {
		mIsWidgetEditMode = false;
		if (mBaseWidgetInfo != null) {
			mBaseWidgetInfo.container = BaseLauncherSettings.Favorites.CONTAINER_DESKTOP;
			BaseLauncherModel.resizeItemInDatabase(mLauncher, mBaseWidgetInfo);
			mBaseWidgetInfo = null;
		}
		// Remove the resizehandler view
		if (mWidgetEditor != null) {
			mLauncher.mDragLayer.removeView(mWidgetEditor);
			mWidgetEditor = null;
		}
	}
}
