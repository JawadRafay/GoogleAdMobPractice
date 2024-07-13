package com.example.googleadmob;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

   private AppOpenAdManager appOpenAdManager;
   private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();



    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public void loadAd(@NonNull Activity activity) {
        appOpenAdManager.loadAd(activity);
    }

    public interface onShowAdCompleteListener {
        void onAdShown();
    }

    public void showAdIfAvailable(Activity activity, onShowAdCompleteListener listener) {
        appOpenAdManager.showAdIfAvailable(activity, listener);
    }

    private static class AppOpenAdManager {

        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        public AppOpenAdManager() {
        }

        private void loadAd(Context context) {
            if (isLoadingAd || isAdAvailable()) {
                return;
            }
            isLoadingAd = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(context, AD_UNIT_ID, adRequest, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isLoadingAd = false;
                    Log.d("TAG", "onAdFailedToLoad: "+loadAdError);
                    Toast.makeText(context, ""+loadAdError, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "There was an error loading", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd openAd) {
                    super.onAdLoaded(openAd);
                    appOpenAd = openAd;
                    isLoadingAd = false;
                    Toast.makeText(context, "Ad loaded", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private boolean isAdAvailable() {
            return appOpenAd != null;
        }

        private void showAdIfAvailable(Activity activity){
            showAdIfAvailable(activity, new onShowAdCompleteListener() {
                @Override
                public void onAdShown() {

                }
            });
        }

        private void showAdIfAvailable(Activity activity, onShowAdCompleteListener listener) {
            if (isShowingAd){
                return;
            }

            if (!isAdAvailable()) {
                listener.onAdShown();
                return;
            }
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    isShowingAd = false;
                    listener.onAdShown();
                    appOpenAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    isShowingAd = false;
                    listener.onAdShown();
                    appOpenAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
            isShowingAd = true;
            appOpenAd.show(activity);
        }

    }
}
