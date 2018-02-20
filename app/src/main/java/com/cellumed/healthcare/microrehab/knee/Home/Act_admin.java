package com.cellumed.healthcare.microrehab.knee.Home;

import android.content.Context;
import android.content.Intent;
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

import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microrehab.knee.Dialog.DialogEmsEdit;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Act_admin extends BTConnectActivity {

    private Context mContext;
    private boolean type;

    //개별프로그램도 평가기록이 저장되도록
    private String db_idx;
    private String startTimeStr;

    private BackPressCloseHandler backPressCloseHandler;
    @Bind(R.id.bt_gait1) Button bt_gait1;
    @Bind(R.id.bt_squat1) Button bt_squat1;
    @Bind(R.id.bt_stepbox1) Button bt_stepbox1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin);
        setTitle("");
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);

        setCustomActionbar();

        //걷기, 스쿼트 , 스텝박스 위해
        startTimeStr=   BudUtil.getInstance().getToday("yyyy.MM.dd HH:mm:ss");
        db_idx = new DBQuery(mContext).getIdxFromStartDate(startTimeStr);

        Log.d("TAG","==== TEST21 ==== "+db_idx);

        //걷기 스쿼트 스텝박스 각 버튼 이동
        backPressCloseHandler = new BackPressCloseHandler(this);
        bt_gait1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                bundle.putInt("mode", 0);
                bundle.putInt("admin_mode", 0);
                bundle.putString("dbidx", db_idx);
                bundle.putString("title", "gait");
                BudUtil.goActivity(mContext, Act_EMS.class,bundle);
                Log.d("TAG","====TEST1===="+startTimeStr);
            }
        });

        bt_squat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                bundle.putInt("mode", 1);
                bundle.putInt("admin_mode", 0);
                bundle.putString("dbidx", db_idx);
                bundle.putString("title", "squat");
                BudUtil.goActivity(mContext, Act_EMS.class,bundle);
            }
        });
        bt_stepbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle bundle = new Bundle();
                bundle.putInt("mode", 2);
                bundle.putInt("admin_mode", 0);
                bundle.putString("dbidx", db_idx);
                bundle.putString("title", "stairs");
                BudUtil.goActivity(mContext, Act_EMS.class,bundle);
            }
        });
    }

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {}


    public void go_ems(View view) {
        final Bundle bundle = new Bundle();
        //BudUtil.goActivity(mContext, Act_Rehab_Pre.class,bundle);
        new DialogEmsEdit(mContext);
    }

    public void go_emg(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_admin_emg.class,bundle);
    }

    public void go_imu(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_admin_imu.class,bundle);
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
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_06);



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
