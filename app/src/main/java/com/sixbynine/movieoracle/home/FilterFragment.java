package com.sixbynine.movieoracle.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sort;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;

import java.util.List;

/**
 * Created by steviekideckel on 11/20/14.
 */
public class FilterFragment extends Fragment{

    private Spinner mFilterSpinner;
    private Spinner mSortSpinner;

    private Spinner mFilterSpinnerOption;

    private Callback mCallback;

    private Filter mFilter;
    private String mFilterParameter;
    private Sort mSort;

    public interface Callback{
        public void applyFilterAndSort(Filter filter, Sort sort, String parameter);
        public List<RottenTomatoesSummary> getSummaries();
        public void hideFilter();
    }

    public static FilterFragment newInstance(){
        return newInstance(Filter.NONE, Sort.ALPHABETICAL, null);
    }

    public static FilterFragment newInstance(Filter filter, Sort sort, String parameter){
        FilterFragment frag = new FilterFragment();
        Bundle b = new Bundle();
        if(filter != null){
            b.putInt("filter", filter.id);
        }
        if(sort != null){
            b.putInt("sort", sort.id);
        }
        if(parameter != null){
            b.putString("param", parameter);
        }
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof Callback){
            mCallback = (Callback) activity;
        }else{
            throw new IllegalStateException(activity.toString() + " must implement Callback interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            Bundle args = getArguments();
            int filterKey = args.getInt("filter", -1);
            if(filterKey != -1) mFilter = Filter.fromId(filterKey);
            int sortKey = args.getInt("sort", -1);
            if(sortKey != -1) mSort = Sort.fromId(sortKey);
            mFilterParameter = args.getString("param");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        mFilterSpinner = (Spinner) view.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter_choices,
                R.layout.spinner_text);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilterSpinner.setAdapter(filterAdapter);
        mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFilterSpinnerOption = (Spinner) view.findViewById(R.id.filter_spinner_choices);
        mFilterSpinnerOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_choices,
                R.layout.spinner_text);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);

        view.findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterParameter = null;
                mFilter = Filter.NONE;
                mSort = Sort.ALPHABETICAL;
                initFilteredViews();
                mCallback.applyFilterAndSort(mFilter, mSort, mFilterParameter);
            }
        });

        final View closeButton = view.findViewById(R.id.close_button);
        if(closeButton != null){
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterParameter = null;
                    mFilter = Filter.NONE;
                    mSort = Sort.ALPHABETICAL;
                    initFilteredViews();
                    mCallback.applyFilterAndSort(mFilter, mSort, mFilterParameter);
                    mCallback.hideFilter();
                }
            });
        }

        initFilteredViews();



        return view;
    }

    private void initFilteredViews(){
        if(mFilter != null){
            mFilterSpinner.setSelection(mFilter.id);

            if(mFilter == Filter.NONE){
                mFilterSpinnerOption.setVisibility(View.INVISIBLE);
            }else if(mFilter == Filter.TITLE){
                mFilterSpinnerOption.setVisibility(View.INVISIBLE);
            }else{
                mFilterSpinnerOption.setVisibility(View.VISIBLE);
                List<String> choices = RottenTomatoesSummary.getAllChoices(mCallback.getSummaries(), mFilter);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_text,
                        choices);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mFilterSpinnerOption.setAdapter(adapter);

                if(mFilterParameter != null){
                    int index = choices.indexOf(mFilterParameter);
                    if(index != -1){
                        mFilterSpinnerOption.setSelection(index);
                    }
                }
            }

        }

        if(mSort != null){
            mSortSpinner.setSelection(mSort.id);
        }
    }

    private void applyFilter(boolean initAfter){

        mFilter = Filter.fromId(mFilterSpinner.getSelectedItemPosition());

        mSort = Sort.fromId(mSortSpinner.getSelectedItemPosition());

        if(mFilterSpinnerOption.getVisibility() == View.VISIBLE){
            mFilterParameter = (String) mFilterSpinnerOption.getSelectedItem();
        }else{
            mFilterParameter = null;
        }
        mCallback.applyFilterAndSort(mFilter, mSort, mFilterParameter);

        if(initAfter){
            initFilteredViews();
        }
    }

    public void setFilter(Filter filter, String param){
        mFilter = filter;
        mFilterParameter = param;
        initFilteredViews();
    }

    public Filter getFilter(){
        return mFilter;
    }

    public Sort getSort(){
        return mSort;
    }
}
