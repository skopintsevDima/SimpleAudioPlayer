package com.pet.simpleplayer.service.di;

import com.pet.simpleplayer.app.di.AppComponent;
import com.pet.simpleplayer.service.AudioPlayerService;
import com.pet.simpleplayer.service.AudioSessionManager;

import dagger.Component;

@Component(modules = ServiceModule.class, dependencies = AppComponent.class)
@ServiceScope
public interface ServiceComponent {
    void inject(AudioPlayerService playerService);

    void inject(AudioSessionManager audioSessionManager);
}
