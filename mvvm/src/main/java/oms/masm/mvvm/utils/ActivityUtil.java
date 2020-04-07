package oms.masm.mvvm.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: 管理所有Activity的实例
 */

public class ActivityUtil {

    private static Stack<Activity> stack;
    private static ActivityUtil mInstance;

    public static ActivityUtil getInstance() {
        if (mInstance == null) {
            synchronized (ActivityUtil.class) {
                if (mInstance == null) {
                    mInstance = new ActivityUtil();
                    stack = new Stack<>();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加Activity
     */
    public synchronized void addActivity(Activity activity) {
        stack.add(activity);
    }

    /**
     * 移除Activity
     */
    public synchronized void removeActivity(Activity activity) {
        stack.remove(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                return;
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            stack.remove(activity);
        }
    }

    /**
     * 是否存在某个Activity
     */
    public boolean containsActivity(Class<?> cls) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : stack) {
            if (activity != null) {
                activity.finish();
            }
        }
        stack.clear();
    }

}
