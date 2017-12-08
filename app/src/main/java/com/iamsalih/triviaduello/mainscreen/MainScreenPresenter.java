package com.iamsalih.triviaduello.mainscreen;

import android.content.Intent;
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
import com.iamsalih.triviaduello.leaderboard.LeaderboardActivity;
import com.iamsalih.triviaduello.service.ReminderJobService;
import com.iamsalih.triviaduello.TriviaDuelloApplication;
import com.iamsalih.triviaduello.mainscreen.data.api.TriviaCall;
import com.iamsalih.triviaduello.mainscreen.data.model.Game;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;
import com.iamsalih.triviaduello.question.QuestionsActivity;

import java.util.UUID;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

public class MainScreenPresenter {

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

        triviaCall.getQuestions(10, "multiple").enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                if (TextUtils.isEmpty(firstPlayer) || TextUtils.isEmpty(secondPlayer)) {
                    view.startGameView(response.body());
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
                .setTag("reminder-job")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(0, 259200))
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public void startDuelProcess() {

        view.showProgressBar();

        final DatabaseReference databaseGameReference = database.getReference("Open Games");
        databaseReference = database.getReference("Games");

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
                        view.startGameView(currentGame.getQuestionList());
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

    public void startGameView(QuestionList questionList) {
        Intent intent = new Intent(view.getAppContext(), QuestionsActivity.class);
        intent.putExtra("list", questionList);
        intent.putExtra("gameID", currentGame == null ? "" : currentGame.getGameId());
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
}
