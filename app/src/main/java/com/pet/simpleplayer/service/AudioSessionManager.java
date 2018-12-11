package com.pet.simpleplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.pet.simpleplayer.Constants;
import com.pet.simpleplayer.R;
import com.pet.simpleplayer.service.di.ServiceComponentImpl;

import javax.inject.Inject;

import static com.pet.simpleplayer.service.AudioPlayerService.*;

public class AudioSessionManager {

    private static final String TAG_MEDIA_SESSION = "AUDIO_PLAYER";

    private AudioPlayerService mPlayerService;

    // MediaSession
    @Inject
    MediaSessionManager mMediaSessionManager;

    @Inject
    NotificationManager mNotificationManager;

    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat.TransportControls mTransportControls;

    // AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 201;

    AudioSessionManager(AudioPlayerService playerService){
        ServiceComponentImpl.get().inject(this);
        mPlayerService = playerService;
    }

    void initMediaSession() {
        mMediaSession = new MediaSessionCompat(mPlayerService.getApplicationContext(),
                TAG_MEDIA_SESSION);
        mTransportControls = mMediaSession.getController().getTransportControls();
        mMediaSession.setActive(true);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, Constants.AUDIO_NAME)
                .build());

        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                mPlayerService.resumeAudio();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                mPlayerService.pauseAudio();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the Service.
                mPlayerService.stopSelf();
            }
        });
    }

    void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent playPauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer.
        if (playbackStatus == PlaybackStatus.PLAYING) {
            playPauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            playPauseAction = playbackAction(0);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                mPlayerService)
                .setShowWhen(false)
                .setStyle(new MediaStyle()
                        // Attach our MediaSession token.
                        .setMediaSession(mMediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1))
                .setColor(mPlayerService.getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentTitle(Constants.AUDIO_NAME)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                // Add playback actions.
                .addAction(notificationAction, "Play/Pause", playPauseAction);
        if (mNotificationManager != null){
            mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private void removeNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(mPlayerService, AudioPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(mPlayerService, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(mPlayerService, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    void play() {
        mTransportControls.play();
    }

    void pause() {
        mTransportControls.pause();
    }

    void stop() {
        mTransportControls.stop();
    }

    void release() {
        removeNotification();
        mMediaSessionManager = null;
        mMediaSession.release();
        mMediaSession = null;
    }

    public enum PlaybackStatus {
        PLAYING,
        PAUSED
    }
}
