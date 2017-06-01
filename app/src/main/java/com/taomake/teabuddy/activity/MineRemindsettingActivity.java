package com.taomake.teabuddy.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.MyGridWeekDayAdapter;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;
import com.taomake.teabuddy.wheelview.OnWheelChangedListener;
import com.taomake.teabuddy.wheelview.OnWheelScrollListener;
import com.taomake.teabuddy.wheelview.WheelView;
import com.taomake.teabuddy.wheelview.adapters.AbstractWheelTextAdapter;
import com.taomake.teabuddy.wheelview.adapters.ArrayWheelAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Set;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticCommon;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_remind_appsetting)
public class MineRemindsettingActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;

    @ViewById(R.id.remind_close_img)
    ImageView remind_close_img;

    @ViewById(R.id.remind_close_text)
    TextView remind_close_text;

    @ViewById(R.id.move_img)
    ImageView move_img;

    @ViewById(R.id.move_text)
    TextView move_text;

    @ViewById(R.id.intelligence_img)
    ImageView intelligence_img;

    @ViewById(R.id.intelligence_text)
    TextView intelligence_text;
    @ViewById(R.id.black_line_id)
    LinearLayout black_line_id;

    @ViewById(R.id.unclick_line)
    LinearLayout unclick_line;

    public void changeButtonGroup(int choosenum){

        chooseNumButton=choosenum;
        remind_close_img.setImageDrawable(getResources().getDrawable(R.drawable.remind_close_gray));
        remind_close_text.setTextColor(getResources().getColor(R.color.text_color));

        move_img.setImageDrawable(getResources().getDrawable(R.drawable.move_icon_gray));
        move_text.setTextColor(getResources().getColor(R.color.text_color));
        intelligence_img.setImageDrawable(getResources().getDrawable(R.drawable.intelligence_icon_gray));
        intelligence_text.setTextColor(getResources().getColor(R.color.text_color));

        switch (choosenum){
            case 0:
                remind_close_img.setImageDrawable(getResources().getDrawable(R.drawable.remind_close));
                remind_close_text.setTextColor(getResources().getColor(R.color.black));
                black_line_id.setVisibility(View.VISIBLE);
                unclick_line.setClickable(false);
                break;
            case 1:
                move_img.setImageDrawable(getResources().getDrawable(R.drawable.move_icon));
                move_text.setTextColor(getResources().getColor(R.color.black));
                black_line_id.setVisibility(View.GONE);
                unclick_line.setClickable(true);


                break;
            case 2:
                intelligence_img.setImageDrawable(getResources().getDrawable(R.drawable.intelligence_icon));
                intelligence_text.setTextColor(getResources().getColor(R.color.black));
                black_line_id.setVisibility(View.VISIBLE);
                unclick_line.setClickable(false);


                break;

        }


    }



    @Click(R.id.remind_close_line)
    void onremind_close_line() {

        changeButtonGroup(0);

    }
    @Click(R.id.move_line)
    void onmove_line() {

        changeButtonGroup(1);

    }
    @Click(R.id.intelligence_line)
    void onintelligence_line() {

        changeButtonGroup(2);
    }

Integer chooseNumButton=0;

    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    @Click(R.id.right_title_line)
    void onright_title_line() {
        //完成 开始调用设置

        Set<Integer> choosePHash = myGridDbRecordAdapter.clickTempHash;

        StringBuffer sb = new StringBuffer();
        for (int i = 7; i >=0; i--) {
            if (choosePHash.contains(i)) {
                sb.append("1");

            } else {
                sb.append("0");
            }
        }

        Integer weekDayValue = Integer.valueOf(sb.toString(), 2);//周期 位开关

//        boolOpen = boolSwitch ? 1 : 0;

        if (afterNoon == 1) {
            hourSetting = hourSetting + 12;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("EB14");
        stringBuffer.append(QuinticCommon.unsignedIntToHexString(chooseNumButton));
        stringBuffer.append(QuinticCommon.unsignedIntToHexString(minuteSetting));
        stringBuffer.append(QuinticCommon.unsignedIntToHexString(hourSetting));
        stringBuffer.append(QuinticCommon.unsignedIntToHexString(weekDayValue));


        setSettingInfo(stringBuffer.toString());
        //finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }

//    boolean boolSwitch = false;
//    Switch switch1;

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
        title.setText("提醒设置");
        title.setTextColor(getResources().getColor(R.color.black));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        TextView right_title = (TextView) findViewById(R.id.right_title);
        right_title.setVisibility(View.VISIBLE);
        right_title.setText("完成");
        right_title.setTextColor(getResources().getColor(R.color.black));

//        switch1 = (Switch) findViewById(R.id.switch1);
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                // TODO Auto-generated method stub
//                boolSwitch = isChecked;
//
//            }
//        });

        initdataWheel();
        initdataWheel2();
        initdataWheel3();


        gridview = (GridView) findViewById(R.id.gridview);


        myGridDbRecordAdapter = new MyGridWeekDayAdapter(MineRemindsettingActivity.this);

        gridview.setAdapter(myGridDbRecordAdapter);
        gridview.setOnItemClickListener(new ItemClickListener());


        connectFindDevice();
        initdata();
    }

    GridView gridview;
    MyGridWeekDayAdapter myGridDbRecordAdapter;

    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View arg1,// The view within the AdapterView that was clicked
                                int postion,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {
//            DbRecordInfoObj dbRecordInfoObj=dbRecordInfoObjList.get(postion);
//
//            // 显示所选Item的ItemText   选中的地址
//           callBackPayWindow.handleCallBackDbSelect(dbRecordInfoObj.voicefile_url);
//            closePopupWindow(activity);


            myGridDbRecordAdapter.setSeclection(postion);
            myGridDbRecordAdapter.notifyDataSetChanged();
            gridview.invalidate();

//            setbatteryLevel(context);

        }

    }

    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = MineRemindsettingActivity.class.getName();
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
        connectFindDevice();

    }


    void initdata() {

    }


    @Override
    public void onDestroy() {
        //退出activity前关闭菜单

        super.onDestroy();

    }

    //**********网络***************/


    //************************蓝牙操作*********************************

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;

    public void connectFindDevice() {
//                foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(getActivity(), "蓝牙读取中...");

        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getSettingInfo();

        } else {
        final Context context = MineRemindsettingActivity.this;
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
//                                        getbatteryLevel();
                                        getSettingInfo();

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
                                            connectFindDevice();
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

    int  minuteSetting, hourSetting, weekInt;
    char[] weekIntCharArray;
    int afterNoon = 0;

    public void connectSettingInfoSuccess() {
        int afternoonbool = 0;
        if (hourSetting > 12) {
            afternoonbool = 1;
            arrayWheel3.setCurrentItem(hourSetting - 12-1);
            setTextviewSize("下午", monthAdapter);

        } else {
            arrayWheel3.setCurrentItem(hourSetting-1);
            setTextviewSize("上午", monthAdapter);

        }


        arrayWheel.setCurrentItem(afternoonbool);
        arrayWheel2.setCurrentItem(minuteSetting-1);
        setTextviewSize(minuteSetting + "", monthAdapter2);
        setTextviewSize(hourSetting + "", monthAdapter3);


        arrayWheel.invalidateWheel(true);
        arrayWheel2.invalidateWheel(true);
        arrayWheel3.invalidateWheel(true);

        //星期初始化
        for(int i=weekIntCharArray.length;i>=1;i--){
          if(weekIntCharArray[i-1]=='1'){
              myGridDbRecordAdapter.clickTempHash.add(7-i);
          }


        }


        myGridDbRecordAdapter.notifyDataSetChanged();
        gridview.invalidate();

    }


    public void setSettingInfo(String code) {
        if (resultDeviceAll == null) return;
//        String code = "EA14";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
//                connectSendCodeFailUi("电量查询失败");

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
//                    connectSendCodeFailUi("电量查询失败");

                    return;
                }
                new Handler(MineRemindsettingActivity.this.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb14")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    Util.Toast(MineRemindsettingActivity.this, "设置成功");


                                    finish();


                                }


                            }
                        });

            }
        });

    }


    public void getSettingInfo() {
        if (resultDeviceAll == null) return;
        String code = "EA14";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
//                connectSendCodeFailUi("电量查询失败");

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
//                    connectSendCodeFailUi("电量查询失败");

                    return;
                }
                new Handler(MineRemindsettingActivity.this.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea14")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    chooseNumButton = QuinticCommon.unsignedByteToInt(data[2]);

                                    changeButtonGroup(chooseNumButton);
//                                    switch1.setChecked(boolOpen == 0 ? false : true);

                                    minuteSetting = QuinticCommon.unsignedByteToInt(data[3]);

                                    hourSetting = QuinticCommon.unsignedByteToInt(data[4]);

                                    weekInt = QuinticCommon.unsignedByteToInt(data[5]);

                                    String eventArray = Integer.toBinaryString(weekInt);
                                    weekIntCharArray = eventArray.toCharArray();
                                    connectSettingInfoSuccess();
//                                    Log.d("当前电量", batteryLevelValue + "");
//                                    getTeaStatus();
                                }


                            }
                        });

            }
        });

    }


    //******************************************************

    private WheelView arrayWheel;
    ArrayWheelAdapter monthAdapter;
    String[] PLANETS= {"上午", "下午"};
    void initdataWheel() {

        //测试数据

        arrayWheel = (WheelView) findViewById(R.id.wheel_view_wv);
        OnWheelChangedListener wheellistener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter);


            }
        };


        OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
//                Log.e("start",wheel.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
//                Log.e("finish",wheel.getCurrentItem()+"");
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());

                afterNoon = wheel.getCurrentItem();
                setTextviewSize(currentText, monthAdapter);
            }
        };

        int temp = PLANETS.length / 2;

        monthAdapter = new ArrayWheelAdapter<String>(MineRemindsettingActivity.this, PLANETS, R.id.wheel_text, temp, 1);

        arrayWheel.setViewAdapter(monthAdapter);
        arrayWheel.setCurrentItem(temp);

        arrayWheel.addChangingListener(wheellistener);
        arrayWheel.addScrollingListener(onWheelScrollListener);
        setTextviewSize(PLANETS[temp], monthAdapter);


    }

    public String[] setdatawheel(int nub) {
        String PLANETS[] = new String[nub];
        for (int i = 0; i < nub; i++) {
            PLANETS[i] = i + "";
        }

        return PLANETS;
    }

    private WheelView arrayWheel2;
    ArrayWheelAdapter monthAdapter2;

    void initdataWheel2() {

        //测试数据
        String[] PLANETS2 = setdatawheel(60);

        arrayWheel2 = (WheelView) findViewById(R.id.wheel_view_wv2);
        OnWheelChangedListener wheellistener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) monthAdapter2.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter2);


            }
        };


        OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
//                Log.e("start",wheel.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
//                Log.e("finish",wheel.getCurrentItem()+"");
                String currentText = (String) monthAdapter2.getItemText(wheel.getCurrentItem());
                minuteSetting = Integer.valueOf(currentText);

                setTextviewSize(currentText, monthAdapter2);
            }
        };

        int temp = PLANETS2.length / 2;

        monthAdapter2 = new ArrayWheelAdapter<String>(MineRemindsettingActivity.this, PLANETS2, R.id.wheel_text, temp, 1);

        arrayWheel2.setViewAdapter(monthAdapter2);
        setTextviewSize(PLANETS2[temp], monthAdapter2);

        arrayWheel2.setCurrentItem(temp);
        arrayWheel2.addChangingListener(wheellistener);
        arrayWheel2.addScrollingListener(onWheelScrollListener);


    }

    private WheelView arrayWheel3;
    ArrayWheelAdapter monthAdapter3;
    void initdataWheel3() {

        //测试数据
        String[]   PLANETS3 = setdatawheel(12);

        arrayWheel3 = (WheelView) findViewById(R.id.wheel_view_wv3);
        OnWheelChangedListener wheellistener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) monthAdapter3.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter3);


            }
        };


        OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
//                Log.e("start",wheel.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
//                Log.e("finish",wheel.getCurrentItem()+"");
                String currentText = (String) monthAdapter3.getItemText(wheel.getCurrentItem());
                hourSetting = Integer.valueOf(currentText);

                setTextviewSize(currentText, monthAdapter3);
            }
        };

        int temp = PLANETS3.length / 2;

        monthAdapter3 = new ArrayWheelAdapter<String>(MineRemindsettingActivity.this, PLANETS3, R.id.wheel_text, temp, 1);

        arrayWheel3.setViewAdapter(monthAdapter3);
        arrayWheel3.setCurrentItem(temp);
        arrayWheel3.addChangingListener(wheellistener);
        arrayWheel3.addScrollingListener(onWheelScrollListener);
        setTextviewSize(PLANETS3[temp], monthAdapter3);


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
                textvew.setBackgroundColor(getResources().getColor(R.color.white));
                textvew.setTextColor(getResources().getColor(R.color.black));

                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);

                textvew.setTextColor(getResources().getColor(R.color.black_alpha50));
                textvew.setBackgroundColor(getResources().getColor(R.color.white));

            }
        }


    }

    private int maxTextSize = 18;
    private int minTextSize = 12;

}




