package com.cellumed.healthcare.microfit.knee.Home;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 *
 */

public class BackPressStopHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressStopHandler(Activity context) {
        this.activity = context;
    }


    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                activity.finishAndRemoveTask();
            } else
            {
                ActivityCompat.finishAffinity(activity);
                // activity.finish();
            }
            toast.cancel();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}