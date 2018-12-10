package com.pet.simpleplayer.activity.di;

import com.pet.simpleplayer.activity.MainActivity;
import com.pet.simpleplayer.app.di.AppComponent;

import dagger.Component;

@Component(modules = ActivityModule.class, dependencies = AppComponent.class)
@ActivityScope
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
}
