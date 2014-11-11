package com.sixbynine.movieoracle.sql.ondemandlistings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.sql.ondemandlistings.OnDemandListingsContract.OnDemandListings;
import com.sixbynine.movieoracle.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnDemandListingsDAO {
	private static OnDemandListingsDAO me;
	private OnDemandListingsHelper helper;
	private boolean saved;
	private Catalogue catalogue;
	
	
	public OnDemandListingsDAO(Context context) {
		helper = new OnDemandListingsHelper(context);
		saved = false;
	}
	
	public static OnDemandListingsDAO getInstance(Context context){
		if(me == null){
			me = new OnDemandListingsDAO(context);
		}
		return me;
	}
	
	public boolean saved(){
		return saved;
	}
	
	public static void destroy(){
		me = null;
	}
	
	public void insertMovie(String title){
		List<String> titles = new ArrayList<String>();
		titles.add(title);
		insertMovies(titles);
	}
	
	public void insertMovies(List<String> titles){
		Catalogue catalogue = new Catalogue();
		for(String title : titles){
			catalogue.add(new Movie(title));
		}
		insertCatalogue(catalogue);
	}
	
	public void insertMedia(Media m){
		Catalogue catalogue = new Catalogue();
		catalogue.add(m);
		insertCatalogue(catalogue);
	}
	
	public void insertCatalogue(Catalogue catalogue){
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(OnDemandListings.TABLE_NAME, null, null);
		
		for(Media m : catalogue){
			
			
			int movie = m instanceof Movie? 1 : 0;
			
			ContentValues values = new ContentValues();
			values.put(OnDemandListings.COLUMN_NAME_TITLE, m.getTitle());
			values.put(OnDemandListings.COLUMN_NAME_MOVIE, movie);
			if(movie == 1){
				values.put(OnDemandListings.COLUMN_NAME_EPISODES, "");
			}else{
				values.put(OnDemandListings.COLUMN_NAME_EPISODES, ((Series) m).getEpisodesString());
			}
			
			
			if(db.insert(OnDemandListings.TABLE_NAME, null, values) == -1){
				Logger.w("Failed to store " + m.getTitle() + " in " + OnDemandListings.TABLE_NAME);
			}
		}
		saved = true;
	}
	
	public Catalogue getCatalogue(){
		if(catalogue == null) catalogue = readCatalogue();
		return catalogue;
	}
	
	private Catalogue readCatalogue(){
		Catalogue result = new Catalogue();
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String[] projection = {
				BaseColumns._ID,
				OnDemandListings.COLUMN_NAME_TITLE,
				OnDemandListings.COLUMN_NAME_MOVIE,
				OnDemandListings.COLUMN_NAME_EPISODES
		};
		
		Cursor cursor = db.query(OnDemandListings.TABLE_NAME, projection, null, null, null, null, null);
		//Cursor cursor = db.rawQuery("SELECT * FROM " + OnDemandListings.TABLE_NAME, new String[] {});
		
		cursor.moveToFirst();
		int titleId = cursor.getColumnIndex(OnDemandListings.COLUMN_NAME_TITLE);
		int movieId = cursor.getColumnIndex(OnDemandListings.COLUMN_NAME_MOVIE);
		int episodeId = cursor.getColumnIndex(OnDemandListings.COLUMN_NAME_EPISODES);
		
		while(!cursor.isAfterLast()){
			
			String title = cursor.getString(titleId);
			int movie = cursor.getInt(movieId);
			String episode = cursor.getString(episodeId);
			
			if(movie == 1){
				Movie m = new Movie(title);
				Log.i(this.getClass().getName(), "Read " + m + " from the table.");
				result.add(m);
			}else{
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				List<String> epList = new ArrayList<String>();
				String[] episodes = episode.split(";");
				for(String e : episodes){
					epList.add(e);
				}
				map.put(title, epList);
				result.addAll(Series.processSeries(map));

			}
			
			cursor.moveToNext();
		}
		
		
		return result;
		
		
	}

}
