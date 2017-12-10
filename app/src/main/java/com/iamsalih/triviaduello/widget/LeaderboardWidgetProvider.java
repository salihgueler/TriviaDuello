package com.iamsalih.triviaduello.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.data.model.LeaderBoardItem;

/**
 * Created by muhammedsalihguler on 10.12.17.
 */

public class LeaderboardWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        if (appWidgetIds != null) {

            for (final int appWidgetId : appWidgetIds) {

                final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.leaderboard_widget_layout);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("leaderBoard");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                        String textForWidget = "";

                        while (iterable.iterator().hasNext()) {
                            DataSnapshot snapshot = iterable.iterator().next();
                            LeaderBoardItem leaderBoardItem = snapshot.getValue(LeaderBoardItem.class);
                            textForWidget += "Name: " + leaderBoardItem.getUserName() + "\n" +
                                    "Point: " + leaderBoardItem.getPoint() + "\n\n";
                        }
                        Intent serviceIntent = new Intent(context, LeaderboardWidgetService.class);
                        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        serviceIntent.putExtra("widget_key", textForWidget);
                        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                        remoteView.setRemoteAdapter(R.id.leaderboard_scrollable_text, serviceIntent);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}