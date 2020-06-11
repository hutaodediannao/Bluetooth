package com.sf.bluetoothcommunication.core;

import android.bluetooth.BluetoothAdapter;
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
        if (connectThread != null) {
            connectThread.disconnect();
            connectThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
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

    /**
     * 连接设备
     *
     * @param device
     */
    public void connect(ExtBluetoothDevice device) {
        if (connectThread != null) {
            connectThread.disconnect();
            connectThread = null;
        }
        //开始重新创建连接线程开启连接任务
        connectThread = new ConnectThread(device);
        connectThread.start();
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

    private BluetoothSocket mSocket;

    /**
     * 作为客户端连接线程
     */
    private class ConnectThread extends Thread {
        private ExtBluetoothDevice device;

        public ConnectThread(ExtBluetoothDevice device) {
            this.device = device;
        }

        /**
         * 关闭连接
         */
        public void disconnect() {

        }

        @Override
        public void run() {
            super.run();
            try {
                mSocket = device.getBluetoothDevice().createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));
                Log.i(TAG, "run: 开始连接");
                mSocket.connect();
                //未出异常，表示连接成功,开始监听任务
                new ConnectedThread(mSocket).start();
            } catch (IOException e) {
                e.printStackTrace();
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
    private class ConnectedThread extends Thread {
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
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    if (bytes > 0) EventBus.getDefault().post(new EventMsg(buffer, 100));
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
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
            } catch (IOException e) {}
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    new ConnectedThread(socket).start();
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

}
