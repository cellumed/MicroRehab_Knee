package com.cellumed.healthcare.microrehab.knee.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cellumed.healthcare.microrehab.knee.Bluetooth.Act_Device_Connect;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Act_noti extends BTConnectActivity implements  IMP_CMD, SqlImp {
    @Bind(R.id.bt_ok)
    ImageButton bt_go_next;
    @Bind(R.id.noti_text)
    TextView txt_noti;
    @Bind(R.id.scrollTxt)
    ScrollView scr_txt;


    private Context mContext;

    private ProgressDialog dialog;

    ListView historyList;
    View updated;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.act_noti);
        ButterKnife.bind(this);
        BudUtil.actList.add(this);
        setCustomActionbar();
     /*   Button.OnClickListener goNextBtn = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                go_next(v);

            }
        };
*/
  //      bt_go_next.setOnClickListener(goNextBtn);
        bt_go_next.setEnabled(false);
//        txt_noti.setMovementMethod(new ScrollingMovementMethod());
/*
        scr_txt.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                if(i1 + scr_txt.getMeasuredHeight() +10 >= txt_noti.getBottom()) {
                    bt_go_next.setEnabled(true);
                    bt_go_next.setBackgroundResource(R.drawable.btn_okay);

                }

            }
        });
        */

        scr_txt.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scr_txt != null) {
                    if (scr_txt.getChildAt(0).getBottom() <= (scr_txt.getHeight() + scr_txt.getScrollY())) {
                        //scroll view is at bottom
                        bt_go_next.setEnabled(true);
                        bt_go_next.setBackgroundResource(R.drawable.btn_okay);
                    } else {
                        //scroll view is not at bottom
                    }
                }
            }
        });


       // final int scrollAmount = txt_noti.getLayout().getLineTop(txt_noti.getLineCount()) - txt_noti.getHeight();

    }


    public void go_next(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_Device_Connect.class,bundle);
       //BudUtil.goActivity(mContext, Act_Home.class,bundle);

        this.finish();
    }


    @Override
    protected void onResume() {
        int resId;
        super.onResume();

    }




    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
       // actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
     //   ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_05);


        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ((ImageButton) findViewById(R.id.custom_back_btn)).setBackground(null);
        ((ImageButton) findViewById(R.id.custom_back_btn)).setEnabled(false);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);



    }



    @Override
    protected void connectedDevice() {}

    @Override
    protected void onPause() {
        super.onPause();
    }




    @Override
    protected void dataAvailableCheck(String data) {

        Log.d("TAG","DATA recved act activity");


        String[] sp=data.split(" ");

        if(sp.length!=20 || sp[0].equals("21")!=true || sp[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }


        String cmd=data.split(" ")[3];
        if(cmd.length()==1) cmd = "0" + cmd;


    }




}
