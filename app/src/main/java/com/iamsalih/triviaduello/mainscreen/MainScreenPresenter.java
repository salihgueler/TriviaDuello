package com.iamsalih.triviaduello.mainscreen;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.iamsalih.triviaduello.TriviaDuelloApplication;
import com.iamsalih.triviaduello.mainscreen.data.api.TriviaCall;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;
import com.iamsalih.triviaduello.question.QuestionsActivity;

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

    private MainScreenView view;


    public MainScreenPresenter(MainScreenView view) {

        this.view = view;
        TriviaDuelloApplication.component.inject(this);
    }

    public void getQuestions(@Nullable final String firstPlayer, @Nullable final String secondPlayer) {

        view.showProgressBar();

        triviaCall.getQuestions(10, "multiple").enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                if (TextUtils.isEmpty(firstPlayer) || TextUtils.isEmpty(secondPlayer)) {
                    view.startGameView(response.body());
                } else {
                    view.startDuelGame(firstPlayer, secondPlayer, response.body());
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
}
