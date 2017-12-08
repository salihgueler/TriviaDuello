package com.iamsalih.triviaduello.mainscreen;

import com.iamsalih.triviaduello.BaseView;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

public interface MainScreenView extends BaseView{

    void startGameView(QuestionList questionList);
    void startDuelGame(QuestionList questionList);
}
