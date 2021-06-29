package com.pomelo.pudding.event;

import android.view.View;

/**
 * NAME: 柚子啊
 * DATE: 2020/7/10
 * DESC: 在限定的时间内只能点击一次（解决快速点击多次的问题）
 */
public abstract class SingleClickListener implements View.OnClickListener {

    private static final long DURATION = 500; //两次点击的间隔时间
    private long mLastTime; //上一次点击的时间

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastTime > DURATION) {
            onSingleClick(v);
        }
        mLastTime = nowTime;
    }

    public abstract void onSingleClick(View view);

}
