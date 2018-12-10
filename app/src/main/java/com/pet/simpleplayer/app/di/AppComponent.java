package com.pet.simpleplayer.app.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
public interface AppComponent {
    Context context();
}
