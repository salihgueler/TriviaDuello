package com.iamsalih.triviaduello.question;

import android.text.Spanned;

import com.iamsalih.triviaduello.BaseView;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public interface QuestionsView extends BaseView {
    void showQuestionView(String question, String firstOption, String secondOption, String thirdOption, String fourthOption);
    void setTimerText(String timer);
    void createResultScreen(int point);
}
