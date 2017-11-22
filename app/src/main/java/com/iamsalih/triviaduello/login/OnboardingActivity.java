package com.iamsalih.triviaduello.login;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iamsalih.triviaduello.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class OnboardingActivity extends AppCompatActivity {

    @BindView(R.id.onboarding_view_pager)
    ViewPager onboardingViewPager;
    @BindView(R.id.onboarding_viewpager_indicator)
    CircleIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        initializeViewPager();
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
