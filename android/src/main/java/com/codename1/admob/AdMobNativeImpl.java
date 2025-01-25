package com.codename1.admob;

import com.google.android.gms.ads.*;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import androidx.annotation.NonNull;
import com.codename1.impl.android.*;

public class AdMobNativeImpl {
    private InterstitialAd interstitialAd;
    private String adID;

    public void init(String adID) {
        this.adID = adID;
        // Initialize MobileAds if you haven't yet:
        // MobileAds.initialize(context, initializationStatus -> {});
    }

    public boolean loadAd() {
        final CodenameOneActivity activity = (CodenameOneActivity) AndroidNativeUtil.getActivity();

        AdRequest adRequest = new AdRequest.Builder().build();

        // InterstitialAd.load(...) is the new approach in v20+
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
                                        // (Optional) Handle error if you need
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
