package com.peruzal.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.model.Movie;
import com.peruzal.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ImageView posterImageView = (ImageView)findViewById(R.id.img_poster);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("DATA")){
                String movieJson = intent.getStringExtra("DATA");
                Movie movie = new Gson().fromJson(movieJson, Movie.class);
                String imageUrl = NetworkUtils.buildPostImageUrl(this,movie.posterPath);
                Picasso.with(this).load(imageUrl).into(posterImageView);
                Log.d(TAG, movieJson);
            }
        }
    }
}
