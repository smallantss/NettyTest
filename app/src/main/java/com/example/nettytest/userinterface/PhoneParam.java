package com.example.nettytest.userinterface;

import android.os.Environment;

import com.example.nettytest.terminal.audio.Rtp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class PhoneParam {
    final public static String CALL_SERVER_ID = "FFFFFFFF";
    final public static String BROAD_ADDRESS = "255.255.255.255";
    final public static int CLIENT_REG_EXPIRE = 60;

    final public static int INVITE_CALL_RTP_PORT = 9090;
    final public static int ANSWER_CALL_RTP_PORT = 9092;
    final public static int BROADCAST_CALL_RTP_PORT = 9094;

    final public static int DEFAULT_AEC_DELAY = 100;

    final public static int BROADCALL_ANSWER_WAIT = 3;
    
    final public static int BROADCALL_USE_UNICAST = 1;
    final public static int BROADCALL_USE_BROADCAST = 2;

    final static String JSON_SERVE_NAME = "server";
    final static String JSON_SERVER_ADDRESS_NAME = "address";
    final static String JSON_SERVER_PORT_NAME = "port";
    final static String JSON_SERVER_ACTIVE_NAME = "active";
    final static String JSON_SNAP_PORT_NAME = "snapPort";

    final static String JSON_CLIENT_NAME = "client";

    final static String JSON_DEVICES_NAME = "devices";
    final static String JSON_DEVICES_ID_NAME = "id";
    final static String JSON_DEVICE_TYPE_NAME = "type";

    public static ArrayList<UserDevice> devicesOnServer = new ArrayList<>();
    public static ArrayList<UserDevice> deviceList = new ArrayList<>();

    public static int callServerPort = 10002;
    public static String callServerAddress = "127.0.0.1";
    public static boolean serverActive = false;
    public static int broadcallCastMode = BROADCALL_USE_BROADCAST;
    
    public static int aecDelay = DEFAULT_AEC_DELAY;
    public static int callRtpCodec = Rtp.RTP_CODEC_711A;
    public static int callRtpPTime = 20;
    public static int callRtpDataRate = 8000;

    public static int snapStartPort = 11005;

    static void InitServerAndDevicesConfig(String info){
        JSONObject json;
        JSONObject serverJson;
        JSONObject clientJson;
        JSONArray devicesJson;
        JSONObject device;
        UserDevice userdev;
        int iTmp;
        try {
            json = new JSONObject(info);
            snapStartPort = json.getInt(JSON_SNAP_PORT_NAME);
            serverJson = json.getJSONObject(JSON_SERVE_NAME);
            clientJson = json.getJSONObject(JSON_CLIENT_NAME);
            callServerPort = serverJson.optInt(JSON_SERVER_PORT_NAME);
            serverActive = serverJson.optBoolean(JSON_SERVER_ACTIVE_NAME);
            devicesJson = serverJson.getJSONArray(JSON_DEVICES_NAME);
            devicesOnServer.clear();
            for(iTmp=0;iTmp<devicesJson.length();iTmp++){
                device = devicesJson.getJSONObject(iTmp);
                userdev = new UserDevice();
                userdev.devid = device.optString(JSON_DEVICES_ID_NAME);
                userdev.type = UserInterface.GetDeviceType(device.optString(JSON_DEVICE_TYPE_NAME));
                devicesOnServer.add(userdev);
            }

            callServerPort = clientJson.optInt(JSON_SERVER_PORT_NAME);
            callServerAddress = clientJson.optString(JSON_SERVER_ADDRESS_NAME);
            devicesJson = clientJson.getJSONArray(JSON_DEVICES_NAME);
            deviceList.clear();
            for (iTmp = 0; iTmp < devicesJson.length(); iTmp++) {
                device = devicesJson.getJSONObject(iTmp);
                userdev = new UserDevice();
                userdev.devid = device.optString(JSON_DEVICES_ID_NAME);
                userdev.type = UserInterface.GetDeviceType(device.optString(JSON_DEVICE_TYPE_NAME));
                deviceList.add(userdev);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void InitPhoneParam(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File configFile = new File(Environment.getExternalStorageDirectory(), "devConfig.conf");
//            File configFile = new File("/storage/self/primary/", "devConfig.conf");
            try {
                if (configFile.exists()) {
                    FileInputStream finput = new FileInputStream(configFile);
                    int len = finput.available();
                    byte[] data = new byte[len];
                    int readlen = finput.read(data);
                    finput.close();
                    if(readlen>0) {
                        String config = new String(data, "UTF-8");
                        InitServerAndDevicesConfig(config);
                    }
                } else {
                    FileOutputStream foutput = new FileOutputStream(configFile);
                    String config = "hello";
                    byte[] data = config.getBytes();
                    foutput.write(data);
                    foutput.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String GetLocalAddress(){
        String  address = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        address =  inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException ex){
            ex.printStackTrace();
        }

        return address;
    }

}
