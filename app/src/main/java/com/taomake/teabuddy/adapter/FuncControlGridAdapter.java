package com.taomake.teabuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taomake.teabuddy.R;


/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class FuncControlGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] img_text = { "泡茶音乐", "音量", "灯光", "找我"};
	public int[] imgs = {
			R.drawable.fc_music,

			R.drawable.fc_voice, R.drawable.fc_light, R.drawable.fc_search};
	public FuncControlGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	private int clickTemp = -1;
	//标识选择的Item
	public void setSeclectionLight(int position,boolean isLight) {
		clickTemp = position;
		this.isLight=isLight;
	}

	//标识选择的Item
	public void setSeclectionLook(int position,boolean isLook) {
		clickTemp = position;
		this.isLook=isLook;
	}
	private  boolean isLook;
	private  boolean isLight;
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.cm_funccontrol_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.db_text_id);
		ImageView iv = BaseViewHolder.get(convertView, R.id.db_img_id);

		tv.setTextColor(mContext.getResources().getColor(R.color.white));
		if (clickTemp==position&&position==2&&isLight) {
			iv.setBackgroundResource(R.drawable.fc_light_c);
		}else {

			iv.setBackgroundResource(imgs[position]);
		}


		if (clickTemp==position&&position==3&&isLook) {
			iv.setBackgroundResource(imgs[position]);

		}else if(clickTemp==position&&position==3&&!isLook) {

			iv.setBackgroundResource(R.drawable.fc_search_c);

			tv.setTextColor(mContext.getResources().getColor(R.color.white_alpha60));

		}


		tv.setText(img_text[position]);
		return convertView;
	}

}
