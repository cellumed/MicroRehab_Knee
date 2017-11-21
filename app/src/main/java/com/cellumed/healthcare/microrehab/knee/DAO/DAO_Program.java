package com.cellumed.healthcare.microrehab.knee.DAO;


public class DAO_Program {
    String Idx;
    String ProgramType ;    // 스쿼트, 계단,
    String ProgramState ;   //프로그램 상태 // 중간중지시 기록안함
    String ProgramStartDate;    // date+time        // pre부터시작
    String ProgramEndDate;    // date+time
    String ProgramSignalType;
    String ProgramName;
    String ProgramTime ; //프로그램 시간. 운동하기로한 원래 시간
    String ProgramTimeProgress ;// 실제 운동한 시간
    String ProgramFrequency ;//주파수
    String ProgramFrequencyProgress ;//주파수
    String ProgramPulseOperationTime ; //자극시간
    String ProgramPulseOperationTimeProgress ; //동작시간
    String ProgramPulsePauseTime ; //휴지 시간
    String ProgramPulsePauseTimeProgress ; //휴지 시간
    String ProgramPulseRiseTime ; //펄스 상승시간
    String ProgramPulseRiseTimeProgress ; //펄스 상승시간
    String ProgramPulseWidth ;//펄스폭
    String ProgramPulseWidthProgress ;//펄스폭

    String PreTime; // in sec
    String PreAngleMin;
    String PreAngleMax;
    String PreEmgAvr;
    String PreEmgMax;
    String PreEmgTotal;
    String PreEmgAvr2;
    String PreEmgMax2;
    String PreEmgTotal2;
    String PreEmgAvr3;
    String PreEmgMax3;
    String PreEmgTotal3;
    String PreEmgAvr4;
    String PreEmgMax4;
    String PreEmgTotal4;
    String PreEmgAvr5;
    String PreEmgMax5;
    String PreEmgTotal5;

    String PostTime; // in sec
    String PostAngleMax;
    String PostEmgAvr;
    String PostAngleMin;
    String PostEmgMax;
    String PostEmgTotal;
    String PostEmgAvr2;
    String PostEmgMax2;
    String PostEmgTotal2;
    String PostEmgAvr3;
    String PostEmgMax3;
    String PostEmgTotal3;
    String PostEmgAvr4;
    String PostEmgMax4;
    String PostEmgTotal4;
    String PostEmgAvr5;
    String PostEmgMax5;
    String PostEmgTotal5;

    // pre
    public String getPreTime() {
        return PreTime;
    }

    public void setPreTime(String inp) {
        PreTime = inp;
    }

    public String getPreAngleMin() {
        return PreAngleMin;
    }

    public void setPreAngleMin(String inp) {
        PreAngleMin = inp;
    }

    public String getPreAngleMax() {
        return PreAngleMax;
    }

    public void setPreAngleMax(String inp) {
        PreAngleMax = inp;
    }

    //ch1
    public String getPreEmgAvr() {
        return PreEmgAvr;
    }
    public void setPreEmgAvr(String inp) {
        PreEmgAvr = inp;
    }
    public String getPreEmgMax() {
        return PreEmgMax;
    }
    public void setPreEmgMax(String inp) {
        PreEmgMax = inp;
    }
    public String getPreEmgTotal() {
        return PreEmgTotal;
    }
    public void setPreEmgTotal(String inp) {
        PreEmgTotal = inp;
    }
    //ch2
    public String getPreEmgAvr2() {
        return PreEmgAvr2;
    }
    public void setPreEmgAvr2(String inp) {
        PreEmgAvr2 = inp;
    }
    public String getPreEmgMax2() {
        return PreEmgMax2;
    }
    public void setPreEmgMax2(String inp) {
        PreEmgMax2 = inp;
    }
    public String getPreEmgTotal2() {
        return PreEmgTotal2;
    }
    public void setPreEmgTotal2(String inp) {
        PreEmgTotal2 = inp;
    }

    //ch3
    public String getPreEmgAvr3() {
        return PreEmgAvr3;
    }
    public void setPreEmgAvr3(String inp) {
        PreEmgAvr3 = inp;
    }
    public String getPreEmgMax3() {
        return PreEmgMax3;
    }
    public void setPreEmgMax3(String inp) {
        PreEmgMax3 = inp;
    }
    public String getPreEmgTotal3() {
        return PreEmgTotal3;
    }
    public void setPreEmgTotal3(String inp) {
        PreEmgTotal3 = inp;
    }

    //ch4
    public String getPreEmgAvr4() {
        return PreEmgAvr4;
    }
    public void setPreEmgAvr4(String inp) {
        PreEmgAvr4 = inp;
    }
    public String getPreEmgMax4() {
        return PreEmgMax4;
    }
    public void setPreEmgMax4(String inp) {
        PreEmgMax4 = inp;
    }
    public String getPreEmgTotal4() {
        return PreEmgTotal4;
    }
    public void setPreEmgTotal4(String inp) {
        PreEmgTotal4 = inp;
    }

    //ch5
    public String getPreEmgAvr5() {
        return PreEmgAvr5;
    }
    public void setPreEmgAvr5(String inp) {
        PreEmgAvr5 = inp;
    }
    public String getPreEmgMax5() {
        return PreEmgMax5;
    }
    public void setPreEmgMax5(String inp) {
        PreEmgMax5 = inp;
    }
    public String getPreEmgTotal5() {
        return PreEmgTotal5;
    }
    public void setPreEmgTotal5(String inp) {
        PreEmgTotal5 = inp;
    }


    //post
    public String getPostTime() {
        return PostTime;
    }

    public void setPostTime(String inp) {
        PostTime = inp;
    }

    public String getPostAngleMin() {
        return PostAngleMin;
    }

    public void setPostAngleMin(String inp) {
        PostAngleMin = inp;
    }

    public String getPostAngleMax() {
        return PostAngleMax;
    }

    public void setPostAngleMax(String inp) {
        PostAngleMax = inp;
    }

    //ch1
    public String getPostEmgAvr() {
        return PostEmgAvr;
    }
    public void setPostEmgAvr(String inp) {
        PostEmgAvr = inp;
    }
    public String getPostEmgMax() {
        return PostEmgMax;
    }
    public void setPostEmgMax(String inp) {
        PostEmgMax = inp;
    }
    public String getPostEmgTotal() {
        return PostEmgTotal;
    }
    public void setPostEmgTotal(String inp) {
        PostEmgTotal = inp;
    }

    //ch2
    public String getPostEmgAvr2() {
        return PostEmgAvr2;
    }
    public void setPostEmgAvr2(String inp) {
        PostEmgAvr2 = inp;
    }
    public String getPostEmgMax2() {
        return PostEmgMax2;
    }
    public void setPostEmgMax2(String inp) {
        PostEmgMax2 = inp;
    }
    public String getPostEmgTotal2() {
        return PostEmgTotal2;
    }
    public void setPostEmgTotal2(String inp) {
        PostEmgTotal2 = inp;
    }

    //ch3
    public String getPostEmgAvr3() {
        return PostEmgAvr3;
    }
    public void setPostEmgAvr3(String inp) {
        PostEmgAvr3 = inp;
    }
    public String getPostEmgMax3() {
        return PostEmgMax3;
    }
    public void setPostEmgMax3(String inp) {
        PostEmgMax3 = inp;
    }
    public String getPostEmgTotal3() {
        return PostEmgTotal3;
    }
    public void setPostEmgTotal3(String inp) {
        PostEmgTotal3 = inp;
    }

    //ch4
    public String getPostEmgAvr4() {
        return PostEmgAvr4;
    }
    public void setPostEmgAvr4(String inp) {
        PostEmgAvr4 = inp;
    }
    public String getPostEmgMax4() {
        return PostEmgMax4;
    }
    public void setPostEmgMax4(String inp) {
        PostEmgMax4 = inp;
    }
    public String getPostEmgTotal4() {
        return PostEmgTotal4;
    }
    public void setPostEmgTotal4(String inp) {
        PostEmgTotal4 = inp;
    }
    //ch5
    public String getPostEmgAvr5() {
        return PostEmgAvr4;
    }
    public void setPostEmgAvr5(String inp) {
        PostEmgAvr5 = inp;
    }
    public String getPostEmgMax5() {
        return PostEmgMax5;
    }
    public void setPostEmgMax5(String inp) {
        PostEmgMax5 = inp;
    }
    public String getPostEmgTotal5() {
        return PostEmgTotal5;
    }
    public void setPostEmgTotal5(String inp) {
        PostEmgTotal5 = inp;
    }




    public String getProgramPulseOperationTimeProgress() {
        return ProgramPulseOperationTimeProgress;
    }

    public void setProgramPulseOperationTimeProgress(String programPulseOperationTimeProgress) {
        ProgramPulseOperationTimeProgress = programPulseOperationTimeProgress;
    }

    public String getProgramPulsePauseTimeProgress() {
        return ProgramPulsePauseTimeProgress;
    }

    public void setProgramPulsePauseTimeProgress(String programPulsePauseTimeProgress) {
        ProgramPulsePauseTimeProgress = programPulsePauseTimeProgress;
    }

    public String getProgramFrequencyProgress() {
        return ProgramFrequencyProgress;
    }

    public void setProgramFrequencyProgress(String programFrequencyProgress) {
        ProgramFrequencyProgress = programFrequencyProgress;
    }

    public String getProgramPulseWidthProgress() {
        return ProgramPulseWidthProgress;
    }

    public void setProgramPulseWidthProgress(String programPulseWidthProgress) {
        ProgramPulseWidthProgress = programPulseWidthProgress;
    }

    public String getProgramPulseRiseTimeProgress() {
        return ProgramPulseRiseTimeProgress;
    }

    public void setProgramPulseRiseTimeProgress(String programPulseRiseTimeProgress) {
        ProgramPulseRiseTimeProgress = programPulseRiseTimeProgress;
    }

    public String getIdx() {
        return Idx;
    }

    public void setIdx(String idx) {
        Idx = idx;
    }

    public String getProgramStartDate() {
        return ProgramStartDate;
    }

    public void setProgramStartDate(String programDate) {
        ProgramStartDate = programDate;
    }

    public String getProgramEndDate() {
        return ProgramEndDate;
    }

    public void setProgramEndDate(String programDate) {
        ProgramEndDate = programDate;
    }

    public String getProgramSignalType() {
        return ProgramSignalType;
    }

    public void setProgramSignalType(String programStartTime) {
        ProgramSignalType = programStartTime;
    }


    public String getProgramType() {
        return ProgramType;
    }

    public void setProgramType(String programType) {
        ProgramType = programType;
    }

    public String getProgramName() {
        return ProgramName;
    }

    public void setProgramName(String programName) {
        ProgramName = programName;
    }

    public String getProgramState() {
        return ProgramState;
    }

    public void setProgramState(String programState) {
        ProgramState = programState;
    }

    public String getProgramTime() {
        return ProgramTime;
    }

    public void setProgramTime(String programTime) {
        ProgramTime = programTime;
    }

    public String getProgramTimeProgress() {
        return ProgramTimeProgress;
    }

    public void setProgramTimeProgress(String programTimeProgress) {
        ProgramTimeProgress = programTimeProgress;
    }

    public String getProgramPulseOperationTime() {
        return ProgramPulseOperationTime;
    }

    public void setProgramPulseOperationTime(String programPulseOperationTime) {
        ProgramPulseOperationTime = programPulseOperationTime;
    }

    public String getProgramPulsePauseTime() {
        return ProgramPulsePauseTime;
    }

    public void setProgramPulsePauseTime(String programPulsePauseTime) {
        ProgramPulsePauseTime = programPulsePauseTime;
    }

    public String getProgramFrequency() {
        return ProgramFrequency;
    }

    public void setProgramFrequency(String programFrequency) {
        ProgramFrequency = programFrequency;
    }

    public String getProgramPulseWidth() {
        return ProgramPulseWidth;
    }

    public void setProgramPulseWidth(String programPulseWidth) {
        ProgramPulseWidth = programPulseWidth;
    }

    public String getProgramPulseRiseTime() {
        return ProgramPulseRiseTime;
    }

    public void setProgramPulseRiseTime(String programPulseRiseTime) {
        ProgramPulseRiseTime = programPulseRiseTime;
    }

}
