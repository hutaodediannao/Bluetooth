package com.sf.bluetoothcommunication.activity;

import android.os.Bundle;
import android.view.View;

import com.sf.bluetoothcommunication.R;

/**
 * 作者:胡涛
 * 日期:2020-6-18
 * 时间:16:19
 * 功能:菜单功能表
 */
public class MenuActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    /**
     * 去聊天页面
     * @param view
     */
    public void chat(View view) {
        startNext(MainActivity.class);
    }

    /**
     * 区间谍页面
     * @param view
     */
    public void spy(View view) {
        startNext(SpyActivity.class);
    }
}
