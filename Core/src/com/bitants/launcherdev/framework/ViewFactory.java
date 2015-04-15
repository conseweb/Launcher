package com.bitants.launcherdev.framework;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitants.launcherdev.framework.view.WarningInfoTextView;
import com.bitants.launcherdev.framework.view.dialog.CommonDialog;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.view.WarningInfoTextView;
import com.bitants.launcherdev.framework.view.dialog.CommonDialog;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.kitset.util.SystemUtil;
import com.bitants.launcherdev.framework.view.WarningInfoTextView;
import com.bitants.launcherdev.framework.view.dialog.CommonDialog;
import com.bitants.launcherdev.kitset.util.ScreenUtil;
import com.bitants.launcherdev.kitset.util.SystemUtil;

/**
 * 对话框和提示视图工厂
 */
public class ViewFactory {
	/** 错误提示View flag */
	public static final int NOMAL_ERR_VIEW = 0;
	/** 数据加载中提示View flag */
	public static final int LOADING_DATA_INFO_VIEW = 1;
	/** 没有搜索到内容提示View flag */
	public static final int SEARCH_NO_DATA_VIEW = 2;
	/** 网络慢提示View flag */
	public static final int NET_SLOWLY_VIEW = 3;
	/** 无网络提示View flag */
	public static final int NET_BREAK_VIEW = 4;
	/** 没有下载记录 */
	public static final int DOWNLOAD_NO_LOG_VIEW = 5;

	/**
	 * 错误提示，仅包含一个TextView
	 * @param hintId
	 * @param ctx
	 * @return View
	 */
	public static View getErrorView(int hintId, Context ctx) {
		View result = LayoutInflater.from(ctx).inflate(R.layout.framework_viewfactory_data_error, null);
		TextView tv = (TextView) result.findViewById(R.id.hint_error);
		tv.setText(hintId);

		return result;
	}

	/**
	 * 通用对话框(有按钮个数判断)
	 * @param ctx
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定回调
	 * @param isSingleBtn 是否单按钮
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialog(Context ctx, CharSequence title, CharSequence message, final OnClickListener ok, boolean isSingleBtn) {
		if (isSingleBtn) {
			CommonDialog.Builder result = new CommonDialog.Builder(ctx);
			result.setTitle(title).setMessage(message).setPositiveButton(R.string.common_button_confirm, ok);
			return result.create();
		} else {
			return getAlertDialog(ctx, title, message, ok, null);
		}
	}

	/**
	 * 通用对话框
	 * @param ctx
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialog(Context ctx, CharSequence title, CharSequence message, final OnClickListener ok) {
		return getAlertDialog(ctx, title, message, ok, null);
	}

	/**
	 * 通用对话框 
	 * @param ctx
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialog(Context ctx, CharSequence title, CharSequence message, final OnClickListener ok, final OnClickListener cancle) {
		return getAlertDialog(ctx, -1, title, message, ok, cancle);
	}

	/**
	 * 通用对话框 
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialog(Context ctx, int icon, CharSequence title, CharSequence message, final OnClickListener ok, final OnClickListener cancle) {
		return getAlertDialog(ctx, icon, title, message, ctx.getText(R.string.common_button_confirm), ctx.getText(R.string.common_button_cancel), ok, cancle);
	}

	/**
	 * 通用对话框
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param message 内容
	 * @param positive 确定按钮文字
	 * @param negative 取消按钮文字
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialog(Context ctx, int icon, CharSequence title, CharSequence message, CharSequence positive, CharSequence negative, final OnClickListener ok,
			final OnClickListener cancle) {
		return getAlertDialog(ctx, icon, title, message, null, positive, negative, ok, cancle);
	}

	/**
	 * 通用对话框 
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param view 视图
	 * @param positive 确定按钮文字
	 * @param negative 取消按钮文字
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialog(Context ctx, int icon, CharSequence title, CharSequence message, View view, CharSequence positive, CharSequence negative, final OnClickListener ok,
			final OnClickListener cancle) {
		CommonDialog.Builder result = new CommonDialog.Builder(ctx);
		if (icon != -1)
			result.setIcon(icon);
		result.setTitle(title).setMessage(message).setContentView(view).setPositiveButton(positive, ok);
		if (cancle != null)
			result.setNegativeButton(negative, cancle);
		else
			result.setNegativeButton(negative, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
		return result.create();
	}

	/**
	 * 通用对话框 
	 * @param ctx
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialogEx(Context ctx, CharSequence title, CharSequence message, final OnClickListener ok, final OnClickListener cancle) {
		return getAlertDialogEx(ctx, -1, title, message, ok, cancle);
	}

	/**
	 * 通用对话框 
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialogEx(Context ctx, int icon, CharSequence title, CharSequence message, final OnClickListener ok, final OnClickListener cancle) {
		return getAlertDialogEx(ctx, icon, title, message, ctx.getText(R.string.common_button_confirm), ctx.getText(R.string.common_button_cancel), ok, cancle);
	}

	/**
	 * 通用对话框
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param message 内容
	 * @param positive 确定按钮文字
	 * @param negative 取消按钮文字
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialogEx(Context ctx, int icon, CharSequence title, CharSequence message, CharSequence positive, CharSequence negative, final OnClickListener ok,
			final OnClickListener cancle) {
		return getAlertDialogEx(ctx, icon, title, message, null, positive, negative, ok, cancle);
	}

	/**
	 * 通用对话框 
	 * @param ctx
	 * @param icon 图标
	 * @param title 标题
	 * @param view 视图
	 * @param positive 确定按钮文字
	 * @param negative 取消按钮文字
	 * @param ok 确定回调
	 * @param cancle 取消回调
	 * @return CommonDialog
	 */
	public static CommonDialog getAlertDialogEx(Context ctx, int icon, CharSequence title, CharSequence message, View view, CharSequence positive, CharSequence negative, final OnClickListener ok,
			final OnClickListener cancle) {
		CommonDialog.Builder result = new CommonDialog.Builder(ctx);
		if (icon != -1)
			result.setIcon(icon);
		result.setTitle(title).setMessage(message).setContentView(view).setPositiveButton(positive, ok);
		if (cancle != null)
			result.setNegativeButton(negative, cancle);
		else
			result.setNegativeButton(negative, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
		return result.createEx();
	}
	
	/**
	 * 提示没有数据View
	 * @param parentView 父控件，无则为null
	 * @param context
	 * @param titleId 标题资源
	 * @param contentId 内容资源
	 * @return View(LinearLayout)
	 */
	public static View getNoDataInfoView(Context context, View parentView, int titleId, int contentId) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.framework_viewfactory_info_view, null);
		TextView titleView = (TextView) view.findViewById(R.id.framework_viewfactory_no_data_title);
		TextView textView = (TextView) view.findViewById(R.id.framework_viewfactory_no_data_textview);

		// 没有标题的
		if (titleId <= 0) {
			titleId = R.string.common_tip;
		} 
		titleView.setText(titleId);
		textView.setText(contentId);
		if (parentView != null) {
			if (parentView instanceof RelativeLayout) {
				RelativeLayout parent = (RelativeLayout) parentView;
				parent.addView(view);
				RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(view.getLayoutParams());
				l.topMargin = context.getResources().getDimensionPixelSize(R.dimen.myphone_info_view_margin_top);
				l.rightMargin = ScreenUtil.dip2px(context, 15);
				l.leftMargin = ScreenUtil.dip2px(context, 15);
				l.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				l.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				view.setLayoutParams(l);
			}
		}
		return view;
	}

	/**
	 * 提示View
	 * @param context
	 * @param parentView 父控件，无则为null
	 * @param flag
	 *            标示符：<br>
	 *            0:错误提示View<br>
	 *            1:数据加载中提示View<br>
	 *            2:没有搜索到内容提示View<br>
	 *            3:网络慢提示View<br>
	 *            4:无网络提示View
	 * @return View(LinearLayout)
	 */
	public static View getNomalErrInfoView(final Context context, View parentView, int flag) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.framework_viewfactory_err_info_view, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.framework_viewfactory_err_img);
		WarningInfoTextView textView = (WarningInfoTextView) view.findViewById(R.id.framework_viewfactory_err_textview);
		switch (flag) {
		case NOMAL_ERR_VIEW:
			imageView.setBackgroundResource(R.drawable.framwork_viewfactory_err_info_img);
			textView.setText(R.string.frame_viewfacotry_err_info_text);
			break;
		case LOADING_DATA_INFO_VIEW:
			imageView.setBackgroundResource(R.drawable.framwork_viewfactory_load_data_img);
			textView.startProcess(context.getResources().getText(R.string.frame_viewfacotry_data_load_text).toString());
			break;
		case SEARCH_NO_DATA_VIEW:
			imageView.setBackgroundResource(R.drawable.frame_viewfacotry_search_null_img);
			textView.setText(R.string.frame_viewfacotry_search_null);
			break;
		case DOWNLOAD_NO_LOG_VIEW:
			imageView.setBackgroundResource(R.drawable.frame_viewfacotry_search_null_img);
			textView.setText(R.string.frame_viewfacotry_download_null);
			break;
		case NET_SLOWLY_VIEW:
			imageView.setBackgroundResource(R.drawable.frame_viewfacotry_net_slowly_img);
			textView.setText(R.string.frame_viewfacotry_net_slowly_text);
			Button refleshBtn = (Button) view.findViewById(R.id.framework_viewfactory_err_btn);
			refleshBtn.setVisibility(View.VISIBLE);
			refleshBtn.setText(R.string.frame_viewfacotry_net_slowly_reflesh_btn);
			view.setTag(refleshBtn);// 刷新按钮放在Tag中
			break;
		case NET_BREAK_VIEW:
			imageView.setBackgroundResource(R.drawable.frame_viewfacotry_net_break_img);
			textView.setText(R.string.frame_viewfacotry_net_break_text);
			Button btn = (Button) view.findViewById(R.id.framework_viewfactory_err_btn);
			btn.setVisibility(View.VISIBLE);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View paramView) {
					try {
						Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						SystemUtil.startActivity(context, intent);
					} catch (Exception e) {
						Toast.makeText(context, R.string.frame_viewfacotry_show_netsetting_err, Toast.LENGTH_SHORT).show();
					}

				}
			});
			break;
		}

		if (parentView != null) {
			if (parentView instanceof RelativeLayout) {
				RelativeLayout parent = (RelativeLayout) parentView;
				parent.addView(view);
				RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(view.getLayoutParams());
				l.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				view.setLayoutParams(l);
			} else if (parentView instanceof LinearLayout) {
				LinearLayout parent = (LinearLayout) parentView;
				parent.addView(view);
				LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(view.getLayoutParams());
				l.topMargin = ScreenUtil.dip2px(context, 50);
				view.setLayoutParams(l);
			}

		}

		return view;
	}
}
