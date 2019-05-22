package com.mickey.mymvpapplication.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Description:
 * Created by Ming on 2019-5-21.
 */
public class BaseModel implements IModel {

    private CompositeDisposable compositeDisposable;

    @Override
    public void addDisposable(Disposable disposable) {
        if (disposable != null){
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
        }
    }

    @Override
    public void unsubscribe() {
        if (compositeDisposable != null){
            compositeDisposable.clear();
        }
        compositeDisposable = null;
    }
}
