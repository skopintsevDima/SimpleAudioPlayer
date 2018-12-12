package com.pet.simpleplayer.activity.di;

import com.pet.simpleplayer.app.App;

public class ActivityComponentImpl {

    private static ActivityComponent sComponent;

    public static ActivityComponent buildComponent(){
        if (sComponent == null) {
            sComponent = DaggerActivityComponent.builder()
                    .appComponent(App.getAppComponent())
                    .activityModule(new ActivityModule())
                    .build();
        }
        return sComponent;
    }

    public static ActivityComponent get() throws IllegalStateException{
        if (sComponent == null)
            throw new IllegalStateException("ActivityComponent is not initialized!");
        return sComponent;
    }
}
