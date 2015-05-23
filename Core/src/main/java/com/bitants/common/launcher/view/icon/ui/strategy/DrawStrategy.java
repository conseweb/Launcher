package com.bitants.common.launcher.view.icon.ui.strategy;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.bitants.common.launcher.view.icon.ui.LauncherIconViewConfig;
import com.bitants.common.launcher.view.icon.ui.LauncherIconData;

public abstract class DrawStrategy {
	abstract public void draw(Canvas canvas, LauncherIconViewConfig config, LauncherIconData data, Rect iconRect, Rect maskRect, boolean isLargeIconMode, boolean isDefaultTheme);
	abstract public DrawStragegyFactory.DrawPriority getDrawDrawPriority();

}
