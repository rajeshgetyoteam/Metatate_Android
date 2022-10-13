package com.getyoteam.budamind.playback;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import androidx.annotation.NonNull;

import com.getyoteam.budamind.Model.ChapterListModel;
import com.getyoteam.budamind.Model.DownloadFileModel;
import com.getyoteam.budamind.Model.MomentListModel;
import com.getyoteam.budamind.Model.SoundListModel;
import com.getyoteam.budamind.utils.AppDatabase;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.IOException;

class MediaPlayerHolder implements PlayerAdapter,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {

    private AudioFocusRequest focusRequest;
    private AppDatabase db;
    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    private static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    private static final float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;
    private final Context mContext;
    private final MusicService mMusicService;
    private final AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private Runnable mSeekBarPositionUpdateTask;
    private DownloadFileModel downloadFileModel = null;
    private Runnable mUpdateSeekbar;
    private MomentListModel mSelectedSong;
    private SoundListModel mSoundListModel;
    private ChapterListModel mChapterListModel;
    private boolean sReplaySong = false;
    private @PlaybackInfoListener.State
    int mState;
    private Handler mSeekbarUpdateHandler;
    private NotificationReceiver mNotificationActionsReceiver;
    private MusicNotificationManager mMusicNotificationManager;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private boolean mPlayOnFocusGain;
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(int focusChange) {

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            mPlayOnFocusGain = isMediaPlayer() && mState == PlaybackInfoListener.State.PLAYING
                                    || mState == PlaybackInfoListener.State.RESUMED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mMediaPlayer != null) {
                        // Update the player state based on the change
                        configurePlayerState();
                    }

                }
            };

    MediaPlayerHolder(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mContext = mMusicService.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private void registerActionsReceiver() {
        mNotificationActionsReceiver = new NotificationReceiver();
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MusicNotificationManager.PLAY_PAUSE_ACTION);
        intentFilter.addAction(MusicNotificationManager.STOP_ACTION);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mMusicService.registerReceiver(mNotificationActionsReceiver, intentFilter);
    }

    private void unregisterActionsReceiver() {
        if (mMusicService != null && mNotificationActionsReceiver != null) {
            try {
                mMusicService.unregisterReceiver(mNotificationActionsReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerNotificationActionsReceiver(final boolean isReceiver) {

        if (isReceiver) {
            registerActionsReceiver();
        } else {
            unregisterActionsReceiver();
        }
    }

    @Override
    public final String getCurrentSong() {
        if (mSelectedSong != null)
            return mSelectedSong.getTitle();
        else if (mChapterListModel != null)
            return "Day " + mChapterListModel.getChapterName();
        else if (mSoundListModel != null)
            return "Day " + mSoundListModel.getTitle();
        else if (downloadFileModel != null)
            return downloadFileModel.getTitle();

        return "";
    }

    @Override
    public final String getCurrentSubTitleSong() {
        if (mSelectedSong != null)
            return mSelectedSong.getSubtitle();
        else if (mChapterListModel != null)
            return mChapterListModel.getCourseName();
        else if (mSoundListModel != null)
            return mSoundListModel.getSubtitle();
        else
            return downloadFileModel.getSubTitle();
    }


    @Override
    public void setCurrentSong(final MomentListModel momentListModel, final SoundListModel soundListModel, final ChapterListModel chapterListModel, final DownloadFileModel downloadModel) {
        if (momentListModel != null) {
            mSelectedSong = momentListModel;
            mChapterListModel = null;
            mSoundListModel = null;
            downloadFileModel = null;
        } else if (soundListModel != null) {
            mSoundListModel = soundListModel;
            mSelectedSong = null;
            mChapterListModel = null;
            downloadFileModel = null;
        } else if (chapterListModel != null) {
            mChapterListModel = chapterListModel;
            mSoundListModel = null;
            mSelectedSong = null;
            downloadFileModel = null;
        } else {
            downloadFileModel = downloadModel;
            mSelectedSong = null;
            mSoundListModel = null;
            mChapterListModel = null;
        }
    }

    @Override
    public void onCompletion(@NonNull final MediaPlayer mediaPlayer) {
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
            mPlaybackInfoListener.onPlaybackCompleted();
            mediaPlayer.stop();
            mediaPlayer.release();
            mMediaPlayer = null;
            mMusicService.stopForeground(false);
            mMusicNotificationManager.cancelNotification();
        }

    }

    @Override
    public void onResumeActivity() {
        startUpdatingCallbackWithPosition();
    }

    @Override
    public void onPauseActivity() {
        stopUpdatingCallbackWithPosition();
    }

    private void tryToGetAudioFocus() {

        final int result = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    public void setPlaybackInfoListener(@NonNull final PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }

    private void setStatus(final @PlaybackInfoListener.State int state, int totalDuration) {
        mState = state;
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(state);
            if (totalDuration > 0)
                mPlaybackInfoListener.onPlaybackStarted(totalDuration);
        }
    }

    private void resumeMediaPlayer() {
        if (!isPlaying()) {
            mMediaPlayer.start();
            setStatus(PlaybackInfoListener.State.PLAYING, mMediaPlayer.getDuration());
            mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
        }
    }

    private void pauseMediaPlayer() {
        setStatus(PlaybackInfoListener.State.PAUSED, mMediaPlayer.getDuration());
        mMediaPlayer.pause();
        mMusicService.stopForeground(false);
        mMusicNotificationManager.getNotificationManager().notify(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
    }

    private void resetSong() {
        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
        setStatus(PlaybackInfoListener.State.PLAYING, mMediaPlayer.getDuration());
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition() {
        if (mSeekbarUpdateHandler == null) {
            mSeekbarUpdateHandler = new Handler();
        }

        if (mSeekbarUpdateHandler != null) {
            mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 50);
                }
            };
            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
        }
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition() {
        if (mSeekbarUpdateHandler != null) {
            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            mSeekbarUpdateHandler = null;
            mUpdateSeekbar = null;
        }
    }

    private void updateProgressCallbackTask() {
        if (isMediaPlayer() && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void instantReset() {
        if (isMediaPlayer()) {
            if (mMediaPlayer.getCurrentPosition() < 5000) {
                skip(false);
            } else {
                resetSong();
            }
        }
    }

    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the {@link com.getyoteam.budamind.activity.PlayActivity} the {@link MediaPlayer} is
     * released. Then in the onStart() of the {@link com.getyoteam.budamind.activity.PlayActivity} a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    @Override
    public void initMediaPlayer() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            } else {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
                mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            }
            tryToGetAudioFocus();
            if (downloadFileModel != null) {
                if (downloadFileModel.getModelName().equalsIgnoreCase("soundModel")) {
                    mMediaPlayer.setLooping(true);
                }
                mMediaPlayer.setDataSource(
                        mContext,
                        Uri.parse("file://" + downloadFileModel.getAudioFilePath() + downloadFileModel.getFileName())
                );
            } else if (mSoundListModel != null) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mSoundListModel.getAudio());
                mMediaPlayer.setLooping(true);
            } else if (mChapterListModel != null) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mChapterListModel.getAudioUrl());
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mSelectedSong.getAudio());
            }
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackword() {
        // 15000 milliseconds
        int seekBackwardTime = 15000;
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - seekBackwardTime);
        updateProgressCallbackTask();
    }

    @Override
    public void stopPlayer() {
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.STOP);
            mPlaybackInfoListener.onPlaybackStop();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mMusicService.stopForeground(false);
            mMusicNotificationManager.cancelNotification();
        }

    }

    @Override
    public void onForward() {
        // 15000 milliseconds
        int seekForwardTime = 15000;
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + seekForwardTime);
        updateProgressCallbackTask();
    }

    @Override
    public final MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public SimpleExoPlayer getExoPlayer() {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        startUpdatingCallbackWithPosition();
        setStatus(PlaybackInfoListener.State.PLAYING, mediaPlayer.getDuration());
    }


    @Override
    public void release() {
        if (isMediaPlayer()) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            giveUpAudioFocus();
        }
    }

    @Override
    public boolean isPlaying() {
        return isMediaPlayer() && mMediaPlayer.isPlaying();
    }

    @Override
    public void resumeOrPause() {
        if(isMediaPlayer()) {
            if (isPlaying()) {
                pauseMediaPlayer();
            } else {
                resumeMediaPlayer();
            }
        }
    }

    @Override
    public final @PlaybackInfoListener.State
    int getState() {
        return mState;
    }

    @Override
    public boolean isMediaPlayer() {
        return mMediaPlayer != null;
    }

    @Override
    public void reset() {
        sReplaySong = !sReplaySong;
    }

    @Override
    public boolean isReset() {
        return sReplaySong;
    }

    @Override
    public void skip(final boolean isNext) {
        getSkipSong(isNext);
    }

    private void getSkipSong(final boolean isNext) {
    }

    @Override
    public void seekTo(final int position) {
        if (isMediaPlayer()) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public int getPlayerPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the MediaPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {

        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pauseMediaPlayer();
        } else {

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                resumeMediaPlayer();
                mPlayOnFocusGain = false;
            }
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mPlaybackInfoListener.onBufferUpdate(percent);
    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();

            if (action != null) {

                switch (action) {
                    case MusicNotificationManager.PLAY_PAUSE_ACTION:
                        resumeOrPause();
                        break;
                    case MusicNotificationManager.STOP_ACTION:
                        stopPlayer();
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (mSelectedSong != null) {
                            pauseMediaPlayer();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        if (mSelectedSong != null && !isPlaying()) {
                            resumeMediaPlayer();
                        }
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        if (mSelectedSong != null) {
                            switch (intent.getIntExtra("state", -1)) {
                                //0 means disconnected
                                case 0:
                                    pauseMediaPlayer();
                                    break;
                                //1 means connected
                                case 1:
                                    if (!isPlaying()) {
                                        resumeMediaPlayer();
                                    }
                                    break;
                            }
                        }
                        break;
                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        if (isPlaying()) {
                            pauseMediaPlayer();
                        }
                        break;
                }
            }
        }
    }
}
