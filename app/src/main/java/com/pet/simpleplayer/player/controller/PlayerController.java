package com.pet.simpleplayer.player.controller;

import android.support.annotation.RawRes;

import com.pet.simpleplayer.player.PlayerState;

import io.reactivex.subjects.Subject;

public interface PlayerController {
    void init(@RawRes int audioFileName);

    Subject<PlayerState> getPlayerState();

    void togglePlayPause();

    void stop();

    void setPlayerScreenState(boolean alive);
}
