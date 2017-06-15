package com.taomake.teabuddy.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.MainActivity;
import com.taomake.teabuddy.activity.MineAppsettingActivity_;
import com.taomake.teabuddy.activity.MineMessageListActivity_;
import com.taomake.teabuddy.activity.MineRemindsettingActivity;
import com.taomake.teabuddy.activity.MineRemindsettingActivity_;
import com.taomake.teabuddy.activity.MineSortListActivity_;
import com.taomake.teabuddy.activity.WelcomeActivity_;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.FoxProgressbarInterfaceHot;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.MinePersonInfoJson;
import com.taomake.teabuddy.object.MinePersonInfoObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticCommon;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;


/**
 * Created by xiaoganghu on 15/7/2.
 */

@EFragment(R.layout.fragment_mine_tab)
public class MineTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Pref
    ConfigPref_ configPref;

    @ViewById(R.id.mine_avar_id)
    RoundedImageView mine_avar_id;

    @ViewById(R.id.mine_name_id)
    TextView mine_name_id;
    @ViewById(R.id.mine_even_tea_id)
    TextView mine_even_tea_id;
    @ViewById(R.id.mine_today_num_id)
    TextView mine_today_num_id;

    @ViewById(R.id.mine_week_num_id)
    TextView mine_week_num_id;

    @ViewById(R.id.mine_month_num_id)
    TextView mine_month_num_id;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Click(R.id.mine_sort_id)
    void onmine_sort_id() {

        Util.startActivity(getActivity(), MineSortListActivity_.class);

    }

    @Click(R.id.mine_appset_id)
    void onmine_appset_id() {

        Util.startActivity(getActivity(), MineAppsettingActivity_.class);

    }

    @Click(R.id.mine_msg_id)
    void onmine_msg_id() {

        Util.startActivity(getActivity(), MineMessageListActivity_.class);

    }


    @Click(R.id.mine_remind_id)
    void onmine_remind_id() {

        if (!MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            Util.Toast(getActivity(), "主人请先链接茶密", null);
        } else {
            foxProgressbarInterfaceHot = new FoxProgressbarInterfaceHot();

            foxProgressbarInterfaceHot.startProgressBar(getActivity(), "加载中..", 15, new FoxProgressbarInterfaceHot.FoxHotCallback() {
                @Override
                public void foxhotCallback() {//超过时间还没有返回则跳转到失败页面
                    connecterror();
                }
            });
            MainApp mainappAll=(MainApp)getActivity().getApplicationContext();
            long endtime=System.currentTimeMillis();

            long starttime=mainappAll.starttime;
            if(endtime-starttime>2000) {
                getSettingInfo();
            }else{


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSettingInfo();
                    }
                },endtime-starttime);

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiverMine);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_mine_tab, container, false);

        initUi(chatView);
        IntentFilter filterMine = new IntentFilter();
        filterMine.addAction(MYACTION_UPDATE_Mine);
        getActivity().registerReceiver(receiverMine, filterMine);
        mine_msg_line = (LinearLayout) chatView.findViewById(R.id.mine_msg_line);
        mine_msg_num_id = (TextView) chatView.findViewById(R.id.mine_msg_num_id);

        int msgnum = configPref.sedentaryInterval().get();
        if (msgnum != 0) {
            mine_msg_line.setVisibility(View.VISIBLE);
            mine_msg_num_id.setText(msgnum + "");
        }
        return chatView;
    }

    LinearLayout mine_msg_line;
    TextView mine_msg_num_id;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    //fragment切换的时候广播
    BroadcastReceiver receiverMine = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MYACTION_UPDATE_Mine.equals(intent.getAction())) {
                Log.i("onReceive", "change receiverMine...");

                changeui();
            }
        }
    };


    public void changeui() {

        int msgnum = configPref.sedentaryInterval().get();
        if (msgnum != 0) {
            mine_msg_line.setVisibility(View.VISIBLE);
            mine_msg_num_id.setText(msgnum + "");
        } else {
            mine_msg_line.setVisibility(View.GONE);
//            mine_msg_num_id.setText(msgnum+"");
        }
        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);

        connectFindDevice();

        Log.d("MineTabFragment", "当前可见");
    }


    public static String MYACTION_UPDATE_Mine = "com.changemine.broadcast";

    public void initUi(View view) {

        RelativeLayout main_title_id = (RelativeLayout) view.findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) view.findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.GONE);
        ImageView right_title_icon = (ImageView) view.findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setVisibility(View.GONE);
//        title.setText("我的收藏");
        View title_line_id = (View) view.findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        TextView right_title = (TextView) view.findViewById(R.id.right_title);
        right_title.setVisibility(View.VISIBLE);
        right_title.setText("退出登录");
        right_title.setTextColor(getResources().getColor(R.color.black));
        LinearLayout right_title_line = (LinearLayout) view.findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //finish();
                Util.outLogin(getActivity(), configPref);
                Util.startActivityNewTask(getActivity(), WelcomeActivity_.class);
                getActivity().finish();
            }
        });

        initFresh(view);
        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();


    }


    @Override
    public void onStart() {
        super.onStart();
    }


    SwipeRefreshLayout mSwipeLayout;

    public void initFresh(View View) {
        mSwipeLayout = (SwipeRefreshLayout) View.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(R.color.white);
//        mSwipeLayout.setBackgroundResource(R.drawable.cm_2code_icon);//需要改变
//        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        connectFindDevice();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                mSwipeLayout.setRefreshing(false);
            }
        }, 5000); // 5秒后发送消息，停止刷新
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init();
    }


    @Override
    public void onResume() {

        super.onResume();
        if (MainActivity.current == 2) {

        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
//            Log.d("HotFragment", "不可见");

        } else {
            changeui();

        }
    }

    @AfterViews
    void init() {

        String mineInfo = configPref.likeListJson().get();
        if (mineInfo != null && !mineInfo.equals("")) {
            MinePersonInfoObj minePersonInfoObj = new Gson().fromJson(mineInfo, MinePersonInfoObj.class);
            imageLoader.displayImage(minePersonInfoObj.headimgurl, mine_avar_id, options);//设置头像
            String username = MyStringUtils.decodeUnicode(minePersonInfoObj.nickname);
            configPref.userName().put(username);
            mine_name_id.setText(username);
            if (!TextUtils.isEmpty(minePersonInfoObj.favtea)) {
                mine_even_tea_id.setText(MyStringUtils.decodeUnicode(minePersonInfoObj.favtea));
            }
            mine_today_num_id.setText(minePersonInfoObj.today);
            mine_week_num_id.setText(minePersonInfoObj.week);
            mine_month_num_id.setText(minePersonInfoObj.month);

        }


    }

    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getMinePersonInfoFunc() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(getActivity(), "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.getMinePersonInfo(getActivity(), new GetMinePersonInfoFuncHandler(), configPref.userUnion().get());//devno 空表示所有


    }


    private class GetMinePersonInfoFuncHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getMinePersonInfoFuncHandler(resp);
        }
    }


    public void getMinePersonInfoFuncHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            MinePersonInfoJson teaDetailJsonGloabl = new Gson().fromJson(resp, MinePersonInfoJson.class);
            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {

//    {"rcode":1,"rmsg":"ok","obj":{
// "headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/ajNVdqHZLLDyPPpNXr09Z3pPiauWkGbCrDssJS9IeWkn5q4U1tfXdjUT4kicB9ggHhUcCjoQQ8PKpTJw9sdTjAQA\/0",
// "nickname":"\u654f\u654f","location":"\u4e0a\u6d77.","favtea":"\u9ed8\u8ba4\u8336",
// "activetime":"2016-04-20","yc_date":"2017-04-26","yc_day":371,"today":0,"week":0,"month":59,"percent":84}}
                MinePersonInfoObj minePersonInfoObj = teaDetailJsonGloabl.obj;
                if (!TextUtils.isEmpty(minePersonInfoObj.headimgurl))

                    imageLoader.displayImage(minePersonInfoObj.headimgurl, mine_avar_id, options);//设置头像
                if (!TextUtils.isEmpty(minePersonInfoObj.nickname)) {

                    String username = MyStringUtils.decodeUnicode(minePersonInfoObj.nickname);
                    configPref.userName().put(username);
                    mine_name_id.setText(username);
                }
                if (!TextUtils.isEmpty(minePersonInfoObj.favtea))
                    mine_even_tea_id.setText(MyStringUtils.decodeUnicode(minePersonInfoObj.favtea));
                mine_today_num_id.setText(minePersonInfoObj.today);
                mine_week_num_id.setText(minePersonInfoObj.week);
                mine_month_num_id.setText(minePersonInfoObj.month);


                configPref.likeListJson().put(new Gson().toJson(minePersonInfoObj));


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

    public void connectFindDevice() {
        if (!MyStringUtils.isopenBluetooth(getActivity())) {

            getMinePersonInfoFunc();
            return;
        }

        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            foxProgressbarInterface = new FoxProgressbarInterface();

            foxProgressbarInterface.startProgressBar(getActivity(), "数据读取中...");
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getLogHistory();

        } else {
            getMinePersonInfoFunc();
        }
//        else {
//        final Context context = getActivity();
//        QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
//                .getInstanceFactory(context);
//
//        quinticDeviceFactory.findDevice(blindDeviceId,
//                new QuinticCallbackTea<QuinticDeviceTea>() {
//
//                    @Override
//                    public void onComplete(final QuinticDeviceTea resultDevice) {
//                        super.onComplete(resultDevice);
//                        new Handler(context.getMainLooper())
//                                .post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        resultDeviceAll = resultDevice;
//                                        QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
//                                        // ************处理动作
//                                        getLogHistory();
//
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onError(final QuinticException ex) {
//                        new Handler(context.getMainLooper())
//                                .post(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//
////                                        if (countError < 1) {
//                                            Log.d("connectFindDevice ex",
//                                                    ex.getCode()
//                                                            + ""
//                                                            + ex.getMessage());
////                                            connectFindDevice();
////                                            countError++;
////                                        } else {
//                                            if(foxProgressbarInterface!=null) foxProgressbarInterface.stopProgressBar();
//
//                                        getMinePersonInfoFunc();
////                                            unconnectUi();
//                                            // *****************连接失败
////                                                Util.Toast(context,
////                                                        "");
////                                        }
//                                    }
//                                });
//                    }
//                });
//        }
    }

    public void connectSendCodeFailUi(String failMsg) {
        Log.e("getLogHistory", failMsg);
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();

        mainappAll.starttime = System.currentTimeMillis();

        getMinePersonInfoFunc();
    }

    public void getLogHistory() {
        if (!MyStringUtils.isopenBluetooth(getActivity())) return;

        if (foxProgressbarInterface != null) foxProgressbarInterface.stopProgressBar();

        if (resultDeviceAll == null) return;
        String code = "EA07";
        final String failMsg = "历史LOG查询失败";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi(failMsg);
                            }
                        });

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                MainApp mainappAll = (MainApp) getActivity().getApplicationContext();

                                mainappAll.starttime = System.currentTimeMillis();

                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("ea07")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    int logValue = (QuinticCommon.unsignedByteToInt(data[3]) << 8) +
                                            QuinticCommon.unsignedByteToInt(data[4]);
                                    Log.d("log总数", logValue + "");

                                    try {
                                        Thread.sleep(logValue * 1000);//等n秒再向服务器请求
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                    getMinePersonInfoFunc();

//                                    connectSuccessUi();
                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }

    FoxProgressbarInterfaceHot foxProgressbarInterfaceHot;

    public void connecterror() {
        if (foxProgressbarInterfaceHot != null) foxProgressbarInterfaceHot.stopProgressBar();
        Util.Toast(getActivity(), "提醒数据读取失败", null);
    }

    public void getSettingInfo() {

        if (resultDeviceAll == null) return;
        String code = "EA14";



        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connecterror();
                            }
                        });

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
//                    connectSendCodeFailUi("电量查询失败");

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea14")) {

                                    if (foxProgressbarInterfaceHot != null)
                                        foxProgressbarInterfaceHot.stopProgressBar();

                                    Intent intent = new Intent(getActivity(), MineRemindsettingActivity_.class);

                                    intent.putExtra("trimResult", trimResult);
                                    getActivity().startActivity(intent);


                                } else {
                                    connecterror();
                                }


                            }
                        });

            }
        });

    }
}
