package com.sixbynine.movieoracle.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sixbynine.movieoracle.R;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.ui.fragment.ActionBarFragment;

/**
 * Created by steviekideckel on 10/22/14.
 */
public class NewMoviesFragment extends ActionBarFragment{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Catalogue mMediaList;

    public static NewMoviesFragment newInstance(Catalogue mediaList){
        NewMoviesFragment frag = new NewMoviesFragment();
        Bundle b = new Bundle();
        b.putParcelable("catalogue", mediaList);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            mMediaList = getArguments().getParcelable("catalogue");
        }else{
            mMediaList = savedInstanceState.getParcelable("catalogue");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("catalogue", mMediaList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewMoviesAdapter(mMediaList);
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }


}
