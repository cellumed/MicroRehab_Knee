package com.cellumed.healthcare.microfit.knee.Home;

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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microfit.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microfit.knee.R;
import com.cellumed.healthcare.microfit.knee.Util.BudUtil;
import com.cellumed.healthcare.microfit.knee.Util.CustomToast;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

@TargetApi(18)
public class Act_Rehab_Pre extends BTConnectActivity implements IMP_CMD, SqlImp {

    @Bind(R.id.count_now)
    TextView cnt_now;
    @Bind(R.id.count_total)
    TextView cnt_total;
    @Bind(R.id.battery_energy)
    ImageView battery_bar;
    @Bind(R.id.battery_left)
    ImageView battery_ico;
    @Bind(R.id.battery_bglayout)
    LinearLayout battery_bgl;
    @Bind(R.id.start_button)
    ImageButton start;
    @Bind(R.id.context_title)
    ImageView ctx_title;
    @Bind(R.id.bg_context)
    ImageView bg_ctx;

    public static final int REHAB_WORKOUT_STOP = 0;
    public static final int REHAB_WORKOUT_WORKING = 1;
    public static final int REHAB_WORKOUT_START_SENS = 2;

    boolean checkOk=false;

    ImageButton backBtn;
    private Context mContext;
    private int isRunning = REHAB_WORKOUT_STOP;  // 0: stopped. 1: running. 2: sent start_sens.

    private int runningPos=0;

    private String userId = null;   // updated by  mBluetoothConnectService.getUserId();

    private int last_move_time; // 마지막 움직인 시간. 1초 변경 없을때 확인용
    private int pre_mode=0; // 1= gait, 2=squat, 3= stairs
    private String rehab_send_txt="00"; // 패킷 전달 타입
    private String rehab_mode_name;
    private String rehab_mode_str;  // db에 저장형식
    private String startTimeStr;

    //원래
    //private String db_idx="";
    //임시
    public static String db_idx="";

    private int report_cnt=0;   // report를 요청하고 수신한 패킷수
    private int work_cnt=0;
    private int total_cnt_int;

    private int programTime;

    private boolean doneFlag=false;

    private int startTime;


    private int tickCnt = 0;

    private Handler timeHandler = new Handler();
    private Runnable r;

    Handler checkOkHandler = new Handler();
    // 확인 눌렀을때 넘어가게

    private boolean not_started = true;

    // report
    int[] s_emg_amp_avr=new int[5];
    int[] s_emg_amp_max=new int[5];
    int[] s_emg_totalWork=new int[5];
    int ang_min=0;
    int ang_max=0;
    int time=0;

    int legtype_idx=0;  // 0:left, 1:right
    int flex_cnt=0;


    int currentPostureId = -1;
    int leftOrRight = 0;            // left: -1, none: 0, right: 1
    String currentPosture = "";

    //재활프로그램에서 오면 1, 개별프로그램에서 오면 0
    public static int admin_mode;

    @Override
    protected void connected_callback()
    {
        if (mBluetoothConnectService != null)
            mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rehab_pre);

        Log.e("TAG", "START ACT REHAB_PRE");
        ButterKnife.bind(this);
        BudUtil.actList.add(this);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Bundle extras = getIntent().getExtras();


        //----------------
        startService();

        // set for each extras
        setStart();

        /*
        // leg
        String sfName="EMS_USER_INFO";   // 사용자 정보 저장
        SharedPreferences sf = mContext.getSharedPreferences(sfName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sf.edit();
        legtype_idx=sf.getInt(UserLegType,0);
        */
        // 사용자 다리 정보가 없으면 오른쪽 다리 기본
        if( ManageDeviceConfiguration.getInstance().getUserLegPart().equals(LEFT_LEG) ){
            // Left Leg (LL)
            legtype_idx = 0;
        } else {
            // Rigth Leg (RL)
            legtype_idx = 1;
        }

        isRunning = REHAB_WORKOUT_STOP;

        start.setOnClickListener(startClickListener);

        setCustomActionbar();


        Log.e("TAG","onCreate PRE");

        if(pre_mode==1) posturePopup(); //걷기일때만 적용


    }
    //뒤로가기와 백버튼 눌렀을 때
    private void checkBack()
    {
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
                        BudUtil.actList.get(i).finish(); //이전 화면 지우기
                    }

                    // 시작해서 db기록이 된 경우 삭제
                    //if(db_idx.length()!=0) new DBQuery(mContext).programRemove(db_idx);
                    finish();   // 현재 종료
                    Intent intent = new Intent(this, Act_Home.class);
                    startActivity(intent);
                    dialog.dismiss();

                })  .onNegative((dialog1, which1) -> dialog1.dismiss()
        ).show();
    }
    @Override
    public void onBackPressed() {

        checkBack();

    }
    private void setWorkoutData() {
        // 시작시 운동 기록.

        final HashMap<String, String> workoutData = new HashMap<>();

        if(ManageDeviceConfiguration.getInstance().getUserId().isEmpty()) {
            CustomToast.makeText(this, getResources().getText(R.string.noneUserInfo).toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        startTimeStr=   BudUtil.getInstance().getToday("yyyy.MM.dd HH:mm:ss");

        workoutData.put(ProgramStartDate, startTimeStr);
        workoutData.put(ProgramType, rehab_mode_str);
        workoutData.put(ProgramName, rehab_mode_name);
        workoutData.put(UserInfoIdFk, ManageDeviceConfiguration.getInstance().getUserId());

        if (new DBQuery(mContext).newProgramInsert(workoutData)) {
            Log.d("ACT ems시작 db저장", "저장");
        }

        db_idx = new DBQuery(mContext).getIdxFromStartDate(startTimeStr);
    }

    private void setPreData() { //사전 운동 끈나고 기록
        // 시작시 운동 기록.
        // HashMap<key, value>

        final HashMap<String, String> workoutData = new HashMap<>();

        if(ManageDeviceConfiguration.getInstance().getUserId().isEmpty()) {
            CustomToast.makeText(this, getResources().getText(R.string.noneUserInfo).toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if(db_idx.length()==0)
        {
            Log.e("ACT ems db저장 정도가 없음", "저장");
            return;
        }

        workoutData.put(PreTime, String.valueOf(time)); // time를 string으로 변환
        ang_min = Act_admin_imu.EXTENTION_ANGLE - ang_min;
        ang_max = Act_admin_imu.EXTENTION_ANGLE - ang_max;
        workoutData.put(PreAngleMin, String.valueOf(ang_min));
        workoutData.put(PreAngleMax, String.valueOf(ang_max));
        workoutData.put(PreEmgAvr, String.valueOf(s_emg_amp_avr[0]));
        workoutData.put(PreEmgMax, String.valueOf(s_emg_amp_max[0]));
        workoutData.put(PreEmgTotal, String.valueOf(s_emg_totalWork[0]));

        workoutData.put(PreEmgAvr2, String.valueOf(s_emg_amp_avr[1]));
        workoutData.put(PreEmgMax2, String.valueOf(s_emg_amp_max[1]));
        workoutData.put(PreEmgTotal2, String.valueOf(s_emg_totalWork[1]));
        workoutData.put(PreEmgAvr3, String.valueOf(s_emg_amp_avr[2]));
        workoutData.put(PreEmgMax3, String.valueOf(s_emg_amp_max[2]));
        workoutData.put(PreEmgTotal3, String.valueOf(s_emg_totalWork[2]));
        workoutData.put(PreEmgAvr4, String.valueOf(s_emg_amp_avr[3]));
        workoutData.put(PreEmgMax4, String.valueOf(s_emg_amp_max[3]));
        workoutData.put(PreEmgTotal4, String.valueOf(s_emg_totalWork[3]));
        workoutData.put(PreEmgAvr5, String.valueOf(s_emg_amp_avr[4]));
        workoutData.put(PreEmgMax5, String.valueOf(s_emg_amp_max[4]));
        workoutData.put(PreEmgTotal5, String.valueOf(s_emg_totalWork[4]));

        // 개별프로그램이면
        if(admin_mode==0){
            //ems data
            workoutData.put(ProgramSignalType,null);
            workoutData.put(ProgramTime, null);
            workoutData.put(ProgramFrequency ,null);
            workoutData.put(ProgramPulseOperationTime , null); //자극시간
            workoutData.put(ProgramPulsePauseTime,null) ; //휴지 시간
            workoutData.put(ProgramPulseRiseTime ,null); //펄스 상승시
            workoutData.put(ProgramPulseWidth,null) ;//펄스폭

            //post data
            workoutData.put(PostTime,null);
            workoutData.put(PostAngleMin,null);
            workoutData.put(PostAngleMax,null);
            workoutData.put(PostEmgAvr, null);
            workoutData.put(PostEmgMax,null);
            workoutData.put(PostEmgTotal, null);
            workoutData.put(PostEmgAvr2, null);
            workoutData.put(PostEmgMax2, null);
            workoutData.put(PostEmgTotal2, null);
            workoutData.put(PostEmgAvr3, null);
            workoutData.put(PostEmgMax3, null);
            workoutData.put(PostEmgTotal3, null);
            workoutData.put(PostEmgAvr4, null);
            workoutData.put(PostEmgMax4, null);
            workoutData.put(PostEmgTotal4, null);
            workoutData.put(PostEmgAvr5, null);
            workoutData.put(PostEmgMax5, null);
            workoutData.put(PostEmgTotal5, null);

            if (new DBQuery(mContext).endProgramData(db_idx)) {
                Log.d("EMSPOST", "시간 저장");
            }
        }


        if (new DBQuery(mContext).setProgramUpdate(workoutData,db_idx)) {
            Log.d("ACT ems시작 db저장", "저장");
        }


    }

    public void saveRehabPreData(){
        final HashMap<String, String> workoutData = new HashMap<>();

        // user id 가 없으면 저장 하지 않는다.
        if(ManageDeviceConfiguration.getInstance().getUserId().isEmpty()) {
            CustomToast.makeText(this, getResources().getText(R.string.noneUserInfo).toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        startTimeStr=   BudUtil.getInstance().getToday("yyyy.MM.dd HH:mm:ss");
        workoutData.put(ProgramStartDate, startTimeStr);
        workoutData.put(ProgramType, rehab_mode_str);
        workoutData.put(ProgramName, rehab_mode_name);
        workoutData.put(PreTime, String.valueOf(time)); // time를 string으로 변환
        ang_min = Act_admin_imu.EXTENTION_ANGLE - ang_min;
        ang_max = Act_admin_imu.EXTENTION_ANGLE - ang_max;
        workoutData.put(PreAngleMin, String.valueOf(ang_min));
        workoutData.put(PreAngleMax, String.valueOf(ang_max));
        workoutData.put(PreEmgAvr, String.valueOf(s_emg_amp_avr[0]));
        workoutData.put(PreEmgMax, String.valueOf(s_emg_amp_max[0]));
        workoutData.put(PreEmgTotal, String.valueOf(s_emg_totalWork[0]));
        workoutData.put(PreEmgAvr2, String.valueOf(s_emg_amp_avr[1]));
        workoutData.put(PreEmgMax2, String.valueOf(s_emg_amp_max[1]));
        workoutData.put(PreEmgTotal2, String.valueOf(s_emg_totalWork[1]));
        workoutData.put(PreEmgAvr3, String.valueOf(s_emg_amp_avr[2]));
        workoutData.put(PreEmgMax3, String.valueOf(s_emg_amp_max[2]));
        workoutData.put(PreEmgTotal3, String.valueOf(s_emg_totalWork[2]));
        workoutData.put(PreEmgAvr4, String.valueOf(s_emg_amp_avr[3]));
        workoutData.put(PreEmgMax4, String.valueOf(s_emg_amp_max[3]));
        workoutData.put(PreEmgTotal4, String.valueOf(s_emg_totalWork[3]));
        workoutData.put(PreEmgAvr5, String.valueOf(s_emg_amp_avr[4]));
        workoutData.put(PreEmgMax5, String.valueOf(s_emg_amp_max[4]));
        workoutData.put(PreEmgTotal5, String.valueOf(s_emg_totalWork[4]));
        workoutData.put(UserInfoIdFk, ManageDeviceConfiguration.getInstance().getUserId());

        // 개별프로그램이면
        if(admin_mode==0){
            //ems data
            workoutData.put(ProgramSignalType,null);
            workoutData.put(ProgramTime, null);
            workoutData.put(ProgramFrequency ,null);
            workoutData.put(ProgramPulseOperationTime , null); //자극시간
            workoutData.put(ProgramPulsePauseTime,null) ; //휴지 시간
            workoutData.put(ProgramPulseRiseTime ,null); //펄스 상승시
            workoutData.put(ProgramPulseWidth,null) ;//펄스폭

            //post data
            workoutData.put(PostTime,null);
            workoutData.put(PostAngleMin,null);
            workoutData.put(PostAngleMax,null);
            workoutData.put(PostEmgAvr, null);
            workoutData.put(PostEmgMax,null);
            workoutData.put(PostEmgTotal, null);
            workoutData.put(PostEmgAvr2, null);
            workoutData.put(PostEmgMax2, null);
            workoutData.put(PostEmgTotal2, null);
            workoutData.put(PostEmgAvr3, null);
            workoutData.put(PostEmgMax3, null);
            workoutData.put(PostEmgTotal3, null);
            workoutData.put(PostEmgAvr4, null);
            workoutData.put(PostEmgMax4, null);
            workoutData.put(PostEmgTotal4, null);
            workoutData.put(PostEmgAvr5, null);
            workoutData.put(PostEmgMax5, null);
            workoutData.put(PostEmgTotal5, null);

            //if (new DBQuery(mContext).endProgramData(db_idx)) {
            //    Log.d("EMSPOST", "시간 저장");
            //}
        }


        if (new DBQuery(mContext).newProgramInsert(workoutData)) {
            Log.d("ACT ems시작 db저장", "저장");
        }

        db_idx = new DBQuery(mContext).getIdxFromStartDate(startTimeStr);
        Log.d("Act_Rehab_Pre", "DB index >>>>>>>>>>>>> " + db_idx);

        if (new DBQuery(mContext).endProgramData(db_idx)) {
            Log.d("EMSPOST", "시간 저장");
        }

    }

    public void checkDonePopup () {
        final Bundle extras = getIntent().getExtras();

        String pre_mode_str1=extras.getString("title");
        String title = "";
        String content = "";
        if(admin_mode==0){
            if(pre_mode_str1.equals("gait")){
                title = "걷기";
                content = "걷기 운동이 완료되었습니다.";
            }else if(pre_mode_str1.equals("squat")){
                title = "스쿼트";
                content = "스쿼트 운동이 완료되었습니다.";
            }else{
                title = "스탭박스";
                content = "스탭박스 운동이 완료되었습니다.";
            }
        }else{
            title = getString(R.string.PreRehab);
            content = "사전평가가 완료되었습니다.";
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(title)
               .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content(content)
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {
                    checkOk = true;
                    dialog.dismiss();
                    if(admin_mode==0){
                        this.finish();
                    }
                }).show();
    }

    public void checkComplete() {
        // if complete go to EMS
        //if(sensing_cnt > 50) {
        checkOk = false;
        checkDonePopup();

        //send req_report
        mBluetoothConnectService.send(CMD_REQ_REPORT_SENS, "");

        report_cnt=0;

        //TODO. 지울것. 패킷 수신부에서 확인
        //reportDone();
        //}
    }

    // 리포트를 모두 수신한 경우 다음으로 넘어감
    public void reportDone()
    {
        // predata저장
        //setPreData();
        saveRehabPreData();

        checkOkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkOkHandler.postDelayed(this, 300);
                if (checkOk) {

                    checkOkHandler.removeCallbacksAndMessages(null);
                    if(admin_mode==1){
                        final Bundle bundle = new Bundle();
                        bundle.putInt("mode", pre_mode);
                        bundle.putString("dbidx", db_idx);
                        BudUtil.goActivity(mContext, Act_EMS.class, bundle);
                    }


                }
            }
        }, 500);


    }

    private void setStart() {
        final Bundle extras = getIntent().getExtras();


        admin_mode = extras.getInt("admin_mode",1);

        String pre_mode_str=extras.getString("title");

        if (pre_mode_str.equals("gait")) {
            pre_mode=1;
            rehab_send_txt="01";
            rehab_mode_name=mContext.getResources().getString(R.string.gait);
            rehab_mode_str="0";

            ctx_title.setImageResource(R.drawable.context_walk);
            bg_ctx.setImageResource(R.drawable.conico_walk);

            cnt_total.setText("03");
            total_cnt_int=3;

        } else if (pre_mode_str.equals("squat")) {
            pre_mode=2;
            rehab_send_txt="02";
            rehab_mode_name=mContext.getResources().getString(R.string.squat);
            rehab_mode_str="1";

            ctx_title.setImageResource(R.drawable.context_squat);
            bg_ctx.setImageResource(R.drawable.conico_squat);

            cnt_total.setText("10");
            total_cnt_int=10;

        } else  // stairs
        {
            pre_mode=3;
            rehab_send_txt="03";
            rehab_mode_name=mContext.getResources().getString(R.string.stepbox);
            rehab_mode_str="2";
            ctx_title.setImageResource(R.drawable.context_stepbox);
            bg_ctx.setImageResource(R.drawable.conico_stepbox);

            cnt_total.setText("10");
            total_cnt_int=10;
        }
    }




    Button.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int interval;

            if (isRunning == REHAB_WORKOUT_STOP) {
                isRunning = REHAB_WORKOUT_START_SENS;
                start.setBackgroundResource(R.drawable.btn_stop);
                backBtn.setClickable(false);
                backBtn.setSelected(true);
                backBtn.setBackgroundResource(R.drawable.bt_back_sel);
                tickCnt=0;

                flex_cnt=0;

                r = new Runnable() {
                    @Override
                    public void run() {

                        timeHandler.postDelayed(this, 100);
                        tickCnt++;
                        //  Log.d("TAG", "pretimer 100. isRunning" + isRunning);
                        // request battery every 1 sec
                        /*
                        if(tickCnt%600 == 0)
                        {
                            mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");    // battery
                         }
                        */

                        if (isRunning == REHAB_WORKOUT_START_SENS)    // request sent but no response recieved in a sec.
                        {
                            /*
                            if(tickCnt>100) {
                                whenRequestStop();
                                finish();   // finish activity and go back to list
                                //TODO err
                            }
                            */
                        } else if (isRunning == REHAB_WORKOUT_WORKING)   // running
                        {
                            // change img if needed

                            if (runningPos == 0x1) {
                                if (tickCnt % 10 == 0) bg_ctx.setImageResource(R.drawable.gait_01);
                                else if (tickCnt % 5 == 0)
                                    bg_ctx.setImageResource(R.drawable.gait_02);
                            } else if (runningPos == 0x4) //in
                            {
                                if (tickCnt % 10 == 0) bg_ctx.setImageResource(R.drawable.in_squat_01);
                                else if(tickCnt%5==0) bg_ctx.setImageResource(R.drawable.in_squat_02);
                            } else if (runningPos == 0x8) //out
                            {
                                if (tickCnt % 10 == 0) bg_ctx.setImageResource(R.drawable.out_squat_01);
                                else if(tickCnt%5==0) bg_ctx.setImageResource(R.drawable.out_squat_02);
                            } else if (runningPos == 0x10) //step
                            {
                                //  if (tickCnt % 20 == 0) bg_ctx.setImageResource(R.drawable.updown_01);
                                //  else if (tickCnt % 10 == 0) bg_ctx.setImageResource(R.drawable.updown_02);
                                if (tickCnt % 10 == 0) {
                                    bg_ctx.setImageResource(currentPostureId);
                                } else if(tickCnt%5==0) {
                                    String resName = currentPosture.replaceAll("res/drawable/", "");
                                    resName = resName.replaceAll(".png", "_eff");
                                    Log.i("STEP", "resName:" + resName + ", " + leftOrRight );
                                    bg_ctx.setImageResource(getResources().getIdentifier(resName, "drawable", getPackageName()));
                                }
                            } else if(runningPos == 0x20) { // stand up
                                if (tickCnt % 10 == 0) {
                                    bg_ctx.setImageResource(currentPostureId);
                                } else if(tickCnt%5==0) {
                                    String resName = currentPosture.replaceAll("res/drawable/", "");

                                    if(legtype_idx == 0) {
                                        if (leftOrRight == -1) {
                                            resName = resName.replaceAll(".png", "_right");
                                        } else if (leftOrRight == 1) {
                                            resName = resName.replaceAll(".png", "_left");
                                        } else {
                                            resName.substring(0, resName.indexOf("."));
                                            Log.i("STAND", "substring" + resName);
                                        }
                                    } else {
                                        if (leftOrRight == 1) {
                                            resName = resName.replaceAll(".png", "_right");
                                        } else if (leftOrRight == -1) {
                                            resName = resName.replaceAll(".png", "_left");
                                        } else {
                                            resName.substring(0, resName.indexOf("."));
                                            Log.i("STAND", "substring" + resName);
                                        }
                                    }


                                    Log.i("STAND", "resName:" + resName + ", " + leftOrRight );
                                    bg_ctx.setImageResource(getResources().getIdentifier(resName, "drawable", getPackageName()));
                                }
                            }
                        }
                    }
                };

                // 맨 처음 버튼이 눌리면 db에 기록하고 db_idx세팅
                //setWorkoutData();

                //send ems info first
                mBluetoothConnectService.send(CMD_START_SENS, "0001" + rehab_send_txt+"03");    // resp=ack?, type=normal, rehab_type=(1,2,3), sens_type= xx
                work_cnt=0;

                Log.e("TAG", "send start send nor");
                timeHandler.postDelayed(r, 100);
                Log.e("TAG", "timer started");


            } else if(isRunning == REHAB_WORKOUT_WORKING || isRunning == REHAB_WORKOUT_START_SENS){
                //stop
                Log.e("TAG", "STop button");
                isRunning = REHAB_WORKOUT_WORKING;
                //Todo: 프로톰콜에 일시정지를 추하해서 처리 해야 데이터 처리에 용이함
                // 프로토콜 추가후 스톱 요청이 아닌 일시정시 요청으로 변경 필요
                whenRequestStop();
                //checkBack();
            }
        }
    };

    private void whenRequestStop() {
        isRunning = REHAB_WORKOUT_STOP;
        //if(timer!=null) timer.cancel();
        start.setBackgroundResource(R.drawable.btn_start);
        backBtn.setClickable(true);
        backBtn.setSelected(false);
        backBtn.setBackgroundResource(R.drawable.custom_back_btn);
        mBluetoothConnectService.send(CMD_STOP_SENS, "");
        timeHandler.removeCallbacks(r);

    }


    public void posturePopup () {
        final Bundle extras = getIntent().getExtras();

        String pre_mode_str1=extras.getString("title");
        String title = "";
        if(admin_mode==0){
             if(pre_mode_str1.equals("gait")){
                 title = "걷기";
             }else if(pre_mode_str1.equals("squat")){
                 title = "스쿼트";
             }else{
                  title= "스텝박스";
             }
        }else{
            title = getResources().getString(R.string.PreRehab);
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(title)
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("자리에 앉은 상태에서 시작을 눌러 주시기 바랍니다")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBluetoothConnectService != null)
            mBluetoothConnectService.send(CMD_REQ_BATT_INFO, "");

        Log.e("TAG","onResume PRE");


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
            //  timer.cancel();
        } catch (Exception e) {}
        //   timer = null;
    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
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


        Log.e("TAG", data);
        int channel_no;
        int emg_amp_avr;
        int emg_amp_max;
        int emg_totalWork;


        String cmd=data.split(" ")[3];
        if(cmd.length()==1) cmd = "0" + cmd;

        int interval;

        int dpValue;
        float d = mContext.getResources().getDisplayMetrics().density;
        int margin;
        LinearLayout.LayoutParams params;


        //임시 강제시작
        //   checkComplete();    // TODO. 지울것

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

        }
        else if (cmd.equals(CMD_RESP_NOR_SENS))
        {
            //
            if(isRunning == REHAB_WORKOUT_WORKING)
            {
                Log.d("TAG","SENS info recved");
                if(!doneFlag) {

                    String count = data.split(" ")[4];

                    if(count.equals("a")) {
                        work_cnt = 10;
                    } else {
                        work_cnt = Integer.valueOf(count);
                    }

                    String n = Integer.toString(work_cnt, 10);
                    if (n.length() == 1) n = "0" + n;

                    //진행바
                    //right margin 373-3
                    dpValue = (int) (3.0 + 370.0 * (1.0 - ((double) work_cnt / (double) total_cnt_int)));
                    margin = (int) (dpValue * d);
                    params = (LinearLayout.LayoutParams) battery_bar.getLayoutParams();
                    params.rightMargin = margin;    // in px
                    battery_bar.setLayoutParams(params);


                    cnt_now.setText(n);
                    Log.e("TAG", n + " " + cnt_total.getText());
                    if (n.equals(cnt_total.getText().toString())) {
                        doneFlag = true;
                        whenRequestStop();
                        checkComplete();
                    }
                }

            }

        }
        else if(cmd.equals(CMD_START_SENS))
        {
            String ack=data.split(" ")[4];
            if(isRunning == REHAB_WORKOUT_START_SENS && ack.equals("6"))
            {
                Log.d("TAG", "StartSens Ack recved");
                isRunning = REHAB_WORKOUT_WORKING;    // recv first ack
            }
            //
            if(isRunning == REHAB_WORKOUT_WORKING) {

            }
        }
        else if (cmd.equals(CMD_REQ_REPORT_SENS))
        {
            if(report_cnt==0) {

                Log.d("TAG", "SENS report comm recv");
                String k;

                k = data.split(" ")[4];
                ang_min = Integer.parseInt(k, 16);
                k = data.split(" ")[5];
                ang_max = Integer.parseInt(k, 16);
                k = data.split(" ")[6] + data.split(" ")[7];
                time = Integer.parseInt(k,16);

                Log.d("TAG", "angle min=" + Integer.toString(ang_min) + " max=" + Integer.toString(ang_max) + " time=" + Integer.toString(time));
            }
            else {
                Log.d("TAG","SENS report ch recv pre");
                String k;
                k= data.split(" ")[4];
                channel_no = Integer.parseInt(k,16);
                String backnum=data.split(" ")[6];
                if(backnum.length()==1) backnum = "0" + backnum;
                k=data.split(" ")[5]+backnum;
                emg_amp_avr = (short)Integer.parseInt(k,16);

                backnum=data.split(" ")[8];
                if(backnum.length()==1) backnum = "0" + backnum;
                k=data.split(" ")[7]+backnum;
                emg_amp_max = (short)Integer.parseInt(k,16);

                backnum=data.split(" ")[10];
                if(backnum.length()==1) backnum = "0" + backnum;
                k=data.split(" ")[9]+backnum;
                emg_totalWork= (short)Integer.parseInt(k,16);

                int idx=channel_no-1;
                if(idx < 0)
                {
                    Log.e("pre_rehab","ch idx error");
                    idx=0;  // 0보다 작으면 에러임. 일단 죽지않게 처리
                }

                s_emg_amp_avr[idx]=emg_amp_avr;
                s_emg_amp_max[idx]=emg_amp_max;
                s_emg_totalWork[idx]=emg_totalWork;

                Log.d("TAG","ch=" + Integer.toString(channel_no) + " avr=" + Integer.toString(emg_amp_avr) + " max=" + Integer.toString(emg_amp_max) + " tot=" + Integer.toString(emg_totalWork)) ;

            }
            report_cnt++;
            if(report_cnt>=6)  reportDone();        // 6번으로 바꿀것
        }
        else if(cmd.equals(CMD_RESP_SENS_PST))
        {
            //String type=data.split(" ")[4];
            String pos=data.split(" ")[6];
            // gait
            if(pos.equals("1"))    // standup

            {
                runningPos = 0x1;
                if (pre_mode == 1) // gait
                {
                    bg_ctx.setImageResource(R.drawable.gait_01);
                }
                else if(pre_mode == 2) //squat
                {
                    bg_ctx.setImageResource(R.drawable.conico_squat);
                }
                else if(pre_mode == 3) //step
                {
                    bg_ctx.setImageResource(R.drawable.step_stand_up);
                }
            }
            else if(pos.equals("2"))
            {
                runningPos=0x2;
                bg_ctx.setImageResource(R.drawable.conico_walk);
            }
            // squart
            else if(pos.equals("4"))    // in
            {
                runningPos=0x4;
                bg_ctx.setImageResource(R.drawable.in_squat_01);
            }
            else if(pos.equals("8"))    // out
            {
                runningPos=0x8;
                bg_ctx.setImageResource(R.drawable.out_squat_01);
            }
            // stepbox
            else if(pos.equals("10"))    // extention
            {
                if(legtype_idx==0)  //left
                {
                    //if(flex_cnt%2==0) bg_ctx.setImageResource(R.drawable.step_left_up);
                    //else bg_ctx.setImageResource(R.drawable.step_right_down);

                    if(flex_cnt%2==0) {
                        currentPostureId = R.drawable.step_left_up;
                        leftOrRight = -1;
                    } else {
                        currentPostureId = R.drawable.step_right_down;
                        leftOrRight = 1;
                    }
                    currentPosture = getResources().getString(currentPostureId);
                }
                else    //right
                {
                    //if(flex_cnt%2==0) bg_ctx.setImageResource(R.drawable.step_right_up);
                    //else bg_ctx.setImageResource(R.drawable.step_left_down);

                    if(flex_cnt%2==0) {
                        currentPostureId = R.drawable.step_right_up;
                        leftOrRight = -1;
                    } else {
                        currentPostureId = R.drawable.step_left_down;
                        leftOrRight = 1;
                    }
                    currentPosture = getResources().getString(currentPostureId);
                }
                flex_cnt++;
                runningPos=0x10;

            } else if(pos.equals("20")) {
                runningPos=0x20;
                //bg_ctx.setImageResource(R.drawable.step_stand_up);
                currentPostureId = R.drawable.step_stand_up;
                currentPosture = getResources().getString(currentPostureId);
            }


        }

    }

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));

        //  ((ImageButton) findViewById(R.id.custom_back_btn)).setBackground(null);
        //  ((ImageButton) findViewById(R.id.custom_back_btn)).setEnabled(false);
        if(admin_mode!=0) {
            ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_03);
        }



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
