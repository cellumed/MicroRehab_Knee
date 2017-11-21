package com.cellumed.healthcare.microrehab.knee.DataBase;

public interface SqlImp {
    String DATABASE_NAME = "ems.db";
    int DATABASE_VERSION = 1;

    String ProgramTable = "ProgramTable"; //운동프로그램
    String UserId = "id";
    String UserPass = "pass";
    String UserType = "type";
    String Idx = "idx";
    String UserName = "name";
    String UserGender = "gender";
    String UserLegType = "legtype";
    String UserBirthday = "birthday";
    String UserRegDate = "reg_date";
    String UserUpDate = "up_date";
    String UserExplanation = "explanation"; //계획


    String ProgramName = "name"; //프로그램 이름
    String ProgramStartDate = "sdate";
    String ProgramEndDate = "edate";
    String ProgramSignalType = "stype";
    String ProgramState = "state"; //프로그램 상태
    String ProgramTime = "time"; //프로그램 시간
    String ProgramTimeProgress = "time_progress"; //프로그램 시간
    String ProgramPulseOperationTime = "pulse_operation_time"; //동작시간
    String ProgramPulseOperationTimeProgress = "pulse_operation_time_progress"; //동작시간
    String ProgramPulsePauseTime = "pulse_pause_time"; //휴지 시간
    String ProgramPulsePauseTimeProgress = "pulse_pause_time_progress"; //휴지 시간
    String ProgramFrequency = "frequency";//주파수
    String ProgramFrequencyProgress = "frequency_progress";//주파수
    String ProgramPulseWidth = "pulse_width";//펄스폭
    String ProgramPulseWidthProgress = "pulse_width_progress";//펄스폭
    String ProgramPulseRiseTime = "pulse_rise_time"; //펄스 상승시간
    String ProgramPulseRiseTimeProgress = "pulse_rise_time_progress"; //펄스 상승시간
    String ProgramExplanation = "explanation";//설명
    String ProgramTitle = "title"; //계획 제목
    String ProgramType = "type"; //계획 타잎

    String PreTime="pre_time"; // in sec
    String PreAngleMin="pre_angle_min";
    String PreAngleMax="pre_angle_max";
    String PreEmgAvr="pre_emg_avr";
    String PreEmgMax="pre_emg_max";
    String PreEmgTotal="pre_emg_total";
    String PreEmgAvr2="pre_emg_2avr";
    String PreEmgMax2="pre_emg_2max";
    String PreEmgTotal2="pre_emg_2total";
    String PreEmgAvr3="pre_emg_avr3";
    String PreEmgMax3="pre_emg_max3";
    String PreEmgTotal3="pre_emg_total3";
    String PreEmgAvr4="pre_emg_avr4";
    String PreEmgMax4="pre_emg_max4";
    String PreEmgTotal4="pre_emg_total4";
    String PreEmgAvr5="pre_emg_avr5";
    String PreEmgMax5="pre_emg_max5";
    String PreEmgTotal5="pre_emg_total5";


    String PostTime="post_time"; // in sec
    String PostAngleMax="post_angle_max";
    String PostAngleMin="post_angle_min";
    String PostEmgAvr="post_emg_avr";
    String PostEmgMax ="post_emg_max";
    String PostEmgTotal="post_emg_total";
    String PostEmgAvr2="post_emg_avr2";
    String PostEmgMax2 ="post_emg_max2";
    String PostEmgTotal2="post_emg_total2";
    String PostEmgAvr3="post_emg_avr3";
    String PostEmgMax3 ="post_emg_max3";
    String PostEmgTotal3="post_emg_total3";
    String PostEmgAvr4="post_emg_avr4";
    String PostEmgMax4 ="post_emg_max4";
    String PostEmgTotal4="post_emg_total4";
    String PostEmgAvr5="post_emg_avr5";
    String PostEmgMax5 ="post_emg_max5";
    String PostEmgTotal5="post_emg_total5";

    String WorkoutDataStartDate = "start_date"; //시작시간
    String WorkoutDataEndDate = "end_date"; //종료시간
    String WorkoutDataTime = "time"; //운동시간
    String WorkoutDataContent = "content"; //내용
    String WorkoutDataETC = "etc"; //내용

    String SignalType = "signal_type";  // 자극타입

    String ORDER_BY = "ORDER BY";
    String BETWEEN = "BETWEEN";
    String DESC = "DESC";
    String ASC = "ASC";
    String ALL_FIELD[] ={"*"};

}
