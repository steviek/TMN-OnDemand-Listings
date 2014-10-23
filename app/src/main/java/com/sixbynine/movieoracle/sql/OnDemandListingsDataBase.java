package com.sixbynine.movieoracle.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Season;
import com.sixbynine.movieoracle.media.Series;

public class OnDemandListingsDataBase {
	private static final String TABLE_NAME = "on_demand_db";
	private static final String TABLE_CREATE = "create table if not exists " + TABLE_NAME 
    		+ "("
    		+ "title varchar(255) not null,"
    		+ "movie bit not null,"
    		+ "episode bit not null,"
    		+ "series varchar(255) null,"
    		+ "season integer null"
    		+ ");";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
	
	public static void clearDatabase(){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      stmt.execute(TABLE_DROP);
	      stmt.execute(TABLE_CREATE);
	      stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Table Cleared");
	  }
  
	 
	
	public static List<String> getMovies(){
		Connection c = null;
	    Statement stmt = null;
	    List<String> movies = new ArrayList<String>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	     
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT title FROM " + TABLE_NAME + 
	    		  " WHERE movie = 1;" );
	      while ( rs.next() ) {
	    	  String title = rs.getString("title");
	    	  movies.add(title);
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Operation done successfully");
	    return movies;
	  }
	
	public static TreeMap<String, List<String>> getSeries(){
		Connection c = null;
	    Statement stmt = null;
	    TreeMap<String, List<String>> series = new TreeMap<String, List<String>>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	     
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM " + TABLE_NAME + 
	    		  " WHERE movie = 0 AND episode = 0;" );
	      List<String> seriesNames = new ArrayList<String>();
	      while (rs.next() ) {
	    	  String seriesName = rs.getString("title");
	    	  seriesNames.add(seriesName);
	    	  
	      }
	      rs.close();
	      
	      for(String seriesName : seriesNames){
	    	  ResultSet episodes = stmt.executeQuery("SELECT * FROM " + TABLE_NAME +
    			  " WHERE episode = 1 AND series = '" + seriesName + "';");
	    	  List<String> episodeListing = new ArrayList<String>();
	    	  while(episodes.next()){
	    		  String episodeTitle = episodes.getString("title");
	    		  episodeListing.add(episodeTitle);
	    	  }
	    	  series.put(seriesName, episodeListing);
	    	  episodes.close();
	      }
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Operation done successfully");
	    return series;
	  }
	
	
	
	

	
  public static void save(Catalogue catalogue)
  {
	  Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");

	      stmt = c.createStatement();
	      stmt.execute(TABLE_DROP);
	      stmt.execute(TABLE_CREATE);
	      
	      for(Media m : catalogue){
	    	  String title = m.getTitle().replaceAll("'", "''");
	    	  if(m instanceof Movie){
	    		  String query = "INSERT INTO " + TABLE_NAME
	    				  + " VALUES('" + title + "',1,0,NULL,NULL);";
	    		  //System.out.println(query);
		    	  stmt.executeUpdate(query);
	    	  }else{
	    		  List<com.sixbynine.movieoracle.media.Season> seasons = ((Series) m ).getSeasons(); 
	    		  for(Season season : seasons){
	    			  for(String episode : season.getEpisodes()){
	    				  String episodeQuery = "INSERT INTO " + TABLE_NAME + 
	    						  " VALUES('" + episode.replaceAll("'", "''") + "',0,1,'" + title + "'," + season.getId() + ");";
	    				  //System.out.println(episodeQuery);
	    		    	  stmt.executeUpdate(episodeQuery);
	    			  }
	    		  }
	    		  String query = "INSERT INTO " + TABLE_NAME
	    				  + " VALUES('" + title + "',0,0,NULL,NULL);";
	    		  //System.out.println(query);
		    	  stmt.executeUpdate(query);
	    	  }
	    	  
	    	  
	      }

	      stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Records created successfully");
	  }
  
	}



