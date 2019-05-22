package com.mickey.mymvpapplication.api;

import com.mickey.mymvpapplication.http.RetrofitHelper;

/**
 * Description:
 * Created by Ming on 2019-5-22.
 */
public class ApiManager {
    private static GitHubService gitHubService;
    private static ApiManager apiManager;

    private ApiManager() {
    }

    public static ApiManager getInstance(){
        if (apiManager == null){
            synchronized (ApiManager.class){
                if (apiManager == null){
                    apiManager = new ApiManager();
                }
            }
        }
        return apiManager;
    }

    public GitHubService getGitHubService(){
        if (gitHubService == null){
            synchronized (ApiManager.class){
                if (gitHubService == null){
                    gitHubService = RetrofitHelper.getInstance().create(GitHubService.class);
                }
            }
        }
        return gitHubService;
    }
}
