package com.pet.simpleplayer.service.di;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.telephony.TelephonyManager;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
class ServiceModule {

    @Provides
    @ServiceScope
    @Inject
    AudioManager provideAudioManager(Context context){
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @ServiceScope
    @Inject
    TelephonyManager provideTelephonyManager(Context context){
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Provides
    @ServiceScope
    @Inject
    MediaSessionManager provideMediaSessionManager(Context context){
        return (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    @Provides
    @ServiceScope
    @Inject
    NotificationManager provideNotificationManager(Context context){
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
