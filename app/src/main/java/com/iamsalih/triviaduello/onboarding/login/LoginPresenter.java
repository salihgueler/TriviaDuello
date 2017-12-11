package com.iamsalih.triviaduello.onboarding.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.iamsalih.triviaduello.mainscreen.MainScreenActivity;

import timber.log.Timber;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class LoginPresenter {

    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {

        this.loginView = loginView;
    }

    public void registerAuthenticationCallback() {

        // Callback registration
        LoginManager.getInstance().registerCallback(loginView.getCallbackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                signInWithFacebookLoginResultToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                loginView.hideProgressBar();
                Timber.v("onCancel()");
            }

            @Override
            public void onError(FacebookException exception) {
                loginView.hideProgressBar();
                Timber.e("onError(): " + exception.toString());
            }
        });

    }

    private void signInWithFacebookLoginResultToken(AccessToken accessToken) {

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        logInWithCredential(credential);
    }

    public void logInWithCredential(AuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(loginView.getAppContext(), "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(loginView.getAppContext(), MainScreenActivity.class);
                    loginView.getAppContext().startActivity(intent);
                    ((Activity) loginView.getAppContext()).finish();
                } else {
                    Toast.makeText(loginView.getAppContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
                    loginView.hideProgressBar();
                }
            }
        });
    }

    public void loginAnonymously() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(loginView.getAppContext(), "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(loginView.getAppContext(), MainScreenActivity.class);
                    loginView.getAppContext().startActivity(intent);
                    ((Activity) loginView.getAppContext()).finish();
                } else {
                    Toast.makeText(loginView.getAppContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
                    loginView.hideProgressBar();
                }
            }
        });
    }
}
