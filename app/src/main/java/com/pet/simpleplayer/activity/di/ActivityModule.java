package com.pet.simpleplayer.activity.di;

import com.pet.simpleplayer.player.controller.PlayerController;
import com.pet.simpleplayer.player.controller.PlayerControllerImpl;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
class ActivityModule {

    private PlayerController mPlayerController;

    ActivityModule(){
        mPlayerController = new PlayerControllerImpl();
    }

    @Provides
    @ActivityScope
    @Inject
    PlayerController providePlayerInteractor(){
        return mPlayerController;
    }
}
