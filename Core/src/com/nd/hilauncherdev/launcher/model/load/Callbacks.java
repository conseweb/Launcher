package com.nd.hilauncherdev.launcher.model.load;

import java.util.List;

import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.info.ItemInfo;
import com.nd.hilauncherdev.launcher.info.WidgetInfo;

public interface Callbacks {
		public void startBinding();

		public void bindItems(List<ItemInfo> shortcuts, int start, int end);

		public void finishBindingItems();

		public void bindAppWidget(WidgetInfo info);
		
		public void bindAllApplications(List<ApplicationInfo> apps);

		public void bindAppsAdded(List<ApplicationInfo> apps, String packageName);

		public void bindAppsUpdated(List<ApplicationInfo> apps, String packageName);

		public void bindAppsRemoved(String packageName);

		public boolean isAllAppsVisible();
}
