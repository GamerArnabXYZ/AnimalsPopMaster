package com.gax.bubbleshoot;

import android.app.Activity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import com.gax.bubbleshoot.util.FileLogger;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class AdManager {

    private final Activity mActivity;

    private RewardedAd mRewardedAd;
    private AdRewardListener mListener;
    private boolean mRewardEarned = false;

    public AdManager(Activity activity) {
        mActivity = activity;
        requestAd();
    }

    public void requestAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(mActivity, mActivity.getString(R.string.txt_admob_reward),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error
                        FileLogger.d("AdManager", "Reward ad failed to load: " + loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(RewardedAd rewardedAd) {
                        FileLogger.d("AdManager", "Reward ad loaded successfully.");
                        mRewardedAd = rewardedAd;
                    }
                });
    }

    public boolean showRewardAd() {
        if (mRewardedAd == null) {
            return false;
        }

        // Reset state
        mRewardEarned = false;

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown
                FileLogger.d("AdManager", "Reward ad was shown.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show
                FileLogger.d("AdManager", "Reward ad failed to show: " + adError.getMessage());
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed
                FileLogger.d("AdManager", "Reward ad was dismissed.");
                mRewardedAd = null;
                // Check if user dismiss Ad before earn
                if (!mRewardEarned) {
                    mListener.onLossReward();
                }
                requestAd();
            }
        });

        mRewardedAd.show(mActivity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(RewardItem rewardItem) {
                // Handle the reward
                // Toast.makeText(mActivity, "Reward!", Toast.LENGTH_SHORT).show();
                mListener.onEarnReward();
                mRewardEarned = true;
            }
        });

        return true;
    }

    public void setListener(AdRewardListener listener) {
        mListener = listener;
    }

    public interface AdRewardListener {
        void onEarnReward();

        void onLossReward();
    }

}
