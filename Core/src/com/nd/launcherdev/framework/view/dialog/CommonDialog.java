package com.nd.launcherdev.framework.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.kitset.util.TelephoneUtil;
import com.nd.launcherdev.kitset.util.ScreenUtil;
import com.nd.launcherdev.kitset.util.TelephoneUtil;

/**
 * 通用对话框
 */
public class CommonDialog extends Dialog {

	public CommonDialog(Context context, int theme) {
		super(context, theme);
	}

	public CommonDialog(Context context) {
		super(context);
	}

	/**
	 * 创建对话框辅助类
	 */
	public static class Builder {

		private Context context;

		private Drawable icon;

		private CharSequence title;

		private CharSequence message;

		private CharSequence positiveButtonText;

		private CharSequence negativeButtonText;

		private View contentView;

		private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * 设置对话框内容
		 * 
		 * @param message
		 * @return Builder
		 */
		public Builder setMessage(CharSequence message) {
			this.message = message;
			return this;
		}

		/**
		 * 设置对话框内容
		 * 
		 * @param message
		 * @return Builder
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * 设置对话框title
		 * 
		 * @param title
		 * @return Builder
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * 设置对话框title
		 * 
		 * @param title
		 * @return Builder
		 */

		public Builder setTitle(CharSequence title) {
			this.title = title;
			return this;
		}

		/**
		 * 设置对话框icon
		 * 
		 * @param icon
		 * @return Builder
		 */
		public Builder setIcon(int icon) {
			this.icon = context.getResources().getDrawable(icon);
			return this;
		}

		/**
		 * 设置对话框icon
		 * @param icon
		 * @return Builder
		 */
		public Builder setIcon(Drawable icon) {
			this.icon = icon;
			return this;
		}

		/**
		 * 设置对话框内容View
		 * 
		 * @param v
		 * @return Builder
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * 设置对话框确认按钮监听
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * 设置对话框确认按钮监听
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setPositiveButton(CharSequence positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * 设置对话框取消按钮监听
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * 设置对话框取消按钮监听
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setNegativeButton(CharSequence negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * 创建对话框
		 */
		public CommonDialog create() {
			// instantiate the dialog with the custom Theme
			final CommonDialog dialog = new CommonDialog(context, R.style.Dialog);
			dialog.setContentView(R.layout.common_dialog_layout);
			ViewGroup layout = (ViewGroup) dialog.findViewById(R.id.common_dialog_layout);
			int width = (int) (ScreenUtil.getCurrentScreenWidth(context) * 0.9f);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
			layout.setLayoutParams(lp);

			// set the dialog title
			if (icon != null) {
				((ImageView) layout.findViewById(R.id.common_dialog_top_icon)).setImageDrawable(icon);
			} else {
				layout.findViewById(R.id.common_dialog_top_icon).setVisibility(View.GONE);
			}
			((TextView) layout.findViewById(R.id.common_dialog_top_title)).setText(title);

			CharSequence dialogLeftBtnTxt = positiveButtonText;
			CharSequence dialogRightBtnTxt = negativeButtonText;
			if (TelephoneUtil.getApiLevel() >= 14) {
				dialogLeftBtnTxt = negativeButtonText;
				dialogRightBtnTxt = positiveButtonText;
			}
			final DialogInterface.OnClickListener dialogLeftBtnClickListener =  
					TelephoneUtil.getApiLevel() >= 14 ? negativeButtonClickListener : positiveButtonClickListener;
			final DialogInterface.OnClickListener dialogRightBtnClickListener = 
					TelephoneUtil.getApiLevel() >= 14 ? positiveButtonClickListener : negativeButtonClickListener;
					
			// set the confirm button
			if (dialogLeftBtnTxt != null) {

				((Button) layout.findViewById(R.id.common_dialog_left_button)).setText(dialogLeftBtnTxt);

				if (dialogLeftBtnClickListener != null) {
					((Button) layout.findViewById(R.id.common_dialog_left_button)).setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							dialogLeftBtnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
							dialog.dismiss();
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.common_dialog_left_button).setVisibility(View.GONE);
			}

			// set the cancel button
			if (dialogRightBtnTxt != null) {

				((Button) layout.findViewById(R.id.common_dialog_right_button)).setText(dialogRightBtnTxt);

				if (dialogRightBtnClickListener != null) {
					((Button) layout.findViewById(R.id.common_dialog_right_button)).setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							dialogRightBtnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
							dialog.dismiss();
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.common_dialog_right_button).setVisibility(View.GONE);
				//layout.findViewById(R.id.separator).setVisibility(View.GONE);
			}
			
			TextView textView = (TextView)layout.findViewById(R.id.common_dialog_content);  
			textView.setMovementMethod(ScrollingMovementMethod.getInstance()); 
			// set the content message
			if (message != null) {
				textView.setText(message);
			} else {
				textView.setVisibility(View.GONE);
			}

			if (contentView != null) {
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.common_dialog_custom_view_layout)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			} else {
				layout.findViewById(R.id.common_dialog_custom_view_layout).setVisibility(View.GONE);
			}

			return dialog;

		}
		
		/**
		 * 创建对话框
		 */
		public CommonDialog createEx() {
			// instantiate the dialog with the custom Theme
			final CommonDialog dialog = new CommonDialog(context, R.style.Dialog);
			dialog.setContentView(R.layout.common_dialog_layout_ex);
			ViewGroup layout = (ViewGroup) dialog.findViewById(R.id.common_dialog_layout);
			int width = (int) (ScreenUtil.getCurrentScreenWidth(context) * 0.9f);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
			layout.setLayoutParams(lp);

			((TextView) layout.findViewById(R.id.common_dialog_top_title)).setText(title);

			TextView leftBtn = (TextView) layout.findViewById(R.id.common_dialog_left_button);
			TextView rightBtn = (TextView) layout.findViewById(R.id.common_dialog_right_button);
			CharSequence dialogLeftBtnTxt = negativeButtonText;
			CharSequence dialogRightBtnTxt = positiveButtonText;
			DialogInterface.OnClickListener dialogLeftBtnClickListener = negativeButtonClickListener;
			DialogInterface.OnClickListener dialogRightBtnClickListener = positiveButtonClickListener;
			leftBtn.setBackgroundResource(R.drawable.app_choose_l_btn_ex);
			rightBtn.setBackgroundResource(R.drawable.app_choose_r_btn_ex);
			leftBtn.setTextColor(context.getResources().getColorStateList(R.color.common_dialog_text_color_selector));
			rightBtn.setTextColor(android.graphics.Color.WHITE);
			if (TelephoneUtil.getApiLevel() <= 14) {
				dialogLeftBtnTxt = positiveButtonText;
				dialogRightBtnTxt = negativeButtonText;
				dialogLeftBtnClickListener = positiveButtonClickListener;
				dialogRightBtnClickListener = negativeButtonClickListener;
				leftBtn.setBackgroundResource(R.drawable.app_choose_r_btn_ex);
				rightBtn.setBackgroundResource(R.drawable.app_choose_l_btn_ex);
				leftBtn.setTextColor(android.graphics.Color.WHITE);
				rightBtn.setTextColor(context.getResources().getColorStateList(R.color.common_dialog_text_color_selector));
			}
			
			final DialogInterface.OnClickListener finalDialogLeftBtnClickListener = dialogLeftBtnClickListener;
			final DialogInterface.OnClickListener finalDialogRightBtnClickListener = dialogRightBtnClickListener;
			// set the confirm button
			if (dialogLeftBtnTxt != null) {
				((TextView) layout.findViewById(R.id.common_dialog_left_button)).setText(dialogLeftBtnTxt);
				if (finalDialogLeftBtnClickListener != null) {
					((TextView) layout.findViewById(R.id.common_dialog_left_button)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							finalDialogLeftBtnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
							dialog.dismiss();
						}
					});
				}
			} else {
				layout.findViewById(R.id.common_dialog_left_button).setVisibility(View.GONE);
			}

			// set the cancel button
			if (dialogRightBtnTxt != null) {
				((TextView) layout.findViewById(R.id.common_dialog_right_button)).setText(dialogRightBtnTxt);
				if (finalDialogRightBtnClickListener != null) {
					((TextView) layout.findViewById(R.id.common_dialog_right_button)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							finalDialogRightBtnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
							dialog.dismiss();
						}
					});
				}
			} else {
				layout.findViewById(R.id.common_dialog_right_button).setVisibility(View.GONE);
				// layout.findViewById(R.id.separator).setVisibility(View.GONE);
			}

			TextView textView = (TextView) layout.findViewById(R.id.common_dialog_content);
			textView.setMovementMethod(ScrollingMovementMethod.getInstance());
			// set the content message
			if (message != null) {
				textView.setText(message);
			} else {
				textView.setVisibility(View.GONE);
			}
			return dialog;
		}

	}
}
