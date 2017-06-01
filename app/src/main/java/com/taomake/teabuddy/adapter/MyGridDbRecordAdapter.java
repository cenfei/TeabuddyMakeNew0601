package com.taomake.teabuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.object.DbRecordInfoObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;

import java.util.List;


/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridDbRecordAdapter extends BaseAdapter {
	private Context mContext;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private List<DbRecordInfoObj> dbRecordInfoObjList;
//	public String[] img_text = { "默认", "芳芳", "丹丹", "刘德华", "张学友", "岑飞","芳芳", "丹丹", "刘德华", "张学友", "岑飞",
//			 };
//	public int[] imgs = {
//			R.drawable.my_zhizuo,
//
//			R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo,
//	R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo};

	public MyGridDbRecordAdapter(Context mContext,List<DbRecordInfoObj> dbRecordInfoObjList) {
		super();
		this.mContext = mContext;
		imageLoader = ImageLoader.getInstance();
		this.dbRecordInfoObjList=dbRecordInfoObjList;
		options = ImageLoaderUtil.getAvatarOptionsInstance();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dbRecordInfoObjList.size();
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
		DbRecordInfoObj dbRecordInfoObj=dbRecordInfoObjList.get(position);
		TextView tv = BaseViewHolder.get(convertView, R.id.db_text_id);
		ImageView iv = BaseViewHolder.get(convertView, R.id.db_img_id);
//		iv.setBackgroundResource(imgs[position]);
		imageLoader.displayImage(dbRecordInfoObj.sys_headurl,iv,options);
		tv.setText(MyStringUtils.decodeUnicode(dbRecordInfoObj.voicefile_title));

		return convertView;
	}

}
