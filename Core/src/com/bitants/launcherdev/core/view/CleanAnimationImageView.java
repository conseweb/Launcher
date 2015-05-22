package com.bitants.launcherdev.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 动画效果的ImageView <br>
 */
public class CleanAnimationImageView extends ImageView {
	static final String TAG = "CleanAnimationImageView";

	private static final float SWEEP_INC = 4;

	private int offset, used, usedSweep;
	private float sweep, used_inc;
	private boolean startAnimation;
	private boolean incraement;
	private String extraText;
	private String text;
	private TextView linkedTextView;
	private boolean isAnimationEnd = true;// 动画结束

	public CleanAnimationImageView(Context context) {
		super(context);
	}

	public CleanAnimationImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CleanAnimationImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!startAnimation) {
			sweep = this.getWidth() - offset;
			usedSweep = used;
			used_inc = -1.0f;
			startAnimation = incraement = false;
			canvas.clipRect(sweep, 0, this.getRight(), this.getBottom());
			this.getDrawable().draw(canvas);
			isAnimationEnd = true;
			return;
		}
		computeUsedInc();
		if (!incraement) {
			sweep -= SWEEP_INC;
			usedSweep -= used_inc;
			if (sweep <= 0)
				incraement = true;
			if (usedSweep <= 0)
				usedSweep = 0;
		} else {
			sweep += SWEEP_INC;
			usedSweep += used_inc;
			if (sweep >= this.getWidth() - offset) {
				startAnimation = false;
				usedSweep = used;
			}
			if (usedSweep >= used)
				usedSweep = used;
		}
		if (usedSweep == 0)
			usedSweep = used;
		canvas.clipRect(sweep, 0, this.getRight(), this.getBottom());
		this.getDrawable().draw(canvas);
		drawText(canvas);
		invalidate();
	}

	private void drawText(Canvas canvas) {

		if (extraText != null && !extraText.equals("")) {
			linkedTextView.setText(extraText + usedSweep + text);
		} else {
			linkedTextView.setText(usedSweep + text);
		}
	}

	private void computeUsedInc() {
		if (used_inc != -1.0f)
			return;

		used_inc = used / ((this.getWidth() - offset) / SWEEP_INC);
	}

	public void setStartAnimation(boolean startAnimation) {
		this.startAnimation = startAnimation;
		isAnimationEnd = false;
		invalidate();
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	public void setLinkedTextView(TextView linkedTextView) {
		this.linkedTextView = linkedTextView;
	}

	public TextView getLinkedTextView() {
		return linkedTextView;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getExtraText() {
		return extraText;
	}

	public void setExtraText(String extraText) {
		this.extraText = extraText;
	}

	public boolean isAnimationEnd() {
		return isAnimationEnd;
	}
}
