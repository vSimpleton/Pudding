package com.pomelo.pudding.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * NAME: 柚子啊
 * DATE: 2020/4/3
 * Describe: 支付宝支付工具类
 */
public class AliPayUtil {

    private static final int SDK_PAY_FLAG = 1;
    private Activity context;
    private ALiPayBuilder builder;

    private AliPayUtil(ALiPayBuilder builder) {
        this.builder = builder;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "8000") || TextUtils.equals(resultStatus, "6004")) {
                        Toast.makeText(context, "正在处理中", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "4000")) {
                        Toast.makeText(context, "订单支付失败", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "5000")) {
                        Toast.makeText(context, "重复请求", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        Toast.makeText(context, "已取消支付", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.equals(resultStatus, "6002")) {
                        Toast.makeText(context, "网络连接出错", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    /**
     * 签名发在客户端来做。
     *
     * @param context
     */
    public void toAliPay(final Activity context) {
        this.context = context;
        boolean rsa2 = (builder.getRsa2().length() > 0);

        Map<String, String> params = buildOrderParamMap(rsa2);
        String orderParam = buildOrderParam(params);
        String privateKey = rsa2 ? builder.getRsa2() : builder.getRsa();
        String sign = getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign; //自己拼接orderInfo

        Runnable payRunnable = () -> {
            PayTask task = new PayTask(context);
            Map<String, String> result = task.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 签名在服务端来做
     *
     * @param context
     * @param orderInfo 由服务器提供
     */
    public void toAliPay(final Activity context, final String orderInfo) {
        this.context = context;
        Runnable payRunnable = () -> {
            PayTask task = new PayTask(context);
            Map<String, String> result = task.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //-----------------------以下为拼接orderInfo的代码-----------------------
    /**
     * 构造支付订单参数列表
     */
    private Map<String, String> buildOrderParamMap(boolean rsa2) {
        Map<String, String> keyValues = new HashMap<>();

        keyValues.put("app_id", builder.appid);
        keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"" + builder.money + "\",\"subject\":\"" + builder.title + "\",\"out_trade_no\":\"" + getOutTradeNo() + "\"}");
        keyValues.put("charset", "utf-8");
        keyValues.put("method", "alipay.trade.app.pay");
        //回调接口
        keyValues.put("notify_url", builder.getNotifyUrl());
        keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");
        keyValues.put("timestamp", getCurrentTimeString());
        keyValues.put("version", "1.0");

        return keyValues;
    }

    /**
     * 获取当前日期字符串
     *
     * @return
     */
    private String getCurrentTimeString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     * @return
     */
    private String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 对支付参数信息进行签名
     *
     * @param map 待签名授权信息
     * @return
     */
    private String getSign(Map<String, String> map, String rsaKey, boolean rsa2) {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = sign(authInfo.toString(), rsaKey, rsa2);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }

    private String sign(String content, String privateKey, boolean rsa2) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(getAlgorithms(rsa2));

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 要求外部订单号必须唯一。
     * @return
     */
    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    private static final String ALGORITHM = "RSA";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private String getAlgorithms(boolean rsa2) {
        return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
    }

    public static class ALiPayBuilder {
        private String rsa2 = "";
        private String rsa = "";
        private String appid;
        private String money;
        private String title;
        private String notifyUrl;
        private String orderTradeId;

        public AliPayUtil build() {
            return new AliPayUtil(this);
        }

        public String getOrderTradeId() {
            return orderTradeId;
        }

        public ALiPayBuilder setOrderTradeId(String orderTradeId) {
            this.orderTradeId = orderTradeId;
            return this;
        }

        public String getRsa2() {
            return rsa2;
        }

        public ALiPayBuilder setRsa2(String rsa2) {
            this.rsa2 = rsa2;
            return this;
        }

        public String getRsa() {
            return rsa;
        }

        public ALiPayBuilder setRsa(String rsa) {
            this.rsa = rsa;
            return this;
        }

        public String getAppid() {
            return appid;
        }

        public ALiPayBuilder setAppid(String appid) {
            this.appid = appid;
            return this;
        }

        public String getMoney() {
            return money;
        }

        public ALiPayBuilder setMoney(String money) {
            this.money = money;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public ALiPayBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public ALiPayBuilder setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }
    }

    //----------------------------使用示例-------------------------------
    //在客户端进行签名
    /*AliPayUtil.ALiPayBuilder builder = new AliPayUtil.ALiPayBuilder();
    builder.setAppid("123")
            .setRsa("456")//根据情况设置Rsa2与Rsa。这里一定要换成正确的私钥，不然会报错
            .setMoney("0.01")//单位时分
            .setTitle("支付测试")
            .setOrderTradeId("487456")//从服务端获取
            .setNotifyUrl("fdsfasdf")//从服务端获取
            .build()
            .toALiPay(MainActivity .this);

    //在服务端进行前面
    //假设已从服务端获取orderInfo
    String orderInfo = "";
    MyALipayUtils.ALiPayBuilder builder = new MyALipayUtils.ALiPayBuilder();
    builder.build().toALiPay(MainActivity.this, orderInfo);*/

}
