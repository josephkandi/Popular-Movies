package com.peruzal.popularmovies.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.peruzal.popularmovies.R;
import com.peruzal.popularmovies.model.MovieApiResult;

import org.json.JSONObject;

public class MovieDiscoverActivity extends AppCompatActivity {
    public static final String BASE_API_URL = "https://api.themoviedb.org/3/movie";
    private static final String TAG = MovieDiscoverActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_discovery);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = buildUrl("popular", "1", this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                MovieApiResult apiResult = new Gson().fromJson(response.toString(),MovieApiResult.class);
                if (apiResult != null){
                    Log.d(TAG, "Total movies " + apiResult.movies.size());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getLocalizedMessage());
            }
        });

        requestQueue.add(request);
    }

    public static String buildUrl(String segment, String page, Context context){
        String apiKey = context.getString(R.string.api_key);
        return String.format("%s/%s?api_key=%s&page=%s", BASE_API_URL, segment, apiKey, page);
    }
}
