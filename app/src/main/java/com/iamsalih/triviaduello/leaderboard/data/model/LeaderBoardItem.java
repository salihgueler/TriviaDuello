package com.iamsalih.triviaduello.leaderboard.data.model;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class LeaderBoardItem {

    private String userName;
    private Integer point;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }
}
