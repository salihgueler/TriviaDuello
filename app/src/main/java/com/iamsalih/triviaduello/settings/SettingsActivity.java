package com.iamsalih.triviaduello.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.onboarding.OnboardingActivity;
import com.iamsalih.triviaduello.helper.AppCompatPreferenceActivity;

import java.util.List;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class SettingsActivity extends AppCompatPreferenceActivity implements SettingsView {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private SettingsPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        initFirebaseAuthListener();
        presenter = new SettingsPresenter(this);
    }

    private void initFirebaseAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Intent intent = new Intent(SettingsActivity.this, OnboardingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        if (header.id == R.id.log_out_button) {
            presenter.showLogoutDialog();
        } else if (header.id == R.id.category_button) {
            presenter.showCategoryDialog();
        } else if (header.id == R.id.profile_button) {
            presenter.showProfileFragment();
        }
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public Context getAppContext() {
        return this;
    }
}
