package com.sixbynine.movieoracle.media;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;



public abstract class Media implements Parcelable{
	protected String title;
	protected String year;
	protected ArrayList<String> genres;
	protected ArrayList<String> cast;
	protected ArrayList<String> directors;
	protected ArrayList<String> countries;
	protected ArrayList<String> languages;
	protected String synopsis;
	protected String parentalAdvisory;
	protected String runtime;
	protected ArrayList<Rating> ratings;
	
	public static final String TITLE = "Title";
	public static final String YEAR = "Year";
	public static final String GENRES = "Genres";
	public static final String CAST = "Cast";
	public static final String DIRECTORS = "Directors";
	public static final String COUNTRIES = "Countries";
	public static final String LANGUAGES = "Languages";
	public static final String SYNOPSIS = "Synopsis";
	public static final String PARENTAL_ADVISORY = "Parental Advisory";
	public static final String RUNTIME = "Runtime";
	public static final String RATINGS = "Ratings";
	public static final String IDS = "Ids";
	
	
	protected Map<String, String> ids;

	public Media(){
		title = "";
		initFields();
	}
	public Media(String title) {
		this.title = title;
		initFields();
	}
	
	@SuppressWarnings("unchecked")
	protected Media(Parcel in){
		title = in.readString();
		year = in.readString();
		genres = in.createStringArrayList();
		cast = in.createStringArrayList();
		directors = in.createStringArrayList();
		countries = in.createStringArrayList();
		languages = in.createStringArrayList();
		synopsis = in.readString();
		parentalAdvisory = in.readString();
		runtime = in.readString();
		ratings = in.readArrayList(Rating.class.getClassLoader());
		Bundle idBundle = in.readBundle();
		List<String> keys = idBundle.getStringArrayList("keys");
		List<String> values = idBundle.getStringArrayList("values");
		int len = keys.size();
		ids = new HashMap<String, String>();
		for(int i = 0; i < len; i ++){
			ids.put(keys.get(i), values.get(i));
		}
	}

	protected void initFields(){
		year = "";
		genres = new ArrayList<String>();
		cast = new ArrayList<String>();
		countries = new ArrayList<String>();
		languages = new ArrayList<String>();
		directors = new ArrayList<String>();
		synopsis = "";
		parentalAdvisory = "";
		runtime = "";
		ratings = new ArrayList<Rating>();
		ids = new HashMap<String, String>();
	}
	
	/**
	 * Attempts to merge data from two media objects.
	 * Constraints: the objects must be the same type, and have the same title
	 * @param m1 
	 * @param m2
	 * @return A new media object of the same type as the objects. If this is an invalid merge, a copy of m1 will be returned
	 */
	public static Media merge(Media m1, Media m2){
		Media result;
		if((m1 instanceof Movie && !(m2 instanceof Movie)) || (m1 instanceof Series && !(m2 instanceof Series)) 
				|| !m1.getTitle().equalsIgnoreCase(m2.getTitle())) return m1;
		
		boolean series = m1 instanceof Series;
		result = series? new Series(m1.getTitle()) : new Movie(m1.getTitle());
		
		result.setYear(longestString(m1.getYear(), m2.getYear()));
		result.setGenres(longestList(m1.getGenres(), m2.getGenres()));
		result.setDirectors(longestList(m1.getDirectors(), m2.getDirectors()));
		result.setLanguages(longestList(m1.getLanguages(), m2.getLanguages()));
		result.setParentalAdvisory(longestString(m1.getParentalAdvisory(), m2.getParentalAdvisory()));
		result.setRuntime(longestString(m1.getRuntime(), m2.getRuntime()));
		result.setSynopsis(longestString(m1.getSynopsis(), m2.getSynopsis()));
		result.setRatings(longestList(m1.getRatings(), m2.getRatings()));
		result.setCast(longestList(m1.getCast(), m2.getCast()));
		result.setIds(longestMap(m1.getIds(), m2.getIds()));
		
		if(series){
			((Series) result).setSeasons(longestList(((Series) m1).getSeasons(), ((Series) m2).getSeasons()));
		}
		
		return result;
	}
	
	private static String longestString(String s1, String s2){
		//SANITY CHECKS
		if(s1 == null && s2 == null) return "";
		if(s1 == null) return s2;
		if(s2 == null) return s1;
		
		if(s1.length() < s2.length()) return s2;
		else return s1;
	}
	
	private static <T> ArrayList<T> longestList(ArrayList<T> l1, ArrayList<T> l2){
		if(l1 == null && l2 == null) return new ArrayList<T>();
		if(l1 == null) return l2;
		if(l2 == null) return l1;
		
		if(l1.size() < l2.size()) return l2;
		else return l1;
	}
	
	private static <K,V> Map<K,V> longestMap(Map<K,V> m1, Map<K,V> m2){
		if(m1 == null && m2 == null) return new HashMap<K,V>();
		if(m1 == null) return m2;
		if(m2 == null) return m1;
		
		if(m1.size() < m2.size()) return m2;
		else return m1;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}
	
	public void addGenre(String genre){
		this.genres.add(genre);
	}


	public ArrayList<String> getDirectors() {
		return directors;
	}
	public void setDirectors(ArrayList<String> directors) {
		this.directors = directors;
	}
	
	public void addDirector(String director){
		this.directors.add(director);
	}
	

	public ArrayList<String> getCountries() {
		return countries;
	}

	public void setCountry(ArrayList<String> country) {
		this.countries = country;
	}
	
	public void addCountry(String country){
		this.countries.add(country);
	}

	public ArrayList<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}
	
	public void addLanguage(String language){
		this.languages.add(language);
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public String getParentalAdvisory() {
		return parentalAdvisory;
	}

	public void setParentalAdvisory(String parentalAdvisory) {
		this.parentalAdvisory = parentalAdvisory;
	}

	
	public String getRuntime(){
		return runtime;
	}
	
	public void setRuntime(String runtime){
		this.runtime = runtime;
	}
	
	public void addCastMember(String name){
		cast.add(name);
	}
	/**
	 * 
	 * @param n the number of cast members desired
	 * @return an array list containing the first n cast members
	 */
	public ArrayList<String> getCast(int n){
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < n; i ++){
			result.add(cast.get(i));
		}
		return result;
	}
	/**
	 * 
	 * @return a copy of the cast list
	 */
	public ArrayList<String> getCast(){
		return cast;
	}
	
	public void setCast(ArrayList<String> cast){
		this.cast = cast;
	}
	
	public void addRating(String label, double rating, String url){
		addRating(new Rating(label, rating, url));
	}
	
	public void addRating(Rating r){
		ratings.add(r);
	}
	
	public boolean hasCriticRating(){
		for(Rating r : ratings){
			if(r.isCriticRating()){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasAudienceRating(){
		for(Rating r : ratings){
			if(r.isAudienceRating()){
				return true;
			}
		}
		return false;
	}
	
	public Rating getCriticRating(){
		for(Rating r : ratings){
			if(r.isCriticRating()){
				return r;
			}
		}
		return null;
	}
	
	public Rating getAudienceRating(){
		for(Rating r : ratings){
			if(r.isAudienceRating()){
				return r;
			}
		}
		return null;
	}
	public ArrayList<Rating> getRatings(){
		return ratings;
	}
	
	public void setRatings(ArrayList<Rating> ratings){
		this.ratings = ratings;
	}
	
	
	@Override
	public String toString(){
		return "Title: " + title
				+ toStringHelper(year, "Year")
				+ toStringHelper(genres, "Genre", "Genres")
				+ toStringHelper(cast, "Cast", "Cast")
				+ toStringHelper(directors, "Director", "Directors")
				+ toStringHelper(countries, "Country", "Countries")
				+ toStringHelper(languages, "Language", "Languages")
				+ toStringHelper(synopsis, "Synopsis")
				+ toStringHelper(parentalAdvisory, "Parental Advisory")
				+ toStringHelper(runtime, "Runtime") 
				+ toStringHelper(ratings, "Rating", "Ratings");
	}
	
	private String toStringHelper(String value, String front){
		if(value == null || value.equals("") || value.equals("0") || value.equals("0.0"))
			return "";
		else
			return "; " + front + ": " + value;
		
	}
	
	private String toStringHelper(List<?> value, String singular, String plural){
		if(value == null || value.size() == 0) return "";
		int i = value.size();
		Iterator<?> iter = value.iterator();
		String val = "; " + (value.size() == 1? singular : plural) + ": ";
		
		while(iter.hasNext()){
			i --;
			Object obj = iter.next();
			val += obj.toString() + (i == 0? "" : ", ");
			
		}
		return val;
	}
	
	


	private String toStringHelper(int value, String front){
		return (value > 0? "; " + front + ": " + value : "");
	}
	
	public String getSaveInfo(){
		return 	"Item::" + (this instanceof Series? "Series" : "Movie")
				+ "\nTitle::" + title
				+ "\nYear::" + year
				+ "\nGenres::" + getCDL(genres)
				+ "\nCast::" + getCDL(cast)
				+ "\nDirectors::" + getCDL(directors)
				+ "\nCountries::" + getCDL(countries)
				+ "\nDescription::" + synopsis
				+ "\nParental Advisory::" + parentalAdvisory
				+ "\nRuntime::" + runtime
				+ "\nRatings::" + getCDL(ratings)
				+ "\nIDs::" + getCDL(ids);
		
	}
	
	public void addId(String site, String id){
		ids.put(site, id);
	}
	
	public String getId(String site){
		return ids.get(site);
	}
	
	public Map<String, String> getIds(){
		return ids;
	}
	
	public void setIds(Map<String, String> ids){
		this.ids = ids;
	}
	
	public static ArrayList<Media> parseFile(BufferedReader r) throws IOException{
		ArrayList<Media> result = new ArrayList<Media>();
		 // reads each line
	     String l;
	     while((l = r.readLine()) != null) {
	    	 String val[] = l.split("::");
	    	 String key = val[0];
	    	 String values = val[1];
	    	if(l.indexOf("Item:") == 0){
	    		
	    	}
	     } 
	     return result;
	}
	
	public String getQuery(String tableName){
		String NULL = "NULL";
		return "insert into " + tableName
				+ " values("
				+ "'" + title.replaceAll("'", "''") + "',"
				+ (year.equals("")? NULL :  year) + ","
				+ "'" + getCDL(genres).replaceAll("'", "''") + "',"
				+ "'" + getCDL(cast).replaceAll("'", "''") + "',"
				+ "'" + getCDL(directors).replaceAll("'", "''") + "',"
				+ "'" + getCDL(languages).replaceAll("'", "''") + "',"
				+ "'" + synopsis.replaceAll("'", "''") + "',"
				+ "'" + parentalAdvisory + "',"
				+ (runtime.equals("")? NULL :  runtime) + ","
				+ "'" + getCDL(ratings).replaceAll("'", "''") + "',"
				+ "'" + getCDL(ids) + "',"
				+ (this instanceof Series? 1 : 0)
				+ ");";
				
				
	}
	
	private String getStars(int n){
		if(n <= 0){
			return "";
		}else{
			return "*" + getStars(n-1);
		}
	}
	
	public String getFieldName(String field){
		Object o = get(field);
		if(o instanceof Map){
			Map m = (Map) o;
			if(m.size() > 1){
				return getPlural(field);
			}else{
				return getSingular(field);
			}
		}else if(o instanceof ArrayList){
			ArrayList al = (ArrayList) o;
			if(al.size() > 1){
				return getPlural(field);
			}else{
				return getSingular(field);
			}
		}else{
			return field;
		}
	}
	
	private String getPlural(String s){
		if(s.equals(CAST)){
			return s;
		}
		
		if(s.endsWith("s")){
			return s;
		}else if(s.endsWith("y")){
			return s.substring(0, s.length()-1) + "ies";
		}else{
			return s + "s";
		}
	}
	
	private String getSingular(String s){
		if(s.endsWith("ies")){
			return s.substring(0, s.length()-3) + "y";
		}else if(s.endsWith("s")){
			return s.substring(0, s.length()-1);
		}else{
			return s;
		}
	}
	
	public String getAsString(String field){
		if(field.equals(RATINGS)){
			String val = "";
			for(Rating r : this.getRatings()){
				val += "\n" + r.getLabel() + ": " + ((int) r.getRating()) + " %";
			}
			return val;
		}
		Object o = get(field);
		if(o instanceof String){
			return (String) o;
		}else if(o instanceof Map){
			return getCDL((Map) o ).replaceAll(",", ", ");
		}else if(o instanceof ArrayList){
			return getCDL((ArrayList) o).replaceAll(",", ", ");
		}
		
		return "";
	}
	
	
	public Object get(String field){
		
		if(field.equals(Media.TITLE)){
			return this.getTitle();
		}
		else if(field.equals(Media.YEAR)){
			return this.getYear();
		}
		else if(field.equals(Media.GENRES)){
			return this.getGenres();
		}
		else if(field.equals(Media.CAST)){
			return this.getCast();
		}
		else if(field.equals(Media.DIRECTORS)){
			return this.getDirectors();
		}
		else if(field.equals(Media.COUNTRIES)){ 
			return this.getCountries();
		} 
		else if(field.equals(Media.LANGUAGES)){ 
			return this.getLanguages();
		} 
		else if(field.equals(Media.SYNOPSIS)){ 
			return this.getSynopsis();
		} 
		else if(field.equals(Media.PARENTAL_ADVISORY)){ 
			return this.getParentalAdvisory();
		} 
		else if(field.equals(Media.RUNTIME)){ 
			return this.getRuntime();
		} 
		else if(field.equals(Media.RATINGS)){ 
			return this.getRatings();
		} 
		else if(field.equals(Media.IDS)){
			return this.getIds();
		}else{
			return null;
		}
	}
	
	public boolean has(String field){
		Object o = this.get(field);
		if(o == null) return false;
		if(o instanceof String){
			return (!(o.equals("") || o.equals("null") || o.equals("0") || o.equals("0.0") || o.equals(" ")));
		}else if(o instanceof Map){
			return !((Map) o).isEmpty();
		}else if(o instanceof ArrayList){
			return !(((ArrayList) o).isEmpty() || ((ArrayList) o).get(0).equals(""));
		}
		return true;
	}
	
	public double getBestRating(){
		List<Rating> r = getRatings();
		if (r.size() == 0) return 0;
		
		Collections.sort(r);
		return r.get(r.size() - 1).getRating();
	}
	
	public double getRatingAverage(){
		double sum = 0;
		List<Rating> r = getRatings();
		if(r.size() == 0) return 0;
		for(int i = r.size()-1; i >= 0; i --){
			sum += r.get(i).getRating();
		}
		return sum / r.size();
	}
	
	private String getCDL(List<?> list){
		String result = "";
		if(list.size() <= 0) return result;
		for(int i = 0; i < list.size(); i ++){
			result += list.get(i).toString() + ",";
		}
		return result.substring(0, result.length()-1);
	}
	
	private String getCDL(Map<String, String> map){
		String result = "";
		if(map.size() <= 0) return result;
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			result += entry.getKey() + ";" + entry.getValue() + ",";
		}
		return result.substring(0, result.length()-1);
	}
	
	public boolean hasSuperiorDataTo(Media other){
		return this.toString().length() > other.toString().length();
	}
	public String getCastString() {
		return getCDL(cast);
	}
	
	public String getDirectorsString(){
		return getCDL(directors);
	}
	
	public String getGenresString(){
		return getCDL(genres);
	}
	
	public String getLanguagesString(){
		return getCDL(languages);
	}
	
	public String getIDString(){
		return getCDL(ids);
	}
	
	public String getRatingsString(){
		return getCDL(ratings);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(year);
		dest.writeStringList(genres);
		dest.writeStringList(cast);
		dest.writeStringList(directors);
		dest.writeStringList(countries);
		dest.writeStringList(languages);
		dest.writeString(synopsis);
		dest.writeString(parentalAdvisory);
		dest.writeString(runtime);
		dest.writeList(ratings);
		Bundle idBundle = new Bundle();
		idBundle.putStringArrayList("keys", new ArrayList<String>(ids.keySet()));
		idBundle.putStringArrayList("values", new ArrayList<String>(ids.values()));
		dest.writeBundle(idBundle);
		
	}
	
}

	

