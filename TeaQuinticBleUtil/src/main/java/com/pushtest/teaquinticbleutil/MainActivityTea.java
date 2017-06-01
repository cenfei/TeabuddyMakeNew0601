package com.pushtest.teaquinticbleutil;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;
import quinticble.QuinticScanCallback;
import quinticble.QuinticScanResult;


public class MainActivityTea extends AppCompatActivity {

    private LinearLayout listDevice;

    private QuinticDeviceFactoryTea quinticDeviceFactory;

    private Set<String> addresses;

    private Button btnRescan;

    private Button btnFindOne;

    private Spinner spinRssi;

    private int rssi;

    private List<Integer> rssiList = new ArrayList<>();


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MYACTION.equals(intent.getAction())){
                Log.i("onReceive", "get the broadcast from DownLoadService...");
            String    curLoad = intent.getStringExtra("CurrentLoading");
                Log.i("Broadcast receive", curLoad);
//                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }
    };
String  MYACTION="com.pushtest.broadcast";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MYACTION);
        registerReceiver(receiver, filter);

        //startService(new Intent(this, DaemonService.class));

        quinticDeviceFactory = QuinticDeviceFactoryTea.getInstance(this);

        listDevice = (LinearLayout) findViewById(R.id.deviceList);

        btnRescan = (Button) findViewById(R.id.btnRescan);

        btnFindOne = (Button) findViewById(R.id.btnFindOne);

        spinRssi = (Spinner) findViewById(R.id.spinRssi);

        rssi = Integer.parseInt((String) spinRssi.getSelectedItem());

        addresses = new HashSet<>();

        btnRescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescan();
            }
        });

        btnFindOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        spinRssi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rssi = -10 * (10 - position);
                for (int i = 0; i < rssiList.size(); i++) {
                    View child = listDevice.getChildAt(i);
                    child.setVisibility(rssiList.get(i) >= rssi ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rescan();
    }

    private void rescan() {
        addresses.clear();
        listDevice.removeAllViews();
        rssiList.clear();
        quinticDeviceFactory.startScanDevice(new QuinticScanCallback() {
            @Override
            public void onScan(final QuinticScanResult scanResult) {
                if (!addresses.contains(scanResult.getDeviceAddress())) {
                    addresses.add(scanResult.getDeviceAddress());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View deviceView = MainActivityTea.this.getLayoutInflater().inflate(R.layout.device_list_item, null);
                            TextView deviceAddress = (TextView) deviceView.findViewById(R.id.deviceAddress);
                            deviceAddress.setText(scanResult.getDeviceAddress() + " (" + scanResult.getRssi() + ")");
                            Button connectDevice = (Button) deviceView.findViewById(R.id.connectDevice);
                            connectDevice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setConnectButtonsEnabled(false);
                                    quinticDeviceFactory.abort();
                                    quinticDeviceFactory.findDevice(scanResult.getDeviceAddress(), new QuinticCallbackTea<QuinticDeviceTea>() {
                                        @Override
                                        public void onComplete(QuinticDeviceTea result) {
                                            super.onComplete(result);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    setConnectButtonsEnabled(true);
//                                                    Intent intent = new Intent(MainActivityTea.this, DeviceActivity.class);
//                                                    intent.putExtra("deviceAddress", scanResult.getDeviceAddress());
//                                                    startActivity(intent);
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
                                                    Toast.makeText(MainActivityTea.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            deviceView.setX(listDevice.getWidth());
                            deviceView.setVisibility(scanResult.getRssi() >= rssi ? View.VISIBLE : View.GONE);
                            rssiList.add(scanResult.getRssi());
                            listDevice.addView(deviceView);
                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(deviceView, "x", deviceView.getX(), 0);
                            objectAnimator.setDuration(500);
                            objectAnimator.start();
                            if (listDevice.getChildCount() % 2 == 0) {
                                deviceView.setBackgroundColor(Color.parseColor("#eeeeee"));
                            }
                        }
                    });
                }
            }

            @Override
            public void onStop() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivityTea.this, "扫描已结束", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(QuinticException ex) {
                quinticDeviceFactory.abort();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivityTea.this, "扫描发生错误", Toast.LENGTH_SHORT).show();
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
        btnFindOne.setEnabled(enabled);
        for (int i = 0; i < listDevice.getChildCount(); i++) {
            Button button = (Button) listDevice.getChildAt(i).findViewById(R.id.connectDevice);
            if (button != null) {
                button.setEnabled(enabled);
            }
        }
    }
}
