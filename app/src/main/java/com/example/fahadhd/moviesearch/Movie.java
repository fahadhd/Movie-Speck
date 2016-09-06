package com.example.fahadhd.moviesearch;


import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable{
    public String posterURL, overview, movieTitle, releaseDate,youtubeLink, ID;
    Double rating;
    public ArrayList<String> comments;

    public Movie(String posterURL, String overview, String movieTitle, String releaseDate, Double rating, String ID,
                 String youtubeLink){
        this.posterURL = posterURL;
        this.overview = overview;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.ID = ID;
        this.youtubeLink = youtubeLink;
        comments = new ArrayList<>();
    }

    public String getOverview(){
        return this.overview;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getID() {
        return ID;
    }

    public Double getRating() {
        return rating;
    }

}
