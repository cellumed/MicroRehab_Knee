package com.cellumed.healthcare.microfit.knee.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cellumed.healthcare.microfit.knee.DAO.DAO_UserInfo;
import com.cellumed.healthcare.microfit.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microfit.knee.Home.ManageDeviceConfiguration;
import com.cellumed.healthcare.microfit.knee.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by test on 2016-11-19.
 */
public class DialogUserInfoEdit extends Dialog implements  SqlImp {

    @Bind(R.id.et_userName)
    EditText etUserName;
    //@Bind(R.id.et_birth)
    //EditText etUserBirth;
    @Bind(R.id.btInputBirth)
    Button btInputBirth;
    @Bind(R.id.cb_male)
    CheckBox cbMale;
    @Bind(R.id.cb_female)
    CheckBox cbFemale;

    @Bind(R.id.cb_left)
    CheckBox cbLeft;
    @Bind(R.id.cb_right)
    CheckBox cbRight;

    @Bind(R.id.saveDone)
    Button saveDone;
    @Bind(R.id.cancel)
    Button cancel;

    private Context mContext;
    CallbackDialog callback;

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (java.text.ParseException pe) {
            return false;
        }
        return true;
    }

    public DialogUserInfoEdit(Context mContext, CallbackDialog callback) {
        super(mContext);
        this.mContext = mContext;
        this.callback = callback;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setTitle(mContext.getString(R.string.SettingUser));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_user_info_edit);
        ButterKnife.bind(this);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) mContext.getApplicationContext().getSystemService(mContext.getApplicationContext().WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.95);
        lp.height = (int) (wm.getDefaultDisplay().getHeight() * 0.8);
        getWindow().setAttributes(lp);

        etUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    downKeyboard(mContext, etUserName);
                    return true;
                }
                return false;
            }
        });


        cbMale.setChecked(true);
        cbFemale.setChecked(false);
        cbLeft.setChecked(false);
        cbRight.setChecked(true);


        cbLeft.setOnClickListener(v-> {
            cbLeft.setChecked(true);
            cbRight.setChecked(false);
        });

        cbRight.setOnClickListener(v-> {
            cbLeft.setChecked(false);
            cbRight.setChecked(true);
        });

        cbMale.setOnClickListener(v-> {
            cbMale.setChecked(true);
            cbFemale.setChecked(false);
        });


        cbFemale.setOnClickListener(v-> {
            cbMale.setChecked(false);
            cbFemale.setChecked(true);
        });

        saveDone.setOnClickListener(v -> {

            if(etUserName.getText().toString().equals(""))
            {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.pleaseName), Toast.LENGTH_SHORT).show();
            }
            /*
            else if(etUserBirth.getText().toString().equals(""))
            {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.pleaseBirthDay), Toast.LENGTH_SHORT).show();
            }

            else if(!isValidDate(etUserBirth.getText().toString()))
            {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.pleaseRightBirthDay), Toast.LENGTH_SHORT).show();

            }
            */
            else {
                /*
                editor.putString(UserName, etUserName.getText().toString());
                editor.putString(UserBirthday, etUserBirth.getText().toString());
                editor.putInt(UserGender, cbFemale.isChecked() ? 1 : 0);
                editor.putInt(UserLegType, cbRight.isChecked() ? 1 : 0);
                editor.commit();
                */


                String name = etUserName.getText().toString();
                String birth = currentDate;
                String gender = cbFemale.isChecked() ? GENDER_FEMALE:GENDER_MALE;
                String leg = cbRight.isChecked() ? RIGHT_LEG:LEFT_LEG;

                // DB에 사용자정보 저장
                final HashMap<String, String> workoutData = new HashMap<>();
                workoutData.put(UserInfoName, name);
                workoutData.put(UserInfoBirth, birth);
                workoutData.put(UserInfoGender, gender);
                workoutData.put(UserInfoLegPart, leg);

                if (new DBQuery(mContext).newUserInfoInsert(workoutData)) {
                    Log.d("DialogUserInfoEdit", "저장");
                }

                // DB 저장후 id 값을 가지고 온다
                String id = new DBQuery(mContext).getUserInfoId(name, birth);

                Log.i("DialoguserInfoEdit", "User ID:" + id + ", " + name + birth + gender + leg);

                // SharedPreference 도 저장
                DAO_UserInfo dao_userInfo = new DAO_UserInfo();
                dao_userInfo.setId(id);
                dao_userInfo.setName(name);
                dao_userInfo.setBirth(birth);
                dao_userInfo.setGender(gender);
                dao_userInfo.setLegPart(leg);
                ManageDeviceConfiguration.getInstance().updateUser(dao_userInfo);

                this.callback.onPositive();
                dismiss();
            }
        });
        cancel.setOnClickListener(v -> {
            this.callback.onNegative();
            dismiss();
        });

        btInputBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = 2000;
                month = 0;
                day = 1;
                new DatePickerDialog(mContext, android.R.style.Theme_Holo_Dialog, dateSetListener, year, month, day).show();
            }
        });

        show();
    }

    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    int year, month, day;
    String currentDate = "";

    public void updateDate(){
        btInputBirth.setText(currentDate);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            currentDate = String.format("%d-%d-%d", year,monthOfYear+1, dayOfMonth);
            updateDate();
            Toast.makeText(mContext, currentDate, Toast.LENGTH_SHORT).show();
        }
    };
}
