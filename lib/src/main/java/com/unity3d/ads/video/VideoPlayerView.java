package com.unity3d.ads.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.VideoView;

import com.unity3d.ads.ThreadTool;
import com.unity3d.ads.log.DeviceLog;
import com.unity3d.ads.webview.WebViewApp;
import com.unity3d.ads.webview.WebViewEventCategory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerView extends VideoView {
    public static String _videoUrl;
    private Timer _videoTimer;
    private Timer _prepareTimer;
    private int _progressEventInterval = 500;
    public static MediaPlayer _mediaPlayer = null;
    private Float _volume = null;
    public boolean _infoListenerEnabled = true;
    public int currentIndex;
    private boolean isFirst = true;
    private boolean isScond = true;
    private int duration;
    private String TAG = "VideoPlayer";

    public VideoPlayerView(Context context) {
        super(context);
    }

    public void startVideoProgressTimer() {
        _videoTimer = new Timer();
        _videoTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean isPlaying = false;
                try {
                    isPlaying = isPlaying();
                    currentIndex = getCurrentPosition();
                    WebViewApp.getCurrentApp().sendEvent("视频计时器：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PROGRESS, currentIndex);
                } catch (IllegalStateException e) {
                    DeviceLog.exception("Exception while sending current position to webapp", e);
                    WebViewApp.getCurrentApp().sendEvent("视频计时器错误：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.ILLEGAL_STATE, VideoPlayerEvent.PROGRESS, _videoUrl, isPlaying);
                }
            }
        }, _progressEventInterval, _progressEventInterval);

    }

    public boolean isComplate = false;

    public void getCurrentIndex() {
        isComplate = false;
        currentIndex += 250;
        if (isScond) {
            isScond = false;
            currentIndex = (new Random().nextInt(10) + 23);
        }
        if (currentIndex >= getDuration()) {
            WebViewApp.getCurrentApp().sendEvent("视频计时器：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PROGRESS, getDuration());
            WebViewApp.getCurrentApp().sendEvent("视频播放完成回调player：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.COMPLETED, _videoUrl);
            stopVideoProgressTimer();
            currentIndex = 0;
            isComplate = true;
            return;
        }
//        WebViewApp.getCurrentApp().sendEvent("视频计时器player：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PROGRESS, currentIndex);
    }

    public void stopVideoProgressTimer() {
        if (_videoTimer != null) {
            _videoTimer.cancel();
            _videoTimer.purge();
            _videoTimer = null;
        }
    }

    private void startPrepareTimer(long delay) {
        _prepareTimer = new Timer();
        _prepareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isPlaying()) {
                    WebViewApp.getCurrentApp().sendEvent("准备视频计时器：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PREPARE_TIMEOUT, _videoUrl);
                    DeviceLog.error("Video player prepare timeout: " + _videoUrl);
                }
            }
        }, delay);
    }

    public void stopPrepareTimer() {
        if (_prepareTimer != null) {
            _prepareTimer.cancel();
            _prepareTimer.purge();
            _prepareTimer = null;
        }
    }

    public boolean prepare(final String url, final float initialVolume, final int timeout) {
        DeviceLog.entered();

        _videoUrl = url;

        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                stopPrepareTimer();

                if (mp != null) {
                    _mediaPlayer = mp;
                }
                duration = mp.getDuration();
                setVolume(initialVolume);
                WebViewApp.getCurrentApp().sendEvent("准备视频回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PREPARED, _videoUrl, mp.getDuration(), mp.getVideoWidth(), mp.getVideoHeight());
            }
        });

        setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stopPrepareTimer();

                if (mp != null) {
                    _mediaPlayer = mp;
                }

                WebViewApp.getCurrentApp().sendEvent("视频错误回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.GENERIC_ERROR, _videoUrl, what, extra);
                stopVideoProgressTimer();
                return true;
            }
        });
        Log.e("playerView", "设置Info: " + _infoListenerEnabled);
        setInfoListenerEnabled(_infoListenerEnabled);

        if (timeout > 0) {
            startPrepareTimer((long) timeout);
        }

        try {
            setVideoPath(_videoUrl);
        } catch (Exception e) {
            WebViewApp.getCurrentApp().sendEvent("设置视频链接异常：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PREPARE_ERROR, _videoUrl);
            DeviceLog.exception("Error preparing video: " + _videoUrl, e);
            return false;
        }

        return true;
    }

    public void play() {
        DeviceLog.entered();

        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    _mediaPlayer = mp;
                }

                WebViewApp.getCurrentApp().sendEvent("视频播放完成回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.COMPLETED, _videoUrl);
                stopVideoProgressTimer();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2000);
//                            WebView webView = WebViewApp.getCurrentApp().getWebView();
//                            if (webView != null) {
//                                Log.e(TAG, "检测webView是否打开");
//                                if (webView.getVisibility() == View.VISIBLE) {
////                                    input tap 100 200
//                                    Log.e(TAG, "webView打开啦");
//                                    int x = TouchUserManager.getDeviceWidth(getContext()) - 15;
//                                    TouchUserManager.manTianGuoHai(webView, x, 20);
//                                }
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
            }
        });

        start();
        stopVideoProgressTimer();
        startVideoProgressTimer();

        WebViewApp.getCurrentApp().sendEvent("视频播放：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PLAY, _videoUrl);
//        threadPlay();

    }

    int[] indexs = {248, 249, 250, 251, 252, 253};

    private void threadPlay() {
        ThreadTool.excute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    _mediaPlayer.pause();
                    stopVideoProgressTimer();
                    while (true) {
                        int r = new Random().nextInt(6);
                        currentIndex += indexs[r];
                        if (currentIndex >= duration) {
                            _mediaPlayer.seekTo(duration - 250);
                            _mediaPlayer.start();
                            return;
                        }
                        WebViewApp.getCurrentApp().sendEvent("视频计时器：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PROGRESS, currentIndex);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setInfoListenerEnabled(boolean enabled) {
        _infoListenerEnabled = enabled;
        if (Build.VERSION.SDK_INT > 16) {
            if (_infoListenerEnabled) {
                setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        WebViewApp.getCurrentApp().sendEvent("视频信息回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.INFO, _videoUrl, what, extra);
                        return true;
                    }
                });
            } else {
                setOnInfoListener(null);
            }
        }
    }

    public void pause() {
        try {
            super.pause();
        } catch (Exception e) {
            WebViewApp.getCurrentApp().sendEvent("视频暂停异常：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PAUSE_ERROR, _videoUrl);
            DeviceLog.exception("Error pausing video", e);
            return;
        }

        stopVideoProgressTimer();
        WebViewApp.getCurrentApp().sendEvent("视频播放暂停回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PAUSE, _videoUrl);
    }

    @Override
    public void seekTo(int msec) {
        try {
            super.seekTo(msec);
        } catch (Exception e) {
            WebViewApp.getCurrentApp().sendEvent("视频SeekTo异常：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.SEEKTO_ERROR, _videoUrl);
            DeviceLog.exception("Error seeking video", e);
            return;
        }

        WebViewApp.getCurrentApp().sendEvent("视频SeekTo：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.SEEKTO, _videoUrl);
    }

    public void stop() {
        stopPlayback();
        stopVideoProgressTimer();
        WebViewApp.getCurrentApp().sendEvent("视频停止播放：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.STOP, _videoUrl);
    }

    public float getVolume() {
        return _volume;
    }

    public void setVolume(Float volume) {
        try {
            _mediaPlayer.setVolume(volume, volume);
            _volume = volume;
        } catch (Exception e) {
            DeviceLog.exception("MediaPlayer generic error", e);
            return;
        }
    }

    public void setProgressEventInterval(int ms) {
        _progressEventInterval = ms;
        if (_videoTimer != null) {
            stopVideoProgressTimer();
            startVideoProgressTimer();
        }
    }

    public int getProgressEventInterval() {
        return _progressEventInterval;
    }
}
