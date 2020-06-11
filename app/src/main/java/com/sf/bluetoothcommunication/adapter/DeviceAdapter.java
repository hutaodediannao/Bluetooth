package com.sf.bluetoothcommunication.adapter;

import android.content.Context;

import com.sf.bluetoothcommunication.R;
import com.sf.bluetoothcommunication.model.ExtBluetoothDevice;

import java.util.List;

public class DeviceAdapter extends BaseRecyclerAdapter<ExtBluetoothDevice>{

    public DeviceAdapter(List<ExtBluetoothDevice> mList, Context context) {
        super(mList, context);
    }

    @Override
    void bindHolder(BaseRecyclerViewHolder holder, int position, ExtBluetoothDevice extBluetoothDevice) {
        holder.setText(R.id.tvName, extBluetoothDevice.getBluetoothDevice().getName())
                .setText(R.id.tvAddress, extBluetoothDevice.getBluetoothDevice().getAddress());
    }

    @Override
    int getLayoutId() {
        return R.layout.device_item_lay;
    }
}
