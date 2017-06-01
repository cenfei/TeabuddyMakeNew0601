package com.taomake.teabuddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.taomake.teabuddy.activity.ControlTeaUpdateActivity_;
import com.taomake.teabuddy.object.TeaInfoObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;

import java.util.List;

/**
 * Created by Sam on 2015/7/27.
 */
public class AdapterTeasListView extends BaseAdapter {

    private static Activity context;
    public List<TeaInfoObj> mPersonal;
    private LayoutInflater mInflater;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
private UpdateGridViewCallBack updateGridViewCallBack;

    public AdapterTeasListView(Activity context, List<TeaInfoObj> personalList,UpdateGridViewCallBack updateGridViewCallBack) {
        super();
        this.context = context;
        this.updateGridViewCallBack=updateGridViewCallBack;
        this.mPersonal = personalList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();

    }

    public void addList(List<TeaInfoObj> addList) {
        if (mPersonal == null || mPersonal.size() == 0) {
            mPersonal = addList;
        } else {
            mPersonal.addAll(addList);
        }

    }


    @Override
    public int getCount() {
        return this.mPersonal != null ? this.mPersonal.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mPersonal.get(i);
    }

//    @Override
//    public String getItem(int position) {
//        return this.mPersonal.get(position);
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TeaInfoObj personalRanking = mPersonal.get(position);

        ViewHolder viewholder;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.cm_pc_control_item, null);
            convertView.setTag(viewholder);

            viewholder.tea_item_name_id=(TextView) convertView.findViewById(R.id.tea_item_name_id);


            viewholder.img_record_id=(ImageView) convertView.findViewById(R.id.img_record_id);

            viewholder.img_play_id=(ImageView) convertView.findViewById(R.id.img_play_id);


        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        final ImageView img=viewholder.img_record_id;

        //是否需要判断系统 还是自定义

if(personalRanking.cz_type.equals("9")){

    viewholder.img_record_id.setVisibility(View.GONE);
    viewholder.img_play_id.setVisibility(View.GONE);

}else{
    viewholder.img_record_id.setVisibility(View.VISIBLE);
    viewholder.img_play_id.setVisibility(View.VISIBLE);

}
        viewholder.tea_item_name_id.setText(MyStringUtils.decodeUnicode(personalRanking.cz));
        viewholder.img_record_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("record","record");
                updateGridViewCallBack.deleteCallFunc(position);

            }
        });
        viewholder.img_play_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("play","play");
                updateGridViewCallBack.updateCallFunc(position);
                Intent intent = new Intent(context, ControlTeaUpdateActivity_.class);

               intent.putExtra("CZ_ID",personalRanking.id);
                context.startActivity(intent);

//                context.finish();

            }
        });

        return convertView;
    }


    public interface  UpdateGridViewCallBack{

        public void updateCallFunc(int postion);
        public void deleteCallFunc(int postion);

    }

    private static class ViewHolder {
        ImageView img_play_id;
        ImageView img_record_id;
        TextView tea_item_name_id;

    }
}
