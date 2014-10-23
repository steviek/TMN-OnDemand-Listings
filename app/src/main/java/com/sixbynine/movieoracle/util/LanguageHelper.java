package com.sixbynine.movieoracle.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class LanguageHelper {

	private static LanguageHelper me = null;
	private HashMap<String, String> codes;
	
	public LanguageHelper(String codefile){
		populateCodes(codefile);
	}
	
	public static LanguageHelper getInstance(){
		if(me == null) me = new LanguageHelper("languagecodes");
		return me;
	}
	
	private void populateCodes(String codefile){
		 try{
		 InputStream is = LanguageHelper.class.getResourceAsStream(codefile);
		 codes = new HashMap<String, String>();
		 BufferedReader r = new BufferedReader(new InputStreamReader(is));

	     // reads each line
	     String l;
	     while((l = r.readLine()) != null) {
	    	String[] vals = l.split(",");
	    	String exp = vals[0];
	    	String code = vals[1];
	    	codes.put(exp, code);
	     } 
	     is.close(); 
		 
		 }catch(IOException e){
			 e.printStackTrace();
		 }
		 
	 }
	
	public String getLanguageFromCode(String title){
		 String result = "";
		 
			 Iterator<Entry<String, String>> iter = codes.entrySet().iterator();
			 while(iter.hasNext()){
				 Entry<String, String> pair = iter.next();
				 if(title.equals(pair.getKey())){
					 result = pair.getValue();
					 break;
				 }	 
			 }
			 
		 
		 
		 return result;
	 }
	
}
