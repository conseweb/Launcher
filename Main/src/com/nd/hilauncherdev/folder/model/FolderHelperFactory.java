package com.nd.hilauncherdev.folder.model;

import com.nd.hilauncherdev.folder.model.IFolderHelper;
import com.nd.hilauncherdev.launcher.Launcher;
import com.nd.hilauncherdev.launcher.view.icon.ui.folder.FolderIconTextView;

/** 
 * 类说明:文件夹打开逻辑类工厂
 *  
 * @author pdw 
 * @date 2012-8-8 下午03:22:53 
 */
public class FolderHelperFactory {
	
	private Launcher launcher;
	
	private LauncherFolderHelper launcherFolderHelper ;
	
//	private DrawerFolderHelper drawerFolderHelper ;
	
	private int folderStyle = FolderIconTextView.FOLDER_STYLE_FULL_SCREEN;
	
	public FolderHelperFactory(Launcher launcher) {
		this.launcher = launcher;
		launcherFolderHelper = new LauncherFolderHelper(launcher);
	}
	
	public void init(Launcher launcher, int folderStyle) {
		this.folderStyle = folderStyle;
		launcherFolderHelper.init(launcher, folderStyle);
//		if (drawerFolderHelper != null) {
//			drawerFolderHelper.init(launcher, folderStyle);
//		}
	}
	
//	public void setDrawer(DrawerMainView mDrawer) {
//		if (mDrawer == null) return;
//		drawerFolderHelper = new DrawerFolderHelper(launcher, mDrawer);
//		drawerFolderHelper.init(launcher, folderStyle);
//	}
	
	/**
	 * <p>获取文件逻辑类</p>
	 * 
	 * <p>date: 2012-8-8 下午03:40:10
	 * @param open
	 * @return
	 */
	public IFolderHelper getFolderHelper(int open) {
		switch(open) {
		
//		case FolderSwitchController.OPEN_FOLDER_FROM_DRAWER :
//			return drawerFolderHelper ;
			
		case FolderIconTextView.OPEN_FOLDER_FROM_LAUNCHER :
			return launcherFolderHelper ;
		
		//其他逻辑类
			
		default :
			return null ;
			
		}
	}
	
}
