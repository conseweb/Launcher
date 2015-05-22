package com.bitants.launcherdev.folder.model;

import java.util.List;

import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.launcher.info.ApplicationInfo;
import com.bitants.launcherdev.launcher.info.FolderInfo;

/**
 * 文件夹帮助类
 */
public class FolderHelper {
	/**
	 * 从文件夹数据中移除拖拽的应用
	 * @param apps
	 * @param info
	 * @return 是否成功从apps中移除info
	 */
	public static boolean removeDragApp(List<? extends ICommonDataItem> apps,ApplicationInfo info) {
		if (apps.size() == 0)
			return false ;
		//顺序查找该应用
		int index = apps.indexOf(info);
		if (index == -1)
			return false ;
		//逆向查找该应用
		int index2 = apps.lastIndexOf(info);
		if (index == index2) { //该文件夹中没有相同应用，有些文件夹可能放置了2个或者2个以上的相同应用，如放置了2个qq
			apps.remove(index);
			return true ;
		} else { //该文件夹放置了2个或者2个以上相同应用
			//查找有相同id的一个
			int theOne = -1 ;
			for (int i = 0 ; i < apps.size() ; i++) {
				final ApplicationInfo app = (ApplicationInfo) apps.get(i) ;
				if (app.equals(info) && app.id == info.id && app.itemType == info.itemType) {
					theOne = i ;
					break;
				}
			}
			if (theOne != -1) {
				apps.remove(theOne);
				return true ;
			}
		}
		return false ;
	}
	
	/**
	 * 从文件夹数据中移除拖拽的应用
	 * @param folderInfo
	 * @param info
	 * @return 是否成功从folderInfo中移除info
	 */
	public static boolean removeDragApp(FolderInfo folderInfo,ApplicationInfo info) {
		final List<ApplicationInfo> list = folderInfo.contents;
		return removeDragApp(list, info);
	}
}
