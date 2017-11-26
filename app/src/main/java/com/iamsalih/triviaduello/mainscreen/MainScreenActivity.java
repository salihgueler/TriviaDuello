package com.iamsalih.triviaduello.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.data.api.TriviaCall;
import com.iamsalih.triviaduello.mainscreen.data.model.Result;
import com.iamsalih.triviaduello.question.QuestionsActivity;

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
        triviaCall.getQuestions(10, "multiple").enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                practiceModeButton.setVisibility(View.VISIBLE);
                duelModeButton.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
                Intent intent = new Intent(MainScreenActivity.this, QuestionsActivity.class);
                intent.putExtra("list", response.body());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
                practiceModeButton.setVisibility(View.VISIBLE);
                duelModeButton.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }
}
