package com.sixbynine.movieoracle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.sixbynine.movieoracle.home.HomeActivity;
import com.sixbynine.movieoracle.manager.RottenTomatoesManager;
import com.sixbynine.movieoracle.manager.UpdateEvent;
import com.sixbynine.movieoracle.manager.UpdateListener;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.sql.allmedia.AllMediaDAO;
import com.sixbynine.movieoracle.sql.ondemandlistings.OnDemandListingsDAO;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;
import com.sixbynine.movieoracle.util.Prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SplashActivity extends BaseActivity implements SplashActivityCallback, UpdateListener{

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
    private TextView mProgressNumberTextView;
    private TextView mProgressTextView;
	
	public static SplashActivityCallback cb;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		pb = (ProgressBar) findViewById(R.id.progress_bar);
		cb = this;
		if(!mediaDataLoaded) {
            mProgressNumberTextView = (TextView) findViewById(R.id.progress_number_text_view);
            mProgressTextView = (TextView) findViewById(R.id.progress_text_view);
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
    protected void onResume() {
        super.onResume();
        RottenTomatoesManager.getInstance().subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RottenTomatoesManager.getInstance().unSubscribe(this);
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
         mProgressTextView.setVisibility(View.VISIBLE);
		 
		 
		 listingsLoaded = false;
		 mediaDataLoaded = false;
		 
		 catalogue = new Catalogue(); //initialize the catalogue
		 allMediaDB = new Catalogue();
		 onDemandListings = new Catalogue();
		 
		
		 if(hasInternet() && (checkEvenIfNotStale || onDemandListingsAreStale())){
			 WebResources.loadResources(new WebResources.Callback() {
				
				@Override
				public void onSuccess() {
					((TextView) findViewById(R.id.progress_text_view)).setText("Checking the internet for listings.");
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
             Intent intent = new Intent(this, HomeActivity.class);
             //intent.putParcelableArrayListExtra("summaries", Prefs.getCurrentSummaries());
             startActivity(intent);
             finish();
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
		if(status == SplashActivityCallback.MOVIE_NETWORK_INTERNET_FINISHED){
			onDemandListings = mndp.retrieveListings();
			listingsLoaded = true;
			saveUpdateDate();
			
			if(!OnDemandListingsDAO.getInstance(getApplicationContext()).saved()) OnDemandListingsDAO.getInstance(getApplicationContext()).insertCatalogue(onDemandListings);
			AllMediaDAO.init(getApplicationContext(), this); //start loading data	
		}else if(status == SplashActivityCallback.MOVIE_NETWORK_DATABASE_FINISHED){
			onDemandListings = OnDemandListingsDAO.getInstance(getApplicationContext()).getCatalogue();
			listingsLoaded = true;
			AllMediaDAO.init(getApplicationContext(), this); //start loading data
			
		}else if(status == SplashActivityCallback.NEW_UNMATCHED_MEDIA_RETRIEVED){
            mProgressTextView.setText("Retrieving data for new movies and series."
					+ "\n\nIf this is the first time running the app this may take a couple of minutes.");
		}else if(status == SplashActivityCallback.NEW_ALL_MEDIA_DATABASE_ENTRY_LOADED){
			AllMediaDAO dao = AllMediaDAO.getInstance();
            mProgressTextView.setText("Retrieving saved data for movies and series.");
		}else if(status == SplashActivityCallback.ALL_MEDIA_CATALOGUE_DATABASE_FINISHED ){
			allMediaDB = AllMediaDAO.getInstance().getCatalogue();
			Catalogue[] mergeResult = Catalogue.merge(onDemandListings, allMediaDB);
			catalogue = mergeResult[0];
			
			if(hasInternet()){
				//if(movieDP == null || !movieDP.isInitialized()) {
					movieDP = null;
					seriesDP = null;
					movieDP = DataProcessor.createDataProcessor(DataProcessor.ROTTEN_TOMATOES_DATA_PROCESSOR, this);
                RottenTomatoesManager.getInstance().loadListings(mergeResult[1].getMovies());
                //movieDP.populate(getResources(), mergeResult[1].getMovies());

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
					//AllMediaDAO.getInstance().saveCatalogue(catalogue.trimDuplicates());
                    mProgressTextView.setText("");
                    mProgressTextView.setVisibility(View.GONE);
					pb.setVisibility(View.GONE);
					((ImageView) findViewById(R.id.imageView1)).setVisibility(View.GONE);
					catalogue = catalogue.trimDuplicates();
					callMain();
					//Toast.makeText(getApplicationContext(), "The data has finished loading.", Toast.LENGTH_SHORT).show();
				}
			}else{
				mediaDataLoaded = true;
                mProgressTextView.setText("");
                mProgressTextView.setVisibility(View.INVISIBLE);
				pb.setVisibility(View.INVISIBLE);
				((ImageView) findViewById(R.id.imageView1)).setVisibility(View.GONE);
				catalogue = catalogue.trimDuplicates();
				callMain();
				//Toast.makeText(getApplicationContext(), "The data has finished loading.", Toast.LENGTH_SHORT).show();
			}
			
			
			
		}
	}
	
	public void callMain(){
		/*CatalogueHolder.getInstance().setCatalogue(catalogue);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("catalogue", (Parcelable) catalogue);
		//intent.putExtra(MainActivity.FREQUENCY, checkOnDemandFrequency);
		//intent.putExtra(MainActivity.NUM_TOP_RATED, showTopRatedNumber);
		startActivity(intent);
		finish();*/
	}
	
	private boolean onDemandListingsAreStale(){
		String oldDate = Prefs.getLastUpdateDate();
		if(oldDate == null){
			return true;
		}else{
			return Integer.parseInt(getLastTuesday()) - Integer.parseInt(oldDate) > 0;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getDateStamp(){
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

    private String getLastTuesday(){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int[] days;
        switch(cal.get(Calendar.DAY_OF_WEEK)){
            case Calendar.TUESDAY:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 0);
                break;
            case Calendar.WEDNESDAY:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 1);
                break;
            case Calendar.THURSDAY:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 2);
                break;
            case Calendar.FRIDAY:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 3);
                break;
            case Calendar.SATURDAY:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 4);
                break;
            case Calendar.SUNDAY:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 5);
                break;
            case Calendar.MONDAY:
            default:
                days = MovieNetworkDataProcessor.getDateMinusDays(year, month, day, 6);
                break;
        }

        cal.set(days[0], days[1], days[2]);
        return dateFormat.format(cal.getTime());
    }
	
	private void saveUpdateDate() {
		Prefs.saveLastUpdateDate(getDateStamp());
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

    @Override
    public void update(UpdateEvent e, Object... data) {
        switch(e){
            case RT_LISTINGS_LOADED:
                saveUpdateDate();
                ArrayList<RottenTomatoesSummary> summaries = (ArrayList<RottenTomatoesSummary>) data[0];
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putParcelableArrayListExtra("summaries", summaries);
                startActivity(intent);
                finish();
                break;
            case RT_LISTING_LOADED:
                String title = (String) data[0];
                int soFar = (Integer) data[1];
                int total = (Integer) data[2];
                mProgressNumberTextView.setText(soFar + "/" + total);
                mProgressTextView.setText(getString(R.string.loaded, title));
                break;

        }
    }
}
