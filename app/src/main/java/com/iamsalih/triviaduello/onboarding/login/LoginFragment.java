package com.iamsalih.triviaduello.onboarding.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.iamsalih.triviaduello.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by muhammedsalihguler on 23.11.17.
 */

public class LoginFragment extends Fragment implements LoginView {

    @BindView(R.id.facebook_login_button)
    Button loginButton;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private CallbackManager callbackManager;

    private LoginPresenter presenter;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.login_page_fragment, container, false);
        ButterKnife.bind(this, rootView);
        callbackManager = CallbackManager.Factory.create();
        presenter = new LoginPresenter(this);
        presenter.registerAuthenticationCallback();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.facebook_login_button)
    public void startLoginWithFacebook() {
        showProgressBar();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    public void showProgressBar() {
        loadingIndicator.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        loadingIndicator.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getAppContext() {
        return getContext();
    }

    @Override
    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
