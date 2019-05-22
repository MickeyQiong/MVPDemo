package com.mickey.mymvpapplication.api;

import com.mickey.mymvpapplication.mvp.beans.User;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public interface GitHubService {
    @GET("users/{user}")
    Observable<User> getUserInfo(@Path("user") String user);
}
