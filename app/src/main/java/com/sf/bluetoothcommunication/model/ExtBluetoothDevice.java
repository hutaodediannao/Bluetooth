package com.sf.bluetoothcommunication.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/11 0011 9:13
 * 功能描述:蓝牙设备model
 */
public class ExtBluetoothDevice implements Parcelable {

    private BluetoothDevice bluetoothDevice;
    private String type;//设备类型，例如：打印机，手机，手环，手表等等

    public ExtBluetoothDevice(BluetoothDevice bluetoothDevice, String type) {
        this.bluetoothDevice = bluetoothDevice;
        this.type = type;
    }

    public ExtBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    protected ExtBluetoothDevice(Parcel in) {
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        type = in.readString();
    }

    public static final Creator<ExtBluetoothDevice> CREATOR = new Creator<ExtBluetoothDevice>() {
        @Override
        public ExtBluetoothDevice createFromParcel(Parcel in) {
            return new ExtBluetoothDevice(in);
        }

        @Override
        public ExtBluetoothDevice[] newArray(int size) {
            return new ExtBluetoothDevice[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bluetoothDevice, flags);
        dest.writeString(type);
    }
}
