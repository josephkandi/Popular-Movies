package com.peruzal.popularmovies.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.data.MovieContract;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ImageView mPosterImageView;
    private RatingBar mVote;
    private TextView mDateTextView;
    private TextView mPlotTextView;
    private TextView mVoteTextView;
    private ImageView mFavoriteImageView;
    private Movie mMovie;
    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mPosterImageView = (ImageView) findViewById(R.id.ivPoster);
        mVote = (RatingBar)findViewById(R.id.rbVote);
        mDateTextView = (TextView)findViewById(R.id.tvDate);
        mPlotTextView = (TextView)findViewById(R.id.tvPlot);
        mVoteTextView = (TextView)findViewById(R.id.tvVote);
        mFavoriteImageView = (ImageView)findViewById(R.id.img_favorite);

        Log.d(TAG, "onCreate");

        Intent intent = getIntent();
        if (intent == null) return;
        if (!intent.hasExtra("DATA")) return;
        mMovie = new Gson().fromJson(intent.getStringExtra("DATA"), Movie.class);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mMovie.title);
        }

        setupFavoriteMovie();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPosterImageView.setTransitionName(mMovie.posterPath);
        }
        mPlotTextView.setText(mMovie.overview);
        mDateTextView.setText(mMovie.releaseDate);
        mVote.setRating((float) mMovie.voteAverage);
        mVoteTextView.setText(Double.toString(mMovie.voteAverage));
        mFavoriteImageView.setOnClickListener(this);




        toggleFavoriteImageView();

        String posterImageurl = NetworkUtils.buildPostImageUrl(this, mMovie.posterPath);

        Picasso.with(this).load(posterImageurl).placeholder(R.drawable.placeholder).into(mPosterImageView, new Callback() {
            @Override
            public void onSuccess() {
                supportStartPostponedEnterTransition();
            }

            @Override
            public void onError() {
                supportStartPostponedEnterTransition();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String reviewsUrl = NetworkUtils.buildReviewsUrl(this,String.valueOf(mMovie.id),"1");
        String videosurl = NetworkUtils.buildVideosUrl(this,String.valueOf(mMovie.id));

        Log.d(TAG, reviewsUrl);
        Log.d(TAG, videosurl);

        NetworkUtils.getInstance(new NetworkUtils.IMovieDownloadListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "********* Reviews " + response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error " + error.getLocalizedMessage());

            }
        }).onGetResponseFromHttpUrl(this, reviewsUrl);



        NetworkUtils.getInstance(new NetworkUtils.IMovieDownloadListener() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "********* Videos " + response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error " + error.getLocalizedMessage());
            }
        }).onGetResponseFromHttpUrl(this, videosurl);
    }

    private void setupFavoriteMovie() {
        AsyncTask<String,Void,Cursor> cursorTask = new AsyncTask<String, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(String... params) {
                return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null, MovieContract.MovieEntry._ID +" = ?", new String[]{String.valueOf(params[0])}, null );
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                if (cursor == null)
                    return;

                if (cursor.moveToFirst()){
                    isFavorite = true;
                    toggleFavoriteImageView();
                }else{
                    isFavorite = false;
                }
            }
        };

        cursorTask.execute(String.valueOf(mMovie.id));

    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_favorite){
            inserOrRemoveFavoriteMovie();
            toggleFavoriteImageView();
        }
    }

    private void inserOrRemoveFavoriteMovie() {
        if (isFavorite){
            removeMovieFromDb();
        }else{
            insertMovieIntoDb();
        }
        isFavorite = !isFavorite;
    }

    private void toggleFavoriteImageView() {
        if (isFavorite){
            mFavoriteImageView.setImageResource(R.drawable.ic_favorite_white_24dp);
        }else{
            mFavoriteImageView.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    private void insertMovieIntoDb() {
        AsyncTask<ContentValues,Void,Long> insertTask = new AsyncTask<ContentValues, Void, Long>() {
            @Override
            protected Long doInBackground(ContentValues... params) {
                Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,params[0]);
                return ContentUris.parseId(uri);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);

                if (id != -1){
                    Log.d(TAG, "Inserted movie with id " + id);
                }else{
                    Log.d(TAG, "Movie not inserted, id " + id);
                }
            }
        };

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry._ID, mMovie.id);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_IS_ADULT, mMovie.isAdult);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.overview);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.originalTitle);
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mMovie.originalLanguage);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.title);
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, mMovie.backdropPath);
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, mMovie.popularity);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, mMovie.voteCount);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.voteAverage);
        values.put(MovieContract.MovieEntry.COLUMN_VIDEO, mMovie.video);


        StringBuilder genreIdBuilder = new StringBuilder(mMovie.genreIds.size());
        for (int i = 0; i < mMovie.genreIds.size(); i++){
            genreIdBuilder.append(mMovie.genreIds.get(i));
            if (i != mMovie.genreIds.size() - 1){
                genreIdBuilder.append(",");
            }
        }

       String genres = genreIdBuilder.toString();

        Log.d(TAG, "Genre IDs " + genreIdBuilder.toString());

        values.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, genreIdBuilder.toString());

        insertTask.execute(values);
    }

    private void removeMovieFromDb() {
        AsyncTask<String,Void,Integer> deleteTask = new AsyncTask<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                return getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry._ID + " = ?",new String[]{ String.valueOf(mMovie.id)});
            }

            @Override
            protected void onPostExecute(Integer rows) {
                super.onPostExecute(rows);

                if (rows != -1){
                    Log.d(TAG, "Removed movie from db");
                }
            }
        };

        deleteTask.execute(String.valueOf(mMovie.id));
    }

}
