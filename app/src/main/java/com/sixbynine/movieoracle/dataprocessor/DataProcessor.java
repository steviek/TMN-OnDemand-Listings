package com.sixbynine.movieoracle.dataprocessor;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Series;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public abstract class DataProcessor {
	public final static int IMDB_DATA_PROCESSOR = 0;
	public final static int ROTTEN_TOMATOES_DATA_PROCESSOR = 1;
	public final static int TVDB_DATA_PROCESSOR = 2;
	
	public static final String ROTTEN_TOMATOES = "Rotten Tomatoes";
	public static final String IMDB = "IMDb";
	public static final String TVDB = "TVDb";
	
	protected boolean initialized;
	protected int loadCount;
	protected int total;
	protected Catalogue mnCatalogue;
	protected Catalogue catalogue;
	private SplashActivityCallback cb;

	
	public void bindCallBack(SplashActivityCallback cb){
		this.cb = cb;
	}
	
	public void notifyCallback(int status){
		cb.notifyChange(status);
	}
	public abstract Media retrieve(Resources res, String title, boolean movie) throws Exception;
	public static DataProcessor createDataProcessor(int type, SplashActivityCallback cb){
		DataProcessor dp = null;
		if(type == IMDB_DATA_PROCESSOR){
			dp =  new IMDbDataProcessor();
		}else if (type == ROTTEN_TOMATOES_DATA_PROCESSOR){
			dp = new RottenTomatoesDataProcessor();
		}else if(type == TVDB_DATA_PROCESSOR){
			dp = new TVDbDataProcessor();
		}
		dp.bindCallBack(cb);
		dp.initialized = false;
		return dp;
		
	}
	
	public Catalogue retrieveCatalogue(){
		if(catalogue == null) return new Catalogue();
		return catalogue;
	}
	
	public boolean isInitialized(){
		return initialized;
	}
	
	public void populate(Resources res, Catalogue catalogue){
		if(catalogue == null || catalogue.isEmpty()) cb.notifyChange(SplashActivityCallback.UNMATCHED_MEDIA_FINISHED);
		this.mnCatalogue = catalogue;
		this.catalogue = new Catalogue();
		loadCount = 0;
		total = catalogue.size();
		initialized = true;
		for(Media m : mnCatalogue){
			String title = m.getTitle();
			new DownloadData(res).execute(title);
		}
		
	}
	
	public boolean isFinishedLoading(){
		return loadCount == total;
	}
	
	public String getStatus(){
		return "Loading new media data:" + loadCount + " of " + total;
	}
	
	public int getProgress(){
		if(total == 0) return 0;
		int progress= (100 * loadCount) / total;
		return  progress;
	}
	
	// Reads an InputStream and converts it to a String.
	private static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
	}

    public static String downloadUrl(String myurl, Map<String, String> params) throws IOException{
        if(params == null || params.size() == 0){
            return downloadUrl(myurl);
        }else{
            StringBuilder query = new StringBuilder(myurl);
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if(!first){
                    query.append("&");
                }else{
                    query.append("?");
                }
                first = false;
                query.append(entry.getKey());
                query.append("=");
                query.append(entry.getValue());
            }
            return downloadUrl( query.toString());
        }
    }
	
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	public static String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(0 /* milliseconds */);
	        conn.setConnectTimeout(0 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d("DEBUG_TAG", "The response is: " + response);
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is, len);
	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	public static String getHtml(String url) throws IOException {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url)
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
    /*HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,3000); // 3s max for connection
		HttpConnectionParams.setSoTimeout(httpParameters, 40000); // 4s max to get data
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpGet httpget = new HttpGet(url); // Set the action you want to do
		HttpResponse response = httpclient.execute(httpget); // Executeit
		HttpEntity entity = response.getEntity(); 
		InputStream is = entity.getContent(); // Create an InputStream with the response
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) // Read line by line
		    sb.append(line + "\n");

		String resString = sb.toString(); // Result is here

		is.close(); // Close the stream
	    return resString;*/
	}
	
	private class DownloadData extends AsyncTask<String, Void, Media>{

        private Resources res;

        public DownloadData(Resources res){
            this.res = res;
        }

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			if(loadCount == total) {
				cb.notifyChange(SplashActivityCallback.UNMATCHED_MEDIA_FINISHED);
			}else{
				cb.notifyChange(SplashActivityCallback.NEW_UNMATCHED_MEDIA_RETRIEVED);
			}
		}


		@Override
		protected void onPostExecute(Media result) {
			super.onPostExecute(result);
			if(result != null){
				Log.i(this.getClass().getName(), "Loaded " + (result instanceof Movie? " movie: " : " series: ") +  "[" + result.getTitle() + "]");
				catalogue.add(result);
			}
			loadCount ++;
		}

		@Override
		protected Media doInBackground(String... title) {
			try {
				return retrieve(res, title[0], !(mnCatalogue.getByTitle(title[0]) instanceof Series));
			} catch (Exception e) {
				Log.e(this.getClass().getName(), e.toString());
				return null;
			}

			
		}
	}
	
}
