package com.iamsalih.triviaduello.question;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.data.model.Question;
import com.iamsalih.triviaduello.mainscreen.data.model.Result;

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
            Question question = questions.remove(0);

            questionText.setText(Html.fromHtml(question.getQuestion()));
            firstOptionText.setText(Html.fromHtml(question.getCorrectAnswer()));
            secondOptionText.setText(Html.fromHtml(question.getIncorrectOptions().get(0)));
            thirdOptionText.setText(Html.fromHtml(question.getIncorrectOptions().get(1)));
            fourthOptionText.setText(Html.fromHtml(question.getIncorrectOptions().get(2)));

            if (countDownTimer != null) {
                countDownTimer.cancel();
            } else {
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
            countDownTimer.start();
        } else {
            finish();
        }
    }

    @OnClick({R.id.first_option_holder, R.id.second_option_holder, R.id.third_option_holder, R.id.fourth_option_holder})
    public void onOptionClick() {
        generateQuestionView();
    }
}
