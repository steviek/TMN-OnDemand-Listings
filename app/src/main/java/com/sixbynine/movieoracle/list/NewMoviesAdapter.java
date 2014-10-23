package com.sixbynine.movieoracle.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.media.Media;

import java.util.List;

/**
 * Created by steviekideckel on 10/22/14.
 */
public class NewMoviesAdapter extends RecyclerView.Adapter<NewMoviesAdapter.ViewHolder>{
    List<Media> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public NewMoviesAdapter(List<Media> dataset){
        mDataSet = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_media_card, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder myViewHolder, int position) {
            //do stuff
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
