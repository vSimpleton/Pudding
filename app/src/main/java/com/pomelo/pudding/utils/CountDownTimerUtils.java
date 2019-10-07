package com.pomelo.pudding.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by Sherry on 2019/10/07
 * 发送验证码倒计时工具类
 */

public class CountDownTimerUtils extends CountDownTimer {
    private Button mBtn;
    private int mEnable;
    private int mDisable;

    public CountDownTimerUtils(long millisInFuture, long countDownInterval, Button btn, int enable, int disable) {
        super(millisInFuture, countDownInterval);
        mBtn = btn;
        mEnable = enable;
        mDisable = disable;
    }

    // 计时过程
    @Override
    public void onTick(long l) {
        // 防止计时过程中重复点击
        mBtn.setClickable(false);
        mBtn.setText(l / 1000 + "s后可重新获取");
        // 设置按钮背景色
        mBtn.setBackgroundResource(mDisable);
    }

    // 计时完毕的方法
    @Override
    public void onFinish() {
        // 重新给Button设置文字
        mBtn.setText("获取验证码");
        // 设置可点击
        mBtn.setClickable(true);
        // 设置按钮背景色
        mBtn.setBackgroundResource(mEnable);
    }
}
