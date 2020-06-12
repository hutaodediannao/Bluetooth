package com.sf.bluetoothcommunication.model;

/**
 * 姓名:胡涛
 * 工号:804074
 * 创建日期:2020/6/11 11 22:01
 * 功能描述:蓝牙通信消息封装
 */
public class EventMsg {

    public final static int CODE_0 = 0;//监听服务启动失败
    public final static int CODE_1 = 1;//监听服务启动成功
    public final static int CODE_2 = 2;//消息发送成功
    public final static int CODE_3 = 3;//消息发送失败
    public final static int CODE_200 = 200;//连接成功
    public final static int CODE_201 = 201;//连接失败
    public final static int CODE_202 = 202;//连接中...
    public final static int CODE_100 = 100;//收到消息成功
    public final static int CODE_101 = 101;//连接断开

    private byte[] data;
    private Object object;
    private int code;

    public EventMsg(byte[] data, int code) {
        this.data = data;
        this.code = code;
    }

    public EventMsg(Object object, int code) {
        this.object = object;
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

    public Object getObject() {
        return object;
    }
}
