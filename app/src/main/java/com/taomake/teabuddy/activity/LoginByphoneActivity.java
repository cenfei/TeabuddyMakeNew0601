package com.taomake.teabuddy.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BindDeviceCodeJson;
import com.taomake.teabuddy.object.PhoneCodeJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.login_phone_view)
public class LoginByphoneActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.phone_edittext_id)
    EditText phone_edittext_id;

    @ViewById(R.id.code_edittext_id)
    EditText code_edittext_id;
    @ViewById(R.id.code_comment_id)
    TextView code_comment_id;

    @ViewById(R.id.age_edittext_id)
    EditText age_edittext_id;

    @ViewById(R.id.sex_switch_id)
    Switch sex_switch_id;

    int y = 0;
    int limit = 10;


    final Handler writehandler = new Handler();

    int process = 1;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process <= 60) {

                code_comment_id.setText((60 - process + "s"));
                code_comment_id.setClickable(false);
                code_comment_id.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_gray_img_white));
                code_comment_id.setTextColor(getResources().getColor(R.color.font_titie2));
                writehandler.postDelayed(this, 1000);
                process = process + 1;
            } else {
                process = 0;
                code_comment_id.setText("获取验证码");
                code_comment_id.setClickable(true);
                code_comment_id.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_black));
                code_comment_id.setTextColor(getResources().getColor(R.color.black));

                writehandler.removeCallbacks(runnable);

            }
        }
    };
    String phonenum = null;

    @Click(R.id.code_comment_id)
    void oncode_comment_id() {//获取验证码

        String phonenum = phone_edittext_id.getText().toString();

        if (phonenum == null || phonenum.equals("") || phonenum.length() != 11) {
            Util.Toast(LoginByphoneActivity.this, "手机号码有误");
            return;
        }
        //调用服务器获取验证码----并且同时开始计时
        writehandler.post(runnable);
        getMessageListFromServerForMsg(phonenum);

    }


    @Click(R.id.begin_use_id)
    void onbegin_use_id() {//开始诊断

        String phonenum = phone_edittext_id.getText().toString();

        if (phonenum == null || phonenum.equals("")) {
            Util.Toast(LoginByphoneActivity.this, "手机号码不能为空");
            return;
        }

        String phonecodeText = code_edittext_id.getText().toString();

        if (phonecodeText == null || phonecodeText.equals("")) {
            Util.Toast(LoginByphoneActivity.this, "验证码不能为空");
            return;
        }
        if (!phonecode.equals(phonecodeText)) {
            Util.Toast(LoginByphoneActivity.this, "验证码错误");
            return;
        }
        String age_edittext_idText = age_edittext_id.getText().toString();

        if (age_edittext_idText == null || age_edittext_idText.equals("")) {
            Util.Toast(LoginByphoneActivity.this, "年龄不能为空");
            return;
        }else{
            try {
                Integer ageint = Integer.valueOf(age_edittext_idText);
                if (ageint > 120) {
                    Util.Toast(LoginByphoneActivity.this, "年龄不能最大120岁");
                    return;
                }
            }catch (Exception e){
                Util.Toast(LoginByphoneActivity.this, "年龄格式不对");

            }
        }




        if (unionid == null || unionid.equals("")) {
            Util.Toast(LoginByphoneActivity.this, "登录失效，请重新登录");
            Util.startActivity(LoginByphoneActivity.this, LoginActivity_.class);
            finish();
            return;
        }
boolean boolman=sex_switch_id.isChecked();

        try {
            Log.d("no encode ticket",ticket);

            ticket= URLEncoder.encode(ticket,"UTF-8");
            Log.d("encode ticket",ticket);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        bindDeviceForMsg(phonenum, ticket, unionid,age_edittext_idText,boolman);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    String ticket = null;
    String unionid = null;
    String MANGER = null;

    public void initUi() {


        ticket = getIntent().getStringExtra("ticket");
        unionid = getIntent().getStringExtra("unionid");
        MANGER = getIntent().getStringExtra("MANGER");

        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = LoginByphoneActivity.class.getName();
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


    void initdata() {

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

        getDataFromServer();

        if(phone_edittext_id!=null)
            phone_edittext_id.requestFocus();
    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getMessageListFromServerForMsg(String mobile) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.getPhoneMsg(this, new GetPhoneCodeHandler(), mobile);


    }


    private class GetPhoneCodeHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getphoneHandler(resp);
        }
    }


    public void getphoneHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据


            PhoneCodeJson baseJson = new Gson().fromJson(resp, PhoneCodeJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                phonecode = baseJson.obj;


            }

        }
    }


    public void bindDeviceForMsg(String mobile, String ticket, String unionid,String age,boolean boolman) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.bindDevcieMsg(this, new BindDeviceHandler(), mobile, unionid, ticket,age,boolman?"0":"1");


    }


    private class BindDeviceHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            bindDeviceHandler(resp);
        }
    }

    String phonecode;

    public void bindDeviceHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据


            BindDeviceCodeJson baseJson = new Gson().fromJson(resp, BindDeviceCodeJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                if (unionid != null && !unionid.equals("")) {
                    configPref.userUnion().put(unionid);
                }
                if (ticket != null && !ticket.equals("")) {

                    configPref.userTicket().put(ticket);
                }
                if (baseJson.obj.mac != null && !baseJson.obj.mac.equals("")) {

                    configPref.userDeviceMac().put(baseJson.obj.mac);
                }
                if (baseJson.obj.deviceid != null && !baseJson.obj.deviceid.equals("")) {

                    configPref.userDeviceId().put(baseJson.obj.deviceid);
                    SharedPreferences.Editor editor = getSharedPreferences("dataUNIION", MODE_PRIVATE).edit();
                    editor.putString("deviceid", baseJson.obj.deviceid);
                    editor.commit();
                }

                if (MANGER != null && MANGER.equals("1")) {
                    finish();
                } else {
                    Util.startActivity(LoginByphoneActivity.this, MainActivity_.class);
                    finish();
                }


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


}




