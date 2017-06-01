package com.pushtest.teaquinticbleutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] func_text =
	{"扫描", "连接最强设备", "断开连接", "查询指示灯状态"
			, "查询音量等级", "查询电池电量", "查询当前茶种ID"
			, "查询泡茶状态", "查询忘记洗茶次数", "查询当前设备状态"
			, "查询当前泡茶状态", "查询设备版本号"
			, "查询是否默认语音", "查询当前是否在充电", "查询当前水温"
			, "查询是否空杯", "设置指示灯使能", "设置指示灯无效", "设置日期命令"
			, "呼叫茶杯", "设置新消息未读"
			, "设置新消息已读", "语音音量10", "语音音量2"
			, "软件关机", "设备重启", "发送9个mp3"


	};
	public String[] func_code_text ={""};

//	public int[] imgs = {
//			R.drawable.my_zhizuo,
//
//			R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo,
//	R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo};

	public MyGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return func_text.length;
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
					R.layout.zhiling_list_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.zhiling_text_id);
//		ImageView iv = BaseViewHolder.get(convertView, R.id.db_img_id);
//		iv.setBackgroundResource(imgs[position]);

		tv.setText(func_text[position]);
		return convertView;
	}

}
