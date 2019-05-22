package com.mickey.mymvpapplication.base;

/**
 * Description:
 * Created by Ming on 2019-5-22.
 */
public abstract class BasePresenter<V extends IView,M extends IModel> implements IPresenter<V> {

    public M mModel;
    public V mViewRef;

    @Override
    public void attachView(V mView) {
        mViewRef = mView;
        mModel = createModel();
    }

    public abstract M createModel();

    @Override
    public void detachView() {
        mModel.unsubscribe();
        mModel = null;
        mViewRef = null;
    }

}
