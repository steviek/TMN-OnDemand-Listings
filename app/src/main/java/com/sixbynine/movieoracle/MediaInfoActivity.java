package com.sixbynine.movieoracle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sixbynine.movieoracle.dataprocessor.DataProcessor;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Rating;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.util.URIHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MediaInfoActivity extends ActionBarActivity{
	private Map<String, TextView> tvs;
	private boolean movie;
	public static final String MOVIE = "Movie";
	private static final String[] fields = {
		Media.TITLE,
		Media.RATINGS, 
		Media.CAST,
		Media.SYNOPSIS,
		Media.YEAR, 
		Media.GENRES,
		Media.DIRECTORS, 
		Media.COUNTRIES,
		Media.LANGUAGES, 
		Media.PARENTAL_ADVISORY, 
		Media.RUNTIME
	};
	private Media m;
	
	
	String imdb_web_page;
	String rt_web_page;
	String tvdb_web_page;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Intent intent = getIntent();
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		if(intent.hasExtra(MOVIE)){
			movie = Boolean.parseBoolean(intent.getStringExtra(MOVIE));
		}else{
			movie = true;
		}
		setTitle("");
		initializeMap();
		loadValues(intent);
		
		if(intent.hasExtra("Media")){
			m = intent.getParcelableExtra("Media");
			//this.setTitle(m.getTitle());
			setContentView(constructInterface(getApplicationContext()));
		}else{
			m = new Movie("null");
			setContentView(R.layout.activity_media_info);
		}
		
		imdb_web_page = getResources().getString(R.string.menu_imdb);
		rt_web_page = getResources().getString(R.string.menu_rotten_tomatoes);
		tvdb_web_page = getResources().getString(R.string.menu_tvdb);
		
		
		
		

	}
	
	private void initializeMap(){
		tvs = new HashMap<String, TextView>();
		tvs.put(Media.TITLE, (TextView) findViewById(R.id.tv_media_info_title));
		tvs.put(Media.YEAR, (TextView) findViewById(R.id.tv_media_info_year));
		tvs.put(Media.GENRES, (TextView) findViewById(R.id.tv_media_info_genres));
		tvs.put(Media.CAST, (TextView) findViewById(R.id.tv_media_info_cast));
		tvs.put(Media.DIRECTORS, (TextView) findViewById(R.id.tv_media_info_directors));
		tvs.put(Media.COUNTRIES, (TextView) findViewById(R.id.tv_media_info_countries));
		tvs.put(Media.LANGUAGES, (TextView) findViewById(R.id.tv_media_info_languages));
		tvs.put(Media.PARENTAL_ADVISORY, (TextView) findViewById(R.id.tv_media_info_parental_advisory));
		tvs.put(Media.SYNOPSIS, (TextView) findViewById(R.id.tv_media_info_synopsis));
		tvs.put(Media.RATINGS, (TextView) findViewById(R.id.tv_media_info_ratings));
		tvs.put(Media.RUNTIME, (TextView) findViewById(R.id.tv_media_info_runtime));
	}
	private void loadValues(Intent intent){
		Iterator<String> iter = tvs.keySet().iterator();
		while(iter.hasNext()){
			String field = iter.next();
			if(intent.hasExtra(field)){
				setValue(field, intent.getStringExtra(field));
			}
		}		
		
	}
	
	private void setValue(String id, String value){
		TextView tv = tvs.get(id);
		if(value == null || value.length() == 0 || value.equals("") || value.equals("0")){
			tv.setVisibility(View.GONE);
			((LinearLayout) tv.getParent()).removeView(tv);
		}else{
			tv.setVisibility(View.VISIBLE);
			if(id.equals(Media.RATINGS)){
				tv.setText("" + id + "\n " + value.replaceAll(",", "\n "));
			}else if (id.equals(Media.TITLE)){
				tv.setText(value);
				this.setTitle(value);
			}else{
				tv.setText("" + id + ": " + value);
			}
		}
	}
	
	private ScrollView constructInterface(Context context){
		ScrollView sv = new ScrollView(context);
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		TextView mytv = new TextView(this);
		mytv.setText(m.getTitle());
		mytv.setTextSize(20);
		mytv.setTypeface(null, Typeface.BOLD);
		ll.addView(mytv, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));		
		float density = this.getResources().getDisplayMetrics().density;
		
		for(int i = 0; i < fields.length; i ++){
			
			String field = fields[i];
			if(m.has(field)){
				TextView tv = new TextView(context);
				if(field.equals(Media.RUNTIME)){
					tv.setText(Html.fromHtml("<b>" + m.getFieldName(field) + "</b>: " + m.getAsString(field) + " minutes"));
				}else if(field.equals(Media.RATINGS)){
					tv.setText(Html.fromHtml("<b>Rotten Tomatoes\u00AE Score" + (m.hasCriticRating() && m.hasAudienceRating()? "s" : "" )+ "<\b>"));
					tv.setTextSize(18);
					tv.setPadding(0, 5, 0, 5);
					tv.setTextColor(Color.BLACK);
					
					LinearLayout scores = new LinearLayout(context);
					scores.setOrientation(LinearLayout.HORIZONTAL);
					
					Display display = getWindowManager().getDefaultDisplay(); 
					int width = display.getWidth();
					int imageDimension = width / 10;
					
					if(m.hasCriticRating()){
						Rating cr = m.getCriticRating();
						ImageView cr_iv = new ImageView(context);
						if(cr.getRating() >= 60){
							cr_iv.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fresh), imageDimension, imageDimension, true));
						}else{
							cr_iv.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rotten), imageDimension, imageDimension, true));
						}
						cr_iv.setOnClickListener(launchRottenTomatoesListener);
						TextView cr_tv = new TextView(context);
						cr_tv.setText((int) cr.getRating() + "%"); 
						cr_tv.setTextColor(Color.BLACK);
						cr_tv.setPadding(4, 0, 4, 0);
						cr_tv.setTextSize(30);
						cr_tv.setOnClickListener(launchRottenTomatoesListener);
						scores.addView(cr_iv);
						scores.addView(cr_tv);
					}
					
					if(m.hasAudienceRating()){
						Rating ar = m.getAudienceRating();
						ImageView ar_iv = new ImageView(context);
						if(ar.getRating() >= 60){
							ar_iv.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.popcorn), imageDimension, imageDimension, true));
						}else{
							ar_iv.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.badpopcorn), imageDimension, imageDimension, true));
						}
						ar_iv.setOnClickListener(launchRottenTomatoesListener);
						TextView ar_tv = new TextView(context);
						ar_tv.setText((int) ar.getRating() + "%"); 
						ar_tv.setTextColor(Color.BLACK);
						ar_tv.setPadding(4, 0, 4, 0);
						ar_tv.setTextSize(30);
						ar_tv.setOnClickListener(launchRottenTomatoesListener);
						scores.addView(ar_iv);
						scores.addView(ar_tv);
					}
					if(!m.hasCriticRating() && !m.hasAudienceRating()){
						continue;
					}
					
					ll.addView(tv);
					scores.setGravity(Gravity.CENTER);
					ll.addView(scores);
					continue;
				}else if(field.equals(Media.TITLE)){
					continue;
				}else{
					tv.setText(Html.fromHtml("<b>" + m.getFieldName(field) + "</b>: " + m.getAsString(field)));
				}
				
				
				tv.setTextSize(18);
				tv.setPadding(0, (int) (5*density), 0, (int) (5*density));
				tv.setTextColor(Color.BLACK);
				ll.addView(tv);
			}
		}		
		
		
		ll.setPadding((int) (15*density), (int) (10*density), (int) (15*density), (int) (10*density));
		sv.addView(ll);
		return sv;
	}
	
	private void setValue(String id, List<?> list){
		setValue(id, getCDL(list));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_media_info, menu);
		
		menu.findItem(R.id.menu_rotten_tomatoes).setVisible(m instanceof Movie);
		menu.findItem(R.id.menu_tvdb).setVisible(m instanceof Series);
		
		
		return true;
	}
	
	private String getCDL(List<?> list){
		String result = "";
		if(list.size() <= 0) return result;
		for(int i = 0; i < list.size(); i ++){
			result += list.get(i).toString() + ", ";
		}
		return result.substring(0, result.length()-1);
	}


	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String selectedValue = parent.getItemAtPosition(position).toString();
		if(m == null) return;
		
		if(selectedValue.equals(this.imdb_web_page)){
			if(!m.getIds().containsKey(DataProcessor.IMDB)) return;
			String imdb_id = m.getId(DataProcessor.IMDB);
			
			String url = "http://www.imdb.com/title/" + imdb_id;
			
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(m == null) return true;
		String url;
		String uriTitle = URIHelper.getInstance(getResources()).getAsURI(m.getTitle());
    	switch(item.getItemId()){
    	case R.id.menu_imdb:
    		
    		if(m.getIds().containsKey(DataProcessor.IMDB)){
    			String imdb_id = m.getId(DataProcessor.IMDB);
    			url = "http://www.imdb.com/title/tt" + imdb_id.replaceAll("t", "");
    			
    		}else{
    			url = "http://www.imdb.com/find?s=all&q=" + uriTitle;
    		}
    		launchURL(url);
    		return true;
    	case R.id.menu_rotten_tomatoes:
    			url = "http://www.rottentomatoes.com/search/?search=" + uriTitle + "&sitesearch=rt";
    			launchURL(url);
    		
    		return true;
    	case R.id.menu_tvdb:
    		if(m.getIds().containsKey(DataProcessor.TVDB)){
    			String tvdb_id = m.getId(DataProcessor.TVDB);
    			url = "http://thetvdb.com/?tab=series&id=" + tvdb_id;
    		}else{
    			url = "http://thetvdb.com/?string=" + uriTitle + "&searchseriesid=&tab=listseries&function=Search";
    		}
    		launchURL(url);
    		return true;
    	case android.R.id.home:
    		//NavUtils.navigateUpFromSameTask(this);
    		finish();
    		 //onBackPressed();
    		return true;
    	default: 
    			return super.onOptionsItemSelected(item);
    	}
		
	}
	
	private void launchURL(String url){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	private View.OnClickListener launchRottenTomatoesListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String uriTitle = URIHelper.getInstance(getResources()).getAsURI(m.getTitle());
			String url = "http://www.rottentomatoes.com/search/?search=" + uriTitle + "&sitesearch=rt";
			launchURL(url);
		}
	};
		
	

}
