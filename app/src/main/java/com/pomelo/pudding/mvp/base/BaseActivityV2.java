package com.pomelo.pudding.mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: Activity有Presenter的基类
 */

public abstract class BaseActivityV2<V extends BaseMvpView, T extends BasePresenter<V>> extends BaseActivity {

    protected T mPresenter;
 
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresent();
        mPresenter.attachView((V) this);
        super.onCreate(savedInstanceState);
    }

    /**
     * 由子类去实现该方法
     */
    protected abstract T createPresent();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
