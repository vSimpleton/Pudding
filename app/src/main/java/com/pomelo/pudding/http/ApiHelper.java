package com.pomelo.pudding.http;

import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.pomelo.pudding.BuildConfig;
import com.pomelo.pudding.utils.Configure;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Sherry on 2019/11/28
 * 存放各种网络请求url
 */

public class ApiHelper {

    public static final String BASE_URL = "https://rest.shanbay.com/";

    public static final String GET_DAILY = "api/v2/quote/quotes/today/";

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

//    private static final MediaType JSON_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");


    /**
     * 当使用JSONObject POST 构造参数时使用该方法把参数转成RequestBody
     *
     * @param paramJson -
     * @return -
     */
//    public static Map<String, RequestBody> getPostParams(JSONObject paramJson) {
//        JSONObject object = getParams(paramJson);
//        RequestBody requestBody = RequestBody.create(JSON_TYPE, object.toString());
//        Map<String, RequestBody> map = new HashMap<>();
//        map.put("req", requestBody); //“req”字段由后端定义
//        return map;
//    }


    /**
     * 当使用JSONObject 时构造GET请求参数
     *
     * @param paramJson -
     * @return -
     */
    public static String getGetParams(JSONObject paramJson) {
//        JSONObject object = getParams(paramJson);
//        return Base64.encode(object.toString().getBytes()); //使用base64
        return null;
    }


    /**
     * post请求，后面需要传入需要添加的请求参数
     *
     * @param param 参数map
     * @return -
     */
    public static HashMap<String, Object> getPostParams(HashMap<String, Object> param) {
        HashMap<String, Object> object = getParams(param);
        return object;
    }


    /**
     * 构造请求参数，需要使用表单提交时使用HashMap构造参数，当需要使用json提交时使用JSONObject构造参数
     *
     * @param param 参数
     * @return -
     */
    private static HashMap<String, Object> getParams(HashMap<String, Object> param) {
        HashMap<String, Object> paramJson = new HashMap<>();
        if (BuildConfig.DEBUG) {
            Log.i("params", "requestParam:" + paramJson.toString() + "\n");
        }

        //在这里写需要构造的公共参数
        paramJson.put("userId", "123456789");

        paramJson.putAll(param);
        return paramJson;

    }

}
