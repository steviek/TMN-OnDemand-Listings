package com.sixbynine.movieoracle.rt;

import android.os.AsyncTask;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.object.RottenTomatoesSummaryContainer;
import com.sixbynine.movieoracle.util.Keys;
import com.sixbynine.movieoracle.util.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by steviekideckel on 10/30/14.
 */
public class RottenTomatoesRestClient {

    public static void getMovies(Catalogue titles){
        new AsyncTask<Catalogue, Void, Void>(){
            @Override
            protected Void doInBackground(Catalogue... args) {
                Catalogue titles = args[0];
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("http://api.rottentomatoes.com/api/public/v1.0")
                        .build();
                RottenTomatoesService service = restAdapter.create(RottenTomatoesService.class);
                for(Media title : titles){
                    try{
                        Map<String, String> params= new HashMap<String, String>();
                        params.put("apikey", Keys.RT_API_KEY);
                        params.put("q", URLEncoder.encode(title.getTitle(), "UTF-8"));
                        params.put("page_limit", "3");
                        RottenTomatoesSummaryContainer summaries = service.listMovies(params);
                        Logger.d(((Object) summaries).toString());
                    }catch(UnsupportedEncodingException e){
                        Logger.e("e");
                    }

                }
                return null;
            }
        }.execute(titles);

    }
}
