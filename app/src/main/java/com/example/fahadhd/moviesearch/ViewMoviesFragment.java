package com.example.fahadhd.moviesearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.PreferenceChangeListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewMoviesFragment extends Fragment {
    GridView movieGrid;
    static int width;
    final String LOG_TAG = ViewMoviesFragment.class.getSimpleName();
    static String API_KEY = "3d265a7f8684cf8cb974b04326f6b5fa";
    static  ArrayList<String> posters = new ArrayList<String>();
    static ArrayList<String> overviews;
    static ArrayList<String> titles;
    static ArrayList<String> dates;
    static ArrayList<String> ratings;
    static ArrayList<String> vidLinks1;
    static ArrayList<String> vidLinks2;
    static ArrayList<String> ids;
    static ArrayList<Boolean> favorites;
    static ArrayList<ArrayList<String>> comments;

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            movieGrid.setAdapter(null);
            onStart();
        }
    }
    public ViewMoviesFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }
    public void updateMovies(){
        SharedPreferences sharedPref = PreferenceManager.
                getDefaultSharedPreferences(getActivity());
        String movieType = sharedPref.getString(getString(R.string.pref_movie_key),
                getString(R.string.pref_popularity));
        if(movieType.equals(getString(R.string.pref_popularity))){
            getActivity().setTitle("Most Popular Movies");
        }
        else if(movieType.equals(getString(R.string.pref_rating))){
            getActivity().setTitle("Highest Rated");
        }
        else if(movieType.equals(getString(R.string.pref_favorites))){
            getActivity().setTitle("Favorites");
            TextView textView = new TextView(getActivity());
            FrameLayout layout = (FrameLayout)getActivity().findViewById(R.id.fragment);
            textView.setText("You have no favorited movies currently");
            layout.addView(textView);
            movieGrid.setVisibility(movieGrid.GONE);
            return;
        }
        else{
            Log.d(LOG_TAG, "Unit type not found: " + movieType);
        }

        if(isNetworkAvailable()){
            //Loading images from internet must be done on a backRound thread and not
            //on the main thread to speed things up.
            movieGrid.setVisibility(movieGrid.VISIBLE);
            new ImageLoadTask().execute(movieType);
        }
        else{
            //TODO: Set up a screen in case user has no internet
            Toast.makeText(getContext(), "No Internet Connection Available",
                    Toast.LENGTH_SHORT ).show();
        }
    }
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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

        //If device is a tablet show 6 items else show 3
        if(MainActivity.TABLET){
            width = size.x/4;
        }
        else{
            width = size.x/2;
        }
        ArrayList<String> paths = new ArrayList<String>();
        ImageAdapter adapter = new ImageAdapter(getActivity(),paths,width);
        movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
        movieGrid.setColumnWidth(width);
        movieGrid.setAdapter(adapter);

        //Listen for a user click on a movie poster
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),MovieDetails.class).
                        putExtra("overview",overviews.get(position)).
                        putExtra("poster", posters.get(position)).
                        putExtra("title",titles.get(position)).
                        putExtra("dates",dates.get(position)).
                        putExtra("rating",ratings.get(position)).
                        putExtra("youtube",vidLinks1.get(position)).
                        putExtra("youtube2",vidLinks2.get(position)).
                        putExtra("comments",comments.get(position)).
                        putExtra("favorite",favorites.get(position));
                startActivity(intent);
            }
        });

        return rootView;
    }
    public class ImageLoadTask extends AsyncTask<String,Void,ArrayList<String>>{
        final String LOG_TAG = ImageLoadTask.class.getSimpleName();
        public ImageLoadTask() {
            super();
        }
        @Override
        //Following is the Strings of the poster paths gotten from the movie database.
        protected ArrayList<String> doInBackground(String... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(params[0])));
                    return posters;
                }
                catch (Exception e){
                    continue;
                }
            }
        }

        @Override
        //Remember onPostExectute fills the adapter with data that was gathered during execution.
        protected void onPostExecute(ArrayList<String> result) {
            if(result != null && getContext() != null){
                ImageAdapter imageAdapter = new ImageAdapter(getContext(),result,width);
                movieGrid.setAdapter(imageAdapter);
            }
        }

        public String[] getPathsFromAPI(String movieType){
                    String urlString = "http://api.themoviedb.org/3/discover/movie?";
                    //Gets most popular movies currently.
                    if(movieType.equals(getString(R.string.pref_popularity))){
                        urlString+="sort_by=popularity.desc&api_key="+API_KEY;
                    }
                    //Gets highest rated movies instead of most popular
                    else if(movieType.equals(getString(R.string.pref_rating))){
                        urlString+="sort_by=vote_average.desc&vote_count.gte=1000&api_key="+API_KEY;
                    }
                    else{
                        Log.d(LOG_TAG, "Unit type not found: " + movieType);
                    }

                    JSONObject JSONResult = getJSONFromInternet(urlString);
                    try{
                        overviews = new ArrayList<String>(Arrays.asList(parseJSON(JSONResult,"overview")));
                        titles = new ArrayList<String>(Arrays.asList(parseJSON(JSONResult,"original_title")));
                        ratings = new ArrayList<String>(Arrays.asList(parseJSON(JSONResult,"vote_average")));
                        dates = new ArrayList<String>(Arrays.asList(parseJSON(JSONResult,"release_date")));
                        ids = new ArrayList<String>(Arrays.asList(parseJSON(JSONResult,"id")));
                        getYoutubeLinks(ids);

                    }
                    catch (Exception e){
                        Log.d(LOG_TAG,"Exception "+e);
                    }
            try {
                return parseJSON(JSONResult, "poster_path");
            }
            catch (JSONException e){
                Log.d(LOG_TAG,"JSON Exception occurred "+e);
            }
            return null;
        }

        public String[] parseJSON(JSONObject root, String info) throws JSONException{
            String[] result;
            JSONArray movies = root.getJSONArray("results");
            result = new String[movies.length()];

            for(int i = 0; i < result.length; i++){
                JSONObject movie = movies.getJSONObject(i);
                if(info.equals("vote_average")){
                    Double rating = movie.getDouble(info);
                    result[i] = rating.toString()+"/10";
                }
                else {
                    result[i] = movie.getString(info);
                }
            }
            return  result;
        }
    }
    public void getYoutubeLinks(ArrayList<String> ids) throws JSONException{
        String urlString;
        String link = "https://www.youtube.com/watch?v=";
            for(int i = 0; i < ids.size(); i++){
                urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) +
                        "/videos?api_key=" + API_KEY;
                JSONArray results = getJSONFromInternet(urlString).
                        getJSONArray("results");
                vidLinks1.add(link+results.getJSONObject(0).getString("key"));
                vidLinks2.add(link+results.getJSONObject(1).getString("key"));
            }
    }
    //Returnsa jsonObject when connected to the given url.
    public JSONObject getJSONFromInternet(String urlString){
        while(true){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONResult;

            try{
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine())!= null){
                    buffer.append(line+"\n");
                }
                if(buffer.length() == 0){
                    return null;
                }
                JSONResult = buffer.toString();
                try{
                    return new JSONObject(JSONResult);
                }
                catch (JSONException e){
                    return null;
                }
            }
            catch (Exception e){
                continue;
            }
            finally {
                urlConnection.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
