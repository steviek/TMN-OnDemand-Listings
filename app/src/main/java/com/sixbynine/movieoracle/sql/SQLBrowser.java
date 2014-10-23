package com.sixbynine.movieoracle.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLBrowser {

	public static void executeQuery(String query){
		Connection c = null;
	    Statement stmt = null;
	    List<String> movies = new ArrayList<String>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	     
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( query);
	      if(query.contains("on_demand_db")){
	    	  System.out.println("title, movie, episode, series, season");
	    	  while ( rs.next() ) {
		    	  System.out.print(rs.getString("title") + ", ");
		    	  System.out.print("" + rs.getObject("movie")+ ", ");
		    	  System.out.print("" + rs.getObject("episode")+ ", ");
		    	  System.out.print(rs.getString("series")+ ", ");
		    	  System.out.println("" + rs.getInt("season"));
		      }
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	}
	
	public static void main(String[] args){
		BufferedReader br =  new BufferedReader(new InputStreamReader(System.in));
		 
		String input = null;
		while(true){
			System.out.println("Enter a query:");
			try {
				input = br.readLine();
			} catch (IOException e) {
				break;
			}
			if(input == null) break;
			executeQuery(input);
		}
	}
}
