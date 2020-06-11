package com.sf.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sf.bluetoothcommunication.core.Pivot;

public class MainActivity extends BaseActivity {

    private TextView tvConnectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvConnectManager = findViewById(R.id.tvConnectManager);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
            tvConnectManager.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "支持蓝牙", Toast.LENGTH_SHORT).show();
            tvConnectManager.setVisibility(View.VISIBLE);

            //启动一个服务端监听的县城
            Pivot.getInstance().startServerThread();
        }
    }

    /**
     * 连接管理
     * @param view
     */
    public void connectManager(View view) {
        startActivity(new Intent(this, ConfigActivity.class));
    }
}
