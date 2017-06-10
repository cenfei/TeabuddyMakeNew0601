package com.taomake.teabuddy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterMsgSortListView;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.fragment.MineTabFragment;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BaseJson;
import com.taomake.teabuddy.object.MsgInfoObj;
import com.taomake.teabuddy.object.MsgListJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.mine_message_list)
public class MineMessageListActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;

    private PullToRefreshListView pullToRefreshListView;

    private AdapterMsgSortListView adapterHomeDesignListView;

    private List<MsgInfoObj> designRoomInfos;

    private RelativeLayout design_choose_line;

    int y = 0;
    int limit = 20;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }


    public void initUi() {

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        left_title_icon.setImageDrawable(getResources().getDrawable(R.drawable.topbar_btn_bank));
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("消息列表");
        title.setTextColor(getResources().getColor(R.color.black));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);


        TextView right_title = (TextView) findViewById(R.id.right_title);
        right_title.setText("全部已读");
        right_title.setTextColor(getResources().getColor(R.color.black));

        right_title.setVisibility(View.VISIBLE);
        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               //设置全部消息已读
                setAllMsgRead();

               // finish();
            }
        });

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_design);

        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }

    @Override
    public void finish() {
        super.finish();
        //geng
        Intent intent = new Intent(MineTabFragment.MYACTION_UPDATE_Mine);
        Log.i("Broadcast Change MINE", "change MINE fragment");

        sendBroadcast(intent);

    }

    @Override
    protected void initActivityName() {
        activityName = MineMessageListActivity.class.getName();
    }

    @Override
    protected void setActivityBg() {
//        if (BgTransitionUtil.bgDrawable != null) {
//            mainPage.setBackgroundDrawable(BgTransitionUtil.bgDrawable);
//        }
    }


    int pageNum = 1;

    int i = 0;

    /**
     * 测试数据
     */
    public void getDataFromServer() {

//
        getMineSortListInfo();

    }


    void initdata() {
        designRoomInfos = new ArrayList<MsgInfoObj>();
//        designRoomInfos=setTestData();

        adapterHomeDesignListView = new AdapterMsgSortListView(this, designRoomInfos,configPref.userUnion().get());
        pullToRefreshListView.setAdapter(adapterHomeDesignListView);

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);// 设置底部下拉刷新模式
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setLastUpdatedLabel("加载更多");
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setPullLabel("");
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("正在刷新");
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("放开以刷新");

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        MineMessageListActivity.this,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);

                y = 0;
                pagenum++;
                getDataFromServer();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapterHomeDesignListView.mPersonal.size() < limit) {
                    pageNum = 0;
                } else {
                    pageNum++;

                }
                if (!refreshView.isHeaderShown()) {
                    y = adapterHomeDesignListView.mPersonal.size();
                } else {
                    // 得到上一次滚动条的位置，让加载后的页面停在上一次的位置，便于用户操作
                    // y = adapter.list.size();

                }
                getDataFromServer();
            }
        });

        // 点击详单
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("当前点击",""+position);
                    jobJsonCurrent = (MsgInfoObj) adapterHomeDesignListView.getItem(position-1);
                if(jobJsonCurrent!=null&&jobJsonCurrent.isread.equals("0")) {
                    setMsgIsReadDevice();
                    setMsgIsRead(jobJsonCurrent.mid);
                }else{
                    //跳转到网页
                    Intent intent = new Intent(MineMessageListActivity.this, WebViewActivity_.class);
                    intent.putExtra("url", jobJsonCurrent.url);
                    intent.putExtra("title", MyStringUtils.decodeUnicode(jobJsonCurrent.tit));
                    startActivity(intent);


                }
//                Util.startActivity(getActivity(), MyWorkDetailActivity_.class);

//                Intent intent = new Intent(getActivity(), HomeAllDetailActivity_.class);
//				intent.putExtra(Constant.CASE_HOME_ID,adapterHomeDesignListView.getItem(position-1).id);
//                intent.putExtra(Constant.CASE_HOME_NAME,adapterHomeDesignListView.getItem(position-1).name);
//
//                startActivity(intent);

            }
        });

    }
    MsgInfoObj    jobJsonCurrent;

    @Override
    public void onDestroy() {
        //退出activity前关闭菜单

        super.onDestroy();

    }

    //**********网络***************/


    //**********获取筛选的参数***************/


    @Override
    public void onResume() {
        super.onResume();

        pageNum = 0;

        getDataFromServer();
    }

    int pagenum=0;

    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void setMsgIsRead(String mid) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.setMineMsgIsRead(this, new SetMsgIsReadHandler(), configPref.userUnion().get(), mid);//devno 空表示所有


    }


    private class SetMsgIsReadHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            setMsgIsReadHandler(resp);
        }
    }


    public void setMsgIsReadHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            BaseJson baseJson = new Gson().fromJson(resp, BaseJson.class);

            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                //进入网页
                if(jobJsonCurrent!=null&&jobJsonCurrent.url!=null&&!jobJsonCurrent.url.equals("")){
                    //跳转到网页
                    Intent intent = new Intent(MineMessageListActivity.this, WebViewActivity_.class);
                    intent.putExtra("url", jobJsonCurrent.url);
                    intent.putExtra("title", MyStringUtils.decodeUnicode(jobJsonCurrent.tit));
                    startActivity(intent);

                }else{
                    Util.Toast(MineMessageListActivity.this,"消息没有内容",null);
                }


            }
        }
    }

    public void getMineSortListInfo() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.getMineMsgListInfo(this, new GetMineSortListInfoHandler(), configPref.userUnion().get(), (pageNum + 1), limit);//devno 空表示所有


    }


    private class GetMineSortListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getMineSortListInfoHandler(resp);
        }
    }


    public void getMineSortListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {


            MsgListJson baseJson = new Gson().fromJson(resp, MsgListJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                //测试数据
//            baseJson.rows = (ArrayList<MessageJson>) setTestData();

                boolean setNoReadBool=false;

                if (baseJson.obj != null && baseJson.obj.size() > 0) {//列表

                    if (pageNum == 0) {
                        designRoomInfos.clear();
                        designRoomInfos.addAll(baseJson.obj);
                    } else {
                        designRoomInfos.addAll(baseJson.obj);
                    }


                    for(MsgInfoObj msgInfoObj:baseJson.obj){
                        if(msgInfoObj.isread!=null&&msgInfoObj.isread.equals("0")&&!setNoReadBool){
                            setNoReadBool=true;

                            break;
                        }
                    }


                }


                if (designRoomInfos.size() <= 0 && pageNum == 0) {
                    pullToRefreshListView.setVisibility(View.GONE);
//                    no_data_id.setVisibility(View.VISIBLE);
                } else {
                    pullToRefreshListView.setVisibility(View.VISIBLE);
//                    no_data_id.setVisibility(View.GONE);

                    adapterHomeDesignListView.notifyDataSetChanged();
                    // Call onRefreshComplete when the list has been refreshed.
                    pullToRefreshListView.onRefreshComplete();
                    pullToRefreshListView.getRefreshableView().setSelection(y);


                }

                if(setNoReadBool){//说明有未读消息  需要设置
                    connectFindDevice(true);
                }else{
                    connectFindDevice(false);

                }
            }

        }
    }






    //************************蓝牙操作*********************************

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;

    public void connectFindDevice(final boolean boolRead) {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(getActivity(), "蓝牙读取中...");
        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);
        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            if(boolRead) {
                setHaveMsgNoRead();
            }
            else{
              setMsgIsReadDevice();
            }
        } else {
            final Context context = MineMessageListActivity.this;
            QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
                    .getInstanceFactory(context);

            quinticDeviceFactory.findDevice(blindDeviceId,
                    new QuinticCallbackTea<QuinticDeviceTea>() {

                        @Override
                        public void onComplete(final QuinticDeviceTea resultDevice) {
                            super.onComplete(resultDevice);
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
                                            resultDeviceAll = resultDevice;
                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
                                            // ************处理动作
                                            if(boolRead) {
                                                setHaveMsgNoRead();
                                            }
                                            else{
                                                setMsgIsReadDevice();
                                            }

                                        }
                                    });
                        }

                        @Override
                        public void onError(final QuinticException ex) {
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (countError < 1) {
                                                Log.d("connectFindDevice ex",
                                                        ex.getCode()
                                                                + ""
                                                                + ex.getMessage());
                                                connectFindDevice(boolRead);
                                                countError++;
                                            } else {
//                                            unconnectUi();
                                                // *****************连接失败
//                                                Util.Toast(context,
//                                                        "");
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    public void connectSendCodeFailUi(String failMsg){
        Log.e("getLogHistory", failMsg);

    }
    public void setMsgIsReadDevice() {
        if (resultDeviceAll == null) return;
        String code = "EB07";
        final String failMsg = "设置消息已读失败";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                connectSendCodeFailUi(failMsg);

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(MineMessageListActivity.this.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("eb01")) {


                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }

    public void setHaveMsgNoRead() {
        if (resultDeviceAll == null) return;
        String code = "EB06";
        final String failMsg = "设置消息未读失败";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                connectSendCodeFailUi(failMsg);

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(MineMessageListActivity.this.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("eb01")) {


                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }





    public void setAllMsgRead() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(MineMessageListActivity.this, "加载中...");

        Log.d("获取有未读短信", "");

        ProtocolUtil.setAllMsgRead(MineMessageListActivity.this, new SendLogToServerHandler(), configPref.userUnion().get());


    }


    private class SendLogToServerHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            sendLogToServerHandler(resp);
        }
    }


    public void sendLogToServerHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据


            BaseJson baseJson = new Gson().fromJson(resp, BaseJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                Log.d("设置全部消息已读", "成功");
                getMineSortListInfo();


            }

        }
    }



}




