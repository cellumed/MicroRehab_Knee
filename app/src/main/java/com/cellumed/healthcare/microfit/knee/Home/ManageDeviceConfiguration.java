package com.cellumed.healthcare.microfit.knee.Home;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ljh0928 on 2018. 3. 5..
 */

public class ManageDeviceConfiguration {

    final String SP_USER_INFO = "SF_USER_INFO";

    final String USER_NAME = "user_name";
    final String USER_BIRTH = "user_birth";
    final String USER_GENDER = "user_gender";
    final String USER_BODYPART = "user_bodypart";

    private Context context;
    private SharedPreferences spref;
    private SharedPreferences.Editor spEditor;

    // Saved UserInfo
    private String userName;
    private String userBirth;                     // 1970-01-01
    private String userGender;                    // M, FM
    private String userBodyPart;                  // LL, RL

    public static ManageDeviceConfiguration getInstance(){
        if( manageConfiguration == null){
            manageConfiguration = new ManageDeviceConfiguration();
            return manageConfiguration;
        }
        return  manageConfiguration;
    }

    public void init(Context context){
        this.context = context;
        spref = context.getSharedPreferences(SP_USER_INFO, MODE_PRIVATE);
        spEditor = spref.edit();

        getInstance().loadUserInfo();
    }

    public ManageDeviceConfiguration loadUserInfo(){
        userName = spref.getString(USER_NAME, "");
        userBirth = spref.getString(USER_BIRTH, "");
        userGender = spref.getString(USER_GENDER, "");
        userBodyPart = spref.getString(USER_BODYPART, "");

        return ManageDeviceConfiguration.getInstance();
    }

    public String getUserName(){
        return userName;
    }

    public String getUserBirth(){
        return userBirth;
    }

    public String getUserGender(){
        return userGender;
    }

    public String getUserBodyPart(){
        return userBodyPart;
    }

    public void udpateUserName(String name){
        spEditor.putString(USER_NAME, name);
        spEditor.commit();
    }

    public void udpateUserBirth(String birth){
        spEditor.putString(USER_BIRTH, birth);
        spEditor.commit();
    }

    public void updateUserGender(String gender){
        spEditor.putString(USER_GENDER, gender);
        spEditor.commit();
    }

    public void setUserBodyPart(String bodyPart){
        spEditor.putString(USER_BODYPART, bodyPart);
        spEditor.commit();
    }

    private static ManageDeviceConfiguration manageConfiguration;
}
