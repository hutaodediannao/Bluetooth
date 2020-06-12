package com.sf.bluetoothcommunication.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sf.bluetoothcommunication.R;
import com.sf.bluetoothcommunication.adapter.BaseRecyclerAdapter;
import com.sf.bluetoothcommunication.adapter.DeviceAdapter;
import com.sf.bluetoothcommunication.core.Pivot;
import com.sf.bluetoothcommunication.model.EventMsg;
import com.sf.bluetoothcommunication.model.ExtBluetoothDevice;
import com.sf.bluetoothcommunication.service.BluetoothService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sf.bluetoothcommunication.model.EventMsg.CODE_100;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_101;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_200;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_201;
import static com.sf.bluetoothcommunication.model.EventMsg.CODE_202;

/**
 * 姓名:胡涛
 * 工号:666
 * 创建日期:2020/6/11 0011 0:06
 * 功能描述:蓝牙配置页面
 */
public class ConfigActivity extends BaseActivity {

    private static final String TAG = "ConfigActivity";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_DISCOVER = 101;

    //已连接
    private RecyclerView connectRecyclerView;
    private DeviceAdapter mConnectedDeviceAdapter;
    private List<ExtBluetoothDevice> extConnectedBluetoothDeviceList = new ArrayList<>();

    //发现
    private DeviceAdapter mDeviceAdapter;
    private List<ExtBluetoothDevice> extBluetoothDeviceList = new ArrayList<>();
    private RecyclerView discoverRecyclerView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        //初始化基本UI
        initBaseUI();
        //点击事件
        initEvent();
        //注册监听设备搜索的广播
        registBroadcastReceiver();
        //打开蓝牙设备
        startBluetoothDevice();
    }

    /**
     * 此页面需要监听消息
     *
     * @return
     */
    @Override
    public boolean supportEventBus() {
        return true;
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        mConnectedDeviceAdapter.setmClickItemListener(new BaseRecyclerAdapter.ClickItemListener<ExtBluetoothDevice>() {
            private AlertDialog dialog;

            @Override
            public void onItemClick(final ExtBluetoothDevice device) {
                //断开设备
                if (dialog == null) {
                    dialog = new AlertDialog.Builder(ConfigActivity.this)
                            .setTitle("是否断开设备？")
                            .setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent service = new Intent(ConfigActivity.this, BluetoothService.class);
                                    service.putExtra(BluetoothService.KEY, device);
                                    service.setAction(BluetoothService.DIS_CONNECT);
                                    startService(service);
                                }
                            }).create();
                }
                dialog.show();
            }
        });
        mDeviceAdapter.setmClickItemListener(new BaseRecyclerAdapter.ClickItemListener<ExtBluetoothDevice>() {
            @Override
            public void onItemClick(ExtBluetoothDevice device) {
                bluetoothAdapter.cancelDiscovery();
                Intent service = new Intent(ConfigActivity.this, BluetoothService.class);
                service.putExtra(BluetoothService.KEY, device);
                service.setAction(BluetoothService.CONNECT);
                startService(service);
            }
        });
    }

    private void initBaseUI() {
        //发现加载框
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //已连接的设备列表
        connectRecyclerView = findViewById(R.id.connectRecyclerView);
        mConnectedDeviceAdapter = new DeviceAdapter(extConnectedBluetoothDeviceList, this);
        connectRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        connectRecyclerView.setAdapter(mConnectedDeviceAdapter);
        updateConnectedDeviceListUI();

        //发现列表初始化
        discoverRecyclerView = findViewById(R.id.discoverRecyclerView);
        mDeviceAdapter = new DeviceAdapter(extBluetoothDeviceList, this);
        discoverRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        discoverRecyclerView.setAdapter(mDeviceAdapter);
    }

    /**
     * 更新已连接UI列表
     */
    private void updateConnectedDeviceListUI() {
        extConnectedBluetoothDeviceList.clear();
        if (Pivot.getInstance().getCurrentConnectedDevice() != null) {
            extConnectedBluetoothDeviceList.add(Pivot.getInstance().getCurrentConnectedDevice());
        }
        mConnectedDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * 注册一个蓝牙设备搜索的广播
     */
    private void registBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//开始扫描
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//完成扫描
        this.registerReceiver(mReceiver, filter);
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    //开始扫描
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    //完成扫描
                    progressBar.setVisibility(View.GONE);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ExtBluetoothDevice extBluetoothDevice = new ExtBluetoothDevice(device);
                    extBluetoothDeviceList.add(extBluetoothDevice);
                    mDeviceAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

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
            Intent service = new Intent(ConfigActivity.this, BluetoothService.class);
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
                    Intent service = new Intent(ConfigActivity.this, BluetoothService.class);
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

    /**
     * 发现搜索设备
     *
     * @param view
     */
    public void discover(View view) {
        startDiscover();
    }

    /**
     * 开启扫描设备
     */
    private void startDiscover() {
        if (!bluetoothAdapter.isDiscovering()) {
            if (Build.VERSION.SDK_INT >= 6.0) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_DISCOVER);
            } else {
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    /**
     * 授权扫描蓝牙设备权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_DISCOVER: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    bluetoothAdapter.startDiscovery();
            }
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsg(EventMsg eventMsg) {
        switch (eventMsg.getCode()) {
            case CODE_202://连接中
                progressDialog = ProgressDialog.show(this, "设备连接", "连接中");//连接设备进度对话框
                break;
            case CODE_200://连接成功
                Toast.makeText(this, "已连接", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                progressDialog.dismiss();
                updateConnectedDeviceListUI();
                finish();
                break;
            case CODE_201://连接失败
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                if (progressDialog != null)
                progressDialog.dismiss();
                break;
            case CODE_100://收到远程设备的消息
                String msg = new String(eventMsg.getData());
                Toast.makeText(this, "收到消息：" + msg, Toast.LENGTH_SHORT).show();
                break;
            case CODE_101://远程设备断开连接
                updateConnectedDeviceListUI();
                break;
            default:
                break;
        }
    }

}
