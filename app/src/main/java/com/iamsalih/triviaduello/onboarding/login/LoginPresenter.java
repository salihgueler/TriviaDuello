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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.MainScreenActivity;

import timber.log.Timber;

/**
 * Presenter for login operations.
 */

public class LoginPresenter {

    private LoginView loginView;

    private GoogleSignInClient mGoogleSignInClient;

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
                    Intent intent = new Intent(loginView.getAppContext(), MainScreenActivity.class);
                    loginView.getAppContext().startActivity(intent);
                    ((Activity) loginView.getAppContext()).finish();
                } else {
                    Toast.makeText(loginView.getAppContext(), loginView.getAppContext().getString(R.string.login_failed_message), Toast.LENGTH_SHORT).show();
                    loginView.hideProgressBar();
                }
            }
        });
    }

    public void initGoogleSingInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(loginView.getAppContext().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(loginView.getAppContext(), gso);
    }

    public void signInWithGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            logInWithCredential(credential);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Timber.w("Google sign in failed\n" + e);
            loginView.hideProgressBar();
        }
    }

    public void startLoginProcessWithEmailandPassword(final String email, final String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(loginView.getAppContext(), MainScreenActivity.class);
                    loginView.getAppContext().startActivity(intent);
                    ((Activity) loginView.getAppContext()).finish();
                } else {
                    Timber.w("createUserWithEmail:failure\n" + task.getException());
                    if (task.getException().toString().contains("FirebaseAuthUserCollisionException")) {
                        loginWithEmailAndPassword(email, password);
                    } else {
                        Toast.makeText(loginView.getAppContext(), loginView.getAppContext().getString(R.string.login_failed_message), Toast.LENGTH_SHORT).show();
                        loginView.hideProgressBar();
                    }
                }
            }
        });
    }

    private void loginWithEmailAndPassword(final String email, final String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(loginView.getAppContext(), MainScreenActivity.class);
                    loginView.getAppContext().startActivity(intent);
                    ((Activity) loginView.getAppContext()).finish();
                } else {
                    Toast.makeText(loginView.getAppContext(), loginView.getAppContext().getString(R.string.login_failed_message), Toast.LENGTH_SHORT).show();
                    loginView.hideProgressBar();
                }
            }
        });
    }

    public void startLoginIntent(LoginFragment loginFragment) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        loginFragment.startActivityForResult(signInIntent, AppConstants.RC_SIGN_IN);
    }
}
