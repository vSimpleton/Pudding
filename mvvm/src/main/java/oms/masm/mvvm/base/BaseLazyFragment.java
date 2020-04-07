package oms.masm.mvvm.base;

import androidx.databinding.ViewDataBinding;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: androidx下的fragment懒加载实现
 *
 * TODO: 没有解决Fragment嵌套的情况
 */

public abstract class BaseLazyFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseFragment<VM, DB> {

    private boolean visibleToUser = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!visibleToUser) {
            visibleToUser = true;
            lazyLoad();
        }
    }

    protected abstract void lazyLoad();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        visibleToUser = false;
    }
}
