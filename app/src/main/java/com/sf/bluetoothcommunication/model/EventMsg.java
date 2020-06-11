package com.sf.bluetoothcommunication.model;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/11 0011 22:01
 * 功能描述:蓝牙通信消息封装
 */
public class EventMsg {

    public static final int RECEIVE_DEVICE_MSG_CODE = 100;//监听到远程蓝牙设备发送过来的消息

    private byte[] data;
    private int code;

    public EventMsg(byte[] data, int code) {
        this.data = data;
        this.code = code;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
