package com.bitants.launcherdev.launcher.appslist.view;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.bitants.launcherdev.launcher.appslist.utils.CellLayoutItem;
import com.bitants.launcherdev.launcher.appslist.utils.PinyinHelper;

import java.util.ArrayList;



public class DXSplitAppList {
    protected final static boolean LOGV = true;
    private static final String TAG = "DXSplitCursor";

    private int[] mRealToDX;
    private int[] mDXToReal;
    private int mCount;

    private Bundle mBundle = Bundle.EMPTY;
    private ArrayList<CellLayoutItem> mAppList;

    private  static int getSortKeyIndex(String sortKey) {
        if (TextUtils.isEmpty(sortKey)) {
            return 27;
        }
        char c = sortKey.charAt(0);
        if (c >= '0' && c <= '9') {
            return 0;
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 1;
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 1;
        } else {
            return 0;
        }
    }

    private void init(Context context,int recetLenth) {
        final ArrayList<CellLayoutItem> list = mAppList;
        if (list == null) {
            mCount = 0;
            return;
        }
        
        ArrayList<String> arrayString = new ArrayList<String>();
       ArrayList<Integer> arrayInteger = new ArrayList<Integer>();
       int[] array =new int[27];
       for(int i = 0;i<array.length;i++)
       {
    	   array[i]=0;
       }
        mCount = list.size();
       
        for(int i = recetLenth;i<list.size();i++)
        {
        	String sortKey = PinyinHelper.getPinYin(new String(list.get(i).getName()));
        	int index =getSortKeyIndex(sortKey);
        	array[index]++;
//        	Log.i("sortKey", "index:"+index+" sortKey:"+sortKey);
        }
        if(recetLenth!=0)
        {
        	arrayString.add("@");
			arrayInteger.add(recetLenth);
        }
        for(int i =0; i<array.length;i++)
        {
 
        		
        		if(i==0&&array[0]!=0)
        		{
        			arrayString.add("#");
        			arrayInteger.add(array[0]);
        			
        		}else
        		if(array[i]!=0)
        		{
        			 arrayString.add(String.valueOf((char)('A' + (i - 1))));
                     arrayInteger.add(array[i]);
        		}
  
        }
      
      int[] counts = new int[arrayInteger.size()];
      for (int i = 0; i < counts.length; i++) {
          counts[i] = arrayInteger.get(i);
      }
        mBundle = new Bundle();
        mBundle.putStringArray("titles", arrayString.toArray(new String[0]));//arrayString为A-Z分类的排序
        mBundle.putIntArray("counts", counts);//arrayInt为对应A-Z的各自个数；

    }

    public DXSplitAppList(final Context context, final ArrayList<CellLayoutItem> list,int recetLenth) {
        mAppList = list;
        try {
            init(context,recetLenth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bundle getExtras() {
        return mBundle;
    }



    public static final int[] getAlphaBetCount28(String[] sections, int[] counts, boolean showHeader) {
        if(sections == null || counts == null) {
            return null;
        }

        int[] ret = new int[28];
        int length = ret.length;
        for(int i = 0; i < ret.length; i++) {
            ret[i] = 0;
        }

        for(int i = 0; i < sections.length; i++) {
            if(sections[i] == null || sections[i].length() <= 0) {
                continue;
            }
            if(sections[i].equals("#")) {
                ret[0] += counts[i];
            } else if(sections[i].equals("*")) {
                ret[27] += counts[i];
            } else {
                char c = sections[i].charAt(0);
                if(c < 'A' || c > 'Z') {
                    continue;
                }
                ret[c - 'A' + 1] += counts[i];
            }
        }
     
        int sum = showHeader ? 1 : 0;
        for(int i = 0; i < ret.length; i++) {
            if(ret[i] == 0) {
                ret[i] = -1;
                length--;
            } else {
                sum += ret[i];
                ret[i] = sum - ret[i];
              
            }
        }
        int[] retDigit = new int[length];
        int begin =0;
        for(int i = 0; i < ret.length; i++) {
        	if(ret[i]!=-1)
        	{
        		retDigit[begin++]=ret[i];
        	}
        }
        return retDigit;
    }

    public static final int[] getAlphaBetCounts29(String[] sections, int[] counts, boolean showHeader)
    {
        if(sections == null || counts == null) {
            return null;
        }
        int sum = showHeader ? 1 : 0;
        int[] retDigit = new int[counts.length];
        System.arraycopy(counts, 0, retDigit, 0, counts.length);
      for(int i = 0; i < retDigit.length; i++) {
   
          sum += retDigit[i];
          retDigit[i] = sum - retDigit[i];
          
//      	Log.i("c", "  retDigit[i]"+ retDigit[i]);
  }
        return retDigit;
    }
    
    
    public static final int[] getAlphaBetCount29(String[] sections, int[] counts, boolean showHeader) {
        if(sections == null || counts == null) {
            return null;
        }

        int[] ret = new int[29];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = 0;
        }

        for(int i = 0; i < sections.length; i++) {
            if(sections[i] == null || sections[i].length() <= 0) {
                continue;
            }
            if(sections[i].equals(DX_FAVORITES_STRING)) {
                ret[0] += counts[i];
            } else if (sections[i].equals(DX_SUGGESTION_STRING)) {
                ret[0] += counts[i] + 2;
            } else if(sections[i].equals("#")) {
                ret[1] += counts[i];
            } else if(sections[i].equals("*")) {
                ret[28] += counts[i];
            } else {
                char c = sections[i].charAt(0);
                if(c < 'A' || c > 'Z') {
                    continue;
                }
                ret[c - 'A' + 2] += counts[i];
            }
        }
        int sum = showHeader ? 1 : 0;
        for(int i = 0; i < ret.length; i++) {
            if(ret[i] == 0) {
                ret[i] = -1;
              
            } else {
                sum += ret[i];
                ret[i] = sum - ret[i];
            }
        }
        return ret;
    }
  public static ArrayList<CellLayoutItem> sortByAppsFistName(ArrayList<CellLayoutItem> list)
  {
	  if(list!=null)
	  {
		  ArrayList<CellLayoutItem> digitArray =new ArrayList<CellLayoutItem>();
		  ArrayList<CellLayoutItem> letterArray = new ArrayList<CellLayoutItem>();
		  for(CellLayoutItem cell:list)
		  {
			  int sortKey= getSortKeyIndex(PinyinHelper.getPinYin(cell.getName()));
			 if(sortKey==0)
			 {
				 digitArray.add(cell);
			 }else if(sortKey>=1&&sortKey<=26)
			 {
				 letterArray.add(cell);
			 }
			 
		  }
		  for(CellLayoutItem cell:letterArray)
		  {
			  digitArray.add(cell);
		  }
		  return digitArray;
	  }
	  return list;
  }
    public static final String DX_FAVORITES_STRING = "@";
    public static final String DX_SUGGESTION_STRING = "$";
}