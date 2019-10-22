package com.pomelo.pudding.utils;

import android.content.Context;

/**
 * Created by Sherry on 2019/10/22
 * 用于存储变量值，要是想存储对象，使用PageCacheUtils
 */

public class Configure {

    public static void setAccessToken(Context context, String token) {
        SharePreferenceUtils.put(context, "token", token);
    }

    public static String getAccessToken(Context context) {
        return (String) SharePreferenceUtils.get(context, "token", "");
    }

    public static void setUserId(Context context, String userId) {
        SharePreferenceUtils.put(context, "userid", userId);
    }

    public static String getUserId(Context context) {
        return (String) SharePreferenceUtils.get(context, "userid", "");
    }

    public static void setUserSex(Context context, String sex) {
        SharePreferenceUtils.put(context, "UserSex" + getUserId(context), sex);
    }

    public static String getUserSex(Context context) {
        return (String) SharePreferenceUtils.get(context, "UserSex" + getUserId(context), "");
    }

}
