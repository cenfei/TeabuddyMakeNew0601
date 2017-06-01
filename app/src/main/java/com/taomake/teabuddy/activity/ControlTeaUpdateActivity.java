package com.taomake.teabuddy.activity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterUpdateTeasListView;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BaseJson;
import com.taomake.teabuddy.object.TeaDetailJson;
import com.taomake.teabuddy.object.TeaDetailTimeObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
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
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_pc_control_update)
public class ControlTeaUpdateActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
    //    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;
    @ViewById(R.id.tea_detail_name_editText)
    EditText tea_detail_name_editText;

    private PullToRefreshListView pullToRefreshListView;

    private AdapterUpdateTeasListView adapterHomeDesignListView;

    private List<TeaDetailTimeObj> designRoomInfos;

    private RelativeLayout design_choose_line;

    int y = 0;
    int limit = 10;


    @Click(R.id.set_ok_id)
    void onset_ok_id() {

//        String teaName=tea_detail_name_editText.getText().toString();
//        if(teaName==null||teaName.equals("")){
//            Util.Toast(ControlTeaUpdateActivity.this,"请设置茶名");
//            return;
//        }
//        teaDetailJsonGloabl.obj.cz= teaName;
//
//       saveTeaDetailInfo(teaDetailJsonGloabl);

        Integer second = fenP * 60 + fenS;

        if (second < 20) {
            Util.Toast(ControlTeaUpdateActivity.this, "主人，不能少于20秒,请重新选择");
            return;
        }

        TeaDetailTimeObj teaDetailTimeObj;
        if (designRoomInfos.size() > 0) {
            teaDetailTimeObj = designRoomInfos.get(postionUpdate);
            if (teaDetailTimeObj == null) {
                teaDetailTimeObj = new TeaDetailTimeObj();

                teaDetailTimeObj.p_second = second + "";

                designRoomInfos.add(teaDetailTimeObj);
            }


        } else {
            teaDetailTimeObj = new TeaDetailTimeObj();
            designRoomInfos.add(teaDetailTimeObj);
        }
        if (designRoomInfos.size() <= 10) {
            designRoomInfos.add(new TeaDetailTimeObj());
        }


        if (teaDetailTimeObj != null) {
            teaDetailTimeObj.p_second = second + "";
            adapterHomeDesignListView.notifyDataSetChanged();
            pullToRefreshListView.onRefreshComplete();
            pullToRefreshListView.getRefreshableView().setSelection(y);

        }

    }

    @Click(R.id.right_title_line)
    void onright_title_line() {

        String teaName = tea_detail_name_editText.getText().toString();
        if (teaName == null || teaName.equals("")) {
            Util.Toast(ControlTeaUpdateActivity.this, "请设置茶名");
            return;
        }
       if(designRoomInfos.size()>0){
           designRoomInfos.remove(designRoomInfos.size());
       }

        if(designRoomInfos.size()<1){
            Util.Toast(ControlTeaUpdateActivity.this, "主人，最少需要1泡");
            return;
        }
        if(designRoomInfos.size()>10){
            Util.Toast(ControlTeaUpdateActivity.this, "主人，最多10泡哦");
            return;
        }



        teaDetailJsonGloabl.obj.cz = teaName;
        teaDetailJsonGloabl.obj2 = designRoomInfos;
        saveTeaDetailInfo(teaDetailJsonGloabl);

    }


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    String isAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }

    String czIdIntent = null;

    public void initUi() {

        czIdIntent = getIntent().getStringExtra("CZ_ID");

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.black));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("管理茶种");
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        TextView right_title = (TextView) findViewById(R.id.right_title);
        right_title.setText("完成");
        right_title.setVisibility(View.VISIBLE);
//        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);


        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_design);
        initdataWheel();
        initdataWheel2();


        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = ControlTeaUpdateActivity.class.getName();
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

        if (czIdIntent != null) {
            getTeaListInfo(czIdIntent);
        }
//
//        getMessageListFromServerForMsg();


    }

    Integer fenP = 0;
    Integer fenS = 0;
    Integer postionUpdate = 0;

    void initdata() {
        designRoomInfos = new ArrayList<TeaDetailTimeObj>();
//        designRoomInfos = setTestData();

        if (czIdIntent == null) {//新建

            designRoomInfos.add(new TeaDetailTimeObj());
        }

        adapterHomeDesignListView = new AdapterUpdateTeasListView(this, designRoomInfos, new AdapterUpdateTeasListView.UpdateGridViewCallBack() {
            @Override
            public void addCallFunc(int postion) {

                postionUpdate = postion;


                designRoomInfos.remove(postion);


            }


            @Override
            public void updateCallFunc(int postion) {
                postionUpdate = postion;
                //更新girdview
                TeaDetailTimeObj teaDetailTimeObj = designRoomInfos.get(postion);
                String psecond = teaDetailTimeObj.p_second;
                Integer psecondInt = Integer.valueOf(psecond);


                fenP = psecondInt / 60;

                fenS = psecondInt % 60;


                arrayWheel.setCurrentItem(fenP);
                arrayWheel2.setCurrentItem(fenS);
                arrayWheel.invalidateWheel(true);
                arrayWheel2.invalidateWheel(true);

            }

            @Override
            public void deleteCallFunc(int postion) {
                //删除girdview
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
                        ControlTeaUpdateActivity.this,
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
    FoxProgressbarInterface foxProgressbarInterface;

    public void getTeaListInfo(String czid) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
//        czid="2533";//测试这个有数据
        ProtocolUtil.getTeaDetailInfo(this, new GetTeaListInfoHandler(), configPref.userUnion().get(), czid);//devno 空表示所有


    }


    private class GetTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getTeaListInfoHandler(resp);
        }
    }


    TeaDetailJson teaDetailJsonGloabl;

    public void getTeaListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            teaDetailJsonGloabl = new Gson().fromJson(resp, TeaDetailJson.class);
            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {

                tea_detail_name_editText.setHint(MyStringUtils.decodeUnicode(teaDetailJsonGloabl.obj.cz));//默认的茶种名字


                if (teaDetailJsonGloabl.obj2 != null && teaDetailJsonGloabl.obj2.size() > 0) {//列表

                    if (pageNum == 1) {
                        designRoomInfos.clear();
                        designRoomInfos.addAll(teaDetailJsonGloabl.obj2);
                    } else {
                        designRoomInfos.addAll(teaDetailJsonGloabl.obj2);
                    }

                }

                //最后一行添加一个+的按钮
                if (designRoomInfos.size() <= 10) {
                    designRoomInfos.add(new TeaDetailTimeObj());

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


    //**********获取筛选的后的list***************/

    public void saveTeaDetailInfo(TeaDetailJson teaDetailJson) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        String objjson = new Gson().toJson(teaDetailJson.obj);
        String objjson2 = new Gson().toJson(teaDetailJson.obj2);

        ProtocolUtil.saveTeaDetailInfo(this, new SaveTeaDetailInfo(), configPref.userUnion().get(), objjson, objjson2);//devno 空表示所有


    }


    private class SaveTeaDetailInfo extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            saveTeaDetailInfo(resp);
        }
    }


    public void saveTeaDetailInfo(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            BaseJson baseJson = new Gson().fromJson(resp, BaseJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                Util.Toast(ControlTeaUpdateActivity.this, "设置成功");
                finish();
            }
        }
    }

    public List<String> setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 6; i++) {


            jobJsons.add("打招呼");

        }

        return jobJsons;
    }


    private int maxTextSize = 18;
    private int minTextSize = 12;


    public String[] setdatawheel() {
        String PLANETS[] = new String[60];
        for (int i = 0; i < 60; i++) {
            PLANETS[i] = i + "";
        }

        return PLANETS;
    }


    private WheelView arrayWheel;
    ArrayWheelAdapter monthAdapter;

    void initdataWheel() {

        //测试数据
        String[] PLANETS = setdatawheel();

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
                fenP = Integer.valueOf(currentText);
                setTextviewSize(currentText, monthAdapter);
            }
        };

        int temp = PLANETS.length / 2;

        monthAdapter = new ArrayWheelAdapter<String>(ControlTeaUpdateActivity.this, PLANETS, R.id.wheel_text, temp, false);

        arrayWheel.setViewAdapter(monthAdapter);
        arrayWheel.setCurrentItem(temp);

        arrayWheel.addChangingListener(wheellistener);
        arrayWheel.addScrollingListener(onWheelScrollListener);
        setTextviewSize(PLANETS[temp], monthAdapter);


    }


    private WheelView arrayWheel2;
    ArrayWheelAdapter monthAdapter2;

    void initdataWheel2() {

        //测试数据
        String[] PLANETS = setdatawheel();

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
                fenS = Integer.valueOf(currentText);

                setTextviewSize(currentText, monthAdapter2);
            }
        };

        int temp = PLANETS.length / 2;

        monthAdapter2 = new ArrayWheelAdapter<String>(ControlTeaUpdateActivity.this, PLANETS, R.id.wheel_text, temp, false);

        arrayWheel2.setViewAdapter(monthAdapter2);
        arrayWheel2.setCurrentItem(temp);
        arrayWheel2.addChangingListener(wheellistener);
        arrayWheel2.addScrollingListener(onWheelScrollListener);
        setTextviewSize(PLANETS[temp], monthAdapter2);


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
                textvew.setBackgroundColor(getResources().getColor(R.color.white_alpha000));
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




