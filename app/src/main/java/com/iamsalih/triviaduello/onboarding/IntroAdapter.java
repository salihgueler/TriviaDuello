package com.iamsalih.triviaduello.onboarding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iamsalih.triviaduello.onboarding.login.LoginFragment;

/**
 * Created by muhammedsalihguler on 22.11.17.
 */

public class IntroAdapter extends FragmentPagerAdapter {

    private final static int ONBOARDING_ITEM_COUNT = 4;
    private int LAST_ELEMENT_INDEX = 3;

    public IntroAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == LAST_ELEMENT_INDEX) {
            return LoginFragment.newInstance();
        }
        return IntroFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return ONBOARDING_ITEM_COUNT;
    }
}
