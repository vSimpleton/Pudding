package com.pomelo.pudding.mvp;

import android.content.Context;

import com.pomelo.pudding.mvp.base.BaseModel;
import com.pomelo.pudding.mvp.base.BaseObserver;
import com.pomelo.pudding.mvp.bean.DailyInfo;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sherry on 2019/11/28
 */

public class DailyPresenter extends DailyContract.Presenter{

    public DailyPresenter(Context context) {
        super(context);
    }

    @Override
    public void getDaily() {
        getApiService().getDailyInfo().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<DailyInfo>() {
                    @Override
                    protected void onSuccess(BaseModel<DailyInfo> t) {
                        getMvpView().getDailySuccess(t.getData());
                    }

                    @Override
                    protected void onFailure(DailyInfo data, int code, String message) {
                        getMvpView().getDailyError(message);
                    }
                });
    }
}
