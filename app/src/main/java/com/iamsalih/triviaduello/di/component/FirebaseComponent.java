package com.iamsalih.triviaduello.di.component;

import com.iamsalih.triviaduello.di.module.FirebaseModule;
import com.iamsalih.triviaduello.leaderboard.LeaderboardPresenter;
import com.iamsalih.triviaduello.question.QuestionsPresenter;

import dagger.Component;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

@Component(modules = {FirebaseModule.class})
public interface FirebaseComponent {
    void inject(QuestionsPresenter questionsPresenter);
    void inject(LeaderboardPresenter leaderboardPresenter);
}
