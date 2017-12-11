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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iamsalih.triviaduello.R;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);
        ButterKnife.bind(this);
        presenter = new MainScreenPresenter(this);
        presenter.resetJobDispatcher();
        MobileAds.initialize(this, "ca-app-pub-9229514993517521~1418153022");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
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
            presenter.getQuestions(null, null);
        } else {
            readPracticeQuestions();
        }
        Bundle bundle = new Bundle();
        bundle.putString("main_screen", "click_start_practice_mode");
        firebaseAnalytics.logEvent("trivia_duello", bundle);
    }

    private void readPracticeQuestions() {

        showProgressBar();
        List<Question> questions = new ArrayList<>();

        String[] projection = {
                _ID,
                QUESTION_TEXT,
                QUESTION_WRONG_ANSWER,
                QUESTION_CATEGORY,
                QUESTION_CORRECT_ANSWER,
                QUESTION_DIFFICULTY
        };

        Cursor cursor = getContentResolver().query(Uri.parse("content://" + PROVIDER_NAME),
                projection,
                null,
                null,
                null,
                null);

        int questionTextPosition = cursor.getColumnIndex(QUESTION_TEXT);
        int questionWrongAnswer = cursor.getColumnIndex(QUESTION_WRONG_ANSWER);
        int questionCategory = cursor.getColumnIndex(QUESTION_CATEGORY);
        int questionCorrectAnswer = cursor.getColumnIndex(QUESTION_CORRECT_ANSWER);
        int questionDifficulty = cursor.getColumnIndex(QUESTION_DIFFICULTY);

        while(cursor.moveToNext()) {
            Question question = new Question();
            String questionText = cursor.getString(questionTextPosition);
            String wrongAnswerText = cursor.getString(questionWrongAnswer);
            String category = cursor.getString(questionCategory);
            String correctAnswer = cursor.getString(questionCorrectAnswer);
            String difficulty = cursor.getString(questionDifficulty);
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> wrongAnswers = new Gson().fromJson(wrongAnswerText, listType);
            question.setQuestion(questionText);
            question.setCorrectAnswer(correctAnswer);
            question.setDifficulty(difficulty);
            question.setCategory(category);
            question.setIncorrectOptions(wrongAnswers);
            questions.add(question);
        }

        QuestionList questionList = new QuestionList();
        questionList.setQuestionList(questions);
        startGameView(questionList, false);
        hideProgressBar();
    }

    public boolean isEmpty(){

        QuestionDbHelper helper = new QuestionDbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database, TABLE_NAME);

        if (NoOfRows == 0){
            return true;
        }else {
            return false;
        }
    }

    @OnClick(R.id.duel_mode_button)
    public void startDuelMode() {
        presenter.startDuelProcess();
        Bundle bundle = new Bundle();
        bundle.putString("main_screen", "click_start_duel_mode");
        firebaseAnalytics.logEvent("trivia_duello", bundle);
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
        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
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
