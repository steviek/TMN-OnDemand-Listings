package com.sixbynine.movieoracle.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.media.Media;

public class SeriesListAdapter extends ArrayAdapter<Media>{
	private Activity context;
	private List<Media> series;
	

	public SeriesListAdapter(Activity context, List<Media> values){
		super(context, R.layout.row_series, values);
		this.context = context;
		this.series = values;
	}
	
	private static class ViewHolder{
		public TextView textViewTitle;
		public ViewHolder(TextView textViewTitle) {
			this.textViewTitle = textViewTitle;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		if(convertView == null){
			rowView = context.getLayoutInflater().inflate(R.layout.row_series, null);
			ViewHolder holder = new ViewHolder((TextView) rowView.findViewById(R.id.textViewMovieTitle));
					
			rowView.setTag(holder);
		}else{
			rowView = convertView;
		}
		
		Media serie = series.get(position);
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.textViewTitle.setText(serie.getTitle());
		
		return rowView;
	}

	
	
	
	
}
