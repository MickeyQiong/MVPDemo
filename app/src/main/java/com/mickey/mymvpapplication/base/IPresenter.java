package com.mickey.mymvpapplication.base;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 * Email mingqiang@bailitop.com
 */
public interface IPresenter<V extends IView> {
    /**
     * 绑定 View
     */
    void attachView(V mView);

    /**
     * 解绑 View
     */
    void detachView();
}
