package com.sf.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sf.bluetoothcommunication.core.Pivot;
import com.sf.bluetoothcommunication.model.EventMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.sf.bluetoothcommunication.model.EventMsg.CODE_200;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_201;

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
        }
    }

    /**
     * 连接管理
     *
     * @param view
     */
    public void connectManager(View view) {
        startActivity(new Intent(this, ConfigActivity.class));
    }

    public void sendMessage(View view) {
        EditText editText = findViewById(R.id.etMessage);
        String msg = editText.getText().toString();
        byte[] data = msg.getBytes();
        Pivot.ConnectedThread connectThread = Pivot.getInstance().getConnectedThread();
        if (connectThread != null) {
            connectThread.write(data);
        } else {
            Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean supportEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsg(EventMsg eventMsg) {
        String msg = new String(eventMsg.getData());
        switch (eventMsg.getCode()) {
            case CODE_200://连接成功
                Toast.makeText(this, "已连接", Toast.LENGTH_SHORT).show();
                break;
            case CODE_201://连接失败
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                break;
            case 100:
                Toast.makeText(this, "收到消息：" + msg, Toast.LENGTH_SHORT).show();
                break;
            case 300:
                Toast.makeText(this, "消息发送成功", Toast.LENGTH_SHORT).show();
                break;
            case -300:
                Toast.makeText(this, "消息发送失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
