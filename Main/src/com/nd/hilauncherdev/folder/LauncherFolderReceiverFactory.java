package com.nd.hilauncherdev.folder;

import com.nd.hilauncherdev.launcher.view.icon.ui.folder.BaseFolderReceiver;
import com.nd.hilauncherdev.launcher.view.icon.ui.util.IconTypeFactoryManager.FolderReceiverFactory;

/**
 * @author Michael
 * Date:2014-3-26下午5:13:22
 *
 */
public class LauncherFolderReceiverFactory extends FolderReceiverFactory {

	@Override
	public BaseFolderReceiver getFolderReceiver() {
		return new FolderIconReceiver();
	}

	

}
