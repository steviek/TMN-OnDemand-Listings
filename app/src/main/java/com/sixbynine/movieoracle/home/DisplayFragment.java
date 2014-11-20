package com.sixbynine.movieoracle.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.manager.PaletteManager;
import com.sixbynine.movieoracle.manager.RottenTomatoesManager;
import com.sixbynine.movieoracle.manager.UpdateEvent;
import com.sixbynine.movieoracle.manager.UpdateListener;
import com.sixbynine.movieoracle.object.RottenTomatoesActorBrief;
import com.sixbynine.movieoracle.object.RottenTomatoesRatings;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.fragment.ActionBarFragment;

/**
 * Created by steviekideckel on 11/11/14.
 */
public class DisplayFragment extends ActionBarFragment implements UpdateListener{

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mRuntime;
    private TextView mYear;
    private TextView mCriticFresh;
    private TextView mCriticRotten;
    private TextView mAudienceFresh;
    private TextView mAudienceRotten;
    private TextView mSynopsisHeader;
    private TextView mSynopsisBody;
    private View mSynopsisDivider;
    private TextView mCastHeader;
    private GridLayout mCastContainer;
    //private TextView mCastBody;
    private View mCastDivider;
    private ViewGroup mRatingsContainer;
    private ViewGroup mRoot;

    private ViewGroup mBigPosterContainer;
    private ImageView mBigPoster;

    private RottenTomatoesSummary mSummary;
    private Callback mCallback;

    public interface Callback{
        public void onActorClicked(RottenTomatoesActorBrief actor);
        public void presentPalette(Palette palette);
    }

    private View.OnClickListener mRottenTomatoesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == mRatingsContainer){
                String url = mSummary.getLinks().getAlternate();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof Callback){
            mCallback = (Callback) activity;
        }else{
            throw new ClassCastException(activity.toString() + " must implement Callback interface");
        }
    }

    public static DisplayFragment newInstance(RottenTomatoesSummary summary){
        DisplayFragment frag = new DisplayFragment();
        Bundle b = new Bundle();
        b.putParcelable("summary", summary);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mSummary = getArguments().getParcelable("summary");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RottenTomatoesManager.getInstance().subscribe(this);
        RottenTomatoesManager.getInstance().getPoster(mSummary);
        PaletteManager.getInstance().subscribe(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        RottenTomatoesManager.getInstance().unSubscribe(this);
        PaletteManager.getInstance().unSubscribe(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment_display, container, false);

        mPoster = (ImageView) view.findViewById(R.id.poster);
        mTitle = (TextView) view.findViewById(R.id.title);
        mRuntime = (TextView) view.findViewById(R.id.runtime);
        mYear = (TextView) view.findViewById(R.id.year);
        mCriticFresh = (TextView) view.findViewById(R.id.critic_fresh_text_view);
        mCriticRotten = (TextView) view.findViewById(R.id.critic_rotten_text_view);
        mAudienceFresh = (TextView) view.findViewById(R.id.audience_fresh_text_view);
        mAudienceRotten = (TextView) view.findViewById(R.id.audience_rotten_text_view);
        mSynopsisHeader = (TextView) view.findViewById(R.id.synopsis_header);
        mSynopsisDivider = view.findViewById(R.id.synopsis_divider);
        mSynopsisBody = (TextView) view.findViewById(R.id.synopsis_text);
        mCastHeader = (TextView) view.findViewById(R.id.cast_header);
        mCastDivider = view.findViewById(R.id.cast_divider);
        mCastContainer = (GridLayout) view.findViewById(R.id.cast_container);
        mRatingsContainer = (ViewGroup) view.findViewById(R.id.ratings_container);
        mRatingsContainer.setOnClickListener(mRottenTomatoesClickListener);

        mBigPosterContainer = (ViewGroup) view.findViewById(R.id.big_poster_container);
        mBigPoster = (ImageView) view.findViewById(R.id.big_poster);

        mRoot = container;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSummary(mSummary);
    }

    public RottenTomatoesSummary getSummary(){
        return mSummary;
    }

    public void setSummary(RottenTomatoesSummary summary){
        mSummary = summary;
        mTitle.setText(mSummary.getTitle());

        if(mSummary.getRuntimeAsInt() > 0){
            mRuntime.setText(getString(R.string.n_minutes, mSummary.getRuntimeAsInt()));
        }else{
            mRuntime.setVisibility(View.INVISIBLE);
        }

        if(mSummary.getYearAsInt() > 0){
            mYear.setText(mSummary.getYear());
        }else{
            mYear.setVisibility(View.INVISIBLE);
        }


        if(mSummary.getCast() == null || mSummary.getCast().isEmpty()){
            hideCast();
        }else{
            showCast();
            mCastContainer.removeAllViews();
            for(RottenTomatoesActorBrief actor : mSummary.getCast()){
                TextView textView = (TextView) getActionBarActivity().getLayoutInflater().inflate(R.layout.text_view_actor, null);
                textView.setTag(actor);
                SpannableString string = new SpannableString(actor.getName());
                string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
                textView.setText(string);
                mCastContainer.addView(textView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onActorClicked((RottenTomatoesActorBrief) v.getTag());
                    }
                });
            }
        }

        if(mSummary.getSynopsis() == null || mSummary.getSynopsis().isEmpty()){
            hideSynopsis();
        }else{
            showSynopsis();
            mSynopsisBody.setText(mSummary.getSynopsis());
        }


        RottenTomatoesRatings ratings = mSummary.getRatings();
        if(ratings != null){
            if(ratings.getCriticsScore() > 0){
                if(ratings.getCriticsScore() >= 60){
                    mCriticFresh.setText(ratings.getCriticsScore() + "%");
                    mCriticRotten.setVisibility(View.GONE);
                    mCriticFresh.setVisibility(View.VISIBLE);
                }else{
                    mCriticRotten.setText(ratings.getCriticsScore() + "%");
                    mCriticFresh.setVisibility(View.GONE);
                    mCriticRotten.setVisibility(View.VISIBLE);
                }
            }else{
                mCriticFresh.setVisibility(View.GONE);
                mCriticRotten.setVisibility(View.INVISIBLE);
            }
            if(ratings.getAudienceScore() > 0){
                if(ratings.getAudienceScore() >= 60){
                    mAudienceFresh.setText(ratings.getAudienceScore() + "%");
                    mAudienceRotten.setVisibility(View.GONE);
                    mAudienceFresh.setVisibility(View.VISIBLE);
                }else{
                    mAudienceRotten.setText(ratings.getAudienceScore() + "%");
                    mAudienceFresh.setVisibility(View.GONE);
                    mAudienceRotten.setVisibility(View.VISIBLE);
                }
            }else{
                mAudienceFresh.setVisibility(View.GONE);
                mAudienceRotten.setVisibility(View.INVISIBLE);
            }
        }else{
            mRatingsContainer.setVisibility(View.GONE);
        }

        if(isResumed()){
            mPoster.setImageResource(0);
            RottenTomatoesManager.getInstance().getPoster(mSummary);
        }
    }

    private void hideCast(){
        mCastDivider.setVisibility(View.GONE);
        mCastHeader.setVisibility(View.GONE);
        mCastContainer.setVisibility(View.GONE);
    }

    private void showCast(){
        mCastDivider.setVisibility(View.VISIBLE);
        mCastHeader.setVisibility(View.VISIBLE);
        mCastContainer.setVisibility(View.VISIBLE);
    }

    private void hideSynopsis(){
        mSynopsisBody.setVisibility(View.GONE);
        mSynopsisDivider.setVisibility(View.GONE);
        mSynopsisHeader.setVisibility(View.GONE);
    }

    private void showSynopsis(){
        mSynopsisBody.setVisibility(View.VISIBLE);
        mSynopsisDivider.setVisibility(View.VISIBLE);
        mSynopsisHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void update(UpdateEvent e, Object... data) {
        switch(e){
            case POSTER_LOADED:
                if(((RottenTomatoesSummary) data[0]).getId().equals(mSummary.getId())){
                    mPoster.setImageBitmap((Bitmap) data[1]);
                    mPoster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBigPosterContainer.setVisibility(View.VISIBLE);
                            mPoster.setVisibility(View.GONE);
                        }
                    });
                    mBigPoster.setImageBitmap((Bitmap) data[1]);
                    mBigPoster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideBigPoster();

                        }
                    });
                    PaletteManager.getInstance().loadPalette((RottenTomatoesSummary) data[0], (Bitmap) data[1]);
                }
                break;
            case POSTER_FAILED_TO_LOAD:
                if(((RottenTomatoesSummary) data[0]).getId().equals(mSummary.getId())){
                }
                break;
            case PALETTE_LOADED:
                if(((RottenTomatoesSummary) data[0]).getId().equals(mSummary.getId())){
                    mCallback.presentPalette((Palette) data[1]);
                }
                break;
        }
    }



    /**
     * Hides the big poster from the screen
     * @return true if this caused a change in layout, false otherwise
     */
    public boolean hideBigPoster(){
        if(mBigPosterContainer.getVisibility() == View.VISIBLE){
            mBigPosterContainer.setVisibility(View.GONE);
            mPoster.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }


}
