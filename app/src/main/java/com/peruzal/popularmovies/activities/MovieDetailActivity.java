package com.peruzal.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.utils.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ImageView mPosterImageView;
    private RatingBar mVote;
    private TextView mDateTextView;
    private TextView mPlotTextView;
    private TextView mTitleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mPosterImageView = (ImageView) findViewById(R.id.ivPoster);
        mVote = (RatingBar)findViewById(R.id.rbVote);
        mDateTextView = (TextView)findViewById(R.id.tvDate);
        mPlotTextView = (TextView)findViewById(R.id.tvPlot);
        mTitleTextView = (TextView)findViewById(R.id.tvTitle);


        Intent intent = getIntent();
        if (intent == null) return;
        if (!intent.hasExtra(Intent.EXTRA_TEXT)) return;
        Movie movie = new Gson().fromJson(intent.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(movie.title);
        }

        mTitleTextView.setText(movie.title);
        mPlotTextView.setText(movie.overview);
        mDateTextView.setText(movie.releaseDate);
        mVote.setRating((float) movie.voteAverage);

        String posterImageurl = NetworkUtils.buildPostImageUrl(this, movie.posterPath);
        Glide.with(this).load(posterImageurl).placeholder(R.drawable.placeholder).into(mPosterImageView);
        Log.d(TAG,movie.backdropPath);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
