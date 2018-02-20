package com.cellumed.healthcare.microrehab.knee.Bluetooth;

/**
 * CMD updated for K100 protocol v0.1
 */
// cmd is a hex value not to convert to fill the packet. ex) 01 => 0x01;

public interface IMP_CMD {
     String CMD_REQ_VER = "01";              // Device 버전 정보 요청
     String CMD_START_SENS = "11";           // 센서 데이터 전송 요청
     String CMD_STOP_SENS ="12";             // 센서 데이터 전송 중지
     String CMD_RESP_NOR_SENS ="13";         // 센서 이용 카운트 데이터
     String CMD_RESP_RAW_SENS ="14";         // Raw 센서 데이터
     String CMD_RESP_SENS_PST ="16";         // Posture 정보
     String CMD_REQ_REPORT_SENS ="15";       // 센서 Report 데이터 요청
     String CMD_REQ_START_EMS = "21";        // EMS 자극 시작
     String CMD_REQ_STOP_EMS ="22";          // EMS 자극 중지
     String CMD_EMS_INFO = "02";             // EMS Program 정보
     String CMD_EMS_LEVEL = "03";            // EMS Level 정보
     String CMD_REQ_BATT_INFO = "04";        // Device 배터리 정보
     String CMD_REQ_START_CAL = "31";        // 센서 calibration 요청
     String CMD_EMS_STATUS = "41";           // EMS 연결 상태 전송

     // error code
     /*
     String ERR_NONE_DATA = "00";
     String ERR_INVALID_LENGTH = "A0";
     String ERR_INVALID_CHECKSUM = "A1";
     String ERR_INVALID_PARAM = "A2";
     String ERR_INVALID_SEQ = "A3";
     String ERR_INVALID_ID = "A5";
     */

}
