package com.sixbynine.movieoracle.display;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.home.DisplayFragment;
import com.sixbynine.movieoracle.object.RottenTomatoesActorBrief;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;

/**
 * Created by steviekideckel on 11/13/14.
 */
public class DisplayActivity extends BaseActivity implements DisplayFragment.Callback{

    private DisplayFragment mDisplayFragment;
    private ViewPager mPager;
    private RottenTomatoesSummary mSummary;
    private float minAlpha = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getSupportActionBar().setTitle("");
        mSummary = getIntent().getParcelableExtra("summary");

        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mPager.setCurrentItem(1);
        mPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float v) {
                view.setAlpha(1- Math.abs(v));
            }
        });
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i != 1){
                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onActorClicked(RottenTomatoesActorBrief actor) {
        Intent intent = new Intent();
        intent.putExtra("actor", actor.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(!mDisplayFragment.hideBigPoster()){
            super.onBackPressed(); //do on back pressed if the big poster wasn't showing
        }
    }

    @Override
    public void presentPalette(Palette palette) {

        Palette.Swatch swatch = palette.getDarkVibrantSwatch();
        if(swatch != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(swatch.getRgb()));
            /*Spannable text = new SpannableString(mDisplayFragment.getSummary().getTitle());
            text.setSpan(new ForegroundColorSpan(swatch.getTitleTextColor()), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(text);*/
            if(Build.VERSION.SDK_INT >= 21){
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    public static class PlaceHolderFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = new FrameLayout(getActivity());
            view.setBackgroundColor(0xffffffff);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter{

        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0 || i == 2){
                return new PlaceHolderFragment();
            }else{
                mDisplayFragment = DisplayFragment.newInstance(mSummary);
                return mDisplayFragment;
            }
        }

        @Override
        public int getCount() {
                return 3;
        }
    }
}
