package com.cellumed.healthcare.microrehab.knee.Home;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;

import java.lang.reflect.Field;

public class MainApplication extends Application {
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private UncaughtExceptionHandler unCatchExceptionHandler;
    private IMP_CMD cmd;
    static String CNAME = MainApplication.class.getSimpleName();
    @Override
    public void onCreate() {

        // set default font
        Field f= null;
        try {
            f = Typeface.class.getDeclaredField("DEFAULT");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(null, Typeface.SANS_SERIF);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            f = Typeface.class.getDeclaredField("SANS_SERIF");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(null, Typeface.SANS_SERIF);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        unCatchExceptionHandler = new UncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(unCatchExceptionHandler);
        super.onCreate();
    }
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return unCatchExceptionHandler;
    }
    public void setCmd(IMP_CMD cmd){
        this.cmd = cmd;
    }
    public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // 이곳에서 로그를 남기는 작업을 하면 된다.
            Toast.makeText(MainApplication.this, "강제종료", Toast.LENGTH_SHORT).show();
            Log.e(CNAME, "error -----------------> ");

            androidDefaultUEH.uncaughtException(thread, ex);
        }
    }
}

