package com.bitants.common.framework;

import android.view.KeyEvent;

/***
 * 需要响应按键的View实现
 */
public interface OnKeyDownListenner {
	 /**
	  * 响应按键
	  * @param keyCode
	  * @param event
	  */
	 public boolean onKeyDownProcess(int keyCode, KeyEvent event);
}