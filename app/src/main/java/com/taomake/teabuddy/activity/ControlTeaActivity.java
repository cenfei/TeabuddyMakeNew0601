package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterTeasListView;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.TeaInfoObj;
import com.taomake.teabuddy.object.TeaListJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_pc_control)
public class ControlTeaActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;

    private PullToRefreshListView pullToRefreshListView;

    private AdapterTeasListView adapterHomeDesignListView;

    private List<TeaInfoObj> designRoomInfos;

    private RelativeLayout design_choose_line;

    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    @Click(R.id.right_title_line)
    void onright_title_line() {


//        Util.startActivity(ControlTeaActivity.this,ControlTeaUpdateActivity_.class);
//        finish();


        Intent intent = new Intent(ControlTeaActivity.this,ControlTeaUpdateActivity_.class);
        startActivity(intent);

//        finish();

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
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.black));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.VISIBLE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("管理茶种");
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);




        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_design);

        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = ControlTeaActivity.class.getName();
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


        getTeaListInfo();


    }


    void initdata() {
        designRoomInfos = new ArrayList<TeaInfoObj>();
//        designRoomInfos=setTestData();

        adapterHomeDesignListView = new AdapterTeasListView(this, designRoomInfos, new AdapterTeasListView.UpdateGridViewCallBack() {
            @Override
            public void updateCallFunc(int postion) {

            }

            @Override
            public void deleteCallFunc(int postion) {

                designRoomInfos.remove(postion);
                adapterHomeDesignListView.notifyDataSetChanged();
                pullToRefreshListView.onRefreshComplete();
                pullToRefreshListView.getRefreshableView().setSelection(y);

            }
        });
        pullToRefreshListView.setAdapter(adapterHomeDesignListView);

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);// 设置底部下拉刷新模式
//        pullToRefreshListView.getLoadingLayoutProxy(true, false)
//                .setLastUpdatedLabel("下拉刷新");
//        pullToRefreshListView.getLoadingLayoutProxy(true, false)
//                .setPullLabel("");
//        pullToRefreshListView.getLoadingLayoutProxy(true, false)
//                .setRefreshingLabel("正在刷新");
//        pullToRefreshListView.getLoadingLayoutProxy(true, false)
//                .setReleaseLabel("放开以刷新");

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        ControlTeaActivity.this,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);

                y = 0;
                getDataFromServer();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapterHomeDesignListView.mPersonal.size() < limit) {
                    pageNum = 1;
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
//                 jobJson = (MessageJson) adapterHomeDesignListView.getItem(position-1);

//                Util.startActivity(getActivity(), MyWorkDetailActivity_.class);

//                Intent intent = new Intent(getActivity(), HomeAllDetailActivity_.class);
//				intent.putExtra(Constant.CASE_HOME_ID,adapterHomeDesignListView.getItem(position-1).id);
//                intent.putExtra(Constant.CASE_HOME_NAME,adapterHomeDesignListView.getItem(position-1).name);
//
//                startActivity(intent);

            }
        });

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

    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

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
//                List<TeaInfoObj> teaInfoObjs = baseJson.obj;
//                Log.d("size", teaInfoObjs.size() + "");
//                PLANETS = new String[teaInfoObjs.size()];
//
//                for (int i = 0; i < teaInfoObjs.size(); i++) {
//                    mapTea.put(teaInfoObjs.get(i).cz, teaInfoObjs.get(i));
//                    PLANETS[i] = MyStringUtils.decodeUnicode(teaInfoObjs.get(i).cz);
//                }



                if (baseJson.obj != null && baseJson.obj.size() > 0) {//列表

                    if (pageNum == 1) {
                        designRoomInfos.clear();
                        designRoomInfos.addAll(baseJson.obj);
                    } else {
                        designRoomInfos.addAll(baseJson.obj);
                    }

                }


                if (designRoomInfos.size() <= 0 && pageNum == 1) {
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




