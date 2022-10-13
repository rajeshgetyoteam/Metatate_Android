package com.getyoteam.budamind.activity;

import android.support.v4.media.MediaMetadataCompat;

import com.getyoteam.budamind.Model.Artist;
import com.getyoteam.budamind.MyApplication;
import com.getyoteam.budamind.utils.MyPreferenceManager;


public interface IMainActivity {

    void hideProgressBar();

    void showPrgressBar();

    void onCategorySelected(String category);

    void onArtistSelected(String category, Artist artist);

    void setActionBarTitle(String title);

    void playPause();

    MyApplication getMyApplicationInstance();

    void onMediaSelected(String playlistId, MediaMetadataCompat mediaItem, int position);

    MyPreferenceManager getMyPreferenceManager();
}
