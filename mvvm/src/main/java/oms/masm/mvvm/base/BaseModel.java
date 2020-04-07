package oms.masm.mvvm.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import oms.masm.mvvm.bean.Resource;
import oms.masm.mvvm.bean.ResponseModel;

/**
 * NAME: 柚子啊
 * DATE: 2020/4/6
 * DESC:
 */

public abstract class BaseModel {

    private CompositeDisposable compositeDisposable;

    public void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * 添加 rxJava 发出的请求
     */
    private void addDisposable(@NonNull Disposable disposable) {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    /**
     *
     * @param observable 网络请求接口
     * @param liveData Resource<T>
     * @param <T> -
     * @return Resource<T>
     */
    public <T> MutableLiveData<T> observeGo(Observable observable, final MutableLiveData<T> liveData) {
        return observe(observable, liveData);
    }

    private <T> MutableLiveData<T> observe(Observable observable, final MutableLiveData<T> liveData) {

        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        liveData.postValue((T) Resource.response((ResponseModel<Object>) o));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        liveData.postValue((T) Resource.error((Throwable) throwable));
                    }
                });

        addDisposable(disposable);

        return liveData;
    }

}
