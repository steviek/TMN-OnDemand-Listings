package com.sixbynine.movieoracle.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Rating;

public class TopRatedMoviesListAdapter extends ArrayAdapter<Media>{
	private Activity context;
	private List<Media> movies;
	

	public TopRatedMoviesListAdapter(Activity context, List<Media> values){
		super(context, R.layout.row_movie_top_rated, values);
		this.context = context;
		this.movies = values;
	}
	
	private static class ViewHolder{
		public TextView textViewTitle;
		public ImageView imgCritic;
		public TextView txtCritic;
		public ImageView imgAudience;
		public TextView txtAudience;
		public LinearLayout ratingsInset;
		public TextView textViewDescription;
		public TextView textViewCast;
		public ViewHolder(TextView textViewTitle, ImageView imgCritic,
				TextView txtCritic, ImageView imgAudience,
				TextView txtAudience, LinearLayout ratingsInset,
				TextView description, TextView cast) {
			this.textViewTitle = textViewTitle;
			this.imgCritic = imgCritic;
			this.txtCritic = txtCritic;
			this.imgAudience = imgAudience;
			this.txtAudience = txtAudience;
			this.ratingsInset = ratingsInset;
			this.textViewDescription = description;
			this.textViewCast = cast;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		if(convertView == null){
			rowView = context.getLayoutInflater().inflate(R.layout.row_movie_top_rated, null);
			ViewHolder holder = new ViewHolder((TextView) rowView.findViewById(R.id.textViewMovieTitle),
					(ImageView) rowView.findViewById(R.id.imgCritic),
					(TextView) rowView.findViewById(R.id.txtCritic),
					(ImageView) rowView.findViewById(R.id.imgAudience),
					(TextView) rowView.findViewById(R.id.txtAudience),
					(LinearLayout) rowView.findViewById(R.id.ratingsInset),
					(TextView) rowView.findViewById(R.id.description),
					(TextView) rowView.findViewById(R.id.cast));
					
			rowView.setTag(holder);
		}else{
			rowView = convertView;
		}
		
		Media movie = movies.get(position);
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.textViewTitle.setText(movie.getTitle());
		if(movie.hasCriticRating()){
			holder.imgCritic.setVisibility(View.VISIBLE);
			holder.txtCritic.setVisibility(View.VISIBLE);
			Rating rating = movie.getCriticRating();
			if(rating.getRating() >= 60){
				holder.imgCritic.setImageResource(R.drawable.fresh);
			}else{
				holder.imgCritic.setImageResource(R.drawable.rotten);
			}
			holder.txtCritic.setText(((int) rating.getRating()) + "%");
		}else{
			holder.imgCritic.setVisibility(View.INVISIBLE);
			holder.txtCritic.setVisibility(View.INVISIBLE);
		}
		if(movie.hasAudienceRating()){
			holder.imgAudience.setVisibility(View.VISIBLE);
			holder.txtAudience.setVisibility(View.VISIBLE);
			Rating rating = movie.getAudienceRating();
			if(rating.getRating() >= 60){
				holder.imgAudience.setImageResource(R.drawable.popcorn);
			}else{
				holder.imgAudience.setImageResource(R.drawable.badpopcorn);
			}
			holder.txtAudience.setText(((int) rating.getRating()) + "%");
		}else{
			holder.imgAudience.setVisibility(View.INVISIBLE);
			holder.txtAudience.setVisibility(View.INVISIBLE);
		}
		holder.ratingsInset.getLayoutParams().width = (context.getWindowManager().getDefaultDisplay().getWidth() * 100) / 360;
		
		holder.textViewDescription.setText(movie.getSynopsis());
		List<String> cast = movie.getCast();
		StringBuilder castString = new StringBuilder();
		for(String member : cast){
			castString.append(member);
			castString.append("\n");
		}
		holder.textViewCast.setText(castString.toString());
		
		
		return rowView;
	}

	
	
	
	
}
