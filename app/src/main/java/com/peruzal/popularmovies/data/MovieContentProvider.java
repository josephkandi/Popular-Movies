package com.peruzal.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class MovieContentProvider extends ContentProvider {
    private SQLiteDatabase mDatabase;
    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.MovieEntry.PATH_MOVIE, MOVIES);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.MovieEntry.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        return matcher;
    }

    public MovieContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsAffected = 0;
        int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIES:
                rowsAffected = mDatabase.delete(MovieContract.MovieEntry.TABLE_NAME,null,null);
                break;
            case  MOVIE_WITH_ID:
                rowsAffected = mDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, selection,selectionArgs);
                break;
            default:
                new SQLiteException("Not valid delete statement for uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;
        int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIES:
                long id = mDatabase.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                }else{
                    throw new SQLiteException("Unable to insert row with uri " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Not supported");
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mDatabase = new MovieDbHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = mDatabase.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
