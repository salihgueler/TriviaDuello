package com.iamsalih.triviaduello.mainscreen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.api.TriviaCall;
import com.iamsalih.triviaduello.data.model.Question;
import com.iamsalih.triviaduello.leaderboard.LeaderboardActivity;
import com.iamsalih.triviaduello.service.ReminderJobService;
import com.iamsalih.triviaduello.TriviaDuelloApplication;
import com.iamsalih.triviaduello.data.model.Game;
import com.iamsalih.triviaduello.data.model.QuestionList;
import com.iamsalih.triviaduello.question.QuestionsActivity;
import com.iamsalih.triviaduello.settings.SettingsActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_CATEGORY;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_CORRECT_ANSWER;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_DIFFICULTY;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_TEXT;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_WRONG_ANSWER;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry._ID;
import static com.iamsalih.triviaduello.data.database.QuestionProvider.PROVIDER_NAME;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

public class MainScreenPresenter {

    private static final String QUESTION_TYPE = "multiple";
    private static final int AMOUNT_OF_QUESTIONS = 10;
    private static final int THREE_DAYS_WINDOW_START = 259200;
    private static final int THREE_DAYS_WINDOW_END = 518400;
    private static final String CATEGORY = "category";

    @Inject
    TriviaCall triviaCall;

    @Inject
    FirebaseDatabase database;

    @Inject
    FirebaseUser user;

    private MainScreenView view;
    private ChildEventListener childEventListener;
    private Game currentGame;
    private DatabaseReference databaseReference;

    public MainScreenPresenter(MainScreenView view) {
        this.view = view;
        TriviaDuelloApplication.networkComponent.inject(this);
    }

    public void getQuestions(@Nullable final String firstPlayer, @Nullable final String secondPlayer) {

        view.showProgressBar();

        triviaCall.getQuestions(AMOUNT_OF_QUESTIONS, QUESTION_TYPE, getCategories()).enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {

                saveValuesToDatabase(response.body());
                if (TextUtils.isEmpty(firstPlayer) || TextUtils.isEmpty(secondPlayer)) {
                    view.startGameView(response.body(), false);
                } else {
                    assignCurrentGame(firstPlayer, secondPlayer, response.body());
                    view.startDuelGame(response.body());
                }
                view.hideProgressBar();
            }

            @Override
            public void onFailure(Call<QuestionList> call, Throwable t) {
                t.printStackTrace();
                view.hideProgressBar();
            }
        });
    }

    private void saveValuesToDatabase(QuestionList questionList) {

        for (Question question : questionList.getQuestionList()) {
            ContentValues values = new ContentValues();
            values.put(QUESTION_TEXT, question.getQuestion());
            values.put(QUESTION_CATEGORY, question.getCategory());
            values.put(QUESTION_CORRECT_ANSWER, question.getCorrectAnswer());
            values.put(QUESTION_DIFFICULTY, question.getDifficulty());
            String wrongAnswers = new Gson().toJson(question.getIncorrectOptions());
            values.put(QUESTION_WRONG_ANSWER, wrongAnswers);
            view.getAppContext().getContentResolver().insert(Uri.parse("content://" + PROVIDER_NAME), values);
        }
    }

    private Map<String, String> getCategories() {
        Map<String, String> categories = new HashMap<>();
        SharedPreferences preferences = view.getAppContext().getSharedPreferences(AppConstants.APP_PREFERENCE_KEY, Context.MODE_PRIVATE);
        boolean[] savedCategory = new Gson().fromJson(preferences.getString(AppConstants.PREFERENCE_CATEGORY_KEY, null), boolean[].class);
        if (savedCategory != null) {
            if (savedCategory[0] == false) {
                arrangeCategories(categories, savedCategory);
            }
        }
        return categories;
    }

    private void arrangeCategories(Map<String, String> categories, boolean[] savedCategory) {
        for (int i = 1; i < savedCategory.length; i++) {
            if (savedCategory[i] == false) {
                continue;
            }
            switch (i) {
                case 1:
                    categories.put(CATEGORY, "9");
                    break;
                case 2:
                    categories.put(CATEGORY, "10");
                    categories.put(CATEGORY, "11");
                    categories.put(CATEGORY, "12");
                    categories.put(CATEGORY, "13");
                    categories.put(CATEGORY, "14");
                    categories.put(CATEGORY, "15");
                    categories.put(CATEGORY, "16");
                    categories.put(CATEGORY, "26");
                    categories.put(CATEGORY, "29");
                    categories.put(CATEGORY, "31");
                    categories.put(CATEGORY, "32");
                    break;
                case 3:
                    categories.put(CATEGORY, "17");
                    categories.put(CATEGORY, "18");
                    categories.put(CATEGORY, "19");
                    categories.put(CATEGORY, "30");
                    break;
                case 4:
                    categories.put(CATEGORY, "21");
                    break;
                case 5:
                    categories.put(CATEGORY, "23");
                    break;
                case 6:
                    categories.put(CATEGORY, "25");
                    break;
            }
        }
    }

    private void assignCurrentGame(String firstPlayer, String secondPlayer, QuestionList questionList) {
        String game_id = UUID.randomUUID().toString();
        currentGame = new Game();
        currentGame.setGameId(game_id);
        currentGame.setFirstPlayer(firstPlayer);
        currentGame.setSecondPlayer(secondPlayer);
        currentGame.setQuestionList(questionList);
        currentGame.setActiveGame(false);
        databaseReference.child(game_id).setValue(currentGame);
    }

    public void resetJobDispatcher() {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(view.getAppContext()));
        dispatcher.cancelAll();
        Job myJob = dispatcher.newJobBuilder()
                .setService(ReminderJobService.class)
                .setTag(AppConstants.REMINDER_JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(THREE_DAYS_WINDOW_START, THREE_DAYS_WINDOW_END))
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public void startDuelProcess() {

        view.showProgressBar();

        final DatabaseReference databaseGameReference = database.getReference(AppConstants.OPEN_GAMES_KEY);
        databaseReference = database.getReference(AppConstants.ALL_GAMES_KEY);

        if (childEventListener == null) {
            initializeEventListenerForGames();
        }
        databaseReference.addChildEventListener(childEventListener);

        databaseGameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String newUserId) {
                if (!user.getUid().equals(dataSnapshot.getKey())) {
                    if (newUserId != null && !newUserId.equals(databaseGameReference.getKey())) {
                        getQuestions(dataSnapshot.getKey(), user.getUid());
                        databaseGameReference.removeEventListener(this);
                        databaseGameReference.child(dataSnapshot.getKey()).removeValue();
                        databaseGameReference.child(user.getUid()).removeValue();
                    }
                    view.hideProgressBar();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        });
        databaseGameReference.child(user.getUid()).setValue(true);
    }

    private void initializeEventListenerForGames() {

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (currentGame == null) {
                    currentGame = dataSnapshot.getValue(Game.class);
                    if (!currentGame.isActiveGame()) {
                        currentGame.setActiveGame(true);
                        databaseReference.child(currentGame.getGameId()).setValue(currentGame);
                        view.startGameView(currentGame.getQuestionList(), true);
                    } else {
                        currentGame = null;
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    }

    public void startGameView(QuestionList questionList, boolean isDuelMode) {
        Intent intent = new Intent(view.getAppContext(), QuestionsActivity.class);
        intent.putExtra(AppConstants.QUESTION_LIST_INTENT_KEY, questionList);
        intent.putExtra(AppConstants.GAMEID_INTENT_KEY, currentGame == null ? "" : currentGame.getGameId());
        intent.putExtra(AppConstants.DUEL_MODE_INTENT_KEY, isDuelMode);
        view.getAppContext().startActivity(intent);
        currentGame = null;
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
        }
        childEventListener = null;
    }

    public void startLeaderboardScreen() {
        Intent intent = new Intent(view.getAppContext(), LeaderboardActivity.class);
        view.getAppContext().startActivity(intent);
    }

    public void startSettingsScreen() {
        Intent intent = new Intent(view.getAppContext(), SettingsActivity.class);
        view.getAppContext().startActivity(intent);
    }

    public void cancelDuelMode() {
        final DatabaseReference databaseGameReference = database.getReference(AppConstants.OPEN_GAMES_KEY);
        databaseGameReference.child(user.getUid()).removeValue();
        view.hideProgressBar();
    }

    public void readPracticeQuestions() {

        view.showProgressBar();
        List<Question> questions = new ArrayList<>();

        String[] projection = {
                _ID,
                QUESTION_TEXT,
                QUESTION_WRONG_ANSWER,
                QUESTION_CATEGORY,
                QUESTION_CORRECT_ANSWER,
                QUESTION_DIFFICULTY
        };

        Cursor cursor = view.getAppContext().getContentResolver().query(Uri.parse("content://" + PROVIDER_NAME),
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
        view.hideProgressBar();
    }
}
