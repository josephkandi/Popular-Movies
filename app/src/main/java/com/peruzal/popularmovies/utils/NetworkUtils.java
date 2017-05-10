package com.peruzal.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.peruzal.popularmovies.BuildConfig;

import java.net.MalformedURLException;

/**
 * Created by joseph on 5/9/17.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    public static final String QUERY_PARAM_API_KEY = "api_key";
    public static final String QUERY_PARAM_PAGE = "page";
    public static final String QUERY_PARAM_LANGUAGE = "language";
    public static final String BASE_API_URL = "https://api.themoviedb.org/3";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String language = "en-US";
    private IMovieDownloadListener mListener;
    private static NetworkUtils mInstance;

    private NetworkUtils(IMovieDownloadListener listener) {
        this.mListener = listener;
    }

    public static NetworkUtils getInstance(IMovieDownloadListener listener){
        if (mInstance == null){
            mInstance = new NetworkUtils(listener);
        }
        return mInstance;
    }

    public void onGetResponseFromHttpUrl(final Context context, String url){
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.onErrorResponse(error);
            }
        });
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "*********Request url" + request.getUrl());
        }
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static String buildUrl(String path, String apikey, String page) throws MalformedURLException{
        return Uri.parse(BASE_API_URL).buildUpon()
                    .appendEncodedPath(path)
                    .appendQueryParameter(QUERY_PARAM_API_KEY, apikey)
                    .appendQueryParameter(QUERY_PARAM_PAGE, page)
                    .appendQueryParameter(QUERY_PARAM_LANGUAGE, language)
                    .build()
                    .toString();
    }

    public interface IMovieDownloadListener {
        void onResponse(String response);
        void onErrorResponse(VolleyError error);
    }
}
