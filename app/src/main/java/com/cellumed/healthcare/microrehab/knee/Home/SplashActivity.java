package com.cellumed.healthcare.microrehab.knee.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.ContextUtil;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.PreferenceUtil;
import com.cellumed.healthcare.microrehab.knee.R;

public class SplashActivity extends BTConnectActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    ImageView imageView;

    @Override
    public void onCreate(Bundle icicle) {
        Log.e("SP","ONCREATE");
        super.onCreate(icicle);
        setContentView(R.layout.act_splash);
        Log.e("SP","ONCREATE contentview");
        ContextUtil.CONTEXT = this;
        getSupportActionBar().hide();
      //  imageView = (ImageView) findViewById(R.id.splashscreen);
      //  imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splash_image));

        startService();
        if (PreferenceUtil.lastConnectedDeviceAddress() != null) {
            mBluetoothConnectService.connect(PreferenceUtil.lastConnectedDeviceAddress());
            connectedDevice();
        } else {
            startHandler();
        }
    }

    private void startHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, Act_noti.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void connectedDevice() {
        Intent mainIntent = new Intent(SplashActivity.this, Act_Home.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void disconnectedDevice(int reason) {
        startHandler();
    }

    @Override
    protected void dataAvailableCheck(String data) {

    }
}