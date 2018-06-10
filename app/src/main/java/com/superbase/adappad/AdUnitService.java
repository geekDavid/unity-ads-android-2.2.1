package com.superbase.adappad;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.log.DeviceLog;
import com.unity3d.ads.misc.Utilities;

public class AdUnitService extends Service {

    private Activity bindActivity;
    private MyBinder binder = new MyBinder();

    final private String TAG = AdUnitService.class.getName();
//    final public static String defaultGameId = "14851";
    final public static String defaultGameId = "2592645";
    final AdUnitService.UnityAdsListener unityAdsListener = new AdUnitService.UnityAdsListener();
    public static boolean isCheckWebViewVisible = true;

    private boolean isStartVideo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return binder;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(TAG, "onStart: ");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class MyBinder extends Binder {

        public AdUnitService getService() {
            return AdUnitService.this;
        }

        public void setActivity(Activity activity) {
            bindActivity = activity;
        }

        public void init() {
            UnityAds.setListener(unityAdsListener);
            String gameId = defaultGameId;
            if (gameId.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Missing game id", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.e("main", "开始初始化 ");
            UnityAds.initialize(bindActivity, gameId, unityAdsListener);
        }

        public void show() {
            Log.e(TAG, "show1");
            if (bindActivity != null) {
                Log.e(TAG, "show2");
                if (UnityAds.isReady()) {
                    Log.e(TAG, "show3");
                    isStartVideo = true;
                    isCheckWebViewVisible = true;
                    UnityAds.show(bindActivity);
                } else if (!UnityAds.isInitialized()) {
                    Log.e(TAG, "show4 + " + defaultGameId);
//                    UnityAds.show(bindActivity);
                    UnityAds.initialize(bindActivity, defaultGameId, unityAdsListener);
                } else {
                    Log.e(TAG, "show5 + " + defaultGameId);
                }
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (isCheckWebViewVisible) {
//                        try {
//                            Thread.sleep(2000);
//                            WebView webView = WebViewApp.getCurrentApp().getWebView();
//                            if (webView != null) {
//                                Log.e(TAG, "检测webView是否打开");
//                                if (webView.getVisibility() == View.VISIBLE) {
////                                    input tap 100 200
//                                    Log.e(TAG, "webView打开啦");
//                                    Thread.sleep(2000);
//                                    int x = TouchUserManager.getDeviceWidth(AdUnitService.this) - 15;
//                                    TouchUserManager.manTianGuoHai(webView, x, 20);
//                                }
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                            return;
//                        }
//                    }
//                }
//            }).start();
        }
    }

    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady(final String zoneId) {
//            bindActivity.moveTaskToBack(true);
            Log.e("main", "缩小窗体");
            DeviceLog.debug("onUnityAdsReady: " + zoneId);
            Log.e("main", "onUnityAdsReady准备就绪: " + zoneId);
            Utilities.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // look for various default placement ids over time
                    switch (zoneId) {
                        case "video":
                        case "defaultZone":
                        case "defaultVideoAndPictureZone": {
                            if (!isStartVideo) {
                                binder.show();
                            }
                            break;
                        }
                        case "rewardedVideo":
                        case "rewardedVideoZone":
                        case "incentivizedZone": {
                            if (!isStartVideo) {
                                binder.show();
                            }
                            break;
                        }
                    }
                }
            });

            toast("Ready", zoneId);
        }

        @Override
        public void onUnityAdsStart(String zoneId) {
            DeviceLog.debug("onUnityAdsStart: " + zoneId);
            Log.e("main", "onUnityAdsStart开始: " + zoneId);
            toast("Start", zoneId);
        }

        @Override
        public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {
            isStartVideo = false;
            binder.show();
            DeviceLog.debug("onUnityAdsFinish: " + zoneId + " - " + result);
            Log.e("main", "onUnityAdsFinish完成: " + zoneId + " - " + result);
            toast("Finish", zoneId + " " + result);
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
            isStartVideo = false;
            UnityAds.initialize(bindActivity, defaultGameId, unityAdsListener);
            DeviceLog.debug("onUnityAdsError: " + error + " - " + message);
            Log.e("main", "onUnityAdsError错误: " + error + " - " + message);
            toast("Error", error + " " + message);
        }

        private void toast(String callback, String msg) {
            Toast.makeText(getApplicationContext(), callback + ": " + msg, Toast.LENGTH_SHORT).show();
        }
    }
}
