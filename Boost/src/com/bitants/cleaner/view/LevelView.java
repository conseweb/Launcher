package com.bitants.cleaner.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class LevelView extends TextView
{

	private int oldLevel = 50;
	private int newLevel = 50;
	public LevelView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}
	
	public void setLevel(int level)
	{
		oldLevel = newLevel;
		newLevel=level;
	}
	
	public int getOldLevel()
	{
		return oldLevel;
	}
	public int getNewLevel()
	{
		return newLevel;
	}

}
