package com.peruzal.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joseph on 5/9/17.
 */

public class MovieApiResult
{
    public int page;
    @SerializedName("results")
    public List<Movie> movies;
    @SerializedName("total_results")
    public int totalMovies;
    @SerializedName("total_pages")
    public int totalPages;
}