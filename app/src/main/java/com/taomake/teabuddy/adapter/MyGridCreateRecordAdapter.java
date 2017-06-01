package com.taomake.teabuddy.adapter;

import android.content.Context;
import android.util.Log;
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
import com.taomake.teabuddy.object.VoiceGroupObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;

import java.util.List;


/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridCreateRecordAdapter extends BaseAdapter {
	private Context mContext;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private List<VoiceGroupObj> dbRecordInfoObjList;
	private boolean chooseBegin=false;
//	public String[] img_text = { "默认", "芳芳", "丹丹", "刘德华", "张学友", "岑飞","芳芳", "丹丹", "刘德华", "张学友", "岑飞",
//			 };
//	public int[] imgs = {
//			R.drawable.my_zhizuo,
//
//			R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo,
//	R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo};

	public MyGridCreateRecordAdapter(Context mContext, List<VoiceGroupObj> dbRecordInfoObjList,boolean chooseBegin) {
		super();
		this.chooseBegin=chooseBegin;
		this.mContext = mContext;
		imageLoader = ImageLoader.getInstance();
		this.dbRecordInfoObjList=dbRecordInfoObjList;
		options = ImageLoaderUtil.getAvatarOptionsInstance();

	}
	private int clickTemp = -1;
	//标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
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
		VoiceGroupObj dbRecordInfoObj=dbRecordInfoObjList.get(position);
		TextView tv = BaseViewHolder.get(convertView, R.id.db_text_id);
		ImageView iv = BaseViewHolder.get(convertView, R.id.db_img_id);

		ImageView db_img_select_id = BaseViewHolder.get(convertView, R.id.db_img_select_id);

//		iv.setBackgroundResource(imgs[position]);


		if(position==0&&chooseBegin){
			iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.begin_czuo));
			tv.setText("开始录音");
		}else {
			if (clickTemp == position) {
				db_img_select_id.setVisibility(View.VISIBLE);
				db_img_select_id.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rc_voice_s));

			} else {
				db_img_select_id.setVisibility(View.GONE);
			}
			Log.e("gougou:", dbRecordInfoObj.gougou);
			if(dbRecordInfoObj.gougou!=null&&dbRecordInfoObj.gougou.equals("1")){
				db_img_select_id.setVisibility(View.VISIBLE);
				db_img_select_id.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rc_default));
			}

			imageLoader.displayImage(dbRecordInfoObj.sys_headurl,iv, options);
			tv.setText(MyStringUtils.decodeUnicode(dbRecordInfoObj.voicefile_title));
		}





		return convertView;
	}

}
