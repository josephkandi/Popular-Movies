package com.peruzal.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by joseph on 2017/09/16.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.peruzal.popularmovies";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final  class MovieEntry implements BaseColumns {
        public static final String PATH_MOVIE = MovieEntry.TABLE_NAME;
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_IS_ADULT = "is_adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY =  "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE =  "vote_average";
    }
}
