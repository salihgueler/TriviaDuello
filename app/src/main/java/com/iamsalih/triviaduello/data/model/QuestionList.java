package com.iamsalih.triviaduello.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by muhammedsalihguler on 26.11.17.
 */

public class QuestionList implements Parcelable {

    @SerializedName("results")
    private List<Question> questionList;

    public QuestionList() {}

    protected QuestionList(Parcel in) {
        questionList = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<QuestionList> CREATOR = new Creator<QuestionList>() {
        @Override
        public QuestionList createFromParcel(Parcel in) {
            return new QuestionList(in);
        }

        @Override
        public QuestionList[] newArray(int size) {
            return new QuestionList[size];
        }
    };

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(questionList);
    }
}
