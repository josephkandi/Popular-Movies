package com.peruzal.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joseph on 2017/09/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final  int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";
    private static final String CREATE_TABLE_STATEMENT =
            String.format("CREATE TABLE %s(" +                      // TABLE_NAME
                            "%s INTEGER PRIMARY KEY NOT NULL," +    // _ID
                            "%s TEXT," +                            // COLUMN_BACKDROP_PATH
                            "%s TEXT," +                            // COLUMN_GENRE_IDS
                            "%s BOOLEAN," +                         // COLUMN_IS_ADULT
                            "%s TEXT," +                            // COLUMN_ORIGINAL_LANGUAGE
                            "%s TEXT," +                            // COLUMN_ORIGINAL_TITLE
                            "%s TEXT," +                            // COLUMN_OVERVIEW
                            "%s INTEGER," +                         // COLUMN_POPULARITY
                            "%s TEXT," +                            // COLUMN_POSTER_PATH
                            "%s INTEGER," +                         // COLUMN_RELEASE_DATE
                            "%s TEXT," +                            // COLUMN_TITLE
                            "%s TEXT," +                            // COLUMN_VIDEO
                            "%s REAL," +                            // COLUMN_VOTE_AVERAGE
                            "%s INTEGER)",                          // COLUMN_VOTE_COUNT
            MovieContract.MovieEntry.TABLE_NAME,
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_GENRE_IDS,
            MovieContract.MovieEntry.COLUMN_IS_ADULT,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VIDEO,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
    private static final String TAG = MovieDbHelper.class.getSimpleName();

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, CREATE_TABLE_STATEMENT);
        try {
            db.execSQL(CREATE_TABLE_STATEMENT);
        }catch (SQLiteException ex){
            if (ex != null){
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
