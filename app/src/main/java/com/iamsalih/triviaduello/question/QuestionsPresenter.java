package com.iamsalih.triviaduello.question;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iamsalih.triviaduello.TriviaDuelloApplication;
import com.iamsalih.triviaduello.leaderboard.data.model.LeaderBoardItem;
import com.iamsalih.triviaduello.mainscreen.data.model.Question;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class QuestionsPresenter {

    @Inject
    FirebaseDatabase firebaseDatabase;

    @Inject
    FirebaseUser user;

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private String gameID;
    private Question currentQuestion;
    private List<Question> questions = new ArrayList<>();
    private QuestionsView questionsView;
    private List<Question> wrongQuestions = new ArrayList<>();
    private CountDownTimer countDownTimer;
    private QuestionList questionList;
    private boolean isDuelMode;

    public QuestionsPresenter(QuestionsView questionsView) {

        this.questionsView = questionsView;
        TriviaDuelloApplication.firebaseComponent.inject(this);
    }

    public void addDatabaseListener() {
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

    public void generateQuestionView() {

        if (questions.size() > 0) {
            currentQuestion = questions.remove(0);

            List<String> options = currentQuestion.getIncorrectOptions();
            options.add(currentQuestion.getCorrectAnswer());
            Collections.shuffle(options);

            questionsView.showQuestionView(Html.fromHtml(currentQuestion.getQuestion()).toString(),
                    Html.fromHtml(options.get(0)).toString(),
                    Html.fromHtml(options.get(1)).toString(),
                    Html.fromHtml(options.get(2)).toString(),
                    Html.fromHtml(options.get(3)).toString());

        } else {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (isDuelMode) {
                calculateDuelPoint();
            } else {
                questionsView.createResultScreen(calculatePracticePoint());
            }
        }
    }

    private int calculatePracticePoint() {
        int point = 0;
        for (Question question: questionList.getQuestionList()) {
            if (questionAnsweredCorrectly(question)) {
                if (question.getDifficulty().trim().equalsIgnoreCase("easy")){
                    point += 1;
                } else if (question.getDifficulty().trim().equalsIgnoreCase("medium")){
                    point += 2;
                } else if (question.getDifficulty().trim().equalsIgnoreCase("hard")){
                    point += 3;
                }
            }
        }
        return point;
    }

    private void calculateDuelPoint() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                QuestionList questionList = dataSnapshot.getValue(QuestionList.class);
                int point = 0;
                for (Question question: questionList.getQuestionList()) {
                    if (question.getAnsweredBy().trim().equalsIgnoreCase(userID)) {
                        if (questionAnsweredCorrectly(question)) {
                            if (question.getDifficulty().trim().equalsIgnoreCase("easy")){
                                point += 1;
                            } else if (question.getDifficulty().trim().equalsIgnoreCase("medium")){
                                point += 2;
                            } else if (question.getDifficulty().trim().equalsIgnoreCase("hard")){
                                point += 3;
                            }
                        }
                    } else {
                        wrongQuestions.add(question);
                    }
                }
                final DatabaseReference leaderBoard = FirebaseDatabase.getInstance().getReference("leaderBoard/"+userID);
                final int finalPoint = point;
                leaderBoard.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LeaderBoardItem pointFromServer = dataSnapshot.getValue(LeaderBoardItem.class);

                        if (pointFromServer == null) {
                            pointFromServer = new LeaderBoardItem();
                            pointFromServer.setUserName(user.getDisplayName());
                            pointFromServer.setPoint(0);
                        }
                        int currentPoint = pointFromServer.getPoint();
                        currentPoint += finalPoint;
                        pointFromServer.setPoint(currentPoint);
                        leaderBoard.setValue(pointFromServer);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                questionsView.createResultScreen(point);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String generateCorrectAnswers() {
        String answers = "";

        for (Question question : wrongQuestions) {
            answers += "Answer for question:\n" + Html.fromHtml(question.getQuestion()) + "\n-> " + Html.fromHtml(question.getCorrectAnswer()) + ".\n\n";
        }
        return answers;
    }

    private boolean questionAnsweredCorrectly(Question question) {
        for (Question wrongQuestion : wrongQuestions) {
            if (wrongQuestion.getQuestion().trim().equalsIgnoreCase(question.getQuestion())) {
                return false;
            }
        }
        return true;
    }

    public void checkCorrectAnswer(String answer) {
        String correctAnswers = currentQuestion.getCorrectAnswer();

        if (!answer.equals(correctAnswers)) {
            wrongQuestions.add(currentQuestion);
        }

        if (isDuelMode) {
            updateAnsweredBy();
        } else {
            generateQuestionView();
        }
    }

    private void updateAnsweredBy() {

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

    public void initCountDownTimer() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        } else {
            countDownTimer = new CountDownTimer(31000, 1000) {
                @Override
                public void onTick(long l) {
                    int timer = (int) l / 1000;
                    questionsView.setTimerText(String.valueOf(timer) + "s");
                }

                @Override
                public void onFinish() {
                    generateQuestionView();
                }
            };
        }
        countDownTimer.start();
    }

    public void initValuesFromIntent(Intent intent) {
        if (intent.getExtras() == null) {
            return;
        }
        questionList = intent.getParcelableExtra("list");
        gameID = intent.getStringExtra("gameID");
        isDuelMode = intent.getBooleanExtra("isDuelMode", false);
        questions.addAll(questionList.getQuestionList());
        generateQuestionView();
    }

    public void invalidateValues() {
        databaseReference.removeEventListener(childEventListener);
        questions.clear();
        countDownTimer.cancel();
    }
}
