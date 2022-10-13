package com.getyoteam.budamind.playback;

import android.media.MediaPlayer;

import com.getyoteam.budamind.Model.ChapterListModel;
import com.getyoteam.budamind.Model.DownloadFileModel;
import com.getyoteam.budamind.Model.MomentListModel;
import com.getyoteam.budamind.Model.SoundListModel;
import com.google.android.exoplayer2.SimpleExoPlayer;

public interface PlayerAdapter {

    void initMediaPlayer();

    void release();

    boolean isMediaPlayer();

    boolean isPlaying();

    void resumeOrPause();

    void onForward();

    void onBackword();

    void stopPlayer();

    void reset();

    boolean isReset();

    void instantReset();

    void skip(final boolean isNext);

    void seekTo(final int position);

    void setPlaybackInfoListener(final PlaybackInfoListener playbackInfoListener);

    String getCurrentSong();

    String getCurrentSubTitleSong();

    @PlaybackInfoListener.State
    int getState();

    int getPlayerPosition();

    void registerNotificationActionsReceiver(final boolean isRegister);


    void setCurrentSong(final MomentListModel song, final SoundListModel sound, final ChapterListModel chapterListModel, final DownloadFileModel downloadFileModel);

    MediaPlayer getMediaPlayer();

    SimpleExoPlayer getExoPlayer();

    void onPauseActivity();

    void onResumeActivity();
}
