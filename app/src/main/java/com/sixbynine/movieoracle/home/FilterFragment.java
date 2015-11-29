package com.sixbynine.movieoracle.home;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

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
import com.sixbynine.movieoracle.abstracts.AbstractOnItemSelectedListener;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.RTMovieQueryMovieSummaryWithTitle;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryCastMember;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Filters;
import com.sixbynine.movieoracle.model.Sorting;

import java.util.Comparator;
import java.util.List;

public class FilterFragment extends BaseFragment {

    private Spinner mFilterSpinner;
    private Spinner mSortSpinner;

    private Spinner mFilterSpinnerOption;

    private Callback mCallback;

    public interface Callback {
        void applyFilterAndSort(Filter filter, Sorting sort);

        List<RTMovieQueryMovieSummaryWithTitle> getSummaries();

        void hideFilter();
    }

    public static FilterFragment newInstance() {
        return newInstance(Filter.Type.NONE, null, Sorting.ALPHABETICAL);
    }

    public static FilterFragment newInstance(Filter.Type filterType, String filterParam, Sorting sort) {
        FilterFragment frag = new FilterFragment();
        Bundle b = new Bundle();
        if (filterType != null) {
            b.putString("filterType", filterType.name());
            b.putString("filterParam", filterParam);
        } else {
            b.putString("filterType", Filter.Type.NONE.name());
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
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        mFilterSpinner = (Spinner) view.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.filter_choices,
                R.layout.spinner_text);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilterSpinner.setAdapter(filterAdapter);
        mFilterSpinner.setOnItemSelectedListener(new AbstractOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterParam = null;
                if (mFilterSpinnerOption.getVisibility() == View.VISIBLE) {
                    filterParam = (String) mFilterSpinnerOption.getAdapter().getItem(mFilterSpinnerOption.getSelectedItemPosition());
                }
                setFilterType(Filter.Type.fromName((String) parent.getSelectedItem()), filterParam);
                syncViews();
            }
        });

        mFilterSpinnerOption = (Spinner) view.findViewById(R.id.filter_spinner_choices);
        mFilterSpinnerOption.setOnItemSelectedListener(new AbstractOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                syncViews();
            }
        });

        mSortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);
        mSortSpinner.setOnItemSelectedListener(new AbstractOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                syncViews();
            }
        });
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_choices,
                R.layout.spinner_text);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);

        Bundle args = getArguments();

        Filter.Type filterType = Filter.Type.valueOf(args.getString("filterType"));
        mFilterSpinner.setSelection(Filters.indexOf(filterType));
        setFilterType(filterType, args.getString("filterParam"));

        Sorting sort = Sorting.valueOf(args.getString("sort"));

        view.findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterSpinner.setSelection(0);
                mSortSpinner.setSelection(0);
                syncViews();
            }
        });

        syncViews();

        return view;
    }

    private void setFilterType(Filter.Type filterType, final String filterParam) {
        mFilterSpinnerOption.setVisibility(Filters.hasOptions(filterType) ? View.VISIBLE : View.INVISIBLE);
        List<String> adapter = null;
        switch (filterType) {
            case RATING:
                adapter = ImmutableList.of(Filter.RATING_ALL, Filter.RATING_FRESH);
                break;
            case ACTOR:
                adapter = FluentIterable.from(mCallback.getSummaries())
                        .transformAndConcat(new Function<RTMovieQueryMovieSummaryWithTitle, Iterable<String>>() {
                            @Override
                            public Iterable<String> apply(@Nullable RTMovieQueryMovieSummaryWithTitle summary) {
                                return FluentIterable.from(summary.getSummary().getCast())
                                        .transform(new Function<RTMovieQueryCastMember, String>() {
                                            @Override
                                            public String apply(@Nullable RTMovieQueryCastMember castMember) {
                                                return castMember.getName();
                                            }
                                        }).toList();
                            }
                        })
                        .append(Filter.SELECT_ACTOR)
                        .toSortedSet(new Comparator<String>() {
                            @Override
                            public int compare(String lhs, String rhs) {
                                if (lhs.equals(Filter.SELECT_ACTOR)) {
                                    return -1;
                                } else if (rhs.equals(Filter.SELECT_ACTOR)) {
                                    return 1;
                                } else {
                                    return lhs.compareTo(rhs);
                                }
                            }
                        })
                        .asList();
                break;
            case TITLE:
                throw new IllegalStateException("Unexpected state: TITLE");
        }

        if (adapter != null) {
            int index = filterParam == null ? 0 : Iterables.indexOf(adapter, new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String s) {
                    return s != null && s.equals(filterParam);
                }
            });
            ArrayAdapter<String> filterSpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text, adapter);
            filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFilterSpinnerOption.setAdapter(filterSpinnerAdapter);
            mFilterSpinnerOption.setSelection(index, true);
        }
    }

    private void syncViews() {
        Filter.Type filterType = Filter.Type.fromName((String) mFilterSpinner.getSelectedItem());
        String filterParam = null;

        if (mFilterSpinnerOption.getVisibility() == View.VISIBLE) {
            filterParam = (String) mFilterSpinnerOption.getAdapter().getItem(mFilterSpinnerOption.getSelectedItemPosition());
        }
        mFilterSpinnerOption.setVisibility(Filters.hasOptions(filterType) ? View.VISIBLE : View.INVISIBLE);

        Sorting sort = Sorting.fromName((String) mSortSpinner.getSelectedItem());

        Filter filter;
        if (filterType != null && !(filterParam == null && Filters.hasOptions(filterType))) {
            filter = Filters.create(filterType, filterParam);
        } else {
            filter = Filters.create(Filter.Type.NONE, null);
        }

        mCallback.applyFilterAndSort(filter, sort);
    }
}
