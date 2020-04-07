package oms.masm.mvvm.base;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import oms.masm.mvvm.repository.RepositoryImpl;


/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: ViewModel基类，管理rxJava发出的请求，ViewModel销毁同时也取消请求
 *
 * 注：与RepositoryImpl配套使用
 */

public class BaseViewModel<T extends BaseModel> extends ViewModel {

    /**
     * 管理RxJava请求
     */
    private CompositeDisposable compositeDisposable;

    private T repository;

    public BaseViewModel() {
        createRepository();
        compositeDisposable = new CompositeDisposable();
        repository.setCompositeDisposable(compositeDisposable);
    }

    public void createRepository() {
        if (repository == null) {
            repository = (T) new RepositoryImpl();
        }
    }

    public T getRepository() {
        return repository;
    }

    /**
     * 用来通知 Activity／Fragment 是否显示等待Dialog
     */
//    protected DialogLiveData<DialogBean> showDialog = new DialogLiveData<>();
//    /**
//     * 当ViewModel层出现错误需要通知到Activity／Fragment
//     */
//    protected MutableLiveData<Object> error = new MutableLiveData<>();
//
//    public void getShowDialog(LifecycleOwner owner, Observer<DialogBean> observer) {
//        showDialog.observe(owner, observer);
//    }
//
//    public void getError(LifecycleOwner owner, Observer<Object> observer) {
//        error.observe(owner, observer);
//    }

    /**
     * ViewModel销毁同时也取消请求
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
//        showDialog = null;
//        error = null;
    }

}
