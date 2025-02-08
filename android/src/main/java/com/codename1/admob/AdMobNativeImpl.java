package com.codename1.admob;

import android.os.Looper;

import com.google.android.gms.ads.*;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import androidx.annotation.NonNull;
import com.codename1.impl.android.*;
import com.codename1.ui.Display;

import java.util.concurrent.CountDownLatch;

public class AdMobNativeImpl {
    private InterstitialAd interstitialAd;
    private String adID;

    public void init(String adID) {
        this.adID = adID;
        // Initialize MobileAds if you haven't yet:
        // MobileAds.initialize(context, initializationStatus -> {});
    }

    public boolean loadAd() {
        if (Display.getInstance().isEdt()) {
            boolean[] res = new boolean[1];
            Display.getInstance().invokeAndBlock(() -> {
                res[0] = loadAd();
            });

            return res[0];
        }

        final CodenameOneActivity activity = (CodenameOneActivity) AndroidNativeUtil.getActivity();
        // If not on the Android main thread, we need to switch to it and block until done.
        if (Looper.myLooper() != Looper.getMainLooper()) {
            final boolean[] resultHolder = new boolean[1];
            final CountDownLatch latch = new CountDownLatch(1);

            activity.runOnUiThread(() -> {
                resultHolder[0] = loadAdOnMainThread(activity);
                latch.countDown();
            });

            try {
                latch.await(); // Block until the UI thread has finished
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return resultHolder[0];
        } else {
            // Already on the main thread
            return loadAdOnMainThread(activity);
        }
    }

    /**
     * Actual logic for loading the ad; must be run on the Android main thread.
     */
    private boolean loadAdOnMainThread(CodenameOneActivity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();

        // InterstitialAd.load(...) is the new approach in Google Mobile Ads SDK v20+
        InterstitialAd.load(
                activity,
                adID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        interstitialAd = null;
                                        Callback.onAdClosed();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        interstitialAd = null;
                                        // (Optional) Handle error if needed
                                        Callback.onAdFailedToLoad(adError.getCode());
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Callback.onAdOpened();
                                    }
                                }
                        );

                        // Ad loaded callback
                        Callback.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Notify that loading failed
                        Callback.onAdFailedToLoad(loadAdError.getCode());
                    }
                }
        );

        // We return true immediately to indicate that we triggered the load process successfully.
        return true;
    }

    public boolean isLoaded() {
        // The new SDK does not provide isLoaded(). You must track if `interstitialAd` is non-null.
        return interstitialAd != null;
    }

    public void showAd() {
        final CodenameOneActivity activity = (CodenameOneActivity) AndroidNativeUtil.getActivity();

        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (interstitialAd != null) {
                    interstitialAd.show(activity);
                }
            }
        });
    }

    public boolean isSupported() {
        return true;
    }
}
