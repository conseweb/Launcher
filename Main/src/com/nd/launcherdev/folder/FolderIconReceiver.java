package com.nd.launcherdev.folder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;

import com.nd.launcherdev.kitset.util.ThreadUtil;
import com.nd.launcherdev.launcher.config.BaseConfig;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.info.FolderInfo;
import com.nd.launcherdev.launcher.view.icon.ui.folder.BaseFolderReceiver;
import com.nd.launcherdev.launcher.view.icon.ui.folder.BaseFolderReceiver;
import com.nd.launcherdev.launcher.view.icon.ui.folder.FolderIconTextView;
import com.nd.launcherdev.util.UIHandlerFactory;
import com.nd.launcherdev.launcher.info.FolderInfo;

public class FolderIconReceiver extends BaseFolderReceiver {

	public void refresh(final FolderIconTextView view) {
		final FolderInfo mInfo = view.mInfo;
		if (mInfo != null && mInfo.contents != null) {
			final int end = mInfo.contents.size() <= 9 ? mInfo.contents.size(): 9;
			final List<ApplicationInfo> previewList = new ArrayList<ApplicationInfo>();
			for (int i = 0; i < end; i++) {
				previewList.add(mInfo.contents.get(i));
			}
			Context mContext = BaseConfig.getApplicationContext();
			ThreadUtil.execute(UIHandlerFactory.getRefreshIconRunnable(4,
					mContext, Looper.myQueue(), previewList, new IdleHandler() {
						public boolean queueIdle() {
							view.iconLoaded = true;
							view.setText(mInfo.title);
							view.invalidate();
							return false;
						}}));
		}
	}
}
