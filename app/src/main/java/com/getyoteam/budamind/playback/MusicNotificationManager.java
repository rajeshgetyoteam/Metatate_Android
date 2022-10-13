package com.getyoteam.budamind.playback;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSessionManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import com.getyoteam.budamind.R;
import com.getyoteam.budamind.activity.PlayActivity;

public class MusicNotificationManager {
    public static final int NOTIFICATION_ID = 2000;
    static final String PLAY_PAUSE_ACTION = "PLAY/PAUSE";
    static final String NEXT_ACTION = "action.NEXT";
    static final String PREV_ACTION = "action.PREV";
    static final String STOP_ACTION = "STOP";
    private final String CHANNEL_ID = "action.CHANNEL_ID";
    private final int REQUEST_CODE = 100;
    private final NotificationManager mNotificationManager;
    private final MusicService mMusicService;
    private NotificationCompat.Builder mNotificationBuilder;
    private MediaSessionCompat mediaSession;
    private MediaSessionManager mediaSessionManager;
    private MediaControllerCompat.TransportControls transportControls;
    private Context context;
    private Bitmap largeIcon;

    MusicNotificationManager(@NonNull final MusicService musicService) {
        mMusicService = musicService;
        mNotificationManager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
        context = musicService.getBaseContext();
    }


    public final NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public final NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    private PendingIntent playerAction(String action) {

        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);
        return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification createNotification() {

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, PlayActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        String artist = "";
        String songTitle = "";

        if (!TextUtils.isEmpty(mMusicService.getMediaPlayerHolder().getCurrentSong())) {
            artist = mMusicService.getMediaPlayerHolder().getCurrentSong();
            songTitle = mMusicService.getMediaPlayerHolder().getCurrentSubTitleSong();
        }

        initMediaSession();
        largeIcon = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.noti_icon
        );
        mNotificationBuilder
                .setShowWhen(false)
                .setColor(context.getResources().getColor(R.color.color_yellow))
                .setContentTitle(artist)
                .setContentText(songTitle)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.noti_icon)
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(STOP_ACTION))
                .setContentIntent(resultPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

//        mNotificationBuilder.setStyle(new NotificationCompat.Style()
//                .setMediaSession(mediaSession.getSessionToken())
//                .setShowActionsInCompactView(0, 1));


        return mNotificationBuilder.build();

    }

    public void cancelNotification() {
        mNotificationManager.cancel(MusicNotificationManager.NOTIFICATION_ID);
    }


    @NonNull
    private NotificationCompat.Action notificationAction(final String action) {
        int icon;
        switch (action) {
            default:
            case PREV_ACTION:
                icon = R.drawable.ic_backword;
                break;
            case STOP_ACTION:
                icon = R.drawable.ic_stop;
                break;
            case PLAY_PAUSE_ACTION:

                icon = mMusicService.getMediaPlayerHolder().getState() != PlaybackInfoListener.State.PAUSED
                        ? R.drawable.ic_pause_white : R.drawable.ic_play_white;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_forward;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    @RequiresApi(26)
    private void createNotificationChannel() {

        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            final NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            mMusicService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    mMusicService.getString(R.string.app_name));


            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint("ServiceCast")
    private void initMediaSession() {
        mediaSessionManager = ((MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE));
        mediaSession = new MediaSessionCompat(context, "AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updateMetaData();
    }

    private void updateMetaData() {
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .build());
    }

}
