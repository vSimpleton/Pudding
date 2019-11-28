package com.pomelo.pudding.mvp.base;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sherry on 2019/11/28
 * model基类 ，后台返回参数的固定模型
 */
public class BaseModel<T> {

    private int code;
    @SerializedName("msg")
    private String message;
    private T data;
    @SerializedName("status_code")
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
