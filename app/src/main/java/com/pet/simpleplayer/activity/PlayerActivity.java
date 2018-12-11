package com.pet.simpleplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;

import com.pet.simpleplayer.Constants;
import com.pet.simpleplayer.R;
import com.pet.simpleplayer.activity.di.ActivityComponentImpl;
import com.pet.simpleplayer.player.controller.PlayerController;
import com.pet.simpleplayer.player.controller.PlayerState;
import com.pet.simpleplayer.player.notification.PlayerNotification;

import javax.inject.Inject;

public class PlayerActivity extends AppCompatActivity {

    private static final String KEY_PLAYER_STATE = "KEY_PLAYER_STATE";

    @Inject
    PlayerController mPlayerController;

    private AppCompatTextView mAudioName;
    private AppCompatImageButton mPlayPauseBtn;
    private AppCompatImageButton mStopBtn;

    private PlayerState mPlayerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponentImpl.buildComponent().inject(this);
        mPlayerController.init(Constants.AUDIO_FILE_RES_ID);
        mPlayerState = new PlayerState();
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_player);

        mAudioName = findViewById(R.id.audioName);
        mAudioName.setText(Constants.AUDIO_NAME);

        mPlayPauseBtn = findViewById(R.id.playPause);
        mPlayPauseBtn.setOnClickListener(btn -> {
            if (mPlayerState.getAudioState() != PlayerState.PLAYING){
                mPlayerState.setAudioState(PlayerState.PLAYING);
            } else {
                mPlayerState.setAudioState(PlayerState.PAUSED);
            }
            renderPlayerState();
        });

        mStopBtn = findViewById(R.id.stop);
        mStopBtn.setOnClickListener(btn -> {
            mPlayerState.setAudioState(PlayerState.STOPPED);
            renderPlayerState();
        });
    }

    private void renderPlayerState(){
        switch (mPlayerState.getAudioState()){
            case PlayerState.STOPPED:{
                PlayerNotification.hidePlayerNotification();
                mPlayerController.stop();
                mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                break;
            }
            case PlayerState.PLAYING:{
                if (mPlayerState.getPreviousAudioState() == PlayerState.STOPPED){
                    PlayerNotification.showPlayerNotification(this, Constants.AUDIO_NAME);
                }
                mPlayerController.play();
                mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                break;
            }
            case PlayerState.PAUSED:{
                mPlayerController.pause();
                mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBundle(KEY_PLAYER_STATE, mPlayerState.getState());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle previousPlayerStateBundle = savedInstanceState.getBundle(KEY_PLAYER_STATE);
        if (previousPlayerStateBundle == null){
            previousPlayerStateBundle = new Bundle();
        }
        mPlayerState = new PlayerState(previousPlayerStateBundle);
        mPlayerController.setServiceState(mPlayerState.isPlayerServiceBound());
        renderPlayerState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerController.releasePlayer();
        mPlayerController = null;
        PlayerNotification.hidePlayerNotification();
    }
}
