package com.santoshmandadi.spotifystreamer.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback, ArtistDetailsActivityFragment.DetailsCallback{
    boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String DIALOGPLAYER_TAG = "DPLAYERTAG";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.artist_detail_container) != null){
            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.artist_detail_container, new ArtistDetailsActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else {
                mTwoPane = false;
            }
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri contentUri) {

        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(ArtistDetailsActivityFragment.DETAIL_URI, contentUri);

            ArtistDetailsActivityFragment fragment = new ArtistDetailsActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.artist_detail_container, fragment, DETAILFRAGMENT_TAG).commit();

        }else {
            Intent intent = new Intent(this, ArtistDetailsActivity.class);
            intent.setData(contentUri);
            startActivity(intent);
             }
    }

    @Override
    public void onTrackItemSelected(Uri contentUri) {

        Log.v(LOG_TAG, "Called the "+LOG_TAG+" DialogFragment Loader");
        Bundle args = new Bundle();
        args.putParcelable(PlayerActivityFragment.TRACK_URI, contentUri);
        PlayerActivityFragment playerActivityFragment = new PlayerActivityFragment();
        playerActivityFragment.setArguments(args);

        playerActivityFragment.show(getSupportFragmentManager(), DIALOGPLAYER_TAG);

    }
}
