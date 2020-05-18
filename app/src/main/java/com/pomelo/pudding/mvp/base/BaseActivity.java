package com.pomelo.pudding.mvp.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pomelo.pudding.utils.ActivityUtil;
import com.pomelo.pudding.utils.StatusBarUtils;

import java.util.Map;
import java.util.Set;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-07
 * DESC: Activity没有Presenter的基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    public LayoutInflater mInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.getInstance().addActivity(this);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        StatusBarUtils.transparencyBar(this);
    }

    /**
     * 无值传递的activity跳转
     */
    protected void startActivity(Class clazz) {
        startActivity(clazz, null);
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        startActivityForResult(clazz, null, requestCode);
    }

    /**
     * 有值传递的activity跳转
     */
    protected void startActivity(Class clazz, Map<String, Object> map) {
        Intent intent = new Intent(this, clazz);
        if (map != null && map.size() != 0) {
            sendData(intent, map);
        }
        startActivity(intent);
    }

    protected void startActivityForResult(Class clazz, Map<String, Object> map, int requestCode) {
        Intent intent = new Intent(this, clazz);
        if (map != null && map.size() != 0) {
            sendData(intent, map);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转传值
     */
    private void sendData(Intent intent, Map<String, Object> map) {
        Bundle bundle = new Bundle();
        //遍历map集合
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (int) value);
            } else if (value instanceof Boolean) {
                bundle.getBoolean(key, (boolean) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (long) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (double) value);
            } else if (value instanceof Float) {
                bundle.putFloat(key, (float) value);
            } else {
                continue;
            }
        }
        intent.putExtras(bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.getInstance().removeActivity(this);
    }

    public LayoutInflater getInflate(int layout) {
        LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate.inflate(layout, null);
        return inflate;
    }


}
