package com.sixbynine.movieoracle.sql.ondemandlistings;

import android.provider.BaseColumns;

public final class OnDemandListingsContract {

	public OnDemandListingsContract() {}
	
	public static abstract class OnDemandListings implements BaseColumns{
		public static final String TABLE_NAME = "mnodl_on_demand_listings";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_MOVIE = "movie";
		public static final String COLUMN_NAME_EPISODES = "episodes";
		
	}

}
