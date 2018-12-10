package com.pet.simpleplayer.service.interactor;

public interface PlayerInteractor {
    boolean isServiceBound();

    void setServiceState(boolean serviceBound);

    void init(String audioFilePath);

    void play();

    void pause();

    void resume();

    void stop();

    void releasePlayer();
}
