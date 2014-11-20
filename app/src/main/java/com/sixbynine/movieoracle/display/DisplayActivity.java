package com.sixbynine.movieoracle.display;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_content_frame);
        getSupportActionBar().setTitle("");

        RottenTomatoesSummary summary = getIntent().getParcelableExtra("summary");
        mDisplayFragment = DisplayFragment.newInstance(summary);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mDisplayFragment).commit();
    }

    @Override
    public void onActorClicked(RottenTomatoesActorBrief actor) {

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
}
