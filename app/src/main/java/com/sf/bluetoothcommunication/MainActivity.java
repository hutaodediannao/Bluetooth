package com.sf.bluetoothcommunication;

import android.app.ProgressDialog;
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
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_202;

public class MainActivity extends BaseActivity {

    private TextView tvConnectManager, tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        tvConnectManager = findViewById(R.id.tvConnectManager);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
            tvConnectManager.setVisibility(View.GONE);
        } else {
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

    private ProgressDialog progressDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsg(EventMsg eventMsg) {
        switch (eventMsg.getCode()) {
            case CODE_202://连接中
                progressDialog = ProgressDialog.show(this, "设备连接", "连接中");
                break;
            case CODE_200://连接成功
                Toast.makeText(this, "已连接", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
                break;
            case CODE_201://连接失败
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
                break;
            case 100:
                String msg = Pivot.getInstance().getCurrentConnectedDevice().getBluetoothDevice().getName() + ":" + new String(eventMsg.getData());
                tvResult.append(msg + "\n");
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
