package com.santoshmandadi.spotifystreamer.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by santosh on 7/28/15.
 */
public class SpotifyDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "spotify.db";

    public SpotifyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + SpotifyContract.ArtistsEntry.TABLE_NAME + "(" +
                SpotifyContract.ArtistsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpotifyContract.ArtistsEntry.COLUMN_ARTIST_ID + " INTEGER UNIQUE NOT NULL , " +
                SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                SpotifyContract.ArtistsEntry.COLUMN_ARTIST_IMAGE + " TEXT);";

        final String SQL_CREATE_TOPTRACKS_TABLE = "CREATE TABLE " + SpotifyContract.TopTracksEntry.TABLE_NAME + "(" +
                SpotifyContract.TopTracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpotifyContract.TopTracksEntry.COLUMN_ARTISTS_ID_KEY + " INTEGER NOT NULL, " +
                SpotifyContract.TopTracksEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                SpotifyContract.TopTracksEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                SpotifyContract.TopTracksEntry.COLUMN_SMALL_ALBUM_IMAGE + " TEXT, " +
                SpotifyContract.TopTracksEntry.COLUMN_LARGE_ALBUM_IMAGE + " TEXT, " +
                SpotifyContract.TopTracksEntry.COLUMN_TRACK_PREVIEW_URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + SpotifyContract.TopTracksEntry.COLUMN_ARTISTS_ID_KEY + ") REFERENCES " +
                SpotifyContract.ArtistsEntry.TABLE_NAME + " (" + SpotifyContract.ArtistsEntry._ID + "));";

        db.execSQL(SQL_CREATE_ARTISTS_TABLE);
        db.execSQL(SQL_CREATE_TOPTRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.ArtistsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.TopTracksEntry.TABLE_NAME);
        onCreate(db);
    }
}
