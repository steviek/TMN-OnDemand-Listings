package com.sixbynine.movieoracle;

public interface SplashActivityCallback {
	public static final int MOVIE_NETWORK_INTERNET_FINISHED = 0;
	public static final int MOVIE_NETWORK_DATABASE_FINISHED = 1;
	
	public static final int NEW_MOVIE_NETWORK_LISTING_RETRIEVED = 2;
	public static final int NEW_UNMATCHED_MEDIA_RETRIEVED = 3;
	
	public static final int UNMATCHED_MEDIA_FINISHED = 4;
	public static final int ALL_MEDIA_CATALOGUE_DATABASE_FINISHED = 5;
	
	public static final int NEW_ALL_MEDIA_DATABASE_ENTRY_LOADED = 6;
	
	public void notifyChange(int status);
		

}
