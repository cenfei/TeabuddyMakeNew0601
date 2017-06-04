package com.taomake.teabuddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.object.TeaDetailTimeObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;

import java.util.List;

/**
 * Created by Sam on 2015/7/27.
 */
public class AdapterUpdateTeasListView extends BaseAdapter {

    private static Activity context;
    public List<TeaDetailTimeObj> mPersonal;
    private LayoutInflater mInflater;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
private UpdateGridViewCallBack updateGridViewCallBack;

    public AdapterUpdateTeasListView(Activity context, List<TeaDetailTimeObj> personalList,UpdateGridViewCallBack updateGridViewCallBack) {
        super();
        this.updateGridViewCallBack=updateGridViewCallBack;
        this.context = context;
        this.mPersonal = personalList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();

    }

    public void addList(List<TeaDetailTimeObj> addList) {
        if (mPersonal == null || mPersonal.size() == 0) {
            mPersonal = addList;
        } else {
            mPersonal.addAll(addList);
        }

    }


    int clickpostion=0;
    public void setClickpostion(int clickpostion1){
        this.clickpostion=clickpostion1;
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
        TeaDetailTimeObj personalRanking = mPersonal.get(position);

        boolean p=(position==(mPersonal.size()-1));

        boolean a=((mPersonal.size()<=11));
        if((position==(mPersonal.size()-1))&&(mPersonal.size()<=11)){
            convertView = mInflater.inflate(R.layout.cm_pc_control_update_add_item, null);


            RelativeLayout    img_add_line=(RelativeLayout)convertView.findViewById(R.id.img_add_line);
            img_add_line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateGridViewCallBack.addCallFunc(position);
                }
            });

            return convertView;
        }



        ViewHolder viewholder;
        if (convertView == null||convertView.getTag()==null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.cm_pc_control_update_item, null);
            convertView.setTag(viewholder);

            viewholder.num_pc_id=(TextView) convertView.findViewById(R.id.num_pc_id);
            viewholder.num_time_id=(TextView) convertView.findViewById(R.id.num_time_id);


            viewholder.img_record_id=(ImageView) convertView.findViewById(R.id.img_record_id);

            viewholder.img_play_id=(ImageView) convertView.findViewById(R.id.img_play_id);


        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        if(position==clickpostion){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white_alpha60));
        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white_alpha000));

        }

        viewholder.num_pc_id.setText("第"+(position+1)+"泡");
        Integer  minute=Integer.valueOf(personalRanking.p_second)/60;
        Integer  second=Integer.valueOf(personalRanking.p_second)%60;

        viewholder.num_time_id.setText(minute+"分"+second+"秒");
        final ImageView img=viewholder.img_record_id;

        viewholder.img_record_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("delete","delete");
//                mPersonal.remove(position);
//                notifyDataSetChanged();
                updateGridViewCallBack.deleteCallFunc(position);

            }
        });
        viewholder.img_play_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.d("update","update");
                updateGridViewCallBack.updateCallFunc(position);
            }
        });

        return convertView;
    }

    public interface  UpdateGridViewCallBack{

        public void updateCallFunc(int postion);
        public void deleteCallFunc(int postion);
        public void addCallFunc(int postion);

    }


    private static class ViewHolder {
        ImageView img_play_id;
        ImageView img_record_id;
        TextView num_pc_id;
        TextView num_time_id;

    }
}
