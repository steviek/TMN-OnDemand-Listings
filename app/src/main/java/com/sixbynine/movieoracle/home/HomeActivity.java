package com.sixbynine.movieoracle.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.sixbynine.movieoracle.AboutFragment;
import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.display.DisplayActivity;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sort;
import com.sixbynine.movieoracle.object.RottenTomatoesActorBrief;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;
import com.sixbynine.movieoracle.util.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class HomeActivity extends BaseActivity implements SummaryListFragment.Callback, DisplayFragment.Callback,
FilterFragment.Callback{


    public static final int CHOOSE_ACTOR = 0;
    private ArrayList<RottenTomatoesSummary> mSummaries;
    private SummaryListFragment mSummaryListFragment;
    private DisplayFragment mDisplayFragment;
    private FilterFragment mFilterFragment;
    private boolean mMultiPane;

    private View mFilterContainer;

    private SearchView mSearchView;

    private Filter mFilter;
    private Sort mSort;
    private String mFilterParameter;

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            mFilterFragment.setFilter(Filter.TITLE, s);
            mSummaryListFragment.sortAndFilter(mSort, Filter.TITLE, s);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle(R.string.movies_this_week);

        if(savedInstanceState == null){
            mSummaries = getIntent().getParcelableArrayListExtra("summaries");
        }else{
            mSummaries = savedInstanceState.getParcelableArrayList("summaries");
        }

        mMultiPane = findViewById(R.id.secondary_content) != null;

        Collections.sort(mSummaries, mAlphabeticalComparator);
        mSummaryListFragment = SummaryListFragment.newInstance(mSummaries);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mSummaryListFragment).commit();

        mFilterFragment = FilterFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, mFilterFragment).commit();
        mFilterContainer = findViewById(R.id.filter_container);
        mFilterContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = mFilterContainer.getHeight();
                mFilterContainer.setTag(height);
                if(Build.VERSION.SDK_INT >= 16) {
                    mFilterContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else{
                    mFilterContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mFilterContainer.setVisibility(View.GONE);
            }
        });
        setElevation(mFilterContainer, 2);

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
    public void onItemSelected(int index, RottenTomatoesSummary item) {
        if(mMultiPane){
            setDisplayedItem(index, item);
        }else{
            Intent intent = new Intent(this, DisplayActivity.class);
            intent.putExtra("summary", item);
            startActivityForResult(intent, CHOOSE_ACTOR);
        }

    }

    @Override
    public void onItemMovedToTop(int index, RottenTomatoesSummary item) {
        if(mMultiPane){
            setDisplayedItem(index, item);
        }
    }

    private void setDisplayedItem(int index, RottenTomatoesSummary item){
        if(mMultiPane){
            if(mDisplayFragment == null || !mDisplayFragment.isAdded()){
                mDisplayFragment = DisplayFragment.newInstance(item);
                getSupportFragmentManager().beginTransaction().replace(R.id.secondary_content, mDisplayFragment).commit();
            }else{
                mDisplayFragment.setSummary(item);
            }
            mSummaryListFragment.onPositionSelected(index);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        ViewHelper.colorSearchView(mSearchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_about:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("about");
                if(prev != null){
                    ft.remove(prev);
                }
                AboutFragment frag = new AboutFragment();
                frag.show(ft, "about");
                return true;
            case R.id.action_filter:
                if(mFilterContainer.getVisibility() == View.VISIBLE){
                    hideFilter();
                }else{
                    showFilter();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDisplayFragment != null && mDisplayFragment.isAdded() && mDisplayFragment.isVisible()){
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

    @Override
    public void applyFilterAndSort(Filter filter, Sort sort, String parameter) {
        mFilter = filter;
        mSort = sort;
        mFilterParameter = parameter;
        if(filter == Filter.TITLE){
            mSearchView.setIconified(false);
        }else{
            mSummaryListFragment.sortAndFilter(sort, filter, parameter);
        }
    }

    @Override
    public List<RottenTomatoesSummary> getSummaries() {
        return mSummaries;
    }

    @Override
    public void hideFilter() {
        final ViewGroup.LayoutParams lp = mFilterContainer.getLayoutParams();
        final int originalHeight = mFilterContainer.getHeight();
        if(mFilterContainer.getVisibility() == View.GONE) return;
        mFilterContainer.setTag(originalHeight);
        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (Integer) animation.getAnimatedValue();
                mFilterContainer.setLayoutParams(lp);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFilterContainer.setVisibility(View.GONE);
                lp.height = originalHeight;
                mFilterContainer.setLayoutParams(lp);
            }
        });
        animator.start();
    }

    public void showFilter() {
        if(mFilterContainer.getVisibility() == View.VISIBLE) return;
        final ViewGroup.LayoutParams lp = mFilterContainer.getLayoutParams();
        mFilterContainer.setVisibility(View.VISIBLE);
        lp.height = 0;
        mFilterContainer.setLayoutParams(lp);
        final int originalHeight = (Integer) mFilterContainer.getTag();
        ValueAnimator animator = ValueAnimator.ofInt(0, originalHeight).setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (Integer) animation.getAnimatedValue();
                mFilterContainer.setLayoutParams(lp);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                lp.height = originalHeight;
                mFilterContainer.setLayoutParams(lp);
            }
        });
        animator.start();
    }

    @Override
    public boolean shouldShowBigPoster() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHOOSE_ACTOR && resultCode == RESULT_OK){
           mFilterParameter = data.getStringExtra("actor");
            if(mFilterParameter != null){
                mFilter = Filter.ACTOR;
                mSort = Sort.ALPHABETICAL;
                mSummaryListFragment.sortAndFilter(mSort, mFilter, mFilterParameter);
                mFilterFragment = FilterFragment.newInstance(mFilter, mSort, mFilterParameter);
                getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, mFilterFragment).commitAllowingStateLoss();
                showFilter();
            }
        }
    }
}
