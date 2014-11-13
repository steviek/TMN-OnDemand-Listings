package com.sixbynine.movieoracle.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.object.RottenTomatoesActorBrief;
import com.sixbynine.movieoracle.object.RottenTomatoesRatings;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.fragment.ActionBarFragment;

/**
 * Created by steviekideckel on 11/11/14.
 */
public class DisplayFragment extends ActionBarFragment{

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mRuntime;
    private TextView mYear;
    private TextView mCriticFresh;
    private TextView mCriticRotten;
    private TextView mAudienceFresh;
    private TextView mAudienceRotten;
    private TextView mSynopsis;
    private TextView mCast;
    private ViewGroup mRatingsContainer;

    private RottenTomatoesSummary mSummary;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);

        mPoster = (ImageView) view.findViewById(R.id.poster);
        mTitle = (TextView) view.findViewById(R.id.title);
        mRuntime = (TextView) view.findViewById(R.id.runtime);
        mYear = (TextView) view.findViewById(R.id.year);
        mCriticFresh = (TextView) view.findViewById(R.id.critic_fresh_text_view);
        mCriticRotten = (TextView) view.findViewById(R.id.critic_rotten_text_view);
        mAudienceFresh = (TextView) view.findViewById(R.id.audience_fresh_text_view);
        mAudienceRotten = (TextView) view.findViewById(R.id.audience_rotten_text_view);
        mSynopsis = (TextView) view.findViewById(R.id.synopsis_text_view);
        mCast = (TextView) view.findViewById(R.id.cast_text_view);
        mRatingsContainer = (ViewGroup) view.findViewById(R.id.ratings_container);

        setSummary(mSummary);

        return view;
    }

    public void setSummary(RottenTomatoesSummary summary){
        mSummary = summary;
        mTitle.setText(mSummary.getTitle());
        mRuntime.setText(getString(R.string.n_minutes, mSummary.getRuntimeAsInt()));
        mYear.setText(mSummary.getYear());

        StringBuilder sb = new StringBuilder();
        for(RottenTomatoesActorBrief actor : mSummary.getCast()){
            sb.append(actor.getName()).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        mCast.setText(getString(R.string.cast, sb.toString()));

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
    }
}
