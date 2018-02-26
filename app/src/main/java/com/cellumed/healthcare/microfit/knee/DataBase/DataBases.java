package com.cellumed.healthcare.microfit.knee.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBases extends SQLiteOpenHelper implements SqlImp {
    private  Context mContext;


    public DataBases(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//초기화시, 앱 설치 후 처음 프로그램 시작시

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
                + ProgramPulseWidthProgress+" TEXT );";

        db.execSQL(program_create_sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("tag","test 28  database - onUpgrade");
        dropAllTables(db);
        onCreate(db);
    }

    public void dropAllTables(SQLiteDatabase db) {
        Log.d("tag","test 28 database - dropAllTables");
        String drop_program = "DROP TABLE IF EXISTS "+ ProgramTable +" ;";
        db.execSQL(drop_program);
    }
    public void reset(){
        Log.d("tag","test 28 database - reset");
        SQLiteDatabase db = this.getWritableDatabase();
        dropAllTables(db);
        onCreate(db);
    }

}
