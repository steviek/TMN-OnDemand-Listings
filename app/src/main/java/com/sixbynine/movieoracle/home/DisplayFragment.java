package com.sixbynine.movieoracle.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.sixbynine.movieoracle.Subscribes;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryCastMember;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryRatings;
import com.sixbynine.movieoracle.events.PaletteLoadedEvent;
import com.sixbynine.movieoracle.manager.PaletteManager;
import com.squareup.picasso.Picasso;

@Subscribes
public class DisplayFragment extends Fragment {

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

    private TextView mLinksHeader;
    private View mLinksDivider;
    private TextView mImdbText;
    private View mImdbDivider;
    private TextView mRtText;
    private View mRtDivider;

    private ViewGroup mBigPosterContainer;
    private ImageView mBigPoster;

    private RTMovieQueryMovieSummary mSummary;
    private Callback mCallback;

    public interface Callback{
        void onActorClicked(RTMovieQueryCastMember actor);
        void onPaletteLoaded(PaletteLoadedEvent event);
        boolean shouldShowBigPoster();
        boolean showLinks();
    }

    private View.OnClickListener mRottenTomatoesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == mRatingsContainer || v == mRtText){
                String url = mSummary.getLinks().getAlternative();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }
    };

    private View.OnClickListener mImdbClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == mImdbText){
                String imdbId = mSummary.getAltIds().getImdbId();
                String url = "http://www.imdb.com/title/tt" + imdbId;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Callback){
            mCallback = (Callback) context;
        }else{
            throw new ClassCastException(context.toString() + " must implement Callback interface");
        }
    }

    public static DisplayFragment newInstance(RTMovieQueryMovieSummary summary){
        DisplayFragment frag = new DisplayFragment();
        frag.mSummary = summary;
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPoster();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_display, container, false);

        mPoster = (ImageView) root.findViewById(R.id.poster);
        mTitle = (TextView) root.findViewById(R.id.title);
        mRuntime = (TextView) root.findViewById(R.id.runtime);
        mYear = (TextView) root.findViewById(R.id.year);
        mCriticFresh = (TextView) root.findViewById(R.id.critic_fresh_text_view);
        mCriticRotten = (TextView) root.findViewById(R.id.critic_rotten_text_view);
        mAudienceFresh = (TextView) root.findViewById(R.id.audience_fresh_text_view);
        mAudienceRotten = (TextView) root.findViewById(R.id.audience_rotten_text_view);
        mSynopsisHeader = (TextView) root.findViewById(R.id.synopsis_header);
        mSynopsisDivider = root.findViewById(R.id.synopsis_divider);
        mSynopsisBody = (TextView) root.findViewById(R.id.synopsis_text);
        mCastHeader = (TextView) root.findViewById(R.id.cast_header);
        mCastDivider = root.findViewById(R.id.cast_divider);
        mCastContainer = (GridLayout) root.findViewById(R.id.cast_container);
        mRatingsContainer = (ViewGroup) root.findViewById(R.id.ratings_container);
        mRatingsContainer.setOnClickListener(mRottenTomatoesClickListener);

        mLinksHeader = (TextView) root.findViewById(R.id.links_header);
        mLinksDivider = root.findViewById(R.id.links_divider);
        mImdbText = (TextView) root.findViewById(R.id.links_imdb);
        mImdbDivider = root.findViewById(R.id.links_imdb_divider);
        mRtText = (TextView) root.findViewById(R.id.links_rotten_tomatoes);
        mRtDivider = root.findViewById(R.id.links_rotten_tomatoes_divider);
        mImdbText.setOnClickListener(mImdbClickListener);
        mRtText.setOnClickListener(mRottenTomatoesClickListener);

        mBigPosterContainer = (ViewGroup) root.findViewById(R.id.big_poster_container);
        mBigPoster = (ImageView) root.findViewById(R.id.big_poster);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSummary(mSummary);
    }

    public void setSummary(RTMovieQueryMovieSummary summary) {
        mSummary = summary;
        mTitle.setText(mSummary.getTitle());
        mTitle.setSelected(true);

        if (mSummary.getRuntime() > 0) {
            mRuntime.setText(getString(R.string.n_minutes, mSummary.getRuntime()));
        } else {
            mRuntime.setVisibility(View.INVISIBLE);
        }

        if (mSummary.getYear() > 0) {
            mYear.setText(mSummary.getYear());
        } else {
            mYear.setVisibility(View.INVISIBLE);
        }


        if (mSummary.getCast() == null || mSummary.getCast().isEmpty()) {
            hideCast();
        } else {
            showCast();
            mCastContainer.removeAllViews();
            for (RTMovieQueryCastMember actor : mSummary.getCast()) {
                TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.text_view_actor, null);
                textView.setTag(actor);
                SpannableString string = new SpannableString(actor.getName());
                string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
                textView.setText(string);
                mCastContainer.addView(textView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onActorClicked((RTMovieQueryCastMember) v.getTag());
                    }
                });
            }
        }

        if (mSummary.getSynopsis() == null || mSummary.getSynopsis().isEmpty()) {
            hideSynopsis();
        } else {
            showSynopsis();
            mSynopsisBody.setText(mSummary.getSynopsis());
        }


        RTMovieQueryRatings ratings = mSummary.getRatings();
        if(ratings != null){
            mRatingsContainer.setVisibility(View.VISIBLE);
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

        setLinks(mSummary.getAltIds() != null && mSummary.getAltIds().getImdbId() != null,
                mSummary.getLinks() != null && mSummary.getLinks().getAlternative() != null);

        if(isResumed()){
            mPoster.setImageResource(0);
            getPoster();
        }
    }

    private void getPoster() {
        Picasso.with(getContext())
                .load(mSummary.getPosters().getDetailed())
                .into(mPoster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (getContext() != null) {
                            Bitmap bitmap = ((BitmapDrawable) mPoster.getDrawable()).getBitmap();
                            Palette palette = PaletteManager.loadPalette(mSummary, bitmap);
                            if (palette != null) {
                                mCallback.onPaletteLoaded(new PaletteLoadedEvent(mSummary.getId(), palette));
                            }
                            if (mCallback.shouldShowBigPoster()) {
                                mPoster.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mBigPosterContainer.setVisibility(View.VISIBLE);
                                        mPoster.setVisibility(View.GONE);
                                    }
                                });
                                mBigPoster.setImageBitmap(bitmap);
                                mBigPoster.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        hideBigPoster();

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
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

    private void setLinks(boolean imdb, boolean rt){
        boolean showLinks = mCallback.showLinks();
        if(showLinks && (imdb || rt)){
            mLinksHeader.setVisibility(View.VISIBLE);
            mLinksDivider.setVisibility(View.VISIBLE);
        }else{
            mLinksHeader.setVisibility(View.GONE);
            mLinksDivider.setVisibility(View.GONE);
        }

        if(showLinks && imdb){
            mImdbText.setVisibility(View.VISIBLE);
            mImdbDivider.setVisibility(View.VISIBLE);
        }else{
            mImdbText.setVisibility(View.GONE);
            mImdbDivider.setVisibility(View.GONE);
        }

        if(showLinks && rt){
            mRtText.setVisibility(View.VISIBLE);
            mRtDivider.setVisibility(View.VISIBLE);
        }else{
            mRtText.setVisibility(View.GONE);
            mRtDivider.setVisibility(View.GONE);
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
