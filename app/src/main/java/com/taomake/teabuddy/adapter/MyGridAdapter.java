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
public class MyGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] img_text = { "默认", "芳芳", "丹丹", "刘德华", "张学友", "岑飞","芳芳", "丹丹", "刘德华", "张学友", "岑飞",
			 };
	public int[] imgs = {
			R.drawable.my_zhizuo,

			R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo,
	R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo};

	public MyGridAdapter(Context mContext) {
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

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.cm_download_popwindow_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.db_text_id);
		ImageView iv = BaseViewHolder.get(convertView, R.id.db_img_id);
		iv.setBackgroundResource(imgs[position]);

		tv.setText(img_text[position]);
		return convertView;
	}

}
