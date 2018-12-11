package com.pet.simpleplayer.player.controller;

import android.os.Bundle;

public class PlayerState {

    private static final String KEY_PLAYER_SERVICE_STATE = "KEY_PLAYER_SERVICE_STATE";
    private static final String KEY_AUDIO_STATE = "KEY_AUDIO_STATE";

    public static final int STOPPED = -1;
    public static final int PLAYING = 0;
    public static final int PAUSED = 1;

    private Bundle mState;
    private int mPreviousAudioState = STOPPED;

    public Bundle getState() {
        return mState;
    }

    public boolean isPlayerServiceBound() {
        return mState.getBoolean(KEY_PLAYER_SERVICE_STATE, false);
    }

    public void setPlayerServiceBound(boolean playerServiceBound) {
        mState.putBoolean(KEY_PLAYER_SERVICE_STATE, playerServiceBound);
    }

    public int getAudioState() {
        return mState.getInt(KEY_AUDIO_STATE, STOPPED);
    }

    public void setAudioState(int audioState) {
        mPreviousAudioState = getAudioState();
        mState.putInt(KEY_AUDIO_STATE, audioState);
    }

    public int getPreviousAudioState() {
        return mPreviousAudioState;
    }

    public PlayerState(){
        mState = new Bundle();
    }

    public PlayerState(Bundle playerStateBundle){
        mState = playerStateBundle;
    }
}
