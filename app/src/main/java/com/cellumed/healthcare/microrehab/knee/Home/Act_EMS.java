package com.cellumed.healthcare.microrehab.knee.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microrehab.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Setting.OnAdapterClick;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

@TargetApi(18)
public class Act_EMS extends BTConnectActivity implements OnAdapterClick, IMP_CMD, SqlImp {
    @Bind(R.id.screenImage)
    ImageView screen;

    @Bind(R.id.signal_type)
    TextView signal_type;
    @Bind(R.id.frequency)
    TextView frequency_txt;
    @Bind(R.id.working_time)
    TextView working_time;
    @Bind(R.id.pulse_op_time)
    TextView pulse_op_time;
    @Bind(R.id.pulse_pause_time)
    TextView pulse_pause_time;
    @Bind(R.id.pulse_rising_time)
    TextView pulse_rising_time;
    @Bind(R.id.pulse_width)
    TextView pulse_width;


    @Bind(R.id.time_minute)
    TextView minute;
    @Bind(R.id.time_second)
    TextView second;
    @Bind(R.id.listview1)
    ListView listView;
    @Bind(R.id.start_button)
    ImageButton start;
    @Bind(R.id.battery_energy)
    ImageView battery_bar;
    @Bind(R.id.battery_left)
    ImageView battery_ico;
    @Bind(R.id.battery_bglayout)
    LinearLayout battery_bgl;

    ImageButton backBtn;
    private Context mContext;
    private int isRunning = 0;  // 0: stopped. 1: running. 2: sent user_param. 3: sent start_req
    private DecimalFormat formatter;
    private String userId = null;   // updated by  mBluetoothConnectService.getUserId();
    private int programTime;
    private int stimulusIntensity;
    private int frequency;
    private int onTime;
    private int offTime;
    private int risingTime;
    private int width;
    private int time;
    private int sumTime;
    private int init_time_min;
    private Custom_List_Adapter customAdapter;
  //  private CountDownTimer timer = null;
    private int listCnt = 0;
    private int exercisePlanRepeatCount = 0;
    private int exercisePlanRepeatCountTotal=0;
   // private int rowCont;
    private int[] levelValues = new int[10];
    private long saveTime = 0;

    private int startTime;
    private String startTimeStr;
    private String rehab_mode_name;
    private String rehab_mode_str;
    private String db_idx;

    private Handler timeHandler = new Handler();
    private Runnable r;

    private String[] SignalTypeStr;
    private int SignalTypeIdx;

    private int rehab_mode_idx=0;
    private boolean not_started = true;

    private boolean isAdminMode = false;    // EMS관리자 모드에서 들어온 경우

    int dpValue;
    float d;
    int margin;
    LinearLayout.LayoutParams params;

    int run_cnt=0;

    long ts_last_ms=0;
    int last_milli=0;

    private void checkBack()
    {
        if(isAdminMode) super.onBackPressed();
        else {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.warning))
                    .titleColor(Color.parseColor("#000000"))
                    .backgroundColor(Color.parseColor("#aec7d5"))
                    .content("운동을 종료하시겠습니까? 운동정보는 모두 삭제됩니다.")
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#000000"))
                    .negativeColor(Color.DKGRAY)
                    .negativeText(getString(R.string.cancel))
                    .onPositive((dialog, which) -> {
                        //   int i= BudUtil.actList.size();
                        //   BudUtil.actList.get(i-1).finish();  // 이전것(사전운동) 종료
                        for (int i = 0; i < BudUtil.actList.size(); i++) {
                            BudUtil.actList.get(i).finish();
                        }
                        if (isAdminMode == false)
                            if (db_idx.length() != 0) new DBQuery(mContext).programRemove(db_idx);
                        finish();   // 현재 종료
                        Intent intent = new Intent(this, Act_Home.class);
                        startActivity(intent);
                        dialog.dismiss();

                    }).onNegative((dialog1, which1) -> dialog1.dismiss()
            ).show();
        }
    }
    @Override
    public void onBackPressed() {
        if(isAdminMode)
        {
            super.onBackPressed();
        }
        else checkBack();

    }

    public void emsDonePopup () {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.rehab_title))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("재활훈련이 완료되었습니다.")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {

                    setWorkoutData();

                    Intent intent = new Intent(this, Act_Rehab_Post.class);
                    final Bundle bundle = new Bundle();
                    bundle.putInt("mode", rehab_mode_idx);
                    bundle.putString("dbidx", db_idx);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    dialog.dismiss();


                }).show();
    }

    public void adminEmsDonePopup () {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.SystemState))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("재활훈련이 완료되었습니다.")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {

                    Intent intent = new Intent(this, Act_Home.class);
                    final Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    startActivity(intent);

                    dialog.dismiss();


                }).show();
    }

    public void checkEmsPad () {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.SystemState))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("EMS 전극을 확인 해주세요")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {

                    Intent intent = new Intent(this, Act_Home.class);
                    final Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    startActivity(intent);

                    dialog.dismiss();


                }).show();
    }


    @Override
    protected void connected_callback()
    {
        if (mBluetoothConnectService != null)
            mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");
            mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ems);
        Log.e("TAG", "START ACT EMS");
        ButterKnife.bind(this);
        BudUtil.actList.add(this);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SignalTypeStr= mContext.getResources().getStringArray(R.array.SignalType);

        // 감춤

        signal_type.setVisibility(View.GONE);
        frequency_txt.setVisibility(View.GONE);
        working_time.setVisibility(View.GONE);
        pulse_op_time.setVisibility(View.GONE);
        pulse_pause_time.setVisibility(View.GONE);
        pulse_rising_time.setVisibility(View.GONE);
        pulse_width.setVisibility(View.GONE);



        d= mContext.getResources().getDisplayMetrics().density;
        // 사전운동에서 넘어온 데이터 세팅
        final Bundle extras = getIntent().getExtras();

        rehab_mode_idx = extras.getInt("mode",9);   // 모드가 없이 오면 관리자 모드

        db_idx=extras.getString("dbidx","");
        if(rehab_mode_idx==0)
        {
            screen.setBackgroundResource(R.drawable.img_gait);
            rehab_mode_name=mContext.getResources().getString(R.string.gait);
            rehab_mode_str="0";
        }
        else if(rehab_mode_idx==1)
        {
            screen.setBackgroundResource(R.drawable.img_squat);
            rehab_mode_name=mContext.getResources().getString(R.string.squat);
            rehab_mode_str="1";
        }
        else if(rehab_mode_idx==2)
        {
            screen.setBackgroundResource(R.drawable.img_stepbox);
            rehab_mode_name=mContext.getResources().getString(R.string.stepbox);
            rehab_mode_str="2";
        }
        if(rehab_mode_idx==9)
        {
            isAdminMode=true;
            screen.setBackgroundResource(R.drawable.img_stepbox);
            rehab_mode_name=mContext.getResources().getString(R.string.admin_ems);
            rehab_mode_str="9";
        }



//****//
        if(isAdminMode) {
            // 현재 ems설정 가져올 값
            String sfName = "EMS_TEMP_SETTING";   // 임시 shared 저장용
            SharedPreferences sf = mContext.getSharedPreferences(sfName, Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sf.edit();

            // check and set default value if null
            SignalTypeIdx = sf.getInt(SignalType, 0);
            signal_type.setText(SignalTypeStr[SignalTypeIdx]);

            String t = sf.getString(ProgramTime, "");
            if (t == null || t == "") {
                t = "20";
                editor.putString(ProgramTime, t);
            }
            working_time.setText(t);

            // 2-150
            t = sf.getString(ProgramFrequency, "");
            if (t == null || t == "") {
                t = "35";
                editor.putString(ProgramFrequency, t);
            }
            frequency_txt.setText(t);

            //1-10
            t = sf.getString(ProgramPulseOperationTime, "");
            if (t == null || t == "") {
                t = "5";
                editor.putString(ProgramPulseOperationTime, t);
            }
            pulse_op_time.setText(t);

            //0-10
            t = sf.getString(ProgramPulsePauseTime, "");
            if (t == null || t == "") {
                t = "5";
                editor.putString(ProgramPulsePauseTime, t);
            }
            pulse_pause_time.setText(t);

            // 0-1
            t = sf.getString(ProgramPulseRiseTime, "");
            if (t == null || t == "") {
                t = "1";
                editor.putString(ProgramPulseRiseTime, t);
            }
            pulse_rising_time.setText(t);

            //50-400. 25us
            t = sf.getString(ProgramPulseWidth, "");
            if (t == null || t == "") {
                t = "350";
                editor.putString(ProgramPulseWidth, t);
            }
            pulse_width.setText(t);

            // sync
            editor.commit();
        }
        else    // default
        {
            SignalTypeIdx = 0;
            signal_type.setText(SignalTypeStr[SignalTypeIdx]);

            // 시간 30분
           String t = "30";
           // String t = "1";
            working_time.setText(t);

            // 2-150
            t = "50";
            frequency_txt.setText(t);

            //1-1
            t = "5";
            pulse_op_time.setText(t);

            //0-10
            t = "5";
            pulse_pause_time.setText(t);

            // 0-1
            t = "1";
            pulse_rising_time.setText(t);

            //50-400. 25us
            t = "350";
            pulse_width.setText(t);

        }

        //----------------
        startService();
        setStart();


        formatter = new DecimalFormat("00");
        String minuteDefault = formatter.format((int) programTime );
        String secondDefault = formatter.format((int) 0);
        minute.setText(minuteDefault);
        second.setText(secondDefault);



        start.setOnClickListener(startClickListener);
        customAdapter = new Custom_List_Adapter(this,new Custom_List_Adapter.OnItemValueChangedListener() {
            @Override
            public void onItemValueChanged() {
                if (isRunning == 1)
                    mBluetoothConnectService.send(CMD_EMS_LEVEL, makeLevelParam());
            }
        });

        setCustomActionbar();
        listView.setAdapter(customAdapter);
        setCustomList();
    }
/*
    private void startCountDownTimer() {
        not_started=false;
        long millisSeconds = time * 1000; // time min
        final Locale locale = getResources().getConfiguration().locale;
        sumTime = Integer.parseInt(String.format("%.0f", (float)(onTime + offTime)));

        // 현재 시간 세팅
        startTimeStr=   BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss");
        setWorkoutData();
        rowCont = sumTime;

        if (timer != null) {
            timer.start();
        } else {

            timer = new CountDownTimer(millisSeconds, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    saveTime = millisUntilFinished / 1000 + 1;

                    runOnUiThread(() -> {
                        rowCont -= 1;
                        if (rowCont == 0) {
                            rowCont = sumTime;
                        }
                    });

                }
                @Override
                public void onFinish() {
                    finish();
                }
            }.start();
        }
    }
*/
    private void setWorkoutData() {

        // 시작시 운동 기록.
        final HashMap<String, String> workoutData = new HashMap<>();

        workoutData.put(ProgramSignalType, signal_type.getText().toString());
        workoutData.put(ProgramTime, working_time.getText().toString());
        workoutData.put(ProgramFrequency , frequency_txt.getText().toString());
        workoutData.put(ProgramPulseOperationTime , pulse_op_time.getText().toString()); //자극시간
        workoutData.put(ProgramPulsePauseTime,pulse_pause_time.getText().toString()) ; //휴지 시간
        workoutData.put(ProgramPulseRiseTime , pulse_rising_time.getText().toString()); //펄스 상승시
        workoutData.put(ProgramPulseWidth,pulse_width.getText().toString()) ;//펄스폭

        if (new DBQuery(mContext).setProgramUpdate(workoutData,db_idx)) {
            Log.d("ACT ems시작 db 업데이트", "저장");
        }
    }



    private void setStart() {
        final Bundle extras = getIntent().getExtras();

         try {
                programTime = Integer.parseInt(working_time.getText().toString());
                time = programTime*60;  // in sec
                init_time_min = programTime;    // const
        } catch (Exception e) {
                programTime = 0;
        }
        try {
            frequency = Integer.parseInt(frequency_txt.getText().toString());
        } catch (NumberFormatException e) {
            finish();
        }
        try {
            //onTime = (int) (Float.parseFloat(pulse_op_time.getText().toString()));
            onTime = Integer.parseInt(pulse_op_time.getText().toString());
        } catch (NumberFormatException e) {
            onTime = 0;
        }
        try {
            //offTime = (int) (Float.parseFloat(pulse_pause_time.getText().toString()));
            offTime = Integer.parseInt(pulse_pause_time.getText().toString());
        } catch (NumberFormatException e) {
            offTime = 0;
        }
        try {
            //risingTime = (int) (Float.parseFloat(pulse_rising_time.getText().toString()) * 10);
            risingTime = Integer.parseInt(pulse_rising_time.getText().toString());
        } catch (NumberFormatException e) {
            risingTime = 0;
        }
        try {
            width = Integer.parseInt(pulse_width.getText().toString());
        } catch (NumberFormatException e) {
            width = 0;
        }
        width = width / 50;



    }

    Button.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int interval;

            if (isRunning == 0) {
                isRunning = 2;
                start.setBackgroundResource(R.drawable.btn_stop);
                backBtn.setClickable(false);
                backBtn.setSelected(true);
                backBtn.setBackgroundResource(R.drawable.bt_back_sel);
                minute.setEnabled(false);
                second.setEnabled(false);

                // 분단위 수정.
                if((int)(time%60) != 0)
                {
                    Log.e("TAG","TIME CUT");
                    time -= (time%60);
                    time++;

                }

                r = new Runnable() {
                    @Override
                    public void run() {
                        //BTConnectActivity send battery info
                        //if(time%60==0) mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");    // battery
                       // Log.e("time","0.1sec");
                        timeHandler.postDelayed(this, 100);

                        if(ts_last_ms==0)
                        {
                            ts_last_ms = (System.currentTimeMillis());
                            last_milli= ((int)ts_last_ms % 10000000);
                        }

                        run_cnt++;

                        if(run_cnt%10==2)  screen.setBackgroundResource(R.drawable.ring_02);
                        else if(run_cnt%10==4) screen.setBackgroundResource(R.drawable.ring_03);
                        else if(run_cnt%10==6) screen.setBackgroundResource(R.drawable.ring_04);
                        else if(run_cnt%10==8) screen.setBackgroundResource(R.drawable.ring_05);
                        else if(run_cnt%10==0) screen.setBackgroundResource(R.drawable.ring_01);

                        // 보정



                        int milli = ((int)System.currentTimeMillis() % 10000000);
                        //if( System.currentTimeMillis() - ts_last_ms > 999) run_cnt++;

                    // every 1sec
                        if(milli - last_milli > 900) {
                            Log.e("diff",Integer.toString(last_milli) + " , " + Integer.toString(milli));
                            run_cnt=0;
                            last_milli+=1000;

                            time--;
                            //ts_last_ms = (System.currentTimeMillis());

                            Log.e("time","1sec");
                            //진행바
                            //right margin 373-3
                            dpValue = (int) (3.0 + (33.0 * (double) time) / ((double) init_time_min * 6.0));
                            margin = (int) (dpValue * d);
                            params = (LinearLayout.LayoutParams) battery_bar.getLayoutParams();
                            params.rightMargin = margin;    // in px
                            battery_bar.setLayoutParams(params);
                            // Log.e("TAG", "t=" + Integer.toString( time) + " it="+Integer.toString(init_time_min*60) + "  dp=" + Integer.toString(dpValue) + ", margin = " + Integer.toString(margin));

                            minute.setText(formatter.format((int) time / 60));
                            second.setText(formatter.format((int) time % 60));

                            if (time <= 0) {
                                Log.e("TAG", "time up");
                                whenRequestStop();
                                if (isAdminMode) adminEmsDonePopup();
                                else emsDonePopup();
                                //finish();   // finish activity and go back to list
                            }
                        }
                    }
                };

                startTime = time;
                // check interval
                // set starttime when resumed

                /*
                // 분단위 조정으로 삭제
                if(onTime + offTime > 0) {
                    interval = (onTime + offTime);
                    Log.e("TAG", "INT=" + interval);
                    if (time % interval > 0) {
                        time -= time % interval;
                        minute.setText(formatter.format((int) time / 60));
                        second.setText(formatter.format((int) time % 60));
                    }
                }
            */

                //send ems info first
                mBluetoothConnectService.send(CMD_EMS_INFO, makeEMSInfoData());


                Log.e("TAG", "time=" + Integer.toString(time) );

            } else if(isRunning==4) // 일시정지
            {
                // 현재 시간 세팅
              //  startTimeStr=   BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss");
                checkEmsPad();

            }
            else
            {
                isRunning = 0;
                whenRequestStop();
            }
        }
    };

    private void whenRequestStop() {
        isRunning = 0;
        ts_last_ms=0;
        //if(timer!=null) timer.cancel();
        start.setBackgroundResource(R.drawable.btn_continue );
        backBtn.setClickable(true);
        backBtn.setSelected(false);
        backBtn.setBackgroundResource(R.drawable.custom_back_btn);
        Log.i("EMS","stop request");
        mBluetoothConnectService.send(CMD_REQ_STOP_EMS, "");
        timeHandler.removeCallbacks(r);

        // reset ems levels to 0 (04/10)
        for (int i = 0; i < customAdapter.getCount(); i++) {
            Custom_List_View_Item item = (Custom_List_View_Item) customAdapter.getItem(i);
            item.setLevelValueString(String.format("%02d", 0));
            customAdapter.notifyDataSetChanged();

        }

        if(rehab_mode_idx==0)
        {
            screen.setBackgroundResource(R.drawable.img_gait);
        }
        else if(rehab_mode_idx==1)
        {
            screen.setBackgroundResource(R.drawable.img_squat);
        }
        else if(rehab_mode_idx==2)
        {
            screen.setBackgroundResource(R.drawable.img_stepbox);
        }
        if(rehab_mode_idx==9) {
            screen.setBackgroundResource(R.drawable.img_stepbox);
        }
/*
        if(!not_started) {
            new DBQuery(mContext).endProgramData( startTimeStr, programTime - time);
        }
*/
    }


    private void setCustomList() {
        customAdapter.addItem(mContext.getResources().getString(R.string.Fem_Up), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Fem_Down), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Ham_In), formatter.format(stimulusIntensity));
        customAdapter.addItem(mContext.getResources().getString(R.string.Ham_Out), formatter.format(stimulusIntensity));
    }

    @Override
    public void onResume() {

        super.onResume();
        if (mBluetoothConnectService != null)
            mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        whenRequestStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(not_started ==false) {
          //  recycleBitmap(screen);
        }
        try{
           // timer.cancel();
        } catch (Exception e) {}
        //timer = null;
    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }

    private String makeLevelParam() {

        StringBuilder toSend = new StringBuilder();
        toSend.append(makeLevelData());
        return toSend.toString();
    }

    private String makeEMSInfoData() {
        int pt=0;
        if(SignalTypeIdx<6) pt=1;
        StringBuilder toSend = new StringBuilder();
        toSend.append(String.format("%02X", SignalTypeIdx + 1));    // prog num
        toSend.append(String.format("%02X", pt));    // 0: normal, 1:multi
        toSend.append(String.format("%02X", frequency));
        //toSend.append(String.format("%02X", init_time_min));
        toSend.append(String.format("%02X", (int)time/60));
        toSend.append(String.format("%02X", onTime));
        toSend.append(String.format("%02X", offTime));
        toSend.append(String.format("%02X", risingTime));
        toSend.append(String.format("%02X", width));


        //        toSend.append(String.format("%02X", frequency));
        //toSend.append(makeLevelData());

        Log.e("TAG", toSend.toString());
        return toSend.toString();
    }



    private String makeLevelData() {
        StringBuilder toSend = new StringBuilder();
        for (int i = 0; i < customAdapter.getCount(); i++) {
            Custom_List_View_Item item = (Custom_List_View_Item) customAdapter.getItem(i);
            int level = Integer.parseInt(item.getLevelValueString());
            levelValues[i] = level;
            toSend.append(String.format("%02X", level));
        }
      //  setLevelValue();
        // 10 bytes dummy
        return toSend.toString();
    }



    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {
        Log.e("TAG", data);

        String[] sp=data.split(" ");

        if(sp.length!=20 || sp[0].equals("21")!=true || sp[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }

        String cmd=data.split(" ")[3];
        if(cmd.length()==1) cmd = "0" + cmd;

        int interval;

        if(cmd.equals(CMD_REQ_BATT_INFO))
        {
            String bt="";
            String k;
            for(int i=4;i<8;i++)
            {
                k=data.split(" ")[i];
                if(k.length()==1) k = "0"+k;
                bt+= k;
            }
            Long bt_left= Long.parseLong(bt,16);
            Float f = Float.intBitsToFloat(bt_left.intValue());

            Log.e("TAG","HEX="+bt+" BATTERY LEFT = " + f);
            if(f >= 75) battery_ico.setImageResource(R.drawable.ico_bat01);
            else if(f >= 50) battery_ico.setImageResource(R.drawable.ico_bat02);
            else if(f >= 25) battery_ico.setImageResource(R.drawable.ico_bat03);
            else  battery_ico.setImageResource(R.drawable.ico_bat04);

            //battery_bgl.setBackgroundResource(R.drawable.battery_bg);

        }
        else if (cmd.equals(CMD_REQ_START_EMS))
        {
            if(isRunning == 3) // ACK for req START EMS
            {


/*
                Log.d("TAG","ACK recved for START_REQ");
                if (timer != null) {
                    timer.start();
                } else {
                    Log.d("TAG","ACK recved for START_REQ. Timer not null");
                    startCountDownTimer();
                }
*/
                isRunning=1;
                run_cnt=0;
                timeHandler.postDelayed(r, 100);

                Log.e("TAG", "ACK!  time=" + Integer.toString(time) );
            }
            else
            {
                Log.d("TAG","ACK recved. Running State = " + isRunning);
            }
        }
        else if (cmd.equals(CMD_EMS_INFO))   // EMS info받으면 req_ems_start보냄
        {
            // 현재 시간 세팅
           // startTimeStr=   BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss");
          //  setWorkoutData();

            mBluetoothConnectService.send(CMD_REQ_START_EMS, "");
            Log.d("TAG","Sent START_REQ");
            isRunning = 3;
        }
        else if (cmd.equals(CMD_EMS_STATUS))
        {
            //Todo: 디바이스와 테스트 필요
            /*
            String com="";
            String k;

            for(int i=4;i<14;i++)
            {
                k=data.split(" ")[i];
                if(k.length()==1) k = "0"+k;
                com+= k;
            }

            char status = com.charAt(9);
            if(status == '1'){

                Log.d("TAG","Pad Drop status !!!");
                isRunning = 4;
                start.callOnClick();
            }
            */
        }
    }

    @Override
    public void onAdapterClick(int pos) {}

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_04);



        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        backBtn = (ImageButton) findViewById(R.id.custom_back_btn);
        backBtn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        checkBack();
                    }
                }
        );

    }

}
