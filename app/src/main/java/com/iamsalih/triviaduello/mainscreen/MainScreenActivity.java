package com.iamsalih.triviaduello.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.iamsalih.triviaduello.mainscreen.data.api.TriviaCall;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;
import com.iamsalih.triviaduello.question.QuestionsActivity;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by muhammedsalihguler on 25.11.17.
 */

public class MainScreenActivity extends AppCompatActivity {


    @BindView(R.id.practice_mode_button)
    Button practiceModeButton;

    @BindView(R.id.duel_mode_button)
    Button duelModeButton;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @OnClick(R.id.practice_mode_button)
    public void startPracticeMode() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TriviaCall triviaCall = retrofit.create(TriviaCall.class);
        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        triviaCall.getQuestions(10, "multiple").enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                practiceModeButton.setVisibility(View.VISIBLE);
                duelModeButton.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
                Intent intent = new Intent(MainScreenActivity.this, QuestionsActivity.class);
                intent.putExtra("list", response.body());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<QuestionList> call, Throwable t) {
                t.printStackTrace();
                practiceModeButton.setVisibility(View.VISIBLE);
                duelModeButton.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.duel_mode_button)
    public void startDuelMode() {
        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference("Open Games");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String newUserId) {
                if (!user.getUid().equals(dataSnapshot.getKey())) {
                    if (newUserId != null && !newUserId.equals(databaseReference.getKey())) {
                        startDuelGame(dataSnapshot.getKey(), user.getUid());
                        databaseReference.removeEventListener(this);
                        databaseReference.child(dataSnapshot.getKey()).removeValue();
                        databaseReference.child(user.getUid()).removeValue();
                    }
                    practiceModeButton.setVisibility(View.VISIBLE);
                    duelModeButton.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
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
        databaseReference.child(user.getUid()).setValue(true);
    }

    private void startDuelGame(String key, String uid) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference("Games");
        String game_id = UUID.randomUUID().toString();
        databaseReference.child(game_id).setValue(true);
        databaseReference.child(game_id).child("player_1").setValue(key);
        databaseReference.child(game_id).child("player_2").setValue(uid);
    }
}
