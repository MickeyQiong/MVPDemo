package com.mickey.mymvpapplication.base;

/**
 * Description:
 * Created by Ming on 2019-5-21.
 */
public abstract class BaseMVPActivity<V extends IView, P extends IPresenter<V>> extends BaseActivity implements IView {

    public P mPresenter = null;

    @Override
    protected void initView() {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        mPresenter = null;
    }

    protected abstract P createPresenter();

}
