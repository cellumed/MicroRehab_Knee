package com.cellumed.healthcare.microrehab.knee.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cellumed.healthcare.microrehab.knee.DAO.DAO_Program;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DialogRehabInfo extends Dialog implements SqlImp {

    @Bind(R.id.tv_program_name)
    TextView tvProgramName;
    @Bind(R.id.tv_starttime)
    TextView tvStartTime;

    @Bind(R.id.tv_pre_time)
    TextView tvPreTime;
    @Bind(R.id.tv_pre_angle1)
    TextView tvPreAngle1;
    @Bind(R.id.tv_pre_angle2)
    TextView tvPreAngle2;
    @Bind(R.id.tv_pre_emg_avr)
    TextView tvPreEmgAvr;
    @Bind(R.id.tv_pre_emg_max)
    TextView tvPreEmgMax;
    @Bind(R.id.tv_pre_emg_total)
    TextView tvPreEmgTotal;

    @Bind(R.id.tv_pre_emg_avr2)
    TextView tvPreEmgAvr2;
    @Bind(R.id.tv_pre_emg_max2)
    TextView tvPreEmgMax2;
    @Bind(R.id.tv_pre_emg_total2)
    TextView tvPreEmgTotal2;


    @Bind(R.id.tv_pre_emg_avr3)
    TextView tvPreEmgAvr3;
    @Bind(R.id.tv_pre_emg_max3)
    TextView tvPreEmgMax3;
    @Bind(R.id.tv_pre_emg_total3)
    TextView tvPreEmgTotal3;


    @Bind(R.id.tv_pre_emg_avr4)
    TextView tvPreEmgAvr4;
    @Bind(R.id.tv_pre_emg_max4)
    TextView tvPreEmgMax4;
    @Bind(R.id.tv_pre_emg_total4)
    TextView tvPreEmgTotal4;


    @Bind(R.id.tv_pre_emg_avr5)
    TextView tvPreEmgAvr5;
    @Bind(R.id.tv_pre_emg_max5)
    TextView tvPreEmgMax5;
    @Bind(R.id.tv_pre_emg_total5)
    TextView tvPreEmgTotal5;
    
    
    @Bind(R.id.tv_post_time)
    TextView tvPostTime;
    @Bind(R.id.tv_post_angle1)
    TextView tvPostAngle1;
    @Bind(R.id.tv_post_angle2)
    TextView tvPostAngle2;
    @Bind(R.id.tv_post_emg_avr)
    TextView tvPostEmgAvr;
    @Bind(R.id.tv_post_emg_max)
    TextView tvPostEmgMax;
    @Bind(R.id.tv_post_emg_total)
    TextView tvPostEmgTotal;

    @Bind(R.id.tv_post_emg_avr2)
    TextView tvPostEmgAvr2;
    @Bind(R.id.tv_post_emg_max2)
    TextView tvPostEmgMax2;
    @Bind(R.id.tv_post_emg_total2)
    TextView tvPostEmgTotal2;

    @Bind(R.id.tv_post_emg_avr3)
    TextView tvPostEmgAvr3;
    @Bind(R.id.tv_post_emg_max3)
    TextView tvPostEmgMax3;
    @Bind(R.id.tv_post_emg_total3)
    TextView tvPostEmgTotal3;


    @Bind(R.id.tv_post_emg_avr4)
    TextView tvPostEmgAvr4;
    @Bind(R.id.tv_post_emg_max4)
    TextView tvPostEmgMax4;
    @Bind(R.id.tv_post_emg_total4)
    TextView tvPostEmgTotal4;


    @Bind(R.id.tv_post_emg_avr5)
    TextView tvPostEmgAvr5;
    @Bind(R.id.tv_post_emg_max5)
    TextView tvPostEmgMax5;
    @Bind(R.id.tv_post_emg_total5)
    TextView tvPostEmgTotal5;


    @Bind(R.id.close)
    Button close;

    private static Typeface typeface;
    Context gContext;

    /*
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(gContext.getAssets(), "NotoSansKR-Regular-Hestia.otf");
        }
        setGlobalFont(getWindow().getDecorView());
    }

    private void setGlobalFont(View view) {
        if(view != null) {
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
    public DialogRehabInfo(Context mContext, DAO_Program program) {

        super(mContext);
        gContext=mContext;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setTitle(mContext.getString(R.string.TitleReportView));
        setContentView(R.layout.dialog_rehab_info);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.95);
        getWindow().setAttributes(lp);

        tvProgramName.setText(program.getProgramName());
        tvStartTime.setText(program.getProgramStartDate());
        
        tvPreTime.setText(program.getPreTime());
        tvPreAngle1.setText(program.getPreAngleMin());
        tvPreAngle2.setText(program.getPreAngleMax());
        tvPreEmgAvr.setText(program.getPreEmgAvr());
        tvPreEmgMax.setText(program.getPreEmgMax());
        tvPreEmgTotal.setText(program.getPreEmgTotal());
        tvPreEmgAvr2.setText(program.getPreEmgAvr2());
        tvPreEmgMax2.setText(program.getPreEmgMax2());
        tvPreEmgTotal2.setText(program.getPreEmgTotal2());
        tvPreEmgAvr3.setText(program.getPreEmgAvr3());
        tvPreEmgMax3.setText(program.getPreEmgMax3());
        tvPreEmgTotal3.setText(program.getPreEmgTotal3());
        tvPreEmgAvr4.setText(program.getPreEmgAvr4());
        tvPreEmgMax4.setText(program.getPreEmgMax4());
        tvPreEmgTotal4.setText(program.getPreEmgTotal4());
        tvPreEmgAvr5.setText(program.getPreEmgAvr5());
        tvPreEmgMax5.setText(program.getPreEmgMax5());
        tvPreEmgTotal5.setText(program.getPreEmgTotal5());

        tvPostTime.setText(program.getPostTime());
        tvPostAngle1.setText(program.getPostAngleMin());
        tvPostAngle2.setText(program.getPostAngleMax());
        tvPostEmgAvr.setText(program.getPostEmgAvr());
        tvPostEmgMax.setText(program.getPostEmgMax());
        tvPostEmgTotal.setText(program.getPostEmgTotal());
        tvPostEmgAvr2.setText(program.getPostEmgAvr2());
        tvPostEmgMax2.setText(program.getPostEmgMax2());
        tvPostEmgTotal2.setText(program.getPostEmgTotal2());
        tvPostEmgAvr3.setText(program.getPostEmgAvr3());
        tvPostEmgMax3.setText(program.getPostEmgMax3());
        tvPostEmgTotal3.setText(program.getPostEmgTotal3());
        tvPostEmgAvr4.setText(program.getPostEmgAvr4());
        tvPostEmgMax4.setText(program.getPostEmgMax4());
        tvPostEmgTotal4.setText(program.getPostEmgTotal4());
        tvPostEmgAvr5.setText(program.getPostEmgAvr5());
        tvPostEmgMax5.setText(program.getPostEmgMax5());
        tvPostEmgTotal5.setText(program.getPostEmgTotal5());

        close.setOnClickListener(v -> dismiss());
    }
}
