package com.pomelo.pudding.utils;

import android.content.Context;

/**
 * Created by Sherry on 2019/10/22
 * 缓存的是页面对象，需要序列化
 */

public class PageCacheUtils {

    final static String MINE_INFO = "Mine_MineInfo";
    final static String OPUS_INFO = "OPUS_INFO";

    public static void saveMinePageCache(Context context, Object info) {
        SharePreferenceUtils.putObject(context, info, MINE_INFO + Configure.getUserId(context));
    }

    public static Object getMinePageCache(Context context) {
        return SharePreferenceUtils.getObject(context, MINE_INFO + Configure.getUserId(context));
    }

    public static void saveOpusInfoCache(Context context, Object info) {
        SharePreferenceUtils.putObject(context, info, OPUS_INFO + Configure.getUserId(context));
    }

    public static Object getOpusInfoCache(Context context) {
        return SharePreferenceUtils.getObject(context, OPUS_INFO + Configure.getUserId(context));
    }

}
