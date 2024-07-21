package com.aituolink.smartbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android_serialport_api.SerialControl;
import android_serialport_api.SerialPortFinder;
import android_serialport_api.SerialServer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;


public class MainActivity extends Activity {

    public final static String TAG = "MainActivity";
    //----------------------------------------------------关闭串口
    private void closeComPort(){
        if (SerialServer.getInstance()!=null){
            SerialServer.getInstance().stopSend();
            SerialServer.getInstance().close();
        }
    }
    //----------------------------------------------------开启串口
    private void openComPort(){
        try
        {
            // LK 型号 ttyS2
            // MT 型号 ttyS3
            SerialServer.getInstance().Init("/dev/ttyS3", SerialControl.LockBoardType.ATAT);


        } catch (SecurityException e) {
            //ShowMessage("打开串口失败:没有串口读/写权限!");
            Log.i("",e.getMessage());
        }   catch (Exception e) {
            //ShowMessage("打开串口失败:参数错误!");
            Log.i("",e.getMessage());
        }
    }



    private static TableLayout tl;

    private static Button[] btnBoard = new Button[12];
    private static Button[] btnBoardType = new Button[3];
    private static Button[] btn = new Button[50];

    private static Button btnTest;

    private static int _board = 1;
    private static int _count = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent1 = new Intent("com.android.action.hide_navigationbar");
        sendBroadcast(intent1);

        Intent intent2 = new Intent("com.android.action.hide_statusbar");
        sendBroadcast(intent2);

        tl = this.findViewById(R.id.table_locker);

        openComPort();

        btn[0] = (Button)findViewById(R.id.btn_1);
        btn[1] = (Button)findViewById(R.id.btn_2);
        btn[2] = (Button)findViewById(R.id.btn_3);
        btn[3] = (Button)findViewById(R.id.btn_4);
        btn[4] = (Button)findViewById(R.id.btn_5);
        btn[5] = (Button)findViewById(R.id.btn_6);
        btn[6] = (Button)findViewById(R.id.btn_7);
        btn[7] = (Button)findViewById(R.id.btn_8);
        btn[8] = (Button)findViewById(R.id.btn_9);
        btn[9] = (Button)findViewById(R.id.btn_10);
        btn[10] = (Button)findViewById(R.id.btn_11);
        btn[11] = (Button)findViewById(R.id.btn_12);
        btn[12] = (Button)findViewById(R.id.btn_13);
        btn[13] = (Button)findViewById(R.id.btn_14);
        btn[14] = (Button)findViewById(R.id.btn_15);
        btn[15] = (Button)findViewById(R.id.btn_16);
        btn[16] = (Button)findViewById(R.id.btn_17);
        btn[17] = (Button)findViewById(R.id.btn_18);
        btn[18] = (Button)findViewById(R.id.btn_19);
        btn[19] = (Button)findViewById(R.id.btn_20);
        btn[20] = (Button)findViewById(R.id.btn_21);
        btn[21] = (Button)findViewById(R.id.btn_22);
        btn[22] = (Button)findViewById(R.id.btn_23);
        btn[23] = (Button)findViewById(R.id.btn_24);
        btn[24] = (Button)findViewById(R.id.btn_25);
        btn[25] = (Button)findViewById(R.id.btn_26);
        btn[26] = (Button)findViewById(R.id.btn_27);
        btn[27] = (Button)findViewById(R.id.btn_28);
        btn[28] = (Button)findViewById(R.id.btn_29);
        btn[29] = (Button)findViewById(R.id.btn_30);
        btn[30] = (Button)findViewById(R.id.btn_31);
        btn[31] = (Button)findViewById(R.id.btn_32);
        btn[32] = (Button)findViewById(R.id.btn_33);
        btn[33] = (Button)findViewById(R.id.btn_34);
        btn[34] = (Button)findViewById(R.id.btn_35);
        btn[35] = (Button)findViewById(R.id.btn_36);
        btn[36] = (Button)findViewById(R.id.btn_37);
        btn[37] = (Button)findViewById(R.id.btn_38);
        btn[38] = (Button)findViewById(R.id.btn_39);
        btn[39] = (Button)findViewById(R.id.btn_40);
        btn[40] = (Button)findViewById(R.id.btn_41);
        btn[41] = (Button)findViewById(R.id.btn_42);
        btn[42] = (Button)findViewById(R.id.btn_43);
        btn[43] = (Button)findViewById(R.id.btn_44);
        btn[44] = (Button)findViewById(R.id.btn_45);
        btn[45] = (Button)findViewById(R.id.btn_46);
        btn[46] = (Button)findViewById(R.id.btn_47);
        btn[47] = (Button)findViewById(R.id.btn_48);
        btn[48] = (Button)findViewById(R.id.btn_49);
        btn[49] = (Button)findViewById(R.id.btn_50);

        btnBoard[0] = findViewById(R.id.btn_board_1);
        btnBoard[1] = findViewById(R.id.btn_board_2);
        btnBoard[2] = findViewById(R.id.btn_board_3);
        btnBoard[3] = findViewById(R.id.btn_board_4);
        btnBoard[4] = findViewById(R.id.btn_board_5);
        btnBoard[5] = findViewById(R.id.btn_board_6);
        btnBoard[6] = findViewById(R.id.btn_board_7);
        btnBoard[7] = findViewById(R.id.btn_board_8);
        btnBoard[8] = findViewById(R.id.btn_board_9);
        btnBoard[9] = findViewById(R.id.btn_board_10);
        btnBoard[10] = findViewById(R.id.btn_board_11);
        btnBoard[11] = findViewById(R.id.btn_board_12);

        btnBoardType[0]  = findViewById(R.id.btn_board_type_1);
        btnBoardType[1]  = findViewById(R.id.btn_board_type_2);
        btnBoardType[2]  = findViewById(R.id.btn_board_type_3);

        btnTest = findViewById(R.id.btn_test_1);

        for (int i = 0 ; i < 12 ; i++){

            btnBoard[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Button subBtn = (Button)view;
                    _board = Integer.parseInt (subBtn.getText().toString());

                    ShowBtn();
                }
            });
        }

        for (int i = 0 ; i < 3 ; i++){

            btnBoardType[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Button subBtn = (Button)view;
                    _count = Integer.parseInt (subBtn.getText().toString());

                    ShowBtn();
                }
            });
        }

        ShowBtn();

        for(int i = 0 ;i < 50; i++)
        {
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Button subBtn = (Button)v;
                    int door = Integer.parseInt(subBtn.getText().toString());

                    AppContext  appContext = (AppContext) AppContext.getContext();
                    SerialControl.ResultMsg resultMsg =SerialServer.getInstance().OpenBox(_board,door,3000);
                    if(resultMsg.getErrType().equals(SerialControl.ErrorType.No_Error))
                    {
                        Log.i("LockTest","ok");
                        Toast.makeText(getApplicationContext(),"OK" , Toast.LENGTH_SHORT).show();
                    }
                    else{

                        if(resultMsg.getErrMsg().length() != 0)
                        {
                            Log.i("LockTest",resultMsg.getErrMsg());
                            Toast.makeText(getApplicationContext(),resultMsg.getErrMsg(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.i("LockTest",resultMsg.getErrMsgById(resultMsg.getErrType()));
                            Toast.makeText(getApplicationContext(),resultMsg.getErrMsgById(resultMsg.getErrType()) , Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button subBtn = (Button)v;
                AppContext  appContext = (AppContext) AppContext.getContext();
                SerialControl.ResultMsg resultMsg =SerialServer.getInstance().hasBoard(_board,3000);
                if(resultMsg.getErrType().equals(SerialControl.ErrorType.No_Error))
                {
                    String show = "ok" + "[" + resultMsg.getValue().toString() + "]";
                    Log.i("LockTest",show);
                    Toast.makeText(getApplicationContext(),show , Toast.LENGTH_SHORT).show();
                }
                else{

                    if(resultMsg.getErrMsg().length() != 0)
                    {
                        Log.i("LockTest",resultMsg.getErrMsg());
                        Toast.makeText(getApplicationContext(),resultMsg.getErrMsg(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Log.i("LockTest",resultMsg.getErrMsgById(resultMsg.getErrType()));
                        Toast.makeText(getApplicationContext(),resultMsg.getErrMsgById(resultMsg.getErrType()) , Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        //------------------test code

//        String address1 = Public.getLocalIpAddress(this);
//        String address2 = Public.GetLocalMacAddressFromIp(this);
//        String address3 = Public.getWifiMacAddress(this);
//
//        Log.i(TAG , "local address 1: " + address1);
//        Log.i(TAG, "local address 2: " + address2);
//        Log.i(TAG, "local address 3: " + address3);

    }
    public void ShowBtn( ){

        for (int i = 0; i < 50 ; i++)
        {
            if(i < _count)
            {
                btn[i].setVisibility(View.VISIBLE);
            }
            else
            {
                btn[i].setVisibility(View.INVISIBLE);
            }


            {
                for(int j = 0 ; j < 3 ; j++){
                    btnBoardType[j].setTextColor(Color.WHITE);
                }
                if(_count == 12){
                    btnBoardType[0].setTextColor(Color.RED);
                }
                else if(_count == 24){
                    btnBoardType[1].setTextColor(Color.RED);
                }
                else if(_count == 50){
                    btnBoardType[2].setTextColor(Color.RED);
                }
            }
            {
                for(int j = 0 ; j < 12 ; j++){
                    btnBoard[j].setTextColor(Color.WHITE);

                    if(j == (_board-1)){
                        btnBoard[j].setTextColor(Color.RED);
                    }
                }
            }
        }
    }

    public void subBtn(){

        int col = 5;
        int count = 50;
        int row = count/col;
        if(count%col != 0)
        {
            row = row + 1;
        }

        for(int i = 0 ; i < row ; i++)
        {
            TableRow tr = new TableRow(this);
            tr.setPadding(3,3,3,3);
            tl.addView(tr);

            for(int j = 0 ; j < col; j++)
            {
                if(i * col + j >= count)
                    break;

                Button btn = new Button(this);
                btn.setTag(1);
                TableLayout.LayoutParams lp = new TableLayout.LayoutParams();
                lp.setMargins(5,5,5,5);
                lp.height = 50;
                lp.weight = 1;
                lp.gravity = Gravity.CENTER;
                btn.setLayoutParams( lp );

                btn.setBackgroundResource(R.color.colorBtnBKPJ);
                btn.setTextColor(Color.WHITE);
                btn.setTextSize(30);

                tr.addView(btn);
            }
        }
    }
}
