package com.pomelo.pudding.mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * NAME: 柚子啊
 * DATE: 2020/4/7
 * DESC: Fragment有Presenter的基类
 */
public abstract class BaseFragmentV2<V extends BaseMvpView, T extends BasePresenter<V>> extends BaseFragment {

    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.attachView((V) this);
    }

    /**
     * 由子类去实现该方法
     * @return
     */
    protected abstract T createPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    @Override
    public void onDetach() {
        mPresenter.detachView();
        super.onDetach();
    }

}
