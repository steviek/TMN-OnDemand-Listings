package com.sixbynine.movieoracle.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sixbynine.movieoracle.MainActivity;
import com.sixbynine.movieoracle.MediaInfoActivity;
import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.adapter.MoviesListAdapter;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.ui.fragment.ActionBarListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoviesListFragment extends ActionBarListFragment {
	private Catalogue allListings;
	private ArrayList<Media> displayListings;
	private ArrayAdapter<Media> mAdapter;
	private String az = "A-Z";
	private String genres = "Movies";
	
    public static MoviesListFragment newInstance(Catalogue movies){
        MoviesListFragment frag = new MoviesListFragment();
        Bundle b = new Bundle();
        b.putParcelable("catalogue", movies);
    }

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MainActivity){
			parent = (MainActivity) activity;
		}else{
			throw new IllegalStateException(activity.getClass().toString() + " must be MainActivity");
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
		allListings = parent.catalogue.getMovies();
		if(allListings != null){
			displayListings = new ArrayList<Media>(allListings);
			mAdapter = new MoviesListAdapter(getActivity(), displayListings);
			this.setListAdapter(mAdapter);
		}
		
		
		this.setHasOptionsMenu(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		getActivity().getMenuInflater().inflate(R.menu.activity_movies_list, menu);
		
		SubMenu genres = menu.findItem(R.id.genres).getSubMenu();
		MenuItem all = genres.add("All");
		all.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity().runOnUiThread(new Runnable(){
					public void run(){	
						displayListings.clear();
						displayListings.addAll(allListings);
						mAdapter.notifyDataSetChanged();
						getListView().setSelection(0);
						MoviesListFragment.this.genres = "Movies";
						az = "A-Z";
						refreshTitle();
					}
				});
				return true;
			}
			
			
		});
		
		for(final String genre : allListings.getGenreListing()){
			MenuItem item = genres.add(genre);
			item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					getActivity().runOnUiThread(new Runnable(){
						public void run(){
							displayListings.clear();
							displayListings.addAll(allListings.filterBy(Catalogue.GENRE, genre));
							mAdapter.notifyDataSetChanged();
							getListView().setSelection(0);
							MoviesListFragment.this.genres = genre + " Movies";
							az = "A-Z";
							refreshTitle();
						}
					});
					return true;
				}
				
			});
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.az:
			sortAlphabetically();
			return true;
		case R.id.rating:
			sortByRating();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	
	private void sortByRating() {
		getActivity().runOnUiThread(new Runnable(){
			public void run(){
				Collections.sort(displayListings, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						Double d1 = m1.getRatingAverage();
						Double d2 = m2.getRatingAverage();
						return d2.compareTo(d1);
					}
				});
				mAdapter.notifyDataSetChanged();
				getListView().setSelection(0);
				az = "- by rating";
				refreshTitle();
			}
		});
	}

	private void sortAlphabetically() {
		getActivity().runOnUiThread(new Runnable(){
			public void run(){
				Collections.sort(displayListings, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						return m1.getTitle().compareTo(m2.getTitle());
					}
				});
				mAdapter.notifyDataSetChanged();
				getListView().setSelection(0);
				az = "A-Z";
				refreshTitle();
			}
		});
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
		refreshTitle();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
	}
	
	private void refreshTitle(){
		getActivity().setTitle(genres + " " + az);
		ActionBar ab = getActionBarActivity().getSupportActionBar();
		ab.setTitle(genres + " " + az);
	}
	

	
}
