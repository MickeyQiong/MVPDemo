package com.mickey.mymvpapplication.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description:
 * Created by Ming on 2019-5-21.
 */
public abstract class BaseFragment extends Fragment {

    private boolean isViewPrepare;
    private boolean isLoadData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutRes(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewPrepare = true;
        lazyLoadDataIfPrepared();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared();
        }
    }

    private void lazyLoadDataIfPrepared() {
        if (getUserVisibleHint() && isViewPrepare && isLoadData) {
            lazyLoad();
            isLoadData = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            App.getRefWatcher(activity).watch(this);
        }
    }

    protected abstract void lazyLoad();

    protected abstract int layoutRes();


}
