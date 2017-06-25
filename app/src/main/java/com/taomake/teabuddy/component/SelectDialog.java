package com.taomake.teabuddy.component;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taomake.teabuddy.R;

/**
 * Created by foxcen on 17/6/24.
 */
public class SelectDialog extends AlertDialog {

        public SelectDialog(Context context, int theme,Record_Popwindow.CallBackPayWindow callBackPayWindow) {
            super(context, theme);
            this.callBackPayWindow=callBackPayWindow;
        }

        public SelectDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.cm_record_popwindow);
               pop_process_record_id = (TextView) findViewById(R.id.pop_process_record_id);

            TextView   pop_stop_record_id = (TextView) findViewById(R.id.pop_stop_record_id);
            callBackPayWindow.handleCallBackPayWindowFromStart("src");
//
            writehandler.post(runnable);
            pop_stop_record_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBackPayWindow.handleCallBackPayWindowFromStop("src");
                    writehandler.removeCallbacks(runnable);
                    closePopupWindow();


                }
            });
        }

    public void closePopupWindow(){
        this.dismiss();
    }


    private Integer chooseType = 1;//1代表刷刷钱包
    PopupWindow window;
    Record_Popwindow.CallBackPayWindow callBackPayWindow;
    TextView  pop_process_record_id;
    final Handler writehandler = new Handler();

    int process = 1;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process <=6) {

                pop_process_record_id.setText((6- process+""));

                writehandler.postDelayed(this, 1000);
                process = process + 1;
            } else {
                callBackPayWindow.handleCallBackPayWindowFromStop("src");


                writehandler.removeCallbacks(runnable);
                closePopupWindow();

            }
        }
    };
    }