package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;
import android.view.View;

import com.nd.launcherdev.launcher.Workspace;
import com.nd.launcherdev.launcher.info.FolderInfo;
import com.nd.launcherdev.launcher.screens.CellLayout;
import com.nd.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;



public class DataTest {

	
	public static List<FolderInfo> getFolderInfoList(Workspace mWorkspace){
		List<FolderInfo> folderInfoList = new ArrayList<FolderInfo>();
		//获取文件夹列表
		for(int i=0; i<mWorkspace.getChildCount(); i++){
			CellLayout mCellLayout = mWorkspace.getCellLayoutAt(i);
			for(int j=0; j<mCellLayout.getChildCount(); j++){
				View child = mCellLayout.getChildAt(j);
				if(child instanceof FolderIconTextView){
					FolderIconTextView folderIconView = (FolderIconTextView) child;
					folderInfoList.add(folderIconView.getUserFolderInfo());
				}
			}
		}
		sort(folderInfoList);
		//打印信息
		for(FolderInfo folderInfo:folderInfoList){
			Log.e("zhenghonglin","============="+folderInfo.title+","+folderInfo.screen+","+folderInfo.cellY+","+folderInfo.cellX);
//			for(ApplicationInfo appInfo:folderInfo.contents){
//				Log.e("zhenghonglin",""+appInfo.title);
//			}
		}
		return folderInfoList;
	}
	
	private static void sort(List<FolderInfo> folderInfoList){
		if(folderInfoList == null || folderInfoList.size() ==0){
			return;
		}
		Collections.sort(folderInfoList, new Comparator<FolderInfo>(){

			@Override
			public int compare(FolderInfo lhs, FolderInfo rhs) {
				//先比较屏幕数 再比较  celly cellx
				if(lhs.screen == rhs.screen){
					if(lhs.cellY == rhs.cellY){
						return new Integer(lhs.cellX).compareTo(new Integer(rhs.cellX));
					} else {
						return new Integer(lhs.cellY).compareTo(new Integer(rhs.cellY));
					}
				} else {
					return new Integer(lhs.screen).compareTo(new Integer(rhs.screen));
				}
			}
		});
	}
	
}
