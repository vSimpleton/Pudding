package com.pomelo.pudding.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-03
 * DESC: 微信支付工具类
 */

public final class WeChatPayUtil {

    private IWXAPI iwxapi; //微信支付api
    private WXPayBuilder builder;

    private WeChatPayUtil(WXPayBuilder builder) {
        this.builder = builder;
    }

    /**
     * 调起微信支付的方法,不需要在客户端签名
     */
    public void toWeChatPay(Context context) {
        iwxapi = WXAPIFactory.createWXAPI(context, null); //初始化微信api
        iwxapi.registerApp(builder.getAppId()); //注册appid  appid可以在开发平台获取

        //这里注意要放在子线程
        Runnable payRunnable = () -> {
            PayReq req = new PayReq(); //调起微信APP的对象
            //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
            req.appId = builder.getAppId();
            req.partnerId = builder.getPartnerId();
            req.prepayId = builder.getPrepayId();
            req.packageValue = builder.getPackageValue();
            req.nonceStr = builder.getNonceStr();
            req.timeStamp = builder.getTimeStamp();
            req.sign = builder.getSign();
            iwxapi.sendReq(req);//发送调起微信的请求
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 调起微信支付的方法,需要在客户端签名
     */
    public void toWeChatPayWithSign(Context context, String appid, final String key) {
        iwxapi = WXAPIFactory.createWXAPI(context, null); //初始化微信api
        iwxapi.registerApp(appid); //注册appid
        //这里注意要放在子线程
        Runnable payRunnable = () -> {
            PayReq request = new PayReq(); //调起微信APP的对象
            //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
            request.appId = builder.getAppId();
            request.partnerId = builder.getPartnerId();
            request.prepayId = builder.getPrepayId();
            request.packageValue = "Sign=WXPay";
            request.nonceStr = builder.getNonceStr();
            request.timeStamp = builder.getTimeStamp();
            request.sign = builder.getSign();
            //签名
            LinkedHashMap<String, String> signParams = new LinkedHashMap<>();
            signParams.put("appid", request.appId);
            signParams.put("noncestr", request.nonceStr);
            signParams.put("package", request.packageValue);
            signParams.put("partnerid", request.partnerId);
            signParams.put("prepayid", request.prepayId);
            signParams.put("timestamp", request.timeStamp);
            request.sign = genPackageSign(signParams, key);
            iwxapi.sendReq(request);//发送调起微信的请求
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 调起微信APP支付，签名
     * 生成签名
     */
    private String genPackageSign(LinkedHashMap<String, String> params, String key) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(key);

        String packageSign = getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    /**
     * md5加密
     *
     * @param buffer
     * @return
     */
    private String getMessageDigest(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static class WXPayBuilder {
        private String appId;
        private String partnerId;
        private String prepayId;
        private String packageValue;
        private String nonceStr;
        private String timeStamp;
        private String sign;

        public WeChatPayUtil build() {
            return new WeChatPayUtil(this);
        }

        public String getAppId() {
            return appId;
        }

        public WXPayBuilder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public WXPayBuilder setPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public String getPrepayId() {
            return prepayId;
        }

        public WXPayBuilder setPrepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }

        public String getPackageValue() {
            return packageValue;
        }

        public WXPayBuilder setPackageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }

        public String getNonceStr() {
            return nonceStr;
        }

        public WXPayBuilder setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public WXPayBuilder setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public String getSign() {
            return sign;
        }

        public WXPayBuilder setSign(String sign) {
            this.sign = sign;
            return this;
        }
    }


}
