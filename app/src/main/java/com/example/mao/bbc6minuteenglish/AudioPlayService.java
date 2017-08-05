package com.example.mao.bbc6minuteenglish;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.mao.bbc6minuteenglish.cache.App;
import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.utilities.NotificationUtility;

import java.io.File;
import java.io.IOException;

/**
 * Create by mao 2017.7
 *
 * Thanks for the guide
 * A Step by Step Guide to Building an Android Audio Player App
 * By Valdio Veliu  August 19, 2016
 * https://www.sitepoint.com/a-step-by-step-guide-to-building-an-android-audio-player-app/
 */


public class AudioPlayService extends Service implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        AudioManager.OnAudioFocusChangeListener, CacheListener{

    private static final String TAG = AudioPlayService.class.getSimpleName();

    private HttpProxyCacheServer mAudioProxy;

    public final IBinder mBinder = new LocalBinder();

    private static final int AUDIO_SERVICE_NOTIFICATION_ID = R.string.audio_service_notification;

    public static final String ACTION_INITIALIZE = "com.example.mao.bbc6minuteenglish.action_initialize";
    public static final String ACTION_PLAY = "com.example.mao.bbc6minuteenglish.action_play";
    public static final String ACTION_PAUSE = "com.example.mao.bbc6minuteenglish.action_pause";
    public static final String ACTION_FORWARD = "com.example.mao.bbc6minuteenglish.action_forward";
    public static final String ACTION_REPLAY = "com.example.mao.bbc6minuteenglish.action_replay";

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mResumePosition = 0;
    private String mAudioHref;
    private boolean mIsPrepared;
    private Uri mUriWithTimeStamp;
    private int mCachedProgress;

    //Handle incoming phone calls
    private boolean mOngoingCall = false;
    private PhoneStateListener mPhoneStateListener;
    private TelephonyManager mTelephonyManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handleIntent(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_INITIALIZE:
                initialize(intent);
                break;
            case ACTION_PLAY:
                resumeMedia();
                break;
            case ACTION_PAUSE:
                pauseMedia();
                break;
            case ACTION_FORWARD:
                controlSeekPosition(getCurrentPosition() + 5000);
                break;
            case ACTION_REPLAY:
                controlSeekPosition(getCurrentPosition() - 5000);
                break;
            default:
                break;
        }
    }

    private void initialize(Intent intent) {

        mAudioHref = intent
                .getStringExtra(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_MP3_HREF);

        mUriWithTimeStamp = intent.getData();

        if (!TextUtils.isEmpty(mAudioHref)) {
            initMediaPlayer();
        }

        updateNotification(ACTION_INITIALIZE);
    }

    private void updateNotification(String action) {
        Notification notification =
                NotificationUtility.buildAudioServiceNotification(this, mUriWithTimeStamp, action);
        startForeground(AUDIO_SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mCachedProgress = percent;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        mIsPrepared = false;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_IO:
                Log.d("MediaPlayer Error", "MEDIA ERROR IO " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.d("MediaPlayer Error", "MEDIA ERROR TIMED OUT " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.d("MediaPlayer Error", "MEDIA ERROR MALFORMED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNSUPPORTED " + extra);
                break;
            default:
                Log.d("MediaPlayer Error", "UNKNOWN" + extra);
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(TAG, "audio on complete");
        stopMedia();
        removeAudioFocus();
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        playMedia();
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                Log.v(TAG, "audio focus gain");
                if (mMediaPlayer == null) initMediaPlayer();
                else playMedia();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Log.v(TAG, "audio_loss");
                pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                Log.v(TAG, "Loss transient");
                pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                Log.v(TAG, "loss transient can duck");
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
            default:
                break;
        }
    }

    private boolean requestAudioFocus() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        callStateListener();
        registerBecomingNoisyReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "on destroy");
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
        }
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        // remove audio focus
        removeAudioFocus();
        unRegisterBecomingNoisyReceiver();
        mAudioProxy.unregisterCacheListener(this);
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if (percentsAvailable == 100) {
            Toast.makeText(this, getString(R.string.cache_complete), Toast.LENGTH_SHORT).show();
        }
    }

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mMediaPlayer != null) {
                            pauseMedia();
                            mOngoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mMediaPlayer != null && mOngoingCall) {
                            mOngoingCall = false;
                            resumeMedia();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Becoming noisy
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            NotificationUtility.buildAudioServiceNotification(
                    AudioPlayService.this, mUriWithTimeStamp, ACTION_PAUSE);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private void unRegisterBecomingNoisyReceiver() {
        unregisterReceiver(becomingNoisyReceiver);
    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy = App.getProxy(this);
        if (proxy.isCached(mAudioHref)) {
            mCachedProgress = 100;
        } else {
            Toast.makeText(this, getString(R.string.cache_start), Toast.LENGTH_SHORT).show();
        }
    }

    public class LocalBinder extends Binder{
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        // On complete not work if set at here
        //mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.reset();

        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        );

        mAudioProxy = App.getProxy(this);
        String proxyUrl = mAudioProxy.getProxyUrl(mAudioHref);
        checkCachedState();
        mAudioProxy.registerCacheListener(this, mAudioHref);
        try {
            mMediaPlayer.setDataSource(proxyUrl);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mMediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!mMediaPlayer.isPlaying() && requestAudioFocus()) {
            mMediaPlayer.start();
            updateNotification(ACTION_PAUSE);
        }
    }

    private void stopMedia() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mResumePosition = 0;
        removeAudioFocus();
        updateNotification(ACTION_PLAY);
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mResumePosition = mMediaPlayer.getCurrentPosition();
            updateNotification(ACTION_PLAY);
        }
    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying() && requestAudioFocus()) {
            mMediaPlayer.seekTo(mResumePosition);
            mMediaPlayer.start();
            updateNotification(ACTION_PAUSE);
        }
    }

    private void seekMedia(int position) {
        if (!mMediaPlayer.isPlaying()) {
            mResumePosition = position;
        }
        mMediaPlayer.seekTo(position);
    }

    public void controlPlayStatus() {
        if (mMediaPlayer.isPlaying()) {
            pauseMedia();
        } else {
            resumeMedia();
        }
    }

    public void controlSeekPosition(int position) {
        if (position >= 0 && position <= mMediaPlayer.getDuration()) {
            seekMedia(position);
        } else if (position < 0) {
            seekMedia(0);
        } else {
            seekMedia(mMediaPlayer.getDuration());
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int getDuration() {
        if (isPrepared()){
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public int getCurrentPosition() {
        if (isPrepared()) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public int getCachedProgress() {
        return mCachedProgress  * getDuration() / 100;
    }
}
