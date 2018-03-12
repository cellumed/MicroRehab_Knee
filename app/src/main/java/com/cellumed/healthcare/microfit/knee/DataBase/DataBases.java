package com.cellumed.healthcare.microfit.knee.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;


public class DataBases extends SQLiteOpenHelper implements SqlImp {
    private  Context mContext;


    public DataBases(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//초기화시, 앱 설치 후 처음 프로그램 시작시

        // 사용자정보 테이블
        String userInfoQuery =
                "       CREATE TABLE IF NOT EXISTS "+ UserInfoTable +" (\n"
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + UserInfoName + " TEXT NOT NULL,\n"
                        + UserInfoBirth + " TEXT NOT NULL,\n"
                        + UserInfoGender +" TEXT NOT NULL,\n"
                        + UserInfoLegPart + " TEXT NOT NULL\n"
                        + ");";

        db.execSQL(userInfoQuery);

        //사전,ems,사후 기록 다 포함 되어있는 테이블
        String program_create_sql = "CREATE TABLE IF NOT EXISTS "+ ProgramTable +"("
                + "idx Integer PRIMARY KEY AUTOINCREMENT,"
                +  PreTime+" TEXT  ," // in sec
                 +PreAngleMin+" TEXT  ,"
                 +PreAngleMax+" TEXT  ,"
                 +PreEmgAvr+" TEXT  ,"
                 +PreEmgMax+" TEXT  ,"
                 +PreEmgTotal+" TEXT  ,"
                +PreEmgAvr2+" TEXT  ,"
                +PreEmgMax2+" TEXT  ,"
                +PreEmgTotal2+" TEXT  ,"
                +PreEmgAvr3+" TEXT  ,"
                +PreEmgMax3+" TEXT  ,"
                +PreEmgTotal3+" TEXT  ,"
                +PreEmgAvr4+" TEXT  ,"
                +PreEmgMax4+" TEXT  ,"
                +PreEmgTotal4+" TEXT  ,"
                +PreEmgAvr5+" TEXT  ,"
                +PreEmgMax5+" TEXT  ,"
                +PreEmgTotal5+" TEXT  ,"

                 +PostTime+" TEXT  ," // in sec
                 +PostAngleMax+" TEXT  ,"
                 +PostAngleMin+" TEXT  ,"
                 +PostEmgAvr+" TEXT  ,"
                 +PostEmgMax+" TEXT  ,"
                 +PostEmgTotal+" TEXT  ,"
                +PostEmgAvr2+" TEXT  ,"
                +PostEmgMax2+" TEXT  ,"
                +PostEmgTotal2+" TEXT  ,"
                +PostEmgAvr3+" TEXT  ,"
                +PostEmgMax3+" TEXT  ,"
                +PostEmgTotal3+" TEXT  ,"
                +PostEmgAvr4+" TEXT  ,"
                +PostEmgMax4+" TEXT  ,"
                +PostEmgTotal4+" TEXT  ,"
                +PostEmgAvr5+" TEXT  ,"
                +PostEmgMax5+" TEXT  ,"
                +PostEmgTotal5+" TEXT  ,"

                + ProgramType +" TEXT  ,"
                + ProgramState +" TEXT,"
                + ProgramStartDate +" TEXT,"
                + ProgramEndDate +" TEXT,"
                + ProgramSignalType +" TEXT,"
                + ProgramName +" TEXT  ,"
                + ProgramTime+" TEXT,"
                + ProgramTimeProgress+" TEXT,"
                + ProgramFrequency+" TEXT,"
                + ProgramFrequencyProgress+" TEXT,"
                + ProgramPulseOperationTime+" TEXT,"
                + ProgramPulseOperationTimeProgress+" TEXT,"
                + ProgramPulsePauseTime+" TEXT,"
                + ProgramPulsePauseTimeProgress+" TEXT,"
                + ProgramPulseRiseTime+" TEXT,"
                + ProgramPulseRiseTimeProgress+" TEXT,"
                + ProgramPulseWidth+" TEXT,"
                + ProgramPulseWidthProgress+" TEXT,"
                + UserInfoIdFk+" INTEGER NOT NULL REFERENCES user_info(id) on delete cascade);";

        Log.i("DB Query", ":: " + program_create_sql);
        db.execSQL(program_create_sql);



        /*
        CREATE TABLE IF NOT EXISTS user_info (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            birthday TEXT NOT NULL,
            sex TEXT,
            part TEXT
            );
        */

                /*
        CREATE TABLE IF NOT EXISTS exercise_result (
                idx INTEGER PRIMARY KEY AUTOINCREMENT,
                program TEXT,
                startTime TEXT,
                runTime TEXT,
                type TEXT,              // 재활, 개별
                pre_idx INTEGER,
                angleMin TEXT,
                angleMax TEXT,
                emgAvr1 TEXT,
                emgAvr2 TEXT,
                emgAvr3 TEXT,
                emgAvr4 TEXT,
                emgAvr5 TEXT,
                emgMax1 TEXT,
                emgMax2 TEXT,
                emgMax3 TEXT,
                emgMax4 TEXT,
                emgMax5 TEXT,
                emgTotal1 TEXT,
                emgTotal2 TEXT,
                emgTotal3 TEXT,
                emgTotal4 TEXT,
                emgTotal5 TEXT,
                user_info_id INTEGER,
                FOREIGN KEY(user_info_id) REFERENCES user_info(id));
        */

        /*
        CREATE TABLE IF NOT EXISTS pre_exercise_result (
                idx INTEGER PRIMARY KEY AUTOINCREMENT,
                program TEXT,
                startTime TEXT,
                runTime TEXT,
                type TEXT,              // 재활, 개별
                angleMin TEXT,
                angleMax TEXT,
                emgAvr1 TEXT,
                emgAvr2 TEXT,
                emgAvr3 TEXT,
                emgAvr4 TEXT,
                emgAvr5 TEXT,
                emgMax1 TEXT,
                emgMax2 TEXT,
                emgMax3 TEXT,
                emgMax4 TEXT,
                emgMax5 TEXT,
                emgTotal1 TEXT,
                emgTotal2 TEXT,
                emgTotal3 TEXT,
                emgTotal4 TEXT,
                emgTotal5 TEXT,
                user_info_id INTEGER,
                FOREIGN KEY(user_info_id) REFERENCES user_info(id));
        */

        /*
        CREATE TABLE IF NOT EXISTS post_exercise_result (
                idx INTEGER PRIMARY KEY AUTOINCREMENT,
                runTime TEXT,
                angleMin TEXT,
                angleMax TEXT,
                emgAvr1 TEXT,
                emgAvr2 TEXT,
                emgAvr3 TEXT,
                emgAvr4 TEXT,
                emgAvr5 TEXT,
                emgMax1 TEXT,
                emgMax2 TEXT,
                emgMax3 TEXT,
                emgMax4 TEXT,
                emgMax5 TEXT,
                emgTotal1 TEXT,
                emgTotal2 TEXT,
                emgTotal3 TEXT,
                emgTotal4 TEXT,
                emgTotal5 TEXT,
                pre_exercise_result_idx INTEGER,
                user_info_id INTEGER,
                FOREIGN KEY(user_info_id) REFERENCES user_info(id)
                FOREIGN KEY(pre_exercise_result_idx) REFERENCES pre_exercise_result(idx));
        */


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("tag","test 28  database - onUpgrade");
        dropAllTables(db);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if(!db.isReadOnly()) {
            Log.d("tag", "DB Configuration....");
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                String query = String.format("PRAGMA foreign_keys = %s", "ON");
                db.execSQL(query);
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    public void dropAllTables(SQLiteDatabase db) {
        Log.d("tag","test 28 database - dropAllTables");

        String query = "DROP TABLE IF EXISTS "+ UserInfoTable +" ;";
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS "+ ProgramTable +" ;";
        db.execSQL(query);
    }
    public void reset(){
        Log.d("tag","test 28 database - reset");
        SQLiteDatabase db = this.getWritableDatabase();
        dropAllTables(db);
        onCreate(db);
    }

}
