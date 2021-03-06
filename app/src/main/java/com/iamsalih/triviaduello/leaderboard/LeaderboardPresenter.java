package com.iamsalih.triviaduello.leaderboard;

import android.content.Intent;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iamsalih.triviaduello.AppConstants;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.TriviaDuelloApplication;
import com.iamsalih.triviaduello.data.model.LeaderBoardItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class LeaderboardPresenter {

    @Inject
    FirebaseDatabase firebaseDatabase;

    @Inject
    FirebaseUser user;

    private LeaderboardView leaderboardView;

    public LeaderboardPresenter(LeaderboardView leaderboardView) {
        this.leaderboardView = leaderboardView;

        TriviaDuelloApplication.firebaseComponent.inject(this);
    }

    /**
     *  Method to retrieve leaderboard points users retrieved from the games.
     */
    public void getLeaderboardPoints() {

        leaderboardView.showProgressBar();
        DatabaseReference databaseReference = firebaseDatabase.getReference(AppConstants.LEADERBOARD_DATABASE_KEY);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                List<LeaderBoardItem> leaderBoardItemList = new ArrayList<>();
                while (iterable.iterator().hasNext()) {
                    DataSnapshot snapshot = iterable.iterator().next();
                    LeaderBoardItem leaderBoardItem = snapshot.getValue(LeaderBoardItem.class);
                    leaderBoardItemList.add(leaderBoardItem);
                }
                leaderboardView.initRecyclerView(sortLeaderBoardList(leaderBoardItemList));
                leaderboardView.hideProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method to sort leaderboard item list.
     * @param leaderBoardItemList sorted version
     */
    private List<LeaderBoardItem> sortLeaderBoardList(List<LeaderBoardItem> leaderBoardItemList) {
        Collections.sort(leaderBoardItemList, new Comparator<LeaderBoardItem>() {
            @Override
            public int compare(LeaderBoardItem leaderBoardItem1, LeaderBoardItem leaderBoardItem2) {
                int point1 = leaderBoardItem1.getPoint();
                int point2 = leaderBoardItem2.getPoint();
                return (point1 > point2 ? -1 : (point1 == point2 ? 0 : 1));

            }
        });
        return leaderBoardItemList;
    }

    /**
     * Sharing the point with the related application
     */
    public void shareItWithAnApplication() {
        DatabaseReference databaseReference = firebaseDatabase.getReference(AppConstants.LEADERBOARD_DATABASE_KEY + user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LeaderBoardItem leaderBoardItem = dataSnapshot.getValue(LeaderBoardItem.class);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, leaderboardView.getAppContext().getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(leaderboardView.getAppContext().getString(R.string.message_to_send), String.valueOf(leaderBoardItem.getPoint())));
                leaderboardView.getAppContext().startActivity(Intent.createChooser(sharingIntent, leaderboardView.getAppContext().getString(R.string.chooser_title)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

