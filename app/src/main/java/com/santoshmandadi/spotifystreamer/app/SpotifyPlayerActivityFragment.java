package com.santoshmandadi.spotifystreamer.app;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.santoshmandadi.spotifystreamer.app.data.SpotifyContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyPlayerActivityFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, LoaderManager.LoaderCallbacks<Cursor>, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    // These indices are tied to TOP_TRACKS_COLUMNS.  If TOP_TRACKS_COLUMNS changes, these
    // must change.
    public static final int COL_TOP_TRACK_ID = 0;
    public static final int COL_ARTIST_ID = 1;
    public static final int COL_ALBUM_NAME = 2;
    public static final int COL_TRACK_NAME = 3;
    public static final int COL_TRACK_PREVIEW_URL = 4;
    public static final int COL_SMALL_ALBUM_IMAGE = 5;
    public static final int COL_LARGE_ALBUM_IMAGE = 6;
    public static final int COL_ARTIST_NAME = 7;
    static final String TRACK_URI = "URI";
    private static final String[] TOP_TRACKS_COLUMNS = {
            SpotifyContract.TopTracksEntry.TABLE_NAME + "." + SpotifyContract.TopTracksEntry._ID,
            SpotifyContract.TopTracksEntry.TABLE_NAME + "." + SpotifyContract.TopTracksEntry.COLUMN_ARTIST_ID,
            SpotifyContract.TopTracksEntry.COLUMN_ALBUM_NAME,
            SpotifyContract.TopTracksEntry.COLUMN_TRACK_NAME,
            SpotifyContract.TopTracksEntry.COLUMN_TRACK_PREVIEW_URL,
            SpotifyContract.TopTracksEntry.COLUMN_SMALL_ALBUM_IMAGE,
            SpotifyContract.TopTracksEntry.COLUMN_LARGE_ALBUM_IMAGE,
            SpotifyContract.ArtistsEntry.TABLE_NAME + "." + SpotifyContract.ArtistsEntry.COLUMN_ARTIST_NAME
    };
    @InjectView(R.id.text_artist_name)
    TextView artistName;
    @InjectView(R.id.text_album_name)
    TextView albumName;
    @InjectView(R.id.text_track_name)
    TextView trackName;
    @InjectView(R.id.image_album)
    ImageView albumImageView;
    @InjectView(R.id.button_play)
    ImageButton playButton;
    @InjectView(R.id.button_previous)
    ImageButton previousButton;
    @InjectView(R.id.button_next)
    ImageButton nextButton;
    @InjectView(R.id.text_max_duration)
    TextView maxDuration;
    @InjectView(R.id.seekbar_status)
    SeekBar statusSeekBar;
    @InjectView(R.id.text_current_duration)
    TextView currentDurationTextView;
    MediaPlayer mediaPlayer;
    Handler myHandler = new Handler();
    boolean play = true;
    int songIndex;
    int seekTo = 0;
    ProgressDialog progressDialog;
    int finalDuration, startTime;
    Cursor mCursor;
    Uri mUri;
    String LOG_TAG = SpotifyPlayerActivityFragment.class.getSimpleName();
    private int PLAYER_LOADER = 2;
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mediaPlayer != null) {
                startTime = mediaPlayer.getCurrentPosition();
                currentDurationTextView.setText(String.format("%d:%02d",

                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) startTime)))
                );
                statusSeekBar.setProgress(Util.getProgressPercentage(startTime, finalDuration));
                myHandler.postDelayed(this, 100);
            }
        }
    };

    public SpotifyPlayerActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(SpotifyPlayerActivityFragment.TRACK_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_player, null);//        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        ButterKnife.inject(this, rootView);
        artistName.setText(getActivity().getIntent().getDataString());
        if (savedInstanceState != null) {
            songIndex = savedInstanceState.getInt("songIndex");
            play = savedInstanceState.getBoolean("isPlaying");
            seekTo = savedInstanceState.getInt("position");
        }
        statusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("Seeking: ", String.valueOf(progress));

                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = Util.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMedia();

            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.moveToPrevious()) {
                    seekTo = 0;
                    if (mediaPlayer != null)
                        mediaPlayer.stop();
                    mediaPlayer = null;
                    updateUI();
                    songIndex--;
                    playMedia();
                } else {
                    mCursor.moveToFirst();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.moveToNext()) {
                    seekTo = 0;
                    if (mediaPlayer != null)
                        mediaPlayer.stop();
                    updateUI();
                    songIndex++;
                    playMedia();

                } else {
                    mCursor.moveToLast();
                }
                mediaPlayer = null;
            }
        });

        return rootView;
    }

    private void playMedia() {
        if (play) {
            playButton.setImageResource(android.R.drawable.ic_media_pause);
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnPreparedListener(SpotifyPlayerActivityFragment.this);
                    mediaPlayer.setDataSource(mCursor.getString(COL_TRACK_PREVIEW_URL));
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.setOnSeekCompleteListener(this);
                    mediaPlayer.setOnErrorListener(this);
                    mediaPlayer.prepareAsync();
                    statusSeekBar.setProgress(0);
                    statusSeekBar.setMax(100);
                    progressDialog = ProgressDialog.show(getActivity(), "", "");
                    progressDialog.setCancelable(true);
                } else {
                    mediaPlayer.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            play = false;
        } else {
            playButton.setImageResource(android.R.drawable.ic_media_play);
            if (mediaPlayer != null)
                mediaPlayer.pause();
            play = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PLAYER_LOADER, null, this);
    }

    private void updateUI() {
        trackName.setText(mCursor.getString(COL_TRACK_NAME));
        artistName.setText(mCursor.getString(COL_ARTIST_NAME));
        albumName.setText(mCursor.getString(COL_ALBUM_NAME));
        trackName.setText(mCursor.getString(COL_TRACK_NAME));
        maxDuration.setText("0:00");
        currentDurationTextView.setText("0:00");
        statusSeekBar.setProgress(0);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                playButton.setImageResource(android.R.drawable.ic_media_pause);
                play = false;
            } else {
                playButton.setImageResource(android.R.drawable.ic_media_play);
                play = true;
            }
        }
        if (!mCursor.getString(COL_LARGE_ALBUM_IMAGE).equalsIgnoreCase(""))
            Picasso.with(getActivity()).load(mCursor.getString(COL_LARGE_ALBUM_IMAGE)).into(albumImageView);
        else
            albumImageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        progressDialog.dismiss();
        if (seekTo != 0)
            mp.seekTo(seekTo);
        finalDuration = mp.getDuration();
        int startTime = mp.getCurrentPosition();

        maxDuration.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalDuration),
                TimeUnit.MILLISECONDS.toSeconds((long) finalDuration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalDuration))));
        updateProgressBar();
        mp.start();

    }

    private void updateProgressBar() {
        myHandler.postDelayed(UpdateSongTime, 100);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("songIndex", songIndex);

        outState.putInt("position", mediaPlayer.getCurrentPosition());
        mediaPlayer.pause();
        outState.putBoolean("isPlaying", mediaPlayer.isPlaying());
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        songIndex = SpotifyContract.TopTracksEntry.getPositionFromTrackUri(mUri);
        if (null != mUri) {
            return new CursorLoader(getActivity(), mUri, TOP_TRACKS_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToPosition(songIndex);
        mCursor = cursor;
        updateUI();
        playMedia();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        loader = null;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        progressDialog.dismiss();
        Log.e(LOG_TAG, "Media player error caught WHAT:" + what + " Extra: " + extra);
        Toast.makeText(getActivity(), "Could not connect to network", Toast.LENGTH_SHORT).show();

        if (!play) {
            playButton.setImageResource(android.R.drawable.ic_media_play);
            if (mediaPlayer != null)
                mediaPlayer.release();
            play = true;
            mediaPlayer = null;
        }
        return true;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        playButton.setImageResource(android.R.drawable.ic_media_play);
        play = true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }
}
