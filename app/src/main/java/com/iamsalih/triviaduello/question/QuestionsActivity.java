package com.iamsalih.triviaduello.question;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by muhammedsalihguler on 26.11.17.
 */

public class QuestionsActivity extends AppCompatActivity implements QuestionsView {

    @BindView(R.id.question_text)
    TextView questionText;

    @BindView(R.id.first_option_text)
    TextView firstOptionText;

    @BindView(R.id.second_option_text)
    TextView secondOptionText;

    @BindView(R.id.third_option_text)
    TextView thirdOptionText;

    @BindView(R.id.fourth_option_text)
    TextView fourthOptionText;

    @BindView(R.id.timer_holder)
    TextView timerText;

    private QuestionsPresenter presenter;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity_view);
        ButterKnife.bind(this);
        presenter = new QuestionsPresenter(this);
        if (savedInstanceState == null) {
            presenter.initValuesFromIntent(getIntent());
        } else {
            presenter.initValuesFromSavedInstanceState(savedInstanceState);
        }
        presenter.addDatabaseListener();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.FIREBASE_KEY_QUESTION_SCREEN, getString(R.string.firebase_question_screen_message));
        firebaseAnalytics.logEvent(AppConstants.FIREBASE_LOG_KEY_APP_NAME, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.invalidateValues();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(presenter.saveInstanceState(outState));
    }

    @Override
    public void createResultScreen(int point) {

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.FIREBASE_KEY_QUESTION_SCREEN, getString(R.string.firebase_question_screen_result_message));
        firebaseAnalytics.logEvent(AppConstants.FIREBASE_LOG_KEY_APP_NAME, bundle);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(String.format(getString(R.string.dialog_result_title), String.valueOf(point)))
                .setMessage(presenter.generateCorrectAnswers())
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .create();

        alertDialog.show();
    }


    @OnClick({R.id.first_option_text, R.id.second_option_text, R.id.third_option_text, R.id.fourth_option_text})
    public void onOptionClick(TextView view) {
        presenter.checkCorrectAnswer(view.getText().toString());
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

    @Override
    public void showQuestionView(String question, String firstOption, String secondOption, String thirdOption, String fourthOption) {
        questionText.setText(question);
        firstOptionText.setText(firstOption);
        secondOptionText.setText(secondOption);
        thirdOptionText.setText(thirdOption);
        fourthOptionText.setText(fourthOption);
        presenter.initCountDownTimer();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.leaving_game_title))
                .setMessage(getString(R.string.leaving_game_message))
                .setPositiveButton(getString(R.string.dialog_continue_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.removeListeners();
                        QuestionsActivity.super.onBackPressed();
                    }
                }).setNegativeButton(getString(R.string.dialog_cancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void setTimerText(String timer) {
        timerText.setText(timer);
    }
}
