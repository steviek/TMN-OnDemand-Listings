package com.sixbynine.movieoracle.sql.allmedia;

import android.provider.BaseColumns;

public final class AllMediaContract {

	public AllMediaContract() {
	}
	
	public static abstract class AllMedia implements BaseColumns{
		public static final String TABLE_NAME = "mnodl_movies";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_YEAR = "year";
		public static final String COLUMN_NAME_GENRES = "genres";
		public static final String COLUMN_NAME_CAST = "cast";
		public static final String COLUMN_NAME_DIRECTORS = "directors";
		public static final String COLUMN_NAME_LANGUAGES = "languages";
		public static final String COLUMN_NAME_SYNOPSIS = "synopsis";
		public static final String COLUMN_NAME_PARENTAL_ADVISORY = "parental_advisory";
		public static final String COLUMN_NAME_RUNTIME = "runtime";
		public static final String COLUMN_NAME_RATINGS = "ratings";
		public static final String COLUMN_NAME_IDS = "ids";
		public static final String COLUMN_NAME_SERIES = "series";
	}

}
