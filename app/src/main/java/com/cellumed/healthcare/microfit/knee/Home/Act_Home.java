package com.cellumed.healthcare.microfit.knee.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microfit.knee.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microfit.knee.Dialog.DialogUserInfoEdit;
import com.cellumed.healthcare.microfit.knee.R;
import com.cellumed.healthcare.microfit.knee.Setting.Act_Setting;
import com.cellumed.healthcare.microfit.knee.Util.BudUtil;
import com.cellumed.healthcare.microfit.knee.Util.CustomToast;

import java.util.ArrayList;

import butterknife.ButterKnife;



public class Act_Home extends BTConnectActivity implements SqlImp,IMP_CMD {

    private Context mContext;
    private boolean type;

    private BackPressCloseHandler backPressCloseHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);
        setTitle("");
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);

        setCustomActionbar();

        backPressCloseHandler = new BackPressCloseHandler(this);

        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            Log.e("yourtag", s);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }

        ManageDeviceConfiguration.getInstance().init(getApplicationContext());
        checkUserInfo();

        //Toast.makeText(this, "Ver " + BudUtil.getInstance().FWVersion, Toast.LENGTH_SHORT).show();
        CustomToast.getInstance(this).showToast(BudUtil.getInstance().FWVersion, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        String sfName="EMS_USER_INFO";   // 사용자 정보 저장
        SharedPreferences sf = mContext.getSharedPreferences(sfName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sf.edit();

        String username=sf.getString(UserName,"");
        if( username== null || username.equals(""))
        {
            // Dialog open
            new DialogUserInfoEdit(mContext);
        }
        */
    }


    @Override
    protected void connectedDevice() {


    }

    @Override
    protected void dataAvailableCheck(String data) {
        Log.e("TAG", data);

        String[] sp=data.split(" ");

        if(sp.length!=20 || sp[0].equals("21")!=true || sp[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }

        String cmd = data.split(" ")[3];
        if (cmd.length() == 1) cmd = "0" + cmd;


        //임시
        for(int i=0 ; i<sp.length;i++){
            Log.d("TAG","==== TEST3 ==== : "+sp[i]);
        }
        Log.d("TAG","==== TEST4 ==== : "+cmd);
        Log.d("TAG","==== TEST5 ==== : "+data);
        int interval;


        if (cmd.equals(CMD_REQ_BATT_INFO)) {
            //battery_bgl.setBackgroundResource(R.drawable.battery_bg);

        } else if (cmd.equals(CMD_REQ_VER)) {// 01
            String fw1=data.split(" ")[5];
            String fw2=data.split(" ")[4];
            String hw1=data.split(" ")[7];
            String hw2=data.split(" ")[6];

            if(fw1.length()==1) fw1 = "0" + fw1;
            if(fw2.length()==1) fw2 = "0" + fw2;
            if(hw1.length()==1) hw1 = "0" + hw1;
            if(hw2.length()==1) hw2 = "0" + hw2;

            Log.e("TAG", "ver updated" + fw1 + "." + fw2 + ","
                    + hw1 + "."+ hw2 );

            BudUtil.getInstance().FWVersion = fw2 + "." + fw1;
            BudUtil.getInstance().HWVersion = hw2 + "." + hw1;
        }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    public boolean checkUserInfo(){

        String userInfo = ManageDeviceConfiguration.getInstance().getUserName();
        if(userInfo.isEmpty()){
            CustomToast.makeText(this, getResources().getText(R.string.noneUserInfo).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

        userInfo += "(";
        userInfo += ManageDeviceConfiguration.getInstance().getUserBirth();
        userInfo += ",";
        userInfo += ManageDeviceConfiguration.getInstance().getUserGender();
        userInfo += ",";
        userInfo += ManageDeviceConfiguration.getInstance().getUserLegPart();
        userInfo += ")";

        CustomToast.makeText(this, userInfo, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void rehab_home(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_Rehab_Home.class,bundle);
    }

    public void go_admin(View view) {
        final Bundle bundle = new Bundle();
        BudUtil.goActivity(mContext, Act_admin.class,bundle);
    }

    public void go_history(View view) {
        final DBQuery dbQuery = new DBQuery(mContext);
        final ArrayList<DAO_Program> progList = dbQuery.getALLProgram();
        for(int i=0;i<progList.size();i++){
            Log.d("tag","test 28 act_home - gohistory : "+progList.get(i));
        }
        if (0 == progList.size()) {

            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.notice))
                    .titleColor(Color.parseColor("#000000"))
                    .backgroundColor(Color.parseColor("#aec7d5"))
                    .content("평가기록에 등록된 내용이 없습니다")
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#000000"))
                    .onPositive(((dialog, which) -> {
                        dialog.dismiss();
                    }))
                    .show();
        } else {
            final Bundle bundle = new Bundle();
            BudUtil.goActivity(mContext, Act_history.class, bundle);
        }
    }

    public void setting(View view) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean("type", type);
        BudUtil.goActivity(mContext, Act_Setting.class, bundle);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) // 이 함수는 젤리빈에서만 호출된다
    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        ((ImageButton) findViewById(R.id.custom_back_btn)).setBackground(null);
        ((ImageButton) findViewById(R.id.custom_back_btn)).setEnabled(false);
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_01);



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
