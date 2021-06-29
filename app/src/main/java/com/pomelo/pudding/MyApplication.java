package com.pomelo.pudding;

import android.app.Application;

/**
 * NAME: 柚子啊
 * DATE: 2020/8/10
 * DESC:
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
