package com.example.fahadhd.moviesearch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {
    Movie currentMovie;
    public static final String TAG = MovieDetailsFragment.class.getSimpleName();
    public static String youtube1;
    public static String overview;
    public static String rating;
    public static String date;
    public static String title;
    public static String poster;
    public static boolean favorite;
    public static ArrayList<String> comments;
    public static Button b;
    private ShareActionProvider mShareActionProvider;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        else{

        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for " + title + ": " +
                "https://www.youtube.com/watch?v=" + youtube1);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<String> posters = ViewMoviesFragment.posters;
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Intent intent = getActivity().getIntent();
        getActivity().setTitle("Movie Details");

        if(intent != null && intent.hasExtra(ViewMoviesFragment.MOVIE_KEY)){
            currentMovie = (Movie)intent.getSerializableExtra(ViewMoviesFragment.MOVIE_KEY);
            Log.v(TAG,currentMovie.movieTitle);
           
        }

        if (intent != null && intent.hasExtra("overview")) {
            overview = intent.getStringExtra("overview");
            ((TextView) rootView.findViewById(R.id.overview)).setText(overview);
        }
        if (intent != null && intent.hasExtra("title")) {
            title = intent.getStringExtra("title");
            ((TextView) rootView.findViewById(R.id.title)).setText(title);
        }
        if (intent != null && intent.hasExtra("rating")) {
            rating = intent.getStringExtra("rating");
            ((TextView) rootView.findViewById(R.id.rating)).setText(rating);
        }
        if (intent != null && intent.hasExtra("dates")) {
            date = intent.getStringExtra("dates");
            ((TextView) rootView.findViewById(R.id.date)).setText(date);
        }
        if (intent != null && intent.hasExtra("poster")) {
            poster = intent.getStringExtra("poster");
            int width = ViewMoviesFragment.width;
            ImageView posterImage = (ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).
                    resize(width, (int) (width * 2)).into(posterImage);
            ;
        }
        if (intent != null && intent.hasExtra("youtube1")) {
            youtube1 = intent.getStringExtra("youtube1");
        }
        if (intent != null && intent.hasExtra("comments")) {
            comments = intent.getStringArrayListExtra("comments");
            for (int i = 0; i < comments.size(); i++) {
                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);
                View divider = new View(getActivity());
                TextView commentView = new TextView(getActivity());
                RelativeLayout.LayoutParams p = new RelativeLayout.
                        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                commentView.setLayoutParams(p);
                int paddingPixel = 10;
                float density = getActivity().getResources().getDisplayMetrics().density;
                int paddingDP = (int) (paddingPixel * density);
                commentView.setPadding(0, paddingDP, 0, paddingDP);
                RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                x.height = 1;
                divider.setLayoutParams(x);
                divider.setBackgroundColor(Color.BLACK);
                commentView.setText(comments.get(i));

            }
            if (intent != null && intent.hasExtra("favorites")) {
                favorite = intent.getBooleanExtra("favorite", false);
                if(!favorite)
                {
                    b.setText("FAVORITE");
                    b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
                else
                {
                    b.setText("UNFAVORITE");
                    b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }

            }
        }
            return rootView;

    }
}
