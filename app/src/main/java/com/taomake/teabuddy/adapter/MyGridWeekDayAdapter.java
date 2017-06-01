package com.taomake.teabuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taomake.teabuddy.R;

import java.util.HashSet;
import java.util.Set;


/**
 * @author http://blog.csdn.net/finddreams
 * @Description:gridview的Adapter
 */
public class MyGridWeekDayAdapter extends BaseAdapter {
    private Context mContext;

    public String[] img_text = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"
    };
//	public int[] imgs = {
//			R.drawable.my_zhizuo,
//
//			R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo,
//	R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo, R.drawable.my_shoucang, R.drawable.my_zhizuo};

    public MyGridWeekDayAdapter(Context mContext) {
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

    public Set<Integer> clickTempHash = new HashSet<Integer>();

    //标识选择的Item
    public void setSeclection(int position) {
        if (clickTempHash.contains(position)) {
            clickTempHash.remove(position);
        } else {
            clickTempHash.add(position);
        }


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.cm_day_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.day_item_name_id);
//		iv.setBackgroundResource(imgs[position]);
//        LinearLayout day_item_name_line = BaseViewHolder.get(convertView, R.id.day_item_name_line);
        if (clickTempHash.contains(position)) {
//            day_item_name_line.setBackgroundColor(mContext.getResources().getColor(R.color.black));

            tv.setBackgroundResource(R.drawable.rounded_blackbutton_wallet);
            tv.setTextColor(mContext.getResources().getColor(R.color.white));
//            tv.setTextColor(mContext.getColor(R.color.white));

        } else {
//            day_item_name_line.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            tv.setBackgroundResource(R.drawable.rounded_white);
            tv.setTextColor(mContext.getResources().getColor(R.color.black));

//            tv.setTextColor(mContext.getColor(R.color.black));

        }
        tv.setText(img_text[position]);

        return convertView;
    }

}
