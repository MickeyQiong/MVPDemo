package com.mickey.mymvpapplication.base;

import io.reactivex.disposables.Disposable;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public interface IModel {
    void addDisposable(Disposable disposable);
    void unsubscribe();
}
