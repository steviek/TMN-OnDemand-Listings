package com.sixbynine.movieoracle.ui.fragment;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by steviekideckel on 10/22/14.
 */
public class ActionBarListFragment extends ListFragment{

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ActionBarActivity == false){
            throw new IllegalStateException("Activity must extend ActionBarActivity");
        }
    }

    protected ActionBarActivity getActionBarActivity() {
        return (ActionBarActivity) getActivity();
    }
}
