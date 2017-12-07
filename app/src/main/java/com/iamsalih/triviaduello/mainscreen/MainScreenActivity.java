package com.iamsalih.triviaduello.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.data.model.Game;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;
import com.iamsalih.triviaduello.question.QuestionsActivity;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private MainScreenPresenter presenter;
    private Game currentGame;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);
        ButterKnife.bind(this);
        presenter = new MainScreenPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @OnClick(R.id.practice_mode_button)
    public void startPracticeMode() {
        presenter.getQuestions(null, null);
    }

    @OnClick(R.id.duel_mode_button)
    public void startDuelMode() {

        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                        presenter.getQuestions(dataSnapshot.getKey(), user.getUid());
                        databaseGameReference.removeEventListener(this);
                        databaseGameReference.child(dataSnapshot.getKey()).removeValue();
                        databaseGameReference.child(user.getUid()).removeValue();
                    }
                    hideProgressBar();
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
            public void onCancelled(DatabaseError databaseError) {}
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
                        startGameView(currentGame.getQuestionList());
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

    @Override
    public void startDuelGame(String key, String uid, QuestionList questionList) {
        String game_id = UUID.randomUUID().toString();
        currentGame = new Game();
        currentGame.setGameId(game_id);
        currentGame.setFirstPlayer(key);
        currentGame.setSecondPlayer(uid);
        currentGame.setQuestionList(questionList);
        currentGame.setActiveGame(false);
        databaseReference.child(game_id).setValue(currentGame);
        startGameView(questionList);
    }

    @Override
    public void showProgressBar() {
        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        practiceModeButton.setVisibility(View.VISIBLE);
        duelModeButton.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void startGameView(QuestionList questionList) {
        Intent intent = new Intent(MainScreenActivity.this, QuestionsActivity.class);
        intent.putExtra("list", questionList);
        intent.putExtra("gameID", currentGame == null ? "" : currentGame.getGameId());
        startActivity(intent);
        currentGame = null;
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
        }
        childEventListener = null;
    }
}
