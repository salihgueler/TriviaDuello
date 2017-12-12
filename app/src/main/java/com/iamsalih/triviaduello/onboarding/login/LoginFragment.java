package com.iamsalih.triviaduello.onboarding.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  Fragment for login operations.
 */

public class LoginFragment extends Fragment implements LoginView {

    @BindView(R.id.facebook_login_button)
    Button loginButton;

    @BindView(R.id.google_plus_login_button)
    Button googlePlusLoginButton;

    @BindView(R.id.login_with_email_and_password_button)
    Button loginWithEmailandPassword;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @BindView(R.id.email_text)
    TextInputEditText emailText;

    @BindView(R.id.password_name_text)
    TextInputEditText passwordText;

    @BindView(R.id.login_layout_holder)
    ConstraintLayout loginLayoutHolder;

    private CallbackManager callbackManager;

    private LoginPresenter presenter;

    private boolean isLoggingIn = false;
    private boolean isEmailPageOpen = false;

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
        presenter.initGoogleSingInClient();
        presenter.registerAuthenticationCallback();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AppConstants.IS_LOGGING_IN, isLoggingIn);
        outState.putBoolean(AppConstants.IS_EMAIL_PAGE_OPEN, isEmailPageOpen);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            isLoggingIn = savedInstanceState.getBoolean(AppConstants.IS_LOGGING_IN);
            isEmailPageOpen = savedInstanceState.getBoolean(AppConstants.IS_EMAIL_PAGE_OPEN);
            if (isEmailPageOpen) {
                showLoginView();
            }
            if (isLoggingIn) {
                showProgressBar();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.RC_SIGN_IN) {
            presenter.signInWithGoogle(data);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.facebook_login_button)
    public void startLoginWithFacebook() {
        showProgressBar();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(AppConstants.FACEBOOK_DEMANDED_INFORMATION));
    }

    @OnClick(R.id.google_plus_login_button)
    public void startLoginWithGooglePlus() {
        showProgressBar();
        presenter.startLoginIntent(this);
    }

    @OnClick(R.id.login_with_email_and_password_button)
    public void startLoginWithEmailAndPassword() {
        showLoginView();
    }

    @OnClick(R.id.cancel_login_with_email)
    public void cancelLoginWithEmailAndPassword() {
        hideLoginView();
    }

    @OnClick(R.id.start_login_with_email_and_password)
    public void startLoginProcess() {
        if (TextUtils.isEmpty(emailText.getText().toString()) ||
                TextUtils.isEmpty(passwordText.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.empty_field_error), Toast.LENGTH_SHORT).show();
        } else {
            showProgressBar();
            presenter.startLoginProcessWithEmailandPassword(emailText.getText().toString(), passwordText.getText().toString());
        }
    }

    @Override
    public void showProgressBar() {
        isLoggingIn = true;
        loadingIndicator.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        googlePlusLoginButton.setVisibility(View.GONE);
        loginWithEmailandPassword.setVisibility(View.GONE);
        loginLayoutHolder.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        isLoggingIn = false;
        loadingIndicator.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        googlePlusLoginButton.setVisibility(View.VISIBLE);
        loginWithEmailandPassword.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getAppContext() {
        return getContext();
    }

    @Override
    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    @Override
    public void showLoginView() {
        isEmailPageOpen = true;
        loadingIndicator.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        googlePlusLoginButton.setVisibility(View.GONE);
        loginWithEmailandPassword.setVisibility(View.GONE);
        loginLayoutHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoginView() {
        isEmailPageOpen = false;
        loadingIndicator.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        googlePlusLoginButton.setVisibility(View.VISIBLE);
        loginWithEmailandPassword.setVisibility(View.VISIBLE);
        loginLayoutHolder.setVisibility(View.GONE);
        emailText.setText("");
        passwordText.setText("");
    }
}
