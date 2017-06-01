package com.pushtest.teaquinticbleutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;
import quinticble.QuinticScanCallback;
import quinticble.QuinticScanResult;


public class MainActivityTeaUi extends AppCompatActivity {

    private LinearLayout listDevice;

    private QuinticDeviceFactoryTea quinticDeviceFactory;

    private Set<String> addresses = new HashSet<>();
    private QuinticDeviceTea quinticDevice;


    private Button btnRescan;

    private Button btnFindOne;

    private Spinner spinRssi;

    private int rssi;

    private List<Integer> rssiList = new ArrayList<>();


    private Map<Integer, String> mapDevice = new HashMap<Integer, String>();
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MYACTION.equals(intent.getAction())) {
                Log.i("onReceive", "get the broadcast from DownLoadService...");
                final String curLoad = intent.getStringExtra("CurrentLoading");
                Log.i("Broadcast receive", curLoad);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLogView(text_log_content_id, curLoad + "\n");

                    }
                });
//                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }
    };
    String MYACTION = "com.pushtest.broadcast";
    TextView text_func_content_id, text_log_content_id;
    //    Editable text_func_contentEditable, text_log_contentEditable;
    GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MYACTION);
        registerReceiver(receiver, filter);

        //startService(new Intent(this, DaemonService.class));

        quinticDeviceFactory = QuinticDeviceFactoryTea.getInstance(this);

        GridView gridview = (GridView) findViewById(R.id.gridview);


        text_func_content_id = (TextView) findViewById(R.id.text_func_content_id);
        text_log_content_id = (TextView) findViewById(R.id.text_log_content_id);
        text_func_content_id.setMovementMethod(ScrollingMovementMethod.getInstance());
        text_log_content_id.setMovementMethod(ScrollingMovementMethod.getInstance());
        final EditText connect_edittext_id = (EditText) findViewById(R.id.connect_edittext_id);


        Button connect_button_id = (Button) findViewById(R.id.connect_button_id);
        connect_button_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDeviceFunc(connect_edittext_id.getText().toString());
            }
        });
        final EditText sendcode_edittext_id = (EditText) findViewById(R.id.sendcode_edittext_id);


        Button sendcode_button_id = (Button) findViewById(R.id.sendcode_button_id);
        sendcode_button_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommonCode(sendcode_edittext_id.getText().toString(), "");

            }
        });


        text_func_content_id.setText("显示日志信息：", TextView.BufferType.EDITABLE);

        text_log_content_id.setText("显示固件上报日志信息：", TextView.BufferType.EDITABLE);

        gridview.setAdapter(new MyGridAdapter(MainActivityTeaUi.this));
        gridview.setOnItemClickListener(new ItemClickListener());


        rescan();//扫描蓝牙
    }

    void refreshLogView(TextView logView, String msg) {
        logView.append(msg);
        int offset = logView.getLineCount() * logView.getLineHeight();
        if (offset > logView.getHeight()) {
            logView.scrollTo(0, offset - logView.getHeight());
        }
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View arg1,// The view within the AdapterView that was clicked
                                int postion,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {

            // 显示所选Item的ItemText
            Log.d("onItemClick:", postion + "");
            int lastpos=func_text.length-1;
            switch (postion) {
                case 0://重新扫描
                    rescan();
                    break;
                case 1://智能连接上蓝牙 信号最强的
                    if (mapDevice.size() == 0) {
                        rescan();
                    } else {
                        connectBestRssiDeviceFunc();
                    }
                    break;

                case 2://断开连接
                    refreshLogView(text_func_content_id, "蓝牙断开 \n");

                    quinticDeviceFactory.abort();
                    break;


                default:
                    if (func_code_text[postion] != null) {
                        if(postion==lastpos){
                            sendMapListCode(func_code_text[postion], func_text[postion]);



                        }else {
                            setCommonCode(func_code_text[postion], func_text[postion]);
                        }
                    } else {

                            Toast.makeText(MainActivityTeaUi.this, "暂不支持", Toast.LENGTH_SHORT).show();

                    }
                    break;

            }


        }

    }

    public String[] func_code_text = {"", "", "", "EA02", "EA03", "EA04", "EA05", "EA06", "EA08", "EA09", "EA0A"
            , "EA0B", "EA0C", "EA0E", "EA0F", "EA10"
            , "EB02", "EB03", "EB0400371404050411", "EB0501", "EB06", "EB07", "EB080A", "EB0802", null, null,""};

    public String[] func_text = {"扫描", "连接最强设备", "断开连接", "查询指示灯状态"
            , "查询音量等级", "查询电池电量", "查询当前茶种ID"
            , "查询泡茶状态", "查询忘记洗茶次数", "查询当前设备状态"
            , "查询当前泡茶状态", "查询设备版本号"
            , "查询是否默认语音", "查询当前是否在充电", "查询当前水温"
            , "查询是否空杯", "设置指示灯使能", "设置指示灯无效", "设置日期命令"
            , "呼叫茶杯", "设置新消息未读"
            , "设置新消息已读", "语音音量10", "语音音量2"
            , "软件关机", "设备重启", "发送9个mp3"


    };

    public String getMaxRssiDevice() {
        Integer max = 0;
        for (int i = 0; i < rssiList.size(); i++) {

            if (rssiList.get(i) < max) {
                max = rssiList.get(i);
            }


        }
        String address = mapDevice.get(max);
        return address;
    }

    public void connectBestRssiDeviceFunc() {

        findDeviceFunc(getMaxRssiDevice());
    }

    public void setCommonCode(final String code, final String codecomment) {
        if (quinticDevice == null) {

            quinticDeviceFactory.abort();
            quinticDeviceFactory.findDevice(getMaxRssiDevice(), new QuinticCallbackTea<QuinticDeviceTea>() {
                @Override
                public void onComplete(final QuinticDeviceTea result) {
                    super.onComplete(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            quinticDevice = result;
                            refreshLogView(text_func_content_id, "已经连接上：" + "macid:" + result.getAddress() + "\n");
                            setCommonCode(code, codecomment);
                        }
                    });
                }

                @Override
                public void onError(final QuinticException ex) {
                    super.onError(ex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setConnectButtonsEnabled(true);
                            Toast.makeText(MainActivityTeaUi.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            refreshLogView(text_func_content_id, codecomment + "\n");

            refreshLogView(text_func_content_id, " send code：" + code + "\n");

            quinticDevice.sendCommonCode(code, new QuinticCallbackTea<String>() {
                @Override
                public void onComplete(final String result) {
                    super.onComplete(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLogView(text_func_content_id, " back code：" + result + "\n");

                        }
                    });
                }
            });
        }
    }

    public void sendMapListCode(final String code, final String codecomment) {
        if (quinticDevice == null) {

            quinticDeviceFactory.abort();
            quinticDeviceFactory.findDevice(getMaxRssiDevice(), new QuinticCallbackTea<QuinticDeviceTea>() {
                @Override
                public void onComplete(final QuinticDeviceTea result) {
                    super.onComplete(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            quinticDevice = result;
                            refreshLogView(text_func_content_id, "已经连接上：" + "macid:" + result.getAddress() + "\n");
                            sendMapListCode(code, codecomment);
                        }
                    });
                }

                @Override
                public void onError(final QuinticException ex) {
                    super.onError(ex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setConnectButtonsEnabled(true);
                            Toast.makeText(MainActivityTeaUi.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            refreshLogView(text_func_content_id, codecomment + "\n");

            refreshLogView(text_func_content_id, " send code：" + code + "\n");

            List<byte[]> codeList=new ArrayList<byte[]>();
            for(int i=0;i<9;i++){
                String fileName=(i+3)+".mp3";

                codeList.add(getFromAssets(fileName));
            }


            quinticDevice.sendListMp3Code(codeList, new QuinticCallbackTea<String>() {
                @Override
                public void onComplete(final String result) {
                    super.onComplete(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLogView(text_func_content_id, " back code：" + result + "\n");

                        }
                    });
                }
            });
        }
    }


    //从assets 文件夹中获取文件并读取数据
    public byte[] getFromAssets(String fileName){
        String result = "";
        byte[]  buffer=null;
        try {
            InputStream in = getResources().getAssets().open(fileName);
//获取文件的字节数
            int lenght = in.available();
//创建byte数组
            buffer = new byte[lenght];
//将文件中的数据读到byte数组中
            in.read(buffer);
//            result = EncodingUtils.getString(buffer, ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;

}

    public void findDeviceFunc(String deviceAddress) {
        quinticDeviceFactory.abort();
        quinticDeviceFactory.findDevice(deviceAddress, new QuinticCallbackTea<QuinticDeviceTea>() {
            @Override
            public void onComplete(final QuinticDeviceTea result) {
                super.onComplete(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        quinticDevice = result;
                        refreshLogView(text_func_content_id, "已经连接上：" + "macid:" + result.getAddress() + "\n");

                    }
                });
            }

            @Override
            public void onError(final QuinticException ex) {
                super.onError(ex);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setConnectButtonsEnabled(true);
                        Toast.makeText(MainActivityTeaUi.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    //扫描所有设备在 日志内容中显示
    private void rescan() {
        refreshLogView(text_func_content_id, "正在扫描蓝牙" + "\n");

        addresses.clear();
//        listDevice.removeAllViews();
        rssiList.clear();
        mapDevice.clear();
        quinticDeviceFactory.startScanDevice(new QuinticScanCallback() {
            @Override
            public void onScan(final QuinticScanResult scanResult) {
                if (!addresses.contains(scanResult.getDeviceAddress())) {
                    addresses.add(scanResult.getDeviceAddress());
                    rssiList.add(scanResult.getRssi());
                    mapDevice.put(scanResult.getRssi(), scanResult.getDeviceAddress());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLogView(text_func_content_id, "" + " macid: " + scanResult.getDeviceAddress() + " 信号：" + scanResult.getRssi() + "\n");


                        }
                    });
                }
            }

            @Override
            public void onStop() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivityTeaUi.this, "扫描已结束", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(QuinticException ex) {
                quinticDeviceFactory.abort();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivityTeaUi.this, "扫描发生错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onStart() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quinticDeviceFactory.abort();
        unregisterReceiver(receiver);
    }

    private void setConnectButtonsEnabled(boolean enabled) {
//        btnFindOne.setEnabled(enabled);
//        for (int i = 0; i < listDevice.getChildCount(); i++) {
//            Button button = (Button) listDevice.getChildAt(i).findViewById(R.id.connectDevice);
//            if (button != null) {
//                button.setEnabled(enabled);
//            }
//        }
    }
}
