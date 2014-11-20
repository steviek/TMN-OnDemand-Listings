package com.sixbynine.movieoracle.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sort;

/**
 * Created by steviekideckel on 11/20/14.
 */
public class FilterFragment extends Fragment{

    private LinearLayout mEditLayout;
    private Spinner mFilterSpinner;
    private Spinner mSortSpinner;
    private Button mGoButton;

    private RelativeLayout mDisplayLayout;
    private TextView mDisplayTextView;
    private Button mChangeButton;
    private Callback mCallback;

    private Filter mFilter;
    private Sort mSort;

    public interface Callback{
        public void applyFilterAndSort(Filter filter, Sort sort);
    }

    public static FilterFragment newInstance(){
        return newInstance(Filter.NONE, Sort.ALPHABETICAL);
    }

    public static FilterFragment newInstance(Filter filter, Sort sort){
        FilterFragment frag = new FilterFragment();
        Bundle b = new Bundle();
        if(filter != null){
            b.putInt("filter", filter.id);
        }
        if(sort != null){
            b.putInt("sort", sort.id);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        mEditLayout = (LinearLayout) view.findViewById(R.id.edit_layout);
        mFilterSpinner = (Spinner) view.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter_choices,
                android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilterSpinner.setAdapter(filterAdapter);

        mSortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_choices,
                android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);

        mGoButton = (Button) view.findViewById(R.id.button_go);
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter();
            }
        });

        mDisplayLayout = (RelativeLayout) view.findViewById(R.id.display_layout);
        mDisplayTextView = (TextView) view.findViewById(R.id.summary_text_view);
        mChangeButton = (Button) view.findViewById(R.id.button_change);
        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLayout.setVisibility(View.VISIBLE);
                mDisplayLayout.setVisibility(View.GONE);
            }
        });

        if(mFilter != null){
            mFilterSpinner.setSelection(mFilter.id);
        }

        if(mSort != null){
            mSortSpinner.setSelection(mSort.id);
        }

        return view;
    }

    private void applyFilter(){
        mFilter = Filter.fromId(mFilterSpinner.getSelectedItemPosition());
        mSort = Sort.fromId(mSortSpinner.getSelectedItemPosition());
        mEditLayout.setVisibility(View.GONE);
        mDisplayLayout.setVisibility(View.VISIBLE);
        if(mFilter == Filter.NONE){
            mDisplayTextView.setText(getString(R.string.sorting_by_string, mSort.name));
        }else{
            mDisplayTextView.setText(getString(R.string.filtering_by_string, mFilter.name) + ", " +
                    getString(R.string.sorting_by_string, mSort.name));
        }
        mCallback.applyFilterAndSort(mFilter, mSort);
    }

    public Filter getFilter(){
        return mFilter;
    }

    public Sort getSort(){
        return mSort;
    }
}
