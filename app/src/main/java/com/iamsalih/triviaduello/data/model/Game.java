package com.iamsalih.triviaduello.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muhammedsalihguler on 05.12.17.
 */

public class Game implements Parcelable {

    private String firstPlayer;
    private String secondPlayer;
    private QuestionList questionList;
    private String gameId;
    private boolean activeGame;

    public boolean isActiveGame() {
        return activeGame;
    }

    public void setActiveGame(boolean activeGame) {
        this.activeGame = activeGame;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public String getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(String secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public QuestionList getQuestionList() {
        return questionList;
    }

    public void setQuestionList(QuestionList questionList) {
        this.questionList = questionList;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Game() {}

    protected Game(Parcel in) {
        firstPlayer = in.readString();
        secondPlayer = in.readString();
        questionList = in.readParcelable(QuestionList.class.getClassLoader());
        gameId = in.readString();
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstPlayer);
        parcel.writeString(secondPlayer);
        parcel.writeParcelable(questionList, i);
        parcel.writeString(gameId);
    }
}
