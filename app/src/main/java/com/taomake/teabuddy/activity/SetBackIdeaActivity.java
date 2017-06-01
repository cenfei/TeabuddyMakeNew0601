package com.taomake.teabuddy.activity;

import android.os.Bundle;
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
import com.taomake.teabuddy.object.BaseJson;
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
@EActivity(R.layout.cm_backidea)
public class SetBackIdeaActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.backidea_content_id)
    EditText backidea_content_id;



    int y = 0;
    int limit = 10;
    @Click(R.id.backidea_send_id)
    void onbackidea_send_id() {

        String content=backidea_content_id.getText().toString();
       if(content==null||content.equals("")){

           Util.Toast(SetBackIdeaActivity.this,"请您留下宝贵反馈意见，谢谢");

           return;
       }

        sendBackIdeaInfo(content);

//        finish();

    }

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
        title.setText("意见反馈");
        title.setTextColor(getResources().getColor(R.color.black));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);


        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });


        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = SetBackIdeaActivity.class.getName();
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

    public void sendBackIdeaInfo(String content) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.sendBackIdea(this, new SendBackIdeaInfoHandler(), configPref.userUnion().get(),content );//devno 空表示所有


    }


    private class SendBackIdeaInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            sendBackIdeaInfoHandler(resp);
        }
    }


    public void sendBackIdeaInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {


            BaseJson baseJson = new Gson().fromJson(resp, BaseJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {


                Util.Toast(SetBackIdeaActivity.this,"反馈意见发送成功");
            }

        }
    }



    public List<String > setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {




            jobJsons.add("打招呼");

        }

        return jobJsons;
    }



}




