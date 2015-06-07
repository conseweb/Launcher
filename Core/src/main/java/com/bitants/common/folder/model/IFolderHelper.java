package com.bitants.common.folder.model;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.bitants.common.app.SerializableAppInfo;
import com.bitants.common.launcher.info.FolderInfo;

/**
 *  文件夹打开接口
 */
public interface IFolderHelper {
	/**
	 * 用于做撕裂的透明背景 <br>
	 *
	 * @param outVRect
	 *            点击的文件夹视图
	 * @param hilightClickView
	 *            是否高亮点击文件夹视图
	 * @return
	 */
	public Bitmap onPrepareAlphaBackground(int[] loc, Rect outVRect,
			boolean hilightClickView);

	public void setAlpha(int alpha);

	public void setIsFullScreen(boolean fullScreen);

	/**
	 * 打开文件夹前调用 <br>
	 */
	public void onPreFolderOpen();

	/**
	 * 打开文件夹调用 <br>
	 *
	 * @param folderStyle 文件夹风格
	 */

	public void onFolderOpen(int folderStyle);

	/**
	 * 关闭文件夹回调 <br>
	 *
	 * @param folder 关闭的文件夹信息
	 * @param isAddMore 是否是由添加app到文件夹引起的文件夹关闭
	 */
	public void onFolderClose(FolderInfo folder,boolean isAddMore);
	
	public void setClickView(View view);
	
	/**
	 * 批量添加app到文件夹中
	 * 
	 * @param list
	 */
	public void addApps2Folder(FolderInfo folderInfo,ArrayList<SerializableAppInfo> list);
	
	/**
	 * 重命名文件夹
	 * 
	 * @param folderInfo
	 * @param name
	 */
	public void renameFolder(FolderInfo folderInfo, String name);
	
	/**
	 * <p>打开文件夹回调</p>
	 * 
	 * @param delayMilli
	 */
	public void openFolderCallback(int delayMilli) ;
	
	/**
	 * <p>删除文件夹回调</p>
	 * 
	 */
	public void deleteFolderCallback() ;
	
	/**
	 * <p>加密文件夹成功后回调</p>
	 * 
	 */
	public void encriptFolderCallback() ;
	
	/**
	 * <p>切割上半部分动画</p>
	 * 
	 * @param canvas 画布
	 * @param clip 切割矩形
	 * @param animDistance 动画移动位移
	 * @param topMargin 文件夹布局顶部margin
	 * @param alpha 撕裂层alpha值
	 */
	public void clipTop(Canvas canvas,Rect clip,int animDistance,int topMargin,int alpha) ;
	
	/**
	 * <p>切割下半部分动画</p>
	 * 
	 * @param canvas 画布
	 * @param clip 切割矩形
	 * @param animDistance 动画移动位移
	 * @param topMargin 文件夹布局顶部margin
	 * @param alpha 撕裂层alpha值
	 */
	public void clipBottom(Canvas canvas,Rect clip,int animDistance,int topMargin,int alpha) ;
	
	/**
	 * <p>获取最终的渐变透明值</p>
	 * 
	 * @return
	 */
	public int getAlpha();
}
