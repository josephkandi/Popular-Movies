package com.peruzal.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.VolleyError;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;

public class MovieDiscoverActivity extends AppCompatActivity implements NetworkUtils.IMovieDownloadListener {
    private static final String TAG = MovieDiscoverActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        String apiKey = getString(R.string.api_key);
        String url = null;
        try {
             url = NetworkUtils.buildUrl("movie/popular", apiKey, "1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url == null) return;

        NetworkUtils.getInstance(this).onGetResponseFromHttpUrl(this,url);
    }

    @Override
    public void onResponse(String response) {
        if (response != null){
            Log.d(TAG, response);
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
}
