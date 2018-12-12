package com.pet.simpleplayer.player.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import com.pet.simpleplayer.R;
import com.pet.simpleplayer.activity.PlayerActivity;
import com.pet.simpleplayer.app.App;
import com.pet.simpleplayer.utils.NotificationUtils;

import static com.pet.simpleplayer.player.service.AudioPlayerService.*;
import static com.pet.simpleplayer.utils.NotificationUtils.DEFAULT_NC_CHANNEL_ID;

public class PlayerNotification {

    private static final int NOTIFICATION_ID = 201;
    public static final String ACTION_CLOSE_PLAYER = "ACTION_CLOSE_PLAYER";

    private static Notification sPlayerNotification;

    public static void showPlayerNotification(Context context, String audioName){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (sPlayerNotification == null) {
            RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                    R.layout.notification_player);
            notificationView.setTextViewText(R.id.audioName, audioName);
            initUI(notificationView, context);

            NotificationCompat.Builder notificationBuilder;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notificationBuilder = new NotificationCompat.Builder(context);
            } else {
                NotificationUtils.initChannels(context);
                notificationBuilder = new NotificationCompat.Builder(context, DEFAULT_NC_CHANNEL_ID);
            }

            Intent notifyIntent = new Intent(context, PlayerActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent deleteIntent = new Intent(ACTION_CLOSE_PLAYER);
            PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 0,
                    deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(notifyPendingIntent);
            notificationBuilder.setSmallIcon(android.R.drawable.ic_media_play);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setDeleteIntent(deletePendingIntent);
            notificationBuilder.setCustomBigContentView(notificationView);
            notificationBuilder.setOngoing(true);

            sPlayerNotification = notificationBuilder.build();
        }
        notificationManager.notify(NOTIFICATION_ID, sPlayerNotification);
    }

    private static void initUI(RemoteViews notificationView, Context context) {
        Intent playIntent = new Intent(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.play, playPendingIntent);

        Intent pauseIntent = new Intent(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.pause, pausePendingIntent);

        Intent stopIntent = new Intent(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0,
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.stop, stopPendingIntent);
    }

    public static void hidePlayerNotification(){
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(App.getAppComponent().context());
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
