package com.sixbynine.movieoracle.util;

import com.sixbynine.movieoracle.media.Catalogue;

public class CatalogueHolder {
	private Catalogue catalogue;
	
	private static CatalogueHolder instance;
	
	private CatalogueHolder(){
		
	}
	
	public static CatalogueHolder getInstance(){
		if(instance == null){
			instance = new CatalogueHolder();
		}
		return instance;
	}
	
	public void setCatalogue(Catalogue catalogue){
		this.catalogue = catalogue;
	}
	
	public Catalogue getCatalogue(){
		return catalogue;
	}
	
	
}
