package com.nd.hilauncherdev.launcher.view.icon.ui.strategy;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconData;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;

public abstract class DrawStrategy {
	abstract public void draw(Canvas canvas, LauncherIconViewConfig config, LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme);
	abstract public DrawPriority getDrawDrawPriority();

}
