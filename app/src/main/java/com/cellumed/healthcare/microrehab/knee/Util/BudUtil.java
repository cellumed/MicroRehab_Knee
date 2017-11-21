package com.cellumed.healthcare.microrehab.knee.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microrehab.knee.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BudUtil {
    private final static String PREF_NAME = "com.cellumed.healthcare.microrehab.ems_ui";


    private static volatile BudUtil INSTANCE;

    public static ArrayList<Activity> actList = new ArrayList<>();
    public static String USER_ID = "";
    public static String SWVersion = "01.00";
    public static String FWVersion = "01.00";
    public static String HWVersion = "01.00";



    public static BudUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (BudUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BudUtil();
                }
            }
        }
        return INSTANCE;
    }


    public static void setShareValue(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);

        editor.apply();
    }


    public static String getShareValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME, Activity.MODE_PRIVATE);
        return sp.getString(key, "");
    }


    public static void removeShareValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME, Activity.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }


    public String getToday(String SimpleDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                SimpleDateFormat);
        Calendar mCalendar = Calendar.getInstance();
        return dateFormat.format(mCalendar.getTime());
    }

    public String getRegDateToString(String SimpleDateFormat,String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                SimpleDateFormat, Locale.KOREA);
        Calendar mCalendar = Calendar.getInstance();
        try {
            mCalendar.setTime(dateFormat.parse(date));
        } catch (Exception e) {
            return "-";
        }
        return dateFormat.format(mCalendar.getTime());
    }




    public static void goActivity(Context mContext, Class<?> className) {
        mContext.startActivity(new Intent(mContext, className).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public static void goActivityForResult(Context mContext, Class<?> className, Bundle mBundle, int resultCode) {
        ((Activity) mContext).startActivityForResult(new Intent(mContext, className).putExtras(mBundle), resultCode);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public static void goActivity(Context mContext, Class<?> className, Bundle mBundle) {
        mContext.startActivity(new Intent(mContext, className).putExtras(mBundle));
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public boolean isLimited(String join, String limit) {
        return join.equals(limit);

    }

    public boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean checkId(String id) {
        if (id.matches(".*" + "[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            Log.d("BudUtil", "id.matches):" + id.matches(".*" + "[ㄱ-ㅎㅏ-ㅣ가-힣]" + ".*"));
        }
        return id.matches(".*" + "[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");

    }

    //분을 초로 변경하는 함수
    public int calcMinute(int iMinute) {
        int iSecone;
        iSecone = iMinute * 60;
        return iSecone;
    }

    @SuppressLint("DefaultLocale")
    public String secondToMinute(int num,String min,String sec) {
        int minute = num % 3600 / 60;
        int second = num % 3600 % 60;
        if (second == 0) {
            if (minute==0) {
                return String.format("%d%s", num/60,min);
            }else{
                return String.format("%d%s", minute,min);

            }
        } else {
            return String.format("%d%s%d%s", minute,min, second,sec);
        }

    }

    public static  int getImage(Context mContext,int imageNumber){
        String resName = "i" + imageNumber;
        return mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
    }

    public void showMaterialDialog(Context mContext, String msg, MaterialDialog.SingleButtonCallback callback) {
        try {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
            .title(mContext.getString(R.string.notice))
            .titleColor(Color.parseColor("#2BCC92"))
            .content(msg)
            .positiveText(mContext.getString(R.string.ok))
            .positiveColor(Color.parseColor("#3BBBCE"))
            .onPositive((dialog, which) -> dialog.dismiss())
            .show();
            if (callback != null) {
                builder.onPositive(callback);
            }
        } catch (Exception e) {
            Log.d("BudUtil", "다이얼로그 오류");
        }
    }

    public String NumberFormatInsert(String date) {
        String result;
        StringBuilder sb = new StringBuilder();
        if (date.trim().length() == 1) {
            sb.append("0");
            sb.append(date.trim());
            result = sb.toString();
        } else {
            result = date;
        }
        return result;

    }

//버전 정보 표시
    public static String getAppVersion(Context context) {
        // application version
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
//            StatusLogger.e(TAG, "", e);
        }

        return versionName;
    }

    public static  int getAppVersionCode(Context mContext){
        int versionCode =0;
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    public Bitmap rotateImage(Bitmap src ,float degree){
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    public  String getName(final Context mContext ,final String attribute) {
        // dynamically search for a translation of the attribute
        final int id = mContext.getResources().getIdentifier(attribute, "string", mContext.getPackageName());
        if (id > 0) {
            final String translated = mContext.getResources().getString(id);
//            if (StringUtils.isNotBlank(translated))
            {
                return translated;
            }
        }
        return attribute;
    }

}
