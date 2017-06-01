package com.taomake.teabuddy.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BaseJson;
import com.taomake.teabuddy.object.TeaDetailJson;
import com.taomake.teabuddy.object.TeaDetailTimeObj;
import com.taomake.teabuddy.object.TeaInfoObj;
import com.taomake.teabuddy.object.TeaListJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;
import com.taomake.teabuddy.wheelview.OnWheelChangedListener;
import com.taomake.teabuddy.wheelview.OnWheelScrollListener;
import com.taomake.teabuddy.wheelview.adapters.AbstractWheelTextAdapter;
import com.taomake.teabuddy.wheelview.adapters.ArrayWheelAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticCommon;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_choose_tea)
public class ChooseTeaActivity extends BaseActivity {

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

    @Click(R.id.choose_ok_title)
    void onchoose_ok_title() {

        //设置选中的茶种保存到服务器

        if (chooseValue != null && !chooseValue.equals("")) {
//            saveChooseTea(mapTea.get(chooseValue).id);
            getTeaListInfo(mapTea.get(chooseValue).id);
        } else {
            Util.Toast(ChooseTeaActivity.this, "请选择茶种");
        }

    }


    @Click(R.id.right_title_line)
    void onright_title_line() {

        Util.startActivity(ChooseTeaActivity.this, ControlTeaActivity_.class);
        //finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }

    String teaName = null;

    public void initUi() {

        teaName = getIntent().getStringExtra("Tea_Name");
        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.black));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("选茶");
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        TextView right_title = (TextView) findViewById(R.id.right_title);
        right_title.setVisibility(View.VISIBLE);
        right_title.setText("管理茶种");


//        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = ChooseTeaActivity.class.getName();
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
//        getMessageListFromServerForMsg();


    }

    private String initValue = "";
    private String chooseValue = null;
    private com.taomake.teabuddy.wheelview.WheelView arrayWheel;

    private int maxTextSize = 18;
    private int minTextSize = 14;

    //    WheelDateAdapter yearAdapter;
//    WheelDateAdapter dayAdapter;
    ArrayWheelAdapter monthAdapter;
    String PLANETS[] = {};

    void initdata() {
        arrayWheel = (com.taomake.teabuddy.wheelview.WheelView) findViewById(R.id.wheel_view_wv);
        OnWheelChangedListener wheellistener = new OnWheelChangedListener() {
            @Override
            public void onChanged(com.taomake.teabuddy.wheelview.WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter);
                chooseValue = currentText;


            }
        };


        OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(com.taomake.teabuddy.wheelview.WheelView wheel) {
//                Log.e("start",wheel.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(com.taomake.teabuddy.wheelview.WheelView wheel) {
//                Log.e("finish",wheel.getCurrentItem()+"");
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter);
                chooseValue = currentText;
            }
        };

        int temp = 0;
        initValue = teaName;
        for (String a : PLANETS) {

            if (a.equals(initValue)) {
                break;
            } else {
                temp++;
            }
        }
        monthAdapter = new ArrayWheelAdapter<String>(ChooseTeaActivity.this, PLANETS, R.id.wheel_text, temp, true);
        chooseValue=PLANETS[temp-1];
        arrayWheel.setViewAdapter(monthAdapter);
        arrayWheel.setCurrentItem(temp);
        arrayWheel.addChangingListener(wheellistener);
        arrayWheel.addScrollingListener(onWheelScrollListener);
        setTextviewSize(PLANETS[temp], monthAdapter);

    }


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
        getTeaListInfo();//返回的时候重新刷新页面
//        getDataFromServer();
    }


    //**********获取筛选的后的list***************/

    public void getTeaListInfo() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.getTeaListInfo(this, new GetTeaListInfoHandler(), configPref.userUnion().get());//devno 空表示所有


    }


    private class GetTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getTeaListInfoHandler(resp);
        }
    }

    Map<String, TeaInfoObj> mapTea = new HashMap<String, TeaInfoObj>();


    public void getTeaListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            TeaListJson baseJson = new Gson().fromJson(resp, TeaListJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

//从服务器获取茶种名字列表
                List<TeaInfoObj> teaInfoObjs = baseJson.obj;
                Log.d("size", teaInfoObjs.size() + "");
                PLANETS = new String[teaInfoObjs.size()];

                for (int i = 0; i < teaInfoObjs.size(); i++) {
                    PLANETS[i] = MyStringUtils.decodeUnicode(teaInfoObjs.get(i).cz);
                    mapTea.put(PLANETS[i], teaInfoObjs.get(i));

                }

                initdata();

            }

        }
    }


    String teaId = null;

    public void saveChooseTea() {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.saveChooseTeaInfo(this, new SaveChooseTeaHandler(), configPref.userUnion().get(), teaId);//devno 空表示所有


    }


    private class SaveChooseTeaHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            saveChooseTeaHandler(resp);
        }
    }


    public void saveChooseTeaHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            BaseJson baseJson = new Gson().fromJson(resp, BaseJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                chooseTeaValue=chooseValue;

                Util.Toast(ChooseTeaActivity.this, "选茶成功");
                finish();
//                getTeaListInfo(teaId);
            }

        }
    }


    public static String chooseTeaValue=null;

    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getTeaListInfo(String czid) {
        teaId = czid;
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.getTeaDetailInfo(this, new GetTeaListInfoHandler2(), configPref.userUnion().get(), czid);//devno 空表示所有


    }


    private class GetTeaListInfoHandler2 extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getTeaListInfoHandler2(resp);
        }
    }


    TeaDetailJson teaDetailJsonGloabl;

    public void getTeaListInfoHandler2(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            teaDetailJsonGloabl = new Gson().fromJson(resp, TeaDetailJson.class);
            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {
//开始蓝牙操作
                List<TeaDetailTimeObj> teaDetailTimeObjs = teaDetailJsonGloabl.obj2;

                //指令
                if (teaDetailTimeObjs.size() > 1 && teaDetailTimeObjs.size() < 10) {
                    //蓝牙设置失败，不符合
                    Util.Toast(ChooseTeaActivity.this, "主人当前泡茶数为" + teaDetailTimeObjs.size() + "不符合1到10泡的范围，请前往编辑");
                    return;
                } else {
                    //
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("EB0A");

                    //添加他的id
                  Integer czid=Integer.valueOf(teaDetailJsonGloabl.obj.id);

                    Log.d("茶种id",""+czid);
//                    String fiveId="00";
//                    stringBuffer.append(fiveId);
//                    String fourId="00";
//                    stringBuffer.append(fourId);
//                    String threeId=QuinticCommon.unsignedIntToHexString(czid/(16*16*16*16));
//                    stringBuffer.append(threeId);
//                    String secondId=QuinticCommon.unsignedIntToHexString(czid/(16*16));
//                    stringBuffer.append(secondId);
//
//                    String firstId=QuinticCommon.unsignedIntToHexString(czid%(16));
//                    stringBuffer.append(firstId);

String  hexczid=Integer.toHexString(czid);
                for(int i=0;i<10-hexczid.length();i++){
                    stringBuffer.append("0");
                }
                    stringBuffer.append(hexczid);
                    //添加是否洗茶
                    String xc=teaDetailJsonGloabl.obj.xc;
                    stringBuffer.append(QuinticCommon.unsignedIntToHexString(Integer.valueOf(xc)));



                    StringBuffer stringBufferOther = new StringBuffer();
                    String lastHstr = null;
                    String lastLstr = null;

                    for (int i = 1; i <= 10; i++) {
                        if (i > teaDetailTimeObjs.size()) {
//                            stringBuffer.append("00");

                            stringBufferOther.append(lastHstr);
                            stringBufferOther.append(lastLstr);


                        } else {
                            //在size
                            TeaDetailTimeObj teaDetailTimeObj = teaDetailTimeObjs.get(i - 1);
                            Integer hvalue = Integer.valueOf(teaDetailTimeObj.p_second) / (16 * 16);

                            String hStr = QuinticCommon.unsignedIntToHexString(hvalue);
                            Integer lvalue = Integer.valueOf(teaDetailTimeObj.p_second) % (16 * 16);

                            String lStr = QuinticCommon.unsignedIntToHexString(lvalue);
                            lastHstr = hStr;
                            lastLstr = lStr;

                            stringBufferOther.append(hStr);
                            stringBufferOther.append(lStr);



                        }


                    }
//温度
                    Integer wd=Integer.valueOf(teaDetailJsonGloabl.obj.wd);
                    String wdStr=QuinticCommon.unsignedIntToHexString(wd);

                    stringBufferOther.append(wdStr);

                    //最后的指令是
                    String code=stringBuffer.append(stringBufferOther.toString()).toString();
Log.d("设置泡数指令",code.toUpperCase());
connectFindDevice(code.toUpperCase());

                }


            }

        }
    }



    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;


    public void closeProgress(){
        if(foxProgressbarInterface!=null){
            foxProgressbarInterface.stopProgressBar();
        }
    }

    boolean mustUpdate=false;

    public void connectFindDevice(final String code) {
        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);
//        foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(getActivity(), "主人茶密连接中...");

        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getbatteryLevel(code);
        } else {
            final Context context = ChooseTeaActivity.this;
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
                                            closeProgress();
                                            resultDeviceAll = resultDevice;
                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
                                            // ************处理动作
                                            getbatteryLevel(code);

                                        }
                                    });
                        }

                        @Override
                        public void onError(final QuinticException ex) {
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
//
                                            closeProgress();
//
                                        }
                                    });
                        }
                    });
        }
    }


    int batteryLevelValue = 0;

    public void connectSendCodeFailUi(String msg){
        closeProgress();
    }


    public void getbatteryLevel(String code) {
        if (resultDeviceAll == null) return;
//        String code = "EA04";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("设置茶失败");
                            }});
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("设置茶失败");

                    return;
                }
                new Handler(getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb0a01")) {
                                    saveChooseTea();
//                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
//                                    batteryLevelValue = QuinticCommon.unsignedByteToInt(data[3]);
                                    Log.d("设置茶",   "设置茶成功,前往服务器保存");
                                } else {
                                    connectSendCodeFailUi("设置茶失败");
                                }

                            }
                        });

            }
        });

    }


    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AbstractWheelTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setBackgroundColor(getResources().getColor(R.color.pc_choose_color));
                textvew.setTextColor(getResources().getColor(R.color.white));

                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);

                textvew.setTextColor(getResources().getColor(R.color.white));
                textvew.setBackgroundColor(getResources().getColor(R.color.white_alpha000));

            }
        }
    }
}




