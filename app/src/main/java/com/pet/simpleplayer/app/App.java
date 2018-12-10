package com.pet.simpleplayer.app;

import android.app.Application;

import com.pet.simpleplayer.app.di.AppComponent;
import com.pet.simpleplayer.app.di.AppModule;

public class App extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        buildAppComponent();
    }

    private void buildAppComponent() {
        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
