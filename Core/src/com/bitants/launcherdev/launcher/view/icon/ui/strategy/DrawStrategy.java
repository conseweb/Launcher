package com.bitants.launcherdev.launcher.view.icon.ui.strategy;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconData;
import com.bitants.launcherdev.launcher.view.icon.ui.LauncherIconViewConfig;

public abstract class DrawStrategy {
	abstract public void draw(Canvas canvas, LauncherIconViewConfig config, LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme);
	abstract public DrawStragegyFactory.DrawPriority getDrawDrawPriority();

}
