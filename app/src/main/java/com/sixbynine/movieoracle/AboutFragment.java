package com.sixbynine.movieoracle;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//TODO put acknowledgement somewhere
public class AboutFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_about, null);
        ((TextView) view.findViewById(R.id.tv_about_about)).setMovementMethod(LinkMovementMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("About")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

    	switch(item.getItemId()){
    	case R.id.menu_dev_website:
    		String url = "http://stevenkideckel.wordpress.com/six-by-nine-apps/";
    		Intent i = new Intent(Intent.ACTION_VIEW);
    		i.setData(Uri.parse(url));
    		startActivity(i);
    		return true;
    	default: 
    			return super.onOptionsItemSelected(item);
    	}
		
	}

}
