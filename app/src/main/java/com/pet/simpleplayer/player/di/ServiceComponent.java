package com.pet.simpleplayer.player.di;

import com.pet.simpleplayer.app.di.AppComponent;
import com.pet.simpleplayer.player.service.AudioPlayerService;

import dagger.Component;

@Component(modules = ServiceModule.class, dependencies = AppComponent.class)
@ServiceScope
public interface ServiceComponent {
    void inject(AudioPlayerService playerService);

    void inject(AudioSessionManager audioSessionManager);
}
