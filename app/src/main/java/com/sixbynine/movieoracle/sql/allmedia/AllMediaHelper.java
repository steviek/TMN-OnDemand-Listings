package com.sixbynine.movieoracle.sql.allmedia;

import com.sixbynine.movieoracle.sql.allmedia.AllMediaContract.AllMedia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AllMediaHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "MNODL_Movies.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	
	private static final String SQL_CREATE_TABLE = 
			"CREATE TABLE " + AllMedia.TABLE_NAME + " (" + 
			AllMedia._ID + INTEGER_TYPE + " PRIMARY KEY," + 
			AllMedia.COLUMN_NAME_TITLE + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_YEAR + INTEGER_TYPE + "," + 
			AllMedia.COLUMN_NAME_GENRES + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_CAST + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_DIRECTORS + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_LANGUAGES + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_SYNOPSIS + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_PARENTAL_ADVISORY + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_RUNTIME + INTEGER_TYPE + "," + 
			AllMedia.COLUMN_NAME_RATINGS + TEXT_TYPE + "," + 
			AllMedia.COLUMN_NAME_IDS + TEXT_TYPE + "," +
			AllMedia.COLUMN_NAME_SERIES + INTEGER_TYPE + ")";
	
	private static final String SQL_DELETE_TABLE = 
			"DROP TABLE IF EXISTS " + AllMedia.TABLE_NAME;
			
	
	
	
	public AllMediaHelper(Context context){
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
