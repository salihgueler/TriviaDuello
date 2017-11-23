package com.iamsalih.triviaduello.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.iamsalih.triviaduello.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by muhammedsalihguler on 23.11.17.
 */

public class LoginFragment extends Fragment {

    @BindView(R.id.login_button)
    Button loginButton;

    private CallbackManager callbackManager;

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

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Timber.v("onSuccess(): " + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Timber.v("onCancel()");
            }

            @Override
            public void onError(FacebookException exception) {
                Timber.e("onError(): " + exception.toString());
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.login_button)
    public void startLoginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }
}
