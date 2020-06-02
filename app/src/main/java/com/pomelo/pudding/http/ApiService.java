package com.pomelo.pudding.http;

import com.pomelo.pudding.mvp.bean.UserInfo;
import com.pomelo.pudding.mvp.base.BaseModel;
import com.pomelo.pudding.mvp.bean.DailyInfo;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Sherry on 2019/11/28
 * Retrofit注解练习
 */

public interface ApiService {

    /**
     * 获取每日一句
     * @return
     */
    @GET(ApiHelper.GET_DAILY)
    Observable<BaseModel<DailyInfo>> getDailyInfo();

    /**
     * 通过@Query传入查询参数，或使用@QueryMap Map<String, String> options传入键值对参数
     * 通过@Path动态指定一个查询参数（URL缺省值）
     *
     * @param userId Url缺省值
     * @param sort 查询参数
     */
    @GET("user/getUser/{id}")
    Observable<BaseModel<UserInfo>> getUser(@Path("id") int userId, @Query("sort") String sort);


    /**
     * 通过@HTTP可以代替各种网络请求
     */
    @HTTP(method = "GET", path = "blog/{id}")
    Observable<BaseModel<UserInfo>> getUserV2(@Path("id") int id);

    @POST("users/new")
    Observable<ResponseBody> postUser(@Body UserInfo userInfo);

    /**
     * 使用@FormUrlEncoded：表示发送form-encoded的数据，每个键值对需要用@Filed来注解键名
     */
    @FormUrlEncoded
    @POST("login")
    Observable<ResponseBody> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Observable<ResponseBody> loginV2(@FieldMap Map<String, Object> map);


    /**
     * 使用@Multipart表示发送form-encoded的数据（适用于有文件上传的场景），每个键值对需要用@Part来注解键名
     */
    @Multipart
    @POST("login")
    Observable<ResponseBody> loginV3(@Part("username") String username, @Part("password") String password);

    @Multipart
    @POST("login")
    Observable<ResponseBody> loginV4(@PartMap Map<String, Object> map);

    /**
     * 使用JSON构造参数并提交时使用该方法进行post请求
     */
    @Multipart
    @POST(ApiHelper.GET_DAILY)
    Observable<BaseModel<Object>> postTest(@PartMap Map<String, RequestBody> requestMap);


    /**
     * 使用JSON构造参数并提交时使用该方法进行get请求
     */
    @GET(ApiHelper.GET_DAILY)
    Observable<BaseModel<Object>> getTest(@Query("req") String req);

}
