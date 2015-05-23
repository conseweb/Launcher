package com.bitants.common.framework;

import java.util.ArrayList;

import android.view.View;
import android.view.animation.Animation;

import com.bitants.common.app.SerializableAppInfo;
import com.bitants.common.folder.model.IFolderHelper;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.info.FolderInfo;
import com.bitants.common.launcher.view.DragView;

/**
 * 集中管理回调函数
 */
public class AnyCallbacks {
	/**
	 * 监听更新
	 */
	public interface Observer {
		public void onUpdate(int state);
	}
	
	/**
	 * 文件夹相关操作回调
	 */
	public interface IFolderCallback{
		/**
		 * 程序从文件夹中移除
		 * @param app 移除的应用程序
		 */
		public void onAppRemove(ApplicationInfo app);
	}
	
	/**
	 * 切割画布回调接口
	 */
	public interface IClipCanvas {
		/**
		 * 展开动画类型
		 */
		public static final int ANIM_TYPE_EXPAND = 1;

		/**
		 * 合并动画类型
		 */
		public static final int ANIM_TYPE_PINCH = 2;

		/**
		 * 不产生动画
		 */
		public static final int ANIM_TYPE_NONE = 0;

		/**
		 * 开始动画,自定义动画
		 * @param animType 动画类型
		 */
		public void startAnim(int animType);

		/**
		 * 设置上半部分位移的距离
		 * @param mTopDistance 距离
		 */
		public void setTopDistance(int mTopDistance);

		/**
		 * 设置下半部分位移的距离
		 * @param mBottomDistance 距离
		 */
		public void setBottomDistance(int mBottomDistance);

		/**
		 * 设置切割的高度
		 * @param mClipHeight 高度
		 */
		public void setClipHeight(int mClipHeight);

		/**
		 * 设置动画监听器
		 * @param mAnimListener 监听器
		 */
		public void setmAnimListener(ClipCanvasAnimationListener mAnimListener);

		/**
		 * 获取动画类型
		 * @return the mAnimType
		 */
		public int getAnimType();

		/**
		 * 设置动画类型
		 * @param animType 动画类型
		 */
		public void setAnimType(int animType);

		/**
		 * 开始系统动画
		 * @param anim
		 */
		public void startAnimation(Animation anim);

		/**
		 * 设置文件夹辅助类
		 * @param mFolderHelper 文件夹辅助类
		 */
		public void setFolderHelper(IFolderHelper mFolderHelper);

		/**
		 * 设置顶部边缘
		 * @param mTopMargin
		 */
		public void setTopMargin(int mTopMargin);

	}
	
	/**
	 * 桌面文件夹撕裂动画回调接口
	 */
	public interface ClipCanvasAnimationListener{
		/**
		 * 动画开始
		 * @param animType 动画类型
		 */
		public void onAnimStart(int animType);
		/**
		 * 动画结束
		 * @param animType 动画类型
		 */
		public void onAnimEnd(int animType);
	}
	/**
	 * 通用滑屏组件回调接口
	 */
	public interface CommonSlidingViewCallback {
		/**
		 * 通用滑屏组件view被长摁
		 * @param v
		 */
		public void onViewLongClick(View v);
		/**
		 * 通用滑屏组件view被点击
		 * @param v
		 */
		public void onViewClick(View v);
		
		/**
		 * app添加到文件夹中
		 * @param folderInfo 文件夹信息
		 * @param list
		 */
		public void onAppAdded2Folder(FolderInfo folderInfo,
				ArrayList<SerializableAppInfo> list);
		
	}
	
	/**
	 * 从文件夹向外拖拽回调
	 */
	public interface OnFolderDragOutCallback{
		
		/**
		 * 向外长时间拖拽，文件夹关闭
		 * @param folder 文件夹信息
		 * @param items
		 */
		public void onDragOut(FolderInfo folder, ArrayList<Object> items);
		
		/**
		 * 在关闭往外拖拽关闭文件夹之前回调
		 * @param folder 文件夹信息
		 * @param items
		 */
		public void onBeforeDragOut(FolderInfo folder, ArrayList<Object> items);
		
		/**
		 * 通过fling手势往文件夹外仍，文件夹不会关闭
		 * @param folder 文件夹信息
		 * @param items
		 * @return 是否fling成功
		 */
		public boolean onFlingOut(FolderInfo folder, ArrayList<Object> items);
		
		/**
		 * 拖拽松手后回调
		 * @param dragTarget
		 * @param folder 文件夹信息
		 * @param items
		 */
		public void onDrop(View dragTarget,FolderInfo folder, ArrayList<Object> items);
	}

	/**
	 * Drag事件回调
	 */
	public interface OnDragEventCallback {
		/**
		 * 进入View的范围时触发的事件
		 * @param view
		 */
		public void onEnterAni(DragView view);

		/**
		 * 移出View的范围时触发的事件
		 * @param view
		 */
		public void onExitAni(DragView view);

		/**
		 * 拖入View放开时触发的事件
		 * @param view
		 */
		public void onDropAni(DragView view);
		
		/**
		 * 是否处于进入或退出文件夹动画中
		 */
		public boolean isOnMergeFolderAni();
	}
	
}
