package com.taomake.teabuddy.activity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterMineSortListView;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.SortInfoJson;
import com.taomake.teabuddy.object.SortInfoObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.mine_sort_list)
public class MineSortListActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;

    private PullToRefreshListView pullToRefreshListView;

    private AdapterMineSortListView adapterHomeDesignListView;

    private List<SortInfoObj> designRoomInfos;

    private RelativeLayout design_choose_line;

    int y = 0;
    int limit = 20;


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
        title.setText("排行榜");
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

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_design);

        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = MineSortListActivity.class.getName();
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
        getMineSortListInfo();

    }


    void initdata() {
        designRoomInfos = new ArrayList<SortInfoObj>();
//        designRoomInfos=setTestData();

        adapterHomeDesignListView = new AdapterMineSortListView(this, designRoomInfos,configPref.userUnion().get());
        pullToRefreshListView.setAdapter(adapterHomeDesignListView);

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);// 设置底部下拉刷新模式
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setLastUpdatedLabel("加载更多");
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setPullLabel("松手刷新");
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("新未来，新物种");
        pullToRefreshListView.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("继续拉动");
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        MineSortListActivity.this,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);

                y = 0;
                pagenum++;
                getDataFromServer();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapterHomeDesignListView.mPersonal.size() < limit) {
                    pageNum = 0;
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

        pageNum = 0;

        getDataFromServer();
    }

    int pagenum=0;

    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getMineSortListInfo() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.getMineSortListInfo(this, new GetMineSortListInfoHandler(), configPref.userUnion().get(), pageNum*limit, limit);//devno 空表示所有


    }


    private class GetMineSortListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getMineSortListInfoHandler(resp);
        }
    }


    public void getMineSortListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {


            SortInfoJson baseJson = new Gson().fromJson(resp, SortInfoJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                //测试数据
//            baseJson.rows = (ArrayList<MessageJson>) setTestData();


                if (baseJson.obj != null && baseJson.obj.size() > 0) {//列表

                    if (pageNum == 0) {
                        designRoomInfos.clear();
                        designRoomInfos.addAll(baseJson.obj);
                    } else {
                        designRoomInfos.addAll(baseJson.obj);
                    }

                }


                if (designRoomInfos.size() <= 0 && pageNum == 0) {
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





}




