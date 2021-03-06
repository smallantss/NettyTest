package com.example.nettytest.backend.backendcall;

import android.os.Handler;
import android.os.Message;

import com.example.nettytest.backend.backendphone.BackEndPhone;
import com.example.nettytest.pub.HandlerMgr;
import com.example.nettytest.pub.LogWork;
import com.example.nettytest.pub.SystemSnap;
import com.example.nettytest.pub.phonecall.CommonCall;
import com.example.nettytest.pub.protocol.AnswerReqPack;
import com.example.nettytest.pub.protocol.AnswerResPack;
import com.example.nettytest.pub.protocol.EndReqPack;
import com.example.nettytest.pub.protocol.EndResPack;
import com.example.nettytest.pub.protocol.InviteReqPack;
import com.example.nettytest.pub.protocol.InviteResPack;
import com.example.nettytest.pub.protocol.ProtocolPacket;
import com.example.nettytest.pub.protocol.UpdateReqPack;
import com.example.nettytest.pub.protocol.UpdateResPack;
import com.example.nettytest.pub.transaction.Transaction;
import com.example.nettytest.userinterface.CallLogMessage;
import com.example.nettytest.userinterface.PhoneParam;
import com.example.nettytest.userinterface.UserInterface;
import com.example.nettytest.userinterface.UserMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BackEndCallConvergenceManager {

    HashMap<String, BackEndCallConvergence> callConvergenceList;
    Handler userMsgHandler = null;


    public BackEndCallConvergenceManager(){

        callConvergenceList = new HashMap<>();

    }

    public void SetUserMessageHandler(Handler h){
        userMsgHandler = h;
    }

    private void PostUserMessage(int type,Object obj){
        if(userMsgHandler!=null){
            Message msg = userMsgHandler.obtainMessage();
            msg.arg1 = type;
            msg.obj = obj;
            userMsgHandler.sendMessage(msg);
        }
    }


    public int GetCallCount(){
        return callConvergenceList.size();
    }

    public byte[] MakeCallConvergenceSnap(String devid){
        byte[] result = null;
        JSONObject json = new JSONObject();

        try {
            json.putOpt(SystemSnap.SNAP_CMD_TYPE_NAME, SystemSnap.SNAP_BACKEND_CALL_RES);
            json.putOpt(SystemSnap.SNAP_DEVID_NAME, devid);
            JSONArray outCalls = new JSONArray();
            JSONArray incomingCalls = new JSONArray();
            JSONObject calljson;
            for (BackEndCallConvergence callConvergence:callConvergenceList.values()) {
                if(callConvergence.inviteCall.caller.compareToIgnoreCase(devid)==0){
                    calljson = new JSONObject();
                    calljson.putOpt(SystemSnap.SNAP_CALLER_NAME,callConvergence.inviteCall.caller);
                    calljson.putOpt(SystemSnap.SNAP_CALLEE_NAME,callConvergence.inviteCall.callee);
                    calljson.putOpt(SystemSnap.SNAP_ANSWERER_NAME,callConvergence.inviteCall.answer);
                    calljson.putOpt(SystemSnap.SNAP_CALLID_NAME,callConvergence.inviteCall.callID);
                    calljson.putOpt(SystemSnap.SNAP_CALLSTATUS_NAME,callConvergence.inviteCall.state);
                    outCalls.put(calljson);
                }

                if(callConvergence.inviteCall.callee.compareToIgnoreCase(devid)==0||callConvergence.inviteCall.answer.compareToIgnoreCase(devid)==0){
                    calljson = new JSONObject();
                    calljson.putOpt(SystemSnap.SNAP_CALLER_NAME,callConvergence.inviteCall.caller);
                    calljson.putOpt(SystemSnap.SNAP_CALLID_NAME,callConvergence.inviteCall.callID);
                    calljson.putOpt(SystemSnap.SNAP_CALLSTATUS_NAME,callConvergence.inviteCall.state);
                    incomingCalls.put(calljson);
                }

                for(BackEndCall call:callConvergence.listenCallList){
                    if(call.devID.compareToIgnoreCase(devid)==0){
                        calljson = new JSONObject();
                        calljson.putOpt(SystemSnap.SNAP_CALLER_NAME,call.caller);
                        calljson.putOpt(SystemSnap.SNAP_CALLID_NAME,call.callID);
                        calljson.putOpt(SystemSnap.SNAP_CALLSTATUS_NAME,call.state);
                        incomingCalls.put(calljson);
                    }
                }
            }
            json.putOpt(SystemSnap.SNAP_OUTGOINGS_NAME,outCalls);
            json.putOpt(SystemSnap.SNAP_INCOMINGS_NAME,incomingCalls);
            result = json.toString().getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }

    private boolean CheckInviteEnable(BackEndPhone phone){
        boolean result = true;
        if(phone==null)
            return  false;
        if(!phone.isReg)
            return false;
        for(BackEndCallConvergence callConvergence:callConvergenceList.values()){
            if(!callConvergence.CheckInviteEnable(phone)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean CheckAnswerEnable(BackEndPhone phone,String callid){
        boolean result = true;
        if(phone == null)
            return false;
        if(!phone.isReg)
            return false;
        for(BackEndCallConvergence callConvergence:callConvergenceList.values()){
            if(!callConvergence.CheckAnswerEnable(phone,callid)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public boolean CheckForwardEnable(BackEndPhone phone,int callType){
        boolean result = true;
        if(phone == null)
            return false;
        if(!phone.isReg)
            return false;
        for(BackEndCallConvergence callConvergence:callConvergenceList.values()){
            if(!callConvergence.CheckForwardEnable(phone,callType)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean CheckInvitedEnable(BackEndPhone phone){
        boolean result = true;
        if(phone==null)
            return  false;
        if(!phone.isReg)
            return false;
        for(BackEndCallConvergence callConvergence:callConvergenceList.values()){
            if(!callConvergence.CheckInvitedEnable(phone)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public void ProcessSecondTick(){
        for(BackEndCallConvergence callConvergence:callConvergenceList.values()){
            callConvergence.ProcessSecondTick();
        }
    }


    public void ProcessPacket(ProtocolPacket packet){
        BackEndCallConvergence callConvergence;
        Transaction trans;
        int error;
        switch (packet.type) {
            case ProtocolPacket.CALL_REQ:
                InviteReqPack inviteReqPack = (InviteReqPack) packet;
                int  resultCode = ProtocolPacket.STATUS_OK;
                BackEndPhone caller = HandlerMgr.GetBackEndPhone(inviteReqPack.caller);
                BackEndPhone callee = HandlerMgr.GetBackEndPhone(inviteReqPack.callee);
                if(CheckInviteEnable(caller)) {

                    inviteReqPack.roomId = caller.devInfo.roomId;
                    inviteReqPack.bedName = caller.devInfo.bedName;
                    inviteReqPack.deviceName = caller.devInfo.deviceName;

                    if(inviteReqPack.callee.compareToIgnoreCase(PhoneParam.CALL_SERVER_ID)==0){
                        LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv %s Req from %s to %s",CommonCall.GetCallTypeName(inviteReqPack.callType),caller.id,PhoneParam.CALL_SERVER_ID);
                        if(inviteReqPack.callType== CommonCall.CALL_TYPE_BROADCAST){
                            callConvergence = new BackEndCallConvergence(caller,inviteReqPack);
                            callConvergenceList.put(inviteReqPack.callID,callConvergence);
                        }else{
                            callConvergence = new BackEndCallConvergence(caller,inviteReqPack);
                            callConvergenceList.put(inviteReqPack.callID,callConvergence);
                        }
                    }else if(CheckInvitedEnable(callee)){
                        LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv Call Req from %s to %s",caller.id,callee.id);
                        callConvergence = new BackEndCallConvergence(caller,callee,inviteReqPack);
                        callConvergenceList.put(inviteReqPack.callID,callConvergence);
                    }else{
                        if(callee==null){
                            LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_ERROR,"Server Could not Find callee DEV %s",inviteReqPack.callee);
                            resultCode = ProtocolPacket.STATUS_NOTFOUND;
                        }else {
                            LogWork.Print(LogWork.BACKEND_CALL_MODULE, LogWork.LOG_INFO, "Server Find DEV %s is not Reg or is Busy", inviteReqPack.callee);
                            resultCode = ProtocolPacket.STATUS_DECLINE;
                        }
                    }
                }else{
                    if(caller==null){
                        LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_ERROR,"Server Could not Find Caller DEV %s",inviteReqPack.caller);
                        resultCode = ProtocolPacket.STATUS_NOTFOUND;
                    }else {
                        LogWork.Print(LogWork.BACKEND_CALL_MODULE, LogWork.LOG_INFO, "Server Find DEV %s is not Reg or Busy", inviteReqPack.caller);
                        resultCode = ProtocolPacket.STATUS_DECLINE;
                    }
                }

                if(resultCode!=ProtocolPacket.STATUS_OK){
                    LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_WARN,"Server Reject Call From %s to %s for %s",inviteReqPack.caller,inviteReqPack.callee,ProtocolPacket.GetResString(resultCode));
                    InviteResPack inviteResPack = new InviteResPack(resultCode,inviteReqPack);
                    trans = new Transaction(inviteReqPack.caller,packet,inviteResPack,Transaction.TRANSCATION_DIRECTION_S2C);
                    HandlerMgr.AddBackEndTrans(packet.msgID, trans);
                }
                break;
            case ProtocolPacket.END_REQ:
                EndReqPack endReqPack = (EndReqPack)packet;
                LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv Call End From %s for Call %s",endReqPack.endDevID,endReqPack.callID);
                callConvergence = callConvergenceList.get(endReqPack.callID);
                
                if(callConvergence!=null) {
                    if(callConvergence.inviteCall.type==CommonCall.CALL_TYPE_BROADCAST) {
                        if(endReqPack.endDevID.compareToIgnoreCase(callConvergence.inviteCall.caller)==0||endReqPack.endDevID.compareToIgnoreCase(PhoneParam.CALL_SERVER_ID)==0){
                            LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server End Call %s",endReqPack.callID);
                            callConvergence.EndCall(endReqPack);
                            CallLogMessage  log = callConvergence.CreateCallLog();
                            PostUserMessage(UserMessage.MESSAGE_BACKEND_CALL_LOG,log);
                            callConvergenceList.remove(endReqPack.callID);
                            callConvergence.Release();
                        }else{
                            LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Remove %s From Call %s",endReqPack.endDevID,endReqPack.callID);
                            callConvergence.SingleEnd(endReqPack);
                        }
                    }else if(callConvergence.inviteCall.type==CommonCall.CALL_TYPE_NORMAL) {
                        LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server End Call %s",endReqPack.callID);
                        callConvergence.EndCall(endReqPack);
                        CallLogMessage  log = callConvergence.CreateCallLog();
                        PostUserMessage(UserMessage.MESSAGE_BACKEND_CALL_LOG,log);
                        callConvergenceList.remove(endReqPack.callID);
                        callConvergence.Release();
                    }
                }else{
                    LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_ERROR,"Server Recv Call End From %s for CallID %s, But Could not Find this Call",endReqPack.endDevID,endReqPack.callID);
                    EndResPack endResP = new EndResPack(ProtocolPacket.STATUS_NOTFOUND,endReqPack);
                    trans = new Transaction(endReqPack.sender,endReqPack,endResP,Transaction.TRANSCATION_DIRECTION_S2C);
                    HandlerMgr.AddBackEndTrans(endReqPack.msgID,trans);
                }
                break;
            case ProtocolPacket.ANSWER_REQ:
                AnswerReqPack answerReqPack  = (AnswerReqPack)packet;
                BackEndPhone answerPhone = HandlerMgr.GetBackEndPhone(answerReqPack.answerer);
                if(answerPhone!=null){
                    answerReqPack.answerBedName = answerPhone.devInfo.bedName;
                    answerReqPack.answerDeviceName = answerPhone.devInfo.deviceName;
                    answerReqPack.answerRoomId = answerPhone.devInfo.roomId;
                }
                callConvergence = callConvergenceList.get(answerReqPack.callID);
                LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv Call Answer From Dev %s for Call %s",answerReqPack.answerer,answerReqPack.callID);
                error = ProtocolPacket.STATUS_OK;
                if(callConvergence==null){
                    LogWork.Print(LogWork.BACKEND_CALL_MODULE, LogWork.LOG_ERROR, "Server Recv Call Answer From %s for CallID %s, But Could not Find this Call", answerReqPack.answerer, answerReqPack.callID);
                    error = ProtocolPacket.STATUS_NOTFOUND;
                }else{
                    if(callConvergence.inviteCall.callType==CommonCall.CALL_TYPE_BROADCAST){
                        callConvergence.AnswerBroadCall(answerReqPack);
                    }else{
                        if(CheckAnswerEnable(answerPhone,answerReqPack.callID)) {
                            callConvergence.AnswerCall(answerReqPack);
                        }else{
                            if(answerPhone==null) {
                                error = ProtocolPacket.STATUS_NOTFOUND;
                                LogWork.Print(LogWork.BACKEND_CALL_MODULE, LogWork.LOG_ERROR, "Server Could not Find DEV %s", answerReqPack.answerer);
                            }else{
                                error = ProtocolPacket.STATUS_FORBID;
                                LogWork.Print(LogWork.BACKEND_CALL_MODULE, LogWork.LOG_WARN, "Server Reject Answer From %s for call %s", answerReqPack.answerer, answerReqPack.callID);
                            }
                        }
                    }
                }
                if(error!=ProtocolPacket.STATUS_OK){
                    AnswerResPack answerResPack = new AnswerResPack(error,answerReqPack);
                    trans = new Transaction(answerReqPack.answerer,answerReqPack,answerResPack,Transaction.TRANSCATION_DIRECTION_S2C);
                    HandlerMgr.AddBackEndTrans(answerReqPack.msgID, trans);
                }
                break;
            case ProtocolPacket.CALL_UPDATE_REQ:
                UpdateReqPack updateReqP = (UpdateReqPack)packet;
                String callid = updateReqP.callId;
                callConvergence = callConvergenceList.get(callid);
                LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv Update From Dev %s for Call %s",updateReqP.devId,updateReqP.callId);
                if(callConvergence==null){
                    LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_WARN,"Server Could not Find Call %s",callid);
                    error = ProtocolPacket.STATUS_NOTFOUND;
                    UpdateResPack updateResP = new UpdateResPack(error,updateReqP);
                    trans = new Transaction(updateReqP.devId,updateReqP,updateResP,Transaction.TRANSCATION_DIRECTION_S2C);
                    HandlerMgr.AddBackEndTrans(updateReqP.msgID, trans);
                }else{
                    callConvergence.UpdateCall(updateReqP);
                }
                break;
            case ProtocolPacket.CALL_RES:
                InviteResPack inviteResPack = (InviteResPack)packet;
                LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv Call Res From %s for call %s",inviteResPack.sender, inviteResPack.callID);
                callConvergence = callConvergenceList.get(inviteResPack.callID);
                if(callConvergence!=null)
                    callConvergence.UpdateStatus(inviteResPack);
                else
                    LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_WARN,"Server Recv Call Res From %s for call %s, but could not Find this Call ",inviteResPack.sender, inviteResPack.callID);
                break;
            case ProtocolPacket.END_RES:
                EndResPack endResPack = (EndResPack)packet;
                LogWork.Print(LogWork.BACKEND_CALL_MODULE,LogWork.LOG_DEBUG,"Server Recv End Res From %s for call %s",endResPack.sender, endResPack.callId);
                break;
        }
    }

    public void ProcessTimeOver(ProtocolPacket packet){
        BackEndCallConvergence callConvergence;
        if (packet.type == ProtocolPacket.CALL_REQ) {
            InviteReqPack inviteReqPack = (InviteReqPack) packet;
            callConvergence = callConvergenceList.get(inviteReqPack.callID);
            if (callConvergence != null) {
                LogWork.Print(LogWork.BACKEND_CALL_MODULE, LogWork.LOG_DEBUG, "Server Send Call Req to Dev %s TimeOver for Call %s", packet.receiver, inviteReqPack.callID);
                callConvergence.InviteTimeOver(inviteReqPack.receiver);
            }
        }
        
    }
}

