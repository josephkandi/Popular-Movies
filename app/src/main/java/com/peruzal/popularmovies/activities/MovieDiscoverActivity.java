package com.peruzal.popularmovies.activities;

import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.adapters.MovieAdapter;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.model.MovieApiResult;
import com.peruzal.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.util.List;

public class MovieDiscoverActivity extends AppCompatActivity implements NetworkUtils.IMovieDownloadListener {
    private static final String TAG = MovieDiscoverActivity.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private List<Movie> mMovies;
    private ContentLoadingProgressBar mContentLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvMovies);
        mContentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.clProgressBar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);

        String apiKey = getString(R.string.api_key);
        String url = null;
        try {
             url = NetworkUtils.buildUrl("movie/popular", apiKey, "1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url == null) return;
        showLoading();
        NetworkUtils.getInstance(this).onGetResponseFromHttpUrl(this,url);
    }

    @Override
    public void onResponse(String response) {
        //TODO Dismiss loading indicator
        if (response != null){
            showData();
            MovieApiResult apiResult = new Gson().fromJson(response, MovieApiResult.class);
            mMovies = apiResult.movies;
            mMovieAdapter.setMovieData(mMovies);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null){
            Log.d(TAG, error.toString());
        }else{
            Log.d(TAG, getString(R.string.generic_error));
        }
    }

    private void showData(){
        mContentLoadingProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading(){
        mContentLoadingProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }


    //TODO Create settings
    //TODO Change filter on settings
}
