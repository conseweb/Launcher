package com.bitants.launcherdev.launcher.view.icon.ui.strategy;

import android.util.SparseArray;

import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.*;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconBackgroundDrawStragegy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconFrontgroundDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconHintDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconMaskDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconNewInstallDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.IconNewMaskDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.LargeIconBackgroundDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.PreDrawStragegy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.TextBackgroundDrawStrategy;
import com.bitants.launcherdev.launcher.view.icon.ui.strategy.impl.TextDrawStrategy;

/**
 * 绘画策略工厂
 * @author Michael
 * @createtime 2013-8-7
 */
public class DrawStragegyFactory {
	
	
	private static DrawStragegyFactory instance;
	
	private SparseArray<DrawStrategy> containter;
	
	private DrawStragegyFactory(){
		containter = new SparseArray<DrawStrategy>();
	}
	
	public static DrawStragegyFactory getInstance(){
		if(instance == null){
			instance = new DrawStragegyFactory();
		}
		return instance;
	}
	
	/**
	 * 画的顺序
	 * @author Michael
	 * @createtime 2013-7-31
	 */
	public enum DrawPriority {
	     Prepare, LargeIconBackgroud, Icon, IconMask, IconFrontground,
	     IconBackGround, TextBackground, Text, Hint, NewMask, NewInstall, NewFunction;
	     
	     public static DrawPriority valueOf(int value) {
	         switch (value) {
	         case 0:
	             return Prepare;
	         case 1:
	             return LargeIconBackgroud;
	         case 2:
	             return Icon;
	         case 3:
	             return IconMask;
	         case 4:
	             return IconFrontground;
	         case 5:
	             return IconBackGround;
	         case 6:
	             return TextBackground;
	         case 7:
	             return Text;
	         case 8:
	             return Hint;
	         case 9:
	             return NewMask;
	         case 10:
	             return NewInstall;
	         case 11:
	             return NewFunction;
	         default:
	             return NewFunction;
	         }
	     }
	};
	
	
	/**
	 * 根据关键字创建对应的绘画策略
	 * @author Michael
	 * @createtime 2013-7-31 
	 * @param priority
	 * @param context
	 * @return
	 */
	public  DrawStrategy getDrawStrategyByPriority(DrawPriority priority){
		if(priority == DrawPriority.Prepare){
			DrawStrategy strategy = containter.get(DrawPriority.Prepare.ordinal());
			if(strategy == null){
				strategy = PreDrawStragegy.getInstance();
				containter.put(DrawPriority.Prepare.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.LargeIconBackgroud){
			DrawStrategy strategy = containter.get(DrawPriority.LargeIconBackgroud.ordinal());
			if(strategy == null){
				strategy = LargeIconBackgroundDrawStrategy.getInstance();
				containter.put(DrawPriority.LargeIconBackgroud.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.Icon){
			DrawStrategy strategy = containter.get(DrawPriority.Icon.ordinal());
			if(strategy == null){
				strategy = IconDrawStrategy.getInstance();
				containter.put(DrawPriority.Icon.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.IconMask){
			DrawStrategy strategy = containter.get(DrawPriority.IconMask.ordinal());
			if(strategy == null){
				strategy = IconMaskDrawStrategy.getInstance();
				containter.put(DrawPriority.IconMask.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.IconFrontground){
			DrawStrategy strategy = containter.get(DrawPriority.IconFrontground.ordinal());
			if(strategy == null){
				strategy = IconFrontgroundDrawStrategy.getInstance();
				containter.put(DrawPriority.IconFrontground.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.IconBackGround){
			DrawStrategy strategy = containter.get(DrawPriority.IconBackGround.ordinal());
			if(strategy == null){
				strategy = IconBackgroundDrawStragegy.getInstance();
				containter.put(DrawPriority.IconBackGround.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.TextBackground){
			DrawStrategy strategy = containter.get(DrawPriority.TextBackground.ordinal());
			if(strategy == null){
				strategy = TextBackgroundDrawStrategy.getInstance();
				containter.put(DrawPriority.TextBackground.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.Text){
			DrawStrategy strategy = containter.get(DrawPriority.Text.ordinal());
			if(strategy == null){
				strategy = TextDrawStrategy.getInstance();
				containter.put(DrawPriority.Text.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.Hint){
			DrawStrategy strategy = containter.get(DrawPriority.Hint.ordinal());
			if(strategy == null){
				strategy = IconHintDrawStrategy.getInstance();
				containter.put(DrawPriority.Hint.ordinal(), strategy);
			}
			return strategy;
		}else if(priority == DrawPriority.NewMask){
			DrawStrategy strategy = containter.get(DrawPriority.NewMask.ordinal());
			if(strategy == null){
				strategy = IconNewMaskDrawStrategy.getInstance();
				containter.put(DrawPriority.NewMask.ordinal(), strategy);
			}
			return strategy;
		}if (priority == DrawPriority.NewInstall) {
		      DrawStrategy strategy = (DrawStrategy)this.containter.get(DrawPriority.NewInstall.ordinal());
		      if (strategy == null) {
		        strategy = IconNewInstallDrawStrategy.getInstance();
		        this.containter.put(DrawPriority.NewInstall.ordinal(), strategy);
		      }
		      return strategy;
		    }
		return null;
	}

}
