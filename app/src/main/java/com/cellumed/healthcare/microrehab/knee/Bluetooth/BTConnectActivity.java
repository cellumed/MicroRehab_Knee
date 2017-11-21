package com.cellumed.healthcare.microrehab.knee.Bluetooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import java.lang.reflect.Field;

import static com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD.CMD_REQ_BATT_INFO;
import static com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD.CMD_REQ_STOP_EMS;
import static com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD.CMD_STOP_SENS;

public abstract class BTConnectActivity extends AppCompatActivity {

    public Typeface typeface;
/*
// cmf1000w스타일로 폰드 변경 부분 변경
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(this.getAssets(), "NotoSansKR-Regular-Hestia.otf");
        }
        setGlobalFont(getWindow().getDecorView());
    }


    public void setGlobalFont(View view) {
        if(view != null && typeface!=null) {
            if(view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)view;
                int vgCnt = viewGroup.getChildCount();
                for(int i = 0; i<vgCnt; i++) {
                    View v = viewGroup.getChildAt(i);
                    if(v instanceof TextView) {
                      ((TextView) v).setTypeface(typeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }
*/
    void setDefaultFont()
    {
        if(typeface == null) {
            typeface = Typeface.createFromAsset(this.getAssets(), "NotoSansKR-Regular-Hestia.otf");
        }
        Field f= null;
        try {
            f = Typeface.class.getDeclaredField("DEFAULT");
            // f = Typeface.class.getDeclaredField("monospace");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(null, typeface);
            Log.e("PAIN0928", "change Font !!!!!!!!!!!!!!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    protected static boolean mConnected = false;

    protected BluetoothConnectService mBluetoothConnectService = null;

    protected static String deviceAddress;

    private static boolean pauseState;
    private static boolean disconnectedState;

    protected void connected_callback() {}
    protected void disconnected_callback() {}

    //heartbeat
    private Handler hbHandler = new Handler();
    private Runnable r_hb;

    private Handler hbTimeoutHandler = new Handler();
    private Runnable r_hbout;
    private int no_reply_cnt=0;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.e("TAG", "Service Connected");
            mBluetoothConnectService = ((BluetoothConnectService.LocalBinder) service).getService();
            mConnected = true;
            connected_callback();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothConnectService = null;
            mConnected = false;
            Log.e("TAG", "Service Disconnected");
            disconnected_callback();
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
         //   Log.e("BTConnACT","gatt recver");
            String data;
            final String action = intent.getAction();
            Log.e("TAG", action);
            if (BluetoothConnectService.ACTION_CONNECTED.equals(action)) {
                connectedDevice();
            } else if (BluetoothConnectService.ACTION_DISCONNECTED.equals(action)) {
                disconnectedDevice(0);
            } else if (BluetoothConnectService.ACTION_WRONG_ID.equals(action)) {
                disconnectedDevice(1);
            } else if (BluetoothConnectService.ACTION_DATA_AVAILABLE.equals(action)) {

                data=intent.getStringExtra(BluetoothConnectService.EXTRA_DATA);
                Log.d("onBroadcastRcv",data);

                String[] sp=data.split(" ");

                String cmd=data.split(" ")[3];
                if(cmd.length()==1) cmd = "0" + cmd;

                // cancel if bat response recved
                if(cmd.equals(CMD_REQ_BATT_INFO)) hbTimeoutHandler.removeCallbacksAndMessages(null);

                dataAvailableCheck(intent.getStringExtra(BluetoothConnectService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultFont();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startService();



        r_hbout = new Runnable()
        {
            @Override
            public void run() {

                no_reply_cnt++;


                if(mConnected)
                {
                    mBluetoothConnectService.send(CMD_REQ_STOP_EMS, "");
                    mBluetoothConnectService.send(CMD_STOP_SENS, "");

                }

                if(no_reply_cnt > 3)
                {
                    finishAllActivityAndStartConnectActivity();
                }
            }
        };

        r_hb = new Runnable() {
            @Override
            public void run() {

                if(mConnected)
                {
                    mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");    // battery
                    no_reply_cnt = 0;
                    hbTimeoutHandler.postDelayed(r_hbout, 2000);
                }

                hbHandler.postDelayed(this, 29000);
            }
        };
    }

    protected void startService() {
        if (mConnected) {
            Intent gattServiceIntent = new Intent(this, BluetoothConnectService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            Log.e("TAG", "bindService");
        } else {
            Intent gattServiceIntent = new Intent(this, BluetoothConnectService.class);
            gattServiceIntent.putExtra("address", deviceAddress);
            startService(gattServiceIntent);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            Log.e("TAG", "startService");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (disconnectedState) {
            finishAllActivityAndStartConnectActivity();
        }

        hbHandler.postDelayed(r_hb, 1000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);

        hbHandler.removeCallbacks(r_hb);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mServiceConnection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothConnectService.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothConnectService.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothConnectService.ACTION_WRONG_ID);
        intentFilter.addAction(BluetoothConnectService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    protected abstract void connectedDevice();

    protected void disconnectedDevice(int reason) {

        Toast toast=null;
		if(reason==0)
		{
			toast = Toast.makeText(this,
                    "블루투스 연결이 종료되서 연결화면으로 이동합니다.", Toast.LENGTH_SHORT);
		}
		else if(reason==1)
		{
			toast = Toast.makeText(this,
                    "잘못된 아이디 수신으로 블루투스 연결화면으로 이동합니다.", Toast.LENGTH_SHORT);
		}

        if(toast!=null) toast.show();
        finishAllActivityAndStartConnectActivity();

    }

    protected abstract void dataAvailableCheck(String data);

    private void finishAllActivityAndStartConnectActivity() {
        disconnectedState = false;
        for (int i = 0; i < BudUtil.actList.size(); i++) {
            BudUtil.actList.get(i).finish();
        }
        finish();
        Intent intent = new Intent(this, Act_Device_Connect.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("fin","act Dev connect intent");
        startActivity(intent);
    }

}
