package com.taomake.teabuddy.adapter;

import android.app.Activity;
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
import com.taomake.teabuddy.object.MsgInfoObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;

import java.util.List;

/**
 * Created by Sam on 2015/7/27.
 */
public class AdapterMsgSortListView extends BaseAdapter {

    private static Activity context;
    public List<MsgInfoObj> mPersonal;
    private LayoutInflater mInflater;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
private String unionid;

    public AdapterMsgSortListView(Activity context, List<MsgInfoObj> personalList, String unionid) {
        super();
        this.context = context;
        this.unionid=unionid;
        this.mPersonal = personalList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();

    }

    public void addList(List<MsgInfoObj> addList) {
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
        final MsgInfoObj personalRanking = mPersonal.get(position);

        ViewHolder viewholder;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.mine_message_lsit_item, null);
            convertView.setTag(viewholder);
            viewholder.msg_read_id=(ImageView) convertView.findViewById(R.id.msg_read_id);

            viewholder.msg_time_id=(TextView) convertView.findViewById(R.id.msg_time_id);
            viewholder.msg_title_id=(TextView) convertView.findViewById(R.id.msg_title_id);



        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        if(personalRanking.isread!=null&&personalRanking.isread.equals("0")){
            viewholder.msg_read_id.setVisibility(View.VISIBLE);
        }else{
            viewholder.msg_read_id.setVisibility(View.GONE);

        }

        viewholder.msg_time_id.setText(MyStringUtils.decodeUnicode(personalRanking.time));

        viewholder.msg_title_id.setText(MyStringUtils.decodeUnicode(personalRanking.txt));



        return convertView;
    }

    private static class ViewHolder {


        ImageView msg_read_id;
        TextView msg_time_id;
        TextView msg_title_id;

    }

//
//    //**********获取筛选的后的list***************/
//    FoxProgressbarInterface foxProgressbarInterface;
//View clickView;
//      TextView sort_love_num_idF=null;
//    String favValue=null;
//    public void clickLoveServer(String tounionid,View view) {
//clickView=view;
////        czid="2533";//测试这个有数据
//
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(context, "加载中...");
//        ProtocolUtil.clickLove(context, new DelTeaListInfoHandler(), unionid, tounionid);//devno 空表示所有
//
//
//    }
//
//
//    private class DelTeaListInfoHandler extends RowMessageHandler {
//        @Override
//        protected void handleResp(String resp) {
//            delTeaListInfoHandler(resp);
//        }
//    }
//
//    public void delTeaListInfoHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
//        if (resp != null && !resp.equals("")) {
//            LoveBackJson dbRecordsJson = new Gson().fromJson(resp, LoveBackJson.class);
//            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
//                Util.Toast(context, "点赞成功");
//         ImageView imageView= (ImageView) clickView;
//                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.sort_love1));
//
//                String fav=favValue;
//                Integer  favInt=Integer.valueOf(fav);
//                favInt=favInt+1;
//                if(fav.length()>4){
//
//                    fav= (favInt/10000)+"w+";
//                    //nickName=nickName.substring(0,4)+"..";
//                }else{
//                    fav=favInt+"";
//                }
//
//                sort_love_num_idF.setText(fav);
//
////                getMyCreateRecordListInfo();
//            }
//            else if ((dbRecordsJson.rcode + "").equals("2")){
//
//                Util.Toast(context,MyStringUtils.decodeUnicode(dbRecordsJson.obj));
//
//            }
//        }
//    }
}
