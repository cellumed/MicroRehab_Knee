package com.cellumed.healthcare.microrehab.knee.Home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import butterknife.ButterKnife;


public class Act_Rehab_Home extends BTConnectActivity implements SqlImp,IMP_CMD {

    private Context mContext;
    private boolean type;
    private boolean calibration_acked=false;

    private BackPressCloseHandler backPressCloseHandler;


    public void initSensor () {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title("센서를 초기화합니다")
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("잠시 정자세로 서서 기다려 주세요")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {

                    mBluetoothConnectService.send(CMD_REQ_START_CAL,"");
                    dialog.dismiss();

                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rehab_home);
        setTitle("");
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);

        setCustomActionbar();

        backPressCloseHandler = new BackPressCloseHandler(this);

        initSensor();
    }

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {

        String[] sp=data.split(" ");

        if(sp.length!=20 || sp[0].equals("21")!=true || sp[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }


        String cmd = data.split(" ")[3];
        if (cmd.length() == 1) cmd = "0" + cmd;

        int interval;


        if (cmd.equals(CMD_REQ_BATT_INFO)) {
            //battery_bgl.setBackgroundResource(R.drawable.battery_bg);

        } else if (cmd.equals(CMD_REQ_START_CAL)) {
            Log.e("TAG", "cal acked"  );
            calibration_acked = true;
        }


    }


    public void go_gait(View view) {
        final Bundle bundle = new Bundle();
        bundle.putString("title", "gait");
        BudUtil.goActivity(mContext, Act_Rehab_Pre.class,bundle);
    }

    public void go_squat(View view) {
        final Bundle bundle = new Bundle();
        bundle.putString("title", "squat");
        BudUtil.goActivity(mContext, Act_Rehab_Pre.class,bundle);
    }

    public void go_stairs(View view) {
        final Bundle bundle = new Bundle();
        bundle.putString("title", "stairs");
        BudUtil.goActivity(mContext, Act_Rehab_Pre.class,bundle);
    }


    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        //((ImageButton) findViewById(R.id.custom_back_btn)).setBackground(null);
        //((ImageButton) findViewById(R.id.custom_back_btn)).setEnabled(false);
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_02);



        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        ImageButton btn = (ImageButton) findViewById(R.id.custom_back_btn);
        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );
    }

}
