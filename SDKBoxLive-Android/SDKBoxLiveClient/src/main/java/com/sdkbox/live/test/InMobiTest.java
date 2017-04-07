package com.sdkbox.live.test;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.sdkbox.live.utils.Utils;

import java.util.Map;

/**
 * Created by htl on 28/03/2017.
 */

public class InMobiTest extends BaseTest {

    private InMobiInterstitial interstitial;
    private InMobiBanner banner;
    private InMobiConfig inmobiCfg;

    public InMobiTest(Activity act, Result r) {
        super(act, "InMobi", r);
    }

    public void run(String config) {
        inmobiCfg = new Gson().fromJson(config, InMobiConfig.class);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!Utils.isNull(inmobiCfg.interstitial)) {
                    testInterstitial(Long.parseLong(inmobiCfg.interstitial, 10));
                }
                if (!Utils.isNull(inmobiCfg.banner)) {
                    testBanner(Long.parseLong(inmobiCfg.banner, 10));
                }
            }
        });
    }

    private void testInterstitial(long id) {
        interstitial = null;
        interstitial = new com.inmobi.ads.InMobiInterstitial(activity, id,
                new InMobiInterstitial.InterstitialAdListener2() {
                    @Override
                    public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
                        onResult("interstitial", "load interstitial failed");
                        interstitial = null;
                    }

                    @Override
                    public void onAdReceived(InMobiInterstitial inMobiInterstitial) {
                    }

                    @Override
                    public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
                        interstitial.show();
                        onResult("interstitial", "load interstitial succeeded");
                    }

                    @Override
                    public void onAdRewardActionCompleted(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
                    }

                    @Override
                    public void onAdDisplayFailed(InMobiInterstitial inMobiInterstitial) {
                        onResult("interstitial", "show interstitial failed");
                        interstitial = null;
                    }

                    @Override
                    public void onAdWillDisplay(InMobiInterstitial inMobiInterstitial) {
                    }

                    @Override
                    public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
                        onResult("interstitial", "show interstitial succeeded");
                        interstitial = null;
                    }

                    @Override
                    public void onAdInteraction(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {

                    }

                    @Override
                    public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {

                    }

                    @Override
                    public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {

                    }
                });
        interstitial.load();
    }

    private void testBanner(long id) {
        banner = new InMobiBanner(activity, id);
        banner.setListener(new InMobiBanner.BannerAdListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                onResult("banner", "load banner succeeded");
            }

            @Override
            public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                onResult("banner", "load banner failed");
            }

            @Override
            public void onAdDisplayed(InMobiBanner inMobiBanner) {
                onResult("banner", "show banner succeeded");
            }

            @Override
            public void onAdDismissed(InMobiBanner inMobiBanner) {}

            @Override
            public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {}

            @Override
            public void onUserLeftApplication(InMobiBanner inMobiBanner) {}

            @Override
            public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {}
        });
        addToRootView(activity, banner);
        banner.load();
    }

    private void addToRootView(Activity activity, View bannerAd) {
        Point s = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(s);
        int w = s.x;
        int h = 200;

        RelativeLayout layout = new RelativeLayout(activity);
        activity.addContentView(layout,
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(bannerAd, params);
        layout.setFocusable(false);
    }

}
