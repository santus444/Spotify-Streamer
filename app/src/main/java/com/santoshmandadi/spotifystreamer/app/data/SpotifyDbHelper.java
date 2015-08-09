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
        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + ArtistContract.ArtistsEntry.TABLE_NAME + "(" +
                ArtistContract.ArtistsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArtistContract.ArtistsEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                ArtistContract.ArtistsEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                ArtistContract.ArtistsEntry.COLUMN_ARTIST_IMAGE + " TEXT);";

        final String SQL_CREATE_TOPTRACKS_TABLE = "CREATE TABLE " + ArtistContract.TopTracksEntry.TABLE_NAME + "(" +
                ArtistContract.TopTracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArtistContract.TopTracksEntry.COLUMN_ARTISTS_ID_KEY + " INTEGER NOT NULL, " +
                ArtistContract.TopTracksEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                ArtistContract.TopTracksEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                ArtistContract.TopTracksEntry.COLUMN_SMALL_ALBUM_IMAGE + " TEXT, " +
                ArtistContract.TopTracksEntry.COLUMN_LARGE_ALBUM_IMAGE + " TEXT, " +
                ArtistContract.TopTracksEntry.COLUMN_TRACK_PREVIEW_URL + " TEXT, " +
                " FOREIGN KEY (" + ArtistContract.TopTracksEntry.COLUMN_ARTISTS_ID_KEY + ") REFERENCES " +
                ArtistContract.ArtistsEntry.TABLE_NAME + " (" + ArtistContract.ArtistsEntry._ID + "));";

        db.execSQL(SQL_CREATE_ARTISTS_TABLE);
        db.execSQL(SQL_CREATE_TOPTRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArtistContract.ArtistsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArtistContract.TopTracksEntry.TABLE_NAME);
        onCreate(db);
    }
}
