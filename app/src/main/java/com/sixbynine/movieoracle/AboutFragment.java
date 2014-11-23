package com.sixbynine.movieoracle;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//TODO put acknowledgement somewhere
public class AboutFragment extends DialogFragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_about, null);
		((TextView) view.findViewById(R.id.tv_about_about)).setMovementMethod(LinkMovementMethod.getInstance());
		return view;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

    	switch(item.getItemId()){
    	case R.id.menu_dev_website:
    		String url = "http://www.sixbynineapps.webs.com";
    		Intent i = new Intent(Intent.ACTION_VIEW);
    		i.setData(Uri.parse(url));
    		startActivity(i);
    		return true;
    	default: 
    			return super.onOptionsItemSelected(item);
    	}
		
	}

}
