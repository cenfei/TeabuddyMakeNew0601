package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.MyGridCreateRecordAdapter;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.Apply_Record_Popwindow;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.component.Record_Play_Popwindow;
import com.taomake.teabuddy.component.SZ_PayPopwindow_MyRecordBottom;
import com.taomake.teabuddy.component.SZ_PayPopwindow_ShareBottom;
import com.taomake.teabuddy.network.API;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.VoiceGroupJson;
import com.taomake.teabuddy.object.VoiceGroupObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;
import com.taomake.teabuddy.wxapi.WechatShareManager;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_mycreate_records)
public class MyCreateRecordsActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;


    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }
    private WechatShareManager mShareManager;

//    PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareManager = WechatShareManager.getInstance(MyCreateRecordsActivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        if (ContextCompat.checkSelfPermission(MyCreateRecordsActivity.this,
//                Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
//            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        }
//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }

    TextView title;
    public void initUi() {

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

         title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("我的创作");
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);


        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        getMyCreateRecordListInfo();
        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = MyCreateRecordsActivity.class.getName();
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
        getMyCreateRecordListInfo();
//
//        getMessageListFromServerForMsg();


    }

    MyGridCreateRecordAdapter myGridCreateRecordAdapter;
    PullToRefreshGridView gridview;

    void initdata() {

        gridview = (PullToRefreshGridView) findViewById(R.id.gridview);


        voiceGroupObjList.add(new VoiceGroupObj());

        myGridCreateRecordAdapter = new MyGridCreateRecordAdapter(this, voiceGroupObjList,true);
        gridview.setAdapter(myGridCreateRecordAdapter);
        gridview.setOnItemClickListener(new ItemClickListener());

        gridview.setOnItemClickListener(new ItemClickListener());
        gridview.setAdapter(myGridCreateRecordAdapter);
        gridview.setOnItemClickListener(new ItemClickListener());
        gridview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);// 设置底部下拉刷新模式
        gridview.getLoadingLayoutProxy(true, false)
                .setLastUpdatedLabel("加载更多");
        gridview.getLoadingLayoutProxy(true, false)
                .setPullLabel("继续拉动");
        gridview.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("新未来，新物种");
        gridview.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("松手刷新");
        gridview
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<GridView> refreshView) {
                        Log.e("TAG", "onPullDownToRefresh"); // Do work to
                        String label = DateUtils.formatDateTime(
                                getApplicationContext(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        // Update the LastUpdatedLabel
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);
                        getDataFromServer();
//                        new GetDataTask().execute();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<GridView> refreshView) {
                        Log.e("TAG", "onPullUpToRefresh"); // Do work to refresh
                        // the list here.
//                        new GetDataTask().execute();
                        getDataFromServer();
                    }
                });
        getDataFromServer();

    }

    ImageView imageViewLast;

    @Override
    public void onDestroy() {
        //退出activity前关闭菜单

        super.onDestroy();
//        mWakeLock.release();
    }

    //**********网络***************/


    //**********获取筛选的参数***************/


    @Override
    public void onResume() {
        super.onResume();
//        mWakeLock.acquire();
        pageNum = 1;

//        if(boolCanusePress) {
//            getDataFromServer();
//        }

    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getMyCreateRecordListInfo() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.getMyCreateRecordList(this, new GetTeaListInfoHandler(), configPref.userUnion().get());//devno 空表示所有


    }


    private class GetTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getTeaListInfoHandler(resp);
        }
    }

    List<VoiceGroupObj> voiceGroupObjList = new ArrayList<VoiceGroupObj>();

    public void getTeaListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            VoiceGroupJson dbRecordsJson = new Gson().fromJson(resp, VoiceGroupJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                voiceGroupObjList.clear();
                voiceGroupObjList.add(new VoiceGroupObj());
                voiceGroupObjList.addAll(dbRecordsJson.obj);
//                initdata();
                myGridCreateRecordAdapter.setSeclection(selectTemp);

                myGridCreateRecordAdapter.notifyDataSetChanged();
                gridview.invalidate();
                gridview.onRefreshComplete();
            }

        }
    }


    public void delMyCreateRecordListInfo(String indexID) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.delMyCreateRecord(this, new DelTeaListInfoHandler(), configPref.userUnion().get(), indexID);//devno 空表示所有


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
            if (resp.contains("OK")) {
                Util.Toast(MyCreateRecordsActivity.this, "删除成功",null);

                getMyCreateRecordListInfo();
            }
        }
    }

    public List<String> setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {


            jobJsons.add("打招呼");

        }

        return jobJsons;
    }

    boolean boolCanusePress=true;

    @Override
    public void onBackPressed() {
        if(boolCanusePress) {
            super.onBackPressed();
        }else{
            Util.Toast(this,"当前不能返回\n请耐心等待",null);
        }
    }
    int selectTemp=-1;
       String titleShare;
    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View view,// The view within the AdapterView that was clicked
                               final int postion,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {
            selectTemp=postion;
            // 显示所选Item的ItemText
            //跳转到
            if (postion == 0) {
                Intent intent = new Intent(MyCreateRecordsActivity.this, BeginRecordActivity_.class);
                startActivity(intent);
            } else {


                final VoiceGroupObj voiceGroupObj = voiceGroupObjList.get(postion);

            final    ImageView imageView = (ImageView) view.findViewById(R.id.db_img_select_id);

                myGridCreateRecordAdapter.setSeclection(postion);
                myGridCreateRecordAdapter.notifyDataSetChanged();
//                gridview.invalidate();

//                if (voiceGroupObj.gougou != null && !voiceGroupObj.gougou.equals("1")) {
//                    imageView.setVisibility(View.VISIBLE);
//                    if (imageViewLast != null) {
//                        imageViewLast.setVisibility(View.GONE);
//
//                    }
//                    imageViewLast = imageView;
////                    gridview.invalidate();
//                }
                titleShare=configPref.userName().get()+"刚刚分享了叫“"+MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_title)+"”的录音";

                new SZ_PayPopwindow_MyRecordBottom().showPopwindow(MyCreateRecordsActivity.this, imageView, new SZ_PayPopwindow_MyRecordBottom.CallBackPayWindow() {

                    @Override
                    public void handleCallBackPlay() {

                        new Record_Play_Popwindow().showPopwindow(MyCreateRecordsActivity.this,
                                imageView,voiceGroupObj.voicefile_index, configPref.userUnion().get(), new Record_Play_Popwindow.CallBackPayWindow() {
                                    @Override
                                    public void handleCallBackDbSelect(String recorddir) {

                                    }
                                });

                    }

                    @Override
                    public void handleCallBackUpdate() {
                        VoiceGroupObj voiceGroupObj = voiceGroupObjList.get(postion);
                        Intent intent = new Intent(MyCreateRecordsActivity.this, UpdateRecordActivity_.class);
                        intent.putExtra("voicefileindex", voiceGroupObj.voicefile_index);
                        startActivity(intent);
                    }

                    @Override
                    public void handleCallBackDelete() {
                        delMyCreateRecordListInfo(voiceGroupObj.voicefile_index);
                    }

                    @Override
                    public void handleCallBackShare() {


                        new SZ_PayPopwindow_ShareBottom().showPopwindow(MyCreateRecordsActivity.this, imageView, new SZ_PayPopwindow_ShareBottom.CallBackPayWindow() {
                            @Override
                            public void handleCallBackPlay() {//微信好友
                                if (!Util.isWebchatAvaliable(MyCreateRecordsActivity.this)) {
                                    Toast.makeText(MyCreateRecordsActivity.this, "请先安装微信", Toast.LENGTH_LONG).show();
                                    return;
                                }

//                            WechatShareManager.ShareContentWebpage webpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag("茶密", "茶密分享最动听的茶道秘书",
//                                    API.share_url + voiceGroupObj.voicefile_index, R.drawable.app_logo);
//                            mShareManager.shareByWebchat(webpage, WechatShareManager.WECHAT_SHARE_TYPE_TALK);
                                WechatShareManager.ShareContentWebpage webpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag(
                                        titleShare, API.shareTitle,
                                        API.share_url + voiceGroupObj.voicefile_index, R.drawable.app_logo);
                                mShareManager.shareByWebchat(webpage, WechatShareManager.WECHAT_SHARE_TYPE_TALK, voiceGroupObj.sys_headurl);
                            }

                            @Override
                            public void handleCallBackCollect() {//朋友圈
                                if (!Util.isWebchatAvaliable(MyCreateRecordsActivity.this)) {
                                    Toast.makeText(MyCreateRecordsActivity.this, "请先安装微信", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                WechatShareManager.ShareContentWebpage webpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag(
                                        titleShare, API.shareTitle,
                                        API.share_url + voiceGroupObj.voicefile_index, R.drawable.app_logo);
                                mShareManager.shareByWebchat(webpage, WechatShareManager.WECHAT_SHARE_TYPE_FRENDS, voiceGroupObj.sys_headurl);

                            }

                            @Override
                            public void handleCallBackShare() {//新浪微博
//                                testShareWebUrl(API.share_url + voiceGroupObj.voicefile_index,
//                                        MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_index), API.shareTitle
//                                );
                            }

                            @Override
                            public void handleCallBackApply() {//qq好友
                                shareOnlyUrlOnQQorZone(false, API.share_url + voiceGroupObj.voicefile_index,
                                        titleShare, API.shareTitle,voiceGroupObj.sys_headurl);
                            }

                            @Override
                            public void handleCallBackQqZone() {//qq空间
                                shareOnlyUrlOnQQorZone(true, API.share_url + voiceGroupObj.voicefile_index,
                                        titleShare, API.shareTitle,voiceGroupObj.sys_headurl);

                            }
                        });


                    }

                    @Override
                    public void handleCallBackApply() {
                        if(!MyStringUtils.isopenBluetooth(MyCreateRecordsActivity.this)) return;


                        if(!perssion_func()){
    return;
}
                        boolCanusePress=false;
                        String   blindDeviceId = configPref.userDeviceMac().get();
                        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
                        Log.e("blindDeviceId:", blindDeviceId);
//                        if (ContextCompat.checkSelfPermission(MyCreateRecordsActivity.this,
//                                Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
//                            mWakeLock.acquire();
//                        }
                        new Apply_Record_Popwindow().showPopwindow(MyCreateRecordsActivity.this, imageView,blindDeviceId,
                                configPref.userUnion().get(),voiceGroupObj.voicefile_index, new Apply_Record_Popwindow.CallBackPayWindow() {
                                    @Override
                                    public void handleCallBackPayWindowFromStop(String recorddir) {
                                        boolCanusePress=true;
//                                        if (ContextCompat.checkSelfPermission(MyCreateRecordsActivity.this,
//                                                Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
//                                            mWakeLock.release();
//                                        }
                                    }

                                    @Override
                                    public void handleCallBackPayWindowFromStart(String recorddir) {

                                    }
                                });
                    }
                });


//                delMyCreateRecordListInfo(voiceGroupObj.voicefile_index);

            }
        }

    }

    public boolean perssion_func() {
        String unionid=configPref.userDeviceMac().get();

        if(unionid!=null&&!unionid.equals("")) {
            return true;
        }else{
            new One_Permission_Popwindow().showPopwindow(MyCreateRecordsActivity.this, title,null,null,null, new One_Permission_Popwindow.CallBackPayWindow() {
                @Override
                public void handleCallBackChangeUser() {
                    Util.outLogin(MyCreateRecordsActivity.this, configPref);
                    Util.startActivityNewTask(MyCreateRecordsActivity.this, WelcomeActivity_.class);
                    MyCreateRecordsActivity.this.finish();


                }

                @Override
                public void handleCallBackBindDevice() {
                    Util.startActivityNewTask(MyCreateRecordsActivity.this, WelcomeActivity_.class);
                    MyCreateRecordsActivity.this.finish();


                }
            });
            return false;

        }
    }
    //qq 分享
    public void shareOnlyUrlOnQQorZone(boolean qqzone, String url,String content,String title,String headUrl) {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "茶密");
        params.putString(QQShare.SHARE_TO_QQ_TITLE, content);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, title);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                headUrl);

//        String iconlocalurl = FileUtilQq.existQQshareIcon();
//        if (!TextUtils.isEmpty(iconlocalurl)) {
//            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,
//                    iconlocalurl);
//
//        }

        if (qqzone) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN); //打开这句话，可以实现分享纯图到QQ空间
        }
        doShareToQQ(params);
    }

    private void doShareToQQ(final Bundle params) {
        // QQ分享要在主线程做


        MainApp mainApp= (MainApp)      getApplicationContext();
        final Tencent mTencent = mainApp.mTencent;


        if (!MyStringUtils.isQQClientAvailable(MyCreateRecordsActivity.this)) {
            //提醒用户没有按照微信
            Toast.makeText(MyCreateRecordsActivity.this, "没有安装qq,请先安装qq!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Handler(getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(MyCreateRecordsActivity.this, params, qqShareListener);
                }
            }
        });
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {

//            Util.Toast(QQActivity.this, "onCancel: ");
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
//            Util.Toast(QQActivity.this, "onComplete: " + response.toString());
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
//            Util.Toast(QQActivity.this, "onError: " + e.errorMessage+ "e");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
    }





}




