package com.taomake.teabuddy.activity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterBeginRecordListView;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.SZ_PayPopwindow_Avar;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.NineRecordsJson;
import com.taomake.teabuddy.object.RecordInfoObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.FileUtil;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_begin_record)
public class BeginRecordActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;


    @ViewById(R.id.cbr_avar_id)
    RoundedImageView cbr_avar_id;

    @ViewById(R.id.cbr_nickname_id)
    TextView cbr_nickname_id;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private PullToRefreshListView pullToRefreshListView;

    private AdapterBeginRecordListView adapterHomeDesignListView;

    private List<RecordInfoObj> designRoomInfos;

    private RelativeLayout design_choose_line;

    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {
        if(unionid.equals("gost001")){
            Util.startActivity(BeginRecordActivity.this, MainActivity_.class);


            Intent intent = new Intent(BeginRecordActivity.this,MainActivity_.class);
            intent.putExtra("FromActivity","BeginRecordActivity");
            startActivity(intent);
            finish();
        }else {
            finish();
        }
    }

    boolean boolSave=true;
    @Click(R.id.right_title_line)
    void onright_title_line() {//完成，上传编辑语音组
if(boolSave) {
boolSave=false;
    String cbr_nickname_idValue = cbr_nickname_id.getText().toString();
    if (cbr_nickname_idValue == null || cbr_nickname_idValue.equals("")) {


        Util.Toast(BeginRecordActivity.this, "请填写语音名称");
        return;
    }

    addBcNineRecords(cbr_nickname_idValue);
}

    }

    @Click(R.id.cbr_avar_click_id)
    void oncbr_avar_click_id() {
        changeAvar();
    }
    @Click(R.id.cbr_avar_id)
    void oncbr_avar_id() {
        changeAvar();
    }

    void changeAvar() {


        new SZ_PayPopwindow_Avar().showPopwindow(this, cbr_avar_id, new SZ_PayPopwindow_Avar.CallBackPayWindow() {
            @Override
            public void handleCallBackPayWindowFromCamara() {

                Intent intentFromCapture = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                if (FileUtil.checkSDCard()) {
                    intentFromCapture.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    IMAGE_FILE_NAME)));
                }
                startActivityForResult(intentFromCapture,
                        CAMERA_REQUEST_CODE);

            }

            @Override
            public void handleCallBackPayWindowFromLib() {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    startActivityForResult(intent, SELECT_PIC_KITKAT);
                } else {
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }

String unionid=null;

    public void initUi() {
        unionid=configPref.userUnion().get();

        if(unionid==null||unionid.equals("")){
//            unionidIntent =getIntent().getStringExtra("unionid");
            unionid="gost001";
        }



        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();


        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("开始创作");
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);
        TextView right_title = (TextView) findViewById(R.id.right_title);
        right_title.setVisibility(View.VISIBLE);

        right_title.setText("完成");
//        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);
//
//        right_title_line.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_design);

        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = BeginRecordActivity.class.getName();
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
        getBcNineRecords();


    }


    void initdata() {
        designRoomInfos = new ArrayList<RecordInfoObj>();

        adapterHomeDesignListView = new AdapterBeginRecordListView(this, designRoomInfos, unionid);
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
                        BeginRecordActivity.this,
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

        getDataFromServer();

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


    }

    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getBcNineRecords() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.bcRecordList(this, new GetBcNineRecordsHandler(), unionid);//devno 空表示所有


    }


    private class GetBcNineRecordsHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getBcNineRecordsHandler(resp);
        }
    }


    //    List<RecordInfoObj> recordInfoObjGloabl;
    public void getBcNineRecordsHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            NineRecordsJson teaDetailJsonGloabl = new Gson().fromJson(resp, NineRecordsJson.class);
            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {

                recordInfoObjGloabl = teaDetailJsonGloabl.obj;

                if (teaDetailJsonGloabl.obj != null && teaDetailJsonGloabl.obj.size() > 0) {//列表

                    if (pageNum == 1) {
                        designRoomInfos.clear();
                        designRoomInfos.addAll(teaDetailJsonGloabl.obj);
                    } else {
                        designRoomInfos.addAll(teaDetailJsonGloabl.obj);
                    }

                    RecordInfoObj recordInfoObj = recordInfoObjGloabl.get(0);

                    imageLoader.loadImage(recordInfoObj.sys_headurl, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            avarByte = Util.getBitmapByte(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
//                    avarByte = Util.getBitmapByte(imageLoader.loadImageSync(recordInfoObj.sys_headurl));
                    imageLoader.displayImage(recordInfoObj.sys_headurl, cbr_avar_id, options);
                    cbr_nickname_id.setText(MyStringUtils.decodeUnicode(recordInfoObj.voicefile_title));


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

                if (designRoomInfos != null && designRoomInfos.size() > 0) {//列表
                    downloadMp3List();
                }

                }

        }
    }

    public void addBcNineRecords(String cbr_nickname_idValue) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");


        List<File> fileList = new ArrayList<File>();
        String filePath = AdapterBeginRecordListView.Voice_Path;
        for (String fileName : AdapterBeginRecordListView.luyinArrays) {
            String filePathName = filePath + fileName + ".mp3";
            File file = new File(filePathName);
            if (file.exists()) {
                fileList.add(file);
            } else {
                Util.Toast(BeginRecordActivity.this, "请先录音");
                return;
            }

        }


        ProtocolUtil.addBcRecordList(this, new UpdateBcNineRecordsHandler(), unionid
                , cbr_nickname_idValue, avarByte, fileList);//devno 空表示所有


    }


    private class UpdateBcNineRecordsHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            foxProgressbarInterface.stopProgressBar();
            String bodyResp = new String(responseBody);
            Util.Toast(BeginRecordActivity.this, "上传成功");
            Log.i("bodyResp", bodyResp);
            if(unionid.equals("gost001")){
                Util.startActivity(BeginRecordActivity.this, MainActivity_.class);


                Intent intent = new Intent(BeginRecordActivity.this,MainActivity_.class);
                intent.putExtra("FromActivity","BeginRecordActivity");
                startActivity(intent);
                finish();
            }else {
                finish();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            foxProgressbarInterface.stopProgressBar();
if(responseBody!=null) {
    String bodyResp = new String(responseBody);
    Log.i("bodyResp", bodyResp);
}
            Util.Toast(BeginRecordActivity.this, "上传失败");
            boolSave=false;

        }
//        @Override
//        protected void handleResp(String resp) {
//            updateBcNineRecordsHandler(resp);
//        }
    }


    List<RecordInfoObj> recordInfoObjGloabl;

    public void updateBcNineRecordsHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            NineRecordsJson teaDetailJsonGloabl = new Gson().fromJson(resp, NineRecordsJson.class);
            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {

            }

        }
    }







    public  void downloadMp3List() {

        for(int position=0;position<designRoomInfos.size();position++)
        {
            RecordInfoObj personalRanking = designRoomInfos.get(position);
            String mp3DbUrl = personalRanking.voicefile_url;
            final String recordName = AdapterBeginRecordListView.luyinArrays[position] + ".mp3";
            downloadMp3(mp3DbUrl,recordName);

        }

//        try {
//            fixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        foxProgressbarInterface.stopProgressBar();


    }

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    public static   String  path = "teabuddy_record_file";
    public static   String  Voice_Path = Environment.getExternalStorageDirectory() + "/" + path + "/" ;


    public  void downloadMp3(final String urlStr, final String fileName) {

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
//                String fileName = "2.mp3";
                OutputStream output = null;
                try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //取得inputStream，并将流中的信息写入SDCard

                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                    String SDCard = Environment.getExternalStorageDirectory() + "";
                    String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

                    File file = new File(pathName);
                    InputStream input = conn.getInputStream();
                    if (file.exists()) {
                       Log.d("exits","exits");
//                        file.delete();
//                        return;
                    } else {
                        String dir = SDCard + "/" + path;
                        new File(dir).mkdir();//新建文件夹
                        file.createNewFile();//新建文件

                    }
                    output = new FileOutputStream(file);
                    //读取大文件
                    byte[] voice_bytes = new byte[1024];
                    int len1 = -1;
                    while ((len1 = input.read(voice_bytes)) != -1) {
                        output.write(voice_bytes, 0, len1);
                        output.flush();

                    }
                    System.out.println("success");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(output!=null)
                        output.close();
                        System.out.println("success");
                    } catch (IOException e) {
                        System.out.println("fail");
                        e.printStackTrace();
                    }
                }
            }

        });
        // String urlStr="http://172.17.54.91:8080/download/1.mp3";


    }











    public List<String> setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {


            jobJsons.add("打招呼");

        }

        return jobJsons;
    }


    //********************************图片选择*********************************************
    Bitmap tempCropBitmap;
    boolean changeImgAvar = true;
    byte[] avarByte = null;

    @OnActivityResult(RESULT_REQUEST_CODE)
    void onActivityResultCropAvatar(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                tempCropBitmap = extras.getParcelable("data");
                if (tempCropBitmap != null) {
                    changeImgAvar = false;

                    cbr_avar_id.setImageBitmap(tempCropBitmap);
                    avarByte = Util.getBitmapByte(tempCropBitmap);


//                    String avarBase64 = Base64Util.encode(avarByte);
//                    uploadAvar(avarBase64, "jpg");

//                    ProtocolUtil.updateAvar(this, new AsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                        }
//                    },configPref.userToken().get(),Util.getBitmapByte(tempCropBitmap));

                    //上传头像

                }
            }
        }
    }


    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int SELECT_PIC_KITKAT = 3;
    private static final int RESULT_REQUEST_CODE = 4;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String IMAGE_FILE_NAME = "tempSSAvatar.jpg";


    @OnActivityResult(SELECT_PIC_KITKAT)
    void onActivityResultSelectPicKitkat(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;
        startPhotoZoom(data.getData());
    }

    @OnActivityResult(IMAGE_REQUEST_CODE)
    void onActivityResultSelectPic(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;
        startPhotoZoom(data.getData());
    }

    @OnActivityResult(CAMERA_REQUEST_CODE)
    void onActivityResultCameraPic(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;
        if (FileUtil.checkSDCard()) {
            File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            startPhotoZoom(Uri.fromFile(tempFile));
        } else {
            Util.showToast(this, "没有sdcard");
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = getPath(this, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }

        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //原本uri返回的是file:  android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}




