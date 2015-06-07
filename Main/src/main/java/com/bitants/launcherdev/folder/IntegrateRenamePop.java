package com.bitants.launcherdev.folder;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout.LayoutParams;

import com.bitants.launcherdev.integratefoler.IntegrateFolderTitleStrip;
import com.bitants.launcher.R;


/**
 * 修改文件夹名称
 * 
 * @ferris 2015/05/11
 * 
 */
public class IntegrateRenamePop {
	private PopupWindow menuPopupWindow;
	private Context mContext;
	private EditText edt_rename;
	private IntegrateFolderTitleStrip pagerTabStrip;
	private CharSequence folder_name;
	
	private FolderReNameSuccessListem folderReNameSuccessListem;
	public FolderReNameSuccessListem getFolderReNameSuccessListem() {
		return folderReNameSuccessListem;
	}

	public void setFolderReNameSuccessListem(
			FolderReNameSuccessListem folderReNameSuccessListem) {
		this.folderReNameSuccessListem = folderReNameSuccessListem;
	}

	public IntegrateRenamePop(Context mContext) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
	}

	/**
	 * 隐藏文件夹名称修改
	 */
	public void dismiss() {
		if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
			menuPopupWindow.dismiss();
		}
	}

	public PopupWindow getMenuPopupWindow() {
		return menuPopupWindow;
	}

	public void setMenuPopupWindow(PopupWindow menuPopupWindow) {
		this.menuPopupWindow = menuPopupWindow;
	}

	/**
	 * 显示文件夹名称修改
	 * 
	 * @param view
	 */
	public void toggleMenu(IntegrateFolderTitleStrip view,
			CharSequence str_rename) {
		pagerTabStrip = view;
		folder_name=str_rename;
		if (menuPopupWindow == null) {
			setupMenuPopup();
		}
		setTitle();
		if (menuPopupWindow.isShowing()) {
			menuPopupWindow.dismiss();
		} else {
			int loacation[] = new int[2];
			pagerTabStrip.getLocationOnScreen(loacation);
			pagerTabStrip.setVisibility(View.INVISIBLE);
			menuPopupWindow.showAtLocation(pagerTabStrip, 0, loacation[0],
					loacation[1]);
		}
	}

	/**
	 * 初始化文件夹重命名
	 */
	private void setupMenuPopup() {
		if (menuPopupWindow != null)
			return;
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.innjoo_foler_rename, null);
		menuPopupWindow = new PopupWindow(view);
		menuPopupWindow.setAnimationStyle(R.style.menu_popup_animation);
		menuPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		menuPopupWindow.setHeight(pagerTabStrip.getHeight());
		menuPopupWindow.setFocusable(true);
		menuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		menuPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				pagerTabStrip.setVisibility(View.VISIBLE);
				//刷新tab
				
				if(folderReNameSuccessListem!=null){
					folder_name=edt_rename.getText();
					folderReNameSuccessListem.onReNameSuccess(folder_name);
				}
				
				
			}
		});
		edt_rename = (EditText) view.findViewById(R.id.edt_rename);
	}

	public void onDestory() {
		menuPopupWindow = null;
		edt_rename = null;
		pagerTabStrip = null;
		System.gc();
	}

	public void setTitle() {
		// TODO Auto-generated method stub
		if (edt_rename != null) {
			edt_rename.setText(folder_name);
			edt_rename.setSelection(folder_name.length());
		}

	}
}