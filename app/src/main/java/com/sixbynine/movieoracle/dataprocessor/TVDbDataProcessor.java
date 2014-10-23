package com.sixbynine.movieoracle.dataprocessor;

import android.content.res.Resources;

import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.util.LanguageHelper;
import com.sixbynine.movieoracle.util.URIHelper;

public class TVDbDataProcessor extends DataProcessor{
	private final static String API_KEY = "186839CC81160B26";

	@Override
	public Media retrieve(Resources res, String title, boolean movie) throws Exception {
		 Media media = movie? new Movie(title) : new Series(title);
		 URIHelper uriHelper = URIHelper.getInstance(res);
		 
		 String linkToGetMirror = "http://thetvdb.com/api/GetSeries.php?seriesname=" + uriHelper.getAsURI(title);
		 URL url = new URL(linkToGetMirror);
		 Scanner s = new Scanner(url.openStream());
		 
		 String xml = "";
		 while(s.hasNext()){
			xml += s.nextLine() + "\n";
		 }

		 Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
		 
		 try{
			 Elements allSeries = doc.select("Series");
			 Element series = allSeries.first();
			 String seriesName = series.select("SeriesName").first().text();
			 //media.setTitle(seriesName);
			 			 
			 if(series.select("seriesid").size() > 0){
				 String seriesID = series.select("seriesid").first().text();
				 media.addId(DataProcessor.TVDB, seriesID);
			 }
			 
			 if(series.select("IMDB_ID").size() > 0){
				 String imdbID = series.select("IMDB_ID").first().text();
				 media.addId(DataProcessor.IMDB, imdbID);
			 }
			 
			 if(series.select("language").size() > 0){
				 LanguageHelper languageHelper = LanguageHelper.getInstance();
				 String languageCode = series.select("language").first().text();
				 media.addLanguage(languageHelper.getLanguageFromCode(languageCode));
			 }
			 
			 if(series.select("Overview").size() > 0){
				 String overview = series.select("Overview").first().text();
				 media.setSynopsis(overview);
			 }
			
		 }catch(Exception e){
			 System.out.println("Unable to retrieve data for " + title + ".");
		 }
		 
		return media;
	}
	
	
}
