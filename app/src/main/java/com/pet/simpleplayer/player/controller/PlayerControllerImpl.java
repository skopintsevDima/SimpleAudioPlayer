package com.pet.simpleplayer.player.controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.RawRes;
import android.util.Log;

import com.pet.simpleplayer.Constants;
import com.pet.simpleplayer.app.App;
import com.pet.simpleplayer.player.PlayerState;
import com.pet.simpleplayer.player.notification.PlayerNotification;
import com.pet.simpleplayer.player.service.AudioPlayerService;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.pet.simpleplayer.player.notification.PlayerNotification.ACTION_CLOSE_PLAYER;
import static com.pet.simpleplayer.player.service.AudioPlayerService.*;

public class PlayerControllerImpl implements PlayerController {

    private static final String TAG = PlayerControllerImpl.class.getSimpleName();

    private AudioPlayerService mPlayerService;

    private boolean mServiceBound = false;
    private boolean mPlayerScreenAlive = false;
    private boolean mIsPlaying = false;
    private boolean mIsStopped = true;
    private Disposable mPlayerStateSubscription;
    private PlayerState mCurrentPlayerState;

    private Subject<PlayerState> mPlayerStateSubject = PublishSubject.create();

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            mPlayerService = binder.getService();
            mServiceBound = true;
            Log.d(TAG, "PLAYER SERVICE CONNECTED!");
            registerControllerReceiver();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            Log.d(TAG, "PLAYER SERVICE DISCONNECTED!");
            mPlayerService.unregisterReceiver(mControllerReceiver);
        }
    };

    private BroadcastReceiver mControllerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_PLAY:{
                        if (!mIsPlaying)
                            togglePlayPause();
                        break;
                    }
                    case ACTION_PAUSE:{
                        if (mIsPlaying)
                            togglePlayPause();
                        break;
                    }
                    case ACTION_STOP:{
                        stop();
                        break;
                    }
                    case ACTION_CLOSE_PLAYER:{
                        if (!mPlayerScreenAlive){
                            releasePlayer();
                        }
                        break;
                    }
                }
            }
        }
    };

    private void registerControllerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_CLOSE_PLAYER);
        mPlayerService.registerReceiver(mControllerReceiver, filter);
    }

    @Override
    public void init(@RawRes int audioFileResId) {
        Context context = App.getAppComponent().context();
        if (!mServiceBound) {
            Intent playerIntent = new Intent(context, AudioPlayerService.class);
            playerIntent.putExtra(AudioPlayerService.KEY_AUDIO_RES_ID, audioFileResId);
            context.startService(playerIntent);
            context.bindService(playerIntent, mServiceConnection, Context.BIND_IMPORTANT);
            mCurrentPlayerState = PlayerState.STOPPED;
            mPlayerStateSubscription = mPlayerStateSubject.subscribe(playerState -> {
                mCurrentPlayerState = playerState;
                switch (playerState){
                    case PLAYING:{
                        if (mIsStopped){
                            PlayerNotification.showPlayerNotification(context, Constants.AUDIO_NAME);
                            mIsStopped = false;
                        }
                        mIsPlaying = true;
                        break;
                    }
                    case PAUSED:{
                        mIsPlaying = false;
                        break;
                    }
                    case STOPPED:{
                        PlayerNotification.hidePlayerNotification();
                        mIsStopped = true;
                        mIsPlaying = false;
                        break;
                    }
                }
            });
        }
        mPlayerStateSubject.onNext(mCurrentPlayerState);
    }

    @Override
    public Subject<PlayerState> getPlayerState() {
        return mPlayerStateSubject;
    }

    @Override
    public void togglePlayPause() {
        if (mPlayerService != null) {
            if (mIsPlaying) {
                mPlayerService.pauseAudio();
                mPlayerStateSubject.onNext(PlayerState.PAUSED);
            } else {
                mPlayerService.playAudio();
                mPlayerStateSubject.onNext(PlayerState.PLAYING);
            }
        }
    }

    @Override
    public void stop() {
        if (mPlayerService != null) {
            mPlayerService.stopAudio();
            mPlayerStateSubject.onNext(PlayerState.STOPPED);
        }
    }

    @Override
    public void setPlayerScreenState(boolean alive) {
        mPlayerScreenAlive = alive;
    }

    private void releasePlayer() {
        Context context = App.getAppComponent().context();
        if (mServiceBound) {
            context.unbindService(mServiceConnection);
            mPlayerService.stopSelf();
            mServiceBound = false;
        }
        if (mPlayerStateSubscription != null && !mPlayerStateSubscription.isDisposed()) {
            mPlayerStateSubscription.dispose();
        }
        mIsPlaying = false;
        mIsStopped = true;
        mCurrentPlayerState = PlayerState.STOPPED;
    }
}
