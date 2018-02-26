package com.cellumed.healthcare.microfit.knee.Setting;

import android.widget.Button;

/**
 * Created by test on 2016-11-06.
 */
public class UserProgramListItem {
    private Button selectButton;
    private String titleString;
    private String dateString;
    private String programNameString;
    private String userNameString;

    public void setSelectButton(Button button) {
        selectButton = button;
    }

    public void setTitleString(String title) {
        titleString = title;
    }

    public void setDateString(String date) {
        dateString = date;
    }

    public void setProgramNameString(String programName) {
        programNameString = programName;
    }

    public void setUserNameString(String userName) {
        userNameString = userName;
    }

    public Button getSelectButton() {
        return this.selectButton;
    }

    public String getTitleString() {
        return this.titleString;
    }

    public String getDateString() {
        return this.dateString;
    }

    public String getProgramNameString() {
        return this.programNameString;
    }

    public String getUserNameString() {
        return this.userNameString;
    }
}
