package com.example.fahadhd.moviesearch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewMoviesFragment extends Fragment {
    GridView movieGrid;
    public ViewMoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_movies_fragment, container, false);
        movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
        return rootView;
    }
}
