package com.iamsalih.triviaduello.question;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.data.model.Question;
import com.iamsalih.triviaduello.mainscreen.data.model.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by muhammedsalihguler on 26.11.17.
 */

public class QuestionsActivity extends AppCompatActivity {

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

    private Result result;
    private CountDownTimer countDownTimer;
    private Question currentQuestion;
    private List<Question> wrongQuestions = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity_view);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            result = getIntent().getParcelableExtra("list");
            generateQuestionView();
        }

    }

    private void generateQuestionView() {
        List<Question> questions = result.getQuestionList();

        if (questions.size() > 0) {
            currentQuestion = questions.remove(0);

            questionText.setText(Html.fromHtml(currentQuestion.getQuestion()));
            List<String> options = currentQuestion.getIncorrectOptions();
            options.add(currentQuestion.getCorrectAnswer());
            Collections.shuffle(options);

            firstOptionText.setText(Html.fromHtml(options.get(0)));
            secondOptionText.setText(Html.fromHtml(options.get(1)));
            thirdOptionText.setText(Html.fromHtml(options.get(2)));
            fourthOptionText.setText(Html.fromHtml(options.get(3)));

            if (countDownTimer != null) {
                countDownTimer.cancel();
            } else {
                initCountDownTimer();
            }
            countDownTimer.start();
        } else {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            createResultScreen();
        }
    }

    private void createResultScreen() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("You have " + wrongQuestions.size() + " wrong answers.")
                .setMessage(generateCorrectAnswers())
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

    private String generateCorrectAnswers() {
        String answers = "";

        for (Question question : wrongQuestions) {
            answers += "Answer for question:\n" + Html.fromHtml(question.getQuestion()) + "\n-> " + Html.fromHtml(question.getCorrectAnswer()) + ".\n\n";
        }
        return answers;
    }

    private void initCountDownTimer() {
        countDownTimer = new CountDownTimer(31000, 1000) {
            @Override
            public void onTick(long l) {
                int timer = (int) l / 1000;
                timerText.setText(String.valueOf(timer) + "s");
            }

            @Override
            public void onFinish() {
                generateQuestionView();
            }
        };
    }

    @OnClick({R.id.first_option_text, R.id.second_option_text, R.id.third_option_text, R.id.fourth_option_text})
    public void onOptionClick(TextView view) {
        checkCorrectAnswer(view.getText().toString());
        generateQuestionView();
    }

    private void checkCorrectAnswer(String answer) {
        String correctAnswers = currentQuestion.getCorrectAnswer();

        if (!answer.equals(correctAnswers)) {
            wrongQuestions.add(currentQuestion);
        }
    }
}
