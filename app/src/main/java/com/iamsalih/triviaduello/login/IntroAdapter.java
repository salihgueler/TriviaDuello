package com.iamsalih.triviaduello.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by muhammedsalihguler on 22.11.17.
 */

public class IntroAdapter extends FragmentPagerAdapter {

    private final static int ONBOARDING_ITEM_COUNT = 4;

    public IntroAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        // TODO: Add login view.
        return IntroFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return ONBOARDING_ITEM_COUNT;
    }
}
