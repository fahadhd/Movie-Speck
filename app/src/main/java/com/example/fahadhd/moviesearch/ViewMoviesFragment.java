package com.example.fahadhd.moviesearch;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewMoviesFragment extends Fragment {
    GridView movieGrid;
    ArrayList<String> posters;
    static int width;
    boolean sortByPop;

    public ViewMoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_movies_fragment, container, false);

        //Get the dimensions of the screen
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //If device is a table show 6 items else show 3
        if(MainActivity.TABLET){
            width = size.x/6;
        }
        else{
            width = size.x/3;
        }
        movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
        movieGrid.setColumnWidth(width);
        return rootView;
    }
    public class MovieAdapter extends ArrayAdapter{

        public MovieAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }
}
