package com.cellumed.healthcare.microrehab.knee.Bluetooth;

/**
 * CMD updated for K100 protocol v0.1
 */
// cmd is a hex value not to convert to fill the packet. ex) 01 => 0x01;

public interface IMP_CMD {
     String CMD_REQ_VER = "01";
     String CMD_START_SENS = "11";
     String CMD_STOP_SENS ="12";
     String CMD_RESP_NOR_SENS ="13";
     String CMD_RESP_RAW_SENS ="14";
     String CMD_RESP_SENS_PST ="16";
     String CMD_REQ_REPORT_SENS ="15";
     String CMD_REQ_START_EMS = "21";
     String CMD_REQ_STOP_EMS ="22";
     String CMD_EMS_INFO = "02";
     String CMD_EMS_LEVEL = "03";
     String CMD_REQ_BATT_INFO = "04";
     String CMD_REQ_START_CAL = "31";
     String CMD_EMS_STATUS = "41";

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
