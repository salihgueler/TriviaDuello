package com.iamsalih.triviaduello.question;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity_view);
        ButterKnife.bind(this);
        presenter = new QuestionsPresenter(this);
        presenter.initValuesFromIntent(getIntent());
        presenter.addDatabaseListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.invalidateValues();
    }


    @Override
    public void createResultScreen(int point) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("You have " + point + " points.")
                .setMessage(presenter.generateCorrectAnswers())
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
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
    public void showProgressBar() {}

    @Override
    public void hideProgressBar() {}

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
    public void setTimerText(String timer) {
        timerText.setText(timer);
    }
}
