package com.pet.simpleplayer.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationUtils {

    public static final String DEFAULT_NC_CHANNEL_ID = "default";
    private static final String DEFAULT_NC_CHANNEL_NAME = "default_nc_channel";

    public static void initChannels(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel defaultChannel = new NotificationChannel(DEFAULT_NC_CHANNEL_ID,
                    DEFAULT_NC_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            defaultChannel.setSound(null, null);
            notificationManager.createNotificationChannel(defaultChannel);
        }
    }
}
