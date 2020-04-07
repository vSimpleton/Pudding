package oms.masm.mvvm.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import oms.masm.mvvm.base.BaseModel;
import oms.masm.mvvm.bean.ArticleBean;
import oms.masm.mvvm.bean.Resource;
import oms.masm.mvvm.bean.ResponseModel;
import oms.masm.mvvm.http.ApiService;
import oms.masm.mvvm.http.RetrofitFactory;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: 把网络请求以及对bean进行的数据存储都在这个类中实现
 */

public final class RepositoryImpl extends BaseModel {

    private ApiService apiService;

    private ApiService getApiService() {
        if (apiService == null) {
            apiService = RetrofitFactory.getInstance().getApiService();
        }
        return apiService;
    }

    // ---------------模拟网络请求----------------
    // TODO 未知错误
    public MutableLiveData<Resource<List<ArticleBean>>> getArticleList() {
        MutableLiveData<Resource<List<ArticleBean>>> liveData = new MutableLiveData<>();

        //------------测试代码--------------
        getApiService().getArticles().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseModel<List<ArticleBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseModel<List<ArticleBean>> listResponseModel) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return liveData;

        //return observeGo(getApiService().getArticles(), liveData);
    }

}
