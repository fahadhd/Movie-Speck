package com.example.fahadhd.moviesearch;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;
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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewMoviesFragment extends Fragment {
    GridView movieGrid;
    static int width;
    boolean sortByPop;
    static String API_KEY = "3d265a7f8684cf8cb974b04326f6b5fa";
    public ViewMoviesFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Most Popular Movies");

        if(isNetworkAvailable()){
            //Loading images from internet must be done on a backRound thread and not
            //on the main thread to speed things up.
            movieGrid.setVisibility(movieGrid.VISIBLE);
            new ImageLoadTask().execute();
        }
        else{
            TextView textView = new TextView(getActivity());
            FrameLayout layout = (FrameLayout)getActivity().findViewById(R.id.framelayout);
            textView.setText("No internet connection");
            Toast.makeText(getContext(), "No Internet Connection Available",
                    Toast.LENGTH_SHORT ).show();

            if(layout.getChildCount() == 1) layout.addView(textView);

            movieGrid.setVisibility(movieGrid.GONE);
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
            width = size.x/6;
        }
        else{
            width = size.x/3;
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
                System.out.println(position);
            }
        });

        return rootView;
    }
    public class ImageLoadTask extends AsyncTask<Void,Void,ArrayList<String>>{
        ArrayList<String> posters;
        public ImageLoadTask() {
            super();
            posters = new ArrayList<String>();
        }
        @Override
        //Following is the Strings of the poster paths gotten from the movie database.
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
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

        public String[] getPathsFromAPI(boolean popSort){
            while(true){
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try{
                    String urlString = "http://api.themoviedb.org/3/discover/movie?";
                    //Gets most popular movies currently.
                    if(popSort){
                        urlString+="sort_by=popularity.desc&api_key="+API_KEY;
                    }
                    //Gets highest rated movies instead of most popular
                    else{
                        urlString+="sort_by=vote_average.desc&vote_count.gte=1000&api_key="+API_KEY;

                    }
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
                        return getPathsFromJSON(JSONResult);
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
        public String[] getPathsFromJSON(String JSONResult) throws JSONException{
            return null;
        }


    }

}
