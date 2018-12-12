package com.pet.simpleplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;

import com.pet.simpleplayer.Constants;
import com.pet.simpleplayer.R;
import com.pet.simpleplayer.activity.di.ActivityComponentImpl;
import com.pet.simpleplayer.player.PlayerState;
import com.pet.simpleplayer.player.controller.PlayerController;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class PlayerActivity extends AppCompatActivity {

    @Inject
    PlayerController mPlayerController;

    private AppCompatTextView mAudioName;
    private AppCompatImageButton mPlayPauseBtn;
    private AppCompatImageButton mStopBtn;

    private Disposable mPlayerStateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponentImpl.buildComponent().inject(this);
        mPlayerController.setPlayerScreenState(true);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_player);

        mAudioName = findViewById(R.id.audioName);
        mAudioName.setText(Constants.AUDIO_NAME);

        mPlayPauseBtn = findViewById(R.id.playPause);
        mPlayPauseBtn.setOnClickListener(btn -> mPlayerController.togglePlayPause());

        mStopBtn = findViewById(R.id.stop);
        mStopBtn.setOnClickListener(btn -> mPlayerController.stop());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerStateSubscription = mPlayerController.getPlayerState()
                .subscribe(this::renderPlayerState);
        mPlayerController.init(Constants.AUDIO_FILE_RES_ID);
    }

    private void renderPlayerState(PlayerState playerState) {
        switch (playerState){
            case PAUSED:
            case STOPPED:{
                mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                break;
            }
            case PLAYING:{
                mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerController.setPlayerScreenState(false);
        mPlayerStateSubscription.dispose();
    }
}
