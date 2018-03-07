package com.cellumed.healthcare.microfit.knee.Home;

import android.content.Context;
import android.content.SharedPreferences;

import com.cellumed.healthcare.microfit.knee.DAO.DAO_UserInfo;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ljh0928 on 2018. 3. 5..
 */

public class ManageDeviceConfiguration {

    final String SP_USER_INFO = "SF_USER_INFO";

    final String USER_ID = "user_id";
    final String USER_NAME = "user_name";
    final String USER_BIRTH = "user_birth";
    final String USER_GENDER = "user_gender";
    final String USER_LEGPART = "user_bodypart";

    private Context context;
    private SharedPreferences spref;
    private SharedPreferences.Editor spEditor;

    // Saved UserInfo
    private String userId;
    private String userName;
    private String userBirth;                     // 1970-01-01
    private String userGender;                    // M, FM
    private String userLegPart;                  // LL, RL

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
        userId = spref.getString(USER_ID, "");
        userName = spref.getString(USER_NAME, "");
        userBirth = spref.getString(USER_BIRTH, "");
        userGender = spref.getString(USER_GENDER, "");
        userLegPart = spref.getString(USER_LEGPART, "");

        return ManageDeviceConfiguration.getInstance();
    }

    public String getUserId() { return userId; }

    public String getUserName(){
        return userName;
    }

    public String getUserBirth(){
        return userBirth;
    }

    public String getUserGender(){
        return userGender;
    }

    public String getUserLegPart(){
        return userLegPart;
    }

    public void updateUserId(String id){
        spEditor.putString(USER_ID, id);
        spEditor.commit();
    }

    public void updateUserName(String name){
        spEditor.putString(USER_NAME, name);
        spEditor.commit();
    }

    public void updateUserBirth(String birth){
        spEditor.putString(USER_BIRTH, birth);
        spEditor.commit();
    }

    public void updateUserGender(String gender){
        spEditor.putString(USER_GENDER, gender);
        spEditor.commit();
    }

    public void updateUserLegPart(String bodyPart){
        spEditor.putString(USER_LEGPART, bodyPart);
        spEditor.commit();
    }

    public void updateUser(DAO_UserInfo user){
        spEditor.putString(USER_ID, user.getId());
        spEditor.putString(USER_NAME, user.getName());
        spEditor.putString(USER_BIRTH, user.getBirth());
        spEditor.putString(USER_GENDER, user.getGender());
        spEditor.putString(USER_LEGPART, user.getLegPart());
        spEditor.commit();

        loadUserInfo();
    }

    private static ManageDeviceConfiguration manageConfiguration;
}
