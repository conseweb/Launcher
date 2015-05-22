package com.bitants.launcherdev.folder;

import com.bitants.launcherdev.launcher.view.icon.ui.folder.BaseFolderReceiver;
import com.bitants.launcherdev.launcher.view.icon.ui.util.IconTypeFactoryManager.FolderReceiverFactory;

/**
 *
 */
public class LauncherFolderReceiverFactory extends FolderReceiverFactory {

	@Override
	public BaseFolderReceiver getFolderReceiver() {
		return new FolderIconReceiver();
	}

	

}
