package com.bitants.launcherdev.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.view.FontableTextView;

import java.util.List;

/**
 * 菜单Adapter
 * @author Administrator
 *
 */
public class MenuAdapter extends BaseAdapter {
	private List<MenuItemInfo> itemInfos;
	private LayoutInflater inflater;
	public MenuAdapter(Context context){
		super();
		init(context);
	}
	private void init(Context context){
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		if(itemInfos==null)
			return 0;
		return itemInfos.size();
	}

	@Override
	public Object getItem(int pos) {
		return itemInfos.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View converView, ViewGroup parent) {		
		View view;
		if (converView == null) {
			view = inflater.inflate(R.layout.launcher_menu_item, null);
		} else {
			view = converView;
		}
		MenuItemInfo itemInfo = itemInfos.get(pos);
		ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
		FontableTextView textView = (FontableTextView) view.findViewById(R.id.item_text);
		imageView.setImageBitmap(itemInfo.bitmap);
		view.setTag(itemInfo.key);
		textView.setText(itemInfo.text, true);
		return view;
	}

	public List<MenuItemInfo> getItemInfos() {
		return itemInfos;
	}

	public void setItemInfos(List<MenuItemInfo> itemInfos) {
		this.itemInfos = itemInfos;
	}
	
}
