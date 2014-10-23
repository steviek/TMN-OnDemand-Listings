package com.sixbynine.movieoracle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sixbynine.movieoracle.dataprocessor.DataProcessor;
import com.sixbynine.movieoracle.dataprocessor.MovieNetworkDataProcessor;
import com.sixbynine.movieoracle.dataprocessor.WebResources;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.sql.allmedia.AllMediaDAO;
import com.sixbynine.movieoracle.sql.ondemandlistings.OnDemandListingsDAO;
import com.sixbynine.movieoracle.util.CatalogueHolder;
import com.sixbynine.movieoracle.util.Prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends ActionBarActivity implements SplashActivityCallback{

	private Catalogue catalogue;
	private Catalogue allMediaDB;
	private Catalogue onDemandListings;
	
	private MovieNetworkDataProcessor mndp;
	
	private boolean listingsLoaded;
	private boolean mediaDataLoaded;
	//private boolean internet;
	
	private DataProcessor movieDP;
	private DataProcessor seriesDP;
	private ProgressBar pb;
	
	public static int checkOnDemandFrequency;
	public static int showTopRatedNumber;
	public static SplashActivityCallback cb;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		pb = (ProgressBar) findViewById(R.id.pb_main_activity);
		cb = this;
		checkOnDemandFrequency = 3;
		showTopRatedNumber = 10;
		loadSettings();
		if(!mediaDataLoaded) {
			pb.setVisibility(View.VISIBLE);
			pb.setMax(100);
			loadListings(false);
		}else{
			callMain();
			pb.setVisibility(View.GONE);
		}
	}
	
	

	@Override
	protected void onRestart() {
		super.onRestart();
		if(!mediaDataLoaded) {
			pb.setVisibility(View.VISIBLE);
			pb.setMax(100);
		//	loadListings(false);
		}else{
			callMain();
			pb.setVisibility(View.GONE);
		}
		
	}


	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_splash, menu);
		return true;
	}

	
	
	private boolean loadListings(boolean checkEvenIfNotStale){
		
		
		 //internet = hasInternet();
			   
		 if(checkEvenIfNotStale && !hasInternet()){
			 Toast.makeText(getApplicationContext(), "No network connection.", Toast.LENGTH_LONG).show();
			 return false;
		 }
		 pb.setVisibility(View.VISIBLE);
		 ((TextView) findViewById(R.id.tv_main_activity)).setVisibility(View.VISIBLE);
		 
		 
		 listingsLoaded = false;
		 mediaDataLoaded = false;
		 
		 catalogue = new Catalogue(); //initialize the catalogue
		 allMediaDB = new Catalogue();
		 onDemandListings = new Catalogue();
		 
		
		 if(hasInternet() && (checkEvenIfNotStale || onDemandListingsAreStale())){
			 
			 WebResources.loadResources(new WebResources.Callback() {
				
				@Override
				public void onSuccess() {
					((TextView) findViewById(R.id.tv_main_activity)).setText("Checking the internet for listings.");
					 mndp = new MovieNetworkDataProcessor(SplashActivity.this); // will initialize an object and fetches the data from the movie network site
					 mndp.repopulate();
				}
				
				@Override
				public void onFailure() {
					Toast.makeText(getApplicationContext(), "An error occurred updating the listings.", Toast.LENGTH_SHORT).show();
					notifyChange(SplashActivityCallback.MOVIE_NETWORK_DATABASE_FINISHED);
				}
			});
			 
		 }else{
			 //TODO: make call to async task
			 
			this.notifyChange(SplashActivityCallback.MOVIE_NETWORK_DATABASE_FINISHED);
		 }

		 return hasInternet();
}

	public boolean hasInternet(){
		ConnectivityManager connMgr = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		 return (networkInfo != null && networkInfo.isConnected());
		 
	}
	
	/* DATA PROCESSOR CALLBACK METHODS */
	@Override
	public void notifyChange(int status) {
		TextView tv = (TextView) findViewById(R.id.tv_main_activity);
		
		if(status == SplashActivityCallback.MOVIE_NETWORK_INTERNET_FINISHED){
			onDemandListings = mndp.retrieveListings();
			listingsLoaded = true;
			saveUpdateDate();
			
			if(!OnDemandListingsDAO.getInstance(getApplicationContext()).saved()) OnDemandListingsDAO.getInstance(getApplicationContext()).insertCatalogue(onDemandListings);
			AllMediaDAO.init(getApplicationContext(), this); //start loading data	
		}else if(status == SplashActivityCallback.MOVIE_NETWORK_DATABASE_FINISHED){
			onDemandListings = OnDemandListingsDAO.getInstance(getApplicationContext()).getCatalogue();
			listingsLoaded = true;
			//tv.setText("Loading data from the database");
			//tv.setVisibility(View.VISIBLE);
			AllMediaDAO.init(getApplicationContext(), this); //start loading data
			
		}else if(status == SplashActivityCallback.NEW_UNMATCHED_MEDIA_RETRIEVED){
			pb.setProgress((movieDP.getProgress() + seriesDP.getProgress())/2);
			
			tv.setText("Retrieving data for new movies and series."
					+ "\n\nIf this is the first time running the app this may take a couple of minutes.");
		}else if(status == SplashActivityCallback.NEW_ALL_MEDIA_DATABASE_ENTRY_LOADED){
			AllMediaDAO dao = AllMediaDAO.getInstance();
			if(dao.isSaved()){
				pb.setProgress(pb.getMax());
			}else{
				pb.setProgress(dao.getProgress());
			}
			tv.setText("Retrieving saved data for movies and series.");
		}else if(status == SplashActivityCallback.ALL_MEDIA_CATALOGUE_DATABASE_FINISHED ){
			pb.setProgress(0);
			allMediaDB = AllMediaDAO.getInstance().getCatalogue();
			Catalogue[] mergeResult = Catalogue.merge(onDemandListings, allMediaDB);
			catalogue = mergeResult[0];
			
			if(hasInternet()){
				//if(movieDP == null || !movieDP.isInitialized()) {
					movieDP = null;
					seriesDP = null;
					movieDP = DataProcessor.createDataProcessor(DataProcessor.ROTTEN_TOMATOES_DATA_PROCESSOR, this);
					movieDP.populate(getResources(), mergeResult[1].getMovies());
				//}
				//if(seriesDP == null || !seriesDP.isInitialized()) {
					seriesDP = DataProcessor.createDataProcessor(DataProcessor.TVDB_DATA_PROCESSOR, this);
					seriesDP.populate(getResources(),mergeResult[1].getSeries());
				//}
			}else{
				catalogue.addAll(mergeResult[1]);
				this.notifyChange(UNMATCHED_MEDIA_FINISHED);
			}
		}else if(status == SplashActivityCallback.UNMATCHED_MEDIA_FINISHED){
			if(hasInternet()){
				if(movieDP == null || seriesDP == null) return;
				mediaDataLoaded = movieDP.isFinishedLoading() && seriesDP.isFinishedLoading();
				if(mediaDataLoaded){
					catalogue.addAll(movieDP.retrieveCatalogue());
					catalogue.addAll(seriesDP.retrieveCatalogue());
					AllMediaDAO.getInstance().saveCatalogue(catalogue.trimDuplicates());
					tv.setText("");
					tv.setVisibility(View.GONE);
					pb.setProgress(pb.getMax());
					pb.setVisibility(View.GONE);
					((ImageView) findViewById(R.id.imageView1)).setVisibility(View.GONE);
					catalogue = catalogue.trimDuplicates();
					callMain();
					//Toast.makeText(getApplicationContext(), "The data has finished loading.", Toast.LENGTH_SHORT).show();
				}
			}else{
				mediaDataLoaded = true;
				tv.setText("");
				tv.setVisibility(View.INVISIBLE);
				pb.setProgress(pb.getMax());
				pb.setVisibility(View.INVISIBLE);
				((ImageView) findViewById(R.id.imageView1)).setVisibility(View.GONE);
				catalogue = catalogue.trimDuplicates();
				callMain();
				//Toast.makeText(getApplicationContext(), "The data has finished loading.", Toast.LENGTH_SHORT).show();
			}
			
			
			
		}
	}
	
	public void callMain(){
		CatalogueHolder.getInstance().setCatalogue(catalogue);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("catalogue", (Parcelable) catalogue);
		//intent.putExtra(MainActivity.FREQUENCY, checkOnDemandFrequency);
		//intent.putExtra(MainActivity.NUM_TOP_RATED, showTopRatedNumber);
		startActivity(intent);
		finish();
	}
	
	public void loadSettings(){
		checkOnDemandFrequency = Prefs.getCheckingFrequency(this, 3);
		showTopRatedNumber = Prefs.getNumTopRated(this, 10);
	}
	
	private boolean onDemandListingsAreStale(){
		if(!Prefs.did08092014BugFix(this)){
			return true;
		}
		
		String oldDate = Prefs.getLastUpdateDate(this);
		if(oldDate == null){
			return true;
		}else{
			String currentDate = getDateStamp();
			return isStaleCompare(oldDate, currentDate);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getDateStamp(){
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}
	
	private void saveUpdateDate() {
		Prefs.saveLastUpdateDate(this, getDateStamp());
	}
	
	
	@SuppressLint("SimpleDateFormat")
	private boolean isStaleCompare(String oldDate, String newDate){
		if(BuildConfig.DEBUG) Log.i(((Object) this).getClass().getName(), "The data was last updated on " + oldDate
						+ "\nThe current date is " + newDate);
		try{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date od = dateFormat.parse(oldDate);
		Date nd = dateFormat.parse(newDate);
		int daysBetween = (int) ((nd.getTime() - od.getTime()) / (1000 * 60 * 60 * 24));
		return daysBetween >= checkOnDemandFrequency;
		
		}catch(Exception e){
			return true;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(BuildConfig.DEBUG) Log.i(((Object) this).getClass().getName(), "onDestroy() called");
		movieDP = null;
		seriesDP = null;
	}
	
	/* Prevent app from being killed on back */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        else {
            // Return
            return super.onKeyDown(keyCode, event);
        }
    }
	
}
