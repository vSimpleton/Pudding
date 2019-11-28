package com.pomelo.pudding.mvp.base;

import android.util.Log;

import com.pomelo.pudding.mvp.base.BaseModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Sherry on 2019/11/28
 * 创建Base抽象类实现Observer
 */

public abstract class BaseObserver<T> implements Observer<BaseModel<T>> {

    private static final String TAG = "BaseObserver";

    @Override
    public void onSubscribe(Disposable d) {
        Log.i(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(BaseModel<T> value) {
        if (value != null) {
            /**
             * 根据具体要求修改
             */
//            if (value.getStatus() != 200) {
//                //服务器端错误
//                if (BuildConfig.DEBUG) {
//                    onFailure(null, value.getStatus(), value.getMessage());
//                }
//                if (value.getStatus() == 401 || value.getStatus() == 403) {
//                    onFailure(null, -1, "无权限操作");
//                }
//            } else if (value.getCode() != 0) {
//                //api层返回错误
//                onFailure(value.getData(), value.getCode(), value.getMessage());
//            } else {
//                onSuccess(value);
//            }
            if (value.getStatus() == 0) {
                onSuccess(value);
            }
        } else {
            //客户端错误
            onFailure(null, -1, "当前网络不佳，请稍后再试");
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "Throwable: " + e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.e(TAG, "onComplete: " );
    }

    /**
     * 返回成功
     *
     * @param t
     */
    protected abstract void onSuccess(BaseModel<T> t);

    /**
     * 返回失败
     *
     * @param code    错误码
     * @param message 错误信息
     */
    protected abstract void onFailure(T data, int code, String message);
}
