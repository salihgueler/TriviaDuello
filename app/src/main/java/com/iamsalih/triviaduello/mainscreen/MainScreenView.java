package com.iamsalih.triviaduello.mainscreen;

import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

public interface MainScreenView {

    void showProgressBar();
    void hideProgressBar();
    void startGameView(QuestionList questionList);
    void startDuelGame(String firstPlayer, String secondPlayer, QuestionList questionList);
}
