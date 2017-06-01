package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.DeviceCubImgJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;

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
@EActivity(R.layout.cm_readme)
public class ReadmeActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.quick_title_line_id)
    LinearLayout quick_title_line_id;

    @ViewById(R.id.detail_title_line_id)
    LinearLayout detail_title_line_id;


    int y = 0;
    int limit = 10;


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
//        left_title_icon.setImageDrawable(getResources().getDrawable(R.drawable.topbar_btn_bank));

        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("说明书");
        title.setTextColor(getResources().getColor(R.color.white));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        getQuickReameUrl();


//        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = ReadmeActivity.class.getName();
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
    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getQuickReameUrl() {

        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.getReadmeUrl(this, new DelTeaListInfoHandler(), "quickinstruction",configPref.userDeviceId().get());//devno 空表示所有


    }


    private class DelTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            delTeaListInfoHandler(resp);
        }
    }

    public void delTeaListInfoHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            DeviceCubImgJson dbRecordsJson = new Gson().fromJson(resp, DeviceCubImgJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                final String url = dbRecordsJson.obj;
                getDetailReameUrl();

                quick_title_line_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ReadmeActivity.this, WebViewActivity_.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", "快速使用说明书");
                        startActivity(intent);

                    }
                });

            }
        }
    }

    public void getDetailReameUrl() {

//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.getReadmeUrl(this, new GetDetailHandler(), "detailedinstruction",configPref.userDeviceId().get());//devno 空表示所有


    }


    private class GetDetailHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getDetailHandler(resp);
        }
    }

    public void getDetailHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            DeviceCubImgJson dbRecordsJson = new Gson().fromJson(resp, DeviceCubImgJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                final String url = dbRecordsJson.obj;
                detail_title_line_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ReadmeActivity.this, WebViewActivity_.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", "详细使用说明书");
                        startActivity(intent);

                    }
                });

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




