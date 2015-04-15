package com.nd.launcherdev.launcher.appslist.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.bitants.launcher.R;

public class AppListItemView extends LinearLayout {
	private String mSectionText = null;
	private Drawable mLineDrawable = null;
	private BubbleTextView[] mChildren = new BubbleTextView[4];
	private AlphalBetLabel mAlphalBet;

	public AppListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static AppListItemView createView(Context context) {
		AppListItemView itemView = (AppListItemView) LayoutInflater.from(context).inflate(R.layout.dx_appslist_item, null);
		return itemView;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mChildren[0] = (BubbleTextView) findViewById(R.id.item1);
		mChildren[1] = (BubbleTextView) findViewById(R.id.item2);
		mChildren[2] = (BubbleTextView) findViewById(R.id.item3);
		mChildren[3] = (BubbleTextView) findViewById(R.id.item4);
		mAlphalBet = (AlphalBetLabel) findViewById(R.id.alphalLabel);
	}

	/**
	 * Sets the flag that determines whether a divider should drawn at the
	 * bottom of the view.
	 */
	public void setDividerVisible(boolean visible) {
	}

	/**
	 * Sets section header or makes it invisible if the title is null.
	 */
	public void setSectionHeader(String title) {
		mSectionText = title;
	}

	/**
	 * Returns the photo view, creating it if necessary.
	 */
	public BubbleTextView getPhotoView(int index) {
		if (index >= 0 && index <= 3) {
			return mChildren[index];
		}
		return null;
	}

	public AlphalBetLabel getHeaderView() {
		return mAlphalBet;
	}

}
