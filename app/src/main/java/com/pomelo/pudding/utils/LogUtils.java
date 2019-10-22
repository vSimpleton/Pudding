package com.pomelo.pudding.utils;

import android.util.Log;

/**
 * Created by Sherry on 2019/9/30
 * 打印日志类
 */

public class LogUtils {

    private static String mTag = "youzi"; //日志输出时的TAG
    private static final boolean DEBUG = true; //是否允许输出log

    /** 以级别为 d 的形式输出LOG */
    public static void v(String msg) {
        if (DEBUG) {
            Log.v(mTag, msg);
        }
    }

    /** 以级别为 d 的形式输出LOG */
    public static void d(String msg) {
        if (DEBUG) {
            Log.d(mTag, msg);
        }
    }

    /** 以级别为 i 的形式输出LOG */
    public static void i(String msg) {
        if (DEBUG) {
            Log.i(mTag, msg);
        }
    }

    /** 以级别为 w 的形式输出LOG */
    public static void w(String msg) {
        if (DEBUG) {
            Log.w(mTag, msg);
        }
    }

    /** 以级别为 e 的形式输出LOG */
    public static void e(String msg) {
        if (DEBUG) {
            Log.e(mTag, msg);
        }
    }
}
