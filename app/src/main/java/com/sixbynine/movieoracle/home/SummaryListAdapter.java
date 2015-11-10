package com.sixbynine.movieoracle.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryRatings;

import java.util.ArrayList;

public class SummaryListAdapter extends ArrayAdapter<RTMovieQueryMovieSummary> {

    public static class ViewHolder{
        public TextView mTitle;
        public TextView mCriticTextViewFresh;
        public TextView mCriticTextViewRotten;
        public TextView mAudienceTextViewFresh;
        public TextView mAudienceTextViewRotten;

        public ViewHolder(View v) {
            mTitle = (TextView) v.findViewById(R.id.title_text_view);
            mCriticTextViewFresh = (TextView) v.findViewById(R.id.rating_critic_fresh);
            mCriticTextViewRotten = (TextView) v.findViewById(R.id.rating_critic_rotten);
            mAudienceTextViewFresh = (TextView) v.findViewById(R.id.rating_audience_fresh);
            mAudienceTextViewRotten = (TextView) v.findViewById(R.id.rating_audience_rotten);
        }
    }

    public SummaryListAdapter(Context context, ArrayList<RTMovieQueryMovieSummary> summaries){
        super(context, R.layout.row_movie_new, summaries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie_new, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        bindView(viewHolder, position);

        return convertView;
    }

    public void bindView(ViewHolder viewHolder, int position) {
        RTMovieQueryMovieSummary summary = getItem(position);
        viewHolder.mTitle.setText(summary.getTitle());

        RTMovieQueryRatings ratings = summary.getRatings();

        int criticsScore = ratings.getCriticsScore();
        if(criticsScore >= 60){
            viewHolder.mCriticTextViewFresh.setVisibility(View.VISIBLE);
            viewHolder.mCriticTextViewRotten.setVisibility(View.GONE);
            viewHolder.mCriticTextViewFresh.setText(criticsScore + "%");
        }else if(criticsScore > 0){
            viewHolder.mCriticTextViewFresh.setVisibility(View.GONE);
            viewHolder.mCriticTextViewRotten.setVisibility(View.VISIBLE);
            viewHolder.mCriticTextViewRotten.setText(criticsScore + "%");
        }else{
            viewHolder.mCriticTextViewFresh.setVisibility(View.GONE);
            viewHolder.mCriticTextViewRotten.setVisibility(View.INVISIBLE);
        }

        final int audienceScore = ratings.getAudienceScore();
        if(audienceScore >= 60){
            viewHolder.mAudienceTextViewFresh.setVisibility(View.VISIBLE);
            viewHolder.mAudienceTextViewRotten.setVisibility(View.GONE);
            viewHolder.mAudienceTextViewFresh.setText(audienceScore + "%");
        }else if(audienceScore > 0){
            viewHolder.mAudienceTextViewFresh.setVisibility(View.GONE);
            viewHolder.mAudienceTextViewRotten.setVisibility(View.VISIBLE);
            viewHolder.mAudienceTextViewRotten.setText(audienceScore + "%");
        }else{
            viewHolder.mAudienceTextViewFresh.setVisibility(View.GONE);
            viewHolder.mAudienceTextViewRotten.setVisibility(View.INVISIBLE);
        }
    }
}
