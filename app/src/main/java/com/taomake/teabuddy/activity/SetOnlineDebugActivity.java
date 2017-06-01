package com.taomake.teabuddy.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.OnlineDebugCodeJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_onlinedebug)
public class SetOnlineDebugActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.code_edittext_id)
    EditText code_edittext_id;
        @ViewById(R.id.begin_debug_id)
        TextView begin_debug_id;

    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }


    @Click(R.id.begin_debug_id)
    void onbegin_debug_id() {//开始诊断

        String codeStr = code_edittext_id.getText().toString();
        if (codeStr == null || codeStr.equals("")) {


            Util.Toast(SetOnlineDebugActivity.this, "请您填写客服提供的诊断验证码，谢谢");

            return;
        }


        getOnlineCode();


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
        title.setText("在线诊断");
        title.setTextColor(getResources().getColor(R.color.black));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        code_edittext_id=(EditText) findViewById(R.id.code_edittext_id);
        code_edittext_id.addTextChangedListener(textWatcher);

        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });


        initdata();
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String  codeValue=code_edittext_id.getText().toString();
            if(codeValue!=null&&!codeValue.equals("")){
                begin_debug_id.setTextColor(getResources().getColor(R.color.black));
            }else{
                begin_debug_id.setTextColor(getResources().getColor(R.color.text_begin_dbug));

            }
//
//            System.out.println("-1-onTextChanged-->"
//                    + code_edittext_id.getText().toString() + "<--");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
//
            String  codeValue=code_edittext_id.getText().toString();
            if(codeValue!=null&&!codeValue.equals("")){
                begin_debug_id.setTextColor(getResources().getColor(R.color.black));
            }else{
                begin_debug_id.setTextColor(getResources().getColor(R.color.text_begin_dbug));

            }
            Log.i("-2-beforeTextChanged-->",
                    code_edittext_id.getText().toString() + "<--");

        }

        @Override
        public void afterTextChanged(Editable s) {
            String  codeValue=code_edittext_id.getText().toString();
            if(codeValue!=null&&!codeValue.equals("")){
                begin_debug_id.setTextColor(getResources().getColor(R.color.black));
            }else{
                begin_debug_id.setTextColor(getResources().getColor(R.color.text_begin_dbug));

            }
//
//            System.out.println("-3-afterTextChanged-->"
//                    + code_edittext_id.getText().toString() + "<--");

        }
    };

    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = SetOnlineDebugActivity.class.getName();
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

//        getDataFromServer();
    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getOnlineCode() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.checkOnlineDebugCode(this, new SendBackIdeaInfoHandler());//devno 空表示所有


    }


    private class SendBackIdeaInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getOnlineCodeHandler(resp);
        }
    }


    public void getOnlineCodeHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {


            OnlineDebugCodeJson baseJson = new Gson().fromJson(resp, OnlineDebugCodeJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                String checkCode = baseJson.obj;

                String codeEditStr = code_edittext_id.getText().toString();

                if (codeEditStr.equals(checkCode)) {

                    Util.startActivity(SetOnlineDebugActivity.this, SetOnlineDebugTwoActivity_.class);
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




