package com.sixbynine.movieoracle.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.sixbynine.movieoracle.AboutFragment;
import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryCastMember;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.display.DisplayActivity;
import com.sixbynine.movieoracle.events.PaletteLoadedEvent;
import com.sixbynine.movieoracle.model.ActorFilter;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sorting;
import com.sixbynine.movieoracle.model.TitleFilter;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;
import com.sixbynine.movieoracle.util.Prefs;
import com.sixbynine.movieoracle.util.ViewHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public final class HomeActivity extends BaseActivity implements SummaryListFragment.Callback,
        DisplayFragment.Callback, FilterFragment.Callback {

    public static final int CHOOSE_ACTOR = 0;
    private List<RTMovieQueryMovieSummary> mSummaries;
    private SummaryListFragment mSummaryListFragment;
    private DisplayFragment mDisplayFragment;
    private FilterFragment mFilterFragment;
    private boolean mMultiPane;

    private View mFilterContainer;

    private SearchView mSearchView;

    private Filter mFilter;
    private Sorting mSort;
    private boolean mFilterShowing;

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            Filter filter = new TitleFilter(s);
            mFilterFragment.setFilter(filter);
            mSummaryListFragment.sortAndFilter(mSort, filter);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle(R.string.movies_this_week);

        int index = -1;
        mSummaries = Prefs.getCurrentBestSummaries();
        if (savedInstanceState == null) {
            mFilterShowing = false;
        } else {
            mFilterShowing = savedInstanceState.getBoolean("filter_showing");
            index = savedInstanceState.getInt("list_index", -1);
        }

        mMultiPane = findViewById(R.id.secondary_content) != null;

        Collections.sort(mSummaries, mAlphabeticalComparator);
        mSummaryListFragment = SummaryListFragment.newInstance(index);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mSummaryListFragment).commit();

        mFilterFragment = FilterFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, mFilterFragment).commit();
        mFilterContainer = findViewById(R.id.filter_container);
        mFilterContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = mFilterContainer.getHeight();
                mFilterContainer.setTag(height);
                if (Build.VERSION.SDK_INT >= 16) {
                    mFilterContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mFilterContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mFilterContainer.setVisibility(mFilterShowing ? View.VISIBLE : View.GONE);
            }
        });
        setElevation(mFilterContainer, 2);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("filter_showing", mFilterShowing);
        if (mSummaryListFragment != null) {
            outState.putInt("list_index", mSummaryListFragment.getIndex());
        }
    }

    private Comparator<RTMovieQueryMovieSummary> mAlphabeticalComparator = new Comparator<RTMovieQueryMovieSummary>() {
        @Override
        public int compare(RTMovieQueryMovieSummary lhs, RTMovieQueryMovieSummary rhs) {
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    };

    @Override
    public void onItemSelected(int index, RTMovieQueryMovieSummary item) {
        if (mMultiPane) {
            setDisplayedItem(index, item);
        } else {
            Intent intent = new Intent(this, DisplayActivity.class);
            intent.putExtra("summary", MyApplication.getInstance().writeValueAsSring(item));
            startActivityForResult(intent, CHOOSE_ACTOR);
        }

    }

    @Override
    public void onItemMovedToTop(int index, RTMovieQueryMovieSummary item) {
        if (mMultiPane) {
            setDisplayedItem(index, item);
        }
    }

    private void setDisplayedItem(int index, RTMovieQueryMovieSummary item) {
        if (mMultiPane) {
            if (mDisplayFragment == null || !mDisplayFragment.isAdded()) {
                mDisplayFragment = DisplayFragment.newInstance(item);
                getSupportFragmentManager().beginTransaction().replace(R.id.secondary_content, mDisplayFragment).commit();
            } else {
                mDisplayFragment.setSummary(item);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setQueryHint(getString(R.string.query_hint));
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        ViewHelper.colorSearchView(mSearchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("about");
                if (prev != null) {
                    ft.remove(prev);
                }
                AboutFragment frag = new AboutFragment();
                frag.show(ft, "about");
                return true;
            case R.id.action_filter:
                if (mFilterContainer.getVisibility() == View.VISIBLE) {
                    hideFilter();
                } else {
                    showFilter();
                }
                return true;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "View movie listings with their rotten tomatoes scores using this cool app!  Download it at https://play.google.com/store/apps/details?id=com.sixbynine.movieoracle");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share App"));
                return true;
            case R.id.action_rate:
                Intent i = MyApplication.getStoreIntent();
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDisplayFragment != null && mDisplayFragment.isAdded() && mDisplayFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, mSummaryListFragment).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActorClicked(RTMovieQueryCastMember actor) {

    }

    @Override
    public void applyFilterAndSort(Filter filter, Sorting sort) {
        mFilter = filter;
        mSort = sort;
        if (filter instanceof TitleFilter) {
            mSearchView.setIconified(false);
        } else {
            mSummaryListFragment.sortAndFilter(sort, filter);
        }
    }

    @Override
    public List<RTMovieQueryMovieSummary> getSummaries() {
        return mSummaries;
    }

    @Override
    public void hideFilter() {
        final ViewGroup.LayoutParams lp = mFilterContainer.getLayoutParams();
        mFilterShowing = false;
        final int originalHeight = mFilterContainer.getHeight();
        if (mFilterContainer.getVisibility() == View.GONE) return;
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

    @Override
    public void onPaletteLoaded(PaletteLoadedEvent event) {

    }

    public void showFilter() {
        if (mFilterContainer.getVisibility() == View.VISIBLE) return;
        mFilterShowing = true;
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
    public boolean showLinks() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_ACTOR && resultCode == RESULT_OK) {
            String actor = data.getStringExtra("actor");
            if (actor != null) {
                mFilter = new ActorFilter(actor);
                mSort = Sorting.ALPHABETICAL;
                mSummaryListFragment.sortAndFilter(mSort, mFilter);
                mFilterFragment = FilterFragment.newInstance(mFilter, mSort);
                getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, mFilterFragment).commitAllowingStateLoss();
                showFilter();
            }
        }
    }
}
