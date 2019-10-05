package com.pomelo.pudding.utils;

import android.util.Log;

/**
 * Created by Sherry on 2019/9/30
 * 打印日志类
 */

public class LogUtils {

    public static final int LEVEL_VERBOSE = 0; //日志输出级别V
    public static final int LEVEL_DEBUG = 1; //日志输出级别D
    public static final int LEVEL_INFO = 2; //日志输出级别I
    public static final int LEVEL_WARN = 3; //日志输出级别W
    public static final int LEVEL_ERROR = 4; //日志输出级别E

    private static String mTag = "youzi"; //日志输出时的TAG
    private static int mDebuggable = 5; //是否允许输出log

    /** 以级别为 d 的形式输出LOG */
    public static void v(String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, msg);
        }
    }

    /** 以级别为 d 的形式输出LOG */
    public static void d(String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, msg);
        }
    }

    /** 以级别为 i 的形式输出LOG */
    public static void i(String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, msg);
        }
    }

    /** 以级别为 w 的形式输出LOG */
    public static void w(String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, msg);
        }
    }

    /** 以级别为 e 的形式输出LOG */
    public static void e(String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, msg);
        }
    }
}
