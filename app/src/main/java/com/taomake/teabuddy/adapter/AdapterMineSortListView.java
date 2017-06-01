package com.taomake.teabuddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.LoveBackJson;
import com.taomake.teabuddy.object.SortInfoObj;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import java.util.List;

/**
 * Created by Sam on 2015/7/27.
 */
public class AdapterMineSortListView extends BaseAdapter {

    private static Activity context;
    public List<SortInfoObj> mPersonal;
    private LayoutInflater mInflater;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private String unionid;

    public AdapterMineSortListView(Activity context, List<SortInfoObj> personalList, String unionid) {
        super();
        this.context = context;
        this.unionid = unionid;
        this.mPersonal = personalList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();

    }

    public void addList(List<SortInfoObj> addList) {
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

    private int selectPos = 0;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SortInfoObj personalRanking = mPersonal.get(position);
        selectPos = position;
        ViewHolder viewholder;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.mine_sort_lsit_item, null);
            convertView.setTag(viewholder);
            viewholder.sort_avar_id = (ImageView) convertView.findViewById(R.id.sort_avar_id);

            viewholder.sort_love_id = (ImageView) convertView.findViewById(R.id.sort_love_id);
            viewholder.sort_nickname_id = (TextView) convertView.findViewById(R.id.sort_nickname_id);

            viewholder.sort_cup_tea_id = (TextView) convertView.findViewById(R.id.sort_cup_tea_id);
            viewholder.sort_head_three_id = (ImageView) convertView.findViewById(R.id.sort_head_three_id);
            viewholder.sort_love_num_id = (TextView) convertView.findViewById(R.id.sort_love_num_id);

            viewholder.sort_ordernum_id = (TextView) convertView.findViewById(R.id.sort_ordernum_id);

        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        final String isclicked = personalRanking.isclicked;
        if (isclicked != null && isclicked.equals("1")) {
            viewholder.sort_love_id.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_love1));
        } else {
            viewholder.sort_love_id.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_love2));

        }

        sort_love_num_idF = viewholder.sort_love_num_id;
        favValue = personalRanking.fav;
        viewholder.sort_love_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//点赞
                if (position != 0) {
                    if (isclicked != null && !isclicked.equals("1")) {
                        SortInfoObj sortInfoObj = mPersonal.get(position);
                        String fav = favValue;
                        Integer favInt = Integer.valueOf(fav);
                        favInt = favInt + 1;


                        sortInfoObj.fav = favInt + "";
                        notifyDataSetChanged();
                        clickLoveServer(personalRanking.unionid, view);
                    } else {
                        Util.Toast(context, "主人您已点过赞");

                    }
                } else {
                    Util.Toast(context, "主人不可以给自己点赞哦");
                }

            }
        });


        imageLoader.displayImage(personalRanking.headimgurl, viewholder.sort_avar_id, options);

        String nickName = MyStringUtils.decodeUnicode(personalRanking.nickname);
//        if(nickName.length()>4){
//            nickName=nickName.substring(0,4)+"..";
//        }
        viewholder.sort_nickname_id.setText(nickName);

        String fav = personalRanking.fav;
        if (fav.length() > 4) {
            Integer favInt = Integer.valueOf(fav);
            fav = (favInt / 10000) + "w+";
            //nickName=nickName.substring(0,4)+"..";
        }

        viewholder.sort_love_num_id.setText(fav);

        String totalcups = personalRanking.totalcups;
        if (totalcups.length() > 4) {
            Integer favInt = Integer.valueOf(totalcups);
            totalcups = (favInt / 10000) + "w+";
            //nickName=nickName.substring(0,4)+"..";
        }
        viewholder.sort_cup_tea_id.setText(totalcups + "杯");

        String orderNo = personalRanking.orderNo;
        if (orderNo.length() > 4) {
            Integer orderNoInt = Integer.valueOf(orderNo);
            orderNo = (orderNoInt / 10000) + "w+";
            //nickName=nickName.substring(0,4)+"..";
        }
        viewholder.sort_ordernum_id.setText(orderNo);
        switch (position) {
            case 1:
                viewholder.sort_head_three_id.setVisibility(View.VISIBLE);
                viewholder.sort_head_three_id.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_hg1));


                break;
            case 2:
                viewholder.sort_head_three_id.setVisibility(View.VISIBLE);
                viewholder.sort_head_three_id.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_hg2));


                break;
            case 3:
                viewholder.sort_head_three_id.setVisibility(View.VISIBLE);
                viewholder.sort_head_three_id.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_hg3));


                break;

            default:
                viewholder.sort_head_three_id.setVisibility(View.GONE);
                break;

        }


//        final ImageView img=viewholder.img_record_id;
//        viewholder.img_download_id.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("download", "download");
//                new Record_Download_Popwindow().showPopwindow(context, img, new Record_Download_Popwindow.CallBackPayWindow() {
//                    @Override
//                    public void handleCallBackPayWindowFromStop(String recorddir) {
//
//                    }
//                });
//            }
//        });
//        viewholder.img_record_id.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("record","record");
//                new  Record_Popwindow().showPopwindow(context, img, new Record_Popwindow.CallBackPayWindow() {
//                    @Override
//                    public void handleCallBackPayWindowFromStop(String recorddir) {
//
//                    }
//                });
//
//            }
//        });
//        viewholder.img_play_id.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("play","play");
//
//            }
//        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView sort_head_three_id;
        ImageView sort_avar_id;

        ImageView sort_love_id;

        TextView sort_cup_tea_id;
        TextView sort_nickname_id;
        TextView sort_love_num_id;

        TextView sort_ordernum_id;
    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;
    View clickView;
    TextView sort_love_num_idF = null;
    String favValue = null;

    public void clickLoveServer(String tounionid, View view) {
        clickView = view;
//        czid="2533";//测试这个有数据

        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(context, "加载中...");
        ProtocolUtil.clickLove(context, new DelTeaListInfoHandler(), unionid, tounionid);//devno 空表示所有


    }


    private class DelTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            delTeaListInfoHandler(resp);
        }
    }

    public void delTeaListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            LoveBackJson dbRecordsJson = new Gson().fromJson(resp, LoveBackJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                Util.Toast(context, "点赞成功");
                ImageView imageView = (ImageView) clickView;
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_love1));

                SortInfoObj sortInfoObj = mPersonal.get(selectPos);
                sortInfoObj.isclicked = "1";
//                String fav=sortInfoObj.fav;
//                Integer  favInt=Integer.valueOf(fav);
//                favInt=favInt+1;
//
//
//
//                sortInfoObj.fav=favInt+"";
//                notifyDataSetChanged();

//                getMyCreateRecordListInfo();
            } else if ((dbRecordsJson.rcode + "").equals("2")) {

                Util.Toast(context, MyStringUtils.decodeUnicode(dbRecordsJson.obj));

            }
        }
    }
}
