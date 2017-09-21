package com.peruzal.popularmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import com.peruzal.popularmovies.data.MovieContract;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.model.MovieApiResult;
import com.peruzal.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDiscoverActivity extends AppCompatActivity implements  MovieAdapter.IMovieClickListener, LoaderManager.LoaderCallbacks<Cursor>,NetworkUtils.IMovieDownloadListener {
    private static final String TAG = MovieDiscoverActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 100;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private List<Movie> mMovies = new ArrayList<>();
    private ContentLoadingProgressBar mContentLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        Log.d(TAG, "onCreate");

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvMovies);
        mContentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.clProgressBar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
        Toast toast = Toast.makeText(this, error ,Toast.LENGTH_LONG);
        toast.cancel();
        toast.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_discovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String path = null;
        String title = null;
        String apiKey = getString(R.string.api_key);
        String url = null;

        switch (id){
            case R.id.action_popular:
                path = getString(R.string.popular_path);
                title = getString(R.string.item_popular);
                try {
                    url = NetworkUtils.buildUrl(path, apiKey, "1");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                showLoading();
                mMovieAdapter.setMovieData(null);
                NetworkUtils.getInstance(this).onGetResponseFromHttpUrl(this, url);
                break;
            case R.id.action_top_rated:
                path = getString(R.string.top_rated_path);
                title = getString(R.string.item_top_rated);
                try {
                    url = NetworkUtils.buildUrl(path, apiKey, "1");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                showLoading();
                mMovieAdapter.setMovieData(null);
                NetworkUtils.getInstance(this).onGetResponseFromHttpUrl(this, url);
                break;
            case R.id.action_favorite:
                    getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,null,this);
                break;
        }



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

        startDetailActivity.putExtra("DATA", movieJson);
        startActivity(startDetailActivity);

        Log.d(TAG, movieJson);

        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("DATA", movieJson);
        //startActivity(intent);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mMovieData = null;
            @Override
            protected void onStartLoading() {

                if (mMovieData != null){
                    deliverResult(mMovieData);
                }else{
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                } catch (Exception ex){
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null){
            return;
        }

        mMovies.clear();
        mMovieAdapter.setMovieData(null);

        if (cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                boolean isAdult = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_ADULT)) == 0 ? false : true;
                String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                String originalTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
                String originalLanguage = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE));
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String backdropPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
                double popularity = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
                int voteCount = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
                double voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                boolean video = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VIDEO)) == 0 ? false : true;
                String genreIds = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_GENRE_IDS));
                String[] genreIdsArray =  genreIds.split(",");

                Movie movie = new Movie();
                movie.id = id;
                movie.posterPath = posterPath;
                movie.isAdult = isAdult;
                movie.overview = overview;
                movie.releaseDate = releaseDate;
                movie.originalTitle = originalTitle;
                movie.originalLanguage = originalLanguage;
                movie.title = title;
                movie.backdropPath = backdropPath;
                movie.popularity = popularity;
                movie.voteCount = voteCount;
                movie.voteAverage = voteAverage;
                movie.video = video;


                movie.genreIds = new ArrayList<>();
                for (String genre : genreIdsArray){
                    movie.genreIds.add(Integer.parseInt(genre));
                }


                mMovies.add(movie);
            }while (cursor.moveToNext());

            mMovieAdapter.setMovieData(mMovies);
            cursor.close();
        }else{
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.setMovieData(null);
    }

    @Override
    public void onResponse(String response) {
        if (response != null){
            showData();
            MovieApiResult apiResult = new Gson().fromJson(response, MovieApiResult.class);
            mMovies.clear();
            mMovies.addAll(apiResult.movies);
            mMovieAdapter.setMovieData(mMovies);
            Log.d(TAG, "onResponse total movies " + mMovies.size());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null){
            showError(getString(R.string.generic_error));
            Log.e(TAG, error.toString());
        }
    }
}
