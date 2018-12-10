package com.pet.simpleplayer.activity.di;

import com.pet.simpleplayer.service.interactor.PlayerInteractor;
import com.pet.simpleplayer.service.interactor.PlayerInteractorImpl;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
class ActivityModule {

    private PlayerInteractor mPlayerInteractor;

    ActivityModule(){
        mPlayerInteractor = new PlayerInteractorImpl();
    }

    @Provides
    @ActivityScope
    @Inject
    PlayerInteractor providePlayerInteractor(){
        return mPlayerInteractor;
    }
}
