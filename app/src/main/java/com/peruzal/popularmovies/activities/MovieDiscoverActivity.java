package com.peruzal.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.adapters.MovieAdapter;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.model.MovieApiResult;
import com.peruzal.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.util.List;

public class MovieDiscoverActivity extends AppCompatActivity implements NetworkUtils.IMovieDownloadListener, MovieAdapter.IMovieClickListener {
    private static final String TAG = MovieDiscoverActivity.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private List<Movie> mMovies;
    private ContentLoadingProgressBar mContentLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvMovies);
        mContentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.clProgressBar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        String apiKey = getString(R.string.api_key);
        String url = null;
        try {
             url = NetworkUtils.buildUrl(getString(R.string.popular_path), apiKey, "1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url == null) return;
        showLoading();
        NetworkUtils.getInstance(this).onGetResponseFromHttpUrl(this,url);
    }

    @Override
    public void onResponse(String response) {
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
            showError(getString(R.string.generic_error));
            Log.e(TAG, error.toString());
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

    private void showError(String error){
        mContentLoadingProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        String genericErrorMessage = getString(R.string.generic_error);
        if (error == null){
            error = genericErrorMessage;
        }
        Toast.makeText(this, error ,Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_discovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String path = null;
        String title = null;

        switch (id){
            case R.id.action_popular:
                path = getString(R.string.popular_path);
                title = getString(R.string.item_popular);
                break;
            case R.id.action_top_rated:
                path = getString(R.string.top_rated_path);
                title = getString(R.string.item_top_rated);
                break;
        }

        String apiKey = getString(R.string.api_key);
        String url = null;
        try {
            url = NetworkUtils.buildUrl(path, apiKey, "1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        showLoading();
        mMovieAdapter.setMovieData(null);
        NetworkUtils.getInstance(this).onGetResponseFromHttpUrl(this, url);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        if (mMovies == null) return;
        Movie movie = mMovies.get(position);
        Intent startDetailActivity = new Intent(this, MovieDetailActivity.class);
        String movieJson = new Gson().toJson(movie);
        startDetailActivity.putExtra(Intent.EXTRA_TEXT, movieJson);
        startActivity(startDetailActivity);
    }
}
