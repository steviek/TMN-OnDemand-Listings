package com.sixbynine.movieoracle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sixbynine.movieoracle.adapter.MoviesListAdapter;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.ui.fragment.ActionBarListFragment;

import java.util.ArrayList;
@Deprecated
public class SeriesListFragment extends ActionBarListFragment {
	private Catalogue allListings;
	private ArrayList<Media> displayListings;
	private ArrayAdapter<Media> mAdapter;
	private MainActivity parent;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MainActivity){
			parent = (MainActivity) activity;
		}else{
			throw new IllegalStateException(activity.getClass().toString() + " must implement be MainActivity");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		parent = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getActivity() != null && parent == null){
			parent = (MainActivity) getActivity();
		}
		allListings = parent.catalogue.getSeries();
		if(allListings != null){
			displayListings = new ArrayList<Media>(allListings);
			mAdapter = new MoviesListAdapter(getActivity(), displayListings);
			this.setListAdapter(mAdapter);
		}
		
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Media m = displayListings.get(position);

		Intent intent = new Intent(getActivity(), MediaInfoActivity.class);
		intent.putExtra("Media", m);
        getActivity().startActivity(intent);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		ActionBar ab = getActionBarActivity().getSupportActionBar();
		ab.setTitle("Series");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
	}
}
