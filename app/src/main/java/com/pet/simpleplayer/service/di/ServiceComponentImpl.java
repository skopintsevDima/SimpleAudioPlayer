package com.pet.simpleplayer.service.di;

import com.pet.simpleplayer.app.App;

public class ServiceComponentImpl {

    private static ServiceComponent sComponent;

    public static ServiceComponent buildComponent(){
        return sComponent = DaggerServiceComponent.builder()
                .appComponent(App.getAppComponent())
                .serviceModule(new ServiceModule())
                .build();
    }

    public static ServiceComponent get() throws IllegalStateException{
        if (sComponent == null)
            throw new IllegalStateException("ServiceComponent is not initialized!");
        return sComponent;
    }
}
