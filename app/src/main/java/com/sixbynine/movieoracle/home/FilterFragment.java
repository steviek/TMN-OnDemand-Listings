package com.sixbynine.movieoracle.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sixbynine.movieoracle.BaseFragment;
import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.NoFilter;
import com.sixbynine.movieoracle.model.Sorting;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;

import java.util.List;

public class FilterFragment extends BaseFragment {

    private Spinner mFilterSpinner;
    private Spinner mSortSpinner;

    private Spinner mFilterSpinnerOption;

    private Callback mCallback;

    private Filter mFilter;
    private Sorting mSort;

    public interface Callback {
        void applyFilterAndSort(Filter filter, Sorting sort);

        List<RTMovieQueryMovieSummary> getSummaries();

        void hideFilter();
    }

    public static FilterFragment newInstance() {
        return newInstance(new NoFilter(), Sorting.ALPHABETICAL);
    }

    public static FilterFragment newInstance(Filter filter, Sorting sort) {
        FilterFragment frag = new FilterFragment();
        Bundle b = new Bundle();
        if (filter != null) {
            b.putParcelable("filter", filter);
        }
        if (sort != null) {
            b.putString("sort", sort.name());
        }
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new IllegalStateException(context.toString() + " must implement Callback interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mFilter = args.getParcelable("filter");
            mSort = Sorting.valueOf(args.getString("sort"));
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
                if (mFilter.showFilter() && mSort == Sorting.ALPHABETICAL) {
                    mCallback.hideFilter();
                } else {
                    mFilter = new NoFilter();
                    mSort = Sorting.ALPHABETICAL;
                    initFilteredViews();
                    mCallback.applyFilterAndSort(mFilter, mSort);
                }
            }
        });

        initFilteredViews();


        return view;
    }

    private void initFilteredViews() {
        if (mFilter != null) {
            mFilterSpinner.setSelection(mFilter.id);

            if (!mFilter.showSpinner()) {
                mFilterSpinnerOption.setVisibility(View.INVISIBLE);
            } else {
                mFilterSpinnerOption.setVisibility(View.VISIBLE);
                List<String> choices = RottenTomatoesSummary.getAllChoices(mCallback.getSummaries(), mFilter);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_text,
                        choices);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mFilterSpinnerOption.setAdapter(adapter);
            }

        }

        if (mSort != null) {
            mSortSpinner.setSelection(mSort.id);
        }
    }

    private void applyFilter(boolean initAfter) {

        mFilter = Filter.fromId(mFilterSpinner.getSelectedItemPosition());

        mSort = Sorting.fromId(mSortSpinner.getSelectedItemPosition());

        mCallback.applyFilterAndSort(mFilter, mSort);

        if (initAfter) {
            initFilteredViews();
        }
    }

    public void setFilter(Filter filter) {
        mFilter = filter;
        initFilteredViews();
    }

    public Filter getFilter() {
        return mFilter;
    }

    public Sorting getSort() {
        return mSort;
    }
}
