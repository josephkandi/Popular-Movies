package com.peruzal.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joseph on 5/9/17.
 */

public class Movie
{
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("adult")
    public boolean isAdult;
    public String overview;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("genre_ids")
    public List<Integer> genreIds;
    public int id;
    @SerializedName("original_title")
    public String originalTitle;
    @SerializedName("original_language")
    public String originalLanguage;
    public String title;
    @SerializedName("backdrop_path")
    public String backdropPath;
    public double popularity;
    @SerializedName("vote_count")
    public int voteCount;
    public boolean video;
    @SerializedName("vote_average")
    public double voteAverage;
}