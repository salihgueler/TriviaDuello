package com.iamsalih.triviaduello.data.database;

import android.provider.BaseColumns;

/**
 * Created by muhammedsalihguler on 09.12.17.
 */

public class QuestionContract {

    public static class QuestionEntry implements BaseColumns {

        public static final String _ID = BaseColumns._ID;

        public static final String TABLE_NAME = "questions";

        public static final String QUESTION_TEXT = "question_text";

        public static final String QUESTION_CATEGORY = "question_category";

        public static final String QUESTION_DIFFICULTY = "question_difficulty";

        public static final String QUESTION_CORRECT_ANSWER = "question_correct_answer";

        public static final String QUESTION_WRONG_ANSWER = "question_wrong_answer";
    }
}
