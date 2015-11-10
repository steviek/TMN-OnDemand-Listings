package com.sixbynine.movieoracle.display;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.Subscribes;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryCastMember;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.events.PaletteLoadedEvent;
import com.sixbynine.movieoracle.home.DisplayFragment;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;
import com.squareup.otto.Subscribe;

@Subscribes
public class DisplayActivity extends BaseActivity implements DisplayFragment.Callback {

    private DisplayFragment mDisplayFragment;
    private RTMovieQueryMovieSummary mSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getSupportActionBar().setTitle("");
        mSummary = getIntent().getParcelableExtra("summary");

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float v) {
                if (Build.VERSION.SDK_INT >= 11) {
                    view.setAlpha(1 - Math.abs(v));
                }
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i != 1) {
                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onActorClicked(RTMovieQueryCastMember actor) {
        Intent intent = new Intent();
        intent.putExtra("actor", MyApplication.getInstance().writeValueAsSring(actor));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mDisplayFragment == null || !mDisplayFragment.hideBigPoster()){
            super.onBackPressed(); //do on back pressed if the big poster wasn't showing
        }
    }

    @Override
    @Subscribe
    public void onPaletteLoaded(PaletteLoadedEvent event) {
        Palette.Swatch swatch = event.getPalette().getDarkVibrantSwatch();
        if(swatch != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(swatch.getRgb()));
            if(Build.VERSION.SDK_INT >= 21){
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.black, getTheme()));
            }
        }
    }

    @Override
    public boolean showLinks() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display, menu);
        String imdbId = null;
        if(mSummary.getAltIds() != null){
            imdbId = mSummary.getAltIds().getImdbId();
        }
        menu.findItem(R.id.action_imdb).setVisible(imdbId != null && !imdbId.isEmpty());
        String rtId = null;
        if(mSummary.getLinks() != null){
            rtId = mSummary.getLinks().getAlternative();
        }
        menu.findItem(R.id.action_rt).setVisible(rtId != null && !rtId.isEmpty());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_imdb:
                String imdbId = mSummary.getAltIds().getImdbId();
                String url = "http://www.imdb.com/title/tt" + imdbId;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.action_rt:
                String url2 = mSummary.getLinks().getAlternative();
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url2));
                startActivity(i2);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public boolean shouldShowBigPoster() {
        return true;
    }
}
