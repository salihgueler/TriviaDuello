package com.iamsalih.triviaduello.question;

import android.content.Intent;
import android.os.Bundle;
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
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.TriviaDuelloApplication;
import com.iamsalih.triviaduello.data.model.LeaderBoardItem;
import com.iamsalih.triviaduello.data.model.Question;
import com.iamsalih.triviaduello.data.model.QuestionList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class QuestionsPresenter {

    private static final int TIMER_START_VALUE = 31000;
    private static final int TIMER_GAP_VALUE = 1000;
    private static final int SECOND_TO_MILISECOND = 1000;
    @Inject
    FirebaseDatabase firebaseDatabase;

    @Inject
    FirebaseUser user;

    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private String gameID;
    private Question currentQuestion;
    private List<Question> questions;
    private QuestionsView questionsView;
    private List<Question> wrongQuestions;
    private CountDownTimer countDownTimer;
    private QuestionList questionList;
    private boolean isDuelMode;

    public QuestionsPresenter(QuestionsView questionsView) {

        this.questionsView = questionsView;
        questions = new ArrayList<>();
        wrongQuestions = new ArrayList<>();
        TriviaDuelloApplication.firebaseComponent.inject(this);
    }

    public void addDatabaseListener() {
        databaseReference = firebaseDatabase.getReference(String.format(AppConstants.QUESTION_LIST_KEY, gameID));

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
        for (Question question : questionList.getQuestionList()) {
            if (questionAnsweredCorrectly(question)) {
                if (question.getDifficulty().trim().equalsIgnoreCase(AppConstants.QUESTION_DIFFICULTY_EASY)) {
                    point += 1;
                } else if (question.getDifficulty().trim().equalsIgnoreCase(AppConstants.QUESTION_DIFFICULTY_MEDIUM)) {
                    point += 2;
                } else if (question.getDifficulty().trim().equalsIgnoreCase(AppConstants.QUESTION_DIFFICULTY_HARD)) {
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
                for (Question question : questionList.getQuestionList()) {
                    if (question.getAnsweredBy().trim().equalsIgnoreCase(userID)) {
                        if (questionAnsweredCorrectly(question)) {
                            if (question.getDifficulty().trim().equalsIgnoreCase(AppConstants.QUESTION_DIFFICULTY_EASY)) {
                                point += 1;
                            } else if (question.getDifficulty().trim().equalsIgnoreCase(AppConstants.QUESTION_DIFFICULTY_MEDIUM)) {
                                point += 2;
                            } else if (question.getDifficulty().trim().equalsIgnoreCase(AppConstants.QUESTION_DIFFICULTY_HARD)) {
                                point += 3;
                            }
                        }
                    } else {
                        wrongQuestions.add(question);
                    }
                }
                final DatabaseReference leaderBoard = FirebaseDatabase.getInstance().getReference("leaderBoard/" + userID);
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
            answers += String.format(questionsView.getAppContext().getString(R.string.wrong_questions_explanation), Html.fromHtml(question.getQuestion()), Html.fromHtml(question.getCorrectAnswer()));
            ;
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

        for (int i = 0; i < questions.size(); i++) {
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
            countDownTimer = new CountDownTimer(TIMER_START_VALUE, TIMER_GAP_VALUE) {
                @Override
                public void onTick(long l) {
                    int timer = (int) l / SECOND_TO_MILISECOND;
                    questionsView.setTimerText(String.format(questionsView.getAppContext().getString(R.string.timer_text), String.valueOf(timer)));
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
        questionList = intent.getParcelableExtra(AppConstants.QUESTION_LIST_INTENT_KEY);
        gameID = intent.getStringExtra(AppConstants.GAMEID_INTENT_KEY);
        isDuelMode = intent.getBooleanExtra(AppConstants.DUEL_MODE_INTENT_KEY, false);
        questions.addAll(questionList.getQuestionList());
        generateQuestionView();
    }

    public void invalidateValues() {
        databaseReference.removeEventListener(childEventListener);
        questions.clear();
        countDownTimer.cancel();
    }

    public void initValuesFromSavedInstanceState(Bundle savedInstanceState) {
        questionList = savedInstanceState.getParcelable(AppConstants.QUESTION_LIST_SAVE_INSTANCE);
        gameID = savedInstanceState.getString(AppConstants.GAMEID_SAVE_INSTANCE);
        isDuelMode = savedInstanceState.getBoolean(AppConstants.DUEL_MODE_SAVE_INSTANCE);
        currentQuestion = savedInstanceState.getParcelable(AppConstants.CURRENT_QUESTION_SAVE_INSTANCE);
        List<Question> tempQuestion = savedInstanceState.getParcelableArrayList(AppConstants.REST_OF_THE_QUESTIONS_SAVE_INSTANCE);
        questions.addAll(tempQuestion);
        questions.add(0, currentQuestion);
        tempQuestion = savedInstanceState.getParcelableArrayList(AppConstants.WRONG_QUESTIONS_SAVE_INSTANCE);
        wrongQuestions.addAll(tempQuestion);
        generateQuestionView();
    }

    public Bundle saveInstanceState(Bundle outState) {
        outState.putParcelable(AppConstants.QUESTION_LIST_SAVE_INSTANCE, questionList);
        // Temp array to pass questions
        List<Question> tempArray = new ArrayList<>();
        tempArray.addAll(questions);

        outState.putParcelableArrayList(AppConstants.REST_OF_THE_QUESTIONS_SAVE_INSTANCE, (ArrayList) tempArray);
        outState.putParcelableArrayList(AppConstants.WRONG_QUESTIONS_SAVE_INSTANCE, (ArrayList) wrongQuestions);
        outState.putString(AppConstants.GAMEID_SAVE_INSTANCE, gameID);
        outState.putBoolean(AppConstants.DUEL_MODE_SAVE_INSTANCE, isDuelMode);
        outState.putParcelable(AppConstants.CURRENT_QUESTION_SAVE_INSTANCE, currentQuestion);
        return outState;
    }

    public void removeListeners() {
        databaseReference.removeEventListener(childEventListener);
    }
}
