package com.bitants.common.launcher.view.icon.ui;

import android.content.Context;
import android.util.SparseArray;

import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStragegyFactory.DrawPriority;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;

/**
 * 
 */
public class LauncherIconViewConfig{
	
	private SparseArray<DrawStrategy> containter;
	
	/**
	 * 是否画newMask
	 */
	private boolean drawNewMask = false;
	
	/**
	 * 是否画Hint
	 */
	private boolean drawHint = false;
	
	/**
	 * 是否画textbackground
	 */
	protected boolean drawTextBackground = false;
	
	/**
	 * 是否画图标
	 */
	private boolean drawIcon = true;
	
	/**
	 * 是否画文字
	 */
	protected boolean drawText = true;
	
	/**
	 * 是否新安装
	 */
	private boolean isNewInstall = false;
	
	/**
	 * 是否新功能
	 */
	private boolean isNewFunction = false;
	
	/**
	 * 是否情景模式充满
	 */
	private boolean isSceneFillContent = false;
	
	/**
	 * 是否情景模式充满
	 */
	private boolean isSceneFillContentFitCenter = false;
	
	
	private boolean isShowText = true;
	
	private Context context;
	
	private boolean drawFrontIconMask = true;
	
	private boolean customIcon = false;

	/**
	 * 是否绘制无法合并文件夹提示View
	 */
	private boolean isDrawNotMergeFoler = false;
	
	public LauncherIconViewConfig(){
	}
	
	public LauncherIconViewConfig(Context context){
		this.context = context;
		containter =  new SparseArray<DrawStrategy>();
		containter.put(DrawPriority.Prepare.ordinal(), 
				DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.Prepare));
		containter.put(DrawPriority.Icon.ordinal(), 
				DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.Icon));
		drawTextBackground = BaseSettingsPreference.getInstance().isShowTitleBackaground();
		if(drawTextBackground){
			containter.put(DrawPriority.TextBackground.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.TextBackground));
		}
		drawHint = false;
	}
	
	
	/**
	 * @return the isLargeIconMode
	 */
	public boolean isLargeIconMode() {
		return BaseConfig.isLargeIconMode();
	}

	/**
	 * @return the drawNewMask
	 */
	public boolean isDrawNewMask() {
		return drawNewMask;
	}

	/**
	 * 当View还未layout不需要触发change事件 因为iconMaskData中的值还未准备好
	 * @param _drawNewMask the drawNewMask to set
	 */
	public void setDrawNewMask(boolean _drawNewMask) {
		this.drawNewMask = _drawNewMask;
		if(drawNewMask){
			containter.put(DrawPriority.NewMask.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.NewMask));
		}else{
			containter.remove(DrawPriority.NewMask.ordinal());
		}
	}

	/**
	 * @return the drawHint
	 */
	public boolean isDrawHint() {
		return drawHint;
	}

	/**
	 * @param _drawHint the drawHint to set
	 */
	public void setDrawHint(boolean _drawHint) {
		this.drawHint = _drawHint;
		if(drawHint){
			containter.put(DrawPriority.Hint.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.Hint));
		}else{
			containter.remove(DrawPriority.Hint.ordinal());
		}
	}

	/**
	 * @return the drawTextBackground
	 */
	public boolean isDrawTextBackground() {
		return drawTextBackground;
	}

	/**
	 * @param _drawTextBackground the drawTextBackground to set
	 */
	public void setDrawTextBackground(boolean _drawTextBackground) {
		drawTextBackground = _drawTextBackground && drawText && isShowText;
		if(drawTextBackground){
			containter.put(DrawPriority.TextBackground.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.TextBackground));
		}else{
			containter.delete(DrawPriority.TextBackground.ordinal());
		}
	}
	
	/**
	 * @param _drawTextBackground the drawTextBackground to set
	 */
	public void setDrawTextBackgroundForce(boolean _drawTextBackground) {
		drawTextBackground = _drawTextBackground && drawText && isShowText;
		if(drawTextBackground){
			containter.put(DrawPriority.TextBackground.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.TextBackground));
		}else{
			containter.delete(DrawPriority.TextBackground.ordinal());
		}
	}
	
	/**
	 * @return the drawIcon
	 */
	public boolean isDrawIcon() {
		return drawIcon;
	}

	/**
	 * @param drawIcon the drawIcon to set
	 */
	public void setDrawIcon(boolean drawIcon) {
		this.drawIcon = drawIcon;
	}

	/**
	 * @return the drawText
	 */
	public boolean isDrawText() {
		return drawText;
	}

	/**
	 * @param _drawText the drawText to set
	 */
	public void setDrawText(boolean _drawText) {
		this.drawText = _drawText && isShowText;
		if(this.drawText){
			containter.put(DrawPriority.Text.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.Text));
		}else{
			containter.delete(DrawPriority.Text.ordinal());
		}
		
	}


	public void setNewInstall(boolean isNewInstall) {
		this.isNewInstall = isNewInstall;
		if(isNewInstall){
			containter.put(DrawPriority.NewInstall.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.NewInstall));
		}else{
			containter.remove(DrawPriority.NewInstall.ordinal());
		}
	}

	public void setNewFunction(boolean isNewFunction) {
		this.isNewFunction = isNewFunction;
		if(isNewFunction){
			containter.put(DrawPriority.NewFunction.ordinal(), 
					DrawStragegyFactory.getInstance().getDrawStrategyByPriority(DrawPriority.NewFunction));
		}else{
			containter.remove(DrawPriority.NewFunction.ordinal());
		}
	}

	public boolean isNewInstall() {
		return isNewInstall;
	}

	public boolean isNewFunction() {
		return isNewFunction;
	}


	public SparseArray<DrawStrategy> getContainter() {
		return containter;
	}

	public boolean isSceneFillContent() {
		return isSceneFillContent;
	}

	public void setSceneFillContent(boolean isSceneFillContent) {
		this.isSceneFillContent = isSceneFillContent;
	}

	public boolean isSceneFillContentFitCenter() {
		return isSceneFillContentFitCenter;
	}

	public void setSceneFillContentFitCenter(boolean isSceneFillContentFitCenter) {
		this.isSceneFillContentFitCenter = isSceneFillContentFitCenter;
	}


	public void setShowText(boolean isShowText) {
		this.isShowText = isShowText;
		setDrawText(isShowText);
		if(!isShowText)
			setDrawTextBackground(false);
	}

	/**
	 * @return the drawFrontIconMask
	 */
	public boolean isDrawFrontIconMask() {
		return drawFrontIconMask;
	}

	/**
	 * @param drawFrontIconMask the drawFrontIconMask to set
	 */
	public void setDrawFrontIconMask(boolean drawFrontIconMask) {
		this.drawFrontIconMask = drawFrontIconMask;
	}

	/**
	 * @return the customIcon
	 */
	public boolean isCustomIcon() {
		return customIcon;
	}

	/**
	 * @param customIcon the customIcon to set
	 */
	public void setCustomIcon(boolean customIcon) {
		this.customIcon = customIcon;
	}
	
	public boolean isDrawNotMergeFoler() {
		return isDrawNotMergeFoler;
	}

	public void setDrawNotMergeFoler(boolean isDrawNotMergeFoler) {
		this.isDrawNotMergeFoler = isDrawNotMergeFoler;
	}
}
