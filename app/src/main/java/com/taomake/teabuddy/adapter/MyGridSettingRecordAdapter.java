package com.taomake.teabuddy.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;


/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridSettingRecordAdapter extends BaseAdapter {
	private Context mContext;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

//	private List<DbRecordInfoObj> dbRecordInfoObjList;
	public String[] img_text = { "默认", "音乐1", "音乐2", "音乐3"
			 };
	public int[] imgs = {
			R.drawable.rc_music,R.drawable.rc_music,R.drawable.rc_music,R.drawable.rc_music};
//
//			R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo,
//	R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo};

	public MyGridSettingRecordAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		imageLoader = ImageLoader.getInstance();
//		this.dbRecordInfoObjList=dbRecordInfoObjList;
		options = ImageLoaderUtil.getAvatarOptionsInstance();
		startMove();

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
	private int clickTemp = -1;
	int clicksum=0;
	//标识选择的Item
	public void setSeclection(int position) {
		clicksum++;
		clickTemp = position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.cm_setting_popwindow_item, parent, false);
		}


//		DbRecordInfoObj dbRecordInfoObj=dbRecordInfoObjList.get(position);
		TextView tv = BaseViewHolder.get(convertView, R.id.db_text_id);
		ImageView iv = BaseViewHolder.get(convertView, R.id.db_img_id);

		ImageView db_img_select_id = BaseViewHolder.get(convertView, R.id.db_img_select_id);
		if (clickTemp == position) {


			db_img_select_id.setVisibility(View.VISIBLE);
			db_img_select_id.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rc_voice_s));
			if(clicksum>1) {
//				if(lastSelectImageview!=null){
//					lastSelectImageview.clearAnimation();
//				}
//
//				if (operatingAnim != null) {
//					iv.startAnimation(operatingAnim);
//				}
//				lastSelectImageview = iv;
//
//				writehandler.post(runnable);
			}

		} else {
			db_img_select_id.setVisibility(View.GONE);
		}

		iv.setBackgroundResource(imgs[position]);
//		imageLoader.displayImage(dbRecordInfoObj.sys_headurl,iv,options);
		tv.setText(MyStringUtils.decodeUnicode(img_text[position]));

		return convertView;
	}
	ImageView  lastSelectImageview;

	Animation operatingAnim;
	public void startMove(){
		 operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.tips);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
	}


	final Handler writehandler = new Handler();

	int process = 1;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//要做的事情
			if (process <=5) {

				writehandler.postDelayed(this, 1000);
				process = process + 1;
			} else {

				lastSelectImageview.clearAnimation();
				writehandler.removeCallbacks(runnable);

			}
		}
	};
}
