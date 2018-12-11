package com.pet.simpleplayer.player.di;

import android.content.Context;
import android.media.AudioManager;
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
}
