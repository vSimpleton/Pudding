package com.pomelo.pudding.mvp;

import android.content.Context;

import com.pomelo.pudding.mvp.base.BaseMvpView;
import com.pomelo.pudding.mvp.base.BasePresenter;
import com.pomelo.pudding.mvp.bean.DailyInfo;

/**
 * Created by Sherry on 2019/11/28
 */

public interface DailyContract {

    interface UserInfoView extends BaseMvpView {

        //========测试=========
        void getDailySuccess(DailyInfo dailyInfo);

        void getDailyError(String result);

    }

    abstract class Presenter extends BasePresenter<UserInfoView> {

        public Presenter(Context context) {
            super(context);
        }

        //========测试=========
        public abstract void getDaily();

    }
}
