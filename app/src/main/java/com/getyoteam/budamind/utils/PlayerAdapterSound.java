package com.getyoteam.budamind.utils;

import com.getyoteam.budamind.playback.PlaybackInfoListener;

public interface PlayerAdapterSound {

    void release();

    boolean isPlaying();

    void resumeOrPause();

    void stopPlayer();

    void setPlaybackInfoListener(final PlaybackInfoListener playbackInfoListener);


}
