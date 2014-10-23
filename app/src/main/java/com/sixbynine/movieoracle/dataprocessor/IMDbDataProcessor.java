package com.sixbynine.movieoracle.dataprocessor;

import android.content.res.Resources;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Series;
import com.sixbynine.movieoracle.util.URIHelper;

public class IMDbDataProcessor extends DataProcessor{

	
	 @Override
	public Media retrieve(Resources res, String title, boolean movie) throws IOException{
		 Media media = movie? new Movie(title) : new Series(title);
		 URIHelper uriHelper = URIHelper.getInstance(res);
		 String url = "http://www.imdb.com/find?q=" + uriHelper.getAsURI(title) + "&s=tt&exact=true&ref_=fn_tt_ex";
		 
		 Document doc = Jsoup.connect(url).timeout(0).get();
		 Elements results = doc.select("a[href~=/title.*fn_tt_tt_1$]");
		 if(results.size() < 2){
			 return media;
		 }
		 Element link = results.get(1);
		 String href = link.attr("abs:href");
		 
		 Document imdbPage = Jsoup.connect(href).get();
		 
		 
		 //Get Year
		 Element yearLink = imdbPage.select("a[href~=^/year.*]").first();
		 if(yearLink != null){
			 String year = yearLink.text();
			 //System.out.println(title + ": " + year);
			 media.setYear(Integer.parseInt(year) + "");
		 }
		 
		 //Get Description
		 Element description = imdbPage.select("p[itemprop=description]").first();
		 if(description != null)
			 media.setSynopsis(description.text());
		 
		 //Get Genre
		 Elements genres = imdbPage.select("span[itemprop=genre]");
		 for(Element genre : genres){
			 if(genre.text() != null && genre.text().equals("") == false)
				 media.addGenre(genre.text());
		 }
		 
		 //Get Stars
		 Elements stars = imdbPage.select("div[itemprop=actors]");
		 Elements starNames = stars.select("span[itemprop=name]");
		 for(Element starName : starNames){
			 if(starName.text() != null && starName.text().equals("") == false)
				 media.addCastMember(starName.text());
		 }
		 
		//Get Director
			 Elements directorFields = imdbPage.select("div[itemprop=director]");
			 Elements directors = directorFields.select("span[itemprop=name]");
			 for(Element director : directors){
				 if(director.text() != null && director.text().equals("") == false)
					 media.addDirector(director.text());
			 }
		 
		 //Inline details?
		 Elements divBlocks = imdbPage.select("div[class=txt-block]");
		 for(Element divBlock : divBlocks){
			 
			 Elements inlines = divBlock.select("h4[class=inline]");
			 
			 for(Element det : inlines){
				 if(det.text().contains("Country")){
					 Elements countries = divBlock.select("a[itemprop=url]");
					 for(Element country : countries){
						 //System.out.println(country.text());
						 if(country.text() != null && country.text().equals("") == false){
							 media.addCountry(country.text());
						 }
					 }
				 }else if(det.text().contains("Language")){
					 Elements languages = divBlock.select("a[itemprop=url]");
					 for(Element language : languages){
						 if(language.text() != null && language.text().equals("") == false){
							 media.addLanguage(language.text());
						 }
					 }
				 }else if(det.text().contains("Runtime")){
					 Element runtime = divBlock.select("time[itemprop=duration]").first();
					 String time = runtime.text();
					 if(time.contains("min")) time = time.substring(0, time.indexOf("min")-1);
					 try{
						 if(runtime != null)
							 media.setRuntime(time);
					 }catch(Exception e){
					 }
					 //Get Parental Advisory
					 Element parentalAdvisory = imdbPage.select("span[itemprop=contentRating]").first();
					 if(parentalAdvisory != null){
						 String[] parentalAdvisoryParts = parentalAdvisory.attr("abs:content").split("/");
						 String advisory = parentalAdvisoryParts[parentalAdvisoryParts.length-1];
						 //System.out.println("Rating for " + title + ": " + rating);
						 media.setParentalAdvisory(advisory);
					 }
					 
					 
				 }
			 }
		 }
		 
		//Get Rating Value
		 Element ratingValue = imdbPage.select("span[itemprop=ratingValue]").first();
		 if(ratingValue != null){
			 try{
			 media.addRating("IMDb", Double.parseDouble(ratingValue.text()), url);
			 }catch(Exception e){
			 }
		 }
		
		 return media;
	 }
}
