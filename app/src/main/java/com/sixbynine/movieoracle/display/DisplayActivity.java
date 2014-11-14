package com.sixbynine.movieoracle.display;

import android.os.Bundle;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.home.DisplayFragment;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;

/**
 * Created by steviekideckel on 11/13/14.
 */
public class DisplayActivity extends BaseActivity{

    private DisplayFragment mDisplayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_content_frame);

        RottenTomatoesSummary summary = getIntent().getParcelableExtra("summary");
        mDisplayFragment = DisplayFragment.newInstance(summary);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mDisplayFragment).commit();
    }
}
