package oms.masm.mvvm.base;

import android.content.Context;

import androidx.databinding.ViewDataBinding;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import oms.masm.mvvm.R;
import oms.masm.mvvm.bean.Resource;
import oms.masm.mvvm.utils.NetWorkUtil;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: ViewModel、ViewDataBinding都需要的基类
 */

public abstract class BaseActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseNoViewModelActivity<DB> {

    protected VM viewModel;

    @Override
    protected DB initDataBinding(int layoutId) {
        /**
         * 将这两个初始化函数插在{@link BaseActivity#initDataBinding}
         */
        viewModel = initViewModel();
        initObserve();
        return super.initDataBinding(layoutId);
    }

    /**
     * 初始化ViewModel
     */
    protected abstract VM initViewModel();

    /**
     * 监听当前ViewModel中 showDialog和error的值
     */
    private void initObserve() {
        if (viewModel == null) return;
//        viewModel.getShowDialog(this, new Observer<DialogBean>() {
//            @Override
//            public void onChanged(DialogBean bean) {
//                if (bean.isShow()) {
//                    showDialog(bean.getMsg());
//                } else {
//                    dismissDialog();
//                }
//            }
//        });
//        viewModel.getError(this, new Observer<Object>() {
//            @Override
//            public void onChanged(Object obj) {
//                Toast.makeText(context, "error:" + obj, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public Context getContext() {
        return this;
    }

    public abstract class OnCallback<T> implements Resource.OnHandleCallback<T> {
        @Override
        public void onLoading(String showMessage) {

        }

        @Override
        public void onSuccess(T data) {

        }

        @Override
        public void onFailure(String msg) {
//            ToastUtil.showShort(msg);
        }

        @Override
        public void onError(Throwable throwable) {
            if (!NetWorkUtil.isNetworkConnected(getContext())) {
//                ToastUtil.showShort(getContext().getResources().getString(R.string.result_network_error));
                return;
            }

            if (throwable instanceof ConnectException) {
//                ToastUtil.showShort(getContext().getResources().getString(R.string.result_server_error));
            } else if (throwable instanceof SocketTimeoutException) {
//                ToastUtil.showShort(getContext().getResources().getString(R.string.result_server_timeout));
            } else if (throwable instanceof JsonSyntaxException) {
//                ToastUtil.showShort("数据解析出错");
            } else {
//                ToastUtil.showShort(getContext().getResources().getString(R.string.result_empty_error));
            }
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onProgress(int percent, long total) {

        }
    }
}
