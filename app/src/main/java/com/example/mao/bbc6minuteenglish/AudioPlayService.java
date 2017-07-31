package com.example.mao.bbc6minuteenglish;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.utilities.NotificationUtility;

import java.io.IOException;

public class AudioPlayService extends Service implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener{

    private static final String TAG = AudioPlayService.class.getName();

    public final IBinder mBinder = new LocalBinder();

    private static final int AUDIO_SERVICE_NOTIFICATION_ID = R.string.audio_service_notification;

    public static final String ACTION_INITIALIZE = "com.example.mao.bbc6minuteenglish.action_initialize";
    public static final String ACTION_PLAY = "com.example.mao.bbc6minuteenglish.action_play";
    public static final String ACTION_PAUSE = "com.example.mao.bbc6minuteenglish.action_pause";

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mResumePosition;
    private String mAudioHref;
    private boolean mIsPrepared;
    private Uri mUriWithTimeStamp;
    private int mCachedProgress;


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
            case ACTION_PLAY:
                resumeMedia();
                break;
            case ACTION_PAUSE:
                pauseMedia();
                break;
            default:
                break;
        }
    }

    private void initialize(Intent intent) {
        //Request audio focus
        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
        }

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
        mCachedProgress = percent * getDuration() / 100;
        Log.v(TAG, "" + mCachedProgress + "; " + getCurrentPosition());
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
            default:
                Log.d("MediaPlayer Error", "UNKNOWN" + extra);
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        removeAudioFocus();
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Log.v(TAG, "On seek complete");
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
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
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
        }
        // remove audio focus
        removeAudioFocus();
    }

    public class LocalBinder extends Binder{
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.reset();

        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        );

        try {
            mMediaPlayer.setDataSource(mAudioHref);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mMediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            updateNotification(ACTION_PAUSE);
        }
    }

    private void stopMedia() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            updateNotification(ACTION_PLAY);
        }
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mResumePosition = mMediaPlayer.getCurrentPosition();
            updateNotification(ACTION_PLAY);
        }
    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
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
        return mCachedProgress;
    }
}
