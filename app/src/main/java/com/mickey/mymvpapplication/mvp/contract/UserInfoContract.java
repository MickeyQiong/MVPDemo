package com.mickey.mymvpapplication.mvp.contract;

import com.mickey.mymvpapplication.base.IModel;
import com.mickey.mymvpapplication.base.IPresenter;
import com.mickey.mymvpapplication.base.IView;
import com.mickey.mymvpapplication.mvp.beans.User;

import io.reactivex.Observable;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public interface UserInfoContract {

    interface View extends IView{
        void getSuccess(User user);
        void getError(String msg);
    }

    interface Presenter extends IPresenter<View>{
        void getUserInfo(String user);
    }

    interface Model extends IModel{
        Observable<User> getUserInfo(String user);
    }
}
