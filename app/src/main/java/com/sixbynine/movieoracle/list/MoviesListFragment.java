package com.sixbynine.movieoracle.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sixbynine.movieoracle.MediaInfoActivity;
import com.sixbynine.movieoracle.adapter.MoviesListAdapter;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.model.MediaListViewListener;
import com.sixbynine.movieoracle.ui.fragment.ActionBarListFragment;

import java.util.ArrayList;

public class MoviesListFragment extends ActionBarListFragment {
	private Catalogue mCatalogue;
	private ArrayList<Media> mDisplayMedia;
	private ArrayAdapter<Media> mAdapter;
	private static final String A_Z = "A-Z";
	private static final String GENRES = "Movies";
    private MediaListViewListener mListener;

    public static MoviesListFragment newInstance(Catalogue movies){
        MoviesListFragment frag = new MoviesListFragment();
        Bundle b = new Bundle();
        b.putParcelable("catalogue", movies);
        frag.setArguments(b);
        return frag;
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MediaListViewListener){
			mListener = (MediaListViewListener) activity;
		}else{
			throw new IllegalStateException(activity.getClass().toString() + " must implement MediaListViewListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            mCatalogue = getArguments().getParcelable("catalogue");
            mDisplayMedia = new ArrayList<Media>(mCatalogue);
        }else{
            mCatalogue = savedInstanceState.getParcelable("catalogue");
            mDisplayMedia = (Catalogue) savedInstanceState.getParcelable("display");
        }
		
		this.setHasOptionsMenu(true);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new MoviesListAdapter(getActivity(), mDisplayMedia);
        setListAdapter(mAdapter);

    }

   /* @Override
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
						mDisplayMedia.clear();
						mDisplayMedia.addAll(mCatalogue);
						mAdapter.notifyDataSetChanged();
						getListView().setSelection(0);
						MoviesListFragment.this.GENRES = "Movies";
						A_Z = "A-Z";
						refreshTitle();
					}
				});
				return true;
			}
			
			
		});
		
		for(final String genre : mCatalogue.getGenreListing()){
			MenuItem item = genres.add(genre);
			item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					getActivity().runOnUiThread(new Runnable(){
						public void run(){
							mDisplayMedia.clear();
							mDisplayMedia.addAll(mCatalogue.filterBy(Catalogue.GENRE, genre));
							mAdapter.notifyDataSetChanged();
							getListView().setSelection(0);
							MoviesListFragment.this.GENRES = genre + " Movies";
							A_Z = "A-Z";
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
				Collections.sort(mDisplayMedia, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						Double d1 = m1.getRatingAverage();
						Double d2 = m2.getRatingAverage();
						return d2.compareTo(d1);
					}
				});
				mAdapter.notifyDataSetChanged();
				getListView().setSelection(0);
				A_Z = "- by rating";
				refreshTitle();
			}
		});
	}

	private void sortAlphabetically() {
		getActivity().runOnUiThread(new Runnable(){
			public void run(){
				Collections.sort(mDisplayMedia, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						return m1.getTitle().compareTo(m2.getTitle());
					}
				});
				mAdapter.notifyDataSetChanged();
				getListView().setSelection(0);
				A_Z = "A-Z";
				refreshTitle();
			}
		});
	}*/

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Media m = mDisplayMedia.get(position);

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
		getActivity().setTitle(GENRES + " " + A_Z);
		ActionBar ab = getActionBarActivity().getSupportActionBar();
		ab.setTitle(GENRES + " " + A_Z);
	}
	

	
}
