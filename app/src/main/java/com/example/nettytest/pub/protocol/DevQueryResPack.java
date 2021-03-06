package com.example.nettytest.pub.protocol;

import com.example.nettytest.pub.commondevice.PhoneDevice;

import java.util.ArrayList;

public class DevQueryResPack extends ProtocolPacket {
    public int status;
    public String result;
    public ArrayList<PhoneDevice> phoneList;

    public DevQueryResPack(int status,DevQueryReqPack pack){
        ExchangeCopyData(pack);
        type = ProtocolPacket.DEV_QUERY_RES;
        phoneList = new ArrayList<>();
        this.status = status;
        result = ProtocolPacket.GetResString(status);
    }

    public DevQueryResPack(){
        super();
        type = ProtocolPacket.DEV_QUERY_RES;
        status = ProtocolPacket.STATUS_OK;
        result = ProtocolPacket.GetResString(status);
        phoneList = new ArrayList<>();
    }

    @Override
    public void Release() {
        super.Release();
        if(phoneList!=null){
//            phoneList.clear();
        }
    }
}
