package com.sf.bluetoothcommunication.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.sf.bluetoothcommunication.R;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/13 0013 16:54
 * 功能描述:音乐播放工具类
 */
public class SoundPoolPlayer {

    private static SoundPoolPlayer instance;

    //设置描述音频流信息的属性
    AudioAttributes abs;

    SoundPool mSoundPoll;

    private SoundPoolPlayer() {
        abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build() ;
        mSoundPoll = new SoundPool.Builder()
                .setMaxStreams(100)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
    }

    public SoundPool getmSoundPoll() {
        return mSoundPoll;
    }

    public static SoundPoolPlayer getInstance() {
        if (instance == null) {
            synchronized (SoundPoolPlayer.class) {
                if (instance == null) {
                    instance = new SoundPoolPlayer();
                }
            }
        }
        return instance;
    }

    public void playerMusic(Context context, int soundResId) {
        mSoundPoll.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId,1,1,1,0,1);//播放
            }
        });
        mSoundPoll.load(context, soundResId,1);//加载资源
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mSoundPoll != null)
        mSoundPoll.release();
    }

}
