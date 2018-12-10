package com.pet.simpleplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;

import com.pet.simpleplayer.R;
import com.pet.simpleplayer.activity.di.ActivityComponentImpl;
import com.pet.simpleplayer.service.interactor.PlayerInteractor;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_PLAYER_SERVICE_STATE = "KEY_PLAYER_SERVICE_STATE";

    @Inject
    PlayerInteractor mPlayerInteractor;

    private AppCompatButton mPlayBtn;
    private AppCompatButton mPauseBtn;
    private AppCompatButton mResumeBtn;
    private AppCompatButton mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponentImpl.buildComponent().inject(this);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);

        mPlayBtn = findViewById(R.id.play);
        mPlayBtn.setOnClickListener(btn -> mPlayerInteractor.play());

        mPauseBtn = findViewById(R.id.pause);
        mPlayBtn.setOnClickListener(btn -> mPlayerInteractor.pause());

        mResumeBtn = findViewById(R.id.resume);
        mPlayBtn.setOnClickListener(btn -> mPlayerInteractor.resume());

        mStopBtn = findViewById(R.id.stop);
        mPlayBtn.setOnClickListener(btn -> mPlayerInteractor.stop());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_PLAYER_SERVICE_STATE, mPlayerInteractor.isServiceBound());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean serviceBound = savedInstanceState.getBoolean(KEY_PLAYER_SERVICE_STATE);
        mPlayerInteractor.setServiceState(serviceBound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerInteractor.releasePlayer();
        mPlayerInteractor = null;
    }
}
