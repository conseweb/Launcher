package test;

import com.nd.launcherdev.integratefoler.IntegrateFolder;
import com.nd.launcherdev.launcher.Launcher;

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
