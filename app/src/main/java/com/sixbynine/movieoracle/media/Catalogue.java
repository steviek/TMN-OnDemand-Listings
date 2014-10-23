package com.sixbynine.movieoracle.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;



public class Catalogue extends ArrayList<Media> implements Parcelable{
	public final static int TITLE = 0;
	public final static int YEAR = 1;
	public final static int RATING = 2;
	public final static int RUNTIME = 3;
	public final static int GENRE = 4;
	public final static int CAST = 5;
	public final static int PARENTAL_ADVISORY = 6;
	public final static int MOVIE = 7;
	public final static int SERIES = 8;
	
	public final static int EQUAL = 0;
	public final static int GTE = 1;
	public final static int LTE = 2;
	public final static int GT = 3;
	public final static int LT = 4;
	
	public final static int G = 0;
	public final static int PG = 1;
	public final static int PG_13 = 2;
	public final static int FOURTEEN_A = 3;
	public final static int NC_17 = 4;
	public final static int EIGHTEEN_A = 5;
	public final static int R = 6;
	public final static int UNRATED = 7;
	
	public final static boolean ASCENDING = true;
	public final static boolean DESCENDING = false;
	
	public static Catalogue getCatalogue(Serializable s){
		if(s instanceof Catalogue) return (Catalogue) s;
		if(!(s instanceof ArrayList)) return new Catalogue();
		ArrayList al = (ArrayList) s;
		Catalogue result = new Catalogue();
		for(Object o  : al){
			result.add((Media) o);
		}
		return result;
	}
	
	public Catalogue(Media[] array){
		super(array.length);
		for(int i = 0; i < array.length; i ++){
			add(array[i]);
		}	
	}
	
	public Catalogue(ArrayList<Media> list){
		super(list);
	}
	
	public Catalogue(){
		super();
	}
	
	
	
	/**
	 * 
	 * @param c1 The full list that you want to retrieve things for
	 * @param c2 The data from the database
	 * @return an pair of catalogues.  The first is the matched media, the second is the unmatched
	 */
	public static Catalogue[] merge(Catalogue c1, Catalogue c2){
		Catalogue matchedResults = new Catalogue();
		Catalogue unmatchedResults = new Catalogue();
		
		for(Media m1 : c1){
			Media m2 = c2.getByTitle(m1.getTitle());
			if(m2 == null){
				unmatchedResults.add(m1);
			}else{
				matchedResults.add(Media.merge(m1, m2));
			}
			
		}
		
		return new Catalogue[] {matchedResults, unmatchedResults};
	}
	
	public void getDataFrom(Catalogue another){
		Catalogue[] results = Catalogue.merge(this, another);
		this.removeAll(this);
		this.addAll(results[0]);
		this.addAll(results[1]);
	}
	
	public Catalogue sortBy(int type, boolean ascending){
		Catalogue result = this;
		
		if(ascending){
			if(type == TITLE){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						return m1.getTitle().compareTo(m2.getTitle());
					}
				});
			}else if(type == YEAR){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						return Integer.valueOf(m1.getYear()).compareTo(Integer.valueOf(m2.getYear()));
					}
				});
			}else if (type == RATING){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						Double d1 = Double.valueOf(m1.getBestRating());
						Double d2 = Double.valueOf(m2.getBestRating());
						return d1.compareTo(d2);
					}
				});
			}else if(type == RUNTIME){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m1, Media m2){
						Integer i1 = Integer.valueOf(m1.getRuntime());
						Integer i2 = Integer.valueOf(m2.getRuntime());
						return i1.compareTo(i2);
					}
				});
			}
		}else{
			if(type == TITLE){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m2, Media m1){
						return m1.getTitle().compareTo(m2.getTitle());
					}
				});
			}else if(type == YEAR){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m2, Media m1){
						return Integer.valueOf(m1.getYear()).compareTo(Integer.valueOf(m2.getYear()));
					}
				});
			}else if (type == RATING){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m2, Media m1){
						Double d1 = Double.valueOf(m1.getBestRating());
						Double d2 = Double.valueOf(m2.getBestRating());
						return d1.compareTo(d2);
					}
				});
			}else if(type == RUNTIME){
				Collections.sort(result, new Comparator<Media>(){
					@Override
					public int compare(Media m2, Media m1){
						Integer i1 = Integer.valueOf(m1.getRuntime());
						Integer i2 = Integer.valueOf(m2.getRuntime());
						return i1.compareTo(i2);
					}
				});
			}
		}
		return result;
		
	}

	public Catalogue filterBy(int type, String value){
		Catalogue result = new Catalogue();
		if(type == TITLE){
			for(Media m : this){
				if(m.getTitle().toLowerCase().contains(value.toLowerCase())) result.add(m);
			}
		}else if(type == GENRE){
			for(Media m : this){
				for(String genre : m.getGenres()){
					if(genre.toLowerCase().contains(value.toLowerCase())) {
						result.add(m);
						break;
					}
				}
			}
		}else if(type == CAST){
			for(Media m : this){
				for(String cast : m.getCast()){
					if(cast.toLowerCase().contains(value.toLowerCase())){
						result.add(m);
						break;
					}
				}
			}
		}else if(type == MOVIE){
			for(Media m : this){
				if(m instanceof Movie) result.add(m);
			}
		}else if(type == SERIES){
			for(Media m : this){
				if(m instanceof Series) result.add(m);
			}
		}
		return result;
	}
	
	public Catalogue filterBy(int type, String value, int comparison){
		Catalogue result = new Catalogue();
		if(type == YEAR){
			try{
				int val = Integer.parseInt(value);
				result = filterBy(type, val, comparison);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(type == PARENTAL_ADVISORY){
			result = filterBy(type, getRating(value), comparison);
		}else if(type == RATING){
			try{
				int val = Integer.parseInt(value);
				result = filterBy(type, val, comparison);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(type == RUNTIME){
			try{
				int val = Integer.parseInt(value);
				result = filterBy(type, val, comparison);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public Catalogue getTop(int n){
		Catalogue result = new Catalogue();
		
		int limit = Math.min(n, this.size());
		for(int i = 0; i < limit; i ++){
			Media m = this.get(i);
			result.add(m);
		}
		
		return result;
			
		
	}
	
	public Catalogue filterBy(int type, int value, int comparison){
		Catalogue result = new Catalogue();
		
		if(type == YEAR){
			for(Media m : this){
				try{
					if(m.getYear() == null || m.getYear().equals("") || m.getYear().equals("0")) continue;
					int year = Integer.parseInt(m.getYear());
					if(compare(value, year, comparison)) result.add(m);
				}catch(Exception e){
					
				}
			}
		}else if(type == RATING){
			for(Media m : this){
				int rating = (int) m.getBestRating();
				if(compare(value, rating, comparison)) result.add(m);
			}
		}else if(type == PARENTAL_ADVISORY){
			for(Media m : this){
				if(m.getParentalAdvisory() == null || m.getParentalAdvisory().equals("") || m.getParentalAdvisory().equals("0")) continue;
				int advisory = getRating(m.getParentalAdvisory());
				if(compare(value, advisory, comparison)) result.add(m);
			}
		}else if(type == RUNTIME){
			for(Media m : this){
				if(m.getRuntime() == null || m.getRuntime().equals("") || m.getRuntime().equals("0")) continue;
				int runtime = Integer.parseInt(m.getRuntime());
				if(compare(value, runtime, comparison)) result.add(m);
			}
		}
		
		return result;
	}

	private int getRating(String ratingNC){
		String ratingLC = ratingNC.toLowerCase();
		if(ratingLC.contains("pg") ){
			if(ratingLC.contains("13")) return PG_13;
			else return PG;
		}else if(ratingLC.contains("14a") ){
			return FOURTEEN_A;
		}else if(ratingLC.contains("18a") ){
			return EIGHTEEN_A;
		}else if(ratingLC.contains("unrated") ){
			return UNRATED;
		}else if(ratingLC.contains("nc-17") ){
			return NC_17;
		}else if(ratingLC.contains("g") ){
			return G;
		}else if(ratingLC.contains("r") ){
			return R;
		}else{
			return UNRATED;
		}
	}
	
	/**
	 * returns the evaluation of o2 [op] o1
	 * e.g. if op were '<=', o1 = 80, o2 = 75, this returns 75 <= 80 == true
	 * @param o1 
	 * @param o2
	 * @param op
	 * @return
	 */
	private boolean compare(int o1, int o2, int op){
		if(op == GTE){
			return o2 >= o1;
		}else if(op == LTE){
			return o2 <= o1;
		}else if(op == GT){
			return o2 > o1;
		}else if(op == LT){
			return o2 < o1;
		}else{
			return o2 == o1;
		}
	}
	
	public static int getOperator(String op){
		if(op.contains("=")){
			if(op.contains("<")) return LTE;
			else if(op.contains(">")) return GTE;
			else return EQUAL;
		}else{
			if(op.contains("<")) return LT;
			else return GT;
		}
	}
	
	public boolean containsTitle(String title){
		for(Media m : this){
			if(m.getTitle().equalsIgnoreCase(title)) return true;
		}
		
		return false;
	}
	
	public List<String> getGenreListing(){
		List<String> result = new ArrayList<String>();
		for(Media m : this){
			for(String genre : m.getGenres()){
				if(!result.contains(genre) && !genre.equals("")){
					result.add(genre);
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	/**
	 * 
	 * @param title the title of the media
	 * @return the corresponding object, null if it's not found
	 */
	public Media getByTitle(String title){
		for(Media m : this){
			if(m.getTitle().equalsIgnoreCase(title)) return m;
		}
		return null;
	}
	
	public Catalogue getMovies(){
		return this.filterBy(Catalogue.MOVIE, null);
	}
	
	public Catalogue getSeries(){
		return this.filterBy(Catalogue.SERIES, null);
	}
	
	
	public Catalogue trimDuplicates(){
		Catalogue result = this.sortBy(TITLE, true);
		for(int i = 0; i < result.size()-1; i ++){
			if(result.get(i).getTitle().replaceAll(",","").trim().equals(result.get(i+1).getTitle().replaceAll(",","").trim())){
				if(result.get(i).hasSuperiorDataTo(result.get(i+1))){
					result.remove(i+1);
				}else{
					result.remove(i);
				}
				i--;
				
			}
		}
		return result;
		
	}
	
	public String[] getTitleArray(){
		String[] result = new String[this.size()];
		for(int i = 0; i < this.size(); i ++){
			Media m = this.get(i);
			String title = m.getTitle();
			result[i] = title;
		}
		return result;
		
	}
	
	public void addAll(List<String> list){
		if(list == null) return;
		for(String item : list){
			add(new Movie(item));
		}
	}

	protected Catalogue(Parcel in){
		int size = in.readInt();
		for(int i = 0; i < size; i ++){
			String type = in.readString();
			Media m;
			if(type != null && type.equals("Series")){
				m = in.readParcelable(Series.class.getClassLoader());
			}else{
				m = in.readParcelable(Movie.class.getClassLoader());
			}
			this.add(m);
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		int size = this.size();
		dest.writeInt(size);
		for(int i = 0; i < size; i ++){
			Media m = get(i);
			if(m instanceof Series){
				dest.writeString("Series");
			}else{
				dest.writeString("Movie");
			}
			dest.writeParcelable(m, flags);
		}
	}
	
	public static final Parcelable.Creator<Catalogue> CREATOR = new Parcelable.Creator<Catalogue>() {

		@Override
		public Catalogue createFromParcel(Parcel source) {
			return new Catalogue(source);
		}

		@Override
		public Catalogue[] newArray(int size) {
			return new Catalogue[size];
		}
	};
	
}
