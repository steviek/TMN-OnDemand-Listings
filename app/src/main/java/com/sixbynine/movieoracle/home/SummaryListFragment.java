package com.sixbynine.movieoracle.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.object.RottenTomatoesSummary;
import com.sixbynine.movieoracle.ui.fragment.ActionBarFragment;

import java.util.ArrayList;

/**
 * Created by steviekideckel on 11/2/14.
 */
public class SummaryListFragment extends ActionBarFragment{

    private ListView mListView;
    private SummaryListAdapter mAdapter;

    private ArrayList<RottenTomatoesSummary> mSummaries;
    private Callback mCallback;

    public interface Callback{
        public void onItemSelected(RottenTomatoesSummary item);
    }

    public static SummaryListFragment newInstance(ArrayList<RottenTomatoesSummary> summaries){
        SummaryListFragment frag = new SummaryListFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList("summaries", summaries);
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
            mSummaries = getArguments().getParcelableArrayList("summaries");
        }else{
            mSummaries = savedInstanceState.getParcelableArrayList("summaries");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("summaries", mSummaries);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view);

        mAdapter = new SummaryListAdapter(getActivity(), mSummaries);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallback != null) mCallback.onItemSelected(mSummaries.get(position));
            }
        });

        return view;
    }
}
