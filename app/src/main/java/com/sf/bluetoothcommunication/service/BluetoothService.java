package com.sf.bluetoothcommunication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sf.bluetoothcommunication.core.Pivot;
import com.sf.bluetoothcommunication.model.EventMsg;
import com.sf.bluetoothcommunication.model.ExtBluetoothDevice;

import org.greenrobot.eventbus.EventBus;

import static com.sf.bluetoothcommunication.model.EventMsg.CODE_201;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/12 0012 14:49
 * 功能描述:蓝牙接收命令指派，有效防止内存泄漏的问题
 */
public class BluetoothService extends Service {

    public static final String KEY = "key";
    public static final String CONNECT = "connect";//连接
    public static final String DIS_CONNECT = "disconnect";//断开连接
    public static final String START_SERVER_LISTENER = "startServerListener";//开启服务端监听
    public static final String SEND_DATA = "sendData";//发送数据

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case CONNECT://连接到远程设备
                    ExtBluetoothDevice device0 = intent.getParcelableExtra(KEY);
                    Pivot.getInstance().connect(device0);
                    break;
                case DIS_CONNECT://断开远程设备连接
                    ExtBluetoothDevice device1 = intent.getParcelableExtra(KEY);
                    Pivot.getInstance().disconnect(device1);
                    break;
                case START_SERVER_LISTENER://开启服务端监听
                    Pivot.getInstance().startServerThread();
                    break;
                case SEND_DATA://发送数据
                    Pivot.ConnectedThread connectThread = Pivot.getInstance().getConnectedThread();
                    if (connectThread != null) {
                        byte[] data = intent.getByteArrayExtra(KEY);
                        connectThread.write(data);
                    } else {
                        EventBus.getDefault().post(new EventMsg(null, CODE_201));
                    }
                    break;
                default:
                    break;
            }

        return super.onStartCommand(intent, flags, startId);
    }
}
