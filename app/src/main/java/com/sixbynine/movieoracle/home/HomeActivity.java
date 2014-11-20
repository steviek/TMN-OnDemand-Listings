package com.sixbynine.movieoracle.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.graphics.Palette;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.display.DisplayActivity;
import com.sixbynine.movieoracle.object.RottenTomatoesActorBrief;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class HomeActivity extends BaseActivity implements SummaryListFragment.Callback, DisplayFragment.Callback{

    private ArrayList<RottenTomatoesSummary> mSummaries;
    private SummaryListFragment mSummaryListFragment;
    private DisplayFragment mDisplayFragment;
    private boolean mMultiPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(savedInstanceState == null){
            mSummaries = getIntent().getParcelableArrayListExtra("summaries");
        }else{
            mSummaries = savedInstanceState.getParcelableArrayList("summaries");
        }

        mMultiPane = findViewById(R.id.secondary_content) != null;

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
        if(mMultiPane){
            setDisplayedItem(item);
        }else{
            Intent intent = new Intent(this, DisplayActivity.class);
            intent.putExtra("summary", item);
            startActivity(intent);
        }

    }

    @Override
    public void onItemMovedToTop(RottenTomatoesSummary item) {
        if(mMultiPane){
            setDisplayedItem(item);
        }
    }

    private void setDisplayedItem(RottenTomatoesSummary item){
        if(mMultiPane){
            if(mDisplayFragment == null || !mDisplayFragment.isAdded()){
                mDisplayFragment = DisplayFragment.newInstance(item);
                getSupportFragmentManager().beginTransaction().replace(R.id.secondary_content, mDisplayFragment).commit();
            }else{
                mDisplayFragment.setSummary(item);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mDisplayFragment.isAdded() && mDisplayFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.content, mSummaryListFragment).commit();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onActorClicked(RottenTomatoesActorBrief actor) {

    }

    @Override
    public void presentPalette(Palette palette) {
        //do nothing
    }
}
