package com.sixbynine.movieoracle.sql;
import java.sql.*;

import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.media.Media;
import com.sixbynine.movieoracle.media.Movie;
import com.sixbynine.movieoracle.media.Rating;
import com.sixbynine.movieoracle.media.Series;

public class MediaDataBase
{
	private static final String DATABASE_NAME = "movies.db";
	private static final String TABLE_NAME = "movie_db";
	private static final String MOVIE_TABLE_CREATE = "create table if not exists " + TABLE_NAME 
    		+ "("
    		+ "title varchar(255) not null,"
    		+ "year integer,"
    		+ " genres varchar(255),"
    		+ "cast varchar(255),"
    		+ "directors varchar(255),"
    		+ "languages varchar(255),"
    		+"synopsis varchar(255),"
    		+"parental_advisory varchar(255),"
    		+"runtime integer,"
    		+"ratings varchar(255),"
    		+" ids varchar(255),"
    		+" series bit"
    		+ ");";
	private static final String MOVIE_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
	
	public static void clearDatabase(){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);

	      stmt = c.createStatement();
	      stmt.execute(MOVIE_TABLE_DROP);
	      stmt.execute(MOVIE_TABLE_CREATE);
	      stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Table Cleared");
	  }
  
	 
	
	public static void load(Catalogue catalogue){
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM " + TABLE_NAME + ";" );
	      while ( rs.next() ) {
	    	  String title = rs.getString("title");
	    	  int series = (Integer) rs.getObject("series");
	         Media m = (series == 1? new Series(title) : new Movie(title));
	         
	         String year = rs.getInt("year") + "";
	         m.setYear(year);
	         
	         String[] genres = rs.getString("genres").split(",");
	         for(String genre : genres){
	        	 m.addGenre(genre);
	         }
	         
	         String[] cast = rs.getString("cast").split(",");
	         for(String member : cast){
	        	 m.addCastMember(member);
	         }
	         
	         String[] directors = rs.getString("directors").split(",");
	         for(String director : directors){
	        	 m.addDirector(director);
	         }
	         
	         String[] languages = rs.getString("languages").split(",");
	         for(String language : languages){
	        	 m.addLanguage(language);
	         }
	         
	         String synopsis = rs.getString("synopsis");
	         m.setSynopsis(synopsis);
	         
	         String advisory = rs.getString("parental_advisory");
	         m.setParentalAdvisory(advisory);
	         
	         String runtime = rs.getInt("runtime") + "";
	         m.setRuntime(runtime);
	         
	         String[] ratings = rs.getString("ratings").split(",");
	         for(String rating : ratings){
	        	 m.addRating(Rating.parseRating(rating));
	         }
	         
	         String [] ids = rs.getString("ids").split(",");
	         for(String id : ids){
	        	 String[] entry = id.split(";");
	        	 if(entry.length == 2)
	        		 m.addId(entry[0], entry[1]);
	         }
	         
	         
	         
	         
	         
	         
	         
	         catalogue.add(m);
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Operation done successfully");
	  }
	

	
  public static void save(Catalogue catalogue, boolean override )
  {
	  Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");

	      stmt = c.createStatement();
	      if(override){
	    	  stmt.execute(MOVIE_TABLE_DROP);
	      }
	      stmt.execute(MOVIE_TABLE_CREATE);
	      
	      for(Media m : catalogue){
	    	  if(!alreadyStored(m, stmt)){
		    	  String query = m.getQuery(TABLE_NAME);
		    	  System.out.println(query);
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
  
	  private static boolean alreadyStored(Media m, Statement stmt) throws SQLException{
		  String queryTitle = m.getTitle().replaceAll("'", "''");
		  String existenceQuery = "SELECT * FROM " + TABLE_NAME + " WHERE title = '" + queryTitle + "';";
		  ResultSet rs = stmt.executeQuery(existenceQuery);
		  if(rs.next()){
			  return true;
		  }else{
			  return false;
		  }
		  
	  }
	}


