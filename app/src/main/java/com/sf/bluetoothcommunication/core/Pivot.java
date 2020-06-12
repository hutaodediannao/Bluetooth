package com.sf.bluetoothcommunication.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.sf.bluetoothcommunication.model.EventMsg;
import com.sf.bluetoothcommunication.model.ExtBluetoothDevice;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.sf.bluetoothcommunication.model.EventMsg.CODE_0;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_1;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_100;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_101;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_200;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_201;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_202;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/11 0011 21:40
 * 功能描述:通信中枢
 */
public class Pivot {

    private static final String TAG = "Pivot";
    private static final String NAME = "BluetoothApp";
    private static Pivot mPivot;
    private static final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private Pivot() {
        initThread();
    }

    /**
     * 线程统一关闭
     */
    private void initThread() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    public static Pivot getInstance() {
        if (mPivot == null) {
            synchronized (Pivot.class) {
                if (mPivot == null) {
                    mPivot = new Pivot();
                }
            }
        }
        return mPivot;
    }

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private AcceptThread acceptThread;
    private ExtBluetoothDevice currentConnectedDevice;

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    /**
     * 获取当前已连接的设备
     * @return
     */
    public ExtBluetoothDevice getCurrentConnectedDevice() {
        return currentConnectedDevice;
    }

    /**
     * 连接设备
     *
     * @param device
     */
    public void connect(ExtBluetoothDevice device) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        //开始重新创建连接线程开启连接任务
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    /**
     * 断开设备
     * @param device
     */
    public void disconnect(ExtBluetoothDevice device) {
        currentConnectedDevice = null;
        initThread();
    }

    /**
     * 服务端线程
     */
    public void startServerThread() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    /**
     * 作为客户端连接线程
     */
    private class ConnectThread extends Thread {

        private ExtBluetoothDevice device;
        private BluetoothSocket mSocket;

        public ConnectThread(ExtBluetoothDevice device) {
            this.device = device;
        }

        /**
         * 关闭连接
         */
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                Log.i(TAG, "run: 开始连接");
                EventBus.getDefault().post(new EventMsg(null, CODE_202));
                mSocket = device.getBluetoothDevice().createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));
                mSocket.connect();
                //未出异常，表示连接成功,开始监听任务
                connectedThread =  new ConnectedThread(mSocket);
                connectedThread.start();
            } catch (IOException e) {
                EventBus.getDefault().post(new EventMsg(null, CODE_201));
                try {
                    mSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 读写公用线程
     */
    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new EventMsg(null, CODE_201));
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            currentConnectedDevice = new ExtBluetoothDevice(socket.getRemoteDevice());
            EventBus.getDefault().post(new EventMsg(currentConnectedDevice, CODE_200));
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    if (bytes > 0) EventBus.getDefault().post(new EventMsg(buffer, CODE_100));
                } catch (IOException e) {
                    e.printStackTrace();
                    //连接已经断开
                    try {
                        currentConnectedDevice = null;
                        EventBus.getDefault().post(new EventMsg(buffer, CODE_101));
                        mmSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                EventBus.getDefault().post(new EventMsg("消息发送成功".getBytes(), 300));
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new EventMsg("消息发送失败".getBytes(), -300));
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 作为服务端监听线程
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(NAME, UUID.fromString(BLUETOOTH_UUID));
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new EventMsg(null, CODE_0));
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new EventMsg(null, CODE_0));
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    connectedThread = new ConnectedThread(socket);
                    connectedThread.start();
                    try {
                        mmServerSocket.close();
                        EventBus.getDefault().post(new EventMsg(null, CODE_1));
                    } catch (IOException e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new EventMsg(null, CODE_0));
                    }
                    break;
                } else {
                    //服务启动失败
                    EventBus.getDefault().post(new EventMsg(null, CODE_0));
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
