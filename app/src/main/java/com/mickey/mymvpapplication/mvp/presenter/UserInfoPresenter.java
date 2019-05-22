package com.mickey.mymvpapplication.mvp.presenter;

import android.util.Log;

import com.mickey.mymvpapplication.base.BasePresenter;
import com.mickey.mymvpapplication.mvp.beans.User;
import com.mickey.mymvpapplication.mvp.contract.UserInfoContract;
import com.mickey.mymvpapplication.mvp.model.UserInfoModel;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:
 * Created by Ming on 2019-5-22.
 */
public class UserInfoPresenter extends BasePresenter<UserInfoContract.View,UserInfoContract.Model> implements UserInfoContract.Presenter  {
    @Override
    public UserInfoContract.Model createModel() {
        return new UserInfoModel();
    }

    @Override
    public void getUserInfo(String user) {
        mViewRef.showProgressDialog();
        Observable<User> userInfo = mModel.getUserInfo(user);
        userInfo.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mModel.addDisposable(d);
                    }

                    @Override
                    public void onNext(User user) {
                        Log.e("dfdsfdsfdsfds",mViewRef.toString());
                        mViewRef.hideProgressDialog();
                        mViewRef.getSuccess(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mViewRef.hideProgressDialog();
                        mViewRef.getError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mViewRef.hideProgressDialog();
                    }
                });
    }
}
