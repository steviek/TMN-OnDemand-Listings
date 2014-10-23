package com.sixbynine.movieoracle.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class URIHelper {
	
	private static URIHelper me = null;
	private HashMap<String, String> codes;
	
	public URIHelper(Resources res, String codefile){
		populateCodes(res, codefile);
	}
	
	public static URIHelper getInstance(Resources resources){
		if(me == null) me = new URIHelper(resources, "uricodes");
		return me;
	}
	
	private void populateCodes(Resources resources, String codefile){
		 try{
         AssetManager assetManager = resources.getAssets();
		 InputStream is = assetManager.open(codefile);
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
	
	public String getAsURI(String title){
		 String result = "";
		 for(int i = 0; i < title.length(); i ++){
			 String c = "" + title.charAt(i);
			 Iterator<Entry<String, String>> iter = codes.entrySet().iterator();
			 while(iter.hasNext()){
				 Entry<String, String> pair = iter.next();
				 if(c.equals(pair.getKey())){
					 //System.out.println("'" + c + "'" + " = " + "'" + pair.getKey() + "'");
					 c = pair.getValue();
					 break;
				 }
				 
					 
			 }
			 result += c;
			 
		 }
		 
		 return result;
	 }
}
