package com.example.nettytest.userinterface;

import android.os.Handler;

import com.example.nettytest.backend.backendphone.BackEndConfig;
import com.example.nettytest.backend.backendphone.BackEndPhone;
import com.example.nettytest.backend.callserver.DemoServer;
import com.example.nettytest.pub.HandlerMgr;
import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.phonecall.CommonCall;
import com.example.nettytest.pub.protocol.ProtocolPacket;
import com.example.nettytest.terminal.terminalphone.TerminalPhone;

import java.util.ArrayList;

public class UserInterface {
    public final static int CALL_BED_DEVICE = 2;
    public final static int CALL_DOOR_DEVICE = 6;
    public final static int CALL_NURSER_DEVICE = 9;
    public final static int CALL_TV_DEVICE = 8;
    public final static int CALL_CORRIDOR_DEVICE = 7;
    public final static int CALL_EMERGENCY_DEVICE = 4;
    public final static int CALL_DOOR_LIGHT_DEVICE = 5;
    public final static int CALL_WHITE_BOARD_DEVICE = 10;
    public final static int CALL_SERVER_DEVICE = 90;
    public final static int CALL_UNKNOW_DEVICE = 100;

    public final static int CALL_NORMAL_TYPE = 1;
    public final static int CALL_EMERGENCY_TYPE = 2;
    public final static int CALL_BROADCAST_TYPE = 3;

    public final static int CALL_DIRECTION_S2C = 1;
    public final static int CALL_DIRECTION_C2S = 2;

    public final static int CALL_ANSWER_MODE_STOP = 1;
    public final static int CALL_ANSWER_MODE_HANDLE = 2;
    public final static int CALL_ANSWER_MODE_ANSWER = 3;

    public static DemoServer callServer=null;

    // backend
    public static void StartServer(){
        callServer = new DemoServer(PhoneParam.callServerPort);
    }

    public static OperationResult ConfigDeviceParamOnServer(String id, ArrayList<UserConfig>list){
        OperationResult result = new OperationResult();

        if(!HandlerMgr.SetBackEndPhoneConfig(id,list)){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTFOUND;
        }
        return result;
    }

    public static OperationResult ConfigSystemParamOnServer(ArrayList<UserConfig>list){
        OperationResult result = new OperationResult();

        if(!HandlerMgr.SetBackEndSystemConfig(list)){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTFOUND;
        }
        return result;
        
    }

    public static OperationResult ConfigServerParam(BackEndConfig config){
        OperationResult result = new OperationResult();
        HandlerMgr.SetBackEndConfig(config);
        return result;
    }

    public static OperationResult RemoveDeviceOnServer(String id){
        OperationResult result = new OperationResult();
        if(!HandlerMgr.RemoveBackEndPhone(id)){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTFOUND;
            PrintLog("Remove Device %s From Server Fail",id);
        }else{
            PrintLog("Remove Device %s From Server Success",id);
        }
        return result;

    }

    
    public static OperationResult RemoveAllDeviceOnServer(){
        OperationResult result = new OperationResult();

        if(!HandlerMgr.RemoveAllBackEndPhone()){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTFOUND;
            PrintLog("Remove All Devices From Server Fail");
        }else{
            PrintLog("Remove All Devices From Server Success");
        }
        return result;

    }

    public static OperationResult ConfigDeviceInfoOnServer(String id, ServerDeviceInfo info){
        OperationResult result = new OperationResult();

        if(!HandlerMgr.SetBackEndPhoneInfo(id,info)){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTFOUND;
        }
        return result;

    }

    public static OperationResult AddDeviceOnServer(String id,int type){
        OperationResult result = new OperationResult();
        int typeInServer = CALL_BED_DEVICE;

        switch(type){
            case CALL_BED_DEVICE:
                typeInServer = BackEndPhone.BED_CALL_DEVICE;
                break;
            case CALL_DOOR_DEVICE:
                typeInServer = BackEndPhone.DOOR_CALL_DEVICE;
                break;
            case CALL_NURSER_DEVICE:
                typeInServer = BackEndPhone.NURSE_CALL_DEVICE;
                break;
            case CALL_TV_DEVICE:
                typeInServer = BackEndPhone.TV_CALL_DEVICE;
                break;
            case CALL_CORRIDOR_DEVICE:
                typeInServer = BackEndPhone.CORRIDOR_CALL_DEVICE;
                break;
            case CALL_DOOR_LIGHT_DEVICE:
                typeInServer = BackEndPhone.DOOR_LIGHT_CALL_DEVICE;
                break;
            case CALL_WHITE_BOARD_DEVICE:
                typeInServer = BackEndPhone.WHITE_BOARD_DEVICE;
                break;
            case CALL_EMERGENCY_DEVICE:
                typeInServer = BackEndPhone.EMER_CALL_DEVICE;
                break;
        }

        if(!callServer.AddBackEndPhone(id, typeInServer)){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_UNKNOW;
        }
        return result;
    }

    public static ArrayList<UserDevice> GetDeviceInfoOnServer(){
        ArrayList<UserDevice> devLists = new ArrayList<>();
        HandlerMgr.GetBackEndPhoneInfo(devLists);
        return devLists;
    }


    public static boolean SetBackEndMessageHandler(Handler handler){
        HandlerMgr.SetBackEndlMessageHandler(handler);
        return true;
    }
    

    //Terminal

    public static boolean SetMessageHandler(Handler handler){
        HandlerMgr.SetTerminalMessageHandler(handler);
        return true;
    }


    public static OperationResult BuildDevice(int type, String ID){
        OperationResult result = new OperationResult();
        switch(type) {
            case CALL_BED_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.BED_CALL_DEVICE);
                break;
            case CALL_NURSER_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.NURSE_CALL_DEVICE);
                break;
            case CALL_DOOR_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.DOOR_CALL_DEVICE);
                break;
            case CALL_TV_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.TV_CALL_DEVICE);
                break;
            case CALL_CORRIDOR_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.CORRIDOR_CALL_DEVICE);
                break;
            case CALL_WHITE_BOARD_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.WHITE_BOARD_DEVICE);
                break;
            case CALL_EMERGENCY_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.EMER_CALL_DEVICE);
                break;
            case CALL_DOOR_LIGHT_DEVICE:
                HandlerMgr.CreateTerminalPhone(ID, TerminalPhone.DOOR_LIGHT_CALL_DEVICE);
                break;
            default:
                result.result = OperationResult.OP_RESULT_FAIL;
                result.reason = FailReason.FAIL_REASON_NOTSUPPORT;
                break;

        }
        return result;
    }

    public static OperationResult RemoveDevice(String id){
        OperationResult result = new OperationResult();
        HandlerMgr.RemoveTerminalPhone(id);
        return result;
    }

    public static OperationResult BuildCall(String id, String peerId, int type){
        OperationResult result = new OperationResult();
        String callid;

        int terminamCallType;
        switch(type){
            case CALL_EMERGENCY_TYPE:
                terminamCallType = CommonCall.CALL_TYPE_EMERGENCY;
                break;
            case CALL_BROADCAST_TYPE:
                terminamCallType = CommonCall.CALL_TYPE_BROADCAST;
                break;
            default:
                terminamCallType = CommonCall.CALL_TYPE_NORMAL;
                break;
        }
        callid = HandlerMgr.BuildTerminalCall(id, peerId,terminamCallType);
        if(callid!=null){
            result.callID = callid;
            LogWork.Print(LogWork.DEBUG_MODULE,LogWork.LOG_DEBUG,"Create Call From %s to %s Success, CallID = %s",id,peerId,callid);
        }else{
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTSUPPORT;
            LogWork.Print(LogWork.DEBUG_MODULE,LogWork.LOG_DEBUG,"Create Call From %s to %s Fail, Reason is %s",id,peerId,FailReason.GetFailName(result.reason));
        }

        return result;
    }

    public static OperationResult EndCall(String devid, String callid){
        int operationCode;
        OperationResult result;

        operationCode = HandlerMgr.EndTerminalCall(devid,callid);
        result = new OperationResult(operationCode);

        if(operationCode== ProtocolPacket.STATUS_OK)
            LogWork.Print(LogWork.DEBUG_MODULE,LogWork.LOG_DEBUG,"End Call %s by %s success",callid,devid);
        else
            LogWork.Print(LogWork.DEBUG_MODULE,LogWork.LOG_DEBUG,"End Call %s by %s Fail, Reason is %s",callid,devid,FailReason.GetFailName(result.reason));

        result.callID = callid;
        return result;
    }

    public static OperationResult AnswerCall(String devid, String callid){
        int operationCode;
        OperationResult result;

        operationCode = HandlerMgr.AnswerTerminalCall(devid,callid);
        result = new OperationResult(operationCode);
        result.callID = callid;
        if(operationCode== ProtocolPacket.STATUS_OK)
            LogWork.Print(LogWork.DEBUG_MODULE,LogWork.LOG_DEBUG,"Answer Call %s by %s success",callid,devid);
        else
            LogWork.Print(LogWork.DEBUG_MODULE,LogWork.LOG_DEBUG,"Anwser Call %s by %s Fail, Reason is %s",callid,devid,FailReason.GetFailName(result.reason));
        return result;
    }

    public static OperationResult QueryDevs(String devid){
        int operationCode;
        OperationResult result;

        operationCode = HandlerMgr.QueryTerminalLists(devid);
        result = new OperationResult(operationCode);

        return result;
    }

    public static OperationResult QueryDevConfig(String devid){
        int operationCode;
        OperationResult result;

        operationCode = HandlerMgr.QueryTerminalConfig(devid);
        result = new OperationResult(operationCode);

        return result;

    }

    public static OperationResult QuerySystemConfig(String devid){
        int operationCode;
        OperationResult result;

        operationCode = HandlerMgr.QuerySystemConfig(devid);
        result = new OperationResult(operationCode);

        return result;

    }


    public static OperationResult SetDevInfo(String devid,TerminalDeviceInfo info){
        OperationResult result = new OperationResult();
        if(!HandlerMgr.SetTerminalPhoneConfig(devid,info)){
            result.result = OperationResult.OP_RESULT_FAIL;
            result.reason = FailReason.FAIL_REASON_NOTFOUND;
        }
        return result;
    }

    public static boolean PrintLog(String f,Object...param){
        LogWork.Print(LogWork.TERMINAL_USER_MODULE,LogWork.LOG_DEBUG,f,param);
        return true;
    }

    public static boolean PrintLog(String f){
        LogWork.Print(LogWork.TERMINAL_USER_MODULE,LogWork.LOG_DEBUG,f);
        return true;
    }

    public static String GetDeviceTypeName(int type){
        String name = "";

        switch (type){
            case CALL_BED_DEVICE:
                name = "bed";
                break;
            case CALL_DOOR_DEVICE:
                name = "door";
                break;
            case CALL_NURSER_DEVICE:
                name = "nurser";
                break;
            case CALL_TV_DEVICE:
                name = "TV";
                break;
            case CALL_EMERGENCY_DEVICE:
                name = "emergency";
                break;
            case CALL_DOOR_LIGHT_DEVICE:
                name = "door light";
                break;
            case CALL_WHITE_BOARD_DEVICE:
                name = "white board";
                break;
            case CALL_CORRIDOR_DEVICE:
                name = "corridor";
                break;
        }

        return name;
    }

    public static int GetDeviceType(String name){
        int type = CALL_BED_DEVICE;

        if(name.compareToIgnoreCase("bed")==0)
            type = CALL_BED_DEVICE;
        else if(name.compareToIgnoreCase("door")==0)
            type = CALL_DOOR_DEVICE;
        else if(name.compareToIgnoreCase("nurser")==0)
            type = CALL_NURSER_DEVICE;
        else if(name.compareToIgnoreCase("TV")==0)
            type = CALL_TV_DEVICE;
        else if(name.compareToIgnoreCase("emergency")==0)
            type = CALL_EMERGENCY_DEVICE;
        else if(name.compareToIgnoreCase("doorLight")==0)
            type = CALL_DOOR_LIGHT_DEVICE;
        else if(name.compareToIgnoreCase("corridor")==0)
            type = CALL_CORRIDOR_DEVICE;
        else if(name.compareToIgnoreCase("whiteBoard")==0)
            type = CALL_WHITE_BOARD_DEVICE;
        return type;
    }
}
