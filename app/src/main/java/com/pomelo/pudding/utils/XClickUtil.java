package com.pomelo.pudding.utils;

import android.view.View;

/**
 * NAME: 柚子啊
 * DATE: 2020/7/10
 * DESC: 重复点击判断工具类
 */
public class XClickUtil {

    /**
     * 最近一次点击的时间
     */
    private static long mLastClickTime;

    /**
     * 最近一次点击的控件ID
     */
    private static int mLastClickViewId;

    /**
     * 是否是快速点击
     *
     * @param v              点击的控件
     * @param intervalMillis 时间间期（毫秒）
     * @return true:是，false:不是
     */
    public static boolean isFastDoubleClick(View v, long intervalMillis) {
        int viewId = v.getId();
        long time = System.currentTimeMillis();
        long timeInterval = Math.abs(time - mLastClickTime);
        if (timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true;
        } else {
            mLastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }

}
