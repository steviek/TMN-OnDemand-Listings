package com.sixbynine.movieoracle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sixbynine.movieoracle.list.MoviesListFragment;
import com.sixbynine.movieoracle.ui.fragment.ActionBarListFragment;
@Deprecated
public class MenuFragment extends ActionBarListFragment{
	private MainActivity parent;
	private static final int MOVIES_INDEX = 0;
	private static final int SERIES_INDEX = 1;
	private static final int ABOUT_INDEX = 2;
	private static final int SETTINGS_INDEX = 3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setAdapter(new MenuFragmentListAdapter());

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof MainActivity){
			parent = (MainActivity) activity;
		}else{
			throw new IllegalStateException("MenuFragment must be created by MainActivity");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		parent = null;
	}
	
	
	private class MenuFragmentListAdapter extends BaseAdapter{
		
		
		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = MenuFragment.this.parent.getLayoutInflater().inflate(R.layout.row_menu, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
			TextView textView = (TextView) view.findViewById(R.id.textView);
			int resId;
			int stringId;
			switch(position){
			case MOVIES_INDEX:
				resId = R.drawable.movie_reel;
				stringId = R.string.movies;
				break;
			case SERIES_INDEX:
				resId = R.drawable.comic_tv;
				stringId = R.string.series;
				break;
			case ABOUT_INDEX:
				resId = R.drawable.ic_action_about;
				stringId = R.string.menu_about;
				break;
			case SETTINGS_INDEX:
				resId = R.drawable.ic_action_settings;
				stringId = R.string.menu_settings;
				break;
			default:
				resId = 0;
				stringId = 0;
			}
			
			imageView.setImageResource(resId);
			textView.setText(getResources().getString(stringId));
			return view;
		}
	
		
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Fragment newContent = null;
		switch(position){
		case MOVIES_INDEX:
			newContent = new MoviesListFragment();
			break;
		case SERIES_INDEX:
			newContent = new SeriesListFragment();
			break;
		case ABOUT_INDEX:
			newContent = new AboutFragment();
			break;
		case SETTINGS_INDEX:
			newContent = new SettingsFragment();
			break;
		}
		if(newContent != null && parent != null){
			parent.switchContent(newContent);
		}
	}
	
}
