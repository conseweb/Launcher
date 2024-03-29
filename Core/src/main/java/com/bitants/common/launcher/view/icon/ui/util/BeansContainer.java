/**
 *
 */
package com.bitants.common.launcher.view.icon.ui.util;

import com.bitants.common.launcher.view.icon.ui.util.IconTypeFactoryManager.FolderReceiverFactory;
import com.bitants.common.launcher.view.icon.ui.util.IconTypeFactoryManager.IconTypeFactory;

/**
 * 
 * 从外部注入的bean的统一管理
 *
 */
public class BeansContainer {

	private static BeansContainer instance;
	
	private BeansContainer container;
	
	public static BeansContainer getInstance(){
		if(instance == null){
			instance = new BeansContainer();
		}
		return instance;
	}
	
	private IconTypeFactory defaultIconTypeFactory = new IconTypeFactory();
	
	
	private FolderReceiverFactory  folderReceiverFactory = new FolderReceiverFactory();

	public IconTypeFactory getDefaultIconTypeFactory() {
		return defaultIconTypeFactory;
	}

	public void setDefaultIconTypeFactory(IconTypeFactory defaultIconTypeFactory) {
		this.defaultIconTypeFactory = defaultIconTypeFactory;
	}

	public FolderReceiverFactory getFolderReceiverFactory() {
		return folderReceiverFactory;
	}

	public void setFolderReceiverFactory(FolderReceiverFactory folderReceiverFactory) {
		this.folderReceiverFactory = folderReceiverFactory;
	}

}
