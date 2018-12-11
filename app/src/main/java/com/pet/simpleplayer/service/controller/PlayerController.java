package com.pet.simpleplayer.service.controller;

import android.support.annotation.RawRes;

public interface PlayerController {
    boolean isServiceBound();

    void setServiceState(boolean serviceBound);

    void init(@RawRes int audioFileName);

    void play();

    void pause();

    void resume();

    void stop();

    void releasePlayer();
}
