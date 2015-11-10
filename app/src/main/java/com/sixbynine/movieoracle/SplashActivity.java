package com.sixbynine.movieoracle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.sixbynine.movieoracle.events.RTMovieQueryResultLoadedEvent;
import com.sixbynine.movieoracle.events.TMNResourcesLoadedEvent;
import com.sixbynine.movieoracle.home.HomeActivity;
import com.sixbynine.movieoracle.manager.DataManager;
import com.sixbynine.movieoracle.ui.activity.BaseActivity;
import com.squareup.otto.Subscribe;

@Subscribes
public final class SplashActivity extends BaseActivity {

    private TextView mProgressNumberTextView;
    private TextView mProgressTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
        mProgressNumberTextView = (TextView) findViewById(R.id.progress_number_text_view);
        mProgressTextView = (TextView) findViewById(R.id.progress_text_view);
	}

    @Override
    protected void onResume() {
        super.onResume();
        syncViews();
    }

    private void syncViews() {
        DataManager.State state = DataManager.loadDataIfNecessary();
        switch (state) {
            case ERROR_NO_INTERNET:
                Toast.makeText(getApplicationContext(), "No network connection.", Toast.LENGTH_LONG).show();
                startHomeActivity();
                break;
            case LOADED:
                startHomeActivity();
                break;
            default:
                mProgressTextView.setText("Retrieving data for new movies and series."
                        + "\n\nIf this is the first time running the app this may take a couple of minutes.");
        }
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onTMNResourcesLoaded(TMNResourcesLoadedEvent event) {
        mProgressTextView.setText("Retrieving data for new movies and series."
                + "\n\nIf this is the first time running the app this may take a couple of minutes.");
    }

    @Subscribe
    public void onRTMovieQueryResultLoaded(RTMovieQueryResultLoadedEvent event) {
        mProgressNumberTextView.setText(event.getIndex() + "/" + event.getTotal());
        mProgressTextView.setText(getString(R.string.loaded, event.getTitle()));
    }
}
