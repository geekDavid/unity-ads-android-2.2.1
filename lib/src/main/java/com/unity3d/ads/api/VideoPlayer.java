package com.unity3d.ads.api;

import android.os.Build;
import android.util.Log;

import com.unity3d.ads.ThreadTool;
import com.unity3d.ads.log.DeviceLog;
import com.unity3d.ads.misc.Utilities;
import com.unity3d.ads.video.VideoPlayerError;
import com.unity3d.ads.video.VideoPlayerEvent;
import com.unity3d.ads.video.VideoPlayerView;
import com.unity3d.ads.webview.WebViewApp;
import com.unity3d.ads.webview.WebViewEventCategory;
import com.unity3d.ads.webview.bridge.WebViewCallback;
import com.unity3d.ads.webview.bridge.WebViewExposed;

import static android.content.ContentValues.TAG;

public class VideoPlayer {
    private static VideoPlayerView _videoPlayerView;

    public static void setVideoPlayerView(VideoPlayerView videoPlayerView) {
        _videoPlayerView = videoPlayerView;
    }

    public static VideoPlayerView getVideoPlayerView() {
        return _videoPlayerView;
    }

    @WebViewExposed
    public static void setProgressEventInterval(final Integer milliseconds, final WebViewCallback callback) {
        Log.e(TAG, "setProgressEventInterval: ------------ :" + milliseconds);
        Utilities.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getVideoPlayerView() != null) {
                    getVideoPlayerView().setProgressEventInterval(milliseconds);
                }
            }
        });

        if (getVideoPlayerView() != null) {
            callback.invoke();
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
    }

    @WebViewExposed
    public static void getProgressEventInterval(final WebViewCallback callback) {
        Log.e(TAG, "getProgressEventInterval: ------------");
        if (getVideoPlayerView() != null) {
            callback.invoke(getVideoPlayerView().getProgressEventInterval());
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
    }

    @WebViewExposed
    public static void prepare(final String url, final Double initialVolume, final WebViewCallback callback) {
        Log.e(TAG, "prepare: ------------");
        prepare(url, initialVolume, 0, callback);
    }

    @WebViewExposed
    public static void prepare(final String url, final Double initialVolume, final Integer timeout, final WebViewCallback callback) {
        DeviceLog.debug("Preparing video for playback: " + url);
        Log.e(TAG, "视频连接：" + url);
//        Utilities.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (getVideoPlayerView() != null) {
//                    getVideoPlayerView().prepare(url, initialVolume.floatValue(), timeout.intValue());
//                }
//            }
//        });
        mUrl = url;
        if (getVideoPlayerView() != null) {
            callback.invoke(url);
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
        getPlayTime(url);
        WebViewApp.getCurrentApp().sendEvent("准备视频回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PREPARED, url, mDuration, mWidth, mHeight);
    }

    @WebViewExposed
    public static void play(final WebViewCallback callback) {
        DeviceLog.debug("Starting playback of prepared video");
        Log.e(TAG, "play播放视频");
//        Utilities.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (getVideoPlayerView() != null) {
//                    getVideoPlayerView().play();
//                }
//            }
//        });


        getVideoPlayerView().stopVideoProgressTimer();
//        Log.e("VidoPlayer", "总时长: " + VideoPlayerView._mediaPlayer.getDuration());


        if (getVideoPlayerView() != null) {
            callback.invoke();
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
        videoPlay();
    }

    @WebViewExposed
    public static void pause(final WebViewCallback callback) {
        DeviceLog.debug("Pausing current video");
        Log.e(TAG, "暂停回调");
//        Utilities.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (getVideoPlayerView() != null) {
//                    getVideoPlayerView().pause();
//                }
//            }
//        });

//        if (getVideoPlayerView() != null) {
//            callback.invoke();
//        } else {
//            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
//        }
    }

    @WebViewExposed
    public static void stop(final WebViewCallback callback) {
        DeviceLog.debug("Stopping current video");
        Log.e("VideoPlayer", "stop停止播放");
//        Utilities.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (getVideoPlayerView() != null) {
//                    getVideoPlayerView().stop();
//                }
//            }
//        });
//
        if (getVideoPlayerView() != null) {
            callback.invoke();
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
    }

    @WebViewExposed
    public static void seekTo(final Integer time, final WebViewCallback callback) {
        DeviceLog.debug("Seeking video to time: " + time);
        Log.e("VideoPlayer", "拖动进度回调");
//        Utilities.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (getVideoPlayerView() != null) {
//                    getVideoPlayerView().seekTo(time);
//                }
//            }
//        });

        if (getVideoPlayerView() != null) {
            callback.invoke();
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }

    }

    @WebViewExposed
    public static void getCurrentPosition(final WebViewCallback callback) {
        Log.e(TAG, "getCurrentPosition: ------------");
        if (getVideoPlayerView() != null) {
            callback.invoke(getVideoPlayerView().currentIndex);
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
//        getVideoPlayerView().getCurrentIndex();
    }

    @WebViewExposed
    public static void getVolume(final WebViewCallback callback) {
        Log.e(TAG, "getVolume: ------------");
        if (getVideoPlayerView() != null) {
            callback.invoke(getVideoPlayerView().getVolume());
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
    }

    @WebViewExposed
    public static void setVolume(final Double volume, final WebViewCallback callback) {
        DeviceLog.debug("Setting video volume: " + volume);
        Log.e("VideoPlayer", "设置音量setVolume: " + volume);
        if (getVideoPlayerView() != null) {
            getVideoPlayerView().setVolume(0.0f);
            callback.invoke(volume);
        } else {
            callback.error(VideoPlayerError.VIDEOVIEW_NULL);
        }
    }

    @WebViewExposed
    public static void setInfoListenerEnabled(final boolean enabled, final WebViewCallback callback) {
        Log.e("VideoPlayer", "setInfoListenerEnabled---------: " + enabled);
        if (Build.VERSION.SDK_INT > 16) {
            if (getVideoPlayerView() != null) {
                getVideoPlayerView().setInfoListenerEnabled(enabled);
                callback.invoke(WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.INFO, enabled);
            } else {
                callback.error(VideoPlayerError.VIDEOVIEW_NULL);
            }
        } else {
            callback.error(VideoPlayerError.API_LEVEL_ERROR, Build.VERSION.SDK_INT, enabled);
        }
    }


    public static void videoPlay() {
        WebViewApp.getCurrentApp().sendEvent("视频播放：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PLAY, mUrl);
        ThreadTool.excute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                    int pos = (int) (Math.random() * 150);
                    index += pos;
                    WebViewApp.getCurrentApp().sendEvent("视频计时器：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PROGRESS, index);
                    WebViewApp.getCurrentApp().sendEvent("视频信息回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.INFO, mUrl, 3, 0);
                    while (true) {
                        pos = (int) (Math.random() * 3);
                        index += indexs[pos];
                        if (index >= mDuration) {
                            break;
                        }
                        WebViewApp.getCurrentApp().sendEvent("视频计时器：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.PROGRESS, index);
                    }
                    Thread.sleep(250);
                    WebViewApp.getCurrentApp().sendEvent("视频播放完成回调：", WebViewEventCategory.VIDEOPLAYER, VideoPlayerEvent.COMPLETED, mUrl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    static int mDuration = 0;
    static int mWidth = 0;
    static int mHeight = 0;
    static String mUrl = "";
    static int index = 0;
    final static int[] indexs = {249, 250, 251};

    private static void getPlayTime(String mUri) {
        mUri = mUri.substring(7, mUri.length());
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            mmr.setDataSource(mUri);
            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

            mDuration = Integer.parseInt(duration);
            mWidth = Integer.parseInt(width);
            mHeight = Integer.parseInt(height);
            Log.e("VidoPlayer", "总时长: " + mDuration + ",宽：" + mWidth + ",高：" + mHeight);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }

    }
}
