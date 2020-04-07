package oms.masm.mvvm.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import oms.masm.mvvm.base.BaseViewModel;
import oms.masm.mvvm.bean.ArticleBean;
import oms.masm.mvvm.bean.Resource;
import oms.masm.mvvm.repository.RepositoryImpl;

/**
 * NAME: 柚子啊
 * DATE: 2020-04-06
 * DESC: TODO:Test代码
 */

public class TestViewModel extends BaseViewModel<RepositoryImpl> {

    public MutableLiveData<Resource<List<ArticleBean>>> getArticles() {
        return getRepository().getArticleList();
    }

}
