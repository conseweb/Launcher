package com.nd.hilauncherdev.launcher.screens.dockbar;

import com.nd.hilauncherdev.launcher.config.BaseConfig;
import com.nd.hilauncherdev.launcher.info.ItemInfo;
import com.nd.hilauncherdev.launcher.view.icon.ui.folder.FolderIconTextView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

/**
 * represent one screen of dockbar
 * 
 * @author pdw
 * @date 2012-5-18 下午05:08:40 
 */
public class DockbarCellLayout extends ViewGroup {
	public DockbarCellLayout(Context context) {
		super(context);
	}

	public DockbarCellLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DockbarCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		TypedArray a = context.obtainStyledAttributes(attrs,
//				R.styleable.MaindockCellLayout, defStyle, 0);
//		mMinCellSize = (int) a.getDimension(
//				R.styleable.MaindockCellLayout_minDockCellSize, 72);
//		mCellCounts = a.getInteger(R.styleable.MaindockCellLayout_maxDockCells, 5);
//		a.recycle();
		setAlwaysDrawnWithCacheEnabled(false);
	}

	@Override
	public void setChildrenDrawingCacheEnabled(boolean enabled) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View view = getChildAt(i);
			view.setDrawingCacheEnabled(enabled);
			view.buildDrawingCache(enabled);
			if (enabled)
				view.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
		}
		super.setChildrenDrawingCacheEnabled(enabled);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		if(count == 0) return;
		if(count > BaseMagicDockbar.DEFAULT_SCREEN_ITEM_COUNT){
			count = BaseMagicDockbar.DEFAULT_SCREEN_ITEM_COUNT;
		}
		int cellW = (r - l) / count;
		int rawCellW = (r - l) / BaseMagicDockbar.DEFAULT_SCREEN_ITEM_COUNT;
		//当dock栏上图标不满5个时，布局增加padding
		int padding = (cellW - rawCellW) / 2;
		if(padding < 0)
			padding = 0;
		
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != VISIBLE) 
				continue;
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) child
					.getLayoutParams();
			if(lp.isOnReorderAnimation){//还处于动画中时，不布局
				continue;
			}
			if(lp.isOnPending){
				int left = lp.cellX * lp.width;
				child.layout(left, t, left + lp.width, b);
			}else{
				int left = lp.cellX * cellW + padding;
				child.layout(left, t, left + rawCellW, b);
			}
		}
	}


	public void addView(View child, int cellX, int cellY, int spanX, int spanY){
		DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams) child.getLayoutParams();
		if (lp == null) {
			lp = new DockbarCellLayout.LayoutParams(cellX, cellY, spanX, spanY);
		} else {
			lp.setup(cellX, cellY, spanX, spanY);
		}
		
		addView(child, -1, lp);
	}
	
	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new DockbarCellLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof DockbarCellLayout.LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p) {
		return new DockbarCellLayout.LayoutParams(p);
	}

	int getMarginLeft() {
		return DockbarCellLayoutConfig.getMarginLeft();
	}

	int getMarginTop() {
		return DockbarCellLayoutConfig.getMarginTop();
	}

	int getMarginRight() {
		return DockbarCellLayoutConfig.getMarginRight();
	}

	int getMarginBottom() {
		return DockbarCellLayoutConfig.getMarginBottom();
	}
	
	public void resetChildLayout(){
		int count = getChildCount();
		for(int i = 0; i < count; i ++){
			View v = getChildAt(i);
			ItemInfo item = (ItemInfo) v.getTag();
			DockbarCellLayout.LayoutParams lp = (DockbarCellLayout.LayoutParams)v.getLayoutParams();
			if(item == null || lp == null)
				continue;
			
			lp.setup(lp.cellX, lp.cellY, lp.spanX, lp.spanY);
			if(BaseConfig.isOnScene()){
				int[] xy = DockbarCellLayoutConfig.getXY(lp.cellX, lp.cellY);
				int[] wh = DockbarCellLayoutConfig.spanXYToWh(lp.spanX, lp.spanY);
				item.cellX = xy[0];
				item.cellY = xy[1];
				item.spanX = wh[0];
				item.spanY = wh[1];
			}
			
			if(v instanceof FolderIconTextView){
				((FolderIconTextView)v).setInitValueOnDraw(true);
			}
			 
		}
	}
	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams {

		/**
		 * Horizontal location of the item in the grid. based on zero
		 */
		@ViewDebug.ExportedProperty
		public int cellX;
		public int cellY;
		
		public int spanX;
		public int spanY;

		// X coordinate of the view in the layout.
		@ViewDebug.ExportedProperty
		int x;
		// Y coordinate of the view in the layout.
		@ViewDebug.ExportedProperty
		int y;

		//用于标示是否已经动画移到其它位置
		public boolean isOnPending = false;
		public boolean isOnReorderAnimation = false;
		public int preCellX = -1;
		
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		public LayoutParams(int cellX) {
			super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			this.cellX = cellX;
		}
		
		public LayoutParams(int cellX, int cellY, int spanX, int spanY) {
			super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			setup(cellX, cellY, spanX, spanY);
		}
		
		public void setup(int cellX, int cellY, int spanX, int spanY){
			this.spanX = 1;
			this.spanY = 1;
			if(BaseConfig.isOnScene()){
				this.cellX = cellX / DockbarCellLayoutConfig.getCellWidth();
				this.cellY = cellY / DockbarCellLayoutConfig.getCellHeight();
				this.x = cellX;
				this.y = cellY;
				this.width = DockbarCellLayoutConfig.getCellWidth();
				this.height = DockbarCellLayoutConfig.getCellHeight();
			}else{
				this.cellX = cellX;
				this.cellY = cellY;
				this.width = DockbarCellLayoutConfig.getCellWidth();
				this.height = DockbarCellLayoutConfig.getCellHeight();
			}
			
		}
		
		public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan, int x, int y, int width, int height) {
			super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			
		}
	}
	
}
