package com.pet.simpleplayer.service.interactor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.pet.simpleplayer.app.App;
import com.pet.simpleplayer.service.AudioPlayerService;

import javax.inject.Inject;

import static com.pet.simpleplayer.service.AudioPlayerService.*;

public class PlayerInteractorImpl implements PlayerInteractor {

    private static final String TAG = PlayerInteractorImpl.class.getSimpleName();

    private AudioPlayerService mPlayerService;

    private boolean mServiceBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            mPlayerService = binder.getService();
            mServiceBound = true;
            Log.d(TAG, "PLAYER SERVICE CONNECTED!");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            Log.d(TAG, "PLAYER SERVICE DISCONNECTED!");
        }
    };

    @Override
    public void setServiceState(boolean serviceBound){
        mServiceBound = serviceBound;
    }

    @Override
    public void init(String audioFilePath) {
        Context context = App.getAppComponent().context();
        if (!mServiceBound) {
            Intent playerIntent = new Intent(context, AudioPlayerService.class);
            playerIntent.putExtra(AudioPlayerService.KEY_AUDIO_PATH, audioFilePath);
            context.startService(playerIntent);
            context.bindService(playerIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            // Service is active. Send a broadcast to the service to play new audio file.
            Intent broadcastIntent = new Intent(ACTION_PLAY_NEW_AUDIO);
            context.sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public boolean isServiceBound(){
        return mServiceBound;
    }

    @Override
    public void play() {
        Context context = App.getAppComponent().context();
        Intent broadcastIntent = new Intent(ACTION_PLAY);
        context.sendBroadcast(broadcastIntent);
    }

    @Override
    public void pause() {
        Context context = App.getAppComponent().context();
        Intent broadcastIntent = new Intent(ACTION_PAUSE);
        context.sendBroadcast(broadcastIntent);
    }

    @Override
    public void resume() {
        Context context = App.getAppComponent().context();
        Intent broadcastIntent = new Intent(ACTION_RESUME);
        context.sendBroadcast(broadcastIntent);
    }

    @Override
    public void stop() {
        Context context = App.getAppComponent().context();
        Intent broadcastIntent = new Intent(ACTION_STOP);
        context.sendBroadcast(broadcastIntent);
    }

    @Override
    public void releasePlayer() {
        Context context = App.getAppComponent().context();
        if (mServiceBound) {
            context.unbindService(mServiceConnection);
            mPlayerService.stopSelf();
        }
    }
}
