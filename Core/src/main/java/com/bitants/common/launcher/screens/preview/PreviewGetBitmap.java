package com.bitants.common.launcher.screens.preview;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.bitants.common.launcher.screens.CellLayout;
import com.bitants.common.launcher.screens.ScreenViewGroup;

public class PreviewGetBitmap {
	private static Canvas canvas;
	private static Bitmap bitmap;
	private static int witch=0;
	private static int hight=0;
	
	
	public static Bitmap getPreviewBitmap(ScreenViewGroup mwGroup){
		CellLayout cellLayout=null;
		
		if(mwGroup==null){
			return null;
		}
		cellLayout=mwGroup.getCurrentCellLayout();
		if(cellLayout==null){
			return null;
		}
		witch=cellLayout.getWidth();
		hight=cellLayout.getHeight();
		if(witch<=0){
			witch=480;
		}
		if(hight<=0){
			hight=800;
		}
		
		if(bitmap!=null){
			if(!bitmap.isRecycled()){
				bitmap.recycle();
			}
			bitmap=null;
		}
		bitmap = Bitmap.createBitmap(witch, hight,  Bitmap.Config.RGB_565);
		
		if(canvas==null){
			canvas=new Canvas(bitmap);
		}
		if (cellLayout != null) {
			cellLayout.dispatchDraw(canvas);
		}
		
		
		
		return bitmap;
	}
}
