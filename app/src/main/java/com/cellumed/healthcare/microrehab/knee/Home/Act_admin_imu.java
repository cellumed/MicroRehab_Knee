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
import android.widget.ImageView;
import android.widget.TextView;

import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Act_admin_imu extends BTConnectActivity implements IMP_CMD, SqlImp {

  //  @Bind(R.id.rtv_a)
  //  ImageView rtvA;
    @Bind(R.id.rtv_b)
    ImageView rtvB;
  //  @Bind(R.id.rtv_c)
  //  ImageView rtvC;
 //   @Bind(R.id.cb_rtv_a)
 //   CheckBox cbRtvA;
    //@Bind(R.id.cb_rtv_b)
    //CheckBox cbRtvB;
 //   @Bind(R.id.cb_rtv_c)
 //   CheckBox cbRtvC;
    @Bind(R.id.angletxt)
    TextView ang_txt;


  //  String angle1;
    String angle2;
  //  String torsion;

    private Context mContext;
    private boolean type;

    String prev_data_recv="";

    private BackPressCloseHandler backPressCloseHandler;
/*
    int[] imageViewsA = { R.drawable.rtv_a0001, R.drawable.rtv_a0002, R.drawable.rtv_a0003, R.drawable.rtv_a0004, R.drawable.rtv_a0005, 
            R.drawable.rtv_a0006, R.drawable.rtv_a0007, R.drawable.rtv_a0008, R.drawable.rtv_a0009, R.drawable.rtv_a0010, 
            R.drawable.rtv_a0011, R.drawable.rtv_a0012, R.drawable.rtv_a0013, R.drawable.rtv_a0014, R.drawable.rtv_a0015};
*/
    // 5도단위. 40도까지는 같은 이미지. 0-5도가 1. 175-180이 35.
    int[] imageViewsB = { R.drawable.rtv_b0001, R.drawable.rtv_b0002, R.drawable.rtv_b0003, R.drawable.rtv_b0004, R.drawable.rtv_b0005,
            R.drawable.rtv_b0006, R.drawable.rtv_b0007, R.drawable.rtv_b0008, R.drawable.rtv_b0009, R.drawable.rtv_b0010,
            R.drawable.rtv_b0011, R.drawable.rtv_b0012, R.drawable.rtv_b0013, R.drawable.rtv_b0014, R.drawable.rtv_b0015,
        R.drawable.rtv_b0016, R.drawable.rtv_b0017, R.drawable.rtv_b0018, R.drawable.rtv_b0019, R.drawable.rtv_b0020,
        R.drawable.rtv_b0021, R.drawable.rtv_b0022, R.drawable.rtv_b0023, R.drawable.rtv_b0024, R.drawable.rtv_b0025,
        R.drawable.rtv_b0026, R.drawable.rtv_b0027, R.drawable.rtv_b0028, R.drawable.rtv_b0029, R.drawable.rtv_b0030,
        R.drawable.rtv_b0031, R.drawable.rtv_b0032, R.drawable.rtv_b0033, R.drawable.rtv_b0034, R.drawable.rtv_b0035,};
/*
    int[] imageViewsC = { R.drawable.rtv_c0001, R.drawable.rtv_c0002, R.drawable.rtv_c0003, R.drawable.rtv_c0004, R.drawable.rtv_c0005,
            R.drawable.rtv_c0006, R.drawable.rtv_c0007, R.drawable.rtv_c0008, R.drawable.rtv_c0009, R.drawable.rtv_c0010,
            R.drawable.rtv_c0011, R.drawable.rtv_c0012, R.drawable.rtv_c0013, R.drawable.rtv_c0014, R.drawable.rtv_c0015};    
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin_imu);

    //    angle1="01";
        angle2="180";
    //    torsion="01";

        setTitle("");
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);

        setCustomActionbar();

        backPressCloseHandler = new BackPressCloseHandler(this);

        // default imu setting



    }

    @Override
    public void onResume() {

        super.onResume();
        if (mBluetoothConnectService != null) {
            mBluetoothConnectService.send(CMD_START_SENS, "00020002");    // resp=ack?, type=raw, rehab_type=xx, sens_type= imu
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothConnectService.send(CMD_STOP_SENS, "");
    }

    @Override
    protected void connected_callback()
    {
        if(mBluetoothConnectService!=null) {
            mBluetoothConnectService.send(CMD_START_SENS, "00020002");    // resp=ack?, type=raw, rehab_type=xx, sens_type= imu
        }
    }

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {


        String[] sp_new=data.split(" ");
        if(sp_new.length!=20 || sp_new[0].equals("21")!=true || sp_new[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }



        String cmd=sp_new[3];
        if(cmd.length()==1) cmd = "0" + cmd;

        int interval;

        if (cmd.equals(CMD_RESP_RAW_SENS))
        {
            int tmp;
          /*  tmp = ((Integer.parseInt( data.split(" ")[4] ) /5));
            rtvA.setBackgroundResource(imageViewsA[tmp]);
            angle1= String.format("%02d",tmp);
*/
            tmp=((Integer.parseInt( data.split(" ")[4],16 ) ));
            int idx = (int)((double)tmp/5.0);
            angle2=String.format("%02d",tmp);
            if(idx < 1) idx=1;
            else if(idx > 35) idx=35;
            idx-=1;
            rtvB.setBackgroundResource(imageViewsB[idx]);

            ang_txt.setText(angle2);

            Log.d("TAG"," angle=" + angle2 +" idx="+idx);

/*
            tmp=((Integer.parseInt( data.split(" ")[6] ) /5));
            rtvC.setBackgroundResource(imageViewsC[tmp]);
            torsion=String.format("%02d",tmp);
*/


        }
    }


    @Override
    public void onBackPressed() {
     //   Log.e("TAG","bbbbbbbbbbbb");
        mBluetoothConnectService.send(CMD_STOP_SENS, "");
        super.onBackPressed();

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
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.widetxt_02);



        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        ImageButton btn = (ImageButton) findViewById(R.id.custom_back_btn);
        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBluetoothConnectService.send(CMD_STOP_SENS, "");
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );
    }

}
