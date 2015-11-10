package com.sixbynine.movieoracle.home;

import com.google.common.collect.FluentIterable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sixbynine.movieoracle.BaseFragment;
import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sorting;
import com.sixbynine.movieoracle.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class SummaryListFragment extends BaseFragment {

    private SummaryListAdapter mAdapter;
    private TextView mNoResultsTextView;

    private List<RTMovieQueryMovieSummary> mAllSummaries;
    private Callback mCallback;

    private int mIndex;

    public interface Callback {
        void onItemSelected(int index, RTMovieQueryMovieSummary item);

        void onItemMovedToTop(int index, RTMovieQueryMovieSummary item);
    }

    public static SummaryListFragment newInstance(int index) {
        SummaryListFragment frag = new SummaryListFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
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
        mAllSummaries = Prefs.getCurrentBestSummaries();
        if (savedInstanceState == null) {
            mIndex = getArguments().getInt("index");
            if (mIndex == -1) {
                mIndex = 0;
            }
        } else {
            mIndex = savedInstanceState.getInt("index", 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", mIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_view);

        mNoResultsTextView = (TextView) view.findViewById(R.id.no_results_view);

        mAdapter = new SummaryListAdapter(getActivity(), new ArrayList<RTMovieQueryMovieSummary>());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) mCallback.onItemSelected(position, mAdapter.getItem(position));
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != lastFirstVisibleItem && !mAdapter.isEmpty()) {
                    lastFirstVisibleItem = firstVisibleItem;
                    mCallback.onItemMovedToTop(firstVisibleItem, mAdapter.getItem(firstVisibleItem));
                }
                mIndex = firstVisibleItem;
            }
        });
        listView.setSelection(mIndex);

        return view;
    }

    public void sortAndFilter(Sorting sort, Filter filter) {
        mAdapter.clear();
        mAdapter.addAll(FluentIterable.from(mAllSummaries).filter(filter).toSortedList(sort));
        mAdapter.notifyDataSetChanged();
        mNoResultsTextView.setVisibility(mAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public int getIndex() {
        return mIndex;
    }
}
