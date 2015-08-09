package com.santoshmandadi.spotifystreamer.app.data;

import android.provider.BaseColumns;

/**
 * Created by santosh on 7/28/15.
 */
public class ArtistContract {

    public static final class TopTracksEntry implements BaseColumns{
        public static final String TABLE_NAME = "toptracks";

        public static final String COLUMN_ARTISTS_ID_KEY = "artists_id";

        public static final String COLUMN_ALBUM_NAME = "album_name";

        public static final String COLUMN_TRACK_NAME = "track_name";

        public static final String COLUMN_SMALL_ALBUM_IMAGE = "small_album_image";

        public static final String COLUMN_LARGE_ALBUM_IMAGE = "large_album_image";

        public static final String COLUMN_TRACK_PREVIEW_URL = "preview_url";

    }

    public static final class ArtistsEntry implements BaseColumns{
        public static final String TABLE_NAME = "artists";

        public static final String COLUMN_ARTIST_ID = "artist_id";

        public static final String COLUMN_ARTIST_NAME = "artist_name";

        public static final String COLUMN_ARTIST_IMAGE = "artist_image";


    }
}
