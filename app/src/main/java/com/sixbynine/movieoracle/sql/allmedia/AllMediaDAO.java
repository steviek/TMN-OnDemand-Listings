package com.sixbynine.movieoracle.sql.allmedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.movieoracle.SplashActivityCallback;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Rating;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.sql.allmedia.AllMediaContract.AllMedia;

public class AllMediaDAO {
	private static AllMediaDAO me;
	private AllMediaHelper helper;
	
	private boolean initialized;
	private boolean saved;
	private boolean loaded;
	private Catalogue catalogue;
	private SplashActivityCallback cb;
	
	private int current;
	private int total;
	
	public AllMediaDAO() {
		saved = false;
		loaded = false;
		current = 0;
		total = 0;
		initialized = false;
		
		
	}
	
	private void start(){
		new loadFromDatabase().execute();
	}
	
	public static void init(Context context, SplashActivityCallback cb){
		if(me == null) me = new AllMediaDAO();
		if(me.isInitialized()){
			if(me.loaded) me.cb.notifyChange(SplashActivityCallback.ALL_MEDIA_CATALOGUE_DATABASE_FINISHED);
			return;
		}
		me.setHelper(new AllMediaHelper(context));
		me.bindCallBack(cb);
		me.initialized = true;
		me.start();
	}
	
	private void setHelper(AllMediaHelper helper){
		this.helper = helper;
	}
	
	public void bindCallBack(SplashActivityCallback cb){
		this.cb = cb;
	}
	
	public static AllMediaDAO getInstance(){
		return me;
	}
	
	public static void destroy(){
		me = null;
	}
	
	public boolean isSaved(){
		return saved;
	}
	
	public void saveCatalogue(Catalogue catalogue){
		new saveToDatabase().execute(catalogue);
	}
	
	public String getStatus(){
		return "Loading data: " + current + " of " + total; 
	}
	
	public int getProgress(){
		return (current * 100) / total;
	}
	
	public boolean isInitialized(){
		return initialized;
	}
	
	
	
	public Catalogue getCatalogue(){
		return catalogue;
	}
	
	
	
	private boolean inDatabase(Media m){
		try{
			SQLiteDatabase db = helper.getReadableDatabase();
			
			Cursor cursor = db.rawQuery("SELECT " + AllMedia.COLUMN_NAME_TITLE 
					+ " FROM " + AllMedia.TABLE_NAME 
					+ " WHERE " + AllMedia.COLUMN_NAME_TITLE + " = ?", new String[]{m.getTitle()});
			
			int count = cursor.getCount();
			return count >= 1;
		}catch(SQLiteException e){
			return false;
		}
		
	}
	
	private class loadFromDatabase extends AsyncTask<Void, Void, Catalogue>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			initialized = true;
		}

		@Override
		protected Catalogue doInBackground(Void... params) {
			Catalogue result = readCatalogue();
			return result;
		}
		
		@Override
		protected void onPostExecute(Catalogue result) {
			super.onPostExecute(result);
			catalogue = result;
			loaded = true;
			cb.notifyChange(SplashActivityCallback.ALL_MEDIA_CATALOGUE_DATABASE_FINISHED);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			cb.notifyChange(SplashActivityCallback.NEW_ALL_MEDIA_DATABASE_ENTRY_LOADED);
		}
		
		private Catalogue readCatalogue(){
			Catalogue result = new Catalogue();
			SQLiteDatabase db = helper.getReadableDatabase();

			Cursor cursor = db.rawQuery("SELECT * FROM " + AllMedia.TABLE_NAME, new String[] {});
			
			cursor.moveToFirst();
			
			total = cursor.getCount();
			
			int titleIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_TITLE);
			int yearIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_YEAR);
			int genresIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_GENRES);
			int castIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_CAST);
			int directorsIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_DIRECTORS);
			int languagesIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_LANGUAGES);
			int synopsisIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_SYNOPSIS);
			int parentalAdvisoryIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_PARENTAL_ADVISORY);
			int runtimeIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_RUNTIME);
			int ratingsIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_RATINGS);
			int idsIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_IDS);
			int seriesIndex = cursor.getColumnIndex(AllMedia.COLUMN_NAME_SERIES);
			
			while(!cursor.isAfterLast()){
				
				String title = cursor.getString(titleIndex);
				Log.i(this.getClass().getName(), "Loading [" + title + "] from " + AllMedia.TABLE_NAME);
				boolean series = cursor.getInt(seriesIndex) == 1;
				Media m = series? new Series(title) : new Movie(title);
				
				String year = cursor.getString(yearIndex);
				if(year != null && !year.equals("")) m.setYear(year);
				
				String[] genres = cursor.getString(genresIndex).split(",");
				for(String genre : genres){
					m.addGenre(genre);
				}
				
				String[] cast = cursor.getString(castIndex).split(",");
				for(String castMember : cast){
					m.addCastMember(castMember);
				}
				
				String[] directors = cursor.getString(directorsIndex).split(",");
				for(String director : directors){
					m.addDirector(director);
				}
				
				
				String[] languages = cursor.getString(languagesIndex).split(",");
				for(String language : languages){
					m.addLanguage(language);
				}
				
				
				String synopsis = cursor.getString(synopsisIndex);
				if(synopsis != null && !synopsis.equals("")) m.setSynopsis(synopsis);
				
				String parentalAdvisory = cursor.getString(parentalAdvisoryIndex);
				if(parentalAdvisory != null && !parentalAdvisory.equals("")) m.setParentalAdvisory(parentalAdvisory);
				
				String runtime = cursor.getString(runtimeIndex);
				if(runtime != null && !runtime.equals("")) m.setRuntime(runtime);
				
				String[] ratings = cursor.getString(ratingsIndex).split(",");
				for(String rating : ratings){
					m.addRating(Rating.parseRating(rating));
				}
				
				String[] ids = cursor.getString(idsIndex).split(",");
				for(String id : ids){
					String[] vals = id.split(";");
					if(vals.length == 2){
						m.addId(vals[0], vals[1]);
					}
				}
				result.add(m);
				current ++;
				cursor.moveToNext();
				publishProgress();
			}
			
			
			return result;
		}

		
		
	}

	private class saveToDatabase extends AsyncTask<Catalogue, Void, Void>{

		@Override
		protected Void doInBackground(Catalogue... params) {
			insertCatalogue(params[0]);
			return null;
			
		}
		
		public void insertCatalogue(Catalogue catalogue){
			if(catalogue == null) return;
			SQLiteDatabase db = helper.getWritableDatabase();
			
			for(Media m : catalogue){
				if(inDatabase(m)){
					Log.i(this.getClass().getName(), m.getTitle() + " is already in the table.");
					continue;
				}
				
				ContentValues values = new ContentValues();
				values.put(AllMedia.COLUMN_NAME_TITLE, m.getTitle());
				values.put(AllMedia.COLUMN_NAME_CAST, m.getCastString());
				values.put(AllMedia.COLUMN_NAME_DIRECTORS, m.getDirectorsString());
				values.put(AllMedia.COLUMN_NAME_GENRES, m.getGenresString());
				values.put(AllMedia.COLUMN_NAME_SYNOPSIS, m.getSynopsis());
				values.put(AllMedia.COLUMN_NAME_YEAR, m.getYear());
				values.put(AllMedia.COLUMN_NAME_LANGUAGES, m.getLanguagesString());
				values.put(AllMedia.COLUMN_NAME_PARENTAL_ADVISORY, m.getParentalAdvisory());
				values.put(AllMedia.COLUMN_NAME_RUNTIME, m.getRuntime());
				values.put(AllMedia.COLUMN_NAME_RATINGS, m.getRatingsString());
				values.put(AllMedia.COLUMN_NAME_IDS, m.getIDString());
				values.put(AllMedia.COLUMN_NAME_SERIES, (m instanceof Series)? "1" : "0");
				long rc = db.insert(AllMedia.TABLE_NAME, null, values);
				if(rc == -1){
					Log.w(this.getClass().getName(), "Failed to store " + m.getTitle() + " into the database");
				}else{
					Log.i(this.getClass().getName(), "Successfully stored " + m.getTitle() + " into " + AllMedia.TABLE_NAME);
				}
			}
			saved = true;
			
		}
		
	}

}
