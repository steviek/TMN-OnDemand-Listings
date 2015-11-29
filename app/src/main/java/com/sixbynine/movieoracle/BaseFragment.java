package com.sixbynine.movieoracle;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        if (this.getClass().getAnnotation(Subscribes.class) != null) {
            MyApplication.getInstance().getBus().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.getClass().getAnnotation(Subscribes.class) != null) {
            MyApplication.getInstance().getBus().unregister(this);
        }
    }
}
