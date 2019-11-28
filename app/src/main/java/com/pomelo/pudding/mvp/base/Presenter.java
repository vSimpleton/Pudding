package com.pomelo.pudding.mvp.base;


/**
 * Created by Sherry on 2019/11/28
 */
public interface Presenter<V extends BaseMvpView> {

    void attachView(V mvpView);

    void detachView();

}
