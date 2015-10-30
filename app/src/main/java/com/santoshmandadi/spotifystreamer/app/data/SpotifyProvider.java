package com.santoshmandadi.spotifystreamer.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by santosh on 8/9/15.
 */
public class SpotifyProvider extends ContentProvider {
    static final int ARTISTS = 100;
    static final int TOP_TRACKS = 300;
    static final int TOP_TRACKS_WITH_ID = 301;
    static final int TRACK_WITH_ID = 302;
    private static final String LOG_TAG = SpotifyProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sTopResultsQueryBuilder;
    private static final String sArtistSelection =
            SpotifyContract.ArtistsEntry.TABLE_NAME +
                    "." + SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID + " = ? ";
    private static final String sTrackSelection =
            SpotifyContract.TopTracksEntry.TABLE_NAME +
                    "." + SpotifyContract.TopTracksEntry._ID + " = ? ";

    static {
        sTopResultsQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sTopResultsQueryBuilder.setTables(
                SpotifyContract.TopTracksEntry.TABLE_NAME + " INNER JOIN " +
                        SpotifyContract.ArtistsEntry.TABLE_NAME +
                        " ON " + SpotifyContract.TopTracksEntry.TABLE_NAME +
                        "." + SpotifyContract.TopTracksEntry.COLUMN_ARTIST_ID +
                        " = " + SpotifyContract.ArtistsEntry.TABLE_NAME +
                        "." + SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID);
    }

    private SpotifyDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        sURIMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_ARTISTS, ARTISTS);
        sURIMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_TOPTRACKS + "/*", TOP_TRACKS_WITH_ID);
        sURIMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_TOPTRACKS + "/*/#", TRACK_WITH_ID);
        sURIMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_TOPTRACKS, TOP_TRACKS);

        // 3) Return the new matcher!
        return sURIMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SpotifyDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case ARTISTS:
                cursor = mOpenHelper.getReadableDatabase().query(SpotifyContract.ArtistsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TOP_TRACKS:
                cursor = sTopResultsQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TOP_TRACKS_WITH_ID:
                String artistId = SpotifyContract.TopTracksEntry.getArtistIdFromTopTracksUri(uri);
                cursor = sTopResultsQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sArtistSelection, new String[]{artistId}, null, null, sortOrder);
                break;
            case TRACK_WITH_ID:
                String trackRowIdFromUri = SpotifyContract.TopTracksEntry.getArtistIdFromTrackUri(uri);
                Log.v(LOG_TAG, "Artist ID from URI: " + trackRowIdFromUri);
                cursor = sTopResultsQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sArtistSelection, new String[]{trackRowIdFromUri}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARTISTS:
                return SpotifyContract.ArtistsEntry.CONTENT_TYPE;
            case TOP_TRACKS:
                return SpotifyContract.TopTracksEntry.CONTENT_TYPE;
            case TOP_TRACKS_WITH_ID:
                return SpotifyContract.TopTracksEntry.CONTENT_TYPE;
            case TRACK_WITH_ID:
                return SpotifyContract.TopTracksEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ARTISTS: {
                long _id = db.insert(SpotifyContract.ArtistsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SpotifyContract.ArtistsEntry.buildArtistsUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert into row into " + uri);
                break;
            }

            case TOP_TRACKS: {
                long _id = db.insert(SpotifyContract.TopTracksEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SpotifyContract.TopTracksEntry.buildTopTracksUri(_id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case TOP_TRACKS: {
                rowsDeleted = db.delete(SpotifyContract.TopTracksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ARTISTS: {
                rowsDeleted = db.delete(SpotifyContract.ArtistsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numberOfRowsImpacted;

        switch (sUriMatcher.match(uri)) {
            case TOP_TRACKS: {
                numberOfRowsImpacted = db.update(SpotifyContract.TopTracksEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ARTISTS: {
                numberOfRowsImpacted = db.update(SpotifyContract.ArtistsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown uri: " + uri);
            }
        }
        if (numberOfRowsImpacted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return numberOfRowsImpacted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case TOP_TRACKS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyContract.TopTracksEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case ARTISTS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyContract.ArtistsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        //db.close();
        return returnCount;
    }


}
