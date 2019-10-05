package com.pomelo.pudding.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

/**
 * Created by Sherry on 2019/9/30
 * 系统软键盘相关的方法
 */

public class KeyBoardUtils {

    /**
     * 收起软键盘
     */
    public static void hideSoftInputFromWindow(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 弹出软键盘
     */
    public static void showSoftInputFromWindow(@NonNull Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * @return 是否显示软键盘
     */
    public static boolean isSoftInputShown(@NonNull Activity activity) {
        return getSupportSoftInputHeight(activity) > 0;
    }

    /**
     * @return 获取软键盘的高度
     */
    public static int getSupportSoftInputHeight(@NonNull Activity activity) {

        Rect r = new Rect();

        Window window = activity.getWindow();
        if (window == null) {
            return 0;
        }

        // decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
        // 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
        View decorView = window.getDecorView();
        if (decorView == null) {
            return 0;
        }
        decorView.getWindowVisibleDisplayFrame(r);

        // 获取屏幕的高度
        int screenHeight = decorView.getRootView().getHeight();

        // 计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;
        // 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
        // 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
        // 我们需要减去底部虚拟按键栏的高度（如果有的话）
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
        }
        if (softInputHeight < 0) {
            LogUtils.i("EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        return softInputHeight;
    }

    /**
     * @return 底部虚拟按键栏的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSoftButtonsBarHeight(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        // 这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        // 获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }
}
