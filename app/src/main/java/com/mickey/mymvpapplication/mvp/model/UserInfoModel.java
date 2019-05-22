package com.mickey.mymvpapplication.mvp.model;

import com.mickey.mymvpapplication.api.ApiManager;
import com.mickey.mymvpapplication.base.BaseModel;
import com.mickey.mymvpapplication.mvp.beans.User;
import com.mickey.mymvpapplication.mvp.contract.UserInfoContract;

import io.reactivex.Observable;

/**
 * Description:
 * Created by Ming on 2019-5-22.
 */
public class UserInfoModel extends BaseModel implements UserInfoContract.Model {
    @Override
    public Observable<User> getUserInfo(String user) {
        return ApiManager.getInstance().getGitHubService().getUserInfo(user);
    }
}
