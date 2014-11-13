package com.sixbynine.movieoracle.home;

import android.os.Bundle;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class HomeActivity extends BaseActivity implements SummaryListFragment.Callback{

    private ArrayList<RottenTomatoesSummary> mSummaries;
    private SummaryListFragment mSummaryListFragment;
    private DisplayFragment mDisplayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(savedInstanceState == null){
            mSummaries = getIntent().getParcelableArrayListExtra("summaries");
        }else{
            mSummaries = savedInstanceState.getParcelableArrayList("summaries");
        }

        Collections.sort(mSummaries, mAlphabeticalComparator);
        mSummaryListFragment = SummaryListFragment.newInstance(mSummaries);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mSummaryListFragment).commit();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("summaries", mSummaries);
    }

    private Comparator<RottenTomatoesSummary> mAlphabeticalComparator = new Comparator<RottenTomatoesSummary>() {
        @Override
        public int compare(RottenTomatoesSummary lhs, RottenTomatoesSummary rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    };

    @Override
    public void onItemSelected(RottenTomatoesSummary item) {
        mDisplayFragment = DisplayFragment.newInstance(item);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mDisplayFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(mDisplayFragment.isAdded() && mDisplayFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.content, mSummaryListFragment).commit();
        }else{
            super.onBackPressed();
        }
    }
}
