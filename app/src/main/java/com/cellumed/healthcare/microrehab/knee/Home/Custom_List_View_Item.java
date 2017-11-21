package com.cellumed.healthcare.microrehab.knee.Home;

import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by test on 2016-10-22.
 */
public class Custom_List_View_Item  {
    private String nameString;
    private String levelValueString;
    private Button decreaseButton;
    private Button increaseButton;

    private boolean touched = false;
    private Handler handler = new Handler();
    private TextView level; // for invalidate while touching


    public synchronized boolean getTouched() { return touched;}
    public synchronized void setTouched(boolean b) { touched = b; }

    public Handler getHandler() { return handler; }

    public void setNameString(String name) {
        nameString = name;
    }

    public void setLevelValueString(String levelValue) {
        levelValueString = levelValue;
    }

    public void setDecreaseButton(Button aDecreasButton) {
        decreaseButton = aDecreasButton;
    }

    public void setIncreaseButton(Button aIncreaseButton) {
        increaseButton = aIncreaseButton;
    }

    public void setLevel(TextView ll) {
        level = ll;
    }
    public TextView getLevel() {
        return level;
    }

    public String getNameString() {
        return this.nameString;
    }

    public String getLevelValueString() {
        return this.levelValueString;
    }

    public Button getDecreaseButton() {
        return this.decreaseButton;
    }

    public Button getIncreaseButton() {
        return this.increaseButton;
    }

}
