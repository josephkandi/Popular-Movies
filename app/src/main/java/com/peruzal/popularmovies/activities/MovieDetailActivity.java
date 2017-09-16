package com.peruzal.popularmovies.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.data.MovieContract;
import com.peruzal.popularmovies.data.MovieDbHelper;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ImageView mPosterImageView;
    private RatingBar mVote;
    private TextView mDateTextView;
    private TextView mPlotTextView;
    private TextView mVoteTextView;
    private ImageView mFavoriteImageView;
    private Movie mMovie;
    private boolean isFavorite = false;
    private SQLiteDatabase db;


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

        db = new MovieDbHelper(this).getWritableDatabase();

        Intent intent = getIntent();
        if (intent == null) return;
        if (!intent.hasExtra(Intent.EXTRA_TEXT)) return;
        mMovie = new Gson().fromJson(intent.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
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
        Log.d(TAG,mMovie.backdropPath);
    }

    private void setupFavoriteMovie() {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null, MovieContract.MovieEntry._ID +" = ?", new String[]{String.valueOf(mMovie.id)}, null );
        if (cursor == null)
            return;

        if (cursor.moveToFirst()){
            isFavorite = true;
            toggleFavoriteImageView();
        }else{
            isFavorite = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

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
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry._ID, mMovie.id);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,values);
        long id = ContentUris.parseId(uri);
        if (id != -1){
            Log.d(TAG, "Inserted movie with id " + id);
        }else{
            Log.d(TAG, "Movie not inserted, id " + id);
        }
    }

    private void removeMovieFromDb() {
        long id = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry._ID + " = ?",new String[]{ String.valueOf(mMovie.id)});
        if (id != -1){
            Log.d(TAG, "Removed movie from db");
        }
    }
}
