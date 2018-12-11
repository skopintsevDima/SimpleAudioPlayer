package com.pet.simpleplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;

import com.pet.simpleplayer.Constants;
import com.pet.simpleplayer.R;
import com.pet.simpleplayer.activity.di.ActivityComponentImpl;
import com.pet.simpleplayer.service.controller.PlayerController;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_PLAYER_SERVICE_STATE = "KEY_PLAYER_SERVICE_STATE";

    @Inject
    PlayerController mPlayerController;

    private AppCompatButton mPlayBtn;
    private AppCompatButton mPauseBtn;
    private AppCompatButton mResumeBtn;
    private AppCompatButton mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponentImpl.buildComponent().inject(this);
        mPlayerController.init(Constants.AUDIO_FILE_RES_ID);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);

        mPlayBtn = findViewById(R.id.play);
        mPlayBtn.setOnClickListener(btn -> mPlayerController.play());

        mPauseBtn = findViewById(R.id.pause);
        mPauseBtn.setOnClickListener(btn -> mPlayerController.pause());

        mResumeBtn = findViewById(R.id.resume);
        mResumeBtn.setOnClickListener(btn -> mPlayerController.resume());

        mStopBtn = findViewById(R.id.stop);
        mStopBtn.setOnClickListener(btn -> mPlayerController.stop());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_PLAYER_SERVICE_STATE, mPlayerController.isServiceBound());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean serviceBound = savedInstanceState.getBoolean(KEY_PLAYER_SERVICE_STATE);
        mPlayerController.setServiceState(serviceBound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerController.releasePlayer();
        mPlayerController = null;
    }
}
