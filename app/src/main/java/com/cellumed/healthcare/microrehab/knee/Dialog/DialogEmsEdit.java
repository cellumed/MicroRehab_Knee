package com.cellumed.healthcare.microrehab.knee.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.content.SharedPreferences;


import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.Home.Act_EMS;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import java.util.HashMap;



/**
 * Created by test on 2016-11-20.
 */
public class DialogEmsEdit implements SqlImp {
    String sfName="EMS_TEMP_SETTING";   // 임시 shared 저장용

    private Context mContext;

    private boolean et_PulseOperationTimeFocus = false;
    private boolean et_PulsePauseTimeFocus = false;
    private boolean et_PulseRiseTimeFocus = false;

    // sliding bar
    final int operMin=1;
    final int operStep=1;

    final int pulseWidthMin = 50;
    final int pulseWidthStep=50;
    final int freqMin = 2;
    final int freqStep=1;
    final int workingTimeMin = 1;


    private static Typeface typeface;



/*
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
    public DialogEmsEdit(Context mContext) {
        this.mContext = mContext;
        if(typeface == null) {
            typeface = Typeface.createFromAsset(mContext.getAssets(), "NotoSansKR-Regular-Hestia.otf");
        }
        showEditProgram();
    }

    // 값 유효성 검사
    private boolean isCheckDialog(MaterialDialog materialDialog) {


        final EditText et_WorkingTime = (EditText) materialDialog.findViewById(R.id.et_WorkingTime);
        final EditText et_PulseOperationTime = (EditText) materialDialog.findViewById(R.id.et_PulseOperationTime);
        final EditText et_PulsePauseTime = (EditText) materialDialog.findViewById(R.id.et_PulsePauseTime);
        final EditText et_Frequency = (EditText) materialDialog.findViewById(R.id.et_Frequency);
        final EditText et_PulseWidth = (EditText) materialDialog.findViewById(R.id.et_PulseWidth);
        final EditText et_PulseRiseTime = (EditText) materialDialog.findViewById(R.id.et_PulseRiseTime);
        if (et_WorkingTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.time_six_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulseOperationTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseOperationTime_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulsePauseTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulsePauseTime_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_Frequency.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.Frequency_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulseWidth.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseWidth_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (et_PulseRiseTime.getText().length() == 0) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.PulseRiseTime_error), (dialog, which) -> materialDialog.show());
            return false;
        } else if (Float.parseFloat(et_PulseOperationTime.getText().toString()) < 0 || Float.parseFloat(et_PulseOperationTime.getText().toString()) > 10) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error1), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Float.parseFloat(et_PulsePauseTime.getText().toString()) < 0 || Float.parseFloat(et_PulsePauseTime.getText().toString()) > 60) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error2), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Integer.parseInt(et_Frequency.getText().toString()) < 2 || Integer.parseInt(et_Frequency.getText().toString()) > 150) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error3), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Integer.parseInt(et_PulseWidth.getText().toString()) < 50 || Integer.parseInt(et_PulseWidth.getText().toString()) > 500) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error4), (dialog, which) -> materialDialog.show());
            return false;
        }else if (Float.parseFloat(et_PulseRiseTime.getText().toString()) < 0 || Float.parseFloat(et_PulseRiseTime.getText().toString()) > 1) {
            BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.program_error5), (dialog, which) -> materialDialog.show());
            return false;
        }

        return true;
    }


    private void showEditProgram() {

        MaterialDialog mMaterialDialog = new MaterialDialog.Builder(mContext)
                .title(mContext.getString(R.string.EMS_Setting))
                .titleColor(Color.parseColor("#ffffff"))
                .positiveColor(Color.parseColor("#ffffff"))
                .negativeColor(Color.parseColor("#dddddd"))
                .backgroundColor(Color.parseColor("#236793"))
                .customView(R.layout.dialog_ems_edit, false)
                .positiveText(mContext.getString(R.string.start))
                .negativeText(mContext.getString(R.string.cancel))
                .onPositive((dialog, which) -> {
                    if (isCheckDialog(dialog)) {
                        final Spinner sp_SignalType = (Spinner)dialog.findViewById(R.id.sp_signal_type);
                        final EditText et_WorkingTime = (EditText) dialog.findViewById(R.id.et_WorkingTime);
                        final EditText et_PulseOperationTime = (EditText) dialog.findViewById(R.id.et_PulseOperationTime);
                        final EditText et_PulsePauseTime = (EditText) dialog.findViewById(R.id.et_PulsePauseTime);
                        final EditText et_Frequency = (EditText) dialog.findViewById(R.id.et_Frequency);
                        final EditText et_PulseWidth = (EditText) dialog.findViewById(R.id.et_PulseWidth);
                        final EditText et_PulseRiseTime = (EditText) dialog.findViewById(R.id.et_PulseRiseTime);
                        final SeekBar sb_WorkingTime = (SeekBar) dialog.findViewById(R.id.sb_WorkingTime);
                        final SeekBar sb_PulseOperationTime = (SeekBar) dialog.findViewById(R.id.sb_PulseOperationTime);
                        final SeekBar sb_PulsePauseTime = (SeekBar) dialog.findViewById(R.id.sb_PulsePauseTime);
                        final SeekBar sb_Frequency = (SeekBar) dialog.findViewById(R.id.sb_Frequency);
                        final SeekBar sb_PulseWidth = (SeekBar) dialog.findViewById(R.id.sb_PulseWidth);
                        final SeekBar sb_PulseRiseTime = (SeekBar) dialog.findViewById(R.id.sb_PulseRiseTime);
                        final HashMap<String, String> programMap = new HashMap<>();
                        final HashMap<String, String>favoritesMap = new HashMap<>();





                        // 저장
                        SharedPreferences sf = mContext.getSharedPreferences(sfName, 0);

                        SharedPreferences.Editor editor = sf.edit();

                        editor.putInt(SignalType, sp_SignalType.getSelectedItemPosition());
                        editor.putString(ProgramFrequency, et_Frequency.getText().toString());
                        editor.putString(ProgramTime, et_WorkingTime.getText().toString());
                        editor.putString(ProgramPulseOperationTime, et_PulseOperationTime.getText().toString());
                        editor.putString(ProgramPulsePauseTime, et_PulsePauseTime.getText().toString());
                        editor.putString(ProgramPulseRiseTime, et_PulseRiseTime.getText().toString());
                        editor.putString(ProgramPulseWidth, et_PulseWidth.getText().toString());

                        editor.commit();

                        // ems로 이동
                        final Bundle bundle = new Bundle();
                        bundle.putInt("mode", 9);
                        BudUtil.goActivity(mContext, Act_EMS.class, bundle);

                    } else {
                        dialog.show();
                    }

                }).onNegative((dialog, which) ->
                        dialog.dismiss()
                ).show();
        mMaterialDialog.setCanceledOnTouchOutside(false);
        //setGlobalFont(mMaterialDialog.getCustomView());

        final Spinner sp_SignalType = (Spinner)mMaterialDialog.findViewById(R.id.sp_signal_type);
        final SeekBar sb_WorkingTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_WorkingTime);
        final SeekBar sb_PulseOperationTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulseOperationTime);
        final SeekBar sb_PulsePauseTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulsePauseTime);
        final SeekBar sb_Frequency = (SeekBar) mMaterialDialog.findViewById(R.id.sb_Frequency);
        final SeekBar sb_PulseWidth = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulseWidth);
        final SeekBar sb_PulseRiseTime = (SeekBar) mMaterialDialog.findViewById(R.id.sb_PulseRiseTime);
        final EditText et_WorkingTime = (EditText) mMaterialDialog.findViewById(R.id.et_WorkingTime);
        final EditText et_PulseOperationTime = (EditText) mMaterialDialog.findViewById(R.id.et_PulseOperationTime);
        final EditText et_PulsePauseTime = (EditText) mMaterialDialog.findViewById(R.id.et_PulsePauseTime);
        final EditText et_Frequency = (EditText) mMaterialDialog.findViewById(R.id.et_Frequency);
        final EditText et_PulseWidth = (EditText) mMaterialDialog.findViewById(R.id.et_PulseWidth);
        final EditText et_PulseRiseTime = (EditText) mMaterialDialog.findViewById(R.id.et_PulseRiseTime);
        final Button add_WorkingTime = (Button) mMaterialDialog.findViewById(R.id.add_WorkingTime);
        final Button add_PulseOperationTime = (Button) mMaterialDialog.findViewById(R.id.add_PulseOperationTime);
        final Button add_PulsePauseTime = (Button) mMaterialDialog.findViewById(R.id.add_PulsePauseTime);
        final Button add_Frequency = (Button) mMaterialDialog.findViewById(R.id.add_Frequency);
        final Button add_PulseWidth = (Button) mMaterialDialog.findViewById(R.id.add_PulseWidth);
        final Button add_PulseRiseTime = (Button) mMaterialDialog.findViewById(R.id.add_PulseRiseTime);
        final Button remove_WorkingTime = (Button) mMaterialDialog.findViewById(R.id.remove_WorkingTime);
        final Button remove_PulseOperationTime = (Button) mMaterialDialog.findViewById(R.id.remove_PulseOperationTime);
        final Button remove_PulsePauseTime = (Button) mMaterialDialog.findViewById(R.id.remove_PulsePauseTime);
        final Button removeFrequency = (Button) mMaterialDialog.findViewById(R.id.removeFrequency);
        final Button removePulseWidth = (Button) mMaterialDialog.findViewById(R.id.removePulseWidth);
        final Button remove_PulseRiseTime = (Button) mMaterialDialog.findViewById(R.id.remove_PulseRiseTime);



        ArrayAdapter a8Adapter = ArrayAdapter.createFromResource(
                mContext, R.array.SignalType, R.layout.custom_spinner);
        a8Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_SignalType.setAdapter(a8Adapter);

        sp_SignalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)    // prog1
                {
                    et_WorkingTime.setText("20");
                    et_Frequency.setText("50");
                    et_PulseOperationTime.setText("5");
                    et_PulsePauseTime.setText("10");
                    et_PulseRiseTime.setText("1");
                    et_PulseWidth.setText("350");
                }
                else if(i==1)    // prog2
                {
                    et_WorkingTime.setText("20");
                    et_Frequency.setText("50");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("10");
                    et_PulseRiseTime.setText("1");
                    et_PulseWidth.setText("350");
                }
                else if(i==2)    // prog3
                {
                    et_WorkingTime.setText("20");
                    et_Frequency.setText("50");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("20");
                    et_PulseRiseTime.setText("1");
                    et_PulseWidth.setText("350");
                }
                else if(i==3)    // prog4
                {
                    et_WorkingTime.setText("20");
                    et_Frequency.setText("50");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("30");
                    et_PulseRiseTime.setText("1");
                    et_PulseWidth.setText("350");
                }
                else if(i==4)    // prog5
                {
                    et_WorkingTime.setText("20");
                    et_Frequency.setText("50");
                    et_PulseOperationTime.setText("5");
                    et_PulsePauseTime.setText("5");
                    et_PulseRiseTime.setText("1");
                    et_PulseWidth.setText("350");
                }
                else if(i==5)    // prog6
                {
                    et_WorkingTime.setText("20");
                    et_Frequency.setText("50");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("50");
                    et_PulseRiseTime.setText("1");
                    et_PulseWidth.setText("350");
                }
                else if(i==6)    // prog7
                {
                    et_WorkingTime.setText("30");
                    et_Frequency.setText("99");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("0");
                    et_PulseRiseTime.setText("0");
                    et_PulseWidth.setText("200");
                }
                else if(i==7)    // prog8
                {
                    et_WorkingTime.setText("30");
                    et_Frequency.setText("4");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("0");
                    et_PulseRiseTime.setText("0");
                    et_PulseWidth.setText("300");
                }
                else if(i==8)    // prog9
                {
                    et_WorkingTime.setText("30");
                    et_Frequency.setText("125");
                    et_PulseOperationTime.setText("10");
                    et_PulsePauseTime.setText("0");
                    et_PulseRiseTime.setText("0");
                    et_PulseWidth.setText("175");
                }


                sb_WorkingTime.setProgress(Integer.parseInt( et_WorkingTime.getText().toString() ) - workingTimeMin);
                sb_Frequency.setProgress(Integer.parseInt(et_Frequency.getText().toString()) - freqMin);
                sb_PulseOperationTime.setProgress(  Integer.parseInt( et_PulseOperationTime.getText().toString()) - operMin   );
                sb_PulsePauseTime.setProgress( Integer.parseInt( et_PulsePauseTime.getText().toString()) );
                sb_PulseWidth.setProgress((Integer.parseInt(et_PulseWidth.getText().toString())-pulseWidthMin)/pulseWidthStep);
                sb_PulseRiseTime.setProgress( Integer.parseInt(  et_PulseRiseTime.getText().toString() ));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 설정 가져올 값
        SharedPreferences sf = mContext.getSharedPreferences(sfName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sf.edit();

        // check and set default value if null
        int ii=sf.getInt(SignalType,0);
        sp_SignalType.setSelection(ii);

        String t=sf.getString(ProgramTime,"");
        if( t== null || t=="")
        {
            t="20";
            editor.putString(ProgramTime, t);
        }
        et_WorkingTime.setText(t);

        // 2-150
        t=sf.getString(ProgramFrequency,"");
        if(t==null  || t=="") { t="35"; editor.putString(ProgramFrequency, t);  }
        et_Frequency.setText(t);

        //1-10
        t=sf.getString(ProgramPulseOperationTime,"");
        if(t==null  || t=="") { t="5"; editor.putString(ProgramPulseOperationTime, t);  }
        et_PulseOperationTime.setText(t);

        //0-10
        t=sf.getString(ProgramPulsePauseTime,"");
        if(t==null  || t=="") { t="5"; editor.putString(ProgramPulsePauseTime, t);  }
        et_PulsePauseTime.setText(t);

        // 0-1
        t=sf.getString(ProgramPulseRiseTime,"");
        if(t==null  || t=="") { t="1"; editor.putString(ProgramPulseRiseTime, t);  }
        et_PulseRiseTime.setText(t);

        //50-400. 25us
        t=sf.getString(ProgramPulseWidth,"");
        if(t==null  || t=="") { t="350"; editor.putString(ProgramPulseWidth, t);  }
        et_PulseWidth.setText(t);

        // sync
        editor.commit();


        // check and set default value if null





        et_WorkingTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_WorkingTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_WorkingTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, et_WorkingTime);
                    return true;
                }
                return false;
            }
        });


        et_PulseOperationTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseOperationTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseOperationTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, et_PulseOperationTime);
                    return true;
                }
                return false;
            }
        });

        et_PulsePauseTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulsePauseTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulsePauseTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, et_PulsePauseTime);
                    return true;
                }
                return false;
            }
        });
        et_PulseRiseTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseRiseTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseRiseTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, et_PulseRiseTime);
                    return true;
                }
                return false;
            }
        });
        et_Frequency.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_Frequency.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_Frequency.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, et_Frequency);
                    return true;
                }
                return false;
            }
        });

        et_PulseWidth.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_PulseWidth.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_PulseWidth.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, et_PulseWidth);
                    return true;
                }
                return false;
            }
        });



        et_WorkingTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                et_WorkingTime.setSelection(et_WorkingTime.getText().length());
            }
        });
        /*
        et_WorkingTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    sb_WorkingTime.setProgress(Integer.parseInt(s.toString())-workingTimeMin);
                    et_WorkingTime.setSelection(s.length());
//                    et_WorkingTime.setSelection();
                } catch (Exception e) {
                }
            }
        });
*/


        et_PulseOperationTime.setOnFocusChangeListener((v, hasFocus) -> {
            et_PulseOperationTimeFocus = hasFocus;
            if (hasFocus) {
                et_PulseOperationTime.setSelection(et_PulseOperationTime.length());
            }
        });
        et_PulsePauseTime.setOnFocusChangeListener((v, hasFocus) -> {
            et_PulsePauseTimeFocus = hasFocus;
            if (hasFocus) {
                et_PulsePauseTime.setSelection(et_PulsePauseTime.getText().length());
            }
        });

        et_Frequency.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                et_Frequency.setSelection(et_Frequency.getText().length());
            }
        });
        et_PulseRiseTime.setOnFocusChangeListener((v, hasFocus) -> {
            et_PulseRiseTimeFocus = hasFocus;
            if (hasFocus) {
                et_PulseRiseTime.setSelection(et_PulseRiseTime.getText().length());
            }
        });
        et_PulseWidth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                et_PulseWidth.setSelection(et_PulseWidth.getText().length());
            }
        });
        sb_WorkingTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              //  Log.d("DialogEditProgram", "progress:" + progress);
                et_WorkingTime.setText(String.valueOf(progress+ workingTimeMin));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_WorkingTime.setProgress(Integer.parseInt( sf.getString(ProgramTime,"") ) - workingTimeMin);
        add_WorkingTime.setOnClickListener(v -> sb_WorkingTime.setProgress(sb_WorkingTime.getProgress() + 1));
        remove_WorkingTime.setOnClickListener(v -> sb_WorkingTime.setProgress(sb_WorkingTime.getProgress() - 1));

/*
        et_PulseOperationTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float v = Float.parseFloat(s.toString());
                    if (v >10) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseoneten), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
                    }else {
                        v = (int)v * 10;
                        sb_PulseOperationTime.setProgress((int) v);
                        et_PulseOperationTime.setSelection(s.length());
                    }
                } catch (Exception e) {
                }
            }
        });
*/

        sb_PulseOperationTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean sb_pulseOperationTimeTracking;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


 //               if (!et_PulseOperationTimeFocus || sb_pulseOperationTimeTracking) {
                int value=operMin + (progress*operStep);
               //     float value = (float) (progress / 10.0);
                    et_PulseOperationTime.setText(String.valueOf((int) value));


          //      }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pulseOperationTimeTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pulseOperationTimeTracking = false;
            }
        });

        sb_PulseOperationTime.setProgress(  Integer.parseInt( sf.getString(ProgramPulseOperationTime,"")) - operMin   );
        add_PulseOperationTime.setOnClickListener(v -> sb_PulseOperationTime.setProgress(sb_PulseOperationTime.getProgress() + 1));
        remove_PulseOperationTime.setOnClickListener(v -> sb_PulseOperationTime.setProgress(sb_PulseOperationTime.getProgress() - 1));
        /*
        add_PulseOperationTime.setOnClickListener(v -> {
            float v1 = Float.parseFloat(et_PulseOperationTime.getText().toString()) + 0.1f;
            if (v1 > 10) {
                return;
            }
            et_PulseOperationTime.setText(String.format("%.0f", v1));
        });
        remove_PulseOperationTime.setOnClickListener(v -> {
            float v1 = Float.parseFloat(et_PulseOperationTime.getText().toString()) - 0.1f;
            if (v1 < 0.0) {
                return;
            }
            et_PulseOperationTime.setText(String.format("%.0f", v1));
        });
        */


        sb_PulsePauseTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean sb_pulsePauseTimeTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
         /*       if (!et_PulsePauseTimeFocus || sb_pulsePauseTimeTracking) {
                    float value = (float) (progress / 10.0);
                    et_PulsePauseTime.setText(String.valueOf((int) value));
*/
                et_PulsePauseTime.setText(String.valueOf((int) progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pulsePauseTimeTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pulsePauseTimeTracking = false;
            }
        });
        /*
        et_PulsePauseTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float v = Float.parseFloat(s.toString());
                    if (v >10) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseoneten), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();

                        });
                    }else{
                        v = (int)v * 10;
                        sb_PulsePauseTime.setProgress((int) v);
                        et_PulsePauseTime.setSelection(s.length());
                    }

                } catch (Exception e) {
                }
            }
        });
        */


        sb_PulsePauseTime.setProgress( Integer.parseInt( sf.getString(ProgramPulsePauseTime,"")) );
        add_PulsePauseTime.setOnClickListener(v -> sb_PulsePauseTime.setProgress(sb_PulsePauseTime.getProgress() + 1));
        remove_PulsePauseTime.setOnClickListener(v -> sb_PulsePauseTime.setProgress(sb_PulsePauseTime.getProgress() - 1));


        //********** FREQ

        sb_Frequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = freqMin + (progress * freqStep);
                if (value > 150) {
                    value = 150;
                }
                et_Frequency.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /*
        et_Frequency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value < 2) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleasetwo), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
                    } else if (value > 150) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseonefive), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
                    } else {
                        value -= freqMin;
                        sb_Frequency.setProgress(value);
                        et_Frequency.setSelection(s.length());
                    }
                } catch (Exception e) {
                }
            }
        });
*/

        // check
        sb_Frequency.setProgress(Integer.parseInt(sf.getString(ProgramFrequency,"")) - freqMin);
        add_Frequency.setOnClickListener(v -> sb_Frequency.setProgress(sb_Frequency.getProgress() + 1));
        removeFrequency.setOnClickListener(v -> sb_Frequency.setProgress(sb_Frequency.getProgress() - 1));


    //********** PULSE WIDTH

        sb_PulseWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = pulseWidthMin + (progress * pulseWidthStep);
                if (value >500) {
                    value = 500;
                }
                et_PulseWidth.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /*
        et_PulseWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value > 500) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseonefore), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
                    } else {
                        value -= pulseWidthMin;
                        value /= pulseWidthStep;
                        value = (int)value;
                        sb_PulseWidth.setProgress(value);
                        et_PulseWidth.setSelection(s.length());
                    }
                } catch (Exception e) {
                }
            }
        });
        et_PulseWidth.setOnEditorActionListener((v, actionId, event) -> {
            final int i = Integer.parseInt(et_PulseWidth.getText().toString());
            if (i < 50) {
                BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleasefive), (dialog, which) -> {
                    dialog.dismiss();
                    mMaterialDialog.show();
                });
                return true;
            }
            return false;
        });
*/
        sb_PulseWidth.setProgress((Integer.parseInt(sf.getString(ProgramPulseWidth,""))-pulseWidthMin)/pulseWidthStep);
        add_PulseWidth.setOnClickListener(v -> sb_PulseWidth.setProgress(sb_PulseWidth.getProgress() + 1));
        removePulseWidth.setOnClickListener(v -> sb_PulseWidth.setProgress(sb_PulseWidth.getProgress() - 1));


    //*********** Pulse Riseing
        sb_PulseRiseTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean sb_pulseRiseTimeTracking;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
   /*             if (!et_PulseRiseTimeFocus || sb_pulseRiseTimeTracking) {
                    //final String pattern = "#.#";
                    //final DecimalFormat decimalFormat = new DecimalFormat(pattern);
                    int v=0;
                    if(progress >= 50)  v=1;

                    et_PulseRiseTime.setText(String.valueOf(v));
                }
                */
                et_PulseRiseTime.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sb_pulseRiseTimeTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sb_pulseRiseTimeTracking = false;
            }
        });


        sb_PulseRiseTime.setProgress( Integer.parseInt(   sf.getString(ProgramPulseRiseTime,"")) );
        /*
        et_PulseRiseTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float v = Float.parseFloat(s.toString());
                    if (v > 1) {
                        BudUtil.getInstance().showMaterialDialog(mContext, mContext.getString(R.string.pleaseone), (dialog, which) -> {
                            dialog.dismiss();
                            mMaterialDialog.show();
                        });
                    } else {
                        if(v!=0) v = 100;
                        sb_PulseRiseTime.setProgress((int) v);
                        et_PulseRiseTime.setSelection(s.length());
                    }

                } catch (Exception e) {
                }
            }
        });
        */
        add_PulseRiseTime.setOnClickListener(v -> sb_PulseRiseTime.setProgress(sb_PulseRiseTime.getProgress() + 1));
        remove_PulseRiseTime.setOnClickListener(v -> sb_PulseRiseTime.setProgress(sb_PulseRiseTime.getProgress() - 1));
    }


    public int secondToMinute(int num) {
        int minute = num % 3600 / 60;
        if (minute==0) {
            return  num/60;
        }else{
            return minute;

        }
    }

    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
