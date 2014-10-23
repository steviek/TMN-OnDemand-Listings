package com.sixbynine.movieoracle.sql.ondemandlistings;

import com.sixbynine.movieoracle.sql.allmedia.AllMediaContract.AllMedia;
import com.sixbynine.movieoracle.sql.ondemandlistings.OnDemandListingsContract.OnDemandListings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OnDemandListingsHelper extends SQLiteOpenHelper{
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "MNODL.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	
	private static final String SQL_CREATE_TABLE = 
			"CREATE TABLE " + OnDemandListings.TABLE_NAME + " (" +
			OnDemandListings._ID + INTEGER_TYPE + " PRIMARY KEY," + 
			OnDemandListings.COLUMN_NAME_TITLE + TEXT_TYPE + "," + 
			OnDemandListings.COLUMN_NAME_MOVIE + INTEGER_TYPE + "," + 
			OnDemandListings.COLUMN_NAME_EPISODES + TEXT_TYPE + ")";
	
	private static final String SQL_DELETE_TABLE = 
			"DROP TABLE IF EXISTS " + AllMedia.TABLE_NAME;
	
	public OnDemandListingsHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_TABLE);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
