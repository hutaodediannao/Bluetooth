package com.sf.bluetoothcommunication.model;

import android.bluetooth.BluetoothDevice;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/11 0011 9:13
 * 功能描述:蓝牙设备model
 */
public class ExtBluetoothDevice {

    private BluetoothDevice bluetoothDevice;
    private String type;//设备类型，例如：打印机，手机，手环，手表等等

    public ExtBluetoothDevice(BluetoothDevice bluetoothDevice, String type) {
        this.bluetoothDevice = bluetoothDevice;
        this.type = type;
    }

    public ExtBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
