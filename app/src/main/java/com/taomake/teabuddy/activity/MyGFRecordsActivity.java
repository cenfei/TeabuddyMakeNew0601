package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.MyGridCreateRecordAdapter;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.Apply_Record_Popwindow;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.Record_Play_Popwindow;
import com.taomake.teabuddy.component.SZ_PayPopwindow_RecordBottom;
import com.taomake.teabuddy.component.SZ_PayPopwindow_ShareBottom;
import com.taomake.teabuddy.network.API;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BaseJson;
import com.taomake.teabuddy.object.VoiceGroupJson;
import com.taomake.teabuddy.object.VoiceGroupObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.FileUtilQq;
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
@EActivity(R.layout.cm_mygf_records)
public class MyGFRecordsActivity extends BaseActivity implements IWeiboHandler.Response {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;


    int y = 0;
    int limit = 10;
    private WechatShareManager mShareManager;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareManager = WechatShareManager.getInstance(MyGFRecordsActivity.this);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//   mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }


    public void initUi() {

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("茶密官方");
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
        activityName = MyGFRecordsActivity.class.getName();
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
    GridView gridview;

    void initdata() {

        gridview = (GridView) findViewById(R.id.gridview);


        myGridCreateRecordAdapter = new MyGridCreateRecordAdapter(this, voiceGroupObjList, false);
        gridview.setAdapter(myGridCreateRecordAdapter);
        gridview.setOnItemClickListener(new ItemClickListener());


    }

    ImageView imageViewLast;

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

        pageNum = 1;

        getDataFromServer();
    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getMyCreateRecordListInfo() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.getGFRecordList(this, new GetTeaListInfoHandler(), configPref.userUnion().get());//devno 空表示所有


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
//                voiceGroupObjList.add(new VoiceGroupObj());
                voiceGroupObjList.addAll(dbRecordsJson.obj);
//                initdata();
                myGridCreateRecordAdapter.setSeclection(selectTemp);

                myGridCreateRecordAdapter.notifyDataSetChanged();
                gridview.invalidate();
            }

        }
    }


    public void collectMyCreateRecordListInfo(String indexID) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.collectMyCreateRecord(this, new CollectMyCreateRecordHandler(), configPref.userUnion().get(), indexID);//devno 空表示所有


    }


    private class CollectMyCreateRecordHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            collectMyCreateRecordHandler(resp);
        }
    }

    public void collectMyCreateRecordHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            BaseJson dbRecordsJson = new Gson().fromJson(resp, BaseJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                Util.Toast(MyGFRecordsActivity.this, "收藏成功");
                collectBool = false;
//                getMyCreateRecordListInfo();
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

    public void delcollectMyCreateRecordListInfo(String indexID) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.undocollectMyCreateRecord(this, new DelTeaListInfoHandler(), configPref.userUnion().get(), indexID);//devno 空表示所有


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
            BaseJson dbRecordsJson = new Gson().fromJson(resp, BaseJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                Util.Toast(MyGFRecordsActivity.this, "已取消收藏");
                collectBool = true;
//                getMyCreateRecordListInfo();
            }
        }
    }


    int selectTemp = -1;
    boolean collectBool = true;

    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View view,// The view within the AdapterView that was clicked
                                final int postion,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {
            selectTemp = postion;
            // 显示所选Item的ItemText
            //跳转到


            final VoiceGroupObj voiceGroupObj = voiceGroupObjList.get(postion);

            final ImageView imageView = (ImageView) view.findViewById(R.id.db_img_select_id);

            myGridCreateRecordAdapter.setSeclection(postion);
            myGridCreateRecordAdapter.notifyDataSetChanged();
            collectBool = true;
            if (voiceGroupObj.fav != null && voiceGroupObj.fav.equals("1")) {
                collectBool = false;
            }

            new SZ_PayPopwindow_RecordBottom().showPopwindow(MyGFRecordsActivity.this, imageView, collectBool, new SZ_PayPopwindow_RecordBottom.CallBackPayWindow() {
                @Override
                public void handleCallBackPlay() {

                    new Record_Play_Popwindow().showPopwindow(MyGFRecordsActivity.this,
                            imageView, voiceGroupObj.voicefile_index, configPref.userUnion().get(), new Record_Play_Popwindow.CallBackPayWindow() {
                                @Override
                                public void handleCallBackDbSelect(String recorddir) {

                                }
                            });

                }

                @Override
                public void handleCallBackCollect() {
                    if (collectBool) {
                        voiceGroupObj.fav = "1";
                        collectMyCreateRecordListInfo(voiceGroupObj.voicefile_index);
                    } else {
                        voiceGroupObj.fav = "0";
                        delcollectMyCreateRecordListInfo(voiceGroupObj.voicefile_index);

                    }
                }

                @Override
                public void handleCallBackShare() {


                    new SZ_PayPopwindow_ShareBottom().showPopwindow(MyGFRecordsActivity.this, imageView, new SZ_PayPopwindow_ShareBottom.CallBackPayWindow() {
                        @Override
                        public void handleCallBackPlay() {//微信好友
                            if (!Util.isWebchatAvaliable(MyGFRecordsActivity.this)) {
                                Toast.makeText(MyGFRecordsActivity.this, "请先安装微信", Toast.LENGTH_LONG).show();
                                return;
                            }

//                            WechatShareManager.ShareContentWebpage webpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag("茶密", "茶密分享最动听的茶道秘书",
//                                    API.share_url + voiceGroupObj.voicefile_index, R.drawable.app_logo);
//                            mShareManager.shareByWebchat(webpage, WechatShareManager.WECHAT_SHARE_TYPE_TALK);
                            WechatShareManager.ShareContentWebpage webpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag(
                                    MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_index), API.shareTitle,
                                    API.share_url + voiceGroupObj.voicefile_index, R.drawable.app_logo);
                            mShareManager.shareByWebchat(webpage, WechatShareManager.WECHAT_SHARE_TYPE_TALK, voiceGroupObj.sys_headurl);
                        }

                        @Override
                        public void handleCallBackCollect() {//朋友圈
                            if (!Util.isWebchatAvaliable(MyGFRecordsActivity.this)) {
                                Toast.makeText(MyGFRecordsActivity.this, "请先安装微信", Toast.LENGTH_LONG).show();
                                return;
                            }

                            WechatShareManager.ShareContentWebpage webpage = (WechatShareManager.ShareContentWebpage) mShareManager.getShareContentWebpag(
                                    MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_index), API.shareTitle,
                                    API.share_url + voiceGroupObj.voicefile_index, R.drawable.app_logo);
                            mShareManager.shareByWebchat(webpage, WechatShareManager.WECHAT_SHARE_TYPE_FRENDS, voiceGroupObj.sys_headurl);

                        }

                        @Override
                        public void handleCallBackShare() {//新浪微博
                            testShareWebUrl(API.share_url + voiceGroupObj.voicefile_index,
                                    MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_index), API.shareTitle
                                    );
                        }

                        @Override
                        public void handleCallBackApply() {//qq好友
                            shareOnlyUrlOnQQorZone(false, API.share_url + voiceGroupObj.voicefile_index,MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_index), API.shareTitle,voiceGroupObj.sys_headurl);
                        }

                        @Override
                        public void handleCallBackQqZone() {//qq空间
                            shareOnlyUrlOnQQorZone(true, API.share_url + voiceGroupObj.voicefile_index,MyStringUtils.decodeUnicode(voiceGroupObj.voicefile_index), API.shareTitle,voiceGroupObj.sys_headurl);

                        }
                    });


                }

                @Override
                public void handleCallBackApply() {
                    if (!MyStringUtils.isopenBluetooth(MyGFRecordsActivity.this)) return;

                    String blindDeviceId = configPref.userDeviceMac().get();
                    blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
                    Log.e("blindDeviceId:", blindDeviceId);
//                        mWakeLock.acquire();
                    new Apply_Record_Popwindow().showPopwindow(MyGFRecordsActivity.this, imageView, blindDeviceId,
                            configPref.userUnion().get(), voiceGroupObj.voicefile_index, new Apply_Record_Popwindow.CallBackPayWindow() {
                                @Override
                                public void handleCallBackPayWindowFromStop(String recorddir) {
//                                mWakeLock.release();
                                }

                                @Override
                                public void handleCallBackPayWindowFromStart(String recorddir) {

                                }
                            });
                }
            });


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


        MainApp mainApp = (MainApp) getApplicationContext();
        final Tencent mTencent = mainApp.mTencent;


        if (!MyStringUtils.isQQClientAvailable(MyGFRecordsActivity.this)) {
            //提醒用户没有按照微信
            Toast.makeText(MyGFRecordsActivity.this, "没有安装qq,请先安装qq!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Handler(getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(MyGFRecordsActivity.this, params, qqShareListener);
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


    //分享到微博
    private WebpageObject getWebpageObj(String url, String title, String description) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;

//// 设置 Bitmap 类型的图片到视频对象里
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);

        mediaObject.thumbData = Util.Bitmap2Bytes(bitmap);
        mediaObject.actionUrl = url;
        mediaObject.defaultText = "照片分享";
        return mediaObject;
    }

    IWeiboShareAPI mWeiboShareAPI;

    public void testShareWebUrl(String url, String title, String description) {

        if (!MyStringUtils.isWeiboClientAvailable(MyGFRecordsActivity.this)) {
            //提醒用户没有按照微信
            Toast.makeText(MyGFRecordsActivity.this, "没有安装微博,请先安装微博!", Toast.LENGTH_SHORT).show();
            return;
        }

        MainApp mainApp = (MainApp) getApplicationContext();
        mWeiboShareAPI = mainApp.mWeiboShareAPI;

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = getWebpageObj(url, title, description);
// 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
// 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        mWeiboShareAPI.sendRequest(MyGFRecordsActivity.this, request);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, MyGFRecordsActivity.this);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */


    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(this, "Error Message: " + baseResp.errMsg,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}




