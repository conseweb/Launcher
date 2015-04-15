package com.bitants.launcherdev.widget;

import android.graphics.drawable.Drawable;

import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.launcher.info.ItemInfo;
import com.bitants.launcherdev.launcher.info.ItemInfo;

public class LauncherItemInfo extends ItemInfo implements ICommonDataItem {

	public static final int CATAGORY_SYSTEM_WIDGET = 0;
	public static final int CATAGORY_CUSTOM_WIDGET = 1;
	public static final int CATAGORY_CUSTOM_SHORTCUT = 2;
	public static final int CATAGORY_SYSTEM_SHORTCUT = 3;
	public static final int CATAGORY_MORE_WIDGET = 4;
	public static final int CATAGORY_MORE_DOWNLOAD = 5;

	/**
	 * 标题
	 */
	protected String title = "";
	
	/**
     * 标题拼音 
     */
	protected CharSequence pinyin;

	/**
	 * 提示
	 */
	protected String tip;

	/**
	 * 预览图
	 */
	protected Drawable previewImage;

	protected int type;

	/**
	 * 类别序号
	 */
	protected int catagoryNo;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Drawable getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(Drawable previewImage) {
		this.previewImage = previewImage;
	}

	public int getSpanX() {
		return spanX;
	}

	public void setSpanX(int spanX) {
		this.spanX = spanX;
	}

	public int getSpanY() {
		return spanY;
	}

	public void setSpanY(int spanY) {
		this.spanY = spanY;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCatagoryNo() {
		return catagoryNo;
	}

	public void setCatagoryNo(int catagoryNo) {
		this.catagoryNo = catagoryNo;
	}

	public Drawable loadPreviewImage() {
		return null;
	}
	
	public CharSequence getPinyin() {
		return pinyin;
	}

	public void setPinyin(CharSequence pinyin) {
		this.pinyin = pinyin;
	}

	@Override
	public int getPosition() {
		return 0;
	}

	@Override
	public void setPosition(int position) {
	}

	@Override
	public boolean isFolder() {
		return false;
	}
}
