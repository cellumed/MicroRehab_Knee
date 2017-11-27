package com.cellumed.healthcare.microrehab.knee.Util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cellumed.healthcare.microrehab.knee.R;

/**
 * Created by ljh0928 on 2017. 11. 27..
 */

public class CustomToast extends Toast {
    Context mContext;
    private static CustomToast mInstance;

    public static CustomToast getInstance(Context ctx){
        if(mInstance == null){
            synchronized (CustomToast.class){
                if(mInstance == null){
                    mInstance = new CustomToast(ctx);
                }
            }
        }

        return mInstance;
    }

    public CustomToast(Context ctx){
        super(ctx);
        mContext = ctx;
    }

    public void showToast(String body, int duration){
        LayoutInflater inflater;
        View v;

        if(false){
            Activity act = (Activity)mContext;
            inflater = act.getLayoutInflater();
            v = inflater.inflate(R.layout.custom_toast_layout, null);
        }
        else{
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.custom_toast_layout, null);
        }

        TextView text = (TextView)v.findViewById(R.id.toast_text);
        text.setText(body);
        show(this, v, duration);
    }

    private void show(Toast toast, View v, int duration){
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }
}
