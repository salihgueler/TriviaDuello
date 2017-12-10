package com.iamsalih.triviaduello.onboarding;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iamsalih.triviaduello.BuildConfig;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.MainScreenActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class OnboardingActivity extends AppCompatActivity {

    @BindView(R.id.onboarding_view_pager)
    ViewPager onboardingViewPager;
    @BindView(R.id.onboarding_viewpager_indicator)
    CircleIndicator indicator;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        initFirebaseAuthListener();
    }

    private void initFirebaseAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(OnboardingActivity.this, MainScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    initializeViewPager();

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                        Bundle bundle = new Bundle();
                        bundle.putString("onboarding_screen", "onboarding_started");
                        FirebaseAnalytics.getInstance(OnboardingActivity.this).logEvent("trivia_duello", bundle);
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthStateListener != null) {
            firebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mAuthStateListener = null;
    }

    private void initializeViewPager() {
        onboardingViewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
        indicator.setViewPager(onboardingViewPager);
    }

    @OnClick(R.id.onboarding_next_button)
    public void onNextClicked() {
        int currentPosition = onboardingViewPager.getCurrentItem();

        if (currentPosition < onboardingViewPager.getChildCount()) {
            currentPosition += 1;
            onboardingViewPager.setCurrentItem(currentPosition);
        }
    }

    @OnClick(R.id.onboarding_previous_button)
    public void onPreviousClicked() {
        int currentPosition = onboardingViewPager.getCurrentItem();

        if (currentPosition > 0) {
            currentPosition -= 1;
            onboardingViewPager.setCurrentItem(currentPosition);
        }
    }
}
