package com.pet.simpleplayer.player.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.RawRes;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pet.simpleplayer.player.di.ServiceComponentImpl;

import javax.inject.Inject;

public class AudioPlayerService
        extends Service

        implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,

        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = AudioPlayerService.class.getSimpleName();

    public static final String KEY_AUDIO_RES_ID = "com.pet.simpleplayer.service.KEY_AUDIO_RES_ID";

    public static final String ACTION_PLAY = "com.pet.simpleplayer.service.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.pet.simpleplayer.service.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.pet.simpleplayer.service.ACTION_RESUME";
    public static final String ACTION_STOP = "com.pet.simpleplayer.service.ACTION_STOP";

    private final IBinder iBinder = new LocalBinder();

    private MediaPlayer mAudioPlayer;

    @Inject
    AudioManager mAudioManager;

    @RawRes
    private int mAudioFileResId;
    private int mResumePosition;

    //Handle incoming phone calls
    private boolean mOngoingCall = false;
    private PhoneStateListener mPhoneStateListener;

    @Inject
    TelephonyManager mTelephonyManager;

    private BroadcastReceiver mControllerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_PLAY:{
                        playAudio();
                        break;
                    }
                    case ACTION_PAUSE:{
                        pauseAudio();
                        break;
                    }
                    case ACTION_RESUME:{
                        resumeAudio();
                        break;
                    }
                    case ACTION_STOP:{
                        stopAudio();
                        break;
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceComponentImpl.buildComponent().inject(this);
        initCallStateListener();
        registerControllerReceiver();
    }

    private void registerControllerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_RESUME);
        filter.addAction(ACTION_STOP);
        registerReceiver(mControllerReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mAudioFileResId = intent.getExtras().getInt(KEY_AUDIO_RES_ID);
        } catch (NullPointerException e) {
            // Stop Service, if audio file path was not provided.
            stopSelf();
        }

        if (!requestAudioFocus()) {
            stopSelf();
        }

        initMediaPlayer();

        return super.onStartCommand(intent, flags, startId);
    }

    private boolean requestAudioFocus() {
        int result = 0;
        if (mAudioManager != null) {
            result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void initMediaPlayer() {
        mAudioPlayer = MediaPlayer.create(this, mAudioFileResId);

        mAudioPlayer.setOnCompletionListener(this);
        mAudioPlayer.setOnErrorListener(this);
        mAudioPlayer.setOnInfoListener(this);

        mAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    void playAudio() {
        if (mAudioPlayer == null)
            return;
        if (!mAudioPlayer.isPlaying()) {
            mAudioPlayer.start();
        }
    }

    void pauseAudio() {
        if (mAudioPlayer == null)
            return;
        if (mAudioPlayer.isPlaying()) {
            mAudioPlayer.pause();
            mResumePosition = mAudioPlayer.getCurrentPosition();
        }
    }

    void resumeAudio() {
        if (mAudioPlayer == null)
            return;
        if (!mAudioPlayer.isPlaying()) {
            mAudioPlayer.seekTo(mResumePosition);
            mAudioPlayer.start();
        }
    }

    void stopAudio() {
        if (mAudioPlayer == null)
            return;
        if (mAudioPlayer.isPlaying()) {
            mAudioPlayer.seekTo(0);
            mAudioPlayer.pause();
        }
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // Resume playback.
                if (mAudioPlayer == null)
                    initMediaPlayer();
                else if (!mAudioPlayer.isPlaying())
                    mAudioPlayer.start();
                mAudioPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mAudioPlayer.isPlaying())
                    mAudioPlayer.stop();
                mAudioPlayer.release();
                mAudioPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mAudioPlayer.isPlaying())
                    mAudioPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mAudioPlayer.isPlaying())
                    mAudioPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    //Handle incoming phone calls.
    private void initCallStateListener() {
        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING: {
                        if (mAudioPlayer != null) {
                            pauseAudio();
                            mOngoingCall = true;
                        }
                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mAudioPlayer != null) {
                            if (mOngoingCall) {
                                mOngoingCall = false;
                                resumeAudio();
                            }
                        }
                        break;
                }
            }
        };
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Stop playback and Service when audio completed.
        stopAudio();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int errorCode, int extra) {
        switch (errorCode) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d(TAG, "MEDIA ERROR - NOT VALID FOR PROGRESSIVE PLAYBACK: " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d(TAG, "MEDIA ERROR - SERVER DIED: " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d(TAG, "MEDIA ERROR - UNKNOWN: " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAudioPlayer != null) {
            stopAudio();
            mAudioPlayer.release();
        }
        removeAudioFocus();

        if (mPhoneStateListener != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        unregisterReceiver(mControllerReceiver);
    }

    private void removeAudioFocus() {
        mAudioManager.abandonAudioFocus(this);
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
}
