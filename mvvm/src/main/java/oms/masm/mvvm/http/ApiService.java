package oms.masm.mvvm.http;

import java.util.List;

import io.reactivex.Observable;
import oms.masm.mvvm.bean.ArticleBean;
import oms.masm.mvvm.bean.ResponseModel;
import retrofit2.http.GET;

/**
 * des:ApiService
 * Created by xsf
 * on 2016.06.15:47
 */
public interface ApiService {

    @GET("wxarticle/chapters/json")
    Observable<ResponseModel<List<ArticleBean>>> getArticles();
}