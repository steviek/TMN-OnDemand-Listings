package com.sixbynine.movieoracle;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.sixbynine.movieoracle.ui.fragment.ActionBarFragment;
import com.sixbynine.movieoracle.util.Prefs;

public class SettingsFragment extends ActionBarFragment implements OnValueChangeListener {

	private NumberPicker npCheckFrequency;
	
	private int checkOnDemandFrequency;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_settings, null);
		
		checkOnDemandFrequency = Prefs.getCheckingFrequency(getActivity(), 3);
		

		npCheckFrequency = (NumberPicker) view.findViewById(R.id.NumberPickerUpdateDays);
		
		npCheckFrequency.setMinValue(0);
		npCheckFrequency.setMaxValue(7);
		npCheckFrequency.setValue(checkOnDemandFrequency);
		npCheckFrequency.setOnValueChangedListener(this);
		
		return view;
	}


	@Override
	public void onValueChange(NumberPicker np, int arg1, int arg2) {
		if(this.isDetached()) return;
		Prefs.saveCheckingFrequency(getActivity(), npCheckFrequency.getValue());
		//SplashActivity.cb.updateSettings(new int[] {npCheckFrequency.getValue(), npNumTopRated.getValue()});
	}
	
	@Override
	public void onResume() {
		super.onResume();
        getActivity().setTitle("Settings");
		ActionBar ab = getActivity().getActionBar();
		ab.setTitle("Settings");
	}
	
}
