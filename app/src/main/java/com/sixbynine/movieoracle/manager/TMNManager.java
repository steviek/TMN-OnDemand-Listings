package com.sixbynine.movieoracle.manager;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.tmn.TMNResources;
import com.sixbynine.movieoracle.datamodel.webresources.WebResources;
import com.sixbynine.movieoracle.dataprocessor.DataProcessor;
import com.sixbynine.movieoracle.events.TMNResourcesLoadedEvent;
import com.sixbynine.movieoracle.util.Logger;
import com.sixbynine.movieoracle.util.Prefs;
import com.sixbynine.movieoracle.util.TMNDateUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public final class TMNManager {

    private static TMNResources resources;

    public static TMNResources loadData(WebResources webResources) {
        if (resources == null) {
            new DownloadListings(webResources).execute();
        }
        return resources;
    }
	
	private static final class DownloadListings extends AsyncTask<Void, Void, TMNResources> {

        private final WebResources webResources;

        public DownloadListings(WebResources webResources) {
            this.webResources = webResources;
        }

		@Override
		protected TMNResources doInBackground(Void... voids) {
			try {
                String urlTemplate = webResources.getTmnUrlTemplate();
                String url = urlTemplate.replace("{date}", getClosestDate());

                Set<String> moviesSet = new HashSet<>();
                Set<String> seriesSet = new HashSet<>();

                String pageCode = DataProcessor.getHtml(url);
                Document doc = Jsoup.parse(pageCode);
                Elements shows = doc.select("td");
                for (Element show : shows) {
                    String title = show.text();
                    String seriesName = null;

                    if (title.contains("Making Of") || title.contains("Recap Show")) continue;

                    if (title.indexOf("Ep.") > 0) {
                        seriesName= title.substring(0, title.indexOf("Ep.") - 2);
                    } else {
                        for (String series : webResources.getSeries()) {
                            if (title.startsWith(series)) {
                                seriesName = series;
                            }
                        }
                    }

                    if (seriesName != null) {
                        seriesSet.add(seriesName);
                        Logger.i("Added " + title + " to series");
                    } else {
                        boolean exclude = false;
                        Set<String> ignoreList = Prefs.getIgnoreList();
                        for (String ignore : ignoreList) {
                            if (!"".equals(ignore) && title.startsWith(ignore)) {
                                exclude = true;
                                break;
                            }
                        }

                        if (!exclude) {
                            Logger.i("Added " + title + " to movies");
                            moviesSet.add(title);
                        }
                    }
                }
                Logger.i("Finished loading data.");

                moviesSet.removeAll(seriesSet);

                Set<String> excludeResources = new HashSet<>();
                for (String excludePrefix : webResources.getExcludePrefixes()) {
                    for (String movie : moviesSet) {
                        if (movie.startsWith(excludePrefix)) {
                            excludeResources.add(movie);
                        }
                    }
                    for (String series : seriesSet) {
                        if (series.startsWith(excludePrefix)) {
                            excludeResources.add(series);
                        }
                    }
                }
                moviesSet.removeAll(excludeResources);
                seriesSet.removeAll(excludeResources);

				return new TMNResources(moviesSet, seriesSet);
			} catch(Exception e) {
				Logger.e(e.getMessage());
				return null;
			}	
		}

        @Override
        protected void onPostExecute(TMNResources result) {
            resources = result;
            MyApplication.getInstance().getBus().post(new TMNResourcesLoadedEvent(result));
        }
		
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getClosestDate(){
		return new SimpleDateFormat("yyyy-MM-dd").format(TMNDateUtils.getLastTuesday().getTime());
	}

}
