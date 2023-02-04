package com.rifcode.nearheart.View;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;


import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Adapter.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationViewEx navigation;
    private Menu menuNavBtm;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionPagerAdapter;

    private String TAG = "Mainactivty";
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd interstitialAd;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.frameLayout);
        navigation = findViewById(R.id.bnvMain);

        handler = new Handler();
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        navigation.enableAnimation(false);
        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
        navigation.setTextVisibility(false);
        navigation.setIconSize(32, 32);
        navigation.setCurrentItem(1);
        navigation.setIconMarginTop(0, 0);
        navigation.setIconMarginTop(1, 0);
        navigation.setIconMarginTop(2, 0);
        menuNavBtm = navigation.getMenu();

        sectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        /// set adapter from section pager adapter class
        viewPager.setAdapter(sectionPagerAdapter);
        /// install View pager :
        navigation.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        //facebook ads interstital ad
        interstitialAd = new InterstitialAd(this, getString(R.string.Interstitial_FacebbokAds));
        // load the ad
        interstitialAd.loadAd();
    }


private void showAdWithDelay() {
    /**
     * Here is an example for displaying the ad with delay;
     * Please do not copy the Handler into your project
     */
    // Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        public void run() {
            // Check if interstitialAd has been loaded successfully
            if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if(interstitialAd.isAdInvalidated()) {
                return;
            }
            // Show the ad
            interstitialAd.show();
        }
    }, 1000 * 60 * 5); // Show the ad after 15 minutes
}

    @Override
    protected void onStart() {
        super.onStart();
        showAdWithDelay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        showAdWithDelay();
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

}
