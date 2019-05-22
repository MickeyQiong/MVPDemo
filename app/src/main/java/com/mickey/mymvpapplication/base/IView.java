package com.mickey.mymvpapplication.base;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public interface IView {
    /**
     * 显示Loading
     */
    void showProgressDialog();

    /**
     * 隐藏Loading
     */
    void hideProgressDialog();
}
