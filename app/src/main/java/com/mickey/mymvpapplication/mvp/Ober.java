package com.mickey.mymvpapplication.mvp;

import com.mickey.mymvpapplication.base.BaseModel;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:
 * Created by Ming on 2019-5-22.
 */
public class Ober<T> {

    private static Ober ober;

    private Ober() {
    }

    public static Ober getInstance(){
        if (ober == null){
            synchronized (Ober.class){
                if (ober == null){
                    ober = new Ober();
                }
            }
        }
        return ober;
    }


    public void r(Observable<T> t, final BaseModel model){
        t.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        model.addDisposable(d);
                    }

                    @Override
                    public void onNext(T user) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
