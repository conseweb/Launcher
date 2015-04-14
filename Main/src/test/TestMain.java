package test;

import com.nd.hilauncherdev.integratefoler.IntegrateFolder;
import com.nd.hilauncherdev.launcher.Launcher;

public class TestMain {

	public static void openFolder(Launcher mLauncher){
		IntegrateFolder folder = IntegrateFolder.fromXml(mLauncher);
		folder.isOpened = true;
		if (folder.getParent() == null) {
			//
			mLauncher.getDragLayer().addView(folder);
			//
//			mDragController.addDropTarget((DropTarget) folder);
		} 
	}
	
	
}
