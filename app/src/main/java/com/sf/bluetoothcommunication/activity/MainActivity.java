package com.sf.bluetoothcommunication.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sf.bluetoothcommunication.R;
import com.sf.bluetoothcommunication.core.Pivot;
import com.sf.bluetoothcommunication.media.SoundPoolPlayer;
import com.sf.bluetoothcommunication.model.EventMsg;
import com.sf.bluetoothcommunication.service.BluetoothService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.sf.bluetoothcommunication.model.EventMsg.CODE_100;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_101;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_2;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_200;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_201;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_202;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_3;

public class MainActivity extends BaseActivity {

    private TextView tvConnectManager, tvResult, tvDeviceName;
    private EditText etMessage;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        //打开蓝牙设备
        startBluetoothDevice();
    }

    @Override
    protected void onDestroy() {
        SoundPoolPlayer.getInstance().release();
        super.onDestroy();
    }

    /**
     * 打开蓝牙设备
     */
    private void startBluetoothDevice() {
        if (!bluetoothAdapter.isEnabled()) {
            //蓝牙未开启，需要调用开启的api
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            //启动一个服务端监听的县城
            Intent service = new Intent(this, BluetoothService.class);
            service.setAction(BluetoothService.START_SERVER_LISTENER);
            startService(service);
        }
    }

    public void back(View view) {
        if (!isFinishing()) finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //启动一个服务端监听的县城
                    Intent service = new Intent(this, BluetoothService.class);
                    service.setAction(BluetoothService.START_SERVER_LISTENER);
                    startService(service);
                } else {
                    //表示蓝牙开启失败
                    Toast.makeText(this, "蓝牙开启失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void initUI() {
        tvResult = findViewById(R.id.tvResult);
        etMessage = findViewById(R.id.etMessage);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvConnectManager = findViewById(R.id.tvConnectManager);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
            tvConnectManager.setVisibility(View.GONE);
        } else {
            tvConnectManager.setVisibility(View.VISIBLE);
        }
        updateConnectedDeviceListUI();
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
        Intent service = new Intent(MainActivity.this, BluetoothService.class);
        service.putExtra(BluetoothService.KEY, data);
        service.setAction(BluetoothService.SEND_DATA);
        startService(service);
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
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.connecting);
                break;
            case CODE_200://连接成功
                Toast.makeText(this, "已连接", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
                updateConnectedDeviceListUI();
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.connect_ok);
                break;
            case CODE_201://连接失败
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
                updateConnectedDeviceListUI();
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.conect_fail);
                break;
            case CODE_100://接收到消息
                String msg = Pivot.getInstance().getCurrentConnectedDevice().getBluetoothDevice().getName() + ":" + new String(eventMsg.getData());
                tvResult.append(msg + "\n");
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.a);
                break;
            case CODE_2:
                Toast.makeText(this, "消息发送成功", Toast.LENGTH_SHORT).show();
                String sendMsg = etMessage.getText().toString();
                tvResult.append(sendMsg + "\n");
                etMessage.setText("");
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.b);
                break;
            case CODE_3:
                Toast.makeText(this, "消息发送失败", Toast.LENGTH_SHORT).show();
                break;
            case CODE_101://远程设备断开连接
                //更新UI
                updateConnectedDeviceListUI();
                //连接断开后，需要重新开启监听线程，否则无法再次连接
                if (bluetoothAdapter.isDiscovering())
                    bluetoothAdapter.cancelDiscovery();
                startBluetoothDevice();
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.disconnect);
                break;
            default:
                break;
        }
    }

    /**
     * 更新设备昵称显示
     */
    private void updateConnectedDeviceListUI() {
        if (Pivot.getInstance().getCurrentConnectedDevice() != null) {
            String deviceName = Pivot.getInstance().getCurrentConnectedDevice().getBluetoothDevice().getName();
            tvDeviceName.setText(deviceName);
        } else {
            tvDeviceName.setText("暂无设备连接");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
