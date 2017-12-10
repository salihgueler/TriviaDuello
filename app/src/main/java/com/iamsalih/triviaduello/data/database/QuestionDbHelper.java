package com.iamsalih.triviaduello.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_CATEGORY;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_CORRECT_ANSWER;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_DIFFICULTY;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_TEXT;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.QUESTION_WRONG_ANSWER;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry.TABLE_NAME;
import static com.iamsalih.triviaduello.data.database.QuestionContract.QuestionEntry._ID;

/**
 * Created by muhammedsalihguler on 09.12.17.
 */

public class QuestionDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TriviaDuello.db";
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    QUESTION_TEXT + " TEXT NON NULL, " +
                    QUESTION_CATEGORY + " TEXT NON NULL, " +
                    QUESTION_CORRECT_ANSWER + " TEXT NON NULL, " +
                    QUESTION_DIFFICULTY + " TEXT NON NULL, " +
                    QUESTION_WRONG_ANSWER + " TEXT NON NULL)";


    public QuestionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
