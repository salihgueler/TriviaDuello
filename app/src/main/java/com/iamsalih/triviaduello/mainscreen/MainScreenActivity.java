package com.iamsalih.triviaduello.mainscreen;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.Utils;
import com.iamsalih.triviaduello.data.database.QuestionDbHelper;
import com.iamsalih.triviaduello.data.model.Question;
import com.iamsalih.triviaduello.data.model.QuestionList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_CATEGORY;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_CORRECT_ANSWER;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_DIFFICULTY;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_TEXT;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_WRONG_ANSWER;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.TABLE_NAME;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry._ID;
import static com.iamsalih.triviaduello.data.database.QuestionProvider.PROVIDER_NAME;

/**
 * Created by muhammedsalihguler on 25.11.17.
 */

public class MainScreenActivity extends AppCompatActivity implements MainScreenView {


    @BindView(R.id.practice_mode_button)
    Button practiceModeButton;

    @BindView(R.id.duel_mode_button)
    Button duelModeButton;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @BindView(R.id.cancel_button)
    Button cancelButton;

    @BindView(R.id.adView)
    AdView adView;

    private MainScreenPresenter presenter;
    private FirebaseAnalytics firebaseAnalytics;
    private boolean isSearchingGame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);
        ButterKnife.bind(this);
        presenter = new MainScreenPresenter(this);
        presenter.resetJobDispatcher();
        MobileAds.initialize(this, getString(R.string.admob_id));
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (savedInstanceState != null) {
            isSearchingGame = savedInstanceState.getBoolean(AppConstants.IS_SEARCHING_GAME);
            if (isSearchingGame) {
                showProgressBar();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AppConstants.IS_SEARCHING_GAME, isSearchingGame);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.leaderboard_button:
                presenter.startLeaderboardScreen();
                break;
            case R.id.settings_button:
                presenter.startSettingsScreen();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.practice_mode_button)
    public void startPracticeMode() {
        if (isEmpty()) {
            if (Utils.isNetworkAvailable(this)) {
                presenter.getQuestions(null, null);
            } else {
                Toast.makeText(this, getString(R.string.connectivity_problem), Toast.LENGTH_SHORT).show();
            }
        } else {
            presenter.readPracticeQuestions();
        }
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.FIREBASE_KEY_MAIN, getString(R.string.firebase_main_message));
        firebaseAnalytics.logEvent(AppConstants.FIREBASE_LOG_KEY_APP_NAME, bundle);
    }

    public boolean isEmpty() {

        QuestionDbHelper helper = new QuestionDbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database, TABLE_NAME);

        if (NoOfRows == 0) {
            return true;
        } else {
            return false;
        }
    }

    @OnClick(R.id.duel_mode_button)
    public void startDuelMode() {
        if (Utils.isNetworkAvailable(this)) {
            presenter.startDuelProcess();
        } else {
            Toast.makeText(this, getString(R.string.connectivity_problem), Toast.LENGTH_SHORT).show();
        }
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.FIREBASE_KEY_MAIN, getString(R.string.firebase_main_duel_message));
        firebaseAnalytics.logEvent(AppConstants.FIREBASE_LOG_KEY_APP_NAME, bundle);
    }

    @OnClick(R.id.cancel_button)
    public void cancelSearch() {
        presenter.cancelDuelMode();
    }

    @Override
    public void startDuelGame(QuestionList questionList) {
        startGameView(questionList, true);
    }

    @Override
    public void showProgressBar() {
        isSearchingGame = true;
        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        isSearchingGame = false;
        practiceModeButton.setVisibility(View.VISIBLE);
        duelModeButton.setVisibility(View.VISIBLE);
        adView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public void startGameView(QuestionList questionList, boolean isDuelMode) {
        presenter.startGameView(questionList, isDuelMode);
    }
}
