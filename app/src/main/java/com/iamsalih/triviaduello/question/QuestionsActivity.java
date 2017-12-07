package com.iamsalih.triviaduello.question;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.data.model.Game;
import com.iamsalih.triviaduello.mainscreen.data.model.Question;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;

import java.util.ArrayList;
import java.util.Collections;
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

    private QuestionList questionList;
    private CountDownTimer countDownTimer;
    private Question currentQuestion;
    private List<Question> wrongQuestions = new ArrayList<>();
    private Game currentGame;
    private String gameID;
    private DatabaseReference databaseReference;
    private List<Question> questions = new ArrayList<>();
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity_view);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            questionList = getIntent().getParcelableExtra("list");
            gameID = getIntent().getStringExtra("gameID");
            questions.addAll(questionList.getQuestionList());
            generateQuestionView();
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Games/"+gameID+"/questionList");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                generateQuestionView();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(childEventListener);
        questions.clear();
        countDownTimer.cancel();
    }

    private void generateQuestionView() {

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<Question> qList = questionList.getQuestionList();
        int position = getPositionOfCurrentQuestion(qList);
        qList.remove(position);
        currentQuestion.setAnsweredBy(user.getUid());
        qList.add(position, currentQuestion);
        questionList.setQuestionList(qList);
        databaseReference.setValue(questionList);
    }

    private int getPositionOfCurrentQuestion(List<Question> questions) {

        for (int i = 0 ; i < questions.size() ; i++) {
            if (questions.get(i).getQuestion().equals(currentQuestion.getQuestion())) {
                return i;
            }
        }
        return -1;
    }

    private void checkCorrectAnswer(String answer) {
        String correctAnswers = currentQuestion.getCorrectAnswer();

        if (!answer.equals(correctAnswers)) {
            wrongQuestions.add(currentQuestion);
        }
    }
}
