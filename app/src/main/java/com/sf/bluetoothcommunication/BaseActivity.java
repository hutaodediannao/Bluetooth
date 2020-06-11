package com.sf.bluetoothcommunication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (supportEventBus()) {
            if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (supportEventBus()) {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
        super.onDestroy();
    }

    /**
     * 是否支持eventBus消息接收
     * @return
     */
    public boolean supportEventBus() {
        return false;
    }

}
