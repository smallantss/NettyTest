package com.example.nettytest.userinterface;

public class TerminalDeviceInfo {
    public String patientName;
    public String patientAge;

    public TerminalDeviceInfo(){
        patientAge = "18";
        patientName = "";
    }

    public void Copy(TerminalDeviceInfo info){
        patientName = info.patientName;
        patientAge = info.patientAge;
    }
}
