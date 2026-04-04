package cn.itcast.yinyue.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private final BaseServiceBinder<MusicService> binder = new BaseServiceBinder<>(this);
    MediaPlayer mediaPlayer;
    OnMusicStateListener mStateListener;

    public static final int IDLE = 0;     // 空闲
    public static final int PLAYING = 1;  // 播放中
    public static final int PAUSED = 2;    // 暂停
    public static final int STOPPED = 3;   // 停止
    // 当前播放状态
    private int currentState = IDLE;
    public MusicService() {}

    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: MusicService");
        mediaPlayer = new MediaPlayer();
        initMediaPlayerListener();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void setOnMusicStateListener(OnMusicStateListener listener) {
        this.mStateListener = listener;
    }
    private void initMediaPlayerListener() {

        // 1. 播放完成
        mediaPlayer.setOnCompletionListener(mp -> {
            currentState = STOPPED;
            if (mStateListener != null) {
                mStateListener.onCompletion(mp);
            }
        });

        // 2. 准备完成
        mediaPlayer.setOnPreparedListener(mp -> {
            currentState = PLAYING;
            mp.start();
            if (mStateListener != null) {
                mStateListener.onPrepared(mp);
            }
        });

        // 3. 错误监听
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            currentState = IDLE;
            if (mStateListener != null) {
                return mStateListener.onError(mp, what, extra);
            }
            return true;
        });

        // 4. 缓冲进度
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            if (mStateListener != null) {
                mStateListener.onBufferingUpdate(mp, percent);
            }
        });
    }

    public void playMusic(String path) {
        if (mediaPlayer == null) return;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "播放失败：" + e.getMessage());
        }
    }

    /**
     * 暂停播放
     */
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentState = PAUSED;
            Log.d(TAG, "暂停播放");
        }
    }

    /**
     * 继续播放
     */
    public void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && currentState == PAUSED) {
            mediaPlayer.start();
            currentState = PLAYING;
            Log.d(TAG, "继续播放");
        }
    }

    /**
     * 停止播放
     */
    public void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            currentState = STOPPED;
            Log.d(TAG, "停止播放");
        }
    }

    /**
     * 调整播放进度
     * @param position 进度（毫秒）
     */
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * 获取当前播放进度
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null && currentState == PLAYING || currentState == PAUSED) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取音频总时长
     */
    public int getDuration() {
        if (mediaPlayer != null && currentState == PLAYING || currentState == PAUSED) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前播放状态
     */
    public int getPlayState() {
        return currentState;
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public interface OnMusicStateListener {
        void onCompletion(MediaPlayer mp);
        // 准备完成，可以开始播放
        void onPrepared(MediaPlayer mp);
        // 播放出错
        boolean onError(MediaPlayer mp, int what, int extra);
        // 缓冲更新
        void onBufferingUpdate(MediaPlayer mp, int percent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放 MediaPlayer 资源
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentState = IDLE;
        Log.d(TAG, "Service 销毁，资源已释放");
    }
}