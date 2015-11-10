package com.sixbynine.movieoracle.util;

import android.graphics.Color;
import android.support.v7.widget.SearchView;
import android.widget.EditText;
import android.widget.ImageView;

import com.sixbynine.movieoracle.R;

public class ViewHelper {

    public static void colorSearchView(SearchView searchView){
        final EditText hint = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if(hint != null){
            hint.setHintTextColor(Color.WHITE);
            hint.setTextColor(Color.WHITE);
        }

        final ImageView closeButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        if(closeButton != null) {
            closeButton.setImageResource(R.drawable.ic_action_navigation_close);
        }

        final ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        if(searchIcon != null){
            searchIcon.setImageResource(R.drawable.ic_action_search);
        }

        final ImageView otherSearchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        if(otherSearchIcon != null){
            otherSearchIcon.setImageResource(R.drawable.ic_action_search);
        }
    }
}
