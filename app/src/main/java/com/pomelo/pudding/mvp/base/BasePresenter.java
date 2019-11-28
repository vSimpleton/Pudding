package com.pomelo.pudding.mvp.base;

import android.content.Context;
import com.pomelo.pudding.http.ApiService;
import com.pomelo.pudding.http.RetrofitFactory;

/**
 * Created by Sherry on 2019/11/28
 * presenter的基类，提供了两个接口用于与v层进行沟通
 */

public class BasePresenter<T extends BaseMvpView> implements Presenter<T> {

    public Context mContext;
    private T mMvpView;
    private ApiService mApiService;

    public BasePresenter(Context context) {
        mContext = context;
        mApiService = RetrofitFactory.getInstance(context).getApiService();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
        mContext = null;
    }

    public T getMvpView() {
        return mMvpView;
    }

    public ApiService getApiService() {
        return mApiService;
    }

}
