package com.sixbynine.movieoracle.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.model.Filter;
import com.sixbynine.movieoracle.model.Sort;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.fragment.ActionBarFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class SummaryListFragment extends ActionBarFragment{

    private ListView mListView;
    private SummaryListAdapter mAdapter;
    private TextView mNoResultsTextView;

    private ArrayList<RottenTomatoesSummary> mAllSummaries;
    private ArrayList<RottenTomatoesSummary> mDisplaySummaries;
    private Callback mCallback;

    private int mIndex;

    public interface Callback{
        public void onItemSelected(int index, RottenTomatoesSummary item);
        public void onItemMovedToTop(int index, RottenTomatoesSummary item);
    }

    public static SummaryListFragment newInstance(ArrayList<RottenTomatoesSummary> summaries, int index){
        SummaryListFragment frag = new SummaryListFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList("summaries", summaries);
        b.putInt("index", index);
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
            mAllSummaries = getArguments().getParcelableArrayList("summaries");
            mDisplaySummaries = new ArrayList<RottenTomatoesSummary>(mAllSummaries);
            mIndex = getArguments().getInt("index");
            if(mIndex == -1){
                mIndex = 0;
            }
        }else{
            mAllSummaries = savedInstanceState.getParcelableArrayList("summaries");
            mIndex = savedInstanceState.getInt("index", 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("summaries", mAllSummaries);
        outState.putInt("index", mIndex);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view);

        mNoResultsTextView = (TextView) view.findViewById(R.id.no_results_view);

        mAdapter = new SummaryListAdapter(getActivity(), mDisplaySummaries);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallback != null) mCallback.onItemSelected(position, mDisplaySummaries.get(position));
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem != lastFirstVisibleItem && !mDisplaySummaries.isEmpty()){
                    lastFirstVisibleItem = firstVisibleItem;
                    mCallback.onItemMovedToTop(firstVisibleItem, mDisplaySummaries.get(firstVisibleItem));
                }
                mIndex = firstVisibleItem;
            }
        });
        mListView.setSelection(mIndex);



        return view;
    }

    public void sortAndFilter(Sort sort, Filter filter, String parameter){
        Object param;
        if(filter == Filter.ACTOR && parameter != null && parameter.equals(RottenTomatoesSummary.SELECT_ACTOR)){
            param = null;
        }else if(filter == Filter.RATING){
            if(parameter != null && parameter.equals(RottenTomatoesSummary.RATING_FRESH)){
                param = 60;
            }else{
                param = null;
            }
        }else{
            param = parameter;
        }
        List<RottenTomatoesSummary> sortedAndFiltered =
                RottenTomatoesSummary.sortAndFilterList(mAllSummaries, sort, filter, param);
        mDisplaySummaries.clear();
        mDisplaySummaries.addAll(sortedAndFiltered);
        mAdapter.notifyDataSetChanged();
        mNoResultsTextView.setVisibility(mAdapter.getCount() == 0? View.VISIBLE : View.GONE);
    }

    public int getIndex(){
        return mIndex;
    }

    public void onPositionSelected(int position){
        mAdapter.setSelectedIndex(position);
    }
}
