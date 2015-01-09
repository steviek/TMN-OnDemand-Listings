package com.sixbynine.movieoracle.home;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.object.RottenTomatoesRatings;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class SummaryListAdapter extends ArrayAdapter<RottenTomatoesSummary> {
    private ArrayList<RottenTomatoesSummary> mSummaries;
    private int mSelected = -1;

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

    public SummaryListAdapter(Context context, ArrayList<RottenTomatoesSummary> summaries){
        super(context, R.layout.row_movie_new, summaries);
        mSummaries = summaries;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie_new, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }



        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        bindView(viewHolder, position);

        /*if(position == mSelected){
            if(Build.VERSION.SDK_INT >= 16) {
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.selected_background));
            }else{
                convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selected_background));
            }
        }else{
            if(Build.VERSION.SDK_INT >= 16) {
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.selector_white));
            }else{
                convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_white));
            }
        }*/

        return convertView;
    }

    public void bindView(ViewHolder viewHolder, int position) {
        final RottenTomatoesSummary summary = mSummaries.get(position);
        viewHolder.mTitle.setText(summary.getTitle());

        final RottenTomatoesRatings ratings = summary.getRatings();

        final int criticsScore = ratings.getCriticsScore();
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

    @Override
    public int getCount() {
        if(mSummaries == null){
            return 0;
        }else{
            return mSummaries.size();
        }
    }

    public void setSelectedIndex(int index){
        mSelected = index;
        notifyDataSetChanged();
    }
}
