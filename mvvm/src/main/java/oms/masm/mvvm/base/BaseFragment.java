package oms.masm.mvvm.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: ViewModel、ViewDataBinding都需要的基类
 */

public abstract class BaseFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseNoViewModelFragment<DB> {

    protected VM viewModel;

    @Override
    protected DB initDataBinding(LayoutInflater inflater, int layoutId, ViewGroup container) {
        /**
         * 将这两个初始化函数插在{@link BaseFragment#initDataBinding}
         */
        viewModel = initViewModel();
        initObserve();
        return super.initDataBinding(inflater, layoutId, container);
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
}
