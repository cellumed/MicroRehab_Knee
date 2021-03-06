package com.cellumed.healthcare.microfit.knee.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.cellumed.healthcare.microfit.knee.DAO.DAO_Program;
import com.cellumed.healthcare.microfit.knee.DAO.DAO_UserInfo;
import com.cellumed.healthcare.microfit.knee.Util.BudUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class DBQuery implements SqlImp {

    private DBOpenHelper db;

    public DBOpenHelper getDb() {
        return db;
    }

    public DBQuery(Context mContext) {
        db = new DBOpenHelper(mContext);
    }

    private boolean queryExecute(Boolean mQuery) {
        final Boolean query = mQuery;
        db.close();
        return query;

    }

    public boolean newUserInfoInsert(HashMap<String, String> programInfo) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }

        final boolean b = db.setRecords(UserInfoTable, mValues);
        db.close();

        return b;
    }

    public boolean newProgramInsert(HashMap<String, String> programInfo) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }

        final boolean b = db.setRecords(ProgramTable, mValues);
        db.close();

        return b;
    }

    public boolean setProgramUpdate(HashMap<String, String> programInfo,String idx) {
        ContentValues mValues = new ContentValues();
        final Set<String> key = programInfo.keySet();
        for (String keyName : key) {
            String valueName = programInfo.get(keyName);
            mValues.put(keyName, valueName);

        }

        final String where = Idx
                + " = '"
                + idx
                + "'";

        final boolean b= db.setField(ProgramTable, mValues,where);
        db.close();

        return b;
    }


    public boolean endProgramData(String idx){
        final String where =Idx
                + " = '"
                + idx
                + "'";
        ContentValues mValues = new ContentValues();
        mValues.put(ProgramEndDate,BudUtil.getInstance().getToday("yyyy.MM.dd HH.mm.ss"));
   //     DecimalFormat formatter = new DecimalFormat("00");
    //    String min = formatter.format(time / 60);
     //   String sec = formatter.format(time % 60);
     //   mValues.put(ProgramTimeProgress, String.format("%s:%s", min, sec));


        final boolean b = db.setField(ProgramTable, mValues,where);
        db.close();

        return b;
    }

    public DAO_UserInfo getUserInfoFromId(String id) {
        final String where = UserInfoId + " = " + id;
        DAO_UserInfo userInfo = new DAO_UserInfo();

        final Cursor mCursor = db.getField(UserInfoTable, ALL_FIELD, where, null, null);

        while (mCursor.moveToNext()) {
            try {

                userInfo.setId(mCursor.getString(mCursor.getColumnIndex(UserInfoId)));
                userInfo.setName(mCursor.getString(mCursor.getColumnIndex(UserInfoName)));
                userInfo.setBirth(mCursor.getString(mCursor.getColumnIndex(UserInfoBirth)));
                userInfo.setGender(mCursor.getString(mCursor.getColumnIndex(UserInfoGender)));
                userInfo.setLegPart(mCursor.getString(mCursor.getColumnIndex(UserInfoLegPart)));

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }

    public String getUserInfoId(String name, String birth) {
        final String where = UserInfoName + " = '" + name + "' AND " + UserInfoBirth + " = '" + birth + "'";
        return db.getField(UserInfoTable, UserInfoId, where, null, null);
    }


    public String getProgramTime(String name) {
        final String where = ProgramName
                + " = '"
                + name
                + "'";

        return db.getField(ProgramTable, ProgramTime, where, null, null);
    }

    public String getProgramPulseOperationTime(String name) {
        final String where = ProgramName
                + " = '"
                + name
                + "'";

        return db.getField(ProgramTable, ProgramPulseOperationTime, where, null, null);
    }

    public String getIdxFromStartDate(String std) {
        final String where = ProgramStartDate
                + " = '"
                + std
                + "'";

        return db.getField(ProgramTable, Idx, where, null, null);
    }

    public String getProgramPulsePauseTime(String name) {
        final String where = ProgramName
                + " = '"
                + name
                + "'";

        return db.getField(ProgramTable, ProgramPulsePauseTime, where, null, null);
    }

    public DAO_Program getProgramFromStartTime(String time) {
        final String where = ProgramStartDate
                + " = '"
                + time
                + "'";
        DAO_Program mDaoProgram = new DAO_Program();

        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, null, null);
        while (mCursor.moveToNext()) {
            try {
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));



                mDaoProgram.setProgramStartDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStartDate)));
                mDaoProgram.setProgramEndDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramEndDate)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulseOperationTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTimeProgress)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramPulsePauseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTimeProgress)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramFrequencyProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequencyProgress)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseWidthProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidthProgress)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramPulseRiseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTimeProgress)));

                mDaoProgram.setPreTime(mCursor.getString(mCursor
                        .getColumnIndex(PreTime)));
                mDaoProgram.setPreAngleMin(mCursor.getString(mCursor
                        .getColumnIndex(PreAngleMin)));
                mDaoProgram.setPreAngleMax(mCursor.getString(mCursor
                        .getColumnIndex(PreAngleMax)));
                mDaoProgram.setPreEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr)));
                mDaoProgram.setPreEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax)));
                mDaoProgram.setPreEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal)));
                mDaoProgram.setPreEmgAvr2(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr2)));
                mDaoProgram.setPreEmgMax2(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax2)));
                mDaoProgram.setPreEmgTotal2(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal2)));
                mDaoProgram.setPreEmgAvr3(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr3)));
                mDaoProgram.setPreEmgMax3(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax3)));
                mDaoProgram.setPreEmgTotal3(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal3)));
                mDaoProgram.setPreEmgAvr4(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr4)));
                mDaoProgram.setPreEmgMax4(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax4)));
                mDaoProgram.setPreEmgTotal4(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal4)));
                mDaoProgram.setPreEmgAvr5(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr5)));
                mDaoProgram.setPreEmgMax5(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax5)));
                mDaoProgram.setPreEmgTotal5(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal5)));

                mDaoProgram.setPostTime(mCursor.getString(mCursor
                        .getColumnIndex(PostTime)));
                mDaoProgram.setPostAngleMin(mCursor.getString(mCursor
                        .getColumnIndex(PostAngleMin)));
                mDaoProgram.setPostAngleMax(mCursor.getString(mCursor
                        .getColumnIndex(PostAngleMax)));
                mDaoProgram.setPostEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr)));
                mDaoProgram.setPostEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax)));
                mDaoProgram.setPostEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal)));
                mDaoProgram.setPostEmgAvr2(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr2)));
                mDaoProgram.setPostEmgMax2(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax2)));
                mDaoProgram.setPostEmgTotal2(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal2)));
                mDaoProgram.setPostEmgAvr3(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr3)));
                mDaoProgram.setPostEmgMax3(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax3)));
                mDaoProgram.setPostEmgTotal3(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal3)));
                mDaoProgram.setPostEmgAvr4(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr4)));
                mDaoProgram.setPostEmgMax4(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax4)));
                mDaoProgram.setPostEmgTotal4(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal4)));
                mDaoProgram.setPostEmgAvr5(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr5)));
                mDaoProgram.setPostEmgMax5(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax5)));
                mDaoProgram.setPostEmgTotal5(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal5)));

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();

        return mDaoProgram;
    }

    public ArrayList<DAO_UserInfo> getALLUserInfo() {

        ArrayList<DAO_UserInfo> userinfo = new ArrayList<>();
        final Cursor mCursor = db.getField(UserInfoTable, ALL_FIELD, null, null, UserInfoId, null);

        while (mCursor.moveToNext()) {
            try {

                DAO_UserInfo user = new DAO_UserInfo();
                user.setId(mCursor.getString(mCursor.getColumnIndex(UserInfoId)));
                user.setName(mCursor.getString(mCursor.getColumnIndex(UserInfoName)));
                user.setBirth(mCursor.getString(mCursor.getColumnIndex(UserInfoBirth)));
                user.setGender(mCursor.getString(mCursor.getColumnIndex(UserInfoGender)));
                user.setLegPart(mCursor.getString(mCursor.getColumnIndex(UserInfoLegPart)));
                userinfo.add(user);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        mCursor.close();
        db.close();

        return userinfo;
    }

    public ArrayList<DAO_Program> getALLProgram(String user_id) {

        ArrayList<DAO_Program> mProgram = new ArrayList<>();
        String where = UserInfoIdFk + " = " + user_id;
        String temp = "";

        final Cursor mCursor = db.getField(ProgramTable, ALL_FIELD, where, null, Idx, null);

        while (mCursor.moveToNext()) {
            try {
                DAO_Program mDaoProgram = new DAO_Program();
                mDaoProgram.setIdx(mCursor.getString(mCursor
                        .getColumnIndex(Idx)));
                mDaoProgram.setProgramType(mCursor.getString(mCursor
                        .getColumnIndex(ProgramType)));

                mDaoProgram.setProgramName(mCursor.getString(mCursor
                        .getColumnIndex(ProgramName)));

                mDaoProgram.setProgramState(mCursor.getString(mCursor
                        .getColumnIndex(ProgramState)));
                mDaoProgram.setProgramStartDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramStartDate)));
                mDaoProgram.setProgramEndDate(mCursor.getString(mCursor
                        .getColumnIndex(ProgramEndDate)));
                mDaoProgram.setProgramPulseOperationTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTime)));
                mDaoProgram.setProgramPulseOperationTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseOperationTimeProgress)));
                mDaoProgram.setProgramPulsePauseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTime)));
                mDaoProgram.setProgramPulsePauseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulsePauseTimeProgress)));
                mDaoProgram.setProgramFrequency(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequency)));
                mDaoProgram.setProgramFrequencyProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramFrequencyProgress)));
                mDaoProgram.setProgramPulseWidth(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidth)));
                mDaoProgram.setProgramPulseWidthProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseWidthProgress)));
                mDaoProgram.setProgramPulseRiseTime(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTime)));
                mDaoProgram.setProgramPulseRiseTimeProgress(mCursor.getString(mCursor
                        .getColumnIndex(ProgramPulseRiseTimeProgress)));

                mDaoProgram.setPreTime(mCursor.getString(mCursor
                        .getColumnIndex(PreTime)));
                mDaoProgram.setPreAngleMin(mCursor.getString(mCursor
                        .getColumnIndex(PreAngleMin)));
                mDaoProgram.setPreAngleMax(mCursor.getString(mCursor
                        .getColumnIndex(PreAngleMax)));
                mDaoProgram.setPreEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr)));
                mDaoProgram.setPreEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax)));
                mDaoProgram.setPreEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal)));
                mDaoProgram.setPreEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr2)));
                mDaoProgram.setPreEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax2)));
                mDaoProgram.setPreEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal2)));
                mDaoProgram.setPreEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr3)));
                mDaoProgram.setPreEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax3)));
                mDaoProgram.setPreEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal3)));
                mDaoProgram.setPreEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr4)));
                mDaoProgram.setPreEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax4)));
                mDaoProgram.setPreEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal4)));
                mDaoProgram.setPreEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgAvr5)));
                mDaoProgram.setPreEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgMax5)));
                mDaoProgram.setPreEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PreEmgTotal5)));

                mDaoProgram.setPostTime(mCursor.getString(mCursor
                        .getColumnIndex(PostTime)));
                mDaoProgram.setPostAngleMin(mCursor.getString(mCursor
                        .getColumnIndex(PostAngleMin)));
                mDaoProgram.setPostAngleMax(mCursor.getString(mCursor
                        .getColumnIndex(PostAngleMax)));
                mDaoProgram.setPostEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr)));
                mDaoProgram.setPostEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax)));
                mDaoProgram.setPostEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal)));
                mDaoProgram.setPostEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr2)));
                mDaoProgram.setPostEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax2)));
                mDaoProgram.setPostEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal2)));
                mDaoProgram.setPostEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr3)));
                mDaoProgram.setPostEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax3)));
                mDaoProgram.setPostEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal3)));
                mDaoProgram.setPostEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr4)));
                mDaoProgram.setPostEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax4)));
                mDaoProgram.setPostEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal4)));
                mDaoProgram.setPostEmgAvr(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgAvr5)));
                mDaoProgram.setPostEmgMax(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgMax5)));
                mDaoProgram.setPostEmgTotal(mCursor.getString(mCursor
                        .getColumnIndex(PostEmgTotal5)));

                mProgram.add(mDaoProgram);

            } catch (SQLiteException | IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        mCursor.close();
        db.close();


        return mProgram;
    }

    public boolean programRemoveNotComplete() {
        final String where = ProgramEndDate + " is null";
        final boolean b = db.dataDelete(ProgramTable, where) != 0;

        return b;
    }

    public boolean programRemove(String idx) {
        boolean b = false;
        final String where = String.format(Idx + " = '%s'", idx);

        return db.dataDelete(ProgramTable, where) != 0;
    }

    public boolean deleteUserInfo(String id) {
        boolean b = false;
        final String where = String.format(UserInfoId + " = '%s'", id);

        return db.dataDelete(UserInfoTable, where) != 0;
    }
}
